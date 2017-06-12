diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index d054f522be..1d4d2bfe4b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,350 +1,385 @@
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
+import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.binding.state.EntityBindingState;
 import org.hibernate.metamodel.domain.Entity;
+import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
+import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Provides the link between the domain and the relational model for an entity.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class EntityBinding {
-	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
+	private Entity entity;
+	private TableSpecification baseTable;
 
-	private boolean isRoot;
+	private EntityMode entityMode;
+	private JavaType proxyInterfaceType;
+
+	private String jpaEntityName;
+
+	private Class<EntityPersister> entityPersisterClass;
+	private Class<EntityTuplizer> entityTuplizerClass;
 
+	private boolean isRoot;
 	private InheritanceType entityInheritanceType;
+
+	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private EntityDiscriminator entityDiscriminator;
 	private SimpleAttributeBinding versionBinding;
 
-	private Entity entity;
-	private TableSpecification baseTable;
-
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 	private Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
 	private Caching caching;
 
 	private MetaAttributeContext metaAttributeContext;
 
-	private String proxyInterfaceName;
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
 	private int optimisticLockMode;
 
-	private Class entityPersisterClass;
 	private Boolean isAbstract;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
-	public EntityBinding initialize(EntityBindingState state) {
+	public EntityBinding initialize(BindingContext bindingContext, EntityBindingState state) {
+		// todo : Entity will need both entityName and className to be effective
+		this.entity = new Entity( state.getEntityName(), state.getSuperType(), bindingContext.makeJavaType( state.getClassName() ) );
+
 		this.isRoot = state.isRoot();
 		this.entityInheritanceType = state.getEntityInheritanceType();
+
+		this.entityMode = state.getEntityMode();
+		this.jpaEntityName = state.getJpaEntityName();
+
+		// todo : handle the entity-persister-resolver stuff
+		this.entityPersisterClass = state.getCustomEntityPersisterClass();
+		this.entityTuplizerClass = state.getCustomEntityTuplizerClass();
+
 		this.caching = state.getCaching();
 		this.metaAttributeContext = state.getMetaAttributeContext();
-		this.proxyInterfaceName = state.getProxyInterfaceName();
-		this.lazy = state.isLazy();
+
+		if ( entityMode == EntityMode.POJO ) {
+			if ( state.getProxyInterfaceName() != null ) {
+				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
+				this.lazy = true;
+			}
+			else if ( state.isLazy() ) {
+				this.proxyInterfaceType = entity.getJavaType();
+				this.lazy = true;
+			}
+		}
+		else {
+			this.proxyInterfaceType = new JavaType( Map.class );
+			this.lazy = state.isLazy();
+		}
+
 		this.mutable = state.isMutable();
 		this.explicitPolymorphism = state.isExplicitPolymorphism();
 		this.whereFilter = state.getWhereFilter();
 		this.rowId = state.getRowId();
-		this.dynamicInsert = state.isDynamicUpdate();
+		this.dynamicUpdate = state.isDynamicUpdate();
 		this.dynamicInsert = state.isDynamicInsert();
 		this.batchSize = state.getBatchSize();
 		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
 		this.optimisticLockMode = state.getOptimisticLockMode();
-		this.entityPersisterClass = state.getEntityPersisterClass();
 		this.isAbstract = state.isAbstract();
 		this.customInsert = state.getCustomInsert();
 		this.customUpdate = state.getCustomUpdate();
 		this.customDelete = state.getCustomDelete();
 		if ( state.getSynchronizedTableNames() != null ) {
 			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
 				addSynchronizedTable( synchronizedTableName );
 			}
 		}
 		return this;
 	}
 
 	public boolean isRoot() {
 		return isRoot;
 	}
 
 	public void setRoot(boolean isRoot) {
 		this.isRoot = isRoot;
 	}
 
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
 
 	public EntityIdentifier getEntityIdentifier() {
 		return entityIdentifier;
 	}
 
 	public void bindEntityIdentifier(SimpleAttributeBinding attributeBinding) {
 		if ( !Column.class.isInstance( attributeBinding.getValue() ) ) {
 			throw new MappingException(
 					"Identifier value must be a Column; instead it is: " + attributeBinding.getValue().getClass()
 			);
 		}
 		entityIdentifier.setValueBinding( attributeBinding );
 		baseTable.getPrimaryKey().addColumn( Column.class.cast( attributeBinding.getValue() ) );
 	}
 
 	public EntityDiscriminator getEntityDiscriminator() {
 		return entityDiscriminator;
 	}
 
 	public void setInheritanceType(InheritanceType entityInheritanceType) {
 		this.entityInheritanceType = entityInheritanceType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	public boolean isVersioned() {
 		return versionBinding != null;
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
 
 	public Iterable<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
 	public SimpleAttributeBinding makeSimpleIdAttributeBinding(String name) {
 		final SimpleAttributeBinding binding = makeSimpleAttributeBinding( name, true, true );
 		getEntityIdentifier().setValueBinding( binding );
 		return binding;
 	}
 
 	public EntityDiscriminator makeEntityDiscriminator(String attributeName) {
 		if ( entityDiscriminator != null ) {
 			throw new AssertionFailure( "Creation of entity discriminator was called more than once" );
 		}
 		entityDiscriminator = new EntityDiscriminator();
 		entityDiscriminator.setValueBinding( makeSimpleAttributeBinding( attributeName, true, false ) );
 		return entityDiscriminator;
 	}
 
 	public SimpleAttributeBinding makeVersionBinding(String attributeName) {
 		versionBinding = makeSimpleAttributeBinding( attributeName, true, false );
 		return versionBinding;
 	}
 
 	public SimpleAttributeBinding makeSimpleAttributeBinding(String name) {
 		return makeSimpleAttributeBinding( name, false, false );
 	}
 
 	private SimpleAttributeBinding makeSimpleAttributeBinding(String name, boolean forceNonNullable, boolean forceUnique) {
 		final SimpleAttributeBinding binding = new SimpleAttributeBinding( this, forceNonNullable, forceUnique );
 		registerAttributeBinding( name, binding );
 		binding.setAttribute( entity.getAttribute( name ) );
 		return binding;
 	}
 
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(String attributeName) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
 		return binding;
 	}
 
 	public BagBinding makeBagAttributeBinding(String attributeName, CollectionElementType collectionElementType) {
 		final BagBinding binding = new BagBinding( this, collectionElementType );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
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
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
-	public String getProxyInterfaceName() {
-		return proxyInterfaceName;
+	public JavaType getProxyInterfaceType() {
+		return proxyInterfaceType;
 	}
 
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public String getDiscriminatorValue() {
 		return entityDiscriminator == null ? null : entityDiscriminator.getDiscriminatorValue();
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	public Class getEntityPersisterClass() {
 		return entityPersisterClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	protected void addSynchronizedTable(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	// Custom SQL ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private String loaderName;
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String loaderName) {
 		this.loaderName = loaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java
index 83963a6859..1fd0ff1aaa 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java
@@ -1,78 +1,123 @@
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
 package org.hibernate.metamodel.binding.state;
 
 import java.util.Set;
 
+import org.hibernate.EntityMode;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
+ * Represents unified set of information about metadata specific to binding an entity.
+ *
  * @author Gail Badner
+ * @author Steve Ebersole
  */
 public interface EntityBindingState {
+	/**
+	 * Obtain the Hibernate entity name.
+	 *
+	 * @return The entity name.
+	 */
+	public String getEntityName();
+
+	/**
+	 * Obtain the JPA entity name.
+	 *
+	 * @return  The JPA entity name
+	 */
+	public String getJpaEntityName();
+
+	/**
+	 * Obtain the entity mode represented by this state.
+	 *
+	 * @return The entity mode.
+	 */
+	public EntityMode getEntityMode();
+
+	/**
+	 * Obtain the name of the entity class.
+	 *
+	 * @return The entity class name.
+	 */
+	public String getClassName();
+
+	/**
+	 * The name of an interface to use for creating instance proxies for this entity.
+	 *
+	 * @return The name of the proxy interface.
+	 */
+	public String getProxyInterfaceName();
+
+	public Class<EntityPersister> getCustomEntityPersisterClass();
+
+	public Class<EntityTuplizer> getCustomEntityTuplizerClass();
+
+	public Hierarchical getSuperType();
+
 	boolean isRoot();
 
 	InheritanceType getEntityInheritanceType();
 
 	Caching getCaching();
 
 	MetaAttributeContext getMetaAttributeContext();
 
-	String getProxyInterfaceName();
-
 	boolean isLazy();
 
 	boolean isMutable();
 
 	boolean isExplicitPolymorphism();
 
 	String getWhereFilter();
 
 	String getRowId();
 
 	boolean isDynamicUpdate();
 
 	boolean isDynamicInsert();
 
 	int getBatchSize();
 
 	boolean isSelectBeforeUpdate();
 
 	int getOptimisticLockMode();
 
-	Class getEntityPersisterClass();
 
 	Boolean isAbstract();
 
 	CustomSQL getCustomInsert();
 
 	CustomSQL getCustomUpdate();
 
 	CustomSQL getCustomDelete();
 
 	Set<String> getSynchronizedTableNames();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
index a043c22ce8..f7d6f1a79c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
@@ -1,235 +1,234 @@
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
 
 /**
  * Convenient base class for {@link AttributeContainer}.  Because in our model all
  * {@link AttributeContainer AttributeContainers} are also {@link Hierarchical} we also implement that here
  * as well.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeContainer implements AttributeContainer, Hierarchical {
 	private final String name;
 	private final Hierarchical superType;
 	private LinkedHashSet<Attribute> attributeSet = new LinkedHashSet<Attribute>();
 	private HashMap<String, Attribute> attributeMap = new HashMap<String, Attribute>();
 
 	public AbstractAttributeContainer(String name, Hierarchical superType) {
 		this.name = name;
 		this.superType = superType;
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	@Override
 	public Set<Attribute> getAttributes() {
 		return Collections.unmodifiableSet( attributeSet );
 	}
 
 	@Override
 	public Attribute getAttribute(String name) {
 		return attributeMap.get( name );
 	}
 
 	@Override
 	public SingularAttribute getOrCreateSingularAttribute(String name) {
 		SingularAttribute attribute = (SingularAttribute) getAttribute( name );
 		if ( attribute == null ) {
 			attribute = new SingularAttributeImpl( name, this );
 			addAttribute( attribute );
 		}
 		return attribute;
 	}
 
 	@Override
 	public PluralAttribute getOrCreateBag(String name) {
 		return getOrCreatePluralAttribute( name, PluralAttributeNature.BAG );
 	}
 
 	@Override
 	public PluralAttribute getOrCreateSet(String name) {
 		return getOrCreatePluralAttribute( name, PluralAttributeNature.SET );
 	}
 
 	@Override
 	public IndexedPluralAttribute getOrCreateList(String name) {
 		return (IndexedPluralAttribute) getOrCreatePluralAttribute( name, PluralAttributeNature.LIST );
 	}
 
 	@Override
 	public IndexedPluralAttribute getOrCreateMap(String name) {
 		return (IndexedPluralAttribute) getOrCreatePluralAttribute( name, PluralAttributeNature.MAP );
 	}
 
 	@Override
 	public PluralAttribute getOrCreatePluralAttribute(String name, PluralAttributeNature nature) {
 		PluralAttribute attribute = (PluralAttribute) getAttribute( name );
 		if ( attribute == null ) {
 			attribute = nature.isIndexed()
 					? new IndexedPluralAttributeImpl( name, nature, this )
 					: new PluralAttributeImpl( name, nature, this );
 			addAttribute( attribute );
 		}
 		return attribute;
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
 
 		boolean isTypeResolved() {
 			return type != null;
 		}
 
 		void resolveType(Type type) {
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
-		private String nodeName;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
index 5e11ae6b06..233002176b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
@@ -1,47 +1,51 @@
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
 
 /**
- * Models a basic type, a simple value.
+ * Models a basic type.
  *
  * @author Steve Ebersole
  */
 public class BasicType implements Type {
-	private final String name;
+	private final JavaType javaType;
 
-	public BasicType(String name) {
-		this.name = name;
+	public BasicType(JavaType javaType) {
+		this.javaType = javaType;
 	}
 
 	@Override
 	public String getName() {
-		return name;
+		return javaType.getName();
+	}
+
+	public JavaType getJavaType() {
+		return javaType;
 	}
 
 	@Override
 	public TypeNature getNature() {
 		return TypeNature.BASIC;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
index 106eb3010a..48c75d44de 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
@@ -1,154 +1,56 @@
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
 
-import org.hibernate.EntityMode;
-import org.hibernate.MappingException;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.tuple.entity.EntityTuplizer;
-
 /**
  * Models the notion of an entity
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class Entity extends AbstractAttributeContainer {
-	private final PojoEntitySpecifics pojoEntitySpecifics = new PojoEntitySpecifics();
-	private final MapEntitySpecifics mapEntitySpecifics = new MapEntitySpecifics();
+	final JavaType javaType;
 
 	/**
 	 * Constructor for the entity
 	 *
 	 * @param name the name of the entity
 	 * @param superType the super type for this entity. If there is not super type {@code null} needs to be passed.
+	 * @param javaType the java type of the entity
 	 */
-	public Entity(String name, Hierarchical superType) {
+	public Entity(String name, Hierarchical superType, JavaType javaType) {
 		super( name, superType );
+		this.javaType = javaType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public TypeNature getNature() {
 		return TypeNature.ENTITY;
 	}
 
-	public PojoEntitySpecifics getPojoEntitySpecifics() {
-		return pojoEntitySpecifics;
-	}
-
-	public MapEntitySpecifics getMapEntitySpecifics() {
-		return mapEntitySpecifics;
-	}
-
-	public static interface EntityModeEntitySpecifics {
-		public EntityMode getEntityMode();
-
-		public String getTuplizerClassName();
-
-		public Class<EntityTuplizer> getTuplizerClass();
-	}
-
-	public static class PojoEntitySpecifics implements EntityModeEntitySpecifics {
-		private JavaType tuplizerClass;
-		private JavaType entityClass;
-		private JavaType proxyInterface;
-
-		@Override
-		public EntityMode getEntityMode() {
-			return EntityMode.POJO;
-		}
-
-		public String getTuplizerClassName() {
-			return tuplizerClass.getName();
-		}
-
-		public void setTuplizerClassName(String tuplizerClassName, ClassLoaderService classLoaderService) {
-			this.tuplizerClass = new JavaType( tuplizerClassName, classLoaderService);
-		}
-
-		@SuppressWarnings( {"unchecked"} )
-		public Class<EntityTuplizer> getTuplizerClass() {
-			Class clazz = tuplizerClass.getClassReference();
-			if ( ! EntityTuplizer.class.isAssignableFrom( clazz ) ) {
-				throw new MappingException( "Class does not implement EntityTuplizer" );
-			}
-			return ( Class<EntityTuplizer> ) clazz;
-		}
-
-		public String getClassName() {
-			return entityClass.getName();
-		}
-
-		public void setClassName(String className, ClassLoaderService classLoaderService) {
-			this.entityClass = new JavaType( className, classLoaderService );
-		}
-
-		public Class<?> getEntityClass() {
-			return entityClass.getClassReference();
-		}
-
-		public String getProxyInterfaceName() {
-			return proxyInterface.getName();
-		}
-
-		public void setProxyInterfaceName(String proxyInterfaceName, ClassLoaderService classLoaderService) {
-			this.proxyInterface = new JavaType( proxyInterfaceName, classLoaderService );
-		}
-
-		public Class<?> getProxyInterfaceClass() {
-			return proxyInterface.getClassReference();
-		}
-	}
-
-
-	public static class MapEntitySpecifics implements EntityModeEntitySpecifics {
-		private JavaType tuplizerClass;
-
-		@Override
-		public EntityMode getEntityMode() {
-			return EntityMode.MAP;
-		}
-
-		public String getTuplizerClassName() {
-			return tuplizerClass.getName();
-		}
-
-		public void setTuplizerClassName(String tuplizerClassName, ClassLoaderService classLoaderService) {
-			this.tuplizerClass = new JavaType( tuplizerClassName, classLoaderService );
-		}
-
-		@SuppressWarnings( {"unchecked"} )
-		public Class<EntityTuplizer> getTuplizerClass() {
-			Class clazz = tuplizerClass.getClassReference();
-			if ( ! EntityTuplizer.class.isAssignableFrom( clazz ) ) {
-				throw new MappingException( "Class does not implement EntityTuplizer" );
-			}
-			return ( Class<EntityTuplizer> ) clazz;
-		}
-
+	public JavaType getJavaType() {
+		return javaType;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
index f861c104ef..c34de36628 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
@@ -1,58 +1,70 @@
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
 package org.hibernate.metamodel.domain;
 
 import org.hibernate.internal.util.Value;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Models the naming of a Java type where we may not have access to that type's {@link Class} reference.  Generally
  * speaking this is the case in various hibernate-tools and reverse-engineering use cases.
  *
  * @author Steve Ebersole
  */
 public class JavaType {
 	private final String name;
 	private final Value<Class<?>> classReference;
 
 	public JavaType(final String name, final ClassLoaderService classLoaderService) {
 		this.name = name;
 		this.classReference = new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.classForName( name );
 					}
 				}
 		);
 	}
 
+	public JavaType(Class<?> theClass) {
+		this.name = theClass.getName();
+		this.classReference = new Value<Class<?>>( theClass );
+	}
+
 	public String getName() {
 		return name;
 	}
 
 	public Class<?> getClassReference() {
 		return classReference.getValue();
 	}
+
+	@Override
+	public String toString() {
+		return new StringBuilder( super.toString() )
+				.append( "[name=" ).append( name ).append( "]" )
+				.toString();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
index 75c332f305..f1ed8f35ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Basic information about a Java type, in regards to its role in particular set of mappings.
  *
  * @author Steve Ebersole
  */
 public interface Type {
 	/**
 	 * Get the name of the type.
 	 *
 	 * @return The name
 	 */
 	public String getName();
 
 	/**
 	 * Return the persistence type.
 	 *
 	 * @return persistence type
 	 */
 	public TypeNature getNature();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index a280b4dd18..0f6ed61d54 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,705 +1,701 @@
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
 
+import javax.persistence.GenerationType;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
-import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.ResultCheckStyle;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.entity.state.binding.AttributeBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.binding.DiscriminatorBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.binding.EntityBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.relational.TupleRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final ConfiguredClass configuredClass;
 	private final MetadataImplementor meta;
 
 	private Schema.Name schemaName;
 
 	public EntityBinder(MetadataImplementor metadata, ConfiguredClass configuredClass) {
 		this.configuredClass = configuredClass;
 		this.meta = metadata;
 	}
 
 	public void bind() {
 		EntityBinding entityBinding = new EntityBinding();
-		EntityBindingStateImpl entityBindingState = new EntityBindingStateImpl( configuredClass );
+		EntityBindingStateImpl entityBindingState = new EntityBindingStateImpl( getSuperType(), configuredClass );
 
-		bindJpaEntityAnnotation( entityBinding, entityBindingState );
+		bindJpaEntityAnnotation( entityBindingState );
 		bindHibernateEntityAnnotation( entityBindingState ); // optional hibernate specific @org.hibernate.annotations.Entity
 
 		schemaName = createSchemaName();
 		bindTable( entityBinding );
 
-		bindInheritance( entityBinding );
-
 		// bind entity level annotations
 		bindWhereFilter( entityBindingState );
 		bindJpaCaching( entityBindingState );
 		bindHibernateCaching( entityBindingState );
 		bindProxy( entityBindingState );
 		bindSynchronize( entityBindingState );
 		bindCustomSQL( entityBindingState );
 		bindRowId( entityBindingState );
 		bindBatchSize( entityBindingState );
 
+		entityBinding.initialize( meta, entityBindingState );
+
+		bindInheritance( entityBinding );
+
 		// take care of the id, attributes and relations
 		if ( configuredClass.isRoot() ) {
 			bindId( entityBinding );
 		}
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindTableUniqueConstraints( entityBinding );
 
 		// last, but not least we initialize and register the new EntityBinding
-		entityBinding.initialize( entityBindingState );
 		meta.addEntity( entityBinding );
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
 	/**
 	 * Bind {@link javax.persistence.UniqueConstraint} to table as a {@link UniqueKey}
 	 *
 	 * @param tableAnnotation JPA annotations which has a {@code uniqueConstraints} attribute.
 	 * @param table Table which the UniqueKey bind to.
 	 */
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
 				uniqueKey.addColumn( table.getOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindInheritance(EntityBinding entityBinding) {
 		entityBinding.setInheritanceType( configuredClass.getInheritanceType() );
 		switch ( configuredClass.getInheritanceType() ) {
 			case SINGLE_TABLE: {
 				bindDiscriminatorColumn( entityBinding );
 				break;
 			}
 			case JOINED: {
 				// todo
 				break;
 			}
 			case TABLE_PER_CLASS: {
 				// todo
 				break;
 			}
 			default: {
 				// do nothing
 			}
 		}
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				configuredClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 
 		bindSingleMappedAttribute( entityBinding, discriminatorAttribute );
 
 		if ( !( discriminatorAttribute.getColumnValues() instanceof DiscriminatorColumnValues ) ) {
 			throw new AssertionFailure( "Expected discriminator column values" );
 		}
 	}
 
 	private void bindWhereFilter(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		if ( whereAnnotation != null ) {
 			// no null check needed, it is a required attribute
 			String clause = whereAnnotation.value( "clause" ).asString();
 			entityBindingState.setWhereFilter( clause );
 		}
 	}
 
 	private void bindHibernateCaching(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( cacheAnnotation == null ) {
 			return;
 		}
 
 		String region;
 		if ( cacheAnnotation.value( "region" ) != null ) {
 			region = cacheAnnotation.value( "region" ).asString();
 		}
 		else {
 			region = entityBindingState.getEntityName();
 		}
 
 		boolean cacheLazyProperties = true;
 		if ( cacheAnnotation.value( "include" ) != null ) {
 			String tmp = cacheAnnotation.value( "include" ).asString();
 			if ( "all".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + tmp );
 			}
 		}
 
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.valueOf(
 				cacheAnnotation.value( "usage" ).asEnum()
 		);
 		Caching caching = new Caching( region, strategy.toAccessType(), cacheLazyProperties );
 		entityBindingState.setCaching( caching );
 	}
 
 	// This does not take care of any inheritance of @Cacheable within a class hierarchy as specified in JPA2.
 	// This is currently not supported (HF)
 	private void bindJpaCaching(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( cacheAnnotation != null && cacheAnnotation.value() != null ) {
 			cacheable = cacheAnnotation.value().asBoolean();
 		}
 
 		Caching caching = null;
 		switch ( meta.getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				caching = createCachingForCacheableAnnotation( entityBindingState );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheAnnotation == null || cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		if ( caching != null ) {
 			entityBindingState.setCaching( caching );
 		}
 	}
 
 	private void bindProxy(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance proxyAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		boolean lazy = true;
 		String proxyInterfaceClass = null;
 
 		if ( proxyAnnotation != null ) {
 			AnnotationValue lazyValue = proxyAnnotation.value( "lazy" );
 			if ( lazyValue != null ) {
 				lazy = lazyValue.asBoolean();
 			}
 
 			AnnotationValue proxyClassValue = proxyAnnotation.value( "proxyClass" );
 			if ( proxyClassValue != null ) {
 				proxyInterfaceClass = proxyClassValue.asString();
 			}
 		}
 
 		entityBindingState.setLazy( lazy );
 		entityBindingState.setProxyInterfaceName( proxyInterfaceClass );
 	}
 
 	private void bindSynchronize(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 
 		if ( synchronizeAnnotation != null ) {
 			String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			for ( String tableName : tableNames ) {
 				entityBindingState.addSynchronizedTableName( tableName );
 			}
 		}
 	}
 
 	private void bindCustomSQL(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBindingState.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBindingState.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		AnnotationInstance sqlDeleteAllAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.SQL_DELETE_ALL
 		);
 		if ( sqlDeleteAllAnnotation != null ) {
 			entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAllAnnotation ) );
 		}
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSQLAnnotation) {
 		if ( customSQLAnnotation == null ) {
 			return null;
 		}
 
 		String sql = customSQLAnnotation.value( "sql" ).asString();
 		boolean isCallable = false;
 		AnnotationValue callableValue = customSQLAnnotation.value( "callable" );
 		if ( callableValue != null ) {
 			isCallable = callableValue.asBoolean();
 		}
 
 		ResultCheckStyle checkStyle = ResultCheckStyle.NONE;
 		AnnotationValue checkStyleValue = customSQLAnnotation.value( "check" );
 		if ( checkStyleValue != null ) {
 			checkStyle = Enum.valueOf( ResultCheckStyle.class, checkStyleValue.asEnum() );
 		}
 
 		return new CustomSQL(
 				sql,
 				isCallable,
 				Enum.valueOf( ExecuteUpdateResultCheckStyle.class, checkStyle.toString() )
 		);
 	}
 
 	private void bindRowId(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 
 		if ( rowIdAnnotation != null ) {
 			entityBindingState.setRowId( rowIdAnnotation.value().asString() );
 		}
 	}
 
 	private void bindBatchSize(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 
 		if ( batchSizeAnnotation != null ) {
 			entityBindingState.setBatchSize( batchSizeAnnotation.value( "size" ).asInt() );
 		}
 	}
 
 	private Caching createCachingForCacheableAnnotation(EntityBindingStateImpl entityBindingState) {
 		String region = entityBindingState.getEntityName();
 		RegionFactory regionFactory = meta.getServiceRegistry().getService( RegionFactory.class );
 		AccessType defaultAccessType = regionFactory.getDefaultAccessType();
 		return new Caching( region, defaultAccessType, true );
 	}
 
 	private Schema.Name createSchemaName() {
 		String schema = null;
 		String catalog = null;
 
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			AnnotationValue schemaValue = tableAnnotation.value( "schema" );
 			AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
 
 			schema = schemaValue != null ? schemaValue.asString() : null;
 			catalog = catalogValue != null ? catalogValue.asString() : null;
 		}
 
 		return new Schema.Name( schema, catalog );
 	}
 
 	private void bindTable(EntityBinding entityBinding) {
 		final Schema schema = meta.getDatabase().getSchema( schemaName );
 		final Identifier tableName = Identifier.toIdentifier( configuredClass.getPrimaryTableName() );
 		org.hibernate.metamodel.relational.Table table = schema.getTable( tableName );
 		if ( table == null ) {
 			table = schema.createTable( tableName );
 		}
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( configuredClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				// todo
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
 
-	private void bindJpaEntityAnnotation(EntityBinding entityBinding, EntityBindingStateImpl entityBindingState) {
+	private void bindJpaEntityAnnotation(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.ENTITY
 		);
 		String name;
 		if ( jpaEntityAnnotation.value( "name" ) == null ) {
 			name = configuredClass.getName();
 		}
 		else {
 			name = jpaEntityAnnotation.value( "name" ).asString();
 		}
-		entityBindingState.setEntityName( name );
-		entityBinding.setEntity( new Entity( name, getSuperType() ) );
+		entityBindingState.setJpaEntityName( name );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = configuredClass.getMappedProperty( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( idName );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( idName );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize( new ColumnRelationalStateImpl( (SimpleAttribute) idAttribute, meta ) );
 		bindSingleIdGeneratedValue( entityBinding, idName );
 
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							configuredClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValueAsString( generatedValueAnn, "generator" );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = meta.getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s refering an undefined generator [%s]",
 								configuredClass.getName(),
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
 				meta.getOptions().useNewIdentifierGenerators()
 		);
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							configuredClass.getName(),
 							idName
 					)
 			);
 		}
 		else {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 	}
 
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		for ( MappedAttribute mappedAttribute : configuredClass.getMappedAttributes() ) {
 			if ( mappedAttribute instanceof AssociationAttribute ) {
 				bindAssociationAttribute( entityBinding, (AssociationAttribute) mappedAttribute );
 			}
 			else {
 				bindSingleMappedAttribute( entityBinding, (SimpleAttribute) mappedAttribute );
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(EntityBinding entityBinding, AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
 				entityBinding.getEntity().getOrCreateSingularAttribute( associationAttribute.getName() );
 				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
 						associationAttribute.getName()
 				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( configuredClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, meta
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
 
 	private void bindSingleMappedAttribute(EntityBinding entityBinding, SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
 		String attributeName = simpleAttribute.getName();
 		entityBinding.getEntity().getOrCreateSingularAttribute( attributeName );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attributeName );
 			DiscriminatorBindingState bindingState = new DiscriminatorBindingStateImpl( simpleAttribute );
 			entityDiscriminator.initialize( bindingState );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attributeName );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attributeName );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 
 		if ( configuredClass.hasOwnTable() ) {
 			ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 					simpleAttribute, meta
 			);
 			TupleRelationalStateImpl relationalState = new TupleRelationalStateImpl();
 			relationalState.addValueState( columnRelationsState );
 
 			attributeBinding.initialize( relationalState );
 		}
 	}
 
 	private void bindHibernateEntityAnnotation(EntityBindingStateImpl entityBindingState) {
 		// initialize w/ the defaults
 		boolean mutable = true;
 		boolean dynamicInsert = false;
 		boolean dynamicUpdate = false;
 		boolean selectBeforeUpdate = false;
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		OptimisticLockType optimisticLock = OptimisticLockType.VERSION;
 
 		AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		if ( hibernateEntityAnnotation != null ) {
 			if ( hibernateEntityAnnotation.value( "mutable" ) != null ) {
 				mutable = hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicInsert" ) != null ) {
 				dynamicInsert = hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicUpdate" ) != null ) {
 				dynamicUpdate = hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null ) {
 				selectBeforeUpdate = hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 				polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 			}
 
 			if ( hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 				optimisticLock = OptimisticLockType.valueOf(
 						hibernateEntityAnnotation.value( "optimisticLock" ).asEnum()
 				);
 			}
 
 			if ( hibernateEntityAnnotation.value( "persister" ) != null ) {
-				String persister = ( hibernateEntityAnnotation.value( "persister" ).toString() );
-				ClassLoaderService classLoaderService = meta.getServiceRegistry()
-						.getService( ClassLoaderService.class );
-				Class<?> persisterClass = classLoaderService.classForName( persister );
-				entityBindingState.setPersisterClass( persisterClass );
+				final String persisterClassName = ( hibernateEntityAnnotation.value( "persister" ).toString() );
+				entityBindingState.setPersisterClass( meta.<EntityPersister>locateClassByName( persisterClassName ) );
 			}
 		}
 
 		// also check for the immutable annotation
 		AnnotationInstance immutableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		if ( immutableAnnotation != null ) {
 			mutable = false;
 		}
 
 		entityBindingState.setMutable( mutable );
 		entityBindingState.setDynamicInsert( dynamicInsert );
 		entityBindingState.setDynamicUpdate( dynamicUpdate );
 		entityBindingState.setSelectBeforeUpdate( selectBeforeUpdate );
 		entityBindingState.setExplicitPolymorphism( PolymorphismType.EXPLICIT.equals( polymorphism ) );
 		entityBindingState.setOptimisticLock( optimisticLock );
 	}
 
 	private Hierarchical getSuperType() {
 		ConfiguredClass parent = configuredClass.getParent();
 		if ( parent == null ) {
 			return null;
 		}
 
 		EntityBinding parentBinding = meta.getEntityBinding( parent.getName() );
 		if ( parentBinding == null ) {
 			throw new AssertionFailure(
 					"Parent entity " + parent.getName() + " of entity " + configuredClass.getName() + " not yet created!"
 			);
 		}
 
 		return parentBinding.getEntity();
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
index 7724e3022c..075eadd5db 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
@@ -1,261 +1,296 @@
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
 package org.hibernate.metamodel.source.annotations.entity.state.binding;
 
 import java.util.HashSet;
 import java.util.Set;
 
+import org.hibernate.EntityMode;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.EntityBindingState;
+import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClass;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Hardy Ferentschik
  */
 public class EntityBindingStateImpl implements EntityBindingState {
+	private String entityName;
+
+	private final String className;
+	private String proxyInterfaceName;
+
+	private final Hierarchical superType;
 	private final boolean isRoot;
 	private final InheritanceType inheritanceType;
 
-	private String entityName;
 
 	private Caching caching;
 
 	private boolean mutable;
 	private boolean explicitPolymorphism;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private OptimisticLockType optimisticLock;
 
-	private Class<?> persisterClass;
+	private Class<EntityPersister> persisterClass;
 
 	private boolean lazy;
-	private String proxyInterfaceName;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames;
 
-	public EntityBindingStateImpl(ConfiguredClass configuredClass) {
+	public EntityBindingStateImpl(Hierarchical superType, ConfiguredClass configuredClass) {
+		this.className = configuredClass.getName();
+		this.superType = superType;
 		this.isRoot = configuredClass.isRoot();
 		this.inheritanceType = configuredClass.getInheritanceType();
 		this.synchronizedTableNames = new HashSet<String>();
 		this.batchSize = -1;
 	}
 
-	public void setEntityName(String entityName) {
+	@Override
+	public String getJpaEntityName() {
+		return entityName;
+	}
+
+	public void setJpaEntityName(String entityName) {
 		this.entityName = entityName;
 	}
 
+	@Override
+	public EntityMode getEntityMode() {
+		return EntityMode.POJO;
+	}
+
 	public String getEntityName() {
-		return entityName;
+		return className;
+	}
+
+	@Override
+	public String getClassName() {
+		return className;
+	}
+
+	@Override
+	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
+		return null; // todo : implement method body
+	}
+
+	@Override
+	public Hierarchical getSuperType() {
+		return superType;
 	}
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public void setOptimisticLock(OptimisticLockType optimisticLock) {
 		this.optimisticLock = optimisticLock;
 	}
 
-	public void setPersisterClass(Class<?> persisterClass) {
+	public void setPersisterClass(Class<EntityPersister> persisterClass) {
 		this.persisterClass = persisterClass;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public void addSynchronizedTableName(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 
 	@Override
 	public boolean isRoot() {
 		return isRoot;
 
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return inheritanceType;
 	}
 
 	@Override
 	public Caching getCaching() {
 		return caching;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		// not needed for annotations!? (HF)
 		return null;
 	}
 
 	@Override
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	@Override
 	public String getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	@Override
 	public int getOptimisticLockMode() {
 		return optimisticLock.ordinal();
 	}
 
 	@Override
-	public Class getEntityPersisterClass() {
+	public Class getCustomEntityPersisterClass() {
 		return persisterClass;
 	}
 
 	@Override
 	public Boolean isAbstract() {
 		// no annotations equivalent
 		return false;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
index a05b55c9b8..34d4802175 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
@@ -1,572 +1,539 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BagBinding;
 import org.hibernate.metamodel.binding.CollectionElementType;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.domain.Entity;
+import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
+import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
+import org.hibernate.metamodel.relational.state.TupleRelationalState;
+import org.hibernate.metamodel.relational.state.ValueRelationalState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmEntityBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmManyToOneAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmPluralAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmManyToOneRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLAnyElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLComponentElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLDynamicComponentElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFilterElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLIdbagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLListElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMapElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertiesElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLResultsetElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSetElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
-import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.relational.state.TupleRelationalState;
-import org.hibernate.metamodel.relational.state.ValueRelationalState;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 abstract class AbstractEntityBinder {
 	private final HbmBindingContext bindingContext;
 	private final Schema.Name schemaName;
 
 	AbstractEntityBinder(HbmBindingContext bindingContext, XMLHibernateMapping.XMLClass entityClazz) {
 		this.bindingContext = bindingContext;
 		this.schemaName = new Schema.Name(
 				entityClazz.getSchema() == null
 						? bindingContext.getMappingDefaults().getSchemaName()
 						: entityClazz.getSchema(),
 				entityClazz.getCatalog() == null
 						? bindingContext.getMappingDefaults().getCatalogName() :
 						entityClazz.getCatalog()
 		);
 	}
 
 	public boolean isRoot() {
 		return false;
 	}
 
 	public abstract InheritanceType getInheritanceType();
 
 	public HbmBindingContext getBindingContext() {
 		return bindingContext;
 	}
 
 	protected MetadataImplementor getMetadata() {
 		return bindingContext.getMetadataImplementor();
 	}
 
 	protected Schema.Name getSchemaName() {
 		return schemaName;
 	}
 
 	protected NamingStrategy getNamingStrategy() {
 		return getMetadata().getOptions().getNamingStrategy();
 	}
 
 	protected void basicEntityBinding(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			Hierarchical superType) {
-		entityBinding.setEntity( new Entity( bindingContext.extractEntityName( entityClazz ), superType ) );
-		entityBinding.initialize( new HbmEntityBindingState( isRoot(), getInheritanceType(), bindingContext, entityClazz ) );
-
-		// TODO: move this stuff out
-		// transfer an explicitly defined lazy attribute
-		bindPojoRepresentation( entityClazz, entityBinding );
-		bindMapRepresentation( entityClazz, entityBinding );
+		entityBinding.initialize(
+				bindingContext,
+				new HbmEntityBindingState(
+						superType,
+						entityClazz,
+						isRoot(),
+						getInheritanceType(),
+						bindingContext
+				)
+		);
 
 		final String entityName = entityBinding.getEntity().getName();
 
 		if ( entityClazz.getFetchProfile() != null ) {
 			bindingContext.bindFetchProfiles( entityClazz.getFetchProfile(), entityName );
 		}
 
 		getMetadata().addImport( entityName, entityName );
 		if ( bindingContext.isAutoImport() ) {
 			if ( entityName.indexOf( '.' ) > 0 ) {
 				getMetadata().addImport( StringHelper.unqualify( entityName ), entityName );
 			}
 		}
 	}
 
 	protected String getDefaultAccess() {
 		return bindingContext.getMappingDefaults().getPropertyAccessorName();
 	}
 
-	private void bindPojoRepresentation(XMLHibernateMapping.XMLClass entityClazz,
-										EntityBinding entityBinding) {
-		String className = bindingContext.getClassName( entityClazz.getName() );
-		String proxyName = entityBinding.getProxyInterfaceName();
-
-		final ClassLoaderService classLoaderService =
-				bindingContext.getServiceRegistry().getService( ClassLoaderService.class );
-
-		entityBinding.getEntity().getPojoEntitySpecifics().setClassName( className, classLoaderService );
-
-		if ( proxyName != null ) {
-			entityBinding.getEntity().getPojoEntitySpecifics().setProxyInterfaceName( proxyName, classLoaderService );
-			entityBinding.setLazy( true );
-		}
-		else if ( entityBinding.isLazy() ) {
-			entityBinding.getEntity().getPojoEntitySpecifics().setProxyInterfaceName( className, classLoaderService );
-		}
-
-		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.POJO );
-		if ( tuplizer != null ) {
-			entityBinding.getEntity().getPojoEntitySpecifics().setTuplizerClassName( tuplizer.getClazz(), classLoaderService );
-		}
-	}
-
-	private void bindMapRepresentation(XMLHibernateMapping.XMLClass entityClazz,
-									   EntityBinding entityBinding) {
-		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.MAP );
-		final ClassLoaderService classLoaderService =
-				bindingContext.getServiceRegistry().getService( ClassLoaderService.class );
-		if ( tuplizer != null ) {
-			entityBinding.getEntity().getMapEntitySpecifics().setTuplizerClassName( tuplizer.getClazz(), classLoaderService );
-		}
-	}
-
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 *
 	 * @return The tuplizer element, or null.
 	 */
 	private static XMLTuplizerElement locateTuplizerDefinition(XMLHibernateMapping.XMLClass container,
 															   EntityMode entityMode) {
 		for ( XMLTuplizerElement tuplizer : container.getTuplizer() ) {
 			if ( entityMode.toString().equals( tuplizer.getEntityMode() ) ) {
 				return tuplizer;
 			}
 		}
 		return null;
 	}
 
 	protected String getClassTableName(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			Table denormalizedSuperTable) {
 		final String entityName = entityBinding.getEntity().getName();
 		String logicalTableName;
 		String physicalTableName;
 		if ( entityClazz.getTable() == null ) {
 			logicalTableName = StringHelper.unqualify( entityName );
 			physicalTableName = getMetadata()
 					.getOptions()
 					.getNamingStrategy()
 					.classToTableName( entityName );
 		}
 		else {
 			logicalTableName = entityClazz.getTable();
 			physicalTableName = getMetadata()
 					.getOptions()
 					.getNamingStrategy()
 					.tableName( logicalTableName );
 		}
 // todo : find out the purpose of these logical bindings
 //			mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	protected void buildAttributeBindings(XMLHibernateMapping.XMLClass entityClazz,
 										  EntityBinding entityBinding) {
 		// null = UniqueKey (we are not binding a natural-id mapping)
 		// true = mutable, by default properties are mutable
 		// true = nullable, by default properties are nullable.
 		buildAttributeBindings( entityClazz, entityBinding, null, true, true );
 	}
 
 	/**
 	 * This form is essentially used to create natural-id mappings.  But the processing is the same, aside from these
 	 * extra parameterized values, so we encapsulate it here.
 	 *
 	 * @param entityClazz
 	 * @param entityBinding
 	 * @param uniqueKey
 	 * @param mutable
 	 * @param nullable
 	 */
 	protected void buildAttributeBindings(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			UniqueKey uniqueKey,
 			boolean mutable,
 			boolean nullable) {
 		final boolean naturalId = uniqueKey != null;
 
 		final String entiytName = entityBinding.getEntity().getName();
 		final TableSpecification tabe = entityBinding.getBaseTable();
 
 		AttributeBinding attributeBinding = null;
 		for ( Object attribute : entityClazz.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLBagElement.class.isInstance( attribute ) ) {
 				XMLBagElement collection = XMLBagElement.class.cast( attribute );
 				BagBinding collectionBinding = makeBagAttributeBinding( collection, entityBinding );
 				bindingContext.getMetadataImplementor().addCollection( collectionBinding );
 				attributeBinding = collectionBinding;
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
 			else if ( XMLManyToOneElement.class.isInstance( attribute ) ) {
 				XMLManyToOneElement manyToOne = XMLManyToOneElement.class.cast( attribute );
 				attributeBinding =  makeManyToOneAttributeBinding( manyToOne, entityBinding );
 			}
 			else if ( XMLAnyElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				value = new Any( mappings, table );
 //				bindAny( subElement, (Any) value, nullable, mappings );
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				value = new OneToOne( mappings, table, persistentClass );
 //				bindOneToOne( subElement, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( XMLPropertyElement.class.isInstance( attribute ) ) {
 				XMLPropertyElement property = XMLPropertyElement.class.cast( attribute );
 				attributeBinding = bindProperty( property, entityBinding );
 			}
 			else if ( XMLComponentElement.class.isInstance( attribute )
 					|| XMLDynamicComponentElement.class.isInstance( attribute )
 					|| XMLPropertiesElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				String subpath = StringHelper.qualify( entityName, propertyName );
 //				value = new Component( mappings, persistentClass );
 //
 //				bindComponent(
 //						subElement,
 //						(Component) value,
 //						persistentClass.getClassName(),
 //						propertyName,
 //						subpath,
 //						true,
 //						"properties".equals( subElementName ),
 //						mappings,
 //						inheritedMetas,
 //						false
 //					);
 			}
 		}
 
 		/*
 Array
 PrimitiveArray
 */
 		for ( XMLJoinElement join : entityClazz.getJoin() ) {
 // todo : implement
 //			Join join = new Join();
 //			join.setPersistentClass( persistentClass );
 //			bindJoin( subElement, join, mappings, inheritedMetas );
 //			persistentClass.addJoin( join );
 		}
 		for ( XMLSubclassElement subclass : entityClazz.getSubclass() ) {
 // todo : implement
 //			handleSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLJoinedSubclassElement subclass : entityClazz.getJoinedSubclass() ) {
 // todo : implement
 //			handleJoinedSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLUnionSubclassElement subclass : entityClazz.getUnionSubclass() ) {
 // todo : implement
 //			handleUnionSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLFilterElement filter : entityClazz.getFilter() ) {
 // todo : implement
 //				parseFilter( subElement, entityBinding );
 		}
 		if ( entityClazz.getNaturalId() != null ) {
 // todo : implement
 //				UniqueKey uk = new UniqueKey();
 //				uk.setName("_UniqueKey");
 //				uk.setTable(table);
 //				//by default, natural-ids are "immutable" (constant)
 //				boolean mutableId = "true".equals( subElement.attributeValue("mutable") );
 //				createClassProperties(
 //						subElement,
 //						persistentClass,
 //						mappings,
 //						inheritedMetas,
 //						uk,
 //						mutableId,
 //						false,
 //						true
 //					);
 //				table.addUniqueKey(uk);
 		}
 		if ( entityClazz.getQueryOrSqlQuery() != null ) {
 			for ( Object queryOrSqlQuery : entityClazz.getQueryOrSqlQuery() ) {
 				if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 // todo : implement
 //				bindNamedQuery(subElement, persistentClass.getEntityName(), mappings);
 				}
 				else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 // todo : implement
 //			bindNamedSQLQuery(subElement, persistentClass.getEntityName(), mappings);
 				}
 			}
 		}
 		if ( entityClazz.getResultset() != null ) {
 			for ( XMLResultsetElement resultSet : entityClazz.getResultset() ) {
 // todo : implement
 //				bindResultSetMappingDefinition( subElement, persistentClass.getEntityName(), mappings );
 			}
 		}
 //			if ( value != null ) {
 //				Property property = createProperty( value, propertyName, persistentClass
 //					.getClassName(), subElement, mappings, inheritedMetas );
 //				if ( !mutable ) property.setUpdateable(false);
 //				if ( naturalId ) property.setNaturalIdentifier(true);
 //				persistentClass.addProperty( property );
 //				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
 //			}
 
 	}
 
 	protected SimpleAttributeBinding bindProperty(
 			XMLPropertyElement property,
 			EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
-				entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+				entityBinding.getEntity().getJavaType().getName(),
 				bindingContext,
 				entityBinding.getMetaAttributeContext(),
 				property
 		);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								bindingContext,
 								true,
 								property
 						)
 				);
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		return entityBinding.makeSimpleAttributeBinding( bindingState.getAttributeName() )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	protected static ValueRelationalState convertToSimpleValueRelationalStateIfPossible(ValueRelationalState state) {
 	// TODO: should a single-valued tuple always be converted???
 		if ( !TupleRelationalState.class.isInstance( state ) ) {
 			return state;
 		}
 		TupleRelationalState tupleRelationalState = TupleRelationalState.class.cast( state );
 		return tupleRelationalState.getRelationalStates().size() == 1 ?
 				tupleRelationalState.getRelationalStates().get( 0 ) :
 				state;
 	}
 
 	protected BagBinding makeBagAttributeBinding(
 			XMLBagElement collection,
 			EntityBinding entityBinding) {
 
 		PluralAttributeBindingState bindingState =
 				new HbmPluralAttributeBindingState(
-						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+						entityBinding.getEntity().getJavaType().getName(),
 						bindingContext,
 						entityBinding.getMetaAttributeContext(),
 						collection
 				);
 
 		BagBinding collectionBinding = entityBinding.makeBagAttributeBinding(
 				bindingState.getAttributeName(),
 				getCollectionElementType( collection ) )
 				.initialize( bindingState );
 
 			// todo : relational model binding
 		return collectionBinding;
 	}
 
 	private CollectionElementType getCollectionElementType(XMLBagElement collection) {
 		if ( collection.getElement() != null ) {
 			return CollectionElementType.BASIC;
 		}
 		else if ( collection.getCompositeElement() != null ) {
 			return CollectionElementType.COMPOSITE;
 		}
 		else if ( collection.getManyToMany() != null ) {
 			return CollectionElementType.MANY_TO_MANY;
 		}
 		else if ( collection.getOneToMany() != null ) {
 			return CollectionElementType.ONE_TO_MANY;
 		}
 		else if ( collection.getManyToAny() != null ) {
 			return CollectionElementType.MANY_TO_ANY;
 		}
 		else {
 			throw new AssertionFailure( "Unknown collection element type: " + collection );
 		}
 	}
 
 	private ManyToOneAttributeBinding makeManyToOneAttributeBinding(XMLManyToOneElement manyToOne,
 							   EntityBinding entityBinding) {
 		ManyToOneAttributeBindingState bindingState =
 				new HbmManyToOneAttributeBindingState(
-						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+						entityBinding.getEntity().getJavaType().getName(),
 						bindingContext,
 						entityBinding.getMetaAttributeContext(),
 						manyToOne
 				);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ManyToOneRelationalState relationalState =
 						new HbmManyToOneRelationalStateContainer(
 								bindingContext,
 								true,
 								manyToOne
 						);
 
 	    entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		ManyToOneAttributeBinding manyToOneAttributeBinding =
 				entityBinding.makeManyToOneAttributeBinding( bindingState.getAttributeName() )
 						.initialize( bindingState )
 						.initialize( relationalState );
 
 		return manyToOneAttributeBinding;
 	}
 
 //	private static Property createProperty(
 //			final Value value,
 //	        final String propertyName,
 //			final String className,
 //	        final Element subnode,
 //	        final Mappings mappings,
 //			java.util.Map inheritedMetas) throws MappingException {
 //
 //		if ( StringHelper.isEmpty( propertyName ) ) {
 //			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 //		}
 //
 //		value.setTypeUsingReflection( className, propertyName );
 //
 //		// this is done here 'cos we might only know the type here (ugly!)
 //		// TODO: improve this a lot:
 //		if ( value instanceof ToOne ) {
 //			ToOne toOne = (ToOne) value;
 //			String propertyRef = toOne.getReferencedPropertyName();
 //			if ( propertyRef != null ) {
 //				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 //			}
 //		}
 //		else if ( value instanceof Collection ) {
 //			Collection coll = (Collection) value;
 //			String propertyRef = coll.getReferencedPropertyName();
 //			// not necessarily a *unique* property reference
 //			if ( propertyRef != null ) {
 //				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 //			}
 //		}
 //
 //		value.createForeignKey();
 //		Property prop = new Property();
 //		prop.setValue( value );
 //		bindProperty( subnode, prop, mappings, inheritedMetas );
 //		return prop;
 //	}
 
 
 //	protected HbmRelationalState processValues(Element identifierElement, TableSpecification baseTable, String propertyPath, boolean isSimplePrimaryKey) {
 	// first boolean (false here) indicates that by default columns are nullable
 	// second boolean (true here) indicates that by default column names should be guessed
 // todo : logical 1-1 handling
 //			final Attribute uniqueAttribute = node.attribute( "unique" );
 //			if ( uniqueAttribute != null
 //					&& "true".equals( uniqueAttribute.getValue() )
 //					&& ManyToOne.class.isInstance( simpleValue ) ) {
 //				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 //			}
 	//return processValues( identifierElement, baseTable, false, true, propertyPath, isSimplePrimaryKey );
 
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index c3a18ac1b1..55bea122cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,369 +1,380 @@
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
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
+import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
  * {@link org.hibernate.metamodel.source.spi.Binder}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor implements HbmBindingContext {
 	private final MetadataImplementor metadata;
 	private final JaxbRoot<XMLHibernateMapping> jaxbRoot;
 
 	private final XMLHibernateMapping hibernateMapping;
 
 	private final MappingDefaults mappingDefaults;
 	private final MetaAttributeContext metaAttributeContext;
 
 	private final boolean autoImport;
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, JaxbRoot<XMLHibernateMapping> jaxbRoot) {
 		this.metadata = metadata;
 		this.jaxbRoot = jaxbRoot;
 
 		this.hibernateMapping = jaxbRoot.getRoot();
 		this.mappingDefaults = new OverriddenMappingDefaults(
 				metadata.getMappingDefaults(),
 				hibernateMapping.getPackage(),
 				hibernateMapping.getSchema(),
 				hibernateMapping.getCatalog(),
 				null,
 				null,
 				hibernateMapping.getDefaultCascade(),
 				hibernateMapping.getDefaultAccess(),
 				hibernateMapping.isDefaultLazy()
 		);
 
 		autoImport = hibernateMapping.isAutoImport();
 
 		metaAttributeContext = extractMetaAttributes();
 	}
 
 	private MetaAttributeContext extractMetaAttributes() {
 		return hibernateMapping.getMeta() == null
 				? new MetaAttributeContext( metadata.getMetaAttributeContext() )
 				: HbmHelper.extractMetaAttributeContext( hibernateMapping.getMeta(), true, metadata.getMetaAttributeContext() );
 	}
 
 	@Override
 	public boolean isAutoImport() {
 		return autoImport;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return jaxbRoot.getOrigin();
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return metadata.getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return metadata.getOptions().getNamingStrategy();
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return metadata;
 	}
 
+	@Override
+	public <T> Class<T> locateClassByName(String name) {
+		return metadata.locateClassByName( name );
+	}
+
+	@Override
+	public JavaType makeJavaType(String className) {
+		return metadata.makeJavaType( className );
+	}
+
 	public void bindIndependentMetadata() {
 		bindDatabaseObjectDefinitions();
 		bindTypeDefinitions();
 	}
 
 	private void bindDatabaseObjectDefinitions() {
 		if ( hibernateMapping.getDatabaseObject() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : hibernateMapping.getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classLoaderService().classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 			}
 			else {
 				Set<String> dialectScopes = new HashSet<String>();
 				if ( databaseObjectElement.getDialectScope() != null ) {
 					for ( XMLHibernateMapping.XMLDatabaseObject.XMLDialectScope dialectScope : databaseObjectElement.getDialectScope() ) {
 						dialectScopes.add( dialectScope.getName() );
 					}
 				}
 				auxiliaryDatabaseObject = new BasicAuxiliaryDatabaseObjectImpl(
 						databaseObjectElement.getCreate(),
 						databaseObjectElement.getDrop(),
 						dialectScopes
 				);
 			}
 			metadata.addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 	}
 
 	private void bindTypeDefinitions() {
 		if ( hibernateMapping.getTypedef() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLTypedef typedef : hibernateMapping.getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition( new TypeDef( typedef.getName(), typedef.getClazz(), parameters ) );
 		}
 	}
 
 	public void bindTypeDependentMetadata() {
 		bindFilterDefinitions();
 		bindIdentifierGenerators();
 	}
 
 	private void bindFilterDefinitions() {
 		if(hibernateMapping.getFilterDef() == null){
 			return;
 		}
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : hibernateMapping.getFilterDef() ) {
 			final String name = filterDefinition.getName();
 			final Map<String,Type> parameters = new HashMap<String, Type>();
 			String condition = null;
 			for ( Object o : filterDefinition.getContent() ) {
 				if ( o instanceof String ) {
 					// represents the condition
 					if ( condition != null ) {
 						// log?
 					}
 					condition = (String) o;
 				}
 				else if ( o instanceof XMLHibernateMapping.XMLFilterDef.XMLFilterParam ) {
 					final XMLHibernateMapping.XMLFilterDef.XMLFilterParam paramElement = (XMLHibernateMapping.XMLFilterDef.XMLFilterParam) o;
 					// todo : should really delay this resolution until later to allow typedef names
 					parameters.put(
 							paramElement.getName(),
 							metadata.getTypeResolver().heuristicType( paramElement.getType() )
 					);
 				}
 				else {
 					throw new MappingException( "Unrecognized nested filter content", jaxbRoot.getOrigin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void bindIdentifierGenerators() {
 		if ( hibernateMapping.getIdentifierGenerator() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : hibernateMapping.getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void bindMappingMetadata(List<String> processedEntityNames) {
 		if ( hibernateMapping.getClazzOrSubclassOrJoinedSubclass() == null ) {
 			return;
 		}
 		for ( Object clazzOrSubclass : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( clazzOrSubclass ) ) {
 				XMLHibernateMapping.XMLClass clazz =
 						XMLHibernateMapping.XMLClass.class.cast( clazzOrSubclass );
 				new RootEntityBinder( this, clazz ).process( clazz );
 			}
 			else if ( XMLSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLJoinedSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLUnionSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"unknown type of class or subclass: " +
 								clazzOrSubclass.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	public void bindMappingDependentMetadata() {
 		bindFetchProfiles();
 		bindImports();
 		bindResultSetMappings();
 		bindNamedQueries();
 	}
 
 	private void bindFetchProfiles(){
 		if(hibernateMapping.getFetchProfile() == null){
 			return;
 		}
 		bindFetchProfiles( hibernateMapping.getFetchProfile(),null );
 	}
 
 	public void bindFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void bindImports() {
 		if ( hibernateMapping.getImport() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLImport importValue : hibernateMapping.getImport() ) {
 			String className = getClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void bindResultSetMappings() {
 		if ( hibernateMapping.getResultset() == null ) {
 			return;
 		}
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void bindNamedQueries() {
 		if ( hibernateMapping.getQueryOrSqlQuery() == null ) {
 			return;
 		}
 		for ( Object queryOrSqlQuery : hibernateMapping.getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	private ClassLoaderService classLoaderService;
 
 	private ClassLoaderService classLoaderService() {
 		if ( classLoaderService == null ) {
 			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz) {
 		return HbmHelper.extractEntityName( entityClazz, mappingDefaults.getPackageName() );
 	}
 
 	@Override
 	public String getClassName(String unqualifiedName) {
 		return HbmHelper.getClassName( unqualifiedName, mappingDefaults.getPackageName() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
index c61710da96..9c79a46825 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
@@ -1,329 +1,329 @@
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
 
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.InLineView;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmDiscriminatorBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLCompositeId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 class RootEntityBinder extends AbstractEntityBinder {
 
 	RootEntityBinder(HbmBindingContext bindingContext, XMLClass xmlClazz) {
 		super( bindingContext, xmlClazz );
 	}
 
 	public boolean isRoot() {
 		return true;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return InheritanceType.SINGLE_TABLE;
 	}
 
 	public void process(XMLClass xmlClazz) {
 		String entityName = getBindingContext().extractEntityName( xmlClazz );
 		if ( entityName == null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 
 		EntityBinding entityBinding = new EntityBinding();
 		basicEntityBinding( xmlClazz, entityBinding, null );
 		basicTableBinding( xmlClazz, entityBinding );
 
 		bindIdentifier( xmlClazz, entityBinding );
 		bindDiscriminator( xmlClazz, entityBinding );
 		bindVersionOrTimestamp( xmlClazz, entityBinding );
 
 		// called createClassProperties in HBMBinder...
 		buildAttributeBindings( xmlClazz, entityBinding );
 
 		getMetadata().addEntity( entityBinding );
 	}
 
 	private void basicTableBinding(XMLClass xmlClazz,
 								   EntityBinding entityBinding) {
 		final Schema schema = getMetadata().getDatabase().getSchema( getSchemaName() );
 
 		final String subSelect =
 				xmlClazz.getSubselectAttribute() == null ? xmlClazz.getSubselect() : xmlClazz.getSubselectAttribute();
 		if ( subSelect != null ) {
 			final String logicalName = entityBinding.getEntity().getName();
 			InLineView inLineView = schema.getInLineView( logicalName );
 			if ( inLineView == null ) {
 				inLineView = schema.createInLineView( logicalName, subSelect );
 			}
 			entityBinding.setBaseTable( inLineView );
 		}
 		else {
 			final Identifier tableName = Identifier.toIdentifier( getClassTableName( xmlClazz, entityBinding, null ) );
 			org.hibernate.metamodel.relational.Table table = schema.getTable( tableName );
 			if ( table == null ) {
 				table = schema.createTable( tableName );
 			}
 			entityBinding.setBaseTable( table );
 			String comment = xmlClazz.getComment();
 			if ( comment != null ) {
 				table.addComment( comment.trim() );
 			}
 			String check = xmlClazz.getCheck();
 			if ( check != null ) {
 				table.addCheckConstraint( check );
 			}
 		}
 	}
 
 	private void bindIdentifier(XMLClass xmlClazz,
 								EntityBinding entityBinding) {
 		if ( xmlClazz.getId() != null ) {
 			bindSimpleId( xmlClazz.getId(), entityBinding );
 			return;
 		}
 
 		if ( xmlClazz.getCompositeId() != null ) {
 			bindCompositeId( xmlClazz.getCompositeId(), entityBinding );
 		}
 
 		throw new InvalidMappingException(
 				"Entity [" + entityBinding.getEntity().getName() + "] did not contain identifier mapping",
 				getBindingContext().getOrigin()
 		);
 	}
 
 	private void bindSimpleId(XMLId id, EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
-				entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+				entityBinding.getEntity().getJavaType().getName(),
 				getBindingContext(),
 				entityBinding.getMetaAttributeContext(),
 				id
 		);
 		// boolean (true here) indicates that by default column names should be guessed
 		HbmSimpleValueRelationalStateContainer relationalStateContainer = new HbmSimpleValueRelationalStateContainer(
 				getBindingContext(), true, id
 		);
 		if ( relationalStateContainer.getRelationalStates().size() > 1 ) {
 			throw new MappingException( "ID is expected to be a single column, but has more than 1 value" );
 		}
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeSimpleIdAttributeBinding( bindingState.getAttributeName() )
 				.initialize( bindingState )
 				.initialize( relationalStateContainer.getRelationalStates().get( 0 ) );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 //		if ( propertyName == null ) {
 //			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 //		}
 //		else {
 //			bindSimpleValue( idNode, id, false, propertyName, mappings );
 //		}
 //
 //		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 //			if ( !id.isTypeSpecified() ) {
 //				throw new MappingException( "must specify an identifier type: "
 //					+ entity.getEntityName() );
 //			}
 //		}
 //		else {
 //			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 //		}
 //
 //		if ( propertyName != null ) {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 //		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(XMLCompositeId compositeId, EntityBinding entityBinding) {
 		final String explicitName = compositeId.getName();
 
 //		String propertyName = idNode.attributeValue( "name" );
 //		Component id = new Component( mappings, entity );
 //		entity.setIdentifier( id );
 //		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 //		if ( propertyName == null ) {
 //			entity.setEmbeddedIdentifier( id.isEmbedded() );
 //			if ( id.isEmbedded() ) {
 //				// todo : what is the implication of this?
 //				id.setDynamic( !entity.hasPojoRepresentation() );
 //				/*
 //				 * Property prop = new Property(); prop.setName("id");
 //				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 //				 * entity.setIdentifierProperty(prop);
 //				 */
 //			}
 //		}
 //		else {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 //
 //		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private void bindDiscriminator(XMLClass xmlEntityClazz,
 								   EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getDiscriminator() == null ) {
 			return;
 		}
 
 		DiscriminatorBindingState bindingState = new HbmDiscriminatorBindingState(
-						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+						entityBinding.getEntity().getJavaType().getName(),
 						entityBinding.getEntity().getName(),
 						getBindingContext(),
 						xmlEntityClazz
 		);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState = convertToSimpleValueRelationalStateIfPossible(
 				new HbmSimpleValueRelationalStateContainer(
 						getBindingContext(),
 						true,
 						xmlEntityClazz.getDiscriminator()
 				)
 		);
 
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeEntityDiscriminator( bindingState.getAttributeName() )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	private void bindVersionOrTimestamp(XMLClass xmlEntityClazz,
 										EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getVersion() != null ) {
 			bindVersion(
 					xmlEntityClazz.getVersion(),
 					entityBinding
 			);
 		}
 		else if ( xmlEntityClazz.getTimestamp() != null ) {
 			bindTimestamp(
 					xmlEntityClazz.getTimestamp(),
 					entityBinding
 			);
 		}
 	}
 
 	protected void bindVersion(XMLHibernateMapping.XMLClass.XMLVersion version,
 							   EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
-						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						version
 				);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								version
 						)
 				);
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeVersionBinding( bindingState.getAttributeName() )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	protected void bindTimestamp(XMLHibernateMapping.XMLClass.XMLTimestamp timestamp,
 								 EntityBinding entityBinding) {
 
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
-						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
+						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						timestamp
 				);
 
 		// relational model has not been bound yet
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								timestamp
 						)
 				);
 
 		entityBinding.makeVersionBinding( bindingState.getAttributeName() )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
index 5b8cf038bf..cbbf21ac34 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
@@ -1,154 +1,154 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.AttributeBindingState;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractHbmAttributeBindingState implements AttributeBindingState {
 	private final String ownerClassName;
 	private final String attributeName;
 	private final BindingContext bindingContext;
 	private final String nodeName;
 	private final String accessorName;
 	private final boolean isOptimisticLockable;
 	private final MetaAttributeContext metaAttributeContext;
 
 	public AbstractHbmAttributeBindingState(
 			String ownerClassName,
 			String attributeName,
 			BindingContext bindingContext,
 			String nodeName,
 			MetaAttributeContext metaAttributeContext,
 			String accessorName,
 			boolean isOptimisticLockable) {
 		if ( attributeName == null ) {
 			throw new MappingException(
 					"Attribute name cannot be null."
 			);
 		}
 
 		this.ownerClassName = ownerClassName;
 		this.attributeName = attributeName;
 		this.bindingContext = bindingContext;
 		this.nodeName = nodeName;
 		this.metaAttributeContext = metaAttributeContext;
 		this.accessorName = accessorName;
 		this.isOptimisticLockable = isOptimisticLockable;
 	}
 
 	// TODO: really don't like this here...
 	protected String getOwnerClassName() {
 		return ownerClassName;
 	}
 
 	protected Set<CascadeType> determineCascadeTypes(String cascade) {
 		String commaSeparatedCascades = MappingHelper.getStringValue( cascade, getBindingContext().getMappingDefaults().getCascadeStyle() );
 		Set<String> cascades = MappingHelper.getStringValueTokens( commaSeparatedCascades, "," );
 		Set<CascadeType> cascadeTypes = new HashSet<CascadeType>( cascades.size() );
 		for ( String s : cascades ) {
 			CascadeType cascadeType = CascadeType.getCascadeType( s );
 			if ( cascadeType == null ) {
 				throw new MappingException( "Invalid cascading option " + s );
 			}
 			cascadeTypes.add( cascadeType );
 		}
 		return cascadeTypes;
 	}
 
 	protected final String getTypeNameByReflection() {
-		Class ownerClass = MappingHelper.classForName( ownerClassName, bindingContext.getServiceRegistry() );
+		Class ownerClass = bindingContext.locateClassByName( ownerClassName );
 		return ReflectHelper.reflectedPropertyClass( ownerClass, attributeName ).getName();
 	}
 
 	public String getAttributeName() {
 		return attributeName;
 	}
 
 	public BindingContext getBindingContext() {
 		return bindingContext;
 	}
 
 	@Deprecated
 	protected final MappingDefaults getDefaults() {
 		return getBindingContext().getMappingDefaults();
 	}
 
 	@Override
 	public final String getPropertyAccessorName() {
 		return accessorName;
 	}
 
 	@Override
 	public final boolean isAlternateUniqueKey() {
 		//TODO: implement
 		return false;
 	}
 
 	@Override
 	public final boolean isOptimisticLockable() {
 		return isOptimisticLockable;
 	}
 
 	@Override
 	public final String getNodeName() {
 		return nodeName == null ? getAttributeName() : nodeName;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public PropertyGeneration getPropertyGeneration() {
 		return PropertyGeneration.NEVER;
 	}
 
 	public boolean isKeyCascadeDeleteEnabled() {
 		return false;
 	}
 
 	public String getUnsavedValue() {
 		//TODO: implement
 		return null;
 	}
 
 	public Map<String, String> getTypeParameters() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
index f532b591a7..fdd783d017 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
@@ -1,301 +1,377 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.HashSet;
 import java.util.Set;
 
+import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.EntityBindingState;
+import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.hbm.HbmBindingContext;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Gail Badner
  */
 public class HbmEntityBindingState implements EntityBindingState {
+	private final String entityName;
+	private final EntityMode entityMode;
+
+	private final String className;
+	private final String proxyInterfaceName;
+
+	private final Class<EntityPersister> entityPersisterClass;
+	private final Class<EntityTuplizer> tuplizerClass;
+
+	private final MetaAttributeContext metaAttributeContext;
+
+	private final Hierarchical superType;
 	private final boolean isRoot;
 	private final InheritanceType entityInheritanceType;
+
 	private final Caching caching;
-	private final MetaAttributeContext metaAttributeContext;
-	private final String proxyInterfaceName;
 
 	private final boolean lazy;
 	private final boolean mutable;
 	private final boolean explicitPolymorphism;
 	private final String whereFilter;
 	private final String rowId;
 
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 
 	private final int batchSize;
 	private final boolean selectBeforeUpdate;
 	private final int optimisticLockMode;
 
-	private final Class entityPersisterClass;
 	private final Boolean isAbstract;
 
 	private final CustomSQL customInsert;
 	private final CustomSQL customUpdate;
 	private final CustomSQL customDelete;
 
 	private final Set<String> synchronizedTableNames;
 
 	public HbmEntityBindingState(
+			Hierarchical superType,
+			XMLHibernateMapping.XMLClass entityClazz,
 			boolean isRoot,
 			InheritanceType inheritanceType,
-			HbmBindingContext bindingContext,
-			XMLHibernateMapping.XMLClass entityClazz) {
+			HbmBindingContext bindingContext) {
+
+		this.superType = superType;
+		this.entityName = bindingContext.extractEntityName( entityClazz );
+
+		final String verbatimClassName = entityClazz.getName();
+		this.entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
+
+		if ( this.entityMode == EntityMode.POJO ) {
+			this.className = bindingContext.getClassName( verbatimClassName );
+			this.proxyInterfaceName = entityClazz.getProxy();
+		}
+		else {
+			this.className = null;
+			this.proxyInterfaceName = null;
+		}
+
+		final String customTuplizerClassName = extractCustomTuplizerClassName( entityClazz, entityMode );
+		tuplizerClass = customTuplizerClassName != null
+				? bindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName )
+				: null;
+
 		this.isRoot = isRoot;
 		this.entityInheritanceType = inheritanceType;
 
 		this.caching = createCaching( entityClazz, bindingContext.extractEntityName( entityClazz ) );
 
 		metaAttributeContext = HbmHelper.extractMetaAttributeContext(
 				entityClazz.getMeta(), true, bindingContext.getMetaAttributeContext()
 		);
 
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		lazy = MappingHelper.getBooleanValue(
 				entityClazz.isLazy(), bindingContext.getMappingDefaults().areAssociationsLazy()
 		);
 		mutable = entityClazz.isMutable();
 
 		explicitPolymorphism = "explicit".equals( entityClazz.getPolymorphism() );
 		whereFilter = entityClazz.getWhere();
 		rowId = entityClazz.getRowid();
-		proxyInterfaceName = entityClazz.getProxy();
 		dynamicUpdate = entityClazz.isDynamicUpdate();
 		dynamicInsert = entityClazz.isDynamicInsert();
 		batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
 		selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
 		optimisticLockMode = getOptimisticLockMode();
 
 		// PERSISTER
-		entityPersisterClass =
-				entityClazz.getPersister() == null ?
-						null :
-						MappingHelper.classForName( entityClazz.getPersister(), bindingContext.getServiceRegistry() );
+		entityPersisterClass = entityClazz.getPersister() == null
+				? null
+				: bindingContext.<EntityPersister>locateClassByName( entityClazz.getPersister() );
 
 		// CUSTOM SQL
 		XMLSqlInsertElement sqlInsert = entityClazz.getSqlInsert();
 		if ( sqlInsert != null ) {
 			customInsert = HbmHelper.getCustomSql(
 					sqlInsert.getValue(),
 					sqlInsert.isCallable(),
 					sqlInsert.getCheck().value()
 			);
 		}
 		else {
 			customInsert = null;
 		}
 
 		XMLSqlDeleteElement sqlDelete = entityClazz.getSqlDelete();
 		if ( sqlDelete != null ) {
 			customDelete = HbmHelper.getCustomSql(
 					sqlDelete.getValue(),
 					sqlDelete.isCallable(),
 					sqlDelete.getCheck().value()
 			);
 		}
 		else {
 			customDelete = null;
 		}
 
 		XMLSqlUpdateElement sqlUpdate = entityClazz.getSqlUpdate();
 		if ( sqlUpdate != null ) {
 			customUpdate = HbmHelper.getCustomSql(
 					sqlUpdate.getValue(),
 					sqlUpdate.isCallable(),
 					sqlUpdate.getCheck().value()
 			);
 		}
 		else {
 			customUpdate = null;
 		}
 
 		if ( entityClazz.getSynchronize() != null ) {
 			synchronizedTableNames = new HashSet<String>( entityClazz.getSynchronize().size() );
 			for ( XMLSynchronizeElement synchronize : entityClazz.getSynchronize() ) {
 				synchronizedTableNames.add( synchronize.getTable() );
 			}
 		}
 		else {
 			synchronizedTableNames = null;
 		}
 		isAbstract = entityClazz.isAbstract();
 	}
 
+	private String extractCustomTuplizerClassName(XMLHibernateMapping.XMLClass entityClazz, EntityMode entityMode) {
+		if ( entityClazz.getTuplizer() == null ) {
+			return null;
+		}
+		for ( XMLTuplizerElement tuplizerElement : entityClazz.getTuplizer() ) {
+			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
+				return tuplizerElement.getClazz();
+			}
+		}
+		return null;
+	}
+
 	private static Caching createCaching(XMLHibernateMapping.XMLClass entityClazz, String entityName) {
 		XMLCacheElement cache = entityClazz.getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : entityName;
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	private static int createOptimisticLockMode(XMLHibernateMapping.XMLClass entityClazz) {
 		String optimisticLockModeString = MappingHelper.getStringValue( entityClazz.getOptimisticLock(), "version" );
 		int optimisticLockMode;
 		if ( "version".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + optimisticLockModeString );
 		}
 		return optimisticLockMode;
 	}
 
 	@Override
+	public String getEntityName() {
+		return entityName;
+	}
+
+	@Override
+	public String getJpaEntityName() {
+		return null;  // no such notion in hbm.xml files
+	}
+
+	@Override
+	public EntityMode getEntityMode() {
+		return entityMode;
+	}
+
+	@Override
+	public String getClassName() {
+		return className;
+	}
+
+	@Override
+	public String getProxyInterfaceName() {
+		return proxyInterfaceName;
+	}
+
+	@Override
+	public Class<EntityPersister> getCustomEntityPersisterClass() {
+		return entityPersisterClass;
+	}
+
+	@Override
+	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
+		return tuplizerClass;
+	}
+
+	@Override
+	public Hierarchical getSuperType() {
+		return superType;
+	}
+
+	@Override
 	public boolean isRoot() {
 		return isRoot;
-
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	@Override
 	public Caching getCaching() {
 		return caching;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
-	public String getProxyInterfaceName() {
-		return proxyInterfaceName;
-	}
-
-	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	@Override
 	public String getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	@Override
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	@Override
-	public Class getEntityPersisterClass() {
-		return entityPersisterClass;
-	}
-
-	@Override
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java
index cc4864b7e6..d884f1f6ac 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java
@@ -1,87 +1,84 @@
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
 package org.hibernate.metamodel.source.hbm.util;
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.MappingException;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * Helper class.
  *
  * @author Gail Badner
  */
 public class MappingHelper {
 	private MappingHelper() {
 	}
 
 	public static String getStringValue(String value, String defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static int getIntValue(String value, int defaultValue) {
 		return value == null ? defaultValue : Integer.parseInt( value );
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
 
 	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		try {
 			return classLoaderService.classForName( className );
 		}
 		catch ( ClassLoadingException e ) {
-			throw new MappingException(
-					"Could not find class: "
-							+ className
-			);
+			throw new MappingException( "Could not find class: " + className );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 8a977c560d..aef42cdee5 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,519 +1,531 @@
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
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
+import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.Type;
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
 	private ClassLoaderService classLoaderService;
 
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
 
 	// todo : keep as part of Database?
 	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
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
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		if ( auxiliaryDatabaseObject == null ) {
 			throw new IllegalArgumentException( "Auxiliary database object is null." );
 		}
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
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
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
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
 			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null || typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
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
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
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
+	@SuppressWarnings( {"unchecked"})
+	public <T> Class<T> locateClassByName(String name) {
+		return classLoaderService().classForName( name );
+	}
+
+	@Override
+	public JavaType makeJavaType(String className) {
+		return new JavaType( className, classLoaderService() );
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
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
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
 	public Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getExplicitType();
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
 	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
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
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
index 9f101e1f78..609052e6b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
@@ -1,42 +1,47 @@
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
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public interface BindingContext {
 	public ServiceRegistry getServiceRegistry();
 
 	public NamingStrategy getNamingStrategy();
 
 	public MappingDefaults getMappingDefaults();
 
 	public MetaAttributeContext getMetaAttributeContext();
 
 	public MetadataImplementor getMetadataImplementor();
+
+	public <T> Class<T> locateClassByName(String name);
+
+	public JavaType makeJavaType(String className);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index 79459607eb..36c1887a79 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,74 +1,75 @@
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
 
 import org.hibernate.metamodel.domain.Entity;
+import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.Table;
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
-		Entity entity = new Entity( "TheEntity", null );
+		Entity entity = new Entity( "TheEntity", null, new JavaType( "NoSuchClass", null ) );
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setRoot( true );
 		entityBinding.setEntity( entity );
 		entityBinding.setBaseTable( table );
 
 		SingularAttribute idAttribute = entity.getOrCreateSingularAttribute( "id" );
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( "id" );
 		attributeBinding.getHibernateTypeDescriptor().setTypeName( "long" );
 		assertSame( idAttribute, attributeBinding.getAttribute() );
 
 		entityBinding.getEntityIdentifier().setValueBinding( attributeBinding );
 
 		Column idColumn = table.getOrCreateColumn( "id" );
 		idColumn.setDatatype( BIGINT );
 		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		//attributeBinding.setValue( idColumn );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/ProxyBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/ProxyBindingTests.java
index 4a769eb881..db087e477d 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/ProxyBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/ProxyBindingTests.java
@@ -1,109 +1,108 @@
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
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
-import org.junit.Test;
-
 import org.hibernate.annotations.Proxy;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
+
+import org.junit.Test;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * Tests for {@code o.h.a.Cache}.
  *
  * @author Hardy Ferentschik
  */
 public class ProxyBindingTests extends BaseAnnotationBindingTestCase {
 	@Test
 	public void testProxyNoAttributes() {
 		buildMetadataSources( ProxiedEntity.class );
 		EntityBinding binding = getEntityBinding( ProxiedEntity.class );
 		assertTrue( "Wrong laziness", binding.isLazy() );
-		assertEquals( "Wrong proxy interface", null, binding.getProxyInterfaceName() );
+		assertEquals( "Wrong proxy interface", ProxiedEntity.class, binding.getProxyInterfaceType().getClassReference() );
 	}
 
 	@Test
 	public void testNoProxy() {
 		buildMetadataSources(NoProxyEntity.class);
 		EntityBinding binding = getEntityBinding( NoProxyEntity.class );
 		assertTrue( "Wrong laziness", binding.isLazy() );
-		assertEquals( "Wrong proxy interface", null, binding.getProxyInterfaceName() );
+		assertEquals( "Wrong proxy interface", NoProxyEntity.class, binding.getProxyInterfaceType().getClassReference() );
 	}
 
 	@Test
 	public void testProxyDisabled() {
 		buildMetadataSources( ProxyDisabledEntity.class );
 		EntityBinding binding = getEntityBinding( ProxyDisabledEntity.class );
 		assertFalse( "Wrong laziness", binding.isLazy() );
-		assertEquals( "Wrong proxy interface", null, binding.getProxyInterfaceName() );
+		assertEquals( "Wrong proxy interface", null, binding.getProxyInterfaceType() );
 	}
 
 	@Test
 	public void testProxyInterface() {
 		buildMetadataSources( ProxyInterfaceEntity.class );
 		EntityBinding binding = getEntityBinding( ProxyInterfaceEntity.class );
 		assertTrue( "Wrong laziness", binding.isLazy() );
 		assertEquals(
 				"Wrong proxy interface",
 				"org.hibernate.metamodel.source.annotations.entity.ProxyBindingTests$ProxyInterfaceEntity",
-				binding.getProxyInterfaceName()
+				binding.getProxyInterfaceType().getName()
 		);
 	}
 
 	@Entity
 	class NoProxyEntity {
 		@Id
 		private int id;
 	}
 
 	@Entity
 	@Proxy
 	class ProxiedEntity {
 		@Id
 		private int id;
 	}
 
 	@Entity
 	@Proxy(lazy = false)
 	class ProxyDisabledEntity {
 		@Id
 		private int id;
 	}
 
 	@Entity
 	@Proxy(proxyClass = ProxyInterfaceEntity.class)
 	class ProxyInterfaceEntity {
 		@Id
 		private int id;
 	}
 }
 
 
