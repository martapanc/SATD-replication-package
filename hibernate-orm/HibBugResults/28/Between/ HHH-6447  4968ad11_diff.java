diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
index 85fd31695f..97ec21dedd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
@@ -1,276 +1,276 @@
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
-	private final Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
+	private final Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
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
 		hibernateTypeDescriptor.setExplicitTypeName( state.getExplicitHibernateTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( state.getExplicitHibernateTypeParameters() );
 		hibernateTypeDescriptor.setJavaTypeName( state.getJavaTypeName() );
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
 
-	public void addEntityReferencingAttributeBinding(EntityReferencingAttributeBinding referencingAttributeBinding) {
+	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding referencingAttributeBinding) {
 		entityReferencingAttributeBindings.add( referencingAttributeBinding );
 	}
 
-	public Set<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
+	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
similarity index 52%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
index 806a06a962..3104c89e83 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
@@ -1,55 +1,47 @@
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
-package org.hibernate.metamodel.source.hbm.state.relational;
+package org.hibernate.metamodel.binding;
 
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
+import org.hibernate.engine.spi.CascadeStyle;
 
 /**
- * @author Gail Badner
+ * Contract describing a binding for attributes which model associations.
+ *
+ * @author Steve Ebersole
  */
-public class HbmManyToOneRelationalStateContainer extends HbmSimpleValueRelationalStateContainer
-implements ManyToOneRelationalState {
-
-	private final boolean isLogicalOneToOne;
-	private final String foreignKeyName;
-
-	public HbmManyToOneRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLManyToOneElement manyToOne ) {
-		super( bindingContext, autoColumnCreation, manyToOne );
-		this.isLogicalOneToOne = manyToOne.isUnique();
-		this.foreignKeyName = manyToOne.getForeignKey();
-	}
-
-	public boolean isLogicalOneToOne() {
-		return isLogicalOneToOne;
-	}
+public interface AssociationAttributeBinding extends AttributeBinding {
+	/**
+	 * Obtain the cascade styles in effect for this association.
+	 *
+	 * @return THe cascade styles.
+	 */
+	public Iterable<CascadeStyle> getCascadeStyles();
 
-	public String getForeignKeyName() {
-		return foreignKeyName;
-	}
+	/**
+	 * Set the cascade styles in effect for this association.
+	 *
+	 * @param cascadeStyles The cascade styles.
+	 */
+	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
index dca2d6cda8..23d0e31dec 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
@@ -1,118 +1,118 @@
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
 
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
 
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
 	 * Obtain the descriptor for the Hibernate {@link org.hibernate.type.Type} for this binding.
 	 * <p/>
 	 * For information about the Java type, query the {@link Attribute} obtained from {@link #getAttribute()}
 	 * instead.
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
 
-	public void addEntityReferencingAttributeBinding(EntityReferencingAttributeBinding attributeBinding);
+	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding attributeBinding);
 
-	public Set<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings();
+	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings();
 
 	public void validate();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 970c12d772..71bc2e7659 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,510 +1,458 @@
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
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.source.MetaAttributeContext;
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
 	private Value<Class<?>> proxyInterfaceType;
 
 	private String jpaEntityName;
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private InheritanceType entityInheritanceType;
 	private EntityBinding superEntityBinding;
 
 	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private EntityDiscriminator entityDiscriminator;
 	private SimpleAttributeBinding versionBinding;
 
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
-	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>();
-	private Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
+
+	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>( );
+	private Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
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
 	private OptimisticLockStyle optimisticLockStyle;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
-//	public EntityBinding initialize(BindingContext bindingContext, EntityDescriptor state) {
-//		// todo : Entity will need both entityName and className to be effective
-//		this.entity = new Entity( state.getEntityName(), state.getSuperType(), bindingContext.makeJavaType( state.getClassName() ) );
-//
-//		this.isRoot = state.isRoot();
-//		this.entityInheritanceType = state.getEntityInheritanceType();
-//
-//		this.entityMode = state.getEntityMode();
-//		this.jpaEntityName = state.getJpaEntityName();
-//
-//		// todo : handle the entity-persister-resolver stuff
-//		this.customEntityPersisterClass = state.getCustomEntityPersisterClass();
-//		this.customEntityTuplizerClass = state.getCustomEntityTuplizerClass();
-//
-//		this.caching = state.getCaching();
-//		this.metaAttributeContext = state.getMetaAttributeContext();
-//
-//		if ( entityMode == EntityMode.POJO ) {
-//			if ( state.getProxyInterfaceName() != null ) {
-//				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
-//				this.lazy = true;
-//			}
-//			else if ( state.isLazy() ) {
-//				this.proxyInterfaceType = entity.getJavaType();
-//				this.lazy = true;
-//			}
-//		}
-//		else {
-//			this.proxyInterfaceType = new JavaType( Map.class );
-//			this.lazy = state.isLazy();
-//		}
-//
-//		this.mutable = state.isMutable();
-//		this.explicitPolymorphism = state.isExplicitPolymorphism();
-//		this.whereFilter = state.getWhereFilter();
-//		this.rowId = state.getRowId();
-//		this.dynamicUpdate = state.isDynamicUpdate();
-//		this.dynamicInsert = state.isDynamicInsert();
-//		this.batchSize = state.getBatchSize();
-//		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
-//		this.optimisticLockMode = state.getOptimisticLockMode();
-//		this.isAbstract = state.isAbstract();
-//		this.customInsert = state.getCustomInsert();
-//		this.customUpdate = state.getCustomUpdate();
-//		this.customDelete = state.getCustomDelete();
-//		if ( state.getSynchronizedTableNames() != null ) {
-//			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
-//				addSynchronizedTable( synchronizedTableName );
-//			}
-//		}
-//		return this;
-//	}
-
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
 		return superEntityBinding == null;
 	}
 
 	public void setInheritanceType(InheritanceType entityInheritanceType) {
 		this.entityInheritanceType = entityInheritanceType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	public void setSuperEntityBinding(EntityBinding superEntityBinding) {
 		this.superEntityBinding = superEntityBinding;
 	}
 
 	public EntityBinding getSuperEntityBinding() {
 		return superEntityBinding;
 	}
 
 	public EntityIdentifier getEntityIdentifier() {
 		return entityIdentifier;
 	}
 
 	public EntityDiscriminator getEntityDiscriminator() {
 		return entityDiscriminator;
 	}
 
 	public boolean isVersioned() {
 		return versionBinding != null;
 	}
 
 	public void setVersionBinding(SimpleAttributeBinding versionBinding) {
 		this.versionBinding = versionBinding;
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
 
-	public Iterable<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
+	public Iterable<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
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
 		binding.setAttribute( attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(Attribute attribute) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this );
 		binding.setAttribute( attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementType collectionElementType) {
 		final BagBinding binding = new BagBinding( this, collectionElementType );
 		binding.setAttribute( attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
-	private void registerAttributeBinding(String name, EntityReferencingAttributeBinding attributeBinding) {
+	private void registerAttributeBinding(String name, SingularAssociationAttributeBinding attributeBinding) {
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
 
 	public Value<Class<?>> getProxyInterfaceType() {
 		return proxyInterfaceType;
 	}
 
 	public void setProxyInterfaceType(Value<Class<?>> proxyInterfaceType) {
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
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
 		this.optimisticLockStyle = optimisticLockStyle;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
index 7848fec079..ce7bb42cbb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
@@ -1,194 +1,194 @@
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
 
 import java.util.Iterator;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.ForeignKey;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
 
 /**
  * TODO : javadoc
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public class ManyToOneAttributeBinding extends SimpleAttributeBinding implements EntityReferencingAttributeBinding {
+public class ManyToOneAttributeBinding extends SimpleAttributeBinding implements SingularAssociationAttributeBinding {
 	private String referencedAttributeName;
 	private String referencedEntityName;
 
 	private boolean isLogicalOneToOne;
 	private String foreignKeyName;
 
 	private AttributeBinding referencedAttributeBinding;
 
 	private Iterable<CascadeStyle> cascadeStyles;
 
 	ManyToOneAttributeBinding(EntityBinding entityBinding) {
 		super( entityBinding, false, false );
 	}
 
 	public final ManyToOneAttributeBinding initialize(ManyToOneAttributeBindingState state) {
 		super.initialize( state );
 		referencedAttributeName = state.getReferencedAttributeName();
 		referencedEntityName = state.getReferencedEntityName();
 		return this;
 	}
 
 	public final ManyToOneAttributeBinding initialize(ManyToOneRelationalState state) {
 		super.initializeValueRelationalState( state );
 		isLogicalOneToOne = state.isLogicalOneToOne();
 		foreignKeyName = state.getForeignKeyName();
 		return this;
 	}
 
 	@Override
 	public final boolean isPropertyReference() {
 		return referencedAttributeName != null;
 	}
 
 	@Override
 	public final String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	@Override
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	@Override
 	public final String getReferencedAttributeName() {
 		return referencedAttributeName;
 	}
 
 	@Override
 	public void setReferencedAttributeName(String referencedEntityAttributeName) {
 		this.referencedAttributeName = referencedEntityAttributeName;
 	}
 
 	@Override
 	public Iterable<CascadeStyle> getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		this.cascadeStyles = cascadeStyles;
 	}
 
 	@Override
 	public final boolean isReferenceResolved() {
 		return referencedAttributeBinding != null;
 	}
 
 	@Override
 	public final void resolveReference(AttributeBinding referencedAttributeBinding) {
 		if ( !referencedEntityName.equals( referencedAttributeBinding.getEntityBinding().getEntity().getName() ) ) {
 			throw new IllegalStateException(
 					"attempt to set EntityBinding with name: [" +
 							referencedAttributeBinding.getEntityBinding().getEntity().getName() +
 							"; entity name should be: " + referencedEntityName
 			);
 		}
 		if ( referencedAttributeName == null ) {
 			referencedAttributeName = referencedAttributeBinding.getAttribute().getName();
 		}
 		else if ( !referencedAttributeName.equals( referencedAttributeBinding.getAttribute().getName() ) ) {
 			throw new IllegalStateException(
 					"Inconsistent attribute name; expected: " + referencedAttributeName +
 							"actual: " + referencedAttributeBinding.getAttribute().getName()
 			);
 		}
 		this.referencedAttributeBinding = referencedAttributeBinding;
 		buildForeignKey();
 	}
 
 	@Override
 	public AttributeBinding getReferencedAttributeBinding() {
 		if ( !isReferenceResolved() ) {
 			throw new IllegalStateException( "Referenced AttributeBiding has not been resolved." );
 		}
 		return referencedAttributeBinding;
 	}
 
 	@Override
 	public final EntityBinding getReferencedEntityBinding() {
 		return referencedAttributeBinding.getEntityBinding();
 	}
 
 	private void buildForeignKey() {
 		// TODO: move this stuff to relational model
 		ForeignKey foreignKey = getValue().getTable()
 				.createForeignKey( referencedAttributeBinding.getValue().getTable(), foreignKeyName );
 		Iterator<SimpleValue> referencingValueIterator = getValues().iterator();
 		Iterator<SimpleValue> targetValueIterator = referencedAttributeBinding.getValues().iterator();
 		while ( referencingValueIterator.hasNext() ) {
 			if ( !targetValueIterator.hasNext() ) {
 				// TODO: improve this message
 				throw new MappingException(
 						"number of values in many-to-one reference is greater than number of values in target"
 				);
 			}
 			SimpleValue referencingValue = referencingValueIterator.next();
 			SimpleValue targetValue = targetValueIterator.next();
 			if ( Column.class.isInstance( referencingValue ) ) {
 				if ( !Column.class.isInstance( targetValue ) ) {
 					// TODO improve this message
 					throw new MappingException( "referencing value is a column, but target is not a column" );
 				}
 				foreignKey.addColumnMapping( Column.class.cast( referencingValue ), Column.class.cast( targetValue ) );
 			}
 			else if ( Column.class.isInstance( targetValue ) ) {
 				// TODO: improve this message
 				throw new MappingException( "referencing value is not a column, but target is a column." );
 			}
 		}
 		if ( targetValueIterator.hasNext() ) {
 			throw new MappingException( "target value has more simple values than referencing value" );
 		}
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public void validate() {
 		// can't check this until both the domain and relational states are initialized...
 		if ( getCascadeTypes().contains( CascadeType.DELETE_ORPHAN ) ) {
 			if ( !isLogicalOneToOne ) {
 				throw new MappingException(
 						"many-to-one attribute [" + getAttribute().getName() + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 		//TODO: validate that the entity reference is resolved
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
index 8e03d1fdb2..c725199c37 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
@@ -1,244 +1,241 @@
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
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.relational.Column;
-import org.hibernate.metamodel.relational.Schema;
-import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
-public class SimpleAttributeBinding extends AbstractAttributeBinding implements KeyValueBinding {
+public class SimpleAttributeBinding extends AbstractAttributeBinding implements SingularAttributeBinding, KeyValueBinding {
 	private boolean insertable;
 	private boolean updatable;
 	private PropertyGeneration generation;
 
 	private String propertyAccessorName;
 	private String unsavedValue;
 
 	private boolean forceNonNullable;
 	private boolean forceUnique;
 	private boolean keyCascadeDeleteEnabled;
 
 	private boolean includedInOptimisticLocking;
 	private MetaAttributeContext metaAttributeContext;
 
 	SimpleAttributeBinding(EntityBinding entityBinding, boolean forceNonNullable, boolean forceUnique) {
 		super( entityBinding );
 		this.forceNonNullable = forceNonNullable;
 		this.forceUnique = forceUnique;
 	}
 
 	public final SimpleAttributeBinding initialize(SimpleAttributeBindingState state) {
 		super.initialize( state );
 		insertable = state.isInsertable();
 		updatable = state.isUpdatable();
 		keyCascadeDeleteEnabled = state.isKeyCascadeDeleteEnabled();
 		unsavedValue = state.getUnsavedValue();
 		generation = state.getPropertyGeneration() == null ? PropertyGeneration.NEVER : state.getPropertyGeneration();
 		return this;
 	}
 
 	public SimpleAttributeBinding initialize(ValueRelationalState state) {
 		super.initializeValueRelationalState( state );
 		return this;
 	}
 
 	private boolean isUnique(ColumnRelationalState state) {
 		return isPrimaryKey() || state.isUnique();
 	}
 
 	@Override
 	public SingularAttribute getAttribute() {
 		return (SingularAttribute) super.getAttribute();
 	}
 
 	@Override
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isInsertable() {
 		return insertable;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public boolean isUpdatable() {
 		return updatable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	@Override
 	public boolean isKeyCascadeDeleteEnabled() {
 		return keyCascadeDeleteEnabled;
 	}
 
 	public void setKeyCascadeDeleteEnabled(boolean keyCascadeDeleteEnabled) {
 		this.keyCascadeDeleteEnabled = keyCascadeDeleteEnabled;
 	}
 
 	@Override
 	public String getUnsavedValue() {
 		return unsavedValue;
 	}
 
 	public void setUnsavedValue(String unsaveValue) {
 		this.unsavedValue = unsaveValue;
 	}
 
 	public boolean forceNonNullable() {
 		return forceNonNullable;
 	}
 
 	public boolean forceUnique() {
 		return forceUnique;
 	}
 
 	public PropertyGeneration getGeneration() {
 		return generation;
 	}
 
 	public void setGeneration(PropertyGeneration generation) {
 		this.generation = generation;
 	}
 
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	public void setPropertyAccessorName(String propertyAccessorName) {
 		this.propertyAccessorName = propertyAccessorName;
 	}
 
 	public boolean isIncludedInOptimisticLocking() {
 		return includedInOptimisticLocking;
 	}
 
 	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
 		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	/* package-protected */
 	IdentifierGenerator createIdentifierGenerator(
 			IdGenerator idGenerator,
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Properties properties) {
 		Properties params = new Properties();
 		params.putAll( properties );
 
 		// use the schema/catalog specified by getValue().getTable() - but note that
 		// if the schema/catalog were specified as params, they will already be initialized and
 		//will override the values set here (they are in idGenerator.getParameters().)
 		Schema schema = getValue().getTable().getSchema();
 		if ( schema != null ) {
 			if ( schema.getName().getSchema() != null ) {
 				params.setProperty( PersistentIdentifierGenerator.SCHEMA, schema.getName().getSchema().getName() );
 			}
 			if ( schema.getName().getCatalog() != null ) {
 				params.setProperty(PersistentIdentifierGenerator.CATALOG, schema.getName().getCatalog().getName() );
 			}
 		}
 
 		// TODO: not sure how this works for collection IDs...
 		//pass the entity-name, if not a collection-id
 		//if ( rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, getEntityBinding().getEntity().getName() );
 		//}
 
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getValue().getTable().getQualifiedName( identifierGeneratorFactory.getDialect() );
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 
 		//pass the column name (a generated id almost always has a single column)
 		if ( getValuesSpan() != 1 ) {
 			throw new MappingException( "A SimpleAttributeBinding has a more than 1 Value: " + getAttribute().getName() );
 		}
 		SimpleValue simpleValue = getValues().iterator().next();
 		if ( ! Column.class.isInstance( simpleValue ) ) {
 			throw new MappingException(
 					"Cannot create an IdentifierGenerator because the value is not a column: " +
 							simpleValue.toLoggableString()
 			);
 		}
 		params.setProperty(
 				PersistentIdentifierGenerator.PK,
 				( ( Column ) simpleValue ).getColumnName().encloseInQuotesIfQuoted(
 						identifierGeneratorFactory.getDialect()
 				)
 		);
 
 		// TODO: is this stuff necessary for SimpleValue???
 		//if (rootClass!=null) {
 		//	StringBuffer tables = new StringBuffer();
 		//	Iterator iter = rootClass.getIdentityTables().iterator();
 		//	while ( iter.hasNext() ) {
 		//		Table table= (Table) iter.next();
 		//		tables.append( table.getQuotedName(dialect) );
 		//		if ( iter.hasNext() ) tables.append(", ");
 		//	}
 		//	params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		//}
 		//else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		//}
 
 		params.putAll( idGenerator.getParameters() );
 
 		return identifierGeneratorFactory.createIdentifierGenerator(
 				idGenerator.getStrategy(), getHibernateTypeDescriptor().getResolvedTypeMapping(), params
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityReferencingAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
similarity index 72%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityReferencingAttributeBinding.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
index 5569b20f45..853d49c4bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityReferencingAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
@@ -1,52 +1,64 @@
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
 package org.hibernate.metamodel.binding;
 
-import org.hibernate.engine.spi.CascadeStyle;
-
 /**
- * TODO : javadoc
+ * Contract describing the attribute binding for singular associations ({@code many-to-one}, {@code one-to-one}).
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public interface EntityReferencingAttributeBinding extends AttributeBinding {
+public interface SingularAssociationAttributeBinding extends SingularAttributeBinding, AssociationAttributeBinding {
+	/**
+	 * Is this association based on a property reference (non PK column(s) as target of FK)?
+	 * <p/>
+	 * Convenience form of checking {@link #getReferencedAttributeName()} for {@code null}.
+	 * 
+	 * @return
+	 */
 	public boolean isPropertyReference();
 
+	/**
+	 * Obtain the name of the referenced entity.
+	 *
+	 * @return The referenced entity name
+	 */
 	public String getReferencedEntityName();
+
+	/**
+	 * Set the name of the
+	 * @param referencedEntityName
+	 */
 	public void setReferencedEntityName(String referencedEntityName);
 
 	public String getReferencedAttributeName();
 	public void setReferencedAttributeName(String referencedAttributeName);
 
-	public Iterable<CascadeStyle> getCascadeStyles();
-	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles);
-
 
 	// "resolvable"
 	public void resolveReference(AttributeBinding attributeBinding);
 	public boolean isReferenceResolved();
 	public EntityBinding getReferencedEntityBinding();
 	public AttributeBinding getReferencedAttributeBinding();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmDerivedValueRelationalState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
similarity index 71%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmDerivedValueRelationalState.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
index f160d074ee..f26c70294e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmDerivedValueRelationalState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
@@ -1,41 +1,30 @@
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
-package org.hibernate.metamodel.source.hbm.state.relational;
-
-import org.hibernate.metamodel.relational.state.DerivedValueRelationalState;
+package org.hibernate.metamodel.binding;
 
 /**
- * @author Gail Badner
+ * @author Steve Ebersole
  */
-public class HbmDerivedValueRelationalState implements DerivedValueRelationalState {
-	private final String formula;
-
-	public HbmDerivedValueRelationalState(String formula) {
-		this.formula = formula.trim();
-	}
-
-	public String getFormula() {
-		return formula;
-	}
+public interface SingularAttributeBinding extends AttributeBinding {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
index 7a57d32920..713c19e73c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
@@ -1,40 +1,40 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import org.hibernate.engine.spi.CascadeStyle;
 
 /**
  * Contract describing sources for attributes which model associations.
  *
  * @author Steve Ebersole
  */
 public interface AssociationAttributeSource extends AttributeSource {
 	/**
 	 * Obtain the cascade styles to be applied to this association.
 	 *
 	 * @return The cascade styles.
 	 */
-	public Iterable<CascadeStyle> getCascadeStyle();
+	public Iterable<CascadeStyle> getCascadeStyles();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index 2eacc9769f..ef61c8b6ac 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,610 +1,612 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.hbm.Helper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * The common binder shared between annotations and {@code hbm.xml} processing.
  * <p/>
  * The API consists of {@link #Binder} and {@link #processEntityHierarchy}
  *
  * @author Steve Ebersole
  */
 public class Binder {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
 	private EntityMode currentHierarchyEntityMode;
 	private LocalBindingContext currentBindingContext;
 
 	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
 	/**
 	 * Process an entity hierarchy.
 	 *
 	 * @param entityHierarchy THe hierarchy to process.
 	 */
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getRootEntitySource(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy.getRootEntitySource(), rootEntityBinding );
 		}
 		currentHierarchyEntityMode = null;
 	}
 
 	private void processHierarchySubEntities(SubclassEntityContainer subclassEntitySource, EntityBinding superEntityBinding) {
 		for ( SubclassEntitySource subEntity : subclassEntitySource.subclassEntitySources() ) {
 			EntityBinding entityBinding = createEntityBinding( subEntity, superEntityBinding );
 			processHierarchySubEntities( subEntity, entityBinding );
 		}
 	}
 
 
 	// Entities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityBinding createEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( processedEntityNames.contains( entitySource.getEntityName() ) ) {
 			return metadata.getEntityBinding( entitySource.getEntityName() );
 		}
 
 		currentBindingContext = entitySource.getBindingContext();
 		try {
 			final EntityBinding entityBinding = doCreateEntityBinding( entitySource, superEntityBinding );
 
 			metadata.addEntity( entityBinding );
 			processedEntityNames.add( entityBinding.getEntity().getName() );
 
 			processFetchProfiles( entitySource, entityBinding );
 
 			return entityBinding;
 		}
 		finally {
 			currentBindingContext = null;
 		}
 	}
 
 	private EntityBinding doCreateEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = createBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindSecondaryTables( entitySource, entityBinding );
 		bindAttributes( entitySource, entityBinding );
 
 		bindTableUniqueConstraints( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( (RootEntitySource) entitySource );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
 				return makeDiscriminatedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
 				return makeJoinedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
 				return makeUnionedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new AssertionFailure( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(RootEntitySource entitySource) {
 		currentHierarchyEntityMode = entitySource.getEntityMode();
 
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, null );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		bindIdentifier( entitySource, entityBinding );
 		bindVersion( entityBinding, entitySource );
 		bindDiscriminator( entitySource, entityBinding );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 		entityBinding.setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 		entityBinding.setCaching( entitySource.getCaching() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setSuperEntityBinding( superEntityBinding );
 		entityBinding.setInheritanceType( currentInheritanceType );
 
 		entityBinding.setEntityMode( currentHierarchyEntityMode );
 
 		final String entityName = entitySource.getEntityName();
 		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
 
 		final Entity entity = new Entity(
 				entityName,
 				className,
 				currentBindingContext.makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 		entityBinding.setJpaEntityName( entitySource.getJpaEntityName() );
 
 		if ( entityBinding.getEntityMode() == EntityMode.POJO ) {
 			final String proxy = entitySource.getProxy();
 			if ( proxy != null ) {
 				entityBinding.setProxyInterfaceType(
 						currentBindingContext.makeClassReference(
 								currentBindingContext.qualifyClassName( proxy )
 						)
 				);
 				entityBinding.setLazy( true );
 			}
 			else if ( entitySource.isLazy() ) {
 				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( null );
 			entityBinding.setLazy( entitySource.isLazy() );
 		}
 
 		final String customTuplizerClassName = entitySource.getCustomTuplizerClassName();
 		if ( customTuplizerClassName != null ) {
 			entityBinding.setCustomEntityTuplizerClass( currentBindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName ) );
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass( currentBindingContext.<EntityPersister>locateClassByName( customPersisterClassName ) );
 		}
 
 		entityBinding.setMetaAttributeContext( buildMetaAttributeContext( entitySource ) );
 
 		entityBinding.setDynamicUpdate( entitySource.isDynamicUpdate() );
 		entityBinding.setDynamicInsert( entitySource.isDynamicInsert() );
 		entityBinding.setBatchSize( entitySource.getBatchSize() );
 		entityBinding.setSelectBeforeUpdate( entitySource.isSelectBeforeUpdate() );
 		entityBinding.setAbstract( entitySource.isAbstract() );
 
 		entityBinding.setCustomLoaderName( entitySource.getCustomLoaderName() );
 		entityBinding.setCustomInsert( entitySource.getCustomSqlInsert() );
 		entityBinding.setCustomUpdate( entitySource.getCustomSqlUpdate() );
 		entityBinding.setCustomDelete( entitySource.getCustomSqlDelete() );
 
 		if ( entitySource.getSynchronizedTableNames() != null ) {
 			entityBinding.addSynchronizedTableNames( entitySource.getSynchronizedTableNames() );
 		}
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeDiscriminatedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
 
 		bindDiscriminatorValue( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeJoinedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : join
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeUnionedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : ??
 
 		return entityBinding;
 	}
 
 
 	// Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
 			if ( attributeSource.isSingular() ) {
 				doBasicSingularAttributeBindingCreation( (SingularAttributeSource) attributeSource, entityBinding );
 			}
 			// todo : components and collections
 		}
 	}
 
 	private void bindIdentifier(RootEntitySource entitySource, EntityBinding entityBinding) {
 		if ( entitySource.getIdentifierSource() == null ) {
 			throw new AssertionFailure( "Expecting identifier information on root entity descriptor" );
 		}
 		switch ( entitySource.getIdentifierSource().getNature() ) {
 			case SIMPLE: {
 				bindSimpleIdentifier( (SimpleIdentifierSource) entitySource.getIdentifierSource(), entityBinding );
 			}
 			case AGGREGATED_COMPOSITE: {
 				// composite id with an actual component class
 			}
 			case COMPOSITE: {
 				// what we used to term an "embedded composite identifier", which is not tobe confused with the JPA
 				// term embedded. Specifically a composite id where there is no component class, though there may
 				// be a @IdClass :/
 			}
 		}
 	}
 
 	private void bindSimpleIdentifier(SimpleIdentifierSource identifierSource, EntityBinding entityBinding) {
 		final SimpleAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getEntityIdentifier().setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
 			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
 					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
 		SimpleAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.setVersionBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private SimpleAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
 			EntityBinding entityBinding) {
 		final SingularAttribute attribute = attributeSource.isVirtualAttribute()
 				? entityBinding.getEntity().locateOrCreateVirtualAttribute( attributeSource.getName() )
 				: entityBinding.getEntity().locateOrCreateSingularAttribute( attributeSource.getName() );
 
 		final SimpleAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
 			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
-			resolveToOneReferenceInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
+			resolveToOneInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
 		attributeBinding.setInsertable( attributeSource.isInsertable() );
 		attributeBinding.setUpdatable( attributeSource.isUpdatable() );
 		attributeBinding.setGeneration( attributeSource.getGeneration() );
 		attributeBinding.setLazy( attributeSource.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 
 		attributeBinding.setPropertyAccessorName(
 				Helper.getPropertyAccessorName(
 						attributeSource.getPropertyAccessorName(),
 						false,
 						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
 				)
 		);
 
 		final org.hibernate.metamodel.relational.Value relationalValue = makeValue( attributeSource, attributeBinding );
 		attributeBinding.setValue( relationalValue );
 
 		attributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), entityBinding.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
 	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute().resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor().getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String,String> parameters = typeSource.getParameters();
 			if ( parameters != null ) {
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( parameters );
 			}
 		}
 		else {
 			if ( attributeJavaType == null ) {
 				// we will have problems later determining the Hibernate Type to use.  Should we throw an
 				// exception now?  Might be better to get better contextual info
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
 		catch ( Exception ignore ) {
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
 
-	private void resolveToOneReferenceInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
+	private void resolveToOneInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
 		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
+
+		attributeBinding.setCascadeStyles( attributeSource.getCascadeStyles() );
 	}
 
 	private MetaAttributeContext buildMetaAttributeContext(EntitySource entitySource) {
 		return buildMetaAttributeContext(
 				entitySource.metaAttributes(),
 				true,
 				currentBindingContext.getMetadataImplementor().getGlobalMetaAttributeContext()
 		);
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			MetaAttributeContext parentContext) {
 		return buildMetaAttributeContext( metaAttributeSources, false, parentContext );
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( MetaAttributeSource metaAttributeSource : metaAttributeSources ) {
 			if ( onlyInheritable & !metaAttributeSource.isInheritable() ) {
 				continue;
 			}
 
 			final String name = metaAttributeSource.getName();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaAttributeSource.getValue() );
 		}
 
 		return subContext;
 	}
 
 
 	// Relational ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindPrimaryTable(EntitySource entitySource, EntityBinding entityBinding) {
 		final TableSource tableSource = entitySource.getPrimaryTable();
 		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
 				? currentBindingContext.getMappingDefaults().getSchemaName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitSchemaName() )
 						: tableSource.getExplicitSchemaName();
 		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
 				? currentBindingContext.getMappingDefaults().getCatalogName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitCatalogName() )
 						: tableSource.getExplicitCatalogName();
 
 		String tableName = tableSource.getExplicitTableName();
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = currentBindingContext.getNamingStrategy()
 					.classToTableName( entityBinding.getEntity().getClassName() );
 		}
 		else {
 			tableName = currentBindingContext.getNamingStrategy().tableName( tableName );
 		}
 		if ( currentBindingContext.isGloballyQuotedIdentifiers() ) {
 			tableName = StringHelper.quote( tableName );
 		}
 
 		final org.hibernate.metamodel.relational.Table table = currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 
 		entityBinding.setBaseTable( table );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private org.hibernate.metamodel.relational.Value makeValue(
 			RelationalValueSourceContainer relationalValueSourceContainer,
 			SimpleAttributeBinding attributeBinding) {
 
 		// todo : to be completely correct, we need to know which table the value belongs to.
 		// 		There is a note about this somewhere else with ideas on the subject.
 		//		For now, just use the entity's base table.
 		final TableSpecification table = attributeBinding.getEntityBinding().getBaseTable();
 
 		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
 			List<SimpleValue> values = new ArrayList<SimpleValue>();
 			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
 					final Column column = table.locateOrCreateColumn( columnSource.getName() );
 					column.setNullable( columnSource.isNullable() );
 					column.setDefaultValue( columnSource.getDefaultValue() );
 					column.setSqlType( columnSource.getSqlType() );
 					column.setSize( columnSource.getSize() );
 					column.setDatatype( columnSource.getDatatype() );
 					column.setReadFragment( columnSource.getReadFragment() );
 					column.setWriteFragment( columnSource.getWriteFragment() );
 					column.setUnique( columnSource.isUnique() );
 					column.setCheckCondition( columnSource.getCheckCondition() );
 					column.setComment( columnSource.getComment() );
 					values.add( column );
 				}
 				else {
 					values.add( table.locateOrCreateDerivedValue( ( (DerivedValueSource) valueSource ).getExpression() ) );
 				}
 			}
 			if ( values.size() == 1 ) {
 				return values.get( 0 );
 			}
 			Tuple tuple = new Tuple( table, null );
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
 			return table.locateOrCreateColumn( name );
 		}
 	}
 
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
index ae412a1742..5733f821bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
@@ -1,327 +1,327 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMetaElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class Helper {
 	static final Iterable<CascadeStyle> NO_CASCADING = Collections.singleton( CascadeStyle.NONE );
 
 	public static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return null;
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	public static InheritanceType interpretInheritanceType(EntityElement entityElement) {
 		if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.SINGLE_TABLE;
 		}
 		else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.JOINED;
 		}
 		else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.TABLE_PER_CLASS;
 		}
 		else {
 			return InheritanceType.NO_INHERITANCE;
 		}
 	}
 
 	/**
 	 * Given a user-specified description of how to perform custom SQL, build the {@link CustomSQL} representation.
 	 *
 	 * @param customSqlElement User-specified description of how to perform custom SQL
 	 *
 	 * @return The {@link CustomSQL} representation
 	 */
 	public static CustomSQL buildCustomSql(CustomSqlElement customSqlElement) {
 		if ( customSqlElement == null ) {
 			return null;
 		}
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlElement.getCheck() == null
 				? customSqlElement.isCallable()
 						? ExecuteUpdateResultCheckStyle.NONE
 						: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.fromExternalName( customSqlElement.getCheck().value() );
 		return new CustomSQL( customSqlElement.getValue(), customSqlElement.isCallable(), checkStyle );
 	}
 
 	/**
 	 * Given the user-specified entity mapping, determine the appropriate entity name
 	 *
 	 * @param entityElement The user-specified entity mapping
 	 * @param unqualifiedClassPackage The package to use for unqualified class names
 	 *
 	 * @return The appropriate entity name
 	 */
 	public static String determineEntityName(EntityElement entityElement, String unqualifiedClassPackage) {
 		return entityElement.getEntityName() != null
 				? entityElement.getEntityName()
 				: qualifyIfNeeded( entityElement.getName(), unqualifiedClassPackage );
 	}
 
 	/**
 	 * Qualify a (supposed class) name with the unqualified-class package name if it is not already qualified
 	 *
 	 * @param name The name
 	 * @param unqualifiedClassPackage The unqualified-class package name
 	 *
 	 * @return {@code null} if the incoming name was {@code null}; or the qualified name.
 	 */
 	public static String qualifyIfNeeded(String name, String unqualifiedClassPackage) {
 		if ( name == null ) {
 			return null;
 		}
 		if ( name.indexOf( '.' ) < 0 && unqualifiedClassPackage != null ) {
 			return unqualifiedClassPackage + '.' + name;
 		}
 		return name;
 	}
 
 	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
 		return getStringValue( access, isEmbedded ? "embedded" : defaultAccess );
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
 
 	public static String getStringValue(String value, String defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static int getIntValue(String value, int defaultValue) {
 		return value == null ? defaultValue : Integer.parseInt( value );
 	}
 
 	public static long getLongValue(String value, long defaultValue) {
 		return value == null ? defaultValue : Long.parseLong( value );
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
 
 	// todo : remove this once the state objects are cleaned up
 
 	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		try {
 			return classLoaderService.classForName( className );
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not find class: " + className );
 		}
 	}
 
 	public static Iterable<CascadeStyle> interpretCascadeStyles(String cascades, LocalBindingContext bindingContext) {
 		final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
 		if ( StringHelper.isEmpty( cascades ) ) {
 			cascades = bindingContext.getMappingDefaults().getCascadeStyle();
 		}
-		for ( String cascade : StringHelper.split( cascades, "," ) ) {
+		for ( String cascade : StringHelper.split( ",", cascades ) ) {
 			cascadeStyles.add( CascadeStyle.getCascadeStyle( cascade ) );
 		}
 		return cascadeStyles;
 	}
 
 	public static Map<String, String> extractParameters(List<XMLParamElement> xmlParamElements) {
 		if ( xmlParamElements == null || xmlParamElements.isEmpty() ) {
 			return null;
 		}
 		final HashMap<String,String> params = new HashMap<String, String>();
 		for ( XMLParamElement paramElement : xmlParamElements ) {
 			params.put( paramElement.getName(), paramElement.getValue() );
 		}
 		return params;
 	}
 
 	public static Iterable<MetaAttributeSource> buildMetaAttributeSources(List<XMLMetaElement> metaElements) {
 		ArrayList<MetaAttributeSource> result = new ArrayList<MetaAttributeSource>();
 		if ( metaElements == null || metaElements.isEmpty() ) {
 			// do nothing
 		}
 		else {
 			for ( final XMLMetaElement metaElement : metaElements ) {
 				result.add(
 						new MetaAttributeSource() {
 							@Override
 							public String getName() {
 								return metaElement.getAttribute();
 							}
 
 							@Override
 							public String getValue() {
 								return metaElement.getValue();
 							}
 
 							@Override
 							public boolean isInheritable() {
 								return metaElement.isInherit();
 							}
 						}
 				);
 			}
 		}
 		return result;
 	}
 
 	public static interface ValueSourcesAdapter {
 		public String getColumnAttribute();
 		public String getFormulaAttribute();
 		public List getColumnOrFormulaElements();
 	}
 
 	public static List<RelationalValueSource> buildValueSources(
 			ValueSourcesAdapter valueSourcesAdapter,
 			LocalBindingContext bindingContext) {
 		List<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
 
 		if ( StringHelper.isNotEmpty( valueSourcesAdapter.getColumnAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column and formula attributes may not be used together",
 						bindingContext.getOrigin()
 				);
 			}
 			result.add(  new ColumnAttributeSourceImpl( valueSourcesAdapter.getColumnAttribute() ) );
 		}
 		else if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			// column/formula attribute combo checked already
 			result.add( new FormulaImpl( valueSourcesAdapter.getFormulaAttribute() ) );
 		}
 		else if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 				&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 			for ( Object columnOrFormulaElement : valueSourcesAdapter.getColumnOrFormulaElements() ) {
 				if ( XMLColumnElement.class.isInstance( columnOrFormulaElement ) ) {
 					result.add( new ColumnSourceImpl( (XMLColumnElement) columnOrFormulaElement ) );
 				}
 				else {
 					result.add( new FormulaImpl( (String) columnOrFormulaElement ) );
 				}
 			}
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
index 870db0c48d..10066304cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
@@ -1,153 +1,153 @@
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
 
 import java.util.List;
 
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.ToOneAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 
 /**
  * Implementation for {@code <many-to-one/> mappings}
  *
  * @author Steve Ebersole
  */
 class ManyToOneAttributeSourceImpl implements ToOneAttributeSource {
 	private final XMLManyToOneElement manyToOneElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	ManyToOneAttributeSourceImpl(final XMLManyToOneElement manyToOneElement, LocalBindingContext bindingContext) {
 		this.manyToOneElement = manyToOneElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return manyToOneElement.getColumn();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return manyToOneElement.getFormula();
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return manyToOneElement.getColumnOrFormula();
 					}
 				},
 				bindingContext
 		);
 	}
 
 	@Override
 	public String getName() {
 			return manyToOneElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return Helper.TO_ONE_ATTRIBUTE_TYPE_SOURCE;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return manyToOneElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return manyToOneElement.isInsert();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return manyToOneElement.isUpdate();
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return PropertyGeneration.NEVER;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return manyToOneElement.isOptimisticLock();
 	}
 
 	@Override
-	public Iterable<CascadeStyle> getCascadeStyle() {
+	public Iterable<CascadeStyle> getCascadeStyles() {
 		return Helper.interpretCascadeStyles( manyToOneElement.getCascade(), bindingContext );
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.MANY_TO_ONE;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( manyToOneElement.getMeta() );
 	}
 
 	@Override
 	public String getReferencedEntityName() {
 		return manyToOneElement.getClazz() != null
 				? manyToOneElement.getClazz()
 				: manyToOneElement.getEntityName();
 	}
 
 	@Override
 	public String getReferencedEntityAttributeName() {
 		return manyToOneElement.getPropertyRef();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
deleted file mode 100644
index 455c2cff79..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
+++ /dev/null
@@ -1,158 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
-
-import java.util.HashSet;
-import java.util.Map;
-import java.util.Set;
-
-import org.hibernate.MappingException;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.source.MappingDefaults;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.binding.state.AttributeBindingState;
-
-/**
- * @author Gail Badner
- */
-public abstract class AbstractHbmAttributeBindingState implements AttributeBindingState {
-	private final String ownerClassName;
-	private final String attributeName;
-	private final BindingContext bindingContext;
-	private final String nodeName;
-	private final String accessorName;
-	private final boolean isOptimisticLockable;
-	private final MetaAttributeContext metaAttributeContext;
-
-	public AbstractHbmAttributeBindingState(
-			String ownerClassName,
-			String attributeName,
-			BindingContext bindingContext,
-			String nodeName,
-			MetaAttributeContext metaAttributeContext,
-			String accessorName,
-			boolean isOptimisticLockable) {
-		if ( attributeName == null ) {
-			throw new MappingException(
-					"Attribute name cannot be null."
-			);
-		}
-
-		this.ownerClassName = ownerClassName;
-		this.attributeName = attributeName;
-		this.bindingContext = bindingContext;
-		this.nodeName = nodeName;
-		this.metaAttributeContext = metaAttributeContext;
-		this.accessorName = accessorName;
-		this.isOptimisticLockable = isOptimisticLockable;
-	}
-
-	// TODO: really don't like this here...
-	protected String getOwnerClassName() {
-		return ownerClassName;
-	}
-
-	protected Set<CascadeType> determineCascadeTypes(String cascade) {
-		String commaSeparatedCascades = Helper.getStringValue(
-				cascade,
-				getBindingContext().getMappingDefaults()
-						.getCascadeStyle()
-		);
-		Set<String> cascades = Helper.getStringValueTokens( commaSeparatedCascades, "," );
-		Set<CascadeType> cascadeTypes = new HashSet<CascadeType>( cascades.size() );
-		for ( String s : cascades ) {
-			CascadeType cascadeType = CascadeType.getCascadeType( s );
-			if ( cascadeType == null ) {
-				throw new MappingException( "Invalid cascading option " + s );
-			}
-			cascadeTypes.add( cascadeType );
-		}
-		return cascadeTypes;
-	}
-
-	protected final String getTypeNameByReflection() {
-		Class ownerClass = bindingContext.locateClassByName( ownerClassName );
-		return ReflectHelper.reflectedPropertyClass( ownerClass, attributeName ).getName();
-	}
-
-	public String getAttributeName() {
-		return attributeName;
-	}
-
-	public BindingContext getBindingContext() {
-		return bindingContext;
-	}
-
-	@Deprecated
-	protected final MappingDefaults getDefaults() {
-		return getBindingContext().getMappingDefaults();
-	}
-
-	@Override
-	public final String getPropertyAccessorName() {
-		return accessorName;
-	}
-
-	@Override
-	public final boolean isAlternateUniqueKey() {
-		//TODO: implement
-		return false;
-	}
-
-	@Override
-	public final boolean isOptimisticLockable() {
-		return isOptimisticLockable;
-	}
-
-	@Override
-	public final String getNodeName() {
-		return nodeName == null ? getAttributeName() : nodeName;
-	}
-
-	@Override
-	public MetaAttributeContext getMetaAttributeContext() {
-		return metaAttributeContext;
-	}
-
-	public PropertyGeneration getPropertyGeneration() {
-		return PropertyGeneration.NEVER;
-	}
-
-	public boolean isKeyCascadeDeleteEnabled() {
-		return false;
-	}
-
-	public String getUnsavedValue() {
-		//TODO: implement
-		return null;
-	}
-
-	public Map<String, String> getExplicitHibernateTypeParameters() {
-		return null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java
deleted file mode 100644
index a04ecd7465..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java
+++ /dev/null
@@ -1,106 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
-
-import java.util.Set;
-
-import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
-
-/**
- * @author Gail Badner
- */
-public class HbmDiscriminatorBindingState
-		extends AbstractHbmAttributeBindingState
-		implements DiscriminatorBindingState {
-	private final String discriminatorValue;
-	private final boolean isForced;
-	private final boolean isInserted;
-
-	private final String explicitHibernateTypeName;
-
-	public HbmDiscriminatorBindingState(
-			String entityName,
-			String ownerClassName,
-			BindingContext bindingContext,
-			XMLHibernateMapping.XMLClass xmlEntityClazz) {
-		super(
-				ownerClassName,
-				bindingContext.getMappingDefaults().getDiscriminatorColumnName(),
-				bindingContext,
-				null,
-				null,
-				null,
-				true
-		);
-		XMLDiscriminator discriminator = xmlEntityClazz.getDiscriminator();
-		this.discriminatorValue =  Helper.getStringValue(
-				xmlEntityClazz.getDiscriminatorValue(), entityName
-		);
-		this.isForced = xmlEntityClazz.getDiscriminator().isForce();
-		this.isInserted = discriminator.isInsert();
-		this.explicitHibernateTypeName = discriminator.getType() == null ? "string" : discriminator.getType();
-	}
-
-	public Set<CascadeType> getCascadeTypes() {
-		return null;
-	}
-
-	protected boolean isEmbedded() {
-		return false;
-	}
-
-	public String getExplicitHibernateTypeName() {
-		return explicitHibernateTypeName;
-	}
-
-	@Override
-	public String getJavaTypeName() {
-		return null;
-	}
-
-	@Override
-	public boolean isLazy() {
-		return false;
-	}
-
-	@Override
-	public boolean isInserted() {
-		return isInserted;
-	}
-
-	@Override
-	public String getDiscriminatorValue() {
-		return discriminatorValue;
-	}
-
-	@Override
-	public boolean isForced() {
-		return isForced;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
deleted file mode 100644
index ff013a9850..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
+++ /dev/null
@@ -1,188 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
-
-import java.util.Set;
-
-import org.hibernate.FetchMode;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
-
-/**
- * @author Gail Badner
- */
-public class HbmManyToOneAttributeBindingState
-		extends AbstractHbmAttributeBindingState
-		implements ManyToOneAttributeBindingState {
-
-	private final FetchMode fetchMode;
-	private final boolean isUnwrapProxy;
-	private final boolean isLazy;
-	private final Set<CascadeType> cascadeTypes;
-	private final boolean isEmbedded;
-	private final String referencedPropertyName;
-	private final String referencedEntityName;
-	private final boolean ignoreNotFound;
-	private final boolean isInsertable;
-	private final boolean isUpdateable;
-
-	public HbmManyToOneAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLManyToOneElement manyToOne) {
-		super(
-				ownerClassName,
-				manyToOne.getName(),
-				bindingContext,
-				manyToOne.getNode(),
-				Helper.extractMetaAttributeContext( manyToOne.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						manyToOne.getAccess(),
-						manyToOne.isEmbedXml(),
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				manyToOne.isOptimisticLock()
-		);
-		fetchMode = getFetchMode( manyToOne );
-		isUnwrapProxy = manyToOne.getLazy() != null && "no-proxy".equals( manyToOne.getLazy().value() );
-		//TODO: better to degrade to lazy="false" if uninstrumented
-		isLazy = manyToOne.getLazy() == null ||
-				isUnwrapProxy ||
-				"proxy".equals( manyToOne.getLazy().value() );
-		cascadeTypes = determineCascadeTypes( manyToOne.getCascade() );
-		isEmbedded = manyToOne.isEmbedXml();
-		referencedEntityName = getReferencedEntityName( ownerClassName, manyToOne, bindingContext );
-		referencedPropertyName = manyToOne.getPropertyRef();
-		ignoreNotFound = "ignore".equals( manyToOne.getNotFound().value() );
-		isInsertable = manyToOne.isInsert();
-		isUpdateable = manyToOne.isUpdate();
-	}
-
-	// TODO: is this needed???
-	protected boolean isEmbedded() {
-		return isEmbedded;
-	}
-
-	private static String getReferencedEntityName(
-			String ownerClassName,
-			XMLManyToOneElement manyToOne,
-			BindingContext bindingContext) {
-		String referencedEntityName;
-		if ( manyToOne.getEntityName() != null ) {
-			referencedEntityName = manyToOne.getEntityName();
-		}
-		else if ( manyToOne.getClazz() != null ) {
-			referencedEntityName = Helper.qualifyIfNeeded(
-					manyToOne.getClazz(), bindingContext.getMappingDefaults().getPackageName()
-			);
-		}
-		else {
-			Class ownerClazz = Helper.classForName( ownerClassName, bindingContext.getServiceRegistry() );
-			referencedEntityName = ReflectHelper.reflectedPropertyClass( ownerClazz, manyToOne.getName() ).getName();
-		}
-		return referencedEntityName;
-	}
-
-	// same as for plural attributes...
-	private static FetchMode getFetchMode(XMLManyToOneElement manyToOne) {
-		FetchMode fetchMode;
-		if ( manyToOne.getFetch() != null ) {
-			fetchMode = "join".equals( manyToOne.getFetch().value() ) ? FetchMode.JOIN : FetchMode.SELECT;
-		}
-		else {
-			String jfNodeValue = ( manyToOne.getOuterJoin() == null ? "auto" : manyToOne.getOuterJoin().value() );
-			if ( "auto".equals( jfNodeValue ) ) {
-				fetchMode = FetchMode.DEFAULT;
-			}
-			else if ( "true".equals( jfNodeValue ) ) {
-				fetchMode = FetchMode.JOIN;
-			}
-			else {
-				fetchMode = FetchMode.SELECT;
-			}
-		}
-		return fetchMode;
-	}
-
-	public String getExplicitHibernateTypeName() {
-		return null;
-	}
-
-	@Override
-	public String getJavaTypeName() {
-		return referencedEntityName;
-	}
-
-	public FetchMode getFetchMode() {
-		return fetchMode;
-	}
-
-	public boolean isLazy() {
-		return isLazy;
-	}
-
-	public boolean isUnwrapProxy() {
-		return isUnwrapProxy;
-	}
-
-	public String getReferencedAttributeName() {
-		return referencedPropertyName;
-	}
-
-	public String getReferencedEntityName() {
-		return referencedEntityName;
-	}
-
-	public Set<CascadeType> getCascadeTypes() {
-		return cascadeTypes;
-	}
-
-	public boolean ignoreNotFound() {
-		return ignoreNotFound;
-	}
-
-	public boolean isInsertable() {
-		return isInsertable;
-	}
-
-	public boolean isUpdatable() {
-		return isUpdateable;
-	}
-
-	public boolean isKeyCascadeDeleteEnabled() {
-		//TODO: implement
-		return false;
-	}
-
-	public String getUnsavedValue() {
-		//TODO: implement
-		return null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java
deleted file mode 100644
index 95b63601a3..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java
+++ /dev/null
@@ -1,291 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
-
-import java.util.Comparator;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Set;
-
-import org.hibernate.FetchMode;
-import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.binding.CustomSQL;
-import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLBagElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlDeleteAllElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlDeleteElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlInsertElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlUpdateElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
-
-/**
- * @author Gail Badner
- */
-public class HbmPluralAttributeBindingState extends AbstractHbmAttributeBindingState
-		implements PluralAttributeBindingState {
-	private final XMLBagElement collection;
-	private final Class collectionPersisterClass;
-	private final Set<CascadeType> cascadeTypes;
-
-	private final String explicitHibernateCollectionTypeName;
-	private final Class javaType;
-
-	public HbmPluralAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLBagElement collection) {
-		super(
-				ownerClassName,
-				collection.getName(),
-				bindingContext,
-				collection.getNode(),
-				Helper.extractMetaAttributeContext( collection.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						collection.getAccess(),
-						collection.isEmbedXml(),
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				collection.isOptimisticLock()
-		);
-		this.collection = collection;
-		this.collectionPersisterClass = Helper.classForName(
-				collection.getPersister(), getBindingContext().getServiceRegistry()
-		);
-		this.cascadeTypes = determineCascadeTypes( collection.getCascade() );
-
-		//Attribute typeNode = collectionElement.attribute( "collection-type" );
-		//if ( typeNode != null ) {
-		// TODO: implement when typedef binding is implemented
-		/*
-		   String typeName = typeNode.getValue();
-		   TypeDef typeDef = mappings.getTypeDef( typeName );
-		   if ( typeDef != null ) {
-			   collectionBinding.setTypeName( typeDef.getTypeClass() );
-			   collectionBinding.setTypeParameters( typeDef.getParameters() );
-		   }
-		   else {
-			   collectionBinding.setTypeName( typeName );
-		   }
-		   */
-		//}
-		this.explicitHibernateCollectionTypeName = collection.getCollectionType();
-		this.javaType = java.util.Collection.class;
-	}
-
-	public FetchMode getFetchMode() {
-		FetchMode fetchMode;
-		if ( collection.getFetch() != null ) {
-			fetchMode = "join".equals( collection.getFetch().value() ) ? FetchMode.JOIN : FetchMode.SELECT;
-		}
-		else {
-			String jfNodeValue = ( collection.getOuterJoin().value() == null ? "auto" : collection.getOuterJoin()
-					.value() );
-			if ( "auto".equals( jfNodeValue ) ) {
-				fetchMode = FetchMode.DEFAULT;
-			}
-			else if ( "true".equals( jfNodeValue ) ) {
-				fetchMode = FetchMode.JOIN;
-			}
-			else {
-				fetchMode = FetchMode.SELECT;
-			}
-		}
-		return fetchMode;
-	}
-
-	public boolean isLazy() {
-		return isExtraLazy() ||
-				Helper.getBooleanValue(
-						collection.getLazy().value(), getBindingContext().getMappingDefaults().areAssociationsLazy()
-				);
-	}
-
-	public boolean isExtraLazy() {
-		return ( "extra".equals( collection.getLazy().value() ) );
-	}
-
-	public String getElementTypeName() {
-		return collection.getElement().getTypeAttribute();
-
-	}
-
-	public String getElementNodeName() {
-		return collection.getElement().getNode();
-	}
-
-	public boolean isInverse() {
-		return collection.isInverse();
-	}
-
-	public boolean isMutable() {
-		return collection.isMutable();
-	}
-
-	public boolean isSubselectLoadable() {
-		return "subselect".equals( collection.getFetch().value() );
-	}
-
-	public String getCacheConcurrencyStrategy() {
-		return collection.getCache() == null ?
-				null :
-				collection.getCache().getUsage();
-	}
-
-	public String getCacheRegionName() {
-		return collection.getCache() == null ?
-				null :
-				collection.getCache().getRegion();
-	}
-
-	public String getOrderBy() {
-		return collection.getOrderBy();
-	}
-
-	public String getWhere() {
-		return collection.getWhere();
-	}
-
-	public String getReferencedPropertyName() {
-		return collection.getKey().getPropertyRef();
-	}
-
-	public boolean isSorted() {
-		// SORT
-		// unsorted, natural, comparator.class.name
-		return ( !"unsorted".equals( getSortString() ) );
-	}
-
-	public Comparator getComparator() {
-		return null;
-	}
-
-	public String getComparatorClassName() {
-		String sortString = getSortString();
-		return (
-				isSorted() && !"natural".equals( sortString ) ?
-						sortString :
-						null
-		);
-	}
-
-	private String getSortString() {
-		//TODO: Bag does not define getSort(); update this when there is a Collection subtype
-		// collection.getSort() == null ? "unsorted" : collection.getSort();
-		return "unsorted";
-	}
-
-	public boolean isOrphanDelete() {
-		// ORPHAN DELETE (used for programmer error detection)
-		return true;
-		//return ( getCascade().indexOf( "delete-orphan" ) >= 0 );
-	}
-
-	public int getBatchSize() {
-		return Helper.getIntValue( collection.getBatchSize(), 0 );
-	}
-
-	@Override
-	public boolean isEmbedded() {
-		return collection.isEmbedXml();
-	}
-
-	public boolean isOptimisticLocked() {
-		return collection.isOptimisticLock();
-	}
-
-	public Class getCollectionPersisterClass() {
-		return collectionPersisterClass;
-	}
-
-	public java.util.Map getFilters() {
-		// TODO: IMPLEMENT
-		//Iterator iter = collectionElement.elementIterator( "filter" );
-		//while ( iter.hasNext() ) {
-		//	final Element filter = (Element) iter.next();
-		//	parseFilter( filter, collectionElement, collectionBinding );
-		//}
-		return new HashMap();
-	}
-
-	public java.util.Set getSynchronizedTables() {
-		java.util.Set<String> synchronizedTables = new HashSet<String>();
-		for ( XMLSynchronizeElement sync : collection.getSynchronize() ) {
-			synchronizedTables.add( sync.getTable() );
-		}
-		return synchronizedTables;
-	}
-
-	public CustomSQL getCustomSQLInsert() {
-		XMLSqlInsertElement sqlInsert = collection.getSqlInsert();
-		return Helper.buildCustomSql( sqlInsert );
-	}
-
-	public CustomSQL getCustomSQLUpdate() {
-		XMLSqlUpdateElement sqlUpdate = collection.getSqlUpdate();
-		return Helper.buildCustomSql( sqlUpdate );
-	}
-
-	public CustomSQL getCustomSQLDelete() {
-		XMLSqlDeleteElement sqlDelete = collection.getSqlDelete();
-		return Helper.buildCustomSql( sqlDelete );
-	}
-
-	public CustomSQL getCustomSQLDeleteAll() {
-		XMLSqlDeleteAllElement sqlDeleteAll = collection.getSqlDeleteAll();
-		return Helper.buildCustomSql( sqlDeleteAll );
-	}
-
-	public String getLoaderName() {
-		return collection.getLoader() == null ?
-				null :
-				collection.getLoader().getQueryRef();
-	}
-
-	public Set<CascadeType> getCascadeTypes() {
-		return cascadeTypes;
-	}
-
-	public boolean isKeyCascadeDeleteEnabled() {
-		//TODO: implement
-		return false;
-	}
-
-	public String getUnsavedValue() {
-		//TODO: implement
-		return null;
-	}
-
-	public String getExplicitHibernateTypeName() {
-		return explicitHibernateCollectionTypeName;
-	}
-
-	@Override
-	public String getJavaTypeName() {
-		return javaType.getName();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
deleted file mode 100644
index 95fd4571bd..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
+++ /dev/null
@@ -1,340 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
-
-import java.beans.BeanInfo;
-import java.beans.PropertyDescriptor;
-import java.util.HashMap;
-import java.util.Map;
-import java.util.Set;
-
-import org.hibernate.MappingException;
-import org.hibernate.internal.util.beans.BeanInfoHelper;
-import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.source.MappingDefaults;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLId;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
-
-/**
- * @author Gail Badner
- */
-public class HbmSimpleAttributeBindingState extends AbstractHbmAttributeBindingState
-		implements SimpleAttributeBindingState {
-
-	private final String explicitHibernateTypeName;
-	private final Map<String, String> explicitHibernateTypeParameters = new HashMap<String, String>();
-
-	private final boolean isLazy;
-	private final PropertyGeneration propertyGeneration;
-	private final boolean isInsertable;
-	private final boolean isUpdatable;
-
-	public HbmSimpleAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLId id) {
-		super(
-				ownerClassName,
-				id.getName() != null ? id.getName() : bindingContext.getMappingDefaults().getIdColumnName(),
-				bindingContext,
-				id.getNode(),
-				Helper.extractMetaAttributeContext( id.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						id.getAccess(),
-						false,
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				true
-		);
-
-		this.isLazy = false;
-		if ( id.getTypeAttribute() != null ) {
-			explicitHibernateTypeName = maybeConvertToTypeDefName( id.getTypeAttribute(), bindingContext.getMappingDefaults() );
-		}
-		else if ( id.getType() != null ) {
-			explicitHibernateTypeName = maybeConvertToTypeDefName( id.getType().getName(), bindingContext.getMappingDefaults() );
-		}
-		else {
-			explicitHibernateTypeName = getTypeNameByReflection();
-		}
-
-		// TODO: how should these be set???
-		this.propertyGeneration = PropertyGeneration.parse( null );
-		this.isInsertable = true;
-
-		this.isUpdatable = false;
-	}
-
-	private static String maybeConvertToTypeDefName(String typeName, MappingDefaults defaults) {
-		String actualTypeName = typeName;
-		if ( typeName != null ) {
-			// TODO: tweak for typedef...
-		}
-		else {
-		}
-		return actualTypeName;
-	}
-
-	public HbmSimpleAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLVersion version) {
-		super(
-				ownerClassName,
-				version.getName(),
-				bindingContext,
-				version.getNode(),
-				Helper.extractMetaAttributeContext( version.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						version.getAccess(),
-						false,
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				true
-		);
-		this.explicitHibernateTypeName = version.getType() == null ? "integer" : version.getType();
-
-		this.isLazy = false;
-
-		// for version properties marked as being generated, make sure they are "always"
-		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
-		// but just to make sure.
-		this.propertyGeneration = PropertyGeneration.parse( version.getGenerated().value() );
-		if ( propertyGeneration == PropertyGeneration.INSERT ) {
-			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
-		}
-		this.isInsertable = Helper.getBooleanValue( version.isInsert(), true );
-		this.isUpdatable = true;
-	}
-
-	public HbmSimpleAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLTimestamp timestamp) {
-
-		super(
-				ownerClassName,
-				timestamp.getName(),
-				bindingContext,
-				timestamp.getNode(),
-				Helper.extractMetaAttributeContext( timestamp.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						timestamp.getAccess(),
-						false,
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				true
-		);
-
-		// Timestamp.getType() is not defined
-		this.explicitHibernateTypeName = "db".equals( timestamp.getSource() ) ? "dbtimestamp" : "timestamp";
-		this.isLazy = false;
-
-		// for version properties marked as being generated, make sure they are "always"
-		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
-		// but just to make sure.
-		this.propertyGeneration = PropertyGeneration.parse( timestamp.getGenerated().value() );
-		if ( propertyGeneration == PropertyGeneration.INSERT ) {
-			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
-		}
-		this.isInsertable = true; //TODO: is this right????
-		this.isUpdatable = true;
-	}
-
-	public HbmSimpleAttributeBindingState(
-			String ownerClassName,
-			BindingContext bindingContext,
-			MetaAttributeContext parentMetaAttributeContext,
-			XMLPropertyElement property) {
-		super(
-				ownerClassName,
-				property.getName(),
-				bindingContext,
-				property.getNode(),
-				Helper.extractMetaAttributeContext( property.getMeta(), parentMetaAttributeContext ),
-				Helper.getPropertyAccessorName(
-						property.getAccess(),
-						false,
-						bindingContext.getMappingDefaults().getPropertyAccessorName()
-				),
-				property.isOptimisticLock()
-		);
-		this.isLazy = property.isLazy();
-		this.propertyGeneration = PropertyGeneration.parse( property.getGenerated() );
-
-		if ( propertyGeneration == PropertyGeneration.ALWAYS || propertyGeneration == PropertyGeneration.INSERT ) {
-			// generated properties can *never* be insertable.
-			if ( property.isInsert() != null && property.isInsert() ) {
-				// the user specifically supplied insert="true", which constitutes an illegal combo
-				throw new MappingException(
-						"cannot specify both insert=\"true\" and generated=\"" + propertyGeneration.getName() +
-								"\" for property: " +
-								property.getName()
-				);
-			}
-			isInsertable = false;
-		}
-		else {
-			isInsertable = Helper.getBooleanValue( property.isInsert(), true );
-		}
-		if ( propertyGeneration == PropertyGeneration.ALWAYS ) {
-			if ( property.isUpdate() != null && property.isUpdate() ) {
-				// the user specifically supplied update="true",
-				// which constitutes an illegal combo
-				throw new MappingException(
-						"cannot specify both update=\"true\" and generated=\"" + propertyGeneration.getName() +
-								"\" for property: " +
-								property.getName()
-				);
-			}
-			isUpdatable = false;
-		}
-		else {
-			isUpdatable = Helper.getBooleanValue( property.isUpdate(), true );
-		}
-
-		if ( property.getTypeAttribute() != null ) {
-			explicitHibernateTypeName = maybeConvertToTypeDefName( property.getTypeAttribute(), bindingContext.getMappingDefaults() );
-		}
-		else if ( property.getType() != null ) {
-			explicitHibernateTypeName = maybeConvertToTypeDefName( property.getType().getName(), bindingContext.getMappingDefaults() );
-			for ( XMLParamElement typeParameter : property.getType().getParam() ) {
-				//TODO: add parameters from typedef
-				explicitHibernateTypeParameters.put( typeParameter.getName(), typeParameter.getValue().trim() );
-			}
-		}
-		else {
-			explicitHibernateTypeName = getTypeNameByReflection();
-		}
-
-
-		// TODO: check for typedef first
-		/*
-		TypeDef typeDef = mappings.getTypeDef( typeName );
-		if ( typeDef != null ) {
-			typeName = typeDef.getTypeClass();
-			// parameters on the property mapping should
-			// override parameters in the typedef
-			Properties allParameters = new Properties();
-			allParameters.putAll( typeDef.getParameters() );
-			allParameters.putAll( parameters );
-			parameters = allParameters;
-		}
-        */
-	}
-
-	protected boolean isEmbedded() {
-		return false;
-	}
-
-	private String javaType;
-
-	@Override
-	public String getJavaTypeName() {
-		if ( javaType == null ) {
-			javaType = tryToResolveAttributeJavaType();
-		}
-		return javaType;
-	}
-
-	private String tryToResolveAttributeJavaType() {
-		try {
-			Class ownerClass = getBindingContext().locateClassByName( super.getOwnerClassName() );
-			AttributeLocatorDelegate delegate = new AttributeLocatorDelegate( getAttributeName() );
-			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
-			return delegate.attributeTypeName;
-		}
-		catch (Exception ignore) {
-		}
-		return null;
-	}
-
-	private static class AttributeLocatorDelegate implements BeanInfoHelper.BeanInfoDelegate {
-		private final String attributeName;
-		private String attributeTypeName;
-
-		private AttributeLocatorDelegate(String attributeName) {
-			this.attributeName = attributeName;
-		}
-
-		@Override
-		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
-			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
-				if ( propertyDescriptor.getName().equals( attributeName ) ) {
-					attributeTypeName = propertyDescriptor.getPropertyType().getName();
-					break;
-				}
-			}
-		}
-	}
-
-	public String getExplicitHibernateTypeName() {
-		return explicitHibernateTypeName;
-	}
-
-	public Map<String, String> getExplicitHibernateTypeParameters() {
-		return explicitHibernateTypeParameters;
-	}
-
-	public boolean isLazy() {
-		return isLazy;
-	}
-
-	public PropertyGeneration getPropertyGeneration() {
-		return propertyGeneration;
-	}
-
-	public boolean isInsertable() {
-		return isInsertable;
-	}
-
-	public boolean isUpdatable() {
-		return isUpdatable;
-	}
-
-	public Set<CascadeType> getCascadeTypes() {
-		return null;
-	}
-
-	public boolean isKeyCascadeDeleteEnabled() {
-		//TODO: implement
-		return false;
-	}
-
-	public String getUnsavedValue() {
-		//TODO: implement
-		return null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
deleted file mode 100644
index ddffd88f5d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
+++ /dev/null
@@ -1,276 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.relational;
-
-import java.util.Set;
-
-import org.hibernate.MappingException;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.source.hbm.Helper;
-import org.hibernate.metamodel.relational.Size;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLId;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.relational.state.ColumnRelationalState;
-
-// TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
-
-/**
- * @author Gail Badner
- */
-public class HbmColumnRelationalState implements ColumnRelationalState {
-	private final HbmSimpleValueRelationalStateContainer container;
-	private final String explicitColumnName;
-	private final Size size;
-	private final boolean isNullable;
-	private final boolean isUnique;
-	private final String checkCondition;
-	private final String defaultColumnValue;
-	private final String sqlType;
-	private final String customWrite;
-	private final String customRead;
-	private final String comment;
-	private final Set<String> uniqueKeys;
-	private final Set<String> indexes;
-
-	/* package-protected */
-	HbmColumnRelationalState(XMLColumnElement columnElement,
-							 HbmSimpleValueRelationalStateContainer container) {
-		this.container = container;
-		this.explicitColumnName = columnElement.getName();
-		this.size = createSize( columnElement.getLength(), columnElement.getScale(), columnElement.getPrecision() );
-		this.isNullable = !Helper.getBooleanValue( columnElement.isNotNull(), true );
-		this.isUnique = Helper.getBooleanValue( columnElement.isUnique(), true );
-		this.checkCondition = columnElement.getCheck();
-		this.defaultColumnValue = columnElement.getDefault();
-		this.sqlType = columnElement.getSqlType();
-		this.customWrite = columnElement.getWrite();
-		if ( customWrite != null && !customWrite.matches( "[^?]*\\?[^?]*" ) ) {
-			throw new MappingException( "write expression must contain exactly one value placeholder ('?') character" );
-		}
-		this.customRead = columnElement.getRead();
-		this.comment = columnElement.getComment() == null ? null : columnElement.getComment().trim();
-		this.uniqueKeys = Helper.getStringValueTokens( columnElement.getUniqueKey(), ", " );
-		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
-		this.indexes = Helper.getStringValueTokens( columnElement.getIndex(), ", " );
-		this.indexes.addAll( container.getPropertyIndexes() );
-	}
-
-	HbmColumnRelationalState(XMLPropertyElement property,
-							 HbmSimpleValueRelationalStateContainer container) {
-		this.container = container;
-		this.explicitColumnName = property.getName();
-		this.size = createSize( property.getLength(), property.getScale(), property.getPrecision() );
-		this.isUnique = Helper.getBooleanValue( property.isUnique(), true );
-		this.isNullable = !Helper.getBooleanValue( property.isNotNull(), true );
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = Helper.getStringValueTokens( property.getUniqueKey(), ", " );
-		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
-		this.indexes = Helper.getStringValueTokens( property.getIndex(), ", " );
-		this.indexes.addAll( container.getPropertyIndexes() );
-	}
-
-	HbmColumnRelationalState(XMLManyToOneElement manyToOne,
-							 HbmSimpleValueRelationalStateContainer container) {
-		this.container = container;
-		this.explicitColumnName = manyToOne.getName();
-		this.size = new Size();
-		this.isNullable = !Helper.getBooleanValue( manyToOne.isNotNull(), false );
-		this.isUnique = manyToOne.isUnique();
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = Helper.getStringValueTokens( manyToOne.getUniqueKey(), ", " );
-		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
-		this.indexes = Helper.getStringValueTokens( manyToOne.getIndex(), ", " );
-		this.indexes.addAll( container.getPropertyIndexes() );
-	}
-
-	HbmColumnRelationalState(XMLId id,
-							 HbmSimpleValueRelationalStateContainer container) {
-		if ( id.getColumn() != null && !id.getColumn().isEmpty() ) {
-			throw new IllegalArgumentException( "This method should not be called with non-empty id.getColumnElement()" );
-		}
-		this.container = container;
-		this.explicitColumnName = id.getName();
-		this.size = createSize( id.getLength(), null, null );
-		this.isNullable = false;
-		this.isUnique = true;
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = container.getPropertyUniqueKeys();
-		this.indexes = container.getPropertyIndexes();
-	}
-
-	HbmColumnRelationalState(XMLDiscriminator discriminator,
-							 HbmSimpleValueRelationalStateContainer container) {
-		if ( discriminator.getColumn() != null ) {
-			throw new IllegalArgumentException(
-					"This method should not be called with null discriminator.getColumnElement()"
-			);
-		}
-		this.container = container;
-		this.explicitColumnName = null;
-		this.size = createSize( discriminator.getLength(), null, null );
-		this.isNullable = false;
-		this.isUnique = true;
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = container.getPropertyUniqueKeys();
-		this.indexes = container.getPropertyIndexes();
-	}
-
-	HbmColumnRelationalState(XMLVersion version,
-							 HbmSimpleValueRelationalStateContainer container) {
-		this.container = container;
-		this.explicitColumnName = version.getColumnAttribute();
-		if ( version.getColumn() != null && !version.getColumn().isEmpty() ) {
-			throw new IllegalArgumentException(
-					"This method should not be called with non-empty version.getColumnElement()"
-			);
-		}
-		// TODO: should set default
-		this.size = new Size();
-		this.isNullable = false;
-		this.isUnique = false;
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = container.getPropertyUniqueKeys();
-		this.indexes = container.getPropertyIndexes();
-	}
-
-	HbmColumnRelationalState(XMLTimestamp timestamp,
-							 HbmSimpleValueRelationalStateContainer container) {
-		this.container = container;
-		this.explicitColumnName = timestamp.getColumn();
-		// TODO: should set default
-		this.size = new Size();
-		this.isNullable = false;
-		this.isUnique = true; // well, it should hopefully be unique...
-		this.checkCondition = null;
-		this.defaultColumnValue = null;
-		this.sqlType = null;
-		this.customWrite = null;
-		this.customRead = null;
-		this.comment = null;
-		this.uniqueKeys = container.getPropertyUniqueKeys();
-		this.indexes = container.getPropertyIndexes();
-	}
-
-	public NamingStrategy getNamingStrategy() {
-		return container.getNamingStrategy();
-	}
-
-    public boolean isGloballyQuotedIdentifiers(){
-        return  container.isGloballyQuotedIdentifiers();
-    }
-
-	public String getExplicitColumnName() {
-		return explicitColumnName;
-	}
-
-	public Size getSize() {
-		return size;
-	}
-
-	protected static Size createSize(String length, String scale, String precision) {
-		// TODO: should this set defaults if length, scale, precision is not specified?
-		Size size = new Size();
-		if ( length != null ) {
-			size.setLength( Integer.parseInt( length ) );
-		}
-		if ( scale != null ) {
-			size.setScale( Integer.parseInt( scale ) );
-		}
-		if ( precision != null ) {
-			size.setPrecision( Integer.parseInt( precision ) );
-		}
-		// TODO: is there an attribute for lobMultiplier?
-		return size;
-	}
-
-	public boolean isNullable() {
-		return isNullable;
-	}
-
-	public boolean isUnique() {
-		return isUnique;
-	}
-
-	public String getCheckCondition() {
-		return checkCondition;
-	}
-
-	public String getDefault() {
-		return defaultColumnValue;
-	}
-
-	public String getSqlType() {
-		return sqlType;
-	}
-
-	public String getCustomWriteFragment() {
-		return customWrite;
-	}
-
-	public String getCustomReadFragment() {
-		return customRead;
-	}
-
-	public String getComment() {
-		return comment;
-	}
-
-	public Set<String> getUniqueKeys() {
-		return uniqueKeys;
-	}
-
-	public Set<String> getIndexes() {
-		return indexes;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
deleted file mode 100644
index 9954e5e114..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
+++ /dev/null
@@ -1,233 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm.state.relational;
-
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-import java.util.Set;
-
-import org.hibernate.MappingException;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.source.BindingContext;
-import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
-import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
-import org.hibernate.metamodel.relational.state.TupleRelationalState;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLId;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
-
-/**
- * @author Gail Badner
- */
-public class HbmSimpleValueRelationalStateContainer implements TupleRelationalState {
-	private final BindingContext bindingContext;
-	private final Set<String> propertyUniqueKeys;
-	private final Set<String> propertyIndexes;
-	private final List<SimpleValueRelationalState> simpleValueStates;
-	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
-
-	public BindingContext getBindingContext() {
-		return bindingContext;
-	}
-    public boolean isGloballyQuotedIdentifiers(){
-        return getBindingContext().isGloballyQuotedIdentifiers();
-    }
-	public NamingStrategy getNamingStrategy() {
-		return getBindingContext().getNamingStrategy();
-	}
-
-	// TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLId id) {
-		this( bindingContext, id.getColumn() );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( id.getColumn() == null && ! autoColumnCreation ) {
-				throw new MappingException( "No columns to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( id, this ) );
-		}
-		else if ( id.getColumn() != null ) {
-			throw new MappingException( "column attribute may not be used together with <column> subelement" );
-		}
-		this.hibernateTypeDescriptor.setExplicitTypeName( id.getTypeAttribute() );
-	}
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLDiscriminator discriminator) {
-		this( bindingContext, discriminator.getFormula(), discriminator.getColumn() );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( discriminator.getColumn() == null && discriminator.getFormula() == null &&  ! autoColumnCreation ) {
-				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( discriminator, this ) );
-		}
-		else if ( discriminator.getColumn() != null || discriminator.getFormula() != null) {
-			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
-		}
-		this.hibernateTypeDescriptor.setExplicitTypeName(
-				discriminator.getType() == null ?
-						"string" :
-						discriminator.getType()
-		);
-	}
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLVersion version) {
-		this( bindingContext, version.getColumn() );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( version.getColumn() == null && ! autoColumnCreation ) {
-				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( version, this ) );
-		}
-		else if ( version.getColumn() != null ) {
-			throw new MappingException( "column attribute may not be used together with <column> subelement" );
-		}
-		this.hibernateTypeDescriptor.setExplicitTypeName( version.getType() == null ? "integer" : version.getType() );
-	}
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLTimestamp timestamp) {
-		this( bindingContext, null );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( timestamp.getColumn() == null && ! autoColumnCreation ) {
-				throw new MappingException( "No columns to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( timestamp, this ) );
-		}
-		else if ( timestamp.getColumn() != null ) {
-			throw new MappingException( "column attribute may not be used together with <column> subelement" );
-		}
-		this.hibernateTypeDescriptor.setExplicitTypeName(
-				"db".equals( timestamp.getSource() ) ?
-						"dbtimestamp" :
-						"timestamp"
-		);
-	}
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLPropertyElement property) {
-		this( bindingContext, property.getColumnOrFormula() );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( property.getColumn() == null && property.getFormula() == null &&  ! autoColumnCreation ) {
-				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( property, this ) );
-		}
-		else if ( property.getColumn() != null || property.getFormula() != null) {
-			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
-		}
-		this.hibernateTypeDescriptor.setExplicitTypeName( property.getTypeAttribute() );
-	}
-
-	public HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			boolean autoColumnCreation,
-			XMLManyToOneElement manyToOne) {
-		this( bindingContext, manyToOne.getColumnOrFormula() );
-		if ( simpleValueStates.isEmpty() ) {
-			if ( manyToOne.getColumn() == null && manyToOne.getFormula() == null &&  ! autoColumnCreation ) {
-				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
-			}
-			simpleValueStates.add( new HbmColumnRelationalState( manyToOne, this ) );
-		}
-		else if ( manyToOne.getColumn() != null || manyToOne.getFormula() != null) {
-			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
-		}
-	}
-
-	private HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			String formulaElement,
-			XMLColumnElement columnElement) {
-		this( bindingContext,
-				formulaElement != null
-						? Collections.singletonList( formulaElement )
-						: columnElement != null
-								? Collections.singletonList( columnElement )
-								: Collections.<Object>emptyList()
-		);
-	}
-
-	private HbmSimpleValueRelationalStateContainer(
-			BindingContext bindingContext,
-			List mappedColumnsOrFormulas) {
-		this.bindingContext = bindingContext;
-		this.propertyUniqueKeys = Collections.emptySet();
-		this.propertyIndexes = Collections.emptySet();
-		simpleValueStates = new ArrayList<SimpleValueRelationalState>(
-							mappedColumnsOrFormulas == null || mappedColumnsOrFormulas.isEmpty()
-									? 1
-									: mappedColumnsOrFormulas.size()
-		);
-		if ( mappedColumnsOrFormulas != null && ! mappedColumnsOrFormulas.isEmpty() ) {
-			for ( Object mappedColumnOrFormula : mappedColumnsOrFormulas ) {
-				simpleValueStates.add( createColumnOrFormulaRelationalState( this, mappedColumnOrFormula ) );
-			}
-		}
-	}
-
-	private static SimpleValueRelationalState createColumnOrFormulaRelationalState(
-			HbmSimpleValueRelationalStateContainer container,
-			Object columnOrFormula) {
-		if ( XMLColumnElement.class.isInstance( columnOrFormula ) ) {
-			return new HbmColumnRelationalState(
-					XMLColumnElement.class.cast( columnOrFormula ),
-					container
-			);
-		}
-		else if ( String.class.isInstance( columnOrFormula ) ) {
-			return new HbmDerivedValueRelationalState( String.class.cast( columnOrFormula ) );
-		}
-		throw new MappingException( "unknown type of column or formula: " + columnOrFormula.getClass().getName() );
-	}
-
-	public List<SimpleValueRelationalState> getRelationalStates() {
-		return simpleValueStates;
-	}
-
-	Set<String> getPropertyUniqueKeys() {
-		return propertyUniqueKeys;
-	}
-
-	Set<String> getPropertyIndexes() {
-		return propertyIndexes;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java
index 2aa3ace0bb..62d9533079 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java
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
 package org.hibernate.metamodel.source.internal;
 
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.EntityReferencingAttributeBinding;
+import org.hibernate.metamodel.binding.SingularAssociationAttributeBinding;
 
 /**
  * @author Gail Badner
  */
-class EntityReferenceResolver {
+class AssociationResolver {
 
 	private final MetadataImplementor metadata;
 
-	EntityReferenceResolver(MetadataImplementor metadata) {
+	AssociationResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
-			for ( EntityReferencingAttributeBinding attributeBinding :  entityBinding.getEntityReferencingAttributeBindings() ) {
+			for ( SingularAssociationAttributeBinding attributeBinding :  entityBinding.getEntityReferencingAttributeBindings() ) {
 				resolve( attributeBinding );
 			}
 		}
 	}
 
-	private void resolve(EntityReferencingAttributeBinding attributeBinding) {
+	private void resolve(SingularAssociationAttributeBinding attributeBinding) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index c0958549bd..f95a0965fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,603 +1,603 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
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
 
 	private final DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 
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
 		Dialect dialect = metadataSources.getServiceRegistry().getService( JdbcServices.class ).getDialect();
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory( dialect );
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final MetadataSourceProcessor[] metadataSourceProcessors;
 		if ( options.getMetadataSourceProcessingOrder() == MetadataSourceProcessingOrder.HBM_FIRST ) {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new HbmMetadataSourceProcessorImpl( this ),
 					new AnnotationMetadataSourceProcessorImpl( this )
 			};
 		}
 		else {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new AnnotationMetadataSourceProcessorImpl( this ),
 					new HbmMetadataSourceProcessorImpl( this )
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
 
 		prepare( metadataSourceProcessors, metadataSources );
 		bindIndependentMetadata( metadataSourceProcessors, metadataSources );
 		bindTypeDependentMetadata( metadataSourceProcessors, metadataSources );
 		bindMappingMetadata( metadataSourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( metadataSourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
-		new EntityReferenceResolver( this ).resolve();
+		new AssociationResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 		// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
 		new IdentifierGeneratorResolver( this ).resolve();
 	}
 
 	private void prepare(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingDependentMetadata( metadataSources );
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
 	public String qualifyClassName(String name) {
 		return name;
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
 		EntityBinding binding = entityBindingMap.get( entityName );
 		if ( binding == null ) {
 			throw new IllegalStateException( "Unknown entity binding: " + entityName );
 		}
 
 		do {
 			if ( binding.isRoot() ) {
 				return binding;
 			}
 			binding = binding.getSuperEntityBinding();
 		} while ( binding != null );
 
 		throw new AssertionFailure( "Entity binding has no root: " + entityName );
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
 
 	@Override
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	@Override
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
 				.getResolvedTypeMapping();
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
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 8ea115db92..21de2e1f1e 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,183 +1,183 @@
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
 
-		Set<EntityReferencingAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
+		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
-		EntityReferencingAttributeBinding referenceBinding = referenceBindings.iterator().next();
+		SingularAssociationAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
-		Iterator<EntityReferencingAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
+		Iterator<SingularAssociationAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
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
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
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
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
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
