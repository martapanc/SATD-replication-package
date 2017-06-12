diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index 34c988e394..ecfe6932f5 100644
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
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
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
 
 	public static CacheDataDescriptionImpl decode(AbstractPluralAttributeBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
-				model.getEntityBinding().isVersioned(),
-				getVersionComparator( model.getEntityBinding() )
+				model.getContainer().seekEntityBinding().isVersioned(),
+				getVersionComparator( model.getContainer().seekEntityBinding() )
 		);
 	}
 
 	private static Comparator getVersionComparator(EntityBinding model ) {
 		Comparator versionComparator = null;
 		if ( model.isVersioned() ) {
 			versionComparator = (
 					( VersionType ) model.getHierarchyDetails()
 							.getVersioningAttributeBinding()
 							.getHibernateTypeDescriptor()
 							.getResolvedTypeMapping()
 			).getComparator();
 		}
 		return versionComparator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
index 4363aae9bc..4ae33f270a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
@@ -1,150 +1,138 @@
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
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * Basic support for {@link AttributeBinding} implementors
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeBinding implements AttributeBinding {
-	private final EntityBinding entityBinding;
+	private final AttributeBindingContainer container;
 	private final Attribute attribute;
 
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 	private final Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
 	private boolean includedInOptimisticLocking;
 
 	private boolean isLazy;
 	private String propertyAccessorName;
 	private boolean isAlternateUniqueKey;
 
 	private MetaAttributeContext metaAttributeContext;
 
-	protected AbstractAttributeBinding(EntityBinding entityBinding, Attribute attribute) {
-		this.entityBinding = entityBinding;
+	protected AbstractAttributeBinding(AttributeBindingContainer container, Attribute attribute) {
+		this.container = container;
 		this.attribute = attribute;
 	}
 
 	@Override
-	public EntityBinding getEntityBinding() {
-		return entityBinding;
+	public AttributeBindingContainer getContainer() {
+		return container;
 	}
 
 	@Override
 	public Attribute getAttribute() {
 		return attribute;
 	}
 
 	@Override
 	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
 		return hibernateTypeDescriptor;
 	}
 
 	@Override
 	public boolean isBasicPropertyAccessor() {
 		return propertyAccessorName == null || "property".equals( propertyAccessorName );
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	public void setPropertyAccessorName(String propertyAccessorName) {
 		this.propertyAccessorName = propertyAccessorName;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return includedInOptimisticLocking;
 	}
 
 	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
 		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
-	protected boolean forceNonNullable() {
-		return false;
-	}
-
-	protected boolean forceUnique() {
-		return false;
-	}
-
-	protected final boolean isPrimaryKey() {
-		return this == getEntityBinding().getHierarchyDetails().getEntityIdentifier().getValueBinding();
-	}
-
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	@Override
 	public boolean isAlternateUniqueKey() {
 		return isAlternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean alternateUniqueKey) {
 		this.isAlternateUniqueKey = alternateUniqueKey;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public void setLazy(boolean isLazy) {
 		this.isLazy = isLazy;
 	}
 
 	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding referencingAttributeBinding) {
 		entityReferencingAttributeBindings.add( referencingAttributeBinding );
 	}
 
 	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBindingContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBindingContainer.java
new file mode 100644
index 0000000000..22571f71a1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBindingContainer.java
@@ -0,0 +1,114 @@
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
+package org.hibernate.metamodel.binding;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.SingularAttribute;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractAttributeBindingContainer implements AttributeBindingContainer {
+	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
+
+	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
+		attributeBindingMap.put( name, attributeBinding );
+	}
+
+	@Override
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
+		return makeSimpleAttributeBinding( attribute, false, false );
+	}
+
+	private SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute, boolean forceNonNullable, boolean forceUnique) {
+		final SimpleSingularAttributeBinding binding = new SimpleSingularAttributeBinding(
+				this,
+				attribute,
+				forceNonNullable,
+				forceUnique
+		);
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
+		final ComponentAttributeBinding binding = new ComponentAttributeBinding( this, attribute );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		final BagBinding binding = new BagBinding( this, attribute, nature );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public AttributeBinding locateAttributeBinding(String name) {
+		return attributeBindingMap.get( name );
+	}
+
+	@Override
+	public Iterable<AttributeBinding> attributeBindings() {
+		return attributeBindingMap.values();
+	}
+
+	/**
+	 * Gets the number of attribute bindings defined on this class, including the
+	 * identifier attribute binding and attribute bindings defined
+	 * as part of a join.
+	 *
+	 * @return The number of attribute bindings
+	 */
+	public int getAttributeBindingClosureSpan() {
+		// TODO: fix this after HHH-6337 is fixed; for now just return size of attributeBindingMap
+		// if this is not a root, then need to include the superclass attribute bindings
+		return attributeBindingMap.size();
+	}
+
+	/**
+	 * Gets the attribute bindings defined on this class, including the
+	 * identifier attribute binding and attribute bindings defined
+	 * as part of a join.
+	 *
+	 * @return The attribute bindings.
+	 */
+	public Iterable<AttributeBinding> getAttributeBindingClosure() {
+		// TODO: fix this after HHH-6337 is fixed. for now, just return attributeBindings
+		// if this is not a root, then need to include the superclass attribute bindings
+		return attributeBindings();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
index ca141eaad1..ee5466a53f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
@@ -1,298 +1,298 @@
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
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.relational.Table;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
 	private CollectionKey collectionKey;
 	private CollectionElement collectionElement;
 
 	private Table collectionTable;
 
 	private CascadeStyle cascadeStyle;
 	private FetchMode fetchMode;
 
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String referencedPropertyName;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private final java.util.Map filters = new HashMap();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	private CustomSQL customSQLInsert;
 	private CustomSQL customSQLUpdate;
 	private CustomSQL customSQLDelete;
 	private CustomSQL customSQLDeleteAll;
 
 	private String loaderName;
 
 	protected AbstractPluralAttributeBinding(
-			EntityBinding entityBinding,
+			AttributeBindingContainer container,
 			PluralAttribute attribute,
 			CollectionElementNature collectionElementNature) {
-		super( entityBinding, attribute );
+		super( container, attribute );
 		this.collectionElement = interpretNature( collectionElementNature );
 	}
 
 	private CollectionElement interpretNature(CollectionElementNature collectionElementNature) {
 		switch ( collectionElementNature ) {
 			case BASIC: {
 				return new BasicCollectionElement( this );
 			}
 			case COMPOSITE: {
 				return new CompositeCollectionElement( this );
 			}
 			case ONE_TO_MANY: {
 				return new OneToManyCollectionElement( this );
 			}
 			case MANY_TO_MANY: {
 				return new ManyToManyCollectionElement( this );
 			}
 			case MANY_TO_ANY: {
 				return new ManyToAnyCollectionElement( this );
 			}
 			default: {
 				throw new AssertionFailure( "Unknown collection element nature : " + collectionElementNature );
 			}
 		}
 	}
 
 //	protected void initializeBinding(PluralAttributeBindingState state) {
 //		super.initialize( state );
 //		fetchMode = state.getFetchMode();
 //		extraLazy = state.isExtraLazy();
 //		collectionElement.setNodeName( state.getElementNodeName() );
 //		collectionElement.setTypeName( state.getElementTypeName() );
 //		inverse = state.isInverse();
 //		mutable = state.isMutable();
 //		subselectLoadable = state.isSubselectLoadable();
 //		if ( isSubselectLoadable() ) {
 //			getEntityBinding().setSubselectLoadableCollections( true );
 //		}
 //		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
 //		cacheRegionName = state.getCacheRegionName();
 //		orderBy = state.getOrderBy();
 //		where = state.getWhere();
 //		referencedPropertyName = state.getReferencedPropertyName();
 //		sorted = state.isSorted();
 //		comparator = state.getComparator();
 //		comparatorClassName = state.getComparatorClassName();
 //		orphanDelete = state.isOrphanDelete();
 //		batchSize = state.getBatchSize();
 //		embedded = state.isEmbedded();
 //		optimisticLocked = state.isOptimisticLocked();
 //		collectionPersisterClass = state.getCollectionPersisterClass();
 //		filters.putAll( state.getFilters() );
 //		synchronizedTables.addAll( state.getSynchronizedTables() );
 //		customSQLInsert = state.getCustomSQLInsert();
 //		customSQLUpdate = state.getCustomSQLUpdate();
 //		customSQLDelete = state.getCustomSQLDelete();
 //		customSQLDeleteAll = state.getCustomSQLDeleteAll();
 //		loaderName = state.getLoaderName();
 //	}
 
 	@Override
 	public boolean isAssociation() {
 		return collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_ANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_MANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table collectionTable) {
 		this.collectionTable = collectionTable;
 	}
 
 	public CollectionKey getCollectionKey() {
 		return collectionKey;
 	}
 
 	public void setCollectionKey(CollectionKey collectionKey) {
 		this.collectionKey = collectionKey;
 	}
 
 	public CollectionElement getCollectionElement() {
 		return collectionElement;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle() {
 		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
 		for ( CascadeStyle style : cascadeStyles ) {
 			if ( style != CascadeStyle.NONE ) {
 				cascadeStyleList.add( style );
 			}
 		}
 		if ( cascadeStyleList.isEmpty() ) {
 			cascadeStyle = CascadeStyle.NONE;
 		}
 		else if ( cascadeStyleList.size() == 1 ) {
 			cascadeStyle = cascadeStyleList.get( 0 );
 		}
 		else {
 			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
 					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
 			);
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName;
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		return comparator;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public boolean isOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
 	}
 
 	public java.util.Map getFilterMap() {
 		return filters;
 	}
 
 	public CustomSQL getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public CustomSQL getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public CustomSQL getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public CustomSQL getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java
index 29ef6b0cfd..ef7fb1fded 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java
@@ -1,113 +1,113 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.relational.Value;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractSingularAttributeBinding
 		extends AbstractAttributeBinding
 		implements SingularAttributeBinding {
 
 	private Value value;
 	private List<SimpleValueBinding> simpleValueBindings = new ArrayList<SimpleValueBinding>();
 
 	private boolean hasDerivedValue;
 	private boolean isNullable = true;
 
-	protected AbstractSingularAttributeBinding(EntityBinding entityBinding, SingularAttribute attribute) {
-		super( entityBinding, attribute );
+	protected AbstractSingularAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
+		super( container, attribute );
 	}
 
 	@Override
 	public SingularAttribute getAttribute() {
 		return (SingularAttribute) super.getAttribute();
 	}
 
 	public Value getValue() {
 		return value;
 	}
 
 	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings) {
 		List<SimpleValue> values = new ArrayList<SimpleValue>();
 		for ( SimpleValueBinding simpleValueBinding : simpleValueBindings ) {
 			this.simpleValueBindings.add( simpleValueBinding );
 			values.add( simpleValueBinding.getSimpleValue() );
 			this.hasDerivedValue = this.hasDerivedValue || simpleValueBinding.isDerived();
 			this.isNullable = this.isNullable && simpleValueBinding.isNullable();
 		}
 		if ( values.size() == 1 ) {
 			this.value = values.get( 0 );
 		}
 		else {
 			final Tuple tuple = values.get( 0 ).getTable().createTuple( getRole() );
 			for ( SimpleValue value : values ) {
 				tuple.addValue( value );
 			}
 			this.value = tuple;
 		}
 	}
 
 	private String getRole() {
-		return getEntityBinding().getEntity().getName() + '.' + getAttribute().getName();
+		return getContainer().getPathBase() + '.' + getAttribute().getName();
 	}
 
 	@Override
 	public int getSimpleValueSpan() {
 		checkValueBinding();
 		return simpleValueBindings.size();
 	}
 
-	private void checkValueBinding() {
+	protected void checkValueBinding() {
 		if ( value == null ) {
 			throw new AssertionFailure( "No values yet bound!" );
 		}
 	}
 
 	@Override
 	public Iterable<SimpleValueBinding> getSimpleValueBindings() {
 		return simpleValueBindings;
 	}
 
 	@Override
 	public boolean hasDerivedValue() {
 		checkValueBinding();
 		return hasDerivedValue;
 	}
 
 	@Override
 	public boolean isNullable() {
 		checkValueBinding();
 		return isNullable;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
index 8903e32daf..5d317cc01e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
@@ -1,89 +1,89 @@
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
 
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * The basic contract for binding a {@link #getAttribute() attribute} from the domain model to the relational model.
  *
  * @author Steve Ebersole
  */
 public interface AttributeBinding {
 	/**
 	 * Obtain the entity binding to which this attribute binding exists.
 	 *
 	 * @return The entity binding.
 	 */
-	public EntityBinding getEntityBinding();
+	public AttributeBindingContainer getContainer();
 
 	/**
 	 * Obtain the attribute bound.
 	 *
 	 * @return The attribute.
 	 */
 	public Attribute getAttribute();
 
 	/**
 	 * Obtain the descriptor for the Hibernate {@link org.hibernate.type.Type} for this binding.
 	 * <p/>
 	 * For information about the Java type, query the {@link Attribute} obtained from {@link #getAttribute()}
 	 * instead.
 	 *
 	 * @return The type descriptor
 	 */
 	public HibernateTypeDescriptor getHibernateTypeDescriptor();
 
 	public boolean isAssociation();
 
 	public boolean isBasicPropertyAccessor();
 
 	public String getPropertyAccessorName();
 
 	public void setPropertyAccessorName(String propertyAccessorName);
 
 	public boolean isIncludedInOptimisticLocking();
 
 	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking);
 
 	/**
 	 * Obtain the meta attributes associated with this binding
 	 *
 	 * @return The meta attributes
 	 */
 	public MetaAttributeContext getMetaAttributeContext();
 
 	public boolean isAlternateUniqueKey();
 
 	public boolean isLazy();
 
 	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding attributeBinding);
 
 	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings();
 
 	public void validate();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java
new file mode 100644
index 0000000000..0465a05241
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java
@@ -0,0 +1,61 @@
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
+package org.hibernate.metamodel.binding;
+
+import org.hibernate.metamodel.domain.AttributeContainer;
+import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.SingularAttribute;
+import org.hibernate.metamodel.relational.TableSpecification;
+import org.hibernate.metamodel.source.MetaAttributeContext;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AttributeBindingContainer {
+	public String getPathBase();
+
+	public AttributeContainer getAttributeContainer();
+
+	public Iterable<AttributeBinding> attributeBindings();
+
+	public AttributeBinding locateAttributeBinding(String name);
+
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute);
+
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute);
+
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute);
+
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
+
+	public EntityBinding seekEntityBinding();
+
+	public TableSpecification getPrimaryTable();
+
+	public TableSpecification locateTable(String containingTableName);
+
+	public Class<?> getClassReference();
+
+	public MetaAttributeContext getMetaAttributeContext();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
index 633339a80f..099fb5110c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
@@ -1,37 +1,37 @@
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
 
 import org.hibernate.metamodel.domain.PluralAttribute;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class BagBinding extends AbstractPluralAttributeBinding {
-	protected BagBinding(EntityBinding entityBinding, PluralAttribute attribute, CollectionElementNature nature) {
-		super( entityBinding, attribute, nature );
+	protected BagBinding(AttributeBindingContainer container, PluralAttribute attribute, CollectionElementNature nature) {
+		super( container, attribute, nature );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java
new file mode 100644
index 0000000000..43de669f6e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java
@@ -0,0 +1,167 @@
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
+package org.hibernate.metamodel.binding;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.domain.AttributeContainer;
+import org.hibernate.metamodel.domain.Component;
+import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.SingularAttribute;
+import org.hibernate.metamodel.relational.TableSpecification;
+import org.hibernate.metamodel.source.MetaAttributeContext;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ComponentAttributeBinding extends AbstractSingularAttributeBinding implements AttributeBindingContainer {
+	private final String path;
+	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
+
+	private SingularAttribute parentReference;
+
+	private MetaAttributeContext metaAttributeContext;
+
+	public ComponentAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
+		super( container, attribute );
+		this.path = container.getPathBase() + '.' + attribute.getName();
+	}
+
+	@Override
+	public EntityBinding seekEntityBinding() {
+		return getContainer().seekEntityBinding();
+	}
+
+	@Override
+	public String getPathBase() {
+		return path;
+	}
+
+	@Override
+	public AttributeContainer getAttributeContainer() {
+		return getComponent();
+	}
+
+	public Component getComponent() {
+		return (Component) getAttribute().getSingularAttributeType();
+	}
+
+	@Override
+	public boolean isAssociation() {
+		return false;
+	}
+
+	@Override
+	public MetaAttributeContext getMetaAttributeContext() {
+		return metaAttributeContext;
+	}
+
+	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
+		this.metaAttributeContext = metaAttributeContext;
+	}
+
+	@Override
+	public TableSpecification getPrimaryTable() {
+		return getContainer().getPrimaryTable();
+	}
+
+	@Override
+	public TableSpecification locateTable(String containingTableName) {
+		return getContainer().locateTable( containingTableName );
+	}
+
+	@Override
+	public AttributeBinding locateAttributeBinding(String name) {
+		return attributeBindingMap.get( name );
+	}
+
+	@Override
+	public Iterable<AttributeBinding> attributeBindings() {
+		return attributeBindingMap.values();
+	}
+
+	@Override
+	protected void checkValueBinding() {
+		// do nothing here...
+	}
+
+	@Override
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
+		final SimpleSingularAttributeBinding binding = new SimpleSingularAttributeBinding(
+				this,
+				attribute,
+				isNullable(),
+				isAlternateUniqueKey() // todo : is this accurate?
+		);
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
+		// todo : hook this into the EntityBinding notion of "entity referencing attribute bindings"
+		attributeBindingMap.put( name, attributeBinding );
+	}
+
+	@Override
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
+		final ComponentAttributeBinding binding = new ComponentAttributeBinding( this, attribute );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		final BagBinding binding = new BagBinding( this, attribute, nature );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
+	public Class<?> getClassReference() {
+		return getComponent().getClassReference();
+	}
+
+	public SingularAttribute getParentReference() {
+		return parentReference;
+	}
+
+	public void setParentReference(SingularAttribute parentReference) {
+		this.parentReference = parentReference;
+	}
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		// todo : not sure the correct thing to return here since it essentially relies on the simple sub-attributes.
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 1794ff855c..51cb648cdb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,425 +1,381 @@
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
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Entity;
-import org.hibernate.metamodel.domain.PluralAttribute;
-import org.hibernate.metamodel.domain.SingularAttribute;
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
-public class EntityBinding {
+public class EntityBinding extends AbstractAttributeBindingContainer {
 	private final EntityBinding superEntityBinding;
 	private final HierarchyDetails hierarchyDetails;
 
 	private Entity entity;
 	private TableSpecification baseTable;
 	private Map<String, TableSpecification> secondaryTables = new HashMap<String, TableSpecification>();
 
 	private Value<Class<?>> proxyInterfaceType;
 
 	private String jpaEntityName;
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private String discriminatorMatchValue;
 
-	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
-
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>();
 	private Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
-
 	private MetaAttributeContext metaAttributeContext;
 
 	private boolean lazy;
 	private boolean mutable;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private boolean hasSubselectLoadableCollections;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is the root of an inheritance hierarchy
 	 *
 	 * @param inheritanceType The inheritance type for the hierarchy
 	 * @param entityMode The entity mode used in this hierarchy.
 	 */
 	public EntityBinding(InheritanceType inheritanceType, EntityMode entityMode) {
 		this.superEntityBinding = null;
 		this.hierarchyDetails = new HierarchyDetails( this, inheritanceType, entityMode );
 	}
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is a subclass (sub-entity) in an inheritance hierarchy
 	 *
 	 * @param superEntityBinding The entity binding of this binding's super
 	 */
 	public EntityBinding(EntityBinding superEntityBinding) {
 		this.superEntityBinding = superEntityBinding;
 		this.hierarchyDetails = superEntityBinding.getHierarchyDetails();
 	}
 
 
 	public HierarchyDetails getHierarchyDetails() {
 		return hierarchyDetails;
 	}
 
 	public EntityBinding getSuperEntityBinding() {
 		return superEntityBinding;
 	}
 
 	public boolean isRoot() {
 		return superEntityBinding == null;
 	}
 
 	public Entity getEntity() {
 		return entity;
 	}
 
 	public void setEntity(Entity entity) {
 		this.entity = entity;
 	}
 
-	public TableSpecification getBaseTable() {
+	@Override
+	public TableSpecification getPrimaryTable() {
 		return baseTable;
 	}
 
 	public void setBaseTable(TableSpecification baseTable) {
 		this.baseTable = baseTable;
 	}
 
-	public TableSpecification getTable(String tableName) {
+	public TableSpecification locateTable(String tableName) {
 		if ( tableName == null ) {
 			return baseTable;
 		}
 
 		TableSpecification tableSpec = secondaryTables.get( tableName );
 		if ( tableSpec == null ) {
 		   throw new AssertionFailure( String.format("Unable to find table %s amongst tables %s", tableName, secondaryTables.keySet()) );
 		}
 		return tableSpec;
 	}
 
 	public void addSecondaryTable(String tableName, TableSpecification table) {
 		secondaryTables.put( tableName, table );
 	}
 
 	public boolean isVersioned() {
 		return getHierarchyDetails().getVersioningAttributeBinding() != null;
 	}
 
 	public String getDiscriminatorMatchValue() {
 		return discriminatorMatchValue;
 	}
 
 	public void setDiscriminatorMatchValue(String discriminatorMatchValue) {
 		this.discriminatorMatchValue = discriminatorMatchValue;
 	}
 
-	public Iterable<AttributeBinding> getAttributeBindings() {
-		return attributeBindingMap.values();
-	}
-
-	public AttributeBinding getAttributeBinding(String name) {
-		return attributeBindingMap.get( name );
-	}
-
-	/**
-	 * Gets the number of attribute bindings defined on this class, including the
-	 * identifier attribute binding and attribute bindings defined
-	 * as part of a join.
-	 *
-	 * @return The number of attribute bindings
-	 */
-	public int getAttributeBindingClosureSpan() {
-		// TODO: fix this after HHH-6337 is fixed; for now just return size of attributeBindingMap
-		// if this is not a root, then need to include the superclass attribute bindings
-		return attributeBindingMap.size();
-	}
-
-	/**
-	 * Gets the attribute bindings defined on this class, including the
-	 * identifier attribute binding and attribute bindings defined
-	 * as part of a join.
-	 *
-	 * @return The attribute bindings.
-	 */
-	public Iterable<AttributeBinding> getAttributeBindingClosure() {
-		// TODO: fix this after HHH-6337 is fixed. for now, just return attributeBindings
-		// if this is not a root, then need to include the superclass attribute bindings
-		return getAttributeBindings();
-	}
-
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		filterDefinitions.add( filterDefinition );
 	}
 
 	public Iterable<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
-	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
-		return makeSimpleAttributeBinding( attribute, false, false );
-	}
-
-	private SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute, boolean forceNonNullable, boolean forceUnique) {
-		final SimpleSingularAttributeBinding binding = new SimpleSingularAttributeBinding(
-				this,
-				attribute,
-				forceNonNullable,
-				forceUnique
-		);
-		registerAttributeBinding( attribute.getName(), binding );
-		return binding;
+	@Override
+	public EntityBinding seekEntityBinding() {
+		return this;
 	}
 
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
-		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
-		registerAttributeBinding( attribute.getName(), binding );
-		return binding;
+	@Override
+	public String getPathBase() {
+		return getEntity().getName();
 	}
 
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-		final BagBinding binding = new BagBinding( this, attribute, nature );
-		registerAttributeBinding( attribute.getName(), binding );
-		return binding;
+	@Override
+	public Class<?> getClassReference() {
+		return getEntity().getClassReference();
 	}
 
-	private void registerAttributeBinding(String name, SingularAssociationAttributeBinding attributeBinding) {
-		entityReferencingAttributeBindings.add( attributeBinding );
-		registerAttributeBinding( name, (AttributeBinding) attributeBinding );
+	@Override
+	public AttributeContainer getAttributeContainer() {
+		return getEntity();
 	}
 
-	private void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
-		attributeBindingMap.put( name, attributeBinding );
+	@Override
+	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
+		if ( SingularAssociationAttributeBinding.class.isInstance( attributeBinding ) ) {
+			entityReferencingAttributeBindings.add( (SingularAssociationAttributeBinding) attributeBinding );
+		}
+		super.registerAttributeBinding( name, attributeBinding );
 	}
 
+	@Override
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
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
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
 
 	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
 		this.synchronizedTableNames.addAll( synchronizedTableNames );
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
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityBinding" );
 		sb.append( "{entity=" ).append( entity != null ? entity.getName() : "not set" );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
index f0a02076a9..84f3e86241 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
@@ -1,203 +1,208 @@
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
 import java.util.List;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.domain.SingularAttribute;
 
 /**
  * TODO : javadoc
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class ManyToOneAttributeBinding extends SimpleSingularAttributeBinding implements SingularAssociationAttributeBinding {
 	private String referencedEntityName;
 	private String referencedAttributeName;
 	private AttributeBinding referencedAttributeBinding;
 
 	private boolean isLogicalOneToOne;
 	private String foreignKeyName;
 
 	private CascadeStyle cascadeStyle;
 	private FetchMode fetchMode;
 
-	ManyToOneAttributeBinding(EntityBinding entityBinding, SingularAttribute attribute) {
-		super( entityBinding, attribute, false, false );
+	ManyToOneAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
+		super( container, attribute, false, false );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
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
 	public CascadeStyle getCascadeStyle() {
 		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
 		for ( CascadeStyle style : cascadeStyles ) {
 			if ( style != CascadeStyle.NONE ) {
 				cascadeStyleList.add( style );
 			}
 		}
 		if ( cascadeStyleList.isEmpty() ) {
 			cascadeStyle = CascadeStyle.NONE;
 		}
 		else if ( cascadeStyleList.size() == 1 ) {
 			cascadeStyle = cascadeStyleList.get( 0 );
 		}
 		else {
 			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
 					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
 			);
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	@Override
 	public final boolean isReferenceResolved() {
 		return referencedAttributeBinding != null;
 	}
 
 	@Override
 	public final void resolveReference(AttributeBinding referencedAttributeBinding) {
-		if ( !referencedEntityName.equals( referencedAttributeBinding.getEntityBinding().getEntity().getName() ) ) {
+		if ( ! EntityBinding.class.isInstance( referencedAttributeBinding.getContainer() ) ) {
+			throw new AssertionFailure( "Illegal attempt to resolve many-to-one reference based on non-entity attribute" );
+		}
+		final EntityBinding entityBinding = (EntityBinding) referencedAttributeBinding.getContainer();
+		if ( !referencedEntityName.equals( entityBinding.getEntity().getName() ) ) {
 			throw new IllegalStateException(
 					"attempt to set EntityBinding with name: [" +
-							referencedAttributeBinding.getEntityBinding().getEntity().getName() +
+							entityBinding.getEntity().getName() +
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
 //		buildForeignKey();
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
-		return referencedAttributeBinding.getEntityBinding();
+		return (EntityBinding) referencedAttributeBinding.getContainer();
 	}
 
 //	private void buildForeignKey() {
 //		// TODO: move this stuff to relational model
 //		ForeignKey foreignKey = getValue().getTable()
 //				.createForeignKey( referencedAttributeBinding.getValue().getTable(), foreignKeyName );
 //		Iterator<SimpleValue> referencingValueIterator = getSimpleValues().iterator();
 //		Iterator<SimpleValue> targetValueIterator = referencedAttributeBinding.getSimpleValues().iterator();
 //		while ( referencingValueIterator.hasNext() ) {
 //			if ( !targetValueIterator.hasNext() ) {
 //				// TODO: improve this message
 //				throw new MappingException(
 //						"number of values in many-to-one reference is greater than number of values in target"
 //				);
 //			}
 //			SimpleValue referencingValue = referencingValueIterator.next();
 //			SimpleValue targetValue = targetValueIterator.next();
 //			if ( Column.class.isInstance( referencingValue ) ) {
 //				if ( !Column.class.isInstance( targetValue ) ) {
 //					// TODO improve this message
 //					throw new MappingException( "referencing value is a column, but target is not a column" );
 //				}
 //				foreignKey.addColumnMapping( Column.class.cast( referencingValue ), Column.class.cast( targetValue ) );
 //			}
 //			else if ( Column.class.isInstance( targetValue ) ) {
 //				// TODO: improve this message
 //				throw new MappingException( "referencing value is not a column, but target is a column." );
 //			}
 //		}
 //		if ( targetValueIterator.hasNext() ) {
 //			throw new MappingException( "target value has more simple values than referencing value" );
 //		}
 //	}
 //
 //	public void validate() {
 //		// can't check this until both the domain and relational states are initialized...
 //		if ( getCascadeTypes().contains( CascadeType.DELETE_ORPHAN ) ) {
 //			if ( !isLogicalOneToOne ) {
 //				throw new MappingException(
 //						"many-to-one attribute [" + locateAttribute().getName() + "] does not support orphan delete as it is not unique"
 //				);
 //			}
 //		}
 //		//TODO: validate that the entity reference is resolved
 //	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
index 554a696170..595625248b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
@@ -1,197 +1,197 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.MappingException;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class SimpleSingularAttributeBinding
 		extends AbstractSingularAttributeBinding
 		implements SingularAttributeBinding, KeyValueBinding {
 
 	private String unsavedValue;
 	private PropertyGeneration generation;
 	private boolean includedInOptimisticLocking;
 
 	private boolean forceNonNullable;
 	private boolean forceUnique;
 	private boolean keyCascadeDeleteEnabled;
 
 	private MetaAttributeContext metaAttributeContext;
 
 	SimpleSingularAttributeBinding(
-			EntityBinding entityBinding,
+			AttributeBindingContainer container,
 			SingularAttribute attribute,
 			boolean forceNonNullable,
 			boolean forceUnique) {
-		super( entityBinding, attribute );
+		super( container, attribute );
 		this.forceNonNullable = forceNonNullable;
 		this.forceUnique = forceUnique;
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return false;
 	}
 
 	@Override
 	public String getUnsavedValue() {
 		return unsavedValue;
 	}
 
 	public void setUnsavedValue(String unsavedValue) {
 		this.unsavedValue = unsavedValue;
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return generation;
 	}
 
 	public void setGeneration(PropertyGeneration generation) {
 		this.generation = generation;
 	}
 
 	public boolean isIncludedInOptimisticLocking() {
 		return includedInOptimisticLocking;
 	}
 
 	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
 		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
 	@Override
 	public boolean isKeyCascadeDeleteEnabled() {
 		return keyCascadeDeleteEnabled;
 	}
 
 	public void setKeyCascadeDeleteEnabled(boolean keyCascadeDeleteEnabled) {
 		this.keyCascadeDeleteEnabled = keyCascadeDeleteEnabled;
 	}
 
 	public boolean forceNonNullable() {
 		return forceNonNullable;
 	}
 
 	public boolean forceUnique() {
 		return forceUnique;
 	}
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
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
 				params.setProperty( PersistentIdentifierGenerator.CATALOG, schema.getName().getCatalog().getName() );
 			}
 		}
 
 		// TODO: not sure how this works for collection IDs...
 		//pass the entity-name, if not a collection-id
 		//if ( rootClass!=null) {
-		params.setProperty( IdentifierGenerator.ENTITY_NAME, getEntityBinding().getEntity().getName() );
+			params.setProperty( IdentifierGenerator.ENTITY_NAME, getContainer().seekEntityBinding().getEntity().getName() );
 		//}
 
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getValue().getTable().getQualifiedName( identifierGeneratorFactory.getDialect() );
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 
 		//pass the column name (a generated id almost always has a single column)
 		if ( getSimpleValueSpan() > 1 ) {
 			throw new MappingException(
 					"A SimpleAttributeBinding used for an identifier has more than 1 Value: " + getAttribute().getName()
 			);
 		}
 		SimpleValue simpleValue = (SimpleValue) getValue();
 		if ( !Column.class.isInstance( simpleValue ) ) {
 			throw new MappingException(
 					"Cannot create an IdentifierGenerator because the value is not a column: " +
 							simpleValue.toLoggableString()
 			);
 		}
 		params.setProperty(
 				PersistentIdentifierGenerator.PK,
 				( (Column) simpleValue ).getColumnName().encloseInQuotesIfQuoted(
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
index a59571d451..51cda724c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
@@ -1,296 +1,297 @@
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
 package org.hibernate.metamodel.domain;
 
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.Value;
 
 /**
  * Convenient base class for {@link AttributeContainer}.  Because in our model all
  * {@link AttributeContainer AttributeContainers} are also {@link Hierarchical} we also implement that here
  * as well.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeContainer implements AttributeContainer, Hierarchical {
 	private final String name;
 	private final String className;
 	private final Value<Class<?>> classReference;
 	private final Hierarchical superType;
 	private LinkedHashSet<Attribute> attributeSet = new LinkedHashSet<Attribute>();
 	private HashMap<String, Attribute> attributeMap = new HashMap<String, Attribute>();
 
 	public AbstractAttributeContainer(String name, String className, Value<Class<?>> classReference, Hierarchical superType) {
 		this.name = name;
 		this.className = className;
 		this.classReference = classReference;
 		this.superType = superType;
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
 	public Class<?> getClassReference() {
 		return classReference.getValue();
 	}
 
 	@Override
 	public Value<Class<?>> getClassReferenceUnresolved() {
 		return classReference;
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	@Override
 	public Set<Attribute> attributes() {
 		return Collections.unmodifiableSet( attributeSet );
 	}
 
 	@Override
 	public Attribute locateAttribute(String name) {
 		return attributeMap.get( name );
 	}
 
 	@Override
 	public SingularAttribute locateSingularAttribute(String name) {
 		return (SingularAttribute) locateAttribute( name );
 	}
 
 	@Override
 	public SingularAttribute createSingularAttribute(String name) {
 		SingularAttribute attribute = new SingularAttributeImpl( name, this );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public SingularAttribute createVirtualSingularAttribute(String name) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public SingularAttribute locateComponentAttribute(String name) {
 		return (SingularAttributeImpl) locateAttribute( name );
 	}
 
 	@Override
 	public SingularAttribute createComponentAttribute(String name, Component component) {
-		SingularAttributeImpl attribute = new SingularAttributeImpl( name, component );
+		SingularAttributeImpl attribute = new SingularAttributeImpl( name, this );
+		attribute.resolveType( component );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public PluralAttribute locatePluralAttribute(String name) {
 		return (PluralAttribute) locateAttribute( name );
 	}
 
 	protected PluralAttribute createPluralAttribute(String name, PluralAttributeNature nature) {
 		PluralAttribute attribute = nature.isIndexed()
 				? new IndexedPluralAttributeImpl( name, nature, this )
 				: new PluralAttributeImpl( name, nature, this );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public PluralAttribute locateBag(String name) {
 		return locatePluralAttribute( name );
 	}
 
 	@Override
 	public PluralAttribute createBag(String name) {
 		return createPluralAttribute( name, PluralAttributeNature.BAG );
 	}
 
 	@Override
 	public PluralAttribute locateSet(String name) {
 		return locatePluralAttribute( name );
 	}
 
 	@Override
 	public PluralAttribute createSet(String name) {
 		return createPluralAttribute( name, PluralAttributeNature.SET );
 	}
 
 	@Override
 	public IndexedPluralAttribute locateList(String name) {
 		return (IndexedPluralAttribute) locatePluralAttribute( name );
 	}
 
 	@Override
 	public IndexedPluralAttribute createList(String name) {
 		return (IndexedPluralAttribute) createPluralAttribute( name, PluralAttributeNature.LIST );
 	}
 
 	@Override
 	public IndexedPluralAttribute locateMap(String name) {
 		return (IndexedPluralAttribute) locatePluralAttribute( name );
 	}
 
 	@Override
 	public IndexedPluralAttribute createMap(String name) {
 		return (IndexedPluralAttribute) createPluralAttribute( name, PluralAttributeNature.MAP );
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "AbstractAttributeContainer" );
 		sb.append( "{name='" ).append( name ).append( '\'' );
 		sb.append( ", superType=" ).append( superType );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	protected void addAttribute(Attribute attribute) {
 		// todo : how to best "secure" this?
 		if ( attributeMap.put( attribute.getName(), attribute ) != null ) {
 			throw new IllegalArgumentException( "Attribute with name [" + attribute.getName() + "] already registered" );
 		}
 		attributeSet.add( attribute );
 	}
 
 	// todo : inner classes for now..
 
 	public static class SingularAttributeImpl implements SingularAttribute {
 		private final AttributeContainer attributeContainer;
 		private final String name;
 		private Type type;
 
 		public SingularAttributeImpl(String name, AttributeContainer attributeContainer) {
 			this.name = name;
 			this.attributeContainer = attributeContainer;
 		}
 
 		public boolean isTypeResolved() {
 			return type != null;
 		}
 
 		public void resolveType(Type type) {
 			if ( type == null ) {
 				throw new IllegalArgumentException( "Attempt to resolve with null type" );
 			}
 			this.type = type;
 		}
 
 		@Override
 		public Type getSingularAttributeType() {
 			return type;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 
 		@Override
 		public AttributeContainer getAttributeContainer() {
 			return attributeContainer;
 		}
 
 		@Override
 		public boolean isSingular() {
 			return true;
 		}
 	}
 
 	public static class PluralAttributeImpl implements PluralAttribute {
 		private final AttributeContainer attributeContainer;
 		private final PluralAttributeNature nature;
 		private final String name;
 
 		private Type elementType;
 
 		public PluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
 			this.name = name;
 			this.nature = nature;
 			this.attributeContainer = attributeContainer;
 		}
 
 		@Override
 		public AttributeContainer getAttributeContainer() {
 			return attributeContainer;
 		}
 
 		@Override
 		public boolean isSingular() {
 			return false;
 		}
 
 		@Override
 		public PluralAttributeNature getNature() {
 			return nature;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 
 		@Override
 		public Type getElementType() {
 			return elementType;
 		}
 
 		@Override
 		public void setElementType(Type elementType) {
 			this.elementType = elementType;
 		}
 	}
 
 	public static class IndexedPluralAttributeImpl extends PluralAttributeImpl implements IndexedPluralAttribute {
 		private Type indexType;
 
 		public IndexedPluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
 			super( name, nature, attributeContainer );
 		}
 
 		@Override
 		public Type getIndexType() {
 			return indexType;
 		}
 
 		@Override
 		public void setIndexType(Type indexType) {
 			this.indexType = indexType;
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
index e806f2c335..d2fc925d49 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
@@ -1,273 +1,280 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.SourceType;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.BasicAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SingularAttributeSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.ToOneAttributeSourceImpl;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.ConstraintSource;
 import org.hibernate.metamodel.source.binder.EntitySource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Hardy Ferentschik
  */
 public class EntitySourceImpl implements EntitySource {
 	private final EntityClass entityClass;
 	private final Set<SubclassEntitySource> subclassEntitySources;
 	private final Origin origin;
+	private final LocalBindingContextImpl localBindingContext;
 
 	public EntitySourceImpl(EntityClass entityClass) {
 		this.entityClass = entityClass;
 		this.subclassEntitySources = new HashSet<SubclassEntitySource>();
 		this.origin = new Origin( SourceType.ANNOTATION, entityClass.getName() );
+		this.localBindingContext = new LocalBindingContextImpl( entityClass.getContext() );
 	}
 
 	public EntityClass getEntityClass() {
 		return entityClass;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return origin;
 	}
 
 	@Override
-	public LocalBindingContext getBindingContext() {
-		return new LocalBindingContextImpl( entityClass.getContext() );
+	public LocalBindingContext getLocalBindingContext() {
+		return localBindingContext;
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityClass.getName();
 	}
 
 	@Override
 	public String getClassName() {
 		return entityClass.getName();
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return entityClass.getExplicitEntityName();
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		return entityClass.getPrimaryTableSource();
 	}
 
 	@Override
 	public boolean isAbstract() {
 		return false;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return entityClass.isLazy();
 	}
 
 	@Override
 	public String getProxy() {
 		return entityClass.getProxy();
 	}
 
 	@Override
 	public int getBatchSize() {
 		return entityClass.getBatchSize();
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return entityClass.isDynamicInsert();
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return entityClass.isDynamicUpdate();
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return entityClass.isSelectBeforeUpdate();
 	}
 
 	@Override
 	public String getCustomTuplizerClassName() {
 		return entityClass.getCustomTuplizer();
 	}
 
 	@Override
 	public String getCustomPersisterClassName() {
 		return entityClass.getCustomPersister();
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return entityClass.getCustomLoaderQueryName();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return entityClass.getCustomInsert();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return entityClass.getCustomUpdate();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return entityClass.getCustomDelete();
 	}
 
 	@Override
 	public List<String> getSynchronizedTableNames() {
 		return entityClass.getSynchronizedTableNames();
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Collections.emptySet();
 	}
 
 	@Override
+	public String getPath() {
+		return entityClass.getName();
+	}
+
+	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeList = new ArrayList<AttributeSource>();
 		for ( BasicAttribute attribute : entityClass.getSimpleAttributes() ) {
 			attributeList.add( new SingularAttributeSourceImpl( attribute ) );
 		}
 		for ( AssociationAttribute associationAttribute : entityClass.getAssociationAttributes() ) {
 			attributeList.add( new ToOneAttributeSourceImpl( associationAttribute ) );
 		}
 		return attributeList;
 	}
 
 	@Override
 	public void add(SubclassEntitySource subclassEntitySource) {
 		subclassEntitySources.add( subclassEntitySource );
 	}
 
 	@Override
 	public Iterable<SubclassEntitySource> subclassEntitySources() {
 		return subclassEntitySources;
 	}
 
 	@Override
 	public String getDiscriminatorMatchValue() {
 		return entityClass.getDiscriminatorMatchValue();
 	}
 
 	@Override
 	public Iterable<ConstraintSource> getConstraints() {
 		return entityClass.getConstraintSources();
 	}
 
 	@Override
 	public Iterable<TableSource> getSecondaryTables() {
 		return entityClass.getSecondaryTableSources();
 	}
 
 	class LocalBindingContextImpl implements LocalBindingContext {
 		private final AnnotationBindingContext contextDelegate;
 
 		LocalBindingContextImpl(AnnotationBindingContext context) {
 			this.contextDelegate = context;
 		}
 
 		@Override
 		public Origin getOrigin() {
 			return origin;
 		}
 
 		@Override
 		public ServiceRegistry getServiceRegistry() {
 			return contextDelegate.getServiceRegistry();
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return contextDelegate.getNamingStrategy();
 		}
 
 		@Override
 		public MappingDefaults getMappingDefaults() {
 			return contextDelegate.getMappingDefaults();
 		}
 
 		@Override
 		public MetadataImplementor getMetadataImplementor() {
 			return contextDelegate.getMetadataImplementor();
 		}
 
 		@Override
 		public <T> Class<T> locateClassByName(String name) {
 			return contextDelegate.locateClassByName( name );
 		}
 
 		@Override
 		public Type makeJavaType(String className) {
 			return contextDelegate.makeJavaType( className );
 		}
 
 		@Override
 		public boolean isGloballyQuotedIdentifiers() {
 			return contextDelegate.isGloballyQuotedIdentifiers();
 		}
 
 		@Override
 		public Value<Class<?>> makeClassReference(String className) {
 			return contextDelegate.makeClassReference( className );
 		}
 
 		@Override
 		public String qualifyClassName(String name) {
 			return contextDelegate.qualifyClassName( name );
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
index 5371c4d29c..d4d088bbe3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
@@ -1,39 +1,55 @@
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
 
+import org.hibernate.metamodel.source.LocalBindingContext;
+
 /**
  * Contract for a container of {@link AttributeSource} references.  Both entities and components contain
  * attributes.
  *
  * @author Steve Ebersole
  */
 public interface AttributeSourceContainer {
 	/**
-	 Obtain this container's attribute sources.
+	 * Obtain the path used to uniquely identify this container.
+	 *
+	 * @return The unique identifier path
+	 */
+	public String getPath();
+
+	/**
+	 * Obtain this container's attribute sources.
 	 *
-	 * @return TYhe attribute sources.
+	 * @return The attribute sources.
 	 */
 	public Iterable<AttributeSource> attributeSources();
+
+	/**
+	 * Obtain the local binding context associated with this container.
+	 *
+	 * @return The local binding context
+	 */
+	public LocalBindingContext getLocalBindingContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index a5be5ae056..84850bfd96 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,760 +1,783 @@
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
-import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.AttributeBindingContainer;
 import org.hibernate.metamodel.binding.CollectionElementNature;
+import org.hibernate.metamodel.binding.ComponentAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
+import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.relational.UniqueKey;
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
 
-		currentBindingContext = entitySource.getBindingContext();
+		currentBindingContext = entitySource.getLocalBindingContext();
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
 
 		entityBinding.getHierarchyDetails().setCaching( entitySource.getCaching() );
 		entityBinding.getHierarchyDetails().setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.getHierarchyDetails().setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = superEntityBinding == null
 				? new EntityBinding( currentInheritanceType, currentHierarchyEntityMode )
 				: new EntityBinding( superEntityBinding );
 
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
 
 		if ( currentHierarchyEntityMode == EntityMode.POJO ) {
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
 			entityBinding.setCustomEntityTuplizerClass(
 					currentBindingContext.<EntityTuplizer>locateClassByName(
 							customTuplizerClassName
 					)
 			);
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass(
 					currentBindingContext.<EntityPersister>locateClassByName(
 							customPersisterClassName
 					)
 			);
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
 
-		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
+		entityBinding.setBaseTable( superEntityBinding.getPrimaryTable() );
 
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
 		final SimpleSingularAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getHierarchyDetails().getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getHierarchyDetails()
 				.getEntityIdentifier()
 				.setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
-			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
+			entityBinding.getPrimaryTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
-					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
+					entityBinding.getPrimaryTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
 		SimpleSingularAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.getHierarchyDetails().setVersioningAttributeBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		final DiscriminatorSource discriminatorSource = entitySource.getDiscriminatorSource();
 		if ( discriminatorSource == null ) {
 			return;
 		}
 
 		EntityDiscriminator discriminator = new EntityDiscriminator();
 		SimpleValue relationalValue = makeSimpleValue(
 				entityBinding,
 				discriminatorSource.getDiscriminatorRelationalValueSource()
 		);
 		discriminator.setBoundValue( relationalValue );
 
 		discriminator.getExplicitHibernateTypeDescriptor().setExplicitTypeName(
 				discriminatorSource.getExplicitHibernateTypeName() != null
 						? discriminatorSource.getExplicitHibernateTypeName()
 						: "string"
 		);
 
 		discriminator.setInserted( discriminatorSource.isInserted() );
 		discriminator.setForced( discriminatorSource.isForced() );
 
 		entityBinding.getHierarchyDetails().setEntityDiscriminator( discriminator );
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
 		final String discriminatorValue = entitySource.getDiscriminatorMatchValue();
 		if ( discriminatorValue == null ) {
 			return;
 		}
 		entityBinding.setDiscriminatorMatchValue( discriminatorValue );
 	}
 
-	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
+	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, AttributeBindingContainer attributeBindingContainer) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
 			if ( attributeSource.isSingular() ) {
 				final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) attributeSource;
 				if ( singularAttributeSource.getNature() == SingularAttributeNature.COMPONENT ) {
-					bindComponent( singularAttributeSource, entityBinding );
+					bindComponent( (ComponentAttributeSource) singularAttributeSource, attributeBindingContainer );
 				}
 				else {
-					doBasicSingularAttributeBindingCreation( singularAttributeSource, entityBinding );
+					doBasicSingularAttributeBindingCreation( singularAttributeSource, attributeBindingContainer );
 				}
 			}
 			else {
-				bindPersistentCollection( (PluralAttributeSource) attributeSource, entityBinding );
+				bindPersistentCollection( (PluralAttributeSource) attributeSource, attributeBindingContainer );
 			}
 		}
 	}
 
-	private void bindComponent(SingularAttributeSource singularAttributeSource, EntityBinding entityBinding) {
-		throw new NotYetImplementedException( "Component binding not yet implemented :(" );
+	private void bindComponent(ComponentAttributeSource attributeSource, AttributeBindingContainer container) {
+		final String attributeName = attributeSource.getName();
+		SingularAttribute attribute = container.getAttributeContainer().locateComponentAttribute( attributeName );
+		if ( attribute == null ) {
+			final Component component = new Component(
+					attributeSource.getPath(),
+					attributeSource.getClassName(),
+					attributeSource.getClassReference(),
+					null // component inheritance not YET supported
+			);
+			attribute = container.getAttributeContainer().createComponentAttribute( attributeName, component );
+		}
+		ComponentAttributeBinding componentAttributeBinding = container.makeComponentAttributeBinding( attribute );
+
+		if ( StringHelper.isNotEmpty( attributeSource.getParentReferenceAttributeName() ) ) {
+			final SingularAttribute parentReferenceAttribute =
+					componentAttributeBinding.getComponent().createSingularAttribute( attributeSource.getParentReferenceAttributeName() );
+			componentAttributeBinding.setParentReference( parentReferenceAttribute );
+		}
+
+		componentAttributeBinding.setMetaAttributeContext(
+				buildMetaAttributeContext( attributeSource.metaAttributes(), container.getMetaAttributeContext() )
+		);
+
+		bindAttributes( attributeSource, componentAttributeBinding );
 	}
 
-	private void bindPersistentCollection(PluralAttributeSource attributeSource, EntityBinding entityBinding) {
-		final PluralAttribute existingAttribute = entityBinding.getEntity()
-				.locatePluralAttribute( attributeSource.getName() );
+	private void bindPersistentCollection(PluralAttributeSource attributeSource, AttributeBindingContainer attributeBindingContainer) {
+		final PluralAttribute existingAttribute = attributeBindingContainer.getAttributeContainer().locatePluralAttribute( attributeSource.getName() );
 		final AbstractPluralAttributeBinding pluralAttributeBinding;
 
 		if ( attributeSource.getPluralAttributeNature() == PluralAttributeNature.BAG ) {
 			final PluralAttribute attribute = existingAttribute != null
 					? existingAttribute
-					: entityBinding.getEntity().createBag( attributeSource.getName() );
-			pluralAttributeBinding = entityBinding.makeBagAttributeBinding(
+					: attributeBindingContainer.getAttributeContainer().createBag( attributeSource.getName() );
+			pluralAttributeBinding = attributeBindingContainer.makeBagAttributeBinding(
 					attribute,
 					convert( attributeSource.getPluralAttributeElementNature() )
 			);
 		}
 		else {
 			// todo : implement other collection types
 			throw new NotYetImplementedException( "Collections other than bag not yet implmented :(" );
 		}
 
 		doBasicAttributeBinding( attributeSource, pluralAttributeBinding );
 	}
 
 	private void doBasicAttributeBinding(AttributeSource attributeSource, AttributeBinding attributeBinding) {
 		attributeBinding.setPropertyAccessorName( attributeSource.getPropertyAccessorName() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 	}
 
 	private CollectionElementNature convert(PluralAttributeElementNature pluralAttributeElementNature) {
 		return CollectionElementNature.valueOf( pluralAttributeElementNature.name() );
 	}
 
 	private SimpleSingularAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
-			EntityBinding entityBinding) {
-		final SingularAttribute existingAttribute = entityBinding.getEntity()
-				.locateSingularAttribute( attributeSource.getName() );
+			AttributeBindingContainer attributeBindingContainer) {
+		final SingularAttribute existingAttribute = attributeBindingContainer.getAttributeContainer().locateSingularAttribute( attributeSource.getName() );
 		final SingularAttribute attribute;
 		if ( existingAttribute != null ) {
 			attribute = existingAttribute;
 		}
 		else if ( attributeSource.isVirtualAttribute() ) {
-			attribute = entityBinding.getEntity().createVirtualSingularAttribute( attributeSource.getName() );
+			attribute = attributeBindingContainer.getAttributeContainer().createVirtualSingularAttribute( attributeSource.getName() );
 		}
 		else {
-			attribute = entityBinding.getEntity().createSingularAttribute( attributeSource.getName() );
+			attribute = attributeBindingContainer.getAttributeContainer().createSingularAttribute( attributeSource.getName() );
 		}
 
 		final SimpleSingularAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
-			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
+			attributeBinding = attributeBindingContainer.makeSimpleAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
-			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
+			attributeBinding = attributeBindingContainer.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 			resolveToOneInformation(
 					(ToOneAttributeSource) attributeSource,
 					(ManyToOneAttributeBinding) attributeBinding
 			);
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
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
 
 		bindRelationalValues( attributeSource, attributeBinding );
 
 		attributeBinding.setMetaAttributeContext(
-				buildMetaAttributeContext( attributeSource.metaAttributes(), entityBinding.getMetaAttributeContext() )
+				buildMetaAttributeContext( attributeSource.metaAttributes(), attributeBindingContainer.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
 	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleSingularAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute()
 					.resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor()
 					.getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String, String> parameters = typeSource.getParameters();
 			if ( parameters!=null) {
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
 			final Class<?> ownerClass = attribute.getAttributeContainer().getClassReference();
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
 
 	private void resolveToOneInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
 		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
 
 		attributeBinding.setCascadeStyles( attributeSource.getCascadeStyles() );
 		attributeBinding.setFetchMode( attributeSource.getFetchMode() );
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
 		final Table table = createTable( entityBinding, tableSource );
 		entityBinding.setBaseTable( table );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		for ( TableSource secondaryTableSource : entitySource.getSecondaryTables() ) {
 			final Table table = createTable( entityBinding, secondaryTableSource );
 			entityBinding.addSecondaryTable( secondaryTableSource.getLogicalName(), table );
 		}
 	}
 
 	private Table createTable(EntityBinding entityBinding, TableSource tableSource) {
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
 
 		return currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		for ( ConstraintSource constraintSource : entitySource.getConstraints() ) {
 			if ( constraintSource instanceof UniqueConstraintSource ) {
-				TableSpecification table = entityBinding.getTable( constraintSource.getTableName() );
+				TableSpecification table = entityBinding.locateTable( constraintSource.getTableName() );
 				if ( table == null ) {
 					// throw exception !?
 				}
 				String constraintName = constraintSource.name();
 				if ( constraintName == null ) {
 					// create a default name
 				}
 
 				UniqueKey uniqueKey = table.getOrCreateUniqueKey( constraintName );
 				for ( String columnName : constraintSource.columnNames() ) {
 					uniqueKey.addColumn( table.locateOrCreateColumn( columnName ) );
 				}
 			}
 		}
 	}
 
 	private void bindRelationalValues(
 			RelationalValueSourceContainer relationalValueSourceContainer,
 			SingularAttributeBinding attributeBinding) {
 
 		List<SimpleValueBinding> valueBindings = new ArrayList<SimpleValueBinding>();
 
 		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
 			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
-				final TableSpecification table = attributeBinding.getEntityBinding()
-						.getTable( valueSource.getContainingTableName() );
+				final TableSpecification table = attributeBinding.getContainer()
+						.locateTable( valueSource.getContainingTableName() );
 
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
 					final Column column = makeColumn( (ColumnSource) valueSource, table );
 					valueBindings.add(
 							new SimpleValueBinding(
 									column,
 									columnSource.isIncludedInInsert(),
 									columnSource.isIncludedInUpdate()
 							)
 					);
 				}
 				else {
 					valueBindings.add(
 							new SimpleValueBinding(
 									makeDerivedValue( ( (DerivedValueSource) valueSource ), table )
 							)
 					);
 				}
 			}
 		}
 		else {
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
 			valueBindings.add(
 					new SimpleValueBinding(
-							attributeBinding.getEntityBinding().getBaseTable().locateOrCreateColumn( name )
+							attributeBinding.getContainer().getPrimaryTable().locateOrCreateColumn( name )
 					)
 			);
 		}
 		attributeBinding.setSimpleValueBindings( valueBindings );
 	}
 
 	private SimpleValue makeSimpleValue(
 			EntityBinding entityBinding,
 			RelationalValueSource valueSource) {
-		final TableSpecification table = entityBinding.getTable( valueSource.getContainingTableName() );
+		final TableSpecification table = entityBinding.locateTable( valueSource.getContainingTableName() );
 
 		if ( ColumnSource.class.isInstance( valueSource ) ) {
 			return makeColumn( (ColumnSource) valueSource, table );
 		}
 		else {
 			return makeDerivedValue( (DerivedValueSource) valueSource, table );
 		}
 	}
 
 	private Column makeColumn(ColumnSource columnSource, TableSpecification table) {
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
 		return column;
 	}
 
 	private DerivedValue makeDerivedValue(DerivedValueSource derivedValueSource, TableSpecification table) {
 		return table.locateOrCreateDerivedValue( derivedValueSource.getExpression() );
 	}
 
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
new file mode 100644
index 0000000000..3448bdccaf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
@@ -0,0 +1,36 @@
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
+package org.hibernate.metamodel.source.binder;
+
+import org.hibernate.internal.util.Value;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ComponentAttributeSource extends SingularAttributeSource, AttributeSourceContainer {
+	public String getClassName();
+	public Value<Class<?>> getClassReference();
+
+	public String getParentReferenceAttributeName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
index 7c0e529231..238a9fe291 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
@@ -1,206 +1,206 @@
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
  * Contract describing source of an entity
  *
  * @author Steve Ebersole
  */
 public interface EntitySource extends SubclassEntityContainer, AttributeSourceContainer {
 	/**
 	 * Obtain the origin of this source.
 	 *
 	 * @return The origin of this source.
 	 */
 	public Origin getOrigin();
 
 	/**
 	 * Obtain the binding context local to this entity source.
 	 *
 	 * @return The local binding context
 	 */
-	public LocalBindingContext getBindingContext();
+	public LocalBindingContext getLocalBindingContext();
 
 	/**
 	 * Obtain the entity name
 	 *
 	 * @return The entity name
 	 */
 	public String getEntityName();
 
 	/**
 	 * Obtain the name of the entity {@link Class}
 	 *
 	 * @return THe entity class name
 	 */
 	public String getClassName();
 
 	/**
 	 * Obtain the JPA name of the entity
 	 *
 	 * @return THe JPA-specific entity name
 	 */
 	public String getJpaEntityName();
 
 	/**
 	 * Obtain the primary table for this entity.
 	 *
 	 * @return The primary table.
 	 */
 	public TableSource getPrimaryTable();
 
 	/**
 	 * Obtain the secondary tables for this entity
 	 *
 	 * @return returns an iterator over the secondary tables for this entity
 	 */
 	public Iterable<TableSource> getSecondaryTables();
 
 	/**
 	 * Obtain the name of a custom tuplizer class to be used.
 	 *
 	 * @return The custom tuplizer class name
 	 */
 	public String getCustomTuplizerClassName();
 
 	/**
 	 * Obtain the name of a custom persister class to be used.
 	 *
 	 * @return The custom persister class name
 	 */
 	public String getCustomPersisterClassName();
 
 	/**
 	 * Is this entity lazy (proxyable)?
 	 *
 	 * @return {@code true} indicates the entity is lazy; {@code false} non-lazy.
 	 */
 	public boolean isLazy();
 
 	/**
 	 * For {@link #isLazy() lazy} entities, obtain the interface to use in constructing its proxies.
 	 *
 	 * @return The proxy interface name
 	 */
 	public String getProxy();
 
 	/**
 	 * Obtain the batch-size to be applied when initializing proxies of this entity.
 	 *
 	 * @return returns the the batch-size.
 	 */
 	public int getBatchSize();
 
 	/**
 	 * Is the entity abstract?
 	 * <p/>
 	 * The implication is whether the entity maps to a database table.
 	 *
 	 * @return {@code true} indicates the entity is abstract; {@code false} non-abstract.
 	 */
 	public boolean isAbstract();
 
 	/**
 	 * Did the source specify dynamic inserts?
 	 *
 	 * @return {@code true} indicates dynamic inserts will be used; {@code false} otherwise.
 	 */
 	public boolean isDynamicInsert();
 
 	/**
 	 * Did the source specify dynamic updates?
 	 *
 	 * @return {@code true} indicates dynamic updates will be used; {@code false} otherwise.
 	 */
 	public boolean isDynamicUpdate();
 
 	/**
 	 * Did the source specify to perform selects to decide whether to perform (detached) updates?
 	 *
 	 * @return {@code true} indicates selects will be done; {@code false} otherwise.
 	 */
 	public boolean isSelectBeforeUpdate();
 
 	/**
 	 * Obtain the name of a named-query that will be used for loading this entity
 	 *
 	 * @return THe custom loader query name
 	 */
 	public String getCustomLoaderName();
 
 	/**
 	 * Obtain the custom SQL to be used for inserts for this entity
 	 *
 	 * @return The custom insert SQL
 	 */
 	public CustomSQL getCustomSqlInsert();
 
 	/**
 	 * Obtain the custom SQL to be used for updates for this entity
 	 *
 	 * @return The custom update SQL
 	 */
 	public CustomSQL getCustomSqlUpdate();
 
 	/**
 	 * Obtain the custom SQL to be used for deletes for this entity
 	 *
 	 * @return The custom delete SQL
 	 */
 	public CustomSQL getCustomSqlDelete();
 
 	/**
 	 * Obtain any additional table names on which to synchronize (auto flushing) this entity.
 	 *
 	 * @return Additional synchronized table names.
 	 */
 	public List<String> getSynchronizedTableNames();
 
 	/**
 	 * Obtain the meta-attribute sources associated with this entity.
 	 *
 	 * @return The meta-attribute sources.
 	 */
 	public Iterable<MetaAttributeSource> metaAttributes();
 
 	/**
 	 * Get the actual discriminator value in case of a single table inheritance
 	 *
 	 * @return the actual discriminator value in case of a single table inheritance or {@code null} in case there is no
 	 *         explicit value or a different inheritance scheme
 	 */
 	public String getDiscriminatorMatchValue();
 
 	/**
 	 * @return returns the source information for constraints defined on the table
 	 */
 	public Iterable<ConstraintSource> getConstraints();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
index 08df3d96d6..578ccfe97e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
@@ -1,265 +1,280 @@
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
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.EntityMode;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.ConstraintSource;
 import org.hibernate.metamodel.source.binder.EntitySource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
 
 /**
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public abstract class AbstractEntitySourceImpl implements EntitySource {
 	private final MappingDocument sourceMappingDocument;
 	private final EntityElement entityElement;
 
 	private List<SubclassEntitySource> subclassEntitySources = new ArrayList<SubclassEntitySource>();
 
 	protected AbstractEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
 		this.sourceMappingDocument = sourceMappingDocument;
 		this.entityElement = entityElement;
 	}
 
 	protected EntityElement entityElement() {
 		return entityElement;
 	}
 
 	protected MappingDocument sourceMappingDocument() {
 		return sourceMappingDocument;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return sourceMappingDocument.getOrigin();
 	}
 
 	@Override
-	public LocalBindingContext getBindingContext() {
+	public LocalBindingContext getLocalBindingContext() {
 		return sourceMappingDocument.getMappingLocalBindingContext();
 	}
 
 	@Override
 	public String getEntityName() {
 		return StringHelper.isNotEmpty( entityElement.getEntityName() )
 				? entityElement.getEntityName()
 				: getClassName();
 	}
 
 	@Override
 	public String getClassName() {
-		return getBindingContext().qualifyClassName( entityElement.getName() );
+		return getLocalBindingContext().qualifyClassName( entityElement.getName() );
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAbstract() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), false );
 	}
 
 	@Override
 	public boolean isLazy() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), true );
 	}
 
 	@Override
 	public String getProxy() {
 		return entityElement.getProxy();
 	}
 
 	@Override
 	public int getBatchSize() {
 		return Helper.getIntValue( entityElement.getBatchSize(), -1 );
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return entityElement.isDynamicInsert();
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return entityElement.isDynamicUpdate();
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return entityElement.isSelectBeforeUpdate();
 	}
 
 	protected EntityMode determineEntityMode() {
 		return StringHelper.isNotEmpty( getClassName() ) ? EntityMode.POJO : EntityMode.MAP;
 	}
 
 	@Override
 	public String getCustomTuplizerClassName() {
 		if ( entityElement.getTuplizer() == null ) {
 			return null;
 		}
 		final EntityMode entityMode = determineEntityMode();
 		for ( XMLTuplizerElement tuplizerElement : entityElement.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String getCustomPersisterClassName() {
-		return getBindingContext().qualifyClassName( entityElement.getPersister() );
+		return getLocalBindingContext().qualifyClassName( entityElement.getPersister() );
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return entityElement.getLoader() != null ? entityElement.getLoader().getQueryRef() : null;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return Helper.buildCustomSql( entityElement.getSqlInsert() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return Helper.buildCustomSql( entityElement.getSqlUpdate() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return Helper.buildCustomSql( entityElement.getSqlDelete() );
 	}
 
 	@Override
 	public List<String> getSynchronizedTableNames() {
 		List<String> tableNames = new ArrayList<String>();
 		for ( XMLSynchronizeElement synchronizeElement : entityElement.getSynchronize() ) {
 			tableNames.add( synchronizeElement.getTable() );
 		}
 		return tableNames;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( entityElement.getMeta() );
 	}
 
 	@Override
+	public String getPath() {
+		return sourceMappingDocument.getMappingLocalBindingContext().determineEntityName( entityElement );
+	}
+
+	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attributeElement : entityElement.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new PropertyAttributeSourceImpl(
 								XMLPropertyElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
+			else if ( XMLComponentElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new ComponentAttributeSourceImpl(
+								(XMLComponentElement) attributeElement,
+								this,
+								sourceMappingDocument.getMappingLocalBindingContext()
+						)
+				);
+			}
 			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ManyToOneAttributeSourceImpl(
 								XMLManyToOneElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLAnyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLOneToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLManyToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 		}
 		return attributeSources;
 	}
 
 	private EntityHierarchyImpl entityHierarchy;
 
 	public void injectHierarchy(EntityHierarchyImpl entityHierarchy) {
 		this.entityHierarchy = entityHierarchy;
 	}
 
 	@Override
 	public void add(SubclassEntitySource subclassEntitySource) {
 		add( (SubclassEntitySourceImpl) subclassEntitySource );
 	}
 
 	public void add(SubclassEntitySourceImpl subclassEntitySource) {
 		entityHierarchy.processSubclass( subclassEntitySource );
 		subclassEntitySources.add( subclassEntitySource );
 	}
 
 	@Override
 	public Iterable<SubclassEntitySource> subclassEntitySources() {
 		return subclassEntitySources;
 	}
 
 	@Override
 	public String getDiscriminatorMatchValue() {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public Iterable<ConstraintSource> getConstraints() {
 		return Collections.emptySet();
 	}
 
 	@Override
 	public Iterable<TableSource> getSecondaryTables() {
 		return Collections.emptySet();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
new file mode 100644
index 0000000000..70f6726e3b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
@@ -0,0 +1,220 @@
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
+import org.hibernate.internal.util.Value;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.AttributeSource;
+import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
+import org.hibernate.metamodel.source.binder.ComponentAttributeSource;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.binder.SingularAttributeNature;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
+	private final XMLComponentElement componentElement;
+	private final AttributeSourceContainer parentContainer;
+
+	private final Value<Class<?>> componentClassReference;
+	private final String path;
+
+	public ComponentAttributeSourceImpl(
+			XMLComponentElement componentElement,
+			AttributeSourceContainer parentContainer,
+			LocalBindingContext bindingContext) {
+		this.componentElement = componentElement;
+		this.parentContainer = parentContainer;
+
+		this.componentClassReference = bindingContext.makeClassReference( componentElement.getClazz() );
+		this.path = parentContainer.getPath() + '.' + componentElement.getName();
+	}
+
+	@Override
+	public String getClassName() {
+		return componentElement.getClazz();
+	}
+
+	@Override
+	public Value<Class<?>> getClassReference() {
+		return componentClassReference;
+	}
+
+	@Override
+	public String getPath() {
+		return path;
+	}
+
+	@Override
+	public LocalBindingContext getLocalBindingContext() {
+		return parentContainer.getLocalBindingContext();
+	}
+
+	@Override
+	public String getParentReferenceAttributeName() {
+		return componentElement.getParent() == null ? null : componentElement.getParent().getName();
+	}
+
+	@Override
+	public Iterable<AttributeSource> attributeSources() {
+		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
+		for ( Object attributeElement : componentElement.getPropertyOrManyToOneOrOneToOne() ) {
+			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new PropertyAttributeSourceImpl(
+								XMLPropertyElement.class.cast( attributeElement ),
+								getLocalBindingContext()
+						)
+				);
+			}
+			else if ( XMLComponentElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new ComponentAttributeSourceImpl(
+								(XMLComponentElement) attributeElement,
+								this,
+								getLocalBindingContext()
+						)
+				);
+			}
+			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new ManyToOneAttributeSourceImpl(
+								XMLManyToOneElement.class.cast( attributeElement ),
+								getLocalBindingContext()
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
+	@Override
+	public boolean isVirtualAttribute() {
+		return false;
+	}
+
+	@Override
+	public SingularAttributeNature getNature() {
+		return SingularAttributeNature.COMPONENT;
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getTypeInformation() {
+		// <component/> does not support type information.
+		return null;
+	}
+
+	@Override
+	public String getName() {
+		return componentElement.getName();
+	}
+
+	@Override
+	public boolean isSingular() {
+		return true;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return componentElement.getAccess();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return componentElement.isInsert();
+	}
+
+	@Override
+	public boolean isUpdatable() {
+		return componentElement.isUpdate();
+	}
+
+	@Override
+	public PropertyGeneration getGeneration() {
+		// todo : is this correct here?
+		return null;
+	}
+
+	@Override
+	public boolean isLazy() {
+		return componentElement.isLazy();
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return componentElement.isOptimisticLock();
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( componentElement.getMeta() );
+	}
+
+	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return isInsertable();
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return isUpdatable();
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return true;
+	}
+
+	@Override
+	public List<RelationalValueSource> relationalValueSources() {
+		// none, they are defined on the simple sub-attributes
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
index 8cefc7689a..b2ae01681c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
@@ -1,246 +1,251 @@
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
 
 import java.util.Iterator;
 
 import org.hibernate.EntityMode;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.DiscriminatorSource;
 import org.hibernate.metamodel.source.binder.IdentifierSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.RootEntitySource;
 import org.hibernate.metamodel.source.binder.SimpleIdentifierSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * @author Steve Ebersole
  */
 public class RootEntitySourceImpl extends AbstractEntitySourceImpl implements RootEntitySource {
 	protected RootEntitySourceImpl(MappingDocument sourceMappingDocument, XMLHibernateMapping.XMLClass entityElement) {
 		super( sourceMappingDocument, entityElement );
 	}
 
 	@Override
 	protected XMLHibernateMapping.XMLClass entityElement() {
 		return (XMLHibernateMapping.XMLClass) super.entityElement();
 	}
 
 	@Override
 	public IdentifierSource getIdentifierSource() {
 		if ( entityElement().getId() != null ) {
 			return new SimpleIdentifierSource() {
 				@Override
 				public SingularAttributeSource getIdentifierAttributeSource() {
 					return new SingularIdentifierAttributeSourceImpl(
 							entityElement().getId(),
 							sourceMappingDocument().getMappingLocalBindingContext()
 					);
 				}
 
 				@Override
 				public IdGenerator getIdentifierGeneratorDescriptor() {
 					if ( entityElement().getId().getGenerator() != null ) {
 						final String generatorName = entityElement().getId().getGenerator().getClazz();
 						IdGenerator idGenerator = sourceMappingDocument().getMappingLocalBindingContext()
 								.getMetadataImplementor()
 								.getIdGenerator( generatorName );
 						if ( idGenerator == null ) {
 							idGenerator = new IdGenerator(
 									getEntityName() + generatorName,
 									generatorName,
 									Helper.extractParameters( entityElement().getId().getGenerator().getParam() )
 							);
 						}
 						return idGenerator;
 					}
 					return null;
 				}
 
 				@Override
 				public Nature getNature() {
 					return Nature.SIMPLE;
 				}
 			};
 		}
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public SingularAttributeSource getVersioningAttributeSource() {
 		if ( entityElement().getVersion() != null ) {
 			return new VersionAttributeSourceImpl(
 					entityElement().getVersion(),
 					sourceMappingDocument().getMappingLocalBindingContext()
 			);
 		}
 		else if ( entityElement().getTimestamp() != null ) {
 			return new TimestampAttributeSourceImpl(
 					entityElement().getTimestamp(),
 					sourceMappingDocument().getMappingLocalBindingContext()
 			);
 		}
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return determineEntityMode();
 	}
 
 	@Override
 	public boolean isMutable() {
 		return entityElement().isMutable();
 	}
 
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return "explicit".equals( entityElement().getPolymorphism() );
 	}
 
 	@Override
 	public String getWhere() {
 		return entityElement().getWhere();
 	}
 
 	@Override
 	public String getRowId() {
 		return entityElement().getRowid();
 	}
 
 	@Override
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		final String optimisticLockModeString = Helper.getStringValue( entityElement().getOptimisticLock(), "version" );
 		try {
 			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
 		}
 		catch ( Exception e ) {
 			throw new MappingException(
 					"Unknown optimistic-lock value : " + optimisticLockModeString,
 					sourceMappingDocument().getOrigin()
 			);
 		}
 	}
 
 	@Override
 	public Caching getCaching() {
 		final XMLCacheElement cache = entityElement().getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : getEntityName();
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		return new TableSource() {
 			@Override
 			public String getExplicitSchemaName() {
 				return entityElement().getSchema();
 			}
 
 			@Override
 			public String getExplicitCatalogName() {
 				return entityElement().getCatalog();
 			}
 
 			@Override
 			public String getExplicitTableName() {
 				return entityElement().getTable();
 			}
 
 			@Override
 			public String getLogicalName() {
 				// logical name for the primary table is null
 				return null;
 			}
 		};
 	}
 
 	@Override
+	public String getDiscriminatorMatchValue() {
+		return entityElement().getDiscriminatorValue();
+	}
+
+	@Override
 	public DiscriminatorSource getDiscriminatorSource() {
 		final XMLHibernateMapping.XMLClass.XMLDiscriminator discriminatorElement = entityElement().getDiscriminator();
 		if ( discriminatorElement == null ) {
 			return null;
 		}
 
 		return new DiscriminatorSource() {
 			@Override
 			public RelationalValueSource getDiscriminatorRelationalValueSource() {
 				if ( StringHelper.isNotEmpty( discriminatorElement.getColumnAttribute() ) ) {
 					return new ColumnAttributeSourceImpl(
 							null, // root table
 							discriminatorElement.getColumnAttribute(),
 							discriminatorElement.isInsert(),
 							discriminatorElement.isInsert()
 					);
 				}
 				else if ( StringHelper.isNotEmpty( discriminatorElement.getFormulaAttribute() ) ) {
 					return new FormulaImpl( null, discriminatorElement.getFormulaAttribute() );
 				}
 				else if ( discriminatorElement.getColumn() != null ) {
 					return new ColumnSourceImpl(
 							null, // root table
 							discriminatorElement.getColumn(),
 							discriminatorElement.isInsert(),
 							discriminatorElement.isInsert()
 					);
 				}
 				else if ( StringHelper.isNotEmpty( discriminatorElement.getFormula() ) ) {
 					return new FormulaImpl( null, discriminatorElement.getFormula() );
 				}
 				else {
 					throw new MappingException( "could not determine source of discriminator mapping", getOrigin() );
 				}
 			}
 
 			@Override
 			public String getExplicitHibernateTypeName() {
 				return discriminatorElement.getType();
 			}
 
 			@Override
 			public boolean isForced() {
 				return discriminatorElement.isForce();
 			}
 
 			@Override
 			public boolean isInserted() {
 				return discriminatorElement.isInsert();
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
index e6206525d9..e0fa0bbe06 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
@@ -1,92 +1,100 @@
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
 
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 
 /**
  * @author Steve Ebersole
  */
 public class SubclassEntitySourceImpl extends AbstractEntitySourceImpl implements SubclassEntitySource {
 	protected SubclassEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
 		super( sourceMappingDocument, entityElement );
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		if ( XMLJoinedSubclassElement.class.isInstance( entityElement() ) ) {
 			return new TableSource() {
 				@Override
 				public String getExplicitSchemaName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getSchema();
 				}
 
 				@Override
 				public String getExplicitCatalogName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getCatalog();
 				}
 
 				@Override
 				public String getExplicitTableName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getTable();
 				}
 
 				@Override
 				public String getLogicalName() {
 					// logical name for the primary table is null
 					return null;
 				}
 			};
 		}
 		else if ( XMLUnionSubclassElement.class.isInstance( entityElement() ) ) {
 			return new TableSource() {
 				@Override
 				public String getExplicitSchemaName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getSchema();
 				}
 
 				@Override
 				public String getExplicitCatalogName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getCatalog();
 				}
 
 				@Override
 				public String getExplicitTableName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getTable();
 				}
 
 				@Override
 				public String getLogicalName() {
 					// logical name for the primary table is null
 					return null;
 				}
 			};
 		}
 		return null;
 	}
+
+	@Override
+	public String getDiscriminatorMatchValue() {
+		return XMLSubclassElement.class.isInstance( entityElement() )
+				? ( (XMLSubclassElement) entityElement() ).getDiscriminatorValue()
+				: null;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java
index 8164b5af4c..faf3bf251a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AssociationResolver.java
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
 import org.hibernate.metamodel.binding.SingularAssociationAttributeBinding;
 
 /**
  * @author Gail Badner
  */
 class AssociationResolver {
 
 	private final MetadataImplementor metadata;
 
 	AssociationResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			for ( SingularAssociationAttributeBinding attributeBinding :  entityBinding.getEntityReferencingAttributeBindings() ) {
 				resolve( attributeBinding );
 			}
 		}
 	}
 
 	private void resolve(SingularAssociationAttributeBinding attributeBinding) {
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
-						entityBinding.getAttributeBinding( attributeBinding.getReferencedAttributeName() ) :
+						entityBinding.locateAttributeBinding( attributeBinding.getReferencedAttributeName() ) :
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
index 6508052a44..0065d581a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
@@ -1,156 +1,156 @@
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
 
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.metamodel.source.MetadataImplementor;
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
-			for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindings() ) {
+			for ( AttributeBinding attributeBinding : entityBinding.attributeBindings() ) {
 				resolveTypeInformation( attributeBinding );
 			}
 		}
 	}
 
 	private void resolveTypeInformation(AttributeBinding attributeBinding) {
 		// perform any needed type resolutions
 
 		final HibernateTypeDescriptor hibernateTypeDescriptor = attributeBinding.getHibernateTypeDescriptor();
 
 		Type resolvedHibernateType = hibernateTypeDescriptor.getResolvedTypeMapping();
 		if ( resolvedHibernateType == null ) {
 			resolvedHibernateType = determineHibernateType( attributeBinding );
 			if ( resolvedHibernateType != null ) {
 				hibernateTypeDescriptor.setResolvedTypeMapping( resolvedHibernateType );
 			}
 		}
 
 		if ( resolvedHibernateType != null ) {
 			pushHibernateTypeInformationDownIfNeeded( attributeBinding, resolvedHibernateType );
 		}
 	}
 
 	private Type determineHibernateType(AttributeBinding attributeBinding) {
 		String typeName = null;
 		Properties typeParameters = new Properties();
 
 		// we can determine the Hibernate Type if either:
 		// 		1) the user explicitly named a Type
 		// 		2) we know the java type of the attribute
 
 		if ( attributeBinding.getHibernateTypeDescriptor().getExplicitTypeName() != null ) {
 			typeName = attributeBinding.getHibernateTypeDescriptor().getExplicitTypeName();
 			if ( attributeBinding.getHibernateTypeDescriptor().getTypeParameters() != null ) {
 				typeParameters.putAll( attributeBinding.getHibernateTypeDescriptor().getTypeParameters() );
 			}
 		}
 		else {
 			typeName = attributeBinding.getHibernateTypeDescriptor().getJavaTypeName();
 			if ( typeName == null ) {
 				if ( attributeBinding.getAttribute().isSingular() ) {
 					SingularAttribute singularAttribute = (SingularAttribute) attributeBinding.getAttribute();
 					if ( singularAttribute.getSingularAttributeType() != null ) {
 						typeName = singularAttribute.getSingularAttributeType().getClassName();
 					}
 				}
 			}
 		}
 
 		if ( typeName != null ) {
 			try {
 				return metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 			}
 			catch (Exception ignore) {
 			}
 		}
 
 		return null;
 	}
 
 	private void pushHibernateTypeInformationDownIfNeeded(AttributeBinding attributeBinding, Type resolvedHibernateType) {
 		final HibernateTypeDescriptor hibernateTypeDescriptor = attributeBinding.getHibernateTypeDescriptor();
 
 		// java type information ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		if ( hibernateTypeDescriptor.getJavaTypeName() == null ) {
 			hibernateTypeDescriptor.setJavaTypeName( resolvedHibernateType.getReturnedClass().getName() );
 		}
 
 		if ( SingularAttribute.class.isInstance( attributeBinding.getAttribute() ) ) {
 			final SingularAttribute singularAttribute = (SingularAttribute) attributeBinding.getAttribute();
 			if ( ! singularAttribute.isTypeResolved() ) {
 				if ( hibernateTypeDescriptor.getJavaTypeName() != null ) {
 					singularAttribute.resolveType( metadata.makeJavaType( hibernateTypeDescriptor.getJavaTypeName() ) );
 				}
 			}
 		}
 
 
 		// sql type information ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		// todo : this can be made a lot smarter, but for now this will suffice.  currently we only handle single value bindings
 
 		if ( SingularAttribute.class.isInstance( attributeBinding.getAttribute() ) ) {
 			final Value value = SingularAttributeBinding.class.cast( attributeBinding ).getValue();
 			if ( SimpleValue.class.isInstance( value ) ) {
 				SimpleValue simpleValue = (SimpleValue) value;
 				if ( simpleValue.getDatatype() == null ) {
 					simpleValue.setDatatype(
 							new Datatype(
 									resolvedHibernateType.sqlTypes( metadata )[0],
 									resolvedHibernateType.getName(),
 									resolvedHibernateType.getReturnedClass()
 							)
 					);
 				}
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 974e7ea5b4..70b2eeda6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,604 +1,604 @@
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
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
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
 
 	private Map<String, AbstractPluralAttributeBinding> collectionBindingMap = new HashMap<String, AbstractPluralAttributeBinding>();
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
 		new AssociationResolver( this ).resolve();
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
 
 	public AbstractPluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
 	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(AbstractPluralAttributeBinding pluralAttributeBinding) {
-		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
+		final String owningEntityName = pluralAttributeBinding.getContainer().getPathBase();
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
 				.getHierarchyDetails()
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
 		AttributeBinding idBinding = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
-		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
+		AttributeBinding attributeBinding = entityBinding.locateAttributeBinding( propertyName );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 4c82282401..605a2c6c88 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1986 +1,1985 @@
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
 import java.util.List;
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
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
-import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
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
 				entityBinding.getHierarchyDetails().getCaching() == null ?
 						false :
 						entityBinding.getHierarchyDetails().getCaching().isCacheLazyProperties();
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
 
 		identifierColumnSpan = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding().getSimpleValueSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = entityBinding.getRowId();
 
 		loaderName = entityBinding.getCustomLoaderName();
 
 		int i = 0;
-		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getBaseTable().getPrimaryKey().getColumns() ) {
+		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getPrimaryTable().getPrimaryKey().getColumns() ) {
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
-			// identifierAliases[i] = col.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable() );
+			// identifierAliases[i] = col.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getPrimaryTable() );
 			identifierAliases[i] = col.getAlias( factory.getDialect() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( entityBinding.isVersioned() ) {
 			final Value versioningValue = entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getValue();
 			if ( ! org.hibernate.metamodel.relational.Column.class.isInstance( versioningValue ) ) {
 				throw new AssertionFailure( "Bad versioning attribute binding : " + versioningValue );
 			}
 			org.hibernate.metamodel.relational.Column versionColumn = org.hibernate.metamodel.relational.Column.class.cast( versioningValue );
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
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			thisClassProperties.add( singularAttributeBinding );
 
-			propertySubclassNames[i] = singularAttributeBinding.getEntityBinding().getEntity().getName();
+			propertySubclassNames[i] = ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName();
 
 			int span = singularAttributeBinding.getSimpleValueSpan();
 			propertyColumnSpans[i] = span;
 
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			boolean[] propertyColumnInsertability = new boolean[span];
 			boolean[] propertyColumnUpdatability = new boolean[span];
 
 			int k = 0;
 
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				colAliases[k] = valueBinding.getSimpleValue().getAlias( factory.getDialect() );
 				if ( valueBinding.isDerived() ) {
 					foundFormula = true;
 					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) valueBinding.getSimpleValue() ).getExpression(), factory );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) valueBinding.getSimpleValue();
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment() == null ? "?" : col.getWriteFragment();
 				}
 				propertyColumnInsertability[k] = valueBinding.isIncludeInInsert();
 				propertyColumnUpdatability[k] = valueBinding.isIncludeInUpdate();
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			propertyColumnUpdateable[i] = propertyColumnInsertability;
 			propertyColumnInsertable[i] = propertyColumnUpdatability;
 
 			if ( lazyAvailable && singularAttributeBinding.isLazy() ) {
 				lazyProperties.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNames.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping());
 				lazyColAliases.add( colAliases );
 			}
 
 
 			// TODO: fix this when backrefs are working
 			//propertySelectable[i] = singularAttributeBinding.isBackRef();
 			propertySelectable[i] = true;
 
 			propertyUniqueness[i] = singularAttributeBinding.isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		List<String> columns = new ArrayList<String>();
 		List<Boolean> columnsLazy = new ArrayList<Boolean>();
 		List<String> columnReaderTemplates = new ArrayList<String>();
 		List<String> aliases = new ArrayList<String>();
 		List<String> formulas = new ArrayList<String>();
 		List<String> formulaAliases = new ArrayList<String>();
 		List<String> formulaTemplates = new ArrayList<String>();
 		List<Boolean> formulasLazy = new ArrayList<Boolean>();
 		List<Type> types = new ArrayList<Type>();
 		List<String> names = new ArrayList<String>();
 		List<String> classes = new ArrayList<String>();
 		List<String[]> templates = new ArrayList<String[]>();
 		List<String[]> propColumns = new ArrayList<String[]>();
 		List<String[]> propColumnReaders = new ArrayList<String[]>();
 		List<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
 		List<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		List<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		List<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		List<int[]> propColumnNumbers = new ArrayList<int[]>();
 		List<int[]> propFormulaNumbers = new ArrayList<int[]>();
 		List<Boolean> columnSelectables = new ArrayList<Boolean>();
 		List<Boolean> propNullables = new ArrayList<Boolean>();
 
 		// TODO: fix this when EntityBinding.getSubclassAttributeBindingClosure() is working
 		// for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			names.add( singularAttributeBinding.getAttribute().getName() );
-			classes.add( singularAttributeBinding.getEntityBinding().getEntity().getName() );
+			classes.add( ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName() );
 			boolean isDefinedBySubclass = ! thisClassProperties.contains( singularAttributeBinding );
 			definedBySubclass.add( isDefinedBySubclass );
 			propNullables.add( singularAttributeBinding.isNullable() || isDefinedBySubclass ); //TODO: is this completely correct?
 			types.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 			final int span = singularAttributeBinding.getSimpleValueSpan();
 			String[] cols = new String[ span ];
 			String[] readers = new String[ span ];
 			String[] readerTemplates = new String[ span ];
 			String[] forms = new String[ span ];
 			int[] colnos = new int[ span ];
 			int[] formnos = new int[ span ];
 			int l = 0;
 			Boolean lazy = singularAttributeBinding.isLazy() && lazyAvailable;
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( valueBinding.isDerived() ) {
 					DerivedValue derivedValue = DerivedValue.class.cast( valueBinding.getSimpleValue() );
 					String template = getTemplateFromString( derivedValue.getExpression(), factory );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( derivedValue.getExpression() );
 					formulaAliases.add( derivedValue.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = org.hibernate.metamodel.relational.Column.class.cast( valueBinding.getSimpleValue() );
 					String colName = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( col.getAlias( factory.getDialect() ) );
 					columnsLazy.add( lazy );
 					// TODO: properties only selectable if they are non-plural???
 					columnSelectables.add( singularAttributeBinding.getAttribute().isSingular() );
 
 					readers[l] =
 							col.getReadFragment() == null ?
 									col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 									col.getReadFragment();
 					String readerTemplate = getTemplateFromColumn( col, factory );
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
 
 			// TODO: fix this when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
 			//joinedFetchesList.add( singularAttributeBinding.getValue().getFetchMode() );
 			joinedFetchesList.add( FetchMode.DEFAULT );
 			// TODO: fix this when HHH-6355 is fixed; for now assume CascadeStyle.NONE
 			//cascades.add( singularAttributeBinding.getCascadeStyle() );
 			cascades.add( CascadeStyle.NONE );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 7935f32e5b..e7a0032a10 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1025 +1,1025 @@
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
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
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
-		//final Table table = entityBinding.getRootEntityBinding().getBaseTable();
-		final TableSpecification table = entityBinding.getBaseTable();
+		//final Table table = entityBinding.getRootEntityBinding().getPrimaryTable();
+		final TableSpecification table = entityBinding.getPrimaryTable();
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
 			SimpleValue discriminatorRelationalValue = entityBinding.getHierarchyDetails().getEntityDiscriminator().getBoundValue();
 			if ( discriminatorRelationalValue == null ) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getHierarchyDetails().getEntityDiscriminator().isForced();
 			if ( DerivedValue.class.isInstance( discriminatorRelationalValue ) ) {
 				DerivedValue formula = ( DerivedValue ) discriminatorRelationalValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discriminatorRelationalValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				// TODO: fix this when EntityBinding.getRootEntityBinding() is implemented;
 				// for now, assume entityBinding is the root
-				//discriminatorAlias = column.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable );
+				//discriminatorAlias = column.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getPrimaryTable );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 
 			discriminatorType = entityBinding.getHierarchyDetails()
 					.getEntityDiscriminator()
 					.getExplicitHibernateTypeDescriptor()
 					.getResolvedTypeMapping();
 			if ( entityBinding.getDiscriminatorMatchValue() == null ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NOT_NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = entityBinding.getHierarchyDetails().getEntityDiscriminator().isInserted()
 						&& ! DerivedValue.class.isInstance( discriminatorRelationalValue );
 				try {
 					DiscriminatorType dtype = ( DiscriminatorType ) discriminatorType;
 					discriminatorValue = dtype.stringToObject( entityBinding.getDiscriminatorMatchValue() );
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
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				continue; // skip identifier binding
 			}
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			propertyTableNumbers[ i++ ] = 0;
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 
 		// TODO: fix when subclasses are working (HHH-6337)
 		//for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			// TODO: fix when joins are working (HHH-6391)
 			//int join = entityBinding.getJoinNumber(singularAttributeBinding);
 			int join = 0;
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( singularAttributeBinding.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
-					singularAttributeBinding.getEntityBinding().getEntity().getName() + '.' + singularAttributeBinding.getAttribute().getName(),
+					singularAttributeBinding.getContainer().getPathBase() + '.' + singularAttributeBinding.getAttribute().getName(),
 					join
 			);
 
 			for ( SimpleValueBinding simpleValueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( DerivedValue.class.isInstance( simpleValueBinding.getSimpleValue() ) ) {
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
index 01b87c21dc..d4ba5c6a63 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,405 +1,405 @@
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
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
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
 
 		final SimpleSingularAttributeBinding property = mappedEntity.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		// (steve) virtual attributes will still be attributes, they will simply be marked as virtual.
 		//		see org.hibernate.metamodel.domain.AbstractAttributeContainer.locateOrCreateVirtualAttribute()
 
 		final String mappedUnsavedValue = property.getUnsavedValue();
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
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
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
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
 	public static VersionProperty buildVersionProperty(SimpleSingularAttributeBinding property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
-				getConstructor( property.getEntityBinding() )
+				getConstructor( (EntityBinding) property.getContainer() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		final CascadeStyle cascadeStyle = property.isAssociation()
 				? ( (AssociationAttributeBinding) property ).getCascadeStyle()
 				: CascadeStyle.NONE;
 
 		return new VersionProperty(
 		        property.getAttribute().getName(),
 		        null,
 		        property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 		        lazy,
 				true, // insertable
 				true, // updatable
 		        property.getGeneration() == PropertyGeneration.INSERT
 						|| property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isNullable(),
 				!lazy,
 				property.isIncludedInOptimisticLocking(),
 				cascadeStyle,
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
 
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		final boolean alwaysDirtyCheck = type.isAssociationType() && ( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		if ( property.getAttribute().isSingular() ) {
 			final SingularAttributeBinding singularAttributeBinding = ( SingularAttributeBinding ) property;
 			final CascadeStyle cascadeStyle = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getCascadeStyle()
 					: CascadeStyle.NONE;
 			final FetchMode fetchMode = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					singularAttributeBinding.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && singularAttributeBinding.isLazy(),
 					true, // insertable
 					true, // updatable
 					singularAttributeBinding.getGeneration() == PropertyGeneration.INSERT
 							|| singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.isNullable(),
 					alwaysDirtyCheck || areAllValuesIncludedInUpdate( singularAttributeBinding ),
 					singularAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 			);
 		}
 		else {
 			final AbstractPluralAttributeBinding pluralAttributeBinding = (AbstractPluralAttributeBinding) property;
 			final CascadeStyle cascadeStyle = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getCascadeStyle()
 					: CascadeStyle.NONE;
 			final FetchMode fetchMode = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					pluralAttributeBinding.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && pluralAttributeBinding.isLazy(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					true, // pluralAttributeBinding.isInsertable(),
 					true, //pluralAttributeBinding.isUpdatable(),
 					false,
 					false,
 					false, // nullable - not sure what that means for a collection
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					//alwaysDirtyCheck || pluralAttributeBinding.isUpdatable(),
 					true,
 					pluralAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 				);
 		}
 	}
 
 	private static boolean areAllValuesIncludedInUpdate(SingularAttributeBinding attributeBinding) {
 		if ( attributeBinding.hasDerivedValue() ) {
 			return false;
 		}
 		for ( SimpleValueBinding valueBinding : attributeBinding.getSimpleValueBindings() ) {
 			if ( ! valueBinding.isIncludeInUpdate() ) {
 				return false;
 			}
 		}
 		return true;
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
-		if ( mappingProperty == null || mappingProperty.getEntityBinding().getEntity() == null ) {
+		if ( mappingProperty == null || mappingProperty.getContainer().getClassReference() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
-				mappingProperty.getEntityBinding().getEntity().getClassReference(),
+				mappingProperty.getContainer().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 0540ccaa4a..c685de0972 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,555 +1,555 @@
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
 
-		for ( AttributeBinding property : entityBinding.getAttributeBindings() ) {
+		for ( AttributeBinding property : entityBinding.attributeBindings() ) {
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
 					entityBinding.getHierarchyDetails().getEntityIdentifier().isEmbedded()
 							? ( CompositeType ) entityBinding
 									.getHierarchyDetails()
 									.getEntityIdentifier()
 									.getValueBinding()
 									.getHibernateTypeDescriptor()
 									.getResolvedTypeMapping()
 							: null
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
-				mappedProperty.getEntityBinding().getEntity().getClassReference(),
+				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getSetter(
-				mappedProperty.getEntityBinding().getEntity().getClassReference(),
+				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
 		// TODO: Fix this then backrefs are working in new metamodel
 		return PropertyAccessorFactory.getPropertyAccessor(
-				mappedProperty.getEntityBinding().getEntity().getClassReference(),
+				mappedProperty.getContainer().getClassReference(),
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 04ece30937..db1afadfa4 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,180 +1,205 @@
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
+import org.junit.Assert;
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
-	private MetadataSources sources;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
-		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
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
-		MetadataImpl metadata = addSourcesForSimpleEntityBinding( sources );
+		MetadataSources sources = new MetadataSources( serviceRegistry );
+		addSourcesForSimpleEntityBinding( sources );
+		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
-		MetadataImpl metadata = addSourcesForSimpleVersionedEntityBinding( sources );
+		MetadataSources sources = new MetadataSources( serviceRegistry );
+		addSourcesForSimpleVersionedEntityBinding( sources );
+		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleVersionedEntity.class.getName() );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getAttribute() );
 	}
 
 	@Test
 	public void testEntityWithManyToOneMapping() {
-		MetadataImpl metadata = addSourcesForManyToOne( sources );
+		MetadataSources sources = new MetadataSources( serviceRegistry );
+		addSourcesForSimpleEntityBinding( sources );
+		addSourcesForManyToOne( sources );
+		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding simpleEntityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertIdAndSimpleProperty( simpleEntityBinding );
 
-		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
+		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.locateAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
 		SingularAssociationAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
 		Iterator<SingularAssociationAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
 				.iterator();
 		assertTrue( it.hasNext() );
-		assertSame( entityWithManyToOneBinding.getAttributeBinding( "simpleEntity" ), it.next() );
+		assertSame( entityWithManyToOneBinding.locateAttributeBinding( "simpleEntity" ), it.next() );
 		assertFalse( it.hasNext() );
 	}
 
-	public abstract MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
+	@Test
+	public void testSimpleEntityWithSimpleComponentMapping() {
+		MetadataSources sources = new MetadataSources( serviceRegistry );
+		addSourcesForComponentBinding( sources );
+		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
+		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntityWithSimpleComponent.class.getName() );
+		assertRoot( metadata, entityBinding );
+		assertIdAndSimpleProperty( entityBinding );
+
+		ComponentAttributeBinding componentAttributeBinding = (ComponentAttributeBinding) entityBinding.locateAttributeBinding( "simpleComponent" );
+		assertNotNull( componentAttributeBinding );
+		assertSame( componentAttributeBinding.getAttribute().getSingularAttributeType(), componentAttributeBinding.getAttributeContainer() );
+		assertEquals( SimpleEntityWithSimpleComponent.class.getName() + ".simpleComponent", componentAttributeBinding.getPathBase() );
+		assertSame( entityBinding.getPrimaryTable(), componentAttributeBinding.getPrimaryTable() );
+		assertNotNull( componentAttributeBinding.getComponent() );
+	}
+
+	public abstract void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
+
+	public abstract void addSourcesForSimpleEntityBinding(MetadataSources sources);
 
-	public abstract MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources);
+	public abstract void addSourcesForManyToOne(MetadataSources sources);
 
-	public abstract MetadataImpl addSourcesForManyToOne(MetadataSources sources);
+	public abstract void addSourcesForComponentBinding(MetadataSources sources);
 
 	protected void assertIdAndSimpleProperty(EntityBinding entityBinding) {
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier() );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 
-		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
+		AttributeBinding idAttributeBinding = entityBinding.locateAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		SingularAttributeBinding singularIdAttributeBinding = (SingularAttributeBinding) idAttributeBinding;
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getClassReference() );
 
 		assertNotNull( singularIdAttributeBinding.getValue() );
 		assertTrue( singularIdAttributeBinding.getValue() instanceof Column );
 		Datatype idDataType = ( (Column) singularIdAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
-		assertNotNull( entityBinding.getAttributeBinding( "name" ) );
-		assertNotNull( entityBinding.getAttributeBinding( "name" ).getAttribute() );
-		assertTrue( entityBinding.getAttributeBinding( "name" ).getAttribute().isSingular() );
+		assertNotNull( entityBinding.locateAttributeBinding( "name" ) );
+		assertNotNull( entityBinding.locateAttributeBinding( "name" ).getAttribute() );
+		assertTrue( entityBinding.locateAttributeBinding( "name" ).getAttribute().isSingular() );
 
-		SingularAttributeBinding nameBinding = (SingularAttributeBinding) entityBinding.getAttributeBinding( "name" );
+		SingularAttributeBinding nameBinding = (SingularAttributeBinding) entityBinding.locateAttributeBinding( "name" );
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getClassReference() );
 
 		assertNotNull( nameBinding.getValue() );
 		SimpleValue nameValue = (SimpleValue) nameBinding.getValue();
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
index 2d907b2993..aeed08e742 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
@@ -1,50 +1,54 @@
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
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 
 /**
  * Basic tests of annotation based binding code
  *
  * @author Hardy Ferentschik
  */
 public class BasicAnnotationBindingTests extends AbstractBasicBindingTests {
-	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
+	@Override
+	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
 		sources.addAnnotatedClass( SimpleEntity.class );
-		return (MetadataImpl) sources.buildMetadata();
 	}
 
-	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
+	@Override
+	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
 		sources.addAnnotatedClass( SimpleVersionedEntity.class );
-		return (MetadataImpl) sources.buildMetadata();
 	}
 
-	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
+	@Override
+	public void addSourcesForManyToOne(MetadataSources sources) {
 		sources.addAnnotatedClass( ManyToOneEntity.class );
-		sources.addAnnotatedClass( SimpleEntity.class );
-		return (MetadataImpl) sources.buildMetadata();
+	}
+
+	@Override
+	public void addSourcesForComponentBinding(MetadataSources sources) {
+		sources.addAnnotatedClass(  SimpleEntityWithSimpleComponent.class );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
index 927354b15e..805dc4bab6 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
@@ -1,50 +1,49 @@
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
 
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
 
 /**
  * Basic tests of {@code hbm.xml} binding code
  *
  * @author Steve Ebersole
  */
 public class BasicHbmBindingTests extends AbstractBasicBindingTests {
-	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/SimpleEntity.hbm.xml" );
-		return (MetadataImpl) sources.buildMetadata();
 	}
 
-	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/SimpleVersionedEntity.hbm.xml" );
-		return (MetadataImpl) sources.buildMetadata();
 	}
 
-	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
+	public void addSourcesForManyToOne(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/ManyToOneEntity.hbm.xml" );
-		sources.addResource( "org/hibernate/metamodel/binding/SimpleEntity.hbm.xml" );
-		return (MetadataImpl) sources.buildMetadata();
+	}
+
+	public void addSourcesForComponentBinding(MetadataSources sources) {
+		sources.addResource( "org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.hbm.xml" );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.hbm.xml b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.hbm.xml
new file mode 100644
index 0000000000..af62001456
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.hbm.xml
@@ -0,0 +1,19 @@
+<?xml version="1.0"?>
+<hibernate-mapping
+        xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
+        xsi:schemaLocation="http://www.hibernate.org/xsd/hibernate-mapping hibernate-mapping-4.0.xsd"
+        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
+        package="org.hibernate.metamodel.binding">
+
+    <class name="SimpleEntityWithSimpleComponent">
+    	<id name="id">
+    		<generator class="increment"/>
+    	</id>
+        <property name="name"/>
+        <component name="simpleComponent" class="SimpleEntityWithSimpleComponent$SimpleComponent">
+            <property name="value1"/>
+            <property name="value2"/>
+        </component>
+	</class>
+
+</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.java
new file mode 100644
index 0000000000..1ae7cd8901
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleEntityWithSimpleComponent.java
@@ -0,0 +1,92 @@
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
+package org.hibernate.metamodel.binding;
+
+import javax.persistence.Embeddable;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class SimpleEntityWithSimpleComponent {
+	@Id
+	private Long id;
+	private String name;
+	private SimpleComponent simpleComponent;
+
+	public SimpleEntityWithSimpleComponent() {
+	}
+
+	public SimpleEntityWithSimpleComponent(String name) {
+		this.name = name;
+	}
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+
+	public SimpleComponent getSimpleComponent() {
+		return simpleComponent;
+	}
+
+	public void setSimpleComponent(SimpleComponent simpleComponent) {
+		this.simpleComponent = simpleComponent;
+	}
+
+	@Embeddable
+	public static class SimpleComponent {
+		private String value1;
+		private String value2;
+
+		public String getValue1() {
+			return value1;
+		}
+
+		public void setValue1(String value1) {
+			this.value1 = value1;
+		}
+
+		public String getValue2() {
+			return value2;
+		}
+
+		public void setValue2(String value2) {
+			this.value2 = value2;
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/AccessBindingTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/AccessBindingTest.java
index ce79f62ccb..bfbe32b9f3 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/AccessBindingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/AccessBindingTest.java
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import javax.persistence.Access;
 import javax.persistence.AccessType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.metamodel.binding.EntityBinding;
 
 import static junit.framework.Assert.assertEquals;
 
 /**
  * Tests for different types of attribute access
  *
  * @author Hardy Ferentschik
  */
 
 public class AccessBindingTest extends BaseAnnotationBindingTestCase {
 	@Entity
 	class FieldAccess {
 		@Id
 		private int id;
 	}
 
 	@Test
 	@Resources(annotatedClasses = { FieldAccess.class })
 	public void testDefaultFieldAccess() {
 		EntityBinding binding = getEntityBinding( FieldAccess.class );
-		assertEquals( "Wrong access type", "field", binding.getAttributeBinding( "id" ).getPropertyAccessorName() );
+		assertEquals( "Wrong access type", "field", binding.locateAttributeBinding( "id" ).getPropertyAccessorName() );
 	}
 
 	@Entity
 	class PropertyAccess {
 		private int id;
 
 		@Id
 		public int getId() {
 			return id;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = { PropertyAccess.class })
 	public void testDefaultPropertyAccess() {
 		EntityBinding binding = getEntityBinding( PropertyAccess.class );
-		assertEquals( "Wrong access type", "property", binding.getAttributeBinding( "id" ).getPropertyAccessorName() );
+		assertEquals( "Wrong access type", "property", binding.locateAttributeBinding( "id" ).getPropertyAccessorName() );
 	}
 
 
 	@Entity
 	class NoAccess {
 		private int id;
 
 		public int getId() {
 			return id;
 		}
 	}
 
 	@Test(expected = AnnotationException.class)
 	@Resources(annotatedClasses = { NoAccess.class })
 	public void testNoAccess() {
 		// actual error happens when the binding gets created
 	}
 
 	@Entity
 	class MixedAccess {
 		@Id
 		private int id;
 
 		private String name;
 
 		@Access(AccessType.PROPERTY)
 		public String getName() {
 			return name;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = { MixedAccess.class })
 	public void testMixedAccess() {
 		EntityBinding binding = getEntityBinding( MixedAccess.class );
-		assertEquals( "Wrong access type", "field", binding.getAttributeBinding( "id" ).getPropertyAccessorName() );
+		assertEquals( "Wrong access type", "field", binding.locateAttributeBinding( "id" ).getPropertyAccessorName() );
 		assertEquals(
 				"Wrong access type",
 				"property",
-				binding.getAttributeBinding( "name" ).getPropertyAccessorName()
+				binding.locateAttributeBinding( "name" ).getPropertyAccessorName()
 		);
 	}
 
 	@Entity
 	class Base {
 		@Id
 		int id;
 	}
 
 	@Entity
 	@Access(AccessType.PROPERTY)
 	class ClassConfiguredAccess extends Base {
 		private String name;
 
 		public String getName() {
 			return name;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = { ClassConfiguredAccess.class, Base.class })
 	public void testExplicitClassConfiguredAccess() {
 		EntityBinding binding = getEntityBinding( Base.class );
 		assertEquals(
 				"Wrong access type",
 				"field",
-				binding.getAttributeBinding( "id" ).getPropertyAccessorName()
+				binding.locateAttributeBinding( "id" ).getPropertyAccessorName()
 		);
 
 
 		binding = getEntityBinding( ClassConfiguredAccess.class );
 		assertEquals(
 				"Wrong access type",
 				"property",
-				binding.getAttributeBinding( "name" ).getPropertyAccessorName()
+				binding.locateAttributeBinding( "name" ).getPropertyAccessorName()
 		);
 	}
 
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTest.java
index 74f905d6dd..a35651e39f 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTest.java
@@ -1,119 +1,118 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.MappedSuperclass;
 
 import org.junit.Test;
 
-import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.testing.FailureExpected;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * Tests for {@link javax.persistence.MappedSuperclass} {@link javax.persistence.AttributeOverrides}
  * and {@link javax.persistence.AttributeOverride}.
  *
  * @author Hardy Ferentschik
  */
 @FailureExpected(jiraKey = "HHH-6447", message = "Work in progress")
 public class MappedSuperclassTest extends BaseAnnotationBindingTestCase {
 	@Test
 //	@Resources(annotatedClasses = { MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class })
 	public void testSimpleAttributeOverrideInMappedSuperclass() {
 		EntityBinding binding = getEntityBinding( MyEntity.class );
-		SingularAttributeBinding nameBinding = (SingularAttributeBinding) binding.getAttributeBinding( "name" );
+		SingularAttributeBinding nameBinding = (SingularAttributeBinding) binding.locateAttributeBinding( "name" );
 		assertNotNull( "the name attribute should be bound to MyEntity", nameBinding );
 
 		Column column = (Column) nameBinding.getValue();
 		assertEquals( "Wrong column name", "MY_NAME", column.getColumnName().toString() );
 	}
 
 	@Test
 //	@Resources(annotatedClasses = { MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class })
 	public void testLastAttributeOverrideWins() {
 		EntityBinding binding = getEntityBinding( MyEntity.class );
-		SingularAttributeBinding fooBinding = (SingularAttributeBinding) binding.getAttributeBinding( "foo" );
+		SingularAttributeBinding fooBinding = (SingularAttributeBinding) binding.locateAttributeBinding( "foo" );
 		assertNotNull( "the foo attribute should be bound to MyEntity", fooBinding );
 
 		Column column = (Column) fooBinding.getValue();
 		assertEquals( "Wrong column name", "MY_FOO", column.getColumnName().toString() );
 	}
 
 	@Test
 //	@Resources(annotatedClasses = { SubclassOfNoEntity.class, NoEntity.class })
 	public void testNonEntityBaseClass() {
 		EntityBinding binding = getEntityBinding( SubclassOfNoEntity.class );
 		assertEquals( "Wrong entity name", SubclassOfNoEntity.class.getName(), binding.getEntity().getName() );
 		assertEquals( "Wrong entity name", NoEntity.class.getName(), binding.getEntity().getSuperType().getName() );
 		assertTrue( binding.getEntity().getSuperType() instanceof NonEntity );
 	}
 
 	@MappedSuperclass
 	class MyMappedSuperClassBase {
 		@Id
 		private int id;
 		String foo;
 	}
 
 	@MappedSuperclass
 	@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "SUPER_FOO"))
 	class MyMappedSuperClass extends MyMappedSuperClassBase {
 		String name;
 	}
 
 	@Entity
 	@AttributeOverrides( {
 			@AttributeOverride(name = "name", column = @javax.persistence.Column(name = "MY_NAME")),
 			@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "MY_FOO"))
 	})
 	class MyEntity extends MyMappedSuperClass {
 		private Long count;
 
 	}
 
 	class NoEntity {
 		String name;
 		int age;
 	}
 
 	@Entity
 	class SubclassOfNoEntity extends NoEntity {
 		@Id
 		private int id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTest.java
index 517970329f..61ea86b750 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTest.java
@@ -1,67 +1,67 @@
 package org.hibernate.metamodel.source.annotations.entity;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.relational.Identifier;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Strong Liu
  */
 public class QuotedIdentifierTest extends BaseAnnotationBindingTestCase {
 	private final String ormPath = "org/hibernate/metamodel/source/annotations/xml/orm-quote-identifier.xml";
 
 	@Test
 	@Resources(annotatedClasses = { Item.class, Item2.class, Item3.class, Item4.class }, ormXmlPath = ormPath)
 	public void testDelimitedIdentifiers() {
 		EntityBinding item = getEntityBinding( Item.class );
 		assertIdentifierEquals( "`QuotedIdentifierTest$Item`", item );
 
 		item = getEntityBinding( Item2.class );
 		assertIdentifierEquals( "`TABLE_ITEM2`", item );
 
 		item = getEntityBinding( Item3.class );
 		assertIdentifierEquals( "`TABLE_ITEM3`", item );
 
 		item = getEntityBinding( Item4.class );
 		assertIdentifierEquals( "`TABLE_ITEM4`", item );
 	}
 
 	private void assertIdentifierEquals(String expected, EntityBinding realValue) {
-		org.hibernate.metamodel.relational.Table table = (org.hibernate.metamodel.relational.Table) realValue.getBaseTable();
+		org.hibernate.metamodel.relational.Table table = (org.hibernate.metamodel.relational.Table) realValue.getPrimaryTable();
 		assertEquals( Identifier.toIdentifier( expected ), table.getTableName() );
 	}
 
 	@Entity
 	private static class Item {
 		@Id
 		Long id;
 	}
 
 	@Entity
 	@Table(name = "TABLE_ITEM2")
 	private static class Item2 {
 		@Id
 		Long id;
 	}
 
 	@Entity
 	@Table(name = "`TABLE_ITEM3`")
 	private static class Item3 {
 		@Id
 		Long id;
 	}
 
 	@Entity
 	@Table(name = "\"TABLE_ITEM4\"")
 	private static class Item4 {
 		@Id
 		Long id;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
index e4c4fb2b02..d7faf047e2 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
@@ -1,142 +1,142 @@
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
 
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 import javax.persistence.Table;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 
 import static junit.framework.Assert.assertEquals;
 
 /**
  * @author Hardy Ferentschik
  */
 public class TableNameTest extends BaseAnnotationBindingTestCase {
 
 	@Entity
 	class A {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 
 	@Entity
 	class B extends A {
 	}
 
 	@Test
 	@Resources(annotatedClasses = { A.class, B.class })
 	public void testSingleInheritanceDefaultTableName() {
 		EntityBinding binding = getEntityBinding( A.class );
 		assertEquals( "wrong inheritance type", InheritanceType.SINGLE_TABLE, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"TableNameTest$A",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 
 		binding = getEntityBinding( B.class );
 		assertEquals( "wrong inheritance type", InheritanceType.SINGLE_TABLE, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"TableNameTest$A",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 	}
 
 	@Entity
 	@Inheritance(strategy = javax.persistence.InheritanceType.JOINED)
 	@Table(name = "FOO")
 	class JoinedA {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 
 	@Entity
 	class JoinedB extends JoinedA {
 	}
 
 	@Test
 	@Resources(annotatedClasses = { JoinedA.class, JoinedB.class })
 	public void testJoinedSubclassDefaultTableName() {
 		EntityBinding binding = getEntityBinding( JoinedA.class );
 		assertEquals( "wrong inheritance type", InheritanceType.JOINED, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"FOO",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 
 		binding = getEntityBinding( JoinedB.class );
 		assertEquals( "wrong inheritance type", InheritanceType.JOINED, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"TableNameTest$JoinedB",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 	}
 
 
 	@Entity
 	@Inheritance(strategy = javax.persistence.InheritanceType.TABLE_PER_CLASS)
 	class TablePerClassA {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 
 	@Entity
 	class TablePerClassB extends TablePerClassA {
 	}
 
 	@Test
 	@Resources(annotatedClasses = { TablePerClassA.class, TablePerClassB.class })
 	public void testTablePerClassDefaultTableName() {
 		EntityBinding binding = getEntityBinding( TablePerClassA.class );
 		assertEquals( "wrong inheritance type", InheritanceType.TABLE_PER_CLASS, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"TableNameTest$TablePerClassA",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 
 		binding = getEntityBinding( TablePerClassB.class );
 		assertEquals( "wrong inheritance type", InheritanceType.TABLE_PER_CLASS, binding.getHierarchyDetails().getInheritanceType() );
 		assertEquals(
 				"wrong table name",
 				"TableNameTest$TablePerClassB",
-				( (org.hibernate.metamodel.relational.Table) binding.getBaseTable() ).getTableName().getName()
+				( (org.hibernate.metamodel.relational.Table) binding.getPrimaryTable() ).getTableName().getName()
 		);
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/UniqueConstraintBindingTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/UniqueConstraintBindingTest.java
index ac51759f03..bccbc2ecc7 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/UniqueConstraintBindingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/UniqueConstraintBindingTest.java
@@ -1,79 +1,79 @@
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
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.UniqueConstraint;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * test for {@link javax.persistence.UniqueConstraint}
  *
  * @author Strong Liu
  */
 public class UniqueConstraintBindingTest extends BaseAnnotationBindingTestCase {
 	@Test
 	@Resources(annotatedClasses = TableWithUniqueConstraint.class)
 	public void testTableUniqueConstraints() {
 		EntityBinding binding = getEntityBinding( TableWithUniqueConstraint.class );
-		TableSpecification table = binding.getBaseTable();
+		TableSpecification table = binding.getPrimaryTable();
 		Iterable<UniqueKey> uniqueKeyIterable = table.getUniqueKeys();
 		assertNotNull( uniqueKeyIterable );
 		int i = 0;
 		for ( UniqueKey key : uniqueKeyIterable ) {
 			i++;
 			assertEquals( "u1", key.getName() );
 			assertTrue( table == key.getTable() );
 			assertNotNull( key.getColumns() );
 			int j = 0;
 			for ( Column column : key.getColumns() ) {
 				j++;
 			}
 			assertEquals( "There should be two columns in the unique constraint", 2, j );
 		}
 		assertEquals( "There should only be one unique constraint", 1, i );
 	}
 
 	@Entity
 	@Table(uniqueConstraints = { @UniqueConstraint(name = "u1", columnNames = { "name", "age" }) })
 	class TableWithUniqueConstraint {
 		@Id
 		int id;
 		String name;
 		int age;
 	}
 }
