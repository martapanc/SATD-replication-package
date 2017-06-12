diff --git a/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
index cc118ab4eb..7f2579a260 100644
--- a/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
@@ -1,144 +1,169 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.property;
 
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Property;
+import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.type.Type;
 
 /**
  * A factory for building/retrieving PropertyAccessor instances.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PropertyAccessorFactory {
 
 	private static final PropertyAccessor BASIC_PROPERTY_ACCESSOR = new BasicPropertyAccessor();
 	private static final PropertyAccessor DIRECT_PROPERTY_ACCESSOR = new DirectPropertyAccessor();
 	private static final PropertyAccessor MAP_ACCESSOR = new MapAccessor();
 	private static final PropertyAccessor NOOP_ACCESSOR = new NoopAccessor();
 	private static final PropertyAccessor EMBEDDED_PROPERTY_ACCESSOR = new EmbeddedPropertyAccessor();
 
 	//TODO: ideally we need the construction of PropertyAccessor to take the following:
 	//      1) EntityMode
 	//      2) EntityMode-specific data (i.e., the classname for pojo entities)
 	//      3) Property-specific data based on the EntityMode (i.e., property-name or dom4j-node-name)
 	// The easiest way, with the introduction of the new runtime-metamodel classes, would be the
 	// the following predicates:
 	//      1) PropertyAccessorFactory.getPropertyAccessor() takes references to both a
 	//          org.hibernate.metadata.EntityModeMetadata and org.hibernate.metadata.Property
 	//      2) What is now termed a "PropertyAccessor" stores any values needed from those two
 	//          pieces of information
 	//      3) Code can then simply call PropertyAccess.getGetter() with no parameters; likewise with
 	//          PropertyAccessor.getSetter()
 
     /**
      * Retrieves a PropertyAccessor instance based on the given property definition and
      * entity mode.
      *
      * @param property The property for which to retrieve an accessor.
      * @param mode The mode for the resulting entity.
      * @return An appropriate accessor.
      * @throws MappingException
      */
 	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
 		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
 	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
 		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
 	    }
 	    else if ( EntityMode.MAP.equals( mode ) ) {
 		    return getDynamicMapPropertyAccessor();
 	    }
 	    else {
 		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
 	    }
-	}	/**
+	}
+
+	/**
+     * Retrieves a PropertyAccessor instance based on the given property definition and
+     * entity mode.
+     *
+     * @param property The property for which to retrieve an accessor.
+     * @param mode The mode for the resulting entity.
+     * @return An appropriate accessor.
+     * @throws MappingException
+     */
+	public static PropertyAccessor getPropertyAccessor(AttributeBinding property, EntityMode mode) throws MappingException {
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
+	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
+		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
+	    }
+	    else if ( EntityMode.MAP.equals( mode ) ) {
+		    return getDynamicMapPropertyAccessor();
+	    }
+	    else {
+		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
+	    }
+	}
+
+	/**
 	 * Retreives a PropertyAccessor specific for a PojoRepresentation with the given access strategy.
 	 *
 	 * @param pojoAccessorStrategy The access strategy.
 	 * @return An appropriate accessor.
 	 */
 	private static PropertyAccessor getPojoPropertyAccessor(String pojoAccessorStrategy) {
 		if ( StringHelper.isEmpty( pojoAccessorStrategy ) || "property".equals( pojoAccessorStrategy ) ) {
 			return BASIC_PROPERTY_ACCESSOR;
 		}
 		else if ( "field".equals( pojoAccessorStrategy ) ) {
 			return DIRECT_PROPERTY_ACCESSOR;
 		}
 		else if ( "embedded".equals( pojoAccessorStrategy ) ) {
 			return EMBEDDED_PROPERTY_ACCESSOR;
 		}
 		else if ( "noop".equals(pojoAccessorStrategy) ) {
 			return NOOP_ACCESSOR;
 		}
 		else {
 			return resolveCustomAccessor( pojoAccessorStrategy );
 		}
 	}
 
 	public static PropertyAccessor getDynamicMapPropertyAccessor() throws MappingException {
 		return MAP_ACCESSOR;
 	}
 
 	private static PropertyAccessor resolveCustomAccessor(String accessorName) {
 		Class accessorClass;
 		try {
 			accessorClass = ReflectHelper.classForName( accessorName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("could not find PropertyAccessor class: " + accessorName, cnfe);
 		}
 		try {
 			return (PropertyAccessor) accessorClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new MappingException("could not instantiate PropertyAccessor class: " + accessorName, e);
 		}
 	}
 
 	private PropertyAccessorFactory() {}
 
 	// todo : this eventually needs to be removed
 	public static PropertyAccessor getPropertyAccessor(Class optionalClass, String type) throws MappingException {
 		if ( type==null ) type = optionalClass==null || optionalClass==Map.class ? "map" : "property";
 		return getPropertyAccessor(type);
 	}
 
 	// todo : this eventually needs to be removed
 	public static PropertyAccessor getPropertyAccessor(String type) throws MappingException {
 		if ( type==null || "property".equals(type) ) return BASIC_PROPERTY_ACCESSOR;
 		if ( "field".equals(type) ) return DIRECT_PROPERTY_ACCESSOR;
 		if ( "map".equals(type) ) return MAP_ACCESSOR;
 		if ( "embedded".equals(type) ) return EMBEDDED_PROPERTY_ACCESSOR;
 		if ( "noop".equals(type)) return NOOP_ACCESSOR;
 
 		return resolveCustomAccessor(type);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index e5bba66811..2f23d04551 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,192 +1,382 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.tuple;
 import java.lang.reflect.Constructor;
 import org.hibernate.EntityMode;
+import org.hibernate.FetchMode;
+import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.EntityIdentifier;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public class PropertyFactory {
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(PersistentClass mappedEntity, IdentifierGenerator generator) {
 
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 		
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor(mappedEntity)
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
 					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
+	 * Generates an IdentifierProperty representation of the for a given entity mapping.
+	 *
+	 * @param mappedEntity The mapping definition of the entity.
+	 * @param generator The identifier value generator to use for this identifier.
+	 * @return The appropriate IdentifierProperty definition.
+	 */
+	public static IdentifierProperty buildIdentifierProperty(EntityBinding mappedEntity, IdentifierGenerator generator) {
+
+		final SimpleAttributeBinding property = mappedEntity.getEntityIdentifier().getValueBinding();
+
+		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
+		final String mappedUnsavedValue = property.getUnsavedValue();
+		final Type type = property.getHibernateTypeDescriptor().getExplicitType();
+
+		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
+				mappedUnsavedValue,
+				getGetter( property ),
+				type,
+				getConstructor( mappedEntity )
+			);
+
+		if ( property == null ) {
+			// this is a virtual id property...
+			return new IdentifierProperty(
+			        type,
+					mappedEntity.getEntityIdentifier().isEmbedded(),
+					mappedEntity.getEntityIdentifier().isIdentifierMapper(),
+					unsavedValue,
+					generator
+				);
+		}
+		else {
+			return new IdentifierProperty(
+					property.getAttribute().getName(),
+					property.getNodeName(),
+					type,
+					mappedEntity.getEntityIdentifier().isEmbedded(),
+					unsavedValue,
+					generator
+				);
+		}
+	}
+
+	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(Property property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 		
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
-				mappedUnsavedValue, 
+				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
 			);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 		        property.getName(),
 		        property.getNodeName(),
 		        property.getValue().getType(),
 		        lazy,
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				property.isUpdateable() && !lazy,
 				property.isOptimisticLocked(),
 		        property.getCascadeStyle(),
 		        unsavedValue
 			);
 	}
 
 	/**
+	 * Generates a VersionProperty representation for an entity mapping given its
+	 * version mapping Property.
+	 *
+	 * @param property The version mapping Property.
+	 * @param lazyAvailable Is property lazy loading currently available.
+	 * @return The appropriate VersionProperty definition.
+	 */
+	public static VersionProperty buildVersionProperty(SimpleAttributeBinding property, boolean lazyAvailable) {
+		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
+
+		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
+				mappedUnsavedValue,
+				getGetter( property ),
+				( VersionType ) property.getHibernateTypeDescriptor().getExplicitType(),
+				getConstructor( property.getEntityBinding() )
+		);
+
+		boolean lazy = lazyAvailable && property.isLazy();
+
+		return new VersionProperty(
+		        property.getAttribute().getName(),
+		        property.getNodeName(),
+		        property.getHibernateTypeDescriptor().getExplicitType(),
+		        lazy,
+				property.isInsertable(),
+				property.isUpdatable(),
+		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
+				property.getGeneration() == PropertyGeneration.ALWAYS,
+				property.isNullable(),
+				property.isUpdatable() && !lazy,
+				property.isOptimisticLockable(),
+				// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
+				//property.getCascadeStyle(),
+				CascadeStyle.NONE,
+		        unsavedValue
+			);
+	}
+
+	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
 		
 		final Type type = property.getValue().getType();
 		
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 		
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 		
 		boolean alwaysDirtyCheck = type.isAssociationType() && 
 				( (AssociationType) type ).isAlwaysDirtyChecked(); 
 
 		return new StandardProperty(
 				property.getName(),
 				property.getNodeName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
 		        property.getValue().getFetchMode()
 			);
 	}
 
+	/**
+	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
+	 * mapped property.
+	 *
+	 * @param property The mapped property.
+	 * @param lazyAvailable Is property lazy loading currently available.
+	 * @return The appropriate StandardProperty definition.
+	 */
+	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
+
+		final Type type = property.getHibernateTypeDescriptor().getExplicitType();
+
+		// we need to dirty check collections, since they can cause an owner
+		// version number increment
+
+		// we need to dirty check many-to-ones with not-found="ignore" in order
+		// to update the cache (not the database), since in this case a null
+		// entity reference can lose information
+
+		boolean alwaysDirtyCheck = type.isAssociationType() &&
+				( (AssociationType) type ).isAlwaysDirtyChecked();
+
+		if ( property.isSimpleValue() ) {
+			SimpleAttributeBinding simpleProperty = ( SimpleAttributeBinding ) property;
+			return new StandardProperty(
+					simpleProperty.getAttribute().getName(),
+					simpleProperty.getNodeName(),
+					type,
+					lazyAvailable && simpleProperty.isLazy(),
+					simpleProperty.isInsertable(),
+					simpleProperty.isUpdatable(),
+					simpleProperty.getGeneration() == PropertyGeneration.INSERT || simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
+					simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
+					simpleProperty.isNullable(),
+					alwaysDirtyCheck || simpleProperty.isUpdatable(),
+					simpleProperty.isOptimisticLockable(),
+					// TODO: get cascadeStyle from simpleProperty when HHH-6355 is fixed; for now, assume NONE
+					//simpleProperty.getCascadeStyle(),
+					CascadeStyle.NONE,
+					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
+					//simpleProperty.getFetchMode()
+					FetchMode.DEFAULT
+				);
+		}
+		else {
+			PluralAttributeBinding pluralProperty = ( PluralAttributeBinding ) property;
+
+			return new StandardProperty(
+					pluralProperty.getAttribute().getName(),
+					pluralProperty.getNodeName(),
+					type,
+					lazyAvailable && pluralProperty.isLazy(),
+					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
+					// pluralProperty.isInsertable(),
+					//pluralProperty.isUpdatable(),
+					true,
+					true,
+					false,
+					false,
+					pluralProperty.isNullable(),
+					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
+					//alwaysDirtyCheck || pluralProperty.isUpdatable(),
+					true,
+					pluralProperty.isOptimisticLocked(),
+					// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
+					//pluralProperty.getCascadeStyle(),
+					CascadeStyle.NONE,
+					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
+					//pluralProperty.getFetchMode()
+					FetchMode.DEFAULT
+				);
+		}
+	}
+
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
+	private static Constructor getConstructor(EntityBinding entityBinding) {
+		if ( entityBinding == null || entityBinding.getEntity().getJavaType() == null ) {
+			return null;
+		}
+
+		try {
+			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getJavaType().getClassReference() );
+		}
+		catch( Throwable t ) {
+			return null;
+		}
+	}
+
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
+	private static Getter getGetter(AttributeBinding mappingProperty) {
+		if ( mappingProperty == null || mappingProperty.getEntityBinding().getEntity().getJavaType() == null ) {
+			return null;
+		}
+
+		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
+		return pa.getGetter(
+				mappingProperty.getEntityBinding().getEntity().getJavaType().getClassReference(),
+				mappingProperty.getAttribute().getName()
+		);
+	}
+
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index a4caad1f5b..85ae60e4a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,933 +1,930 @@
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
 
-		// TODO: fix this when Type is available (HHH-6360)
-		//identifierProperty = PropertyFactory.buildIdentifierProperty(
-		//        entityBinding,
-		//        sessionFactory.getIdentifierGenerator( rootName )
-		//);
-		identifierProperty = null;
+		identifierProperty = PropertyFactory.buildIdentifierProperty(
+		        entityBinding,
+		        sessionFactory.getIdentifierGenerator( rootName )
+		);
 
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
 
-			// TODO: fix this when Type is available (HHH-6360)
-			//if ( attributeBinding == entityBinding.getVersioningValueBinding() ) {
-			//	tempVersionProperty = i;
-			//	properties[i] = PropertyFactory.buildVersionProperty( entityBinding.getVersioningValueBinding(), lazyAvailable );
-			//}
-			//else {
-			//	properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, lazyAvailable );
-			//}
+			if ( attributeBinding == entityBinding.getVersioningValueBinding() ) {
+				tempVersionProperty = i;
+				properties[i] = PropertyFactory.buildVersionProperty( entityBinding.getVersioningValueBinding(), lazyAvailable );
+			}
+			else {
+				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, lazyAvailable );
+			}
 
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
 		Class<EntityTuplizer> tuplizerClass = entityBinding.getEntityTuplizerClass();
 
 		// TODO: fix this when integrated into tuplizers (HHH-6359)
 		//if ( tuplizerClass == null ) {
 		//	entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		//}
 		//else {
 		//	entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		//}
 		entityTuplizer = null;
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
