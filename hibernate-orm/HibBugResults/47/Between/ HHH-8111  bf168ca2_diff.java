diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index fca9a1849b..0ee10369cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,676 +1,679 @@
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
 
 import java.io.Serializable;
 import java.lang.reflect.TypeVariable;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Properties;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converts;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.Nationalized;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.annotations.common.util.ReflectHelper;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.CharacterArrayNClobType;
 import org.hibernate.type.CharacterNCharType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.PrimitiveCharacterArrayNClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.StringNVarcharType;
 import org.hibernate.type.WrappedMaterializedBlobType;
 import org.hibernate.usertype.DynamicParameterizedType;
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private String defaultType = "";
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 	private XProperty xproperty;
 	private AccessType accessType;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 
 		if ( defaultType.length() == 0 ) {
 			defaultType = returnedClassName;
 		}
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
 	public void setType(XProperty property, XClass returnedClass, String declaringClassName) {
 		if ( returnedClass == null ) {
 			// we cannot guess anything
 			return;
 		}
 		XClass returnedClassOrElement = returnedClass;
                 boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		this.xproperty = property;
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 		final boolean isNationalized = property.isAnnotationPresent( Nationalized.class )
 				|| mappings.useNationalizedCharacterData();
 
 		Type annType = property.getAnnotation( Type.class );
 		if ( annType != null ) {
 			setExplicitType( annType );
 			type = explicitType;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
 			explicitType = type;
 		}
 		else if ( property.isAnnotationPresent( Lob.class ) ) {
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.NCLOB.getName()
 						: StandardBasicTypes.CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.NClob.class ) ) {
 				type = StandardBasicTypes.NCLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.MATERIALIZED_NCLOB.getName()
 						: StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = isNationalized
 						? CharacterArrayNClobType.class.getName()
 						: CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = isNationalized
 						? PrimitiveCharacterArrayNClobType.class.getName()
 						: PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( mappings.getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 			explicitType = type;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Enumerated.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) ) {
 			final Class attributeJavaType = mappings.getReflectionManager().toClass( returnedClassOrElement );
 			if ( !Enum.class.isAssignableFrom( attributeJavaType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"Attribute [%s.%s] was annotated as enumerated, but its java type is not an enum [%s]",
 								declaringClassName,
 								xproperty.getName(),
 								attributeJavaType.getName()
 						)
 				);
 			}
 			type = EnumType.class.getName();
 			explicitType = type;
 		}
 		else if ( isNationalized ) {
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				// nvarchar
 				type = StringNVarcharType.INSTANCE.getName();
 				explicitType = type;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) ) {
 				if ( isArray ) {
 					// nvarchar
 					type = StringNVarcharType.INSTANCE.getName();
 				}
 				else {
 					// nchar
 					type = CharacterNCharType.INSTANCE.getName();
 				}
 				explicitType = type;
 			}
 		}
 
 		// implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 			}
 		}
 
 		defaultType = BinderHelper.isEmptyAnnotationValue( type ) ? returnedClassName : type;
 		this.typeParameters = typeParameters;
 
 		applyAttributeConverter( property );
 	}
 
 	private void applyAttributeConverter(XProperty property) {
 		final boolean canBeConverted = ! property.isAnnotationPresent( Id.class )
 				&& ! isVersion
 				&& ! isAssociation()
 				&& ! property.isAnnotationPresent( Temporal.class )
 				&& ! property.isAnnotationPresent( Enumerated.class );
 
 		if ( canBeConverted ) {
 			// @Convert annotations take precedence
 			final Convert convertAnnotation = locateConvertAnnotation( property );
 			if ( convertAnnotation != null ) {
 				if ( ! convertAnnotation.disableConversion() ) {
 					attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
 				}
 			}
 			else {
 				attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
 			}
 		}
 	}
 
 	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
 		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
 		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
+			if ( ! attributeConverterDefinition.isAutoApply() ) {
+				continue;
+			}
 			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
 				return attributeConverterDefinition;
 			}
 		}
 		return null;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
 	}
 
 	@SuppressWarnings("unchecked")
 	private Convert locateConvertAnnotation(XProperty property) {
 		// first look locally on the property for @Convert
 		Convert localConvertAnnotation = property.getAnnotation( Convert.class );
 		if ( localConvertAnnotation != null ) {
 			return localConvertAnnotation;
 		}
 
 		if ( persistentClassName == null ) {
 			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
 			return null;
 		}
 
 		final XClass owner;
 		try {
 			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
 			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
 		}
 		catch (ClassNotFoundException e) {
 			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
 		}
 
 		return lookForEntityDefinedConvertAnnotation( property, owner );
 	}
 
 	private Convert lookForEntityDefinedConvertAnnotation(XProperty property, XClass owner) {
 		if ( owner == null ) {
 			// we have hit the root of the entity hierarchy
 			return null;
 		}
 
 		{
 			Convert convertAnnotation = owner.getAnnotation( Convert.class );
 			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
 				return convertAnnotation;
 			}
 		}
 
 		{
 			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
 			if ( convertsAnnotation != null ) {
 				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
 					if ( isMatch( convertAnnotation, property ) ) {
 						return convertAnnotation;
 					}
 				}
 			}
 		}
 
 		// finally, look on superclass
 		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
 	}
 
 	@SuppressWarnings("unchecked")
 	private boolean isMatch(Convert convertAnnotation, XProperty property) {
 		return property.getName().equals( convertAnnotation.attributeName() )
 				&& isTypeMatch( convertAnnotation.converter(), property );
 	}
 
 	private boolean isTypeMatch(Class<? extends AttributeConverter> attributeConverterClass, XProperty property) {
 		return areTypeMatch(
 				extractEntityAttributeType( attributeConverterClass ),
 				mappings.getReflectionManager().toClass( property.getType() )
 		);
 	}
 
 	private Class extractEntityAttributeType(Class<? extends AttributeConverter> attributeConverterClass) {
 		// this is duplicated in SimpleValue...
 		final TypeVariable[] attributeConverterTypeInformation = attributeConverterClass.getTypeParameters();
 		if ( attributeConverterTypeInformation == null || attributeConverterTypeInformation.length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterTypeInformation.length > 2 ) {
 			LOG.debug(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		final Class entityAttributeJavaType = extractType( attributeConverterTypeInformation[0] );
 		if ( entityAttributeJavaType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 		return entityAttributeJavaType;
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType == null ) {
 			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
 		}
 		if ( propertyType == null ) {
 			throw new AnnotationException( "Property defined java type cannot be null" );
 		}
 
 		return converterDefinedType.equals( propertyType )
 				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
 	}
 
 	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType.isPrimitive() ) {
 			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
 		}
 		else if ( propertyType.isPrimitive() ) {
 			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
 		}
 		return false;
 	}
 
 	private static Class getWrapperEquivalent(Class primitive) {
 		if ( ! primitive.isPrimitive() ) {
 			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
 		}
 
 		if ( boolean.class.equals( primitive ) ) {
 			return Boolean.class;
 		}
 		else if ( char.class.equals( primitive ) ) {
 			return Character.class;
 		}
 		else if ( byte.class.equals( primitive ) ) {
 			return Byte.class;
 		}
 		else if ( short.class.equals( primitive ) ) {
 			return Short.class;
 		}
 		else if ( int.class.equals( primitive ) ) {
 			return Integer.class;
 		}
 		else if ( long.class.equals( primitive ) ) {
 			return Long.class;
 		}
 		else if ( float.class.equals( primitive ) ) {
 			return Float.class;
 		}
 		else if ( double.class.equals( primitive ) ) {
 			return Double.class;
 		}
 
 		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
 	}
 	
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
 		LOG.debugf( "building SimpleValue for %s", propertyName );
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( mappings, table );
 
 		linkWithValue();
 
 		boolean isInSecondPass = mappings.isInSecondPass();
 		SetSimpleValueTypeSecondPass secondPass = new SetSimpleValueTypeSecondPass( this );
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			mappings.addSecondPass( secondPass );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !mappings.isInSecondPass() && referencedEntityName != null ) {
 			mappings.addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, ( Ejb3JoinColumn[] ) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
 		LOG.debugf( "Setting SimpleValue typeName for %s", propertyName );
                 
 		if ( attributeConverterDefinition != null ) {
 			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
 										"remove @Type or specify @Convert(disableConversion = true)",
 								persistentClassName,
 								propertyName
 						)
 				);
 			}
 			simpleValue.setJpaAttributeConverterDefinition( attributeConverterDefinition );
 		}
 		else {
 			String type;
 			org.hibernate.mapping.TypeDef typeDef;
 
 			if ( !BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				type = explicitType;
 				typeDef = mappings.getTypeDef( type );
 			}
 			else {
 				// try implicit type
 				org.hibernate.mapping.TypeDef implicitTypeDef = mappings.getTypeDef( returnedClassName );
 				if ( implicitTypeDef != null ) {
 					typeDef = implicitTypeDef;
 					type = returnedClassName;
 				}
 				else {
 					typeDef = mappings.getTypeDef( defaultType );
 					type = defaultType;
 				}
 			}
 
 			if ( typeDef != null ) {
 				type = typeDef.getTypeClass();
 				simpleValue.setTypeParameters( typeDef.getParameters() );
 			}
 			if ( typeParameters != null && typeParameters.size() != 0 ) {
 				//explicit type params takes precedence over type def params
 				simpleValue.setTypeParameters( typeParameters );
 			}
 			simpleValue.setTypeName( type );
 		}
 
 		if ( persistentClassName != null || attributeConverterDefinition != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 		
 		if ( simpleValue.getTypeName() != null && simpleValue.getTypeName().length() > 0
 				&& simpleValue.getMappings().getTypeResolver().basic( simpleValue.getTypeName() ) == null ) {
 			try {
 				Class typeClass = ReflectHelper.classForName( simpleValue.getTypeName() );
 
 				if ( typeClass != null && DynamicParameterizedType.class.isAssignableFrom( typeClass ) ) {
 					Properties parameters = simpleValue.getTypeParameters();
 					if ( parameters == null ) {
 						parameters = new Properties();
 					}
 					parameters.put( DynamicParameterizedType.IS_DYNAMIC, Boolean.toString( true ) );
 					parameters.put( DynamicParameterizedType.RETURNED_CLASS, returnedClassName );
 					parameters.put( DynamicParameterizedType.IS_PRIMARY_KEY, Boolean.toString( key ) );
 
 					parameters.put( DynamicParameterizedType.ENTITY, persistentClassName );
 					parameters.put( DynamicParameterizedType.XPROPERTY, xproperty );
 					parameters.put( DynamicParameterizedType.PROPERTY, xproperty.getName() );
 					parameters.put( DynamicParameterizedType.ACCESS_TYPE, accessType.getType() );
 					simpleValue.setTypeParameters( parameters );
 				}
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), cnfe );
 			}
 		}
 
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 
 	public AccessType getAccessType() {
 		return accessType;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 6f660262bd..1adddba2ea 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,601 +1,573 @@
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
 
 import javax.persistence.AttributeConverter;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.TypeVariable;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
+import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final Logger log = Logger.getLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition jpaAttributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( jpaAttributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
-	 * For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
-	 // the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
-	 // "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
-	 // and use that type-code to resolve the SqlTypeDescriptor to use.
-	 *
-	 *
-	 * <p/>
-	 *
-	 * <p/>
-	 * <p/>
+	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
-	 *
-	 * @return The built AttributeConverter -> Type adapter
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
+		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
-		final Type type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor ) {
-			@Override
-			public String getName() {
-				return name;
-			}
-		};
-		log.debug( "Created : " + name );
-
-		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
-
-		return type;
-	}
-
-	private Class extractType(TypeVariable typeVariable) {
-		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
-		if ( boundTypes == null || boundTypes.length != 1 ) {
-			return null;
-		}
-
-		return (Class) boundTypes[0];
+		return new AttributeConverterTypeAdapter( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor, name );
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
 		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							ReflectHelper.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, cnfe );
 		}
 	}
 
 	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
new file mode 100644
index 0000000000..a322050194
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
@@ -0,0 +1,54 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.converter;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.type.AbstractSingleColumnStandardBasicType;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AttributeConverterTypeAdapter extends AbstractSingleColumnStandardBasicType {
+	private static final Logger log = Logger.getLogger( AttributeConverterTypeAdapter.class );
+
+	private final String name;
+
+	public AttributeConverterTypeAdapter(
+			SqlTypeDescriptor sqlTypeDescriptor,
+			JavaTypeDescriptor javaTypeDescriptor,
+			String name) {
+		super( sqlTypeDescriptor, javaTypeDescriptor );
+		this.name = name;
+
+		log.debug( "Created AttributeConverterTypeAdapter -> " + name );
+	}
+
+	@Override
+	public String getName() {
+		return name;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
index 895367f738..7c60c71f7e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
@@ -1,349 +1,382 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.type;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converter;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import java.io.Serializable;
 import java.sql.Clob;
 import java.sql.Timestamp;
 import java.sql.Types;
 
 import org.hibernate.IrrelevantEntity;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
+import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
 import org.hibernate.type.descriptor.java.StringTypeDescriptor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
+import static org.junit.Assert.fail;
 
 /**
  * Tests the principle of adding "AttributeConverter" to the mix of {@link org.hibernate.type.Type} resolution
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicOperation() {
 		Configuration cfg = new Configuration();
 		SimpleValue simpleValue = new SimpleValue( cfg.createMappings() );
 		simpleValue.setJpaAttributeConverterDefinition(
 				new AttributeConverterDefinition( new StringClobConverter(), true )
 		);
 		simpleValue.setTypeUsingReflection( IrrelevantEntity.class.getName(), "name" );
 
 		Type type = simpleValue.getType();
 		assertNotNull( type );
 		assertTyping( BasicType.class, type );
 		AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 		assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 		assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 	}
 
 	@Test
+	public void testNonAutoApplyHandling() {
+		Configuration cfg = new Configuration();
+		cfg.addAttributeConverter( NotAutoAppliedConverter.class, false );
+		cfg.addAnnotatedClass( Tester.class );
+		cfg.buildMappings();
+
+		PersistentClass tester = cfg.getClassMapping( Tester.class.getName() );
+		Property nameProp = tester.getProperty( "name" );
+		SimpleValue nameValue = (SimpleValue) nameProp.getValue();
+		Type type = nameValue.getType();
+		assertNotNull( type );
+		if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
+			fail( "AttributeConverter with autoApply=false was auto applied" );
+		}
+	}
+
+	@Test
 	public void testBasicConverterApplication() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( StringClobConverter.class, true );
 		cfg.addAnnotatedClass( Tester.class );
 		cfg.addAnnotatedClass( Tester2.class );
 		cfg.buildMappings();
 
 		{
 			PersistentClass tester = cfg.getClassMapping( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 
 		{
 			PersistentClass tester = cfg.getClassMapping( Tester2.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 	}
 
 	@Test
 	public void testBasicUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( IntegerToVarcharConverter.class, false );
 		cfg.addAnnotatedClass( Tester4.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new Tester4( 1L, "steve", 200 ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			session.get( Tester4.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			Tester4 t4 = (Tester4) session.get( Tester4.class, 1L );
 			t4.code = 300;
 			session.getTransaction().commit();
 			session.close();
 
 			session = sf.openSession();
 			session.beginTransaction();
 			t4 = (Tester4) session.get( Tester4.class, 1L );
 			assertEquals( 300, t4.code.longValue() );
 			session.delete( t4 );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testBasicTimestampUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( InstantConverter.class, false );
 		cfg.addAnnotatedClass( IrrelevantInstantEntity.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new IrrelevantInstantEntity( 1L ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			IrrelevantInstantEntity e = (IrrelevantInstantEntity) session.get( IrrelevantInstantEntity.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			session.delete( e );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	// Entity declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Entity(name = "T1")
 	public static class Tester {
 		@Id
 		private Long id;
 		private String name;
 
 		public Tester() {
 		}
 
 		public Tester(Long id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 	}
 
 	@Entity(name = "T2")
 	public static class Tester2 {
 		@Id
 		private Long id;
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T3")
 	public static class Tester3 {
 		@Id
 		private Long id;
 		@org.hibernate.annotations.Type( type = "string" )
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T4")
 	public static class Tester4 {
 		@Id
 		private Long id;
 		private String name;
 		@Convert( converter = IntegerToVarcharConverter.class )
 		private Integer code;
 
 		public Tester4() {
 		}
 
 		public Tester4(Long id, String name, Integer code) {
 			this.id = id;
 			this.name = name;
 			this.code = code;
 		}
 	}
 
 	// This class is for mimicking an Instant from Java 8, which a converter might convert to a java.sql.Timestamp
 	public static class Instant implements Serializable {
 		private static final long serialVersionUID = 1L;
 
 		private long javaMillis;
 
 		public Instant(long javaMillis) {
 			this.javaMillis = javaMillis;
 		}
 
 		public long toJavaMillis() {
 			return javaMillis;
 		}
 
 		public static Instant fromJavaMillis(long javaMillis) {
 			return new Instant( javaMillis );
 		}
 
 		public static Instant now() {
 			return new Instant( System.currentTimeMillis() );
 		}
 	}
 
 	@Entity
 	public static class IrrelevantInstantEntity {
 		@Id
 		private Long id;
 		private Instant dateCreated;
 
 		public IrrelevantInstantEntity() {
 		}
 
 		public IrrelevantInstantEntity(Long id) {
 			this( id, Instant.now() );
 		}
 
 		public IrrelevantInstantEntity(Long id, Instant dateCreated) {
 			this.id = id;
 			this.dateCreated = dateCreated;
 		}
 
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		public Instant getDateCreated() {
 			return dateCreated;
 		}
 
 		public void setDateCreated(Instant dateCreated) {
 			this.dateCreated = dateCreated;
 		}
 	}
 
 
 	// Converter declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Converter(autoApply = false)
+	public static class NotAutoAppliedConverter implements AttributeConverter<String,String> {
+		@Override
+		public String convertToDatabaseColumn(String attribute) {
+			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
+		}
+
+		@Override
+		public String convertToEntityAttribute(String dbData) {
+			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
+		}
+	}
+
 	@Converter( autoApply = true )
 	public static class StringClobConverter implements AttributeConverter<String,Clob> {
 		@Override
 		public Clob convertToDatabaseColumn(String attribute) {
 			return null;
 		}
 
 		@Override
 		public String convertToEntityAttribute(Clob dbData) {
 			return null;
 		}
 	}
 
 	@Converter( autoApply = true )
 	public static class IntegerToVarcharConverter implements AttributeConverter<Integer,String> {
 		@Override
 		public String convertToDatabaseColumn(Integer attribute) {
 			return attribute == null ? null : attribute.toString();
 		}
 
 		@Override
 		public Integer convertToEntityAttribute(String dbData) {
 			return dbData == null ? null : Integer.valueOf( dbData );
 		}
 	}
 
 
 	@Converter( autoApply = true )
 	public static class InstantConverter implements AttributeConverter<Instant, Timestamp> {
 		@Override
 		public Timestamp convertToDatabaseColumn(Instant attribute) {
 			return new Timestamp( attribute.toJavaMillis() );
 		}
 
 		@Override
 		public Instant convertToEntityAttribute(Timestamp dbData) {
 			return Instant.fromJavaMillis( dbData.getTime() );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/ExplicitlyNamedConverterClassesTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/ExplicitlyNamedConverterClassesTest.java
new file mode 100644
index 0000000000..39f5591613
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/ExplicitlyNamedConverterClassesTest.java
@@ -0,0 +1,115 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.convert;
+
+import javax.persistence.AttributeConverter;
+import javax.persistence.Convert;
+import javax.persistence.Converter;
+import javax.persistence.Entity;
+import javax.persistence.EntityManager;
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.Id;
+
+import java.util.Arrays;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+/**
+ * Tests for asserting correct behavior of applying AttributeConverters explicitly listed in persistence.xml.
+ *
+ * @author Steve Ebersole
+ */
+public class ExplicitlyNamedConverterClassesTest extends BaseUnitTestCase {
+
+	// test handling of explicitly named, but non-auto-applied converter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Converter(autoApply = false)
+	public static class NotAutoAppliedConverter implements AttributeConverter<String,String> {
+		@Override
+		public String convertToDatabaseColumn(String attribute) {
+			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
+		}
+
+		@Override
+		public String convertToEntityAttribute(String dbData) {
+			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
+		}
+	}
+
+	@Entity( name = "Entity1" )
+	public static class Entity1 {
+		@Id
+		private Integer id;
+		private String name;
+
+		public Entity1() {
+		}
+
+		public Entity1(Integer id, String name) {
+			this.id = id;
+			this.name = name;
+		}
+	}
+
+	@Test
+	public void testNonAutoAppliedConvertIsNotApplied() {
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				return Arrays.asList( Entity1.class.getName(), NotAutoAppliedConverter.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+		try {
+			EntityManager em = emf.createEntityManager();
+			em.getTransaction().begin();
+			em.persist( new Entity1( 1, "1" ) );
+			em.getTransaction().commit();
+			em.close();
+
+			em = emf.createEntityManager();
+			em.getTransaction().begin();
+			em.createQuery( "delete Entity1" ).executeUpdate();
+			em.getTransaction().commit();
+			em.close();
+		}
+		finally {
+			emf.close();
+		}
+	}
+}
