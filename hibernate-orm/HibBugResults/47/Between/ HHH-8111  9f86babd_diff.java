diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
index 371f43a546..c43c3017c6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
@@ -1,124 +1,135 @@
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
 package org.hibernate.cfg;
 
 import javax.persistence.AttributeConverter;
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 
 /**
  * @author Steve Ebersole
  */
 public class AttributeConverterDefinition {
 	private static final Logger log = Logger.getLogger( AttributeConverterDefinition.class );
 
 	private final AttributeConverter attributeConverter;
 	private final boolean autoApply;
 	private final Class entityAttributeType;
 	private final Class databaseColumnType;
 
 	public AttributeConverterDefinition(AttributeConverter attributeConverter, boolean autoApply) {
 		this.attributeConverter = attributeConverter;
 		this.autoApply = autoApply;
 
 		final Class attributeConverterClass = attributeConverter.getClass();
 		final ParameterizedType attributeConverterSignature = extractAttributeConverterParameterizedType( attributeConverterClass );
 
 		if ( attributeConverterSignature.getActualTypeArguments().length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterSignature.getActualTypeArguments().length > 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		entityAttributeType = (Class) attributeConverterSignature.getActualTypeArguments()[0];
 		if ( entityAttributeType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 
 		databaseColumnType = (Class) attributeConverterSignature.getActualTypeArguments()[1];
 		if ( databaseColumnType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'database column' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 	}
 
 	private ParameterizedType extractAttributeConverterParameterizedType(Class attributeConverterClass) {
 		for ( Type type : attributeConverterClass.getGenericInterfaces() ) {
 			if ( ParameterizedType.class.isInstance( type ) ) {
 				final ParameterizedType parameterizedType = (ParameterizedType) type;
 				if ( AttributeConverter.class.equals( parameterizedType.getRawType() ) ) {
 					return parameterizedType;
 				}
 			}
 		}
 
 		throw new AssertionFailure(
 				"Could not extract ParameterizedType representation of AttributeConverter definition " +
 						"from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]"
 		);
 	}
 
 	public AttributeConverter getAttributeConverter() {
 		return attributeConverter;
 	}
 
 	public boolean isAutoApply() {
 		return autoApply;
 	}
 
 	public Class getEntityAttributeType() {
 		return entityAttributeType;
 	}
 
 	public Class getDatabaseColumnType() {
 		return databaseColumnType;
 	}
 
 	private static Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
+
+	@Override
+	public String toString() {
+		return String.format(
+				"%s[converterClass=%s, domainType=%s, jdbcType=%s]",
+				this.getClass().getName(),
+				attributeConverter.getClass().getName(),
+				entityAttributeType.getName(),
+				databaseColumnType.getName()
+		);
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 0ee10369cc..da236a84ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,679 +1,767 @@
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
-		final boolean canBeConverted = ! property.isAnnotationPresent( Id.class )
-				&& ! isVersion
-				&& ! isAssociation()
-				&& ! property.isAnnotationPresent( Temporal.class )
-				&& ! property.isAnnotationPresent( Enumerated.class );
-
-		if ( canBeConverted ) {
-			// @Convert annotations take precedence
-			final Convert convertAnnotation = locateConvertAnnotation( property );
-			if ( convertAnnotation != null ) {
-				if ( ! convertAnnotation.disableConversion() ) {
-					attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
-				}
-			}
-			else {
-				attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
-			}
+		if ( attributeConverterDefinition != null ) {
+			return;
+		}
+
+		LOG.debugf( "Starting applyAttributeConverter [%s:%s]", persistentClassName, property.getName() );
+
+		if ( property.isAnnotationPresent( Id.class ) ) {
+			LOG.debugf( "Skipping AttributeConverter checks for Id attribute [%s]", property.getName() );
+			return;
+		}
+
+		if ( isVersion ) {
+			LOG.debugf( "Skipping AttributeConverter checks for version attribute [%s]", property.getName() );
+			return;
+		}
+
+		if ( property.isAnnotationPresent( Temporal.class ) ) {
+			LOG.debugf( "Skipping AttributeConverter checks for Temporal attribute [%s]", property.getName() );
+			return;
+		}
+
+		if ( property.isAnnotationPresent( Enumerated.class ) ) {
+			LOG.debugf( "Skipping AttributeConverter checks for Enumerated attribute [%s]", property.getName() );
+			return;
+		}
+
+		if ( isAssociation() ) {
+			LOG.debugf( "Skipping AttributeConverter checks for association attribute [%s]", property.getName() );
+			return;
+		}
+
+		// @Convert annotations take precedence if present
+		final Convert convertAnnotation = locateConvertAnnotation( property );
+		if ( convertAnnotation != null ) {
+			LOG.debugf(
+					"Applying located @Convert AttributeConverter [%s] to attribute [%]",
+					convertAnnotation.converter().getName(),
+					property.getName()
+			);
+			attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
+		}
+		else {
+			attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
 		}
 	}
 
 	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
 		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
 		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
 			if ( ! attributeConverterDefinition.isAutoApply() ) {
 				continue;
 			}
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
-		// first look locally on the property for @Convert
-		Convert localConvertAnnotation = property.getAnnotation( Convert.class );
-		if ( localConvertAnnotation != null ) {
-			return localConvertAnnotation;
+		LOG.debugf(
+				"Attempting to locate Convert annotation for property [%s:%s]",
+				persistentClassName,
+				property.getName()
+		);
+
+		// first look locally on the property for @Convert/@Converts
+		{
+			Convert localConvertAnnotation = property.getAnnotation( Convert.class );
+			if ( localConvertAnnotation != null ) {
+				LOG.debugf(
+						"Found matching local @Convert annotation [disableConversion=%s]",
+						localConvertAnnotation.disableConversion()
+				);
+				return localConvertAnnotation.disableConversion()
+						? null
+						: localConvertAnnotation;
+			}
+		}
+
+		{
+			Converts localConvertsAnnotation = property.getAnnotation( Converts.class );
+			if ( localConvertsAnnotation != null ) {
+				for ( Convert localConvertAnnotation : localConvertsAnnotation.value() ) {
+					if ( isLocalMatch( localConvertAnnotation, property ) ) {
+						LOG.debugf(
+								"Found matching @Convert annotation as part local @Converts [disableConversion=%s]",
+								localConvertAnnotation.disableConversion()
+						);
+						return localConvertAnnotation.disableConversion()
+								? null
+								: localConvertAnnotation;
+					}
+				}
+			}
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
 
+		LOG.debugf(
+				"Attempting to locate any AttributeConverter defined via @Convert/@Converts on type-hierarchy [%s] to apply to attribute [%s]",
+				owner.getName(),
+				property.getName()
+		);
+
 		{
 			Convert convertAnnotation = owner.getAnnotation( Convert.class );
 			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
-				return convertAnnotation;
+				LOG.debugf(
+						"Found matching @Convert annotation [disableConversion=%s]",
+						convertAnnotation.disableConversion()
+				);
+				return convertAnnotation.disableConversion() ? null : convertAnnotation;
 			}
 		}
 
 		{
 			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
 			if ( convertsAnnotation != null ) {
 				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
 					if ( isMatch( convertAnnotation, property ) ) {
-						return convertAnnotation;
+						LOG.debugf(
+								"Found matching @Convert annotation as part @Converts [disableConversion=%s]",
+								convertAnnotation.disableConversion()
+						);
+						return convertAnnotation.disableConversion() ? null : convertAnnotation;
 					}
 				}
 			}
 		}
 
 		// finally, look on superclass
 		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
 	}
 
 	@SuppressWarnings("unchecked")
-	private boolean isMatch(Convert convertAnnotation, XProperty property) {
+	private boolean isLocalMatch(Convert convertAnnotation, XProperty property) {
+		if ( StringHelper.isEmpty( convertAnnotation.attributeName() ) ) {
+			return isTypeMatch( convertAnnotation.converter(), property );
+		}
+
 		return property.getName().equals( convertAnnotation.attributeName() )
 				&& isTypeMatch( convertAnnotation.converter(), property );
 	}
 
+	@SuppressWarnings("unchecked")
+	private boolean isMatch(Convert convertAnnotation, XProperty property) {
+		return property.getName().equals( convertAnnotation.attributeName() );
+//		return property.getName().equals( convertAnnotation.attributeName() )
+//				&& isTypeMatch( convertAnnotation.converter(), property );
+	}
+
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
-		LOG.debugf( "Setting SimpleValue typeName for %s", propertyName );
+		LOG.debugf( "Starting fillSimpleValue for %s", propertyName );
                 
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
+			LOG.debugf(
+					"Applying JPA AttributeConverter [%s] to [%s:%s]",
+					attributeConverterDefinition,
+					persistentClassName,
+					propertyName
+			);
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
index 1adddba2ea..716cdb0704 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,573 +1,582 @@
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
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
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
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
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
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
-		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
-		return new AttributeConverterTypeAdapter( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor, name );
+		final String name = String.format(
+				"BasicType adapter for AttributeConverter<%s,%s>",
+				entityAttributeJavaType.getSimpleName(),
+				databaseColumnJavaType.getSimpleName()
+		);
+		return new AttributeConverterTypeAdapter(
+				name,
+				jpaAttributeConverterDefinition.getAttributeConverter(),
+				sqlTypeDescriptorAdapter,
+				entityAttributeJavaTypeDescriptor
+		);
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
index a322050194..d23d6dfe59 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
@@ -1,54 +1,65 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.type.descriptor.converter;
 
+import javax.persistence.AttributeConverter;
+
 import org.jboss.logging.Logger;
 
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
+ * Adapts the Hibernate Type contract to incorporate JPA AttributeConverter calls.
+ *
  * @author Steve Ebersole
  */
-public class AttributeConverterTypeAdapter extends AbstractSingleColumnStandardBasicType {
+public class AttributeConverterTypeAdapter<T> extends AbstractSingleColumnStandardBasicType<T> {
 	private static final Logger log = Logger.getLogger( AttributeConverterTypeAdapter.class );
 
 	private final String name;
+	private final AttributeConverter<? extends T,?> attributeConverter;
 
 	public AttributeConverterTypeAdapter(
-			SqlTypeDescriptor sqlTypeDescriptor,
-			JavaTypeDescriptor javaTypeDescriptor,
-			String name) {
-		super( sqlTypeDescriptor, javaTypeDescriptor );
+			String name,
+			AttributeConverter<? extends T,?> attributeConverter,
+			SqlTypeDescriptor sqlTypeDescriptorAdapter,
+			JavaTypeDescriptor<T> entityAttributeJavaTypeDescriptor) {
+		super( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor );
 		this.name = name;
+		this.attributeConverter = attributeConverter;
 
 		log.debug( "Created AttributeConverterTypeAdapter -> " + name );
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
+
+	public AttributeConverter<? extends T,?> getAttributeConverter() {
+		return attributeConverter;
+	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertAnnotationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertAnnotationTest.java
new file mode 100644
index 0000000000..f0397ab931
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertAnnotationTest.java
@@ -0,0 +1,157 @@
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
+import java.net.MalformedURLException;
+import java.net.URL;
+import java.util.Arrays;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.Type;
+import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SimpleConvertAnnotationTest extends BaseUnitTestCase {
+
+	// test handling of an AttributeConverter explicitly named via a @Convert annotation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Test
+	public void testSimpleConvertUsage() throws MalformedURLException {
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				return Arrays.asList( Entity1.class.getName(), UrlConverter.class.getName(), AutoUrlConverter.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+		final EntityPersister ep = emf.unwrap( SessionFactoryImplementor.class ).getEntityPersister( Entity1.class.getName() );
+		final Type websitePropertyType = ep.getPropertyType( "website" );
+		final AttributeConverterTypeAdapter type = assertTyping( AttributeConverterTypeAdapter.class, websitePropertyType );
+		assertTyping( UrlConverter.class, type.getAttributeConverter() );
+
+		try {
+			EntityManager em = emf.createEntityManager();
+			em.getTransaction().begin();
+			em.persist( new Entity1( 1, "1", new URL( "http://hibernate.org" ) ) );
+			em.getTransaction().commit();
+			em.close();
+
+			assertEquals( 1, callsToConverter );
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
+
+	static int callsToConverter = 0;
+
+	@Converter(autoApply = false)
+	public static class UrlConverter implements AttributeConverter<URL,String> {
+		@Override
+		public String convertToDatabaseColumn(URL attribute) {
+			callsToConverter++;
+			return attribute == null ? null : attribute.toExternalForm();
+		}
+
+		@Override
+		public URL convertToEntityAttribute(String dbData) {
+			callsToConverter++;
+			if ( dbData == null ) {
+				return null;
+			}
+
+			try {
+				return new URL( dbData );
+			}
+			catch (MalformedURLException e) {
+				throw new IllegalArgumentException( "Could not convert incoming value to URL : " + dbData );
+			}
+		}
+	}
+
+	@Converter( autoApply = true )
+	public static class AutoUrlConverter implements AttributeConverter<URL,String> {
+		@Override
+		public String convertToDatabaseColumn(URL attribute) {
+			throw new IllegalStateException( "Should not be called" );
+		}
+
+		@Override
+		public URL convertToEntityAttribute(String dbData) {
+			throw new IllegalStateException( "Should not be called" );
+		}
+	}
+
+	@Entity( name = "Entity1" )
+	public static class Entity1 {
+		@Id
+		private Integer id;
+		private String name;
+		@Convert( converter = UrlConverter.class )
+		private URL website;
+
+		public Entity1() {
+		}
+
+		public Entity1(Integer id, String name, URL website) {
+			this.id = id;
+			this.name = name;
+			this.website = website;
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertsAnnotationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertsAnnotationTest.java
new file mode 100644
index 0000000000..4ea035de12
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleConvertsAnnotationTest.java
@@ -0,0 +1,159 @@
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
+import javax.persistence.Converts;
+import javax.persistence.Entity;
+import javax.persistence.EntityManager;
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.Id;
+import java.net.MalformedURLException;
+import java.net.URL;
+import java.util.Arrays;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.Type;
+import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SimpleConvertsAnnotationTest extends BaseUnitTestCase {
+
+	// test handling of an AttributeConverter explicitly named via a @Convert annotation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Test
+	public void testSimpleConvertUsage() throws MalformedURLException {
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				return Arrays.asList( Entity1.class.getName(), UrlConverter.class.getName(), AutoUrlConverter.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+		final EntityPersister ep = emf.unwrap( SessionFactoryImplementor.class ).getEntityPersister( Entity1.class.getName() );
+		final Type websitePropertyType = ep.getPropertyType( "website" );
+		final AttributeConverterTypeAdapter type = assertTyping( AttributeConverterTypeAdapter.class, websitePropertyType );
+		assertTyping( UrlConverter.class, type.getAttributeConverter() );
+
+		try {
+			EntityManager em = emf.createEntityManager();
+			em.getTransaction().begin();
+			em.persist( new Entity1( 1, "1", new URL( "http://hibernate.org" ) ) );
+			em.getTransaction().commit();
+			em.close();
+
+			assertEquals( 1, callsToConverter );
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
+
+	static int callsToConverter = 0;
+
+	@Converter(autoApply = false)
+	public static class UrlConverter implements AttributeConverter<URL,String> {
+		@Override
+		public String convertToDatabaseColumn(URL attribute) {
+			callsToConverter++;
+			return attribute == null ? null : attribute.toExternalForm();
+		}
+
+		@Override
+		public URL convertToEntityAttribute(String dbData) {
+			callsToConverter++;
+			if ( dbData == null ) {
+				return null;
+			}
+
+			try {
+				return new URL( dbData );
+			}
+			catch (MalformedURLException e) {
+				throw new IllegalArgumentException( "Could not convert incoming value to URL : " + dbData );
+			}
+		}
+	}
+
+	@Converter( autoApply = true )
+	public static class AutoUrlConverter implements AttributeConverter<URL,String> {
+		@Override
+		public String convertToDatabaseColumn(URL attribute) {
+			throw new IllegalStateException( "Should not be called" );
+		}
+
+		@Override
+		public URL convertToEntityAttribute(String dbData) {
+			throw new IllegalStateException( "Should not be called" );
+		}
+	}
+
+	@Entity( name = "Entity1" )
+	@Converts(
+			@Convert( attributeName = "website", converter = UrlConverter.class )
+	)
+	public static class Entity1 {
+		@Id
+		private Integer id;
+		private String name;
+		private URL website;
+
+		public Entity1() {
+		}
+
+		public Entity1(Integer id, String name, URL website) {
+			this.id = id;
+			this.name = name;
+			this.website = website;
+		}
+	}
+}
