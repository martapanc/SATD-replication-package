diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index 4e2bdd9f6a..f0a9d66014 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
@@ -1,107 +1,107 @@
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
 package org.hibernate.cache.internal;
 
 import java.util.Comparator;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.type.VersionType;
 
 /**
  * {@inheritDoc}
  *
  * @author Steve Ebersole
  */
 public class CacheDataDescriptionImpl implements CacheDataDescription {
 	private final boolean mutable;
 	private final boolean versioned;
 	private final Comparator versionComparator;
 
 	public CacheDataDescriptionImpl(boolean mutable, boolean versioned, Comparator versionComparator) {
 		this.mutable = mutable;
 		this.versioned = versioned;
 		this.versionComparator = versionComparator;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public Comparator getVersionComparator() {
 		return versionComparator;
 	}
 
 	public static CacheDataDescriptionImpl decode(PersistentClass model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
 				model.isVersioned() ? ( ( VersionType ) model.getVersion().getType() ).getComparator() : null
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(EntityBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
 				getVersionComparator( model )
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(Collection model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getOwner().isVersioned(),
 				model.getOwner().isVersioned() ? ( ( VersionType ) model.getOwner().getVersion().getType() ).getComparator() : null
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getEntityBinding().isVersioned(),
 				getVersionComparator( model.getEntityBinding() )
 		);
 	}
 
 	private static Comparator getVersionComparator(EntityBinding model ) {
 		Comparator versionComparator = null;
 		if ( model.isVersioned() ) {
 			versionComparator = (
 					( VersionType ) model
 								.getVersioningValueBinding()
 								.getHibernateTypeDescriptor()
-								.getExplicitType()
+								.getResolvedTypeMapping()
 			).getComparator();
 		}
 		return versionComparator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index c13791cfca..88967b2b78 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,372 +1,372 @@
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
 
 import javax.persistence.Enumerated;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import java.io.Serializable;
 import java.sql.Types;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
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
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 
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
 
 	public void setType(XProperty property, XClass returnedClass) {
 		if ( returnedClass == null ) {
 			return;
 		} //we cannot guess anything
 		XClass returnedClassOrElement = returnedClass;
 		boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
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
 				//typeParameters = new Properties();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 		}
 		//implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 				typeParameters = new Properties();
 				typeParameters.setProperty( EnumType.ENUM, returnedClassOrElement.getName() );
 				String schema = columns[0].getTable().getSchema();
 				schema = schema == null ? "" : schema;
 				String catalog = columns[0].getTable().getCatalog();
 				catalog = catalog == null ? "" : catalog;
 				typeParameters.setProperty( EnumType.SCHEMA, schema );
 				typeParameters.setProperty( EnumType.CATALOG, catalog );
 				typeParameters.setProperty( EnumType.TABLE, columns[0].getTable().getName() );
 				typeParameters.setProperty( EnumType.COLUMN, columns[0].getName() );
 				javax.persistence.EnumType enumType = getEnumType( property );
 				if ( enumType != null ) {
 					if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.INTEGER ) );
 					}
 					else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.VARCHAR ) );
 					}
 					else {
 						throw new AssertionFailure( "Unknown EnumType: " + enumType );
 					}
 				}
 			}
 		}
 		explicitType = type;
 		this.typeParameters = typeParameters;
 		Type annType = property.getAnnotation( Type.class );
 		setExplicitType( annType );
 	}
 
 	private javax.persistence.EnumType getEnumType(XProperty property) {
 		javax.persistence.EnumType enumType = null;
 		if ( key ) {
 			MapKeyEnumerated enumAnn = property.getAnnotation( MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = property.getAnnotation( Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
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
 
-	//FIXME raise an assertion failure  if setExplicitType(String) and setExplicitType(Type) are use at the same time
+	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
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
         LOG.debugf("building SimpleValue for %s", propertyName);
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
 
         LOG.debugf("Setting SimpleValue typeName for %s", propertyName);
 
 		String type = BinderHelper.isEmptyAnnotationValue( explicitType ) ? returnedClassName : explicitType;
 		org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
 		if ( typeDef != null ) {
 			type = typeDef.getTypeClass();
 			simpleValue.setTypeParameters( typeDef.getParameters() );
 		}
 		if ( typeParameters != null && typeParameters.size() != 0 ) {
 			//explicit type params takes precedence over type def params
 			simpleValue.setTypeParameters( typeParameters );
 		}
 		simpleValue.setTypeName( type );
 		if ( persistentClassName != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
index a42b3fd875..dce78cee7b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
@@ -1,275 +1,275 @@
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
 import org.hibernate.metamodel.source.MetaAttributeContext;
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
 
 /**
  * Basic support for {@link AttributeBinding} implementors
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeBinding implements AttributeBinding {
 	private final EntityBinding entityBinding;
 
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 	private final Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
 	private Attribute attribute;
 	private Value value;
 
 	private boolean isLazy;
 	private String propertyAccessorName;
 	private boolean isAlternateUniqueKey;
 	private Set<CascadeType> cascadeTypes;
 	private boolean optimisticLockable;
 
 	private MetaAttributeContext metaAttributeContext;
 
 	protected AbstractAttributeBinding(EntityBinding entityBinding) {
 		this.entityBinding = entityBinding;
 	}
 
 	protected void initialize(AttributeBindingState state) {
-		hibernateTypeDescriptor.setTypeName( state.getTypeName() );
+		hibernateTypeDescriptor.setExplicitTypeName( state.getTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( state.getTypeParameters() );
 		isLazy = state.isLazy();
 		propertyAccessorName = state.getPropertyAccessorName();
 		isAlternateUniqueKey = state.isAlternateUniqueKey();
 		cascadeTypes = state.getCascadeTypes();
 		optimisticLockable = state.isOptimisticLockable();
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
 
 	public void setValue(Value value) {
 		this.value = value;
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
 
 	public void setLazy(boolean isLazy) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
index c1fa1c6150..1ff1902f07 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
@@ -1,69 +1,69 @@
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
 
 import org.hibernate.metamodel.relational.Value;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class CollectionElement {
 
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 	private final PluralAttributeBinding collectionBinding;
 	private final CollectionElementType collectionElementType;
 	private String nodeName;
 
 	private Value elementValue;
 
 	CollectionElement(PluralAttributeBinding collectionBinding, CollectionElementType collectionElementType) {
 		this.collectionBinding = collectionBinding;
 		this.collectionElementType = collectionElementType;
 	}
 
 	public final CollectionElementType getCollectionElementType() {
 		return collectionElementType;
 	}
 
 	/* package-protected */
 	void setTypeName(String typeName) {
-		hibernateTypeDescriptor.setTypeName( typeName );
+		hibernateTypeDescriptor.setExplicitTypeName( typeName );
 	}
 
 	/* package-protected */
 	void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public boolean isOneToMany() {
 		return collectionElementType.isOneToMany();
 	}
 
 	public boolean isManyToMany() {
 		return collectionElementType.isManyToMany();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
index 7d75853f44..67898e6826 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
@@ -1,106 +1,105 @@
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
 
 import java.util.Properties;
-import javax.persistence.GenerationType;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Binds the entity identifier.
  *
  * @author Steve Ebersole
  */
 public class EntityIdentifier {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			EntityIdentifier.class.getName()
 	);
 
 	private final EntityBinding entityBinding;
 	private SimpleAttributeBinding attributeBinding;
 	private IdentifierGenerator identifierGenerator;
 	private IdGenerator idGenerator;
 	private boolean isIdentifierMapper = false;
 	// todo : mappers, etc
 
 	/**
 	 * Create an identifier
 	 *
 	 * @param entityBinding the entity binding for which this instance is the id
 	 */
 	public EntityIdentifier(EntityBinding entityBinding) {
 		this.entityBinding = entityBinding;
 	}
 
 	public SimpleAttributeBinding getValueBinding() {
 		return attributeBinding;
 	}
 
 	public void setValueBinding(SimpleAttributeBinding attributeBinding) {
 		if ( this.attributeBinding != null ) {
 			// todo : error?  or just log?  For now throw exception and see what happens. Easier to see whether this
 			// method gets called multiple times
 			LOG.entityIdentifierValueBindingExists( entityBinding.getEntity().getName() );
 		}
 		this.attributeBinding = attributeBinding;
 	}
 
 	public void setIdGenerator(IdGenerator idGenerator) {
 		this.idGenerator = idGenerator;
 	}
 
 	public boolean isEmbedded() {
 		return attributeBinding.getValuesSpan()>1;
 	}
 
 	public boolean isIdentifierMapper() {
 		return isIdentifierMapper;
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory factory) {
 		if ( identifierGenerator == null ) {
 			Properties props = new Properties();
 			if ( idGenerator != null ) {
 				props.putAll( idGenerator.getParameters() );
 			}
 			identifierGenerator = factory.createIdentifierGenerator(
 					idGenerator.getStrategy(),
-					getValueBinding().getHibernateTypeDescriptor().getExplicitType(),
+					getValueBinding().getHibernateTypeDescriptor().getResolvedTypeMapping(),
 					props
 			);
 		}
 		return identifierGenerator;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/HibernateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/HibernateTypeDescriptor.java
index c4c1d5c13c..c4699bc2fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/HibernateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/HibernateTypeDescriptor.java
@@ -1,63 +1,82 @@
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
 
 import java.util.Map;
 
 import org.hibernate.type.Type;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class HibernateTypeDescriptor {
-	private String typeName;
-	private Type explicitType;
+	private String explicitTypeName;
+	private String javaTypeName;
+	private boolean isToOne;
 	private Map<String, String> typeParameters;
 
-	public String getTypeName() {
-		return typeName;
+	private Type resolvedTypeMapping;
+
+	public String getExplicitTypeName() {
+		return explicitTypeName;
+	}
+
+	public void setExplicitTypeName(String explicitTypeName) {
+		this.explicitTypeName = explicitTypeName;
+	}
+
+	public String getJavaTypeName() {
+		return javaTypeName;
 	}
 
-	public void setTypeName(String typeName) {
-		this.typeName = typeName;
+	public void setJavaTypeName(String javaTypeName) {
+		this.javaTypeName = javaTypeName;
 	}
 
-	public Type getExplicitType() {
-		return explicitType;
+	public boolean isToOne() {
+		return isToOne;
 	}
 
-	public void setExplicitType(Type explicitType) {
-		this.explicitType = explicitType;
+	public void setToOne(boolean toOne) {
+		isToOne = toOne;
 	}
 
 	public Map<String, String> getTypeParameters() {
 		return typeParameters;
 	}
 
-	void setTypeParameters(Map<String, String> typeParameters) {
+	public void setTypeParameters(Map<String, String> typeParameters) {
 		this.typeParameters = typeParameters;
 	}
+
+	public Type getResolvedTypeMapping() {
+		return resolvedTypeMapping;
+	}
+
+	public void setResolvedTypeMapping(Type resolvedTypeMapping) {
+		this.resolvedTypeMapping = resolvedTypeMapping;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java
index 5bf17a2b6f..6907f7db3e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java
@@ -1,865 +1,937 @@
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
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Stack;
 
 import org.hibernate.EntityMode;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.JoinElementSource;
 import org.hibernate.metamodel.binding.BagBinding;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.AbstractAttributeContainer;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.SingularAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLDynamicComponentElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLIdbagElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLListElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMapElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertiesElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSetElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Steve Ebersole
  */
 public class BindingCreator {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
 	private HbmBindingContext currentBindingContext;
 
 	public BindingCreator(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
 	// todo : currently this does not allow inheritance across hbm/annotations.  Do we need to?
 
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getEntitySourceInformation(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy, rootEntityBinding );
 		}
 	}
 
 	private void processHierarchySubEntities(SubEntityContainer subEntityContainer, EntityBinding superEntityBinding) {
 		for ( EntityHierarchySubEntity subEntity : subEntityContainer.subEntityDescriptors() ) {
 			EntityBinding entityBinding = createEntityBinding( subEntity.getEntitySourceInformation(), superEntityBinding );
 			processHierarchySubEntities( subEntity, entityBinding );
 		}
 	}
 
 	private EntityBinding createEntityBinding(EntitySourceInformation entitySourceInfo, EntityBinding superEntityBinding) {
 		if ( processedEntityNames.contains( entitySourceInfo.getMappedEntityName() ) ) {
 			return metadata.getEntityBinding( entitySourceInfo.getMappedEntityName() );
 		}
 
 		currentBindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
 		try {
 			final EntityBinding entityBinding = doCreateEntityBinding( entitySourceInfo, superEntityBinding );
 
 			metadata.addEntity( entityBinding );
 			processedEntityNames.add( entityBinding.getEntity().getName() );
 			return entityBinding;
 		}
 		finally {
 			currentBindingContext = null;
 		}
 	}
 
 	private EntityBinding doCreateEntityBinding(EntitySourceInformation entitySourceInfo, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = createBasicEntityBinding( entitySourceInfo, superEntityBinding );
 
-		bindPrimaryTable( entitySourceInfo, entityBinding );
 		bindAttributes( entitySourceInfo, entityBinding );
 		bindSecondaryTables( entitySourceInfo, entityBinding );
 		bindTableUniqueConstraints( entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(
 			EntitySourceInformation entitySourceInfo,
 			EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( entitySourceInfo );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
 				return makeDiscriminatedSubclassBinding( entitySourceInfo, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
 				return makeJoinedSubclassBinding( entitySourceInfo, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
 				return makeUnionedSubclassBinding( entitySourceInfo, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new RuntimeException( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(EntitySourceInformation entitySourceInfo) {
 		final EntityBinding entityBinding = new EntityBinding();
 		// todo : this is actually not correct
 		// 		the problem is that we need to know whether we have mapped subclasses which happens later
 		//		one option would be to simply reset the InheritanceType at that time.
 		entityBinding.setInheritanceType( currentInheritanceType );
 		entityBinding.setRoot( true );
 
 		final XMLHibernateMapping.XMLClass xmlClass = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
 		final String entityName = entitySourceInfo.getMappedEntityName();
 		final String verbatimClassName = xmlClass.getName();
 
 		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
 		entityBinding.setEntityMode( entityMode );
 
 		final String className;
 		if ( entityMode == EntityMode.POJO ) {
 			className = entitySourceInfo.getSourceMappingDocument()
 					.getMappingLocalBindingContext()
 					.qualifyClassName( verbatimClassName );
 		}
 		else {
 			className = null;
 		}
 
 		Entity entity = new Entity(
 				entityName,
 				className,
 				entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 		performBasicEntityBind( entityBinding, entitySourceInfo );
+		bindIdentifier( entityBinding, entitySourceInfo );
 
 		entityBinding.setMutable( xmlClass.isMutable() );
 		entityBinding.setExplicitPolymorphism( "explicit".equals( xmlClass.getPolymorphism() ) );
 		entityBinding.setWhereFilter( xmlClass.getWhere() );
 		entityBinding.setRowId( xmlClass.getRowid() );
 		entityBinding.setOptimisticLockStyle( interpretOptimisticLockStyle( entitySourceInfo ) );
 		entityBinding.setCaching( interpretCaching( entitySourceInfo ) );
 
 		return entityBinding;
 	}
 
 	private OptimisticLockStyle interpretOptimisticLockStyle(EntitySourceInformation entitySourceInfo) {
 		final String optimisticLockModeString = Helper.getStringValue(
 				( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getOptimisticLock(),
 				"version"
 		);
 		try {
 			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
 		}
 		catch (Exception e) {
 			throw new MappingException(
 					"Unknown optimistic-lock value : " + optimisticLockModeString,
 					entitySourceInfo.getSourceMappingDocument().getOrigin()
 			);
 		}
 	}
 
 	private static Caching interpretCaching(EntitySourceInformation entitySourceInfo) {
 		final XMLCacheElement cache = ( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : entitySourceInfo.getMappedEntityName();
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	private EntityBinding makeDiscriminatedSubclassBinding(
 			EntitySourceInformation entitySourceInfo,
 			EntityBinding superEntityBinding) {
 		// temporary!!!
 
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.SINGLE_TABLE );
 		bindSuperType( entityBinding, superEntityBinding );
 
 		final String verbatimClassName = entitySourceInfo.getEntityElement().getName();
 
 		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
 		entityBinding.setEntityMode( entityMode );
 
 		final String className;
 		if ( entityMode == EntityMode.POJO ) {
 			className = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().qualifyClassName( verbatimClassName );
 		}
 		else {
 			className = null;
 		}
 
 		final Entity entity = new Entity(
 				entitySourceInfo.getMappedEntityName(),
 				className,
 				entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 
 		performBasicEntityBind( entityBinding, entitySourceInfo );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeJoinedSubclassBinding(
 			EntitySourceInformation entitySourceInfo,
 			EntityBinding superEntityBinding) {
 		// temporary!!!
 
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.JOINED );
 		bindSuperType( entityBinding, superEntityBinding );
 
 		final XMLJoinedSubclassElement joinedEntityElement = (XMLJoinedSubclassElement) entitySourceInfo.getEntityElement();
 		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
 
 		final String entityName = bindingContext.determineEntityName( joinedEntityElement );
 		final String verbatimClassName = joinedEntityElement.getName();
 
 		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
 		entityBinding.setEntityMode( entityMode );
 
 		final String className;
 		if ( entityMode == EntityMode.POJO ) {
 			className = bindingContext.qualifyClassName( verbatimClassName );
 		}
 		else {
 			className = null;
 		}
 
 		final Entity entity = new Entity( entityName, className, bindingContext.makeClassReference( className ), null );
 		entityBinding.setEntity( entity );
 
 		performBasicEntityBind( entityBinding, entitySourceInfo );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeUnionedSubclassBinding(
 			EntitySourceInformation entitySourceInfo,
 			EntityBinding superEntityBinding) {
 		// temporary!!!
 
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.TABLE_PER_CLASS );
 		bindSuperType( entityBinding, superEntityBinding );
 
 		final XMLUnionSubclassElement unionEntityElement = (XMLUnionSubclassElement) entitySourceInfo.getEntityElement();
 		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
 
 		final String entityName = bindingContext.determineEntityName( unionEntityElement );
 		final String verbatimClassName = unionEntityElement.getName();
 
 		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
 		entityBinding.setEntityMode( entityMode );
 
 		final String className;
 		if ( entityMode == EntityMode.POJO ) {
 			className = bindingContext.qualifyClassName( verbatimClassName );
 		}
 		else {
 			className = null;
 		}
 
 		final Entity entity = new Entity( entityName, className, bindingContext.makeClassReference( className ), null );
 		entityBinding.setEntity( entity );
 
 		performBasicEntityBind( entityBinding, entitySourceInfo );
 
 		return entityBinding;
 	}
 
 	private void bindSuperType(EntityBinding entityBinding, EntityBinding superEntityBinding) {
 //		entityBinding.setSuperEntityBinding( superEntityBinding );
 //		// not sure what to do with the domain model super type...
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private void performBasicEntityBind(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
+		bindPrimaryTable( entitySourceInfo, entityBinding );
+
 		entityBinding.setJpaEntityName( null );
 
 		final EntityElement entityElement = entitySourceInfo.getEntityElement();
 		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
 
 		final String proxy = entityElement.getProxy();
 		final boolean isLazy = entityElement.isLazy() == null
 				? true
 				: entityElement.isLazy();
 		if ( entityBinding.getEntityMode() == EntityMode.POJO ) {
 			if ( proxy != null ) {
 				entityBinding.setProxyInterfaceType(
 						bindingContext.makeClassReference(
 								bindingContext.qualifyClassName( proxy )
 						)
 				);
 				entityBinding.setLazy( true );
 			}
 			else if ( isLazy ) {
 				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( new Value( Map.class ) );
 			entityBinding.setLazy( isLazy );
 		}
 
 		final String customTuplizerClassName = extractCustomTuplizerClassName(
 				entityElement,
 				entityBinding.getEntityMode()
 		);
 		if ( customTuplizerClassName != null ) {
 			entityBinding.setCustomEntityTuplizerClass( bindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName ) );
 		}
 
 		if ( entityElement.getPersister() != null ) {
 			entityBinding.setCustomEntityPersisterClass( bindingContext.<EntityPersister>locateClassByName( entityElement.getPersister() ) );
 		}
 
 		entityBinding.setMetaAttributeContext(
 				Helper.extractMetaAttributeContext(
 						entityElement.getMeta(), true, bindingContext.getMetaAttributeContext()
 				)
 		);
 
 		entityBinding.setDynamicUpdate( entityElement.isDynamicUpdate() );
 		entityBinding.setDynamicInsert( entityElement.isDynamicInsert() );
 		entityBinding.setBatchSize( Helper.getIntValue( entityElement.getBatchSize(), 0 ) );
 		entityBinding.setSelectBeforeUpdate( entityElement.isSelectBeforeUpdate() );
 		entityBinding.setAbstract( entityElement.isAbstract() );
 
 		if ( entityElement.getLoader() != null ) {
 			entityBinding.setCustomLoaderName( entityElement.getLoader().getQueryRef() );
 		}
 
 		final XMLSqlInsertElement sqlInsert = entityElement.getSqlInsert();
 		if ( sqlInsert != null ) {
 			entityBinding.setCustomInsert( Helper.buildCustomSql( sqlInsert ) );
 		}
 
 		final XMLSqlDeleteElement sqlDelete = entityElement.getSqlDelete();
 		if ( sqlDelete != null ) {
 			entityBinding.setCustomDelete( Helper.buildCustomSql( sqlDelete ) );
 		}
 
 		final XMLSqlUpdateElement sqlUpdate = entityElement.getSqlUpdate();
 		if ( sqlUpdate != null ) {
 			entityBinding.setCustomUpdate( Helper.buildCustomSql( sqlUpdate ) );
 		}
 
 		if ( entityElement.getSynchronize() != null ) {
 			for ( XMLSynchronizeElement synchronize : entityElement.getSynchronize() ) {
 				entityBinding.addSynchronizedTable( synchronize.getTable() );
 			}
 		}
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
 
 	private void bindPrimaryTable(EntitySourceInformation entitySourceInformation, EntityBinding entityBinding) {
 		final EntityElement entityElement = entitySourceInformation.getEntityElement();
 		final HbmBindingContext bindingContext = entitySourceInformation.getSourceMappingDocument().getMappingLocalBindingContext();
 
 		if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
 			// todo : need to look it up from root entity, or have persister manage it
 		}
 		else {
 			// todo : add mixin interface
 			final String explicitTableName;
 			final String explicitSchemaName;
 			final String explicitCatalogName;
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( entityElement ) ) {
 				explicitTableName = ( (XMLHibernateMapping.XMLClass) entityElement ).getTable();
 				explicitSchemaName = ( (XMLHibernateMapping.XMLClass) entityElement ).getSchema();
 				explicitCatalogName = ( (XMLHibernateMapping.XMLClass) entityElement ).getCatalog();
 			}
 			else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
 				explicitTableName = ( (XMLJoinedSubclassElement) entityElement ).getTable();
 				explicitSchemaName = ( (XMLJoinedSubclassElement) entityElement ).getSchema();
 				explicitCatalogName = ( (XMLJoinedSubclassElement) entityElement ).getCatalog();
 			}
 			else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
 				explicitTableName = ( (XMLUnionSubclassElement) entityElement ).getTable();
 				explicitSchemaName = ( (XMLUnionSubclassElement) entityElement ).getSchema();
 				explicitCatalogName = ( (XMLUnionSubclassElement) entityElement ).getCatalog();
 			}
 			else {
 				// throw up
 				explicitTableName = null;
 				explicitSchemaName = null;
 				explicitCatalogName = null;
 			}
 			final NamingStrategy namingStrategy = bindingContext.getMetadataImplementor()
 					.getOptions()
 					.getNamingStrategy();
 			final String tableName = explicitTableName != null
 					? namingStrategy.tableName( explicitTableName )
 					: namingStrategy.tableName( namingStrategy.classToTableName( entityBinding.getEntity().getName() ) );
 
 			final String schemaName = explicitSchemaName == null
 					? bindingContext.getMappingDefaults().getSchemaName()
 					: explicitSchemaName;
 			final String catalogName = explicitCatalogName == null
 					? bindingContext.getMappingDefaults().getCatalogName()
 					: explicitCatalogName;
 
 			final Schema schema = metadata.getDatabase().getSchema( new Schema.Name( schemaName, catalogName ) );
 			entityBinding.setBaseTable( schema.locateOrCreateTable( Identifier.toIdentifier( tableName ) ) );
 		}
 	}
 
 
 	private Stack<TableSpecification> attributeColumnTableStack = new Stack<TableSpecification>();
 
+	private void bindIdentifier(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
+		final XMLHibernateMapping.XMLClass rootClassElement = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
+		if ( rootClassElement.getId() != null ) {
+			bindSimpleIdentifierAttribute( entityBinding, entitySourceInfo );
+		}
+		else if ( rootClassElement.getCompositeId() != null ) {
+			bindCompositeIdentifierAttribute( entityBinding, entitySourceInfo );
+		}
+	}
+
+	private void bindSimpleIdentifierAttribute(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
+		final XMLHibernateMapping.XMLClass.XMLId idElement = ( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getId();
+		final String idAttributeName = idElement.getName() == null
+				? "id"
+				: idElement.getName();
+
+		final SimpleAttributeBinding idAttributeBinding = doBasicSimpleAttributeBindingCreation(
+				idAttributeName,
+				idElement,
+				entityBinding
+		);
+
+		idAttributeBinding.setInsertable( false );
+		idAttributeBinding.setUpdatable( false );
+		idAttributeBinding.setGeneration( PropertyGeneration.INSERT );
+		idAttributeBinding.setLazy( false );
+		idAttributeBinding.setIncludedInOptimisticLocking( false );
+
+		final org.hibernate.metamodel.relational.Value relationalValue = makeValue(
+				new RelationValueMetadataSource() {
+					@Override
+					public String getColumnAttribute() {
+						return idElement.getColumnAttribute();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return null;
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return idElement.getColumn();
+					}
+				},
+				idAttributeBinding
+		);
+
+		idAttributeBinding.setValue( relationalValue );
+		if ( SimpleValue.class.isInstance( relationalValue ) ) {
+			if ( !Column.class.isInstance( relationalValue ) ) {
+				// this should never ever happen..
+				throw new MappingException( "Simple ID is not a column.", currentBindingContext.getOrigin() );
+			}
+			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
+		}
+		else {
+			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
+				if ( Column.class.isInstance( subValue ) ) {
+					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
+				}
+			}
+		}
+	}
+
+	private void bindCompositeIdentifierAttribute(
+			EntityBinding entityBinding,
+			EntitySourceInformation entitySourceInfo) {
+		//To change body of created methods use File | Settings | File Templates.
+	}
+
 	private void bindAttributes(final EntitySourceInformation entitySourceInformation, EntityBinding entityBinding) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		attributeColumnTableStack.push( entityBinding.getBaseTable() );
 		try {
 			bindAttributes(
 					new AttributeMetadataContainer() {
 						@Override
 						public List<Object> getAttributeElements() {
 							return entitySourceInformation.getEntityElement().getPropertyOrManyToOneOrOneToOne();
 						}
 					},
 					entityBinding
 			);
 		}
 		finally {
 			attributeColumnTableStack.pop();
 		}
 
 	}
 
 	private void bindAttributes(AttributeMetadataContainer attributeMetadataContainer, EntityBinding entityBinding) {
 		for ( Object attribute : attributeMetadataContainer.getAttributeElements() ) {
 
 			if ( XMLPropertyElement.class.isInstance( attribute ) ) {
 				XMLPropertyElement property = XMLPropertyElement.class.cast( attribute );
 				bindProperty( property, entityBinding );
 			}
 			else if ( XMLManyToOneElement.class.isInstance( attribute ) ) {
 				XMLManyToOneElement manyToOne = XMLManyToOneElement.class.cast( attribute );
 				makeManyToOneAttributeBinding( manyToOne, entityBinding );
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attribute ) ) {
 // todo : implement
 // value = new OneToOne( mappings, table, persistentClass );
 // bindOneToOne( subElement, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( XMLBagElement.class.isInstance( attribute ) ) {
 				XMLBagElement collection = XMLBagElement.class.cast( attribute );
 				BagBinding collectionBinding = makeBagAttributeBinding( collection, entityBinding );
 				metadata.addCollection( collectionBinding );
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
 			else if ( XMLAnyElement.class.isInstance( attribute ) ) {
 // todo : implement
 // value = new Any( mappings, table );
 // bindAny( subElement, (Any) value, nullable, mappings );
 			}
 			else if ( XMLComponentElement.class.isInstance( attribute )
 			|| XMLDynamicComponentElement.class.isInstance( attribute )
 			|| XMLPropertiesElement.class.isInstance( attribute ) ) {
 // todo : implement
 // String subpath = StringHelper.qualify( entityName, propertyName );
 // value = new Component( mappings, persistentClass );
 //
 // bindComponent(
 // subElement,
 // (Component) value,
 // persistentClass.getClassName(),
 // propertyName,
 // subpath,
 // true,
 // "properties".equals( subElementName ),
 // mappings,
 // inheritedMetas,
 // false
 // );
 			}
 		}
 	}
 
-	private void bindProperty(XMLPropertyElement property, EntityBinding entityBinding) {
-		SingularAttribute attr = entityBinding.getEntity().locateOrCreateSingularAttribute( property.getName() );
-		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( attr );
-		resolveTypeInformation( property, attributeBinding );
+	private void bindProperty(final XMLPropertyElement property, EntityBinding entityBinding) {
+		SimpleAttributeBinding attributeBinding = doBasicSimpleAttributeBindingCreation( property.getName(), property, entityBinding );
 
 		attributeBinding.setInsertable( Helper.getBooleanValue( property.isInsert(), true ) );
-		attributeBinding.setInsertable( Helper.getBooleanValue( property.isUpdate(), true ) );
+		attributeBinding.setUpdatable( Helper.getBooleanValue( property.isUpdate(), true ) );
 		attributeBinding.setGeneration( PropertyGeneration.parse( property.getGenerated() ) );
 		attributeBinding.setLazy( property.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( property.isOptimisticLock() );
 
+// todo : implement.  Is this meant to indicate the natural-id?
+//		attributeBinding.setAlternateUniqueKey( ... );
+
+		attributeBinding.setValue(
+				makeValue(
+						new RelationValueMetadataSource() {
+							@Override
+							public String getColumnAttribute() {
+								return property.getColumn();
+							}
+
+							@Override
+							public String getFormulaAttribute() {
+								return property.getFormula();
+							}
+
+							@Override
+							public List getColumnOrFormulaElements() {
+								return property.getColumnOrFormula();
+							}
+						},
+						attributeBinding
+				)
+		);
+	}
+
+	private SimpleAttributeBinding doBasicSimpleAttributeBindingCreation(
+			String attributeName,
+			SingularAttributeSource attributeSource,
+			EntityBinding entityBinding) {
+		// todo : the need to pass in the attribute name here could be alleviated by making name required on <id/> etc
+		SingularAttribute attribute = entityBinding.getEntity().locateOrCreateSingularAttribute( attributeName );
+		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
+		resolveTypeInformation( attributeSource, attributeBinding );
+
 		attributeBinding.setPropertyAccessorName(
 				Helper.getPropertyAccessorName(
-						property.getAccess(),
+						attributeSource.getAccess(),
 						false,
 						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
 				)
 		);
 
 		attributeBinding.setMetaAttributeContext(
-				Helper.extractMetaAttributeContext( property.getMeta(), entityBinding.getMetaAttributeContext() )
+				Helper.extractMetaAttributeContext( attributeSource.getMeta(), entityBinding.getMetaAttributeContext() )
 		);
 
-// todo : implement.  Is this meant to indicate the natural-id?
-//		attributeBinding.setAlternateUniqueKey( ... );
-
-		attributeBinding.setValue( makeValue( property, attributeBinding ) );
-
+		return attributeBinding;
 	}
 
-	private void resolveTypeInformation(XMLPropertyElement property, final SimpleAttributeBinding attributeBinding) {
+	private void resolveTypeInformation(SingularAttributeSource property, final SimpleAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
+			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			( (AbstractAttributeContainer.SingularAttributeImpl) attributeBinding.getAttribute() ).resolveType(
 					currentBindingContext.makeJavaType( attributeJavaType.getName() )
 			);
 		}
 
 		// prefer type attribute over nested <type/> element
 		if ( property.getTypeAttribute() != null ) {
 			final String explicitTypeName = property.getTypeAttribute();
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor().getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
-				attributeBinding.getHibernateTypeDescriptor().setTypeName( typeDef.getTypeClass() );
+				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
-				attributeBinding.getHibernateTypeDescriptor().setTypeName( explicitTypeName );
+				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 		}
 		else if ( property.getType() != null ) {
 			// todo : consider changing in-line type definitions to implicitly generate uniquely-named type-defs
-			attributeBinding.getHibernateTypeDescriptor().setTypeName( property.getType().getName() );
+			attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( property.getType().getName() );
 			for ( XMLParamElement xmlParamElement : property.getType().getParam() ) {
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().put(
 						xmlParamElement.getName(),
 						xmlParamElement.getValue()
 				);
 			}
 		}
 		else {
-			// see if we can reflect to determine the appropriate type
-			try {
-				final String attributeName = attributeBinding.getAttribute().getName();
-				final Class ownerClass = attributeBinding.getAttribute().getAttributeContainer().getClassReference();
-				BeanInfoHelper.visitBeanInfo(
-					ownerClass,
-					new BeanInfoHelper.BeanInfoDelegate() {
-						@Override
-						public void processBeanInfo(BeanInfo beanInfo) throws Exception {
-							for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
-								if ( propertyDescriptor.getName().equals( attributeName ) ) {
-									attributeBinding.getHibernateTypeDescriptor().setTypeName(
-											propertyDescriptor.getPropertyType().getName()
-									);
-									break;
-								}
-							}
-						}
-					}
-				);
-			}
-			catch ( Exception e ) {
-				// todo : log it?
+			if ( attributeJavaType == null ) {
+				// we will have problems later determining the Hibernate Type to use.  Should we throw an
+				// exception now?  Might be better to get better contextual info
 			}
 		}
 	}
 
 	private Class<?> determineJavaType(final Attribute attribute) {
 		try {
 			final Class ownerClass = attribute.getAttributeContainer().getClassReference();
 			AttributeJavaTypeDeterminerDelegate delegate = new AttributeJavaTypeDeterminerDelegate( attribute.getName() );
 			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
 			return delegate.javaType;
 		}
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 			// todo : log it?
 		}
 		return null;
 	}
 
 	private static class AttributeJavaTypeDeterminerDelegate implements BeanInfoHelper.BeanInfoDelegate {
 		private final String attributeName;
 		private Class<?> javaType = null;
 
 		private AttributeJavaTypeDeterminerDelegate(String attributeName) {
 			this.attributeName = attributeName;
 		}
 
 		@Override
 		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
 				if ( propertyDescriptor.getName().equals( attributeName ) ) {
 					javaType = propertyDescriptor.getPropertyType();
 					break;
 				}
 			}
 		}
 	}
 
+	private static interface RelationValueMetadataSource {
+		public String getColumnAttribute();
+		public String getFormulaAttribute();
+		public List getColumnOrFormulaElements();
+	}
+
 	private org.hibernate.metamodel.relational.Value makeValue(
-			XMLPropertyElement property,
+			RelationValueMetadataSource relationValueMetadataSource,
 			SimpleAttributeBinding attributeBinding) {
-
 		// todo : to be completely correct, we need to know which table the value belongs to.
 		// 		There is a note about this somewhere else with ideas on the subject.
 		//		For now, just use the entity's base table.
 		final TableSpecification valueSource = attributeBinding.getEntityBinding().getBaseTable();
 
-		if ( property.getColumn() != null && ! property.getColumn().isEmpty() ) {
-			if ( property.getColumnOrFormula() != null && ! property.getColumnOrFormula().isEmpty() ) {
+		if ( StringHelper.isNotEmpty( relationValueMetadataSource.getColumnAttribute() ) ) {
+			if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
+					&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
 				throw new MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						currentBindingContext.getOrigin()
 				);
 			}
-			if ( property.getFormula() != null ) {
+			if ( StringHelper.isNotEmpty( relationValueMetadataSource.getFormulaAttribute() ) ) {
 				throw new MappingException(
 						"column and formula attributes may not be used together",
 						currentBindingContext.getOrigin()
 				);
 			}
-			return valueSource.locateOrCreateColumn( property.getColumn() );
+			return valueSource.locateOrCreateColumn( relationValueMetadataSource.getColumnAttribute() );
 		}
-		else if ( property.getFormula() != null && ! property.getFormula().isEmpty() ) {
-			if ( property.getColumnOrFormula() != null && ! property.getColumnOrFormula().isEmpty() ) {
+		else if ( StringHelper.isNotEmpty( relationValueMetadataSource.getFormulaAttribute() ) ) {
+			if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
+					&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
 				throw new MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						currentBindingContext.getOrigin()
 				);
 			}
-			return valueSource.locateOrCreateDerivedValue( property.getFormula() );
+			// column/formula attribute combo checked already
+			return valueSource.locateOrCreateDerivedValue( relationValueMetadataSource.getFormulaAttribute() );
 		}
-		else if ( property.getColumnOrFormula() != null && ! property.getColumnOrFormula().isEmpty() ) {
+		else if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
+				&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
 			List<SimpleValue> values = new ArrayList<SimpleValue>();
-			for ( Object columnOrFormula : property.getColumnOrFormula() ) {
+			for ( Object columnOrFormula : relationValueMetadataSource.getColumnOrFormulaElements() ) {
 				final SimpleValue value;
 				if ( XMLColumnElement.class.isInstance( columnOrFormula ) ) {
 					final XMLColumnElement columnElement = (XMLColumnElement) columnOrFormula;
 					final Column column = valueSource.locateOrCreateColumn( columnElement.getName() );
 					column.setNullable( ! columnElement.isNotNull() );
 					column.setDefaultValue( columnElement.getDefault() );
 					column.setSqlType( columnElement.getSqlType() );
 					column.setSize(
 							new Size(
 									Helper.getIntValue( columnElement.getPrecision(), -1 ),
 									Helper.getIntValue( columnElement.getScale(), -1 ),
 									Helper.getLongValue( columnElement.getLength(), -1 ),
 									Size.LobMultiplier.NONE
 							)
 					);
 					column.setDatatype( null ); // todo : ???
 					column.setReadFragment( columnElement.getRead() );
 					column.setWriteFragment( columnElement.getWrite() );
 					column.setUnique( columnElement.isUnique() );
 					column.setCheckCondition( columnElement.getCheck() );
 					column.setComment( columnElement.getComment() );
 					value = column;
 				}
 				else {
-					// todo : ??? Seems jaxb is not generating this class ?!?!?!
-//					final XMLFormulaElement formulaElement = (XMLFormulaElement) columnOrFormula;
-					value = null;
+					value = valueSource.locateOrCreateDerivedValue( (String) columnOrFormula );
 				}
 				if ( value != null ) {
 					values.add( value );
 				}
 			}
 
 			if ( values.size() == 1 ) {
 				return values.get( 0 );
 			}
 
 			final Tuple tuple = valueSource.createTuple(
 					attributeBinding.getEntityBinding().getEntity().getName() + '.'
 							+ attributeBinding.getAttribute().getName()
 			);
 			for ( SimpleValue value : values ) {
 				tuple.addValue( value );
 			}
 			return tuple;
 		}
 		else {
 			// assume a column named based on the NamingStrategy
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
 			return valueSource.locateOrCreateColumn( name );
 		}
-
-//		// TODO: not sure I like this here...
-//		if ( isPrimaryKey() ) {
-//			if ( SimpleValue.class.isInstance( value ) ) {
-//				if ( !Column.class.isInstance( value ) ) {
-//					// this should never ever happen..
-//					throw new org.hibernate.MappingException( "Simple ID is not a column." );
-//				}
-//				entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( value ) );
-//			}
-//			else {
-//				for ( SimpleValueRelationalState val : TupleRelationalState.class.cast( state )
-//						.getRelationalStates() ) {
-//					if ( Column.class.isInstance( val ) ) {
-//						entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( val ) );
-//					}
-//				}
-//			}
-//		}
 	}
 
 	private void makeManyToOneAttributeBinding(XMLManyToOneElement manyToOne, EntityBinding entityBinding) {
 		//To change body of created methods use File | Settings | File Templates.
 	}
 
 	private BagBinding makeBagAttributeBinding(XMLBagElement collection, EntityBinding entityBinding) {
 		return null;  //To change body of created methods use File | Settings | File Templates.
 	}
 
 	private void bindSecondaryTables(EntitySourceInformation entitySourceInfo, EntityBinding entityBinding) {
 		final EntityElement entityElement = entitySourceInfo.getEntityElement();
 
 		if ( ! ( entityElement instanceof JoinElementSource) ) {
 			return;
 		}
 
 		for ( XMLJoinElement join : ( (JoinElementSource) entityElement ).getJoin() ) {
 			// todo : implement
 			// Join join = new Join();
 			// join.setPersistentClass( persistentClass );
 			// bindJoin( subElement, join, mappings, inheritedMetas );
 			// persistentClass.addJoin( join );
 		}
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		//To change body of created methods use File | Settings | File Templates.
 	}
 
 	private static interface AttributeMetadataContainer {
 		public List<Object> getAttributeElements();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/jaxb/mapping/SingularAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/jaxb/mapping/SingularAttributeSource.java
new file mode 100644
index 0000000000..6163bbf281
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/jaxb/mapping/SingularAttributeSource.java
@@ -0,0 +1,37 @@
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
+package org.hibernate.metamodel.source.hbm.jaxb.mapping;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface SingularAttributeSource extends MetaAttributeContainer {
+	public String getName();
+
+	public String getTypeAttribute();
+
+    public XMLTypeElement getType();
+
+	public String getAccess();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
index 9092a6e70b..9954e5e114 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
@@ -1,225 +1,233 @@
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
 import org.hibernate.metamodel.source.BindingContext;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
 
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
-		this.hibernateTypeDescriptor.setTypeName( id.getTypeAttribute() );
+		this.hibernateTypeDescriptor.setExplicitTypeName( id.getTypeAttribute() );
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
-		this.hibernateTypeDescriptor.setTypeName( discriminator.getType() == null ? "string" : discriminator.getType() );
+		this.hibernateTypeDescriptor.setExplicitTypeName(
+				discriminator.getType() == null ?
+						"string" :
+						discriminator.getType()
+		);
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
-		this.hibernateTypeDescriptor.setTypeName( version.getType() == null ? "integer" : version.getType() );
+		this.hibernateTypeDescriptor.setExplicitTypeName( version.getType() == null ? "integer" : version.getType() );
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
-		this.hibernateTypeDescriptor.setTypeName( "db".equals( timestamp.getSource() ) ? "dbtimestamp" : "timestamp" );
+		this.hibernateTypeDescriptor.setExplicitTypeName(
+				"db".equals( timestamp.getSource() ) ?
+						"dbtimestamp" :
+						"timestamp"
+		);
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
-		this.hibernateTypeDescriptor.setTypeName( property.getTypeAttribute() );
+		this.hibernateTypeDescriptor.setExplicitTypeName( property.getTypeAttribute() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
index b27526c6ca..3d51927d3a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
@@ -1,139 +1,136 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
-import org.hibernate.metamodel.domain.AbstractAttributeContainer;
 import org.hibernate.metamodel.domain.Attribute;
-import org.hibernate.metamodel.domain.BasicType;
-import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.Type;
 
 /**
  * This is a TEMPORARY way to initialize HibernateTypeDescriptor.explicitType.
  * This class will be removed when types are resolved properly.
  *
  * @author Gail Badner
  */
 class AttributeTypeResolver {
 
 	private final MetadataImplementor metadata;
 
 	AttributeTypeResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindings() ) {
 				Type type = resolveHibernateType( attributeBinding );
 				if ( type != null && ! type.isAssociationType() && ! type.isCollectionType() && ! type.isComponentType() ) {
 					resolveJavaType( attributeBinding.getAttribute(), type );
 					for ( Value value : attributeBinding.getValues() ) {
 						resolveSqlType( value, type );
 					}
 				}
 			}
 		}
 	}
 
 	private Type resolveHibernateType(AttributeBinding attributeBinding) {
-		if ( attributeBinding.getHibernateTypeDescriptor().getExplicitType() != null ) {
-			return attributeBinding.getHibernateTypeDescriptor().getExplicitType(); // already resolved
+		if ( attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() != null ) {
+			return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping(); // already resolved
 		}
 
 		// this only works for "basic" attribute types
 		HibernateTypeDescriptor typeDescriptor = attributeBinding.getHibernateTypeDescriptor();
-		if ( typeDescriptor == null || typeDescriptor.getTypeName() == null) {
+		if ( typeDescriptor == null || typeDescriptor.getExplicitTypeName() == null) {
 			throw new MappingException( "Hibernate type name has not been defined for attribute: " +
 					getQualifiedAttributeName( attributeBinding )
 			);
 		}
 		Type type = null;
-		if ( typeDescriptor.getTypeName() != null ) {
+		if ( typeDescriptor.getExplicitTypeName() != null ) {
 			Properties typeParameters = null;
 			if ( typeDescriptor.getTypeParameters() != null ) {
 				typeParameters = new Properties();
 				typeParameters.putAll( typeDescriptor.getTypeParameters() );
 			}
 			type = metadata.getTypeResolver().heuristicType(
-							typeDescriptor.getTypeName(),
+							typeDescriptor.getExplicitTypeName(),
 							typeParameters
 					);
-			typeDescriptor.setExplicitType( type );
+			typeDescriptor.setResolvedTypeMapping( type );
 		}
 		return type;
 	}
 
 	// this only works for singular basic types
 	private void resolveJavaType(Attribute attribute, Type type) {
 //		if ( ! ( type instanceof AbstractStandardBasicType ) || ! attribute.isSingular() ) {
 //			return;
 //		}
 //		// Converting to SingularAttributeImpl is bad, but this resolver is TEMPORARY!
 //		AbstractAttributeContainer.SingularAttributeImpl singularAttribute =
 //				( AbstractAttributeContainer.SingularAttributeImpl ) attribute;
 //		if ( ! singularAttribute.isTypeResolved() ) {
 //			singularAttribute.resolveType(
 //					new BasicType(
 //							new JavaType( ( ( AbstractStandardBasicType) type ).getJavaTypeDescriptor().getJavaTypeClass() )
 //					)
 //			);
 //		}
 	}
 
 	// this only works for singular basic types
 	private void resolveSqlType(Value value, Type type) {
 		if ( value == null || ! ( value instanceof SimpleValue ) || ! ( type instanceof AbstractStandardBasicType )  ) {
 			return;
 		}
 		// Converting to AbstractStandardBasicType is bad, but this resolver is TEMPORARY!
 		AbstractStandardBasicType basicType = ( AbstractStandardBasicType ) type;
 		Datatype dataType = new Datatype(
 								basicType.getSqlTypeDescriptor().getSqlType(),
 								basicType.getName(),
 								basicType.getReturnedClass()
 						);
 		( (SimpleValue) value ).setDatatype( dataType );
 	}
 
 	// TODO: this does not work for components
 	private static String getQualifiedAttributeName(AttributeBinding attributebinding) {
 		return new StringBuilder()
 				.append( attributebinding.getEntityBinding().getEntity().getName() )
 				.append( "." )
 				.append( attributebinding.getAttribute().getName() )
 				.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 6811d33a44..593e8d3525 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,592 +1,592 @@
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
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
 import org.hibernate.metamodel.source.hbm.HbmSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
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
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
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
 					new HbmSourceProcessorImpl( this ),
 					new AnnotationsSourceProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
 					new AnnotationsSourceProcessor( this ),
 					new HbmSourceProcessorImpl( this )
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
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
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
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
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
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
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
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
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
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
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
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
-				.getExplicitType();
+				.getResolvedTypeMapping();
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
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
-		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
+		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
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
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 3076c610fa..0f071a3e27 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1907 +1,1906 @@
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
 import org.hibernate.engine.OptimisticLockStyle;
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
-import org.hibernate.metamodel.relational.Identifier;
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
 
 		loaderName = entityBinding.getCustomLoaderName();
 
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
-				lazyTypes.add( prop.getHibernateTypeDescriptor().getExplicitType());
+				lazyTypes.add( prop.getHibernateTypeDescriptor().getResolvedTypeMapping());
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
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index cc293e6c83..4f30c3d2f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1024 +1,1024 @@
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
 package org.hibernate.persister.entity;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
-import org.hibernate.EntityMode;
+
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.Type;
 
 /**
  * The default implementation of the <tt>EntityPersister</tt> interface.
  * Implements the "table-per-class-hierarchy" or "roll-up" mapping strategy
  * for an entity class and its inheritence hierarchy.  This is implemented
  * as a single table holding all classes in the hierarchy with a discrimator
  * column used to determine which concrete class is referenced.
  *
  * @author Gavin King
  */
 public class SingleTableEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int joinSpan;
 	private final String[] qualifiedTableNames;
 	private final boolean[] isInverseTable;
 	private final boolean[] isNullableTable;
 	private final String[][] keyColumnNames;
 	private final boolean[] cascadeDeleteEnabled;
 	private final boolean hasSequentialSelects;
 	
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final boolean[] subclassTableIsLazyClosure;
 	private final boolean[] isInverseSubclassTable;
 	private final boolean[] isNullableSubclassTable;
 	private final boolean[] subclassTableSequentialSelect;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] propertyTableNumbers;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	// discriminator column
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map propertyTableNumbersByNameAndSubclass = new HashMap();
 	
 	private final Map sequentialSelectStringsByEntityName = new HashMap();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan()+1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = table.getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
 		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
 		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
 									  : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
 		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
 		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
 									  : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
 		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
 		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
 									  : persistentClass.getCustomSQLDeleteCheckStyle();
 
 		// JOINS
 
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		int j = 1;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			);
 			isInverseTable[j] = join.isInverse();
 			isNullableTable[j] = join.isOptional();
 			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && 
 				factory.getDialect().supportsCascadeDelete();
 
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
 
 			Iterator iter = join.getKey().getColumnIterator();
 			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
 			}
 
 			j++;
 		}
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames, 
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 		
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
 			subclassTables.add( join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add(keyCols);
 		}
 		
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			discriminatorType = persistentClass.getDiscriminator().getType();
 			if ( persistentClass.isDiscriminatorValueNull() ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
 				try {
 					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 		
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 		
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber(prop);
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put( 
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
 					join 
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add(join);
 				}
 				else {
 					columnJoinNumbers.add(join);
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						subclassesByDiscriminatorValue.put(
 							dtype.stringToObject( sc.getDiscriminatorValue() ),
 							sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public SingleTableEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( entityBinding, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		// TODO: fix when joins are working (HHH-6391)
 		//joinSpan = entityBinding.getJoinClosureSpan() + 1;
 		joinSpan = 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 
 		// TODO: fix when EntityBinhding.getRootEntityBinding() exists (HHH-6337)
 		//final Table table = entityBinding.getRootEntityBinding().getBaseTable();
 		final TableSpecification table = entityBinding.getBaseTable();
 		qualifiedTableNames[0] = table.getQualifiedName( factory.getDialect() );
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		initializeCustomSql( entityBinding.getCustomInsert(), 0, customSQLInsert, insertCallable, insertResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomUpdate(), 0, customSQLUpdate, updateCallable, updateResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomDelete(), 0, customSQLDelete, deleteCallable, deleteResultCheckStyles );
 
 		// JOINS
 
 		// TODO: add join stuff when HHH-6391 is working
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames,
 				ArrayHelper.toStringArray( entityBinding.getSynchronizedTableNames() )
 		);
 
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 
 		// TODO: add join stuff when HHH-6391 is working
 
 
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		//  for now set hasSubclasses to false
 		//hasSubclasses = entityBinding.hasSubclasses();
 		boolean hasSubclasses = false;
 
 		//polymorphic = ! entityBinding.isRoot() || entityBinding.hasSubclasses();
 		boolean isPolymorphic = ! entityBinding.isRoot() || hasSubclasses;
 		final Object discriminatorValue;
 		if ( isPolymorphic ) {
 			org.hibernate.metamodel.relational.Value discrimValue =
 					entityBinding.getEntityDiscriminator().getValueBinding().getValue();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getEntityDiscriminator().isForced();
 			if ( ! SimpleValue.class.isInstance(  discrimValue ) ) {
 				throw new MappingException( "discriminator must be mapped to a single column or formula." );
 			}
 			if ( DerivedValue.class.isInstance( discrimValue ) ) {
 				DerivedValue formula = ( DerivedValue ) discrimValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else if ( org.hibernate.metamodel.relational.Column.class.isInstance( discrimValue ) ) {
 				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discrimValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				// TODO: fix this when EntityBinding.getRootEntityBinding() is implemented;
 				// for now, assume entityBinding is the root
 				//discriminatorAlias = column.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			else {
 				throw new MappingException( "Unknown discriminator value type:" + discrimValue.toLoggableString() );
 			}
 			discriminatorType =
 					entityBinding
 							.getEntityDiscriminator()
 							.getValueBinding()
 							.getHibernateTypeDescriptor()
-							.getExplicitType();
+							.getResolvedTypeMapping();
 			if ( entityBinding.getDiscriminatorValue() == null ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorValue().equals( NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorValue().equals( NOT_NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable =
 						entityBinding.getEntityDiscriminator().isInserted() &&
 								! DerivedValue.class.isInstance( discrimValue );
 				try {
 					DiscriminatorType dtype = ( DiscriminatorType ) discriminatorType;
 					discriminatorValue = dtype.stringToObject( entityBinding.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		int i=0;
 		for( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			// TODO: fix when joins are working (HHH-6391)
 			//propertyTableNumbers[i++] = entityBinding.getJoinNumber( attributeBinding);
 			if ( attributeBinding == entityBinding.getEntityIdentifier().getValueBinding() ) {
 				continue; // skip identifier binding
 			}
 			propertyTableNumbers[ i++ ] = 0;
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 
 		// TODO: fix when subclasses are working (HHH-6337)
 		//for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
 		for ( AttributeBinding prop : entityBinding.getAttributeBindingClosure() ) {
 			// TODO: fix when joins are working (HHH-6391)
 			//int join = entityBinding.getJoinNumber(prop);
 			int join = 0;
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
 					prop.getEntityBinding().getEntity().getName() + '.' + prop.getAttribute().getName(),
 					join
 			);
 
 			for ( SimpleValue simpleValue : prop.getValues() ) {
 				if ( DerivedValue.class.isInstance( simpleValue ) ) {
 					formulaJoinedNumbers.add( join );
 				}
 				else {
 					columnJoinNumbers.add( join );
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		// TODO; fix when subclasses are working (HHH-6337)
 		//int subclassSpan = entityBinding.getSubclassSpan() + 1;
 		int subclassSpan = 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( isPolymorphic ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 
 		// TODO; fix when subclasses are working (HHH-6337)
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( entityBinding );
 
 		postConstruct( mapping );
 	}
 
 	private static void initializeCustomSql(
 			CustomSQL customSql,
 			int i,
 			String[] sqlStrings,
 			boolean[] callable,
 			ExecuteUpdateResultCheckStyle[] checkStyles) {
 		sqlStrings[i] = customSql != null ?  customSql.getSql(): null;
 		callable[i] = sqlStrings[i] != null && customSql.isCallable();
 		checkStyles[i] = customSql != null && customSql.getCheckStyle() != null ?
 				customSql.getCheckStyle() :
 				ExecuteUpdateResultCheckStyle.determineDefault( sqlStrings[i], callable[i] );
 	}
 
 	protected boolean isInverseTable(int j) {
 		return isInverseTable[j];
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return isInverseSubclassTable[j];
 	}
 
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return discriminatorColumnReaders;
 	}			
 	
 	public String getDiscriminatorColumnReaderTemplate() {
 		return discriminatorColumnReaderTemplate;
 	}	
 	
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return discriminatorFormulaTemplate;
 	}
 
 	public String getTableName() {
 		return qualifiedTableNames[0];
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if (value==null) {
 			return (String) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
 		}
 		else {
 			String result = (String) subclassesByDiscriminatorValue.get(value);
 			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName==null;
 	}
 
 	protected String getDiscriminatorFormula() {
 		return discriminatorFormula;
 	}
 
 	protected String getTableName(int j) {
 		return qualifiedTableNames[j];
 	}
 	
 	protected String[] getKeyColumns(int j) {
 		return keyColumnNames[j];
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return cascadeDeleteEnabled[j];
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return propertyTableNumbers[property]==j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment(alias);
 		if ( hasWhere() ) result += " and " + getSQLWhereString(alias);
 		return result;
 	}
 	
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return forceDiscriminator ?
 			discriminatorFilterFragment(alias) :
 			"";
 	}
 
 	private String discriminatorFilterFragment(String alias) throws MappingException {
 		if ( needsDiscriminator() ) {
 			InFragment frag = new InFragment();
 
 			if ( isDiscriminatorFormula() ) {
 				frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 			}
 			else {
 				frag.setColumn( alias, getDiscriminatorColumnName() );
 			}
 
 			String[] subclasses = getSubclassClosure();
 			for ( int i=0; i<subclasses.length; i++ ) {
 				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclasses[i] );
 				if ( !queryable.isAbstract() ) frag.addValue( queryable.getDiscriminatorSQLValue() );
 			}
 
 			StringBuffer buf = new StringBuffer(50)
 				.append(" and ")
 				.append( frag.toFragmentString() );
 
 			return buf.toString();
 		}
 		else {
 			return "";
 		}
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 		}
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return joinSpan;
 	}
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 
 		if (discriminatorInsertable) {
 			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue );
 		}
 
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return propertyTableNumbers;
 	}
 		
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return hasSequentialSelects && 
 			isSubclassTableSequentialSelect( getSubclassPropertyTableNumber(propertyName, entityName) );
 	}
 	
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 	
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType(propertyName);
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) return 0;
 		final Integer tabnum = (Integer) propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
 		return tabnum==null ? 0 : tabnum.intValue();
 	}
 	
 	protected String getSequentialSelect(String entityName) {
 		return (String) sequentialSelectStringsByEntityName.get(entityName);
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 		
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet tableNumbers = new HashSet();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i=0; i<props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect(propTableNumber) && !isSubclassTableLazy(propTableNumber) ) {
 				tableNumbers.add( propTableNumber);
 			}
 		}
 		if ( tableNumbers.isEmpty() ) return null;
 		
 		//figure out which columns are needed
 		ArrayList columnNumbers = new ArrayList();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 		
 		//figure out which formulas are needed
 		ArrayList formulaNumbers = new ArrayList();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 		
 		//render the SQL
 		return renderSelect( 
 			ArrayHelper.toIntArray(tableNumbers),
 			ArrayHelper.toIntArray(columnNumbers),
 			ArrayHelper.toIntArray(formulaNumbers)
 		);
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
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 	
 	protected boolean isNullableTable(int j) {
 		return isNullableTable[j];
 	}
 	
 	protected boolean isNullableSubclassTable(int j) {
 		return isNullableSubclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if (index==null) return null;
 		return qualifiedTableNames[ propertyTableNumbers[ index.intValue() ] ];
 	}
 	
 	public void postInstantiate() {
 		super.postInstantiate();
 		if (hasSequentialSelects) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i=1; i<entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect(loadable);
 					sequentialSelectStringsByEntityName.put( entityNames[i], sequentialSelect );
 				}
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return getTableSpan() > 1;
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index 6ff5e12423..00db55906e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,382 +1,381 @@
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
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.EntityIdentifier;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
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
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(EntityBinding mappedEntity, IdentifierGenerator generator) {
 
 		final SimpleAttributeBinding property = mappedEntity.getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		final String mappedUnsavedValue = property.getUnsavedValue();
-		final Type type = property.getHibernateTypeDescriptor().getExplicitType();
+		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.getEntityIdentifier().isEmbedded(),
 					mappedEntity.getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getEntityIdentifier().isEmbedded(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
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
 				mappedUnsavedValue,
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
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(SimpleAttributeBinding property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
-				( VersionType ) property.getHibernateTypeDescriptor().getExplicitType(),
+				( VersionType ) property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 				getConstructor( property.getEntityBinding() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 		        property.getAttribute().getName(),
 		        null,
-		        property.getHibernateTypeDescriptor().getExplicitType(),
+		        property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 		        lazy,
 				property.isInsertable(),
 				property.isUpdatable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isNullable(),
 				property.isUpdatable() && !lazy,
 				property.isOptimisticLockable(),
 				// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
 				//property.getCascadeStyle(),
 				CascadeStyle.NONE,
 		        unsavedValue
 			);
 	}
 
 	/**
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
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
 
-		final Type type = property.getHibernateTypeDescriptor().getExplicitType();
+		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		boolean alwaysDirtyCheck = type.isAssociationType() &&
 				( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		if ( property.isSimpleValue() ) {
 			SimpleAttributeBinding simpleProperty = ( SimpleAttributeBinding ) property;
 			return new StandardProperty(
 					simpleProperty.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && simpleProperty.isLazy(),
 					simpleProperty.isInsertable(),
 					simpleProperty.isUpdatable(),
 					simpleProperty.getGeneration() == PropertyGeneration.INSERT || simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
 					simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
 					simpleProperty.isNullable(),
 					alwaysDirtyCheck || simpleProperty.isUpdatable(),
 					simpleProperty.isOptimisticLockable(),
 					// TODO: get cascadeStyle from simpleProperty when HHH-6355 is fixed; for now, assume NONE
 					//simpleProperty.getCascadeStyle(),
 					CascadeStyle.NONE,
 					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
 					//simpleProperty.getFetchMode()
 					FetchMode.DEFAULT
 				);
 		}
 		else {
 			PluralAttributeBinding pluralProperty = ( PluralAttributeBinding ) property;
 
 			return new StandardProperty(
 					pluralProperty.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && pluralProperty.isLazy(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
 					// pluralProperty.isInsertable(),
 					//pluralProperty.isUpdatable(),
 					true,
 					true,
 					false,
 					false,
 					pluralProperty.isNullable(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
 					//alwaysDirtyCheck || pluralProperty.isUpdatable(),
 					true,
 					pluralProperty.isOptimisticLocked(),
 					// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
 					//pluralProperty.getCascadeStyle(),
 					CascadeStyle.NONE,
 					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
 					//pluralProperty.getFetchMode()
 					FetchMode.DEFAULT
 				);
 		}
 	}
 
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
 
 	private static Constructor getConstructor(EntityBinding entityBinding) {
 		if ( entityBinding == null || entityBinding.getEntity() == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getClassReference() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
 	private static Getter getGetter(AttributeBinding mappingProperty) {
 		if ( mappingProperty == null || mappingProperty.getEntityBinding().getEntity() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
 				mappingProperty.getEntityBinding().getEntity().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 73ccd8c088..04879fa3ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,554 +1,554 @@
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
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.PojoInstantiator;
 import org.hibernate.type.CompositeType;
 
 /**
  * An {@link EntityTuplizer} specific to the pojo entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class PojoEntityTuplizer extends AbstractEntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoEntityTuplizer.class.getName());
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set lazyPropertyNames = new HashSet();
 	private final ReflectionOptimizer optimizer;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 
 		Iterator iter = mappedEntity.getPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property property = (Property) iter.next();
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[i].getMethodName();
 			setterNames[i] = setters[i].getMethodName();
 			propTypes[i] = getters[i].getReturnType();
 		}
 
 		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 
 	}
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getEntity().getClassReference();
 		this.proxyInterface = mappedEntity.getProxyInterfaceType().getValue();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 
 		for ( AttributeBinding property : mappedEntity.getAttributeBindingClosure() ) {
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getAttribute().getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[ i ].getMethodName();
 			setterNames[i] = setters[ i ].getMethodName();
 			propTypes[i] = getters[ i ].getReturnType();
 		}
 
 		if ( hasCustomAccessors || ! Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
 					mappedClass, getterNames, setterNames, propTypes
 			);
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
         // determine all interfaces needed by the resulting proxy
 		HashSet<Class> proxyInterfaces = new HashSet<Class>();
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( !proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		Iterator subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = ( Subclass ) subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter(clazz).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 			method = property.getSetter(clazz).getMethod();
             if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					persistentClass.hasEmbeddedIdentifier() ?
 			                (CompositeType) persistentClass.getIdentifier().getType() :
 			                null
 			);
 		}
 		catch ( HibernateException he ) {
             LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected ProxyFactory buildProxyFactory(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
 		// determine all interfaces needed by the resulting proxy
 		HashSet<Class> proxyInterfaces = new HashSet<Class>();
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Class mappedClass = entityBinding.getEntity().getClassReference();
 		Class proxyInterface = entityBinding.getProxyInterfaceType().getValue();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( ! proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		// TODO: fix when it's possible to get subclasses from an EntityBinding
 		//Iterator subclasses = entityBinding.getSubclassIterator();
 		//while ( subclasses.hasNext() ) {
 		//	final Subclass subclass = ( Subclass ) subclasses.next();
 		//	final Class subclassProxy = subclass.getProxyInterface();
 		//	final Class subclassClass = subclass.getMappedClass();
 		//	if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 		//		if ( !subclassProxy.isInterface() ) {
 		//			throw new MappingException(
 		//					"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 		//			);
 		//		}
 		//		proxyInterfaces.add( subclassProxy );
 		//	}
 		//}
 
 		for ( AttributeBinding property : entityBinding.getAttributeBindings() ) {
 			Method method = getGetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.gettersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 			method = getSetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.settersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( entityBinding, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					entityBinding.getEntityIdentifier().isEmbedded() ?
 			                ( CompositeType ) entityBinding
 									.getEntityIdentifier()
 									.getValueBinding()
 									.getHibernateTypeDescriptor()
-									.getExplicitType() :
+									.getResolvedTypeMapping() :
 			                null
 			);
 		}
 		catch ( HibernateException he ) {
 			LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Instantiator buildInstantiator(EntityBinding entityBinding) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( entityBinding, null );
 		}
 		else {
 			return new PojoInstantiator( entityBinding, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValuesToInsert( entity, mergeMap, session );
 		}
 	}
 
 	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
 		optimizer.getAccessOptimizer().setPropertyValues( object, values );
 	}
 
 	protected Object[] getPropertyValuesWithOptimizer(Object object) {
 		return optimizer.getAccessOptimizer().getPropertyValues( object );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
 		return getGetter( mappedProperty );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
 		return getSetter( mappedProperty );
 	}
 
 	private Getter getGetter(AttributeBinding mappedProperty)  throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getGetter(
 				mappedProperty.getEntityBinding().getEntity().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getSetter(
 				mappedProperty.getEntityBinding().getEntity().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
 		// TODO: Fix this then backrefs are working in new metamodel
 		return PropertyAccessorFactory.getPropertyAccessor(
 				mappedProperty.getEntityBinding().getEntity().getClassReference(),
 				mappedProperty.getPropertyAccessorName()
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
     //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		if ( isInstrumented() ) {
 			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
 					lazyPropertyNames : null;
 			//TODO: if we support multiple fetch groups, we would need
 			//      to clone the set of lazy properties!
 			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean hasUninitializedLazyProperties(Object entity) {
 		if ( getEntityMetamodel().hasLazyProperties() ) {
 			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return callback != null && !callback.isInitialized();
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isInstrumented() {
 		return FieldInterceptionHelper.isInstrumented( getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		final Class concreteEntityClass = entityInstance.getClass();
 		if ( concreteEntityClass == getMappedClass() ) {
 			return getEntityName();
 		}
 		else {
 			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
 			if ( entityName == null ) {
 				throw new HibernateException(
 						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
 								+ " expected instance/subclass of [" + getEntityName() + "]"
 				);
 			}
 			return entityName;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/xjb/hbm-mapping-bindings.xjb b/hibernate-core/src/main/xjb/hbm-mapping-bindings.xjb
index 74dcd601f6..a306b2269a 100644
--- a/hibernate-core/src/main/xjb/hbm-mapping-bindings.xjb
+++ b/hibernate-core/src/main/xjb/hbm-mapping-bindings.xjb
@@ -1,134 +1,149 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <jaxb:bindings xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
         xmlns:xsd="http://www.w3.org/2001/XMLSchema"
         xmlns:inheritance="http://jaxb2-commons.dev.java.net/basic/inheritance"
         jaxb:extensionBindingPrefixes="inheritance"
         jaxb:version="2.1">
 
     <jaxb:bindings schemaLocation="../resources/org/hibernate/hibernate-mapping-4.0.xsd" node="/xsd:schema">
 
         <jaxb:schemaBindings>
             <jaxb:nameXmlTransform>
                 <jaxb:typeName prefix="XML"/>
                 <jaxb:elementName prefix="XML"/>
                 <jaxb:modelGroupName prefix="XML"/>
                 <jaxb:anonymousTypeName prefix="XML"/>
             </jaxb:nameXmlTransform>
         </jaxb:schemaBindings>
 
-        <!-- Inheritance -->
+        <!-- Mix-ins -->
         <jaxb:bindings node="//xsd:element[@name='class']/xsd:complexType">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement</inheritance:implements>
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.JoinElementSource</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='subclass-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SubEntityElement</inheritance:implements>
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.JoinElementSource</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='joined-subclass-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SubEntityElement</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='union-subclass-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SubEntityElement</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='sql-insert-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='sql-update-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='sql-delete-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement</inheritance:implements>
         </jaxb:bindings>
         <jaxb:bindings node="//xsd:complexType[@name='sql-delete-all-element']">
             <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement</inheritance:implements>
         </jaxb:bindings>
 
+        <jaxb:bindings node="//xsd:complexType[@name='property-element']">
+            <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SingularAttributeSource</inheritance:implements>
+        </jaxb:bindings>
+        <jaxb:bindings node="//xsd:element[@name='id']/xsd:complexType">
+            <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SingularAttributeSource</inheritance:implements>
+        </jaxb:bindings>
+<!--
+        <jaxb:bindings node="//xsd:element[@name='version']/xsd:complexType">
+            <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SingularAttributeSource</inheritance:implements>
+        </jaxb:bindings>
+        <jaxb:bindings node="//xsd:element[@name='timestamp']/xsd:complexType">
+            <inheritance:implements>org.hibernate.metamodel.source.hbm.jaxb.mapping.SingularAttributeSource</inheritance:implements>
+        </jaxb:bindings>
+-->
+
         <jaxb:bindings node="//xsd:element[@name='class']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:element[@name='discriminator']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:element[@name='discriminator']//xsd:attribute[@name='formula']">
             <jaxb:property name="formulaAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:element[@name='id']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:element[@name='id']//xsd:attribute[@name='type']">
             <jaxb:property name="typeAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:element[@name='version']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='array-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='bag-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='element-element']//xsd:attribute[@name='type']">
             <jaxb:property name="typeAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='idbag-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='idbag-element']//xsd:element[@name='collection-id']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='index-element']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='join-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='joined-subclass-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='key-element']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='key-many-to-one-element']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='key-property-element']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='key-property-element']//xsd:attribute[@name='type']">
             <jaxb:property name="typeAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='list-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='list-index-element']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='map-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='map-element']//xsd:element[@name='map-key']//xsd:attribute[@name='type']">
             <jaxb:property name="typeAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='map-element']//xsd:element[@name='index-many-to-many']//xsd:attribute[@name='column']">
             <jaxb:property name="columnAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='one-to-one-element']//xsd:attribute[@name='formula']">
             <jaxb:property name="formulaAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='primitive-array-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='property-element']//xsd:attribute[@name='type']">
             <jaxb:property name="typeAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='set-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
         <jaxb:bindings node="//xsd:complexType[@name='union-subclass-element']//xsd:attribute[@name='subselect']">
             <jaxb:property name="subselectAttribute"/>
         </jaxb:bindings> 
     </jaxb:bindings>
 
 </jaxb:bindings>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index ca7f01602f..8ea115db92 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,184 +1,183 @@
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
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.SingularAttribute;
-import org.hibernate.metamodel.domain.TypeNature;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
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
-		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getExplicitType() );
+		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getClassReference() );
 
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 		Datatype idDataType = ( (Column) idAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
-		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getExplicitType() );
+		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 
 		assertTrue( nameBinding.getAttribute().isSingular() );
 		assertNotNull( nameBinding.getAttribute() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getClassReference() );
 
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index 633a990aea..250420892d 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,93 +1,93 @@
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
 
 import org.junit.Test;
 
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertSame;
 
 /**
  * Basic binding "smoke" tests
  *
  * @author Steve Ebersole
  */
 public class SimpleValueBindingTests extends BaseUnitTestCase {
 	public static final Datatype BIGINT = new Datatype( Types.BIGINT, "BIGINT", Long.class );
 	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
 
 
 	@Test
 	public void testBasicMiddleOutBuilding() {
 		Table table = new Table( new Schema( null, null ), "the_table" );
 		Entity entity = new Entity( "TheEntity", "NoSuchClass", makeJavaType( "NoSuchClass" ), null );
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setRoot( true );
 		entityBinding.setEntity( entity );
 		entityBinding.setBaseTable( table );
 
 		SingularAttribute idAttribute = entity.locateOrCreateSingularAttribute( "id" );
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( idAttribute );
-		attributeBinding.getHibernateTypeDescriptor().setTypeName( "long" );
+		attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( "long" );
 		assertSame( idAttribute, attributeBinding.getAttribute() );
 
 		entityBinding.getEntityIdentifier().setValueBinding( attributeBinding );
 
 		Column idColumn = table.locateOrCreateColumn( "id" );
 		idColumn.setDatatype( BIGINT );
 		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		//attributeBinding.setValue( idColumn );
 	}
 
 	Value<Class<?>> makeJavaType(final String name) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						try {
 							return Class.forName( name );
 						}
 						catch ( Exception e ) {
 							throw new ClassLoadingException( "Could not load class : " + name, e );
 						}
 					}
 				}
 		);
 	}
 }
