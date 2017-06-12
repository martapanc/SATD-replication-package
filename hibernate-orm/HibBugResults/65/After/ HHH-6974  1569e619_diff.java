diff --git a/hibernate-core/src/main/java/org/hibernate/Cache.java b/hibernate-core/src/main/java/org/hibernate/Cache.java
index 4f0d5eb3d5..cf0e71deb7 100644
--- a/hibernate-core/src/main/java/org/hibernate/Cache.java
+++ b/hibernate-core/src/main/java/org/hibernate/Cache.java
@@ -1,164 +1,185 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
  */
 package org.hibernate;
 import java.io.Serializable;
 
 /**
  * Provides an API for querying/managing the second level cache regions.
  * <p/>
  * CAUTION: None of these methods respect any isolation or transactional
  * semantics associated with the underlying caches.  Specifically, evictions
  * perform an immediate "hard" removal outside any transactions and/or locking
  * scheme(s).
  *
  * @author Steve Ebersole
  */
 public interface Cache {
 	/**
 	 * Determine whether the cache contains data for the given entity "instance".
 	 * <p/>
 	 * The semantic here is whether the cache contains data visible for the
 	 * current call context.
 	 *
 	 * @param entityClass The entity class.
 	 * @param identifier The entity identifier
 	 *
 	 * @return True if the underlying cache contains corresponding data; false
 	 * otherwise.
 	 */
 	public boolean containsEntity(Class entityClass, Serializable identifier);
 
 	/**
 	 * Determine whether the cache contains data for the given entity "instance".
 	 * <p/>
 	 * The semantic here is whether the cache contains data visible for the
 	 * current call context.
 	 *
 	 * @param entityName The entity name.
 	 * @param identifier The entity identifier
 	 *
 	 * @return True if the underlying cache contains corresponding data; false otherwise.
 	 */
 	public boolean containsEntity(String entityName, Serializable identifier);
 
 	/**
 	 * Evicts the entity data for a particular entity "instance".
 	 *
 	 * @param entityClass The entity class.
 	 * @param identifier The entity identifier
 	 */
 	public void evictEntity(Class entityClass, Serializable identifier);
 
 	/**
 	 * Evicts the entity data for a particular entity "instance".
 	 *
 	 * @param entityName The entity name.
 	 * @param identifier The entity identifier
 	 */
 	public void evictEntity(String entityName, Serializable identifier);
 
 	/**
 	 * Evicts all entity data from the given region (i.e. for all entities of
 	 * type).
 	 *
 	 * @param entityClass The entity class.
 	 */
 	public void evictEntityRegion(Class entityClass);
 
 	/**
 	 * Evicts all entity data from the given region (i.e. for all entities of
 	 * type).
 	 *
 	 * @param entityName The entity name.
 	 */
 	public void evictEntityRegion(String entityName);
 
 	/**
 	 * Evict data from all entity regions.
 	 */
 	public void evictEntityRegions();
 
 	/**
+	 * Evicts all naturalId data from the given region (i.e. for all entities of
+	 * type).
+	 *
+	 * @param naturalIdClass The naturalId class.
+	 */
+	public void evictNaturalIdRegion(Class naturalIdClass);
+
+	/**
+	 * Evicts all naturalId data from the given region (i.e. for all entities of
+	 * type).
+	 *
+	 * @param naturalIdName The naturalId name.
+	 */
+	public void evictNaturalIdRegion(String naturalIdName);
+
+	/**
+	 * Evict data from all naturalId regions.
+	 */
+	public void evictNaturalIdRegions();
+
+	/**
 	 * Determine whether the cache contains data for the given collection.
 	 * <p/>
 	 * The semantic here is whether the cache contains data visible for the
 	 * current call context.
 	 *
 	 * @param role The name of the collection role (in form
 	 * [owner-entity-name].[collection-property-name]) whose regions should be
 	 * evicted.
 	 * @param ownerIdentifier The identifier of the owning entity
 	 *
 	 * @return True if the underlying cache contains corresponding data; false otherwise.
 	 */
 	public boolean containsCollection(String role, Serializable ownerIdentifier);
 
 	/**
 	 * Evicts the cache data for the given identified collection instance.
 	 *
 	 * @param role The "collection role" (in form [owner-entity-name].[collection-property-name]).
 	 * @param ownerIdentifier The identifier of the owning entity
 	 */
 	public void evictCollection(String role, Serializable ownerIdentifier);
 
 	/**
 	 * Evicts all entity data from the given region (i.e. evicts cached data
 	 * for all of the specified c9ollection role).
 	 *
 	 * @param role The "collection role" (in form [owner-entity-name].[collection-property-name]).
 	 */
 	public void evictCollectionRegion(String role);
 
 	/**
 	 * Evict data from all collection regions.
 	 */
 	public void evictCollectionRegions();
 
 	/**
 	 * Determine whether the cache contains data for the given query.
 	 * <p/>
 	 * The semantic here is whether the cache contains any data for the given
 	 * region name since query result caches are not transactionally isolated.
 	 *
 	 * @param regionName The cache name given to the query.
 	 *
 	 * @return True if the underlying cache contains corresponding data; false otherwise.
 	 */
 	public boolean containsQuery(String regionName);
 
 	/**
 	 * Evicts all cached query results from the default region.
 	 */
 	public void evictDefaultQueryRegion();
 
 	/**
 	 * Evicts all cached query results under the given name.
 	 *
 	 * @param regionName The cache name associated to the queries being cached.
 	 */
 	public void evictQueryRegion(String regionName);
 
 	/**
 	 * Evict data from all query regions.
 	 */
 	public void evictQueryRegions();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
index 05c25aff32..be9645687e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
@@ -1,147 +1,158 @@
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
-import java.util.Arrays;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Allows multiple entity classes / collection roles to be
  * stored in the same cache region. Also allows for composite
  * keys which do not properly implement equals()/hashCode().
  *
  * @author Gavin King
  */
 public class NaturalIdCacheKey implements Serializable {
 	private final Serializable[] naturalId;
-	private final Type[] types;
+	private final Type[] naturalIdTypes;
 	private final String entityName;
 	private final String tenantId;
 	private final int hashCode;
 	private final String toString;
 
 	/**
 	 * Construct a new key for a collection or entity instance.
 	 * Note that an entity name should always be the root entity
 	 * name, not a subclass entity name.
 	 *
 	 * @param naturalId The naturalId associated with the cached data
-	 * @param types The Hibernate type mappings
-	 * @param entityOrRoleName The entity name.
-	 * @param tenantId The tenant identifier associated this data.
-	 * @param factory The session factory for which we are caching
+	 * @param persister The persister for the entity
+	 * @param session The session for which we are caching
 	 */
 	public NaturalIdCacheKey(
-			final Serializable[] naturalId,
-			final Type[] types,
-			final String entityOrRoleName,
-			final String tenantId,
-			final SessionFactoryImplementor factory) {
+			final Object[] naturalId,
+			final EntityPersister persister,
+			final SessionImplementor session) {
 		
-		this.naturalId = naturalId;
-		this.types = types;
-		this.entityName = entityOrRoleName;
-		this.tenantId = tenantId;
+		this.entityName = persister.getEntityName();
+		this.tenantId = session.getTenantIdentifier();
 		
-		this.hashCode = this.generateHashCode( this.naturalId, this.types, factory );
-		this.toString = entityOrRoleName + "##NaturalId" + Arrays.toString( this.naturalId );
-	}
-	
-	private int generateHashCode(final Serializable[] naturalId, final Type[] types,
-			final SessionFactoryImplementor factory) {
+		final Serializable[] disassembledNaturalId = new Serializable[naturalId.length];
+		final Type[] naturalIdTypes = new Type[naturalId.length];
+		final StringBuilder str = new StringBuilder(entityName).append( "##NaturalId[" );
 		
+		
+		final SessionFactoryImplementor factory = session.getFactory();
+		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
+		final Type[] propertyTypes = persister.getPropertyTypes();
+
 		final int prime = 31;
 		int result = 1;
-		result = prime * result + ( ( entityName == null ) ? 0 : entityName.hashCode() );
-		result = prime * result + ( ( tenantId == null ) ? 0 : tenantId.hashCode() );
+		result = prime * result + ( ( this.entityName == null ) ? 0 : this.entityName.hashCode() );
+		result = prime * result + ( ( this.tenantId == null ) ? 0 : this.tenantId.hashCode() );
 		for ( int i = 0; i < naturalId.length; i++ ) {
-			result = prime * result + types[i].getHashCode( naturalId[i], factory );
+			final Type type = propertyTypes[naturalIdPropertyIndexes[i]];
+			final Object value = naturalId[i];
+			
+			result = prime * result + type.getHashCode( value, factory );
+			
+			disassembledNaturalId[i] = type.disassemble( value, session, null );
+			
+			naturalIdTypes[i] = type;
+			
+			str.append( type.toLoggableString( value, factory ) );
+			if (i + 1 < naturalId.length) {
+				str.append( ", " );
+			}
 		}
-		return result;
+		str.append( "]" );
+		
+		this.naturalId = disassembledNaturalId;
+		this.naturalIdTypes = naturalIdTypes;
+		this.hashCode = result;
+		this.toString = str.toString();
 	}
 
-	@Override
-	public String toString() {
-		// Mainly for OSCache
-		return this.toString;
+	public String getEntityName() {
+		return entityName;
+	}
+	
+	public String getTenantId() {
+		return tenantId;
 	}
 	
 	public Serializable[] getNaturalId() {
 		return naturalId;
 	}
 
-	public Type[] getTypes() {
-		return types;
+	@Override
+	public String toString() {
+		return this.toString;
 	}
-
+	
 	@Override
 	public int hashCode() {
 		return this.hashCode;
 	}
 
 	@Override
 	public boolean equals(Object obj) {
 		if ( this == obj )
 			return true;
 		if ( obj == null )
 			return false;
 		if ( getClass() != obj.getClass() )
 			return false;
 		NaturalIdCacheKey other = (NaturalIdCacheKey) obj;
-		if (this.hashCode != other.hashCode) {
-			//Short circuit on hashCode, if they aren't equal there is no chance the keys are equal
-			return false;
-		}
 		if ( entityName == null ) {
 			if ( other.entityName != null )
 				return false;
 		}
 		else if ( !entityName.equals( other.entityName ) )
 			return false;
-		if ( !Arrays.equals( naturalId, other.naturalId ) )
-			return false;
 		if ( tenantId == null ) {
 			if ( other.tenantId != null )
 				return false;
 		}
 		else if ( !tenantId.equals( other.tenantId ) )
 			return false;
-		
-		for ( int i = 0; i < naturalId.length; i++ ) {
-			if ( !types[i].isEqual( naturalId[i], other.naturalId[i] ) ) {
+		if ( naturalId == other.naturalId )
+			return true;
+		if ( naturalId == null || other.naturalId == null )
+			return false;
+		int length = naturalId.length;
+		if ( other.naturalId.length != length )
+			return false;
+		for ( int i = 0; i < length; i++ ) {
+			if ( !this.naturalIdTypes[i].isEqual( naturalId[i], other.naturalId[i] ) ) {
 				return false;
 			}
 		}
-		
 		return true;
 	}
-
-	public String getEntityOrRoleName() {
-		return entityName;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 6785739002..5515fa5d30 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1578 +1,1578 @@
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
 import org.hibernate.annotations.Source;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.TypeDef;
 import org.hibernate.annotations.TypeDefs;
 import org.hibernate.annotations.Where;
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
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Component;
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
 		{
 			List<SqlResultSetMapping> anns = ( List<SqlResultSetMapping> ) defaults.get( SqlResultSetMapping.class );
 			if ( anns != null ) {
 				for ( SqlResultSetMapping ann : anns ) {
 					QueryBinder.bindSqlResultsetMapping( ann, mappings, true );
 				}
 			}
 		}
 	}
 
 	public static void bindPackage(String packageName, Mappings mappings) {
 		XPackage pckg;
 		try {
 			pckg = mappings.getReflectionManager().packageForName( packageName );
 		}
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
 		if ( clazzToProcess.isAnnotationPresent( javax.persistence.Table.class ) ) {
 			javax.persistence.Table tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
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
-	    entityBinder.setNaturalIdCache( clazzToProcess.getAnnotation( NaturalIdCache.class ) );
+	    entityBinder.setNaturalIdCache( clazzToProcess, clazzToProcess.getAnnotation( NaturalIdCache.class ) );
 
 		//Filters are not allowed on subclasses
 		if ( !inheritanceState.hasParents() ) {
 			bindFilters( clazzToProcess, entityBinder, mappings );
 		}
 
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
 							( Map<String, Join> ) null, ( PropertyHolder ) null, mappings
 					);
 				}
 			}
 			else {
 				PrimaryKeyJoinColumn jcAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumn.class );
 				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
 				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						jcAnn, null, superEntity.getIdentifier(),
 						( Map<String, Join> ) null, ( PropertyHolder ) null, mappings
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
 				entityBinder.addFilter( filter.name(), filter.condition() );
 			}
 		}
 
 		Filter filterAnn = annotatedElement.getAnnotation( Filter.class );
 		if ( filterAnn != null ) {
 			entityBinder.addFilter( filterAnn.name(), filterAnn.condition() );
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
 		Map<String, org.hibernate.type.Type> params = new HashMap<String, org.hibernate.type.Type>();
 		for ( ParamDef param : defAnn.parameters() ) {
 			params.put( param.name(), mappings.getTypeResolver().heuristicType( param.type() ) );
 		}
 		FilterDefinition def = new FilterDefinition( defAnn.name(), defAnn.defaultCondition(), params );
 		LOG.debugf( "Binding filter definition: %s", def.getFilterName() );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void bindTypeDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		TypeDef defAnn = annotatedElement.getAnnotation( TypeDef.class );
 		TypeDefs defsAnn = annotatedElement.getAnnotation( TypeDefs.class );
 		if ( defAnn != null ) {
 			bindTypeDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( TypeDef def : defsAnn.value() ) {
 				bindTypeDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfiles(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FetchProfile fetchProfileAnnotation = annotatedElement.getAnnotation( FetchProfile.class );
 		FetchProfiles fetchProfileAnnotations = annotatedElement.getAnnotation( FetchProfiles.class );
 		if ( fetchProfileAnnotation != null ) {
 			bindFetchProfile( fetchProfileAnnotation, mappings );
 		}
 		if ( fetchProfileAnnotations != null ) {
 			for ( FetchProfile profile : fetchProfileAnnotations.value() ) {
 				bindFetchProfile( profile, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfile(FetchProfile fetchProfileAnnotation, Mappings mappings) {
 		for ( FetchProfile.FetchOverride fetch : fetchProfileAnnotation.fetchOverrides() ) {
 			org.hibernate.annotations.FetchMode mode = fetch.mode();
 			if ( !mode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 
 			SecondPass sp = new VerifyFetchProfileReferenceSecondPass( fetchProfileAnnotation.name(), fetch, mappings );
 			mappings.addSecondPass( sp );
 		}
 	}
 
 	private static void bindTypeDef(TypeDef defAnn, Mappings mappings) {
 		Properties params = new Properties();
 		for ( Parameter param : defAnn.parameters() ) {
 			params.setProperty( param.name(), param.value() );
 		}
 
 		if ( BinderHelper.isEmptyAnnotationValue( defAnn.name() ) && defAnn.defaultForType().equals( void.class ) ) {
 			throw new AnnotationException(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass " +
 							defAnn.typeClass().getName()
 			);
 		}
 
 		final String typeBindMessageF = "Binding type definition: %s";
 		if ( !BinderHelper.isEmptyAnnotationValue( defAnn.name() ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.name() );
 			}
 			mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
 		}
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.defaultForType().getName() );
 			}
 			mappings.addTypeDef( defAnn.defaultForType().getName(), defAnn.typeClass().getName(), params );
 		}
 
 	}
 
 
 	private static void bindDiscriminatorToPersistentClass(
 			RootClass rootClass,
 			Ejb3DiscriminatorColumn discriminatorColumn,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			Mappings mappings) {
 		if ( rootClass.getDiscriminator() == null ) {
 			if ( discriminatorColumn == null ) {
 				throw new AssertionFailure( "discriminator column should have been built" );
 			}
 			discriminatorColumn.setJoins( secondaryTables );
 			discriminatorColumn.setPropertyHolder( propertyHolder );
 			SimpleValue discrim = new SimpleValue( mappings, rootClass.getTable() );
 			rootClass.setDiscriminator( discrim );
 			discriminatorColumn.linkWithValue( discrim );
 			discrim.setTypeName( discriminatorColumn.getDiscriminatorTypeName() );
 			rootClass.setPolymorphic( true );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Setting discriminator for entity {0}", rootClass.getEntityName() );
 			}
 		}
 	}
 
 	/**
 	 * @param elements List of {@code ProperyData} instances
 	 * @param defaultAccessType The default value access strategy which has to be used in case no explicit local access
 	 * strategy is used
 	 * @param propertyContainer Metadata about a class and its properties
 	 * @param mappings Mapping meta data
 	 *
 	 * @return the number of id properties found while iterating the elements of {@code annotatedClass} using
 	 *         the determined access strategy, {@code false} otherwise.
 	 */
 	static int addElementsOfClass(
 			List<PropertyData> elements,
 			AccessType defaultAccessType,
 			PropertyContainer propertyContainer,
 			Mappings mappings) {
 		int idPropertyCounter = 0;
 		AccessType accessType = defaultAccessType;
 
 		if ( propertyContainer.hasExplicitAccessStrategy() ) {
 			accessType = propertyContainer.getExplicitAccessStrategy();
 		}
 
 		Collection<XProperty> properties = propertyContainer.getProperties( accessType );
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
 						if ( prop.isAnnotationPresent( JoinColumn.class )
 								&& prop.getAnnotation( JoinColumn.class ).name().equals( columnName )
 								&& !prop.isAnnotationPresent( MapsId.class ) ) {
 							//create a PropertyData fpr the specJ property holding the mapping
 							PropertyData specJPropertyData = new PropertyInferredData(
 									declaringClass,  //same dec
 									prop, // the actual @XToOne property
 									propertyAccessor, //TODO we should get the right accessor but the same as id would do
 									mappings.getReflectionManager()
 							);
 							mappings.addPropertyAnnotatedWithMapsIdSpecj( entity, specJPropertyData, element.toString() );
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
 
 		if ( LOG.isTraceEnabled() ) {
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
 			if ( LOG.isTraceEnabled() ) {
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
 			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
 			if ( LOG.isTraceEnabled() ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index ae7d076356..fa06ed925d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,965 +1,965 @@
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
 import java.util.List;
 import java.util.Map;
 
 import javax.persistence.Access;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.NaturalIdCache;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.RowId;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Subselect;
 import org.hibernate.annotations.Synchronize;
 import org.hibernate.annotations.Tables;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
 import org.jboss.logging.Logger;
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
     private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
 	
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
 	private Mappings mappings;
 	private String discriminatorValue = "";
 	private Boolean forceDiscriminator;
 	private Boolean insertableDiscriminator;
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private boolean explicitHibernateEntityAnnotation;
 	private OptimisticLockType optimisticLockType;
 	private PolymorphismType polymorphismType;
 	private boolean selectBeforeUpdate;
 	private int batchSize;
 	private boolean lazy;
 	private XClass proxyClass;
 	private String where;
 	private java.util.Map<String, Join> secondaryTables = new HashMap<String, Join>();
 	private java.util.Map<String, Object> secondaryTableJoins = new HashMap<String, Object>();
 	private String cacheConcurrentStrategy;
 	private String cacheRegion;
 	private String naturalIdCacheRegion;
 	private java.util.Map<String, String> filters = new HashMap<String, String>();
 	private InheritanceState inheritanceState;
 	private boolean ignoreIdAnnotations;
 	private boolean cacheLazyProperty;
 	private AccessType propertyAccessType = AccessType.DEFAULT;
 	private boolean wrapIdsInEmbeddedComponents;
 	private String subselect;
 
 	public boolean wrapIdsInEmbeddedComponents() {
 		return wrapIdsInEmbeddedComponents;
 	}
 
 	/**
 	 * Use as a fake one for Collection of elements
 	 */
 	public EntityBinder() {
 	}
 
 	public EntityBinder(
 			Entity ejb3Ann,
 			org.hibernate.annotations.Entity hibAnn,
 			XClass annotatedClass,
 			PersistentClass persistentClass,
 			Mappings mappings) {
 		this.mappings = mappings;
 		this.persistentClass = persistentClass;
 		this.annotatedClass = annotatedClass;
 		bindEjb3Annotation( ejb3Ann );
 		bindHibernateAnnotation( hibAnn );
 	}
 
 	private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
 		if ( hibAnn != null ) {
 			dynamicInsert = hibAnn.dynamicInsert();
 			dynamicUpdate = hibAnn.dynamicUpdate();
 			optimisticLockType = hibAnn.optimisticLock();
 			selectBeforeUpdate = hibAnn.selectBeforeUpdate();
 			polymorphismType = hibAnn.polymorphism();
 			explicitHibernateEntityAnnotation = true;
 			//persister handled in bind
 		}
 		else {
 			//default values when the annotation is not there
 			dynamicInsert = false;
 			dynamicUpdate = false;
 			optimisticLockType = OptimisticLockType.VERSION;
 			polymorphismType = PolymorphismType.IMPLICIT;
 			selectBeforeUpdate = false;
 		}
 	}
 
 	private void bindEjb3Annotation(Entity ejb3Ann) {
 		if ( ejb3Ann == null ) throw new AssertionFailure( "@Entity should always be not null" );
 		if ( BinderHelper.isEmptyAnnotationValue( ejb3Ann.name() ) ) {
 			name = StringHelper.unqualify( annotatedClass.getName() );
 		}
 		else {
 			name = ejb3Ann.name();
 		}
 	}
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setForceDiscriminator(boolean forceDiscriminator) {
 		this.forceDiscriminator = forceDiscriminator;
 	}
 
 	public void setInsertableDiscriminator(boolean insertableDiscriminator) {
 		this.insertableDiscriminator = insertableDiscriminator;
 	}
 
 	public void bindEntity() {
 		persistentClass.setAbstract( annotatedClass.isAbstract() );
 		persistentClass.setClassName( annotatedClass.getName() );
 		persistentClass.setNodeName( name );
 		persistentClass.setJpaEntityName(name);
 		//persistentClass.setDynamic(false); //no longer needed with the Entity name refactoring?
 		persistentClass.setEntityName( annotatedClass.getName() );
 		bindDiscriminatorValue();
 
 		persistentClass.setLazy( lazy );
 		if ( proxyClass != null ) {
 			persistentClass.setProxyInterfaceName( proxyClass.getName() );
 		}
 		persistentClass.setDynamicInsert( dynamicInsert );
 		persistentClass.setDynamicUpdate( dynamicUpdate );
 
 		if ( persistentClass instanceof RootClass ) {
 			RootClass rootClass = (RootClass) persistentClass;
 			boolean mutable = true;
 			//priority on @Immutable, then @Entity.mutable()
 			if ( annotatedClass.isAnnotationPresent( Immutable.class ) ) {
 				mutable = false;
 			}
 			else {
 				org.hibernate.annotations.Entity entityAnn =
 						annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 				if ( entityAnn != null ) {
 					mutable = entityAnn.mutable();
 				}
 			}
 			rootClass.setMutable( mutable );
 			rootClass.setExplicitPolymorphism( isExplicitPolymorphism( polymorphismType ) );
 			if ( StringHelper.isNotEmpty( where ) ) rootClass.setWhere( where );
 			if ( cacheConcurrentStrategy != null ) {
 				rootClass.setCacheConcurrencyStrategy( cacheConcurrentStrategy );
 				rootClass.setCacheRegionName( cacheRegion );
 				rootClass.setLazyPropertiesCacheable( cacheLazyProperty );
 			}
 			rootClass.setNaturalIdCacheRegionName( naturalIdCacheRegion );
 			boolean forceDiscriminatorInSelects = forceDiscriminator == null
 					? mappings.forceDiscriminatorInSelectsByDefault()
 					: forceDiscriminator;
 			rootClass.setForceDiscriminator( forceDiscriminatorInSelects );
 			if( insertableDiscriminator != null) {
 				rootClass.setDiscriminatorInsertable( insertableDiscriminator );
 			}
 		}
 		else {
             if (explicitHibernateEntityAnnotation) {
 				LOG.entityAnnotationOnNonRoot(annotatedClass.getName());
 			}
             if (annotatedClass.isAnnotationPresent(Immutable.class)) {
 				LOG.immutableAnnotationOnNonRoot(annotatedClass.getName());
 			}
 		}
 		persistentClass.setOptimisticLockMode( getVersioning( optimisticLockType ) );
 		persistentClass.setSelectBeforeUpdate( selectBeforeUpdate );
 
 		//set persister if needed
 		Persister persisterAnn = annotatedClass.getAnnotation( Persister.class );
 		Class persister = null;
 		if ( persisterAnn != null ) {
 			persister = persisterAnn.impl();
 		}
 		else {
 			org.hibernate.annotations.Entity entityAnn = annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 			if ( entityAnn != null && !BinderHelper.isEmptyAnnotationValue( entityAnn.persister() ) ) {
 				try {
 					persister = ReflectHelper.classForName( entityAnn.persister() );
 				}
 				catch (ClassNotFoundException cnfe) {
 					throw new AnnotationException( "Could not find persister class: " + persister );
 				}
 			}
 		}
 		if ( persister != null ) {
 			persistentClass.setEntityPersisterClass( persister );
 		}
 
 		persistentClass.setBatchSize( batchSize );
 
 		//SQL overriding
 		SQLInsert sqlInsert = annotatedClass.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = annotatedClass.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = annotatedClass.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = annotatedClass.getAnnotation( SQLDeleteAll.class );
 		Loader loader = annotatedClass.getAnnotation( Loader.class );
 
 		if ( sqlInsert != null ) {
 			persistentClass.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			persistentClass.setLoaderName( loader.namedQuery() );
 		}
 
 		if ( annotatedClass.isAnnotationPresent( Synchronize.class )) {
 			Synchronize synchronizedWith = annotatedClass.getAnnotation(Synchronize.class);
 
 			String [] tables = synchronizedWith.value();
 			for (String table : tables) {
 				persistentClass.addSynchronizedTable(table);
 			}
 		}
 
 		if ( annotatedClass.isAnnotationPresent(Subselect.class )) {
 			Subselect subselect = annotatedClass.getAnnotation(Subselect.class);
 			this.subselect = subselect.value();
 		}
 
 		//tuplizers
 		if ( annotatedClass.isAnnotationPresent( Tuplizers.class ) ) {
 			for (Tuplizer tuplizer : annotatedClass.getAnnotation( Tuplizers.class ).value()) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				//todo tuplizer.entityModeType
 				persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( annotatedClass.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = annotatedClass.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 
 		if ( !inheritanceState.hasParents() ) {
 			for ( Map.Entry<String, String> filter : filters.entrySet() ) {
 				String filterName = filter.getKey();
 				String cond = filter.getValue();
 				if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 					FilterDefinition definition = mappings.getFilterDefinition( filterName );
 					cond = definition == null ? null : definition.getDefaultFilterCondition();
 					if ( StringHelper.isEmpty( cond ) ) {
 						throw new AnnotationException(
 								"no filter condition found for filter " + filterName + " in " + this.name
 						);
 					}
 				}
 				persistentClass.addFilter( filterName, cond );
 			}
 		}
 		else if ( filters.size() > 0 ) {
 			LOG.filterAnnotationOnSubclass( persistentClass.getEntityName() );
 		}
 		LOG.debugf( "Import with entity name %s", name );
 		try {
 			mappings.addImport( persistentClass.getEntityName(), name );
 			String entityName = persistentClass.getEntityName();
 			if ( !entityName.equals( name ) ) {
 				mappings.addImport( entityName, entityName );
 			}
 		}
 		catch (MappingException me) {
 			throw new AnnotationException( "Use of the same entity name twice: " + name, me );
 		}
 	}
 
 	public void bindDiscriminatorValue() {
 		if ( StringHelper.isEmpty( discriminatorValue ) ) {
 			Value discriminator = persistentClass.getDiscriminator();
 			if ( discriminator == null ) {
 				persistentClass.setDiscriminatorValue( name );
 			}
 			else if ( "character".equals( discriminator.getType().getName() ) ) {
 				throw new AnnotationException(
 						"Using default @DiscriminatorValue for a discriminator of type CHAR is not safe"
 				);
 			}
 			else if ( "integer".equals( discriminator.getType().getName() ) ) {
 				persistentClass.setDiscriminatorValue( String.valueOf( name.hashCode() ) );
 			}
 			else {
 				persistentClass.setDiscriminatorValue( name ); //Spec compliant
 			}
 		}
 		else {
 			//persistentClass.getDiscriminator()
 			persistentClass.setDiscriminatorValue( discriminatorValue );
 		}
 	}
 
 	int getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return Versioning.OPTIMISTIC_LOCK_VERSION;
 			case NONE:
 				return Versioning.OPTIMISTIC_LOCK_NONE;
 			case DIRTY:
 				return Versioning.OPTIMISTIC_LOCK_DIRTY;
 			case ALL:
 				return Versioning.OPTIMISTIC_LOCK_ALL;
 			default:
 				throw new AssertionFailure( "optimistic locking not supported: " + type );
 		}
 	}
 
 	private boolean isExplicitPolymorphism(PolymorphismType type) {
 		switch ( type ) {
 			case IMPLICIT:
 				return false;
 			case EXPLICIT:
 				return true;
 			default:
 				throw new AssertionFailure( "Unknown polymorphism type: " + type );
 		}
 	}
 
 	public void setBatchSize(BatchSize sizeAnn) {
 		if ( sizeAnn != null ) {
 			batchSize = sizeAnn.size();
 		}
 		else {
 			batchSize = -1;
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void setProxy(Proxy proxy) {
 		if ( proxy != null ) {
 			lazy = proxy.lazy();
 			if ( !lazy ) {
 				proxyClass = null;
 			}
 			else {
 				if ( AnnotationBinder.isDefault(
 						mappings.getReflectionManager().toXClass( proxy.proxyClass() ), mappings
 				) ) {
 					proxyClass = annotatedClass;
 				}
 				else {
 					proxyClass = mappings.getReflectionManager().toXClass( proxy.proxyClass() );
 				}
 			}
 		}
 		else {
 			lazy = true; //needed to allow association lazy loading.
 			proxyClass = annotatedClass;
 		}
 	}
 
 	public void setWhere(Where whereAnn) {
 		if ( whereAnn != null ) {
 			where = whereAnn.clause();
 		}
 	}
 
 	public void setWrapIdsInEmbeddedComponents(boolean wrapIdsInEmbeddedComponents) {
 		this.wrapIdsInEmbeddedComponents = wrapIdsInEmbeddedComponents;
 	}
 
 
 	private static class EntityTableObjectNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private EntityTableObjectNameSource(String explicitName, String entityName) {
 			this.explicitName = explicitName;
 			this.logicalName = StringHelper.isNotEmpty( explicitName )
 					? explicitName
 					: StringHelper.unqualify( entityName );
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	private static class EntityTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		private final String entityName;
 
 		private EntityTableNamingStrategyHelper(String entityName) {
 			this.entityName = entityName;
 		}
 
 		public String determineImplicitName(NamingStrategy strategy) {
 			return strategy.classToTableName( entityName );
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperclassTable) {
 		EntityTableObjectNameSource tableNameContext = new EntityTableObjectNameSource( tableName, name );
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper( name );
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				tableNameContext,
 				namingStrategyHelper,
 				persistentClass.isAbstract(),
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperclassTable,
 				mappings,
 				this.subselect
 		);
 		final RowId rowId = annotatedClass.getAnnotation( RowId.class );
 		if ( rowId != null ) {
 			table.setRowId( rowId.value() );
 		}
 
 		if ( persistentClass instanceof TableOwner ) {
 			LOG.debugf( "Bind entity %s on table %s", persistentClass.getEntityName(), table.getName() );
 			( (TableOwner) persistentClass ).setTable( table );
 		}
 		else {
 			throw new AssertionFailure( "binding a table for a subclass" );
 		}
 	}
 
 	public void finalSecondaryTableBinding(PropertyHolder propertyHolder) {
 		/*
 		 * Those operations has to be done after the id definition of the persistence class.
 		 * ie after the properties parsing
 		 */
 		Iterator joins = secondaryTables.values().iterator();
 		Iterator joinColumns = secondaryTableJoins.values().iterator();
 
 		while ( joins.hasNext() ) {
 			Object uncastedColumn = joinColumns.next();
 			Join join = (Join) joins.next();
 			createPrimaryColumnsToSecondaryTable( uncastedColumn, propertyHolder, join );
 		}
 		mappings.addJoins( persistentClass, secondaryTables );
 	}
 
 	private void createPrimaryColumnsToSecondaryTable(Object uncastedColumn, PropertyHolder propertyHolder, Join join) {
 		Ejb3JoinColumn[] ejb3JoinColumns;
 		PrimaryKeyJoinColumn[] pkColumnsAnn = null;
 		JoinColumn[] joinColumnsAnn = null;
 		if ( uncastedColumn instanceof PrimaryKeyJoinColumn[] ) {
 			pkColumnsAnn = (PrimaryKeyJoinColumn[]) uncastedColumn;
 		}
 		if ( uncastedColumn instanceof JoinColumn[] ) {
 			joinColumnsAnn = (JoinColumn[]) uncastedColumn;
 		}
 		if ( pkColumnsAnn == null && joinColumnsAnn == null ) {
 			ejb3JoinColumns = new Ejb3JoinColumn[1];
 			ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 					null,
 					null,
 					persistentClass.getIdentifier(),
 					secondaryTables,
 					propertyHolder, mappings
 			);
 		}
 		else {
 			int nbrOfJoinColumns = pkColumnsAnn != null ?
 					pkColumnsAnn.length :
 					joinColumnsAnn.length;
 			if ( nbrOfJoinColumns == 0 ) {
 				ejb3JoinColumns = new Ejb3JoinColumn[1];
 				ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						null,
 						null,
 						persistentClass.getIdentifier(),
 						secondaryTables,
 						propertyHolder, mappings
 				);
 			}
 			else {
 				ejb3JoinColumns = new Ejb3JoinColumn[nbrOfJoinColumns];
 				if ( pkColumnsAnn != null ) {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								pkColumnsAnn[colIndex],
 								null,
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder, mappings
 						);
 					}
 				}
 				else {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								null,
 								joinColumnsAnn[colIndex],
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder, mappings
 						);
 					}
 				}
 			}
 		}
 
 		for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
 			joinColumn.forceNotNull();
 		}
 		bindJoinToPersistentClass( join, ejb3JoinColumns, mappings );
 	}
 
 	private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, Mappings mappings) {
 		SimpleValue key = new DependantValue( mappings, join.getTable(), persistentClass.getIdentifier() );
 		join.setKey( key );
 		setFKNameIfDefined( join );
 		key.setCascadeDeleteEnabled( false );
 		TableBinder.bindFk( persistentClass, null, ejb3JoinColumns, key, false, mappings );
 		join.createPrimaryKey();
 		join.createForeignKey();
 		persistentClass.addJoin( join );
 	}
 
 	private void setFKNameIfDefined(Join join) {
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null && !BinderHelper.isEmptyAnnotationValue( matchingTable.foreignKey().name() ) ) {
 			( (SimpleValue) join.getKey() ).setForeignKeyName( matchingTable.foreignKey().name() );
 		}
 	}
 
 	private org.hibernate.annotations.Table findMatchingComplimentTableAnnotation(Join join) {
 		String tableName = join.getTable().getQuotedName();
 		org.hibernate.annotations.Table table = annotatedClass.getAnnotation( org.hibernate.annotations.Table.class );
 		org.hibernate.annotations.Table matchingTable = null;
 		if ( table != null && tableName.equals( table.appliesTo() ) ) {
 			matchingTable = table;
 		}
 		else {
 			Tables tables = annotatedClass.getAnnotation( Tables.class );
 			if ( tables != null ) {
 				for (org.hibernate.annotations.Table current : tables.value()) {
 					if ( tableName.equals( current.appliesTo() ) ) {
 						matchingTable = current;
 						break;
 					}
 				}
 			}
 		}
 		return matchingTable;
 	}
 
 	public void firstLevelSecondaryTablesBinding(
 			SecondaryTable secTable, SecondaryTables secTables
 	) {
 		if ( secTables != null ) {
 			//loop through it
 			for (SecondaryTable tab : secTables.value()) {
 				addJoin( tab, null, null, false );
 			}
 		}
 		else {
 			if ( secTable != null ) addJoin( secTable, null, null, false );
 		}
 	}
 
 	//Used for @*ToMany @JoinTable
 	public Join addJoin(JoinTable joinTable, PropertyHolder holder, boolean noDelayInPkColumnCreation) {
 		return addJoin( null, joinTable, holder, noDelayInPkColumnCreation );
 	}
 
 	private static class SecondaryTableNameSource implements ObjectNameSource {
 		// always has an explicit name
 		private final String explicitName;
 
 		private SecondaryTableNameSource(String explicitName) {
 			this.explicitName = explicitName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return explicitName;
 		}
 	}
 
 	private static class SecondaryTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		public String determineImplicitName(NamingStrategy strategy) {
 			// todo : throw an error?
 			return null;
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 	}
 
 	private static SecondaryTableNamingStrategyHelper SEC_TBL_NS_HELPER = new SecondaryTableNamingStrategyHelper();
 
 	private Join addJoin(
 			SecondaryTable secondaryTable,
 			JoinTable joinTable,
 			PropertyHolder propertyHolder,
 			boolean noDelayInPkColumnCreation) {
 		// A non null propertyHolder means than we process the Pk creation without delay
 		Join join = new Join();
 		join.setPersistentClass( persistentClass );
 
 		final String schema;
 		final String catalog;
 		final SecondaryTableNameSource secondaryTableNameContext;
 		final Object joinColumns;
 		final List<UniqueConstraintHolder> uniqueConstraintHolders;
 
 		if ( secondaryTable != null ) {
 			schema = secondaryTable.schema();
 			catalog = secondaryTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( secondaryTable.name() );
 			joinColumns = secondaryTable.pkJoinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( secondaryTable.uniqueConstraints() );
 		}
 		else if ( joinTable != null ) {
 			schema = joinTable.schema();
 			catalog = joinTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( joinTable.name() );
 			joinColumns = joinTable.joinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( joinTable.uniqueConstraints() );
 		}
 		else {
 			throw new AssertionFailure( "Both JoinTable and SecondaryTable are null" );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				secondaryTableNameContext,
 				SEC_TBL_NS_HELPER,
 				false,
 				uniqueConstraintHolders,
 				null,
 				null,
 				mappings,
 				null
 		);
 
 		//no check constraints available on joins
 		join.setTable( table );
 
 		//somehow keep joins() for later.
 		//Has to do the work later because it needs persistentClass id!
 		LOG.debugf( "Adding secondary table to entity %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null ) {
 			join.setSequentialSelect( FetchMode.JOIN != matchingTable.fetch() );
 			join.setInverse( matchingTable.inverse() );
 			join.setOptional( matchingTable.optional() );
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlInsert().sql() ) ) {
 				join.setCustomSQLInsert( matchingTable.sqlInsert().sql().trim(),
 						matchingTable.sqlInsert().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlInsert().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlUpdate().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlDelete().check().toString().toLowerCase()
 						)
 				);
 			}
 		}
 		else {
 			//default
 			join.setSequentialSelect( false );
 			join.setInverse( false );
 			join.setOptional( true ); //perhaps not quite per-spec, but a Good Thing anyway
 		}
 
 		if ( noDelayInPkColumnCreation ) {
 			createPrimaryColumnsToSecondaryTable( joinColumns, propertyHolder, join );
 		}
 		else {
 			secondaryTables.put( table.getQuotedName(), join );
 			secondaryTableJoins.put( table.getQuotedName(), joinColumns );
 		}
 		return join;
 	}
 
 	public java.util.Map<String, Join> getSecondaryTables() {
 		return secondaryTables;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegion = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ?
 					null :
 					cacheAnn.region();
 			cacheConcurrentStrategy = getCacheConcurrencyStrategy( cacheAnn.usage() );
 			if ( "all".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + cacheAnn.include() );
 			}
 		}
 		else {
 			cacheConcurrentStrategy = null;
 			cacheRegion = null;
 			cacheLazyProperty = true;
 		}
 	}
 	
-	public void setNaturalIdCache(NaturalIdCache naturalIdCacheAnn) {
+	public void setNaturalIdCache(XClass clazzToProcess, NaturalIdCache naturalIdCacheAnn) {
 		if ( naturalIdCacheAnn != null ) {
 			if ( BinderHelper.isEmptyAnnotationValue( naturalIdCacheAnn.region() ) ) {
 				if (cacheRegion != null) {
 					naturalIdCacheRegion = cacheRegion + NATURAL_ID_CACHE_SUFFIX;
 				}
 				else {
-					naturalIdCacheRegion = persistentClass.getEntityName() + NATURAL_ID_CACHE_SUFFIX;
+					naturalIdCacheRegion = clazzToProcess.getName() + NATURAL_ID_CACHE_SUFFIX;
 				}
 			}
 			else {
 				naturalIdCacheRegion = naturalIdCacheAnn.region();
 			}
 		}
 		else {
 			naturalIdCacheRegion = null;
 		}
 	}
 
 	public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
 		org.hibernate.cache.spi.access.AccessType accessType = strategy.toAccessType();
 		return accessType == null ? null : accessType.getExternalName();
 	}
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
 	}
 
 	public void setInheritanceState(InheritanceState inheritanceState) {
 		this.inheritanceState = inheritanceState;
 	}
 
 	public boolean isIgnoreIdAnnotations() {
 		return ignoreIdAnnotations;
 	}
 
 	public void setIgnoreIdAnnotations(boolean ignoreIdAnnotations) {
 		this.ignoreIdAnnotations = ignoreIdAnnotations;
 	}
 
 	public void processComplementaryTableDefinitions(org.hibernate.annotations.Table table) {
 		//comment and index are processed here
 		if ( table == null ) return;
 		String appliedTable = table.appliesTo();
 		Iterator tables = persistentClass.getTableClosureIterator();
 		Table hibTable = null;
 		while ( tables.hasNext() ) {
 			Table pcTable = (Table) tables.next();
 			if ( pcTable.getQuotedName().equals( appliedTable ) ) {
 				//we are in the correct table to find columns
 				hibTable = pcTable;
 				break;
 			}
 			hibTable = null;
 		}
 		if ( hibTable == null ) {
 			//maybe a join/secondary table
 			for ( Join join : secondaryTables.values() ) {
 				if ( join.getTable().getQuotedName().equals( appliedTable ) ) {
 					hibTable = join.getTable();
 					break;
 				}
 			}
 		}
 		if ( hibTable == null ) {
 			throw new AnnotationException(
 					"@org.hibernate.annotations.Table references an unknown table: " + appliedTable
 			);
 		}
 		if ( !BinderHelper.isEmptyAnnotationValue( table.comment() ) ) hibTable.setComment( table.comment() );
 		TableBinder.addIndexes( hibTable, table.indexes(), mappings );
 	}
 
 	public void processComplementaryTableDefinitions(Tables tables) {
 		if ( tables == null ) return;
 		for (org.hibernate.annotations.Table table : tables.value()) {
 			processComplementaryTableDefinitions( table );
 		}
 	}
 
 	public AccessType getPropertyAccessType() {
 		return propertyAccessType;
 	}
 
 	public void setPropertyAccessType(AccessType propertyAccessor) {
 		this.propertyAccessType = getExplicitAccessType( annotatedClass );
 		// only set the access type if there is no explicit access type for this class
 		if( this.propertyAccessType == null ) {
 			this.propertyAccessType = propertyAccessor;
 		}
 	}
 
 	public AccessType getPropertyAccessor(XAnnotatedElement element) {
 		AccessType accessType = getExplicitAccessType( element );
 		if ( accessType == null ) {
 		   accessType = propertyAccessType;
 		}
 		return accessType;
 	}
 
 	public AccessType getExplicitAccessType(XAnnotatedElement element) {
 		AccessType accessType = null;
 
 		AccessType hibernateAccessType = null;
 		AccessType jpaAccessType = null;
 
 		org.hibernate.annotations.AccessType accessTypeAnnotation = element.getAnnotation( org.hibernate.annotations.AccessType.class );
 		if ( accessTypeAnnotation != null ) {
 			hibernateAccessType = AccessType.getAccessStrategy( accessTypeAnnotation.value() );
 		}
 
 		Access access = element.getAnnotation( Access.class );
 		if ( access != null ) {
 			jpaAccessType = AccessType.getAccessStrategy( access.value() );
 		}
 
 		if ( hibernateAccessType != null && jpaAccessType != null && hibernateAccessType != jpaAccessType ) {
 			throw new MappingException(
 					"Found @Access and @AccessType with conflicting values on a property in class " + annotatedClass.toString()
 			);
 		}
 
 		if ( hibernateAccessType != null ) {
 			accessType = hibernateAccessType;
 		}
 		else if ( jpaAccessType != null ) {
 			accessType = jpaAccessType;
 		}
 
 		return accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
index 7dfd895aa6..ca23c2c794 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
@@ -1,50 +1,57 @@
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
 package org.hibernate.criterion;
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
+import org.hibernate.Session;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * @author Gavin King
+ * @deprecated Use {@link Session#byNaturalId(Class)}
+ * @see Session#byNaturalId(Class)
+ * @see Session#byNaturalId(String)
+ * @see Session#bySimpleNaturalId(Class)
+ * @see Session#bySimpleNaturalId(String)
  */
+@Deprecated
 public class NaturalIdentifier implements Criterion {
 		
 	private Junction conjunction = new Conjunction();
 
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.getTypedValues(criteria, criteriaQuery);
 	}
 
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.toSqlString(criteria, criteriaQuery);
 	}
 	
 	public NaturalIdentifier set(String property, Object value) {
 		conjunction.add( Restrictions.eq(property, value) );
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
index 5c693382b8..aef0585e83 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
@@ -1,390 +1,399 @@
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
 package org.hibernate.criterion;
 
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 
+import org.hibernate.Session;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.Type;
 
 /**
  * The <tt>criterion</tt> package may be used by applications as a framework for building
  * new kinds of <tt>Criterion</tt>. However, it is intended that most applications will
  * simply use the built-in criterion types via the static factory methods of this class.
  *
  * @see org.hibernate.Criteria
  * @see Projections factory methods for <tt>Projection</tt> instances
  * @author Gavin King
  */
 public class Restrictions {
 
 	Restrictions() {
 		//cannot be instantiated
 	}
 
 	/**
 	 * Apply an "equal" constraint to the identifier property
 	 * @param value
 	 * @return Criterion
 	 */
 	public static Criterion idEq(Object value) {
 		return new IdentifierEqExpression(value);
 	}
 	/**
 	 * Apply an "equal" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression eq(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, "=");
 	}
 	/**
 	 * Apply a "not equal" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression ne(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, "<>");
 	}
 	/**
 	 * Apply a "like" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression like(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, " like ");
 	}
 	/**
 	 * Apply a "like" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
 		return new SimpleExpression(propertyName, matchMode.toMatchString(value), " like " );
 	}
 	/**
 	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
 	 * operator
 	 *
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
 		return new LikeExpression(propertyName, value, matchMode, null, true);
 	}
 	/**
 	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
 	 * operator
 	 *
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static Criterion ilike(String propertyName, Object value) {
 		return new LikeExpression(propertyName, value.toString());
 	}
 	/**
 	 * Apply a "greater than" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression gt(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, ">");
 	}
 	/**
 	 * Apply a "less than" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression lt(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, "<");
 	}
 	/**
 	 * Apply a "less than or equal" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression le(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, "<=");
 	}
 	/**
 	 * Apply a "greater than or equal" constraint to the named property
 	 * @param propertyName
 	 * @param value
 	 * @return Criterion
 	 */
 	public static SimpleExpression ge(String propertyName, Object value) {
 		return new SimpleExpression(propertyName, value, ">=");
 	}
 	/**
 	 * Apply a "between" constraint to the named property
 	 * @param propertyName
 	 * @param lo value
 	 * @param hi value
 	 * @return Criterion
 	 */
 	public static Criterion between(String propertyName, Object lo, Object hi) {
 		return new BetweenExpression(propertyName, lo, hi);
 	}
 	/**
 	 * Apply an "in" constraint to the named property
 	 * @param propertyName
 	 * @param values
 	 * @return Criterion
 	 */
 	public static Criterion in(String propertyName, Object[] values) {
 		return new InExpression(propertyName, values);
 	}
 	/**
 	 * Apply an "in" constraint to the named property
 	 * @param propertyName
 	 * @param values
 	 * @return Criterion
 	 */
 	public static Criterion in(String propertyName, Collection values) {
 		return new InExpression( propertyName, values.toArray() );
 	}
 	/**
 	 * Apply an "is null" constraint to the named property
 	 * @return Criterion
 	 */
 	public static Criterion isNull(String propertyName) {
 		return new NullExpression(propertyName);
 	}
 	/**
 	 * Apply an "equal" constraint to two properties
 	 */
 	public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, "=");
 	}
 	/**
 	 * Apply a "not equal" constraint to two properties
 	 */
 	public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, "<>");
 	}
 	/**
 	 * Apply a "less than" constraint to two properties
 	 */
 	public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, "<");
 	}
 	/**
 	 * Apply a "less than or equal" constraint to two properties
 	 */
 	public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, "<=");
 	}
 	/**
 	 * Apply a "greater than" constraint to two properties
 	 */
 	public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, ">");
 	}
 	/**
 	 * Apply a "greater than or equal" constraint to two properties
 	 */
 	public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
 		return new PropertyExpression(propertyName, otherPropertyName, ">=");
 	}
 	/**
 	 * Apply an "is not null" constraint to the named property
 	 * @return Criterion
 	 */
 	public static Criterion isNotNull(String propertyName) {
 		return new NotNullExpression(propertyName);
 	}
 	/**
 	 * Return the conjuction of two expressions
 	 *
 	 * @param lhs
 	 * @param rhs
 	 * @return Criterion
 	 */
 	public static LogicalExpression and(Criterion lhs, Criterion rhs) {
 		return new LogicalExpression(lhs, rhs, "and");
 	}
 	/**
 	 * Return the disjuction of two expressions
 	 *
 	 * @param lhs
 	 * @param rhs
 	 * @return Criterion
 	 */
 	public static LogicalExpression or(Criterion lhs, Criterion rhs) {
 		return new LogicalExpression(lhs, rhs, "or");
 	}
 	/**
 	 * Return the negation of an expression
 	 *
 	 * @param expression
 	 * @return Criterion
 	 */
 	public static Criterion not(Criterion expression) {
 		return new NotExpression(expression);
 	}
 	/**
 	 * Apply a constraint expressed in SQL, with the given JDBC
 	 * parameters. Any occurrences of <tt>{alias}</tt> will be
 	 * replaced by the table alias.
 	 *
 	 * @param sql
 	 * @param values
 	 * @param types
 	 * @return Criterion
 	 */
 	public static Criterion sqlRestriction(String sql, Object[] values, Type[] types) {
 		return new SQLCriterion(sql, values, types);
 	}
 	/**
 	 * Apply a constraint expressed in SQL, with the given JDBC
 	 * parameter. Any occurrences of <tt>{alias}</tt> will be replaced
 	 * by the table alias.
 	 *
 	 * @param sql
 	 * @param value
 	 * @param type
 	 * @return Criterion
 	 */
 	public static Criterion sqlRestriction(String sql, Object value, Type type) {
 		return new SQLCriterion(sql, new Object[] { value }, new Type[] { type } );
 	}
 	/**
 	 * Apply a constraint expressed in SQL. Any occurrences of <tt>{alias}</tt>
 	 * will be replaced by the table alias.
 	 *
 	 * @param sql
 	 * @return Criterion
 	 */
 	public static Criterion sqlRestriction(String sql) {
 		return new SQLCriterion(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
 	}
 
 	/**
 	 * Group expressions together in a single conjunction (A and B and C...)
 	 *
 	 * @return Conjunction
 	 */
 	public static Conjunction conjunction() {
 		return new Conjunction();
 	}
 
 	/**
 	 * Group expressions together in a single disjunction (A or B or C...)
 	 *
 	 * @return Conjunction
 	 */
 	public static Disjunction disjunction() {
 		return new Disjunction();
 	}
 
 	/**
 	 * Apply an "equals" constraint to each property in the
 	 * key set of a <tt>Map</tt>
 	 *
 	 * @param propertyNameValues a map from property names to values
 	 * @return Criterion
 	 */
 	public static Criterion allEq(Map propertyNameValues) {
 		Conjunction conj = conjunction();
 		Iterator iter = propertyNameValues.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			conj.add( eq( (String) me.getKey(), me.getValue() ) );
 		}
 		return conj;
 	}
 
 	/**
 	 * Constrain a collection valued property to be empty
 	 */
 	public static Criterion isEmpty(String propertyName) {
 		return new EmptyExpression(propertyName);
 	}
 
 	/**
 	 * Constrain a collection valued property to be non-empty
 	 */
 	public static Criterion isNotEmpty(String propertyName) {
 		return new NotEmptyExpression(propertyName);
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeEq(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, "=");
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeNe(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, "<>");
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeGt(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, "<");
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeLt(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, ">");
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeGe(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, "<=");
 	}
 
 	/**
 	 * Constrain a collection valued property by size
 	 */
 	public static Criterion sizeLe(String propertyName, int size) {
 		return new SizeExpression(propertyName, size, ">=");
 	}
 
+	/**
+	 * @deprecated Use {@link Session#byNaturalId(Class)}
+	 * @see Session#byNaturalId(Class)
+	 * @see Session#byNaturalId(String)
+	 * @see Session#bySimpleNaturalId(Class)
+	 * @see Session#bySimpleNaturalId(String)
+	 */
+	@Deprecated
 	public static NaturalIdentifier naturalId() {
 		return new NaturalIdentifier();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index 12afc94142..3b5dd35620 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1917 +1,2007 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
-import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * </p>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
 
 	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
 
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	// Identity map of EntityEntry instances, by the entity instance
 	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map<EntityKey, Object> proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map<EntityKey, Object> entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map<Object, PersistentCollection> arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map<CollectionKey, PersistentCollection> collectionsByKey;
 
 	// Set of EntityKeys of deleted objects
 	private HashSet<EntityKey> nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet<AssociationKey> nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List<PersistentCollection> nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map<Object,Object> parentsByChild;
 
 	private int cascading = 0;
 	private int loadCounter = 0;
 	private boolean flushing = false;
 
 	private boolean defaultReadOnly = false;
 	private boolean hasNonReadOnlyEntities = false;
 
 	private LoadContexts loadContexts;
 	private BatchFetchQueue batchFetchQueue;
 
 
 
 	/**
 	 * Constructs a PersistentContext, bound to the given session.
 	 *
 	 * @param session The session "owning" this context.
 	 */
 	public StatefulPersistenceContext(SessionImplementor session) {
 		this.session = session;
 
 		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
 		//noinspection unchecked
 		proxiesByKey = (Map<EntityKey, Object>) new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
 		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
 	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(8);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		if ( unownedCollections == null ) {
 			return null;
 		}
 		else {
 			return unownedCollections.remove(key);
 		}
 	}
 
 	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			final LazyInitializer li = ((HibernateProxy) o).getHibernateLazyInitializer();
 			li.unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntries.clear();
 		parentsByChild.clear();
 		entitySnapshotsByKey.clear();
 		collectionsByKey.clear();
 		collectionEntries.clear();
 		if ( unownedCollections != null ) {
 			unownedCollections.clear();
 		}
 		proxiesByKey.clear();
 		nullifiableEntityKeys.clear();
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.clear();
 		}
 		// defaultReadOnly is unaffected by clear()
 		hasNonReadOnlyEntities = false;
 		if ( loadContexts != null ) {
 			loadContexts.cleanup();
 		}
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		// Downgrade locks
 		for ( EntityEntry o : entityEntries.values() ) {
 			o.setLockMode( LockMode.NONE );
 		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 */
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		Object cached = entitySnapshotsByKey.get(key);
 		if (cached!=null) {
 			return cached==NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot==null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			cacheNaturalIdResolution( persister, id, dbValue, CachedNaturalIdValueSource.LOAD );
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
 			cacheNaturalIdResolution( persister, id, naturalIdSnapshotSubSet, CachedNaturalIdValueSource.LOAD );
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	/**
 	 * Retrieve the cached database snapshot for the requested entity key.
 	 * <p/>
 	 * This differs from {@link #getDatabaseSnapshot} is two important respects:<ol>
 	 * <li>no snapshot is obtained from the database if not already cached</li>
 	 * <li>an entry of {@link #NO_ROW} here is interpretet as an exception</li>
 	 * </ol>
 	 * @param key The entity key for which to retrieve the cached snapshot
 	 * @return The cached snapshot
 	 * @throws IllegalStateException if the cached snapshot was == {@link #NO_ROW}.
 	 */
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
 	@Override
 	public Object removeEntity(EntityKey key) {
 		Object entity = entitiesByKey.remove(key);
 		Iterator iter = entitiesByUniqueKey.values().iterator();
 		while ( iter.hasNext() ) {
 			if ( iter.next()==entity ) iter.remove();
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove(key);
 		nullifiableEntityKeys.remove(key);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 		getBatchFetchQueue().removeSubselect(key);
 		return entity;
 	}
 
 	/**
 	 * Get an entity cached by unique key
 	 */
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retrieve the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get(coll);
 	}
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	@Override
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 		addEntity( entityKey, entity );
 		return addEntry(
 				entity,
 				status,
 				loadedState,
 				null,
 				entityKey.getIdentifier(),
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 	}
 
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it
 	 * to the event source's internal caches.
 	 */
 	@Override
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 
 		EntityEntry e = new EntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				persister.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				this
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	/**
 	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
 	 *
 	 * @param value The possible proxy to be reassociated.
 	 * @return Whether the passed value represented an actual proxy which got initialized.
 	 * @throws MappingException
 	 */
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( !Hibernate.isInitialized(value) ) {
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
 	 * reset the identifier of the proxy
 	 */
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier(id);
 			reassociateProxy(li, proxy);
 		}
 	}
 
 	/**
 	 * Associate a proxy that was instantiated by another session with this session
 	 *
 	 * @param li The proxy initializer.
 	 * @param proxy The proxy to reassociate.
 	 */
 	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
 		if ( li.getSession() != this.getSession() ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
 			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
 		  	// any earlier proxy takes precedence
 			if ( !proxiesByKey.containsKey( key ) ) {
 				proxiesByKey.put( key, proxy );
 			}
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	/**
 	 * Get the entity instance underlying the given proxy, throwing
 	 * an exception if the proxy is uninitialized. If the given object
 	 * is not a proxy, simply return the argument.
 	 */
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " +
 						li.getEntityName()
 				);
 			}
 			return li.getImplementation(); //unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Possibly unproxy the given reference and reassociate it with the current session.
 	 *
 	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
 	 * @return The unproxied instance.
 	 * @throws HibernateException
 	 */
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return li.getImplementation(); //initialize + unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Attempts to check whether the given key represents an entity already loaded within the
 	 * current session.
 	 * @param object The entity reference against which to perform the uniqueness check.
 	 * @throws HibernateException
 	 */
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		Object entity = getEntity(key);
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	/**
 	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
 	 * and overwrite the registration of the old one. This breaks == and occurs only for
 	 * "class" proxies rather than "interface" proxies. Also init the proxy to point to
 	 * the given target implementation if necessary.
 	 *
 	 * @param proxy The proxy instance to be narrowed.
 	 * @param persister The persister for the proxied entity.
 	 * @param key The internal cache key for the proxied entity.
 	 * @param object (optional) the actual proxied entity instance.
 	 * @return An appropriately narrowed instance.
 	 * @throws HibernateException
 	 */
 	@Override
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			if ( object != null ) {
 				proxiesByKey.remove(key);
 				return object; //return the proxied object
 			}
 			else {
 				proxy = persister.createProxy( key.getIdentifier(), session );
 				Object proxyOrig = proxiesByKey.put(key, proxy); //overwrite old proxy
 				if ( proxyOrig != null ) {
 					if ( ! ( proxyOrig instanceof HibernateProxy ) ) {
 						throw new AssertionFailure(
 								"proxy not of type HibernateProxy; it is " + proxyOrig.getClass()
 						);
 					}
 					// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 					boolean readOnlyOrig = ( ( HibernateProxy ) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 				}
 				return proxy;
 			}
 		}
 		else {
 
 			if ( object != null ) {
 				LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation(object);
 			}
 
 			return proxy;
 
 		}
 
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * third argument (the entity associated with the key) if no proxy exists. Init
 	 * the proxy to the target implementation, if necessary.
 	 */
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 	throws HibernateException {
 		if ( !persister.hasProxy() ) return impl;
 		Object proxy = proxiesByKey.get(key);
 		if ( proxy != null ) {
 			return narrowProxy(proxy, persister, key, impl);
 		}
 		else {
 			return impl;
 		}
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	/**
 	 * Get the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner, if its entity ID is available from the collection's loaded key
 	 * and the owner entity is in the persistence context; otherwise, returns null
 	 */
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null; // early exit...
 		}
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	@Override
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
 		return getLoadedCollectionOwnerIdOrNull( getCollectionEntry( collection ) );
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param ce The collection entry
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
 		if ( ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null ) {
 			return null;
 		}
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// get the ID from collection.getOwner()
 		return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull( ce.getLoadedKey(), session );
 	}
 
 	/**
 	 * add a collection we just loaded up (still needs initializing)
 	 */
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 	throws HibernateException {
 		addCollection(collection, persister);
 	}
 
 	/**
 	 * Add an collection to the cache, with a given collection entry.
 	 *
 	 * @param coll The collection for which we are adding an entry.
 	 * @param entry The entry representing the collection.
 	 * @param key The key of the collection's entry.
 	 */
 	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
 		collectionEntries.put( coll, entry );
 		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure("bug adding collection twice");
 			}
 			// or should it actually throw an exception?
 			old.unsetSession( session );
 			collectionEntries.remove( old );
 			// watch out for a case where old is still referenced
 			// somewhere in the object graph! (which is a user error)
 		}
 	}
 
 	/**
 	 * Add a collection to the cache, creating a new collection entry for it
 	 *
 	 * @param collection The collection for which we are adding an entry.
 	 * @param persister The collection persister
 	 */
 	private void addCollection(PersistentCollection collection, CollectionPersister persister) {
 		CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	/**
 	 * add an (initialized) collection that was created by another session and passed
 	 * into update() (ie. one with a snapshot and existing state on the database)
 	 */
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 	throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	/**
 	 * add a collection we just pulled out of the cache (does not need initializing)
 	 */
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 	throws HibernateException {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		ce.postInitialize(collection);
 		addCollection(collection, ce, id);
 		return ce;
 	}
 
 	/**
 	 * Get the collection instance associated with the <tt>CollectionKey</tt>
 	 */
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			LOG.debug( "Initializing non-lazy collections" );
 			//do this work only at the very highest level of the load
 			loadCounter++; //don't let this method be called recursively
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					nonlazyCollections.remove( size - 1 ).forceInitialization();
 				}
 			}
 			finally {
 				loadCounter--;
 				clearNullProperties();
 			}
 		}
 	}
 
 
 	/**
 	 * Get the <tt>PersistentCollection</tt> object for an array
 	 */
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder(collection);
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					PersistentCollection pc = wrappers.next();
 					if ( pc.isWrapper(collection) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry(coll);
 	}
 
 	/**
 	 * Get an existing proxy by key
 	 */
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put(key, proxy);
 	}
 
 	/**
 	 * Remove a proxy from the session cache.
 	 * <p/>
 	 * Additionally, ensure that any load optimization references
 	 * such as batch or subselect loading get cleaned up as well.
 	 *
 	 * @param key The key of the entity proxy to be removed
 	 * @return The proxy reference.
 	 */
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
 	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	@Override
 	public void setFlushing(boolean flushing) {
 		this.flushing = flushing;
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	@Override
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
 
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	@Override
     public String toString() {
 		return new StringBuffer()
 				.append("PersistenceContext[entityKeys=")
 				.append(entitiesByKey.keySet())
 				.append(",collectionKeys=")
 				.append(collectionsByKey.keySet())
 				.append("]")
 				.toString();
 	}
 
 	/**
 	 * Search <tt>this</tt> persistence context for an associated entity instance which is considered the "owner" of
 	 * the given <tt>childEntity</tt>, and return that owner's id value.  This is performed in the scenario of a
 	 * uni-directional, non-inverse one-to-many collection (which means that the collection elements do not maintain
 	 * a direct reference to the owner).
 	 * <p/>
 	 * As such, the processing here is basically to loop over every entity currently associated with this persistence
 	 * context and for those of the correct entity (sub) type to extract its collection role property value and see
 	 * if the child is contained within that collection.  If so, we have found the owner; if not, we go on.
 	 * <p/>
 	 * Also need to account for <tt>mergeMap</tt> which acts as a local copy cache managed for the duration of a merge
 	 * operation.  It represents a map of the detached entity instances pointing to the corresponding managed instance.
 	 *
 	 * @param entityName The entity name for the entity type which would own the child
 	 * @param propertyName The name of the property on the owning entity type which would name this child association.
 	 * @param childEntity The child entity instance for which to locate the owner instance id.
 	 * @param mergeMap A map of non-persistent instances from an on-going merge operation (possibly null).
 	 *
 	 * @return The id of the entityName instance which is said to own the child; null if an appropriate owner not
 	 * located.
 	 */
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntries.get( parent );
 			//there maybe more than one parent, filter by type
 			if ( 	persister.isSubclassEntityName(entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				parentsByChild.remove( childEntity ); // remove wrong entry
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			final EntityEntry entityEntry = me.getValue();
 			// does this entity entry pertain to the entity persister in which we are interested (owner)?
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				final Object entityEntryInstance = me.getKey();
 
 				//check if the managed object is the parent
 				boolean found = isFoundInParent(
 						propertyName,
 						childEntity,
 						persister,
 						collectionPersister,
 						entityEntryInstance
 				);
 
 				if ( !found && mergeMap != null ) {
 					//check if the detached object being merged is the parent
 					Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 					}
 				}
 
 				if ( found ) {
 					return entityEntry.getId();
 				}
 
 			}
 		}
 
 		// if we get here, it is possible that we have a proxy 'in the way' of the merge map resolution...
 		// 		NOTE: decided to put this here rather than in the above loop as I was nervous about the performance
 		//		of the loop-in-loop especially considering this is far more likely the 'edge case'
 		if ( mergeMap != null ) {
 			for ( Object o : mergeMap.entrySet() ) {
 				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
 					if ( persister.isSubclassEntityName( proxy.getHibernateLazyInitializer().getEntityName() ) ) {
 						boolean found = isFoundInParent(
 								propertyName,
 								childEntity,
 								persister,
 								collectionPersister,
 								mergeMap.get( proxy )
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 						}
 						if ( found ) {
 							return proxy.getHibernateLazyInitializer().getIdentifier();
 						}
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	private boolean isFoundInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent) {
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 
 		EntityPersister persister = session.getFactory()
 				.getEntityPersister(entity);
 		CollectionPersister cp = session.getFactory()
 				.getCollectionPersister(entity + '.' + property);
 
 	    // try cache lookup first
 	    Object parent = parentsByChild.get(childEntity);
 		if (parent != null) {
 			final EntityEntry entityEntry = entityEntries.get(parent);
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent(property, childEntity, persister, cp, parent);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(parent);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 				if (index!=null) {
 					return index;
 				}
 			}
 			else {
 				parentsByChild.remove(childEntity); // remove wrong entry
 			}
 		}
 
 		//Not found in cache, proceed
 		for ( Entry<Object, EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			EntityEntry ee = me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
 				Object instance = me.getKey();
 
 				Object index = getIndexInParent(property, childEntity, persister, cp, instance);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(instance);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 
 				if (index!=null) return index;
 			}
 		}
 		return null;
 	}
 
 	private Object getIndexInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent){
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		if ( collection!=null && Hibernate.isInitialized(collection) ) {
 			return collectionPersister.getCollectionType().indexOf(collection, childEntity);
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Record the fact that the association belonging to the keyed
 	 * entity is null.
 	 */
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		if ( entityOrProxy == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		boolean isReadOnly;
 		if ( entityOrProxy instanceof HibernateProxy ) {
 			isReadOnly = ( ( HibernateProxy ) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
 			EntityEntry ee =  getEntry( entityOrProxy );
 			if ( ee == null ) {
 				throw new TransientObjectException("Instance was not associated with this persistence context" );
 			}
 			isReadOnly = ee.isReadOnly();
 		}
 		return isReadOnly;
 	}
 
 	@Override
 	public void setReadOnly(Object object, boolean readOnly) {
 		if ( object == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		if ( isReadOnly( object ) == readOnly ) {
 			return;
 		}
 		if ( object instanceof HibernateProxy ) {
 			HibernateProxy proxy = ( HibernateProxy ) object;
 			setProxyReadOnly( proxy, readOnly );
 			if ( Hibernate.isInitialized( proxy ) ) {
 				setEntityReadOnly(
 						proxy.getHibernateLazyInitializer().getImplementation(),
 						readOnly
 				);
 			}
 		}
 		else {
 			setEntityReadOnly( object, readOnly );
 			// PersistenceContext.proxyFor( entity ) returns entity if there is no proxy for that entity
 			// so need to check the return value to be sure it is really a proxy
 			Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
 				setProxyReadOnly( ( HibernateProxy ) maybeProxy, readOnly );
 			}
 		}
 	}
 
 	private void setProxyReadOnly(HibernateProxy proxy, boolean readOnly) {
 		if ( proxy.getHibernateLazyInitializer().getSession() != getSession() ) {
 			throw new AssertionFailure(
 					"Attempt to set a proxy to read-only that is associated with a different session" );
 		}
 		proxy.getHibernateLazyInitializer().setReadOnly( readOnly );
 	}
 
 	private void setEntityReadOnly(Object entity, boolean readOnly) {
 		EntityEntry entry = getEntry(entity);
 		if (entry == null) {
 			throw new TransientObjectException("Instance was not associated with this persistence context" );
 		}
 		entry.setReadOnly(readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		Object entity = entitiesByKey.remove( oldKey );
 		EntityEntry oldEntry = entityEntries.remove( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
 		addEntity( newKey, entity );
 		addEntry(
 				entity,
 		        oldEntry.getStatus(),
 		        oldEntry.getLoadedState(),
 		        oldEntry.getRowId(),
 		        generatedId,
 		        oldEntry.getVersion(),
 		        oldEntry.getLockMode(),
 		        oldEntry.isExistsInDatabase(),
 		        oldEntry.getPersister(),
 		        oldEntry.isBeingReplicated(),
 		        oldEntry.isLoadedWithLazyPropertiesUnfetched()
 		);
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * persistence context.
 	 *
 	 * @param oos The stream to which the persistence context should get written
 	 * @throws IOException serialization errors.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace( "Serializing persistent-context" );
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
 		Iterator itr = entitiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
 		itr = entitiesByUniqueKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
 		itr = proxiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( (EntityKey) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
 		itr = entitySnapshotsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entityEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
 		itr = entityEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( EntityEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( collectionsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
 		itr = collectionsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( CollectionKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
 		itr = collectionEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
 		itr = arrayHolders.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
 		for ( EntityKey entry : nullifiableEntityKeys ) {
 			entry.serialize( oos );
 		}
 	}
 
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace("Serializing persistent-context");
 		StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
 
 		// during deserialization, we need to reconnect all proxies and
 		// collections to this session, as well as the EntityEntry and
 		// CollectionEntry instances; these associations are transient
 		// because serialization is used for different things.
 
 		try {
 			rtn.defaultReadOnly = ois.readBoolean();
 			// todo : we can actually just determine this from the incoming EntityEntry-s
 			rtn.hasNonReadOnlyEntities = ois.readBoolean();
 
 			int count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
 			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
 			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
 			//noinspection unchecked
 			rtn.proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK, count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count, .75f );
 			for ( int i = 0; i < count; i++ ) {
 				EntityKey ek = EntityKey.deserialize( ois, session );
 				Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
 				} else {
 					if ( tracing ) LOG.trace("Encountered prunded proxy");
 				}
 				// otherwise, the proxy was pruned during the serialization process
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
 			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, rtn );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
 			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
 			rtn.arrayHolders = new IdentityHashMap<Object, PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
 			rtn.nullifiableEntityKeys = new HashSet<EntityKey>();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	@Override
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
 	@Override
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	// INSERTED KEYS HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
 	@Override
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		// we only are worried about registering these if the persister defines caching
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap == null ) {
 				insertedKeysMap = new HashMap<String, List<Serializable>>();
 			}
 			final String rootEntityName = persister.getRootEntityName();
 			List<Serializable> insertedEntityIds = insertedKeysMap.get( rootEntityName );
 			if ( insertedEntityIds == null ) {
 				insertedEntityIds = new ArrayList<Serializable>();
 				insertedKeysMap.put( rootEntityName, insertedEntityIds );
 			}
 			insertedEntityIds.add( id );
 		}
 	}
 
 	@Override
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
 		// again, we only really care if the entity is cached
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap != null ) {
 				List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
 				if ( insertedEntityIds != null ) {
 					return insertedEntityIds.contains( id );
 				}
 			}
 		}
 		return false;
 	}
 
 	private void cleanUpInsertedKeysAfterTransaction() {
 		if ( insertedKeysMap != null ) {
 			insertedKeysMap.clear();
 		}
 	}
 
 
 
 	// NATURAL ID RESOLUTION HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-	public void loadedStateUpdatedNotification(EntityEntry entityEntry) {
+	public void loadedStateInsertedNotification(EntityEntry entityEntry) {
 		final EntityPersister persister = entityEntry.getPersister();
-		if ( ! persister.hasNaturalIdentifier() ) {
+		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( entityEntry, persister );
 
-		// re-cache
-		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.UPDATE );
+		// cache
+		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.INSERT );
 	}
-	
+
 	@Override
-	public void loadedStateDeletedNotification(EntityEntry entityEntry) {
+	public void loadedStateUpdatedNotification(EntityEntry entityEntry) {
 		final EntityPersister persister = entityEntry.getPersister();
-		if ( ! persister.hasNaturalIdentifier() ) {
+		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( entityEntry, persister );
-		
-		// evict from cache
-		evictNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues );
+
+		// re-cache
+		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.UPDATE );
 	}
 
 	@Override
-	public void loadedStateInsertedNotification(EntityEntry entityEntry) {
+	public void loadedStateDeletedNotification(EntityEntry entityEntry) {
 		final EntityPersister persister = entityEntry.getPersister();
-		if ( ! persister.hasNaturalIdentifier() ) {
+		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( entityEntry, persister );
 
-		// cache
-		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.INSERT );
+		// evict from cache
+		evictNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues );
 	}
-	
+
 	private Object[] getNaturalIdValues(EntityEntry entityEntry, EntityPersister persister) {
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 
 		final Object[] loadedState = entityEntry.getLoadedState();
 		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 			naturalIdValues[i] = loadedState[naturalIdPropertyIndexes[i]];
 		}
 
 		return naturalIdValues;
 	}
 
-	private NaturalIdCacheKey getNaturalIdCacheKey(Object[] naturalIdValues, EntityPersister persister) {
-		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
+	private static class LocalNaturalIdCacheKey {
+		private final EntityPersister persister;
+		private final Object[] values;
+		private int hashCode;
 
-		final Serializable[] naturalIdKeyValues = new Serializable[naturalIdPropertyIndexes.length];
-		final Type[] naturalIdTypes = new Type[naturalIdPropertyIndexes.length];
+		public LocalNaturalIdCacheKey(EntityPersister persister, Object[] values) {
+			this.persister = persister;
+			this.values = values;
 
-		final Type[] propertyTypes = persister.getPropertyTypes();
+			final int prime = 31;
+			int result = 1;
+			result = prime * result + ( ( persister == null ) ? 0 : persister.hashCode() );
+			result = prime * result + Arrays.hashCode( values );
+			this.hashCode = result;
+		}
 
-		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
-			naturalIdTypes[i] = propertyTypes[naturalIdPropertyIndexes[i]];
-			naturalIdKeyValues[i] = naturalIdTypes[i].disassemble( naturalIdValues[i], session, null );
+		public Object[] getValues() {
+			return values;
 		}
 
-		return new NaturalIdCacheKey( naturalIdKeyValues, naturalIdTypes, persister.getEntityName(),
-				session.getTenantIdentifier(), session.getFactory() );
+		@Override
+		public int hashCode() {
+			return this.hashCode;
+		}
+
+		@Override
+		public boolean equals(Object obj) {
+			if ( this == obj )
+				return true;
+			if ( obj == null )
+				return false;
+			if ( getClass() != obj.getClass() )
+				return false;
+			LocalNaturalIdCacheKey other = (LocalNaturalIdCacheKey) obj;
+			if ( persister == null ) {
+				if ( other.persister != null )
+					return false;
+			}
+			else if ( !persister.equals( other.persister ) )
+				return false;
+			if ( !Arrays.equals( values, other.values ) )
+				return false;
+			return true;
+		}
 	}
 
-	private class NaturalIdResolutionCache implements Serializable {
+	private static class NaturalIdResolutionCache implements Serializable {
 		private final EntityPersister persister;
 
 		private NaturalIdResolutionCache(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		public EntityPersister getPersister() {
 			return persister;
 		}
 
-		private Map<Serializable,NaturalIdCacheKey> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, NaturalIdCacheKey>();
-		private Map<NaturalIdCacheKey,Serializable> naturalIdToPkMap = new ConcurrentHashMap<NaturalIdCacheKey,Serializable>();
+		private Map<Serializable, LocalNaturalIdCacheKey> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, LocalNaturalIdCacheKey>();
+		private Map<LocalNaturalIdCacheKey, Serializable> naturalIdToPkMap = new ConcurrentHashMap<LocalNaturalIdCacheKey, Serializable>();
 	}
 
-	private Map<EntityPersister,NaturalIdResolutionCache> naturalIdResolutionCacheMap
-			= new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
+	private void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
+		if ( !persister.hasNaturalIdentifier() ) {
+			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
+		}
+		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
+			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
+		}
+	}
 
+	private final Map<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
 
 	@Override
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			return null;
 		}
-		else {
-			final NaturalIdCacheKey naturalIdCacheKey = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
-			if (naturalIdCacheKey == null) {
-				return null;
-			}
-			
-			final Serializable[] naturalIdKeyValues = naturalIdCacheKey.getNaturalId();
-			final Type[] types = naturalIdCacheKey.getTypes();
-			Object[] naturalIdValues = new Object[naturalIdKeyValues.length];
-			for (int i = 0; i < naturalIdKeyValues.length; i++) {
-				naturalIdValues[i] = types[i].assemble( naturalIdKeyValues[i], session, null );
-			}
-			
-			return naturalIdValues;
+
+		final LocalNaturalIdCacheKey localNaturalIdCacheKey = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
+		if ( localNaturalIdCacheKey == null ) {
+			return null;
 		}
+
+		return localNaturalIdCacheKey.getValues();
 	}
 
 	@Override
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
-		if ( ! persister.hasNaturalIdentifier() ) {
-			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
+		validateNaturalId( persister, naturalIdValues );
+
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+
+		Serializable pk;
+		final LocalNaturalIdCacheKey localNaturalIdCacheKey = new LocalNaturalIdCacheKey( persister, naturalIdValues );
+		if ( entityNaturalIdResolutionCache != null ) {
+			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( localNaturalIdCacheKey );
+
+			// Found in session cache
+			if ( pk != null ) {
+				if ( LOG.isTraceEnabled() )
+					LOG.tracev(
+							"Resolved primary key in session cache: {0}",
+							MessageHelper.infoString( persister, Arrays.toString( naturalIdValues ),
+									session.getFactory() ) );
+
+				return pk;
+			}
 		}
-		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
-			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
+
+		// Session cache miss, see if second-level caching is enabled
+		if ( !persister.hasNaturalIdCache() ) {
+			return null;
 		}
+		
+		// Try resolution from second-level cache
+		final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
+		
+		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+		pk = (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session.getTimestamp() );
+
+		// Found in second-level cache, store in session cache
+		final SessionFactoryImplementor factory = getSession().getFactory();
+		if ( pk != null ) {
+			if ( factory.getStatistics().isStatisticsEnabled() ) {
+				factory.getStatisticsImplementor().naturalIdCacheHit(
+						naturalIdCacheAccessStrategy.getRegion().getName() );
+			}
 
-		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-		final NaturalIdCacheKey naturalIdCacheKey = getNaturalIdCacheKey( naturalIdValues, persister );
-		if ( entityNaturalIdResolutionCache == null ) {
-			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
-					.getNaturalIdCacheAccessStrategy();
-			return (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session.getTimestamp() );
+			if ( LOG.isTraceEnabled() )
+				LOG.tracev( "Resolved primary key in second-level cache: {0}",
+						MessageHelper.infoString( persister, naturalIdCacheKey.getNaturalId(), session.getFactory() ) );
+
+			if ( entityNaturalIdResolutionCache == null ) {
+				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
+				naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
+			}
+
+			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, localNaturalIdCacheKey );
+			entityNaturalIdResolutionCache.naturalIdToPkMap.put( localNaturalIdCacheKey, pk );
 		}
-		else {
-			return entityNaturalIdResolutionCache.naturalIdToPkMap.get( naturalIdCacheKey );
+		else if ( factory.getStatistics().isStatisticsEnabled() ) {
+			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
 		}
+
+		return pk;
 	}
-	
-	@Override
-	public void cacheNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues, CachedNaturalIdValueSource valueSource ) {
-		if ( ! persister.hasNaturalIdentifier() ) {
-			throw new IllegalArgumentException( "Entity did not define a natural-id" );
-		}
-		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
-			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
-		}
 
+	@Override
+	public void cacheNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues,
+			CachedNaturalIdValueSource valueSource) {
+		validateNaturalId( persister, naturalIdValues );
+		
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
 		}
 
-		final NaturalIdCacheKey naturalIdCacheKey = getNaturalIdCacheKey( naturalIdValues, persister );
-		
-		entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, naturalIdCacheKey );
-		entityNaturalIdResolutionCache.naturalIdToPkMap.put( naturalIdCacheKey, pk );
-		
-		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-		switch ( valueSource ) {
-		case LOAD: {
-			naturalIdCacheAccessStrategy.putFromLoad( naturalIdCacheKey, pk, session.getTimestamp(), null );
-			break;
-		}
-		case INSERT: {
-			naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, pk );
-
-			( (EventSource) this.session ).getActionQueue().registerProcess( new AfterTransactionCompletionProcess() {
-				@Override
-				public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-					naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, pk );
-				}
-			} );
-			
-			break;
-		}
-		case UPDATE: {
-			final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
+		final LocalNaturalIdCacheKey localNaturalIdCacheKey = new LocalNaturalIdCacheKey( persister, naturalIdValues );
+		entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, localNaturalIdCacheKey );
+		entityNaturalIdResolutionCache.naturalIdToPkMap.put( localNaturalIdCacheKey, pk );
 
-			naturalIdCacheAccessStrategy.update( naturalIdCacheKey, pk );
+		//If second-level caching is enabled cache the resolution there as well
+		if ( persister.hasNaturalIdCache() ) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
 
-			( (EventSource) this.session ).getActionQueue().registerProcess( new AfterTransactionCompletionProcess() {
-				@Override
-				public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-					naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, pk, lock );
-				}
-			} );
+			final SessionFactoryImplementor factory = getSession().getFactory();
 			
-			break;
-		}
+			switch ( valueSource ) {
+			case LOAD: {
+				final boolean put = naturalIdCacheAccessStrategy.putFromLoad( naturalIdCacheKey, pk, session.getTimestamp(), null );
+
+				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+					factory.getStatisticsImplementor().naturalIdCachePut(
+							naturalIdCacheAccessStrategy.getRegion().getName() );
+				}
+
+				break;
+			}
+			case INSERT: {
+				naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, pk );
+
+				( (EventSource) this.session ).getActionQueue().registerProcess(
+						new AfterTransactionCompletionProcess() {
+							@Override
+							public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+								final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, pk );
+
+								if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+									factory.getStatisticsImplementor().naturalIdCachePut(
+											naturalIdCacheAccessStrategy.getRegion().getName() );
+								}
+							}
+						} );
+
+				break;
+			}
+			case UPDATE: {
+				final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
+
+				naturalIdCacheAccessStrategy.update( naturalIdCacheKey, pk );
+
+				( (EventSource) this.session ).getActionQueue().registerProcess(
+						new AfterTransactionCompletionProcess() {
+							@Override
+							public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+								final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, pk, lock );
+								
+								if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+									factory.getStatisticsImplementor().naturalIdCachePut(
+											naturalIdCacheAccessStrategy.getRegion().getName() );
+								}
+							}
+						} );
+
+				break;
+			}
+			}
 		}
 	}
-	
+
 	@Override
-	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues ) {
-		if ( ! persister.hasNaturalIdentifier() ) {
+	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues) {
+		if ( !persister.hasNaturalIdentifier() ) {
 			throw new IllegalArgumentException( "Entity did not define a natural-id" );
 		}
 		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
 			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
 		}
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-		if ( entityNaturalIdResolutionCache == null ) {
-			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
-			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
+		if ( entityNaturalIdResolutionCache != null ) {
+			final LocalNaturalIdCacheKey localNaturalIdCacheKey = new LocalNaturalIdCacheKey( persister,
+					naturalIdValues );
+			entityNaturalIdResolutionCache.pkToNaturalIdMap.remove( pk );
+			entityNaturalIdResolutionCache.naturalIdToPkMap.remove( localNaturalIdCacheKey );
 		}
 
-		final NaturalIdCacheKey naturalIdCacheKey = getNaturalIdCacheKey( naturalIdValues, persister );
-		entityNaturalIdResolutionCache.pkToNaturalIdMap.remove( pk ); //Should I compare the value returned here with the naturalIdCacheKey parameter? 
-		entityNaturalIdResolutionCache.naturalIdToPkMap.remove( naturalIdCacheKey );
-		
-		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-		naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
+		if ( persister.hasNaturalIdCache() ) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
+			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index 438e4b2a54..92ffc323da 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,145 +1,131 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.PersistenceContext.CachedNaturalIdValueSource;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class DefaultResolveNaturalIdEventListener
 		extends AbstractLockUpgradeEventListener
 		implements ResolveNaturalIdEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DefaultResolveNaturalIdEventListener.class.getName()
 	);
 
 	@Override
 	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
 		final Serializable entityId = resolveNaturalId( event );
 		event.setEntityId( entityId );
 	}
 
 	/**
 	 * Coordinates the efforts to load a given entity. First, an attempt is
 	 * made to load the entity from the session-level cache. If not found there,
 	 * an attempt is made to locate it in second-level cache. Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The loaded entity, or null.
 	 */
 	protected Serializable resolveNaturalId(final ResolveNaturalIdEvent event) {
 		final EntityPersister persister = event.getEntityPersister();
 
-		if ( LOG.isTraceEnabled() ) {
-			LOG.trace(
-					"Attempting to resolve: " +
-							MessageHelper.infoString(
-									persister, event.getNaturalIdValues(), event.getSession().getFactory()
-							)
-			);
-		}
+		final boolean traceEnabled = LOG.isTraceEnabled();
+		if ( traceEnabled )
+			LOG.tracev( "Attempting to resolve: {0}",
+					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
-		Serializable entityId = resolveFromSessionCache( event );
+		Serializable entityId = resolveFromCache( event );
 		if ( entityId != null ) {
-			if ( LOG.isTraceEnabled() ) {
-				LOG.trace(
-						"Resolved object in session cache: " +
-								MessageHelper.infoString(
-										persister, event.getNaturalIdValues(), event.getSession().getFactory()
-								)
-				);
-			}
+			if ( traceEnabled )
+				LOG.tracev( "Resolved object in cache: {0}",
+						MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 			return entityId;
 		}
 
-		if ( LOG.isTraceEnabled() ) {
-			LOG.trace(
-					"Object not resolved in any cache: " +
-							MessageHelper.infoString(
-									persister, event.getNaturalIdValues(), event.getSession().getFactory()
-							)
-			);
-		}
+		if ( traceEnabled )
+			LOG.tracev( "Object not resolved in any cache: {0}",
+					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		return loadFromDatasource( event );
 	}
 
 	/**
 	 * Attempts to resolve the entity id corresponding to the event's natural id values from the session
 	 * 
 	 * @param event The load event
 	 *
-	 * @return The entity from the session-level cache, or null.
+	 * @return The entity from the cache, or null.
 	 */
-	protected Serializable resolveFromSessionCache(final ResolveNaturalIdEvent event) {
+	protected Serializable resolveFromCache(final ResolveNaturalIdEvent event) {
 		return event.getSession().getPersistenceContext().findCachedNaturalIdResolution(
 				event.getEntityPersister(),
 				event.getOrderedNaturalIdValues()
 		);
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Serializable loadFromDatasource(final ResolveNaturalIdEvent event) {
 		final Serializable pk = event.getEntityPersister().loadEntityIdByNaturalId(
 				event.getOrderedNaturalIdValues(),
 				event.getLockOptions(),
 				event.getSession()
 		);
 		event.getSession().getPersistenceContext().cacheNaturalIdResolution(
 				event.getEntityPersister(),
 				pk,
 				event.getOrderedNaturalIdValues(),
 				CachedNaturalIdValueSource.LOAD
 		);
 		return pk;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 478135a693..5ded9a8ba7 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -490,1485 +490,1505 @@ public final class SessionFactoryImpl
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties, this);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( entry.getKey() ).append( sep );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy( properties );
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver(
 				cfg.getCurrentTenantIdentifierResolver(),
 				properties
 		);
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private CustomEntityDirtinessStrategy determineCustomEntityDirtinessStrategy(Properties properties) {
 		final Object value = properties.get( AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY );
 		if ( value != null ) {
 			if ( CustomEntityDirtinessStrategy.class.isInstance( value ) ) {
 				return CustomEntityDirtinessStrategy.class.cast( value );
 			}
 			Class<CustomEntityDirtinessStrategy> customEntityDirtinessStrategyClass;
 			if ( Class.class.isInstance( value ) ) {
 				customEntityDirtinessStrategyClass = Class.class.cast( customEntityDirtinessStrategy );
 			}
 			else {
 				try {
 					customEntityDirtinessStrategyClass = serviceRegistry.getService( ClassLoaderService.class )
 							.classForName( value.toString() );
 				}
 				catch (Exception e) {
 					LOG.debugf(
 							"Unable to locate CustomEntityDirtinessStrategy implementation class %s",
 							value.toString()
 					);
 					customEntityDirtinessStrategyClass = null;
 				}
 			}
 			if ( customEntityDirtinessStrategyClass != null ) {
 				try {
 					return customEntityDirtinessStrategyClass.newInstance();
 				}
 				catch (Exception e) {
 					LOG.debugf(
 							"Unable to instantiate CustomEntityDirtinessStrategy class %s",
 							customEntityDirtinessStrategyClass.getName()
 					);
 				}
 			}
 		}
 
 		// last resort
 		return new CustomEntityDirtinessStrategy() {
 			@Override
 			public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public boolean isDirty(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public void resetDirty(Object entity, EntityPersister persister, Session session) {
 			}
 
 			@Override
 			public void findDirty(
 					Object entity,
 					EntityPersister persister,
 					Session session,
 					DirtyCheckContext dirtyCheckContext) {
 				// todo : implement proper method body
 			}
 		};
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private CurrentTenantIdentifierResolver determineCurrentTenantIdentifierResolver(
 			CurrentTenantIdentifierResolver explicitResolver,
 			Properties properties) {
 		if ( explicitResolver != null ) {
 			return explicitResolver;
 		}
 
 		final Object value = properties.get( AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER );
 		if ( value == null ) {
 			return null;
 		}
 
 		if ( CurrentTenantIdentifierResolver.class.isInstance( value ) ) {
 			return CurrentTenantIdentifierResolver.class.cast( value );
 		}
 
 		Class<CurrentTenantIdentifierResolver> implClass;
 		if ( Class.class.isInstance( value ) ) {
 			implClass = Class.class.cast( customEntityDirtinessStrategy );
 		}
 		else {
 			try {
 				implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( value.toString() );
 			}
 			catch (Exception e) {
 				LOG.debugf(
 						"Unable to locate CurrentTenantIdentifierResolver implementation class %s",
 						value.toString()
 				);
 				return null;
 			}
 		}
 
 		try {
 			return implClass.newInstance();
 		}
 		catch (Exception e) {
 			LOG.debugf(
 					"Unable to instantiate CurrentTenantIdentifierResolver class %s",
 					implClass.getName()
 			);
 		}
 
 		return null;
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 
 		// TODO: get SQL functions from JdbcServices (HHH-6559)
 		//this.sqlFunctionRegistry = new SQLFunctionRegistry( this.jdbcServices.getSqlFunctions() );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( this.dialect, new HashMap<String, SQLFunction>() );
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 		// TODO: get RegionFactory from service registry
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
         final IntegratorObserver integratorObserver = new IntegratorObserver();
         this.observer.addObserver(integratorObserver);
         for (Integrator integrator : serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
             integrator.integrate(metadata, this, this.serviceRegistry);
             integratorObserver.integrators.add(integrator);
         }
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				identifierGenerators.put(
 						entityBinding.getEntity().getName(),
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator()
 				);
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap<String,EntityPersister>();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getHierarchyDetails().getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getHierarchyDetails().getCaching().getAccessType();
 					if ( LOG.isTraceEnabled() ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String, CollectionMetadata> tmpCollectionMetadata = new HashMap<String, CollectionMetadata>();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			final String cacheRegionName = cacheRegionPrefix + model.getCaching().getRegion();
 			final AccessType accessType = model.getCaching().getAccessType();
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
 			collectionPersisters.put( model.getAttribute().getRole(), persister );
 			tmpCollectionMetadata.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		for ( Map.Entry<String, Set<String>> entry : tmpEntityToCollectionRoleMap.entrySet() ) {
 			entry.setValue( Collections.unmodifiableSet( entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
 			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
 			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
 		}
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
 			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid, 
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		LOG.debug("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache( settings, properties, this );
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache( null, updateTimestampsCache, settings, properties );
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( entry.getKey() ).append( sep );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.metamodel.binding.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.metamodel.binding.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null ? null : entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || ! associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy( properties );
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( null, properties );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		Map<String,HibernateException> errors = new HashMap<String,HibernateException>();
 
 		// Check named HQL queries
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named HQL queries", namedQueries.size() );
 		}
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named query: %s", queryName );
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named SQL queries", namedSqlQueries.size() );
 		}
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named SQL query: %s", queryName );
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		return errors;
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get(entityName);
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
      * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[ results.size() ] );
 	}
 
 	public String getImportedClassName(String className) {
 		String result = imports.get(className);
 		if (result==null) {
 			try {
 				ReflectHelper.classForName( className );
 				return className;
 			}
 			catch (ClassNotFoundException cnfe) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister( className ).getPropertyType( propertyName );
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return jdbcServices.getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionFactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		if ( settings.isQueryCacheEnabled() )  {
 			queryCache.destroy();
 
 			iter = queryCaches.values().iterator();
 			while ( iter.hasNext() ) {
 				QueryCache cache = (QueryCache) iter.next();
 				cache.destroy();
 			}
 			updateTimestampsCache.destroy();
 		}
 
 		settings.getRegionFactory().stop();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 	}
 
 	private class CacheImpl implements Cache {
 		public boolean containsEntity(Class entityClass, Serializable identifier) {
 			return containsEntity( entityClass.getName(), identifier );
 		}
 
 		public boolean containsEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( identifier, p ) );
 		}
 
 		public void evictEntity(Class entityClass, Serializable identifier) {
 			evictEntity( entityClass.getName(), identifier );
 		}
 
 		public void evictEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.infoString( p, identifier, SessionFactoryImpl.this ) );
 				}
 				p.getCacheAccessStrategy().evict( buildCacheKey( identifier, p ) );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable identifier, EntityPersister p) {
 			return new CacheKey(
 					identifier,
 					p.getIdentifierType(),
 					p.getRootEntityName(),
 					null, 						// have to assume non tenancy
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictEntityRegion(Class entityClass) {
 			evictEntityRegion( entityClass.getName() );
 		}
 
 		public void evictEntityRegion(String entityName) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getEntityName() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictEntityRegions() {
 			for ( String s : entityPersisters.keySet() ) {
 				evictEntityRegion( s );
 			}
 		}
 
+		public void evictNaturalIdRegion(Class entityClass) {
+			evictNaturalIdRegion( entityClass.getName() );
+		}
+
+		public void evictNaturalIdRegion(String entityName) {
+			EntityPersister p = getEntityPersister( entityName );
+			if ( p.hasNaturalIdCache() ) {
+				if ( LOG.isDebugEnabled() ) {
+					LOG.debugf( "Evicting second-level cache: %s", p.getEntityName() );
+				}
+				p.getNaturalIdCacheAccessStrategy().evictAll();
+			}
+		}
+
+		public void evictNaturalIdRegions() {
+			for ( String s : entityPersisters.keySet() ) {
+				evictNaturalIdRegion( s );
+			}
+		}
+
 		public boolean containsCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( ownerIdentifier, p ) );
 		}
 
 		public void evictCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.collectionInfoString( p, ownerIdentifier, SessionFactoryImpl.this ) );
 				}
 				CacheKey cacheKey = buildCacheKey( ownerIdentifier, p );
 				p.getCacheAccessStrategy().evict( cacheKey );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable ownerIdentifier, CollectionPersister p) {
 			return new CacheKey(
 					ownerIdentifier,
 					p.getKeyType(),
 					p.getRole(),
 					null,						// have to assume non tenancy
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictCollectionRegion(String role) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getRole() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictCollectionRegions() {
 			for ( String s : collectionPersisters.keySet() ) {
 				evictCollectionRegion( s );
 			}
 		}
 
 		public boolean containsQuery(String regionName) {
 			return queryCaches.containsKey( regionName );
 		}
 
 		public void evictDefaultQueryRegion() {
 			if ( settings.isQueryCacheEnabled() ) {
 				queryCache.clear();
 			}
 		}
 
 		public void evictQueryRegion(String regionName) {
             if (regionName == null) throw new NullPointerException(
                                                                    "Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)");
             if (settings.isQueryCacheEnabled()) {
                 QueryCache namedQueryCache = queryCaches.get(regionName);
                 // TODO : cleanup entries in queryCaches + allCacheRegions ?
                 if (namedQueryCache != null) namedQueryCache.clear();
 			}
 		}
 
 		public void evictQueryRegions() {
 			if ( queryCaches != null ) {
 				for ( QueryCache queryCache : queryCaches.values() ) {
 					queryCache.clear();
 					// TODO : cleanup entries in queryCaches + allCacheRegions ?
 				}
 			}
 		}
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.clear();
 		}
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return updateTimestampsCache;
 	}
 
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		QueryCache currentQueryCache = queryCaches.get( regionName );
 		if ( currentQueryCache == null ) {
 			synchronized ( allCacheRegions ) {
 				currentQueryCache = queryCaches.get( regionName );
 				if ( currentQueryCache == null ) {
 					currentQueryCache = settings.getQueryCacheFactory()
 							.getQueryCache( regionName, updateTimestampsCache, settings, properties );
 					queryCaches.put( regionName, currentQueryCache );
 					allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 				} else {
 					return currentQueryCache;
 				}
 			}
 		}
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return new HashMap( allCacheRegions );
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return identifierGenerators.get(rootEntityName);
 	}
 
 	private org.hibernate.engine.transaction.spi.TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 				LOG.autoFlushWillNotWork();
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = ReflectHelper.classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
 				return null;
 			}
 		}
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return customEntityDirtinessStrategy;
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index d512e37322..72f238b693 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -2905,1728 +2905,1732 @@ public abstract class AbstractEntityPersister
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					update.close();
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					delete.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for entity: %s", getEntityName());
             if (sqlLazySelectString != null) LOG.debugf(" Lazy select: %s", sqlLazySelectString);
             if (sqlVersionSelectString != null) LOG.debugf(" Version select: %s", sqlVersionSelectString);
             if (sqlSnapshotSelectString != null) LOG.debugf(" Snapshot select: %s", sqlSnapshotSelectString);
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf(" Insert %s: %s", j, getSQLInsertStrings()[j]);
                 LOG.debugf(" Update %s: %s", j, getSQLUpdateStrings()[j]);
                 LOG.debugf(" Delete %s: %s", j, getSQLDeleteStrings()[j]);
 			}
             if (sqlIdentityInsertString != null) LOG.debugf(" Identity insert: %s", sqlIdentityInsertString);
             if (sqlUpdateByRowIdString != null) LOG.debugf(" Update by row id (all fields): %s", sqlUpdateByRowIdString);
             if (sqlLazyUpdateByRowIdString != null) LOG.debugf(" Update by row id (non-lazy fields): %s",
                                                                sqlLazyUpdateByRowIdString);
             if (sqlInsertGeneratedValuesSelectString != null) LOG.debugf("Insert-generated property select: %s",
                                                                          sqlInsertGeneratedValuesSelectString);
             if (sqlUpdateGeneratedValuesSelectString != null) LOG.debugf("Update-generated property select: %s",
                                                                          sqlUpdateGeneratedValuesSelectString);
             if (sqlEntityIdByNaturalIdString != null) LOG.debugf("Id by Natural Id: %s",
             													 sqlEntityIdByNaturalIdString);
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, generateFilterConditionAlias( alias ), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 		
 		if (hasNaturalIdentifier()) {
 		    sqlEntityIdByNaturalIdString = generateEntityIdByNaturalIdSql();
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 	
+	public boolean hasNaturalIdCache() {
+		return naturalIdRegionAccessStrategy != null;
+	}
+	
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		//if ( hasLazyProperties() ) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( hasCache() ) {
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i] );
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
 				ps.close();
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
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
 			        sql
 			);
 		}
 	}
     
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Type type = getPropertyTypes()[idPosition];
 					type.nullSafeSet( ps, naturalIdValues[loop++], positions, session );
 					positions += type.getColumnSpan( session.getFactory() );
 				}
 				ResultSet rs = ps.executeQuery();
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					// entity ID has to be serializable right?
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
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
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private String generateEntityIdByNaturalIdSql() {
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index 72d2fd1a2e..f23cbe3479 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,745 +1,750 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.OptimisticCacheSource;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Implementors define mapping and persistence logic for a particular
  * strategy of entity mapping.  An instance of entity persisters corresponds
  * to a given mapped entity.
  * <p/>
  * Implementors must be threadsafe (preferrably immutable) and must provide a constructor
  * matching the signature of: {@link org.hibernate.mapping.PersistentClass}, {@link org.hibernate.engine.spi.SessionFactoryImplementor}
  *
  * @author Gavin King
  */
 public interface EntityPersister extends OptimisticCacheSource {
 
 	/**
 	 * The property name of the "special" identifier property in HQL
 	 */
 	public static final String ENTITY_ID = "id";
 
 	/**
 	 * Finish the initialization of this object.
 	 * <p/>
 	 * Called only once per {@link org.hibernate.SessionFactory} lifecycle,
 	 * after all entity persisters have been instantiated.
 	 *
 	 * @throws org.hibernate.MappingException Indicates an issue in the metadata.
 	 */
 	public void postInstantiate() throws MappingException;
 
 	/**
 	 * Return the SessionFactory to which this persister "belongs".
 	 *
 	 * @return The owning SessionFactory.
 	 */
 	public SessionFactoryImplementor getFactory();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns an object that identifies the space in which identifiers of
 	 * this entity hierarchy are unique.  Might be a table name, a JNDI URL, etc.
 	 *
 	 * @return The root entity name.
 	 */
 	public String getRootEntityName();
 
 	/**
 	 * The entity name which this persister maps.
 	 *
 	 * @return The name of the entity which this persister maps.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Retrieve the underlying entity metamodel instance...
 	 *
 	 *@return The metamodel
 	 */
 	public EntityMetamodel getEntityMetamodel();
 
 	/**
 	 * Determine whether the given name represents a subclass entity
 	 * (or this entity itself) of the entity mapped by this persister.
 	 *
 	 * @param entityName The entity name to be checked.
 	 * @return True if the given entity name represents either the entity
 	 * mapped by this persister or one of its subclass entities; false
 	 * otherwise.
 	 */
 	public boolean isSubclassEntityName(String entityName);
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class only.
 	 * <p/>
 	 * For most implementations, this returns the complete set of table names
 	 * to which instances of the mapped entity are persisted (not accounting
 	 * for superclass entity mappings).
 	 *
 	 * @return The property spaces.
 	 */
 	public Serializable[] getPropertySpaces();
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class and its subclasses.
 	 * <p/>
 	 * Much like {@link #getPropertySpaces()}, except that here we include subclass
 	 * entity spaces.
 	 *
 	 * @return The query spaces.
 	 */
 	public Serializable[] getQuerySpaces();
 
 	/**
 	 * Determine whether this entity supports dynamic proxies.
 	 *
 	 * @return True if the entity has dynamic proxy support; false otherwise.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections.
 	 *
 	 * @return True if the entity does contain persistent collections; false otherwise.
 	 */
 	public boolean hasCollections();
 
 	/**
 	 * Determine whether any properties of this entity are considered mutable.
 	 *
 	 * @return True if any properties of the entity are mutable; false otherwise (meaning none are).
 	 */
 	public boolean hasMutableProperties();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections
 	 * which are fetchable by subselect?
 	 *
 	 * @return True if the entity contains collections fetchable by subselect; false otherwise.
 	 */
 	public boolean hasSubselectLoadableCollections();
 
 	/**
 	 * Determine whether this entity has any non-none cascading.
 	 *
 	 * @return True if the entity has any properties with a cascade other than NONE;
 	 * false otherwise (aka, no cascading).
 	 */
 	public boolean hasCascades();
 
 	/**
 	 * Determine whether instances of this entity are considered mutable.
 	 *
 	 * @return True if the entity is considered mutable; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Determine whether the entity is inherited one or more other entities.
 	 * In other words, is this entity a subclass of other entities.
 	 *
 	 * @return True if other entities extend this entity; false otherwise.
 	 */
 	public boolean isInherited();
 
 	/**
 	 * Are identifiers of this entity assigned known before the insert execution?
 	 * Or, are they generated (in the database) by the insert execution.
 	 *
 	 * @return True if identifiers for this entity are generated by the insert
 	 * execution.
 	 */
 	public boolean isIdentifierAssignedByInsert();
 
 	/**
 	 * Get the type of a particular property by name.
 	 *
 	 * @param propertyName The name of the property for which to retrieve
 	 * the type.
 	 * @return The type.
 	 * @throws org.hibernate.MappingException Typically indicates an unknown
 	 * property name.
 	 */
 	public Type getPropertyType(String propertyName) throws MappingException;
 
 	/**
 	 * Compare the two snapshots to determine if they represent dirty state.
 	 *
 	 * @param currentState The current snapshot
 	 * @param previousState The baseline snapshot
 	 * @param owner The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all dirty properties, or null if no properties
 	 * were dirty.
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
 
 	/**
 	 * Compare the two snapshots to determine if they represent modified state.
 	 *
 	 * @param old The baseline snapshot
 	 * @param current The current snapshot
 	 * @param object The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all modified properties, or null if no properties
 	 * were modified.
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
 
 	/**
 	 * Determine whether the entity has a particular property holding
 	 * the identifier value.
 	 *
 	 * @return True if the entity has a specific property holding identifier value.
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Determine whether detached instances of this entity carry their own
 	 * identifier value.
 	 * <p/>
 	 * The other option is the deprecated feature where users could supply
 	 * the id during session calls.
 	 *
 	 * @return True if either (1) {@link #hasIdentifierProperty()} or
 	 * (2) the identifier is an embedded composite identifier; false otherwise.
 	 */
 	public boolean canExtractIdOutOfEntity();
 
 	/**
 	 * Determine whether optimistic locking by column is enabled for this
 	 * entity.
 	 *
 	 * @return True if optimistic locking by column (i.e., <version/> or
 	 * <timestamp/>) is enabled; false otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the type of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or null, if not versioned.
 	 */
 	public VersionType getVersionType();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the index of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or -66, if not versioned.
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Determine whether this entity defines a natural identifier.
 	 *
 	 * @return True if the entity defines a natural id; false otherwise.
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * If the entity defines a natural id ({@link #hasNaturalIdentifier()}), which
 	 * properties make up the natural id.
 	 *
 	 * @return The indices of the properties making of the natural id; or
 	 * null, if no natural id is defined.
 	 */
 	public int[] getNaturalIdentifierProperties();
 
 	/**
 	 * Retrieve the current state of the natural-id properties from the database.
 	 *
 	 * @param id The identifier of the entity for which to retrieve the natural-id values.
 	 * @param session The session from which the request originated.
 	 * @return The natural-id snapshot.
 	 */
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
 
 	/**
 	 * Determine which identifier generation strategy is used for this entity.
 	 *
 	 * @return The identifier generation strategy.
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 
 	/**
 	 * Determine whether this entity defines any lazy properties (ala
 	 * bytecode instrumentation).
 	 *
 	 * @return True if the entity has properties mapped as lazy; false otherwise.
 	 */
 	public boolean hasLazyProperties();
 
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session);
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance
 	 */
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance, using a natively generated identifier (optional operation)
 	 */
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Delete a persistent instance
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Update a persistent instance
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException;
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the names of the class properties - doesn't have to be the names of the
 	 * actual Java properties (used for XML generation only)
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the "insertability" of the properties of this class
 	 * (does the property appear in an SQL INSERT)
 	 */
 	public boolean[] getPropertyInsertability();
 
 	/**
 	 * Which of the properties of this class are database generated values on insert?
 	 */
 	public ValueInclusion[] getPropertyInsertGenerationInclusions();
 
 	/**
 	 * Which of the properties of this class are database generated values on update?
 	 */
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
 
 	/**
 	 * Get the "updateability" of the properties of this class
 	 * (does the property appear in an SQL UPDATE)
 	 */
 	public boolean[] getPropertyUpdateability();
 
 	/**
 	 * Get the "checkability" of the properties of this class
 	 * (is the property dirty checked, does the cache need
 	 * to be updated)
 	 */
 	public boolean[] getPropertyCheckability();
 
 	/**
 	 * Get the nullability of the properties of this class
 	 */
 	public boolean[] getPropertyNullability();
 
 	/**
 	 * Get the "versionability" of the properties of this class
 	 * (is the property optimistic-locked)
 	 */
 	public boolean[] getPropertyVersionability();
 	public boolean[] getPropertyLaziness();
 	/**
 	 * Get the cascade styles of the properties (optional operation)
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles();
 
 	/**
 	 * Get the identifier type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the name of the identifier property (or return null) - need not return the
 	 * name of an actual Java property
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Should we always invalidate the cache instead of
 	 * recaching updated state
 	 */
 	public boolean isCacheInvalidationRequired();
 	/**
 	 * Should lazy properties of this entity be cached?
 	 */
 	public boolean isLazyPropertiesCacheable();
 	/**
 	 * Does this class have a cache.
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache (optional operation)
 	 */
 	public EntityRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 	
 	/**
+	 * Does this class have a natural id cache
+	 */
+	public boolean hasNaturalIdCache();
+	
+	/**
 	 * Get the NaturalId cache (optional operation)
 	 */
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
 
 	/**
 	 * Get the user-visible metadata for the class (optional operation)
 	 */
 	public ClassMetadata getClassMetadata();
 
 	/**
 	 * Is batch loading enabled?
 	 */
 	public boolean isBatchLoadable();
 
 	/**
 	 * Is select snapshot before update enabled?
 	 */
 	public boolean isSelectBeforeUpdateRequired();
 
 	/**
 	 * Get the current database state of the object, in a "hydrated" form, without
 	 * resolving identifiers
 	 * @return null if there is no row in the database
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Get the current version of the object, or return null if there is no row for
 	 * the given identifier. In the case of unversioned data, return any object
 	 * if the row exists.
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Has the class actually been bytecode instrumented?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Does this entity define any properties as being database generated on insert?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasInsertGeneratedProperties();
 
 	/**
 	 * Does this entity define any properties as being database generated on update?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasUpdateGeneratedProperties();
 
 	/**
 	 * Does this entity contain a version property that is defined
 	 * to be database generated?
 	 *
 	 * @return true if this entity contains a version property and that
 	 * property has been marked as generated.
 	 */
 	public boolean isVersionPropertyGenerated();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Called just after the entities properties have been initialized
 	 */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Called just after the entity has been reassociated with the session
 	 */
 	public void afterReassociate(Object entity, SessionImplementor session);
 
 	/**
 	 * Create a new proxy instance
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Is this a new transient instance?
 	 */
 	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Return the values of the insertable properties of the object (including backrefs)
 	 */
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
 	/**
 	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
 	 */
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the proxy interface that instances of <em>this</em> concrete class will be
 	 * cast to (optional operation).
 	 */
 	public Class getConcreteProxyClass();
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values);
 
 	/**
 	 * Set the value of a particular property
 	 */
 	public void setPropertyValue(Object object, int i, Object value);
 
 	/**
 	 * Return the (loaded) values of the mapped properties of the object (not including backrefs)
 	 */
 	public Object[] getPropertyValues(Object object);
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, int i) throws HibernateException;
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, String propertyName);
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @param entity The entity for which to get the identifier
 	 * @param session The session from which the request originated
 	 *
 	 * @return The identifier
 	 */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 	/**
 	 * Create a class instance initialized with the given identifier
 	 *
 	 * @param id The identifier value to use (may be null to represent no value)
 	 * @param session The session from which the request originated.
 	 *
 	 * @return The instantiated entity.
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
 	/**
 	 * Is the given object an instance of this entity?
 	 */
 	public boolean isInstance(Object object);
 
 	/**
 	 * Does the given instance have any uninitialized lazy properties?
 	 */
 	public boolean hasUninitializedLazyProperties(Object object);
 
 	/**
 	 * Set the identifier and version of the given instance back to its "unsaved" value.
 	 *
 	 * @param entity The entity instance
 	 * @param currentId The currently assigned identifier value.
 	 * @param currentVersion The currently assigned version value.
 	 * @param session The session from which the request originated.
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
 	/**
 	 * A request has already identified the entity-name of this persister as the mapping for the given instance.
 	 * However, we still need to account for possible subclassing and potentially re-route to the more appropriate
 	 * persister.
 	 * <p/>
 	 * For example, a request names <tt>Animal</tt> as the entity-name which gets resolved to this persister.  But the
 	 * actual instance is really an instance of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  So, here the
 	 * <tt>Animal</tt> persister is being asked to return the persister specific to <tt>Cat</tt>.
 	 * <p/>
 	 * It is also possible that the instance is actually an <tt>Animal</tt> instance in the above example in which
 	 * case we would return <tt>this</tt> from this method.
 	 *
 	 * @param instance The entity instance
 	 * @param factory Reference to the SessionFactory
 	 *
 	 * @return The appropriate persister
 	 *
 	 * @throws HibernateException Indicates that instance was deemed to not be a subclass of the entity mapped by
 	 * this persister.
 	 */
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
 
 	public EntityMode getEntityMode();
 	public EntityTuplizer getEntityTuplizer();
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index bd4e67124a..41550e06bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,255 +1,256 @@
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
 package org.hibernate.persister.internal;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * The standard Hibernate {@link PersisterFactory} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			PersistentClass.class,
 			EntityRegionAccessStrategy.class,
+			NaturalIdRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations using
 	 * an {@link EntityBinding}.
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW to ENTITY_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			EntityBinding.class,
 			EntityRegionAccessStrategy.class,
 			NaturalIdRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			Collection.class,
 			CollectionRegionAccessStrategy.class,
 			Configuration.class,
 			SessionFactoryImplementor.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations using
 	 * a {@link org.hibernate.metamodel.binding.AbstractPluralAttributeBinding}
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			AbstractPluralAttributeBinding.class,
 			CollectionRegionAccessStrategy.class,
 			MetadataImplementor.class,
 			SessionFactoryImplementor.class
 	};
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS, metadata, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, cfg );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(EntityBinding metadata,
 												 EntityRegionAccessStrategy cacheAccessStrategy,
 												 SessionFactoryImplementor factory,
 												 Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getCustomEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, cacheAccessStrategy, null, factory, cfg );
 	}
 
 	// TODO: change metadata arg type to EntityBinding when new metadata is integrated
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
 			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, cfg );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			MetadataImplementor metadata,
 			PluralAttributeBinding collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	// TODO: change collectionMetadata arg type to AbstractPluralAttributeBinding when new metadata is integrated
 	// TODO: change metadata arg type to MetadataImplementor when new metadata is integrated
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object cfg,
 			Object collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
 			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( collectionMetadata, cacheAccessStrategy, cfg, factory );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/A.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/A.java
index af1b8f7f3d..dd41cb6942 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/A.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/A.java
@@ -1,89 +1,92 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import java.util.HashSet;
 import java.util.Set;
+
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 import javax.persistence.Version;
 
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.NaturalId;
+import org.hibernate.annotations.NaturalIdCache;
 
 /**
  * @author Guenther Demetz
  */
 @Entity
 @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+@NaturalIdCache
 public class A {
 
 	@Id
 	@GeneratedValue(strategy = GenerationType.TABLE)
 	private long oid;
 
 	@Version
 	private int version;
 
 	@Column
 	@NaturalId(mutable = false)
 	private String name;
 
 	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 	@org.hibernate.annotations.OptimisticLock(excluded = true)
 	@javax.persistence.OneToMany(mappedBy = "a")
 	private Set<D> ds = new HashSet<D>();
 
 	@javax.persistence.OneToOne
 	private D singleD = null;
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public Set<D> getDs() {
 		return ds;
 	}
 
 	public void setDs(Set<D> ds) {
 		this.ds = ds;
 	}
 
 	public D getSingleD() {
 		return singleD;
 	}
 
 	public void setSingleD(D singleD) {
 		this.singleD = singleD;
 	}
 
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Citizen.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Citizen.java
index 296bef3de9..f30b3f2531 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Citizen.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Citizen.java
@@ -1,66 +1,68 @@
 //$Id$
 package org.hibernate.test.annotations.naturalid;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 
 import org.hibernate.annotations.NaturalId;
+import org.hibernate.annotations.NaturalIdCache;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
+@NaturalIdCache
 public class Citizen {
 	@Id
 	@GeneratedValue
 	private Integer id;
 	private String firstname;
 	private String lastname;
 	@NaturalId
 	@ManyToOne
 	private State state;
 	@NaturalId
 	private String ssn;
 
 
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getFirstname() {
 		return firstname;
 	}
 
 	public void setFirstname(String firstname) {
 		this.firstname = firstname;
 	}
 
 	public String getLastname() {
 		return lastname;
 	}
 
 	public void setLastname(String lastname) {
 		this.lastname = lastname;
 	}
 
 	public State getState() {
 		return state;
 	}
 
 	public void setState(State state) {
 		this.state = state;
 	}
 
 	public String getSsn() {
 		return ssn;
 	}
 
 	public void setSsn(String ssn) {
 		this.ssn = ssn;
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
index 49f6e0a417..04a43eb100 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
@@ -1,251 +1,298 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * Test case for NaturalId annotation
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class NaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testMappingProperties() {
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				Citizen.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertTrue( "Wrong number of elements", propertiesIndex.length == 2 );
 	}
 
 	@Test
 	public void testNaturalIdCached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		Criteria criteria = s.createCriteria( Citizen.class );
 		criteria.add(
 				Restrictions.naturalId().set( "ssn", "1234" ).set(
 						"state",
 						france
 				)
 		);
 		criteria.setCacheable( true );
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 		assertEquals(
 				"First query should be a miss", 1, stats
 						.getQueryCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 1, stats
 						.getQueryCachePutCount()
 		);
 
 		// query a second time - result should be cached
 		criteria.list();
 		assertEquals(
 				"Cache hits should be empty", 1, stats
 						.getQueryCacheHitCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
-	public void testNaturalIdLoaderCached() {
+	public void testNaturalIdLoaderNotCached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
+		//NaturalId cache gets populated during entity loading, need to clear it out
+		sessionFactory().getCache().evictNaturalIdRegions();
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 
 		// first query
 		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First load should be a miss", 1, stats
 						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 1, stats
 						.getNaturalIdCachePutCount()
 		);
 
-		// query a second time - result should be cached
-		citizen = (Citizen)naturalIdLoader.load();
+		// cleanup
+		tx.rollback();
+		s.close();
+	}
+
+	@Test
+	public void testNaturalIdLoaderCached() {
+		saveSomeCitizens();
+		
+		Statistics stats = sessionFactory().getStatistics();
+		assertEquals(
+				"Cache hits should be empty", 0, stats
+						.getNaturalIdCacheHitCount()
+		);
+		assertEquals(
+				"First load should be a miss", 1, stats
+						.getNaturalIdCacheMissCount()
+		);
+		assertEquals(
+				"Query result should be added to cache", 3, stats
+						.getNaturalIdCachePutCount()
+		);
+
+		Session s = openSession();
+		Transaction tx = s.beginTransaction();
+		State france = ( State ) s.load( State.class, 2 );
+		final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
+		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
+
+		//Not clearing naturalId caches, should be warm from entity loading
+		stats.setStatisticsEnabled( true );
+		stats.clear();
+		assertEquals(
+				"Cache hits should be empty", 0, stats
+						.getQueryCacheHitCount()
+		);
+
+		// first query
+		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals(
 				"Cache hits should be empty", 1, stats
 						.getNaturalIdCacheHitCount()
 		);
+		assertEquals(
+				"First load should be a miss", 0, stats
+						.getNaturalIdCacheMissCount()
+		);
+		assertEquals(
+				"Query result should be added to cache", 0, stats
+						.getNaturalIdCachePutCount()
+		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdUncached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		Criteria criteria = s.createCriteria( Citizen.class );
 		criteria.add(
 				Restrictions.naturalId().set( "ssn", "1234" ).set(
 						"state",
 						france
 				)
 		);
 		criteria.setCacheable( false );
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 0, stats
 						.getQueryCachePutCount()
 		);
 
 		// query a second time
 		criteria.list();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getQueryCacheHitCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Citizen.class, State.class,
 				NaturalIdOnManyToOne.class
 		};
 	}
 
 	private void saveSomeCitizens() {
 		Citizen c1 = new Citizen();
 		c1.setFirstname( "Emmanuel" );
 		c1.setLastname( "Bernard" );
 		c1.setSsn( "1234" );
 
 		State france = new State();
 		france.setName( "Ile de France" );
 		c1.setState( france );
 
 		Citizen c2 = new Citizen();
 		c2.setFirstname( "Gavin" );
 		c2.setLastname( "King" );
 		c2.setSsn( "000" );
 		State australia = new State();
 		australia.setName( "Australia" );
 		c2.setState( australia );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( australia );
 		s.persist( france );
 		s.persist( c1 );
 		s.persist( c2 );
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 70bf1536a2..ef9b55976c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,788 +1,794 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
+								   NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
+		public boolean hasNaturalIdCache() {
+			return false;
+		}
+
+		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionType getCollectionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index e68e364344..0f1b0e35dd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,642 +1,647 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
+			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
+	public boolean hasNaturalIdCache() {
+		return false;
+	}
+
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
index f1708801f1..45e8498d5b 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
@@ -1,227 +1,271 @@
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
 package org.hibernate.cache.ehcache.internal.nonstop;
 
 import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * Implementation of {@link NaturalIdRegionAccessStrategy} that handles {@link NonStopCacheException} using
  * {@link HibernateNonstopCacheExceptionHandler}
  *
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 public class NonstopAwareNaturalIdRegionAccessStrategy implements NaturalIdRegionAccessStrategy {
 
 	private final NaturalIdRegionAccessStrategy actualStrategy;
 	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;
 
 	/**
 	 * Constructor accepting the actual {@link NaturalIdRegionAccessStrategy} and the {@link HibernateNonstopCacheExceptionHandler}
 	 *
 	 * @param actualStrategy
 	 * @param hibernateNonstopExceptionHandler
 	 */
 	public NonstopAwareNaturalIdRegionAccessStrategy(NaturalIdRegionAccessStrategy actualStrategy,
 													  HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
 		this.actualStrategy = actualStrategy;
 		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
 	}
+	
+	@Override
+	public boolean insert(Object key, Object value) throws CacheException {
+		try {
+			return actualStrategy.insert( key, value );
+		}
+		catch ( NonStopCacheException nonStopCacheException ) {
+			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
+			return false;
+		}
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value) throws CacheException {
+		try {
+			return actualStrategy.afterInsert( key, value );
+		}
+		catch ( NonStopCacheException nonStopCacheException ) {
+			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
+			return false;
+		}
+	}
+
+	@Override
+	public boolean update(Object key, Object value) throws CacheException {
+		try {
+			return actualStrategy.update( key, value );
+		}
+		catch ( NonStopCacheException nonStopCacheException ) {
+			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
+			return false;
+		}
+	}
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
+		try {
+			return actualStrategy.afterUpdate( key, value, lock );
+		}
+		catch ( NonStopCacheException nonStopCacheException ) {
+			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
+			return false;
+		}
+	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#getRegion()
 	 */
 	public NaturalIdRegion getRegion() {
 		return actualStrategy.getRegion();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#evict(java.lang.Object)
 	 */
 	public void evict(Object key) throws CacheException {
 		try {
 			actualStrategy.evict( key );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#evictAll()
 	 */
 	public void evictAll() throws CacheException {
 		try {
 			actualStrategy.evictAll();
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#get(java.lang.Object, long)
 	 */
 	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			return actualStrategy.get( key, txTimestamp );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockItem(java.lang.Object, java.lang.Object)
 	 */
 	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		try {
 			return actualStrategy.lockItem( key, version );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockRegion()
 	 */
 	public SoftLock lockRegion() throws CacheException {
 		try {
 			return actualStrategy.lockRegion();
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object,
 	 *	  boolean)
 	 */
 	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)
 	 */
 	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#remove(java.lang.Object)
 	 */
 	public void remove(Object key) throws CacheException {
 		try {
 			actualStrategy.remove( key );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#removeAll()
 	 */
 	public void removeAll() throws CacheException {
 		try {
 			actualStrategy.removeAll();
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockItem(java.lang.Object, org.hibernate.cache.spi.access.SoftLock)
 	 */
 	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockItem( key, lock );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 */
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockRegion( lock );
 		}
 		catch ( NonStopCacheException nonStopCacheException ) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
index cbcf454aa0..6f6f8f26fb 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,99 +1,129 @@
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
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cfg.Settings;
 
 /**
  * Ehcache specific non-strict read/write NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy
 		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a non-strict read/write access strategy accessing the given NaturalId region.
 	 */
 	public NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, Settings settings) {
 		super( region, settings );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region.get( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region.contains( key ) ) {
 			return false;
 		}
 		else {
 			region.put( key, value );
 			return true;
 		}
 	}
 
 	/**
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	/**
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region.remove( key );
 	}
 
 	/**
+	 * Returns <code>false</code> since this is an asynchronous cache access strategy.
+	 */
+	public boolean insert(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	/**
+	 * Returns <code>false</code> since this is a non-strict read/write cache access strategy
+	 */
+	public boolean afterInsert(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	/**
+	 * Removes the entry since this is a non-strict read/write cache strategy.
+	 */
+	public boolean update(Object key, Object value ) throws CacheException {
+		remove( key );
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
+		unlockItem( key, lock );
+		return false;
+	}
+
+	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public void remove(Object key) throws CacheException {
 		region.remove( key );
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
index fe497383e3..993932943a 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,93 +1,126 @@
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
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cfg.Settings;
 
 /**
  * Ehcache specific read-only NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadOnlyEhcacheNaturalIdRegionAccessStrategy
 		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a read-only access strategy accessing the given NaturalId region.
 	 */
 	public ReadOnlyEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, Settings settings) {
 		super( region, settings );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region.get( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region.contains( key ) ) {
 			return false;
 		}
 		else {
 			region.put( key, value );
 			return true;
 		}
 	}
 
 	/**
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
 
 	/**
 	 * A no-op since this cache is read-only
 	 */
 	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		//throw new UnsupportedOperationException("Can't write to a readonly object");
 	}
+
+	/**
+	 * This cache is asynchronous hence a no-op
+	 */
+	public boolean insert(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean afterInsert(Object key, Object value ) throws CacheException {
+		region.put( key, value );
+		return true;
+	}
+
+	/**
+	 * Throws UnsupportedOperationException since this cache is read-only
+	 *
+	 * @throws UnsupportedOperationException always
+	 */
+	public boolean update(Object key, Object value ) throws UnsupportedOperationException {
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
+
+	/**
+	 * Throws UnsupportedOperationException since this cache is read-only
+	 *
+	 * @throws UnsupportedOperationException always
+	 */
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws UnsupportedOperationException {
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
index 0384da7789..6d287f0bf7 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,54 +1,127 @@
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
 package org.hibernate.cache.ehcache.internal.strategy;
 
+import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cfg.Settings;
 
 /**
  * Ehcache specific read/write NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadWriteEhcacheNaturalIdRegionAccessStrategy
 		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a read/write access strategy accessing the given NaturalId region.
 	 */
 	public ReadWriteEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, Settings settings) {
 		super( region, settings );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
+
+	/**
+	 * A no-op since this is an asynchronous cache access strategy.
+	 */
+	public boolean insert(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 * <p/>
+	 * Inserts will only succeed if there is no existing value mapped to this key.
+	 */
+	public boolean afterInsert(Object key, Object value ) throws CacheException {
+		region.writeLock( key );
+		try {
+			Lockable item = (Lockable) region.get( key );
+			if ( item == null ) {
+				region.put( key, new Item( value, null, region.nextTimestamp() ) );
+				return true;
+			}
+			else {
+				return false;
+			}
+		}
+		finally {
+			region.writeUnlock( key );
+		}
+	}
+
+	/**
+	 * A no-op since this is an asynchronous cache access strategy.
+	 */
+	public boolean update(Object key, Object value )
+			throws CacheException {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 * <p/>
+	 * Updates will only succeed if this entry was locked by this transaction and exclusively this transaction for the
+	 * duration of this transaction.  It is important to also note that updates will fail if the soft-lock expired during
+	 * the course of this transaction.
+	 */
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
+		//what should we do with previousVersion here?
+		region.writeLock( key );
+		try {
+			Lockable item = (Lockable) region.get( key );
+
+			if ( item != null && item.isUnlockable( lock ) ) {
+				Lock lockItem = (Lock) item;
+				if ( lockItem.wasLockedConcurrently() ) {
+					decrementLock( key, lockItem );
+					return false;
+				}
+				else {
+					region.put( key, new Item( value, null, region.nextTimestamp() ) );
+					return true;
+				}
+			}
+			else {
+				handleLockExpiry( key, item );
+				return false;
+			}
+		}
+		finally {
+			region.writeUnlock( key );
+		}
+	}
 }
\ No newline at end of file
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
index 42e30c9046..f87461312a 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,127 +1,168 @@
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
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import net.sf.ehcache.Ehcache;
 import net.sf.ehcache.Element;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cfg.Settings;
 
 /**
  * JTA NaturalIdRegionAccessStrategy.
  *
  * @author Chris Dennis
  * @author Ludovic Orban
  * @author Alex Snaps
  */
 public class TransactionalEhcacheNaturalIdRegionAccessStrategy
 		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	private final Ehcache ehcache;
 
 	/**
 	 * Construct a new collection region access strategy.
 	 *
 	 * @param region the Hibernate region.
 	 * @param ehcache the cache.
 	 * @param settings the Hibernate settings.
 	 */
 	public TransactionalEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, Ehcache ehcache, Settings settings) {
 		super( region, settings );
 		this.ehcache = ehcache;
 	}
 
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean afterInsert(Object key, Object value ) {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) {
+		return false;
+	}
+
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			Element element = ehcache.get( key );
 			return element == null ? null : element.getObjectValue();
 		}
 		catch ( net.sf.ehcache.CacheException e ) {
 			throw new CacheException( e );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	public boolean insert(Object key, Object value ) throws CacheException {
+		//OptimisticCache? versioning?
+		try {
+			ehcache.put( new Element( key, value ) );
+			return true;
+		}
+		catch ( net.sf.ehcache.CacheException e ) {
+			throw new CacheException( e );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
 	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean putFromLoad(Object key, Object value, long txTimestamp,
 							   Object version, boolean minimalPutOverride) throws CacheException {
 		try {
 			if ( minimalPutOverride && ehcache.get( key ) != null ) {
 				return false;
 			}
 			//OptimisticCache? versioning?
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch ( net.sf.ehcache.CacheException e ) {
 			throw new CacheException( e );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public void remove(Object key) throws CacheException {
 		try {
 			ehcache.remove( key );
 		}
 		catch ( net.sf.ehcache.CacheException e ) {
 			throw new CacheException( e );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		// no-op
 	}
 
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean update(Object key, Object value ) throws CacheException {
+		try {
+			ehcache.put( new Element( key, value ) );
+			return true;
+		}
+		catch ( net.sf.ehcache.CacheException e ) {
+			throw new CacheException( e );
+		}
+	}
+
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
index 548c28bc7b..365586b9fa 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,578 +1,591 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.ejb.test.ejb3configuration;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 import java.io.Serializable;
 import java.util.Comparator;
+import java.util.HashMap;
 import java.util.Map;
 
 import org.junit.Assert;
 import org.junit.Test;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest {
     @Test
 	public void testPersisterClassProvider() {
 		Ejb3Configuration conf = new Ejb3Configuration();
 		conf.getProperties().put( PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class );
 		conf.addAnnotatedClass( Bell.class );
 		try {
 			final EntityManagerFactory entityManagerFactory = conf.buildEntityManagerFactory();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
             Assert.assertNotNull( e.getCause() );
 			Assert.assertNotNull( e.getCause().getCause() );
 			Assert.assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
 	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		@SuppressWarnings( {"UnusedParameters"})
 		public GoofyProvider(
 				org.hibernate.mapping.PersistentClass persistentClass,
 				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
+				NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				SessionFactoryImplementor sf,
 				Mapping mapping) {
 			throw new GoofyException();
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( getEntityName() );
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 		
         @Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
-
+		
 		@Override
+        public boolean hasNaturalIdCache() {
+            return false;
+        }
+
+        @Override
+        public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
+            return null;
+        }
+
+        @Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
index ca0fd82902..cae0b285d7 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
@@ -1,53 +1,75 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.testing.cache;
 
+import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Eric Dalquist
  */
 class BaseNaturalIdRegionAccessStrategy extends BaseRegionAccessStrategy implements NaturalIdRegionAccessStrategy {
 	private final NaturalIdRegionImpl region;
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
+	@Override
+	public boolean insert(Object key, Object value ) throws CacheException {
+		return putFromLoad( key, value, 0 , null );
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value ) throws CacheException {
+		return true;
+	}
+
+	@Override
+	public boolean update(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
+		return false;
+	}
+
 	BaseNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		this.region = region;
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
index d2092e1c5b..c0908a0fd3 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
@@ -1,62 +1,121 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.testing.cache;
 
 import java.util.Comparator;
 
+import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Eric Dalquist
  */
 class ReadWriteNaturalIdRegionAccessStrategy extends AbstractReadWriteAccessStrategy
 		implements NaturalIdRegionAccessStrategy {
 
 	private final NaturalIdRegionImpl region;
 
 	ReadWriteNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
+	public boolean insert(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	@Override
+	public boolean update(Object key, Object value ) throws CacheException {
+		return false;
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value ) throws CacheException {
+
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) region.get( key );
+			if ( item == null ) {
+				region.put( key, new Item( value, null, region.nextTimestamp() ) );
+				return true;
+			}
+			else {
+				return false;
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) region.get( key );
+
+			if ( item != null && item.isUnlockable( lock ) ) {
+				Lock lockItem = (Lock) item;
+				if ( lockItem.wasLockedConcurrently() ) {
+					decrementLock( key, lockItem );
+					return false;
+				}
+				else {
+					region.put( key, new Item( value, null, region.nextTimestamp() ) );
+					return true;
+				}
+			}
+			else {
+				handleLockExpiry( key, item );
+				return false;
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+	@Override
 	Comparator getVersionComparator() {
 		return region.getCacheDataDescription().getVersionComparator();
 	}
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 }
