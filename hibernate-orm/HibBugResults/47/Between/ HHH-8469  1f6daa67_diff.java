diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
index bccd2d9a36..22d8e47c53 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
@@ -1,401 +1,523 @@
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
-import java.util.HashMap;
-import java.util.Map;
+
 import javax.persistence.AssociationOverride;
 import javax.persistence.AssociationOverrides;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Column;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.MappedSuperclass;
+import java.util.HashMap;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
 
+import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * No idea.
  *
  * @author Emmanuel Bernard
  */
 public abstract class AbstractPropertyHolder implements PropertyHolder {
+	private static final Logger log = CoreLogging.logger( AbstractPropertyHolder.class );
+
 	protected AbstractPropertyHolder parent;
 	private Map<String, Column[]> holderColumnOverride;
 	private Map<String, Column[]> currentPropertyColumnOverride;
 	private Map<String, JoinColumn[]> holderJoinColumnOverride;
 	private Map<String, JoinColumn[]> currentPropertyJoinColumnOverride;
 	private Map<String, JoinTable> holderJoinTableOverride;
 	private Map<String, JoinTable> currentPropertyJoinTableOverride;
 	private String path;
 	private Mappings mappings;
 	private Boolean isInIdClass;
 
-
 	AbstractPropertyHolder(
 			String path,
 			PropertyHolder parent,
 			XClass clazzToProcess,
 			Mappings mappings) {
 		this.path = path;
 		this.parent = (AbstractPropertyHolder) parent;
 		this.mappings = mappings;
 		buildHierarchyColumnOverride( clazzToProcess );
 	}
 
+	protected abstract String normalizeCompositePathForLogging(String attributeName);
+	protected abstract String normalizeCompositePath(String attributeName);
+
+	protected abstract AttributeConversionInfo locateAttributeConversionInfo(XProperty property);
+	protected abstract AttributeConversionInfo locateAttributeConversionInfo(String path);
+
+	@Override
+	public AttributeConverterDefinition resolveAttributeConverterDefinition(XProperty property) {
+		AttributeConversionInfo info = locateAttributeConversionInfo( property );
+		if ( info != null ) {
+			if ( info.isConversionDisabled() ) {
+				return null;
+			}
+			else {
+				try {
+					return makeAttributeConverterDefinition( info );
+				}
+				catch (Exception e) {
+					throw new IllegalStateException(
+							String.format( "Unable to instantiate AttributeConverter [%s", info.getConverterClass().getName() ),
+							e
+					);
+				}
+			}
+		}
+
+		// look for auto applied converters
+		// todo : hook in the orm.xml local entity work brett did
+		// for now, just look in global converters
+
+
+		log.debugf( "Attempting to locate auto-apply AttributeConverter for property [%s:%s]", path, property.getName() );
+
+		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
+		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
+			if ( ! attributeConverterDefinition.isAutoApply() ) {
+				continue;
+			}
+			log.debugf(
+					"Checking auto-apply AttributeConverter [%s] type [%s] for match [%s]",
+					attributeConverterDefinition.toString(),
+					attributeConverterDefinition.getEntityAttributeType().getSimpleName(),
+					propertyType.getSimpleName()
+			);
+			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
+				return attributeConverterDefinition;
+			}
+		}
+
+		return null;
+	}
+
+	private AttributeConverterDefinition makeAttributeConverterDefinition(AttributeConversionInfo conversion) {
+		try {
+			return new AttributeConverterDefinition( conversion.getConverterClass().newInstance(), false );
+		}
+		catch (Exception e) {
+			throw new AnnotationException( "Unable to create AttributeConverter instance", e );
+		}
+	}
+
+	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
+		if ( converterDefinedType == null ) {
+			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
+		}
+		if ( propertyType == null ) {
+			throw new AnnotationException( "Property defined java type cannot be null" );
+		}
+
+		return converterDefinedType.equals( propertyType )
+				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
+	}
+
+	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
+		if ( converterDefinedType.isPrimitive() ) {
+			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
+		}
+		else if ( propertyType.isPrimitive() ) {
+			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
+		}
+		return false;
+	}
+
+	private static Class getWrapperEquivalent(Class primitive) {
+		if ( ! primitive.isPrimitive() ) {
+			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
+		}
+
+		if ( boolean.class.equals( primitive ) ) {
+			return Boolean.class;
+		}
+		else if ( char.class.equals( primitive ) ) {
+			return Character.class;
+		}
+		else if ( byte.class.equals( primitive ) ) {
+			return Byte.class;
+		}
+		else if ( short.class.equals( primitive ) ) {
+			return Short.class;
+		}
+		else if ( int.class.equals( primitive ) ) {
+			return Integer.class;
+		}
+		else if ( long.class.equals( primitive ) ) {
+			return Long.class;
+		}
+		else if ( float.class.equals( primitive ) ) {
+			return Float.class;
+		}
+		else if ( double.class.equals( primitive ) ) {
+			return Double.class;
+		}
+
+		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
+	}
+
 	@Override
 	public boolean isInIdClass() {
 		return isInIdClass != null ? isInIdClass : parent != null ? parent.isInIdClass() : false;
 	}
 
 	@Override
 	public void setInIdClass(Boolean isInIdClass) {
 		this.isInIdClass = isInIdClass;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	/**
 	 * Get the mappings
 	 *
 	 * @return The mappings
 	 */
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	/**
 	 * Set the property be processed.  property can be null
 	 *
 	 * @param property The property
 	 */
 	protected void setCurrentProperty(XProperty property) {
 		if ( property == null ) {
 			this.currentPropertyColumnOverride = null;
 			this.currentPropertyJoinColumnOverride = null;
 			this.currentPropertyJoinTableOverride = null;
 		}
 		else {
 			this.currentPropertyColumnOverride = buildColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyColumnOverride.size() == 0 ) {
 				this.currentPropertyColumnOverride = null;
 			}
 			this.currentPropertyJoinColumnOverride = buildJoinColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinColumnOverride.size() == 0 ) {
 				this.currentPropertyJoinColumnOverride = null;
 			}
 			this.currentPropertyJoinTableOverride = buildJoinTableOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinTableOverride.size() == 0 ) {
 				this.currentPropertyJoinTableOverride = null;
 			}
 		}
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public Column[] getOverriddenColumn(String propertyName) {
 		Column[] result = getExactOverriddenColumn( propertyName );
 		if (result == null) {
 			//the commented code can be useful if people use the new prefixes on old mappings and vice versa
 			// if we enable them:
 			// WARNING: this can conflict with user's expectations if:
 	 		//  - the property uses some restricted values
 	 		//  - the user has overridden the column
 			// also change getOverriddenJoinColumn and getOverriddenJoinTable as well
 	 		
 //			if ( propertyName.contains( ".key." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".key.", ".index."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".key" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".key".length() ) + ".index"
 //						);
 //			}
 //			if ( result == null && propertyName.contains( ".value." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".value.", ".element."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".value" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".value".length() ) + ".element"
 //						);
 //			}
 			if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 				//support for non map collections where no prefix is needed
 				//TODO cache the underlying regexp
 				result = getExactOverriddenColumn( propertyName.replace( ".collection&&element.", "."  ) );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * find the overridden rules from the exact property name.
 	 */
 	private Column[] getExactOverriddenColumn(String propertyName) {
 		Column[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenColumn( propertyName );
 		}
 		if ( override == null && currentPropertyColumnOverride != null ) {
 			override = currentPropertyColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderColumnOverride != null ) {
 			override = holderColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] result = getExactOverriddenJoinColumn( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinColumn( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinColumn[] getExactOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinColumn( propertyName );
 		}
 		if ( override == null && currentPropertyJoinColumnOverride != null ) {
 			override = currentPropertyJoinColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinColumnOverride != null ) {
 			override = holderJoinColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public JoinTable getJoinTable(XProperty property) {
 		final String propertyName = StringHelper.qualify( getPath(), property.getName() );
 		JoinTable result = getOverriddenJoinTable( propertyName );
 		if (result == null) {
 			result = property.getAnnotation( JoinTable.class );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	public JoinTable getOverriddenJoinTable(String propertyName) {
 		JoinTable result = getExactOverriddenJoinTable( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinTable( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinTable getExactOverriddenJoinTable(String propertyName) {
 		JoinTable override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinTable( propertyName );
 		}
 		if ( override == null && currentPropertyJoinTableOverride != null ) {
 			override = currentPropertyJoinTableOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinTableOverride != null ) {
 			override = holderJoinTableOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	private void buildHierarchyColumnOverride(XClass element) {
 		XClass current = element;
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		Map<String, JoinColumn[]> joinColumnOverride = new HashMap<String, JoinColumn[]>();
 		Map<String, JoinTable> joinTableOverride = new HashMap<String, JoinTable>();
 		while ( current != null && !mappings.getReflectionManager().toXClass( Object.class ).equals( current ) ) {
 			if ( current.isAnnotationPresent( Entity.class ) || current.isAnnotationPresent( MappedSuperclass.class )
 					|| current.isAnnotationPresent( Embeddable.class ) ) {
 				//FIXME is embeddable override?
 				Map<String, Column[]> currentOverride = buildColumnOverride( current, getPath() );
 				Map<String, JoinColumn[]> currentJoinOverride = buildJoinColumnOverride( current, getPath() );
 				Map<String, JoinTable> currentJoinTableOverride = buildJoinTableOverride( current, getPath() );
 				currentOverride.putAll( columnOverride ); //subclasses have precedence over superclasses
 				currentJoinOverride.putAll( joinColumnOverride ); //subclasses have precedence over superclasses
 				currentJoinTableOverride.putAll( joinTableOverride ); //subclasses have precedence over superclasses
 				columnOverride = currentOverride;
 				joinColumnOverride = currentJoinOverride;
 				joinTableOverride = currentJoinTableOverride;
 			}
 			current = current.getSuperclass();
 		}
 
 		holderColumnOverride = columnOverride.size() > 0 ? columnOverride : null;
 		holderJoinColumnOverride = joinColumnOverride.size() > 0 ? joinColumnOverride : null;
 		holderJoinTableOverride = joinTableOverride.size() > 0 ? joinTableOverride : null;
 	}
 
 	private static Map<String, Column[]> buildColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		if ( element == null ) return columnOverride;
 		AttributeOverride singleOverride = element.getAnnotation( AttributeOverride.class );
 		AttributeOverrides multipleOverrides = element.getAnnotation( AttributeOverrides.class );
 		AttributeOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AttributeOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AttributeOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						new Column[] { depAttr.column() }
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinColumn[]> buildJoinColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinColumn[]> columnOverride = new HashMap<String, JoinColumn[]>();
 		if ( element == null ) return columnOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						depAttr.joinColumns()
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinTable> buildJoinTableOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinTable> tableOverride = new HashMap<String, JoinTable>();
 		if ( element == null ) return tableOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden tables
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				if ( depAttr.joinColumns().length == 0 ) {
 					tableOverride.put(
 							StringHelper.qualify( path, depAttr.name() ),
 							depAttr.joinTable()
 					);
 				}
 			}
 		}
 		return tableOverride;
 	}
 
 	@Override
 	public void setParentProperty(String parentProperty) {
 		throw new AssertionFailure( "Setting the parent property to a non component" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 3d982551b6..8ef9b03f86 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1294 +1,1300 @@
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
 
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.EnumSet;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import javax.persistence.Basic;
 import javax.persistence.Cacheable;
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.DiscriminatorType;
 import javax.persistence.DiscriminatorValue;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 import javax.persistence.IdClass;
 import javax.persistence.InheritanceType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.MapKeyJoinColumn;
 import javax.persistence.MapKeyJoinColumns;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.MapsId;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.NamedStoredProcedureQueries;
 import javax.persistence.NamedStoredProcedureQuery;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.OrderColumn;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.PrimaryKeyJoinColumns;
 import javax.persistence.SequenceGenerator;
 import javax.persistence.SharedCacheMode;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 import javax.persistence.Table;
 import javax.persistence.TableGenerator;
 import javax.persistence.UniqueConstraint;
 import javax.persistence.Version;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.Cascade;
 import org.hibernate.annotations.CascadeType;
 import org.hibernate.annotations.Check;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.Columns;
 import org.hibernate.annotations.DiscriminatorOptions;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Formula;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.GenericGenerators;
 import org.hibernate.annotations.Index;
 import org.hibernate.annotations.LazyToOne;
 import org.hibernate.annotations.LazyToOneOption;
 import org.hibernate.annotations.ListIndexBase;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.NaturalIdCache;
 import org.hibernate.annotations.NotFound;
 import org.hibernate.annotations.NotFoundAction;
 import org.hibernate.annotations.OnDelete;
 import org.hibernate.annotations.OnDeleteAction;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.ParamDef;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Parent;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortComparator;
 import org.hibernate.annotations.SortNatural;
 import org.hibernate.annotations.Source;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.TypeDef;
 import org.hibernate.annotations.TypeDefs;
 import org.hibernate.annotations.Where;
+import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;
+import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XMethod;
 import org.hibernate.annotations.common.reflection.XPackage;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.annotations.CollectionBinder;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.cfg.annotations.MapKeyColumnDelegator;
 import org.hibernate.cfg.annotations.MapKeyJoinColumnDelegator;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.annotations.PropertyBinder;
 import org.hibernate.cfg.annotations.QueryBinder;
 import org.hibernate.cfg.annotations.SimpleValueBinder;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.UnionSubclass;
 import org.jboss.logging.Logger;
 
 /**
  * JSR 175 annotation binder which reads the annotations from classes, applies the
  * principles of the EJB3 spec and produces the Hibernate configuration-time metamodel
  * (the classes in the {@code org.hibernate.mapping} package)
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public final class AnnotationBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AnnotationBinder.class.getName() );
 
     /*
      * Some design description
      * I tried to remove any link to annotation except from the 2 first level of
      * method call.
      * It'll enable to:
      *   - facilitate annotation overriding
      *   - mutualize one day xml and annotation binder (probably a dream though)
      *   - split this huge class in smaller mapping oriented classes
      *
      * bindSomething usually create the mapping container and is accessed by one of the 2 first level method
      * makeSomething usually create the mapping container and is accessed by bindSomething[else]
      * fillSomething take the container into parameter and fill it.
      */
 
 	private AnnotationBinder() {
 	}
 
 	public static void bindDefaults(Mappings mappings) {
 		Map defaults = mappings.getReflectionManager().getDefaults();
 
 		// id generators ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<SequenceGenerator> anns = ( List<SequenceGenerator> ) defaults.get( SequenceGenerator.class );
 			if ( anns != null ) {
 				for ( SequenceGenerator ann : anns ) {
 					IdGenerator idGen = buildIdGenerator( ann, mappings );
 					if ( idGen != null ) {
 						mappings.addDefaultGenerator( idGen );
 					}
 				}
 			}
 		}
 		{
 			List<TableGenerator> anns = ( List<TableGenerator> ) defaults.get( TableGenerator.class );
 			if ( anns != null ) {
 				for ( TableGenerator ann : anns ) {
 					IdGenerator idGen = buildIdGenerator( ann, mappings );
 					if ( idGen != null ) {
 						mappings.addDefaultGenerator( idGen );
 					}
 				}
 			}
 		}
 
 		// queries ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<NamedQuery> anns = ( List<NamedQuery> ) defaults.get( NamedQuery.class );
 			if ( anns != null ) {
 				for ( NamedQuery ann : anns ) {
 					QueryBinder.bindQuery( ann, mappings, true );
 				}
 			}
 		}
 		{
 			List<NamedNativeQuery> anns = ( List<NamedNativeQuery> ) defaults.get( NamedNativeQuery.class );
 			if ( anns != null ) {
 				for ( NamedNativeQuery ann : anns ) {
 					QueryBinder.bindNativeQuery( ann, mappings, true );
 				}
 			}
 		}
 
 		// result-set-mappings ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<SqlResultSetMapping> anns = ( List<SqlResultSetMapping> ) defaults.get( SqlResultSetMapping.class );
 			if ( anns != null ) {
 				for ( SqlResultSetMapping ann : anns ) {
 					QueryBinder.bindSqlResultsetMapping( ann, mappings, true );
 				}
 			}
 		}
 
 		// stored procs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			final List<NamedStoredProcedureQuery> annotations =
 					(List<NamedStoredProcedureQuery>) defaults.get( NamedStoredProcedureQuery.class );
 			if ( annotations != null ) {
 				for ( NamedStoredProcedureQuery annotation : annotations ) {
 					bindNamedStoredProcedureQuery( mappings, annotation, true );
 				}
 			}
 		}
 		{
 			final List<NamedStoredProcedureQueries> annotations =
 					(List<NamedStoredProcedureQueries>) defaults.get( NamedStoredProcedureQueries.class );
 			if ( annotations != null ) {
 				for ( NamedStoredProcedureQueries annotation : annotations ) {
 					bindNamedStoredProcedureQueries( mappings, annotation, true );
 				}
 			}
 		}
 	}
 
 	public static void bindPackage(String packageName, Mappings mappings) {
 		XPackage pckg;
 		try {
 			pckg = mappings.getReflectionManager().packageForName( packageName );
 		}
+		catch (ClassLoadingException e) {
+			LOG.packageNotFound( packageName );
+			return;
+		}
 		catch ( ClassNotFoundException cnf ) {
 			LOG.packageNotFound( packageName );
 			return;
 		}
 		if ( pckg.isAnnotationPresent( SequenceGenerator.class ) ) {
 			SequenceGenerator ann = pckg.getAnnotation( SequenceGenerator.class );
 			IdGenerator idGen = buildIdGenerator( ann, mappings );
 			mappings.addGenerator( idGen );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add sequence generator with name: {0}", idGen.getName() );
 			}
 		}
 		if ( pckg.isAnnotationPresent( TableGenerator.class ) ) {
 			TableGenerator ann = pckg.getAnnotation( TableGenerator.class );
 			IdGenerator idGen = buildIdGenerator( ann, mappings );
 			mappings.addGenerator( idGen );
 
 		}
 		bindGenericGenerators( pckg, mappings );
 		bindQueries( pckg, mappings );
 		bindFilterDefs( pckg, mappings );
 		bindTypeDefs( pckg, mappings );
 		bindFetchProfiles( pckg, mappings );
 		BinderHelper.bindAnyMetaDefs( pckg, mappings );
 
 	}
 
 	private static void bindGenericGenerators(XAnnotatedElement annotatedElement, Mappings mappings) {
 		GenericGenerator defAnn = annotatedElement.getAnnotation( GenericGenerator.class );
 		GenericGenerators defsAnn = annotatedElement.getAnnotation( GenericGenerators.class );
 		if ( defAnn != null ) {
 			bindGenericGenerator( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( GenericGenerator def : defsAnn.value() ) {
 				bindGenericGenerator( def, mappings );
 			}
 		}
 	}
 
 	private static void bindGenericGenerator(GenericGenerator def, Mappings mappings) {
 		IdGenerator idGen = buildIdGenerator( def, mappings );
 		mappings.addGenerator( idGen );
 	}
 
 	private static void bindQueries(XAnnotatedElement annotatedElement, Mappings mappings) {
 		{
 			SqlResultSetMapping ann = annotatedElement.getAnnotation( SqlResultSetMapping.class );
 			QueryBinder.bindSqlResultsetMapping( ann, mappings, false );
 		}
 		{
 			SqlResultSetMappings ann = annotatedElement.getAnnotation( SqlResultSetMappings.class );
 			if ( ann != null ) {
 				for ( SqlResultSetMapping current : ann.value() ) {
 					QueryBinder.bindSqlResultsetMapping( current, mappings, false );
 				}
 			}
 		}
 		{
 			NamedQuery ann = annotatedElement.getAnnotation( NamedQuery.class );
 			QueryBinder.bindQuery( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQuery.class
 			);
 			QueryBinder.bindQuery( ann, mappings );
 		}
 		{
 			NamedQueries ann = annotatedElement.getAnnotation( NamedQueries.class );
 			QueryBinder.bindQueries( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQueries.class
 			);
 			QueryBinder.bindQueries( ann, mappings );
 		}
 		{
 			NamedNativeQuery ann = annotatedElement.getAnnotation( NamedNativeQuery.class );
 			QueryBinder.bindNativeQuery( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQuery.class
 			);
 			QueryBinder.bindNativeQuery( ann, mappings );
 		}
 		{
 			NamedNativeQueries ann = annotatedElement.getAnnotation( NamedNativeQueries.class );
 			QueryBinder.bindNativeQueries( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQueries.class
 			);
 			QueryBinder.bindNativeQueries( ann, mappings );
 		}
 
 		// NamedStoredProcedureQuery handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		bindNamedStoredProcedureQuery( mappings, annotatedElement.getAnnotation( NamedStoredProcedureQuery.class ), false );
 
 		// NamedStoredProcedureQueries handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		bindNamedStoredProcedureQueries(
 				mappings,
 				annotatedElement.getAnnotation( NamedStoredProcedureQueries.class ),
 				false
 		);
 	}
 
 	private static void bindNamedStoredProcedureQueries(Mappings mappings, NamedStoredProcedureQueries annotation, boolean isDefault) {
 		if ( annotation != null ) {
 			for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
 				bindNamedStoredProcedureQuery( mappings, queryAnnotation, isDefault );
 			}
 		}
 	}
 
 	private static void bindNamedStoredProcedureQuery(Mappings mappings, NamedStoredProcedureQuery annotation, boolean isDefault) {
 		if ( annotation != null ) {
 			QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings, isDefault );
 		}
 	}
 
 	private static IdGenerator buildIdGenerator(java.lang.annotation.Annotation ann, Mappings mappings) {
 		IdGenerator idGen = new IdGenerator();
 		if ( mappings.getSchemaName() != null ) {
 			idGen.addParam( PersistentIdentifierGenerator.SCHEMA, mappings.getSchemaName() );
 		}
 		if ( mappings.getCatalogName() != null ) {
 			idGen.addParam( PersistentIdentifierGenerator.CATALOG, mappings.getCatalogName() );
 		}
 		final boolean useNewGeneratorMappings = mappings.useNewGeneratorMappings();
 		if ( ann == null ) {
 			idGen = null;
 		}
 		else if ( ann instanceof TableGenerator ) {
 			TableGenerator tabGen = ( TableGenerator ) ann;
 			idGen.setName( tabGen.name() );
 			if ( useNewGeneratorMappings ) {
 				idGen.setIdentifierGeneratorStrategy( org.hibernate.id.enhanced.TableGenerator.class.getName() );
 				idGen.addParam( org.hibernate.id.enhanced.TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true" );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, tabGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, tabGen.schema() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.table() ) ) {
 					idGen.addParam( org.hibernate.id.enhanced.TableGenerator.TABLE_PARAM, tabGen.table() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnName() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.SEGMENT_COLUMN_PARAM, tabGen.pkColumnName()
 					);
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnValue() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.SEGMENT_VALUE_PARAM, tabGen.pkColumnValue()
 					);
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.valueColumnName() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.VALUE_COLUMN_PARAM, tabGen.valueColumnName()
 					);
 				}
 				idGen.addParam(
 						org.hibernate.id.enhanced.TableGenerator.INCREMENT_PARAM,
 						String.valueOf( tabGen.allocationSize() )
 				);
 				// See comment on HHH-4884 wrt initialValue.  Basically initialValue is really the stated value + 1
 				idGen.addParam(
 						org.hibernate.id.enhanced.TableGenerator.INITIAL_PARAM,
 						String.valueOf( tabGen.initialValue() + 1 )
 				);
                 if (tabGen.uniqueConstraints() != null && tabGen.uniqueConstraints().length > 0) LOG.warn(tabGen.name());
 			}
 			else {
 				idGen.setIdentifierGeneratorStrategy( MultipleHiLoPerTableGenerator.class.getName() );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.table() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.ID_TABLE, tabGen.table() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, tabGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, tabGen.schema() );
 				}
 				//FIXME implement uniqueconstrains
                 if (tabGen.uniqueConstraints() != null && tabGen.uniqueConstraints().length > 0) LOG.ignoringTableGeneratorConstraints(tabGen.name());
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnName() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.PK_COLUMN_NAME, tabGen.pkColumnName() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.valueColumnName() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME, tabGen.valueColumnName() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnValue() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.PK_VALUE_NAME, tabGen.pkColumnValue() );
 				}
 				idGen.addParam( TableHiLoGenerator.MAX_LO, String.valueOf( tabGen.allocationSize() - 1 ) );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add table generator with name: {0}", idGen.getName() );
 			}
 		}
 		else if ( ann instanceof SequenceGenerator ) {
 			SequenceGenerator seqGen = ( SequenceGenerator ) ann;
 			idGen.setName( seqGen.name() );
 			if ( useNewGeneratorMappings ) {
 				idGen.setIdentifierGeneratorStrategy( SequenceStyleGenerator.class.getName() );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, seqGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, seqGen.schema() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.sequenceName() ) ) {
 					idGen.addParam( SequenceStyleGenerator.SEQUENCE_PARAM, seqGen.sequenceName() );
 				}
 				idGen.addParam( SequenceStyleGenerator.INCREMENT_PARAM, String.valueOf( seqGen.allocationSize() ) );
 				idGen.addParam( SequenceStyleGenerator.INITIAL_PARAM, String.valueOf( seqGen.initialValue() ) );
 			}
 			else {
 				idGen.setIdentifierGeneratorStrategy( "seqhilo" );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.sequenceName() ) ) {
 					idGen.addParam( org.hibernate.id.SequenceGenerator.SEQUENCE, seqGen.sequenceName() );
 				}
 				//FIXME: work on initialValue() through SequenceGenerator.PARAMETERS
 				//		steve : or just use o.h.id.enhanced.SequenceStyleGenerator
 				if ( seqGen.initialValue() != 1 ) {
 					LOG.unsupportedInitialValue( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				}
 				idGen.addParam( SequenceHiLoGenerator.MAX_LO, String.valueOf( seqGen.allocationSize() - 1 ) );
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Add sequence generator with name: {0}", idGen.getName() );
 				}
 			}
 		}
 		else if ( ann instanceof GenericGenerator ) {
 			GenericGenerator genGen = ( GenericGenerator ) ann;
 			idGen.setName( genGen.name() );
 			idGen.setIdentifierGeneratorStrategy( genGen.strategy() );
 			Parameter[] params = genGen.parameters();
 			for ( Parameter parameter : params ) {
 				idGen.addParam( parameter.name(), parameter.value() );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add generic generator with name: {0}", idGen.getName() );
 			}
 		}
 		else {
 			throw new AssertionFailure( "Unknown Generator annotation: " + ann );
 		}
 		return idGen;
 	}
 
 	/**
 	 * Bind a class having JSR175 annotations. Subclasses <b>have to</b> be bound after its parent class.
 	 *
 	 * @param clazzToProcess entity to bind as {@code XClass} instance
 	 * @param inheritanceStatePerClass Meta data about the inheritance relationships for all mapped classes
 	 * @param mappings Mapping meta data
 	 *
 	 * @throws MappingException in case there is an configuration error
 	 */
 	public static void bindClass(
 			XClass clazzToProcess,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) throws MappingException {
 		//@Entity and @MappedSuperclass on the same class leads to a NPE down the road
 		if ( clazzToProcess.isAnnotationPresent( Entity.class )
 				&&  clazzToProcess.isAnnotationPresent( MappedSuperclass.class ) ) {
 			throw new AnnotationException( "An entity cannot be annotated with both @Entity and @MappedSuperclass: "
 					+ clazzToProcess.getName() );
 		}
 
 		//TODO: be more strict with secondarytable allowance (not for ids, not for secondary table join columns etc)
 		InheritanceState inheritanceState = inheritanceStatePerClass.get( clazzToProcess );
 		AnnotatedClassType classType = mappings.getClassType( clazzToProcess );
 
 		//Queries declared in MappedSuperclass should be usable in Subclasses
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 			bindQueries( clazzToProcess, mappings );
 			bindTypeDefs( clazzToProcess, mappings );
 			bindFilterDefs( clazzToProcess, mappings );
 		}
 
 		if ( !isEntityClassType( clazzToProcess, classType ) ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding entity from annotated class: %s", clazzToProcess.getName() );
 		}
 
 		PersistentClass superEntity = getSuperEntity(
 				clazzToProcess, inheritanceStatePerClass, mappings, inheritanceState
 		);
 
 		PersistentClass persistentClass = makePersistentClass( inheritanceState, superEntity );
 		Entity entityAnn = clazzToProcess.getAnnotation( Entity.class );
 		org.hibernate.annotations.Entity hibEntityAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.Entity.class
 		);
 		EntityBinder entityBinder = new EntityBinder(
 				entityAnn, hibEntityAnn, clazzToProcess, persistentClass, mappings
 		);
 		entityBinder.setInheritanceState( inheritanceState );
 
 		bindQueries( clazzToProcess, mappings );
 		bindFilterDefs( clazzToProcess, mappings );
 		bindTypeDefs( clazzToProcess, mappings );
 		bindFetchProfiles( clazzToProcess, mappings );
 		BinderHelper.bindAnyMetaDefs( clazzToProcess, mappings );
 
 		String schema = "";
 		String table = ""; //might be no @Table annotation on the annotated class
 		String catalog = "";
 		List<UniqueConstraintHolder> uniqueConstraints = new ArrayList<UniqueConstraintHolder>();
 		javax.persistence.Table tabAnn = null;
 		if ( clazzToProcess.isAnnotationPresent( javax.persistence.Table.class ) ) {
 			tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
 			table = tabAnn.name();
 			schema = tabAnn.schema();
 			catalog = tabAnn.catalog();
 			uniqueConstraints = TableBinder.buildUniqueConstraintHolders( tabAnn.uniqueConstraints() );
 		}
 
 		Ejb3JoinColumn[] inheritanceJoinedColumns = makeInheritanceJoinColumns(
 				clazzToProcess, mappings, inheritanceState, superEntity
 		);
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			discriminatorColumn = processDiscriminatorProperties(
 					clazzToProcess, mappings, inheritanceState, entityBinder
 			);
 		}
 
 		entityBinder.setProxy( clazzToProcess.getAnnotation( Proxy.class ) );
 		entityBinder.setBatchSize( clazzToProcess.getAnnotation( BatchSize.class ) );
 		entityBinder.setWhere( clazzToProcess.getAnnotation( Where.class ) );
 	    entityBinder.setCache( determineCacheSettings( clazzToProcess, mappings ) );
 	    entityBinder.setNaturalIdCache( clazzToProcess, clazzToProcess.getAnnotation( NaturalIdCache.class ) );
 
 		bindFilters( clazzToProcess, entityBinder, mappings );
 
 		entityBinder.bindEntity();
 
 		if ( inheritanceState.hasTable() ) {
 			Check checkAnn = clazzToProcess.getAnnotation( Check.class );
 			String constraints = checkAnn == null ?
 					null :
 					checkAnn.constraints();
 			entityBinder.bindTable(
 					schema, catalog, table, uniqueConstraints,
 					constraints, inheritanceState.hasDenormalizedTable() ?
 							superEntity.getTable() :
 							null
 			);
 		}
 		else if ( clazzToProcess.isAnnotationPresent( Table.class ) ) {
 			LOG.invalidTableAnnotation( clazzToProcess.getName() );
 		}
 
 
 		PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(
 				clazzToProcess,
 				persistentClass,
 				entityBinder, mappings, inheritanceStatePerClass
 		);
 
 		javax.persistence.SecondaryTable secTabAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTable.class
 		);
 		javax.persistence.SecondaryTables secTabsAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTables.class
 		);
 		entityBinder.firstLevelSecondaryTablesBinding( secTabAnn, secTabsAnn );
 
 		OnDelete onDeleteAnn = clazzToProcess.getAnnotation( OnDelete.class );
 		boolean onDeleteAppropriate = false;
 		if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) && inheritanceState.hasParents() ) {
 			onDeleteAppropriate = true;
 			final JoinedSubclass jsc = ( JoinedSubclass ) persistentClass;
 			SimpleValue key = new DependantValue( mappings, jsc.getTable(), jsc.getIdentifier() );
 			jsc.setKey( key );
 			ForeignKey fk = clazzToProcess.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			if ( onDeleteAnn != null ) {
 				key.setCascadeDeleteEnabled( OnDeleteAction.CASCADE.equals( onDeleteAnn.action() ) );
 			}
 			else {
 				key.setCascadeDeleteEnabled( false );
 			}
 			//we are never in a second pass at that stage, so queue it
 			SecondPass sp = new JoinedSubclassFkSecondPass( jsc, inheritanceJoinedColumns, key, mappings );
 			mappings.addSecondPass( sp );
 			mappings.addSecondPass( new CreateKeySecondPass( jsc ) );
 
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			if ( ! inheritanceState.hasParents() ) {
 				if ( inheritanceState.hasSiblings() || !discriminatorColumn.isImplicit() ) {
 					//need a discriminator column
 					bindDiscriminatorToPersistentClass(
 							( RootClass ) persistentClass,
 							discriminatorColumn,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							mappings
 					);
 					entityBinder.bindDiscriminatorValue();//bind it again since the type might have changed
 				}
 			}
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			//nothing to do
 		}
         if (onDeleteAnn != null && !onDeleteAppropriate) LOG.invalidOnDeleteAnnotation(propertyHolder.getEntityName());
 
 		// try to find class level generators
 		HashMap<String, IdGenerator> classGenerators = buildLocalGenerators( clazzToProcess, mappings );
 
 		// check properties
 		final InheritanceState.ElementsToProcess elementsToProcess = inheritanceState.getElementsToProcess();
 		inheritanceState.postProcess( persistentClass, entityBinder );
 
 		final boolean subclassAndSingleTableStrategy = inheritanceState.getType() == InheritanceType.SINGLE_TABLE
 				&& inheritanceState.hasParents();
 		Set<String> idPropertiesIfIdClass = new HashSet<String>();
 		boolean isIdClass = mapAsIdClass(
 				inheritanceStatePerClass,
 				inheritanceState,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				elementsToProcess,
 				idPropertiesIfIdClass,
 				mappings
 		);
 
 		if ( !isIdClass ) {
 			entityBinder.setWrapIdsInEmbeddedComponents( elementsToProcess.getIdPropertyCount() > 1 );
 		}
 
 		processIdPropertiesIfNotAlready(
 				inheritanceStatePerClass,
 				mappings,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				classGenerators,
 				elementsToProcess,
 				subclassAndSingleTableStrategy,
 				idPropertiesIfIdClass
 		);
 
 		if ( !inheritanceState.hasParents() ) {
 			final RootClass rootClass = ( RootClass ) persistentClass;
 			mappings.addSecondPass( new CreateKeySecondPass( rootClass ) );
 		}
 		else {
 			superEntity.addSubclass( ( Subclass ) persistentClass );
 		}
 
 		mappings.addClass( persistentClass );
 
 		//Process secondary tables and complementary definitions (ie o.h.a.Table)
 		mappings.addSecondPass( new SecondaryTableSecondPass( entityBinder, propertyHolder, clazzToProcess ) );
 
 		//add process complementary Table definition (index & all)
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Table.class ) );
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Tables.class ) );
 		entityBinder.processComplementaryTableDefinitions( tabAnn );
 	}
 
 	// parse everything discriminator column relevant in case of single table inheritance
 	private static Ejb3DiscriminatorColumn processDiscriminatorProperties(XClass clazzToProcess, Mappings mappings, InheritanceState inheritanceState, EntityBinder entityBinder) {
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		javax.persistence.DiscriminatorColumn discAnn = clazzToProcess.getAnnotation(
 				javax.persistence.DiscriminatorColumn.class
 		);
 		DiscriminatorType discriminatorType = discAnn != null ?
 				discAnn.discriminatorType() :
 				DiscriminatorType.STRING;
 
 		org.hibernate.annotations.DiscriminatorFormula discFormulaAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.DiscriminatorFormula.class
 		);
 		if ( !inheritanceState.hasParents() ) {
 			discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
 					discriminatorType, discAnn, discFormulaAnn, mappings
 			);
 		}
 		if ( discAnn != null && inheritanceState.hasParents() ) {
 			LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 		}
 
 		String discrimValue = clazzToProcess.isAnnotationPresent( DiscriminatorValue.class ) ?
 				clazzToProcess.getAnnotation( DiscriminatorValue.class ).value() :
 				null;
 		entityBinder.setDiscriminatorValue( discrimValue );
 
 		DiscriminatorOptions discriminatorOptions = clazzToProcess.getAnnotation( DiscriminatorOptions.class );
 		if ( discriminatorOptions != null) {
 			entityBinder.setForceDiscriminator( discriminatorOptions.force() );
 			entityBinder.setInsertableDiscriminator( discriminatorOptions.insert() );
 		}
 
 		return discriminatorColumn;
 	}
 
 	private static void processIdPropertiesIfNotAlready(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			HashMap<String, IdGenerator> classGenerators,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			boolean subclassAndSingleTableStrategy,
 			Set<String> idPropertiesIfIdClass) {
 		Set<String> missingIdProperties = new HashSet<String>( idPropertiesIfIdClass );
 		for ( PropertyData propertyAnnotatedElement : elementsToProcess.getElements() ) {
 			String propertyName = propertyAnnotatedElement.getPropertyName();
 			if ( !idPropertiesIfIdClass.contains( propertyName ) ) {
 				processElementAnnotations(
 						propertyHolder,
 						subclassAndSingleTableStrategy ?
 								Nullability.FORCED_NULL :
 								Nullability.NO_CONSTRAINT,
 						propertyAnnotatedElement, classGenerators, entityBinder,
 						false, false, false, mappings, inheritanceStatePerClass
 				);
 			}
 			else {
 				missingIdProperties.remove( propertyName );
 			}
 		}
 
 		if ( missingIdProperties.size() != 0 ) {
 			StringBuilder missings = new StringBuilder();
 			for ( String property : missingIdProperties ) {
 				missings.append( property ).append( ", " );
 			}
 			throw new AnnotationException(
 					"Unable to find properties ("
 							+ missings.substring( 0, missings.length() - 2 )
 							+ ") in entity annotated with @IdClass:" + persistentClass.getEntityName()
 			);
 		}
 	}
 
 	private static boolean mapAsIdClass(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			InheritanceState inheritanceState,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			Set<String> idPropertiesIfIdClass,
 			Mappings mappings) {
 		/*
 		 * We are looking for @IdClass
 		 * In general we map the id class as identifier using the mapping metadata of the main entity's properties
 		 * and we create an identifier mapper containing the id properties of the main entity
 		 *
 		 * In JPA 2, there is a shortcut if the id class is the Pk of the associated class pointed to by the id
 		 * it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 		 */
 		XClass classWithIdClass = inheritanceState.getClassWithIdClass( false );
 		if ( classWithIdClass != null ) {
 			IdClass idClass = classWithIdClass.getAnnotation( IdClass.class );
 			XClass compositeClass = mappings.getReflectionManager().toXClass( idClass.value() );
 			PropertyData inferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", compositeClass
 			);
 			PropertyData baseInferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", classWithIdClass
 			);
 			AccessType propertyAccessor = entityBinder.getPropertyAccessor( compositeClass );
 			//In JPA 2, there is a shortcut if the IdClass is the Pk of the associated class pointed to by the id
 			//it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 			final boolean isFakeIdClass = isIdClassPkOfTheAssociatedEntity(
 					elementsToProcess,
 					compositeClass,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					inheritanceStatePerClass,
 					mappings
 			);
 
 			if ( isFakeIdClass ) {
 				return false;
 			}
 
 			boolean isComponent = true;
 			String generatorType = "assigned";
 			String generator = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 			boolean ignoreIdAnnotations = entityBinder.isIgnoreIdAnnotations();
 			entityBinder.setIgnoreIdAnnotations( true );
 			propertyHolder.setInIdClass( true );
 			bindIdClass(
 					generatorType,
 					generator,
 					inferredData,
 					baseInferredData,
 					null,
 					propertyHolder,
 					isComponent,
 					propertyAccessor,
 					entityBinder,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			propertyHolder.setInIdClass( null );
 			inferredData = new PropertyPreloadedData(
 					propertyAccessor, "_identifierMapper", compositeClass
 			);
 			Component mapper = fillComponent(
 					propertyHolder,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					false,
 					entityBinder,
 					true,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			entityBinder.setIgnoreIdAnnotations( ignoreIdAnnotations );
 			persistentClass.setIdentifierMapper( mapper );
 
 			//If id definition is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass =
 					BinderHelper.getMappedSuperclassOrNull(
 							inferredData.getDeclaringClass(),
 							inheritanceStatePerClass,
 							mappings
 					);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierMapper( mapper );
 			}
 			else {
 				//we are for sure on the entity
 				persistentClass.setDeclaredIdentifierMapper( mapper );
 			}
 
 			Property property = new Property();
 			property.setName( "_identifierMapper" );
 			property.setNodeName( "id" );
 			property.setUpdateable( false );
 			property.setInsertable( false );
 			property.setValue( mapper );
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty( property );
 			entityBinder.setIgnoreIdAnnotations( true );
 
 			Iterator properties = mapper.getPropertyIterator();
 			while ( properties.hasNext() ) {
 				idPropertiesIfIdClass.add( ( ( Property ) properties.next() ).getName() );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static boolean isIdClassPkOfTheAssociatedEntity(
 			InheritanceState.ElementsToProcess elementsToProcess,
 			XClass compositeClass,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) {
 		if ( elementsToProcess.getIdPropertyCount() == 1 ) {
 			final PropertyData idPropertyOnBaseClass = getUniqueIdPropertyFromBaseClass(
 					inferredData, baseInferredData, propertyAccessor, mappings
 			);
 			final InheritanceState state = inheritanceStatePerClass.get( idPropertyOnBaseClass.getClassOrElement() );
 			if ( state == null ) {
 				return false; //while it is likely a user error, let's consider it is something that might happen
 			}
 			final XClass associatedClassWithIdClass = state.getClassWithIdClass( true );
 			if ( associatedClassWithIdClass == null ) {
 				//we cannot know for sure here unless we try and find the @EmbeddedId
 				//Let's not do this thorough checking but do some extra validation
 				final XProperty property = idPropertyOnBaseClass.getProperty();
 				return property.isAnnotationPresent( ManyToOne.class )
 						|| property.isAnnotationPresent( OneToOne.class );
 
 			}
 			else {
 				final XClass idClass = mappings.getReflectionManager().toXClass(
 						associatedClassWithIdClass.getAnnotation( IdClass.class ).value()
 				);
 				return idClass.equals( compositeClass );
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static Cache determineCacheSettings(XClass clazzToProcess, Mappings mappings) {
 		Cache cacheAnn = clazzToProcess.getAnnotation( Cache.class );
 		if ( cacheAnn != null ) {
 			return cacheAnn;
 		}
 
 		Cacheable cacheableAnn = clazzToProcess.getAnnotation( Cacheable.class );
 		SharedCacheMode mode = determineSharedCacheMode( mappings );
 		switch ( mode ) {
 			case ALL: {
 				cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheableAnn != null && cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheableAnn == null || cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		return cacheAnn;
 	}
 
 	private static SharedCacheMode determineSharedCacheMode(Mappings mappings) {
 		SharedCacheMode mode;
 		final Object value = mappings.getConfigurationProperties().get( "javax.persistence.sharedCache.mode" );
 		if ( value == null ) {
 			LOG.debug( "No value specified for 'javax.persistence.sharedCache.mode'; using UNSPECIFIED" );
 			mode = SharedCacheMode.UNSPECIFIED;
 		}
 		else {
 			if ( SharedCacheMode.class.isInstance( value ) ) {
 				mode = ( SharedCacheMode ) value;
 			}
 			else {
 				try {
 					mode = SharedCacheMode.valueOf( value.toString() );
 				}
 				catch ( Exception e ) {
 					LOG.debugf( "Unable to resolve given mode name [%s]; using UNSPECIFIED : %s", value, e );
 					mode = SharedCacheMode.UNSPECIFIED;
 				}
 			}
 		}
 		return mode;
 	}
 
 	private static Cache buildCacheMock(String region, Mappings mappings) {
 		return new LocalCacheAnnotationImpl( region, determineCacheConcurrencyStrategy( mappings ) );
 	}
 
 	private static CacheConcurrencyStrategy DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	static void prepareDefaultCacheConcurrencyStrategy(Properties properties) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY != null ) {
 			LOG.trace( "Default cache concurrency strategy already defined" );
 			return;
 		}
 
 		if ( !properties.containsKey( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
 			LOG.trace( "Given properties did not contain any default cache concurrency strategy setting" );
 			return;
 		}
 
 		final String strategyName = properties.getProperty( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
 		LOG.tracev( "Discovered default cache concurrency strategy via config [{0}]", strategyName );
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.parse( strategyName );
 		if ( strategy == null ) {
 			LOG.trace( "Discovered default cache concurrency strategy specified nothing" );
 			return;
 		}
 
 		LOG.debugf( "Setting default cache concurrency strategy via config [%s]", strategy.name() );
 		DEFAULT_CACHE_CONCURRENCY_STRATEGY = strategy;
 	}
 
 	private static CacheConcurrencyStrategy determineCacheConcurrencyStrategy(Mappings mappings) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY == null ) {
 			final RegionFactory cacheRegionFactory = SettingsFactory.createRegionFactory(
 					mappings.getConfigurationProperties(), true
 			);
 			DEFAULT_CACHE_CONCURRENCY_STRATEGY = CacheConcurrencyStrategy.fromAccessType( cacheRegionFactory.getDefaultAccessType() );
 		}
 		return DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 	}
 
 	@SuppressWarnings({ "ClassExplicitlyAnnotation" })
 	private static class LocalCacheAnnotationImpl implements Cache {
 		private final String region;
 		private final CacheConcurrencyStrategy usage;
 
 		private LocalCacheAnnotationImpl(String region, CacheConcurrencyStrategy usage) {
 			this.region = region;
 			this.usage = usage;
 		}
 
 		public CacheConcurrencyStrategy usage() {
 			return usage;
 		}
 
 		public String region() {
 			return region;
 		}
 
 		public String include() {
 			return "all";
 		}
 
 		public Class<? extends Annotation> annotationType() {
 			return Cache.class;
 		}
 	}
 
 	private static PersistentClass makePersistentClass(InheritanceState inheritanceState, PersistentClass superEntity) {
 		//we now know what kind of persistent entity it is
 		PersistentClass persistentClass;
 		//create persistent class
 		if ( !inheritanceState.hasParents() ) {
 			persistentClass = new RootClass();
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			persistentClass = new SingleTableSubclass( superEntity );
 		}
 		else if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			persistentClass = new JoinedSubclass( superEntity );
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			persistentClass = new UnionSubclass( superEntity );
 		}
 		else {
 			throw new AssertionFailure( "Unknown inheritance type: " + inheritanceState.getType() );
 		}
 		return persistentClass;
 	}
 
 	private static Ejb3JoinColumn[] makeInheritanceJoinColumns(
 			XClass clazzToProcess,
 			Mappings mappings,
 			InheritanceState inheritanceState,
 			PersistentClass superEntity) {
 		Ejb3JoinColumn[] inheritanceJoinedColumns = null;
 		final boolean hasJoinedColumns = inheritanceState.hasParents()
 				&& InheritanceType.JOINED.equals( inheritanceState.getType() );
 		if ( hasJoinedColumns ) {
 			//@Inheritance(JOINED) subclass need to link back to the super entity
 			PrimaryKeyJoinColumns jcsAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumns.class );
 			boolean explicitInheritanceJoinedColumns = jcsAnn != null && jcsAnn.value().length != 0;
 			if ( explicitInheritanceJoinedColumns ) {
 				int nbrOfInhJoinedColumns = jcsAnn.value().length;
 				PrimaryKeyJoinColumn jcAnn;
 				inheritanceJoinedColumns = new Ejb3JoinColumn[nbrOfInhJoinedColumns];
 				for ( int colIndex = 0; colIndex < nbrOfInhJoinedColumns; colIndex++ ) {
 					jcAnn = jcsAnn.value()[colIndex];
 					inheritanceJoinedColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 							jcAnn, null, superEntity.getIdentifier(),
 							null, null, mappings
 					);
 				}
 			}
 			else {
 				PrimaryKeyJoinColumn jcAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumn.class );
 				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
 				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						jcAnn, null, superEntity.getIdentifier(),
 						null, null, mappings
 				);
 			}
 			LOG.trace( "Subclass joined column(s) created" );
 		}
 		else {
 			if ( clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumns.class )
 					|| clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumn.class ) ) {
 				LOG.invalidPrimaryKeyJoinColumnAnnotation();
 			}
 		}
 		return inheritanceJoinedColumns;
 	}
 
 	private static PersistentClass getSuperEntity(XClass clazzToProcess, Map<XClass, InheritanceState> inheritanceStatePerClass, Mappings mappings, InheritanceState inheritanceState) {
 		InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 				clazzToProcess, inheritanceStatePerClass
 		);
 		PersistentClass superEntity = superEntityState != null ?
 				mappings.getClass(
 						superEntityState.getClazz().getName()
 				) :
 				null;
 		if ( superEntity == null ) {
 			//check if superclass is not a potential persistent class
 			if ( inheritanceState.hasParents() ) {
 				throw new AssertionFailure(
 						"Subclass has to be binded after it's mother class: "
 								+ superEntityState.getClazz().getName()
 				);
 			}
 		}
 		return superEntity;
 	}
 
 	private static boolean isEntityClassType(XClass clazzToProcess, AnnotatedClassType classType) {
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) //will be processed by their subentities
 				|| AnnotatedClassType.NONE.equals( classType ) //to be ignored
 				|| AnnotatedClassType.EMBEDDABLE.equals( classType ) //allow embeddable element declaration
 				) {
 			if ( AnnotatedClassType.NONE.equals( classType )
 					&& clazzToProcess.isAnnotationPresent( org.hibernate.annotations.Entity.class ) ) {
 				LOG.missingEntityAnnotation( clazzToProcess.getName() );
 			}
 			return false;
 		}
 
 		if ( !classType.equals( AnnotatedClassType.ENTITY ) ) {
 			throw new AnnotationException(
 					"Annotated class should have a @javax.persistence.Entity, @javax.persistence.Embeddable or @javax.persistence.EmbeddedSuperclass annotation: " + clazzToProcess
 							.getName()
 			);
 		}
 
 		return true;
 	}
 
 	/*
 	 * Process the filters defined on the given class, as well as all filters defined
 	 * on the MappedSuperclass(s) in the inheritance hierarchy
 	 */
 
 	private static void bindFilters(XClass annotatedClass, EntityBinder entityBinder,
 									Mappings mappings) {
 
 		bindFilters( annotatedClass, entityBinder );
 
 		XClass classToProcess = annotatedClass.getSuperclass();
 		while ( classToProcess != null ) {
 			AnnotatedClassType classType = mappings.getClassType( classToProcess );
 			if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 				bindFilters( classToProcess, entityBinder );
 			}
 			classToProcess = classToProcess.getSuperclass();
 		}
 
 	}
 
 	private static void bindFilters(XAnnotatedElement annotatedElement, EntityBinder entityBinder) {
 
 		Filters filtersAnn = annotatedElement.getAnnotation( Filters.class );
 		if ( filtersAnn != null ) {
 			for ( Filter filter : filtersAnn.value() ) {
 				entityBinder.addFilter(filter);
 			}
 		}
 
 		Filter filterAnn = annotatedElement.getAnnotation( Filter.class );
 		if ( filterAnn != null ) {
 			entityBinder.addFilter(filterAnn);
 		}
 	}
 
 	private static void bindFilterDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FilterDef defAnn = annotatedElement.getAnnotation( FilterDef.class );
 		FilterDefs defsAnn = annotatedElement.getAnnotation( FilterDefs.class );
 		if ( defAnn != null ) {
 			bindFilterDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( FilterDef def : defsAnn.value() ) {
 				bindFilterDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFilterDef(FilterDef defAnn, Mappings mappings) {
@@ -1418,1757 +1424,1765 @@ public final class AnnotationBinder {
 		for ( XProperty p : properties ) {
 			final int currentIdPropertyCounter = addProperty(
 					propertyContainer, p, elements, accessType.getType(), mappings
 			);
 			idPropertyCounter += currentIdPropertyCounter;
 		}
 		return idPropertyCounter;
 	}
 
 	private static int addProperty(
 			PropertyContainer propertyContainer,
 			XProperty property,
 			List<PropertyData> annElts,
 			String propertyAccessor,
 			Mappings mappings) {
 		final XClass declaringClass = propertyContainer.getDeclaringClass();
 		final XClass entity = propertyContainer.getEntityAtStake();
 		int idPropertyCounter = 0;
 		PropertyData propertyAnnotatedElement = new PropertyInferredData(
 				declaringClass, property, propertyAccessor,
 				mappings.getReflectionManager()
 		);
 
 		/*
 		 * put element annotated by @Id in front
 		 * since it has to be parsed before any association by Hibernate
 		 */
 		final XAnnotatedElement element = propertyAnnotatedElement.getProperty();
 		if ( element.isAnnotationPresent( Id.class ) || element.isAnnotationPresent( EmbeddedId.class ) ) {
 			annElts.add( 0, propertyAnnotatedElement );
 			/**
 			 * The property must be put in hibernate.properties as it's a system wide property. Fixable?
 			 * TODO support true/false/default on the property instead of present / not present
 			 * TODO is @Column mandatory?
 			 * TODO add method support
 			 */
 			if ( mappings.isSpecjProprietarySyntaxEnabled() ) {
 				if ( element.isAnnotationPresent( Id.class ) && element.isAnnotationPresent( Column.class ) ) {
 					String columnName = element.getAnnotation( Column.class ).name();
 					for ( XProperty prop : declaringClass.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 						if ( !prop.isAnnotationPresent( MapsId.class ) ) {
 							/**
 							 * The detection of a configured individual JoinColumn differs between Annotation
 							 * and XML configuration processing.
 							 */
 							boolean isRequiredAnnotationPresent = false;
 							JoinColumns groupAnnotation = prop.getAnnotation( JoinColumns.class );
 							if ( (prop.isAnnotationPresent( JoinColumn.class )
 									&& prop.getAnnotation( JoinColumn.class ).name().equals( columnName )) ) {
 								isRequiredAnnotationPresent = true;
 							}
 							else if ( prop.isAnnotationPresent( JoinColumns.class ) ) {
 								for ( JoinColumn columnAnnotation : groupAnnotation.value() ) {
 									if ( columnName.equals( columnAnnotation.name() ) ) {
 										isRequiredAnnotationPresent = true;
 										break;
 									}
 								}
 							}
 							if ( isRequiredAnnotationPresent ) {
 								//create a PropertyData fpr the specJ property holding the mapping
 								PropertyData specJPropertyData = new PropertyInferredData(
 										declaringClass,
 										//same dec
 										prop,
 										// the actual @XToOne property
 										propertyAccessor,
 										//TODO we should get the right accessor but the same as id would do
 										mappings.getReflectionManager()
 								);
 								mappings.addPropertyAnnotatedWithMapsIdSpecj(
 										entity,
 										specJPropertyData,
 										element.toString()
 								);
 							}
 						}
 					}
 				}
 			}
 
 			if ( element.isAnnotationPresent( ManyToOne.class ) || element.isAnnotationPresent( OneToOne.class ) ) {
 				mappings.addToOneAndIdProperty( entity, propertyAnnotatedElement );
 			}
 			idPropertyCounter++;
 		}
 		else {
 			annElts.add( propertyAnnotatedElement );
 		}
 		if ( element.isAnnotationPresent( MapsId.class ) ) {
 			mappings.addPropertyAnnotatedWithMapsId( entity, propertyAnnotatedElement );
 		}
 
 		return idPropertyCounter;
 	}
 
 	/*
 	 * Process annotation of a particular property
 	 */
 
 	private static void processElementAnnotations(
 			PropertyHolder propertyHolder,
 			Nullability nullability,
 			PropertyData inferredData,
 			HashMap<String, IdGenerator> classGenerators,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			boolean isComponentEmbedded,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) throws MappingException {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) {
 			LOG.tracev( "Processing annotations of {0}.{1}" , propertyHolder.getEntityName(), inferredData.getPropertyName() );
 		}
 
 		final XProperty property = inferredData.getProperty();
 		if ( property.isAnnotationPresent( Parent.class ) ) {
 			if ( propertyHolder.isComponent() ) {
 				propertyHolder.setParentProperty( property.getName() );
 			}
 			else {
 				throw new AnnotationException(
 						"@Parent cannot be applied outside an embeddable object: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			return;
 		}
 
 		ColumnsBuilder columnsBuilder = new ColumnsBuilder(
 				propertyHolder, nullability, property, inferredData, entityBinder, mappings
 		).extractMetadata();
 		Ejb3Column[] columns = columnsBuilder.getColumns();
 		Ejb3JoinColumn[] joinColumns = columnsBuilder.getJoinColumns();
 
 		final XClass returnedClass = inferredData.getClassOrElement();
 
 		//prepare PropertyBinder
 		PropertyBinder propertyBinder = new PropertyBinder();
 		propertyBinder.setName( inferredData.getPropertyName() );
 		propertyBinder.setReturnedClassName( inferredData.getTypeName() );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setHolder( propertyHolder );
 		propertyBinder.setProperty( property );
 		propertyBinder.setReturnedClass( inferredData.getPropertyClass() );
 		propertyBinder.setMappings( mappings );
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		propertyBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 		propertyBinder.setEntityBinder( entityBinder );
 		propertyBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 
 		boolean isId = !entityBinder.isIgnoreIdAnnotations() &&
 				( property.isAnnotationPresent( Id.class )
 						|| property.isAnnotationPresent( EmbeddedId.class ) );
 		propertyBinder.setId( isId );
 
 		if ( property.isAnnotationPresent( Version.class ) ) {
 			if ( isIdentifierMapper ) {
 				throw new AnnotationException(
 						"@IdClass class should not have @Version property"
 				);
 			}
 			if ( !( propertyHolder.getPersistentClass() instanceof RootClass ) ) {
 				throw new AnnotationException(
 						"Unable to define/override @Version on a subclass: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( !propertyHolder.isEntity() ) {
 				throw new AnnotationException(
 						"Unable to define @Version on an embedded class: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( traceEnabled ) {
 				LOG.tracev( "{0} is a version property", inferredData.getPropertyName() );
 			}
 			RootClass rootClass = ( RootClass ) propertyHolder.getPersistentClass();
 			propertyBinder.setColumns( columns );
 			Property prop = propertyBinder.makePropertyValueAndBind();
 			setVersionInformation( property, propertyBinder );
 			rootClass.setVersion( prop );
 
 			//If version is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredVersion( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredVersion( prop );
 			}
 
 			SimpleValue simpleValue = ( SimpleValue ) prop.getValue();
 			simpleValue.setNullValue( "undefined" );
 			rootClass.setOptimisticLockStyle( OptimisticLockStyle.VERSION );
 			if ( traceEnabled ) {
 				LOG.tracev( "Version name: {0}, unsavedValue: {1}", rootClass.getVersion().getName(),
 						( (SimpleValue) rootClass.getVersion().getValue() ).getNullValue() );
 			}
 		}
 		else {
 			final boolean forcePersist = property.isAnnotationPresent( MapsId.class )
 					|| property.isAnnotationPresent( Id.class );
 			if ( property.isAnnotationPresent( ManyToOne.class ) ) {
 				ManyToOne ann = property.getAnnotation( ManyToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @ManyToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindManyToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, false, forcePersist ),
 						joinColumns,
 						!mandatory,
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData, false, isIdentifierMapper,
 						inSecondPass, propertyBinder, mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToOne.class ) ) {
 				OneToOne ann = property.getAnnotation( OneToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @OneToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				//FIXME support a proper PKJCs
 				boolean trueOneToOne = property.isAnnotationPresent( PrimaryKeyJoinColumn.class )
 						|| property.isAnnotationPresent( PrimaryKeyJoinColumns.class );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				//MapsId means the columns belong to the pk => not null
 				//@OneToOne with @PKJC can still be optional
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindOneToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, ann.orphanRemoval(), forcePersist ),
 						joinColumns,
 						!mandatory,
 						getFetchMode( ann.fetch() ),
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData,
 						ann.mappedBy(),
 						trueOneToOne,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @Any property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				bindAny(
 						getCascadeStrategy( null, hibernateCascade, false, forcePersist ),
 						//@Any has not cascade attribute
 						joinColumns,
 						onDeleteCascade,
 						nullability,
 						propertyHolder,
 						inferredData,
 						entityBinder,
 						isIdentifierMapper,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToMany.class )
 					|| property.isAnnotationPresent( ManyToMany.class )
 					|| property.isAnnotationPresent( ElementCollection.class )
 					|| property.isAnnotationPresent( ManyToAny.class ) ) {
 				OneToMany oneToManyAnn = property.getAnnotation( OneToMany.class );
 				ManyToMany manyToManyAnn = property.getAnnotation( ManyToMany.class );
 				ElementCollection elementCollectionAnn = property.getAnnotation( ElementCollection.class );
 
 				final IndexColumn indexColumn;
 
 				if ( property.isAnnotationPresent( OrderColumn.class ) ) {
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( OrderColumn.class ),
 							propertyHolder,
 							inferredData,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 					if ( property.isAnnotationPresent( ListIndexBase.class ) ) {
 						indexColumn.setBase( ( property.getAnnotation( ListIndexBase.class ) ).value() );
 					}
 				}
 				else {
 					//if @IndexColumn is not there, the generated IndexColumn is an implicit column and not used.
 					//so we can leave the legacy processing as the default
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( org.hibernate.annotations.IndexColumn.class ),
 							propertyHolder,
 							inferredData,
 							mappings
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( MapKeyType.class ),
 						mappings
 				);
 				collectionBinder.setIndexColumn( indexColumn );
 				collectionBinder.setMapKey( property.getAnnotation( MapKey.class ) );
 				collectionBinder.setPropertyName( inferredData.getPropertyName() );
 
 				collectionBinder.setBatchSize( property.getAnnotation( BatchSize.class ) );
 
 				collectionBinder.setJpaOrderBy( property.getAnnotation( javax.persistence.OrderBy.class ) );
 				collectionBinder.setSqlOrderBy( property.getAnnotation( OrderBy.class ) );
 
 				collectionBinder.setSort( property.getAnnotation( Sort.class ) );
 				collectionBinder.setNaturalSort( property.getAnnotation( SortNatural.class ) );
 				collectionBinder.setComparatorSort( property.getAnnotation( SortComparator.class ) );
 
 				Cache cachAnn = property.getAnnotation( Cache.class );
 				collectionBinder.setCache( cachAnn );
 				collectionBinder.setPropertyHolder( propertyHolder );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				collectionBinder.setIgnoreNotFound( ignoreNotFound );
 				collectionBinder.setCollectionType( inferredData.getProperty().getElementClass() );
 				collectionBinder.setMappings( mappings );
 				collectionBinder.setAccessType( inferredData.getDefaultAccess() );
 
 				Ejb3Column[] elementColumns;
 				//do not use "element" if you are a JPA 2 @ElementCollection only for legacy Hibernate mappings
 				boolean isJPA2ForValueMapping = property.isAnnotationPresent( ElementCollection.class );
 				PropertyData virtualProperty = isJPA2ForValueMapping ? inferredData : new WrappedInferredData(
 						inferredData, "element"
 				);
 				if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent(
 						Formula.class
 				) ) {
 					Column ann = property.getAnnotation( Column.class );
 					Formula formulaAnn = property.getAnnotation( Formula.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							new Column[] { ann },
 							formulaAnn,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(), null, nullability, propertyHolder, virtualProperty,
 							entityBinder.getSecondaryTables(), mappings
 					);
 				}
 				else {
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							null,
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				{
 					Column[] keyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						keyColumns = new Column[] { new MapKeyColumnDelegator( property.getAnnotation( MapKeyColumn.class ) ) };
 					}
 
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					//nullify empty array
 					keyColumns = keyColumns != null && keyColumns.length > 0 ? keyColumns : null;
 
 					//"mapkey" is the legacy column name of the key column pre JPA 2
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3Column[] mapColumns = Ejb3Column.buildColumnFromAnnotation(
 							keyColumns,
 							null,
 							Nullability.FORCED_NOT_NULL,
 							propertyHolder,
 							isJPA2 ? inferredData : mapKeyVirtualProperty,
 							isJPA2 ? "_KEY" : null,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 					collectionBinder.setMapKeyColumns( mapColumns );
 				}
 				{
 					JoinColumn[] joinKeyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyJoinColumns.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						final MapKeyJoinColumn[] mapKeyJoinColumns = property.getAnnotation( MapKeyJoinColumns.class )
 								.value();
 						joinKeyColumns = new JoinColumn[mapKeyJoinColumns.length];
 						int index = 0;
 						for ( MapKeyJoinColumn joinColumn : mapKeyJoinColumns ) {
 							joinKeyColumns[index] = new MapKeyJoinColumnDelegator( joinColumn );
 							index++;
 						}
 						if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 							throw new AnnotationException(
 									"@MapKeyJoinColumn and @MapKeyJoinColumns used on the same property: "
 											+ BinderHelper.getPath( propertyHolder, inferredData )
 							);
 						}
 					}
 					else if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						joinKeyColumns = new JoinColumn[] {
 								new MapKeyJoinColumnDelegator(
 										property.getAnnotation(
 												MapKeyJoinColumn.class
 										)
 								)
 						};
 					}
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3JoinColumn[] mapJoinColumns = Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix(
 							joinKeyColumns,
 							null,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							isJPA2 ? inferredData.getPropertyName() : mapKeyVirtualProperty.getPropertyName(),
 							isJPA2 ? "_KEY" : null,
 							mappings
 					);
 					collectionBinder.setMapKeyManyToManyColumns( mapJoinColumns );
 				}
 
 				//potential element
 				collectionBinder.setEmbedded( property.isAnnotationPresent( Embedded.class ) );
 				collectionBinder.setElementColumns( elementColumns );
 				collectionBinder.setProperty( property );
 
 				//TODO enhance exception with @ManyToAny and @CollectionOfElements
 				if ( oneToManyAnn != null && manyToManyAnn != null ) {
 					throw new AnnotationException(
 							"@OneToMany and @ManyToMany on the same property is not allowed: "
 									+ propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
 					);
 				}
 				String mappedBy = null;
 				if ( oneToManyAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = oneToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									oneToManyAnn.cascade(), hibernateCascade, oneToManyAnn.orphanRemoval(), false
 							)
 					);
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( elementCollectionAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = "";
 					final Class<?> targetElement = elementCollectionAnn.targetClass();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									manyToManyAnn.cascade(), hibernateCascade, false, false
 							)
 					);
 					collectionBinder.setOneToMany( false );
 				}
 				else if ( property.isAnnotationPresent( ManyToAny.class ) ) {
 					mappedBy = "";
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property, mappings, entityBinder, collectionBinder, propertyHolder, inferredData, mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 					collectionBinder.setLocalGenerators( localGenerators );
 
 				}
 				collectionBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 				collectionBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 				collectionBinder.bind();
 
 			}
 			//Either a regular property or a basic @Id or @EmbeddedId while not ignoring id annotations
 			else if ( !isId || !entityBinder.isIgnoreIdAnnotations() ) {
 				//define whether the type is a component or not
 
 				boolean isComponent = false;
 
 				//Overrides from @MapsId if needed
 				boolean isOverridden = false;
 				if ( isId || propertyHolder.isOrWithinEmbeddedId() || propertyHolder.isInIdClass() ) {
 					//the associated entity could be using an @IdClass making the overridden property a component
 					final PropertyData overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					if ( overridingProperty != null ) {
 						isOverridden = true;
 						final InheritanceState state = inheritanceStatePerClass.get( overridingProperty.getClassOrElement() );
 						if ( state != null ) {
 							isComponent = isComponent || state.hasIdClassOrEmbeddedId();
 						}
 						//Get the new column
 						columns = columnsBuilder.overrideColumnFromMapperOrMapsIdProperty( isId );
 					}
 				}
 
 				isComponent = isComponent
 						|| property.isAnnotationPresent( Embedded.class )
 						|| property.isAnnotationPresent( EmbeddedId.class )
 						|| returnedClass.isAnnotationPresent( Embeddable.class );
 
 
 				if ( isComponent ) {
 					String referencedEntityName = null;
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						referencedEntityName = mapsIdProperty.getClassOrElementName();
 					}
 					AccessType propertyAccessor = entityBinder.getPropertyAccessor( property );
 					propertyBinder = bindComponent(
 							inferredData,
 							propertyHolder,
 							propertyAccessor,
 							entityBinder,
 							isIdentifierMapper,
 							mappings,
 							isComponentEmbedded,
 							isId,
 							inheritanceStatePerClass,
 							referencedEntityName,
 							isOverridden ? ( Ejb3JoinColumn[] ) columns : null
 					);
 				}
 				else {
 					//provide the basic property mapping
 					boolean optional = true;
 					boolean lazy = false;
 					if ( property.isAnnotationPresent( Basic.class ) ) {
 						Basic ann = property.getAnnotation( Basic.class );
 						optional = ann.optional();
 						lazy = ann.fetch() == FetchType.LAZY;
 					}
 					//implicit type will check basic types and Serializable classes
 					if ( isId || ( !optional && nullability != Nullability.FORCED_NULL ) ) {
 						//force columns to not null
 						for ( Ejb3Column col : columns ) {
 							col.forceNotNull();
 						}
 					}
 
 					propertyBinder.setLazy( lazy );
 					propertyBinder.setColumns( columns );
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						propertyBinder.setReferencedEntityName( mapsIdProperty.getClassOrElementName() );
 					}
 
 					propertyBinder.makePropertyValueAndBind();
 
 				}
 				if ( isOverridden ) {
 					final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					Map<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					final IdGenerator foreignGenerator = new IdGenerator();
 					foreignGenerator.setIdentifierGeneratorStrategy( "assigned" );
 					foreignGenerator.setName( "Hibernate-local--foreign generator" );
 					foreignGenerator.setIdentifierGeneratorStrategy( "foreign" );
 					foreignGenerator.addParam( "property", mapsIdProperty.getPropertyName() );
 					localGenerators.put( foreignGenerator.getName(), foreignGenerator );
 
 					BinderHelper.makeIdGenerator(
 							( SimpleValue ) propertyBinder.getValue(),
 							foreignGenerator.getIdentifierGeneratorStrategy(),
 							foreignGenerator.getName(),
 							mappings,
 							localGenerators
 					);
 				}
 				if ( isId ) {
 					//components and regular basic types create SimpleValue objects
 					final SimpleValue value = ( SimpleValue ) propertyBinder.getValue();
 					if ( !isOverridden ) {
 						processId(
 								propertyHolder,
 								inferredData,
 								value,
 								classGenerators,
 								isIdentifierMapper,
 								mappings
 						);
 					}
 				}
 			}
 		}
 		//init index
 		//process indexes after everything: in second pass, many to one has to be done before indexes
 		Index index = property.getAnnotation( Index.class );
 		if ( index != null ) {
 			if ( joinColumns != null ) {
 
 				for ( Ejb3Column column : joinColumns ) {
 					column.addIndex( index, inSecondPass );
 				}
 			}
 			else {
 				if ( columns != null ) {
 					for ( Ejb3Column column : columns ) {
 						column.addIndex( index, inSecondPass );
 					}
 				}
 			}
 		}
 
 		// Natural ID columns must reside in one single UniqueKey within the Table.
 		// For now, simply ensure consistent naming.
 		// TODO: AFAIK, there really isn't a reason for these UKs to be created
 		// on the secondPass.  This whole area should go away...
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
 				}
 			}
 		}
 	}
 
 	private static void setVersionInformation(XProperty property, PropertyBinder propertyBinder) {
 		propertyBinder.getSimpleValueBinder().setVersion( true );
 		if(property.isAnnotationPresent( Source.class )) {
 			Source source = property.getAnnotation( Source.class );
 			propertyBinder.getSimpleValueBinder().setTimestampVersionType( source.value().typeName() );
 		}
 	}
 
 	private static void processId(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			SimpleValue idValue,
 			HashMap<String, IdGenerator> classGenerators,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		if ( isIdentifierMapper ) {
 			throw new AnnotationException(
 					"@IdClass class should not have @Id nor @EmbeddedId properties: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		XClass returnedClass = inferredData.getClassOrElement();
 		XProperty property = inferredData.getProperty();
 		//clone classGenerator and override with local values
 		HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 		localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 		//manage composite related metadata
 		//guess if its a component and find id data access (property, field etc)
 		final boolean isComponent = returnedClass.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( EmbeddedId.class );
 
 		GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 		String generatorType = generatedValue != null ?
 				generatorType( generatedValue.strategy(), mappings ) :
 				"assigned";
 		String generatorName = generatedValue != null ?
 				generatedValue.generator() :
 				BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( isComponent ) {
 			generatorType = "assigned";
 		} //a component must not have any generator
 		BinderHelper.makeIdGenerator( idValue, generatorType, generatorName, mappings, localGenerators );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Bind {0} on {1}", ( isComponent ? "@EmbeddedId" : "@Id" ), inferredData.getPropertyName() );
 		}
 	}
 
 	//TODO move that to collection binder?
 
 	private static void bindJoinedTableAssociation(
 			XProperty property,
 			Mappings mappings,
 			EntityBinder entityBinder,
 			CollectionBinder collectionBinder,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy) {
 		TableBinder associationTableBinder = new TableBinder();
 		JoinColumn[] annJoins;
 		JoinColumn[] annInverseJoins;
 		JoinTable assocTable = propertyHolder.getJoinTable( property );
 		CollectionTable collectionTable = property.getAnnotation( CollectionTable.class );
 		if ( assocTable != null || collectionTable != null ) {
 
 			final String catalog;
 			final String schema;
 			final String tableName;
 			final UniqueConstraint[] uniqueConstraints;
 			final JoinColumn[] joins;
 			final JoinColumn[] inverseJoins;
 			final javax.persistence.Index[] jpaIndexes;
 
 
 			//JPA 2 has priority
 			if ( collectionTable != null ) {
 				catalog = collectionTable.catalog();
 				schema = collectionTable.schema();
 				tableName = collectionTable.name();
 				uniqueConstraints = collectionTable.uniqueConstraints();
 				joins = collectionTable.joinColumns();
 				inverseJoins = null;
 				jpaIndexes = collectionTable.indexes();
 			}
 			else {
 				catalog = assocTable.catalog();
 				schema = assocTable.schema();
 				tableName = assocTable.name();
 				uniqueConstraints = assocTable.uniqueConstraints();
 				joins = assocTable.joinColumns();
 				inverseJoins = assocTable.inverseJoinColumns();
 				jpaIndexes = assocTable.indexes();
 			}
 
 			collectionBinder.setExplicitAssociationTable( true );
 			if ( jpaIndexes != null && jpaIndexes.length > 0 ) {
 				associationTableBinder.setJpaIndex( jpaIndexes );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( schema ) ) {
 				associationTableBinder.setSchema( schema );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( catalog ) ) {
 				associationTableBinder.setCatalog( catalog );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( tableName ) ) {
 				associationTableBinder.setName( tableName );
 			}
 			associationTableBinder.setUniqueConstraints( uniqueConstraints );
 			associationTableBinder.setJpaIndex( jpaIndexes );
 			//set check constaint in the second pass
 			annJoins = joins.length == 0 ? null : joins;
 			annInverseJoins = inverseJoins == null || inverseJoins.length == 0 ? null : inverseJoins;
 		}
 		else {
 			annJoins = null;
 			annInverseJoins = null;
 		}
 		Ejb3JoinColumn[] joinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy,
 				mappings
 		);
 		Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annInverseJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(),
 				mappedBy, mappings
 		);
 		associationTableBinder.setMappings( mappings );
 		collectionBinder.setTableBinder( associationTableBinder );
 		collectionBinder.setJoinColumns( joinColumns );
 		collectionBinder.setInverseJoinColumns( inverseJoinColumns );
 	}
 
 	private static PropertyBinder bindComponent(
 			PropertyData inferredData,
 			PropertyHolder propertyHolder,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			Mappings mappings,
 			boolean isComponentEmbedded,
 			boolean isId, //is a identifier
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			String referencedEntityName, //is a component who is overridden by a @MapsId
 			Ejb3JoinColumn[] columns) {
 		Component comp;
 		if ( referencedEntityName != null ) {
 			comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 			SecondPass sp = new CopyIdentifierComponentSecondPass(
 					comp,
 					referencedEntityName,
 					columns,
 					mappings
 			);
 			mappings.addSecondPass( sp );
 		}
 		else {
 			comp = fillComponent(
 					propertyHolder, inferredData, propertyAccessor, !isId, entityBinder,
 					isComponentEmbedded, isIdentifierMapper,
 					false, mappings, inheritanceStatePerClass
 			);
 		}
 		if ( isId ) {
 			comp.setKey( true );
 			if ( propertyHolder.getPersistentClass().getIdentifier() != null ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " must not have @Id properties when used as an @EmbeddedId: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			if ( referencedEntityName == null && comp.getPropertySpan() == 0 ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " has no persistent id property: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 		}
 		XProperty property = inferredData.getProperty();
 		setupComponentTuplizer( property, comp );
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( comp );
 		binder.setProperty( inferredData.getProperty() );
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setEmbedded( isComponentEmbedded );
 		binder.setHolder( propertyHolder );
 		binder.setId( isId );
 		binder.setEntityBinder( entityBinder );
 		binder.setInheritanceStatePerClass( inheritanceStatePerClass );
 		binder.setMappings( mappings );
 		binder.makePropertyAndBind();
 		return binder;
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		return fillComponent(
 				propertyHolder, inferredData, null, propertyAccessor,
 				isNullable, entityBinder, isComponentEmbedded, isIdentifierMapper, inSecondPass, mappings,
 				inheritanceStatePerClass
 		);
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			PropertyData baseInferredData, //base inferred data correspond to the entity reproducing inferredData's properties (ie IdClass)
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 		Component comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 		String subpath = BinderHelper.getPath( propertyHolder, inferredData );
 		LOG.tracev( "Binding component with path: {0}", subpath );
 		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(
-				comp, subpath,
-				inferredData, propertyHolder, mappings
+				comp,
+				subpath,
+				inferredData,
+				propertyHolder,
+				mappings
 		);
 
+
+		// propertyHolder here is the owner of the component property.  Tell it we are about to start the component...
+
+		propertyHolder.startingProperty( inferredData.getProperty() );
+
 		final XClass xClassProcessed = inferredData.getPropertyClass();
 		List<PropertyData> classElements = new ArrayList<PropertyData>();
 		XClass returnedClassOrElement = inferredData.getClassOrElement();
 
 		List<PropertyData> baseClassElements = null;
 		Map<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
 		XClass baseReturnedClassOrElement;
 		if ( baseInferredData != null ) {
 			baseClassElements = new ArrayList<PropertyData>();
 			baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 			bindTypeDefs( baseReturnedClassOrElement, mappings );
 			PropertyContainer propContainer = new PropertyContainer( baseReturnedClassOrElement, xClassProcessed );
 			addElementsOfClass( baseClassElements, propertyAccessor, propContainer, mappings );
 			for ( PropertyData element : baseClassElements ) {
 				orderedBaseClassElements.put( element.getPropertyName(), element );
 			}
 		}
 
 		//embeddable elements can have type defs
 		bindTypeDefs( returnedClassOrElement, mappings );
 		PropertyContainer propContainer = new PropertyContainer( returnedClassOrElement, xClassProcessed );
 		addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
 
 		//add elements of the embeddable superclass
 		XClass superClass = xClassProcessed.getSuperclass();
 		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
 			//FIXME: proper support of typevariables incl var resolved at upper levels
 			propContainer = new PropertyContainer( superClass, xClassProcessed );
 			addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
 			superClass = superClass.getSuperclass();
 		}
 		if ( baseClassElements != null ) {
 			//useful to avoid breaking pre JPA 2 mappings
 			if ( !hasAnnotationsOnIdClass( xClassProcessed ) ) {
 				for ( int i = 0; i < classElements.size(); i++ ) {
 					final PropertyData idClassPropertyData = classElements.get( i );
 					final PropertyData entityPropertyData = orderedBaseClassElements.get( idClassPropertyData.getPropertyName() );
 					if ( propertyHolder.isInIdClass() ) {
 						if ( entityPropertyData == null ) {
 							throw new AnnotationException(
 									"Property of @IdClass not found in entity "
 											+ baseInferredData.getPropertyClass().getName() + ": "
 											+ idClassPropertyData.getPropertyName()
 							);
 						}
 						final boolean hasXToOneAnnotation = entityPropertyData.getProperty()
 								.isAnnotationPresent( ManyToOne.class )
 								|| entityPropertyData.getProperty().isAnnotationPresent( OneToOne.class );
 						final boolean isOfDifferentType = !entityPropertyData.getClassOrElement()
 								.equals( idClassPropertyData.getClassOrElement() );
 						if ( hasXToOneAnnotation && isOfDifferentType ) {
 							//don't replace here as we need to use the actual original return type
 							//the annotation overriding will be dealt with by a mechanism similar to @MapsId
 						}
 						else {
 							classElements.set( i, entityPropertyData );  //this works since they are in the same order
 						}
 					}
 					else {
 						classElements.set( i, entityPropertyData );  //this works since they are in the same order
 					}
 				}
 			}
 		}
 		for ( PropertyData propertyAnnotatedElement : classElements ) {
 			processElementAnnotations(
 					subHolder, isNullable ?
 							Nullability.NO_CONSTRAINT :
 							Nullability.FORCED_NOT_NULL,
 					propertyAnnotatedElement,
 					new HashMap<String, IdGenerator>(), entityBinder, isIdentifierMapper, isComponentEmbedded,
 					inSecondPass, mappings, inheritanceStatePerClass
 			);
 
 			XProperty property = propertyAnnotatedElement.getProperty();
 			if ( property.isAnnotationPresent( GeneratedValue.class ) &&
 					property.isAnnotationPresent( Id.class ) ) {
 				//clone classGenerator and override with local values
 				Map<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 				localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 				GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 				String generatorType = generatedValue != null ? generatorType(
 						generatedValue.strategy(), mappings
 				) : "assigned";
 				String generator = generatedValue != null ? generatedValue.generator() : BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 				BinderHelper.makeIdGenerator(
 						( SimpleValue ) comp.getProperty( property.getName() ).getValue(),
 						generatorType,
 						generator,
 						mappings,
 						localGenerators
 				);
 			}
 
 		}
 		return comp;
 	}
 
 	public static Component createComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		Component comp = new Component( mappings, propertyHolder.getPersistentClass() );
 		comp.setEmbedded( isComponentEmbedded );
 		//yuk
 		comp.setTable( propertyHolder.getTable() );
 		//FIXME shouldn't identifier mapper use getClassOrElementName? Need to be checked.
 		if ( isIdentifierMapper || ( isComponentEmbedded && inferredData.getPropertyName() == null ) ) {
 			comp.setComponentClassName( comp.getOwner().getClassName() );
 		}
 		else {
 			comp.setComponentClassName( inferredData.getClassOrElementName() );
 		}
 		comp.setNodeName( inferredData.getPropertyName() );
 		return comp;
 	}
 
 	private static void bindIdClass(
 			String generatorType,
 			String generatorName,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			Ejb3Column[] columns,
 			PropertyHolder propertyHolder,
 			boolean isComposite,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isEmbedded,
 			boolean isIdentifierMapper,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		/*
 		 * Fill simple value and property since and Id is a property
 		 */
 		PersistentClass persistentClass = propertyHolder.getPersistentClass();
 		if ( !( persistentClass instanceof RootClass ) ) {
 			throw new AnnotationException(
 					"Unable to define/override @Id(s) on a subclass: "
 							+ propertyHolder.getEntityName()
 			);
 		}
 		RootClass rootClass = ( RootClass ) persistentClass;
 		String persistentClassName = rootClass.getClassName();
 		SimpleValue id;
 		final String propertyName = inferredData.getPropertyName();
 		HashMap<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 		if ( isComposite ) {
 			id = fillComponent(
 					propertyHolder, inferredData, baseInferredData, propertyAccessor,
 					false, entityBinder, isEmbedded, isIdentifierMapper, false, mappings, inheritanceStatePerClass
 			);
 			Component componentId = ( Component ) id;
 			componentId.setKey( true );
 			if ( rootClass.getIdentifier() != null ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " must not have @Id properties when used as an @EmbeddedId" );
 			}
 			if ( componentId.getPropertySpan() == 0 ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " has no persistent id property" );
 			}
 			//tuplizers
 			XProperty property = inferredData.getProperty();
 			setupComponentTuplizer( property, componentId );
 		}
 		else {
 			//TODO I think this branch is never used. Remove.
 
 			for ( Ejb3Column column : columns ) {
 				column.forceNotNull(); //this is an id
 			}
 			SimpleValueBinder value = new SimpleValueBinder();
 			value.setPropertyName( propertyName );
 			value.setReturnedClassName( inferredData.getTypeName() );
 			value.setColumns( columns );
 			value.setPersistentClassName( persistentClassName );
 			value.setMappings( mappings );
-			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName );
+			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName, null );
 			value.setAccessType( propertyAccessor );
 			id = value.make();
 		}
 		rootClass.setIdentifier( id );
 		BinderHelper.makeIdGenerator( id, generatorType, generatorName, mappings, localGenerators );
 		if ( isEmbedded ) {
 			rootClass.setEmbeddedIdentifier( inferredData.getPropertyClass() == null );
 		}
 		else {
 			PropertyBinder binder = new PropertyBinder();
 			binder.setName( propertyName );
 			binder.setValue( id );
 			binder.setAccessType( inferredData.getDefaultAccess() );
 			binder.setProperty( inferredData.getProperty() );
 			Property prop = binder.makeProperty();
 			rootClass.setIdentifierProperty( prop );
 			//if the id property is on a superclass, update the metamodel
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierProperty( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredIdentifierProperty( prop );
 			}
 		}
 	}
 
 	private static PropertyData getUniqueIdPropertyFromBaseClass(
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			Mappings mappings) {
 		List<PropertyData> baseClassElements = new ArrayList<PropertyData>();
 		XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 		PropertyContainer propContainer = new PropertyContainer(
 				baseReturnedClassOrElement, inferredData.getPropertyClass()
 		);
 		addElementsOfClass( baseClassElements, propertyAccessor, propContainer, mappings );
 		//Id properties are on top and there is only one
 		return baseClassElements.get( 0 );
 	}
 
 	private static void setupComponentTuplizer(XProperty property, Component component) {
 		if ( property == null ) {
 			return;
 		}
 		if ( property.isAnnotationPresent( Tuplizers.class ) ) {
 			for ( Tuplizer tuplizer : property.getAnnotation( Tuplizers.class ).value() ) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				//todo tuplizer.entityModeType
 				component.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( property.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = property.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			component.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 	}
 
 	private static void bindManyToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean optional,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean unique,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			Mappings mappings) {
 		//All FK columns should be in the same table
 		org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne( mappings, columns[0].getTable() );
 		// This is a @OneToOne mapped to a physical o.h.mapping.ManyToOne
 		if ( unique ) {
 			value.markAsLogicalOneToOne();
 		}
 		value.setReferencedEntityName( ToOneBinder.getReferenceEntityName( inferredData, targetEntity, mappings ) );
 		final XProperty property = inferredData.getProperty();
 		defineFetchingStrategy( value, property );
 		//value.setFetchMode( fetchMode );
 		value.setIgnoreNotFound( ignoreNotFound );
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		//value.setLazy( fetchMode != FetchMode.JOIN );
 		if ( !optional ) {
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setNullable( false );
 			}
 		}
 		if ( property.isAnnotationPresent( MapsId.class ) ) {
 			//read only
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setInsertable( false );
 				column.setUpdatable( false );
 			}
 		}
 
 		//Make sure that JPA1 key-many-to-one columns are read only tooj
 		boolean hasSpecjManyToOne=false;
 		if ( mappings.isSpecjProprietarySyntaxEnabled() ) {
 			String columnName = "";
 			for ( XProperty prop : inferredData.getDeclaringClass()
 					.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 				if ( prop.isAnnotationPresent( Id.class ) && prop.isAnnotationPresent( Column.class ) ) {
 					columnName = prop.getAnnotation( Column.class ).name();
 				}
 
 				final JoinColumn joinColumn = property.getAnnotation( JoinColumn.class );
 				if ( property.isAnnotationPresent( ManyToOne.class ) && joinColumn != null
 						&& ! BinderHelper.isEmptyAnnotationValue( joinColumn.name() )
 						&& joinColumn.name().equals( columnName )
 						&& !property.isAnnotationPresent( MapsId.class ) ) {
 				   hasSpecjManyToOne = true;
 					for ( Ejb3JoinColumn column : columns ) {
 						column.setInsertable( false );
 						column.setUpdatable( false );
 					}
 				}
 			}
 
 		}
 		value.setTypeName( inferredData.getClassOrElementName() );
 		final String propertyName = inferredData.getPropertyName();
 		value.setTypeUsingReflection( propertyHolder.getClassName(), propertyName );
 
 		ForeignKey fk = property.getAnnotation( ForeignKey.class );
 		String fkName = fk != null ?
 				fk.name() :
 				"";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) {
 			value.setForeignKeyName( fkName );
 		}
 
 		String path = propertyHolder.getPath() + "." + propertyName;
 		FkSecondPass secondPass = new ToOneFkSecondPass(
 				value, columns,
 				!optional && unique, //cannot have nullable and unique on certain DBs like Derby
 				propertyHolder.getEntityOwnerClassName(),
 				path, mappings
 		);
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( mappings.getClasses() );
 		}
 		else {
 			mappings.addSecondPass(
 					secondPass
 			);
 		}
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + propertyName );
 		//PropertyBinder binder = new PropertyBinder();
 		propertyBinder.setName( propertyName );
 		propertyBinder.setValue( value );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else if (hasSpecjManyToOne) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else {
 			propertyBinder.setInsertable( columns[0].isInsertable() );
 			propertyBinder.setUpdatable( columns[0].isUpdatable() );
 		}
 		propertyBinder.setColumns( columns );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setCascade( cascadeStrategy );
 		propertyBinder.setProperty( property );
 		propertyBinder.setXToMany( true );
 		propertyBinder.makePropertyAndBind();
 	}
 
 	protected static void defineFetchingStrategy(ToOne toOne, XProperty property) {
 		LazyToOne lazy = property.getAnnotation( LazyToOne.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		ManyToOne manyToOne = property.getAnnotation( ManyToOne.class );
 		OneToOne oneToOne = property.getAnnotation( OneToOne.class );
 		FetchType fetchType;
 		if ( manyToOne != null ) {
 			fetchType = manyToOne.fetch();
 		}
 		else if ( oneToOne != null ) {
 			fetchType = oneToOne.fetch();
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @OneToMany nor @OneToOne"
 			);
 		}
 		if ( lazy != null ) {
 			toOne.setLazy( !( lazy.value() == LazyToOneOption.FALSE ) );
 			toOne.setUnwrapProxy( ( lazy.value() == LazyToOneOption.NO_PROXY ) );
 		}
 		else {
 			toOne.setLazy( fetchType == FetchType.LAZY );
 			toOne.setUnwrapProxy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				toOne.setFetchMode( FetchMode.JOIN );
 				toOne.setLazy( false );
 				toOne.setUnwrapProxy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				toOne.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				throw new AnnotationException( "Use of FetchMode.SUBSELECT not allowed on ToOne associations" );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			toOne.setFetchMode( getFetchMode( fetchType ) );
 		}
 	}
 
 	private static void bindOneToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] joinColumns,
 			boolean optional,
 			FetchMode fetchMode,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy,
 			boolean trueOneToOne,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			Mappings mappings) {
 		//column.getTable() => persistentClass.getTable()
 		final String propertyName = inferredData.getPropertyName();
 		LOG.tracev( "Fetching {0} with {1}", propertyName, fetchMode );
 		boolean mapToPK = true;
 		if ( !trueOneToOne ) {
 			//try to find a hidden true one to one (FK == PK columns)
 			KeyValue identifier = propertyHolder.getIdentifier();
 			if ( identifier == null ) {
 				//this is a @OneToOne in a @EmbeddedId (the persistentClass.identifier is not set yet, it's being built)
 				//by definition the PK cannot refers to itself so it cannot map to itself
 				mapToPK = false;
 			}
 			else {
 				Iterator idColumns = identifier.getColumnIterator();
 				List<String> idColumnNames = new ArrayList<String>();
 				org.hibernate.mapping.Column currentColumn;
 				if ( identifier.getColumnSpan() != joinColumns.length ) {
 					mapToPK = false;
 				}
 				else {
 					while ( idColumns.hasNext() ) {
 						currentColumn = ( org.hibernate.mapping.Column ) idColumns.next();
 						idColumnNames.add( currentColumn.getName() );
 					}
 					for ( Ejb3JoinColumn col : joinColumns ) {
 						if ( !idColumnNames.contains( col.getMappingColumn().getName() ) ) {
 							mapToPK = false;
 							break;
 						}
 					}
 				}
 			}
 		}
 		if ( trueOneToOne || mapToPK || !BinderHelper.isEmptyAnnotationValue( mappedBy ) ) {
 			//is a true one-to-one
 			//FIXME referencedColumnName ignored => ordering may fail.
 			OneToOneSecondPass secondPass = new OneToOneSecondPass(
 					mappedBy,
 					propertyHolder.getEntityName(),
 					propertyName,
 					propertyHolder, inferredData, targetEntity, ignoreNotFound, cascadeOnDelete,
 					optional, cascadeStrategy, joinColumns, mappings
 			);
 			if ( inSecondPass ) {
 				secondPass.doSecondPass( mappings.getClasses() );
 			}
 			else {
 				mappings.addSecondPass(
 						secondPass, BinderHelper.isEmptyAnnotationValue( mappedBy )
 				);
 			}
 		}
 		else {
 			//has a FK on the table
 			bindManyToOne(
 					cascadeStrategy, joinColumns, optional, ignoreNotFound, cascadeOnDelete,
 					targetEntity,
 					propertyHolder, inferredData, true, isIdentifierMapper, inSecondPass,
 					propertyBinder, mappings
 			);
 		}
 	}
 
 	private static void bindAny(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean cascadeOnDelete,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		org.hibernate.annotations.Any anyAnn = inferredData.getProperty()
 				.getAnnotation( org.hibernate.annotations.Any.class );
 		if ( anyAnn == null ) {
 			throw new AssertionFailure(
 					"Missing @Any annotation: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		Any value = BinderHelper.buildAnyValue(
 				anyAnn.metaDef(), columns, anyAnn.metaColumn(), inferredData,
 				cascadeOnDelete, nullability, propertyHolder, entityBinder, anyAnn.optional(), mappings
 		);
 
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( value );
 
 		binder.setLazy( anyAnn.fetch() == FetchType.LAZY );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			binder.setInsertable( false );
 			binder.setUpdatable( false );
 		}
 		else {
 			binder.setInsertable( columns[0].isInsertable() );
 			binder.setUpdatable( columns[0].isUpdatable() );
 		}
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setCascade( cascadeStrategy );
 		Property prop = binder.makeProperty();
 		//composite FK columns are in the same table so its OK
 		propertyHolder.addProperty( prop, columns, inferredData.getDeclaringClass() );
 	}
 
 	private static String generatorType(GenerationType generatorEnum, Mappings mappings) {
 		boolean useNewGeneratorMappings = mappings.useNewGeneratorMappings();
 		switch ( generatorEnum ) {
 			case IDENTITY:
 				return "identity";
 			case AUTO:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.SequenceStyleGenerator.class.getName()
 						: "native";
 			case TABLE:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.TableGenerator.class.getName()
 						: MultipleHiLoPerTableGenerator.class.getName();
 			case SEQUENCE:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.SequenceStyleGenerator.class.getName()
 						: "seqhilo";
 		}
 		throw new AssertionFailure( "Unknown GeneratorType: " + generatorEnum );
 	}
 
 	private static EnumSet<CascadeType> convertToHibernateCascadeType(javax.persistence.CascadeType[] ejbCascades) {
 		EnumSet<CascadeType> hibernateCascadeSet = EnumSet.noneOf( CascadeType.class );
 		if ( ejbCascades != null && ejbCascades.length > 0 ) {
 			for ( javax.persistence.CascadeType cascade : ejbCascades ) {
 				switch ( cascade ) {
 					case ALL:
 						hibernateCascadeSet.add( CascadeType.ALL );
 						break;
 					case PERSIST:
 						hibernateCascadeSet.add( CascadeType.PERSIST );
 						break;
 					case MERGE:
 						hibernateCascadeSet.add( CascadeType.MERGE );
 						break;
 					case REMOVE:
 						hibernateCascadeSet.add( CascadeType.REMOVE );
 						break;
 					case REFRESH:
 						hibernateCascadeSet.add( CascadeType.REFRESH );
 						break;
 					case DETACH:
 						hibernateCascadeSet.add( CascadeType.DETACH );
 						break;
 				}
 			}
 		}
 
 		return hibernateCascadeSet;
 	}
 
 	private static String getCascadeStrategy(
 			javax.persistence.CascadeType[] ejbCascades,
 			Cascade hibernateCascadeAnnotation,
 			boolean orphanRemoval,
 			boolean forcePersist) {
 		EnumSet<CascadeType> hibernateCascadeSet = convertToHibernateCascadeType( ejbCascades );
 		CascadeType[] hibernateCascades = hibernateCascadeAnnotation == null ?
 				null :
 				hibernateCascadeAnnotation.value();
 
 		if ( hibernateCascades != null && hibernateCascades.length > 0 ) {
 			hibernateCascadeSet.addAll( Arrays.asList( hibernateCascades ) );
 		}
 
 		if ( orphanRemoval ) {
 			hibernateCascadeSet.add( CascadeType.DELETE_ORPHAN );
 			hibernateCascadeSet.add( CascadeType.REMOVE );
 		}
 		if ( forcePersist ) {
 			hibernateCascadeSet.add( CascadeType.PERSIST );
 		}
 
 		StringBuilder cascade = new StringBuilder();
 		for ( CascadeType aHibernateCascadeSet : hibernateCascadeSet ) {
 			switch ( aHibernateCascadeSet ) {
 				case ALL:
 					cascade.append( "," ).append( "all" );
 					break;
 				case SAVE_UPDATE:
 					cascade.append( "," ).append( "save-update" );
 					break;
 				case PERSIST:
 					cascade.append( "," ).append( "persist" );
 					break;
 				case MERGE:
 					cascade.append( "," ).append( "merge" );
 					break;
 				case LOCK:
 					cascade.append( "," ).append( "lock" );
 					break;
 				case REFRESH:
 					cascade.append( "," ).append( "refresh" );
 					break;
 				case REPLICATE:
 					cascade.append( "," ).append( "replicate" );
 					break;
 				case EVICT:
 				case DETACH:
 					cascade.append( "," ).append( "evict" );
 					break;
 				case DELETE:
 					cascade.append( "," ).append( "delete" );
 					break;
 				case DELETE_ORPHAN:
 					cascade.append( "," ).append( "delete-orphan" );
 					break;
 				case REMOVE:
 					cascade.append( "," ).append( "delete" );
 					break;
 			}
 		}
 		return cascade.length() > 0 ?
 				cascade.substring( 1 ) :
 				"none";
 	}
 
 	public static FetchMode getFetchMode(FetchType fetch) {
 		if ( fetch == FetchType.EAGER ) {
 			return FetchMode.JOIN;
 		}
 		else {
 			return FetchMode.SELECT;
 		}
 	}
 
 	private static HashMap<String, IdGenerator> buildLocalGenerators(XAnnotatedElement annElt, Mappings mappings) {
 		HashMap<String, IdGenerator> generators = new HashMap<String, IdGenerator>();
 		TableGenerator tabGen = annElt.getAnnotation( TableGenerator.class );
 		SequenceGenerator seqGen = annElt.getAnnotation( SequenceGenerator.class );
 		GenericGenerator genGen = annElt.getAnnotation( GenericGenerator.class );
 		if ( tabGen != null ) {
 			IdGenerator idGen = buildIdGenerator( tabGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( seqGen != null ) {
 			IdGenerator idGen = buildIdGenerator( seqGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( genGen != null ) {
 			IdGenerator idGen = buildIdGenerator( genGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		return generators;
 	}
 
 	public static boolean isDefault(XClass clazz, Mappings mappings) {
 		return mappings.getReflectionManager().equals( clazz, void.class );
 	}
 
 	/**
 	 * For the mapped entities build some temporary data-structure containing information about the
 	 * inheritance status of a class.
 	 *
 	 * @param orderedClasses Order list of all annotated entities and their mapped superclasses
 	 *
 	 * @return A map of {@code InheritanceState}s keyed against their {@code XClass}.
 	 */
 	public static Map<XClass, InheritanceState> buildInheritanceStates(
 			List<XClass> orderedClasses,
 			Mappings mappings) {
 		ReflectionManager reflectionManager = mappings.getReflectionManager();
 		Map<XClass, InheritanceState> inheritanceStatePerClass = new HashMap<XClass, InheritanceState>(
 				orderedClasses.size()
 		);
 		for ( XClass clazz : orderedClasses ) {
 			InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState(
 					clazz, inheritanceStatePerClass
 			);
 			InheritanceState state = new InheritanceState( clazz, inheritanceStatePerClass, mappings );
 			if ( superclassState != null ) {
 				//the classes are ordered thus preventing an NPE
 				//FIXME if an entity has subclasses annotated @MappedSperclass wo sub @Entity this is wrong
 				superclassState.setHasSiblings( true );
 				InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 						clazz, inheritanceStatePerClass
 				);
 				state.setHasParents( superEntityState != null );
 				final boolean nonDefault = state.getType() != null && !InheritanceType.SINGLE_TABLE
 						.equals( state.getType() );
 				if ( superclassState.getType() != null ) {
 					final boolean mixingStrategy = state.getType() != null && !state.getType()
 							.equals( superclassState.getType() );
 					if ( nonDefault && mixingStrategy ) {
 						LOG.invalidSubStrategy( clazz.getName() );
 					}
 					state.setType( superclassState.getType() );
 				}
 			}
 			inheritanceStatePerClass.put( clazz, state );
 		}
 		return inheritanceStatePerClass;
 	}
 
 	private static boolean hasAnnotationsOnIdClass(XClass idClass) {
 //		if(idClass.getAnnotation(Embeddable.class) != null)
 //			return true;
 
 		List<XProperty> properties = idClass.getDeclaredProperties( XClass.ACCESS_FIELD );
 		for ( XProperty property : properties ) {
 			if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent( OneToMany.class ) ||
 					property.isAnnotationPresent( ManyToOne.class ) || property.isAnnotationPresent( Id.class ) ||
 					property.isAnnotationPresent( GeneratedValue.class ) || property.isAnnotationPresent( OneToOne.class ) ||
 					property.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		List<XMethod> methods = idClass.getDeclaredMethods();
 		for ( XMethod method : methods ) {
 			if ( method.isAnnotationPresent( Column.class ) || method.isAnnotationPresent( OneToMany.class ) ||
 					method.isAnnotationPresent( ManyToOne.class ) || method.isAnnotationPresent( Id.class ) ||
 					method.isAnnotationPresent( GeneratedValue.class ) || method.isAnnotationPresent( OneToOne.class ) ||
 					method.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConversionInfo.java b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConversionInfo.java
new file mode 100644
index 0000000000..ece8b90faf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConversionInfo.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.cfg;
+
+import javax.persistence.AttributeConverter;
+import javax.persistence.Convert;
+
+import org.hibernate.annotations.common.reflection.XAnnotatedElement;
+
+/**
+ * Describes a {@link javax.persistence.Convert} conversion
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConversionInfo {
+	private final Class<? extends AttributeConverter> converterClass;
+	private final boolean conversionDisabled;
+
+	private final String attributeName;
+
+	private final XAnnotatedElement source;
+
+	public AttributeConversionInfo(
+			Class<? extends AttributeConverter> converterClass,
+			boolean conversionDisabled,
+			String attributeName,
+			XAnnotatedElement source) {
+		this.converterClass = converterClass;
+		this.conversionDisabled = conversionDisabled;
+		this.attributeName = attributeName;
+		this.source = source;
+	}
+
+	@SuppressWarnings("unchecked")
+	public AttributeConversionInfo(Convert convertAnnotation, XAnnotatedElement xAnnotatedElement) {
+		this(
+				convertAnnotation.converter(),
+				convertAnnotation.disableConversion(),
+				convertAnnotation.attributeName(),
+				xAnnotatedElement
+		);
+	}
+
+	public Class<? extends AttributeConverter> getConverterClass() {
+		return converterClass;
+	}
+
+	public boolean isConversionDisabled() {
+		return conversionDisabled;
+	}
+
+	public String getAttributeName() {
+		return attributeName;
+	}
+
+	public XAnnotatedElement getSource() {
+		return source;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
index 925bfe60e2..b83b49c9a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
@@ -1,289 +1,404 @@
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
-import java.util.HashMap;
-import java.util.Map;
+
 import javax.persistence.Convert;
 import javax.persistence.Converts;
 import javax.persistence.JoinTable;
+import java.util.HashMap;
+import java.util.Map;
 
 import org.jboss.logging.Logger;
 
-import org.hibernate.AnnotationException;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
-import org.hibernate.annotations.common.util.ReflectHelper;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.internal.CoreLogging;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
  * @author Emmanuel Bernard
  */
 public class ClassPropertyHolder extends AbstractPropertyHolder {
-	private static final Logger log = CoreLogging.logger( ClassPropertyHolder.class );
-
 	private PersistentClass persistentClass;
 	private Map<String, Join> joins;
 	private transient Map<String, Join> joinsPerRealTableName;
 	private EntityBinder entityBinder;
 	private final Map<XClass, InheritanceState> inheritanceStatePerClass;
 
+	private Map<String,AttributeConversionInfo> attributeConversionInfoMap;
+
 	public ClassPropertyHolder(
 			PersistentClass persistentClass,
-			XClass clazzToProcess,
+			XClass entityXClass,
 			Map<String, Join> joins,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
-		super( persistentClass.getEntityName(), null, clazzToProcess, mappings );
+		super( persistentClass.getEntityName(), null, entityXClass, mappings );
 		this.persistentClass = persistentClass;
 		this.joins = joins;
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
+
+		this.attributeConversionInfoMap = buildAttributeConversionInfoMap( entityXClass );
 	}
 
 	public ClassPropertyHolder(
 			PersistentClass persistentClass,
-			XClass clazzToProcess,
+			XClass entityXClass,
 			EntityBinder entityBinder,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
-		this( persistentClass, clazzToProcess, entityBinder.getSecondaryTables(), mappings, inheritanceStatePerClass );
+		this( persistentClass, entityXClass, entityBinder.getSecondaryTables(), mappings, inheritanceStatePerClass );
 		this.entityBinder = entityBinder;
 	}
 
+	@Override
+	protected String normalizeCompositePath(String attributeName) {
+		return attributeName;
+	}
+
+	@Override
+	protected String normalizeCompositePathForLogging(String attributeName) {
+		return getEntityName() + '.' + attributeName;
+	}
+
+	protected Map<String, AttributeConversionInfo> buildAttributeConversionInfoMap(XClass entityXClass) {
+		final HashMap<String, AttributeConversionInfo> map = new HashMap<String, AttributeConversionInfo>();
+		collectAttributeConversionInfo( map, entityXClass );
+		return map;
+	}
+
+	private void collectAttributeConversionInfo(Map<String, AttributeConversionInfo> infoMap, XClass xClass) {
+		if ( xClass == null ) {
+			// typically indicates we have reached the end of the inheritance hierarchy
+			return;
+		}
+
+		// collect superclass info first
+		collectAttributeConversionInfo( infoMap, xClass.getSuperclass() );
+
+		final boolean canContainConvert = xClass.isAnnotationPresent( javax.persistence.Entity.class )
+				|| xClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class )
+				|| xClass.isAnnotationPresent( javax.persistence.Embeddable.class );
+		if ( ! canContainConvert ) {
+			return;
+		}
+
+		{
+			final Convert convertAnnotation = xClass.getAnnotation( Convert.class );
+			if ( convertAnnotation != null ) {
+				final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, xClass );
+				if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+					throw new IllegalStateException( "@Convert placed on @Entity/@MappedSuperclass must define attributeName" );
+				}
+				infoMap.put( info.getAttributeName(), info );
+			}
+		}
+		{
+			final Converts convertsAnnotation = xClass.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, xClass );
+					if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+						throw new IllegalStateException( "@Converts placed on @Entity/@MappedSuperclass must define attributeName" );
+					}
+					infoMap.put( info.getAttributeName(), info );
+				}
+			}
+		}
+	}
+
+	@Override
+	public void startingProperty(XProperty property) {
+		if ( property == null ) {
+			return;
+		}
+
+		final String propertyName = property.getName();
+		if ( attributeConversionInfoMap.containsKey( propertyName ) ) {
+			return;
+		}
+
+		{
+			// @Convert annotation on the Embeddable attribute
+			final Convert convertAnnotation = property.getAnnotation( Convert.class );
+			if ( convertAnnotation != null ) {
+				final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, property );
+				if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+					attributeConversionInfoMap.put( propertyName, info );
+				}
+				else {
+					attributeConversionInfoMap.put( propertyName + '.' + info.getAttributeName(), info );
+				}
+			}
+		}
+		{
+			// @Converts annotation on the Embeddable attribute
+			final Converts convertsAnnotation = property.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, property );
+					if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+						attributeConversionInfoMap.put( propertyName, info );
+					}
+					else {
+						attributeConversionInfoMap.put( propertyName + '.' + info.getAttributeName(), info );
+					}
+				}
+			}
+		}
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
+		return locateAttributeConversionInfo( property.getName() );
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
+		return attributeConversionInfoMap.get( path );
+	}
+
 	public String getEntityName() {
 		return persistentClass.getEntityName();
 	}
 
 	public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
 		//Ejb3Column.checkPropertyConsistency( ); //already called earlier
 		if ( columns != null && columns[0].isSecondary() ) {
 			//TODO move the getJoin() code here?
 			final Join join = columns[0].getJoin();
 			addPropertyToJoin( prop, declaringClass, join );
 		}
 		else {
 			addProperty( prop, declaringClass );
 		}
 	}
 
 	public void addProperty(Property prop, XClass declaringClass) {
 		if ( prop.getValue() instanceof Component ) {
 			//TODO handle quote and non quote table comparison
 			String tableName = prop.getValue().getTable().getName();
 			if ( getJoinsPerRealTableName().containsKey( tableName ) ) {
 				final Join join = getJoinsPerRealTableName().get( tableName );
 				addPropertyToJoin( prop, declaringClass, join );
 			}
 			else {
 				addPropertyToPersistentClass( prop, declaringClass );
 			}
 		}
 		else {
 			addPropertyToPersistentClass( prop, declaringClass );
 		}
 	}
 
 	public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
 		Join join = entityBinder.addJoin( joinTableAnn, this, noDelayInPkColumnCreation );
 		this.joins = entityBinder.getSecondaryTables();
 		return join;
 	}
 
 	private void addPropertyToPersistentClass(Property prop, XClass declaringClass) {
 		if ( declaringClass != null ) {
 			final InheritanceState inheritanceState = inheritanceStatePerClass.get( declaringClass );
 			if ( inheritanceState == null ) {
 				throw new AssertionFailure(
 						"Declaring class is not found in the inheritance state hierarchy: " + declaringClass
 				);
 			}
 			if ( inheritanceState.isEmbeddableSuperclass() ) {
 				persistentClass.addMappedsuperclassProperty(prop);
 				addPropertyToMappedSuperclass( prop, declaringClass );
 			}
 			else {
 				persistentClass.addProperty( prop );
 			}
 		}
 		else {
 			persistentClass.addProperty( prop );
 		}
 	}
 
 	private void addPropertyToMappedSuperclass(Property prop, XClass declaringClass) {
 		final Mappings mappings = getMappings();
 		final Class type = mappings.getReflectionManager().toClass( declaringClass );
 		MappedSuperclass superclass = mappings.getMappedSuperclass( type );
 		superclass.addDeclaredProperty( prop );
 	}
 
 	private void addPropertyToJoin(Property prop, XClass declaringClass, Join join) {
 		if ( declaringClass != null ) {
 			final InheritanceState inheritanceState = inheritanceStatePerClass.get( declaringClass );
 			if ( inheritanceState == null ) {
 				throw new AssertionFailure(
 						"Declaring class is not found in the inheritance state hierarchy: " + declaringClass
 				);
 			}
 			if ( inheritanceState.isEmbeddableSuperclass() ) {
 				join.addMappedsuperclassProperty(prop);
 				addPropertyToMappedSuperclass( prop, declaringClass );
 			}
 			else {
 				join.addProperty( prop );
 			}
 		}
 		else {
 			join.addProperty( prop );
 		}
 	}
 
 	/**
 	 * Needed for proper compliance with naming strategy, the property table
 	 * can be overriden if the properties are part of secondary tables
 	 */
 	private Map<String, Join> getJoinsPerRealTableName() {
 		if ( joinsPerRealTableName == null ) {
 			joinsPerRealTableName = new HashMap<String, Join>( joins.size() );
 			for (Join join : joins.values()) {
 				joinsPerRealTableName.put( join.getTable().getName(), join );
 			}
 		}
 		return joinsPerRealTableName;
 	}
 
 	public String getClassName() {
 		return persistentClass.getClassName();
 	}
 
 	public String getEntityOwnerClassName() {
 		return getClassName();
 	}
 
 	public Table getTable() {
 		return persistentClass.getTable();
 	}
 
 	public boolean isComponent() {
 		return false;
 	}
 
 	public boolean isEntity() {
 		return true;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return persistentClass;
 	}
 
 	public KeyValue getIdentifier() {
 		return persistentClass.getIdentifier();
 	}
 
 	public boolean isOrWithinEmbeddedId() {
 		return false;
 	}
 
 //	@Override
 //	public AttributeConverterDefinition resolveAttributeConverter(String attributeName) {
 //
 //		// @Convert annotations take precedence if present
 //		final Convert convertAnnotation = locateConvertAnnotation( property );
 //		if ( convertAnnotation != null ) {
 //			log.debugf(
 //					"Applying located @Convert AttributeConverter [%s] to attribute [%]",
 //					convertAnnotation.converter().getName(),
 //					property.getName()
 //			);
 //			attributeConverterDefinition = getMappings().locateAttributeConverter( convertAnnotation.converter() );
 //		}
 //		else {
 //			attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
 //		}
 //	}
 //
 //	@SuppressWarnings("unchecked")
 //	private Convert locateConvertAnnotation(XProperty property) {
 //		LOG.debugf(
 //				"Attempting to locate Convert annotation for property [%s:%s]",
 //				persistentClassName,
 //				property.getName()
 //		);
 //
 //		// first look locally on the property for @Convert/@Converts
 //		{
 //			Convert localConvertAnnotation = property.getAnnotation( Convert.class );
 //			if ( localConvertAnnotation != null ) {
 //				LOG.debugf(
 //						"Found matching local @Convert annotation [disableConversion=%s]",
 //						localConvertAnnotation.disableConversion()
 //				);
 //				return localConvertAnnotation.disableConversion()
 //						? null
 //						: localConvertAnnotation;
 //			}
 //		}
 //
 //		{
 //			Converts localConvertsAnnotation = property.getAnnotation( Converts.class );
 //			if ( localConvertsAnnotation != null ) {
 //				for ( Convert localConvertAnnotation : localConvertsAnnotation.value() ) {
 //					if ( isLocalMatch( localConvertAnnotation, property ) ) {
 //						LOG.debugf(
 //								"Found matching @Convert annotation as part local @Converts [disableConversion=%s]",
 //								localConvertAnnotation.disableConversion()
 //						);
 //						return localConvertAnnotation.disableConversion()
 //								? null
 //								: localConvertAnnotation;
 //					}
 //				}
 //			}
 //		}
 //
 //		if ( persistentClassName == null ) {
 //			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
 //			return null;
 //		}
 //
 //		final XClass owner;
 //		try {
 //			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
 //			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
 //		}
 //		catch (ClassNotFoundException e) {
 //			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
 //		}
 //
 //		return lookForEntityDefinedConvertAnnotation( property, owner );
 //	}
+
+
+	@Override
+	public String toString() {
+		return super.toString() + "(" + getEntityName() + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
index 84656bd428..3db168c2f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
@@ -1,103 +1,136 @@
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
+
 import javax.persistence.JoinTable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
  * @author Emmanuel Bernard
  */
 public class CollectionPropertyHolder extends AbstractPropertyHolder {
 	Collection collection;
 
 	public CollectionPropertyHolder(
 			Collection collection,
 			String path,
 			XClass clazzToProcess,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			Mappings mappings) {
 		super( path, parentPropertyHolder, clazzToProcess, mappings );
 		this.collection = collection;
 		setCurrentProperty( property );
 	}
 
+	@Override
+	protected String normalizeCompositePath(String attributeName) {
+		return attributeName;
+	}
+
+	@Override
+	protected String normalizeCompositePathForLogging(String attributeName) {
+		return collection.getRole() + '.' + attributeName;
+	}
+
+	@Override
+	public void startingProperty(XProperty property) {
+		// todo : implement
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
+		// todo : implement
+		return null;
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
+		// todo : implement
+		return null;
+	}
+
 	public String getClassName() {
 		throw new AssertionFailure( "Collection property holder does not have a class name" );
 	}
 
 	public String getEntityOwnerClassName() {
 		return null;
 	}
 
 	public Table getTable() {
 		return collection.getCollectionTable();
 	}
 
 	public void addProperty(Property prop, XClass declaringClass) {
 		throw new AssertionFailure( "Cannot add property to a collection" );
 	}
 
 	public KeyValue getIdentifier() {
 		throw new AssertionFailure( "Identifier collection not yet managed" );
 	}
 
 	public boolean isOrWithinEmbeddedId() {
 		return false;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return collection.getOwner();
 	}
 
 	public boolean isComponent() {
 		return false;
 	}
 
 	public boolean isEntity() {
 		return false;
 	}
 
 	public String getEntityName() {
 		return collection.getOwner().getEntityName();
 	}
 
 	public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
 		//Ejb3Column.checkPropertyConsistency( ); //already called earlier
 		throw new AssertionFailure( "addProperty to a join table of a collection: does it make sense?" );
 	}
 
 	public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
 		throw new AssertionFailure( "Add a <join> in a second pass" );
 	}
+
+	@Override
+	public String toString() {
+		return super.toString() + "(" + collection.getRole() + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
index 2bf5f45fda..7fc1137540 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
@@ -1,176 +1,389 @@
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
 import javax.persistence.Column;
+import javax.persistence.Convert;
+import javax.persistence.Converts;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
- * Component implementation of property holder
+ * PropertyHolder for composites (Embeddable/Embedded).
+ * <p/>
+ * To facilitate code comments, I'll often refer to this example:
+ * <pre>
+ *     &#064;Embeddable
+ *     &#064;Convert( attributeName="city", ... )
+ *     class Address {
+ *         ...
+ *         &#064;Convert(...)
+ *         public String city;
+ *     }
+ *
+ *     &#064;Entity
+ *     &#064;Convert( attributeName="homeAddress.city", ... )
+ *     class Person {
+ *         ...
+ *         &#064;Embedded
+ *         &#064;Convert( attributeName="city", ... )
+ *         public Address homeAddress;
+ *     }
+ * </pre>
+ *
+ * As you can see, lots of ways to specify the conversion for embeddable attributes :(
  *
+ * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class ComponentPropertyHolder extends AbstractPropertyHolder {
 	//TODO introduce a overrideTable() method for columns held by sec table rather than the hack
 	//     joinsPerRealTableName in ClassPropertyHolder
 	private Component component;
 	private boolean isOrWithinEmbeddedId;
 
+	private boolean virtual;
+	private String embeddedAttributeName;
+	private Map<String,AttributeConversionInfo> attributeConversionInfoMap;
+
+	public ComponentPropertyHolder(
+			Component component,
+			String path,
+			PropertyData inferredData,
+			PropertyHolder parent,
+			Mappings mappings) {
+		super( path, parent, inferredData.getPropertyClass(), mappings );
+		final XProperty embeddedXProperty = inferredData.getProperty();
+		setCurrentProperty( embeddedXProperty );
+		this.component = component;
+		this.isOrWithinEmbeddedId =
+				parent.isOrWithinEmbeddedId()
+						|| ( embeddedXProperty != null &&
+						( embeddedXProperty.isAnnotationPresent( Id.class )
+								|| embeddedXProperty.isAnnotationPresent( EmbeddedId.class ) ) );
+
+		this.virtual = embeddedXProperty == null;
+		if ( !virtual ) {
+			this.embeddedAttributeName = embeddedXProperty.getName();
+			this.attributeConversionInfoMap = processAttributeConversions( embeddedXProperty );
+		}
+		else {
+			embeddedAttributeName = "";
+			this.attributeConversionInfoMap = Collections.emptyMap();
+		}
+	}
+
+	/**
+	 * This is called from our constructor and handles (in order):<ol>
+	 *     <li>@Convert annotation at the Embeddable class level</li>
+	 *     <li>@Converts annotation at the Embeddable class level</li>
+	 *     <li>@Convert annotation at the Embedded attribute level</li>
+	 *     <li>@Converts annotation at the Embedded attribute level</li>
+	 * </ol>
+	 * <p/>
+	 * The order is important to ensure proper precedence.
+	 * <p/>
+	 * {@literal @Convert/@Converts} annotations at the Embeddable attribute level are handled in the calls to
+	 * {@link #startingProperty}.  Duplicates are simply ignored there.
+	 *
+	 * @param embeddedXProperty The property that is the composite being described by this ComponentPropertyHolder
+	 */
+	private Map<String,AttributeConversionInfo> processAttributeConversions(XProperty embeddedXProperty) {
+		final Map<String,AttributeConversionInfo> infoMap = new HashMap<String, AttributeConversionInfo>();
+
+		final XClass embeddableXClass = embeddedXProperty.getType();
+
+		// as a baseline, we want to apply conversions from the Embeddable and then overlay conversions
+		// from the Embedded
+
+		// first apply conversions from the Embeddable...
+		{
+			// @Convert annotation on the Embeddable class level
+			final Convert convertAnnotation = embeddableXClass.getAnnotation( Convert.class );
+			if ( convertAnnotation != null ) {
+				final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, embeddableXClass );
+				if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+					throw new IllegalStateException( "@Convert placed on @Embeddable must define attributeName" );
+				}
+				infoMap.put( info.getAttributeName(), info );
+			}
+		}
+		{
+			// @Converts annotation on the Embeddable class level
+			final Converts convertsAnnotation = embeddableXClass.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, embeddableXClass );
+					if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+						throw new IllegalStateException( "@Converts placed on @Embeddable must define attributeName" );
+					}
+					infoMap.put( info.getAttributeName(), info );
+				}
+			}
+		}
+
+		// then we can overlay any conversions from the Embedded attribute
+		{
+			// @Convert annotation on the Embedded attribute
+			final Convert convertAnnotation = embeddedXProperty.getAnnotation( Convert.class );
+			if ( convertAnnotation != null ) {
+				final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, embeddableXClass );
+				if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+					throw new IllegalStateException( "Convert placed on Embedded attribute must define (sub)attributeName" );
+				}
+				infoMap.put( info.getAttributeName(), info );
+			}
+		}
+		{
+			// @Converts annotation on the Embedded attribute
+			final Converts convertsAnnotation = embeddedXProperty.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, embeddableXClass );
+					if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
+						throw new IllegalStateException( "Convert placed on Embedded attribute must define (sub)attributeName" );
+					}
+					infoMap.put( info.getAttributeName(), info );
+				}
+			}
+		}
+
+		return infoMap;
+	}
+
+	@Override
+	protected String normalizeCompositePath(String attributeName) {
+		return embeddedAttributeName + '.' + attributeName;
+	}
+
+	@Override
+	protected String normalizeCompositePathForLogging(String attributeName) {
+		return normalizeCompositePath( attributeName );
+	}
+
+	@Override
+	public void startingProperty(XProperty property) {
+		if ( property == null ) {
+			return;
+		}
+
+		if ( virtual ) {
+			return;
+		}
+
+		// again : the property coming in here *should* be the property on the embeddable (Address#city in the example),
+		// so we just ignore it if there is already an existing conversion info for that path since they would have
+		// precedence
+
+		// technically we should only do this for properties of "basic type"
+
+		final String path = embeddedAttributeName + '.' + property.getName();
+		if ( attributeConversionInfoMap.containsKey( path ) ) {
+			return;
+		}
+
+		{
+			// @Convert annotation on the Embeddable attribute
+			final Convert convertAnnotation = property.getAnnotation( Convert.class );
+			if ( convertAnnotation != null ) {
+				final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, property );
+				attributeConversionInfoMap.put( property.getName(), info );
+			}
+		}
+		{
+			// @Converts annotation on the Embeddable attribute
+			final Converts convertsAnnotation = property.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, property );
+					attributeConversionInfoMap.put( property.getName(), info );
+				}
+			}
+		}
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
+		final String propertyName = property.getName();
+
+		// conversions on parent would have precedence
+		AttributeConversionInfo conversion = locateAttributeConversionInfo( propertyName );
+		if ( conversion != null ) {
+			return conversion;
+		}
+
+
+		Convert localConvert = property.getAnnotation( Convert.class );
+		if ( localConvert != null ) {
+			return new AttributeConversionInfo( localConvert, property );
+		}
+
+		return null;
+	}
+
+	@Override
+	protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
+		final String embeddedPath = embeddedAttributeName + '.' + path;
+		AttributeConversionInfo fromParent = parent.locateAttributeConversionInfo( embeddedPath );
+		if ( fromParent != null ) {
+			return fromParent;
+		}
+
+		AttributeConversionInfo fromEmbedded = attributeConversionInfoMap.get( embeddedPath );
+		if ( fromEmbedded != null ) {
+			return fromEmbedded;
+		}
+
+		return attributeConversionInfoMap.get( path );
+	}
+
 	public String getEntityName() {
 		return component.getComponentClassName();
 	}
 
 	public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
 		//Ejb3Column.checkPropertyConsistency( ); //already called earlier
 		/*
 		 * Check table matches between the component and the columns
 		 * if not, change the component table if no properties are set
 		 * if a property is set already the core cannot support that
 		 */
 		if (columns != null) {
 			Table table = columns[0].getTable();
 			if ( !table.equals( component.getTable() ) ) {
 				if ( component.getPropertySpan() == 0 ) {
 					component.setTable( table );
 				}
 				else {
 					throw new AnnotationException(
 							"A component cannot hold properties split into 2 different tables: "
 									+ this.getPath()
 					);
 				}
 			}
 		}
 		addProperty( prop, declaringClass );
 	}
 
 	public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
 		return parent.addJoin( joinTableAnn, noDelayInPkColumnCreation );
 
 	}
 
-	public ComponentPropertyHolder(
-			Component component,
-			String path,
-			PropertyData inferredData,
-			PropertyHolder parent,
-			Mappings mappings) {
-		super( path, parent, inferredData.getPropertyClass(), mappings );
-		final XProperty property = inferredData.getProperty();
-		setCurrentProperty( property );
-		this.component = component;
-		this.isOrWithinEmbeddedId =
-				parent.isOrWithinEmbeddedId()
-				|| ( property != null &&
-					( property.isAnnotationPresent( Id.class )
-					|| property.isAnnotationPresent( EmbeddedId.class ) ) );
-	}
-
 	public String getClassName() {
 		return component.getComponentClassName();
 	}
 
 	public String getEntityOwnerClassName() {
 		return component.getOwner().getClassName();
 	}
 
 	public Table getTable() {
 		return component.getTable();
 	}
 
 	public void addProperty(Property prop, XClass declaringClass) {
 		component.addProperty( prop );
 	}
 
 	public KeyValue getIdentifier() {
 		return component.getOwner().getIdentifier();
 	}
 
 	public boolean isOrWithinEmbeddedId() {
 		return isOrWithinEmbeddedId;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return component.getOwner();
 	}
 
 	public boolean isComponent() {
 		return true;
 	}
 
 	public boolean isEntity() {
 		return false;
 	}
 
 	public void setParentProperty(String parentProperty) {
 		component.setParentProperty( parentProperty );
 	}
 
 	@Override
 	public Column[] getOverriddenColumn(String propertyName) {
 		//FIXME this is yukky
 		Column[] result = super.getOverriddenColumn( propertyName );
 		if ( result == null ) {
 			String userPropertyName = extractUserPropertyName( "id", propertyName );
 			if ( userPropertyName != null ) result = super.getOverriddenColumn( userPropertyName );
 		}
 		if ( result == null ) {
 			String userPropertyName = extractUserPropertyName( "_identifierMapper", propertyName );
 			if ( userPropertyName != null ) result = super.getOverriddenColumn( userPropertyName );
 		}
 		return result;
 	}
 
 	private String extractUserPropertyName(String redundantString, String propertyName) {
 		String result = null;
 		String className = component.getOwner().getClassName();
 		if ( propertyName.startsWith( className )
 				&& propertyName.length() > className.length() + 2 + redundantString.length() // .id.
 				&& propertyName.substring(
 				className.length() + 1, className.length() + 1 + redundantString.length()
 		).equals( redundantString )
 				) {
 			//remove id we might be in a @IdCLass case
 			result = className + propertyName.substring( className.length() + 1 + redundantString.length() );
 		}
 		return result;
 	}
 
 	@Override
 	public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
 		return super.getOverriddenJoinColumn( propertyName );
 	}
+
+	@Override
+	public String toString() {
+		return super.toString() + "(" + parent.normalizeCompositePathForLogging( embeddedAttributeName ) + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
index bb79c2810e..1094771b19 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
@@ -1,97 +1,112 @@
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
+import javax.persistence.AttributeConverter;
 import javax.persistence.Column;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
  * Property holder abstract property containers from their direct implementation
  *
  * @author Emmanuel Bernard
  */
 public interface PropertyHolder {
 	String getClassName();
 
 	String getEntityOwnerClassName();
 
 	Table getTable();
 
 	void addProperty(Property prop, XClass declaringClass);
 
 	void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass);
 
 	KeyValue getIdentifier();
 
 	/**
 	 * Return true if this component is or is embedded in a @EmbeddedId
 	 */
 	boolean isOrWithinEmbeddedId();
 
 	PersistentClass getPersistentClass();
 
 	boolean isComponent();
 
 	boolean isEntity();
 
 	void setParentProperty(String parentProperty);
 
 	String getPath();
 
-//	public AttributeConverterDefinition resolveAttributeConverter(String attributeName);
-
 	/**
 	 * return null if the column is not overridden, or an array of column if true
 	 */
 	Column[] getOverriddenColumn(String propertyName);
 
 	/**
 	 * return null if the column is not overridden, or an array of column if true
 	 */
 	JoinColumn[] getOverriddenJoinColumn(String propertyName);
 
 	/**
 	 * return
 	 *  - null if no join table is present,
 	 *  - the join table if not overridden,
 	 *  - the overridden join table otherwise
 	 */
 	JoinTable getJoinTable(XProperty property);
 
 	String getEntityName();
 
 	Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation);
 
 	boolean isInIdClass();
 
 	void setInIdClass(Boolean isInIdClass);
+
+	/**
+	 * Called during binding to allow the PropertyHolder to inspect its discovered properties.  Mainly
+	 * this is used in collecting attribute conversion declarations (via @Convert/@Converts).
+	 *
+	 * @param property The property
+	 */
+	void startingProperty(XProperty property);
+
+	/**
+	 * Determine the AttributeConverter to use for the given property.
+	 *
+	 * @param property
+	 * @return
+	 */
+	AttributeConverterDefinition resolveAttributeConverterDefinition(XProperty property);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolver.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolver.java
new file mode 100644
index 0000000000..1fec9188f3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolver.java
@@ -0,0 +1,40 @@
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
+package org.hibernate.cfg.annotations;
+
+import javax.persistence.AttributeConverter;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AttributeConverterResolver {
+	/**
+	 * Resolve the AttributeConverter to use for an attribute during binding.  The assumption of the API
+	 * is that the path information is encoded into the AttributeConverterResolver instance itself to account
+	 * for all the different paths converters can be defined on.
+	 *
+	 * @return The AttributeConverter to use, or {@code null} if none.
+	 */
+	public AttributeConverter resolveAttributeConverter();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolverContext.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolverContext.java
new file mode 100644
index 0000000000..061951ad02
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/AttributeConverterResolverContext.java
@@ -0,0 +1,184 @@
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
+package org.hibernate.cfg.annotations;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.annotations.common.reflection.XClass;
+import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.cfg.Mappings;
+
+/**
+ * Manages the resolution of the AttributeConverter, if one, for a property (path).
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConverterResolverContext {
+	private final Mappings mappings;
+
+	public AttributeConverterResolverContext(Mappings mappings) {
+		this.mappings = mappings;
+	}
+
+	private Map<String,EntityPropertyPathSource> entityPropertyPathSourceMap;
+
+	public EntityPropertyPathSource resolveEntityPropertyPathSource(XClass entityClass) {
+		EntityPropertyPathSource found = null;
+		if ( entityPropertyPathSourceMap == null ) {
+			entityPropertyPathSourceMap = new HashMap<String, EntityPropertyPathSource>();
+		}
+		else {
+			found = entityPropertyPathSourceMap.get( entityClass.getName() );
+		}
+
+		if ( found == null ) {
+			found = new EntityPropertyPathSource( entityClass );
+			entityPropertyPathSourceMap.put( entityClass.getName(), found );
+		}
+
+		return found;
+	}
+
+	private Map<String,CollectionPropertyPathSource> collectionPropertyPathSourceMap;
+
+	public CollectionPropertyPathSource resolveRootCollectionPropertyPathSource(XClass owner, XProperty property) {
+		CollectionPropertyPathSource found = null;
+		if ( collectionPropertyPathSourceMap == null ) {
+			collectionPropertyPathSourceMap = new HashMap<String, CollectionPropertyPathSource>();
+		}
+		else {
+			found = collectionPropertyPathSourceMap.get( owner.getName() );
+		}
+
+		if ( found == null ) {
+			final EntityPropertyPathSource ownerSource = resolveEntityPropertyPathSource( owner );
+			found = new CollectionPropertyPathSource( ownerSource, property.getName() );
+			collectionPropertyPathSourceMap.put( owner.getName(), found );
+		}
+
+		return found;
+	}
+
+	public static interface PropertyPathSource {
+		public CompositePropertyPathSource makeComposite(String propertyName);
+		public CollectionPropertyPathSource makeCollection(String propertyName);
+	}
+
+	public static abstract class AbstractPropertyPathSource implements PropertyPathSource {
+		protected abstract String normalize(String path);
+
+		private Map<String,CompositePropertyPathSource> compositePropertyPathSourceMap;
+
+		@Override
+		public CompositePropertyPathSource makeComposite(String propertyName) {
+			CompositePropertyPathSource found = null;
+			if ( compositePropertyPathSourceMap == null ) {
+				compositePropertyPathSourceMap = new HashMap<String, CompositePropertyPathSource>();
+			}
+			else {
+				found = compositePropertyPathSourceMap.get( propertyName );
+			}
+
+			if ( found == null ) {
+				found = new CompositePropertyPathSource( this, propertyName );
+				compositePropertyPathSourceMap.put( propertyName, found );
+			}
+
+			return found;
+		}
+
+		private Map<String,CollectionPropertyPathSource> collectionPropertyPathSourceMap;
+
+		@Override
+		public CollectionPropertyPathSource makeCollection(String propertyName) {
+			CollectionPropertyPathSource found = null;
+			if ( collectionPropertyPathSourceMap == null ) {
+				collectionPropertyPathSourceMap = new HashMap<String, CollectionPropertyPathSource>();
+			}
+			else {
+				found = collectionPropertyPathSourceMap.get( propertyName );
+			}
+
+			if ( found == null ) {
+				found = new CollectionPropertyPathSource( this, propertyName );
+				collectionPropertyPathSourceMap.put( propertyName, found );
+			}
+
+			return found;
+		}
+	}
+
+	public static class EntityPropertyPathSource extends AbstractPropertyPathSource implements PropertyPathSource {
+		private final XClass entityClass;
+
+		private EntityPropertyPathSource(XClass entityClass) {
+			this.entityClass = entityClass;
+		}
+
+		@Override
+		protected String normalize(String path) {
+			return path;
+		}
+	}
+
+	public static class CompositePropertyPathSource extends AbstractPropertyPathSource implements PropertyPathSource {
+		private final AbstractPropertyPathSource sourceOfComposite;
+		private final String compositeName;
+
+		public CompositePropertyPathSource(AbstractPropertyPathSource sourceOfComposite, String compositeName) {
+			this.sourceOfComposite = sourceOfComposite;
+			this.compositeName = compositeName;
+		}
+
+		@Override
+		protected String normalize(String path) {
+			return getSourceOfComposite().normalize( compositeName ) + "." + path;
+		}
+
+		public AbstractPropertyPathSource getSourceOfComposite() {
+			return sourceOfComposite;
+		}
+
+		public String getCompositeName() {
+			return compositeName;
+		}
+	}
+
+	public static class CollectionPropertyPathSource extends AbstractPropertyPathSource implements PropertyPathSource {
+		private final AbstractPropertyPathSource collectionSource;
+		private final String collectionName;
+
+		private CollectionPropertyPathSource(AbstractPropertyPathSource collectionSource, String collectionName) {
+			this.collectionSource = collectionSource;
+			this.collectionName = collectionName;
+		}
+
+		@Override
+		protected String normalize(String path) {
+			return path;
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 5c6ddefbca..539b7c5419 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -370,1162 +370,1162 @@ public abstract class CollectionBinder {
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
 
 		applySortingAndOrdering( collection );
 
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
-				elementBinder.setType( property, elementClass, collValue.getOwnerEntityName() );
+				elementBinder.setType( property, elementClass, collValue.getOwnerEntityName(), null );
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
 			( (ManyToOne) value ).setReferenceToPrimaryKey( referencedPropertyName == null );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
index 1e5e7d8150..a137d1daa2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
@@ -1,436 +1,436 @@
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
 import java.util.Map;
 import java.util.Random;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.MapKeyClass;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.sql.Template;
 
 /**
  * Implementation to bind a Map
  *
  * @author Emmanuel Bernard
  */
 public class MapBinder extends CollectionBinder {
 	public MapBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	public boolean isMap() {
 		return true;
 	}
 
 	protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Map( getMappings(), persistentClass );
 	}
 
 	@Override
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
 		return new CollectionSecondPass( mappings, MapBinder.this.collection ) {
 			public void secondPass(Map persistentClasses, Map inheritedMetas)
 					throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
 				);
 				bindKeyFromAssociationTable(
 						collType, persistentClasses, mapKeyPropertyName, property, isEmbedded, mappings,
 						mapKeyColumns, mapKeyManyToManyColumns,
 						inverseColumns != null ? inverseColumns[0].getPropertyName() : null
 				);
 			}
 		};
 	}
 
 	private void bindKeyFromAssociationTable(
 			XClass collType,
 			Map persistentClasses,
 			String mapKeyPropertyName,
 			XProperty property,
 			boolean isEmbedded,
 			Mappings mappings,
 			Ejb3Column[] mapKeyColumns,
 			Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			String targetPropertyName) {
 		if ( mapKeyPropertyName != null ) {
 			//this is an EJB3 @MapKey
 			PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType.getName() );
 			if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
 			Property mapProperty = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
 			if ( mapProperty == null ) {
 				throw new AnnotationException(
 						"Map key property not found: " + collType + "." + mapKeyPropertyName
 				);
 			}
 			org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
 			Value indexValue = createFormulatedValue(
 					mapProperty.getValue(), map, targetPropertyName, associatedClass, mappings
 			);
 			map.setIndex( indexValue );
 		}
 		else {
 			//this is a true Map mapping
 			//TODO ugly copy/pastle from CollectionBinder.bindManyToManySecondPass
 			String mapKeyType;
 			Class target = void.class;
 			/*
 			 * target has priority over reflection for the map key type
 			 * JPA 2 has priority
 			 */
 			if ( property.isAnnotationPresent( MapKeyClass.class ) ) {
 				target = property.getAnnotation( MapKeyClass.class ).value();
 			}
 			if ( !void.class.equals( target ) ) {
 				mapKeyType = target.getName();
 			}
 			else {
 				mapKeyType = property.getMapKey().getName();
 			}
 			PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( mapKeyType );
 			boolean isIndexOfEntities = collectionEntity != null;
 			ManyToOne element = null;
 			org.hibernate.mapping.Map mapValue = (org.hibernate.mapping.Map) this.collection;
 			if ( isIndexOfEntities ) {
 				element = new ManyToOne( mappings, mapValue.getCollectionTable() );
 				mapValue.setIndex( element );
 				element.setReferencedEntityName( mapKeyType );
 				//element.setFetchMode( fetchMode );
 				//element.setLazy( fetchMode != FetchMode.JOIN );
 				//make the second join non lazy
 				element.setFetchMode( FetchMode.JOIN );
 				element.setLazy( false );
 				//does not make sense for a map key element.setIgnoreNotFound( ignoreNotFound );
 			}
 			else {
 				XClass elementClass;
 				AnnotatedClassType classType;
 				PropertyHolder holder = null;
 				if ( BinderHelper.PRIMITIVE_NAMES.contains( mapKeyType ) ) {
 					classType = AnnotatedClassType.NONE;
 					elementClass = null;
 				}
 				else {
 					try {
 						elementClass = mappings.getReflectionManager().classForName( mapKeyType, MapBinder.class );
 					}
 					catch (ClassNotFoundException e) {
 						throw new AnnotationException( "Unable to find class: " + mapKeyType, e );
 					}
 					classType = mappings.getClassType( elementClass );
 
 					holder = PropertyHolderBuilder.buildPropertyHolder(
 							mapValue,
 							StringHelper.qualify( mapValue.getRole(), "mapkey" ),
 							elementClass,
 							property, propertyHolder, mappings
 					);
 					//force in case of attribute override
 					boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 							|| property.isAnnotationPresent( AttributeOverrides.class );
 					if ( isEmbedded || attributeOverride ) {
 						classType = AnnotatedClassType.EMBEDDABLE;
 					}
 				}
 
 				PersistentClass owner = mapValue.getOwner();
 				AccessType accessType;
 				// FIXME support @Access for collection of elements
 				// String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					accessType = prop.getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 					EntityBinder entityBinder = new EntityBinder();
 
 					PropertyData inferredData;
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "index", elementClass );
 					}
 					else {
 						//"key" is the JPA 2 prefix for map keys
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "key", elementClass );
 					}
 
 					//TODO be smart with isNullable
 					Component component = AnnotationBinder.fillComponent(
 							holder, inferredData, accessType, true,
 							entityBinder, false, false,
 							true, mappings, inheritanceStatePerClass
 					);
 					mapValue.setIndex( component );
 				}
 				else {
 					SimpleValueBinder elementBinder = new SimpleValueBinder();
 					elementBinder.setMappings( mappings );
 					elementBinder.setReturnedClassName( mapKeyType );
 
 					Ejb3Column[] elementColumns = mapKeyColumns;
 					if ( elementColumns == null || elementColumns.length == 0 ) {
 						elementColumns = new Ejb3Column[1];
 						Ejb3Column column = new Ejb3Column();
 						column.setImplicit( false );
 						column.setNullable( true );
 						column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 						column.setLogicalColumnName( Collection.DEFAULT_KEY_COLUMN_NAME );
 						//TODO create an EMPTY_JOINS collection
 						column.setJoins( new HashMap<String, Join>() );
 						column.setMappings( mappings );
 						column.bind();
 						elementColumns[0] = column;
 					}
 					//override the table
 					for (Ejb3Column column : elementColumns) {
 						column.setTable( mapValue.getCollectionTable() );
 					}
 					elementBinder.setColumns( elementColumns );
 					//do not call setType as it extract the type from @Type
 					//the algorithm generally does not apply for map key anyway
 					elementBinder.setKey(true);
 					MapKeyType mapKeyTypeAnnotation = property.getAnnotation( MapKeyType.class );
 					if ( mapKeyTypeAnnotation != null && !BinderHelper.isEmptyAnnotationValue(
 							mapKeyTypeAnnotation.value()
 									.type()
 					) ) {
 						elementBinder.setExplicitType( mapKeyTypeAnnotation.value() );
 					}
 					else {
-						elementBinder.setType( property, elementClass, this.collection.getOwnerEntityName() );
+						elementBinder.setType( property, elementClass, this.collection.getOwnerEntityName(), null );
 					}
 					elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 					elementBinder.setAccessType( accessType );
 					mapValue.setIndex( elementBinder.make() );
 				}
 			}
 			//FIXME pass the Index Entity JoinColumns
 			if ( !collection.isOneToMany() ) {
 				//index column shoud not be null
 				for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
 					col.forceNotNull();
 				}
 			}
 			if ( isIndexOfEntities ) {
 				bindManytoManyInverseFk(
 						collectionEntity,
 						mapKeyManyToManyColumns,
 						element,
 						false, //a map key column has no unique constraint
 						mappings
 				);
 			}
 		}
 	}
 
 	protected Value createFormulatedValue(
 			Value value,
 			Collection collection,
 			String targetPropertyName,
 			PersistentClass associatedClass,
 			Mappings mappings) {
 		Value element = collection.getElement();
 		String fromAndWhere = null;
 		if ( !( element instanceof OneToMany ) ) {
 			String referencedPropertyName = null;
 			if ( element instanceof ToOne ) {
 				referencedPropertyName = ( (ToOne) element ).getReferencedPropertyName();
 			}
 			else if ( element instanceof DependantValue ) {
 				//TODO this never happen I think
 				if ( propertyName != null ) {
 					referencedPropertyName = collection.getReferencedPropertyName();
 				}
 				else {
 					throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 				}
 			}
 			Iterator referencedEntityColumns;
 			if ( referencedPropertyName == null ) {
 				referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
 			}
 			else {
 				Property referencedProperty = associatedClass.getRecursiveProperty( referencedPropertyName );
 				referencedEntityColumns = referencedProperty.getColumnIterator();
 			}
 			String alias = "$alias$";
 			StringBuilder fromAndWhereSb = new StringBuilder( " from " )
 					.append( associatedClass.getTable().getName() )
 							//.append(" as ") //Oracle doesn't support it in subqueries
 					.append( " " )
 					.append( alias ).append( " where " );
 			Iterator collectionTableColumns = element.getColumnIterator();
 			while ( collectionTableColumns.hasNext() ) {
 				Column colColumn = (Column) collectionTableColumns.next();
 				Column refColumn = (Column) referencedEntityColumns.next();
 				fromAndWhereSb.append( alias ).append( '.' ).append( refColumn.getQuotedName() )
 						.append( '=' ).append( colColumn.getQuotedName() ).append( " and " );
 			}
 			fromAndWhere = fromAndWhereSb.substring( 0, fromAndWhereSb.length() - 5 );
 		}
 
 		if ( value instanceof Component ) {
 			Component component = (Component) value;
 			Iterator properties = component.getPropertyIterator();
 			Component indexComponent = new Component( mappings, collection );
 			indexComponent.setComponentClassName( component.getComponentClassName() );
 			//TODO I don't know if this is appropriate
 			indexComponent.setNodeName( "index" );
 			while ( properties.hasNext() ) {
 				Property current = (Property) properties.next();
 				Property newProperty = new Property();
 				newProperty.setCascade( current.getCascade() );
 				newProperty.setGeneration( current.getGeneration() );
 				newProperty.setInsertable( false );
 				newProperty.setUpdateable( false );
 				newProperty.setMetaAttributes( current.getMetaAttributes() );
 				newProperty.setName( current.getName() );
 				newProperty.setNodeName( current.getNodeName() );
 				newProperty.setNaturalIdentifier( false );
 				//newProperty.setOptimisticLocked( false );
 				newProperty.setOptional( false );
 				newProperty.setPersistentClass( current.getPersistentClass() );
 				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
 				newProperty.setSelectable( current.isSelectable() );
 				newProperty.setValue(
 						createFormulatedValue(
 								current.getValue(), collection, targetPropertyName, associatedClass, mappings
 						)
 				);
 				indexComponent.addProperty( newProperty );
 			}
 			return indexComponent;
 		}
 		else if ( value instanceof SimpleValue ) {
 			SimpleValue sourceValue = (SimpleValue) value;
 			SimpleValue targetValue;
 			if ( value instanceof ManyToOne ) {
 				ManyToOne sourceManyToOne = (ManyToOne) sourceValue;
 				ManyToOne targetManyToOne = new ManyToOne( mappings, collection.getCollectionTable() );
 				targetManyToOne.setFetchMode( FetchMode.DEFAULT );
 				targetManyToOne.setLazy( true );
 				//targetValue.setIgnoreNotFound( ); does not make sense for a map key
 				targetManyToOne.setReferencedEntityName( sourceManyToOne.getReferencedEntityName() );
 				targetValue = targetManyToOne;
 			}
 			else {
 				targetValue = new SimpleValue( mappings, collection.getCollectionTable() );
 				targetValue.setTypeName( sourceValue.getTypeName() );
 				targetValue.setTypeParameters( sourceValue.getTypeParameters() );
 			}
 			Iterator columns = sourceValue.getColumnIterator();
 			Random random = new Random();
 			while ( columns.hasNext() ) {
 				Object current = columns.next();
 				Formula formula = new Formula();
 				String formulaString;
 				if ( current instanceof Column ) {
 					formulaString = ( (Column) current ).getQuotedName();
 				}
 				else if ( current instanceof Formula ) {
 					formulaString = ( (Formula) current ).getFormula();
 				}
 				else {
 					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
 				}
 				if ( fromAndWhere != null ) {
 					formulaString = Template.renderWhereStringTemplate( formulaString, "$alias$", new HSQLDialect() );
 					formulaString = "(select " + formulaString + fromAndWhere + ")";
 					formulaString = StringHelper.replace(
 							formulaString,
 							"$alias$",
 							"a" + random.nextInt( 16 )
 					);
 				}
 				formula.setFormula( formulaString );
 				targetValue.addFormula( formula );
 
 			}
 			return targetValue;
 		}
 		else {
 			throw new AssertionFailure( "Unknown type encounters for map key: " + value.getClass() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
index 0aa513d6b3..0fcf74f9d2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
@@ -1,373 +1,379 @@
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
 import javax.persistence.Lob;
 
 import org.hibernate.AnnotationException;
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
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
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
+
 		LOG.debugf( "MetadataSourceProcessor property %s with lazy=%s", name, lazy );
-		String containerClassName = holder == null ?
-				null :
-				holder.getClassName();
+		final String containerClassName = holder.getClassName();
+		holder.startingProperty( property );
+
 		simpleValueBinder = new SimpleValueBinder();
 		simpleValueBinder.setMappings( mappings );
 		simpleValueBinder.setPropertyName( name );
 		simpleValueBinder.setReturnedClassName( returnedClassName );
 		simpleValueBinder.setColumns( columns );
 		simpleValueBinder.setPersistentClassName( containerClassName );
-		simpleValueBinder.setType( property, returnedClass, containerClassName );
+		simpleValueBinder.setType(
+				property,
+				returnedClass,
+				containerClassName,
+				holder.resolveAttributeConverterDefinition( property )
+		);
 		simpleValueBinder.setMappings( mappings );
 		simpleValueBinder.setReferencedEntityName( referencedEntityName );
 		simpleValueBinder.setAccessType( accessType );
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
 		LOG.debugf( "Building property %s", name );
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
 		
 		NaturalId naturalId = property != null ? property.getAnnotation( NaturalId.class ) : null;
 		if ( naturalId != null ) {
 			if ( ! entityBinder.isRootEntity() ) {
 				throw new AnnotationException( "@NaturalId only valid on root entity (or its @MappedSuperclasses)" );
 			}
 			if ( ! naturalId.mutable() ) {
 				updatable = false;
 			}
 			prop.setNaturalIdentifier( true );
 		}
 		
 		// HHH-4635 -- needed for dialect-specific property ordering
 		Lob lob = property != null ? property.getAnnotation( Lob.class ) : null;
 		prop.setLob( lob != null );
 		
 		prop.setInsertable( insertable );
 		prop.setUpdateable( updatable );
 
 		// this is already handled for collections in CollectionBinder...
 		if ( Collection.class.isInstance( value ) ) {
 			prop.setOptimisticLocked( ( (Collection) value ).isOptimisticLocked() );
 		}
 		else {
 			final OptimisticLock lockAnn = property != null
 					? property.getAnnotation( OptimisticLock.class )
 					: null;
 			if ( lockAnn != null ) {
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
 			final boolean isOwnedValue = !isToOneValue( value ) || insertable; // && updatable as well???
 			final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 					? ! lockAnn.excluded()
 					: isOwnedValue;
 			prop.setOptimisticLocked( includeInOptimisticLockChecks );
 		}
 
 		LOG.tracev( "Cascading {0} with {1}", name, cascade );
 		this.mappingProperty = prop;
 		return prop;
 	}
 
 	private boolean isCollection(Value value) {
 		return Collection.class.isInstance( value );
 	}
 
 	private boolean isToOneValue(Value value) {
 		return ToOne.class.isInstance( value );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 8a78261d37..04d088436f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,793 +1,527 @@
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
 
-import java.io.Serializable;
-import java.lang.reflect.TypeVariable;
-import java.util.Calendar;
-import java.util.Date;
-import java.util.Properties;
-
-import javax.persistence.AttributeConverter;
-import javax.persistence.Convert;
-import javax.persistence.Converts;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
+import java.io.Serializable;
+import java.util.Calendar;
+import java.util.Date;
+import java.util.Properties;
+
+import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.Nationalized;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
+import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
-import org.hibernate.annotations.common.util.ReflectHelper;
+import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
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
-import org.jboss.logging.Logger;
 
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
 
-	public void setType(XProperty property, XClass returnedClass, String declaringClassName) {
+	public void setType(XProperty property, XClass returnedClass, String declaringClassName, AttributeConverterDefinition attributeConverterDefinition) {
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
 
-		applyAttributeConverter( property );
+		applyAttributeConverter( property, attributeConverterDefinition );
 	}
 
-	private void applyAttributeConverter(XProperty property) {
-		if ( attributeConverterDefinition != null ) {
+	private void applyAttributeConverter(XProperty property, AttributeConverterDefinition attributeConverterDefinition) {
+		if ( attributeConverterDefinition == null ) {
 			return;
 		}
 
 		LOG.debugf( "Starting applyAttributeConverter [%s:%s]", persistentClassName, property.getName() );
 
 		if ( property.isAnnotationPresent( Id.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Id attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( isVersion ) {
 			LOG.debugf( "Skipping AttributeConverter checks for version attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( property.isAnnotationPresent( Temporal.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Temporal attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( property.isAnnotationPresent( Enumerated.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Enumerated attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( isAssociation() ) {
 			LOG.debugf( "Skipping AttributeConverter checks for association attribute [%s]", property.getName() );
 			return;
 		}
 
-		// @Convert annotations take precedence if present
-		final Convert convertAnnotation = locateConvertAnnotation( property );
-		if ( convertAnnotation != null ) {
-			LOG.debugf(
-					"Applying located @Convert AttributeConverter [%s] to attribute [%]",
-					convertAnnotation.converter().getName(),
-					property.getName()
-			);
-			attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
-			if ( attributeConverterDefinition == null ) {
-				attributeConverterDefinition = makeAttributeConverterDefinition( convertAnnotation );
-			}
-		}
-		else {
-			attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
-		}
-	}
-
-	private AttributeConverterDefinition makeAttributeConverterDefinition(Convert convertAnnotation) {
-		try {
-			return new AttributeConverterDefinition(
-					(AttributeConverter) convertAnnotation.converter().newInstance(),
-					false
-			);
-		}
-		catch (Exception e) {
-			throw new AnnotationException( "Unable to create AttributeConverter instance", e );
-		}
-	}
-
-	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
-		LOG.debugf(
-				"Attempting to locate auto-apply AttributeConverter for property [%s:%s]",
-				persistentClassName,
-				property.getName()
-		);
-		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
-		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
-			if ( ! attributeConverterDefinition.isAutoApply() ) {
-				continue;
-			}
-			LOG.debugf(
-					"Checking auto-apply AttributeConverter [%s] type [%s] for match [%s]",
-					attributeConverterDefinition.toString(),
-					attributeConverterDefinition.getEntityAttributeType().getSimpleName(),
-					propertyType.getSimpleName()
-			);
-			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
-				return attributeConverterDefinition;
-			}
-		}
-		return null;
+		this.attributeConverterDefinition = attributeConverterDefinition;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
 	}
 
-	@SuppressWarnings("unchecked")
-	private Convert locateConvertAnnotation(XProperty property) {
-		LOG.debugf(
-				"Attempting to locate Convert annotation for property [%s:%s]",
-				persistentClassName,
-				property.getName()
-		);
-
-		// first look locally on the property for @Convert/@Converts
-		{
-			Convert localConvertAnnotation = property.getAnnotation( Convert.class );
-			if ( localConvertAnnotation != null ) {
-				LOG.debugf(
-						"Found matching local @Convert annotation [disableConversion=%s]",
-						localConvertAnnotation.disableConversion()
-				);
-				return localConvertAnnotation.disableConversion()
-						? null
-						: localConvertAnnotation;
-			}
-		}
-
-		{
-			Converts localConvertsAnnotation = property.getAnnotation( Converts.class );
-			if ( localConvertsAnnotation != null ) {
-				for ( Convert localConvertAnnotation : localConvertsAnnotation.value() ) {
-					if ( isLocalMatch( localConvertAnnotation, property ) ) {
-						LOG.debugf(
-								"Found matching @Convert annotation as part local @Converts [disableConversion=%s]",
-								localConvertAnnotation.disableConversion()
-						);
-						return localConvertAnnotation.disableConversion()
-								? null
-								: localConvertAnnotation;
-					}
-				}
-			}
-		}
-
-		if ( persistentClassName == null ) {
-			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
-			return null;
-		}
-
-		final XClass owner;
-		try {
-			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
-			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
-		}
-		catch (ClassNotFoundException e) {
-			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
-		}
-
-		return lookForEntityDefinedConvertAnnotation( property, owner );
-	}
-
-	private Convert lookForEntityDefinedConvertAnnotation(XProperty property, XClass owner) {
-		if ( owner == null ) {
-			// we have hit the root of the entity hierarchy
-			return null;
-		}
-
-		LOG.debugf(
-				"Attempting to locate any AttributeConverter defined via @Convert/@Converts on type-hierarchy [%s] to apply to attribute [%s]",
-				owner.getName(),
-				property.getName()
-		);
-
-		{
-			Convert convertAnnotation = owner.getAnnotation( Convert.class );
-			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
-				LOG.debugf(
-						"Found matching @Convert annotation [disableConversion=%s]",
-						convertAnnotation.disableConversion()
-				);
-				return convertAnnotation.disableConversion() ? null : convertAnnotation;
-			}
-		}
-
-		{
-			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
-			if ( convertsAnnotation != null ) {
-				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
-					if ( isMatch( convertAnnotation, property ) ) {
-						LOG.debugf(
-								"Found matching @Convert annotation as part @Converts [disableConversion=%s]",
-								convertAnnotation.disableConversion()
-						);
-						return convertAnnotation.disableConversion() ? null : convertAnnotation;
-					}
-				}
-			}
-		}
-
-		// finally, look on superclass
-		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
-	}
-
-	@SuppressWarnings("unchecked")
-	private boolean isLocalMatch(Convert convertAnnotation, XProperty property) {
-		if ( StringHelper.isEmpty( convertAnnotation.attributeName() ) ) {
-			return isTypeMatch( convertAnnotation.converter(), property );
-		}
-
-		return property.getName().equals( convertAnnotation.attributeName() )
-				&& isTypeMatch( convertAnnotation.converter(), property );
-	}
-
-	@SuppressWarnings("unchecked")
-	private boolean isMatch(Convert convertAnnotation, XProperty property) {
-		return property.getName().equals( convertAnnotation.attributeName() );
-//		return property.getName().equals( convertAnnotation.attributeName() )
-//				&& isTypeMatch( convertAnnotation.converter(), property );
-	}
-
-	private boolean isTypeMatch(Class<? extends AttributeConverter> attributeConverterClass, XProperty property) {
-		return areTypeMatch(
-				extractEntityAttributeType( attributeConverterClass ),
-				mappings.getReflectionManager().toClass( property.getType() )
-		);
-	}
-
-	private Class extractEntityAttributeType(Class<? extends AttributeConverter> attributeConverterClass) {
-		// this is duplicated in SimpleValue...
-		final TypeVariable[] attributeConverterTypeInformation = attributeConverterClass.getTypeParameters();
-		if ( attributeConverterTypeInformation == null || attributeConverterTypeInformation.length < 2 ) {
-			throw new AnnotationException(
-					"AttributeConverter [" + attributeConverterClass.getName()
-							+ "] did not retain parameterized type information"
-			);
-		}
-
-		if ( attributeConverterTypeInformation.length > 2 ) {
-			LOG.debug(
-					"AttributeConverter [" + attributeConverterClass.getName()
-							+ "] specified more than 2 parameterized types"
-			);
-		}
-		final Class entityAttributeJavaType = extractType( attributeConverterTypeInformation[0] );
-		if ( entityAttributeJavaType == null ) {
-			throw new AnnotationException(
-					"Could not determine 'entity attribute' type from given AttributeConverter [" +
-							attributeConverterClass.getName() + "]"
-			);
-		}
-		return entityAttributeJavaType;
-	}
-
-	private Class extractType(TypeVariable typeVariable) {
-		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
-		if ( boundTypes == null || boundTypes.length != 1 ) {
-			return null;
-		}
-
-		return (Class) boundTypes[0];
-	}
-
-	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
-		if ( converterDefinedType == null ) {
-			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
-		}
-		if ( propertyType == null ) {
-			throw new AnnotationException( "Property defined java type cannot be null" );
-		}
-
-		return converterDefinedType.equals( propertyType )
-				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
-	}
-
-	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
-		if ( converterDefinedType.isPrimitive() ) {
-			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
-		}
-		else if ( propertyType.isPrimitive() ) {
-			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
-		}
-		return false;
-	}
-
-	private static Class getWrapperEquivalent(Class primitive) {
-		if ( ! primitive.isPrimitive() ) {
-			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
-		}
-
-		if ( boolean.class.equals( primitive ) ) {
-			return Boolean.class;
-		}
-		else if ( char.class.equals( primitive ) ) {
-			return Character.class;
-		}
-		else if ( byte.class.equals( primitive ) ) {
-			return Byte.class;
-		}
-		else if ( short.class.equals( primitive ) ) {
-			return Short.class;
-		}
-		else if ( int.class.equals( primitive ) ) {
-			return Integer.class;
-		}
-		else if ( long.class.equals( primitive ) ) {
-			return Long.class;
-		}
-		else if ( float.class.equals( primitive ) ) {
-			return Float.class;
-		}
-		else if ( double.class.equals( primitive ) ) {
-			return Double.class;
-		}
-
-		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
-	}
-	
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
 		LOG.debugf( "Starting fillSimpleValue for %s", propertyName );
                 
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
 			LOG.debugf(
 					"Applying JPA AttributeConverter [%s] to [%s:%s]",
 					attributeConverterDefinition,
 					persistentClassName,
 					propertyName
 			);
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
-				Class typeClass = ReflectHelper.classForName( simpleValue.getTypeName() );
+				Class typeClass = StandardClassLoaderDelegateImpl.INSTANCE.classForName( simpleValue.getTypeName() );
 
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
-			catch ( ClassNotFoundException cnfe ) {
-				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), cnfe );
+			catch (ClassLoadingException e) {
+				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), e );
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
index 716cdb0704..528341bd70 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,582 +1,582 @@
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
 
-	private AttributeConverterDefinition jpaAttributeConverterDefinition;
+	private AttributeConverterDefinition attributeConverterDefinition;
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
 
-		if ( jpaAttributeConverterDefinition == null ) {
+		if ( attributeConverterDefinition == null ) {
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
 
-		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
-		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
+		final Class entityAttributeJavaType = attributeConverterDefinition.getEntityAttributeType();
+		final Class databaseColumnJavaType = attributeConverterDefinition.getDatabaseColumnType();
 
 
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
-				jpaAttributeConverterDefinition.getAttributeConverter(),
+				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
-				jpaAttributeConverterDefinition.getAttributeConverter(),
+				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaTypeDescriptor
 		);
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
 
-	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
-		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
+	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition attributeConverterDefinition) {
+		this.attributeConverterDefinition = attributeConverterDefinition;
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
index 824e4b54f2..02d0f10dcb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
@@ -1,400 +1,399 @@
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
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.fail;
 
 import java.io.Serializable;
 import java.sql.Timestamp;
 import java.sql.Types;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converter;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import org.hibernate.IrrelevantEntity;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.StringTypeDescriptor;
 import org.junit.Test;
 
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
 	public void testNonAutoApplyHandling() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( NotAutoAppliedConverter.class, false );
 		cfg.addAnnotatedClass( Tester.class );
 		cfg.buildMappings();
 
 		PersistentClass tester = cfg.getClassMapping( Tester.class.getName() );
 		Property nameProp = tester.getProperty( "name" );
 		SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 		Type type = nameValue.getType();
 		assertNotNull( type );
 		if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 			fail( "AttributeConverter with autoApply=false was auto applied" );
 		}
 	}
 
 	@Test
 	public void testBasicConverterApplication() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( StringClobConverter.class, true );
 		cfg.addAnnotatedClass( Tester.class );
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
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8462")
 	@FailureExpected(jiraKey = "HHH-8462")
 	public void testBasicOrmXmlConverterApplication() {
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Tester.class );
 		cfg.addURL( ConfigHelper.findAsResource( "org/hibernate/test/type/orm.xml") );
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
 	}
 
 	@Test
-	@FailureExpected( jiraKey = "HHH-8449" )
 	public void testBasicConverterDisableApplication() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( StringClobConverter.class, true );
 		cfg.addAnnotatedClass( Tester2.class );
 		cfg.buildMappings();
 
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
 
 	@Converter(autoApply = false)
 	public static class NotAutoAppliedConverter implements AttributeConverter<String,String> {
 		@Override
 		public String convertToDatabaseColumn(String attribute) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
 		}
 
 		@Override
 		public String convertToEntityAttribute(String dbData) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
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
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleEmbeddableOverriddenConverterTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleEmbeddableOverriddenConverterTest.java
new file mode 100644
index 0000000000..ccd331ba14
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleEmbeddableOverriddenConverterTest.java
@@ -0,0 +1,120 @@
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
+import javax.persistence.Convert;
+import javax.persistence.Embeddable;
+import javax.persistence.Embedded;
+import javax.persistence.Entity;
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.Id;
+import javax.persistence.MappedSuperclass;
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
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.StringType;
+import org.hibernate.type.Type;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+
+/**
+ * Tests MappedSuperclass/Entity overriding of Convert definitions
+ *
+ * @author Steve Ebersole
+ */
+public class SimpleEmbeddableOverriddenConverterTest extends BaseUnitTestCase {
+	/**
+	 * Test outcome of annotations exclusively.
+	 */
+	@Test
+	public void testSimpleConvertOverrides() {
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				return Arrays.asList( Person.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+//		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+
+		final SessionFactoryImplementor sfi = emf.unwrap( SessionFactoryImplementor.class );
+		try {
+			final EntityPersister ep = sfi.getEntityPersister( Person.class.getName() );
+
+			CompositeType homeAddressType = assertTyping( CompositeType.class, ep.getPropertyType( "homeAddress" ) );
+			Type homeAddressCityType = findCompositeAttributeType( homeAddressType, "city" );
+			assertTyping( StringType.class, homeAddressCityType );
+		}
+		finally {
+			emf.close();
+		}
+	}
+
+	public Type findCompositeAttributeType(CompositeType compositeType, String attributeName) {
+		int pos = 0;
+		for ( String name : compositeType.getPropertyNames() ) {
+			if ( name.equals( attributeName ) ) {
+				break;
+			}
+			pos++;
+		}
+
+		if ( pos >= compositeType.getPropertyNames().length ) {
+			throw new IllegalStateException( "Could not locate attribute index for [" + attributeName + "] in composite" );
+		}
+
+		return compositeType.getSubtypes()[pos];
+	}
+
+	@Embeddable
+	public static class Address {
+		public String street;
+		@Convert(converter = SillyStringConverter.class)
+		public String city;
+	}
+
+	@Entity( name="Person" )
+	public static class Person {
+		@Id
+		public Integer id;
+		@Embedded
+		@Convert( attributeName = "city", disableConversion = true )
+		public Address homeAddress;
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleOverriddenConverterTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleOverriddenConverterTest.java
index a7c14b332b..134088ca3e 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleOverriddenConverterTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/convert/SimpleOverriddenConverterTest.java
@@ -1,101 +1,99 @@
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
 package org.hibernate.jpa.test.convert;
 
 import javax.persistence.Convert;
 import javax.persistence.Entity;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Id;
 import javax.persistence.MappedSuperclass;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.jpa.boot.spi.Bootstrap;
 import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.StringType;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
-import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 
 /**
  * Tests MappedSuperclass/Entity overriding of Convert definitions
  *
  * @author Steve Ebersole
  */
 public class SimpleOverriddenConverterTest extends BaseUnitTestCase {
 	/**
 	 * Test outcome of annotations exclusively.
 	 */
 	@Test
-	@FailureExpected( jiraKey = "HHH-8449" )
 	public void testSimpleConvertOverrides() {
 		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
 			@Override
 			public List<String> getManagedClassNames() {
 				return Arrays.asList( Super.class.getName(), Sub.class.getName() );
 			}
 		};
 
 		final Map settings = new HashMap();
 		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 
 		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
 
 		final SessionFactoryImplementor sfi = emf.unwrap( SessionFactoryImplementor.class );
 		try {
 			final EntityPersister ep = sfi.getEntityPersister( Sub.class.getName() );
 
 			Type type = ep.getPropertyType( "it" );
 			assertTyping( StringType.class, type );
 		}
 		finally {
 			emf.close();
 		}
 	}
 
 	@MappedSuperclass
 	public static class Super {
 		@Id
 		public Integer id;
 		@Convert(converter = SillyStringConverter.class)
 		public String it;
 	}
 
 	@Entity(name = "Sub")
 	@Convert( attributeName = "it", disableConversion = true )
 	public static class Sub extends Super {
 
 	}
 
 }
diff --git a/libraries.gradle b/libraries.gradle
index 3d474567c9..9464f2287d 100644
--- a/libraries.gradle
+++ b/libraries.gradle
@@ -1,122 +1,122 @@
 
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
 
 // build a map of the dependency artifacts to use.  Allows centralized definition of the version of artifacts to
 // use.  In that respect it serves a role similar to <dependencyManagement> in Maven
 ext {
 
     slf4jVersion = '1.6.1'
     junitVersion = '4.10'
     h2Version = '1.2.145'
     bytemanVersion = '2.1.2'
     infinispanVersion = '5.3.0.Final'
     jnpVersion = '5.0.6.CR1'
 
     libraries = [
             // Ant
             ant:            'org.apache.ant:ant:1.8.2',
 
             // Antlr
             antlr:          'antlr:antlr:2.7.7',
 
             // Annotations
             commons_annotations:
-                            'org.hibernate.common:hibernate-commons-annotations:4.0.2.Final@jar',
+                            'org.hibernate.common:hibernate-commons-annotations:4.0.3.Final@jar',
             jandex:         'org.jboss:jandex:1.1.0.Alpha1',
             classmate:      'com.fasterxml:classmate:0.8.0',
 
             // Dom4J
             dom4j:          'dom4j:dom4j:1.6.1@jar',
 
             // Javassist
             javassist:      'org.javassist:javassist:3.18.1-GA',
 
             // javax
             jpa:            'org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.0.Final',
             jta:            'org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:1.0.0.Alpha1',
             validation:     'javax.validation:validation-api:1.1.0.Final',
             jacc:           'org.jboss.spec.javax.security.jacc:jboss-jacc-api_1.4_spec:1.0.2.Final',
 
             // logging
             logging:        'org.jboss.logging:jboss-logging:3.1.0.GA',
             logging_processor:  'org.jboss.logging:jboss-logging-processor:1.0.0.Final',
 
             // jaxb task
             jaxb:           'com.sun.xml.bind:jaxb-xjc:2.1.6',
             jaxb2_basics:   'org.jvnet.jaxb2_commons:jaxb2-basics:0.6.0',
             jaxb2_ant:      'org.jvnet.jaxb2_commons:jaxb2-basics-ant:0.6.0',
 
             // Animal Sniffer Ant Task and Java 1.6 API signature file
             // not using 1.9 for the time being due to MANIMALSNIFFER-34
             animal_sniffer: 'org.codehaus.mojo:animal-sniffer-ant-tasks:1.8',
             java16_signature: 'org.codehaus.mojo.signature:java16:1.0@signature',
 
             //Maven plugin framework
             maven_plugin: 'org.apache.maven:maven-plugin-api:3.0.5',
             maven_plugin_tools: 'org.apache.maven.plugin-tools:maven-plugin-annotations:3.2',
 
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~ testing
 
             // logging for testing
             slf4j_api:      "org.slf4j:slf4j-api:${slf4jVersion}",
             slf4j_log4j12:  "org.slf4j:slf4j-log4j12:${slf4jVersion}",
             jcl_slf4j:      "org.slf4j:jcl-over-slf4j:${slf4jVersion}",
             jcl_api:        'commons-logging:commons-logging-api:99.0-does-not-exist',
             jcl:            'commons-logging:commons-logging:99.0-does-not-exist',
 
 
             junit:           "junit:junit:${junitVersion}",
             byteman:         "org.jboss.byteman:byteman:${bytemanVersion}",
             byteman_install: "org.jboss.byteman:byteman-install:${bytemanVersion}",
             byteman_bmunit:  "org.jboss.byteman:byteman-bmunit:${bytemanVersion}",
             jpa_modelgen:    'org.hibernate:hibernate-jpamodelgen:1.1.1.Final',
             shrinkwrap_api:  'org.jboss.shrinkwrap:shrinkwrap-api:1.0.0-beta-6',
             shrinkwrap:      'org.jboss.shrinkwrap:shrinkwrap-impl-base:1.0.0-beta-6',
             validator:       'org.hibernate:hibernate-validator:5.0.1.Final',
             h2:              "com.h2database:h2:${h2Version}",
             derby:           "org.apache.derby:derby:10.9.1.0",
             jboss_jta:       "org.jboss.jbossts:jbossjta:4.16.4.Final",
             xapool:          "com.experlog:xapool:1.5.0",
             mockito:         'org.mockito:mockito-core:1.9.0',
 
             // required by Hibernate Validator at test runtime
             unified_el:      "org.glassfish:javax.el:3.0-b07",
 
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~~  infinsipan
             infinispan:      "org.infinispan:infinispan-core:${infinispanVersion}",
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~~  infinispan test
             infinispan_test: "org.infinispan:infinispan-core:${infinispanVersion}:tests@jar",
             rhq:             "org.rhq.helpers:rhq-pluginAnnotations:3.0.4",
             jboss_common_core: "org.jboss:jboss-common-core:2.2.16.GA@jar",
             jnp_client:      "org.jboss.naming:jnp-client:${jnpVersion}",
             jnp_server:      "org.jboss.naming:jnpserver:${jnpVersion}",
 
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ c3p0
             c3p0:            "com.mchange:c3p0:0.9.2.1",
             ehcache:         "net.sf.ehcache:ehcache-core:2.4.3",
             proxool:         "proxool:proxool:0.8.3"
 
         ]
 }
