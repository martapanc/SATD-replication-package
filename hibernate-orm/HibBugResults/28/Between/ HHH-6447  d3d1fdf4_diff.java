diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 8b55d06af5..9bb686d8cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,518 +1,512 @@
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
 import org.hibernate.MappingException;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
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
 
-	private boolean isRoot;
 	private InheritanceType entityInheritanceType;
+	private EntityBinding superEntityBinding;
 
 	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private EntityDiscriminator entityDiscriminator;
 	private SimpleAttributeBinding versionBinding;
 
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>( );
 	private Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
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
 
 //	public EntityBinding initialize(BindingContext bindingContext, EntityDescriptor state) {
 //		// todo : Entity will need both entityName and className to be effective
 //		this.entity = new Entity( state.getEntityName(), state.getSuperType(), bindingContext.makeJavaType( state.getClassName() ) );
 //
 //		this.isRoot = state.isRoot();
 //		this.entityInheritanceType = state.getEntityInheritanceType();
 //
 //		this.entityMode = state.getEntityMode();
 //		this.jpaEntityName = state.getJpaEntityName();
 //
 //		// todo : handle the entity-persister-resolver stuff
 //		this.customEntityPersisterClass = state.getCustomEntityPersisterClass();
 //		this.customEntityTuplizerClass = state.getCustomEntityTuplizerClass();
 //
 //		this.caching = state.getCaching();
 //		this.metaAttributeContext = state.getMetaAttributeContext();
 //
 //		if ( entityMode == EntityMode.POJO ) {
 //			if ( state.getProxyInterfaceName() != null ) {
 //				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
 //				this.lazy = true;
 //			}
 //			else if ( state.isLazy() ) {
 //				this.proxyInterfaceType = entity.getJavaType();
 //				this.lazy = true;
 //			}
 //		}
 //		else {
 //			this.proxyInterfaceType = new JavaType( Map.class );
 //			this.lazy = state.isLazy();
 //		}
 //
 //		this.mutable = state.isMutable();
 //		this.explicitPolymorphism = state.isExplicitPolymorphism();
 //		this.whereFilter = state.getWhereFilter();
 //		this.rowId = state.getRowId();
 //		this.dynamicUpdate = state.isDynamicUpdate();
 //		this.dynamicInsert = state.isDynamicInsert();
 //		this.batchSize = state.getBatchSize();
 //		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
 //		this.optimisticLockMode = state.getOptimisticLockMode();
 //		this.isAbstract = state.isAbstract();
 //		this.customInsert = state.getCustomInsert();
 //		this.customUpdate = state.getCustomUpdate();
 //		this.customDelete = state.getCustomDelete();
 //		if ( state.getSynchronizedTableNames() != null ) {
 //			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
 //				addSynchronizedTable( synchronizedTableName );
 //			}
 //		}
 //		return this;
 //	}
 
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
-		return isRoot;
+		return superEntityBinding == null;
 	}
 
-	public void setRoot(boolean isRoot) {
-		this.isRoot = isRoot;
+	public void setInheritanceType(InheritanceType entityInheritanceType) {
+		this.entityInheritanceType = entityInheritanceType;
 	}
 
-	public EntityIdentifier getEntityIdentifier() {
-		return entityIdentifier;
+	public InheritanceType getInheritanceType() {
+		return entityInheritanceType;
 	}
 
-	public void bindEntityIdentifier(SimpleAttributeBinding attributeBinding) {
-		if ( !Column.class.isInstance( attributeBinding.getValue() ) ) {
-			throw new MappingException(
-					"Identifier value must be a Column; instead it is: " + attributeBinding.getValue().getClass()
-			);
-		}
-		entityIdentifier.setValueBinding( attributeBinding );
-		baseTable.getPrimaryKey().addColumn( Column.class.cast( attributeBinding.getValue() ) );
+	public void setSuperEntityBinding(EntityBinding superEntityBinding) {
+		this.superEntityBinding = superEntityBinding;
 	}
 
-	public EntityDiscriminator getEntityDiscriminator() {
-		return entityDiscriminator;
+	public EntityBinding getSuperEntityBinding() {
+		return superEntityBinding;
 	}
 
-	public void setInheritanceType(InheritanceType entityInheritanceType) {
-		this.entityInheritanceType = entityInheritanceType;
+	public EntityIdentifier getEntityIdentifier() {
+		return entityIdentifier;
 	}
 
-	public InheritanceType getInheritanceType() {
-		return entityInheritanceType;
+	public EntityDiscriminator getEntityDiscriminator() {
+		return entityDiscriminator;
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
 
 	public Iterable<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
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
 		binding.setAttribute( attribute);
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	private void registerAttributeBinding(String name, EntityReferencingAttributeBinding attributeBinding) {
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
index acb6229659..7848fec079 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
@@ -1,173 +1,194 @@
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
+import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.ForeignKey;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
 
 /**
  * TODO : javadoc
  *
  * @author Gail Badner
+ * @author Steve Ebersole
  */
 public class ManyToOneAttributeBinding extends SimpleAttributeBinding implements EntityReferencingAttributeBinding {
 	private String referencedAttributeName;
 	private String referencedEntityName;
 
 	private boolean isLogicalOneToOne;
 	private String foreignKeyName;
 
 	private AttributeBinding referencedAttributeBinding;
 
+	private Iterable<CascadeStyle> cascadeStyles;
+
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
 
-	public final boolean isReferenceResolved() {
-		return referencedAttributeBinding != null;
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyles() {
+		return cascadeStyles;
 	}
 
-	public final EntityBinding getReferencedEntityBinding() {
-		if ( !isReferenceResolved() ) {
-			throw new IllegalStateException( "EntityBinding reference has not be referenced." );
-		}
-		// TODO: throw exception if referencedEntityBinding is null?
-		return referencedAttributeBinding.getEntityBinding();
+	@Override
+	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
+		this.cascadeStyles = cascadeStyles;
 	}
 
+	@Override
+	public final boolean isReferenceResolved() {
+		return referencedAttributeBinding != null;
+	}
+
+	@Override
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
 
+	@Override
+	public AttributeBinding getReferencedAttributeBinding() {
+		if ( !isReferenceResolved() ) {
+			throw new IllegalStateException( "Referenced AttributeBiding has not been resolved." );
+		}
+		return referencedAttributeBinding;
+	}
+
+	@Override
+	public final EntityBinding getReferencedEntityBinding() {
+		return referencedAttributeBinding.getEntityBinding();
+	}
+
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
index 3e056ec497..23ce3cc73f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
@@ -1,204 +1,205 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.domain.Superclass;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
 import org.hibernate.metamodel.source.annotation.jaxb.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassHierarchy;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassType;
 import org.hibernate.metamodel.source.annotations.entity.EntityBinder;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.annotations.global.FetchProfileBinder;
 import org.hibernate.metamodel.source.annotations.global.FilterDefBinder;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.global.QueryBinder;
 import org.hibernate.metamodel.source.annotations.global.TableBinder;
 import org.hibernate.metamodel.source.annotations.global.TypeDefBinder;
 import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
 import org.hibernate.metamodel.source.annotations.xml.mocker.EntityMappingsMocker;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 public class AnnotationProcessor implements SourceProcessor {
 	private static final Logger LOG = Logger.getLogger( AnnotationProcessor.class );
 
 	private final MetadataImplementor metadata;
 	private AnnotationBindingContext bindingContext;
 
 	public AnnotationProcessor(MetadataImpl metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( { "unchecked" })
 	public void prepare(MetadataSources sources) {
 		// create a jandex index from the annotated classes
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : sources.getAnnotatedClasses() ) {
 			indexClass( indexer, clazz.getName().replace( '.', '/' ) + ".class" );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : sources.getAnnotatedPackages() ) {
 			indexClass( indexer, packageName.replace( '.', '/' ) + "/package-info.class" );
 		}
 
 		Index index = indexer.complete();
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : sources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			index = parseAndUpdateIndex( mappings, index );
 		}
 
 		if ( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
 			// todo : this needs to move to AnnotationBindingContext
 			// what happens right now is that specifying this in an orm.xml causes it to effect all orm.xmls
 			metadata.setGloballyQuotedIdentifiers( true );
 		}
 		bindingContext = new AnnotationBindingContextImpl( metadata, index );
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
 		assertBindingContextExists();
 		TypeDefBinder.bind( bindingContext );
 	}
 
 	private void assertBindingContextExists() {
 		if ( bindingContext == null ) {
 			throw new AssertionFailure( "The binding context should exist. Has prepare been called!?" );
 		}
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
 		assertBindingContextExists();
 		IdGeneratorBinder.bind( bindingContext );
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		assertBindingContextExists();
 		// need to order our annotated entities into an order we can process
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				bindingContext
 		);
 
 		// now we process each hierarchy one at the time
 		Hierarchical parent = null;
 		for ( ConfiguredClassHierarchy<EntityClass> hierarchy : hierarchies ) {
 			for ( EntityClass entityClass : hierarchy ) {
 				// for classes annotated w/ @Entity we create a EntityBinding
 				if ( ConfiguredClassType.ENTITY.equals( entityClass.getConfiguredClassType() ) ) {
 					LOG.debugf( "Binding entity from annotated class: %s", entityClass.getName() );
 					EntityBinder entityBinder = new EntityBinder( entityClass, parent, bindingContext );
 					EntityBinding binding = entityBinder.bind( processedEntityNames );
 					parent = binding.getEntity();
 				}
 				// for classes annotated w/ @MappedSuperclass we just create the domain instance
 				// the attribute bindings will be part of the first entity subclass
 				else if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( entityClass.getConfiguredClassType() ) ) {
 					parent = new Superclass(
 							entityClass.getName(),
 							entityClass.getName(),
 							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 				// for classes which are not annotated at all we create the NonEntity domain class
 				// todo - not sure whether this is needed. It might be that we don't need this information (HF)
 				else {
 					parent = new NonEntity(
 							entityClass.getName(),
 							entityClass.getName(),
 							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 			}
 		}
 	}
 
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		TableBinder.bind( bindingContext );
 		FetchProfileBinder.bind( bindingContext );
 		QueryBinder.bind( bindingContext );
 		FilterDefBinder.bind( bindingContext );
 	}
 
 	private Index parseAndUpdateIndex(List<JaxbRoot<XMLEntityMappings>> mappings, Index annotationIndex) {
 		List<XMLEntityMappings> list = new ArrayList<XMLEntityMappings>( mappings.size() );
 		for ( JaxbRoot<XMLEntityMappings> jaxbRoot : mappings ) {
 			list.add( jaxbRoot.getRoot() );
 		}
 		return new EntityMappingsMocker( list, annotationIndex, metadata.getServiceRegistry() ).mockNewIndex();
 	}
 
 	private void indexClass(Indexer indexer, String className) {
 		InputStream stream = metadata.getServiceRegistry().getService( ClassLoaderService.class ).locateResourceStream(
 				className
 		);
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
+
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index 6eb07ffe7f..22bdb43b35 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,998 +1,997 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.UnknownInheritanceTypeException;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
 import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.AttributeBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.TupleRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final EntityClass entityClass;
 	private final Hierarchical superType;
 	private final AnnotationBindingContext bindingContext;
 
 	private final Schema.Name schemaName;
 
 	public EntityBinder(EntityClass entityClass, Hierarchical superType, AnnotationBindingContext bindingContext) {
 		this.entityClass = entityClass;
 		this.superType = superType;
 		this.bindingContext = bindingContext;
 		this.schemaName = determineSchemaName();
 	}
 
 	public EntityBinding bind(List<String> processedEntityNames) {
 		if ( processedEntityNames.contains( entityClass.getName() ) ) {
 			return bindingContext.getMetadataImplementor().getEntityBinding( entityClass.getName() );
 		}
 
 		final EntityBinding entityBinding = createEntityBinding();
 
 		bindingContext.getMetadataImplementor().addEntity( entityBinding );
 		processedEntityNames.add( entityBinding.getEntity().getName() );
 
 		return entityBinding;
 	}
 
 	private Schema.Name determineSchemaName() {
 		String schema = bindingContext.getMappingDefaults().getSchemaName();
 		String catalog = bindingContext.getMappingDefaults().getCatalogName();
 
 		final AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			final AnnotationValue schemaValue = tableAnnotation.value( "schema" );
 			if ( schemaValue != null ) {
 				schema = schemaValue.asString();
 			}
 
 			final AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
 			if ( catalogValue != null ) {
 				catalog = catalogValue.asString();
 			}
 		}
 
 		if ( bindingContext.isGloballyQuotedIdentifiers() ) {
 			schema = StringHelper.quote( schema );
 			catalog = StringHelper.quote( catalog );
 		}
 		return new Schema.Name( schema, catalog );
 	}
 
 	private EntityBinding createEntityBinding() {
 		final EntityBinding entityBinding = buildBasicEntityBinding();
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindEmbeddedAttributes( entityBinding );
 
 		bindTableUniqueConstraints( entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding buildBasicEntityBinding() {
 		switch ( entityClass.getInheritanceType() ) {
 			case NO_INHERITANCE: {
 				return doRootEntityBindingCreation();
 			}
 			case SINGLE_TABLE: {
 				return doRootEntityBindingCreation();
 				//return doDiscriminatedSubclassBindingCreation();
 			}
 			case JOINED: {
 				return doJoinedSubclassBindingCreation();
 			}
 			case TABLE_PER_CLASS: {
 				return doUnionSubclassBindingCreation();
 			}
 			default: {
 				throw new UnknownInheritanceTypeException( "Unknown InheritanceType : " + entityClass.getInheritanceType() );
 			}
 		}
 	}
 
 	private EntityBinding doRootEntityBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.NO_INHERITANCE );
-		entityBinding.setRoot( entityClass.isEntityRoot() );
 
 		doBasicEntityBinding( entityBinding );
 
 		// technically the rest of these binds should only apply to root entities, but they are really available on all
 		// because we do not currently subtype EntityBinding
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6400
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 			polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 		}
 		entityBinding.setExplicitPolymorphism( polymorphism == PolymorphismType.EXPLICIT );
 
 		// see HHH-6401
 		OptimisticLockType optimisticLockType = OptimisticLockType.VERSION;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 			optimisticLockType = OptimisticLockType.valueOf(
 					hibernateEntityAnnotation.value( "optimisticLock" )
 							.asEnum()
 			);
 		}
 		entityBinding.setOptimisticLockStyle( OptimisticLockStyle.valueOf( optimisticLockType.name() ) );
 
 		final AnnotationInstance hibernateImmutableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		final boolean mutable = hibernateImmutableAnnotation == null
 				&& hibernateEntityAnnotation != null
 				&& hibernateEntityAnnotation.value( "mutable" ) != null
 				&& hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 		entityBinding.setMutable( mutable );
 
 		final AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		entityBinding.setWhereFilter(
 				whereAnnotation != null && whereAnnotation.value( "clause" ) != null
 						? whereAnnotation.value( "clause" ).asString()
 						: null
 		);
 
 		final AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 		entityBinding.setRowId(
 				rowIdAnnotation != null && rowIdAnnotation.value() != null
 						? rowIdAnnotation.value().asString()
 						: null
 		);
 
 		entityBinding.setCaching( interpretCaching( entityClass, bindingContext ) );
 
 		bindPrimaryTable( entityBinding );
 		bindId( entityBinding );
 
 		if ( entityClass.getInheritanceType() == InheritanceType.SINGLE_TABLE ) {
 			bindDiscriminatorColumn( entityBinding );
 		}
 
 		// todo : version
 
 		return entityBinding;
 	}
 
 	private Caching interpretCaching(ConfiguredClass configuredClass, AnnotationBindingContext bindingContext) {
 		final AnnotationInstance hibernateCacheAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( hibernateCacheAnnotation != null ) {
 			final AccessType accessType = hibernateCacheAnnotation.value( "usage" ) == null
 					? bindingContext.getMappingDefaults().getCacheAccessType()
 					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() )
 					.toAccessType();
 			return new Caching(
 					hibernateCacheAnnotation.value( "region" ) == null
 							? configuredClass.getName()
 							: hibernateCacheAnnotation.value( "region" ).asString(),
 					accessType,
 					hibernateCacheAnnotation.value( "include" ) != null
 							&& "all".equals( hibernateCacheAnnotation.value( "include" ).asString() )
 			);
 		}
 
 		final AnnotationInstance jpaCacheableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( jpaCacheableAnnotation != null && jpaCacheableAnnotation.value() != null ) {
 			cacheable = jpaCacheableAnnotation.value().asBoolean();
 		}
 
 		final boolean doCaching;
 		switch ( bindingContext.getMetadataImplementor().getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				doCaching = true;
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				doCaching = cacheable;
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				doCaching = jpaCacheableAnnotation == null || cacheable;
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				doCaching = false;
 				break;
 			}
 		}
 
 		if ( !doCaching ) {
 			return null;
 		}
 
 		return new Caching(
 				configuredClass.getName(),
 				bindingContext.getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	private EntityBinding doDiscriminatedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.SINGLE_TABLE );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind discriminator-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doJoinedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.JOINED );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind join-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doUnionSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.TABLE_PER_CLASS );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind union-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private void doBasicEntityBinding(EntityBinding entityBinding) {
 		entityBinding.setEntityMode( EntityMode.POJO );
 
 		final Entity entity = new Entity(
 				entityClass.getName(),
 				entityClass.getName(),
 				bindingContext.makeClassReference( entityClass.getName() ),
 				superType
 		);
 		entityBinding.setEntity( entity );
 
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.ENTITY
 		);
 
 		final AnnotationValue explicitJpaEntityName = jpaEntityAnnotation.value( "name" );
 		if ( explicitJpaEntityName == null ) {
 			entityBinding.setJpaEntityName( entityClass.getName() );
 		}
 		else {
 			entityBinding.setJpaEntityName( explicitJpaEntityName.asString() );
 		}
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6397
 		entityBinding.setDynamicInsert(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean()
 		);
 
 		// see HHH-6398
 		entityBinding.setDynamicUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean()
 		);
 
 		// see HHH-6399
 		entityBinding.setSelectBeforeUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean()
 		);
 
 		// Custom sql loader
 		final AnnotationInstance sqlLoaderAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.LOADER
 		);
 		if ( sqlLoaderAnnotation != null ) {
 			entityBinding.setCustomLoaderName( sqlLoaderAnnotation.value( "namedQuery" ).asString() );
 		}
 
 		// Custom sql insert
 		final AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBinding.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		// Custom sql update
 		final AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBinding.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		// Custom sql delete
 		final AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBinding.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		// Batch size
 		final AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 		entityBinding.setBatchSize( batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt() );
 
 		// Proxy generation
 		final boolean lazy;
 		final Value<Class<?>> proxyInterfaceType;
 		final AnnotationInstance hibernateProxyAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		if ( hibernateProxyAnnotation != null ) {
 			lazy = hibernateProxyAnnotation.value( "lazy" ) == null
 					|| hibernateProxyAnnotation.value( "lazy" ).asBoolean();
 			if ( lazy ) {
 				final AnnotationValue proxyClassValue = hibernateProxyAnnotation.value( "proxyClass" );
 				if ( proxyClassValue == null ) {
 					proxyInterfaceType = entity.getClassReferenceUnresolved();
 				}
 				else {
 					proxyInterfaceType = bindingContext.makeClassReference( proxyClassValue.asString() );
 				}
 			}
 			else {
 				proxyInterfaceType = null;
 			}
 		}
 		else {
 			lazy = true;
 			proxyInterfaceType = entity.getClassReferenceUnresolved();
 		}
 		entityBinding.setLazy( lazy );
 		entityBinding.setProxyInterfaceType( proxyInterfaceType );
 
 		// Custom persister
 		final Class<? extends EntityPersister> entityPersisterClass;
 		final AnnotationInstance persisterAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PERSISTER
 		);
 		if ( persisterAnnotation == null || persisterAnnotation.value( "impl" ) == null ) {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				entityPersisterClass = bindingContext.locateClassByName(
 						hibernateEntityAnnotation.value( "persister" )
 								.asString()
 				);
 			}
 			else {
 				entityPersisterClass = null;
 			}
 		}
 		else {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				// todo : error?
 			}
 			entityPersisterClass = bindingContext.locateClassByName( persisterAnnotation.value( "impl" ).asString() );
 		}
 		entityBinding.setCustomEntityPersisterClass( entityPersisterClass );
 
 		// Custom tuplizer
 		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
 		if ( pojoTuplizerAnnotation != null ) {
 			final Class<? extends EntityTuplizer> tuplizerClass =
 					bindingContext.locateClassByName( pojoTuplizerAnnotation.value( "impl" ).asString() );
 			entityBinding.setCustomEntityTuplizerClass( tuplizerClass );
 		}
 
 		// table synchronizations
 		final AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 		if ( synchronizeAnnotation != null ) {
 			final String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			entityBinding.addSynchronizedTableNames( Arrays.asList( tableNames ) );
 		}
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSqlAnnotation) {
 		if ( customSqlAnnotation == null ) {
 			return null;
 		}
 
 		final String sql = customSqlAnnotation.value( "sql" ).asString();
 		final boolean isCallable = customSqlAnnotation.value( "callable" ) != null
 				&& customSqlAnnotation.value( "callable" ).asBoolean();
 
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlAnnotation.value( "check" ) == null
 				? isCallable
 				? ExecuteUpdateResultCheckStyle.NONE
 				: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.valueOf( customSqlAnnotation.value( "check" ).asEnum() );
 
 		return new CustomSQL( sql, isCallable, checkStyle );
 	}
 
 	private void bindPrimaryTable(EntityBinding entityBinding) {
 		final Schema schema = bindingContext.getMetadataImplementor().getDatabase().getSchema( schemaName );
 
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 
 		String tableName = null;
 		if ( tableAnnotation != null ) {
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = bindingContext.getNamingStrategy().tableName( explicitTableName );
 			}
 		}
 
 		// no explicit table name given, let's use the entity name as table name (taking inheritance into consideration
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = bindingContext.getNamingStrategy().classToTableName( entityClass.getClassNameForTable() );
 		}
 
 		if ( bindingContext.isGloballyQuotedIdentifiers() && !Identifier.isQuoted( tableName ) ) {
 			tableName = StringHelper.quote( tableName );
 		}
 		org.hibernate.metamodel.relational.Table table = schema.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private AnnotationInstance locatePojoTuplizerAnnotation() {
 		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.TUPLIZERS
 		);
 		if ( tuplizersAnnotation == null ) {
 			return null;
 		}
 
 		AnnotationInstance[] annotations = JandexHelper.getValue(
 				tuplizersAnnotation,
 				"value",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
 			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
 				return tuplizerAnnotation;
 			}
 		}
 
 		return null;
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				entityClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 		bindSingleMappedAttribute( entityBinding, entityBinding.getEntity(), discriminatorAttribute );
 
 		if ( !( discriminatorAttribute.getColumnValues() instanceof DiscriminatorColumnValues ) ) {
 			throw new AssertionFailure( "Expected discriminator column values" );
 		}
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
 	private void bindUniqueConstraints(AnnotationInstance tableAnnotation, TableSpecification table) {
 		AnnotationValue value = tableAnnotation.value( "uniqueConstraints" );
 		if ( value == null ) {
 			return;
 		}
 		AnnotationInstance[] uniqueConstraints = value.asNestedArray();
 		for ( AnnotationInstance unique : uniqueConstraints ) {
 			String name = unique.value( "name" ).asString();
 			UniqueKey uniqueKey = table.getOrCreateUniqueKey( name );
 			String[] columnNames = unique.value( "columnNames" ).asStringArray();
 			if ( columnNames.length == 0 ) {
 				//todo throw exception?
 			}
 			for ( String columnName : columnNames ) {
 				uniqueKey.addColumn( table.locateOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( entityClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				bindEmbeddedIdAnnotation( entityBinding );
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
 	private void bindEmbeddedIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.EMBEDDED_ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = entityClass.getMappedAttribute( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		SingularAttribute attribute = entityBinding.getEntity().locateOrCreateComponentAttribute( idName );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize(
 				new ColumnRelationalStateImpl(
 						(SimpleAttribute) idAttribute,
 						bindingContext.getMetadataImplementor()
 				)
 		);
 		bindSingleIdGeneratedValue( entityBinding, idName );
 
 		TupleRelationalStateImpl state = new TupleRelationalStateImpl();
 		EmbeddableClass embeddableClass = entityClass.getEmbeddedClasses().get( idName );
 		for ( SimpleAttribute attr : embeddableClass.getSimpleAttributes() ) {
 			state.addValueState( new ColumnRelationalStateImpl( attr, bindingContext.getMetadataImplementor() ) );
 		}
 		attributeBinding.initialize( state );
 		Map<String, String> parameterMap = new HashMap<String, String>( 1 );
 		parameterMap.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
 		IdGenerator generator = new IdGenerator( "NAME", "assigned", parameterMap );
 		entityBinding.getEntityIdentifier().setIdGenerator( generator );
 		// entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		// we know we are dealing w/ a single @Id, but potentially it is defined in a mapped super class
 		ConfiguredClass configuredClass = entityClass;
 		EntityClass superEntity = entityClass.getEntityParent();
 		Hierarchical container = entityBinding.getEntity();
 		Iterator<SimpleAttribute> iter = null;
 		while ( configuredClass != null && configuredClass != superEntity ) {
 			iter = configuredClass.getIdAttributes().iterator();
 			if ( iter.hasNext() ) {
 				break;
 			}
 			configuredClass = configuredClass.getParent();
 			container = container.getSuperType();
 		}
 
 		// if we could not find the attribute our assumptions were wrong
 		if ( iter == null || !iter.hasNext() ) {
 			throw new AnnotationException(
 					String.format(
 							"Unable to find id attribute for class %s",
 							entityClass.getName()
 					)
 			);
 		}
 
 		// now that we have the id attribute we can create the attribute and binding
 		MappedAttribute idAttribute = iter.next();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( idAttribute.getName() );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize(
 				new ColumnRelationalStateImpl(
 						(SimpleAttribute) idAttribute,
 						bindingContext.getMetadataImplementor()
 				)
 		);
 		bindSingleIdGeneratedValue( entityBinding, idAttribute.getName() );
 
 		if ( !attribute.isTypeResolved() ) {
 			attribute.resolveType(
 					bindingContext.makeJavaType(
 							attributeBinding.getHibernateTypeDescriptor()
 									.getJavaTypeName()
 					)
 			);
 		}
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							entityClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValue( generatedValueAnn, "generator", String.class );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = bindingContext.getMetadataImplementor().getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s referring an undefined generator [%s]",
 								entityClass.getName(),
 								idName,
 								generator
 						)
 				);
 			}
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 		GenerationType generationType = JandexHelper.getValueAsEnum(
 				generatedValueAnn,
 				"strategy",
 				GenerationType.class
 		);
 		String strategy = IdGeneratorBinder.generatorType(
 				generationType,
 				bindingContext.getMetadataImplementor().getOptions().useNewIdentifierGenerators()
 		);
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							entityClass.getName(),
 							idName
 					)
 			);
 		}
 		if ( idGenerator == null ) {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 //        entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		// collect attribute overrides as we map the attributes
 		Map<String, AttributeOverride> attributeOverrideMap = new HashMap<String, AttributeOverride>();
 
 		// bind the attributes of this entity
 		AttributeContainer entity = entityBinding.getEntity();
 		bindAttributes( entityBinding, entity, entityClass, attributeOverrideMap );
 
 		// bind potential mapped super class attributes
 		attributeOverrideMap.putAll( entityClass.getAttributeOverrideMap() );
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindAttributes( entityBinding, superTypeContainer, parent, attributeOverrideMap );
 			addNewOverridesToMap( parent, attributeOverrideMap );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void addNewOverridesToMap(ConfiguredClass parent, Map<String, AttributeOverride> attributeOverrideMap) {
 		Map<String, AttributeOverride> overrides = parent.getAttributeOverrideMap();
 		for ( Map.Entry<String, AttributeOverride> entry : overrides.entrySet() ) {
 			if ( !attributeOverrideMap.containsKey( entry.getKey() ) ) {
 				attributeOverrideMap.put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private boolean containsMappedSuperclassAttributes(ConfiguredClass parent) {
 		return parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) );
 	}
 
 	private void bindAttributes(
 			EntityBinding entityBinding,
 			AttributeContainer attributeContainer,
 			ConfiguredClass configuredClass,
 			Map<String, AttributeOverride> attributeOverrideMap) {
 		for ( SimpleAttribute simpleAttribute : configuredClass.getSimpleAttributes() ) {
 			String attributeName = simpleAttribute.getName();
 
 			// if there is a override apply it
 			AttributeOverride override = attributeOverrideMap.get( attributeName );
 			if ( override != null ) {
 				simpleAttribute = SimpleAttribute.createSimpleAttribute( simpleAttribute, override.getColumnValues() );
 			}
 
 			bindSingleMappedAttribute(
 					entityBinding,
 					attributeContainer,
 					simpleAttribute
 			);
 		}
 		for ( AssociationAttribute associationAttribute : configuredClass.getAssociationAttributes() ) {
 			bindAssociationAttribute(
 					entityBinding,
 					attributeContainer,
 					associationAttribute
 			);
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding) {
 		AttributeContainer entity = entityBinding.getEntity();
 		bindEmbeddedAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class embeddables
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindEmbeddedAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void bindEmbeddedAttributes(
 			EntityBinding entityBinding,
 			AttributeContainer attributeContainer,
 			ConfiguredClass configuredClass) {
 		for ( Map.Entry<String, EmbeddableClass> entry : configuredClass.getEmbeddedClasses().entrySet() ) {
 			String attributeName = entry.getKey();
 			EmbeddableClass embeddedClass = entry.getValue();
 			SingularAttribute componentAttribute = attributeContainer.locateOrCreateComponentAttribute( attributeName );
 			// we have to resolve the type, if the attribute was just created
 			if ( !componentAttribute.isTypeResolved() ) {
 				Component c = new Component(
 						attributeName,
 						embeddedClass.getName(),
 						new Value<Class<?>>( embeddedClass.getConfiguredClass() ),
 						null
 				);
 				componentAttribute.resolveType( c );
 			}
 			for ( SimpleAttribute simpleAttribute : embeddedClass.getSimpleAttributes() ) {
 				bindSingleMappedAttribute(
 						entityBinding,
 						componentAttribute.getAttributeContainer(),
 						simpleAttribute
 				);
 			}
 			for ( AssociationAttribute associationAttribute : embeddedClass.getAssociationAttributes() ) {
 				bindAssociationAttribute(
 						entityBinding,
 						componentAttribute.getAttributeContainer(),
 						associationAttribute
 				);
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(
 			EntityBinding entityBinding,
 			AttributeContainer container,
 			AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
 				Attribute attribute = entityBinding.getEntity()
 						.locateOrCreateSingularAttribute( associationAttribute.getName() );
 				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
 						attribute
 				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( entityClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, bindingContext.getMetadataImplementor()
 					);
 					relationalState.addValueState( columnRelationsState );
 				}
 				manyToOneAttributeBinding.initialize( relationalState );
 				break;
 			}
 			default: {
 				// todo
 			}
 		}
 	}
 
 	private void bindSingleMappedAttribute(
 			EntityBinding entityBinding,
 			AttributeContainer container,
 			SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
 		ColumnValues columnValues = simpleAttribute.getColumnValues();
 
 		String attributeName = simpleAttribute.getName();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( attributeName );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attribute );
 			entityDiscriminator.setDiscriminatorValue( ( (DiscriminatorColumnValues) columnValues ).getDiscriminatorValue() );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attribute );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 		}
 
 		attributeBinding.setInsertable( simpleAttribute.isInsertable() );
 		attributeBinding.setUpdatable( simpleAttribute.isUpdatable() );
 		attributeBinding.setGeneration( simpleAttribute.getPropertyGeneration() );
 		attributeBinding.setLazy( simpleAttribute.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( simpleAttribute.isOptimisticLockable() );
 		HibernateTypeDescriptor hibernateTypeDescriptor= attributeBinding.getHibernateTypeDescriptor();
 		hibernateTypeDescriptor.setExplicitTypeName( simpleAttribute.getExplicitHibernateTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( simpleAttribute.getExplicitHibernateTypeParameters() );
 		hibernateTypeDescriptor.setJavaTypeName( simpleAttribute.getJavaType().getName() );
 
 //		attributeBinding.setPropertyAccessorName(
 //				Helper.getPropertyAccessorName(
 //						simpleAttribute.getPropertyAccessorName(),
 //						false,
 //						bindingContext.getMappingDefaults().getPropertyAccessorName()
 //				)
 //		);
 
 		final TableSpecification valueSource = attributeBinding.getEntityBinding().getBaseTable();
 		String columnName = simpleAttribute.getColumnValues()
 				.getName()
 				.isEmpty() ? attribute.getName() : simpleAttribute.getColumnValues().getName();
 		Column column = valueSource.locateOrCreateColumn( columnName );
 		column.setNullable( columnValues.isNullable() );
 		column.setDefaultValue( null ); // todo
 		column.setSqlType( null ); // todo
 		Size size = new Size(
 				columnValues.getPrecision(),
 				columnValues.getScale(),
 				columnValues.getLength(),
 				Size.LobMultiplier.NONE
 		);
 		column.setSize( size );
 		column.setDatatype( null ); // todo : ???
 		column.setReadFragment( simpleAttribute.getCustomReadFragment() );
 		column.setWriteFragment( simpleAttribute.getCustomWriteFragment() );
 		column.setUnique( columnValues.isUnique() );
 		column.setCheckCondition( simpleAttribute.getCheckCondition() );
 		column.setComment( null ); // todo
 
 		attributeBinding.setValue( column );
 
 		if ( ! attribute.isTypeResolved() ) {
 			attribute.resolveType( bindingContext.makeJavaType( attributeBinding.getHibernateTypeDescriptor().getJavaTypeName() ) );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index 284b8655ab..0d713c05f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,508 +1,601 @@
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
+import org.hibernate.internal.util.StringHelper;
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
+import org.hibernate.metamodel.relational.Identifier;
+import org.hibernate.metamodel.relational.Schema;
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
  * @author Steve Ebersole
  */
 public class Binder {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
+	private EntityMode currentHierarchyEntityMode;
 	private LocalBindingContext currentBindingContext;
 
 	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getRootEntitySource(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy.getRootEntitySource(), rootEntityBinding );
 		}
+		currentHierarchyEntityMode = null;
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
+
+			processFetchProfiles( entitySource, entityBinding );
+
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
 
-		bindTableUniqueConstraints( entityBinding );
+		bindTableUniqueConstraints( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( (RootEntitySource) entitySource );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
-				return makeDiscriminatedSubclassBinding( entitySource, superEntityBinding );
+				return makeDiscriminatedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
-				return makeJoinedSubclassBinding( entitySource, superEntityBinding );
+				return makeJoinedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
-				return makeUnionedSubclassBinding( entitySource, superEntityBinding );
+				return makeUnionedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new AssertionFailure( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(RootEntitySource entitySource) {
-		final EntityBinding entityBinding = new EntityBinding();
-		entityBinding.setInheritanceType( currentInheritanceType );
-		entityBinding.setRoot( true );
+		currentHierarchyEntityMode = entitySource.getEntityMode();
 
-		final EntityMode entityMode = entitySource.getEntityMode();
-		entityBinding.setEntityMode( entityMode );
+		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, null );
 
-		final String entityName = entitySource.getEntityName();
-		final String className = entityMode == EntityMode.POJO ? entitySource.getClassName() : null;
-
-		final Entity entity = new Entity(
-				entityName,
-				className,
-				currentBindingContext.makeClassReference( className ),
-				null
-		);
-		entityBinding.setEntity( entity );
-
-		performBasicEntityBind( entitySource, entityBinding );
-
-		bindPrimaryTable( entityBinding, entitySource );
+		bindPrimaryTable( entitySource, entityBinding );
 
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
 
-	private EntityBinding makeDiscriminatedSubclassBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
-		return null;  //To change body of created methods use File | Settings | File Templates.
-	}
 
-	private EntityBinding makeJoinedSubclassBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
-		return null;  //To change body of created methods use File | Settings | File Templates.
-	}
+	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
+		final EntityBinding entityBinding = new EntityBinding();
+		entityBinding.setSuperEntityBinding( superEntityBinding );
+		entityBinding.setInheritanceType( currentInheritanceType );
 
-	private EntityBinding makeUnionedSubclassBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
-		return null;  //To change body of created methods use File | Settings | File Templates.
-	}
+		entityBinding.setEntityMode( currentHierarchyEntityMode );
+
+		final String entityName = entitySource.getEntityName();
+		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
+
+		final Entity entity = new Entity(
+				entityName,
+				className,
+				currentBindingContext.makeClassReference( className ),
+				null
+		);
+		entityBinding.setEntity( entity );
 
-	private void performBasicEntityBind(EntitySource entitySource, EntityBinding entityBinding) {
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
+
+		return entityBinding;
+	}
+
+	private EntityBinding makeDiscriminatedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
+		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
+
+		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
+
+		bindDiscriminatorValue( entitySource, entityBinding );
+
+		return entityBinding;
+	}
+
+	private EntityBinding makeJoinedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
+		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
+
+		bindPrimaryTable( entitySource, entityBinding );
+
+		// todo : join
+
+		return entityBinding;
+	}
+
+	private EntityBinding makeUnionedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
+		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
+
+		bindPrimaryTable( entitySource, entityBinding );
+
+		// todo : ??
+
+		return entityBinding;
+	}
+
+
+	// Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
+		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
+		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
+		// for now, simply assume all columns come from the base table....
+
+		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
+			if ( attributeSource.isSingular() ) {
+				doBasicSingularAttributeBindingCreation( (SingularAttributeSource) attributeSource, entityBinding );
+			}
+			// todo : components and collections
+		}
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
-
+				// composite id with an actual component class
 			}
 			case COMPOSITE: {
+				// what we used to term an "embedded composite identifier", which is not tobe confused with the JPA
+				// term embedded. Specifically a composite id where there is no component class, though there may
+				// be a @IdClass :/
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
-		//To change body of created methods use File | Settings | File Templates.
+		// todo : implement
 	}
 
-	private void bindAttributes(EntitySource entitySource, EntityBinding entityBinding) {
-		for ( AttributeSource attributeSource : entitySource.attributeSources() ) {
-			if ( attributeSource.isSingular() ) {
-				doBasicSingularAttributeBindingCreation( (SingularAttributeSource) attributeSource, entityBinding );
-			}
-			// todo : components and collections
-		}
+	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
+		// todo : implement
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
 			resolveToOneReferenceInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
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
 
 	private void resolveToOneReferenceInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
+		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
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
 
+
+	// Relational ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private void bindPrimaryTable(EntitySource entitySource, EntityBinding entityBinding) {
+		final TableSource tableSource = entitySource.getPrimaryTable();
+		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
+				? currentBindingContext.getMappingDefaults().getSchemaName()
+				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
+						? StringHelper.quote( tableSource.getExplicitSchemaName() )
+						: tableSource.getExplicitSchemaName();
+		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
+				? currentBindingContext.getMappingDefaults().getCatalogName()
+				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
+						? StringHelper.quote( tableSource.getExplicitCatalogName() )
+						: tableSource.getExplicitCatalogName();
+
+		String tableName = tableSource.getExplicitTableName();
+		if ( StringHelper.isEmpty( tableName ) ) {
+			tableName = currentBindingContext.getNamingStrategy()
+					.classToTableName( entityBinding.getEntity().getClassName() );
+		}
+		else {
+			tableName = currentBindingContext.getNamingStrategy().tableName( tableName );
+		}
+		if ( currentBindingContext.isGloballyQuotedIdentifiers() ) {
+			tableName = StringHelper.quote( tableName );
+		}
+
+		final org.hibernate.metamodel.relational.Table table = currentBindingContext.getMetadataImplementor()
+				.getDatabase()
+				.getSchema( new Schema.Name( schemaName, catalogName ) )
+				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
+
+		entityBinding.setBaseTable( table );
+	}
+
+	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
+		// todo : implement
+	}
+
+	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
+		// todo : implement
+	}
+
 	private org.hibernate.metamodel.relational.Value makeValue(
 			RelationValueMetadataSource relationValueMetadataSource,
 			SimpleAttributeBinding attributeBinding) {
 
 		// todo : to be completely correct, we need to know which table the value belongs to.
 		// 		There is a note about this somewhere else with ideas on the subject.
 		//		For now, just use the entity's base table.
 		final TableSpecification table = attributeBinding.getEntityBinding().getBaseTable();
 
 		if ( relationValueMetadataSource.relationalValueSources().size() > 0 ) {
 			List<SimpleValue> values = new ArrayList<SimpleValue>();
 			for ( RelationalValueSource valueSource : relationValueMetadataSource.relationalValueSources() ) {
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
 
+	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
+		// todo : process the entity-local fetch-profile declaration
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
index c2adfe69bd..4a18919c90 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
@@ -1,65 +1,67 @@
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
 
 import java.util.List;
 
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 
 /**
  * @author Steve Ebersole
  */
 public interface EntitySource extends SubclassEntityContainer, AttributeSourceContainer {
 	public Origin getOrigin();
 	public LocalBindingContext getBindingContext();
 
 	public String getEntityName();
 	public String getClassName();
 	public String getJpaEntityName();
 
+	public TableSource getPrimaryTable();
+
     public boolean isAbstract();
     public boolean isLazy();
     public String getProxy();
     public int getBatchSize();
     public boolean isDynamicInsert();
     public boolean isDynamicUpdate();
     public boolean isSelectBeforeUpdate();
 
 	public String getCustomTuplizerClassName();
     public String getCustomPersisterClassName();
 
 	public String getCustomLoaderName();
 	public CustomSQL getCustomSqlInsert();
 	public CustomSQL getCustomSqlUpdate();
 	public CustomSQL getCustomSqlDelete();
 
 	public List<String> getSynchronizedTableNames();
 
 	public Iterable<MetaAttributeSource> metaAttributes();
 
 //	public List<XMLFetchProfileElement> getFetchProfile();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SubclassEntityContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SubclassEntityContainer.java
index ab30fd92ed..a73a9ed283 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SubclassEntityContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SubclassEntityContainer.java
@@ -1,34 +1,35 @@
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
 
 /**
  * Contract for elements within a {@link EntityHierarchy} which can contain sub elements.  Essentially this
  * abstracts that common aspect away from both root and sub entities.
  *
  * @author Steve Ebersole
  */
 public interface SubclassEntityContainer {
+	public void add(SubclassEntitySource subclassEntitySource);
 	public Iterable<SubclassEntitySource> subclassEntitySources();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchySubEntity.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
similarity index 63%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchySubEntity.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
index ba5dc913e7..d1c3112271 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchySubEntity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
@@ -1,43 +1,33 @@
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
-package org.hibernate.metamodel.source.hbm;
-
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+package org.hibernate.metamodel.source.binder;
 
 /**
- * A sub entity within an entity hierarchy.
- * 
  * @author Steve Ebersole
  */
-public class EntityHierarchySubEntity extends AbstractSubEntityContainer {
-	private final EntitySourceInformation entitySourceInformation;
-
-	public EntityHierarchySubEntity(EntityElement entityElement, MappingDocument sourceMappingDocument) {
-		this.entitySourceInformation = new EntitySourceInformation( entityElement, sourceMappingDocument );
-	}
-
-	public EntitySourceInformation getEntitySourceInformation() {
-		return entitySourceInformation;
-	}
+public interface TableSource {
+	public String getExplicitSchemaName();
+	public String getExplicitCatalogName();
+	public String getExplicitTableName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
new file mode 100644
index 0000000000..583cf301d0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
@@ -0,0 +1,245 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.EntityMode;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binding.CustomSQL;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.source.binder.AttributeSource;
+import org.hibernate.metamodel.source.binder.EntitySource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.SubclassEntitySource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractEntitySourceImpl implements EntitySource {
+	private final MappingDocument sourceMappingDocument;
+	private final EntityElement entityElement;
+
+	private List<SubclassEntitySource> subclassEntitySources = new ArrayList<SubclassEntitySource>();
+
+	protected AbstractEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
+		this.sourceMappingDocument = sourceMappingDocument;
+		this.entityElement = entityElement;
+	}
+
+	protected EntityElement entityElement() {
+		return entityElement;
+	}
+
+	protected MappingDocument sourceMappingDocument() {
+		return sourceMappingDocument;
+	}
+
+	@Override
+	public Origin getOrigin() {
+		return sourceMappingDocument.getOrigin();
+	}
+
+	@Override
+	public LocalBindingContext getBindingContext() {
+		return sourceMappingDocument.getMappingLocalBindingContext();
+	}
+
+	@Override
+	public String getEntityName() {
+		return StringHelper.isNotEmpty( entityElement.getEntityName() )
+				? entityElement.getEntityName()
+				: getClassName();
+	}
+
+	@Override
+	public String getClassName() {
+		return getBindingContext().qualifyClassName( entityElement.getName() );
+	}
+
+	@Override
+	public String getJpaEntityName() {
+		return null;
+	}
+
+	@Override
+	public boolean isAbstract() {
+		return Helper.getBooleanValue( entityElement.isAbstract(), false );
+	}
+
+	@Override
+	public boolean isLazy() {
+		return Helper.getBooleanValue( entityElement.isAbstract(), true );
+	}
+
+	@Override
+	public String getProxy() {
+		return entityElement.getProxy();
+	}
+
+	@Override
+	public int getBatchSize() {
+		return Helper.getIntValue( entityElement.getBatchSize(), -1 );
+	}
+
+	@Override
+	public boolean isDynamicInsert() {
+		return entityElement.isDynamicInsert();
+	}
+
+	@Override
+	public boolean isDynamicUpdate() {
+		return entityElement.isDynamicUpdate();
+	}
+
+	@Override
+	public boolean isSelectBeforeUpdate() {
+		return entityElement.isSelectBeforeUpdate();
+	}
+
+	protected EntityMode determineEntityMode() {
+		return StringHelper.isNotEmpty( getClassName() ) ? EntityMode.POJO : EntityMode.MAP;
+	}
+
+	@Override
+	public String getCustomTuplizerClassName() {
+		if ( entityElement.getTuplizer() == null ) {
+			return null;
+		}
+		final EntityMode entityMode = determineEntityMode();
+		for ( XMLTuplizerElement tuplizerElement : entityElement.getTuplizer() ) {
+			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
+				return tuplizerElement.getClazz();
+			}
+		}
+		return null;
+	}
+
+	@Override
+	public String getCustomPersisterClassName() {
+		return getBindingContext().qualifyClassName( entityElement.getPersister() );
+	}
+
+	@Override
+	public String getCustomLoaderName() {
+		return entityElement.getLoader() != null ? entityElement.getLoader().getQueryRef() : null;
+	}
+
+	@Override
+	public CustomSQL getCustomSqlInsert() {
+		return Helper.buildCustomSql( entityElement.getSqlInsert() );
+	}
+
+	@Override
+	public CustomSQL getCustomSqlUpdate() {
+		return Helper.buildCustomSql( entityElement.getSqlUpdate() );
+	}
+
+	@Override
+	public CustomSQL getCustomSqlDelete() {
+		return Helper.buildCustomSql( entityElement.getSqlDelete() );
+	}
+
+	@Override
+	public List<String> getSynchronizedTableNames() {
+		List<String> tableNames = new ArrayList<String>();
+		for ( XMLSynchronizeElement synchronizeElement : entityElement.getSynchronize() ) {
+			tableNames.add( synchronizeElement.getTable() );
+		}
+		return tableNames;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( entityElement.getMeta() );
+	}
+
+	@Override
+	public Iterable<AttributeSource> attributeSources() {
+		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
+		for ( Object attributeElement : entityElement.getPropertyOrManyToOneOrOneToOne() ) {
+			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new PropertyAttributeSourceImpl(
+								XMLPropertyElement.class.cast( attributeElement ),
+								sourceMappingDocument().getMappingLocalBindingContext()
+						)
+				);
+			}
+			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new ManyToOneAttributeSourceImpl(
+								XMLManyToOneElement.class.cast( attributeElement ),
+								sourceMappingDocument().getMappingLocalBindingContext()
+						)
+				);
+			}
+			else if ( XMLOneToOneElement.class.isInstance( attributeElement ) ) {
+				// todo : implement
+			}
+			else if ( XMLAnyElement.class.isInstance( attributeElement ) ) {
+				// todo : implement
+			}
+			else if ( XMLOneToManyElement.class.isInstance( attributeElement ) ) {
+				// todo : implement
+			}
+			else if ( XMLManyToManyElement.class.isInstance( attributeElement ) ) {
+				// todo : implement
+			}
+		}
+		return attributeSources;
+	}
+
+	private EntityHierarchyImpl entityHierarchy;
+
+	public void injectHierarchy(EntityHierarchyImpl entityHierarchy) {
+		this.entityHierarchy = entityHierarchy;
+	}
+
+	@Override
+	public void add(SubclassEntitySource subclassEntitySource) {
+		add( (SubclassEntitySourceImpl) subclassEntitySource );
+	}
+
+	public void add(SubclassEntitySourceImpl subclassEntitySource) {
+		entityHierarchy.processSubclass( subclassEntitySource );
+		subclassEntitySources.add( subclassEntitySource );
+	}
+
+	@Override
+	public Iterable<SubclassEntitySource> subclassEntitySources() {
+		return subclassEntitySources;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractSubEntityContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractSubEntityContainer.java
deleted file mode 100644
index 620122e3ee..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractSubEntityContainer.java
+++ /dev/null
@@ -1,48 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm;
-
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-
-/**
- * @author Steve Ebersole
- */
-public class AbstractSubEntityContainer implements SubEntityContainer {
-	private List<EntityHierarchySubEntity> subEntityDescriptors;
-
-	public void addSubEntityDescriptor(EntityHierarchySubEntity subEntityDescriptor) {
-		if ( subEntityDescriptors == null ) {
-			subEntityDescriptors = new ArrayList<EntityHierarchySubEntity>();
-		}
-		subEntityDescriptors.add( subEntityDescriptor );
-	}
-
-	public Iterable<EntityHierarchySubEntity> subEntityDescriptors() {
-		return subEntityDescriptors == null
-				? Collections.<EntityHierarchySubEntity>emptyList()
-				: subEntityDescriptors;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java
deleted file mode 100644
index 01a9bc5f92..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BindingCreator.java
+++ /dev/null
@@ -1,1882 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm;
-
-import java.beans.BeanInfo;
-import java.beans.PropertyDescriptor;
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.List;
-import java.util.Map;
-import java.util.Set;
-import java.util.Stack;
-
-import org.hibernate.EntityMode;
-import org.hibernate.cache.spi.access.AccessType;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.cfg.NotYetImplementedException;
-import org.hibernate.engine.OptimisticLockStyle;
-import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
-import org.hibernate.internal.util.beans.BeanInfoHelper;
-import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.binding.BagBinding;
-import org.hibernate.metamodel.binding.Caching;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.IdGenerator;
-import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
-import org.hibernate.metamodel.binding.MetaAttribute;
-import org.hibernate.metamodel.binding.SimpleAttributeBinding;
-import org.hibernate.metamodel.binding.TypeDef;
-import org.hibernate.metamodel.domain.Attribute;
-import org.hibernate.metamodel.domain.Entity;
-import org.hibernate.metamodel.domain.SingularAttribute;
-import org.hibernate.metamodel.relational.Column;
-import org.hibernate.metamodel.relational.Datatype;
-import org.hibernate.metamodel.relational.Identifier;
-import org.hibernate.metamodel.relational.Schema;
-import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.metamodel.relational.Size;
-import org.hibernate.metamodel.relational.TableSpecification;
-import org.hibernate.metamodel.relational.Tuple;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.binder.ColumnSource;
-import org.hibernate.metamodel.source.binder.DerivedValueSource;
-import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
-import org.hibernate.metamodel.source.binder.MetaAttributeSource;
-import org.hibernate.metamodel.source.binder.RelationValueMetadataSource;
-import org.hibernate.metamodel.source.binder.RelationalValueSource;
-import org.hibernate.metamodel.source.binder.SingularAttributeNature;
-import org.hibernate.metamodel.source.binder.SingularAttributeSource;
-import org.hibernate.metamodel.source.binder.ToOneAttributeSource;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.JoinElementSource;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLBagElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLDynamicComponentElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLIdbagElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLListElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMapElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMetaElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertiesElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSetElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlDeleteElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlInsertElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlUpdateElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.tuple.entity.EntityTuplizer;
-
-/**
- * @author Steve Ebersole
- */
-public class BindingCreator {
-	private final MetadataImplementor metadata;
-	private final List<String> processedEntityNames;
-
-	private InheritanceType currentInheritanceType;
-	private HbmBindingContext currentBindingContext;
-
-	public BindingCreator(MetadataImplementor metadata, List<String> processedEntityNames) {
-		this.metadata = metadata;
-		this.processedEntityNames = processedEntityNames;
-	}
-
-	// todo : currently this does not allow inheritance across hbm/annotations.  Do we need to?
-
-	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
-		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
-		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getEntitySourceInformation(), null );
-		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
-			processHierarchySubEntities( entityHierarchy, rootEntityBinding );
-		}
-	}
-
-	private void processHierarchySubEntities(SubEntityContainer subEntityContainer, EntityBinding superEntityBinding) {
-		for ( EntityHierarchySubEntity subEntity : subEntityContainer.subEntityDescriptors() ) {
-			EntityBinding entityBinding = createEntityBinding( subEntity.getEntitySourceInformation(), superEntityBinding );
-			processHierarchySubEntities( subEntity, entityBinding );
-		}
-	}
-
-	private EntityBinding createEntityBinding(EntitySourceInformation entitySourceInfo, EntityBinding superEntityBinding) {
-		if ( processedEntityNames.contains( entitySourceInfo.getMappedEntityName() ) ) {
-			return metadata.getEntityBinding( entitySourceInfo.getMappedEntityName() );
-		}
-
-		currentBindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
-		try {
-			final EntityBinding entityBinding = doCreateEntityBinding( entitySourceInfo, superEntityBinding );
-
-			metadata.addEntity( entityBinding );
-			processedEntityNames.add( entityBinding.getEntity().getName() );
-			return entityBinding;
-		}
-		finally {
-			currentBindingContext = null;
-		}
-	}
-
-	private EntityBinding doCreateEntityBinding(EntitySourceInformation entitySourceInfo, EntityBinding superEntityBinding) {
-		final EntityBinding entityBinding = createBasicEntityBinding( entitySourceInfo, superEntityBinding );
-
-		bindAttributes( entitySourceInfo, entityBinding );
-		bindSecondaryTables( entitySourceInfo, entityBinding );
-		bindTableUniqueConstraints( entityBinding );
-
-		return entityBinding;
-	}
-
-	private EntityBinding createBasicEntityBinding(
-			EntitySourceInformation entitySourceInfo,
-			EntityBinding superEntityBinding) {
-		if ( superEntityBinding == null ) {
-			return makeRootEntityBinding( entitySourceInfo );
-		}
-		else {
-			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
-				return makeDiscriminatedSubclassBinding( entitySourceInfo, superEntityBinding );
-			}
-			else if ( currentInheritanceType == InheritanceType.JOINED ) {
-				return makeJoinedSubclassBinding( entitySourceInfo, superEntityBinding );
-			}
-			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
-				return makeUnionedSubclassBinding( entitySourceInfo, superEntityBinding );
-			}
-			else {
-				// extreme internal error!
-				throw new RuntimeException( "Internal condition failure" );
-			}
-		}
-	}
-
-	private EntityBinding makeRootEntityBinding(EntitySourceInformation entitySourceInfo) {
-		final EntityBinding entityBinding = new EntityBinding();
-		// todo : this is actually not correct
-		// 		the problem is that we need to know whether we have mapped subclasses which happens later
-		//		one option would be to simply reset the InheritanceType at that time.
-		entityBinding.setInheritanceType( currentInheritanceType );
-		entityBinding.setRoot( true );
-
-		final XMLHibernateMapping.XMLClass xmlClass = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
-		final String entityName = entitySourceInfo.getMappedEntityName();
-		final String verbatimClassName = xmlClass.getName();
-
-		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
-		entityBinding.setEntityMode( entityMode );
-
-		final String className;
-		if ( entityMode == EntityMode.POJO ) {
-			className = entitySourceInfo.getSourceMappingDocument()
-					.getMappingLocalBindingContext()
-					.qualifyClassName( verbatimClassName );
-		}
-		else {
-			className = null;
-		}
-
-		Entity entity = new Entity(
-				entityName,
-				className,
-				entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().makeClassReference( className ),
-				null
-		);
-		entityBinding.setEntity( entity );
-
-		performBasicEntityBind( entityBinding, entitySourceInfo );
-		bindIdentifier( entityBinding, entitySourceInfo );
-		bindVersion( entityBinding, entitySourceInfo );
-		bindDiscriminator( entityBinding, entitySourceInfo );
-
-		entityBinding.setMutable( xmlClass.isMutable() );
-		entityBinding.setExplicitPolymorphism( "explicit".equals( xmlClass.getPolymorphism() ) );
-		entityBinding.setWhereFilter( xmlClass.getWhere() );
-		entityBinding.setRowId( xmlClass.getRowid() );
-		entityBinding.setOptimisticLockStyle( interpretOptimisticLockStyle( entitySourceInfo ) );
-		entityBinding.setCaching( interpretCaching( entitySourceInfo ) );
-
-		return entityBinding;
-	}
-
-	private OptimisticLockStyle interpretOptimisticLockStyle(EntitySourceInformation entitySourceInfo) {
-		final String optimisticLockModeString = Helper.getStringValue(
-				( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getOptimisticLock(),
-				"version"
-		);
-		try {
-			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
-		}
-		catch (Exception e) {
-			throw new MappingException(
-					"Unknown optimistic-lock value : " + optimisticLockModeString,
-					entitySourceInfo.getSourceMappingDocument().getOrigin()
-			);
-		}
-	}
-
-	private static Caching interpretCaching(EntitySourceInformation entitySourceInfo) {
-		final XMLCacheElement cache = ( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getCache();
-		if ( cache == null ) {
-			return null;
-		}
-		final String region = cache.getRegion() != null ? cache.getRegion() : entitySourceInfo.getMappedEntityName();
-		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
-		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
-		return new Caching( region, accessType, cacheLazyProps );
-	}
-
-	private EntityBinding makeDiscriminatedSubclassBinding(
-			EntitySourceInformation entitySourceInfo,
-			EntityBinding superEntityBinding) {
-		// temporary!!!
-
-		final EntityBinding entityBinding = new EntityBinding();
-		entityBinding.setInheritanceType( InheritanceType.SINGLE_TABLE );
-		bindSuperType( entityBinding, superEntityBinding );
-
-		final String verbatimClassName = entitySourceInfo.getEntityElement().getName();
-
-		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
-		entityBinding.setEntityMode( entityMode );
-
-		final String className;
-		if ( entityMode == EntityMode.POJO ) {
-			className = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().qualifyClassName( verbatimClassName );
-		}
-		else {
-			className = null;
-		}
-
-		final Entity entity = new Entity(
-				entitySourceInfo.getMappedEntityName(),
-				className,
-				entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext().makeClassReference( className ),
-				null
-		);
-		entityBinding.setEntity( entity );
-
-
-		performBasicEntityBind( entityBinding, entitySourceInfo );
-
-		return entityBinding;
-	}
-
-	private EntityBinding makeJoinedSubclassBinding(
-			EntitySourceInformation entitySourceInfo,
-			EntityBinding superEntityBinding) {
-		// temporary!!!
-
-		final EntityBinding entityBinding = new EntityBinding();
-		entityBinding.setInheritanceType( InheritanceType.JOINED );
-		bindSuperType( entityBinding, superEntityBinding );
-
-		final XMLJoinedSubclassElement joinedEntityElement = (XMLJoinedSubclassElement) entitySourceInfo.getEntityElement();
-		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
-
-		final String entityName = bindingContext.determineEntityName( joinedEntityElement );
-		final String verbatimClassName = joinedEntityElement.getName();
-
-		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
-		entityBinding.setEntityMode( entityMode );
-
-		final String className;
-		if ( entityMode == EntityMode.POJO ) {
-			className = bindingContext.qualifyClassName( verbatimClassName );
-		}
-		else {
-			className = null;
-		}
-
-		final Entity entity = new Entity( entityName, className, bindingContext.makeClassReference( className ), null );
-		entityBinding.setEntity( entity );
-
-		performBasicEntityBind( entityBinding, entitySourceInfo );
-
-		return entityBinding;
-	}
-
-	private EntityBinding makeUnionedSubclassBinding(
-			EntitySourceInformation entitySourceInfo,
-			EntityBinding superEntityBinding) {
-		// temporary!!!
-
-		final EntityBinding entityBinding = new EntityBinding();
-		entityBinding.setInheritanceType( InheritanceType.TABLE_PER_CLASS );
-		bindSuperType( entityBinding, superEntityBinding );
-
-		final XMLUnionSubclassElement unionEntityElement = (XMLUnionSubclassElement) entitySourceInfo.getEntityElement();
-		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
-
-		final String entityName = bindingContext.determineEntityName( unionEntityElement );
-		final String verbatimClassName = unionEntityElement.getName();
-
-		final EntityMode entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
-		entityBinding.setEntityMode( entityMode );
-
-		final String className;
-		if ( entityMode == EntityMode.POJO ) {
-			className = bindingContext.qualifyClassName( verbatimClassName );
-		}
-		else {
-			className = null;
-		}
-
-		final Entity entity = new Entity( entityName, className, bindingContext.makeClassReference( className ), null );
-		entityBinding.setEntity( entity );
-
-		performBasicEntityBind( entityBinding, entitySourceInfo );
-
-		return entityBinding;
-	}
-
-	private void bindSuperType(EntityBinding entityBinding, EntityBinding superEntityBinding) {
-//		entityBinding.setSuperEntityBinding( superEntityBinding );
-//		// not sure what to do with the domain model super type...
-	}
-
-	@SuppressWarnings( {"unchecked"})
-	private void performBasicEntityBind(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
-		bindPrimaryTable( entitySourceInfo, entityBinding );
-
-		entityBinding.setJpaEntityName( null );
-
-		final EntityElement entityElement = entitySourceInfo.getEntityElement();
-		final HbmBindingContext bindingContext = entitySourceInfo.getSourceMappingDocument().getMappingLocalBindingContext();
-
-		final String proxy = entityElement.getProxy();
-		final boolean isLazy = entityElement.isLazy() == null
-				? true
-				: entityElement.isLazy();
-		if ( entityBinding.getEntityMode() == EntityMode.POJO ) {
-			if ( proxy != null ) {
-				entityBinding.setProxyInterfaceType(
-						bindingContext.makeClassReference(
-								bindingContext.qualifyClassName( proxy )
-						)
-				);
-				entityBinding.setLazy( true );
-			}
-			else if ( isLazy ) {
-				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
-				entityBinding.setLazy( true );
-			}
-		}
-		else {
-			entityBinding.setProxyInterfaceType( new Value( Map.class ) );
-			entityBinding.setLazy( isLazy );
-		}
-
-		final String customTuplizerClassName = extractCustomTuplizerClassName(
-				entityElement,
-				entityBinding.getEntityMode()
-		);
-		if ( customTuplizerClassName != null ) {
-			entityBinding.setCustomEntityTuplizerClass( bindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName ) );
-		}
-
-		if ( entityElement.getPersister() != null ) {
-			entityBinding.setCustomEntityPersisterClass( bindingContext.<EntityPersister>locateClassByName( entityElement.getPersister() ) );
-		}
-
-		entityBinding.setMetaAttributeContext(
-				Helper.extractMetaAttributeContext(
-						entityElement.getMeta(), true, bindingContext.getMetaAttributeContext()
-				)
-		);
-
-		entityBinding.setDynamicUpdate( entityElement.isDynamicUpdate() );
-		entityBinding.setDynamicInsert( entityElement.isDynamicInsert() );
-		entityBinding.setBatchSize( Helper.getIntValue( entityElement.getBatchSize(), 0 ) );
-		entityBinding.setSelectBeforeUpdate( entityElement.isSelectBeforeUpdate() );
-		entityBinding.setAbstract( entityElement.isAbstract() );
-
-		if ( entityElement.getLoader() != null ) {
-			entityBinding.setCustomLoaderName( entityElement.getLoader().getQueryRef() );
-		}
-
-		final XMLSqlInsertElement sqlInsert = entityElement.getSqlInsert();
-		if ( sqlInsert != null ) {
-			entityBinding.setCustomInsert( Helper.buildCustomSql( sqlInsert ) );
-		}
-
-		final XMLSqlDeleteElement sqlDelete = entityElement.getSqlDelete();
-		if ( sqlDelete != null ) {
-			entityBinding.setCustomDelete( Helper.buildCustomSql( sqlDelete ) );
-		}
-
-		final XMLSqlUpdateElement sqlUpdate = entityElement.getSqlUpdate();
-		if ( sqlUpdate != null ) {
-			entityBinding.setCustomUpdate( Helper.buildCustomSql( sqlUpdate ) );
-		}
-
-		if ( entityElement.getSynchronize() != null ) {
-			for ( XMLSynchronizeElement synchronize : entityElement.getSynchronize() ) {
-				entityBinding.addSynchronizedTable( synchronize.getTable() );
-			}
-		}
-	}
-
-	private String extractCustomTuplizerClassName(EntityElement entityMapping, EntityMode entityMode) {
-		if ( entityMapping.getTuplizer() == null ) {
-			return null;
-		}
-		for ( XMLTuplizerElement tuplizerElement : entityMapping.getTuplizer() ) {
-			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
-				return tuplizerElement.getClazz();
-			}
-		}
-		return null;
-	}
-
-	private void bindPrimaryTable(EntitySourceInformation entitySourceInformation, EntityBinding entityBinding) {
-		final EntityElement entityElement = entitySourceInformation.getEntityElement();
-		final HbmBindingContext bindingContext = entitySourceInformation.getSourceMappingDocument().getMappingLocalBindingContext();
-
-		if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
-			// todo : need to look it up from root entity, or have persister manage it
-		}
-		else {
-			// todo : add mixin interface
-			final String explicitTableName;
-			final String explicitSchemaName;
-			final String explicitCatalogName;
-			if ( XMLHibernateMapping.XMLClass.class.isInstance( entityElement ) ) {
-				explicitTableName = ( (XMLHibernateMapping.XMLClass) entityElement ).getTable();
-				explicitSchemaName = ( (XMLHibernateMapping.XMLClass) entityElement ).getSchema();
-				explicitCatalogName = ( (XMLHibernateMapping.XMLClass) entityElement ).getCatalog();
-			}
-			else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
-				explicitTableName = ( (XMLJoinedSubclassElement) entityElement ).getTable();
-				explicitSchemaName = ( (XMLJoinedSubclassElement) entityElement ).getSchema();
-				explicitCatalogName = ( (XMLJoinedSubclassElement) entityElement ).getCatalog();
-			}
-			else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
-				explicitTableName = ( (XMLUnionSubclassElement) entityElement ).getTable();
-				explicitSchemaName = ( (XMLUnionSubclassElement) entityElement ).getSchema();
-				explicitCatalogName = ( (XMLUnionSubclassElement) entityElement ).getCatalog();
-			}
-			else {
-				// throw up
-				explicitTableName = null;
-				explicitSchemaName = null;
-				explicitCatalogName = null;
-			}
-			final NamingStrategy namingStrategy = bindingContext.getMetadataImplementor()
-					.getOptions()
-					.getNamingStrategy();
-			final String tableName = explicitTableName != null
-					? namingStrategy.tableName( explicitTableName )
-					: namingStrategy.tableName( namingStrategy.classToTableName( entityBinding.getEntity().getName() ) );
-
-			final String schemaName = explicitSchemaName == null
-					? bindingContext.getMappingDefaults().getSchemaName()
-					: explicitSchemaName;
-			final String catalogName = explicitCatalogName == null
-					? bindingContext.getMappingDefaults().getCatalogName()
-					: explicitCatalogName;
-
-			final Schema schema = metadata.getDatabase().getSchema( new Schema.Name( schemaName, catalogName ) );
-			entityBinding.setBaseTable( schema.locateOrCreateTable( Identifier.toIdentifier( tableName ) ) );
-		}
-	}
-
-
-	private Stack<TableSpecification> attributeColumnTableStack = new Stack<TableSpecification>();
-
-	private void bindIdentifier(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
-		final XMLHibernateMapping.XMLClass rootClassElement = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
-		if ( rootClassElement.getId() != null ) {
-			bindSimpleIdentifierAttribute( entityBinding, entitySourceInfo );
-		}
-		else if ( rootClassElement.getCompositeId() != null ) {
-			bindCompositeIdentifierAttribute( entityBinding, entitySourceInfo );
-		}
-	}
-
-	private void bindSimpleIdentifierAttribute(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
-		final XMLHibernateMapping.XMLClass.XMLId idElement = ( (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement() ).getId();
-		final SimpleAttributeBinding idAttributeBinding = doBasicSimpleAttributeBindingCreation(
-				new SingularIdentifierAttributeSource( idElement ),
-				entityBinding
-		);
-
-		entityBinding.getEntityIdentifier().setValueBinding( idAttributeBinding );
-
-		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
-
-		if ( idElement.getGenerator() != null ) {
-			final String generatorName = idElement.getGenerator().getClazz();
-			IdGenerator idGenerator = currentBindingContext.getMetadataImplementor().getIdGenerator( generatorName );
-			if ( idGenerator == null ) {
-				idGenerator = new IdGenerator(
-						entityBinding.getEntity().getName() + generatorName,
-						generatorName,
-						extractParameters( idElement.getGenerator().getParam() )
-				);
-			}
-			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
-		}
-
-		if ( SimpleValue.class.isInstance( relationalValue ) ) {
-			if ( !Column.class.isInstance( relationalValue ) ) {
-				// this should never ever happen..
-				throw new MappingException( "Simple ID is not a column.", currentBindingContext.getOrigin() );
-			}
-			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
-		}
-		else {
-			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
-				if ( Column.class.isInstance( subValue ) ) {
-					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
-				}
-			}
-		}
-	}
-
-	private SimpleAttributeBinding doBasicSimpleAttributeBindingCreation(SingularAttributeSource attributeSource, EntityBinding entityBinding) {
-		final SingularAttribute attribute = entityBinding.getEntity().locateOrCreateSingularAttribute( attributeSource.getName() );
-		final SimpleAttributeBinding attributeBinding;
-		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
-			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
-			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
-		}
-		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
-			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
-			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
-			resolveToOneReferenceInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
-		}
-		else {
-			throw new NotYetImplementedException();
-		}
-
-		attributeBinding.setInsertable( attributeSource.isInsertable() );
-		attributeBinding.setUpdatable( attributeSource.isUpdatable() );
-		attributeBinding.setGeneration( attributeSource.getGeneration() );
-		attributeBinding.setLazy( attributeSource.isLazy() );
-		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
-
-		attributeBinding.setPropertyAccessorName(
-				Helper.getPropertyAccessorName(
-						attributeSource.getPropertyAccessorName(),
-						false,
-						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
-				)
-		);
-
-		final org.hibernate.metamodel.relational.Value relationalValue = makeValue(
-				attributeSource.getValueInformation(), attributeBinding
-		);
-		attributeBinding.setValue( relationalValue );
-
-		attributeBinding.setMetaAttributeContext(
-				extractMetaAttributeContext(
-						attributeSource.metaAttributes(),
-						entityBinding.getMetaAttributeContext()
-				)
-		);
-
-		return attributeBinding;
-	}
-
-	private void bindCompositeIdentifierAttribute(
-			EntityBinding entityBinding,
-			EntitySourceInformation entitySourceInfo) {
-		//To change body of created methods use File | Settings | File Templates.
-	}
-
-	private void bindVersion(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
-		final XMLHibernateMapping.XMLClass rootClassElement = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
-		final XMLHibernateMapping.XMLClass.XMLVersion versionElement = rootClassElement.getVersion();
-		final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement = rootClassElement.getTimestamp();
-
-		if ( versionElement == null && timestampElement == null ) {
-			return;
-		}
-		else if ( versionElement != null && timestampElement != null ) {
-			throw new MappingException( "version and timestamp elements cannot be specified together", currentBindingContext.getOrigin() );
-		}
-
-		final SimpleAttributeBinding attributeBinding;
-		if ( versionElement != null ) {
-			attributeBinding = doBasicSimpleAttributeBindingCreation(
-					new VersionAttributeSource( versionElement ),
-					entityBinding
-			);
-		}
-		else {
-			attributeBinding = doBasicSimpleAttributeBindingCreation(
-					new TimestampAttributeSource( timestampElement ),
-					entityBinding
-			);
-		}
-
-		entityBinding.setVersionBinding( attributeBinding );
-	}
-
-	private void bindDiscriminator(EntityBinding entityBinding, EntitySourceInformation entitySourceInfo) {
-		// discriminator is a tad different in that it is a "virtual attribute" because it does not exist in the
-		// actual domain model.
-		final XMLHibernateMapping.XMLClass rootClassElement = (XMLHibernateMapping.XMLClass) entitySourceInfo.getEntityElement();
-		final XMLHibernateMapping.XMLClass.XMLDiscriminator discriminatorElement = rootClassElement.getDiscriminator();
-		if ( discriminatorElement == null ) {
-			return;
-		}
-
-		// todo ...
-	}
-
-	private void bindAttributes(final EntitySourceInformation entitySourceInformation, EntityBinding entityBinding) {
-		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
-		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
-		// for now, simply assume all columns come from the base table....
-
-		// todo : intg with "attribute source" concept below as means for single hbm/annotation handling
-
-		attributeColumnTableStack.push( entityBinding.getBaseTable() );
-		try {
-			bindAttributes(
-					new AttributeMetadataContainer() {
-						@Override
-						public List<Object> getAttributeElements() {
-							return entitySourceInformation.getEntityElement().getPropertyOrManyToOneOrOneToOne();
-						}
-					},
-					entityBinding
-			);
-		}
-		finally {
-			attributeColumnTableStack.pop();
-		}
-
-	}
-
-	private void bindAttributes(AttributeMetadataContainer attributeMetadataContainer, EntityBinding entityBinding) {
-		for ( Object attribute : attributeMetadataContainer.getAttributeElements() ) {
-
-			if ( XMLPropertyElement.class.isInstance( attribute ) ) {
-				XMLPropertyElement property = XMLPropertyElement.class.cast( attribute );
-				bindProperty( property, entityBinding );
-			}
-			else if ( XMLManyToOneElement.class.isInstance( attribute ) ) {
-				XMLManyToOneElement manyToOne = XMLManyToOneElement.class.cast( attribute );
-				bindManyToOne( manyToOne, entityBinding );
-			}
-			else if ( XMLOneToOneElement.class.isInstance( attribute ) ) {
-// todo : implement
-// value = new OneToOne( mappings, table, persistentClass );
-// bindOneToOne( subElement, (OneToOne) value, propertyName, true, mappings );
-			}
-			else if ( XMLBagElement.class.isInstance( attribute ) ) {
-				XMLBagElement bagElement = XMLBagElement.class.cast( attribute );
-				bindBag( bagElement, entityBinding );
-			}
-			else if ( XMLIdbagElement.class.isInstance( attribute ) ) {
-				XMLIdbagElement collection = XMLIdbagElement.class.cast( attribute );
-//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-//bindIdbag( collection, bagBinding, entityBinding, PluralAttributeNature.BAG, collection.getName() );
-// todo: handle identifier
-//attributeBinding = collectionBinding;
-//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLSetElement.class.isInstance( attribute ) ) {
-				XMLSetElement collection = XMLSetElement.class.cast( attribute );
-//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-//bindSet( collection, collectionBinding, entityBinding, PluralAttributeNature.SET, collection.getName() );
-//attributeBinding = collectionBinding;
-//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLListElement.class.isInstance( attribute ) ) {
-				XMLListElement collection = XMLListElement.class.cast( attribute );
-//ListBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-//bindList( collection, bagBinding, entityBinding, PluralAttributeNature.LIST, collection.getName() );
-// todo : handle list index
-//attributeBinding = collectionBinding;
-//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLMapElement.class.isInstance( attribute ) ) {
-				XMLMapElement collection = XMLMapElement.class.cast( attribute );
-//BagBinding bagBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-//bindMap( collection, bagBinding, entityBinding, PluralAttributeNature.MAP, collection.getName() );
-// todo : handle map key
-//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLAnyElement.class.isInstance( attribute ) ) {
-// todo : implement
-// value = new Any( mappings, table );
-// bindAny( subElement, (Any) value, nullable, mappings );
-			}
-			else if ( XMLComponentElement.class.isInstance( attribute )
-			|| XMLDynamicComponentElement.class.isInstance( attribute )
-			|| XMLPropertiesElement.class.isInstance( attribute ) ) {
-// todo : implement
-// String subpath = StringHelper.qualify( entityName, propertyName );
-// value = new Component( mappings, persistentClass );
-//
-// bindComponent(
-// subElement,
-// (Component) value,
-// persistentClass.getClassName(),
-// propertyName,
-// subpath,
-// true,
-// "properties".equals( subElementName ),
-// mappings,
-// inheritedMetas,
-// false
-// );
-			}
-		}
-	}
-
-	private void bindProperty(final XMLPropertyElement property, EntityBinding entityBinding) {
-		doBasicSimpleAttributeBindingCreation( new PropertyAttributeSource( property ), entityBinding );
-	}
-
-	private void bindManyToOne(XMLManyToOneElement manyToOne, EntityBinding entityBinding) {
-		doBasicSimpleAttributeBindingCreation( new ManyToOneAttributeSource( manyToOne ), entityBinding );
-	}
-
-	private BagBinding bindBag(XMLBagElement collection, EntityBinding entityBinding) {
-		final BagBinding bagBinding = null;
-//		metadata.addCollection( bagBinding );
-		return bagBinding;
-	}
-
-	private void bindSecondaryTables(EntitySourceInformation entitySourceInfo, EntityBinding entityBinding) {
-		final EntityElement entityElement = entitySourceInfo.getEntityElement();
-
-		if ( ! ( entityElement instanceof JoinElementSource) ) {
-			return;
-		}
-
-		for ( XMLJoinElement join : ( (JoinElementSource) entityElement ).getJoin() ) {
-			// todo : implement
-			// Join join = new Join();
-			// join.setPersistentClass( persistentClass );
-			// bindJoin( subElement, join, mappings, inheritedMetas );
-			// persistentClass.addJoin( join );
-		}
-	}
-
-	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
-		//To change body of created methods use File | Settings | File Templates.
-	}
-
-	private static interface AttributeMetadataContainer {
-		public List<Object> getAttributeElements();
-	}
-
-
-	// HBM specific implementations of "attribute sources" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * Implementation for {@code <property/>} mappings
-	 */
-	private class PropertyAttributeSource implements SingularAttributeSource {
-		private final XMLPropertyElement propertyElement;
-		private final ExplicitHibernateTypeSource typeSource;
-		private final List<RelationalValueSource> valueSources;
-
-		private PropertyAttributeSource(final XMLPropertyElement propertyElement) {
-			this.propertyElement = propertyElement;
-			this.typeSource = new ExplicitHibernateTypeSource() {
-				private final String name = propertyElement.getTypeAttribute() != null
-						? propertyElement.getTypeAttribute()
-						: propertyElement.getType() != null
-								? propertyElement.getType().getName()
-								: null;
-				private final Map<String, String> parameters = ( propertyElement.getType() != null )
-						? extractParameters( propertyElement.getType().getParam() )
-						: null;
-
-				@Override
-				public String getName() {
-					return name;
-				}
-
-				@Override
-				public Map<String, String> getParameters() {
-					return parameters;
-				}
-			};
-			this.valueSources = buildValueSources(
-					new ValueSourcesAdapter() {
-						@Override
-						public String getColumnAttribute() {
-							return propertyElement.getColumn();
-						}
-
-						@Override
-						public String getFormulaAttribute() {
-							return propertyElement.getFormula();
-						}
-
-						@Override
-						public List getColumnOrFormulaElements() {
-							return propertyElement.getColumnOrFormula();
-						}
-					}
-			);
-		}
-
-		@Override
-		public String getName() {
-			return propertyElement.getName();
-		}
-
-		@Override
-		public ExplicitHibernateTypeSource getTypeInformation() {
-			return typeSource;
-		}
-
-		@Override
-		public String getPropertyAccessorName() {
-			return propertyElement.getAccess();
-		}
-
-		@Override
-		public boolean isInsertable() {
-			return Helper.getBooleanValue( propertyElement.isInsert(), true );
-		}
-
-		@Override
-		public boolean isUpdatable() {
-			return Helper.getBooleanValue( propertyElement.isUpdate(), true );
-		}
-
-		@Override
-		public PropertyGeneration getGeneration() {
-			return PropertyGeneration.parse( propertyElement.getGenerated() );
-		}
-
-		@Override
-		public boolean isLazy() {
-			return Helper.getBooleanValue( propertyElement.isLazy(), false );
-		}
-
-		@Override
-		public boolean isIncludedInOptimisticLocking() {
-			return Helper.getBooleanValue( propertyElement.isOptimisticLock(), true );
-		}
-
-		@Override
-		public Iterable<CascadeStyle> getCascadeStyle() {
-			return NO_CASCADING;
-		}
-
-		@Override
-		public SingularAttributeNature getNature() {
-			return SingularAttributeNature.BASIC;
-		}
-
-		@Override
-		public boolean isVirtualAttribute() {
-			return false;
-		}
-
-		@Override
-		public List<RelationalValueSource> relationalValueSources() {
-			return valueSources;
-		}
-
-		@Override
-		public boolean isSingular() {
-			return true;
-		}
-
-		@Override
-		public Iterable<MetaAttributeSource> metaAttributes() {
-			return buildMetaAttributeSources( propertyElement.getMeta() );
-		}
-	}
-
-	/**
-	 * Implementation for {@code <id/>} mappings
-	 */
-	private class SingularIdentifierAttributeSource implements SingularAttributeSource {
-		private final XMLHibernateMapping.XMLClass.XMLId idElement;
-		private final ExplicitHibernateTypeSource typeSource;
-		private final List<RelationalValueSource> valueSources;
-
-		public SingularIdentifierAttributeSource(final XMLHibernateMapping.XMLClass.XMLId idElement) {
-			this.idElement = idElement;
-			this.typeSource = new ExplicitHibernateTypeSource() {
-				private final String name = idElement.getTypeAttribute() != null
-						? idElement.getTypeAttribute()
-						: idElement.getType() != null
-								? idElement.getType().getName()
-								: null;
-				private final Map<String, String> parameters = ( idElement.getType() != null )
-						? extractParameters( idElement.getType().getParam() )
-						: null;
-
-				@Override
-				public String getName() {
-					return name;
-				}
-
-				@Override
-				public Map<String, String> getParameters() {
-					return parameters;
-				}
-			};
-			this.valueSources = buildValueSources(
-					new ValueSourcesAdapter() {
-						@Override
-						public String getColumnAttribute() {
-							return idElement.getColumnAttribute();
-						}
-
-						@Override
-						public String getFormulaAttribute() {
-							return null;
-						}
-
-						@Override
-						public List getColumnOrFormulaElements() {
-							return idElement.getColumn();
-						}
-					}
-			);
-		}
-
-		@Override
-		public String getName() {
-			return idElement.getName() == null
-					? "id"
-					: idElement.getName();
-		}
-
-		@Override
-		public ExplicitHibernateTypeSource getTypeInformation() {
-			return typeSource;
-		}
-
-		@Override
-		public String getPropertyAccessorName() {
-			return idElement.getAccess();
-		}
-
-		@Override
-		public boolean isInsertable() {
-			return true;
-		}
-
-		@Override
-		public boolean isUpdatable() {
-			return false;
-		}
-
-		@Override
-		public PropertyGeneration getGeneration() {
-			return PropertyGeneration.INSERT;
-		}
-
-		@Override
-		public boolean isLazy() {
-			return false;
-		}
-
-		@Override
-		public boolean isIncludedInOptimisticLocking() {
-			return false;
-		}
-
-		@Override
-		public Iterable<CascadeStyle> getCascadeStyle() {
-			return NO_CASCADING;
-		}
-
-		@Override
-		public SingularAttributeNature getNature() {
-			return SingularAttributeNature.BASIC;
-		}
-
-		@Override
-		public boolean isVirtualAttribute() {
-			return false;
-		}
-
-		@Override
-		public List<RelationalValueSource> relationalValueSources() {
-			return valueSources;
-		}
-
-		@Override
-		public boolean isSingular() {
-			return true;
-		}
-
-		@Override
-		public Iterable<MetaAttributeSource> metaAttributes() {
-			return buildMetaAttributeSources( idElement.getMeta() );
-		}
-	}
-
-	/**
-	 * Implementation for {@code <version/>} mappings
-	 */
-	private class VersionAttributeSource implements SingularAttributeSource {
-		private final XMLHibernateMapping.XMLClass.XMLVersion versionElement;
-		private final List<RelationalValueSource> valueSources;
-
-		private VersionAttributeSource(final XMLHibernateMapping.XMLClass.XMLVersion versionElement) {
-			this.versionElement = versionElement;
-			this.valueSources = buildValueSources(
-					new ValueSourcesAdapter() {
-						@Override
-						public String getColumnAttribute() {
-							return versionElement.getColumnAttribute();
-						}
-
-						@Override
-						public String getFormulaAttribute() {
-							return null;
-						}
-
-						@Override
-						public List getColumnOrFormulaElements() {
-							return versionElement.getColumn();
-						}
-					}
-			);
-		}
-
-		private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
-			@Override
-			public String getName() {
-				return versionElement.getType() == null ? "integer" : versionElement.getType();
-			}
-
-			@Override
-			public Map<String, String> getParameters() {
-				return null;
-			}
-		};
-
-		@Override
-		public String getName() {
-			return versionElement.getName();
-		}
-
-		@Override
-		public ExplicitHibernateTypeSource getTypeInformation() {
-			return typeSource;
-		}
-
-		@Override
-		public String getPropertyAccessorName() {
-			return versionElement.getAccess();
-		}
-
-		@Override
-		public boolean isInsertable() {
-			return Helper.getBooleanValue( versionElement.isInsert(), true );
-		}
-
-		@Override
-		public boolean isUpdatable() {
-			return true;
-		}
-
-		private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
-				new Value.DeferredInitializer<PropertyGeneration>() {
-					@Override
-					public PropertyGeneration initialize() {
-						final PropertyGeneration propertyGeneration = versionElement.getGenerated() == null
-								? PropertyGeneration.NEVER
-								: PropertyGeneration.parse( versionElement.getGenerated().value() );
-						if ( propertyGeneration == PropertyGeneration.INSERT ) {
-							throw new MappingException(
-									"'generated' attribute cannot be 'insert' for versioning property",
-									currentBindingContext.getOrigin()
-							);
-						}
-						return propertyGeneration;
-					}
-				}
-		);
-
-		@Override
-		public PropertyGeneration getGeneration() {
-			return propertyGenerationValue.getValue();
-		}
-
-		@Override
-		public boolean isLazy() {
-			return false;
-		}
-
-		@Override
-		public boolean isIncludedInOptimisticLocking() {
-			return false;
-		}
-
-		@Override
-		public Iterable<CascadeStyle> getCascadeStyle() {
-			return NO_CASCADING;
-		}
-
-		@Override
-		public SingularAttributeNature getNature() {
-			return SingularAttributeNature.BASIC;
-		}
-
-		@Override
-		public boolean isVirtualAttribute() {
-			return false;
-		}
-
-		@Override
-		public List<RelationalValueSource> relationalValueSources() {
-			return valueSources;
-		}
-
-		@Override
-		public boolean isSingular() {
-			return true;
-		}
-
-		@Override
-		public Iterable<MetaAttributeSource> metaAttributes() {
-			return buildMetaAttributeSources( versionElement.getMeta() );
-		}
-	}
-
-	/**
-	 * Implementation for {@code <timestamp/>} mappings
-	 */
-	private class TimestampAttributeSource implements SingularAttributeSource {
-		private final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement;
-		private final List<RelationalValueSource> valueSources;
-
-		private TimestampAttributeSource(final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement) {
-			this.timestampElement = timestampElement;
-			this.valueSources = buildValueSources(
-					new ValueSourcesAdapter() {
-						@Override
-						public String getColumnAttribute() {
-							return timestampElement.getColumn();
-						}
-
-						@Override
-						public String getFormulaAttribute() {
-							return null;
-						}
-
-						@Override
-						public List getColumnOrFormulaElements() {
-							return null;
-						}
-					}
-			);
-		}
-
-		private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
-			@Override
-			public String getName() {
-				return "db".equals( timestampElement.getSource() ) ? "dbtimestamp" : "timestamp";
-			}
-
-			@Override
-			public Map<String, String> getParameters() {
-				return null;
-			}
-		};
-
-		@Override
-		public String getName() {
-			return timestampElement.getName();
-		}
-
-		@Override
-		public ExplicitHibernateTypeSource getTypeInformation() {
-			return typeSource;
-		}
-
-		@Override
-		public String getPropertyAccessorName() {
-			return timestampElement.getAccess();
-		}
-
-		@Override
-		public boolean isInsertable() {
-			return true;
-		}
-
-		@Override
-		public boolean isUpdatable() {
-			return true;
-		}
-
-		private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
-				new Value.DeferredInitializer<PropertyGeneration>() {
-					@Override
-					public PropertyGeneration initialize() {
-						final PropertyGeneration propertyGeneration = timestampElement.getGenerated() == null
-								? PropertyGeneration.NEVER
-								: PropertyGeneration.parse( timestampElement.getGenerated().value() );
-						if ( propertyGeneration == PropertyGeneration.INSERT ) {
-							throw new MappingException(
-									"'generated' attribute cannot be 'insert' for versioning property",
-									currentBindingContext.getOrigin()
-							);
-						}
-						return propertyGeneration;
-					}
-				}
-		);
-
-		@Override
-		public PropertyGeneration getGeneration() {
-			return propertyGenerationValue.getValue();
-		}
-
-		@Override
-		public boolean isLazy() {
-			return false;
-		}
-
-		@Override
-		public boolean isIncludedInOptimisticLocking() {
-			return false;
-		}
-
-		@Override
-		public Iterable<CascadeStyle> getCascadeStyle() {
-			return NO_CASCADING;
-		}
-
-		@Override
-		public SingularAttributeNature getNature() {
-			return SingularAttributeNature.BASIC;
-		}
-
-		@Override
-		public boolean isVirtualAttribute() {
-			return false;
-		}
-
-		@Override
-		public List<RelationalValueSource> relationalValueSources() {
-			return valueSources;
-		}
-
-		@Override
-		public boolean isSingular() {
-			return true;
-		}
-
-		@Override
-		public Iterable<MetaAttributeSource> metaAttributes() {
-			return buildMetaAttributeSources( timestampElement.getMeta() );
-		}
-	}
-
-	/**
-	 * Implementation for {@code <many-to-one/> mappings}
-	 */
-	private class ManyToOneAttributeSource implements ToOneAttributeSource {
-		private final XMLManyToOneElement manyToOneElement;
-		private final List<RelationalValueSource> valueSources;
-
-		private ManyToOneAttributeSource(final XMLManyToOneElement manyToOneElement) {
-			this.manyToOneElement = manyToOneElement;
-			this.valueSources = buildValueSources(
-					new ValueSourcesAdapter() {
-						@Override
-						public String getColumnAttribute() {
-							return manyToOneElement.getColumn();
-						}
-
-						@Override
-						public String getFormulaAttribute() {
-							return manyToOneElement.getFormula();
-						}
-
-						@Override
-						public List getColumnOrFormulaElements() {
-							return manyToOneElement.getColumnOrFormula();
-						}
-					}
-			);
-		}
-
-		@Override
-		public String getName() {
-				return manyToOneElement.getName();
-		}
-
-		@Override
-		public ExplicitHibernateTypeSource getTypeInformation() {
-			return TO_ONE_ATTRIBUTE_TYPE_SOURCE;
-		}
-
-		@Override
-		public String getPropertyAccessorName() {
-			return manyToOneElement.getAccess();
-		}
-
-		@Override
-		public boolean isInsertable() {
-			return manyToOneElement.isInsert();
-		}
-
-		@Override
-		public boolean isUpdatable() {
-			return manyToOneElement.isUpdate();
-		}
-
-		@Override
-		public PropertyGeneration getGeneration() {
-			return PropertyGeneration.NEVER;
-		}
-
-		@Override
-		public boolean isLazy() {
-			return false;
-		}
-
-		@Override
-		public boolean isIncludedInOptimisticLocking() {
-			return manyToOneElement.isOptimisticLock();
-		}
-
-		@Override
-		public Iterable<CascadeStyle> getCascadeStyle() {
-			return interpretCascadeStyles( manyToOneElement.getCascade() );
-		}
-
-		@Override
-		public SingularAttributeNature getNature() {
-			return SingularAttributeNature.MANY_TO_ONE;
-		}
-
-		@Override
-		public boolean isVirtualAttribute() {
-			return false;
-		}
-
-		@Override
-		public List<RelationalValueSource> relationalValueSources() {
-			return valueSources;
-		}
-
-		@Override
-		public boolean isSingular() {
-			return true;
-		}
-
-		@Override
-		public Iterable<MetaAttributeSource> metaAttributes() {
-			return buildMetaAttributeSources( manyToOneElement.getMeta() );
-		}
-
-		@Override
-		public String getReferencedEntityName() {
-			return manyToOneElement.getClazz() != null
-					? manyToOneElement.getClazz()
-					: manyToOneElement.getEntityName();
-		}
-
-		@Override
-		public String getReferencedEntityAttributeName() {
-			return manyToOneElement.getPropertyRef();
-		}
-	}
-
-	private static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
-		@Override
-		public String getName() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		@Override
-		public Map<String, String> getParameters() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-	};
-
-
-	// Helpers for building "attribute sources" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	private static final Iterable<CascadeStyle> NO_CASCADING = Collections.singleton( CascadeStyle.NONE );
-
-	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleAttributeBinding attributeBinding) {
-		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
-		if ( attributeJavaType != null ) {
-			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
-			attributeBinding.getAttribute().resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
-		}
-
-		final String explicitTypeName = typeSource.getName();
-		if ( explicitTypeName != null ) {
-			final TypeDef typeDef = currentBindingContext.getMetadataImplementor().getTypeDefinition( explicitTypeName );
-			if ( typeDef != null ) {
-				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
-				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
-			}
-			else {
-				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
-			}
-			final Map<String,String> parameters = typeSource.getParameters();
-			if ( parameters != null ) {
-				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( parameters );
-			}
-		}
-		else {
-			if ( attributeJavaType == null ) {
-				// we will have problems later determining the Hibernate Type to use.  Should we throw an
-				// exception now?  Might be better to get better contextual info
-			}
-		}
-	}
-
-	private Class<?> determineJavaType(final Attribute attribute) {
-		try {
-			final Class ownerClass = attribute.getAttributeContainer().getClassReference();
-			AttributeJavaTypeDeterminerDelegate delegate = new AttributeJavaTypeDeterminerDelegate( attribute.getName() );
-			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
-			return delegate.javaType;
-		}
-		catch ( Exception ignore ) {
-			// todo : log it?
-		}
-		return null;
-	}
-
-	private static class AttributeJavaTypeDeterminerDelegate implements BeanInfoHelper.BeanInfoDelegate {
-		private final String attributeName;
-		private Class<?> javaType = null;
-
-		private AttributeJavaTypeDeterminerDelegate(String attributeName) {
-			this.attributeName = attributeName;
-		}
-
-		@Override
-		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
-			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
-				if ( propertyDescriptor.getName().equals( attributeName ) ) {
-					javaType = propertyDescriptor.getPropertyType();
-					break;
-				}
-			}
-		}
-	}
-
-	private Map<String, String> extractParameters(List<XMLParamElement> xmlParamElements) {
-		if ( xmlParamElements == null || xmlParamElements.isEmpty() ) {
-			return null;
-		}
-		final HashMap<String,String> params = new HashMap<String, String>();
-		for ( XMLParamElement paramElement : xmlParamElements ) {
-			params.put( paramElement.getName(), paramElement.getValue() );
-		}
-		return params;
-	}
-
-	private void resolveToOneReferenceInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
-		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
-				? attributeSource.getReferencedEntityName()
-				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
-		attributeBinding.setReferencedEntityName( referencedEntityName );
-		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
-	}
-
-	private org.hibernate.metamodel.relational.Value makeValue(
-			RelationValueMetadataSource relationValueMetadataSource,
-			SimpleAttributeBinding attributeBinding) {
-		// todo : to be completely correct, we need to know which table the value belongs to.
-		// 		There is a note about this somewhere else with ideas on the subject.
-		//		For now, just use the entity's base table.
-		final TableSpecification valueSource = attributeBinding.getEntityBinding().getBaseTable();
-
-		if ( StringHelper.isNotEmpty( relationValueMetadataSource.getColumnAttribute() ) ) {
-			if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
-					&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
-				throw new MappingException(
-						"column/formula attribute may not be used together with <column>/<formula> subelement",
-						currentBindingContext.getOrigin()
-				);
-			}
-			if ( StringHelper.isNotEmpty( relationValueMetadataSource.getFormulaAttribute() ) ) {
-				throw new MappingException(
-						"column and formula attributes may not be used together",
-						currentBindingContext.getOrigin()
-				);
-			}
-			return valueSource.locateOrCreateColumn( relationValueMetadataSource.getColumnAttribute() );
-		}
-		else if ( StringHelper.isNotEmpty( relationValueMetadataSource.getFormulaAttribute() ) ) {
-			if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
-					&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
-				throw new MappingException(
-						"column/formula attribute may not be used together with <column>/<formula> subelement",
-						currentBindingContext.getOrigin()
-				);
-			}
-			// column/formula attribute combo checked already
-			return valueSource.locateOrCreateDerivedValue( relationValueMetadataSource.getFormulaAttribute() );
-		}
-		else if ( relationValueMetadataSource.getColumnOrFormulaElements() != null
-				&& ! relationValueMetadataSource.getColumnOrFormulaElements().isEmpty() ) {
-			List<SimpleValue> values = new ArrayList<SimpleValue>();
-			for ( Object columnOrFormula : relationValueMetadataSource.getColumnOrFormulaElements() ) {
-				final SimpleValue value;
-				if ( XMLColumnElement.class.isInstance( columnOrFormula ) ) {
-					final XMLColumnElement columnElement = (XMLColumnElement) columnOrFormula;
-					final Column column = valueSource.locateOrCreateColumn( columnElement.getName() );
-					column.setNullable( ! columnElement.isNotNull() );
-					column.setDefaultValue( columnElement.getDefault() );
-					column.setSqlType( columnElement.getSqlType() );
-					column.setSize(
-							new Size(
-									Helper.getIntValue( columnElement.getPrecision(), -1 ),
-									Helper.getIntValue( columnElement.getScale(), -1 ),
-									Helper.getLongValue( columnElement.getLength(), -1 ),
-									Size.LobMultiplier.NONE
-							)
-					);
-					column.setDatatype( null ); // todo : ???
-					column.setReadFragment( columnElement.getRead() );
-					column.setWriteFragment( columnElement.getWrite() );
-					column.setUnique( columnElement.isUnique() );
-					column.setCheckCondition( columnElement.getCheck() );
-					column.setComment( columnElement.getComment() );
-					value = column;
-				}
-				else {
-					value = valueSource.locateOrCreateDerivedValue( (String) columnOrFormula );
-				}
-				if ( value != null ) {
-					values.add( value );
-				}
-			}
-
-			if ( values.size() == 1 ) {
-				return values.get( 0 );
-			}
-
-			final Tuple tuple = valueSource.createTuple(
-					attributeBinding.getEntityBinding().getEntity().getName() + '.'
-							+ attributeBinding.getAttribute().getName()
-			);
-			for ( SimpleValue value : values ) {
-				tuple.addValue( value );
-			}
-			return tuple;
-		}
-		else {
-			// assume a column named based on the NamingStrategy
-			final String name = metadata.getOptions()
-					.getNamingStrategy()
-					.propertyToColumnName( attributeBinding.getAttribute().getName() );
-			return valueSource.locateOrCreateColumn( name );
-		}
-	}
-
-	private MetaAttributeContext extractMetaAttributeContext(Iterable<MetaAttributeSource> sources, MetaAttributeContext parentContext) {
-		return extractMetaAttributeContext( sources, false, parentContext );
-	}
-
-	public static MetaAttributeContext extractMetaAttributeContext(
-			Iterable<MetaAttributeSource> sources,
-			boolean onlyInheritable,
-			MetaAttributeContext parentContext) {
-		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
-
-		for ( MetaAttributeSource source : sources ) {
-			if ( onlyInheritable & !source.isInheritable() ) {
-				continue;
-			}
-
-			final String name = source.getName();
-			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
-			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
-			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
-				metaAttribute = new MetaAttribute( name );
-				subContext.add( metaAttribute );
-			}
-			metaAttribute.addValue( source.getValue() );
-		}
-
-		return subContext;
-	}
-
-	private Iterable<MetaAttributeSource> buildMetaAttributeSources(List<XMLMetaElement> metaElements) {
-		ArrayList<MetaAttributeSource> result = new ArrayList<MetaAttributeSource>();
-		if ( metaElements == null || metaElements.isEmpty() ) {
-			// do nothing
-		}
-		else {
-			for ( final XMLMetaElement metaElement : metaElements ) {
-				result.add(
-						new MetaAttributeSource() {
-							@Override
-							public String getName() {
-								return metaElement.getAttribute();
-							}
-
-							@Override
-							public String getValue() {
-								return metaElement.getValue();
-							}
-
-							@Override
-							public boolean isInheritable() {
-								return metaElement.isInherit();
-							}
-						}
-				);
-			}
-		}
-		return result;
-	}
-
-	private Iterable<CascadeStyle> interpretCascadeStyles(String cascades) {
-		final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
-		if ( StringHelper.isEmpty( cascades ) ) {
-			cascades = currentBindingContext.getMappingDefaults().getCascadeStyle();
-		}
-		for ( String cascade : StringHelper.split( cascades, "," ) ) {
-			cascadeStyles.add( CascadeStyle.getCascadeStyle( cascade ) );
-		}
-		return cascadeStyles;
-	}
-
-	private static interface ValueSourcesAdapter {
-		public String getColumnAttribute();
-		public String getFormulaAttribute();
-		public List getColumnOrFormulaElements();
-	}
-
-	private List<RelationalValueSource> buildValueSources(final ValueSourcesAdapter valueSourcesAdapter) {
-		List<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
-
-		if ( StringHelper.isNotEmpty( valueSourcesAdapter.getColumnAttribute() ) ) {
-			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
-					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
-				throw new MappingException(
-						"column/formula attribute may not be used together with <column>/<formula> subelement",
-						currentBindingContext.getOrigin()
-				);
-			}
-			if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
-				throw new MappingException(
-						"column and formula attributes may not be used together",
-						currentBindingContext.getOrigin()
-				);
-			}
-			result.add(  new ColumnAttributeSource( valueSourcesAdapter.getColumnAttribute() ) );
-		}
-		else if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
-			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
-					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
-				throw new MappingException(
-						"column/formula attribute may not be used together with <column>/<formula> subelement",
-						currentBindingContext.getOrigin()
-				);
-			}
-			// column/formula attribute combo checked already
-			result.add( new Formula( valueSourcesAdapter.getFormulaAttribute() ) );
-		}
-		else if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
-				&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
-			List<SimpleValue> values = new ArrayList<SimpleValue>();
-			for ( Object columnOrFormulaElement : valueSourcesAdapter.getColumnOrFormulaElements() ) {
-				if ( XMLColumnElement.class.isInstance( columnOrFormulaElement ) ) {
-					result.add( new ColumnSource( (XMLColumnElement) columnOrFormulaElement ) );
-				}
-				else {
-					result.add( new Formula( (String) columnOrFormulaElement ) );
-				}
-			}
-		}
-		return result;
-	}
-
-	private static class ColumnAttributeSource implements org.hibernate.metamodel.source.binder.ColumnSource {
-		private final String columnName;
-
-		private ColumnAttributeSource(String columnName) {
-			this.columnName = columnName;
-		}
-
-		@Override
-		public String getName() {
-			return columnName;
-		}
-
-		@Override
-		public boolean isNullable() {
-			return true;
-		}
-
-		@Override
-		public String getDefaultValue() {
-			return null;
-		}
-
-		@Override
-		public String getSqlType() {
-			return null;
-		}
-
-		@Override
-		public Datatype getDatatype() {
-			return null;
-		}
-
-		@Override
-		public Size getSize() {
-			return null;
-		}
-
-		@Override
-		public String getReadFragment() {
-			return null;
-		}
-
-		@Override
-		public String getWriteFragment() {
-			return null;
-		}
-
-		@Override
-		public boolean isUnique() {
-			return false;
-		}
-
-		@Override
-		public String getCheckCondition() {
-			return null;
-		}
-
-		@Override
-		public String getComment() {
-			return null;
-		}
-	}
-
-	private static class Formula implements DerivedValueSource {
-		private final String expression;
-
-		private Formula(String expression) {
-			this.expression = expression;
-		}
-
-		@Override
-		public String getExpression() {
-			return expression;
-		}
-	}
-
-	private class ColumnSource implements org.hibernate.metamodel.source.binder.ColumnSource {
-		private final XMLColumnElement columnElement;
-
-		private ColumnSource(XMLColumnElement columnElement) {
-			this.columnElement = columnElement;
-		}
-
-		@Override
-		public String getName() {
-			return columnElement.getName();
-		}
-
-		@Override
-		public boolean isNullable() {
-			return ! columnElement.isNotNull();
-		}
-
-		@Override
-		public String getDefaultValue() {
-			return columnElement.getDefault();
-		}
-
-		@Override
-		public String getSqlType() {
-			return columnElement.getSqlType();
-		}
-
-		@Override
-		public Datatype getDatatype() {
-			return null;
-		}
-
-		@Override
-		public Size getSize() {
-			return new Size(
-					Helper.getIntValue( columnElement.getPrecision(), -1 ),
-					Helper.getIntValue( columnElement.getScale(), -1 ),
-					Helper.getLongValue( columnElement.getLength(), -1 ),
-					Size.LobMultiplier.NONE
-			);
-		}
-
-		@Override
-		public String getReadFragment() {
-			return columnElement.getRead();
-		}
-
-		@Override
-		public String getWriteFragment() {
-			return columnElement.getWrite();
-		}
-
-		@Override
-		public boolean isUnique() {
-			return columnElement.isUnique();
-		}
-
-		@Override
-		public String getCheckCondition() {
-			return columnElement.getCheck();
-		}
-
-		@Override
-		public String getComment() {
-			return columnElement.getComment();
-		}
-
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntitySourceInformation.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
similarity index 51%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntitySourceInformation.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
index a02553db8e..66899a144f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntitySourceInformation.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
@@ -1,55 +1,94 @@
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
 
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+import org.hibernate.metamodel.relational.Datatype;
+import org.hibernate.metamodel.relational.Size;
+import org.hibernate.metamodel.source.binder.ColumnSource;
 
 /**
- * An aggregation of information about the source of an entity mapping.
- * 
- * @author Steve Ebersole
- */
-public class EntitySourceInformation {
-	private final EntityElement entityElement;
-	private final MappingDocument sourceMappingDocument;
-	private final String mappedEntityName;
+* @author Steve Ebersole
+*/
+class ColumnAttributeSourceImpl implements ColumnSource {
+	private final String columnName;
+
+	ColumnAttributeSourceImpl(String columnName) {
+		this.columnName = columnName;
+	}
+
+	@Override
+	public String getName() {
+		return columnName;
+	}
+
+	@Override
+	public boolean isNullable() {
+		return true;
+	}
+
+	@Override
+	public String getDefaultValue() {
+		return null;
+	}
+
+	@Override
+	public String getSqlType() {
+		return null;
+	}
+
+	@Override
+	public Datatype getDatatype() {
+		return null;
+	}
+
+	@Override
+	public Size getSize() {
+		return null;
+	}
+
+	@Override
+	public String getReadFragment() {
+		return null;
+	}
 
-	public EntitySourceInformation(EntityElement entityElement, MappingDocument sourceMappingDocument) {
-		this.entityElement = entityElement;
-		this.sourceMappingDocument = sourceMappingDocument;
-		this.mappedEntityName = sourceMappingDocument.getMappingLocalBindingContext().determineEntityName( entityElement );
+	@Override
+	public String getWriteFragment() {
+		return null;
 	}
 
-	public EntityElement getEntityElement() {
-		return entityElement;
+	@Override
+	public boolean isUnique() {
+		return false;
 	}
 
-	public MappingDocument getSourceMappingDocument() {
-		return sourceMappingDocument;
+	@Override
+	public String getCheckCondition() {
+		return null;
 	}
 
-	public String getMappedEntityName() {
-		return mappedEntityName;
+	@Override
+	public String getComment() {
+		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java
new file mode 100644
index 0000000000..dc7da3d807
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java
@@ -0,0 +1,100 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.metamodel.relational.Datatype;
+import org.hibernate.metamodel.relational.Size;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
+
+/**
+* @author Steve Ebersole
+*/
+class ColumnSourceImpl implements org.hibernate.metamodel.source.binder.ColumnSource {
+	private final XMLColumnElement columnElement;
+
+	ColumnSourceImpl(XMLColumnElement columnElement) {
+		this.columnElement = columnElement;
+	}
+
+	@Override
+	public String getName() {
+		return columnElement.getName();
+	}
+
+	@Override
+	public boolean isNullable() {
+		return ! columnElement.isNotNull();
+	}
+
+	@Override
+	public String getDefaultValue() {
+		return columnElement.getDefault();
+	}
+
+	@Override
+	public String getSqlType() {
+		return columnElement.getSqlType();
+	}
+
+	@Override
+	public Datatype getDatatype() {
+		return null;
+	}
+
+	@Override
+	public Size getSize() {
+		return new Size(
+				Helper.getIntValue( columnElement.getPrecision(), -1 ),
+				Helper.getIntValue( columnElement.getScale(), -1 ),
+				Helper.getLongValue( columnElement.getLength(), -1 ),
+				Size.LobMultiplier.NONE
+		);
+	}
+
+	@Override
+	public String getReadFragment() {
+		return columnElement.getRead();
+	}
+
+	@Override
+	public String getWriteFragment() {
+		return columnElement.getWrite();
+	}
+
+	@Override
+	public boolean isUnique() {
+		return columnElement.isUnique();
+	}
+
+	@Override
+	public String getCheckCondition() {
+		return columnElement.getCheck();
+	}
+
+	@Override
+	public String getComment() {
+		return columnElement.getComment();
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchy.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchyImpl.java
similarity index 58%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchy.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchyImpl.java
index 933023a839..c3acf22596 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchy.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityHierarchyImpl.java
@@ -1,64 +1,61 @@
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
 
 import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.RootEntitySource;
 
 /**
- * Models the source view of an entity hierarchy.
- *
  * @author Steve Ebersole
  */
-public class EntityHierarchy extends AbstractSubEntityContainer {
-	private final EntitySourceInformation entitySourceInformation;
+public class EntityHierarchyImpl implements org.hibernate.metamodel.source.binder.EntityHierarchy {
+	private final RootEntitySourceImpl rootEntitySource;
 	private InheritanceType hierarchyInheritanceType = InheritanceType.NO_INHERITANCE;
 
-	public EntityHierarchy(XMLHibernateMapping.XMLClass rootEntity, MappingDocument sourceMappingDocument) {
-		this.entitySourceInformation = new EntitySourceInformation( rootEntity, sourceMappingDocument );
-	}
-
-	public EntitySourceInformation getEntitySourceInformation() {
-		return entitySourceInformation;
+	public EntityHierarchyImpl(RootEntitySourceImpl rootEntitySource) {
+		this.rootEntitySource = rootEntitySource;
+		this.rootEntitySource.injectHierarchy( this );
 	}
 
+	@Override
 	public InheritanceType getHierarchyInheritanceType() {
 		return hierarchyInheritanceType;
 	}
 
 	@Override
-	public void addSubEntityDescriptor(EntityHierarchySubEntity subEntityDescriptor) {
-		super.addSubEntityDescriptor( subEntityDescriptor );
+	public RootEntitySource getRootEntitySource() {
+		return rootEntitySource;
+	}
 
-		// check inheritance type consistency
-		final InheritanceType inheritanceType = Helper.interpretInheritanceType(
-				subEntityDescriptor.getEntitySourceInformation().getEntityElement()
-		);
-		if ( this.hierarchyInheritanceType != InheritanceType.NO_INHERITANCE
-				&& this.hierarchyInheritanceType != inheritanceType ) {
-			// throw exception
+	public void processSubclass(SubclassEntitySourceImpl subclassEntitySource) {
+		final InheritanceType inheritanceType = Helper.interpretInheritanceType( subclassEntitySource.entityElement() );
+		if ( hierarchyInheritanceType == InheritanceType.NO_INHERITANCE ) {
+			hierarchyInheritanceType = inheritanceType;
+		}
+		else if ( hierarchyInheritanceType != inheritanceType ) {
+			throw new MappingException( "Mixed inheritance strategies not supported", subclassEntitySource.getOrigin() );
 		}
-		this.hierarchyInheritanceType = inheritanceType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubEntityContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
similarity index 73%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubEntityContainer.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
index d54cd95c58..2db0051946 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubEntityContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
@@ -1,35 +1,42 @@
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
 
+import org.hibernate.metamodel.source.binder.DerivedValueSource;
+
 /**
- * Contract for elements within a {@link EntityHierarchy} which can contain sub elements.  Essentially this
- * abstracts that common aspect away from both root and sub entities.
- *
- * @author Steve Ebersole
- */
-public interface SubEntityContainer {
-	public void addSubEntityDescriptor(EntityHierarchySubEntity subEntityDescriptor);
-	public Iterable<EntityHierarchySubEntity> subEntityDescriptors();
+* @author Steve Ebersole
+*/
+class FormulaImpl implements DerivedValueSource {
+	private final String expression;
+
+	FormulaImpl(String expression) {
+		this.expression = expression;
+	}
+
+	@Override
+	public String getExpression() {
+		return expression;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java
index 8581a143c8..5c9d6b1eab 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java
@@ -1,97 +1,98 @@
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
 import java.util.List;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
+import org.hibernate.metamodel.source.binder.Binder;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * The {@link SourceProcessor} implementation responsible for processing {@code hbm.xml} sources.
  *
  * @author Steve Ebersole
  */
 public class HbmSourceProcessorImpl implements SourceProcessor {
 	private final MetadataImplementor metadata;
 
 	private List<HibernateMappingProcessor> processors = new ArrayList<HibernateMappingProcessor>();
-	private List<EntityHierarchy> entityHierarchies;
+	private List<EntityHierarchyImpl> entityHierarchies;
 
 	public HbmSourceProcessorImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void prepare(MetadataSources sources) {
-		final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder( metadata );
+		final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
 
 		for ( JaxbRoot jaxbRoot : sources.getJaxbRootList() ) {
 			if ( ! XMLHibernateMapping.class.isInstance( jaxbRoot.getRoot() ) ) {
 				continue;
 			}
 
 			final MappingDocument mappingDocument = new MappingDocument( jaxbRoot, metadata );
 			processors.add( new HibernateMappingProcessor( metadata, mappingDocument ) );
 
 			hierarchyBuilder.processMappingDocument( mappingDocument );
 		}
 
 		this.entityHierarchies = hierarchyBuilder.groupEntityHierarchies();
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processIndependentMetadata();
 		}
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processTypeDependentMetadata();
 		}
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
-		BindingCreator bindingCreator = new BindingCreator( metadata, processedEntityNames );
-		for ( EntityHierarchy entityHierarchy : entityHierarchies ) {
-			bindingCreator.processEntityHierarchy( entityHierarchy );
+		Binder binder = new Binder( metadata, processedEntityNames );
+		for ( EntityHierarchyImpl entityHierarchy : entityHierarchies ) {
+			binder.processEntityHierarchy( entityHierarchy );
 		}
 	}
 
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processMappingDependentMetadata();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
index ece88bb44b..8ffafda5f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
@@ -1,198 +1,326 @@
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
 
+import java.util.ArrayList;
 import java.util.Collections;
+import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
+import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.MappingException;
+import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.MetaAttribute;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.MetaAttributeContext;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMetaElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
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
+	static final Iterable<CascadeStyle> NO_CASCADING = Collections.singleton( CascadeStyle.NONE );
+	public static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
+		@Override
+		public String getName() {
+			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		}
+
+		@Override
+		public Map<String, String> getParameters() {
+			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		}
+	};
+
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
+
+	public static Iterable<CascadeStyle> interpretCascadeStyles(String cascades, LocalBindingContext bindingContext) {
+		final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
+		if ( StringHelper.isEmpty( cascades ) ) {
+			cascades = bindingContext.getMappingDefaults().getCascadeStyle();
+		}
+		for ( String cascade : StringHelper.split( cascades, "," ) ) {
+			cascadeStyles.add( CascadeStyle.getCascadeStyle( cascade ) );
+		}
+		return cascadeStyles;
+	}
+
+	public static Map<String, String> extractParameters(List<XMLParamElement> xmlParamElements) {
+		if ( xmlParamElements == null || xmlParamElements.isEmpty() ) {
+			return null;
+		}
+		final HashMap<String,String> params = new HashMap<String, String>();
+		for ( XMLParamElement paramElement : xmlParamElements ) {
+			params.put( paramElement.getName(), paramElement.getValue() );
+		}
+		return params;
+	}
+
+	public static Iterable<MetaAttributeSource> buildMetaAttributeSources(List<XMLMetaElement> metaElements) {
+		ArrayList<MetaAttributeSource> result = new ArrayList<MetaAttributeSource>();
+		if ( metaElements == null || metaElements.isEmpty() ) {
+			// do nothing
+		}
+		else {
+			for ( final XMLMetaElement metaElement : metaElements ) {
+				result.add(
+						new MetaAttributeSource() {
+							@Override
+							public String getName() {
+								return metaElement.getAttribute();
+							}
+
+							@Override
+							public String getValue() {
+								return metaElement.getValue();
+							}
+
+							@Override
+							public boolean isInheritable() {
+								return metaElement.isInherit();
+							}
+						}
+				);
+			}
+		}
+		return result;
+	}
+
+	public static interface ValueSourcesAdapter {
+		public String getColumnAttribute();
+		public String getFormulaAttribute();
+		public List getColumnOrFormulaElements();
+	}
+
+	public static List<RelationalValueSource> buildValueSources(
+			ValueSourcesAdapter valueSourcesAdapter,
+			LocalBindingContext bindingContext) {
+		List<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
+
+		if ( StringHelper.isNotEmpty( valueSourcesAdapter.getColumnAttribute() ) ) {
+			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
+					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
+				throw new org.hibernate.metamodel.source.MappingException(
+						"column/formula attribute may not be used together with <column>/<formula> subelement",
+						bindingContext.getOrigin()
+				);
+			}
+			if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
+				throw new org.hibernate.metamodel.source.MappingException(
+						"column and formula attributes may not be used together",
+						bindingContext.getOrigin()
+				);
+			}
+			result.add(  new ColumnAttributeSourceImpl( valueSourcesAdapter.getColumnAttribute() ) );
+		}
+		else if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
+			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
+					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
+				throw new org.hibernate.metamodel.source.MappingException(
+						"column/formula attribute may not be used together with <column>/<formula> subelement",
+						bindingContext.getOrigin()
+				);
+			}
+			// column/formula attribute combo checked already
+			result.add( new FormulaImpl( valueSourcesAdapter.getFormulaAttribute() ) );
+		}
+		else if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
+				&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
+			for ( Object columnOrFormulaElement : valueSourcesAdapter.getColumnOrFormulaElements() ) {
+				if ( XMLColumnElement.class.isInstance( columnOrFormulaElement ) ) {
+					result.add( new ColumnSourceImpl( (XMLColumnElement) columnOrFormulaElement ) );
+				}
+				else {
+					result.add( new FormulaImpl( (String) columnOrFormulaElement ) );
+				}
+			}
+		}
+		return result;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HierarchyBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HierarchyBuilder.java
index 7d24fdec2b..822f2b581b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HierarchyBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HierarchyBuilder.java
@@ -1,175 +1,167 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.MappingException;
-import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.binder.SubclassEntityContainer;
+import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.SubEntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 
 /**
  * @author Steve Ebersole
  */
 public class HierarchyBuilder {
-	private final MetadataImplementor metadata;
-
-	private final List<EntityHierarchy> entityHierarchies = new ArrayList<EntityHierarchy>();
+	private final List<EntityHierarchyImpl> entityHierarchies = new ArrayList<EntityHierarchyImpl>();
 
 	// process state
-	private final Map<String,SubEntityContainer> subEntityContainerMap = new HashMap<String, SubEntityContainer>();
+	private final Map<String,SubclassEntityContainer> subEntityContainerMap = new HashMap<String, SubclassEntityContainer>();
 	private final List<ExtendsQueueEntry> extendsQueue = new ArrayList<ExtendsQueueEntry>();
 
 	// mapping file specific state
 	private MappingDocument currentMappingDocument;
 
-	public HierarchyBuilder(MetadataImplementor metadata) {
-		this.metadata = metadata;
-	}
-
 	public void processMappingDocument(MappingDocument mappingDocument) {
 		this.currentMappingDocument = mappingDocument;
 		try {
 			processCurrentMappingDocument();
 		}
 		finally {
 			this.currentMappingDocument = null;
 		}
 	}
 
 	private void processCurrentMappingDocument() {
 		for ( Object entityElementO : currentMappingDocument.getMappingRoot().getClazzOrSubclassOrJoinedSubclass() ) {
 			final EntityElement entityElement = (EntityElement) entityElementO;
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( entityElement ) ) {
 				// we can immediately handle <class/> elements in terms of creating the hierarchy entry
 				final XMLHibernateMapping.XMLClass xmlClass = (XMLHibernateMapping.XMLClass) entityElement;
-				final EntityHierarchy hierarchy = new EntityHierarchy( xmlClass, currentMappingDocument );
+				final RootEntitySourceImpl rootEntitySource = new RootEntitySourceImpl( currentMappingDocument, xmlClass );
+				final EntityHierarchyImpl hierarchy = new EntityHierarchyImpl( rootEntitySource );
+
 				entityHierarchies.add( hierarchy );
-				subEntityContainerMap.put( hierarchy.getEntitySourceInformation().getMappedEntityName(), hierarchy );
-				processSubElements( entityElement, hierarchy );
+				subEntityContainerMap.put( rootEntitySource.getEntityName(), rootEntitySource );
+
+				processSubElements( entityElement, rootEntitySource );
 			}
 			else {
 				// we have to see if this things super-type has been found yet, and if not add it to the
 				// extends queue
-				final EntityHierarchySubEntity subEntityDescriptor = new EntityHierarchySubEntity(
-						entityElement,
-						currentMappingDocument
-				);
-				final String entityName = subEntityDescriptor.getEntitySourceInformation().getMappedEntityName();
-				subEntityContainerMap.put( entityName, subEntityDescriptor );
+				final SubclassEntitySourceImpl subClassEntitySource = new SubclassEntitySourceImpl( currentMappingDocument, entityElement );
+				final String entityName = subClassEntitySource.getEntityName();
+				subEntityContainerMap.put( entityName, subClassEntitySource );
 				final String entityItExtends = currentMappingDocument.getMappingLocalBindingContext().qualifyClassName(
 						((SubEntityElement) entityElement).getExtends()
 				);
-				processSubElements( entityElement, subEntityDescriptor );
-				final SubEntityContainer container = subEntityContainerMap.get( entityItExtends );
+				processSubElements( entityElement, subClassEntitySource );
+				final SubclassEntityContainer container = subEntityContainerMap.get( entityItExtends );
 				if ( container != null ) {
 					// we already have this entity's super, attach it and continue
-					container.addSubEntityDescriptor( subEntityDescriptor );
+					container.add( subClassEntitySource );
 				}
 				else {
 					// we do not yet have the super and have to wait, so add it fto the extends queue
-					extendsQueue.add( new ExtendsQueueEntry( subEntityDescriptor, entityItExtends ) );
+					extendsQueue.add( new ExtendsQueueEntry( subClassEntitySource, entityItExtends ) );
 				}
 			}
 		}
 	}
 
-	public List<EntityHierarchy> groupEntityHierarchies() {
+	public List<EntityHierarchyImpl> groupEntityHierarchies() {
 		while ( ! extendsQueue.isEmpty() ) {
 			// set up a pass over the queue
 			int numberOfMappingsProcessed = 0;
 			Iterator<ExtendsQueueEntry> iterator = extendsQueue.iterator();
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = iterator.next();
-				final SubEntityContainer container = subEntityContainerMap.get( entry.entityItExtends );
+				final SubclassEntityContainer container = subEntityContainerMap.get( entry.entityItExtends );
 				if ( container != null ) {
 					// we now have this entity's super, attach it and remove entry from extends queue
-					container.addSubEntityDescriptor( entry.subEntityDescriptor );
+					container.add( entry.subClassEntitySource );
 					iterator.remove();
 					numberOfMappingsProcessed++;
 				}
 			}
 
 			if ( numberOfMappingsProcessed == 0 ) {
 				// todo : we could log the waiting dependencies...
 				throw new MappingException( "Unable to process extends dependencies in hbm files" );
 			}
 		}
 
 		return entityHierarchies;
 	}
 
-	private void processSubElements(EntityElement entityElement, SubEntityContainer container) {
+	private void processSubElements(EntityElement entityElement, SubclassEntityContainer container) {
 		if ( XMLHibernateMapping.XMLClass.class.isInstance( entityElement ) ) {
 			final XMLHibernateMapping.XMLClass xmlClass = (XMLHibernateMapping.XMLClass) entityElement;
 			processElements( xmlClass.getJoinedSubclass(), container );
 			processElements( xmlClass.getSubclass(), container );
 			processElements( xmlClass.getUnionSubclass(), container );
 		}
 		else if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
 			final XMLSubclassElement xmlSubclass = (XMLSubclassElement) entityElement;
 			processElements( xmlSubclass.getSubclass(), container );
 		}
 		else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
 			final XMLJoinedSubclassElement xmlJoinedSubclass = (XMLJoinedSubclassElement) entityElement;
 			processElements( xmlJoinedSubclass.getJoinedSubclass(), container );
 		}
 		else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
 			final XMLUnionSubclassElement xmlUnionSubclass = (XMLUnionSubclassElement) entityElement;
 			processElements( xmlUnionSubclass.getUnionSubclass(), container );
 		}
 	}
 
-	private void processElements(List subElements, SubEntityContainer container) {
+	private void processElements(List subElements, SubclassEntityContainer container) {
 		for ( Object subElementO : subElements ) {
 			final SubEntityElement subElement = (SubEntityElement) subElementO;
-			final EntityHierarchySubEntity subEntityDescriptor = new EntityHierarchySubEntity(
-					subElement,
-					currentMappingDocument
-			);
-			container.addSubEntityDescriptor( subEntityDescriptor );
-			final String subEntityName = subEntityDescriptor.getEntitySourceInformation().getMappedEntityName();
-			subEntityContainerMap.put( subEntityName, subEntityDescriptor );
+			final SubclassEntitySourceImpl subclassEntitySource = new SubclassEntitySourceImpl( currentMappingDocument, subElement );
+			container.add( subclassEntitySource );
+			final String subEntityName = subclassEntitySource.getEntityName();
+			subEntityContainerMap.put( subEntityName, subclassEntitySource );
 		}
 	}
 
 	private static class ExtendsQueueEntry {
-		private final EntityHierarchySubEntity subEntityDescriptor;
+		private final SubclassEntitySource subClassEntitySource;
 		private final String entityItExtends;
 
-		private ExtendsQueueEntry(EntityHierarchySubEntity subEntityDescriptor, String entityItExtends) {
-			this.subEntityDescriptor = subEntityDescriptor;
+		private ExtendsQueueEntry(SubclassEntitySource subClassEntitySource, String entityItExtends) {
+			this.subClassEntitySource = subClassEntitySource;
 			this.entityItExtends = entityItExtends;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
new file mode 100644
index 0000000000..870db0c48d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
@@ -0,0 +1,153 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.binder.ToOneAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
+
+/**
+ * Implementation for {@code <many-to-one/> mappings}
+ *
+ * @author Steve Ebersole
+ */
+class ManyToOneAttributeSourceImpl implements ToOneAttributeSource {
+	private final XMLManyToOneElement manyToOneElement;
+	private final LocalBindingContext bindingContext;
+	private final List<RelationalValueSource> valueSources;
+
+	ManyToOneAttributeSourceImpl(final XMLManyToOneElement manyToOneElement, LocalBindingContext bindingContext) {
+		this.manyToOneElement = manyToOneElement;
+		this.bindingContext = bindingContext;
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getColumnAttribute() {
+						return manyToOneElement.getColumn();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return manyToOneElement.getFormula();
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return manyToOneElement.getColumnOrFormula();
+					}
+				},
+				bindingContext
+		);
+	}
+
+	@Override
+	public String getName() {
+			return manyToOneElement.getName();
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		return Helper.TO_ONE_ATTRIBUTE_TYPE_SOURCE;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return manyToOneElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return manyToOneElement.isInsert();
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return manyToOneElement.isUpdate();
+	}
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		return PropertyGeneration.NEVER;
+	}
+
+	@Override
+	public boolean isLazy() {
+		return false;
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return manyToOneElement.isOptimisticLock();
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyle() {
+		return Helper.interpretCascadeStyles( manyToOneElement.getCascade(), bindingContext );
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.MANY_TO_ONE;
+	}
+
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( manyToOneElement.getMeta() );
+	}
+
+	@Override
+	public String getReferencedEntityName() {
+		return manyToOneElement.getClazz() != null
+				? manyToOneElement.getClazz()
+				: manyToOneElement.getEntityName();
+	}
+
+	@Override
+	public String getReferencedEntityAttributeName() {
+		return manyToOneElement.getPropertyRef();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java
new file mode 100644
index 0000000000..79905e29b0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java
@@ -0,0 +1,161 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.binder.SingularAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
+
+/**
+ * Implementation for {@code <property/>} mappings
+ *
+ * @author Steve Ebersole
+ */
+class PropertyAttributeSourceImpl implements SingularAttributeSource {
+	private final XMLPropertyElement propertyElement;
+	private final ExplicitHibernateTypeSource typeSource;
+	private final List<RelationalValueSource> valueSources;
+
+	PropertyAttributeSourceImpl(final XMLPropertyElement propertyElement, LocalBindingContext bindingContext) {
+		this.propertyElement = propertyElement;
+		this.typeSource = new ExplicitHibernateTypeSource() {
+			private final String name = propertyElement.getTypeAttribute() != null
+					? propertyElement.getTypeAttribute()
+					: propertyElement.getType() != null
+							? propertyElement.getType().getName()
+							: null;
+			private final Map<String, String> parameters = ( propertyElement.getType() != null )
+					? Helper.extractParameters( propertyElement.getType().getParam() )
+					: null;
+
+			@Override
+			public String getName() {
+				return name;
+			}
+
+			@Override
+			public Map<String, String> getParameters() {
+				return parameters;
+			}
+		};
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getColumnAttribute() {
+						return propertyElement.getColumn();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return propertyElement.getFormula();
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return propertyElement.getColumnOrFormula();
+					}
+				},
+				bindingContext
+		);
+	}
+
+	@Override
+	public String getName() {
+		return propertyElement.getName();
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		return typeSource;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return propertyElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return Helper.getBooleanValue( propertyElement.isInsert(), true );
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return Helper.getBooleanValue( propertyElement.isUpdate(), true );
+	}
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		return PropertyGeneration.parse( propertyElement.getGenerated() );
+	}
+
+	@Override
+	public boolean isLazy() {
+		return Helper.getBooleanValue( propertyElement.isLazy(), false );
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return Helper.getBooleanValue( propertyElement.isOptimisticLock(), true );
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyle() {
+		return Helper.NO_CASCADING;
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.BASIC;
+	}
+
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( propertyElement.getMeta() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
new file mode 100644
index 0000000000..0ad2ac575f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
@@ -0,0 +1,178 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.EntityMode;
+import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.metamodel.binding.Caching;
+import org.hibernate.metamodel.binding.IdGenerator;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.IdentifierSource;
+import org.hibernate.metamodel.source.binder.RootEntitySource;
+import org.hibernate.metamodel.source.binder.SimpleIdentifierSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeSource;
+import org.hibernate.metamodel.source.binder.TableSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
+
+/**
+ * @author Steve Ebersole
+ */
+public class RootEntitySourceImpl extends AbstractEntitySourceImpl implements RootEntitySource {
+	protected RootEntitySourceImpl(MappingDocument sourceMappingDocument, XMLHibernateMapping.XMLClass entityElement) {
+		super( sourceMappingDocument, entityElement );
+	}
+
+	@Override
+	protected XMLHibernateMapping.XMLClass entityElement() {
+		return (XMLHibernateMapping.XMLClass) super.entityElement();
+	}
+
+	@Override
+	public IdentifierSource getIdentifierSource() {
+		if ( entityElement().getId() != null ) {
+			return new SimpleIdentifierSource() {
+				@Override
+				public SingularAttributeSource getIdentifierAttributeSource() {
+					return new SingularIdentifierAttributeSourceImpl( entityElement().getId(), sourceMappingDocument().getMappingLocalBindingContext() );
+				}
+
+				@Override
+				public IdGenerator getIdentifierGeneratorDescriptor() {
+					if ( entityElement().getId().getGenerator() != null ) {
+						final String generatorName = entityElement().getId().getGenerator().getClazz();
+						IdGenerator idGenerator = sourceMappingDocument().getMappingLocalBindingContext()
+								.getMetadataImplementor()
+								.getIdGenerator( generatorName );
+						if ( idGenerator == null ) {
+							idGenerator = new IdGenerator(
+									getEntityName() + generatorName,
+									generatorName,
+									Helper.extractParameters( entityElement().getId().getGenerator().getParam() )
+							);
+						}
+						return idGenerator;
+					}
+					return null;
+				}
+
+				@Override
+				public Nature getNature() {
+					return Nature.SIMPLE;
+				}
+			};
+		}
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public SingularAttributeSource getVersioningAttributeSource() {
+		if ( entityElement().getVersion() != null ) {
+			return new VersionAttributeSourceImpl( entityElement().getVersion(), sourceMappingDocument().getMappingLocalBindingContext() );
+		}
+		else if ( entityElement().getTimestamp() != null ) {
+			return new TimestampAttributeSourceImpl( entityElement().getTimestamp(), sourceMappingDocument().getMappingLocalBindingContext() );
+		}
+		return null;
+	}
+
+	@Override
+	public SingularAttributeSource getDiscriminatorAttributeSource() {
+		// todo : implement
+		return null;
+	}
+
+	@Override
+	public EntityMode getEntityMode() {
+		return determineEntityMode();
+	}
+
+	@Override
+	public boolean isMutable() {
+		return entityElement().isMutable();
+	}
+
+
+	@Override
+	public boolean isExplicitPolymorphism() {
+		return "explicit".equals( entityElement().getPolymorphism() );
+	}
+
+	@Override
+	public String getWhere() {
+		return entityElement().getWhere();
+	}
+
+	@Override
+	public String getRowId() {
+		return entityElement().getRowid();
+	}
+
+	@Override
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		final String optimisticLockModeString = Helper.getStringValue( entityElement().getOptimisticLock(), "version" );
+		try {
+			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
+		}
+		catch (Exception e) {
+			throw new MappingException(
+					"Unknown optimistic-lock value : " + optimisticLockModeString,
+					sourceMappingDocument().getOrigin()
+			);
+		}
+	}
+
+	@Override
+	public Caching getCaching() {
+		final XMLCacheElement cache = entityElement().getCache();
+		if ( cache == null ) {
+			return null;
+		}
+		final String region = cache.getRegion() != null ? cache.getRegion() : getEntityName();
+		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
+		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
+		return new Caching( region, accessType, cacheLazyProps );
+	}
+
+	@Override
+	public TableSource getPrimaryTable() {
+		return new TableSource() {
+			@Override
+			public String getExplicitSchemaName() {
+				return entityElement().getSchema();
+			}
+
+			@Override
+			public String getExplicitCatalogName() {
+				return entityElement().getCatalog();
+			}
+
+			@Override
+			public String getExplicitTableName() {
+				return entityElement().getTable();
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java
new file mode 100644
index 0000000000..bd326ba057
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java
@@ -0,0 +1,165 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.binder.SingularAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
+
+/**
+ * Implementation for {@code <id/>} mappings
+ *
+ * @author Steve Ebersole
+ */
+class SingularIdentifierAttributeSourceImpl implements SingularAttributeSource {
+	private final XMLHibernateMapping.XMLClass.XMLId idElement;
+	private final ExplicitHibernateTypeSource typeSource;
+	private final List<RelationalValueSource> valueSources;
+
+	public SingularIdentifierAttributeSourceImpl(
+			final XMLHibernateMapping.XMLClass.XMLId idElement,
+			LocalBindingContext bindingContext) {
+		this.idElement = idElement;
+		this.typeSource = new ExplicitHibernateTypeSource() {
+			private final String name = idElement.getTypeAttribute() != null
+					? idElement.getTypeAttribute()
+					: idElement.getType() != null
+							? idElement.getType().getName()
+							: null;
+			private final Map<String, String> parameters = ( idElement.getType() != null )
+					? Helper.extractParameters( idElement.getType().getParam() )
+					: null;
+
+			@Override
+			public String getName() {
+				return name;
+			}
+
+			@Override
+			public Map<String, String> getParameters() {
+				return parameters;
+			}
+		};
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
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
+				bindingContext
+		);
+	}
+
+	@Override
+	public String getName() {
+		return idElement.getName() == null
+				? "id"
+				: idElement.getName();
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		return typeSource;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return idElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return true;
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return false;
+	}
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		return PropertyGeneration.INSERT;
+	}
+
+	@Override
+	public boolean isLazy() {
+		return false;
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return false;
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyle() {
+		return Helper.NO_CASCADING;
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.BASIC;
+	}
+
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( idElement.getMeta() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
new file mode 100644
index 0000000000..4472d793bd
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.metamodel.source.binder.SubclassEntitySource;
+import org.hibernate.metamodel.source.binder.TableSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SubclassEntitySourceImpl extends AbstractEntitySourceImpl implements SubclassEntitySource {
+	protected SubclassEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
+		super( sourceMappingDocument, entityElement );
+	}
+
+	@Override
+	public TableSource getPrimaryTable() {
+		if ( XMLJoinedSubclassElement.class.isInstance( entityElement() ) ) {
+			return new TableSource() {
+				@Override
+				public String getExplicitSchemaName() {
+					return ( (XMLJoinedSubclassElement) entityElement() ).getSchema();
+				}
+
+				@Override
+				public String getExplicitCatalogName() {
+					return ( (XMLJoinedSubclassElement) entityElement() ).getCatalog();
+				}
+
+				@Override
+				public String getExplicitTableName() {
+					return ( (XMLJoinedSubclassElement) entityElement() ).getTable();
+				}
+			};
+		}
+		else if ( XMLUnionSubclassElement.class.isInstance( entityElement() ) ) {
+			return new TableSource() {
+				@Override
+				public String getExplicitSchemaName() {
+					return ( (XMLUnionSubclassElement) entityElement() ).getSchema();
+				}
+
+				@Override
+				public String getExplicitCatalogName() {
+					return ( (XMLUnionSubclassElement) entityElement() ).getCatalog();
+				}
+
+				@Override
+				public String getExplicitTableName() {
+					return ( (XMLUnionSubclassElement) entityElement() ).getTable();
+				}
+			};
+		}
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
new file mode 100644
index 0000000000..16c297d87f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
@@ -0,0 +1,176 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.internal.util.Value;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.binder.SingularAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
+
+/**
+ * Implementation for {@code <timestamp/>} mappings
+ *
+ * @author Steve Ebersole
+ */
+class TimestampAttributeSourceImpl implements SingularAttributeSource {
+	private final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement;
+	private final LocalBindingContext bindingContext;
+	private final List<RelationalValueSource> valueSources;
+
+	TimestampAttributeSourceImpl(
+			final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement,
+			LocalBindingContext bindingContext) {
+		this.timestampElement = timestampElement;
+		this.bindingContext = bindingContext;
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getColumnAttribute() {
+						return timestampElement.getColumn();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return null;
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return null;
+					}
+				},
+				bindingContext
+		);
+	}
+
+	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
+		@Override
+		public String getName() {
+			return "db".equals( timestampElement.getSource() ) ? "dbtimestamp" : "timestamp";
+		}
+
+		@Override
+		public Map<String, String> getParameters() {
+			return null;
+		}
+	};
+
+	@Override
+	public String getName() {
+		return timestampElement.getName();
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		return typeSource;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return timestampElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return true;
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return true;
+	}
+
+	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
+			new Value.DeferredInitializer<PropertyGeneration>() {
+				@Override
+				public PropertyGeneration initialize() {
+					final PropertyGeneration propertyGeneration = timestampElement.getGenerated() == null
+							? PropertyGeneration.NEVER
+							: PropertyGeneration.parse( timestampElement.getGenerated().value() );
+					if ( propertyGeneration == PropertyGeneration.INSERT ) {
+						throw new MappingException(
+								"'generated' attribute cannot be 'insert' for versioning property",
+								bindingContext.getOrigin()
+						);
+					}
+					return propertyGeneration;
+				}
+			}
+	);
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		return propertyGenerationValue.getValue();
+	}
+
+	@Override
+	public boolean isLazy() {
+		return false;
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return false;
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyle() {
+		return Helper.NO_CASCADING;
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.BASIC;
+	}
+
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( timestampElement.getMeta() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
new file mode 100644
index 0000000000..f6bf322520
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
@@ -0,0 +1,176 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.internal.util.Value;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.binder.SingularAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
+
+/**
+ * Implementation for {@code <version/>} mappings
+ *
+ * @author Steve Ebersole
+ */
+class VersionAttributeSourceImpl implements SingularAttributeSource {
+	private final XMLHibernateMapping.XMLClass.XMLVersion versionElement;
+	private final LocalBindingContext bindingContext;
+	private final List<RelationalValueSource> valueSources;
+
+	VersionAttributeSourceImpl(
+			final XMLHibernateMapping.XMLClass.XMLVersion versionElement,
+			LocalBindingContext bindingContext) {
+		this.versionElement = versionElement;
+		this.bindingContext = bindingContext;
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getColumnAttribute() {
+						return versionElement.getColumnAttribute();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return null;
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return versionElement.getColumn();
+					}
+				},
+				bindingContext
+		);
+	}
+
+	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
+		@Override
+		public String getName() {
+			return versionElement.getType() == null ? "integer" : versionElement.getType();
+		}
+
+		@Override
+		public Map<String, String> getParameters() {
+			return null;
+		}
+	};
+
+	@Override
+	public String getName() {
+		return versionElement.getName();
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		return typeSource;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return versionElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return Helper.getBooleanValue( versionElement.isInsert(), true );
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return true;
+	}
+
+	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
+			new Value.DeferredInitializer<PropertyGeneration>() {
+				@Override
+				public PropertyGeneration initialize() {
+					final PropertyGeneration propertyGeneration = versionElement.getGenerated() == null
+							? PropertyGeneration.NEVER
+							: PropertyGeneration.parse( versionElement.getGenerated().value() );
+					if ( propertyGeneration == PropertyGeneration.INSERT ) {
+						throw new MappingException(
+								"'generated' attribute cannot be 'insert' for versioning property",
+								bindingContext.getOrigin()
+						);
+					}
+					return propertyGeneration;
+				}
+			}
+	);
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		return propertyGenerationValue.getValue();
+	}
+
+	@Override
+	public boolean isLazy() {
+		return false;
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return false;
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyle() {
+		return Helper.NO_CASCADING;
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.BASIC;
+	}
+
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( versionElement.getMeta() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index c6ccd24d2a..f05293ab26 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,594 +1,599 @@
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
 import org.hibernate.metamodel.source.annotations.AnnotationProcessor;
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
 
 	private final Database database;
 
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
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			sourceProcessors = new SourceProcessor[] {
 					new HbmSourceProcessorImpl( this ),
 					new AnnotationProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
 					new AnnotationProcessor( this ),
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
+	public String qualifyClassName(String name) {
+		return name;
+	}
+
+	@Override
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index 250420892d..db8f41d1fd 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,93 +1,92 @@
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
-		entityBinding.setRoot( true );
 		entityBinding.setEntity( entity );
 		entityBinding.setBaseTable( table );
 
 		SingularAttribute idAttribute = entity.locateOrCreateSingularAttribute( "id" );
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( idAttribute );
 		attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( "long" );
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
index 87567e9910..c29262dcd4 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
@@ -1,141 +1,146 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import com.fasterxml.classmate.MemberResolver;
 import com.fasterxml.classmate.ResolvedType;
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import com.fasterxml.classmate.TypeResolver;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
 public class TestAnnotationsBindingContextImpl implements AnnotationBindingContext {
 	private Index index;
 	private ServiceRegistry serviceRegistry;
 
 	private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 
 	private final TypeResolver typeResolver = new TypeResolver();
 	private final Map<Class<?>, ResolvedType> resolvedTypeCache = new HashMap<Class<?>, ResolvedType>();
 
 	public TestAnnotationsBindingContextImpl(Index index, ServiceRegistry serviceRegistry) {
 		this.index = index;
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public Index getIndex() {
 		return index;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return serviceRegistry.getService( ClassLoaderService.class ).classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(String className) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
+	public String qualifyClassName(String name) {
+		return name;
+	}
+
+	@Override
 	public ClassInfo getClassInfo(String name) {
 		DotName dotName = DotName.createSimple( name );
 		return index.getClassByName( dotName );
 	}
 
 	@Override
 	public void resolveAllTypes(String className) {
 		// the resolved type for the top level class in the hierarchy
 		Class<?> clazz = locateClassByName( className );
 		ResolvedType resolvedType = typeResolver.resolve( clazz );
 		while ( resolvedType != null ) {
 			// todo - check whether there is already something in the map
 			resolvedTypeCache.put( clazz, resolvedType );
 			resolvedType = resolvedType.getParentClass();
 			if ( resolvedType != null ) {
 				clazz = resolvedType.getErasedType();
 			}
 		}
 	}
 
 	@Override
 	public ResolvedType getResolvedType(Class<?> clazz) {
 		return resolvedTypeCache.get( clazz );
 	}
 
 	@Override
 	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
 		// todo : is there a reason we create this resolver every time?
 		MemberResolver memberResolver = new MemberResolver( typeResolver );
 		return memberResolver.resolve( type, null, null );
 	}
 
 	@Override
 	public boolean isGloballyQuotedIdentifiers() {
 		return false;
 	}
 }
