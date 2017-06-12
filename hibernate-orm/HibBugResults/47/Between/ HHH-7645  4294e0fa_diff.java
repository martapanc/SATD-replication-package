diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 4a3efa5b53..86469b3f45 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,638 +1,639 @@
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
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
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
 				type = "clob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = PrimitiveCharacterArrayClobType.class.getName();
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
+					parameters.put( DynamicParameterizedType.XPROPERTY, xproperty );
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
index c6cde9d0af..22ae48bb50 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,615 +1,607 @@
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
 
-import java.lang.annotation.Annotation;
-import java.lang.reflect.Field;
 import javax.persistence.AttributeConverter;
+import java.lang.annotation.Annotation;
 import java.lang.reflect.TypeVariable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
-import org.hibernate.cfg.AccessType;
+import org.hibernate.annotations.common.reflection.XProperty;
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
-import org.hibernate.property.DirectPropertyAccessor;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
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
 
 	private final List columns = new ArrayList();
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
 	public Iterator getColumnIterator() {
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
 
 	@SuppressWarnings("unchecked")
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
 
 		// todo : we should validate the number of columns present
 		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 		//		then we can "play them against each other" in terms of determining proper typing
 		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 
 		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
 		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
 		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
 		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
 		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
 		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
 		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
 		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor
 		);
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
 		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
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
 
 	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 		private final AttributeConverter converter;
 		private final SqlTypeDescriptor delegate;
 
 		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
 			this.converter = converter;
 			this.delegate = delegate;
 		}
 
 		@Override
 		public int getSqlType() {
 			return delegate.getSqlType();
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return delegate.canBeRemapped();
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
 				}
 			};
 		}
 
 		@Override
 		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 						throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
 				}
 			};
 		}
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
 			}
 
-			AccessType accessType = AccessType.getAccessStrategy( typeParameters
-					.getProperty( DynamicParameterizedType.ACCESS_TYPE ) );
-			final Class classEntity = ReflectHelper.classForName( typeParameters
-					.getProperty( DynamicParameterizedType.ENTITY ) );
-			final String propertyName = typeParameters.getProperty( DynamicParameterizedType.PROPERTY );
-
-			Annotation[] annotations;
-			if ( accessType == AccessType.FIELD ) {
-				annotations = ( (Field) new DirectPropertyAccessor().getGetter( classEntity, propertyName ).getMember() )
-						.getAnnotations();
-
-			}
-			else {
-				annotations = ReflectHelper.getGetter( classEntity, propertyName ).getMethod().getAnnotations();
-			}
+			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
+			// todo : not sure this works for handling @MapKeyEnumerated
+			final Annotation[] annotations = xProperty.getAnnotations();
 
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
-					new ParameterTypeImpl( ReflectHelper.classForName( typeParameters
-							.getProperty( DynamicParameterizedType.RETURNED_CLASS ) ), annotations, table.getCatalog(),
-							table.getSchema(), table.getName(), Boolean.valueOf( typeParameters
-									.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ), columnsNames ) );
-
+					new ParameterTypeImpl(
+							ReflectHelper.classForName(
+									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
+							),
+							annotations,
+							table.getCatalog(),
+							table.getSchema(),
+							table.getName(),
+							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
+							columnsNames )
+			);
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
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
index 2ce36d0979..284f2da6b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
@@ -1,496 +1,500 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 
 import javax.persistence.Enumerated;
 import javax.persistence.MapKeyEnumerated;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.usertype.DynamicParameterizedType;
 import org.hibernate.usertype.EnhancedUserType;
 
 /**
  * Value type mapper for enumerations.
  *
  * Generally speaking, the proper configuration is picked up from the annotations associated with the mapped attribute.
  *
  * There are a few configuration parameters understood by this type mapper:<ul>
  *     <li>
  *         <strong>enumClass</strong> - Names the enumeration class.
  *     </li>
  *     <li>
  *         <strong>useNamed</strong> - Should enum be mapped via name.  Default is to map as ordinal.  Used when
  *         annotations are not used (otherwise {@link javax.persistence.EnumType} is used).
  *     </li>
  *     <li>
  *         <strong>type</strong> - Identifies the JDBC type (via type code) to be used for the column.
  *     </li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 @SuppressWarnings("unchecked")
 public class EnumType implements EnhancedUserType, DynamicParameterizedType, Serializable {
     private static final Logger LOG = Logger.getLogger( EnumType.class.getName() );
 
 	public static final String ENUM = "enumClass";
 	public static final String NAMED = "useNamed";
 	public static final String TYPE = "type";
 
 	private Class<? extends Enum> enumClass;
 	private EnumValueMapper enumValueMapper;
 	private int sqlType = Types.INTEGER;  // before any guessing
 
 	@Override
 	public int[] sqlTypes() {
 		return new int[] { sqlType };
 	}
 
 	@Override
 	public Class<? extends Enum> returnedClass() {
 		return enumClass;
 	}
 
 	@Override
 	public boolean equals(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public int hashCode(Object x) throws HibernateException {
 		return x == null ? 0 : x.hashCode();
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( rs,  names[0] );
 		}
 		return enumValueMapper.getValue( rs, names );
 	}
 
 	private void resolveEnumValueMapper(ResultSet rs, String name) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( rs.getMetaData().getColumnType( rs.findColumn( name ) ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ResultSetMetaData.getColumnType; " +
 								"using fallback determination [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				// peek at the result value to guess type (this is legacy behavior)
 				try {
 					Object value = rs.getObject( name );
 					if ( Number.class.isInstance( value ) ) {
 						treatAsOrdinal();
 					}
 					else {
 						treatAsNamed();
 					}
 				}
 				catch (SQLException ignore) {
 					treatAsOrdinal();
 				}
 			}
 		}
 	}
 
 	private void resolveEnumValueMapper(int columnType) {
 		// fallback for cases where not enough parameter/parameterization information was passed in
 		if ( isOrdinal( columnType ) ) {
 			treatAsOrdinal();
 		}
 		else {
 			treatAsNamed();
 		}
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( st, index );
 		}
 		enumValueMapper.setValue( st, (Enum) value, index );
 	}
 
 	private void resolveEnumValueMapper(PreparedStatement st, int index) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( st.getParameterMetaData().getParameterType( index ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ParameterMetaData#getParameterType; " +
 								"falling back to ordinal-based enum mapping [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				treatAsOrdinal();
 			}
 		}
 	}
 
 	@Override
 	public Object deepCopy(Object value) throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Serializable disassemble(Object value) throws HibernateException {
 		return ( Serializable ) value;
 	}
 
 	@Override
 	public Object assemble(Serializable cached, Object owner) throws HibernateException {
 		return cached;
 	}
 
 	@Override
 	public Object replace(Object original, Object target, Object owner) throws HibernateException {
 		return original;
 	}
 
 	@Override
 	public void setParameterValues(Properties parameters) {
 		final ParameterType reader = (ParameterType) parameters.get( PARAMETER_TYPE );
 
 		// IMPL NOTE : be protective about not setting enumValueMapper (i.e. calling treatAsNamed/treatAsOrdinal)
 		// in cases where we do not have enough information.  In such cases we do additional checks
 		// as part of nullSafeGet/nullSafeSet to query against the JDBC metadata to make the determination.
 
 		if ( reader != null ) {
 			enumClass = reader.getReturnedClass().asSubclass( Enum.class );
 
 			final boolean isOrdinal;
 			final javax.persistence.EnumType enumType = getEnumType( reader );
 			if ( enumType == null ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 				isOrdinal = false;
 			}
 			else {
 				throw new AssertionFailure( "Unknown EnumType: " + enumType );
 			}
 
 			if ( isOrdinal ) {
 				treatAsOrdinal();
 			}
 			else {
 				treatAsNamed();
 			}
 			sqlType = enumValueMapper.getSqlType();
 		}
 		else {
 			String enumClassName = (String) parameters.get( ENUM );
 			try {
 				enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass( Enum.class );
 			}
 			catch ( ClassNotFoundException exception ) {
 				throw new HibernateException( "Enum class not found", exception );
 			}
 
 			final Object useNamedSetting = parameters.get( NAMED );
 			if ( useNamedSetting != null ) {
 				final boolean useNamed = ConfigurationHelper.getBoolean( NAMED, parameters );
 				if ( useNamed ) {
 					treatAsNamed();
 				}
 				else {
 					treatAsOrdinal();
 				}
 				sqlType = enumValueMapper.getSqlType();
 			}
 		}
 
 		final String type = (String) parameters.get( TYPE );
 		if ( type != null ) {
 			sqlType = Integer.decode( type );
 		}
 	}
 
 	private void treatAsOrdinal() {
 		if ( enumValueMapper == null || ! OrdinalEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new OrdinalEnumValueMapper();
 		}
 	}
 
 	private void treatAsNamed() {
 		if ( enumValueMapper == null || ! NamedEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new NamedEnumValueMapper();
 		}
 	}
 
 	private javax.persistence.EnumType getEnumType(ParameterType reader) {
 		javax.persistence.EnumType enumType = null;
 		if ( reader.isPrimaryKey() ) {
 			MapKeyEnumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
 	}
 
 	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> anClass) {
 		for ( Annotation annotation : annotations ) {
 			if ( anClass.isInstance( annotation ) ) {
 				return (T) annotation;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String objectToSQLString(Object value) {
 		return enumValueMapper.objectToSQLString( (Enum) value );
 	}
 
 	@Override
 	public String toXMLString(Object value) {
 		return enumValueMapper.toXMLString( (Enum) value );
 	}
 
 	@Override
 	public Object fromXMLString(String xmlValue) {
 		return enumValueMapper.fromXMLString( xmlValue );
 	}
 
 	private static interface EnumValueMapper {
 		public int getSqlType();
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException;
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException;
 
 		public String objectToSQLString(Enum value);
 		public String toXMLString(Enum value);
 		public Enum fromXMLString(String xml);
 	}
 
 	public abstract class EnumValueMapperSupport implements EnumValueMapper {
 		protected abstract Object extractJdbcValue(Enum value);
 
 		@Override
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException {
 			final Object jdbcValue = value == null ? null : extractJdbcValue( value );
 
 			if ( jdbcValue == null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Binding null to parameter: [%s]", index));
 				}
 				st.setNull( index, getSqlType() );
 				return;
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Binding [%s] to parameter: [%s]", jdbcValue, index));
 			}
 			st.setObject( index, jdbcValue, EnumType.this.sqlType );
 		}
 	}
 
 	private class OrdinalEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper {
 		private transient Enum[] enumsByOrdinal;
 
 		@Override
 		public int getSqlType() {
 			return Types.INTEGER;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final int ordinal = rs.getInt( names[0] );
 			if ( rs.wasNull() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromOrdinal( ordinal );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromOrdinal(int ordinal) {
 			final Enum[] enumsByOrdinal = enumsByOrdinal();
 			if ( ordinal < 0 || ordinal >= enumsByOrdinal.length ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown ordinal value [%s] for enum class [%s]",
 								ordinal,
 								enumClass.getName()
 						)
 				);
 			}
 			return enumsByOrdinal[ordinal];
 
 		}
 
 		private Enum[] enumsByOrdinal() {
 			if ( enumsByOrdinal == null ) {
 				enumsByOrdinal = enumClass.getEnumConstants();
 				if ( enumsByOrdinal == null ) {
 					throw new HibernateException( "Failed to init enum values" );
 				}
 			}
 			return enumsByOrdinal;
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return toXMLString( value );
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return Integer.toString( value.ordinal() );
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromOrdinal( Integer.parseInt( xml ) );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.ordinal();
 		}
 	}
 
 	private class NamedEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper {
 		@Override
 		public int getSqlType() {
 			return Types.VARCHAR;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final String value = rs.getString( names[0] );
 
 			if ( rs.wasNull() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromName( value );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromName(String name) {
 			try {
 				return Enum.valueOf( enumClass, name );
 			}
 			catch ( IllegalArgumentException iae ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown name value [%s] for enum class [%s]",
 								name,
 								enumClass.getName()
 						)
 				);
 			}
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return '\'' + toXMLString( value ) + '\'';
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return value.name();
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromName( xml );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.name();
 		}
 	}
 
+	public boolean isOrdinal() {
+		return isOrdinal( sqlType );
+	}
+
 	private boolean isOrdinal(int paramType) {
 		switch ( paramType ) {
 			case Types.INTEGER:
 			case Types.NUMERIC:
 			case Types.SMALLINT:
 			case Types.TINYINT:
 			case Types.BIGINT:
 			case Types.DECIMAL: //for Oracle Driver
 			case Types.DOUBLE:  //for Oracle Driver
 			case Types.FLOAT:   //for Oracle Driver
 				return true;
 			case Types.CHAR:
 			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 				return false;
 			default:
 				throw new HibernateException( "Unable to persist an Enum in a column of SQL Type: " + paramType );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java b/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java
index 07043eda19..00520b6d84 100644
--- a/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java
@@ -1,66 +1,67 @@
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
 package org.hibernate.usertype;
 
 import java.lang.annotation.Annotation;
 
 /**
  * Types who implements this interface will have in the setParameterValues an
  * instance of the class DynamicParameterizedType$ParameterType instead of
  * the key PARAMETER_TYPE = "org.hibernate.type.ParameterType"
  * 
  * The interface ParameterType provides some methods to read information
  * dynamically for build the type
  * 
  * @author Janario Oliveira
  */
 public interface DynamicParameterizedType extends ParameterizedType {
 	public static final String PARAMETER_TYPE = "org.hibernate.type.ParameterType";
 
 	public static final String IS_DYNAMIC = "org.hibernate.type.ParameterType.dynamic";
 
 	public static final String RETURNED_CLASS = "org.hibernate.type.ParameterType.returnedClass";
 	public static final String IS_PRIMARY_KEY = "org.hibernate.type.ParameterType.primaryKey";
 	public static final String ENTITY = "org.hibernate.type.ParameterType.entityClass";
 	public static final String PROPERTY = "org.hibernate.type.ParameterType.propertyName";
 	public static final String ACCESS_TYPE = "org.hibernate.type.ParameterType.accessType";
+	public static final String XPROPERTY = "org.hibernate.type.ParameterType.xproperty";
 
 	public static interface ParameterType {
 
 		public Class getReturnedClass();
 
 		public Annotation[] getAnnotationsMethod();
 
 		public String getCatalog();
 
 		public String getSchema();
 
 		public String getTable();
 
 		public boolean isPrimaryKey();
 
 		public String[] getColumns();
 
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/MapKeyEnumeratedTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/MapKeyEnumeratedTest.java
new file mode 100644
index 0000000000..a7dff77569
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/MapKeyEnumeratedTest.java
@@ -0,0 +1,63 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.mapkey;
+
+import org.hibernate.Session;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class MapKeyEnumeratedTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { User.class, SocialNetworkProfile.class };
+	}
+
+	@Test
+	public void testMapKeyEnumerated() {
+		Session s = openSession();
+		s.beginTransaction();
+		User user = new User(SocialNetwork.STUB_NETWORK_NAME, "facebookId");
+		s.save( user );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = (User) s.get( User.class, user.getId() );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = (User) s.get( User.class, user.getId() );
+		s.delete( user );
+		s.getTransaction().commit();
+		s.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetwork.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetwork.java
new file mode 100644
index 0000000000..efc550c87d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetwork.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.mapkey;
+
+/**
+ * @author Dmitry Spikhalskiy
+ * @author Steve Ebersole
+ */
+public enum SocialNetwork {
+	STUB_NETWORK_NAME
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetworkProfile.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetworkProfile.java
new file mode 100644
index 0000000000..98e4de2bc8
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/SocialNetworkProfile.java
@@ -0,0 +1,68 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.mapkey;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.EnumType;
+import javax.persistence.Enumerated;
+import javax.persistence.FetchType;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+import javax.persistence.UniqueConstraint;
+
+/**
+ * @author Dmitry Spikhalskiy
+ * @author Steve Ebersole
+ */
+@Entity
+@Table(name = "social_network_profile", uniqueConstraints = {@UniqueConstraint(columnNames = {"social_network", "network_id"})})
+public class SocialNetworkProfile {
+	@javax.persistence.Id
+	@javax.persistence.GeneratedValue(generator = "system-uuid")
+	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid2")
+	@javax.persistence.Column(name = "id", unique = true)
+	private java.lang.String id;
+
+	@ManyToOne(fetch = FetchType.LAZY)
+	@JoinColumn(name = "user_id", nullable = false)
+	private User user;
+
+	@Enumerated(value = EnumType.STRING) //if change type to ordinal - test will not failure
+	@Column(name = "social_network", nullable = false)
+	private SocialNetwork socialNetworkType;
+
+	@Column(name = "network_id", nullable = false)
+	private String networkId;
+
+	protected SocialNetworkProfile() {
+	}
+
+	protected SocialNetworkProfile(User user, SocialNetwork socialNetworkType, String networkId) {
+		this.user = user;
+		this.socialNetworkType = socialNetworkType;
+		this.networkId = networkId;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/User.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/User.java
new file mode 100644
index 0000000000..a286af8c18
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/mapkey/User.java
@@ -0,0 +1,68 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.mapkey;
+
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.EnumType;
+import javax.persistence.FetchType;
+import javax.persistence.MapKeyColumn;
+import javax.persistence.MapKeyEnumerated;
+import javax.persistence.OneToMany;
+import java.util.EnumMap;
+import java.util.Map;
+
+/**
+ * @author Dmitry Spikhalskiy
+ * @author Steve Ebersole
+ */
+@Entity
+public class User {
+	@javax.persistence.Id
+	@javax.persistence.GeneratedValue(generator = "system-uuid")
+	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid2")
+	@javax.persistence.Column(name = "id", unique = true)
+	private java.lang.String id;
+
+	@MapKeyEnumerated( EnumType.STRING )
+	@MapKeyColumn(name = "social_network")
+	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
+	private Map<SocialNetwork, SocialNetworkProfile> socialNetworkProfiles = new EnumMap<SocialNetwork, SocialNetworkProfile>(SocialNetwork.class);
+
+	protected User() {
+	}
+
+	public User(SocialNetwork sn, String socialNetworkId) {
+		SocialNetworkProfile profile = new SocialNetworkProfile(this, sn, socialNetworkId);
+		socialNetworkProfiles.put(sn, profile);
+	}
+
+	public SocialNetworkProfile getSocialNetworkProfile(SocialNetwork socialNetwork) {
+		return socialNetworkProfiles.get(socialNetwork);
+	}
+
+	public String getId() {
+		return id;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/Binding.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/Binding.java
new file mode 100644
index 0000000000..ba0588070b
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/Binding.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.ormXml;
+
+/**
+ * @author Oliverio
+ * @author Steve Ebersole
+ */
+public enum Binding {
+	PAPERBACK,
+	HARDCOVER
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/BookWithOrmEnum.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/BookWithOrmEnum.java
new file mode 100644
index 0000000000..2572862310
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/BookWithOrmEnum.java
@@ -0,0 +1,54 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.ormXml;
+
+/**
+ * @author Oliverio
+ * @author Steve Ebersole
+ */
+public class BookWithOrmEnum {
+	private Long id;
+	private Binding bindingOrdinalEnum;
+	private Binding bindingStringEnum;
+
+	public Long getId() {
+		return id;
+	}
+
+	public Binding getBindingOrdinalEnum() {
+		return bindingOrdinalEnum;
+	}
+
+	public void setBindingOrdinalEnum(Binding bindingOrdinalEnum) {
+		this.bindingOrdinalEnum = bindingOrdinalEnum;
+	}
+
+	public Binding getBindingStringEnum() {
+		return bindingStringEnum;
+	}
+
+	public void setBindingStringEnum(Binding bindingStringEnum) {
+		this.bindingStringEnum = bindingStringEnum;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/OrmXmlEnumTypeTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/OrmXmlEnumTypeTest.java
new file mode 100644
index 0000000000..0fee63658e
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/ormXml/OrmXmlEnumTypeTest.java
@@ -0,0 +1,57 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.enumerated.ormXml;
+
+import org.hibernate.type.CustomType;
+import org.hibernate.type.EnumType;
+import org.hibernate.type.Type;
+
+import org.junit.Test;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.testing.junit4.ExtraAssertions;
+
+import static org.junit.Assert.assertFalse;
+
+/**
+ * @author Steve Ebersole
+ */
+@TestForIssue( jiraKey = "HHH-7645" )
+public class OrmXmlEnumTypeTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected String[] getXmlFiles() {
+		return new String[] { "org/hibernate/test/annotations/enumerated/ormXml/orm.xml" };
+	}
+
+	@Test
+	public void testOrmXmlDefinedEnumType() {
+		Type bindingPropertyType = configuration().getClassMapping( BookWithOrmEnum.class.getName() )
+				.getProperty( "bindingStringEnum" )
+				.getType();
+		CustomType customType = ExtraAssertions.assertTyping( CustomType.class, bindingPropertyType );
+		EnumType enumType = ExtraAssertions.assertTyping( EnumType.class, customType.getUserType() );
+		assertFalse( enumType.isOrdinal() );
+	}
+}
diff --git a/hibernate-core/src/test/resources/org/hibernate/test/annotations/enumerated/ormXml/orm.xml b/hibernate-core/src/test/resources/org/hibernate/test/annotations/enumerated/ormXml/orm.xml
new file mode 100644
index 0000000000..8628dea213
--- /dev/null
+++ b/hibernate-core/src/test/resources/org/hibernate/test/annotations/enumerated/ormXml/orm.xml
@@ -0,0 +1,15 @@
+<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
+                 xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd" version="2.0">
+    <entity name="bookWithOrmEnum" class="org.hibernate.test.annotations.enumerated.ormXml.BookWithOrmEnum" access="FIELD" metadata-complete="true">
+        <attributes>
+            <id name="id" />
+            <basic name="bindingOrdinalEnum">
+                <enumerated>ORDINAL</enumerated>
+            </basic>
+            <basic name="bindingStringEnum">
+                <column column-definition="VARCHAR(10)" />
+                <enumerated>STRING</enumerated>
+            </basic>
+        </attributes>
+    </entity>
+</entity-mappings>
