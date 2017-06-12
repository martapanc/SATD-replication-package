diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index 16abb15d66..86a03fbeb8 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,659 +1,669 @@
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
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Mapping for a collection. Subclasses specialize to particular collection styles.
  * 
  * @author Gavin King
  */
 public abstract class Collection implements Fetchable, Value, Filterable {
 
 	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
 	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
 
 	private final Mappings mappings;
 	private PersistentClass owner;
 
 	private KeyValue key;
 	private Value element;
 	private Table collectionTable;
 	private String role;
 	private boolean lazy;
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 	private String referencedPropertyName;
 	private String nodeName;
 	private String elementNodeName;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private FetchMode fetchMode;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private String typeName;
 	private Properties typeParameters;
 	private final java.util.List filters = new ArrayList();
 	private final java.util.List manyToManyFilters = new ArrayList();
 	private final java.util.Set synchronizedTables = new HashSet();
 
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private String customSQLDeleteAll;
 	private boolean customDeleteAllCallable;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private String loaderName;
 
 	protected Collection(Mappings mappings, PersistentClass owner) {
 		this.mappings = mappings;
 		this.owner = owner;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isSet() {
 		return false;
 	}
 
 	public KeyValue getKey() {
 		return key;
 	}
 
 	public Value getElement() {
 		return element;
 	}
 
 	public boolean isIndexed() {
 		return false;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table table) {
 		this.collectionTable = table;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		if ( comparator == null && comparatorClassName != null ) {
 			try {
 				setComparator( (Comparator) ReflectHelper.classForName( comparatorClassName ).newInstance() );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Could not instantiate comparator class [" + comparatorClassName
 						+ "] for collection " + getRole()  
 				);
 			}
 		}
 		return comparator;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public abstract CollectionType getDefaultCollectionType() throws MappingException;
 
 	public boolean isPrimitiveArray() {
 		return false;
 	}
 
 	public boolean isArray() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isOneToMany() {
 		return element instanceof OneToMany;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public String getOwnerEntityName() {
 		return owner.getEntityName();
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public void setElement(Value element) {
 		this.element = element;
 	}
 
 	public void setKey(KeyValue key) {
 		this.key = key;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	public void setRole(String role) {
 		this.role = role==null ? null : role.intern();
 	}
 
 	public void setSorted(boolean sorted) {
 		this.sorted = sorted;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	/**
 	 * @deprecated Inject the owner into constructor.
 	 *
 	 * @param owner The owner
 	 */
 	@Deprecated
     public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrdering() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrdering(String orderFragment) {
 		this.manyToManyOrderBy = orderFragment;
 	}
 
 	public boolean isIdentified() {
 		return false;
 	}
 
 	public boolean hasOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public void setOrphanDelete(boolean orphanDelete) {
 		this.orphanDelete = orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int i) {
 		batchSize = i;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public void setCollectionPersisterClass(Class persister) {
 		this.collectionPersisterClass = persister;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 				"only inverse one-to-many associations may use on-delete=\"cascade\": " 
 				+ getRole() );
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 				"collection foreign key mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getKey().getType().getName() );
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException( 
 				"collection element mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getElement().getType().getName() );
 		}
 
 		checkColumnDuplication();
 		
 		if ( elementNodeName!=null && elementNodeName.startsWith("@") ) {
 			throw new MappingException("element node must not be an attribute: " + elementNodeName );
 		}
 		if ( elementNodeName!=null && elementNodeName.equals(".") ) {
 			throw new MappingException("element node must not be the parent: " + elementNodeName );
 		}
 		if ( nodeName!=null && nodeName.indexOf('@')>-1 ) {
 			throw new MappingException("collection node must not be an attribute: " + elementNodeName );
 		}
 	}
 
 	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
 			throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable s = (Selectable) columns.next();
 			if ( !s.isFormula() ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( "Repeated column in mapping for collection: "
 						+ getRole()
 						+ " column: "
 						+ col.getName() );
 				}
 			}
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
 		checkColumnDuplication( cols, getKey().getColumnIterator() );
 		if ( isIndexed() ) {
 			checkColumnDuplication( cols, ( (IndexedCollection) this )
 				.getIndex()
 				.getColumnIterator() );
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication( cols, ( (IdentifierCollection) this )
 				.getIdentifier()
 				.getColumnIterator() );
 		}
 		if ( !isOneToMany() ) {
 			checkColumnDuplication( cols, getElement().getColumnIterator() );
 		}
 	}
 
 	public Iterator getColumnIterator() {
 		return EmptyIterator.INSTANCE;
 	}
 
 	public int getColumnSpan() {
 		return 0;
 	}
 
 	public Type getType() throws MappingException {
 		return getCollectionType();
 	}
 
 	public CollectionType getCollectionType() {
 		if ( typeName == null ) {
 			return getDefaultCollectionType();
 		}
 		else {
 			return mappings.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName, isEmbedded() );
 		}
 	}
 
 	public boolean isNullable() {
 		return true;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public Table getTable() {
 		return owner.getTable();
 	}
 
 	public void createForeignKey() {
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return true;
 	}
 
 	private void createForeignKeys() throws MappingException {
 		// if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
 		if ( referencedPropertyName == null ) {
 			getElement().createForeignKey();
 			key.createForeignKeyOfEntity( getOwner().getEntityName() );
 		}
 		// }
 	}
 
 	abstract void createPrimaryKey();
 
 	public void createAllKeys() throws MappingException {
 		createForeignKeys();
 		if ( !isInverse() ) createPrimaryKey();
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName == null ? role : cacheRegionName;
 	}
 
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
 	}
 
 
 
 	public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLInsert = customSQLInsert;
 		this.customInsertCallable = callable;
 		this.insertCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public boolean isCustomInsertCallable() {
 		return customInsertCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLUpdate = customSQLUpdate;
 		this.customUpdateCallable = callable;
 		this.updateCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public boolean isCustomUpdateCallable() {
 		return customUpdateCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDelete = customSQLDelete;
 		this.customDeleteCallable = callable;
 		this.deleteCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public boolean isCustomDeleteCallable() {
 		return customDeleteCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDeleteAll = customSQLDeleteAll;
 		this.customDeleteAllCallable = callable;
 		this.deleteAllCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public boolean isCustomDeleteAllCallable() {
 		return customDeleteAllCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
 	}
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public void addManyToManyFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		manyToManyFilters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
 	}
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name==null ? null : name.intern();
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef==null ? null : propertyRef.intern();
 	}
 
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		this.typeName = typeName;
 	}
 
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 
 	public boolean[] getColumnInsertability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public void setElementNodeName(String elementNodeName) {
 		this.elementNodeName = elementNodeName;
 	}
 
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 	
 
 	public void setSubselectLoadable(boolean subqueryLoadable) {
 		this.subselectLoadable = subqueryLoadable;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public void setExtraLazy(boolean extraLazy) {
 		this.extraLazy = extraLazy;
 	}
 	
 	public boolean hasOrder() {
 		return orderBy!=null || manyToManyOrderBy!=null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;		
 	}
 	
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
index 8b14cfb616..3322e2ad34 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
@@ -1,170 +1,180 @@
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
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A mapping for a one-to-many association
  * @author Gavin King
  */
 public class OneToMany implements Value {
 
 	private final Mappings mappings;
 	private final Table referencingTable;
 
 	private String referencedEntityName;
 	private PersistentClass associatedClass;
 	private boolean embedded;
 	private boolean ignoreNotFound;
 
 	private EntityType getEntityType() {
 		return mappings.getTypeResolver().getTypeFactory().manyToOne(
 				getReferencedEntityName(), 
 				null, 
 				false,
 				false,
 				isEmbedded(),
 				isIgnoreNotFound(),
 				false
 			);
 	}
 
 	public OneToMany(Mappings mappings, PersistentClass owner) throws MappingException {
 		this.mappings = mappings;
 		this.referencingTable = (owner==null) ? null : owner.getTable();
 	}
 
 	public PersistentClass getAssociatedClass() {
 		return associatedClass;
 	}
 
     /**
      * Associated entity on the many side
      */
 	public void setAssociatedClass(PersistentClass associatedClass) {
 		this.associatedClass = associatedClass;
 	}
 
 	public void createForeignKey() {
 		// no foreign key element of for a one-to-many
 	}
 
 	public Iterator getColumnIterator() {
 		return associatedClass.getKey().getColumnIterator();
 	}
 
 	public int getColumnSpan() {
 		return associatedClass.getKey().getColumnSpan();
 	}
 
 	public FetchMode getFetchMode() {
 		return FetchMode.JOIN;
 	}
 
     /** 
      * Table of the owner entity (the "one" side)
      */
 	public Table getTable() {
 		return referencingTable;
 	}
 
 	public Type getType() {
 		return getEntityType();
 	}
 
 	public boolean isNullable() {
 		return false;
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 	
 	public boolean isValid(Mapping mapping) throws MappingException {
 		if (referencedEntityName==null) {
 			throw new MappingException("one to many association must specify the referenced entity");
 		}
 		return true;
 	}
 
     public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
     /** 
      * Associated entity on the "many" side
      */    
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName==null ? null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	
 	public boolean[] getColumnInsertability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
index a33fb7355d..9a6ab734f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
@@ -1,123 +1,133 @@
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
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.Type;
 
 /**
  * A simple-point association (ie. a reference to another entity).
  * @author Gavin King
  */
 public abstract class ToOne extends SimpleValue implements Fetchable {
 
 	private FetchMode fetchMode;
 	protected String referencedPropertyName;
 	private String referencedEntityName;
 	private boolean embedded;
 	private boolean lazy = true;
 	protected boolean unwrapProxy;
 
 	protected ToOne(Mappings mappings, Table table) {
 		super( mappings, table );
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode=fetchMode;
 	}
 
 	public abstract void createForeignKey() throws MappingException;
 	public abstract Type getType() throws MappingException;
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String name) {
 		referencedPropertyName = name==null ? null : name.intern();
 	}
 
 	public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName==null ? 
 				null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName)
 	throws MappingException {
 		if (referencedEntityName==null) {
 			referencedEntityName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 		}
 	}
 
 	public boolean isTypeSpecified() {
 		return referencedEntityName!=null;
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		if (referencedEntityName==null) {
 			throw new MappingException("association must specify the referenced entity");
 		}
 		return super.isValid( mapping );
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 	
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isUnwrapProxy() {
 		return unwrapProxy;
 	}
 
 	public void setUnwrapProxy(boolean unwrapProxy) {
 		this.unwrapProxy = unwrapProxy;
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
index 0c2aef0ab5..893518f3c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
@@ -1,143 +1,154 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentArrayHolder;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * A type for persistent arrays.
  * @author Gavin King
  */
 public class ArrayType extends CollectionType {
 
 	private final Class elementClass;
 	private final Class arrayClass;
 
+	/**
+	 * @deprecated Use {@link #ArrayType(TypeFactory.TypeScope, String, String, Class )} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.elementClass = elementClass;
 		arrayClass = Array.newInstance(elementClass, 0).getClass();
 	}
 
+	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass) {
+		super( typeScope, role, propertyRef );
+		this.elementClass = elementClass;
+		arrayClass = Array.newInstance(elementClass, 0).getClass();
+	}
+
 	public Class getReturnedClass() {
 		return arrayClass;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentArrayHolder(session, persister);
 	}
 
 	/**
 	 * Not defined for collections of primitive type
 	 */
 	public Iterator getElementsIterator(Object collection) {
 		return Arrays.asList( (Object[]) collection ).iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object array) {
 		return new PersistentArrayHolder(session, array);
 	}
 
 	public boolean isArrayType() {
 		return true;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		int length = Array.getLength(value);
 		List list = new ArrayList(length);
 		Type elemType = getElementType(factory);
 		for ( int i=0; i<length; i++ ) {
 			list.add( elemType.toLoggableString( Array.get(value, i), factory ) );
 		}
 		return list.toString();
 	}
 	
 	public Object instantiateResult(Object original) {
 		return Array.newInstance( elementClass, Array.getLength(original) );
 	}
 
 	public Object replaceElements(
 		Object original,
 		Object target,
 		Object owner, 
 		Map copyCache, 
 		SessionImplementor session)
 	throws HibernateException {
 		
 		int length = Array.getLength(original);
 		if ( length!=Array.getLength(target) ) {
 			//note: this affects the return value!
 			target=instantiateResult(original);
 		}
 		
 		Type elemType = getElementType( session.getFactory() );
 		for ( int i=0; i<length; i++ ) {
 			Array.set( target, i, elemType.replace( Array.get(original, i), null, session, owner, copyCache ) );
 		}
 		
 		return target;
 	
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object indexOf(Object array, Object element) {
 		int length = Array.getLength(array);
 		for ( int i=0; i<length; i++ ) {
 			//TODO: proxies!
 			if ( Array.get(array, i)==element ) return i;
 		}
 		return null;
 	}
 
 	@Override
 	protected boolean initializeImmediately() {
 		return true;
 	}
 
 	@Override
 	public boolean hasHolder() {
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
index de865bbea7..88ed066c4b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
@@ -1,96 +1,101 @@
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
 package org.hibernate.type;
 
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.Joinable;
 
 /**
  * A type that represents some kind of association between entities.
  * @see org.hibernate.engine.internal.Cascade
  * @author Gavin King
  */
 public interface AssociationType extends Type {
 
 	/**
 	 * Get the foreign key directionality of this association
 	 */
 	public ForeignKeyDirection getForeignKeyDirection();
 
 	//TODO: move these to a new JoinableType abstract class,
 	//extended by EntityType and PersistentCollectionType:
 
 	/**
 	 * Is the primary key of the owning entity table
 	 * to be used in the join?
 	 */
 	public boolean useLHSPrimaryKey();
 	/**
 	 * Get the name of a property in the owning entity 
 	 * that provides the join key (null if the identifier)
 	 */
 	public String getLHSPropertyName();
 	
 	/**
 	 * The name of a unique property of the associated entity 
 	 * that provides the join key (null if the identifier of
 	 * an entity, or key of a collection)
 	 */
 	public String getRHSUniqueKeyPropertyName();
 
 	/**
 	 * Get the "persister" for this association - a class or
 	 * collection persister
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the entity name of the associated entity
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the "filtering" SQL fragment that is applied in the 
 	 * SQL on clause, in addition to the usual join condition
 	 */	
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) 
 	throws MappingException;
 	
 	/**
 	 * Do we dirty check this association, even when there are
 	 * no columns to be updated?
 	 */
 	public abstract boolean isAlwaysDirtyChecked();
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public boolean isEmbeddedInXML();
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BagType.java b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
index 969aa96dd1..08f7ad2a15 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
@@ -1,59 +1,68 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentBag;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class BagType extends CollectionType {
 
+	/**
+	 * @deprecated Use {@link #BagType(TypeFactory.TypeScope, String, String )}
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public BagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public BagType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentBag(session);
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentBag( session, (Collection) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index d119badddf..4950059ea2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,788 +1,800 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.jboss.logging.Logger;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 	private final boolean isEmbeddedInXML;
 
+	/**
+	 * @deprecated Use {@link #CollectionType(TypeFactory.TypeScope, String, String)} instead
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 	}
 
+	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
+		this.typeScope = typeScope;
+		this.role = role;
+		this.foreignKeyPropertyName = foreignKeyPropertyName;
+		this.isEmbeddedInXML = true;
+	}
+
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) element = li.getImplementation();
 			}
 			if ( element == childObject ) return true;
 		}
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	public int getHashCode(Object x) {
 		throw new UnsupportedOperationException( "cannot doAfterTransactionCompletion lookups on collections" );
 	}
 
 	/**
 	 * Instantiate an uninitialized collection wrapper or holder. Callers MUST add the holder to the
 	 * persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param persister The underlying collection persister (metadata)
 	 * @param key The owner key.
 	 * @return The instantiated collection.
 	 */
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
 		}
 		return list.toString();
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public String getName() {
 		return getReturnedClass().getName() + '(' + getRole() + ')';
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection, which may not yet be wrapped
 	 *
 	 * @param collection The collection to be iterated
 	 * @param session The session from which the request is originating.
 	 * @return The iterator.
 	 */
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
 		return getElementsIterator(collection);
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection in POJO mode
 	 *
 	 * @param collection The collection to be iterated
 	 * @return The iterator.
 	 */
 	protected Iterator getElementsIterator(Object collection) {
 		return ( (Collection) collection ).iterator();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//remember the uk value
 		
 		//This solution would allow us to eliminate the owner arg to disassemble(), but
 		//what if the collection was null, and then later had elements added? seems unsafe
 		//session.getPersistenceContext().getCollectionEntry( (PersistentCollection) value ).getKey();
 		
 		final Serializable key = getKeyOfOwner(owner, session);
 		if (key==null) {
 			return null;
 		}
 		else {
 			return getPersister(session)
 					.getKeyType()
 					.disassemble( key, session, owner );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//we must use the "remembered" uk value, since it is 
 		//not available from the EntityEntry during assembly
 		if (cached==null) {
 			return null;
 		}
 		else {
 			final Serializable key = (Serializable) getPersister(session)
 					.getKeyType()
 					.assemble( cached, session, owner);
 			return resolveKey( key, session, owner );
 		}
 	}
 
 	/**
 	 * Is the owning entity versioned?
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return True if the collection owner is versioned; false otherwise.
 	 * @throws org.hibernate.MappingException Indicates our persister could not be located.
 	 */
 	private boolean isOwnerVersioned(SessionImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SessionImplementor session) {
 		return session.getFactory().getCollectionPersister( role );
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty(old, current, session);
 	}
 
 	/**
 	 * Wrap the naked collection instance in a wrapper, or instantiate a
 	 * holder. Callers <b>MUST</b> add the holder to the persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param collection The bare collection to be wrapped.
 	 * @return The wrapped collection.
 	 */
 	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) return null; // This just handles a particular case of component
 									  // projection, perhaps get rid of it and throw an exception
 		
 		if ( foreignKeyPropertyName == null ) {
 			return entityEntry.getId();
 		}
 		else {
 			// TODO: at the point where we are resolving collection references, we don't
 			// know if the uk value has been resolved (depends if it was earlier or
 			// later in the mapping document) - now, we could try and use e.getStatus()
 			// to decide to semiResolve(), trouble is that initializeEntity() reuses
 			// the same array for resolved and hydrated values
 			Object id;
 			if ( entityEntry.getLoadedState() != null ) {
 				id = entityEntry.getLoadedValue( foreignKeyPropertyName );
 			}
 			else {
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = (Serializable) keyType.semiResolve(
 						entityEntry.getLoadedValue( foreignKeyPropertyName ),
 						session,
 						owner 
 					);
 			}
 
 			return (Serializable) id;
 		}
 	}
 
 	/**
 	 * Get the id value from the owning entity key, usually the same as the key, but might be some
 	 * other property, in the case of property-ref
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's id, if it can be obtained from the key;
 	 * otherwise, null is returned
 	 */
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass();
 			if ( ownerMappedClass.isAssignableFrom( keyType.getReturnedClass() ) &&
 					keyType.getReturnedClass().isInstance( key ) ) {
 				// the key is the owning entity itself, so get the ID from the key
 				ownerId = ownerPersister.getIdentifier( key, session );
 			}
 			else {
 				// TODO: check if key contains the owner ID
 			}
 		}
 		return ownerId;
 	}
 
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SessionImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 			throws MappingException {
 		try {
 			
 			QueryableCollection collectionPersister = (QueryableCollection) factory
 					.getCollectionPersister( role );
 			
 			if ( !collectionPersister.getElementType().isEntityType() ) {
 				throw new MappingException( 
 						"collection was not an association: " + 
 						collectionPersister.getRole() 
 					);
 			}
 			
 			return collectionPersister.getElementPersister().getEntityName();
 			
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "collection role is not queryable " + role );
 		}
 	}
 
 	/**
 	 * Replace the elements of a collection with the elements of another collection.
 	 *
 	 * @param original The 'source' of the replacement elements (where we copy from)
 	 * @param target The target of the replacement elements (where we copy to)
 	 * @param owner The owner of the collection being merged
 	 * @param copyCache The map of elements already replaced.
 	 * @param session The session from which the merge event originated.
 	 * @return The merged collection.
 	 */
 	public Object replaceElements(
 			Object original,
 			Object target,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		// TODO: does not work for EntityMode.DOM4J yet!
 		java.util.Collection result = ( java.util.Collection ) target;
 		result.clear();
 
 		// copy elements into newly empty target collection
 		Type elemType = getElementType( session.getFactory() );
 		Iterator iter = ( (java.util.Collection) original ).iterator();
 		while ( iter.hasNext() ) {
 			result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
 		}
 
 		// if the original is a PersistentCollection, and that original
 		// was not flagged as dirty, then reset the target's dirty flag
 		// here after the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				if ( ! ( ( PersistentCollection ) original ).isDirty() ) {
 					( ( PersistentCollection ) result ).clearDirty();
 				}
 
 				if ( elemType instanceof AssociationType ) {
 					preserveSnapshot( (PersistentCollection) original,
 							(PersistentCollection) result,
 							(AssociationType) elemType, owner, copyCache,
 							session );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void preserveSnapshot(PersistentCollection original,
 			PersistentCollection result, AssociationType elemType,
 			Object owner, Map copyCache, SessionImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
 				( (List) targetSnapshot ).add( elemType.replace(
 						obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
 				targetSnapshot = new TreeMap(
 						( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
 						CollectionHelper.determineProperSizing(
 								( (Map) originalSnapshot ).size() ),
 						CollectionHelper.LOAD_FACTOR );
 			}
 
 			for ( Map.Entry<Object, Object> entry : (
 					(Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
 				Object resultSnapshotValue = ( resultSnapshot == null ) ? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
 				if ( key == value ) {
 					Object newValue = elemType.replace( value,
 							resultSnapshotValue, session, owner, copyCache );
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
 					Object newValue = elemType.replace( value,
 							resultSnapshotValue, session, owner, copyCache );
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
 				arr[i] = elemType.replace(
 						arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(
 				result );
 		if ( ce != null ) {
 			ce.resetStoredSnapshot( result, targetSnapshot );
 		}
 
 	}
 
 	/**
 	 * Instantiate a new "underlying" collection exhibiting the same capacity
 	 * charactersitcs and the passed "original".
 	 *
 	 * @param original The original collection.
 	 * @return The newly instantiated collection.
 	 */
 	protected Object instantiateResult(Object original) {
 		// by default just use an unanticipated capacity since we don't
 		// know how to extract the capacity to use from original here...
 		return instantiate( -1 );
 	}
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial capacity
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			return target;
 		}
 
 		// for a null target, or a target which is the same as the original, we
 		// need to put the merged elements in a new collection
 		Object result = target == null || target == original ? instantiateResult( original ) : target;
 		
 		//for arrays, replaceElements() may return a different reference, since
 		//the array length might not match
 		result = replaceElements( original, result, owner, copyCache, session );
 
 		if ( original == target ) {
 			// get the elements back into the target making sure to handle dirty flag
 			boolean wasClean = PersistentCollection.class.isInstance( target ) && !( ( PersistentCollection ) target ).isDirty();
 			//TODO: this is a little inefficient, don't need to do a whole
 			//      deep replaceElements() call
 			replaceElements( result, target, owner, copyCache, session );
 			if ( wasClean ) {
 				( ( PersistentCollection ) target ).clearDirty();
 			}
 			result = target;
 		}
 
 		return result;
 	}
 
 	/**
 	 * Get the Hibernate type of the collection elements
 	 *
 	 * @param factory The session factory.
 	 * @return The type of the collection elements
 	 * @throws MappingException Indicates the underlying persister could not be located.
 	 */
 	public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
 		return factory.getCollectionPersister( getRole() ).getElementType();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
 				
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately() ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef( "Created collection wrapper: %s",
 						MessageHelper.collectionInfoString( persister, collection,
 								key, session ) );
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder() {
 		return false;
 	}
 
 	protected boolean initializeImmediately() {
 		return false;
 	}
 
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			node.detach();
 		}
 		else {
 			replaceNode( node, (Element) value );
 		}
 	}
 	
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
index d2cff37443..e0531c4145 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
@@ -1,121 +1,138 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.usertype.LoggableUserType;
 import org.hibernate.usertype.UserCollectionType;
 
 /**
  * A custom type for mapping user-written classes that implement <tt>PersistentCollection</tt>
  * 
  * @see org.hibernate.collection.spi.PersistentCollection
  * @see org.hibernate.usertype.UserCollectionType
  * @author Gavin King
  */
 public class CustomCollectionType extends CollectionType {
 
 	private final UserCollectionType userType;
 	private final boolean customLogging;
 
+	/**
+	 * @deprecated Use {@link #CustomCollectionType(TypeFactory.TypeScope, Class, String, String )} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CustomCollectionType(
 			TypeFactory.TypeScope typeScope,
 			Class userTypeClass,
 			String role,
 			String foreignKeyPropertyName,
 			boolean isEmbeddedInXML) {
 		super( typeScope, role, foreignKeyPropertyName, isEmbeddedInXML );
+		userType = createUserCollectionType( userTypeClass );
+		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
+	}
 
+	public CustomCollectionType(
+			TypeFactory.TypeScope typeScope,
+			Class userTypeClass,
+			String role,
+			String foreignKeyPropertyName) {
+		super( typeScope, role, foreignKeyPropertyName );
+		userType = createUserCollectionType( userTypeClass );
+		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
+	}
+
+	private static UserCollectionType createUserCollectionType(Class userTypeClass) {
 		if ( !UserCollectionType.class.isAssignableFrom( userTypeClass ) ) {
 			throw new MappingException( "Custom type does not implement UserCollectionType: " + userTypeClass.getName() );
 		}
 
 		try {
-			userType = ( UserCollectionType ) userTypeClass.newInstance();
+			return ( UserCollectionType ) userTypeClass.newInstance();
 		}
 		catch ( InstantiationException ie ) {
 			throw new MappingException( "Cannot instantiate custom type: " + userTypeClass.getName() );
 		}
 		catch ( IllegalAccessException iae ) {
 			throw new MappingException( "IllegalAccessException trying to instantiate custom type: " + userTypeClass.getName() );
 		}
-
-		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return userType.instantiate(session, persister);
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return userType.wrap(session, collection);
 	}
 
 	public Class getReturnedClass() {
 		return userType.instantiate( -1 ).getClass();
 	}
 
 	public Object instantiate(int anticipatedType) {
 		return userType.instantiate( anticipatedType );
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return userType.getElementsIterator(collection);
 	}
 	public boolean contains(Object collection, Object entity, SessionImplementor session) {
 		return userType.contains(collection, entity);
 	}
 	public Object indexOf(Object collection, Object entity) {
 		return userType.indexOf(collection, entity);
 	}
 
 	public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SessionImplementor session)
 	throws HibernateException {
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 		return userType.replaceElements(original, target, cp, owner, copyCache, session);
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( customLogging ) {
 			return ( ( LoggableUserType ) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return super.renderLoggableString( value, factory );
 		}
 	}
 
 	public UserCollectionType getUserType() {
 		return userType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index 9265c31a2f..0451e09dcc 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,672 +1,707 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.tuple.ElementWrapper;
 
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
 	protected final boolean isEmbeddedInXML;
 	private final boolean eager;
 	private final boolean unwrapProxy;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
+	 *
+	 * @deprecated Use {@link #EntityType(TypeFactory.TypeScope, String, String, boolean, boolean )} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
+	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean isEmbeddedInXML,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 	}
 
+	/**
+	 * Constructs the requested entity type mapping.
+	 *
+	 * @param scope The type scope
+	 * @param entityName The name of the associated entity.
+	 * @param uniqueKeyPropertyName The property-ref name, or null if we
+	 * reference the PK of the associated entity.
+	 * @param eager Is eager fetching enabled.
+	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
+	 * says to return the "implementation target" of lazy prooxies; typically only possible
+	 * with lazy="no-proxy".
+	 */
+	protected EntityType(
+			TypeFactory.TypeScope scope,
+			String entityName,
+			String uniqueKeyPropertyName,
+			boolean eager,
+			boolean unwrapProxy) {
+		this.scope = scope;
+		this.associatedEntityName = entityName;
+		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
+		this.isEmbeddedInXML = true;
+		this.eager = eager;
+		this.unwrapProxy = unwrapProxy;
+	}
+
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
 	public String getName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * Does this association foreign key reference the primary key of the other table?
 	 * Otherwise, it references a property-ref.
 	 *
 	 * @return True if this association reference the PK of the associated entity.
 	 */
 	public boolean isReferenceToPrimaryKey() {
 		return uniqueKeyPropertyName==null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return uniqueKeyPropertyName;
 	}
 
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public String getPropertyName() {
 		return null;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @return The associated entity name.
 	 */
 	public final String getAssociatedEntityName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @param factory The session factory, for resolution.
 	 * @return The associated entity name.
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 * @return The associated joinable
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return ( Joinable ) factory.getEntityPersister( associatedEntityName );
 	}
 
 	/**
 	 * This returns the wrong class for an entity with a proxy, or for a named
 	 * entity.  Theoretically it should return the proxy class, but it doesn't.
 	 * <p/>
 	 * The problem here is that we do not necessarily have a ref to the associated
 	 * entity persister (nor to the session factory, to look it up) which is really
 	 * needed to "do the right thing" here...
 	 *
 	 * @return The entiyt class.
 	 */
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
 	private Class determineAssociatedEntityClass() {
 		try {
 			return ReflectHelper.classForName( getAssociatedEntityName() );
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			return java.util.Map.class;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate(rs, names, session, owner), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 * @return True if x == y; false otherwise.
 	 */
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get(original);
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null  &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getFactory().getEntityPersister( associatedEntityName )
 						.instantiate( null, session );
 				//TODO: should this be Session.instantiate(Persister, ...)?
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure("non-transient entity has a null id");
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace(id, null, session, owner, copyCache);
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if (x instanceof HibernateProxy) {
 			id = ( (HibernateProxy) x ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			final Class mappedClass = persister.getMappedClass();
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				id = persister.getIdentifier( x );
 			}
 			else {
 				id = (Serializable) x;
 			}
 		}
 		return persister.getIdentifierType().getHashCode( id, factory );
 	}
 
 	@Override
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		// associations (many-to-one and one-to-one) can be null...
 		if ( x == null || y == null ) {
 			return x == y;
 		}
 
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual(x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if (x instanceof HibernateProxy) {
 			xid = ( (HibernateProxy) x ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				xid = persister.getIdentifier( x );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				xid = (Serializable) x;
 			}
 		}
 
 		Serializable yid;
 		if (y instanceof HibernateProxy) {
 			yid = ( (HibernateProxy) y ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( y.getClass() ) ) {
 				yid = persister.getIdentifier( y );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				yid = (Serializable) y;
 			}
 		}
 
 		return persister.getIdentifierType()
 				.isEqual(xid, yid, factory);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isXMLElement() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			return getIdentifierType(factory).fromXMLNode(xml, factory);
 		}
 		else {
 			return xml;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			getIdentifierType(factory).setToXMLNode(node, value, factory);
 		}
 		else {
 			Element elt = (Element) value;
 			replaceNode( node, new ElementWrapper(elt) );
 		}
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( isNotEmbedded( session ) ) {
 			return value;
 		}
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			if ( isNull( owner, session ) ) {
 				return null; //EARLY EXIT!
 			}
 
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
 			else {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
 	}
 
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return factory.getEntityPersister( associatedEntityName ).getIdentifierType();
 	}
 
 	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		if ( isNotEmbedded(session) ) {
 			return value;
 		}
 
 		if ( isReferenceToPrimaryKey() ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session ); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( ( EntityType ) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	protected boolean isNotEmbedded(SessionImplementor session) {
 //		return !isEmbeddedInXML;
 		return false;
 	}
 
 	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 * @return The loggable string.
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 		
 		EntityPersister persister = factory.getEntityPersister( associatedEntityName );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
 			final EntityMode entityMode = persister.getEntityMode();
 			final Serializable id;
 			if ( entityMode == null ) {
 				if ( isEmbeddedInXML ) {
 					throw new ClassCastException( value.getClass().getName() );
 				}
 				id = ( Serializable ) value;
 			} else if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = ( HibernateProxy ) value;
 				id = proxy.getHibernateLazyInitializer().getIdentifier();
 			}
 			else {
 				id = persister.getIdentifier( value );
 			}
 			
 			result.append( '#' )
 				.append( persister.getIdentifierType().toLoggableString( id, factory ) );
 		}
 		
 		return result.toString();
 	}
 
 	/**
 	 * Is the association modeled here defined as a 1-1 in the database (physical model)?
 	 *
 	 * @return True if a 1-1 in the database; false otherwise.
 	 */
 	public abstract boolean isOneToOne();
 
 	/**
 	 * Is the association modeled here a 1-1 according to the logical moidel?
 	 *
 	 * @return True if a 1-1 in the logical model; false otherwise.
 	 */
 	public boolean isLogicalOneToOne() {
 		return isOneToOne();
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param factory The mappings...
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(Mapping factory) {
 		return factory.getIdentifierType( getAssociatedEntityName() );
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(SessionImplementor session) {
 		return getIdentifierType( session.getFactory() );
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate type.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
 		if ( isReferenceToPrimaryKey() ) {
 			return getIdentifierType(factory);
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( ( EntityType ) type).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate property name.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() ) {
 			return factory.getIdentifierPropertyName( getAssociatedEntityName() );
 		}
 		else {
 			return uniqueKeyPropertyName;
 		}
 	}
 	
 	protected abstract boolean isNullable();
 
 	/**
 	 * Resolve an identifier via a load.
 	 *
 	 * @param id The entity id to resolve
 	 * @param session The orginating session.
 	 * @return The resolved identifier (i.e., loaded entity).
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				session.getFactory()
 						.getEntityPersister( getAssociatedEntityName() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxyOrEntity ).getHibernateLazyInitializer()
 					.setUnwrap( isProxyUnwrapEnabled );
 		}
 
 		return proxyOrEntity;
 	}
 
 	protected boolean isNull(Object owner, SessionImplementor session) {
 		return false;
 	}
 
 	/**
 	 * Load an instance by a unique key that is not the primary key.
 	 *
 	 * @param entityName The name of the entity to load
 	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
 	 * @param key The unique key property value.
 	 * @param session The originating session.
 	 * @return The loaded entity
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName, 
 			String uniqueKeyPropertyName, 
 			Object key, 
 			SessionImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );
 
 		//TODO: implement caching?! proxies?!
 
 		EntityUniqueKey euk = new EntityUniqueKey(
 				entityName, 
 				uniqueKeyPropertyName, 
 				key, 
 				getIdentifierOrUniqueKeyType( factory ),
 				persister.getEntityMode(),
 				session.getFactory()
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		Object result = persistenceContext.getEntity( euk );
 		if ( result == null ) {
 			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
 		}
 		return result == null ? null : persistenceContext.proxyFor( result );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
index 306a815884..2d34237b95 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
@@ -1,67 +1,76 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentIdentifierBag;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class IdentifierBagType extends CollectionType {
 
+	/**
+	 * @deprecated Use {@link #IdentifierBagType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	public PersistentCollection instantiate(
 		SessionImplementor session,
 		CollectionPersister persister, Serializable key)
 		throws HibernateException {
 
 		return new PersistentIdentifierBag(session);
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentIdentifierBag( session, (java.util.Collection) collection );
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ListType.java b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
index fd825d9727..b7d9824b4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
@@ -1,71 +1,80 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.collection.internal.PersistentList;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class ListType extends CollectionType {
 
+	/**
+	 * @deprecated Use {@link #ListType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		return new PersistentList(session);
 	}
 
 	public Class getReturnedClass() {
 		return List.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentList( session, (List) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Object indexOf(Object collection, Object element) {
 		List list = (List) collection;
 		for ( int i=0; i<list.size(); i++ ) {
 			//TODO: proxies!
 			if ( list.get(i)==element ) return i;
 		}
 		return null;
 	}
 	
 }
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
index 446dbf3467..c7cd51db99 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
@@ -1,299 +1,318 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A many-to-one association to an entity.
  *
  * @author Gavin King
  */
 public class ManyToOneType extends EntityType {
 	private final boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity.
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName) {
 		this( scope, referencedEntityName, false );
 	}
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity and the
 	 * given laziness characteristic
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 * @param lazy Should the association be handled lazily
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean lazy) {
 		this( scope, referencedEntityName, null, lazy, true, false, false, false );
 	}
 
+
+	/**
+	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, String, boolean, boolean, boolean, boolean ) } instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.ignoreNotFound = ignoreNotFound;
 		this.isLogicalOneToOne = isLogicalOneToOne;
 	}
 
+	public ManyToOneType(
+			TypeFactory.TypeScope scope,
+			String referencedEntityName,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			boolean ignoreNotFound,
+			boolean isLogicalOneToOne) {
+		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, unwrapProxy );
+		this.ignoreNotFound = ignoreNotFound;
+		this.isLogicalOneToOne = isLogicalOneToOne;
+	}
+
 	protected boolean isNullable() {
 		return ignoreNotFound;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		// always need to dirty-check, even when non-updateable;
 		// this ensures that when the association is updated,
 		// the entity containing this association will be updated
 		// in the cache
 		return true;
 	}
 
 	public boolean isOneToOne() {
 		return false;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		// our column span is the number of columns in the PK
 		return getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, settable, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, session );
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public Object hydrate(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		// return the (fully resolved) identifier value, but do not resolve
 		// to the actual referenced entity instance
 		// NOTE: the owner of the association is not really the owner of the id!
 		Serializable id = (Serializable) getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeGet( rs, names, session, null );
 		scheduleBatchLoadIfNeeded( id, session );
 		return id;
 	}
 
 	/**
 	 * Register the entity as batch loadable, if enabled
 	 */
 	@SuppressWarnings({ "JavaDoc" })
 	private void scheduleBatchLoadIfNeeded(Serializable id, SessionImplementor session) throws MappingException {
 		//cannot batch fetch by unique key (property-ref associations)
 		if ( uniqueKeyPropertyName == null && id != null ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			final EntityKey entityKey = session.generateEntityKey( id, persister );
 			if ( entityKey.isBatchLoadable() && !session.getPersistenceContext().containsEntity( entityKey ) ) {
 				session.getPersistenceContext().getBatchFetchQueue().addBatchLoadableEntityKey( entityKey );
 			}
 		}
 	}
 	
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public boolean isModified(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old!=null;
 		}
 		if ( old == null ) {
 			// we already know current is not null...
 			return true;
 		}
 		// the ids are fully resolved, so compare them with isDirty(), not isModified()
 		return getIdentifierOrUniqueKeyType( session.getFactory() )
 				.isDirty( old, getIdentifier( current, session ), session );
 	}
 
 	public Serializable disassemble(
 			Object value,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 
 		if ( isNotEmbedded( session ) ) {
 			return getIdentifierType( session ).disassemble( value, session, owner );
 		}
 		
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					getAssociatedEntityName(),
 					value,
 					session
 			);
 			if ( id == null ) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName()
 				);
 			}
 			return getIdentifierType( session ).disassemble( id, session, owner );
 		}
 	}
 
 	public Object assemble(
 			Serializable oid,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		
 		Serializable id = assembleId( oid, session );
 
 		if ( isNotEmbedded( session ) ) {
 			return id;
 		}
 		
 		if ( id == null ) {
 			return null;
 		}
 		else {
 			return resolveIdentifier( id, session );
 		}
 	}
 
 	private Serializable assembleId(Serializable oid, SessionImplementor session) {
 		//the owner of the association is not the owner of the id
 		return ( Serializable ) getIdentifierType( session ).assemble( oid, session, null );
 	}
 
 	public void beforeAssemble(Serializable oid, SessionImplementor session) {
 		scheduleBatchLoadIfNeeded( assembleId( oid, session ), session );
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 	
 	public boolean isDirty(
 			Object old,
 			Object current,
 			SessionImplementor session) throws HibernateException {
 		if ( isSame( old, current ) ) {
 			return false;
 		}
 		Object oldid = getIdentifier( old, session );
 		Object newid = getIdentifier( current, session );
 		return getIdentifierType( session ).isDirty( oldid, newid, session );
 	}
 
 	public boolean isDirty(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( isAlwaysDirtyChecked() ) {
 			return isDirty( old, current, session );
 		}
 		else {
 			if ( isSame( old, current ) ) {
 				return false;
 			}
 			Object oldid = getIdentifier( old, session );
 			Object newid = getIdentifier( current, session );
 			return getIdentifierType( session ).isDirty( oldid, newid, checkable, session );
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MapType.java b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
index 38a737b23d..f0aa1d1447 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
@@ -1,101 +1,110 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentMap;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class MapType extends CollectionType {
 
+	/**
+	 * @deprecated Use {@link #MapType(TypeFactory.TypeScope, String, String ) } instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		return new PersistentMap(session);
 	}
 
 	public Class getReturnedClass() {
 		return Map.class;
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return ( (java.util.Map) collection ).values().iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentMap( session, (java.util.Map) collection );
 	}
 	
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 
 		       ? new HashMap()
 		       : new HashMap( anticipatedSize + (int)( anticipatedSize * .75f ), .75f );
 	}
 
 	public Object replaceElements(
 		final Object original,
 		final Object target,
 		final Object owner, 
 		final java.util.Map copyCache, 
 		final SessionImplementor session)
 		throws HibernateException {
 
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 		
 		java.util.Map result = (java.util.Map) target;
 		result.clear();
 		
 		Iterator iter = ( (java.util.Map) original ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
 			Object key = cp.getIndexType().replace( me.getKey(), null, session, owner, copyCache );
 			Object value = cp.getElementType().replace( me.getValue(), null, session, owner, copyCache );
 			result.put(key, value);
 		}
 		
 		return result;
 		
 	}
 	
 	public Object indexOf(Object collection, Object element) {
 		Iterator iter = ( (Map) collection ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			//TODO: proxies!
 			if ( me.getValue()==element ) return me.getKey();
 		}
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
index ccf543cae3..838955bf72 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
@@ -1,176 +1,197 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A one-to-one association to an entity
  * @author Gavin King
  */
 public class OneToOneType extends EntityType {
 
 	private final ForeignKeyDirection foreignKeyType;
 	private final String propertyName;
 	private final String entityName;
 
+	/**
+	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, String, boolean, boolean, String, String)}
+	 *  instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.foreignKeyType = foreignKeyType;
 		this.propertyName = propertyName;
 		this.entityName = entityName;
 	}
-	
+
+	public OneToOneType(
+			TypeFactory.TypeScope scope,
+			String referencedEntityName,
+			ForeignKeyDirection foreignKeyType,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
+		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, unwrapProxy );
+		this.foreignKeyType = foreignKeyType;
+		this.propertyName = propertyName;
+		this.entityName = entityName;
+	}
+
 	public String getPropertyName() {
 		return propertyName;
 	}
 	
 	public boolean isNull(Object owner, SessionImplementor session) {
 		if ( propertyName != null ) {
 			final EntityPersister ownerPersister = session.getFactory().getEntityPersister( entityName );
 			final Serializable id = session.getContextEntityIdentifier( owner );
 			final EntityKey entityKey = session.generateEntityKey( id, ownerPersister );
 			return session.getPersistenceContext().isPropertyNull( entityKey, getPropertyName() );
 		}
 		else {
 			return false;
 		}
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	private static final Size[] SIZES = new Size[0];
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public boolean isOneToOne() {
 		return true;
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return foreignKeyType;
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 
 		return session.getContextEntityIdentifier(owner);
 	}
 
 	protected boolean isNullable() {
 		return foreignKeyType==ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return null;
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//this should be a call to resolve(), not resolveIdentifier(), 
 		//'cos it might be a property-ref, and we did not cache the
 		//referenced value
 		return resolve( session.getContextEntityIdentifier(owner), session, owner );
 	}
 	
 	/**
 	 * We don't need to dirty check one-to-one because of how 
 	 * assemble/disassemble is implemented and because a one-to-one 
 	 * association is never dirty
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		//TODO: this is kinda inconsistent with CollectionType
 		return false; 
 	}
 	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
index 708be71586..b00b1eaf4b 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
@@ -1,53 +1,62 @@
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
 package org.hibernate.type;
 import java.util.LinkedHashMap;
 
 /**
  * A specialization of the map type, with (resultset-based) ordering.
  */
 public class OrderedMapType extends MapType {
 
 	/**
 	 * Constructs a map type capable of creating ordered maps of the given
 	 * role.
 	 *
 	 * @param role The collection role name.
 	 * @param propertyRef The property ref name.
 	 * @param isEmbeddedInXML Is this collection to embed itself in xml
+	 *
+	 * @deprecated Use {@link #OrderedMapType(TypeFactory.TypeScope, String, String)} instead.
+	 *  instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
+	@Deprecated
 	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashMap( anticipatedSize )
 				: new LinkedHashMap();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
index 970b01821e..b9aae70074 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
@@ -1,54 +1,63 @@
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
 package org.hibernate.type;
 import java.util.LinkedHashSet;
 
 /**
  * A specialization of the set type, with (resultset-based) ordering.
  */
 public class OrderedSetType extends SetType {
 
 	/**
 	 * Constructs a set type capable of creating ordered sets of the given
 	 * role.
 	 *
 	 * @param typeScope The scope for this type instance.
 	 * @param role The collection role name.
 	 * @param propertyRef The property ref name.
 	 * @param isEmbeddedInXML Is this collection to embed itself in xml
+	 *
+	 * @deprecated Use {@link #OrderedSetType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
+	@Deprecated
 	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashSet( anticipatedSize )
 				: new LinkedHashSet();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SetType.java b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
index 52b03dbd70..e1b6bd3242 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
@@ -1,58 +1,67 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.HashSet;
 
 import org.hibernate.collection.internal.PersistentSet;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SetType extends CollectionType {
 
+	/**
+	 * @deprecated Use {@link #SetType(org.hibernate.type.TypeFactory.TypeScope, String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
+	public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
+		super( typeScope, role, propertyRef );
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		return new PersistentSet(session);
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Set.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSet( session, (java.util.Set) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0
 		       ? new HashSet()
 		       : new HashSet( anticipatedSize + (int)( anticipatedSize * .75f ), .75f );
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
index 172bcb2fd0..1087ea0add 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
@@ -1,70 +1,81 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeMap;
 
 import org.hibernate.collection.internal.PersistentSortedMap;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class SortedMapType extends MapType {
 
 	private final Comparator comparator;
 
+	/**
+	 * @deprecated Use {@link #SortedMapType(org.hibernate.type.TypeFactory.TypeScope, String, String, java.util.Comparator)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.comparator = comparator;
 	}
 
+	public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
+		super( typeScope, role, propertyRef );
+		this.comparator = comparator;
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		PersistentSortedMap map = new PersistentSortedMap(session);
 		map.setComparator(comparator);
 		return map;
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedMap.class;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Object instantiate(int anticipatedSize) {
 		return new TreeMap(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSortedMap( session, (java.util.SortedMap) collection );
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
index 8e78e05436..eeb6ff48bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
@@ -1,61 +1,72 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeSet;
 
 import org.hibernate.collection.internal.PersistentSortedSet;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SortedSetType extends SetType {
 	private final Comparator comparator;
 
+	/**
+	 * @deprecated Use {@link #SortedSetType(org.hibernate.type.TypeFactory.TypeScope, String, String, java.util.Comparator)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.comparator = comparator;
 	}
 
+	public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
+		super( typeScope, role, propertyRef );
+		this.comparator = comparator;
+	}
+
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		PersistentSortedSet set = new PersistentSortedSet(session);
 		set.setComparator(comparator);
 		return set;
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedSet.class;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Object instantiate(int anticipatedSize) {
 		return new TreeSet(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSortedSet( session, (java.util.SortedSet) collection );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/Type.java b/hibernate-core/src/main/java/org/hibernate/type/Type.java
index d2b3aa058d..3a75ac8b78 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
@@ -1,605 +1,618 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Node;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.metamodel.relational.Size;
 
 /**
  * Defines a mapping between a Java type and one or more JDBC {@linkplain java.sql.Types types}, as well
  * as describing the in-memory semantics of the given java type (how do we check it for 'dirtiness', how do
  * we copy values, etc).
  * <p/>
  * Application developers needing custom types can implement this interface (either directly or via subclassing an
  * existing impl) or by the (slightly more stable, though more limited) {@link org.hibernate.usertype.UserType}
  * interface.
  * <p/>
  * Implementations of this interface must certainly be thread-safe.  It is recommended that they be immutable as
  * well, though that is difficult to achieve completely given the no-arg constructor requirement for custom types.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Type extends Serializable {
 	/**
 	 * Return true if the implementation is castable to {@link AssociationType}. This does not necessarily imply that
 	 * the type actually represents an association.  Essentially a polymorphic version of
 	 * {@code (type instanceof AssociationType.class)}
 	 *
 	 * @return True if this type is also an {@link AssociationType} implementor; false otherwise.
 	 */
 	public boolean isAssociationType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CollectionType}. Essentially a polymorphic version of
 	 * {@code (type instanceof CollectionType.class)}
 	 * <p/>
 	 * A {@link CollectionType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link CollectionType} implementor; false otherwise.
 	 */
 	public boolean isCollectionType();
 
 	/**
 	 * Return true if the implementation is castable to {@link EntityType}. Essentially a polymorphic
 	 * version of {@code (type instanceof EntityType.class)}.
 	 * <p/>
 	 * An {@link EntityType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link EntityType} implementor; false otherwise.
 	 */
 	public boolean isEntityType();
 
 	/**
 	 * Return true if the implementation is castable to {@link AnyType}. Essentially a polymorphic
 	 * version of {@code (type instanceof AnyType.class)}.
 	 * <p/>
 	 * An {@link AnyType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link AnyType} implementor; false otherwise.
 	 */
 	public boolean isAnyType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CompositeType}. Essentially a polymorphic
 	 * version of {@code (type instanceof CompositeType.class)}.  A component type may own collections or
 	 * associations and hence must provide certain extra functionality.
 	 *
 	 * @return True if this type is also an {@link CompositeType} implementor; false otherwise.
 	 */
 	public boolean isComponentType();
 
 	/**
 	 * How many columns are used to persist this type.  Always the same as {@code sqlTypes(mapping).length}
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The number of columns
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int getColumnSpan(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the JDBC types codes (per {@link java.sql.Types}) for the columns mapped by this type.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The JDBC type codes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int[] sqlTypes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the column sizes dictated by this type.  For example, the mapping for a {@code char}/{@link Character} would
 	 * have a dictated length limit of 1; for a string-based {@link java.util.UUID} would have a size limit of 36; etc.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The dictated sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Defines the column sizes to use according to this type if the user did not explicitly say (and if no
 	 * {@link #dictatedSizes} were given).
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The default sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] defaultSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * The class returned by {@link #nullSafeGet} methods. This is used to  establish the class of an array of
 	 * this type.
 	 *
 	 * @return The java type class handled by this type.
 	 */
 	public Class getReturnedClass();
-	
+
+	/**
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
+	@Deprecated
 	public boolean isXMLElement();
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state) taking a shortcut for entity references.
 	 * <p/>
 	 * For most types this should equate to an {@link Object#equals equals} check on the values.  For associations
 	 * the implication is a bit different.  For most types it is conceivable to simply delegate to {@link #isEqual}
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered the same (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isSame(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param factory The session factory
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @param factory The session factory
 	 *
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) throws HibernateException;
 	
 	/**
 	 * Perform a {@link java.util.Comparator} style comparison between values
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return The comparison result.  See {@link java.util.Comparator#compare} for a discussion.
 	 */
 	public int compare(Object x, Object y);
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 * 
 	 * @param old the old value
 	 * @param current the current value
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 *
 	 * @param oldState the old value
 	 * @param currentState the current value
 	 * @param checkable An array of booleans indicating which columns making up the value are actually checkable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Has the value been modified compared to the current database state?  The difference between this
 	 * and the {@link #isDirty} methods is that here we need to account for "partially" built values.  This is really
 	 * only an issue with association types.  For most type implementations it is enough to simply delegate to
 	 * {@link #isDirty} here/
 	 *
 	 * @param dbState the database state, in a "hydrated" form, with identifiers unresolved
 	 * @param currentState the current state of the object
 	 * @param checkable which columns are actually updatable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field has been modified
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isModified(Object dbState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
 	 * should handle possibility of null values.
 	 *
 	 * @param rs The result set from which to extract value.
 	 * @param names the column names making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 *
 	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object) alternative, 2-phase property initialization
 	 */
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
 	 * should handle possibility of null values.  This form might be called if the type is known to be a
 	 * single-column type.
 	 *
 	 * @param rs The result set from which to extract value.
 	 * @param name the column name making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
 	 * statement, ignoring some columns as dictated by the 'settable' parameter.  Implementors should handle the
 	 * possibility of null values.  A multi-column type should bind parameters starting from <tt>index</tt>.
 	 *
 	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
 	 * @param index starting parameter bind index
 	 * @param settable an array indicating which columns to bind/ignore
 	 * @param session The originating session
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
 	 * statement.  Implementors should handle possibility of null values.  A multi-column type should bind parameters
 	 * starting from <tt>index</tt>.
 	 *
 	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
 	 * @param index starting parameter bind index
 	 * @param session The originating session
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Generate a representation of the value for logging purposes.
 	 *
 	 * @param value The value to be logged
 	 * @param factory The session factory
 	 *
 	 * @return The loggable representation
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * A representation of the value to be embedded in an XML element.
 	 *
 	 * @param node The XML node to which to write the value
 	 * @param value The value to write
 	 * @param factory The session factory
 	 *
 	 * @throws HibernateException An error from Hibernate
+	 *
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
+	@Deprecated
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Parse the XML representation of an instance.
 	 *
 	 * @param xml The XML node from which to read the value
 	 * @param factory The session factory
 	 *
 	 * @return an instance of the {@link #getReturnedClass() mapped class}
 	 *
 	 * @throws HibernateException An error from Hibernate
+	 *
+	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
+	@Deprecated
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException;
 
 	/**
 	 * Returns the abbreviated name of the type.
 	 *
 	 * @return String the Hibernate type name
 	 */
 	public String getName();
 
 	/**
 	 * Return a deep copy of the persistent state, stopping at entities and at collections.
 	 *
 	 * @param value The value to be copied
 	 * @param factory The session factory
 	 *
 	 * @return The deep copy
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Are objects of this type mutable. (With respect to the referencing object ...
 	 * entities and collections are considered immutable because they manage their
 	 * own internal state.)
 	 *
 	 * @return boolean
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Return a disassembled representation of the object.  This is the value Hibernate will use in second level
 	 * caching, so care should be taken to break values down to their simplest forms; for entities especially, this
 	 * means breaking them down into their constituent parts.
 	 *
 	 * @param value the value to cache
 	 * @param session the originating session
 	 * @param owner optional parent entity object (needed for collections)
 	 *
 	 * @return the disassembled, deep cloned state
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException;
 
 	/**
 	 * Reconstruct the object from its disassembled state.  This method is the reciprocal of {@link #disassemble}
 	 *
 	 * @param cached the disassembled state from the cache
 	 * @param session the originating session
 	 * @param owner the parent entity object
 	 *
 	 * @return the (re)assembled object
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Called before assembling a query result set from the query cache, to allow batch fetching
 	 * of entities missing from the second-level cache.
 	 *
 	 * @param cached The key
 	 * @param session The originating session
 	 */
 	public void beforeAssemble(Serializable cached, SessionImplementor session);
 
 	/**
 	 * Extract a value from the JDBC result set.  This is useful for 2-phase property initialization - the second
 	 * phase is a call to {@link #resolve}
 	 * This hydrated value will be either:<ul>
 	 *     <li>in the case of an entity or collection type, the key</li>
 	 *     <li>otherwise, the value itself</li>
 	 * </ul>
 	 * 
 	 * @param rs The JDBC result set
 	 * @param names the column names making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return An entity or collection key, or an actual value.
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 *
 	 * @see #resolve
 	 */
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * The second phase of 2-phase loading.  Only really pertinent for entities and collections.  Here we resolve the
 	 * identifier to an entity or collection instance
 	 * 
 	 * @param value an identifier or value returned by <tt>hydrate()</tt>
 	 * @param owner the parent entity
 	 * @param session the session
 	 * 
 	 * @return the given value, or the value associated with the identifier
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 *
 	 * @see #hydrate
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Given a hydrated, but unresolved value, return a value that may be used to reconstruct property-ref
 	 * associations.
 	 *
 	 * @param value The unresolved, hydrated value
 	 * @param session THe originating session
 	 * @param owner The value owner
 	 *
 	 * @return The semi-resolved value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * As part of 2-phase loading, when we perform resolving what is the resolved type for this type?  Generally
 	 * speaking the type and its semi-resolved type will be the same.  The main deviation from this is in the
 	 * case of an entity where the type would be the entity type and semi-resolved type would be its identifier type
 	 *
 	 * @param factory The session factory
 	 *
 	 * @return The semi-resolved type
 	 */
 	public Type getSemiResolvedType(SessionFactoryImplementor factory);
 
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @param session The originating session
 	 * @param owner The owner of the value
 	 * @param copyCache The cache of already copied/replaced values
 	 *
 	 * @return the value to be merged
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache) throws HibernateException;
 	
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @param session The originating session
 	 * @param owner The owner of the value
 	 * @param copyCache The cache of already copied/replaced values
 	 * @param foreignKeyDirection For associations, which direction does the foreign key point?
 	 *
 	 * @return the value to be merged
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
 			ForeignKeyDirection foreignKeyDirection) throws HibernateException;
 	
 	/**
 	 * Given an instance of the type, return an array of boolean, indicating
 	 * which mapped columns would be null.
 	 * 
 	 * @param value an instance of the type
 	 * @param mapping The mapping abstraction
 	 *
 	 * @return array indicating column nullness for a value instance
 	 */
 	public boolean[] toColumnNullness(Object value, Mapping mapping);
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
index 5086bd846e..131251349c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
@@ -1,328 +1,483 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.ParameterizedType;
 import org.hibernate.usertype.UserType;
 
 /**
  * Used internally to build instances of {@link Type}, specifically it builds instances of
  *
  *
  * Used internally to obtain instances of <tt>Type</tt>. Applications should use static methods
  * and constants on <tt>org.hibernate.Hibernate</tt>.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "unchecked" })
 public final class TypeFactory implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeFactory.class.getName());
 
 	private final TypeScopeImpl typeScope = new TypeScopeImpl();
 
 	public static interface TypeScope extends Serializable {
 		public SessionFactoryImplementor resolveFactory();
 	}
 
 	private static class TypeScopeImpl implements TypeFactory.TypeScope {
 		private SessionFactoryImplementor factory;
 
 		public void injectSessionFactory(SessionFactoryImplementor factory) {
 			if ( this.factory != null ) {
 				LOG.scopingTypesToSessionFactoryAfterAlreadyScoped( this.factory, factory );
 			}
 			else {
 				LOG.tracev( "Scoping types to session factory {0}", factory );
 			}
 			this.factory = factory;
 		}
 
 		public SessionFactoryImplementor resolveFactory() {
 			if ( factory == null ) {
 				throw new HibernateException( "SessionFactory for type scoping not yet known" );
 			}
 			return factory;
 		}
 	}
 
 	public void injectSessionFactory(SessionFactoryImplementor factory) {
 		typeScope.injectSessionFactory( factory );
 	}
 
 	public SessionFactoryImplementor resolveSessionFactory() {
 		return typeScope.resolveFactory();
 	}
 
 	public Type byClass(Class clazz, Properties parameters) {
 		if ( Type.class.isAssignableFrom( clazz ) ) {
 			return type( clazz, parameters );
 		}
 
 		if ( CompositeUserType.class.isAssignableFrom( clazz ) ) {
 			return customComponent( clazz, parameters );
 		}
 
 		if ( UserType.class.isAssignableFrom( clazz ) ) {
 			return custom( clazz, parameters );
 		}
 
 		if ( Lifecycle.class.isAssignableFrom( clazz ) ) {
 			// not really a many-to-one association *necessarily*
 			return manyToOne( clazz.getName() );
 		}
 
 		if ( Serializable.class.isAssignableFrom( clazz ) ) {
 			return serializable( clazz );
 		}
 
 		return null;
 	}
 
 	public Type type(Class<Type> typeClass, Properties parameters) {
 		try {
 			Type type = typeClass.newInstance();
 			injectParameters( type, parameters );
 			return type;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not instantiate Type: " + typeClass.getName(), e );
 		}
 	}
 
 	public static void injectParameters(Object type, Properties parameters) {
 		if ( ParameterizedType.class.isInstance( type ) ) {
 			( (ParameterizedType) type ).setParameterValues(parameters);
 		}
 		else if ( parameters!=null && !parameters.isEmpty() ) {
 			throw new MappingException( "type is not parameterized: " + type.getClass().getName() );
 		}
 	}
 
 	public CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters) {
 		return customComponent( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			CompositeUserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CompositeCustomType( userType );
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
+	/**
+	 * @deprecated Use {@link #customCollection(String, java.util.Properties, String, String)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType customCollection(
 			String typeName,
 			Properties typeParameters,
 			String role,
 			String propertyRef,
 			boolean embedded) {
 		Class typeClass;
 		try {
 			typeClass = ReflectHelper.classForName( typeName );
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
 		}
 		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef, embedded );
 		if ( typeParameters != null ) {
 			injectParameters( result.getUserType(), typeParameters );
 		}
 		return result;
 	}
 
+	public CollectionType customCollection(
+			String typeName,
+			Properties typeParameters,
+			String role,
+			String propertyRef) {
+		Class typeClass;
+		try {
+			typeClass = ReflectHelper.classForName( typeName );
+		}
+		catch ( ClassNotFoundException cnfe ) {
+			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
+		}
+		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef );
+		if ( typeParameters != null ) {
+			injectParameters( result.getUserType(), typeParameters );
+		}
+		return result;
+	}
+
 	public CustomType custom(Class<UserType> typeClass, Properties parameters) {
 		return custom( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
     public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			UserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CustomType( userType );
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
 	/**
 	 * Build a {@link SerializableType} from the given {@link Serializable} class.
 	 *
 	 * @param serializableClass The {@link Serializable} class.
 	 * @param <T> The actual class type (extends Serializable)
 	 *
 	 * @return The built {@link SerializableType}
 	 */
 	public static <T extends Serializable> SerializableType<T> serializable(Class<T> serializableClass) {
 		return new SerializableType<T>( serializableClass );
 	}
 
 
 	// one-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	/**
+	 * @deprecated Use {@link #oneToOne(String, ForeignKeyDirection, String, boolean, boolean, String, String)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public EntityType oneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
 		return new OneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
 				lazy, unwrapProxy, isEmbeddedInXML, entityName, propertyName );
 	}
 
+	public EntityType oneToOne(
+			String persistentClass,
+			ForeignKeyDirection foreignKeyType,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
+		return new OneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
+				lazy, unwrapProxy, entityName, propertyName );
+	}
+
 	public EntityType specialOneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		return new SpecialOneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
 				lazy, unwrapProxy, entityName, propertyName );
 	}
 
 
 	// many-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType manyToOne(String persistentClass) {
 		return new ManyToOneType( typeScope, persistentClass );
 	}
 
 	public EntityType manyToOne(String persistentClass, boolean lazy) {
 		return new ManyToOneType( typeScope, persistentClass, lazy );
 	}
 
+	/**
+	 * @deprecated Use {@link #manyToOne(String, String, boolean, boolean, boolean, boolean)}
+	 * instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public EntityType manyToOne(
 			String persistentClass,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		return new ManyToOneType(
 				typeScope,
 				persistentClass,
 				uniqueKeyPropertyName,
 				lazy,
 				unwrapProxy,
 				isEmbeddedInXML,
 				ignoreNotFound,
 				isLogicalOneToOne
 		);
 	}
 
+	public EntityType manyToOne(
+			String persistentClass,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			boolean ignoreNotFound,
+			boolean isLogicalOneToOne) {
+		return new ManyToOneType(
+				typeScope,
+				persistentClass,
+				uniqueKeyPropertyName,
+				lazy,
+				unwrapProxy,
+				ignoreNotFound,
+				isLogicalOneToOne
+		);
+	}
 
 	// collection type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	/**
+	 * @deprecated Use {@link #array(String, String, Class)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType array(String role, String propertyRef, boolean embedded, Class elementClass) {
 		return new ArrayType( typeScope, role, propertyRef, elementClass, embedded );
 	}
 
+	public CollectionType array(String role, String propertyRef, Class elementClass) {
+		return new ArrayType( typeScope, role, propertyRef, elementClass );
+	}
+
+	/**
+	 * @deprecated Use {@link #list(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType list(String role, String propertyRef, boolean embedded) {
 		return new ListType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType list(String role, String propertyRef) {
+		return new ListType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #bag(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType bag(String role, String propertyRef, boolean embedded) {
 		return new BagType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType bag(String role, String propertyRef) {
+		return new BagType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #idbag(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType idbag(String role, String propertyRef, boolean embedded) {
 		return new IdentifierBagType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType idbag(String role, String propertyRef) {
+		return new IdentifierBagType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #map(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType map(String role, String propertyRef, boolean embedded) {
 		return new MapType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType map(String role, String propertyRef) {
+		return new MapType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #orderedMap(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType orderedMap(String role, String propertyRef, boolean embedded) {
 		return new OrderedMapType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType orderedMap(String role, String propertyRef) {
+		return new OrderedMapType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #sortedMap(String, String, java.util.Comparator)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType sortedMap(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedMapType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
+	public CollectionType sortedMap(String role, String propertyRef, Comparator comparator) {
+		return new SortedMapType( typeScope, role, propertyRef, comparator );
+	}
+
+	/**
+	 * @deprecated Use {@link #set(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType set(String role, String propertyRef, boolean embedded) {
 		return new SetType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType set(String role, String propertyRef) {
+		return new SetType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #orderedSet(String, String)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType orderedSet(String role, String propertyRef, boolean embedded) {
 		return new OrderedSetType( typeScope, role, propertyRef, embedded );
 	}
 
+	public CollectionType orderedSet(String role, String propertyRef) {
+		return new OrderedSetType( typeScope, role, propertyRef );
+	}
+
+	/**
+	 * @deprecated Use {@link #sortedSet(String, String, java.util.Comparator)} instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
 	public CollectionType sortedSet(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedSetType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
+	public CollectionType sortedSet(String role, String propertyRef, Comparator comparator) {
+		return new SortedSetType( typeScope, role, propertyRef, comparator );
+	}
 
 	// component type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public ComponentType component(ComponentMetamodel metamodel) {
 		return new ComponentType( typeScope, metamodel );
 	}
 
 	public EmbeddedComponentType embeddedComponent(ComponentMetamodel metamodel) {
 		return new EmbeddedComponentType( typeScope, metamodel );
 	}
 
 
 	// any type builder ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Type any(Type metaType, Type identifierType) {
 		return new AnyType( metaType, identifierType );
 	}
 }
