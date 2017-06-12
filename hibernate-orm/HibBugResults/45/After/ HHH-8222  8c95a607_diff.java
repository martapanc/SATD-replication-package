diff --git a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
index 33f76243cf..d847c28a4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
@@ -1,158 +1,169 @@
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 import org.hibernate.procedure.ProcedureCall;
+import org.hibernate.procedure.ProcedureCallMemento;
 
 /**
  * Contract methods shared between {@link Session} and {@link StatelessSession}.
  * 
  * @author Steve Ebersole
  */
 public interface SharedSessionContract extends Serializable {
 	/**
 	 * Obtain the tenant identifier associated with this session.
 	 *
 	 * @return The tenant identifier associated with this session, or {@code null}
 	 */
 	public String getTenantIdentifier();
 
 	/**
 	 * Begin a unit of work and return the associated {@link Transaction} object.  If a new underlying transaction is
 	 * required, begin the transaction.  Otherwise continue the new work in the context of the existing underlying
 	 * transaction.
 	 *
 	 * @return a Transaction instance
 	 *
 	 * @see #getTransaction
 	 */
 	public Transaction beginTransaction();
 
 	/**
 	 * Get the {@link Transaction} instance associated with this session.  The concrete type of the returned
 	 * {@link Transaction} object is determined by the {@code hibernate.transaction_factory} property.
 	 *
 	 * @return a Transaction instance
 	 */
 	public Transaction getTransaction();
 
 	/**
 	 * Create a {@link Query} instance for the named query string defined in the metadata.
 	 *
 	 * @param queryName the name of a query defined externally
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query getNamedQuery(String queryName);
 
 	/**
 	 * Create a {@link Query} instance for the given HQL query string.
 	 *
 	 * @param queryString The HQL query
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query createQuery(String queryString);
 
 	/**
 	 * Create a {@link SQLQuery} instance for the given SQL query string.
 	 *
 	 * @param queryString The SQL query
 	 * 
 	 * @return The query instance for manipulation and execution
 	 */
 	public SQLQuery createSQLQuery(String queryString);
 
 	/**
+	 * Gets a ProcedureCall based on a named template
+	 *
+	 * @param name The name given to the template
+	 *
+	 * @return The ProcedureCall
+	 *
+	 * @see javax.persistence.NamedStoredProcedureQuery
+	 */
+	public ProcedureCall getNamedProcedureCall(String name);
+
+	/**
 	 * Creates a call to a stored procedure.
 	 *
 	 * @param procedureName The name of the procedure.
 	 *
 	 * @return The representation of the procedure call.
 	 */
 	public ProcedureCall createStoredProcedureCall(String procedureName);
 
 	/**
 	 * Creates a call to a stored procedure with specific result set entity mappings.  Each class named
 	 * is considered a "root return".
 	 *
 	 * @param procedureName The name of the procedure.
 	 * @param resultClasses The entity(s) to map the result on to.
 	 *
 	 * @return The representation of the procedure call.
 	 */
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses);
 
 	/**
 	 * Creates a call to a stored procedure with specific result set entity mappings.
 	 *
 	 * @param procedureName The name of the procedure.
 	 * @param resultSetMappings The explicit result set mapping(s) to use for mapping the results
 	 *
 	 * @return The representation of the procedure call.
 	 */
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings);
 
 	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors).
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass);
 
 	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors), using a specific
 	 * alias.
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass, String alias);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name.
 	 *
 	 * @param entityName The entity name
 
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name, using a specific alias.
 	 *
 	 * @param entityName The entity name
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName, String alias);
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 0414122b4f..77034482f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1360 +1,1402 @@
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
+import javax.persistence.NamedStoredProcedureQueries;
+import javax.persistence.NamedStoredProcedureQuery;
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
+
+		{
+			final List<NamedStoredProcedureQuery> annotations =
+					(List<NamedStoredProcedureQuery>) defaults.get( NamedStoredProcedureQuery.class );
+			if ( annotations != null ) {
+				for ( NamedStoredProcedureQuery annotation : annotations ) {
+					QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings );
+				}
+			}
+		}
+
+		{
+			final List<NamedStoredProcedureQueries> annotations =
+					(List<NamedStoredProcedureQueries>) defaults.get( NamedStoredProcedureQueries.class );
+			if ( annotations != null ) {
+				for ( NamedStoredProcedureQueries annotation : annotations ) {
+					for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
+						QueryBinder.bindNamedStoredProcedureQuery( queryAnnotation, mappings );
+					}
+				}
+			}
+		}
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
+
+		// NamedStoredProcedureQuery handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		{
+			final NamedStoredProcedureQuery annotation = annotatedElement.getAnnotation( NamedStoredProcedureQuery.class );
+			if ( annotation != null ) {
+				QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings );
+			}
+		}
+
+		// NamedStoredProcedureQueries handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		{
+			final NamedStoredProcedureQueries annotation = annotatedElement.getAnnotation( NamedStoredProcedureQueries.class );
+			if ( annotation != null ) {
+				for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
+					QueryBinder.bindNamedStoredProcedureQuery( queryAnnotation, mappings );
+				}
+			}
+		}
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index cc6139a56a..ea3f4b8a2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,3749 +1,3765 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
+import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.util.xml.ErrorLogger;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.Origin;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XMLHelper;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.internal.util.xml.XmlDocumentImpl;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.metamodel.spi.TypeContributions;
 import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 /**
  * An instance of <tt>Configuration</tt> allows the application
  * to specify properties and mapping documents to be used when
  * creating a <tt>SessionFactory</tt>. Usually an application will create
  * a single <tt>Configuration</tt>, build a single instance of
  * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in
  * threads servicing client requests. The <tt>Configuration</tt> is meant
  * only as an initialization-time object. <tt>SessionFactory</tt>s are
  * immutable and do not retain any association back to the
  * <tt>Configuration</tt>.<br>
  * <br>
  * A new <tt>Configuration</tt> will use the properties specified in
  * <tt>hibernate.properties</tt> by default.
  * <p/>
  * NOTE : This will be replaced by use of {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder} and
  * {@link org.hibernate.metamodel.MetadataSources} instead after the 4.0 release at which point this class will become
  * deprecated and scheduled for removal in 5.0.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6183">HHH-6183</a>,
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-2578">HHH-2578</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6586">HHH-6586</a> for details
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
 	public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
 
 	/**
 	 * Class name of the class needed to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_CLASS = "org.hibernate.search.event.EventListenerRegister";
 
 	/**
 	 * Method to call to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_METHOD = "enableHibernateSearch";
 
 	protected MetadataSourceQueue metadataSourceQueue;
 	private transient ReflectionManager reflectionManager;
 
 	protected Map<String, PersistentClass> classes;
 	protected Map<String, String> imports;
 	protected Map<String, Collection> collections;
 	protected Map<String, Table> tables;
 	protected List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects;
 
 	protected Map<String, NamedQueryDefinition> namedQueries;
 	protected Map<String, NamedSQLQueryDefinition> namedSqlQueries;
+	protected Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 
 	protected Map<String, TypeDef> typeDefs;
 	protected Map<String, FilterDefinition> filterDefinitions;
 	protected Map<String, FetchProfile> fetchProfiles;
 
 	protected Map tableNameBinding;
 	protected Map columnNameBindingPerTable;
 
 	protected List<SecondPass> secondPasses;
 	protected List<Mappings.PropertyReference> propertyReferences;
 	protected Map<ExtendsQueueEntry, ?> extendsQueue;
 
 	protected Map<String, SQLFunction> sqlFunctions;
 	
 	private TypeResolver typeResolver = new TypeResolver();
 	private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
 
 	private EntityTuplizerFactory entityTuplizerFactory;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 
 	private Interceptor interceptor;
 	private Properties properties;
 	private EntityResolver entityResolver;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 
 	protected transient XMLHelper xmlHelper;
 	protected NamingStrategy namingStrategy;
 	private SessionFactoryObserver sessionFactoryObserver;
 
 	protected final SettingsFactory settingsFactory;
 
 	private transient Mapping mapping = buildMapping();
 
 	private MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private Map<Class<?>, org.hibernate.mapping.MappedSuperclass> mappedSuperClasses;
 
 	private Map<String, IdGenerator> namedGenerators;
 	private Map<String, Map<String, Join>> joins;
 	private Map<String, AnnotatedClassType> classTypes;
 	private Set<String> defaultNamedQueryNames;
 	private Set<String> defaultNamedNativeQueryNames;
 	private Set<String> defaultSqlResultSetMappingNames;
 	private Set<String> defaultNamedGenerators;
 	private Map<String, Properties> generatorTables;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private List<CacheHolder> caches;
 	private boolean inSecondPass = false;
 	private boolean isDefaultProcessed = false;
 	private boolean isValidatorNotPresentLogged;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 	private boolean specjProprietarySyntaxEnabled;
 
 	private ConcurrentHashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 
 	protected Configuration(SettingsFactory settingsFactory) {
 		this.settingsFactory = settingsFactory;
 		reset();
 	}
 
 	public Configuration() {
 		this( new SettingsFactory() );
 	}
 
 	protected void reset() {
 		metadataSourceQueue = new MetadataSourceQueue();
 		createReflectionManager();
 
 		classes = new HashMap<String,PersistentClass>();
 		imports = new HashMap<String,String>();
 		collections = new HashMap<String,Collection>();
 		tables = new TreeMap<String,Table>();
 
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 
 		typeDefs = new HashMap<String,TypeDef>();
 		filterDefinitions = new HashMap<String, FilterDefinition>();
 		fetchProfiles = new HashMap<String, FetchProfile>();
 		auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 		tableNameBinding = new HashMap();
 		columnNameBindingPerTable = new HashMap();
 
 		secondPasses = new ArrayList<SecondPass>();
 		propertyReferences = new ArrayList<Mappings.PropertyReference>();
 		extendsQueue = new HashMap<ExtendsQueueEntry, String>();
 
 		xmlHelper = new XMLHelper();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = Environment.getProperties();
 		entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
 
 		sqlFunctions = new HashMap<String, SQLFunction>();
 
 		entityTuplizerFactory = new EntityTuplizerFactory();
 //		componentTuplizerFactory = new ComponentTuplizerFactory();
 
 		identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 		mappedSuperClasses = new HashMap<Class<?>, MappedSuperclass>();
 
 		metadataSourcePrecedence = Collections.emptyList();
 
 		namedGenerators = new HashMap<String, IdGenerator>();
 		joins = new HashMap<String, Map<String, Join>>();
 		classTypes = new HashMap<String, AnnotatedClassType>();
 		generatorTables = new HashMap<String, Properties>();
 		defaultNamedQueryNames = new HashSet<String>();
 		defaultNamedNativeQueryNames = new HashSet<String>();
 		defaultSqlResultSetMappingNames = new HashSet<String>();
 		defaultNamedGenerators = new HashSet<String>();
 		uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
 		jpaIndexHoldersByTable = new HashMap<Table,List<JPAIndexHolder>>(  );
 		mappedByResolver = new HashMap<String, String>();
 		propertyRefResolver = new HashMap<String, String>();
 		caches = new ArrayList<CacheHolder>();
 		namingStrategy = EJB3NamingStrategy.INSTANCE;
 		setEntityResolver( new EJB3DTDEntityResolver() );
 		anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		specjProprietarySyntaxEnabled = System.getProperty( "hibernate.enable_specj_proprietary_syntax" ) != null;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	public ReflectionManager getReflectionManager() {
 		return reflectionManager;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	/**
 	 * Iterate the entity mappings
 	 *
 	 * @return Iterator of the entity mappings currently contained in the configuration.
 	 */
 	public Iterator<PersistentClass> getClassMappings() {
 		return classes.values().iterator();
 	}
 
 	/**
 	 * Iterate the collection mappings
 	 *
 	 * @return Iterator of the collection mappings currently contained in the configuration.
 	 */
 	public Iterator getCollectionMappings() {
 		return collections.values().iterator();
 	}
 
 	/**
 	 * Iterate the table mappings
 	 *
 	 * @return Iterator of the table mappings currently contained in the configuration.
 	 */
 	public Iterator<Table> getTableMappings() {
 		return tables.values().iterator();
 	}
 
 	/**
 	 * Iterate the mapped super class mappings
 	 * EXPERIMENTAL Consider this API as PRIVATE
 	 *
 	 * @return iterator over the MappedSuperclass mapping currently contained in the configuration.
 	 */
 	public Iterator<MappedSuperclass> getMappedSuperclassMappings() {
 		return mappedSuperClasses.values().iterator();
 	}
 
 	/**
 	 * Get the mapping for a particular entity
 	 *
 	 * @param entityName An entity name.
 	 * @return the entity mapping information
 	 */
 	public PersistentClass getClassMapping(String entityName) {
 		return classes.get( entityName );
 	}
 
 	/**
 	 * Get the mapping for a particular collection role
 	 *
 	 * @param role a collection role
 	 * @return The collection mapping information
 	 */
 	public Collection getCollectionMapping(String role) {
 		return collections.get( role );
 	}
 
 	/**
 	 * Set a custom entity resolver. This entity resolver must be
 	 * set before addXXX(misc) call.
 	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
 	 *
 	 * @param entityResolver entity resolver to use
 	 */
 	public void setEntityResolver(EntityResolver entityResolver) {
 		this.entityResolver = entityResolver;
 	}
 
 	public EntityResolver getEntityResolver() {
 		return entityResolver;
 	}
 
 	/**
 	 * Retrieve the user-supplied delegate to handle non-existent entity
 	 * scenarios.  May be null.
 	 *
 	 * @return The user-supplied delegate
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	/**
 	 * Specify a user-supplied delegate to be used to handle scenarios where an entity could not be
 	 * located by specified id.  This is mainly intended for EJB3 implementations to be able to
 	 * control how proxy initialization errors should be handled...
 	 *
 	 * @param entityNotFoundDelegate The delegate to use
 	 */
 	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates inability to locate or parse
 	 * the specified mapping file.
 	 * @see #addFile(java.io.File)
 	 */
 	public Configuration addFile(String xmlFile) throws MappingException {
 		return addFile( new File( xmlFile ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.  Historically this could
 	 * have indicated a problem parsing the XML document, but that is now delayed until after {@link #buildMappings}
 	 */
 	public Configuration addFile(final File xmlFile) throws MappingException {
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		final String name =  xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 		add( inputSource, "file", name );
 		return this;
 	}
 
 	private XmlDocument add(InputSource inputSource, String originType, String originName) {
 		return add( inputSource, new OriginImpl( originType, originName ) );
 	}
 
 	private XmlDocument add(InputSource inputSource, Origin origin) {
 		XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument( entityResolver, inputSource, origin );
 		add( metadataXml );
 		return metadataXml;
 	}
 
 	public void add(XmlDocument metadataXml) {
 		if ( inSecondPass || !isOrmXml( metadataXml ) ) {
 			metadataSourceQueue.add( metadataXml );
 		}
 		else {
 			final MetadataProvider metadataProvider = ( (MetadataProviderInjector) reflectionManager ).getMetadataProvider();
 			JPAMetadataProvider jpaMetadataProvider = ( JPAMetadataProvider ) metadataProvider;
 			List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument( metadataXml.getDocumentTree() );
 			for ( String className : classNames ) {
 				try {
 					metadataSourceQueue.add( reflectionManager.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to load class defined in XML: " + className, e );
 				}
 			}
 		}
 	}
 
 	private static boolean isOrmXml(XmlDocument xmlDocument) {
 		return "entity-mappings".equals( xmlDocument.getDocumentTree().getRootElement().getName() );
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation
 	 * of the DOM structure of a particular mapping.  It is saved from a previous
 	 * call as a file with the name <tt>xmlFile + ".bin"</tt> where xmlFile is
 	 * the name of the original mapping file.
 	 * </p>
 	 * If a cached <tt>xmlFile + ".bin"</tt> exists and is newer than
 	 * <tt>xmlFile</tt> the <tt>".bin"</tt> file will be read directly. Otherwise
 	 * xmlFile is read and then serialized to <tt>xmlFile + ".bin"</tt> for use
 	 * the next time.
 	 *
 	 * @param xmlFile The cacheable mapping file to be added.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 */
 	public Configuration addCacheableFile(File xmlFile) throws MappingException {
 		File cachedFile = determineCachedDomFile( xmlFile );
 
 		try {
 			return addCacheableFileStrictly( xmlFile );
 		}
 		catch ( SerializationException e ) {
 			LOG.unableToDeserializeCache( cachedFile.getPath(), e );
 		}
 		catch ( FileNotFoundException e ) {
 			LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			LOG.debugf( "Writing cache file for: %s to: %s", xmlFile, cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( Exception e ) {
 			LOG.unableToWriteCachedFile( cachedFile.getPath(), e.getMessage() );
 		}
 
 		return this;
 	}
 
 	private File determineCachedDomFile(File xmlFile) {
 		return new File( xmlFile.getAbsolutePath() + ".bin" );
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param xmlFile The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
 		final File cachedFile = determineCachedDomFile( xmlFile );
 
 		final boolean useCachedFile = xmlFile.exists()
 				&& cachedFile.exists()
 				&& xmlFile.lastModified() < cachedFile.lastModified();
 
 		if ( ! useCachedFile ) {
 			throw new FileNotFoundException( "Cached file could not be found or could not be used" );
 		}
 
 		LOG.readingCachedMappings( cachedFile );
 		Document document = ( Document ) SerializationHelper.deserialize( new FileInputStream( cachedFile ) );
 		add( new XmlDocumentImpl( document, "file", xmlFile.getAbsolutePath() ) );
 		return this;
 	}
 
 	/**
 	 * Add a cacheable mapping file.
 	 *
 	 * @param xmlFile The name of the file to be added.  This must be in a form
 	 * useable to simply construct a {@link java.io.File} instance.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public Configuration addCacheableFile(String xmlFile) throws MappingException {
 		return addCacheableFile( new File( xmlFile ) );
 	}
 
 
 	/**
 	 * Read mappings from a <tt>String</tt>
 	 *
 	 * @param xml an XML string
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates problems parsing the
 	 * given XML string
 	 */
 	public Configuration addXML(String xml) throws MappingException {
 		LOG.debugf( "Mapping XML:\n%s", xml );
 		final InputSource inputSource = new InputSource( new StringReader( xml ) );
 		add( inputSource, "string", "XML String" );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a <tt>URL</tt>
 	 *
 	 * @param url The url for the mapping document to be read.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the URL or processing
 	 * the mapping document.
 	 */
 	public Configuration addURL(URL url) throws MappingException {
 		final String urlExternalForm = url.toExternalForm();
 
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		try {
 			add( url.openStream(), "URL", urlExternalForm );
 		}
 		catch ( IOException e ) {
 			throw new InvalidMappingException( "Unable to open url stream [" + urlExternalForm + "]", "URL", urlExternalForm, e );
 		}
 		return this;
 	}
 
 	private XmlDocument add(InputStream inputStream, final String type, final String name) {
 		final InputSource inputSource = new InputSource( inputStream );
 		try {
 			return add( inputSource, type, name );
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch ( IOException ignore ) {
 				LOG.trace( "Was unable to close input stream");
 			}
 		}
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 */
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		LOG.debugf( "Mapping Document:\n%s", doc );
 
 		final Document document = xmlHelper.createDOMReader().read( doc );
 		add( new XmlDocumentImpl( document, "unknown", null ) );
 
 		return this;
 	}
 
 	/**
 	 * Read mappings from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the stream, or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		add( xmlInputStream, "input stream", null );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resource (i.e. classpath lookup).
 	 *
 	 * @param resourceName The resource name
 	 * @param classLoader The class loader to use.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		InputStream resourceInputStream = classLoader.getResourceAsStream( resourceName );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup)
 	 * trying different class loaders.
 	 *
 	 * @param resourceName The resource name
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
 		InputStream resourceInputStream = null;
 		if ( contextClassLoader != null ) {
 			resourceInputStream = contextClassLoader.getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			resourceInputStream = Environment.class.getClassLoader().getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class
 	 * named <tt>foo.bar.Foo</tt> is mapped by a file <tt>foo/bar/Foo.hbm.xml</tt>
 	 * which can be resolved as a classpath resource.
 	 *
 	 * @param persistentClass The mapped class
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addClass(Class persistentClass) throws MappingException {
 		String mappingResourceName = persistentClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		LOG.readingMappingsFromResource( mappingResourceName );
 		return addResource( mappingResourceName, persistentClass.getClassLoader() );
 	}
 
 	/**
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		XClass xClass = reflectionManager.toXClass( annotatedClass );
 		metadataSourceQueue.add( xClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws MappingException in case there is an error in the mapping data
 	 */
 	public Configuration addPackage(String packageName) throws MappingException {
 		LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			LOG.unableToParseMetadata( packageName );
 			throw me;
 		}
 	}
 
 	/**
 	 * Read all mappings from a jar file
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addJar(File jar) throws MappingException {
 		LOG.searchingForMappingDocuments( jar.getName() );
 		JarFile jarFile = null;
 		try {
 			try {
 				jarFile = new JarFile( jar );
 			}
 			catch (IOException ioe) {
 				throw new InvalidMappingException(
 						"Could not read mapping documents from jar: " + jar.getName(), "jar", jar.getName(),
 						ioe
 				);
 			}
 			Enumeration jarEntries = jarFile.entries();
 			while ( jarEntries.hasMoreElements() ) {
 				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
 				if ( ze.getName().endsWith( ".hbm.xml" ) ) {
 					LOG.foundMappingDocument( ze.getName() );
 					try {
 						addInputStream( jarFile.getInputStream( ze ) );
 					}
 					catch (Exception e) {
 						throw new InvalidMappingException(
 								"Could not read mapping documents from jar: " + jar.getName(),
 								"jar",
 								jar.getName(),
 								e
 						);
 					}
 				}
 			}
 		}
 		finally {
 			try {
 				if ( jarFile != null ) {
 					jarFile.close();
 				}
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseJar( ioe.getMessage() );
 			}
 		}
 
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addDirectory(File dir) throws MappingException {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Create a new <tt>Mappings</tt> to add class and collection mappings to.
 	 *
 	 * @return The created mappings
 	 */
 	public Mappings createMappings() {
 		return new MappingsImpl();
 	}
 
 
 	@SuppressWarnings({ "unchecked" })
 	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
 		TreeMap generators = new TreeMap();
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		for ( PersistentClass pc : classes.values() ) {
 			if ( !pc.isInherited() ) {
 				IdentifierGenerator ig = pc.getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						(RootClass) pc
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 				else if ( ig instanceof IdentifierGeneratorAggregator ) {
 					( (IdentifierGeneratorAggregator) ig ).registerPersistentGenerators( generators );
 				}
 			}
 		}
 
 		for ( Collection collection : collections.values() ) {
 			if ( collection.isIdentified() ) {
 				IdentifierGenerator ig = ( ( IdentifierCollection ) collection ).getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						null
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 			}
 		}
 
 		return generators.values().iterator();
 	}
 
 	/**
 	 * Generate DDL for dropping tables
 	 *
 	 * @param dialect The dialect for which to generate the drop script
 
 	 * @return The sequence of DDL commands to drop the schema objects
 
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		// drop them in reverse order in case db needs it done that way...
 		{
 			ListIterator itr = auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 			while ( itr.hasPrevious() ) {
 				AuxiliaryDatabaseObject object = (AuxiliaryDatabaseObject) itr.previous();
 				if ( object.appliesToDialect( dialect ) ) {
 					script.add( object.sqlDropString( dialect, defaultCatalog, defaultSchema ) );
 				}
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			Iterator itr = getTableMappings();
 			while ( itr.hasNext() ) {
 				Table table = (Table) itr.next();
 				if ( table.isPhysicalTable() ) {
 					Iterator subItr = table.getForeignKeyIterator();
 					while ( subItr.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subItr.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlDropString(
 											dialect,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 			}
 		}
 
 
 		Iterator itr = getTableMappings();
 		while ( itr.hasNext() ) {
 
 			Table table = (Table) itr.next();
 			if ( table.isPhysicalTable() ) {
 
 				/*Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
 						script.add( index.sqlDropString(dialect) );
 					}
 				}*/
 
 				script.add(
 						table.sqlDropString(
 								dialect,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 
 			}
 
 		}
 
 		itr = iterateGenerators( dialect );
 		while ( itr.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) itr.next() ).sqlDropStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 *
 	 * @return The sequence of DDL commands to create the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 				script.add(
 						table.sqlCreateString(
 								dialect,
 								mapping,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				Iterator subIter = table.getUniqueKeyIterator();
 				while ( subIter.hasNext() ) {
 					UniqueKey uk = (UniqueKey) subIter.next();
 					String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 					if (constraintString != null) script.add( constraintString );
 				}
 
 
 				subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlCreateString(
 											dialect, mapping,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				script.add( auxiliaryDatabaseObject.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 * 
 	 * @deprecated Use {@link #generateSchemaUpdateScriptList(Dialect, DatabaseMetadata)} instead
 	 */
 	@SuppressWarnings({ "unchecked" })
 	@Deprecated
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		List<SchemaUpdateScript> scripts = generateSchemaUpdateScriptList( dialect, databaseMetadata );
 		return SchemaUpdateScript.toStringArray( scripts );
 	}
 	
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public List<SchemaUpdateScript> generateSchemaUpdateScriptList(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		UniqueConstraintSchemaUpdateStrategy constraintMethod = UniqueConstraintSchemaUpdateStrategy.interpret( properties
 				.get( Environment.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY ) );
 
 		List<SchemaUpdateScript> scripts = new ArrayList<SchemaUpdateScript>();
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema();
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), tableSchema,
 						tableCatalog, table.isQuoted() );
 				if ( tableInfo == null ) {
 					scripts.add( new SchemaUpdateScript( table.sqlCreateString( dialect, mapping, tableCatalog,
 							tableSchema ), false ) );
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings( dialect, mapping, tableInfo, tableCatalog,
 							tableSchema );
 					while ( subiter.hasNext() ) {
 						scripts.add( new SchemaUpdateScript( subiter.next(), false ) );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					scripts.add( new SchemaUpdateScript( comments.next(), false ) );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema();
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), tableSchema,
 						tableCatalog, table.isQuoted() );
 
 				if (! constraintMethod.equals( UniqueConstraintSchemaUpdateStrategy.SKIP )) {
 					Iterator uniqueIter = table.getUniqueKeyIterator();
 					while ( uniqueIter.hasNext() ) {
 						final UniqueKey uniqueKey = (UniqueKey) uniqueIter.next();
 						// Skip if index already exists. Most of the time, this
 						// won't work since most Dialects use Constraints. However,
 						// keep it for the few that do use Indexes.
 						if ( tableInfo != null && StringHelper.isNotEmpty( uniqueKey.getName() ) ) {
 							final IndexMetadata meta = tableInfo.getIndexMetadata( uniqueKey.getName() );
 							if ( meta != null ) {
 								continue;
 							}
 						}
 						String constraintString = uniqueKey.sqlCreateString( dialect, mapping, tableCatalog, tableSchema );
 						if ( constraintString != null && !constraintString.isEmpty() )
 							if ( constraintMethod.equals( UniqueConstraintSchemaUpdateStrategy.DROP_RECREATE_QUIETLY ) ) {
 								String constraintDropString = uniqueKey.sqlDropString( dialect, tableCatalog, tableCatalog );
 								scripts.add( new SchemaUpdateScript( constraintDropString, true) );
 							}
 							scripts.add( new SchemaUpdateScript( constraintString, true) );
 					}
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || ( tableInfo.getForeignKeyMetadata( fk ) == null && (
 							// Icky workaround for MySQL bug:
 									!( dialect instanceof MySQLDialect ) || tableInfo.getIndexMetadata( fk.getName() ) == null ) );
 							if ( create ) {
 								scripts.add( new SchemaUpdateScript( fk.sqlCreateString( dialect, mapping,
 										tableCatalog, tableSchema ), false ) );
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					scripts.add( new SchemaUpdateScript( index.sqlCreateString( dialect, mapping, tableCatalog,
 							tableSchema ), false ) );
 				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				scripts.addAll( SchemaUpdateScript.fromStringArray( lines, false ) );
 			}
 		}
 
 		return scripts;
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
 		LOG.trace( "Starting secondPassCompile() processing" );
 		
 		// TEMPORARY
 		// Ensure the correct ClassLoader is used in commons-annotations.
 		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				Map defaults = reflectionManager.getDefaults();
 				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 				// Set default schema name if orm.xml declares it.
 				final String schema = (String) defaults.get( "schema" );
 				if ( StringHelper.isNotEmpty( schema ) ) {
 					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
 				}
 				// Set default catalog name if orm.xml declares it.
 				final String catalog = (String) defaults.get( "catalog" );
 				if ( StringHelper.isNotEmpty( catalog ) ) {
 					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns() );
 			}
 		}
 		
 		for(Table table : jpaIndexHoldersByTable.keySet()){
 			final List<JPAIndexHolder> jpaIndexHolders = jpaIndexHoldersByTable.get( table );
 			for ( JPAIndexHolder holder : jpaIndexHolders ) {
 				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns(), holder.getOrdering(), holder.isUnique() );
 			}
 		}
 		
 		Thread.currentThread().setContextClassLoader( tccl );
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
 		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = quotedTableName(classMapping.getTable());
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = quotedTableName(sp.getValue().getTable());
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private String quotedTableName(Table table) {
 		return Table.qualify( table.getCatalog(), table.getQuotedSchema(), table.getQuotedName() );
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
 			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = (RuntimeException) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames){
 		buildUniqueKeyFromColumnNames( table, keyName, columnNames, null, true );
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames, String[] orderings, boolean unique) {
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			String column = columnNames[index];
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( column, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( column ) );
 			}
 		}
 		
 		if ( StringHelper.isEmpty( keyName ) ) {
 			keyName = Constraint.generateName( "UK_", table, columns );
 		}
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 		
 		if ( unique ) {
 			UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					uk.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 		else {
 			Index index = table.getOrCreateIndex( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					index.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		LOG.debug( "Processing extends queue" );
 		processExtendsQueue();
 
 		LOG.debug( "Processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		LOG.debug( "Processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		LOG.debug( "Processing association property references" );
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 		
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
 		LOG.debug( "Creating tables' unique integer identifiers" );
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		int uniqueInteger = 0;
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			Table table = (Table) itr.next();
 			table.setUniqueInteger( uniqueInteger++ );
 			secondPassCompileForeignKeys( table, done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		LOG.debug( "Processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuilder buf = new StringBuilder( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
 	protected void secondPassCompileForeignKeys(Table table, Set<ForeignKey> done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
 				LOG.debugf( "Resolving reference to class: %s", referencedEntityName );
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
+	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
+		return namedProcedureCallMap;
+	}
+
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		LOG.debugf( "Preparing to build session factory with filters : %s", filterDefinitions );
 		
 		buildTypeRegistrations( serviceRegistry );
 		secondPassCompile();
 		if ( !metadataSourceQueue.isEmpty() ) {
 			LOG.incompleteMappingMetadataCacheProcessing();
 		}
 
 		validate();
 
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				sessionFactoryObserver
 			);
 	}
 	
 	private void buildTypeRegistrations(ServiceRegistry serviceRegistry) {
 		final TypeContributions typeContributions = new TypeContributions() {
 			@Override
 			public void contributeType(BasicType type) {
 				typeResolver.registerTypeOverride( type );
 			}
 
 			@Override
 			public void contributeType(UserType type, String[] keys) {
 				typeResolver.registerTypeOverride( type, keys );
 			}
 
 			@Override
 			public void contributeType(CompositeUserType type, String[] keys) {
 				typeResolver.registerTypeOverride( type, keys );
 			}
 		};
 
 		// add Dialect contributed types
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		dialect.contributeTypes( typeContributions, serviceRegistry );
 
 		// add TypeContributor contributed types.
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		for ( TypeContributor contributor : classLoaderService.loadJavaServices( TypeContributor.class ) ) {
 			contributor.contribute( typeContributions, serviceRegistry );
 		}
 		// from app registrations
 		for ( TypeContributor contributor : typeContributorRegistrations ) {
 			contributor.contribute( typeContributions, serviceRegistry );
 		}
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
 				.applySettings( properties )
 				.build();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
 	 * Retrieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value currently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
 			LOG.debugf( "%s=%s", name, value );
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		LOG.configuringFromResource( resource );
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
 		LOG.configurationResource( resource );
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		LOG.configuringFromUrl( url );
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		LOG.configuringFromFile( configFile.getName() );
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			ErrorLogger errorLogger = new ErrorLogger( resourceName );
 			Document document = xmlHelper.createSAXReader( errorLogger,  entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errorLogger.hasErrors() ) {
 				throw new MappingException( "invalid configuration", errorLogger.getErrors().get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseInputStreamForResource( resourceName, ioe );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
 		LOG.configuredSessionFactory( name );
 		LOG.debugf( "Properties: %s", properties );
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named resource [%s] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named file [%s] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named package [%s] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named class [%s] for mapping", name, className );
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private JaccPermissionDeclarations jaccPermissionDeclarations;
 
 	private void parseSecurity(Element secNode) {
 		final String nodeContextId = secNode.attributeValue( "context" );
 
 		final String explicitContextId = getProperty( AvailableSettings.JACC_CONTEXT_ID );
 		if ( explicitContextId == null ) {
 			setProperty( AvailableSettings.JACC_CONTEXT_ID, nodeContextId );
 			LOG.jaccContextId( nodeContextId );
 		}
 		else {
 			// if they dont match, throw an error
 			if ( ! nodeContextId.equals( explicitContextId ) ) {
 				throw new HibernateException( "Non-matching JACC context ids" );
 			}
 		}
 		jaccPermissionDeclarations = new JaccPermissionDeclarations( nodeContextId );
 
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			final Element grantElement = (Element) grantElements.next();
 			final String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jaccPermissionDeclarations.addPermissionDeclaration(
 						new GrantedPermission(
 								grantElement.attributeValue( "role" ),
 								grantElement.attributeValue( "entity-name" ),
 								grantElement.attributeValue( "actions" )
 						)
 				);
 			}
 		}
 	}
 
 	public JaccPermissionDeclarations getJaccPermissionDeclarations() {
 		return jaccPermissionDeclarations;
 	}
 
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
 	 * @param serviceRegistry The registry of services to be used in building these settings.
 	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				Property prop = pc.getReferencedProperty( propertyName );
 				if ( prop == null ) {
 					throw new MappingException(
 							"property not known: " +
 							entityName + '.' + propertyName
 						);
 				}
 				return prop.getType();
 			}
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( functionName.toLowerCase(), function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeContributor(TypeContributor typeContributor) {
 		typeContributorRegistrations.add( typeContributor );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
 		this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
 		final AttributeConverter attributeConverter;
 		try {
 			attributeConverter = attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]"
 			);
 		}
 		addAttributeConverter( attributeConverter, autoApply );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
 		}
 
 		final Object old = attributeConverterDefinitionsByClass.put(
 				attributeConverter.getClass(),
 				new AttributeConverterDefinition( attributeConverter, autoApply )
 		);
 
 		if ( old != null ) {
 			throw new AssertionFailure(
 					"AttributeConverter class [" + attributeConverter.getClass() + "] registered multiple times"
 			);
 		}
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	@SuppressWarnings( {"deprecation", "unchecked"})
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
-			return tables.get(key);
+			return tables.get( key );
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
+		@Override
+		public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
+				throws DuplicateMappingException {
+			final String name = definition.getRegisteredName();
+			final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
+			if ( previous != null ) {
+				throw new DuplicateMappingException( "named stored procedure query", name );
+			}
+		}
+
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
 			LOG.debugf( "Added %s with class %s", typeName, typeClass );
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
 			return auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 		}
 
 		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 			auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 		}
 
 		/**
 		 * Internal struct used to help track physical table names to logical table names.
 		 */
 		private class TableDescription implements Serializable {
 			final String logicalName;
 			final Table denormalizedSupertable;
 
 			TableDescription(String logicalName, Table denormalizedSupertable) {
 				this.logicalName = logicalName;
 				this.denormalizedSupertable = denormalizedSupertable;
 			}
 		}
 
 		public String getLogicalTableName(Table table) throws MappingException {
 			return getLogicalTableName( table.getQuotedSchema(), table.getQuotedCatalog(), table.getQuotedName() );
 		}
 
 		private String getLogicalTableName(String schema, String catalog, String physicalName) throws MappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription descriptor = (TableDescription) tableNameBinding.get( key );
 			if (descriptor == null) {
 				throw new MappingException( "Unable to find physical table: " + physicalName);
 			}
 			return descriptor.logicalName;
 		}
 
 		public void addTableBinding(
 				String schema,
 				String catalog,
 				String logicalName,
 				String physicalName,
 				Table denormalizedSuperTable) throws DuplicateMappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription tableDescription = new TableDescription( logicalName, denormalizedSuperTable );
 			TableDescription oldDescriptor = ( TableDescription ) tableNameBinding.put( key, tableDescription );
 			if ( oldDescriptor != null && ! oldDescriptor.logicalName.equals( logicalName ) ) {
 				//TODO possibly relax that
 				throw new DuplicateMappingException(
 						"Same physical table name [" + physicalName + "] references several logical table names: [" +
 								oldDescriptor.logicalName + "], [" + logicalName + ']',
 						"table",
 						physicalName
 				);
 			}
 		}
 
 		private String buildTableNameKey(String schema, String catalog, String finalName) {
 			StringBuilder keyBuilder = new StringBuilder();
 			if (schema != null) keyBuilder.append( schema );
 			keyBuilder.append( ".");
 			if (catalog != null) keyBuilder.append( catalog );
 			keyBuilder.append( ".");
 			keyBuilder.append( finalName );
 			return keyBuilder.toString();
 		}
 
 		/**
 		 * Internal struct used to maintain xref between physical and logical column
 		 * names for a table.  Mainly this is used to ensure that the defined
 		 * {@link NamingStrategy} is not creating duplicate column names.
 		 */
 		private class TableColumnNameBinding implements Serializable {
 			private final String tableName;
 			private Map/*<String, String>*/ logicalToPhysical = new HashMap();
 			private Map/*<String, String>*/ physicalToLogical = new HashMap();
 
 			private TableColumnNameBinding(String tableName) {
 				this.tableName = tableName;
 			}
 
 			public void addBinding(String logicalName, Column physicalColumn) {
 				bindLogicalToPhysical( logicalName, physicalColumn );
 				bindPhysicalToLogical( logicalName, physicalColumn );
 			}
 
 			private void bindLogicalToPhysical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String logicalKey = logicalName.toLowerCase();
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingPhysicalName = ( String ) logicalToPhysical.put( logicalKey, physicalName );
 				if ( existingPhysicalName != null ) {
 					boolean areSamePhysicalColumn = physicalColumn.isQuoted()
 							? existingPhysicalName.equals( physicalName )
 							: existingPhysicalName.equalsIgnoreCase( physicalName );
 					if ( ! areSamePhysicalColumn ) {
 						throw new DuplicateMappingException(
 								" Table [" + tableName + "] contains logical column name [" + logicalName
 										+ "] referenced by multiple physical column names: [" + existingPhysicalName
 										+ "], [" + physicalName + "]",
 								"column-binding",
 								tableName + "." + logicalName
 						);
 					}
 				}
 			}
 
 			private void bindPhysicalToLogical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingLogicalName = ( String ) physicalToLogical.put( physicalName, logicalName );
 				if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 					throw new DuplicateMappingException(
 							" Table [" + tableName + "] contains phyical column name [" + physicalName
 									+ "] represented by different logical column names: [" + existingLogicalName
 									+ "], [" + logicalName + "]",
 							"column-binding",
 							tableName + "." + physicalName
 					);
 				}
 			}
 		}
 
 		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
 			TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( table );
 			if ( binding == null ) {
 				binding = new TableColumnNameBinding( table.getName() );
 				columnNameBindingPerTable.put( table, binding );
 			}
 			binding.addBinding( logicalName, physicalColumn );
 		}
 
 		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
 			logicalName = logicalName.toLowerCase();
 			String finalName = null;
 			Table currentTable = table;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					finalName = ( String ) binding.logicalToPhysical.get( logicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
 				);
 				TableDescription description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			} while ( finalName == null && currentTable != null );
 
 			if ( finalName == null ) {
 				throw new MappingException(
 						"Unable to find column with logical name " + logicalName + " in table " + table.getName()
 				);
 			}
 			return finalName;
 		}
 
 		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
 			String logical = null;
 			Table currentTable = table;
 			TableDescription description = null;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					logical = ( String ) binding.physicalToLogical.get( physicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
 				);
 				description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			}
 			while ( logical == null && currentTable != null && description != null );
 			if ( logical == null ) {
 				throw new MappingException(
 						"Unable to find logical column name from physical name "
 								+ physicalName + " in table " + table.getName()
 				);
 			}
 			return logical;
 		}
 
 		public void addSecondPass(SecondPass sp) {
 			addSecondPass( sp, false );
 		}
 
 		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
 			if ( onTopOfTheQueue ) {
 				secondPasses.add( 0, sp );
 			}
 			else {
 				secondPasses.add( sp );
 			}
 		}
 
 		@Override
 		public AttributeConverterDefinition locateAttributeConverter(Class converterClass) {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return null;
 			}
 			return attributeConverterDefinitionsByClass.get( converterClass );
 		}
 
 		@Override
 		public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return Collections.emptyList();
 			}
 			return attributeConverterDefinitionsByClass.values();
 		}
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 			return identifierGeneratorFactory;
 		}
 
 		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 			mappedSuperClasses.put( type, mappedSuperclass );
 		}
 
 		public MappedSuperclass getMappedSuperclass(Class type) {
 			return mappedSuperClasses.get( type );
 		}
 
 		public ObjectNameNormalizer getObjectNameNormalizer() {
 			return normalizer;
 		}
 
 		public Properties getConfigurationProperties() {
 			return properties;
 		}
 
 		public void addDefaultGenerator(IdGenerator generator) {
 			this.addGenerator( generator );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 
 		public boolean isInSecondPass() {
 			return inSecondPass;
 		}
 
 		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 		}
 
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( mapsIdValue, property );
 		}
 
 		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 			}
 			map.put( property.getPropertyName(), property );
 		}
 
 		private Boolean useNewGeneratorMappings;
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		private Boolean useNationalizedCharacterData;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean useNationalizedCharacterData() {
 			if ( useNationalizedCharacterData == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA );
 				useNationalizedCharacterData = Boolean.valueOf( booleanName );
 			}
 			return useNationalizedCharacterData.booleanValue();
 		}
 
 		private Boolean forceDiscriminatorInSelectsByDefault;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean forceDiscriminatorInSelectsByDefault() {
 			if ( forceDiscriminatorInSelectsByDefault == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT );
 				forceDiscriminatorInSelectsByDefault = Boolean.valueOf( booleanName );
 			}
 			return forceDiscriminatorInSelectsByDefault.booleanValue();
 		}
 
 		public IdGenerator getGenerator(String name) {
 			return getGenerator( name, null );
 		}
 
 		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
 			if ( localGenerators != null ) {
 				IdGenerator result = localGenerators.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return namedGenerators.get( name );
 		}
 
 		public void addGenerator(IdGenerator generator) {
 			if ( !defaultNamedGenerators.contains( generator.getName() ) ) {
 				IdGenerator old = namedGenerators.put( generator.getName(), generator );
 				if ( old != null ) {
 					LOG.duplicateGeneratorName( old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				LOG.duplicateGeneratorTable( name );
 			}
 		}
 
 		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
 			if ( localGeneratorTables != null ) {
 				Properties result = localGeneratorTables.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return generatorTables.get( name );
 		}
 
 		public Map<String, Join> getJoins(String entityName) {
 			return joins.get( entityName );
 		}
 
 		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
 			Object old = Configuration.this.joins.put( persistentClass.getEntityName(), joins );
 			if ( old != null ) {
 				LOG.duplicateJoins( persistentClass.getEntityName() );
 			}
 		}
 
 		public AnnotatedClassType getClassType(XClass clazz) {
 			AnnotatedClassType type = classTypes.get( clazz.getName() );
 			if ( type == null ) {
 				return addClassType( clazz );
 			}
 			else {
 				return type;
 			}
 		}
 
 		//FIXME should be private but is part of the ExtendedMapping contract
 
 		public AnnotatedClassType addClassType(XClass clazz) {
 			AnnotatedClassType type;
 			if ( clazz.isAnnotationPresent( Entity.class ) ) {
 				type = AnnotatedClassType.ENTITY;
 			}
 			else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE;
 			}
 			else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 			}
 			else {
 				type = AnnotatedClassType.NONE;
 			}
 			classTypes.put( clazz.getName(), type );
 			return type;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Map<Table, List<String[]>> getTableUniqueConstraints() {
 			final Map<Table, List<String[]>> deprecatedStructure = new HashMap<Table, List<String[]>>(
 					CollectionHelper.determineProperSizing( getUniqueConstraintHoldersByTable() ),
 					CollectionHelper.LOAD_FACTOR
 			);
 			for ( Map.Entry<Table, List<UniqueConstraintHolder>> entry : getUniqueConstraintHoldersByTable().entrySet() ) {
 				List<String[]> columnsPerConstraint = new ArrayList<String[]>(
 						CollectionHelper.determineProperSizing( entry.getValue().size() )
 				);
 				deprecatedStructure.put( entry.getKey(), columnsPerConstraint );
 				for ( UniqueConstraintHolder holder : entry.getValue() ) {
 					columnsPerConstraint.add( holder.getColumns() );
 				}
 			}
 			return deprecatedStructure;
 		}
 
 		public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable() {
 			return uniqueConstraintHoldersByTable;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		public void addUniqueConstraints(Table table, List uniqueConstraints) {
 			List<UniqueConstraintHolder> constraintHolders = new ArrayList<UniqueConstraintHolder>(
 					CollectionHelper.determineProperSizing( uniqueConstraints.size() )
 			);
 
 			int keyNameBase = determineCurrentNumberOfUniqueConstraintHolders( table );
 			for ( String[] columns : ( List<String[]> ) uniqueConstraints ) {
 				final String keyName = "key" + keyNameBase++;
 				constraintHolders.add(
 						new UniqueConstraintHolder().setName( keyName ).setColumns( columns )
 				);
 			}
 			addUniqueConstraintHolders( table, constraintHolders );
 		}
 
 		private int determineCurrentNumberOfUniqueConstraintHolders(Table table) {
 			List currentHolders = getUniqueConstraintHoldersByTable().get( table );
 			return currentHolders == null
 					? 0
 					: currentHolders.size();
 		}
 
 		public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
 			List<UniqueConstraintHolder> holderList = getUniqueConstraintHoldersByTable().get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<UniqueConstraintHolder>();
 				getUniqueConstraintHoldersByTable().put( table, holderList );
 			}
 			holderList.addAll( uniqueConstraintHolders );
 		}
 
 		public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
 			List<JPAIndexHolder> holderList = jpaIndexHoldersByTable.get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<JPAIndexHolder>();
 				jpaIndexHoldersByTable.put( table, holderList );
 			}
 			holderList.addAll( holders );
 		}
 
 		public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
 			mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
 		}
 
 		public String getFromMappedBy(String entityName, String propertyName) {
 			return mappedByResolver.get( entityName + "." + propertyName );
 		}
 
 		public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
 			propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
 		}
 
 		public String getPropertyReferencedAssociation(String entityName, String propertyName) {
 			return propertyRefResolver.get( entityName + "." + propertyName );
 		}
 
 		public ReflectionManager getReflectionManager() {
 			return reflectionManager;
 		}
 
 		public Map getClasses() {
 			return classes;
 		}
 
 		public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 			anyMetaDefs.put( defAnn.name(), defAnn );
 		}
 
 		public AnyMetaDef getAnyMetaDef(String name) {
 			return anyMetaDefs.get( name );
 		}
 	}
 
 	final ObjectNameNormalizer normalizer = new ObjectNameNormalizerImpl();
 
 	final class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
 		public boolean isUseQuotedIdentifiersGlobally() {
 			//Do not cache this value as we lazily set it in Hibernate Annotation (AnnotationConfiguration)
 			//TODO use a dedicated protected useQuotedIdentifier flag in Configuration (overriden by AnnotationConfiguration)
 			String setting = (String) properties.get( Environment.GLOBALLY_QUOTED_IDENTIFIERS );
 			return setting != null && Boolean.valueOf( setting ).booleanValue();
 		}
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 	}
 
 	protected class MetadataSourceQueue implements Serializable {
 		private LinkedHashMap<XmlDocument, Set<String>> hbmMetadataToEntityNamesMap
 				= new LinkedHashMap<XmlDocument, Set<String>>();
 		private Map<String, XmlDocument> hbmMetadataByEntityNameXRef = new HashMap<String, XmlDocument>();
 
 		//XClass are not serializable by default
 		private transient List<XClass> annotatedClasses = new ArrayList<XClass>();
 		//only used during the secondPhaseCompile pass, hence does not need to be serialized
 		private transient Map<String, XClass> annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			ois.defaultReadObject();
 			annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 			//build back annotatedClasses
 			@SuppressWarnings( "unchecked" )
 			List<Class> serializableAnnotatedClasses = (List<Class>) ois.readObject();
 			annotatedClasses = new ArrayList<XClass>( serializableAnnotatedClasses.size() );
 			for ( Class clazz : serializableAnnotatedClasses ) {
 				annotatedClasses.add( reflectionManager.toXClass( clazz ) );
 			}
 		}
 
 		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 			out.defaultWriteObject();
 			List<Class> serializableAnnotatedClasses = new ArrayList<Class>( annotatedClasses.size() );
 			for ( XClass xClass : annotatedClasses ) {
 				serializableAnnotatedClasses.add( reflectionManager.toClass( xClass ) );
 			}
 			out.writeObject( serializableAnnotatedClasses );
 		}
 
 		public void add(XmlDocument metadataXml) {
 			final Document document = metadataXml.getDocumentTree();
 			final Element hmNode = document.getRootElement();
 			Attribute packNode = hmNode.attribute( "package" );
 			String defaultPackage = packNode != null ? packNode.getValue() : "";
 			Set<String> entityNames = new HashSet<String>();
 			findClassNames( defaultPackage, hmNode, entityNames );
 			for ( String entity : entityNames ) {
 				hbmMetadataByEntityNameXRef.put( entity, metadataXml );
 			}
 			this.hbmMetadataToEntityNamesMap.put( metadataXml, entityNames );
 		}
 
 		private void findClassNames(String defaultPackage, Element startNode, Set<String> names) {
 			// if we have some extends we need to check if those classes possibly could be inside the
 			// same hbm.xml file...
 			Iterator[] classes = new Iterator[4];
 			classes[0] = startNode.elementIterator( "class" );
 			classes[1] = startNode.elementIterator( "subclass" );
 			classes[2] = startNode.elementIterator( "joined-subclass" );
 			classes[3] = startNode.elementIterator( "union-subclass" );
 
 			Iterator classIterator = new JoinedIterator( classes );
 			while ( classIterator.hasNext() ) {
 				Element element = ( Element ) classIterator.next();
 				String entityName = element.attributeValue( "entity-name" );
 				if ( entityName == null ) {
 					entityName = getClassName( element.attribute( "name" ), defaultPackage );
 				}
 				names.add( entityName );
 				findClassNames( defaultPackage, element, names );
 			}
 		}
 
 		private String getClassName(Attribute name, String defaultPackage) {
 			if ( name == null ) {
 				return null;
 			}
 			String unqualifiedName = name.getValue();
 			if ( unqualifiedName == null ) {
 				return null;
 			}
 			if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 				return defaultPackage + '.' + unqualifiedName;
 			}
 			return unqualifiedName;
 		}
 
 		public void add(XClass annotatedClass) {
 			annotatedClasses.add( annotatedClass );
 		}
 
 		protected void syncAnnotatedClasses() {
 			final Iterator<XClass> itr = annotatedClasses.iterator();
 			while ( itr.hasNext() ) {
 				final XClass annotatedClass = itr.next();
 				if ( annotatedClass.isAnnotationPresent( Entity.class ) ) {
 					annotatedClassesByEntityNameMap.put( annotatedClass.getName(), annotatedClass );
 					continue;
 				}
 
 				if ( !annotatedClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 					itr.remove();
 				}
 			}
 		}
 
 		protected void processMetadata(List<MetadataSourceType> order) {
 			syncAnnotatedClasses();
 
 			for ( MetadataSourceType type : order ) {
 				if ( MetadataSourceType.HBM.equals( type ) ) {
 					processHbmXmlQueue();
 				}
 				else if ( MetadataSourceType.CLASS.equals( type ) ) {
 					processAnnotatedClassesQueue();
 				}
 			}
 		}
 
 		private void processHbmXmlQueue() {
 			LOG.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), Collections.EMPTY_MAP, entityNames );
 			}
 			catch ( MappingException me ) {
 				throw new InvalidMappingException(
 						metadataXml.getOrigin().getType(),
 						metadataXml.getOrigin().getName(),
 						me
 				);
 			}
 
 			for ( String entityName : entityNames ) {
 				if ( annotatedClassesByEntityNameMap.containsKey( entityName ) ) {
 					annotatedClasses.remove( annotatedClassesByEntityNameMap.get( entityName ) );
 					annotatedClassesByEntityNameMap.remove( entityName );
 				}
 			}
 		}
 
 		private void processAnnotatedClassesQueue() {
 			LOG.debug( "Process annotated classes" );
 			//bind classes in the correct order calculating some inheritance state
 			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
 			Mappings mappings = createMappings();
 			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
 					orderedClasses, mappings
 			);
 
 
 			for ( XClass clazz : orderedClasses ) {
 				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
 
 				final String entityName = clazz.getName();
 				if ( hbmMetadataByEntityNameXRef.containsKey( entityName ) ) {
 					hbmMetadataToEntityNamesMap.remove( hbmMetadataByEntityNameXRef.get( entityName ) );
 					hbmMetadataByEntityNameXRef.remove( entityName );
 				}
 			}
 			annotatedClasses.clear();
 			annotatedClassesByEntityNameMap.clear();
 		}
 
 		private List<XClass> orderAndFillHierarchy(List<XClass> original) {
 			List<XClass> copy = new ArrayList<XClass>( original );
 			insertMappedSuperclasses( original, copy );
 
 			// order the hierarchy
 			List<XClass> workingCopy = new ArrayList<XClass>( copy );
 			List<XClass> newList = new ArrayList<XClass>( copy.size() );
 			while ( workingCopy.size() > 0 ) {
 				XClass clazz = workingCopy.get( 0 );
 				orderHierarchy( workingCopy, newList, copy, clazz );
 			}
 			return newList;
 		}
 
 		private void insertMappedSuperclasses(List<XClass> original, List<XClass> copy) {
 			for ( XClass clazz : original ) {
 				XClass superClass = clazz.getSuperclass();
 				while ( superClass != null
 						&& !reflectionManager.equals( superClass, Object.class )
 						&& !copy.contains( superClass ) ) {
 					if ( superClass.isAnnotationPresent( Entity.class )
 							|| superClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 						copy.add( superClass );
 					}
 					superClass = superClass.getSuperclass();
 				}
 			}
 		}
 
 		private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
 			if ( clazz == null || reflectionManager.equals( clazz, Object.class ) ) {
 				return;
 			}
 			//process superclass first
 			orderHierarchy( copy, newList, original, clazz.getSuperclass() );
 			if ( original.contains( clazz ) ) {
 				if ( !newList.contains( clazz ) ) {
 					newList.add( clazz );
 				}
 				copy.remove( clazz );
 			}
 		}
 
 		public boolean isEmpty() {
 			return hbmMetadataToEntityNamesMap.isEmpty() && annotatedClasses.isEmpty();
 		}
 
 	}
 
 
 	public static final MetadataSourceType[] DEFAULT_ARTEFACT_PROCESSING_ORDER = new MetadataSourceType[] {
 			MetadataSourceType.HBM,
 			MetadataSourceType.CLASS
 	};
 
 	private List<MetadataSourceType> metadataSourcePrecedence;
 
 	private List<MetadataSourceType> determineMetadataSourcePrecedence() {
 		if ( metadataSourcePrecedence.isEmpty()
 				&& StringHelper.isNotEmpty( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) ) ) {
 			metadataSourcePrecedence = parsePrecedence( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) );
 		}
 		if ( metadataSourcePrecedence.isEmpty() ) {
 			metadataSourcePrecedence = Arrays.asList( DEFAULT_ARTEFACT_PROCESSING_ORDER );
 		}
 		metadataSourcePrecedence = Collections.unmodifiableList( metadataSourcePrecedence );
 
 		return metadataSourcePrecedence;
 	}
 
 	public void setPrecedence(String precedence) {
 		this.metadataSourcePrecedence = parsePrecedence( precedence );
 	}
 
 	private List<MetadataSourceType> parsePrecedence(String s) {
 		if ( StringHelper.isEmpty( s ) ) {
 			return Collections.emptyList();
 		}
 		StringTokenizer precedences = new StringTokenizer( s, ",; ", false );
 		List<MetadataSourceType> tmpPrecedences = new ArrayList<MetadataSourceType>();
 		while ( precedences.hasMoreElements() ) {
 			tmpPrecedences.add( MetadataSourceType.parsePrecedence( ( String ) precedences.nextElement() ) );
 		}
 		return tmpPrecedences;
 	}
 
 	private static class CacheHolder {
 		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.isClass = isClass;
 			this.cacheLazy = cacheLazy;
 		}
 
 		public String role;
 		public String usage;
 		public String region;
 		public boolean isClass;
 		public boolean cacheLazy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
index a0d6543fc6..4b49851563 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,787 +1,797 @@
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
 
 import javax.persistence.AttributeConverter;
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
+import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.type.TypeResolver;
 
 /**
  * A collection of mappings from classes and collections to relational database tables.  Represents a single
  * <tt>&lt;hibernate-mapping&gt;</tt> element.
  * <p/>
  * todo : the statement about this representing a single mapping element is simply not true if it was ever the case.
  * this contract actually represents 3 scopes of information: <ol>
  * <li><i>bounded</i> state : this is information which is indeed scoped by a single mapping</li>
  * <li><i>unbounded</i> state : this is information which is Configuration wide (think of metadata repository)</li>
  * <li><i>transient</i> state : state which changed at its own pace (naming strategy)</li>
  * </ol>
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Mappings {
 	/**
 	 * Retrieve the type resolver in effect.
 	 *
 	 * @return The type resolver.
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get the current naming strategy.
 	 *
 	 * @return The current naming strategy.
 	 */
 	public NamingStrategy getNamingStrategy();
 
 	/**
 	 * Set the current naming strategy.
 	 *
 	 * @param namingStrategy The naming strategy to use.
 	 */
 	public void setNamingStrategy(NamingStrategy namingStrategy);
 
 	/**
 	 * Returns the currently bound default schema name.
 	 *
 	 * @return The currently bound schema name
 	 */
 	public String getSchemaName();
 
 	/**
 	 * Sets the currently bound default schema name.
 	 *
 	 * @param schemaName The schema name to bind as the current default.
 	 */
 	public void setSchemaName(String schemaName);
 
 	/**
 	 * Returns the currently bound default catalog name.
 	 *
 	 * @return The currently bound catalog name, or null if none.
 	 */
 	public String getCatalogName();
 
     /**
      * Sets the currently bound default catalog name.
 	 *
      * @param catalogName The catalog name to use as the current default.
      */
     public void setCatalogName(String catalogName);
 
 	/**
 	 * Get the currently bound default package name.
 	 *
 	 * @return The currently bound default package name
 	 */
 	public String getDefaultPackage();
 
 	/**
 	 * Set the current default package name.
 	 *
 	 * @param defaultPackage The package name to set as the current default.
 	 */
 	public void setDefaultPackage(String defaultPackage);
 
 	/**
 	 * Determine whether auto importing of entity names is currently enabled.
 	 *
 	 * @return True if currently enabled; false otherwise.
 	 */
 	public boolean isAutoImport();
 
 	/**
 	 * Set whether to enable auto importing of entity names.
 	 *
 	 * @param autoImport True to enable; false to diasable.
 	 * @see #addImport
 	 */
 	public void setAutoImport(boolean autoImport);
 
 	/**
 	 * Determine whether default laziness is currently enabled.
 	 *
 	 * @return True if enabled, false otherwise.
 	 */
 	public boolean isDefaultLazy();
 
 	/**
 	 * Set whether to enable default laziness.
 	 *
 	 * @param defaultLazy True to enable, false to disable.
 	 */
 	public void setDefaultLazy(boolean defaultLazy);
 
 	/**
 	 * Get the current default cascade style.
 	 *
 	 * @return The current default cascade style.
 	 */
 	public String getDefaultCascade();
 
 	/**
 	 * Sets the current default cascade style.
 	 * .
 	 * @param defaultCascade The cascade style to set as the current default.
 	 */
 	public void setDefaultCascade(String defaultCascade);
 
 	/**
 	 * Get the current default property access style.
 	 *
 	 * @return The current default property access style.
 	 */
 	public String getDefaultAccess();
 
 	/**
 	 * Sets the current default property access style.
 	 *
 	 * @param defaultAccess The access style to use as the current default.
 	 */
 	public void setDefaultAccess(String defaultAccess);
 
 
 	/**
 	 * Retrieves an iterator over the entity metadata present in this repository.
 	 *
 	 * @return Iterator over class metadata.
 	 */
 	public Iterator<PersistentClass> iterateClasses();
 
 	/**
 	 * Retrieves the entity mapping metadata for the given entity name.
 	 *
 	 * @param entityName The entity name for which to retrieve the metadata.
 	 * @return The entity mapping metadata, or null if none found matching given entity name.
 	 */
 	public PersistentClass getClass(String entityName);
 
 	/**
 	 * Retrieves the entity mapping metadata for the given entity name, potentially accounting
 	 * for imports.
 	 *
 	 * @param entityName The entity name for which to retrieve the metadata.
 	 * @return The entity mapping metadata, or null if none found matching given entity name.
 	 */
 	public PersistentClass locatePersistentClassByEntityName(String entityName);
 
 	/**
 	 * Add entity mapping metadata.
 	 *
 	 * @param persistentClass The entity metadata
 	 * @throws DuplicateMappingException Indicates there4 was already an extry
 	 * corresponding to the given entity name.
 	 */
 	public void addClass(PersistentClass persistentClass) throws DuplicateMappingException;
 
 	/**
 	 * Adds an import (HQL entity rename) to the repository.
 	 *
 	 * @param entityName The entity name being renamed.
 	 * @param rename The rename
 	 * @throws DuplicateMappingException If rename already is mapped to another
 	 * entity name in this repository.
 	 */
 	public void addImport(String entityName, String rename) throws DuplicateMappingException;
 
 	/**
 	 * Retrieves the collection mapping metadata for the given collection role.
 	 *
 	 * @param role The collection role for which to retrieve the metadata.
 	 * @return The collection mapping metadata, or null if no matching collection role found.
 	 */
 	public Collection getCollection(String role);
 
 	/**
 	 * Returns an iterator over collection metadata.
 	 *
 	 * @return Iterator over collection metadata.
 	 */
 	public Iterator<Collection> iterateCollections();
 
 	/**
 	 * Add collection mapping metadata to this repository.
 	 *
 	 * @param collection The collection metadata
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given collection role
 	 */
 	public void addCollection(Collection collection) throws DuplicateMappingException;
 
 	/**
 	 * Returns the named table metadata.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @return The table metadata, or null.
 	 */
 	public Table getTable(String schema, String catalog, String name);
 
 	/**
 	 * Returns an iterator over table metadata.
 	 *
 	 * @return Iterator over table metadata.
 	 */
 	public Iterator<Table> iterateTables();
 
 	/**
 	 * Adds table metadata to this repository returning the created
 	 * metadata instance.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 * @return The created table metadata, or the existing reference.
 	 */
 	public Table addTable(String schema, String catalog, String name, String subselect, boolean isAbstract);
 
 	/**
 	 * Adds a 'denormalized table' to this repository.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param includedTable ???
 	 * @return The created table metadata.
 	 * @throws DuplicateMappingException If such a table mapping already exists.
 	 */
 	public Table addDenormalizedTable(String schema, String catalog, String name, boolean isAbstract, String subselect, Table includedTable)
 			throws DuplicateMappingException;
 
 	/**
 	 * Get named query metadata by name.
 	 *
 	 * @param name The named query name
 	 * @return The query metadata, or null.
 	 */
 	public NamedQueryDefinition getQuery(String name);
 
 	/**
 	 * Adds metadata for a named query to this repository.
 	 *
 	 * @param name The name
 	 * @param query The metadata
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Get named SQL query metadata.
 	 *
 	 * @param name The named SQL query name.
 	 * @return The meatdata, or null if none found.
 	 */
 	public NamedSQLQueryDefinition getSQLQuery(String name);
 
 	/**
 	 * Adds metadata for a named SQL query to this repository.
 	 *
 	 * @param name The name
 	 * @param query The metadata
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException;
 
 	/**
+	 * Adds metadata for a named stored procedure call to this repository.
+	 *
+	 * @param definition The procedure call information
+	 *
+	 * @throws DuplicateMappingException If a query already exists with that name.
+	 */
+	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
+
+	/**
 	 * Get the metadata for a named SQL result set mapping.
 	 *
 	 * @param name The mapping name.
 	 * @return The SQL result set mapping metadat, or null if none found.
 	 */
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Adds the metadata for a named SQL result set mapping to this repository.
 	 *
 	 * @param sqlResultSetMapping The metadata
 	 * @throws DuplicateMappingException If metadata for another SQL result mapping was
 	 * already found under the given name.
 	 */
 	public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException;
 
 	/**
 	 * Retrieve a type definition by name.
 	 *
 	 * @param typeName The name of the type definition to retrieve.
 	 * @return The type definition, or null if none found.
 	 */
 	public TypeDef getTypeDef(String typeName);
 
 	/**
 	 * Adds a type definition to this metadata repository.
 	 *
 	 * @param typeName The type name.
 	 * @param typeClass The class implementing the {@link org.hibernate.type.Type} contract.
 	 * @param paramMap Map of parameters to be used to configure the type after instantiation.
 	 */
 	public void addTypeDef(String typeName, String typeClass, Properties paramMap);
 
 	/**
 	 * Retrieves the copmplete map of filter definitions.
 	 *
 	 * @return The filter definition map.
 	 */
 	public Map getFilterDefinitions();
 
 	/**
 	 * Retrieves a filter definition by name.
 	 *
 	 * @param name The name of the filter definition to retrieve.
 	 * @return The filter definition, or null.
 	 */
 	public FilterDefinition getFilterDefinition(String name);
 
 	/**
 	 * Adds a filter definition to this repository.
 	 *
 	 * @param definition The filter definition to add.
 	 */
 	public void addFilterDefinition(FilterDefinition definition);
 
 	/**
 	 * Retrieves a fetch profile by either finding one currently in this repository matching the given name
 	 * or by creating one (and adding it).
 	 *
 	 * @param name The name of the profile.
 	 * @param source The source from which this profile is named.
 	 * @return The fetch profile metadata.
 	 */
 	public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source);
 
 	/**
 	 * @deprecated To fix misspelling; use {@link #iterateAuxiliaryDatabaseObjects} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects();
 
 	/**
 	 * Retrieves an iterator over the metadata pertaining to all auxiliary database objects int this repository.
 	 *
 	 * @return Iterator over the auxiliary database object metadata.
 	 */
 	public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects();
 
 	/**
 	 * @deprecated To fix misspelling; use {@link #iterateAuxiliaryDatabaseObjectsInReverse} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse();
 
 	/**
 	 * Same as {@link #iterateAuxiliaryDatabaseObjects()} except that here the iterator is reversed.
 	 *
 	 * @return The reversed iterator.
 	 */
 	public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse();
 
 	/**
 	 * Add metadata pertaining to an auxiliary database object to this repository.
 	 *
 	 * @param auxiliaryDatabaseObject The metadata.
 	 */
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 	/**
 	 * Get the logical table name mapped for the given physical table.
 	 *
 	 * @param table The table for which to determine the logical name.
 	 * @return The logical name.
 	 * @throws MappingException Indicates that no logical name was bound for the given physical table.
 	 */
 	public String getLogicalTableName(Table table) throws MappingException;
 
 	/**
 	 * Adds a table binding to this repository.
 	 *
 	 * @param schema The schema in which the table belongs (may be null).
 	 * @param catalog The catalog in which the table belongs (may be null).
 	 * @param logicalName The logical table name.
 	 * @param physicalName The physical table name.
 	 * @param denormalizedSuperTable ???
 	 * @throws DuplicateMappingException Indicates physical table was already bound to another logical name.
 	 */
 	public void addTableBinding(
 			String schema,
 			String catalog,
 			String logicalName,
 			String physicalName,
 			Table denormalizedSuperTable) throws DuplicateMappingException;
 
 	/**
 	 * Binds the given 'physicalColumn' to the give 'logicalName' within the given 'table'.
 	 *
 	 * @param logicalName The logical column name binding.
 	 * @param physicalColumn The physical column metadata.
 	 * @param table The table metadata.
 	 * @throws DuplicateMappingException Indicates a duplicate binding for either the physical column name
 	 * or the logical column name.
 	 */
 	public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException;
 
 	/**
 	 * Find the physical column name for the given logical column name within the given table.
 	 *
 	 * @param logicalName The logical name binding.
 	 * @param table The table metatdata.
 	 * @return The physical column name.
 	 * @throws MappingException Indicates that no such binding was found.
 	 */
 	public String getPhysicalColumnName(String logicalName, Table table) throws MappingException;
 
 	/**
 	 * Find the logical column name against whcih the given physical column name was bound within the given table.
 	 *
 	 * @param physicalName The physical column name
 	 * @param table The table metadata.
 	 * @return The logical column name.
 	 * @throws MappingException Indicates that no such binding was found.
 	 */
 	public String getLogicalColumnName(String physicalName, Table table) throws MappingException;
 
 	/**
 	 * Adds a second-pass to the end of the current queue.
 	 *
 	 * @param sp The second pass to add.
 	 */
 	public void addSecondPass(SecondPass sp);
 
 	/**
 	 * Adds a second pass.
 	 * @param sp The second pass to add.
 	 * @param onTopOfTheQueue True to add to the beginning of the queue; false to add to the end.
 	 */
 	public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue);
 
 	/**
 	 * Locate the AttributeConverterDefinition corresponding to the given AttributeConverter Class.
 	 *
 	 * @param attributeConverterClass The AttributeConverter Class for which to get the definition
 	 *
 	 * @return The corresponding AttributeConverter definition; will return {@code null} if no corresponding
 	 * definition found.
 	 */
 	public AttributeConverterDefinition locateAttributeConverter(Class attributeConverterClass);
 
 	/**
 	 * All all AttributeConverter definitions
 	 *
 	 * @return The collection of all AttributeConverter definitions.
 	 */
 	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
 
 	/**
 	 * Represents a property-ref mapping.
 	 * <p/>
 	 * TODO : currently needs to be exposed because Configuration needs access to it for second-pass processing
 	 */
 	public static final class PropertyReference implements Serializable {
 		public final String referencedClass;
 		public final String propertyName;
 		public final boolean unique;
 
 		public PropertyReference(String referencedClass, String propertyName, boolean unique) {
 			this.referencedClass = referencedClass;
 			this.propertyName = propertyName;
 			this.unique = unique;
 		}
 	}
 
 	/**
 	 * Adds a property reference binding to this repository.
 	 *
 	 * @param referencedClass The referenced entity name.
 	 * @param propertyName The referenced property name.
 	 */
 	public void addPropertyReference(String referencedClass, String propertyName);
 
 	/**
 	 * Adds a property reference binding to this repository where said proeprty reference is marked as unique.
 	 *
 	 * @param referencedClass The referenced entity name.
 	 * @param propertyName The referenced property name.
 	 */
 	public void addUniquePropertyReference(String referencedClass, String propertyName);
 
 	/**
 	 * Adds an entry to the extends queue queue.
 	 *
 	 * @param entry The entry to add.
 	 */
 	public void addToExtendsQueue(ExtendsQueueEntry entry);
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this mapping.
 	 *
 	 * @return The IdentifierGeneratorFactory
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory();
 
 	/**
 	 * add a new MappedSuperclass
 	 * This should not be called if the MappedSuperclass already exists
 	 * (it would be erased)
 	 * @param type type corresponding to the Mappedsuperclass
 	 * @param mappedSuperclass MappedSuperclass
 	 */
 	public void addMappedSuperclass(Class type, org.hibernate.mapping.MappedSuperclass mappedSuperclass);
 
 	/**
 	 * Get a MappedSuperclass or null if not mapped
 	 *
 	 * @param type class corresponding to the MappedSuperclass
 	 * @return the MappedSuperclass
 	 */
 	org.hibernate.mapping.MappedSuperclass getMappedSuperclass(Class type);
 
 	/**
 	 * Retrieve the database identifier normalizer for this context.
 	 *
 	 * @return The normalizer.
 	 */
 	public ObjectNameNormalizer getObjectNameNormalizer();
 
 	/**
 	 * Retrieve the configuration properties currently in effect.
 	 *
 	 * @return The configuration properties
 	 */
 	public Properties getConfigurationProperties();
 
 
 
 
 
 
 
 
 
 	/**
 	 * Adds a default id generator.
 	 *
 	 * @param generator The id generator
 	 */
 	public void addDefaultGenerator(IdGenerator generator);
 
 	/**
 	 * Retrieve the id-generator by name.
 	 *
 	 * @param name The generator name.
 	 *
 	 * @return The generator, or null.
 	 */
 	public IdGenerator getGenerator(String name);
 
 	/**
 	 * Try to find the generator from the localGenerators
 	 * and then from the global generator list
 	 *
 	 * @param name generator name
 	 * @param localGenerators local generators
 	 *
 	 * @return the appropriate idgenerator or null if not found
 	 */
 	public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators);
 
 	/**
 	 * Add a generator.
 	 *
 	 * @param generator The generator to add.
 	 */
 	public void addGenerator(IdGenerator generator);
 
 	/**
 	 * Add a generator table properties.
 	 *
 	 * @param name The generator name
 	 * @param params The generator table properties.
 	 */
 	public void addGeneratorTable(String name, Properties params);
 
 	/**
 	 * Retrieve the properties related to a generator table.
 	 *
 	 * @param name generator name
 	 * @param localGeneratorTables local generator tables
 	 *
 	 * @return The properties, or null.
 	 */
 	public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables);
 
 	/**
 	 * Retrieve join metadata for a particular persistent entity.
 	 *
 	 * @param entityName The entity name
 	 *
 	 * @return The join metadata
 	 */
 	public Map<String, Join> getJoins(String entityName);
 
 	/**
 	 * Add join metadata for a persistent entity.
 	 *
 	 * @param persistentClass The persistent entity metadata.
 	 * @param joins The join metadata to add.
 	 *
 	 * @throws MappingException
 	 */
 	public void addJoins(PersistentClass persistentClass, Map<String, Join> joins);
 
 	/**
 	 * Get and maintain a cache of class type.
 	 *
 	 * @param clazz The XClass mapping
 	 *
 	 * @return The class type.
 	 */
 	public AnnotatedClassType getClassType(XClass clazz);
 
 	/**
 	 * FIXME should be private but will this break things?
 	 * Add a class type.
 	 *
 	 * @param clazz The XClass mapping.
 	 *
 	 * @return The class type.
 	 */
 	public AnnotatedClassType addClassType(XClass clazz);
 
 	/**
 	 * @deprecated Use {@link #getUniqueConstraintHoldersByTable} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public Map<Table, List<String[]>> getTableUniqueConstraints();
 
 	public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable();
 
 	/**
 	 * @deprecated Use {@link #addUniqueConstraintHolders} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public void addUniqueConstraints(Table table, List uniqueConstraints);
 
 	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders);
 
 	public void addJpaIndexHolders(Table table, List<JPAIndexHolder> jpaIndexHolders);
 
 	public void addMappedBy(String entityName, String propertyName, String inversePropertyName);
 
 	public String getFromMappedBy(String entityName, String propertyName);
 
 	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef);
 
 	public String getPropertyReferencedAssociation(String entityName, String propertyName);
 
 	public ReflectionManager getReflectionManager();
 
 	public void addDefaultQuery(String name, NamedQueryDefinition query);
 
 	public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query);
 
 	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition);
 
 	public Map getClasses();
 
 	public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException;
 
 	public AnyMetaDef getAnyMetaDef(String name);
 
 	public boolean isInSecondPass();
 
 	/**
 	 * Return the property annotated with @MapsId("propertyName") if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName);
 
 	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property);
 
 	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue);
 
 	public boolean isSpecjProprietarySyntaxEnabled();
 
 	/**
 	 * Should we use the new generator strategy mappings.  This is controlled by the
 	 * {@link AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
 	 *
 	 * @return True if the new generators should be used, false otherwise.
 	 */
 	public boolean useNewGeneratorMappings();
 
 	/**
 	 * Should we use nationalized variants of character data by default?  This is controlled by the
 	 * {@link AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA} setting.
 	 *
 	 * @return {@code true} if nationalized character data should be used by default; {@code false} otherwise.
 	 */
 	public boolean useNationalizedCharacterData();
 
 	/**
 	 * Return the property annotated with @ToOne and @Id if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData property);
 
 	public boolean forceDiscriminatorInSelectsByDefault();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java
new file mode 100644
index 0000000000..b50173c016
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java
@@ -0,0 +1,237 @@
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
+import javax.persistence.NamedStoredProcedureQuery;
+import javax.persistence.ParameterMode;
+import javax.persistence.QueryHint;
+import javax.persistence.StoredProcedureParameter;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.LockMode;
+import org.hibernate.MappingException;
+import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.SessionFactoryImpl;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.procedure.ProcedureCallMemento;
+import org.hibernate.procedure.internal.ParameterStrategy;
+import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
+import org.hibernate.procedure.internal.Util;
+
+import static org.hibernate.procedure.internal.ProcedureCallMementoImpl.ParameterMemento;
+
+/**
+ * Holds all the information needed from a named procedure call declaration in order to create a
+ * {@link org.hibernate.procedure.internal.ProcedureCallImpl}
+ *
+ * @author Steve Ebersole
+ *
+ * @see javax.persistence.NamedStoredProcedureQuery
+ */
+public class NamedProcedureCallDefinition {
+	private final String registeredName;
+	private final String procedureName;
+	private final Class[] resultClasses;
+	private final String[] resultSetMappings;
+	private final ParameterDefinitions parameterDefinitions;
+	private final Map<String,Object> hints;
+
+	NamedProcedureCallDefinition(NamedStoredProcedureQuery annotation) {
+		this.registeredName = annotation.name();
+		this.procedureName = annotation.procedureName();
+		this.resultClasses = annotation.resultClasses();
+		this.resultSetMappings = annotation.resultSetMappings();
+		this.parameterDefinitions = new ParameterDefinitions( annotation.parameters() );
+		this.hints = extract( annotation.hints() );
+
+		final boolean specifiesResultClasses = resultClasses != null && resultClasses.length > 0;
+		final boolean specifiesResultSetMappings = resultSetMappings != null && resultSetMappings.length > 0;
+
+		if ( specifiesResultClasses && specifiesResultSetMappings ) {
+			throw new MappingException(
+					String.format(
+							"NamedStoredProcedureQuery [%s] specified both resultClasses and resultSetMappings",
+							registeredName
+					)
+			);
+		}
+	}
+
+	private Map<String, Object> extract(QueryHint[] hints) {
+		if ( hints == null || hints.length == 0 ) {
+			return Collections.emptyMap();
+		}
+		final Map<String,Object> hintsMap = new HashMap<String, Object>();
+		for ( QueryHint hint : hints ) {
+			hintsMap.put( hint.name(), hint.value() );
+		}
+		return hintsMap;
+	}
+
+	public String getRegisteredName() {
+		return registeredName;
+	}
+
+	public String getProcedureName() {
+		return procedureName;
+	}
+
+	public ProcedureCallMemento toMemento(
+			final SessionFactoryImpl sessionFactory,
+			final Map<String,ResultSetMappingDefinition> resultSetMappingDefinitions) {
+		final List<NativeSQLQueryReturn> collectedQueryReturns = new ArrayList<NativeSQLQueryReturn>();
+		final Set<String> collectedQuerySpaces = new HashSet<String>();
+
+		final boolean specifiesResultClasses = resultClasses != null && resultClasses.length > 0;
+		final boolean specifiesResultSetMappings = resultSetMappings != null && resultSetMappings.length > 0;
+
+		if ( specifiesResultClasses ) {
+			Util.resolveResultClasses(
+					new Util.ResultClassesResolutionContext() {
+						@Override
+						public SessionFactoryImplementor getSessionFactory() {
+							return sessionFactory;
+						}
+
+						@Override
+						public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
+							Collections.addAll( collectedQueryReturns, queryReturns );
+						}
+
+						@Override
+						public void addQuerySpaces(String... spaces) {
+							Collections.addAll( collectedQuerySpaces, spaces );
+						}
+					},
+					resultClasses
+			);
+		}
+		else if ( specifiesResultSetMappings ) {
+			Util.resolveResultSetMappings(
+					new Util.ResultSetMappingResolutionContext() {
+						@Override
+						public SessionFactoryImplementor getSessionFactory() {
+							return sessionFactory;
+						}
+
+						@Override
+						public ResultSetMappingDefinition findResultSetMapping(String name) {
+							return resultSetMappingDefinitions.get( name );
+						}
+
+						@Override
+						public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
+							Collections.addAll( collectedQueryReturns, queryReturns );
+						}
+
+						@Override
+						public void addQuerySpaces(String... spaces) {
+							Collections.addAll( collectedQuerySpaces, spaces );
+						}
+					},
+					resultSetMappings
+			);
+		}
+
+		return new ProcedureCallMementoImpl(
+				procedureName,
+				collectedQueryReturns.toArray( new NativeSQLQueryReturn[ collectedQueryReturns.size() ] ),
+				parameterDefinitions.getParameterStrategy(),
+				parameterDefinitions.toMementos( sessionFactory ),
+				collectedQuerySpaces,
+				hints
+		);
+	}
+
+	static class ParameterDefinitions {
+		private final ParameterStrategy parameterStrategy;
+		private final ParameterDefinition[] parameterDefinitions;
+
+		ParameterDefinitions(StoredProcedureParameter[] parameters) {
+			if ( parameters == null || parameters.length == 0 ) {
+				parameterStrategy = ParameterStrategy.POSITIONAL;
+				parameterDefinitions = new ParameterDefinition[0];
+			}
+			else {
+				parameterStrategy = StringHelper.isNotEmpty( parameters[0].name() )
+						? ParameterStrategy.NAMED
+						: ParameterStrategy.POSITIONAL;
+				parameterDefinitions = new ParameterDefinition[ parameters.length ];
+				for ( int i = 0; i < parameters.length; i++ ) {
+					parameterDefinitions[i] = new ParameterDefinition( i, parameters[i] );
+				}
+			}
+		}
+
+		public ParameterStrategy getParameterStrategy() {
+			return parameterStrategy;
+		}
+
+		public List<ParameterMemento> toMementos(SessionFactoryImpl sessionFactory) {
+			final List<ParameterMemento> mementos = new ArrayList<ParameterMemento>();
+			for ( ParameterDefinition definition : parameterDefinitions ) {
+				definition.toMemento( sessionFactory );
+			}
+			return mementos;
+		}
+	}
+
+	static class ParameterDefinition {
+		private final Integer position;
+		private final String name;
+		private final ParameterMode parameterMode;
+		private final Class type;
+
+		ParameterDefinition(int position, StoredProcedureParameter annotation) {
+			this.position = position;
+			this.name = normalize( annotation.name() );
+			this.parameterMode = annotation.mode();
+			this.type = annotation.type();
+		}
+
+		public ParameterMemento toMemento(SessionFactoryImpl sessionFactory) {
+			return new ParameterMemento(
+					position,
+					name,
+					parameterMode,
+					type,
+					sessionFactory.getTypeResolver().heuristicType( type.getName() )
+			);
+		}
+	}
+
+	private static String normalize(String name) {
+		return StringHelper.isNotEmpty( name ) ? name : null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
index 1299636ba5..2c21637b04 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
@@ -1,452 +1,467 @@
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
 import javax.persistence.LockModeType;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
+import javax.persistence.NamedStoredProcedureQuery;
 import javax.persistence.QueryHint;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.annotations.CacheModeType;
 import org.hibernate.annotations.FlushModeType;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.LockModeConverter;
 
 /**
  * Query binder
  *
  * @author Emmanuel Bernard
  */
 public abstract class QueryBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryBinder.class.getName());
 
 	public static void bindQuery(NamedQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		//EJBQL Query
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
 		NamedQueryDefinition queryDefinition = new NamedQueryDefinitionBuilder( queryAnn.name() )
 				.setLockOptions( determineLockOptions( queryAnn, hints ) )
 				.setQuery( queryName )
 				.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
 				.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
 				.setTimeout( getTimeout( queryName, hints ) )
 				.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
 				.setFlushMode( getFlushMode( queryName, hints ) )
 				.setCacheMode( getCacheMode( queryName, hints ) )
 				.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
 				.setComment( getString( queryName, "org.hibernate.comment", hints ) )
 				.setParameterTypes( null )
 				.createNamedQueryDefinition();
 
 		if ( isDefault ) {
 			mappings.addDefaultQuery( queryDefinition.getName(), queryDefinition );
 		}
 		else {
 			mappings.addQuery( queryDefinition.getName(), queryDefinition );
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named query: %s => %s", queryDefinition.getName(), queryDefinition.getQueryString() );
 		}
 	}
 
 	private static LockOptions determineLockOptions(NamedQuery namedQueryAnnotation, QueryHint[] hints) {
 		LockModeType lockModeType = namedQueryAnnotation.lockMode();
 		Integer lockTimeoutHint = getInteger( namedQueryAnnotation.name(), "javax.persistence.lock.timeout", hints );
 
 		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) );
 		if ( lockTimeoutHint != null ) {
 			lockOptions.setTimeOut( lockTimeoutHint );
 		}
 
 		return lockOptions;
 	}
 
 	public static void bindNativeQuery(NamedNativeQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		String resultSetMapping = queryAnn.resultSetMapping();
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
 		
 		NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( queryAnn.name() )
 				.setQuery( queryName )
 				.setQuerySpaces( null )
 				.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
 				.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
 				.setTimeout( getTimeout( queryName, hints ) )
 				.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
 				.setFlushMode( getFlushMode( queryName, hints ) )
 				.setCacheMode( getCacheMode( queryName, hints ) )
 				.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
 				.setComment( getString( queryName, "org.hibernate.comment", hints ) )
 				.setParameterTypes( null )
 				.setCallable( getBoolean( queryName, "org.hibernate.callable", hints ) );
 		
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
 			builder.setResultSetRef( resultSetMapping )
 					.createNamedQueryDefinition();
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
 			builder.setQueryReturns( new NativeSQLQueryReturn[] {entityQueryReturn} );
 		}
 		else {
 			builder.setQueryReturns( new NativeSQLQueryReturn[0] );
 		}
 		
 		NamedSQLQueryDefinition query = builder.createNamedQueryDefinition();
 		
 		if ( isDefault ) {
 			mappings.addDefaultSQLQuery( query.getName(), query );
 		}
 		else {
 			mappings.addSQLQuery( query.getName(), query );
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named native query: %s => %s", queryAnn.name(), queryAnn.query() );
 		}
 	}
 
 	public static void bindNativeQuery(org.hibernate.annotations.NamedNativeQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		NamedSQLQueryDefinition query;
 		String resultSetMapping = queryAnn.resultSetMapping();
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
 			query = new NamedSQLQueryDefinitionBuilder().setName( queryAnn.name() )
 					.setQuery( queryAnn.query() )
 					.setResultSetRef( resultSetMapping )
 					.setQuerySpaces( null )
 					.setCacheable( queryAnn.cacheable() )
 					.setCacheRegion(
 							BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
 									null :
 									queryAnn.cacheRegion()
 					)
 					.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
 					.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
 					.setFlushMode( getFlushMode( queryAnn.flushMode() ) )
 					.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
 					.setReadOnly( queryAnn.readOnly() )
 					.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
 					.setParameterTypes( null )
 					.setCallable( queryAnn.callable() )
 					.createNamedQueryDefinition();
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
 			query = new NamedSQLQueryDefinitionBuilder().setName( queryAnn.name() )
 					.setQuery( queryAnn.query() )
 					.setQueryReturns( new NativeSQLQueryReturn[] {entityQueryReturn} )
 					.setQuerySpaces( null )
 					.setCacheable( queryAnn.cacheable() )
 					.setCacheRegion(
 							BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
 									null :
 									queryAnn.cacheRegion()
 					)
 					.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
 					.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
 					.setFlushMode( getFlushMode( queryAnn.flushMode() ) )
 					.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
 					.setReadOnly( queryAnn.readOnly() )
 					.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
 					.setParameterTypes( null )
 					.setCallable( queryAnn.callable() )
 					.createNamedQueryDefinition();
 		}
 		else {
 			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 		}
 		mappings.addSQLQuery( query.getName(), query );
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named native query: %s => %s", query.getName(), queryAnn.query() );
 		}
 	}
 
 	public static void bindQueries(NamedQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(NamedNativeQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(
 			org.hibernate.annotations.NamedNativeQueries queriesAnn, Mappings mappings
 	) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings );
 		}
 	}
 
 	public static void bindQuery(org.hibernate.annotations.NamedQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		FlushMode flushMode;
 		flushMode = getFlushMode( queryAnn.flushMode() );
 
 		NamedQueryDefinition query = new NamedQueryDefinitionBuilder().setName( queryAnn.name() )
 				.setQuery( queryAnn.query() )
 				.setCacheable( queryAnn.cacheable() )
 				.setCacheRegion(
 						BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
 								null :
 								queryAnn.cacheRegion()
 				)
 				.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
 				.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
 				.setFlushMode( flushMode )
 				.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
 				.setReadOnly( queryAnn.readOnly() )
 				.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
 				.setParameterTypes( null )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( query.getName(), query );
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named query: %s => %s", query.getName(), query.getQueryString() );
 		}
 	}
 
 	private static FlushMode getFlushMode(FlushModeType flushModeType) {
 		FlushMode flushMode;
 		switch ( flushModeType ) {
 			case ALWAYS:
 				flushMode = FlushMode.ALWAYS;
 				break;
 			case AUTO:
 				flushMode = FlushMode.AUTO;
 				break;
 			case COMMIT:
 				flushMode = FlushMode.COMMIT;
 				break;
 			case NEVER:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case MANUAL:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case PERSISTENCE_CONTEXT:
 				flushMode = null;
 				break;
 			default:
 				throw new AssertionFailure( "Unknown flushModeType: " + flushModeType );
 		}
 		return flushMode;
 	}
 
 	private static CacheMode getCacheMode(CacheModeType cacheModeType) {
 		switch ( cacheModeType ) {
 			case GET:
 				return CacheMode.GET;
 			case IGNORE:
 				return CacheMode.IGNORE;
 			case NORMAL:
 				return CacheMode.NORMAL;
 			case PUT:
 				return CacheMode.PUT;
 			case REFRESH:
 				return CacheMode.REFRESH;
 			default:
 				throw new AssertionFailure( "Unknown cacheModeType: " + cacheModeType );
 		}
 	}
 
 
 	public static void bindQueries(org.hibernate.annotations.NamedQueries queriesAnn, Mappings mappings) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings );
 		}
 	}
 
+	public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, Mappings mappings) {
+		if ( annotation == null ) {
+			return;
+		}
+
+		if ( BinderHelper.isEmptyAnnotationValue( annotation.name() ) ) {
+			throw new AnnotationException( "A named query must have a name when used in class or package level" );
+		}
+
+		final NamedProcedureCallDefinition def = new NamedProcedureCallDefinition( annotation );
+		mappings.addNamedProcedureCallDefinition( def );
+		LOG.debugf( "Bound named stored procedure query : %s => %s", def.getRegisteredName(), def.getProcedureName() );
+	}
+
 	public static void bindSqlResultsetMappings(SqlResultSetMappings ann, Mappings mappings, boolean isDefault) {
 		if ( ann == null ) return;
 		for (SqlResultSetMapping rs : ann.value()) {
 			//no need to handle inSecondPass
 			mappings.addSecondPass( new ResultsetMappingSecondPass( rs, mappings, true ) );
 		}
 	}
 
 	public static void bindSqlResultsetMapping(SqlResultSetMapping ann, Mappings mappings, boolean isDefault) {
 		//no need to handle inSecondPass
 		mappings.addSecondPass( new ResultsetMappingSecondPass( ann, mappings, isDefault ) );
 	}
 
 	private static CacheMode getCacheMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.cacheMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 					return CacheMode.GET;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 					return CacheMode.IGNORE;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 					return CacheMode.NORMAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 					return CacheMode.PUT;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 					return CacheMode.REFRESH;
 				}
 				else {
 					throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static FlushMode getFlushMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.flushMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 					return FlushMode.ALWAYS;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 					return FlushMode.AUTO;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 					return FlushMode.COMMIT;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else {
 					throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static boolean getBoolean(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( "true" ) ) {
 					return true;
 				}
 				else if ( hint.value().equalsIgnoreCase( "false" ) ) {
 					return false;
 				}
 				else {
 					throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return false;
 	}
 
 	private static String getString(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				return hint.value();
 			}
 		}
 		return null;
 	}
 
 	private static Integer getInteger(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				try {
 					return Integer.decode( hint.value() );
 				}
 				catch (NumberFormatException nfe) {
 					throw new AnnotationException( "Not an integer in hint: " + query + ":" + hint.name(), nfe );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(String queryName, QueryHint[] hints) {
 		Integer timeout = getInteger( queryName, "javax.persistence.query.timeout", hints );
 
 		if ( timeout != null ) {
 			// convert milliseconds to seconds
 			timeout = (int)Math.round(timeout.doubleValue() / 1000.0 );
 		}
 		else {
 			// timeout is already in seconds
 			timeout = getInteger( queryName, "org.hibernate.timeout", hints );
 		}
 		return timeout;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
index 9e2686574c..d9c1908465 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
@@ -1,710 +1,793 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Query;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.type.Type;
 
 /**
  * This class is meant to be extended.
  * 
  * Wraps and delegates all methods to a {@link SessionImplementor} and
  * a {@link Session}. This is useful for custom implementations of this
  * API so that only some methods need to be overridden
  * (Used by Hibernate Search).
  * 
  * @author Sanne Grinovero <sanne@hibernate.org> (C) 2012 Red Hat Inc.
  */
 public class SessionDelegatorBaseImpl implements SessionImplementor, Session {
 
 	protected final SessionImplementor sessionImplementor;
 	protected final Session session;
 
 	public SessionDelegatorBaseImpl(SessionImplementor sessionImplementor, Session session) {
 		if ( sessionImplementor == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null sessionImplementor object" );
 		}
 		if ( session == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null session object" );
 		}
 		this.sessionImplementor = sessionImplementor;
 		this.session = session;
 	}
 
 	// Delegates to SessionImplementor
 
 	@Override
 	public <T> T execute(Callback<T> callback) {
 		return sessionImplementor.execute( callback );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return sessionImplementor.getTenantIdentifier();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return sessionImplementor.getJdbcConnectionAccess();
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return sessionImplementor.generateEntityKey( id, persister );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return sessionImplementor.generateCacheKey( id, type, entityOrRoleName );
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return sessionImplementor.getInterceptor();
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		sessionImplementor.setAutoClear( enabled );
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		sessionImplementor.disableTransactionAutoJoin();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return sessionImplementor.isTransactionInProgress();
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
 		sessionImplementor.initializeCollection( collection, writing );
 	}
 
 	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		return sessionImplementor.internalLoad( entityName, id, eager, nullable );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.immediateLoad( entityName, id );
 	}
 
 	@Override
 	public long getTimestamp() {
 		return sessionImplementor.getTimestamp();
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return sessionImplementor.getFactory();
 	}
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( query, queryParameters );
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterate( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		return sessionImplementor.scroll( criteria, scrollMode );
 	}
 
 	@Override
 	public List list(Criteria criteria) {
 		return sessionImplementor.list( criteria );
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterateFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
 		return sessionImplementor.getEntityPersister( entityName, object );
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		return sessionImplementor.getEntityUsingInterceptor( key );
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		return sessionImplementor.getContextEntityIdentifier( object );
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		return sessionImplementor.bestGuessEntityName( object );
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		return sessionImplementor.guessEntityName( entity );
 	}
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.instantiate( entityName, id );
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scrollCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( spec, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( spec, queryParameters );
 	}
 
 	@Override
 	public Object getFilterParameterValue(String filterParameterName) {
 		return sessionImplementor.getFilterParameterValue( filterParameterName );
 	}
 
 	@Override
 	public Type getFilterParameterType(String filterParameterName) {
 		return sessionImplementor.getFilterParameterType( filterParameterName );
 	}
 
 	@Override
 	public Map getEnabledFilters() {
 		return sessionImplementor.getEnabledFilters();
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return sessionImplementor.getDontFlushFromFind();
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return sessionImplementor.getPersistenceContext();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeUpdate( query, queryParameters );
 	}
 
 	@Override
 	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeNativeUpdate( specification, queryParameters );
 	}
 
 	@Override
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		return sessionImplementor.getNonFlushedChanges();
 	}
 
 	@Override
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		sessionImplementor.applyNonFlushedChanges( nonFlushedChanges );
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return sessionImplementor.getCacheMode();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		sessionImplementor.setCacheMode( cm );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return sessionImplementor.isOpen();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return sessionImplementor.isConnected();
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return sessionImplementor.getFlushMode();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		sessionImplementor.setFlushMode( fm );
 	}
 
 	@Override
 	public Connection connection() {
 		return sessionImplementor.connection();
 	}
 
 	@Override
 	public void flush() {
 		sessionImplementor.flush();
 	}
 
 	@Override
 	public Query getNamedQuery(String name) {
 		return sessionImplementor.getNamedQuery( name );
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String name) {
 		return sessionImplementor.getNamedSQLQuery( name );
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return sessionImplementor.isEventSource();
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		sessionImplementor.afterScrollOperation();
 	}
 
 	@Override
 	public String getFetchProfile() {
 		return sessionImplementor.getFetchProfile();
 	}
 
 	@Override
 	public void setFetchProfile(String name) {
 		sessionImplementor.setFetchProfile( name );
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return sessionImplementor.getTransactionCoordinator();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return sessionImplementor.isClosed();
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return sessionImplementor.getLoadQueryInfluencers();
 	}
 
 	// Delegates to Session
 
+	@Override
 	public Transaction beginTransaction() {
 		return session.beginTransaction();
 	}
 
+	@Override
 	public Transaction getTransaction() {
 		return session.getTransaction();
 	}
 
+	@Override
 	public Query createQuery(String queryString) {
 		return session.createQuery( queryString );
 	}
 
+	@Override
 	public SQLQuery createSQLQuery(String queryString) {
 		return session.createSQLQuery( queryString );
 	}
 
 	@Override
+	public ProcedureCall getNamedProcedureCall(String name) {
+		return session.getNamedProcedureCall( name );
+	}
+
+	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		return session.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		return session.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		return session.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		return session.createCriteria( persistentClass );
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		return session.createCriteria( persistentClass, alias );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName) {
 		return session.createCriteria( entityName );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		return session.createCriteria( entityName, alias );
 	}
 
+	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return session.sessionWithOptions();
 	}
 
+	@Override
 	public SessionFactory getSessionFactory() {
 		return session.getSessionFactory();
 	}
 
+	@Override
 	public Connection close() throws HibernateException {
 		return session.close();
 	}
 
+	@Override
 	public void cancelQuery() throws HibernateException {
 		session.cancelQuery();
 	}
 
+	@Override
 	public boolean isDirty() throws HibernateException {
 		return session.isDirty();
 	}
 
+	@Override
 	public boolean isDefaultReadOnly() {
 		return session.isDefaultReadOnly();
 	}
 
+	@Override
 	public void setDefaultReadOnly(boolean readOnly) {
 		session.setDefaultReadOnly( readOnly );
 	}
 
+	@Override
 	public Serializable getIdentifier(Object object) {
 		return session.getIdentifier( object );
 	}
 
+	@Override
 	public boolean contains(Object object) {
 		return session.contains( object );
 	}
 
+	@Override
 	public void evict(Object object) {
 		session.evict( object );
 	}
 
+	@Override
 	public Object load(Class theClass, Serializable id, LockMode lockMode) {
 		return session.load( theClass, id, lockMode );
 	}
 
+	@Override
 	public Object load(Class theClass, Serializable id, LockOptions lockOptions) {
 		return session.load( theClass, id, lockOptions );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) {
 		return session.load( entityName, id, lockMode );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.load( entityName, id, lockOptions );
 	}
 
+	@Override
 	public Object load(Class theClass, Serializable id) {
 		return session.load( theClass, id );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id) {
 		return session.load( entityName, id );
 	}
 
+	@Override
 	public void load(Object object, Serializable id) {
 		session.load( object, id );
 	}
 
+	@Override
 	public void replicate(Object object, ReplicationMode replicationMode) {
 		session.replicate( object, replicationMode );
 	}
 
+	@Override
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
 		session.replicate( entityName, object, replicationMode );
 	}
 
+	@Override
 	public Serializable save(Object object) {
 		return session.save( object );
 	}
 
+	@Override
 	public Serializable save(String entityName, Object object) {
 		return session.save( entityName, object );
 	}
 
+	@Override
 	public void saveOrUpdate(Object object) {
 		session.saveOrUpdate( object );
 	}
 
+	@Override
 	public void saveOrUpdate(String entityName, Object object) {
 		session.saveOrUpdate( entityName, object );
 	}
 
+	@Override
 	public void update(Object object) {
 		session.update( object );
 	}
 
+	@Override
 	public void update(String entityName, Object object) {
 		session.update( entityName, object );
 	}
 
+	@Override
 	public Object merge(Object object) {
 		return session.merge( object );
 	}
 
+	@Override
 	public Object merge(String entityName, Object object) {
 		return session.merge( entityName, object );
 	}
 
+	@Override
 	public void persist(Object object) {
 		session.persist( object );
 	}
 
+	@Override
 	public void persist(String entityName, Object object) {
 		session.persist( entityName, object );
 	}
 
+	@Override
 	public void delete(Object object) {
 		session.delete( object );
 	}
 
+	@Override
 	public void delete(String entityName, Object object) {
 		session.delete( entityName, object );
 	}
 
+	@Override
 	public void lock(Object object, LockMode lockMode) {
 		session.lock( object, lockMode );
 	}
 
+	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) {
 		session.lock( entityName, object, lockMode );
 	}
 
+	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return session.buildLockRequest( lockOptions );
 	}
 
+	@Override
 	public void refresh(Object object) {
 		session.refresh( object );
 	}
 
+	@Override
 	public void refresh(String entityName, Object object) {
 		session.refresh( entityName, object );
 	}
 
+	@Override
 	public void refresh(Object object, LockMode lockMode) {
 		session.refresh( object, lockMode );
 	}
 
+	@Override
 	public void refresh(Object object, LockOptions lockOptions) {
 		session.refresh( object, lockOptions );
 	}
 
+	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) {
 		session.refresh( entityName, object, lockOptions );
 	}
 
+	@Override
 	public LockMode getCurrentLockMode(Object object) {
 		return session.getCurrentLockMode( object );
 	}
 
+	@Override
 	public Query createFilter(Object collection, String queryString) {
 		return session.createFilter( collection, queryString );
 	}
 
+	@Override
 	public void clear() {
 		session.clear();
 	}
 
+	@Override
 	public Object get(Class clazz, Serializable id) {
 		return session.get( clazz, id );
 	}
 
+	@Override
 	public Object get(Class clazz, Serializable id, LockMode lockMode) {
 		return session.get( clazz, id, lockMode );
 	}
 
+	@Override
 	public Object get(Class clazz, Serializable id, LockOptions lockOptions) {
 		return session.get( clazz, id, lockOptions );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id) {
 		return session.get( entityName, id );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		return session.get( entityName, id, lockMode );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.get( entityName, id, lockOptions );
 	}
 
+	@Override
 	public String getEntityName(Object object) {
 		return session.getEntityName( object );
 	}
 
+	@Override
 	public IdentifierLoadAccess byId(String entityName) {
 		return session.byId( entityName );
 	}
 
+	@Override
 	public IdentifierLoadAccess byId(Class entityClass) {
 		return session.byId( entityClass );
 	}
 
+	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return session.byNaturalId( entityName );
 	}
 
+	@Override
 	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
 		return session.byNaturalId( entityClass );
 	}
 
+	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return session.bySimpleNaturalId( entityName );
 	}
 
+	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
 		return session.bySimpleNaturalId( entityClass );
 	}
 
+	@Override
 	public Filter enableFilter(String filterName) {
 		return session.enableFilter( filterName );
 	}
 
+	@Override
 	public Filter getEnabledFilter(String filterName) {
 		return session.getEnabledFilter( filterName );
 	}
 
+	@Override
 	public void disableFilter(String filterName) {
 		session.disableFilter( filterName );
 	}
 
+	@Override
 	public SessionStatistics getStatistics() {
 		return session.getStatistics();
 	}
 
+	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		return session.isReadOnly( entityOrProxy );
 	}
 
+	@Override
 	public void setReadOnly(Object entityOrProxy, boolean readOnly) {
 		session.setReadOnly( entityOrProxy, readOnly );
 	}
 
+	@Override
 	public void doWork(Work work) throws HibernateException {
 		session.doWork( work );
 	}
 
+	@Override
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
 		return session.doReturningWork( work );
 	}
 
+	@Override
 	public Connection disconnect() {
 		return session.disconnect();
 	}
 
+	@Override
 	public void reconnect(Connection connection) {
 		session.reconnect( connection );
 	}
 
+	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return session.isFetchProfileEnabled( name );
 	}
 
+	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		session.enableFetchProfile( name );
 	}
 
+	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		session.disableFetchProfile( name );
 	}
 
+	@Override
 	public TypeHelper getTypeHelper() {
 		return session.getTypeHelper();
 	}
 
+	@Override
 	public LobHelper getLobHelper() {
 		return session.getLobHelper();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index 0e997fc478..aee333ad27 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,292 +1,293 @@
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
 package org.hibernate.engine.spi;
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	@Override
 	public SessionBuilderImplementor withOptions();
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	public Map<String,EntityPersister> getEntityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	public Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 *
 	 * @deprecated Access to connections via {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} should
 	 * be preferred over access via {@link ConnectionProvider}, whenever possible.
 	 * {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} is tied to the Hibernate Session to
 	 * properly account for contextual information.  See {@link SessionImplementor#getJdbcConnectionAccess()}
 	 */
 	@Deprecated
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	public NamedQueryDefinition getNamedQuery(String queryName);
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 	
 	/**
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getNaturalIdCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
     public SqlExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	/**
 	 * Provides access to the named query repository
 	 *
 	 * @return
 	 */
 	public NamedQueryRepository getNamedQueryRepository();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index c6b0153c74..4ed1481626 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,383 +1,401 @@
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
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
+import java.util.Map;
 import java.util.UUID;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.id.uuid.StandardRandomStrategy;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.internal.ProcedureCallImpl;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
-public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract,
-													 SessionImplementor, TransactionContext {
+public abstract class AbstractSessionImpl
+		implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
-	private boolean closed = false;
+	private boolean closed;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getTransactionCoordinator().getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
 	@Override
 	public boolean isClosed() {
 		return closed;
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( closed ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			String queryString = nqd.getQueryString();
 			query = new QueryImpl(
 					queryString,
 			        nqd.getFlushMode(),
 			        this,
 			        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 			);
 			query.setComment( "named HQL query " + queryName );
 			if ( nqd.getLockOptions() != null ) {
 				query.setLockOptions( nqd.getLockOptions() );
 			}
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() );
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 					parameterMetadata
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize() );
 		}
 		if ( nqd.getCacheMode() != null ) {
 			query.setCacheMode( nqd.getCacheMode() );
 		}
 		if ( nqd.getComment() != null ) {
 			query.setComment( nqd.getComment() );
 		}
 		if ( nqd.getFirstResult() != null ) {
 			query.setFirstResult( nqd.getFirstResult() );
 		}
 		if ( nqd.getMaxResults() != null ) {
 			query.setMaxResults( nqd.getMaxResults() );
 		}
 		if ( nqd.getFlushMode() != null ) {
 			query.setFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
-		QueryImpl query = new QueryImpl(
+		final QueryImpl query = new QueryImpl(
 				queryString,
-		        this,
-		        getHQLQueryPlan( queryString, false ).getParameterMetadata()
+				this,
+				getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
-		SQLQueryImpl query = new SQLQueryImpl(
+		final SQLQueryImpl query = new SQLQueryImpl(
 				sql,
-		        this,
-		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
+				this,
+				factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
+	public ProcedureCall getNamedProcedureCall(String name) {
+		errorIfClosed();
+
+		final ProcedureCallMemento memento = factory.getNamedQueryRepository().getNamedProcedureCallMemento( name );
+		if ( memento == null ) {
+			throw new IllegalArgumentException(
+					"Could not find named stored procedure call with that registration name : " + name
+			);
+		}
+		final ProcedureCall procedureCall = memento.makeProcedureCall( this );
+//		procedureCall.setComment( "Named stored procedure call [" + name + "]" );
+		return procedureCall;
+	}
+
+	@Override
+	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister, getTenantIdentifier() );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private UUID sessionIdentifier;
 
 	public UUID getSessionIdentifier() {
 		if ( sessionIdentifier == null ) {
 			sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID( this );
 		}
 		return sessionIdentifier;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			return connectionProvider.getConnection( tenantIdentifier );
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			connectionProvider.releaseConnection( tenantIdentifier, connection );
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java b/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
index 6dc7bb2425..90866b06ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
@@ -1,185 +1,206 @@
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
 package org.hibernate.internal;
 
 import java.util.Collections;
 import java.util.HashMap;
-import java.util.Iterator;
+import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.QueryException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.procedure.ProcedureCallMemento;
 
 /**
  * @author Steve Ebersole
  */
 public class NamedQueryRepository {
 	private static final Logger log = Logger.getLogger( NamedQueryRepository.class );
 
+	private final Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap;
+
 	private volatile Map<String, NamedQueryDefinition> namedQueryDefinitionMap;
 	private volatile Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap;
-	private final Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap;
+	private volatile Map<String, ProcedureCallMemento> procedureCallMementoMap;
 
 	public NamedQueryRepository(
 			Iterable<NamedQueryDefinition> namedQueryDefinitions,
 			Iterable<NamedSQLQueryDefinition> namedSqlQueryDefinitions,
-			Iterable<ResultSetMappingDefinition> namedSqlResultSetMappings) {
+			Iterable<ResultSetMappingDefinition> namedSqlResultSetMappings,
+			List<ProcedureCallMemento> namedProcedureCalls) {
 		final HashMap<String, NamedQueryDefinition> namedQueryDefinitionMap = new HashMap<String, NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition : namedQueryDefinitions ) {
 			namedQueryDefinitionMap.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		this.namedQueryDefinitionMap = Collections.unmodifiableMap( namedQueryDefinitionMap );
 
 
 		final HashMap<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedSqlQueryDefinition : namedSqlQueryDefinitions ) {
 			namedSqlQueryDefinitionMap.put( namedSqlQueryDefinition.getName(), namedSqlQueryDefinition );
 		}
 		this.namedSqlQueryDefinitionMap = Collections.unmodifiableMap( namedSqlQueryDefinitionMap );
 
 		final HashMap<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap = new HashMap<String, ResultSetMappingDefinition>();
 		for ( ResultSetMappingDefinition resultSetMappingDefinition : namedSqlResultSetMappings ) {
 			namedSqlResultSetMappingMap.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
 		this.namedSqlResultSetMappingMap = Collections.unmodifiableMap( namedSqlResultSetMappingMap );
 	}
 
 
 	public NamedQueryDefinition getNamedQueryDefinition(String queryName) {
 		return namedQueryDefinitionMap.get( queryName );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQueryDefinition(String queryName) {
 		return namedSqlQueryDefinitionMap.get( queryName );
 	}
 
+	public ProcedureCallMemento getNamedProcedureCallMemento(String name) {
+		return procedureCallMementoMap.get( name );
+	}
+
 	public ResultSetMappingDefinition getResultSetMappingDefinition(String mappingName) {
 		return namedSqlResultSetMappingMap.get( mappingName );
 	}
 
 	public synchronized void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		if ( NamedSQLQueryDefinition.class.isInstance( definition ) ) {
 			throw new IllegalArgumentException( "NamedSQLQueryDefinition instance incorrectly passed to registerNamedQueryDefinition" );
 		}
 
 		if ( ! name.equals( definition.getName() ) ) {
 			definition = definition.makeCopy( name );
 		}
 
 		final Map<String, NamedQueryDefinition> copy = CollectionHelper.makeCopy( namedQueryDefinitionMap );
 		final NamedQueryDefinition previous = copy.put( name, definition );
 		if ( previous != null ) {
 			log.debugf(
 					"registering named query definition [%s] overriding previously registered definition [%s]",
 					name,
 					previous
 			);
 		}
 
 		this.namedQueryDefinitionMap = Collections.unmodifiableMap( copy );
 	}
 
 	public synchronized void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		if ( ! name.equals( definition.getName() ) ) {
 			definition = definition.makeCopy( name );
 		}
 
 		final Map<String, NamedSQLQueryDefinition> copy = CollectionHelper.makeCopy( namedSqlQueryDefinitionMap );
 		final NamedQueryDefinition previous = copy.put( name, definition );
 		if ( previous != null ) {
 			log.debugf(
 					"registering named SQL query definition [%s] overriding previously registered definition [%s]",
 					name,
 					previous
 			);
 		}
 
 		this.namedSqlQueryDefinitionMap = Collections.unmodifiableMap( copy );
 	}
 
+	public synchronized void registerNamedProcedureCallMemento(String name, ProcedureCallMemento memento) {
+		final Map<String, ProcedureCallMemento> copy = CollectionHelper.makeCopy( procedureCallMementoMap );
+		final ProcedureCallMemento previous = copy.put( name, memento );
+		if ( previous != null ) {
+			log.debugf(
+					"registering named procedure call definition [%s] overriding previously registered definition [%s]",
+					name,
+					previous
+			);
+		}
+
+		this.procedureCallMementoMap = Collections.unmodifiableMap( copy );
+	}
+
 	public Map<String,HibernateException> checkNamedQueries(QueryPlanCache queryPlanCache) {
 		Map<String,HibernateException> errors = new HashMap<String,HibernateException>();
 
 		// Check named HQL queries
 		log.debugf( "Checking %s named HQL queries", namedQueryDefinitionMap.size() );
 		for ( NamedQueryDefinition namedQueryDefinition : namedQueryDefinitionMap.values() ) {
 			// this will throw an error if there's something wrong.
 			try {
 				log.debugf( "Checking named query: %s", namedQueryDefinition.getName() );
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( namedQueryDefinition.getQueryString(), false, Collections.EMPTY_MAP );
 			}
 			catch ( HibernateException e ) {
 				errors.put( namedQueryDefinition.getName(), e );
 			}
 
 
 		}
 
 		// Check native-sql queries
 		log.debugf( "Checking %s named SQL queries", namedSqlQueryDefinitionMap.size() );
 		for ( NamedSQLQueryDefinition namedSQLQueryDefinition : namedSqlQueryDefinitionMap.values() ) {
 			// this will throw an error if there's something wrong.
 			try {
 				log.debugf( "Checking named SQL query: %s", namedSQLQueryDefinition.getName() );
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( namedSQLQueryDefinition.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = getResultSetMappingDefinition( namedSQLQueryDefinition.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + namedSQLQueryDefinition.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							namedSQLQueryDefinition.getQueryString(),
 							definition.getQueryReturns(),
 							namedSQLQueryDefinition.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							namedSQLQueryDefinition.getQueryString(),
 							namedSQLQueryDefinition.getQueryReturns(),
 							namedSQLQueryDefinition.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( HibernateException e ) {
 				errors.put( namedSQLQueryDefinition.getName(), e );
 			}
 		}
 
 		return errors;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 45b2529fa1..3f191d8a8b 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1756 +1,1773 @@
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
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
+import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
+import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
+import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.jboss.logging.Logger;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 	private final transient CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 	private final transient CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 	@SuppressWarnings( {"unchecked", "ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			final Configuration cfg,
 			Mapping mapping,
 			final ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public StandardServiceRegistry getServiceRegistry() {
 				return (StandardServiceRegistry) serviceRegistry;
 			}
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
         this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
         this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		// todo : everything above here consider implementing as standard SF service.  specifically: stats, caches, types, function-reg
 
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
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
 						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
 			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 					EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 			
 			NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 			if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 				final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 				naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) entityAccessStrategies.get( naturalIdCacheRegionName );
 				
 				if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 					final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 					
 					NaturalIdRegion naturalIdRegion = null;
 					try {
 						naturalIdRegion = regionFactory.buildNaturalIdRegion( naturalIdCacheRegionName, properties,
 								cacheDataDescription );
 					}
 					catch ( UnsupportedOperationException e ) {
 						LOG.warnf(
 								"Shared cache region factory [%s] does not support natural id caching; " +
 										"shared NaturalId caching will be disabled for not be enabled for %s",
 								regionFactory.getClass().getName(),
 								model.getEntityName()
 						);
 					}
 					
 					if (naturalIdRegion != null) {
 						naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 						entityAccessStrategies.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 						cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 					}
 				}
 			}
 			
 			EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = persisterFactory.createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = new NamedQueryRepository(
 				cfg.getNamedQueries().values(),
 				cfg.getNamedSQLQueries().values(),
-				cfg.getSqlResultSetMappings().values()
+				cfg.getSqlResultSetMappings().values(),
+				toProcedureCallMementos( cfg.getNamedProcedureCallMap(), cfg.getSqlResultSetMappings() )
 		);
 		imports = new HashMap<String,String>( cfg.getImports() );
 
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
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				cfg.createMappings(),
 				cfg.buildMapping(),
 				properties
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
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
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( cfg.getCurrentTenantIdentifierResolver() );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
+	private List<ProcedureCallMemento> toProcedureCallMementos(
+			Map<String, NamedProcedureCallDefinition> definitions,
+			Map<String, ResultSetMappingDefinition> resultSetMappingMap) {
+		final List<ProcedureCallMemento> rtn = new ArrayList<ProcedureCallMemento>();
+		if ( definitions != null ) {
+			for ( NamedProcedureCallDefinition definition : definitions.values() ) {
+				rtn.add( definition.toMemento( this, resultSetMappingMap ) );
+			}
+		}
+		return rtn;
+	}
+
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CustomEntityDirtinessStrategy determineCustomEntityDirtinessStrategy() {
 		CustomEntityDirtinessStrategy defaultValue = new CustomEntityDirtinessStrategy() {
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
 		return serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY,
 				CustomEntityDirtinessStrategy.class,
 				defaultValue
 		);
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CurrentTenantIdentifierResolver determineCurrentTenantIdentifierResolver(
 			CurrentTenantIdentifierResolver explicitResolver) {
 		if ( explicitResolver != null ) {
 			return explicitResolver;
 		}
 		return serviceRegistry.getService( ConfigurationService.class )
 				.getSetting(
 						AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
 						CurrentTenantIdentifierResolver.class,
 						null
 				);
 
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		final boolean debugEnabled = traceEnabled || LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debug( "Building session factory" );
 		}
 
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
 				sessionFactoryOptions.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 
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
 
 		if ( debugEnabled ) {
 			LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 			LOG.debugf( "Instantiating session factory with properties: %s", properties );
 		}
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
 					if ( traceEnabled ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
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
 				if ( traceEnabled ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion(  cacheRegionName, collectionRegion );
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
 		namedQueryRepository = new NamedQueryRepository(
 				metadata.getNamedQueryDefinitions(),
 				metadata.getNamedNativeQueryDefinitions(),
-				metadata.getResultSetMappingDefinitions()
+				metadata.getResultSetMappingDefinitions(),
+				null
 		);
 
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
 
 		if ( debugEnabled ) {
 			LOG.debug("Instantiated session factory");
 		}
 
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
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( null );
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
 	public SessionBuilderImplementor withOptions() {
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
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get(entityName);
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
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
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnAliases();
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
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
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
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
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
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
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
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
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
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
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
 
 	private TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
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
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
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
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
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
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
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
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
index 377622d2b6..ff56575229 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
@@ -1,155 +1,167 @@
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
  */
 package org.hibernate.internal.util.collections;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
+import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 /**
  * Various help for handling collections.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class CollectionHelper {
     public static final int MINIMUM_INITIAL_CAPACITY = 16;
 	public static final float LOAD_FACTOR = 0.75f;
 
 	@Deprecated // use java.util.Collections.EMPTY_LIST instead
 	public static final List EMPTY_LIST = Collections.EMPTY_LIST;
 	@Deprecated // use java.util.Collections.EMPTY_LIST instead
 	public static final Collection EMPTY_COLLECTION = Collections.EMPTY_LIST;
 	@Deprecated // use java.util.Collections.EMPTY_MAP instead
 	public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
 
 	private CollectionHelper() {
 	}
 
 	/**
 	 * Build a properly sized map, especially handling load size and load factor to prevent immediate resizing.
 	 * <p/>
 	 * Especially helpful for copy map contents.
 	 *
 	 * @param size The size to make the map.
 	 * @return The sized map.
 	 */
 	public static <K,V> Map<K,V> mapOfSize(int size) {
 		return new HashMap<K,V>( determineProperSizing( size ), LOAD_FACTOR );
 	}
 
 	/**
 	 * Given a map, determine the proper initial size for a new Map to hold the same number of values.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param original The original map
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(Map original) {
 		return determineProperSizing( original.size() );
 	}
 
 	/**
 	 * Given a set, determine the proper initial size for a new set to hold the same number of values.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param original The original set
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(Set original) {
 		return determineProperSizing( original.size() );
 	}
 
 	/**
 	 * Determine the proper initial size for a new collection in order for it to hold the given a number of elements.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param numberOfElements The number of elements to be stored.
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(int numberOfElements) {
 		int actual = ( (int) (numberOfElements / LOAD_FACTOR) ) + 1;
 		return Math.max( actual, MINIMUM_INITIAL_CAPACITY );
 	}
 
 	/**
 	 * Create a properly sized {@link ConcurrentHashMap} based on the given expected number of elements.
 	 *
 	 * @param expectedNumberOfElements The expected number of elements for the created map
 	 * @param <K> The map key type
 	 * @param <V> The map value type
 	 *
 	 * @return The created map.
 	 */
 	public static <K,V> ConcurrentHashMap<K,V> concurrentMap(int expectedNumberOfElements) {
 		return concurrentMap( expectedNumberOfElements, LOAD_FACTOR );
 	}
 
 	/**
 	 * Create a properly sized {@link ConcurrentHashMap} based on the given expected number of elements and an
 	 * explicit load factor
 	 *
 	 * @param expectedNumberOfElements The expected number of elements for the created map
 	 * @param loadFactor The collection load factor
 	 * @param <K> The map key type
 	 * @param <V> The map value type
 	 *
 	 * @return The created map.
 	 */
 	public static <K,V> ConcurrentHashMap<K,V> concurrentMap(int expectedNumberOfElements, float loadFactor) {
 		final int size = expectedNumberOfElements + 1 + (int) ( expectedNumberOfElements * loadFactor );
 		return new ConcurrentHashMap<K, V>( size, loadFactor );
 	}
 
 	public static <T> List<T> arrayList(int anticipatedSize) {
 		return new ArrayList<T>( anticipatedSize );
 	}
 
+	public static <T> Set<T> makeCopy(Set<T> source) {
+		if ( source == null ) {
+			return null;
+		}
+
+		final int size = source.size();
+		final Set<T> copy = new HashSet<T>( size + 1 );
+		copy.addAll( source );
+		return copy;
+	}
+
     public static boolean isEmpty(Collection collection) {
         return collection == null || collection.isEmpty();
     }
 
     public static boolean isEmpty(Map map) {
         return map == null || map.isEmpty();
     }
 
     public static boolean isNotEmpty(Collection collection) {
         return !isEmpty( collection );
     }
 
     public static boolean isNotEmpty(Map map) {
         return !isEmpty( map );
     }
 
 	public static <X,Y> Map<X, Y> makeCopy(Map<X, Y> map) {
 		final Map<X,Y> copy = mapOfSize( map.size() + 1 );
 		copy.putAll( map );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
index fe2cc4713d..61921e41ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
@@ -1,521 +1,546 @@
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
 package org.hibernate.loader.custom.sql;
 import java.util.ArrayList;
+import java.util.Collection;
+import java.util.Collections;
 import java.util.HashMap;
+import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.ColumnEntityAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.custom.CollectionFetchReturn;
 import org.hibernate.loader.custom.CollectionReturn;
 import org.hibernate.loader.custom.ColumnCollectionAliases;
 import org.hibernate.loader.custom.EntityFetchReturn;
 import org.hibernate.loader.custom.FetchReturn;
 import org.hibernate.loader.custom.NonScalarReturn;
 import org.hibernate.loader.custom.Return;
 import org.hibernate.loader.custom.RootReturn;
 import org.hibernate.loader.custom.ScalarReturn;
+import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.SQLLoadableCollection;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.SQLLoadable;
+import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing the series of {@link org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn returns}
  * defined by a {@link org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification} and
  * breaking them down into a series of {@link Return returns} for use within the
  * {@link org.hibernate.loader.custom.CustomLoader}.
  *
  * @author Gavin King
  * @author Max Andersen
  * @author Steve Ebersole
  */
 public class SQLQueryReturnProcessor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        SQLQueryReturnProcessor.class.getName());
 
 	private NativeSQLQueryReturn[] queryReturns;
 
 //	private final List persisters = new ArrayList();
 
 	private final Map alias2Return = new HashMap();
 	private final Map alias2OwnerAlias = new HashMap();
 
-	private final Map alias2Persister = new HashMap();
+	private final Map<String,EntityPersister> alias2Persister = new HashMap<String,EntityPersister>();
 	private final Map alias2Suffix = new HashMap();
 
-	private final Map alias2CollectionPersister = new HashMap();
+	private final Map<String,CollectionPersister> alias2CollectionPersister = new HashMap<String,CollectionPersister>();
 	private final Map alias2CollectionSuffix = new HashMap();
 
 	private final Map entityPropertyResultMaps = new HashMap();
 	private final Map collectionPropertyResultMaps = new HashMap();
 
 //	private final List scalarTypes = new ArrayList();
 //	private final List scalarColumnAliases = new ArrayList();
 
 	private final SessionFactoryImplementor factory;
 
 //	private List collectionOwnerAliases = new ArrayList();
 //	private List collectionAliases = new ArrayList();
 //	private List collectionPersisters = new ArrayList();
 //	private List collectionResults = new ArrayList();
 
 	private int entitySuffixSeed = 0;
 	private int collectionSuffixSeed = 0;
 
 
 	public SQLQueryReturnProcessor(NativeSQLQueryReturn[] queryReturns, SessionFactoryImplementor factory) {
 		this.queryReturns = queryReturns;
 		this.factory = factory;
 	}
 
-	/*package*/ class ResultAliasContext {
+	public class ResultAliasContext {
 		public SQLLoadable getEntityPersister(String alias) {
 			return ( SQLLoadable ) alias2Persister.get( alias );
 		}
 
 		public SQLLoadableCollection getCollectionPersister(String alias) {
 			return ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
 		}
 
 		public String getEntitySuffix(String alias) {
 			return ( String ) alias2Suffix.get( alias );
 		}
 
 		public String getCollectionSuffix(String alias) {
 			return ( String ) alias2CollectionSuffix.get ( alias );
 		}
 
 		public String getOwnerAlias(String alias) {
 			return ( String ) alias2OwnerAlias.get( alias );
 		}
 
 		public Map getPropertyResultsMap(String alias) {
 			return internalGetPropertyResultsMap( alias );
 		}
+
+		public String[] collectQuerySpaces() {
+			final HashSet<String> spaces = new HashSet<String>();
+			collectQuerySpaces( spaces );
+			return spaces.toArray( new String[ spaces.size() ] );
+		}
+
+		public void collectQuerySpaces(Collection<String> spaces) {
+			for ( EntityPersister persister : alias2Persister.values() ) {
+				Collections.addAll( spaces, (String[]) persister.getQuerySpaces() );
+			}
+			for ( CollectionPersister persister : alias2CollectionPersister.values() ) {
+				final Type elementType = persister.getElementType();
+				if ( elementType.isEntityType() && ! elementType.isAnyType() ) {
+					final Joinable joinable = ( (EntityType) elementType ).getAssociatedJoinable( factory );
+					Collections.addAll( spaces, (String[]) ( (EntityPersister) joinable ).getQuerySpaces() );
+				}
+			}
+		}
 	}
 
 	private Map internalGetPropertyResultsMap(String alias) {
 		NativeSQLQueryReturn rtn = ( NativeSQLQueryReturn ) alias2Return.get( alias );
 		if ( rtn instanceof NativeSQLQueryNonScalarReturn ) {
 			return ( ( NativeSQLQueryNonScalarReturn ) rtn ).getPropertyResultsMap();
 		}
 		else {
 			return null;
 		}
 	}
 
 	private boolean hasPropertyResultMap(String alias) {
 		Map propertyMaps = internalGetPropertyResultsMap( alias );
 		return propertyMaps != null && ! propertyMaps.isEmpty();
 	}
 
 	public ResultAliasContext process() {
 		// first, break down the returns into maps keyed by alias
 		// so that role returns can be more easily resolved to their owners
 		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 			if ( queryReturn instanceof NativeSQLQueryNonScalarReturn ) {
 				NativeSQLQueryNonScalarReturn rtn = (NativeSQLQueryNonScalarReturn) queryReturn;
 				alias2Return.put( rtn.getAlias(), rtn );
 				if ( rtn instanceof NativeSQLQueryJoinReturn ) {
 					NativeSQLQueryJoinReturn fetchReturn = (NativeSQLQueryJoinReturn) rtn;
 					alias2OwnerAlias.put( fetchReturn.getAlias(), fetchReturn.getOwnerAlias() );
 				}
 			}
 		}
 
 		// Now, process the returns
 		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 			processReturn( queryReturn );
 		}
 
 		return new ResultAliasContext();
 	}
 
 	public List<Return> generateCustomReturns(boolean queryHadAliases) {
 		List<Return> customReturns = new ArrayList<Return>();
 		Map<String,Return> customReturnsByAlias = new HashMap<String,Return>();
 		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 			if ( queryReturn instanceof NativeSQLQueryScalarReturn ) {
 				NativeSQLQueryScalarReturn rtn = (NativeSQLQueryScalarReturn) queryReturn;
 				customReturns.add( new ScalarReturn( rtn.getType(), rtn.getColumnAlias() ) );
 			}
 			else if ( queryReturn instanceof NativeSQLQueryRootReturn ) {
 				NativeSQLQueryRootReturn rtn = (NativeSQLQueryRootReturn) queryReturn;
 				String alias = rtn.getAlias();
 				EntityAliases entityAliases;
 				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 					entityAliases = new DefaultEntityAliases(
 							(Map) entityPropertyResultMaps.get( alias ),
 							(SQLLoadable) alias2Persister.get( alias ),
 							(String) alias2Suffix.get( alias )
 					);
 				}
 				else {
 					entityAliases = new ColumnEntityAliases(
 							(Map) entityPropertyResultMaps.get( alias ),
 							(SQLLoadable) alias2Persister.get( alias ),
 							(String) alias2Suffix.get( alias )
 					);
 				}
 				RootReturn customReturn = new RootReturn(
 						alias,
 						rtn.getReturnEntityName(),
 						entityAliases,
 						rtn.getLockMode()
 				);
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( rtn.getAlias(), customReturn );
 			}
 			else if ( queryReturn instanceof NativeSQLQueryCollectionReturn ) {
 				NativeSQLQueryCollectionReturn rtn = (NativeSQLQueryCollectionReturn) queryReturn;
 				String alias = rtn.getAlias();
 				SQLLoadableCollection persister = (SQLLoadableCollection) alias2CollectionPersister.get( alias );
 				boolean isEntityElements = persister.getElementType().isEntityType();
 				CollectionAliases collectionAliases;
 				EntityAliases elementEntityAliases = null;
 				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 					collectionAliases = new GeneratedCollectionAliases(
 							(Map) collectionPropertyResultMaps.get( alias ),
 							(SQLLoadableCollection) alias2CollectionPersister.get( alias ),
 							(String) alias2CollectionSuffix.get( alias )
 					);
 					if ( isEntityElements ) {
 						elementEntityAliases = new DefaultEntityAliases(
 								(Map) entityPropertyResultMaps.get( alias ),
 								(SQLLoadable) alias2Persister.get( alias ),
 								(String) alias2Suffix.get( alias )
 						);
 					}
 				}
 				else {
 					collectionAliases = new ColumnCollectionAliases(
 							(Map) collectionPropertyResultMaps.get( alias ),
 							(SQLLoadableCollection) alias2CollectionPersister.get( alias )
 					);
 					if ( isEntityElements ) {
 						elementEntityAliases = new ColumnEntityAliases(
 								(Map) entityPropertyResultMaps.get( alias ),
 								(SQLLoadable) alias2Persister.get( alias ),
 								(String) alias2Suffix.get( alias )
 						);
 					}
 				}
 				CollectionReturn customReturn = new CollectionReturn(
 						alias,
 						rtn.getOwnerEntityName(),
 						rtn.getOwnerProperty(),
 						collectionAliases,
 						elementEntityAliases,
 						rtn.getLockMode()
 				);
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( rtn.getAlias(), customReturn );
 			}
 			else if ( queryReturn instanceof NativeSQLQueryJoinReturn ) {
 				NativeSQLQueryJoinReturn rtn = (NativeSQLQueryJoinReturn) queryReturn;
 				String alias = rtn.getAlias();
 				FetchReturn customReturn;
 				NonScalarReturn ownerCustomReturn = (NonScalarReturn) customReturnsByAlias.get( rtn.getOwnerAlias() );
 				if ( alias2CollectionPersister.containsKey( alias ) ) {
 					SQLLoadableCollection persister = (SQLLoadableCollection) alias2CollectionPersister.get( alias );
 					boolean isEntityElements = persister.getElementType().isEntityType();
 					CollectionAliases collectionAliases;
 					EntityAliases elementEntityAliases = null;
 					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 						collectionAliases = new GeneratedCollectionAliases(
 								(Map) collectionPropertyResultMaps.get( alias ),
 								persister,
 								(String) alias2CollectionSuffix.get( alias )
 						);
 						if ( isEntityElements ) {
 							elementEntityAliases = new DefaultEntityAliases(
 									(Map) entityPropertyResultMaps.get( alias ),
 									(SQLLoadable) alias2Persister.get( alias ),
 									(String) alias2Suffix.get( alias )
 							);
 						}
 					}
 					else {
 						collectionAliases = new ColumnCollectionAliases(
 								(Map) collectionPropertyResultMaps.get( alias ),
 								persister
 						);
 						if ( isEntityElements ) {
 							elementEntityAliases = new ColumnEntityAliases(
 									(Map) entityPropertyResultMaps.get( alias ),
 									(SQLLoadable) alias2Persister.get( alias ),
 									(String) alias2Suffix.get( alias )
 							);
 						}
 					}
 					customReturn = new CollectionFetchReturn(
 							alias,
 							ownerCustomReturn,
 							rtn.getOwnerProperty(),
 							collectionAliases,
 							elementEntityAliases,
 							rtn.getLockMode()
 					);
 				}
 				else {
 					EntityAliases entityAliases;
 					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 						entityAliases = new DefaultEntityAliases(
 								(Map) entityPropertyResultMaps.get( alias ),
 								(SQLLoadable) alias2Persister.get( alias ),
 								(String) alias2Suffix.get( alias )
 						);
 					}
 					else {
 						entityAliases = new ColumnEntityAliases(
 								(Map) entityPropertyResultMaps.get( alias ),
 								(SQLLoadable) alias2Persister.get( alias ),
 								(String) alias2Suffix.get( alias )
 						);
 					}
 					customReturn = new EntityFetchReturn(
 							alias,
 							entityAliases,
 							ownerCustomReturn,
 							rtn.getOwnerProperty(),
 							rtn.getLockMode()
 					);
 				}
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( alias, customReturn );
 			}
 		}
 		return customReturns;
 	}
 
 	private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister( entityName );
 		if ( !(persister instanceof SQLLoadable) ) {
 			throw new MappingException( "class persister is not SQLLoadable: " + entityName );
 		}
 		return (SQLLoadable) persister;
 	}
 
 	private String generateEntitySuffix() {
 		return BasicLoader.generateSuffixes( entitySuffixSeed++, 1 )[0];
 	}
 
 	private String generateCollectionSuffix() {
 		return collectionSuffixSeed++ + "__";
 	}
 
 	private void processReturn(NativeSQLQueryReturn rtn) {
 		if ( rtn instanceof NativeSQLQueryScalarReturn ) {
 			processScalarReturn( ( NativeSQLQueryScalarReturn ) rtn );
 		}
 		else if ( rtn instanceof NativeSQLQueryRootReturn ) {
 			processRootReturn( ( NativeSQLQueryRootReturn ) rtn );
 		}
 		else if ( rtn instanceof NativeSQLQueryCollectionReturn ) {
 			processCollectionReturn( ( NativeSQLQueryCollectionReturn ) rtn );
 		}
 		else {
 			processJoinReturn( ( NativeSQLQueryJoinReturn ) rtn );
 		}
 	}
 
 	private void processScalarReturn(NativeSQLQueryScalarReturn typeReturn) {
 //		scalarColumnAliases.add( typeReturn.getColumnAlias() );
 //		scalarTypes.add( typeReturn.getType() );
 	}
 
 	private void processRootReturn(NativeSQLQueryRootReturn rootReturn) {
 		if ( alias2Persister.containsKey( rootReturn.getAlias() ) ) {
 			// already been processed...
 			return;
 		}
 
 		SQLLoadable persister = getSQLLoadable( rootReturn.getReturnEntityName() );
 		addPersister( rootReturn.getAlias(), rootReturn.getPropertyResultsMap(), persister );
 	}
 
 	private void addPersister(String alias, Map propertyResult, SQLLoadable persister) {
 		alias2Persister.put( alias, persister );
 		String suffix = generateEntitySuffix();
 		LOG.tracev( "Mapping alias [{0}] to entity-suffix [{1}]", alias, suffix );
 		alias2Suffix.put( alias, suffix );
 		entityPropertyResultMaps.put( alias, propertyResult );
 	}
 
 	private void addCollection(String role, String alias, Map propertyResults) {
 		SQLLoadableCollection collectionPersister = ( SQLLoadableCollection ) factory.getCollectionPersister( role );
 		alias2CollectionPersister.put( alias, collectionPersister );
 		String suffix = generateCollectionSuffix();
 		LOG.tracev( "Mapping alias [{0}] to collection-suffix [{1}]", alias, suffix );
 		alias2CollectionSuffix.put( alias, suffix );
 		collectionPropertyResultMaps.put( alias, propertyResults );
 
 		if ( collectionPersister.isOneToMany() || collectionPersister.isManyToMany() ) {
 			SQLLoadable persister = ( SQLLoadable ) collectionPersister.getElementPersister();
 			addPersister( alias, filter( propertyResults ), persister );
 		}
 	}
 
 	private Map filter(Map propertyResults) {
 		Map result = new HashMap( propertyResults.size() );
 
 		String keyPrefix = "element.";
 
 		Iterator iter = propertyResults.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry element = ( Map.Entry ) iter.next();
 			String path = ( String ) element.getKey();
 			if ( path.startsWith( keyPrefix ) ) {
 				result.put( path.substring( keyPrefix.length() ), element.getValue() );
 			}
 		}
 
 		return result;
 	}
 
 	private void processCollectionReturn(NativeSQLQueryCollectionReturn collectionReturn) {
 		// we are initializing an owned collection
 		//collectionOwners.add( new Integer(-1) );
 //		collectionOwnerAliases.add( null );
 		String role = collectionReturn.getOwnerEntityName() + '.' + collectionReturn.getOwnerProperty();
 		addCollection(
 				role,
 				collectionReturn.getAlias(),
 				collectionReturn.getPropertyResultsMap()
 		);
 	}
 
 	private void processJoinReturn(NativeSQLQueryJoinReturn fetchReturn) {
 		String alias = fetchReturn.getAlias();
 //		if ( alias2Persister.containsKey( alias ) || collectionAliases.contains( alias ) ) {
 		if ( alias2Persister.containsKey( alias ) || alias2CollectionPersister.containsKey( alias ) ) {
 			// already been processed...
 			return;
 		}
 
 		String ownerAlias = fetchReturn.getOwnerAlias();
 
 		// Make sure the owner alias is known...
 		if ( !alias2Return.containsKey( ownerAlias ) ) {
 			throw new HibernateException( "Owner alias [" + ownerAlias + "] is unknown for alias [" + alias + "]" );
 		}
 
 		// If this return's alias has not been processed yet, do so b4 further processing of this return
 		if ( !alias2Persister.containsKey( ownerAlias ) ) {
 			NativeSQLQueryNonScalarReturn ownerReturn = ( NativeSQLQueryNonScalarReturn ) alias2Return.get(ownerAlias);
 			processReturn( ownerReturn );
 		}
 
 		SQLLoadable ownerPersister = ( SQLLoadable ) alias2Persister.get( ownerAlias );
 		Type returnType = ownerPersister.getPropertyType( fetchReturn.getOwnerProperty() );
 
 		if ( returnType.isCollectionType() ) {
 			String role = ownerPersister.getEntityName() + '.' + fetchReturn.getOwnerProperty();
 			addCollection( role, alias, fetchReturn.getPropertyResultsMap() );
 //			collectionOwnerAliases.add( ownerAlias );
 		}
 		else if ( returnType.isEntityType() ) {
 			EntityType eType = ( EntityType ) returnType;
 			String returnEntityName = eType.getAssociatedEntityName();
 			SQLLoadable persister = getSQLLoadable( returnEntityName );
 			addPersister( alias, fetchReturn.getPropertyResultsMap(), persister );
 		}
 
 	}
 
 //	public List getCollectionAliases() {
 //		return collectionAliases;
 //	}
 //
 //	/*public List getCollectionOwners() {
 //		return collectionOwners;
 //	}*/
 //
 //	public List getCollectionOwnerAliases() {
 //		return collectionOwnerAliases;
 //	}
 //
 //	public List getCollectionPersisters() {
 //		return collectionPersisters;
 //	}
 //
 //	public Map getAlias2Persister() {
 //		return alias2Persister;
 //	}
 //
 //	/*public boolean isCollectionInitializer() {
 //		return isCollectionInitializer;
 //	}*/
 //
 ////	public List getPersisters() {
 ////		return persisters;
 ////	}
 //
 //	public Map getAlias2OwnerAlias() {
 //		return alias2OwnerAlias;
 //	}
 //
 //	public List getScalarTypes() {
 //		return scalarTypes;
 //	}
 //	public List getScalarColumnAliases() {
 //		return scalarColumnAliases;
 //	}
 //
 //	public List getPropertyResults() {
 //		return propertyResults;
 //	}
 //
 //	public List getCollectionPropertyResults() {
 //		return collectionResults;
 //	}
 //
 //
 //	public Map getAlias2Return() {
 //		return alias2Return;
 //	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCall.java b/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCall.java
index 3ad02c4013..62ad4f01ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCall.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCall.java
@@ -1,136 +1,153 @@
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
 package org.hibernate.procedure;
 
 import javax.persistence.ParameterMode;
 import java.util.List;
+import java.util.Map;
 
 import org.hibernate.BasicQueryContract;
 import org.hibernate.MappingException;
 import org.hibernate.SynchronizeableQuery;
 
 /**
  * Defines support for executing database stored procedures and functions
  *
  * @author Steve Ebersole
  */
 public interface ProcedureCall extends BasicQueryContract, SynchronizeableQuery {
 	@Override
 	public ProcedureCall addSynchronizedQuerySpace(String querySpace);
 
 	@Override
 	public ProcedureCall addSynchronizedEntityName(String entityName) throws MappingException;
 
 	@Override
 	public ProcedureCall addSynchronizedEntityClass(Class entityClass) throws MappingException;
 
 	/**
 	 * Get the name of the stored procedure to be called.
 	 *
 	 * @return The procedure name.
 	 */
 	public String getProcedureName();
 
 	/**
 	 * Basic form for registering a positional parameter.
 	 *
 	 * @param position The position
 	 * @param type The Java type of the parameter
 	 * @param mode The parameter mode (in, out, inout)
+	 * @param <T> The parameterized Java type of the parameter.
 	 *
 	 * @return The parameter registration memento
 	 */
 	public <T> ParameterRegistration<T> registerParameter(int position, Class<T> type, ParameterMode mode);
 
 	/**
 	 * Chained form of {@link #registerParameter(int, Class, javax.persistence.ParameterMode)}
 	 *
 	 * @param position The position
 	 * @param type The Java type of the parameter
 	 * @param mode The parameter mode (in, out, inout)
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public ProcedureCall registerParameter0(int position, Class type, ParameterMode mode);
 
 	/**
 	 * Retrieve a previously registered parameter memento by the position under which it was registered.
 	 *
 	 * @param position The parameter position
 	 *
 	 * @return The parameter registration memento
 	 */
 	public ParameterRegistration getParameterRegistration(int position);
 
 	/**
 	 * Basic form for registering a named parameter.
 	 *
 	 * @param parameterName The parameter name
 	 * @param type The Java type of the parameter
 	 * @param mode The parameter mode (in, out, inout)
+	 * @param <T> The parameterized Java type of the parameter.
 	 *
 	 * @return The parameter registration memento
+	 *
+	 * @throws NamedParametersNotSupportedException When the underlying database is known to not support
+	 * named procedure parameters.
 	 */
 	public <T> ParameterRegistration<T> registerParameter(String parameterName, Class<T> type, ParameterMode mode)
 			throws NamedParametersNotSupportedException;
 
 	/**
 	 * Chained form of {@link #registerParameter(String, Class, javax.persistence.ParameterMode)}
 	 *
 	 * @param parameterName The parameter name
 	 * @param type The Java type of the parameter
 	 * @param mode The parameter mode (in, out, inout)
 	 *
 	 * @return The parameter registration memento
+	 *
+	 * @throws NamedParametersNotSupportedException When the underlying database is known to not support
+	 * named procedure parameters.
 	 */
 	public ProcedureCall registerParameter0(String parameterName, Class type, ParameterMode mode)
 			throws NamedParametersNotSupportedException;
 
 	/**
 	 * Retrieve a previously registered parameter memento by the name under which it was registered.
 	 *
 	 * @param name The parameter name
 	 *
 	 * @return The parameter registration memento
 	 */
 	public ParameterRegistration getParameterRegistration(String name);
 
 	/**
 	 * Retrieve all registered parameters.
 	 *
 	 * @return The (immutable) list of all registered parameters.
 	 */
 	public List<ParameterRegistration> getRegisteredParameters();
 
 	/**
 	 * Retrieves access to outputs of this procedure call.  Can be called multiple times, returning the same
 	 * Output instance each time.
 	 * <p/>
 	 * Note that the procedure will not actually be executed until the outputs are actually accessed.
 	 *
 	 * @return The outputs representation
 	 */
 	public ProcedureResult getResult();
 
+	/**
+	 * Extract the disconnected representation of this call.  Used in HEM to allow redefining a named query
+	 *
+	 * @param hints The hints to incorporate into the memento
+	 *
+	 * @return The memento
+	 */
+	public ProcedureCallMemento extractMemento(Map<String, Object> hints);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCallMemento.java b/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCallMemento.java
new file mode 100644
index 0000000000..45d582d8cf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/ProcedureCallMemento.java
@@ -0,0 +1,63 @@
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
+package org.hibernate.procedure;
+
+import java.util.Map;
+
+import org.hibernate.Session;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Represents a "memento" of a ProcedureCall
+ *
+ * @author Steve Ebersole
+ */
+public interface ProcedureCallMemento {
+	/**
+	 * Convert the memento back into an executable (connected) form.
+	 *
+	 * @param session The session to connect the procedure call to
+	 *
+	 * @return The executable call
+	 */
+	public ProcedureCall makeProcedureCall(Session session);
+
+	/**
+	 * Convert the memento back into an executable (connected) form.
+	 *
+	 * @param session The session to connect the procedure call to
+	 *
+	 * @return The executable call
+	 */
+	public ProcedureCall makeProcedureCall(SessionImplementor session);
+
+	/**
+	 * Access to any hints associated with the memento.
+	 * <p/>
+	 * IMPL NOTE : exposed separately because only HEM needs access to this.
+	 *
+	 * @return The hints.
+	 */
+	public Map<String, Object> getHintsMap();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/AbstractParameterRegistrationImpl.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/AbstractParameterRegistrationImpl.java
index 5af4950331..21081e5681 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/AbstractParameterRegistrationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/AbstractParameterRegistrationImpl.java
@@ -1,267 +1,317 @@
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
 package org.hibernate.procedure.internal;
 
 import javax.persistence.ParameterMode;
 import javax.persistence.TemporalType;
 
 import java.sql.CallableStatement;
 import java.sql.SQLException;
 import java.util.Calendar;
 import java.util.Date;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.procedure.ParameterBind;
 import org.hibernate.procedure.ParameterMisuseException;
 import org.hibernate.type.DateType;
 import org.hibernate.type.ProcedureParameterExtractionAware;
 import org.hibernate.type.Type;
 
 /**
+ * Abstract implementation of ParameterRegistration/ParameterRegistrationImplementor
+ *
  * @author Steve Ebersole
  */
 public abstract class AbstractParameterRegistrationImpl<T> implements ParameterRegistrationImplementor<T> {
 	private static final Logger log = Logger.getLogger( AbstractParameterRegistrationImpl.class );
 
 	private final ProcedureCallImpl procedureCall;
 
 	private final Integer position;
 	private final String name;
 
 	private final ParameterMode mode;
 	private final Class<T> type;
 
 	private ParameterBindImpl bind;
 
 	private int startIndex;
 	private Type hibernateType;
 	private int[] sqlTypes;
 
+
+	// positional constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	protected AbstractParameterRegistrationImpl(
+			ProcedureCallImpl procedureCall,
+			Integer position,
+			ParameterMode mode,
+			Class<T> type) {
+		this( procedureCall, position, null, mode, type );
+	}
+
 	protected AbstractParameterRegistrationImpl(
 			ProcedureCallImpl procedureCall,
 			Integer position,
+			ParameterMode mode,
 			Class<T> type,
-			ParameterMode mode) {
-		this( procedureCall, position, null, type, mode );
+			Type hibernateType) {
+		this( procedureCall, position, null, mode, type, hibernateType );
 	}
 
+
+	// named constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
 	protected AbstractParameterRegistrationImpl(
 			ProcedureCallImpl procedureCall,
 			String name,
+			ParameterMode mode,
+			Class<T> type) {
+		this( procedureCall, null, name, mode, type );
+	}
+
+	protected AbstractParameterRegistrationImpl(
+			ProcedureCallImpl procedureCall,
+			String name,
+			ParameterMode mode,
 			Class<T> type,
-			ParameterMode mode) {
-		this( procedureCall, null, name, type, mode );
+			Type hibernateType) {
+		this( procedureCall, null, name, mode, type, hibernateType );
 	}
 
 	private AbstractParameterRegistrationImpl(
 			ProcedureCallImpl procedureCall,
 			Integer position,
 			String name,
+			ParameterMode mode,
 			Class<T> type,
-			ParameterMode mode) {
+			Type hibernateType) {
 		this.procedureCall = procedureCall;
 
 		this.position = position;
 		this.name = name;
 
 		this.mode = mode;
 		this.type = type;
 
-		setHibernateType( session().getFactory().getTypeResolver().heuristicType( type.getName() ) );
+		setHibernateType( hibernateType );
+	}
+
+	private AbstractParameterRegistrationImpl(
+			ProcedureCallImpl procedureCall,
+			Integer position,
+			String name,
+			ParameterMode mode,
+			Class<T> type) {
+		this(
+				procedureCall,
+				position,
+				name,
+				mode,
+				type,
+				procedureCall.getSession().getFactory().getTypeResolver().heuristicType( type.getName() )
+		);
 	}
 
 	protected SessionImplementor session() {
 		return procedureCall.getSession();
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public Integer getPosition() {
 		return position;
 	}
 
 	@Override
 	public Class<T> getType() {
 		return type;
 	}
 
 	@Override
 	public ParameterMode getMode() {
 		return mode;
 	}
 
 	@Override
+	public Type getHibernateType() {
+		return hibernateType;
+	}
+
+	@Override
 	public void setHibernateType(Type type) {
 		if ( type == null ) {
 			throw new IllegalArgumentException( "Type cannot be null" );
 		}
 		this.hibernateType = type;
 		this.sqlTypes = hibernateType.sqlTypes( session().getFactory() );
 	}
 
 	@Override
+	@SuppressWarnings("unchecked")
 	public ParameterBind<T> getBind() {
 		return bind;
 	}
 
 	@Override
 	public void bindValue(T value) {
 		validateBindability();
 		this.bind = new ParameterBindImpl<T>( value );
 	}
 
 	private void validateBindability() {
 		if ( ! canBind() ) {
 			throw new ParameterMisuseException( "Cannot bind value to non-input parameter : " + this );
 		}
 	}
 
 	private boolean canBind() {
 		return mode == ParameterMode.IN || mode == ParameterMode.INOUT;
 	}
 
 	@Override
 	public void bindValue(T value, TemporalType explicitTemporalType) {
 		validateBindability();
 		if ( explicitTemporalType != null ) {
 			if ( ! isDateTimeType() ) {
 				throw new IllegalArgumentException( "TemporalType should not be specified for non date/time type" );
 			}
 		}
 		this.bind = new ParameterBindImpl<T>( value, explicitTemporalType );
 	}
 
 	private boolean isDateTimeType() {
 		return Date.class.isAssignableFrom( type )
 				|| Calendar.class.isAssignableFrom( type );
 	}
 
 	@Override
 	public void prepare(CallableStatement statement, int startIndex) throws SQLException {
 		if ( mode == ParameterMode.REF_CURSOR ) {
 			throw new NotYetImplementedException( "Support for REF_CURSOR parameters not yet supported" );
 		}
 
 		this.startIndex = startIndex;
 		if ( mode == ParameterMode.IN || mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
 			if ( mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
 				if ( sqlTypes.length > 1 ) {
-					if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType )
-							&& ( (ProcedureParameterExtractionAware) hibernateType ).canDoExtraction() ) {
-						// the type can handle multi-param extraction...
-					}
-					else {
+					// there is more than one column involved; see if the Hibernate Type can handle
+					// multi-param extraction...
+					final boolean canHandleMultiParamExtraction =
+							ProcedureParameterExtractionAware.class.isInstance( hibernateType )
+									&& ( (ProcedureParameterExtractionAware) hibernateType ).canDoExtraction();
+					if ( ! canHandleMultiParamExtraction ) {
 						// it cannot...
 						throw new UnsupportedOperationException(
 								"Type [" + hibernateType + "] does support multi-parameter value extraction"
 						);
 					}
 				}
 				for ( int i = 0; i < sqlTypes.length; i++ ) {
 					statement.registerOutParameter( startIndex + i, sqlTypes[i] );
 				}
 			}
 
 			if ( mode == ParameterMode.INOUT || mode == ParameterMode.IN ) {
 				if ( bind == null || bind.getValue() == null ) {
 					// the user did not bind a value to the parameter being processed.  That might be ok *if* the
 					// procedure as defined in the database defines a default value for that parameter.
 					// Unfortunately there is not a way to reliably know through JDBC metadata whether a procedure
 					// parameter defines a default value.  So we simply allow the procedure execution to happen
 					// assuming that the database will complain appropriately if not setting the given parameter
 					// bind value is an error.
 					log.debugf(
 							"Stored procedure [%s] IN/INOUT parameter [%s] not bound; assuming procedure defines default value",
 							procedureCall.getProcedureName(),
 							this
 					);
 				}
 				else {
 					final Type typeToUse;
 					if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.TIMESTAMP ) {
 						typeToUse = hibernateType;
 					}
 					else if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.DATE ) {
 						typeToUse = DateType.INSTANCE;
 					}
 					else {
 						typeToUse = hibernateType;
 					}
 					typeToUse.nullSafeSet( statement, bind.getValue(), startIndex, session() );
 				}
 			}
 		}
 		else {
 			// we have a REF_CURSOR type param
 			if ( procedureCall.getParameterStrategy() == ParameterStrategy.NAMED ) {
 				session().getFactory().getServiceRegistry()
 						.getService( RefCursorSupport.class )
 						.registerRefCursorParameter( statement, getName() );
 			}
 			else {
 				session().getFactory().getServiceRegistry()
 						.getService( RefCursorSupport.class )
 						.registerRefCursorParameter( statement, getPosition() );
 			}
 		}
 	}
 
 	public int[] getSqlTypes() {
 		return sqlTypes;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public T extract(CallableStatement statement) {
 		if ( mode == ParameterMode.IN ) {
 			throw new ParameterMisuseException( "IN parameter not valid for output extraction" );
 		}
 		else if ( mode == ParameterMode.REF_CURSOR ) {
 			throw new ParameterMisuseException( "REF_CURSOR parameters should be accessed via results" );
 		}
 
 		try {
 			if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType ) ) {
 				return (T) ( (ProcedureParameterExtractionAware) hibernateType ).extract( statement, startIndex, session() );
 			}
 			else {
 				return (T) statement.getObject( startIndex );
 			}
 		}
 		catch (SQLException e) {
 			throw procedureCall.getSession().getFactory().getSQLExceptionHelper().convert(
 					e,
 					"Unable to extract OUT/INOUT parameter value"
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/NamedParameterRegistration.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/NamedParameterRegistration.java
index d3a772cb70..6f9321426a 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/NamedParameterRegistration.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/NamedParameterRegistration.java
@@ -1,39 +1,52 @@
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
 package org.hibernate.procedure.internal;
 
 import javax.persistence.ParameterMode;
 
+import org.hibernate.type.Type;
+
 /**
+ * Represents a registered named parameter
+ *
  * @author Steve Ebersole
  */
 public class NamedParameterRegistration<T> extends AbstractParameterRegistrationImpl<T> {
-	public NamedParameterRegistration(
+	NamedParameterRegistration(
+			ProcedureCallImpl procedureCall,
+			String name,
+			ParameterMode mode,
+			Class<T> type) {
+		super( procedureCall, name, mode, type );
+	}
+
+	NamedParameterRegistration(
 			ProcedureCallImpl procedureCall,
 			String name,
+			ParameterMode mode,
 			Class<T> type,
-			ParameterMode mode) {
-		super( procedureCall, name, type, mode );
+			Type hibernateType) {
+		super( procedureCall, name, mode, type, hibernateType );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterRegistrationImplementor.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterRegistrationImplementor.java
index 0d1992d8a3..e349e6649e 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterRegistrationImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterRegistrationImplementor.java
@@ -1,41 +1,71 @@
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
 package org.hibernate.procedure.internal;
 
 import java.sql.CallableStatement;
 import java.sql.SQLException;
 
 import org.hibernate.procedure.ParameterRegistration;
+import org.hibernate.type.Type;
 
 /**
+ * Additional internal contract for ParameterRegistration
+ *
  * @author Steve Ebersole
  */
 public interface ParameterRegistrationImplementor<T> extends ParameterRegistration<T> {
+	/**
+	 * Prepare for execution.
+	 *
+	 * @param statement The statement about to be executed
+	 * @param i The parameter index for this registration (used for positional)
+	 *
+	 * @throws SQLException Indicates a problem accessing the statement object
+	 */
 	public void prepare(CallableStatement statement, int i) throws SQLException;
 
+	/**
+	 * Access to the Hibernate type for this parameter registration
+	 *
+	 * @return The Hibernate Type
+	 */
+	public Type getHibernateType();
+
+	/**
+	 * Access to the SQL type(s) for this parameter
+	 *
+	 * @return The SQL types (JDBC type codes)
+	 */
 	public int[] getSqlTypes();
 
+	/**
+	 * Extract value from the statement after execution (used for OUT/INOUT parameters).
+	 *
+	 * @param statement The callable statement
+	 *
+	 * @return The extracted value
+	 */
 	public T extract(CallableStatement statement);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterStrategy.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterStrategy.java
index bf517dc3f5..f329d46208 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ParameterStrategy.java
@@ -1,33 +1,42 @@
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
 package org.hibernate.procedure.internal;
 
 /**
  * The style/strategy of parameter registration used in a particular procedure call definition.
  */
 public enum ParameterStrategy {
+	/**
+	 * The parameters are named
+	 */
 	NAMED,
+	/**
+	 * The parameters are positional
+	 */
 	POSITIONAL,
+	/**
+	 * We do not (yet) know
+	 */
 	UNKNOWN
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/PositionalParameterRegistration.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/PositionalParameterRegistration.java
index a991525c58..64e63aab35 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/PositionalParameterRegistration.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/PositionalParameterRegistration.java
@@ -1,39 +1,52 @@
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
 package org.hibernate.procedure.internal;
 
 import javax.persistence.ParameterMode;
 
+import org.hibernate.type.Type;
+
 /**
+ * Represents a registered positional parameter
+ *
  * @author Steve Ebersole
  */
 public class PositionalParameterRegistration<T> extends AbstractParameterRegistrationImpl<T> {
-	public  PositionalParameterRegistration(
+	PositionalParameterRegistration(
+			ProcedureCallImpl procedureCall,
+			Integer position,
+			ParameterMode mode,
+			Class<T> type) {
+		super( procedureCall, position, mode, type );
+	}
+
+	PositionalParameterRegistration(
 			ProcedureCallImpl procedureCall,
 			Integer position,
+			ParameterMode mode,
 			Class<T> type,
-			ParameterMode mode) {
-		super( procedureCall, position, type, mode );
+			Type hibernateType) {
+		super( procedureCall, position, mode, type, hibernateType );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallImpl.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallImpl.java
index 0f82a10e6c..0c878894d2 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallImpl.java
@@ -1,394 +1,533 @@
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
 package org.hibernate.procedure.internal;
 
 import javax.persistence.ParameterMode;
 import java.sql.CallableStatement;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
+import java.util.Map;
 import java.util.Set;
 
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
-import org.hibernate.LockMode;
-import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.AbstractBasicQueryContractImpl;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.NamedParametersNotSupportedException;
 import org.hibernate.procedure.ParameterRegistration;
+import org.hibernate.procedure.ProcedureCall;
+import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.ProcedureResult;
 import org.hibernate.result.spi.ResultContext;
 import org.hibernate.type.Type;
 
 /**
  * Standard implementation of {@link org.hibernate.procedure.ProcedureCall}
  *
  * @author Steve Ebersole
  */
 public class ProcedureCallImpl extends AbstractBasicQueryContractImpl implements ProcedureCall, ResultContext {
+	private static final Logger log = Logger.getLogger( ProcedureCallImpl.class );
+
+	private static final NativeSQLQueryReturn[] NO_RETURNS = new NativeSQLQueryReturn[0];
+
 	private final String procedureName;
 	private final NativeSQLQueryReturn[] queryReturns;
 
 	private ParameterStrategy parameterStrategy = ParameterStrategy.UNKNOWN;
 	private List<ParameterRegistrationImplementor<?>> registeredParameters = new ArrayList<ParameterRegistrationImplementor<?>>();
 
 	private Set<String> synchronizedQuerySpaces;
 
 	private ProcedureResultImpl outputs;
 
-
-	@SuppressWarnings("unchecked")
+	/**
+	 * The no-returns form.
+	 *
+	 * @param session The session
+	 * @param procedureName The name of the procedure to call
+	 */
 	public ProcedureCallImpl(SessionImplementor session, String procedureName) {
-		this( session, procedureName, (List) null );
+		super( session );
+		this.procedureName = procedureName;
+		this.queryReturns = NO_RETURNS;
 	}
 
-	public ProcedureCallImpl(SessionImplementor session, String procedureName, List<NativeSQLQueryReturn> queryReturns) {
+	/**
+	 * The result Class(es) return form
+	 *
+	 * @param session The session
+	 * @param procedureName The name of the procedure to call
+	 * @param resultClasses The classes making up the result
+	 */
+	public ProcedureCallImpl(final SessionImplementor session, String procedureName, Class... resultClasses) {
 		super( session );
 		this.procedureName = procedureName;
 
-		if ( queryReturns == null || queryReturns.isEmpty() ) {
-			this.queryReturns = new NativeSQLQueryReturn[0];
-		}
-		else {
-			this.queryReturns = queryReturns.toArray( new NativeSQLQueryReturn[ queryReturns.size() ] );
-		}
-	}
-
-	public ProcedureCallImpl(SessionImplementor session, String procedureName, Class... resultClasses) {
-		this( session, procedureName, collectQueryReturns( resultClasses ) );
-	}
+		final List<NativeSQLQueryReturn> collectedQueryReturns = new ArrayList<NativeSQLQueryReturn>();
+		final Set<String> collectedQuerySpaces = new HashSet<String>();
+
+		Util.resolveResultClasses(
+				new Util.ResultClassesResolutionContext() {
+					@Override
+					public SessionFactoryImplementor getSessionFactory() {
+						return session.getFactory();
+					}
+
+					@Override
+					public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
+						Collections.addAll( collectedQueryReturns, queryReturns );
+					}
+
+					@Override
+					public void addQuerySpaces(String... spaces) {
+						Collections.addAll( collectedQuerySpaces, spaces );
+					}
+				},
+				resultClasses
+		);
+
+		this.queryReturns = collectedQueryReturns.toArray( new NativeSQLQueryReturn[ collectedQueryReturns.size() ] );
+		this.synchronizedQuerySpaces = collectedQuerySpaces;
+	}
+
+	/**
+	 * The result-set-mapping(s) return form
+	 *
+	 * @param session The session
+	 * @param procedureName The name of the procedure to call
+	 * @param resultSetMappings The names of the result set mappings making up the result
+	 */
+	public ProcedureCallImpl(final SessionImplementor session, String procedureName, String... resultSetMappings) {
+		super( session );
+		this.procedureName = procedureName;
 
-	private static List<NativeSQLQueryReturn> collectQueryReturns(Class[] resultClasses) {
-		if ( resultClasses == null || resultClasses.length == 0 ) {
-			return null;
+		final List<NativeSQLQueryReturn> collectedQueryReturns = new ArrayList<NativeSQLQueryReturn>();
+		final Set<String> collectedQuerySpaces = new HashSet<String>();
+
+		Util.resolveResultSetMappings(
+				new Util.ResultSetMappingResolutionContext() {
+					@Override
+					public SessionFactoryImplementor getSessionFactory() {
+						return session.getFactory();
+					}
+
+					@Override
+					public ResultSetMappingDefinition findResultSetMapping(String name) {
+						return session.getFactory().getResultSetMapping( name );
+					}
+
+					@Override
+					public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
+						Collections.addAll( collectedQueryReturns, queryReturns );
+					}
+
+					@Override
+					public void addQuerySpaces(String... spaces) {
+						Collections.addAll( collectedQuerySpaces, spaces );
+					}
+				},
+				resultSetMappings
+		);
+
+		this.queryReturns = collectedQueryReturns.toArray( new NativeSQLQueryReturn[ collectedQueryReturns.size() ] );
+		this.synchronizedQuerySpaces = collectedQuerySpaces;
+	}
+
+	/**
+	 * The named/stored copy constructor
+	 *
+	 * @param session The session
+	 * @param memento The named/stored memento
+	 */
+	@SuppressWarnings("unchecked")
+	ProcedureCallImpl(SessionImplementor session, ProcedureCallMementoImpl memento) {
+		super( session );
+		this.procedureName = memento.getProcedureName();
+
+		this.queryReturns = memento.getQueryReturns();
+		this.synchronizedQuerySpaces = Util.copy( memento.getSynchronizedQuerySpaces() );
+		this.parameterStrategy = memento.getParameterStrategy();
+		if ( parameterStrategy == ParameterStrategy.UNKNOWN ) {
+			// nothing else to do in this case
+			return;
 		}
 
-		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultClasses.length );
-		int i = 1;
-		for ( Class resultClass : resultClasses ) {
-			queryReturns.add( new NativeSQLQueryRootReturn( "alias" + i, resultClass.getName(), LockMode.READ ) );
-			i++;
+		final List<ProcedureCallMementoImpl.ParameterMemento> storedRegistrations = memento.getParameterDeclarations();
+		if ( storedRegistrations == null ) {
+			// most likely a problem if ParameterStrategy is not UNKNOWN...
+			log.debugf(
+					"ParameterStrategy was [%s] on named copy [%s], but no parameters stored",
+					parameterStrategy,
+					procedureName
+			);
+			return;
 		}
-		return queryReturns;
-	}
 
-	public ProcedureCallImpl(SessionImplementor session, String procedureName, String... resultSetMappings) {
-		this( session, procedureName, collectQueryReturns( session, resultSetMappings ) );
-	}
-
-	private static List<NativeSQLQueryReturn> collectQueryReturns(SessionImplementor session, String[] resultSetMappings) {
-		if ( resultSetMappings == null || resultSetMappings.length == 0 ) {
-			return null;
-		}
+		final List<ParameterRegistrationImplementor<?>> parameterRegistrations =
+				CollectionHelper.arrayList( storedRegistrations.size() );
 
-		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultSetMappings.length );
-		for ( String resultSetMapping : resultSetMappings ) {
-			ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( resultSetMapping );
-			if ( mapping == null ) {
-				throw new MappingException( "Unknown SqlResultSetMapping [" + resultSetMapping + "]" );
+		for ( ProcedureCallMementoImpl.ParameterMemento storedRegistration : storedRegistrations ) {
+			final ParameterRegistrationImplementor<?> registration;
+			if ( StringHelper.isNotEmpty( storedRegistration.getName() ) ) {
+				if ( parameterStrategy != ParameterStrategy.NAMED ) {
+					throw new IllegalStateException(
+							"Found named stored procedure parameter associated with positional parameters"
+					);
+				}
+				registration = new NamedParameterRegistration(
+						this,
+						storedRegistration.getName(),
+						storedRegistration.getMode(),
+						storedRegistration.getType(),
+						storedRegistration.getHibernateType()
+				);
 			}
-			queryReturns.addAll( Arrays.asList( mapping.getQueryReturns() ) );
+			else {
+				if ( parameterStrategy != ParameterStrategy.POSITIONAL ) {
+					throw new IllegalStateException(
+							"Found named stored procedure parameter associated with positional parameters"
+					);
+				}
+				registration = new PositionalParameterRegistration(
+						this,
+						storedRegistration.getPosition(),
+						storedRegistration.getMode(),
+						storedRegistration.getType(),
+						storedRegistration.getHibernateType()
+				);
+			}
+			parameterRegistrations.add( registration );
 		}
-		return queryReturns;
+		this.registeredParameters = parameterRegistrations;
 	}
 
-//	public ProcedureCallImpl(
-//			SessionImplementor session,
-//			String procedureName,
-//			List<StoredProcedureParameter> parameters) {
-//		// this form is intended for named stored procedure calls.
-//		// todo : introduce a NamedProcedureCallDefinition object to hold all needed info and pass that in here; will help with EM.addNamedQuery as well..
-//		this( session, procedureName );
-//		for ( StoredProcedureParameter parameter : parameters ) {
-//			registerParameter( (StoredProcedureParameterImplementor) parameter );
-//		}
-//	}
-
 	@Override
 	public SessionImplementor getSession() {
 		return super.session();
 	}
 
 	public ParameterStrategy getParameterStrategy() {
 		return parameterStrategy;
 	}
 
 	@Override
 	public String getProcedureName() {
 		return procedureName;
 	}
 
 	@Override
 	public String getSql() {
 		return getProcedureName();
 	}
 
 	@Override
 	public NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> ParameterRegistration<T> registerParameter(int position, Class<T> type, ParameterMode mode) {
-		final PositionalParameterRegistration parameterRegistration = new PositionalParameterRegistration( this, position, type, mode );
+		final PositionalParameterRegistration parameterRegistration =
+				new PositionalParameterRegistration( this, position, mode, type );
 		registerParameter( parameterRegistration );
 		return parameterRegistration;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ProcedureCall registerParameter0(int position, Class type, ParameterMode mode) {
 		registerParameter( position, type, mode );
 		return this;
 	}
 
 	private void registerParameter(ParameterRegistrationImplementor parameter) {
 		if ( StringHelper.isNotEmpty( parameter.getName() ) ) {
 			prepareForNamedParameters();
 		}
 		else if ( parameter.getPosition() != null ) {
 			prepareForPositionalParameters();
 		}
 		else {
 			throw new IllegalArgumentException( "Given parameter did not define name or position [" + parameter + "]" );
 		}
 		registeredParameters.add( parameter );
 	}
 
 	private void prepareForPositionalParameters() {
 		if ( parameterStrategy == ParameterStrategy.NAMED ) {
 			throw new QueryException( "Cannot mix named and positional parameters" );
 		}
 		parameterStrategy = ParameterStrategy.POSITIONAL;
 	}
 
 	private void prepareForNamedParameters() {
 		if ( parameterStrategy == ParameterStrategy.POSITIONAL ) {
 			throw new QueryException( "Cannot mix named and positional parameters" );
 		}
 		if ( parameterStrategy == null ) {
 			// protect to only do this check once
 			final ExtractedDatabaseMetaData databaseMetaData = getSession().getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getLogicalConnection()
 					.getJdbcServices()
 					.getExtractedMetaDataSupport();
 			if ( ! databaseMetaData.supportsNamedParameters() ) {
 				throw new NamedParametersNotSupportedException(
 						"Named stored procedure parameters used, but JDBC driver does not support named parameters"
 				);
 			}
 			parameterStrategy = ParameterStrategy.NAMED;
 		}
 	}
 
 	@Override
 	public ParameterRegistrationImplementor getParameterRegistration(int position) {
 		if ( parameterStrategy != ParameterStrategy.POSITIONAL ) {
 			throw new IllegalArgumentException( "Positions were not used to register parameters with this stored procedure call" );
 		}
 		try {
 			return registeredParameters.get( position );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "Could not locate parameter registered using that position [" + position + "]" );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> ParameterRegistration<T> registerParameter(String name, Class<T> type, ParameterMode mode) {
-		final NamedParameterRegistration parameterRegistration = new NamedParameterRegistration( this, name, type, mode );
+		final NamedParameterRegistration parameterRegistration
+				= new NamedParameterRegistration( this, name, mode, type );
 		registerParameter( parameterRegistration );
 		return parameterRegistration;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ProcedureCall registerParameter0(String name, Class type, ParameterMode mode) {
 		registerParameter( name, type, mode );
 		return this;
 	}
 
 	@Override
 	public ParameterRegistrationImplementor getParameterRegistration(String name) {
 		if ( parameterStrategy != ParameterStrategy.NAMED ) {
 			throw new IllegalArgumentException( "Names were not used to register parameters with this stored procedure call" );
 		}
 		for ( ParameterRegistrationImplementor parameter : registeredParameters ) {
 			if ( name.equals( parameter.getName() ) ) {
 				return parameter;
 			}
 		}
 		throw new IllegalArgumentException( "Could not locate parameter registered under that name [" + name + "]" );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public List<ParameterRegistration> getRegisteredParameters() {
 		return new ArrayList<ParameterRegistration>( registeredParameters );
 	}
 
 	@Override
 	public ProcedureResult getResult() {
 		if ( outputs == null ) {
 			outputs = buildOutputs();
 		}
 
 		return outputs;
 	}
 
 	private ProcedureResultImpl buildOutputs() {
 		// todo : going to need a very specialized Loader for this.
 		// or, might be a good time to look at splitting Loader up into:
 		//		1) building statement objects
 		//		2) executing statement objects
 		//		3) processing result sets
 
 		// for now assume there are no resultClasses nor mappings defined..
 		// 	TOTAL PROOF-OF-CONCEPT!!!!!!
 
 		final StringBuilder buffer = new StringBuilder().append( "{call " )
 				.append( procedureName )
 				.append( "(" );
 		String sep = "";
 		for ( ParameterRegistrationImplementor parameter : registeredParameters ) {
 			for ( int i = 0; i < parameter.getSqlTypes().length; i++ ) {
 				buffer.append( sep ).append( "?" );
 				sep = ",";
 			}
 		}
 		buffer.append( ")}" );
 
 		try {
 			final CallableStatement statement = (CallableStatement) getSession().getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( buffer.toString(), true );
 
 			// prepare parameters
 			int i = 1;
 			for ( ParameterRegistrationImplementor parameter : registeredParameters ) {
 				if ( parameter == null ) {
 					throw new QueryException( "Registered stored procedure parameters had gaps" );
 				}
 
 				parameter.prepare( statement, i );
 				i += parameter.getSqlTypes().length;
 			}
 
 			return new ProcedureResultImpl( this, statement );
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					e,
 					"Error preparing CallableStatement",
 					getProcedureName()
 			);
 		}
 	}
 
 
 	@Override
 	public Type[] getReturnTypes() throws HibernateException {
 		throw new NotYetImplementedException();
 	}
 
+	/**
+	 * Use this form instead of {@link #getSynchronizedQuerySpaces()} when you want to make sure the
+	 * underlying Set is instantiated (aka, on add)
+	 *
+	 * @return The spaces
+	 */
 	protected Set<String> synchronizedQuerySpaces() {
 		if ( synchronizedQuerySpaces == null ) {
 			synchronizedQuerySpaces = new HashSet<String>();
 		}
 		return synchronizedQuerySpaces;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Set<String> getSynchronizedQuerySpaces() {
 		if ( synchronizedQuerySpaces == null ) {
 			return Collections.emptySet();
 		}
 		else {
 			return Collections.unmodifiableSet( synchronizedQuerySpaces );
 		}
 	}
 
 	@Override
 	public ProcedureCallImpl addSynchronizedQuerySpace(String querySpace) {
 		synchronizedQuerySpaces().add( querySpace );
 		return this;
 	}
 
 	@Override
 	public ProcedureCallImpl addSynchronizedEntityName(String entityName) {
 		addSynchronizedQuerySpaces( getSession().getFactory().getEntityPersister( entityName ) );
 		return this;
 	}
 
 	protected void addSynchronizedQuerySpaces(EntityPersister persister) {
 		synchronizedQuerySpaces().addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
 	}
 
 	@Override
 	public ProcedureCallImpl addSynchronizedEntityClass(Class entityClass) {
 		addSynchronizedQuerySpaces( getSession().getFactory().getEntityPersister( entityClass.getName() ) );
 		return this;
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		return buildQueryParametersObject();
 	}
 
+	@Override
 	public QueryParameters buildQueryParametersObject() {
 		QueryParameters qp = super.buildQueryParametersObject();
 		// both of these are for documentation purposes, they are actually handled directly...
 		qp.setAutoDiscoverScalarTypes( true );
 		qp.setCallable( true );
 		return qp;
 	}
 
+	/**
+	 * Collects any parameter registrations which indicate a REF_CURSOR parameter type/mode.
+	 *
+	 * @return The collected REF_CURSOR type parameters.
+	 */
 	public ParameterRegistrationImplementor[] collectRefCursorParameters() {
-		List<ParameterRegistrationImplementor> refCursorParams = new ArrayList<ParameterRegistrationImplementor>();
+		final List<ParameterRegistrationImplementor> refCursorParams = new ArrayList<ParameterRegistrationImplementor>();
 		for ( ParameterRegistrationImplementor param : registeredParameters ) {
 			if ( param.getMode() == ParameterMode.REF_CURSOR ) {
 				refCursorParams.add( param );
 			}
 		}
 		return refCursorParams.toArray( new ParameterRegistrationImplementor[refCursorParams.size()] );
 	}
+
+	@Override
+	public ProcedureCallMemento extractMemento(Map<String, Object> hints) {
+		return new ProcedureCallMementoImpl(
+				procedureName,
+				Util.copy( queryReturns ),
+				parameterStrategy,
+				toParameterMementos( registeredParameters ),
+				Util.copy( synchronizedQuerySpaces ),
+				Util.copy( hints )
+		);
+	}
+
+
+	private static List<ProcedureCallMementoImpl.ParameterMemento> toParameterMementos(List<ParameterRegistrationImplementor<?>> registeredParameters) {
+		if ( registeredParameters == null ) {
+			return null;
+		}
+
+		final List<ProcedureCallMementoImpl.ParameterMemento> copy = CollectionHelper.arrayList( registeredParameters.size() );
+		for ( ParameterRegistrationImplementor registration : registeredParameters ) {
+			copy.add( ProcedureCallMementoImpl.ParameterMemento.fromRegistration( registration ) );
+		}
+		return copy;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java
new file mode 100644
index 0000000000..7af100283e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java
@@ -0,0 +1,169 @@
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
+package org.hibernate.procedure.internal;
+
+import javax.persistence.ParameterMode;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.Session;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.procedure.ProcedureCall;
+import org.hibernate.procedure.ProcedureCallMemento;
+import org.hibernate.type.Type;
+
+/**
+ * Implementation of ProcedureCallMemento
+ *
+ * @author Steve Ebersole
+ */
+public class ProcedureCallMementoImpl implements ProcedureCallMemento {
+	private final String procedureName;
+	private final NativeSQLQueryReturn[] queryReturns;
+
+	private final ParameterStrategy parameterStrategy;
+	private final List<ParameterMemento> parameterDeclarations;
+
+	private final Set<String> synchronizedQuerySpaces;
+
+	private final Map<String,Object> hintsMap;
+
+	public ProcedureCallMementoImpl(
+			String procedureName,
+			NativeSQLQueryReturn[] queryReturns,
+			ParameterStrategy parameterStrategy,
+			List<ParameterMemento> parameterDeclarations,
+			Set<String> synchronizedQuerySpaces,
+			Map<String,Object> hintsMap) {
+		this.procedureName = procedureName;
+		this.queryReturns = queryReturns;
+		this.parameterStrategy = parameterStrategy;
+		this.parameterDeclarations = parameterDeclarations;
+		this.synchronizedQuerySpaces = synchronizedQuerySpaces;
+		this.hintsMap = hintsMap;
+	}
+
+	@Override
+	public ProcedureCall makeProcedureCall(Session session) {
+		return new ProcedureCallImpl( (SessionImplementor) session, this );
+	}
+
+	@Override
+	public ProcedureCall makeProcedureCall(SessionImplementor session) {
+		return new ProcedureCallImpl( session, this );
+	}
+
+	public String getProcedureName() {
+		return procedureName;
+	}
+
+	public NativeSQLQueryReturn[] getQueryReturns() {
+		return queryReturns;
+	}
+
+	public ParameterStrategy getParameterStrategy() {
+		return parameterStrategy;
+	}
+
+	public List<ParameterMemento> getParameterDeclarations() {
+		return parameterDeclarations;
+	}
+
+	public Set<String> getSynchronizedQuerySpaces() {
+		return synchronizedQuerySpaces;
+	}
+
+	@Override
+	public Map<String, Object> getHintsMap() {
+		return hintsMap;
+	}
+
+	/**
+	 * A "disconnected" copy of the metadata for a parameter, that can be used in ProcedureCallMementoImpl.
+	 */
+	public static class ParameterMemento {
+		private final Integer position;
+		private final String name;
+		private final ParameterMode mode;
+		private final Class type;
+		private final Type hibernateType;
+
+		/**
+		 * Create the memento
+		 *
+		 * @param position The parameter position
+		 * @param name The parameter name
+		 * @param mode The parameter mode
+		 * @param type The Java type of the parameter
+		 * @param hibernateType The Hibernate Type.
+		 */
+		public ParameterMemento(int position, String name, ParameterMode mode, Class type, Type hibernateType) {
+			this.position = position;
+			this.name = name;
+			this.mode = mode;
+			this.type = type;
+			this.hibernateType = hibernateType;
+		}
+
+		public Integer getPosition() {
+			return position;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public ParameterMode getMode() {
+			return mode;
+		}
+
+		public Class getType() {
+			return type;
+		}
+
+		public Type getHibernateType() {
+			return hibernateType;
+		}
+
+		/**
+		 * Build a ParameterMemento from the given parameter registration
+		 *
+		 * @param registration The parameter registration from a ProcedureCall
+		 *
+		 * @return The memento
+		 */
+		public static ParameterMemento fromRegistration(ParameterRegistrationImplementor registration) {
+			return new ParameterMemento(
+					registration.getPosition(),
+					registration.getName(),
+					registration.getMode(),
+					registration.getType(),
+					registration.getHibernateType()
+			);
+		}
+
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/Util.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/Util.java
new file mode 100644
index 0000000000..36e6ffe66f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/Util.java
@@ -0,0 +1,116 @@
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
+package org.hibernate.procedure.internal;
+
+import java.util.Collections;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.LockMode;
+import org.hibernate.MappingException;
+import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class Util {
+	private Util() {
+	}
+
+	/**
+	 * Makes a copy of the given query return array.
+	 *
+	 * @param queryReturns The returns to copy
+	 *
+	 * @return The copy
+	 */
+	static NativeSQLQueryReturn[] copy(NativeSQLQueryReturn[] queryReturns) {
+		if ( queryReturns == null ) {
+			return new NativeSQLQueryReturn[0];
+		}
+
+		final NativeSQLQueryReturn[] copy = new NativeSQLQueryReturn[ queryReturns.length ];
+		System.arraycopy( queryReturns, 0, copy, 0, queryReturns.length );
+		return copy;
+	}
+
+	public static Set<String> copy(Set<String> synchronizedQuerySpaces) {
+		return CollectionHelper.makeCopy( synchronizedQuerySpaces );
+	}
+
+	public static Map<String,Object> copy(Map<String, Object> hints) {
+		return CollectionHelper.makeCopy( hints );
+	}
+
+	public static interface ResultSetMappingResolutionContext {
+		public SessionFactoryImplementor getSessionFactory();
+		public ResultSetMappingDefinition findResultSetMapping(String name);
+		public void addQueryReturns(NativeSQLQueryReturn... queryReturns);
+		public void addQuerySpaces(String... spaces);
+	}
+
+	public static void resolveResultSetMappings(ResultSetMappingResolutionContext context, String... resultSetMappingNames) {
+		for ( String resultSetMappingName : resultSetMappingNames ) {
+			final ResultSetMappingDefinition mapping = context.findResultSetMapping( resultSetMappingName );
+			if ( mapping == null ) {
+				throw new MappingException( "Unknown SqlResultSetMapping [" + resultSetMappingName + "]" );
+			}
+
+			context.addQueryReturns( mapping.getQueryReturns() );
+
+			final SQLQueryReturnProcessor processor =
+					new SQLQueryReturnProcessor( mapping.getQueryReturns(), context.getSessionFactory() );
+			final SQLQueryReturnProcessor.ResultAliasContext processResult = processor.process();
+			context.addQuerySpaces( processResult.collectQuerySpaces() );
+		}
+	}
+
+	public static interface ResultClassesResolutionContext {
+		public SessionFactoryImplementor getSessionFactory();
+		public void addQueryReturns(NativeSQLQueryReturn... queryReturns);
+		public void addQuerySpaces(String... spaces);
+	}
+
+	public static void resolveResultClasses(ResultClassesResolutionContext context, Class... resultClasses) {
+		int i = 1;
+		for ( Class resultClass : resultClasses ) {
+			context.addQueryReturns(
+					new NativeSQLQueryRootReturn( "alias" + i, resultClass.getName(), LockMode.READ )
+			);
+			try {
+				final EntityPersister persister = context.getSessionFactory().getEntityPersister( resultClass.getName() );
+				context.addQuerySpaces( (String[]) persister.getQuerySpaces() );
+			}
+			catch (Exception ignore) {
+
+			}
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
index a94c3228aa..07da96f48b 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
@@ -1,513 +1,520 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal;
 
 import javax.persistence.Cache;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.Query;
 import javax.persistence.SynchronizationType;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.metamodel.EntityType;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Hibernate;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.ejb.HibernateEntityManagerFactory;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateQuery;
 import org.hibernate.jpa.boot.internal.SettingsImpl;
 import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
 import org.hibernate.jpa.graph.internal.EntityGraphImpl;
 import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
 import org.hibernate.jpa.internal.metamodel.MetamodelImpl;
 import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
+import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Actual Hibernate implementation of {@link javax.persistence.EntityManagerFactory}.
  *
  * @author Gavin King
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryImpl implements HibernateEntityManagerFactory {
 	private static final long serialVersionUID = 5423543L;
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private static final Logger log = Logger.getLogger( EntityManagerFactoryImpl.class );
 
 	private final transient SessionFactoryImpl sessionFactory;
 	private final transient PersistenceUnitTransactionType transactionType;
 	private final transient boolean discardOnClose;
 	private final transient Class sessionInterceptorClass;
 	private final transient CriteriaBuilderImpl criteriaBuilder;
 	private final transient MetamodelImpl metamodel;
 	private final transient HibernatePersistenceUnitUtil util;
 	private final transient Map<String,Object> properties;
 	private final String entityManagerFactoryName;
 
 	private final transient PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 	private final transient Map<String,EntityGraphImpl> entityGraphs = new ConcurrentHashMap<String, EntityGraphImpl>();
 
 	@SuppressWarnings( "unchecked" )
 	public EntityManagerFactoryImpl(
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class sessionInterceptorClass,
 			Configuration cfg,
 			ServiceRegistry serviceRegistry,
 			String persistenceUnitName) {
 		this(
 				persistenceUnitName,
 				(SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry ),
 				new SettingsImpl().setReleaseResourcesOnCloseEnabled( discardOnClose ).setSessionInterceptorClass( sessionInterceptorClass ).setTransactionType( transactionType ),
 				cfg.getProperties(),
 				cfg
 		);
 	}
 
 	public EntityManagerFactoryImpl(
 			String persistenceUnitName,
 			SessionFactoryImplementor sessionFactory,
 			SettingsImpl settings,
 			Map<?, ?> configurationValues,
 			Configuration cfg) {
 		this.sessionFactory = (SessionFactoryImpl) sessionFactory;
 		this.transactionType = settings.getTransactionType();
 		this.discardOnClose = settings.isReleaseResourcesOnCloseEnabled();
 		this.sessionInterceptorClass = settings.getSessionInterceptorClass();
 
 		final Iterator<PersistentClass> classes = cfg.getClassMappings();
 		final JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting = determineJpaMetaModelPopulationSetting( cfg );
 		if ( JpaMetaModelPopulationSetting.DISABLED == jpaMetaModelPopulationSetting ) {
 			this.metamodel = null;
 		}
 		else {
 			this.metamodel = MetamodelImpl.buildMetamodel(
 					classes,
 					sessionFactory,
 					JpaMetaModelPopulationSetting.IGNORE_UNSUPPORTED == jpaMetaModelPopulationSetting
 			);
 		}
 		this.criteriaBuilder = new CriteriaBuilderImpl( this );
 		this.util = new HibernatePersistenceUnitUtil( this );
 
 		HashMap<String,Object> props = new HashMap<String, Object>();
 		addAll( props, sessionFactory.getProperties() );
 		addAll( props, cfg.getProperties() );
 		addAll( props, configurationValues );
 		maskOutSensitiveInformation( props );
 		this.properties = Collections.unmodifiableMap( props );
 		String entityManagerFactoryName = (String)this.properties.get( AvailableSettings.ENTITY_MANAGER_FACTORY_NAME);
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = persistenceUnitName;
 		}
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = (String) UUID_GENERATOR.generate(null, null);
 		}
 		this.entityManagerFactoryName = entityManagerFactoryName;
 		EntityManagerFactoryRegistry.INSTANCE.addEntityManagerFactory(entityManagerFactoryName, this);
 	}
 
 	private enum JpaMetaModelPopulationSetting {
 		ENABLED,
 		DISABLED,
 		IGNORE_UNSUPPORTED;
 		
 		private static JpaMetaModelPopulationSetting parse(String setting) {
 			if ( "enabled".equalsIgnoreCase( setting ) ) {
 				return ENABLED;
 			}
 			else if ( "disabled".equalsIgnoreCase( setting ) ) {
 				return DISABLED;
 			}
 			else {
 				return IGNORE_UNSUPPORTED;
 			}
 		}
 	}
 	
 	protected JpaMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Configuration cfg) {
 		String setting = ConfigurationHelper.getString(
 				AvailableSettings.JPA_METAMODEL_POPULATION,
 				cfg.getProperties(),
 				null
 		);
 		if ( setting == null ) {
 			setting = ConfigurationHelper.getString( AvailableSettings.JPA_METAMODEL_GENERATION, cfg.getProperties(), null );
 			if ( setting != null ) {
 				log.infof( 
 						"Encountered deprecated setting [%s], use [%s] instead",
 						AvailableSettings.JPA_METAMODEL_GENERATION,
 						AvailableSettings.JPA_METAMODEL_POPULATION
 				);
 			}
 		}
 		return JpaMetaModelPopulationSetting.parse( setting );
 	}
 
 	private static void addAll(HashMap<String, Object> destination, Map<?,?> source) {
 		for ( Map.Entry entry : source.entrySet() ) {
 			if ( String.class.isInstance( entry.getKey() ) ) {
 				destination.put( (String) entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private void maskOutSensitiveInformation(HashMap<String, Object> props) {
 		maskOutIfSet( props, AvailableSettings.JDBC_PASSWORD );
 		maskOutIfSet( props, org.hibernate.cfg.AvailableSettings.PASS );
 	}
 
 	private void maskOutIfSet(HashMap<String, Object> props, String setting) {
 		if ( props.containsKey( setting ) ) {
 			props.put( setting, "****" );
 		}
 	}
 
 	public EntityManager createEntityManager() {
 		return createEntityManager( Collections.EMPTY_MAP );
 	}
 
 	@Override
 	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
 		return createEntityManager( synchronizationType, Collections.EMPTY_MAP );
 	}
 
 	public EntityManager createEntityManager(Map map) {
 		return createEntityManager( SynchronizationType.SYNCHRONIZED, map );
 	}
 
 	@Override
 	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
 		//TODO support discardOnClose, persistencecontexttype?, interceptor,
 		return new EntityManagerImpl(
 				this,
 				PersistenceContextType.EXTENDED,
 				synchronizationType,
 				transactionType,
 				discardOnClose,
 				sessionInterceptorClass,
 				map
 		);
 	}
 
 	public CriteriaBuilder getCriteriaBuilder() {
 		return criteriaBuilder;
 	}
 
 	public Metamodel getMetamodel() {
 		return metamodel;
 	}
 
 	public void close() {
 		sessionFactory.close();
 		EntityManagerFactoryRegistry.INSTANCE.removeEntityManagerFactory(entityManagerFactoryName, this);
 	}
 
 	public Map<String, Object> getProperties() {
 		return properties;
 	}
 
 	public Cache getCache() {
 		// TODO : cache the cache reference?
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return new JPACache( sessionFactory );
 	}
 
 	public PersistenceUnitUtil getPersistenceUnitUtil() {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return util;
 	}
 
 	@Override
 	public void addNamedQuery(String name, Query query) {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException( "EntityManagerFactory is closed" );
 		}
 
-		if ( ! HibernateQuery.class.isInstance( query ) ) {
-			throw new PersistenceException( "Cannot use query non-Hibernate EntityManager query as basis for named query" );
+		if ( StoredProcedureQueryImpl.class.isInstance( query ) ) {
+			final ProcedureCall procedureCall = ( (StoredProcedureQueryImpl) query ).getHibernateProcedureCall();
+			sessionFactory.getNamedQueryRepository().registerNamedProcedureCallMemento( name, procedureCall.extractMemento( query.getHints() ) );
 		}
-
-		// create and register the proper NamedQueryDefinition...
-		final org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
-		if ( org.hibernate.SQLQuery.class.isInstance( hibernateQuery ) ) {
-			final NamedSQLQueryDefinition namedQueryDefinition = extractSqlQueryDefinition( ( org.hibernate.SQLQuery ) hibernateQuery, name );
-			sessionFactory.registerNamedSQLQueryDefinition( name, namedQueryDefinition );
+		else if ( ! HibernateQuery.class.isInstance( query ) ) {
+			throw new PersistenceException( "Cannot use query non-Hibernate EntityManager query as basis for named query" );
 		}
 		else {
-			final NamedQueryDefinition namedQueryDefinition = extractHqlQueryDefinition( hibernateQuery, name );
-			sessionFactory.registerNamedQueryDefinition( name, namedQueryDefinition );
+			// create and register the proper NamedQueryDefinition...
+			final org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+			if ( org.hibernate.SQLQuery.class.isInstance( hibernateQuery ) ) {
+				sessionFactory.registerNamedSQLQueryDefinition(
+						name,
+						extractSqlQueryDefinition( (org.hibernate.SQLQuery) hibernateQuery, name )
+				);
+			}
+			else {
+				sessionFactory.registerNamedQueryDefinition( name, extractHqlQueryDefinition( hibernateQuery, name ) );
+			}
 		}
 	}
 
 	private NamedSQLQueryDefinition extractSqlQueryDefinition(org.hibernate.SQLQuery nativeSqlQuery, String name) {
 		final NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, nativeSqlQuery );
 		builder.setCallable( nativeSqlQuery.isCallable() )
 				.setQuerySpaces( nativeSqlQuery.getSynchronizedQuerySpaces() )
 				.setQueryReturns( nativeSqlQuery.getQueryReturns() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private NamedQueryDefinition extractHqlQueryDefinition(org.hibernate.Query hqlQuery, String name) {
 		final NamedQueryDefinitionBuilder builder = new NamedQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, hqlQuery );
 		// LockOptions only valid for HQL/JPQL queries...
 		builder.setLockOptions( hqlQuery.getLockOptions().makeCopy() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private void fillInNamedQueryBuilder(NamedQueryDefinitionBuilder builder, org.hibernate.Query query) {
 		builder.setQuery( query.getQueryString() )
 				.setComment( query.getComment() )
 				.setCacheable( query.isCacheable() )
 				.setCacheRegion( query.getCacheRegion() )
 				.setCacheMode( query.getCacheMode() )
 				.setTimeout( query.getTimeout() )
 				.setFetchSize( query.getFetchSize() )
 				.setFirstResult( query.getFirstResult() )
 				.setMaxResults( query.getMaxResults() )
 				.setReadOnly( query.isReadOnly() )
 				.setFlushMode( query.getFlushMode() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T unwrap(Class<T> cls) {
 		if ( SessionFactory.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( SessionFactoryImplementor.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( EntityManager.class.isAssignableFrom( cls ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap EntityManagerFactory as " + cls.getName() );
 	}
 
 	@Override
 	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
 		if ( ! EntityGraphImpl.class.isInstance( entityGraph ) ) {
 			throw new IllegalArgumentException(
 					"Unknown type of EntityGraph for making named : " + entityGraph.getClass().getName()
 			);
 		}
 		final EntityGraphImpl<T> copy = ( (EntityGraphImpl<T>) entityGraph ).makeImmutableCopy( graphName );
 		final EntityGraphImpl old = entityGraphs.put( graphName, copy );
 		if ( old != null ) {
 			log.debugf( "EntityGraph being replaced on EntityManagerFactory for name %s", graphName );
 		}
 	}
 
 	public EntityGraphImpl findEntityGraphByName(String name) {
 		return entityGraphs.get( name );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
 		final EntityType<T> entityType = getMetamodel().entity( entityClass );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "Given class is not an entity : " + entityClass.getName() );
 		}
 
 		final List<EntityGraph<? super T>> results = new ArrayList<EntityGraph<? super T>>();
 		for ( EntityGraphImpl entityGraph : this.entityGraphs.values() ) {
 			if ( entityGraph.appliesTo( entityType ) ) {
 				results.add( entityGraph );
 			}
 		}
 		return results;
 	}
 
 	public boolean isOpen() {
 		return ! sessionFactory.isClosed();
 	}
 
 	public SessionFactoryImpl getSessionFactory() {
 		return sessionFactory;
 	}
 
 	@Override
 	public EntityTypeImpl getEntityTypeByName(String entityName) {
 		final EntityTypeImpl entityType = metamodel.getEntityTypeByName( entityName );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "[" + entityName + "] did not refer to EntityType" );
 		}
 		return entityType;
 	}
 
 	public String getEntityManagerFactoryName() {
 		return entityManagerFactoryName;
 	}
 
 	private static class JPACache implements Cache {
 		private SessionFactoryImplementor sessionFactory;
 
 		private JPACache(SessionFactoryImplementor sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		public boolean contains(Class entityClass, Object identifier) {
 			return sessionFactory.getCache().containsEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass, Object identifier) {
 			sessionFactory.getCache().evictEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass) {
 			sessionFactory.getCache().evictEntityRegion( entityClass );
 		}
 
 		public void evictAll() {
 			sessionFactory.getCache().evictEntityRegions();
 // TODO : if we want to allow an optional clearing of all cache data, the additional calls would be:
 //			sessionFactory.getCache().evictCollectionRegions();
 //			sessionFactory.getCache().evictQueryRegions();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <T> T unwrap(Class<T> cls) {
 			if ( RegionFactory.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getSettings().getRegionFactory();
 			}
 			if ( org.hibernate.Cache.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getCache();
 			}
 			throw new PersistenceException( "Hibernate cannot unwrap Cache as " + cls.getName() );
 		}
 	}
 
 	private static EntityManagerFactory getNamedEntityManagerFactory(String entityManagerFactoryName) throws InvalidObjectException {
 		Object result = EntityManagerFactoryRegistry.INSTANCE.getNamedEntityManagerFactory(entityManagerFactoryName);
 
 		if ( result == null ) {
 			throw new InvalidObjectException( "could not resolve entity manager factory during entity manager deserialization [name=" + entityManagerFactoryName + "]" );
 		}
 
 		return (EntityManagerFactory)result;
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if (entityManagerFactoryName == null) {
 			throw new InvalidObjectException( "could not serialize entity manager factory with null entityManagerFactoryName" );
 		}
 		oos.defaultWriteObject();
 	}
 
 	/**
 	 * After deserialization of an EntityManagerFactory, this is invoked to return the EntityManagerFactory instance
 	 * that is already in use rather than a cloned copy of the object.
 	 *
 	 * @return
 	 * @throws InvalidObjectException
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		return getNamedEntityManagerFactory(entityManagerFactoryName);
 	}
 
 
 
 	private static class HibernatePersistenceUnitUtil implements PersistenceUnitUtil, Serializable {
 		private final HibernateEntityManagerFactory emf;
 		private transient PersistenceUtilHelper.MetadataCache cache;
 
 		private HibernatePersistenceUnitUtil(EntityManagerFactoryImpl emf) {
 			this.emf = emf;
 			this.cache = emf.cache;
 		}
 
 		public boolean isLoaded(Object entity, String attributeName) {
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
 			LoadState state = PersistenceUtilHelper.isLoadedWithoutReference( entity, attributeName, cache );
 			if (state == LoadState.LOADED) {
 				return true;
 			}
 			else if (state == LoadState.NOT_LOADED ) {
 				return false;
 			}
 			else {
 				return PersistenceUtilHelper.isLoadedWithReference( entity, attributeName, cache ) != LoadState.NOT_LOADED;
 			}
 		}
 
 		public boolean isLoaded(Object entity) {
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
 			return PersistenceUtilHelper.isLoaded( entity ) != LoadState.NOT_LOADED;
 		}
 
 		public Object getIdentifier(Object entity) {
 			final Class entityClass = Hibernate.getClass( entity );
 			final ClassMetadata classMetadata = emf.getSessionFactory().getClassMetadata( entityClass );
 			if (classMetadata == null) {
 				throw new IllegalArgumentException( entityClass + " is not an entity" );
 			}
 			//TODO does that work for @IdClass?
 			return classMetadata.getIdentifier( entity );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/StoredProcedureQueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/StoredProcedureQueryImpl.java
index aa9ddd912b..b232696774 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/StoredProcedureQueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/StoredProcedureQueryImpl.java
@@ -1,366 +1,370 @@
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
 package org.hibernate.jpa.internal;
 
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.Parameter;
 import javax.persistence.ParameterMode;
 import javax.persistence.Query;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.TemporalType;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.ProcedureResult;
 import org.hibernate.result.ResultSetReturn;
 import org.hibernate.result.Return;
 import org.hibernate.result.UpdateCountReturn;
 import org.hibernate.jpa.spi.BaseQueryImpl;
 import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class StoredProcedureQueryImpl extends BaseQueryImpl implements StoredProcedureQuery {
 	private final ProcedureCall procedureCall;
 	private ProcedureResult procedureResult;
 
 	public StoredProcedureQueryImpl(ProcedureCall procedureCall, HibernateEntityManagerImplementor entityManager) {
 		super( entityManager );
 		this.procedureCall = procedureCall;
 	}
 
 	@Override
 	protected boolean applyTimeoutHint(int timeout) {
 		procedureCall.setTimeout( timeout );
 		return true;
 	}
 
 	@Override
 	protected boolean applyCacheableHint(boolean isCacheable) {
 		procedureCall.setCacheable( isCacheable );
 		return true;
 	}
 
 	@Override
 	protected boolean applyCacheRegionHint(String regionName) {
 		procedureCall.setCacheRegion( regionName );
 		return true;
 	}
 
 	@Override
 	protected boolean applyReadOnlyHint(boolean isReadOnly) {
 		procedureCall.setReadOnly( isReadOnly );
 		return true;
 	}
 
 	@Override
 	protected boolean applyCacheModeHint(CacheMode cacheMode) {
 		procedureCall.setCacheMode( cacheMode );
 		return true;
 	}
 
 	@Override
 	protected boolean applyFlushModeHint(FlushMode flushMode) {
 		procedureCall.setFlushMode( flushMode );
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
 		entityManager().checkOpen( true );
 		registerParameter(
 				new ParameterRegistrationImpl(
 						procedureCall.registerParameter( position, type, mode )
 				)
 		);
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
 		entityManager().checkOpen( true );
 		registerParameter(
 				new ParameterRegistrationImpl(
 						procedureCall.registerParameter( parameterName, type, mode )
 				)
 		);
 		return this;
 	}
 
 	@Override
 	public <T> StoredProcedureQueryImpl setParameter(Parameter<T> param, T value) {
 		return (StoredProcedureQueryImpl) super.setParameter( param, value );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( param, value, temporalType );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( param, value, temporalType );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(String name, Object value) {
 		return ( StoredProcedureQueryImpl) super.setParameter( name, value );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(String name, Calendar value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( name, value, temporalType );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(String name, Date value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( name, value, temporalType );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(int position, Object value) {
 		return (StoredProcedureQueryImpl) super.setParameter( position, value );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(int position, Calendar value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( position, value, temporalType );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setParameter(int position, Date value, TemporalType temporalType) {
 		return (StoredProcedureQueryImpl) super.setParameter( position, value, temporalType );
 	}
 
 
 	// covariant returns ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public StoredProcedureQueryImpl setFlushMode(FlushModeType jpaFlushMode) {
 		return (StoredProcedureQueryImpl) super.setFlushMode( jpaFlushMode );
 	}
 
 	@Override
 	public StoredProcedureQueryImpl setHint(String hintName, Object value) {
 		return (StoredProcedureQueryImpl) super.setHint( hintName, value );
 	}
 
 
 	// outputs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private ProcedureResult outputs() {
 		if ( procedureResult == null ) {
 			procedureResult = procedureCall.getResult();
 		}
 		return procedureResult;
 	}
 
 	@Override
 	public Object getOutputParameterValue(int position) {
 		return outputs().getOutputParameterValue( position );
 	}
 
 	@Override
 	public Object getOutputParameterValue(String parameterName) {
 		return outputs().getOutputParameterValue( parameterName );
 	}
 
 	@Override
 	public boolean execute() {
 		return outputs().hasMoreReturns();
 	}
 
 	@Override
 	public int executeUpdate() {
 		return getUpdateCount();
 	}
 
 	@Override
 	public boolean hasMoreResults() {
 		return outputs().hasMoreReturns();
 	}
 
 	@Override
 	public int getUpdateCount() {
 		final Return nextReturn = outputs().getNextReturn();
 		if ( nextReturn.isResultSet() ) {
 			return -1;
 		}
 		return ( (UpdateCountReturn) nextReturn ).getUpdateCount();
 	}
 
 	@Override
 	public List getResultList() {
 		final Return nextReturn = outputs().getNextReturn();
 		if ( ! nextReturn.isResultSet() ) {
 			return null; // todo : what should be thrown/returned here?
 		}
 		return ( (ResultSetReturn) nextReturn ).getResultList();
 	}
 
 	@Override
 	public Object getSingleResult() {
 		final Return nextReturn = outputs().getNextReturn();
 		if ( ! nextReturn.isResultSet() ) {
 			return null; // todo : what should be thrown/returned here?
 		}
 		return ( (ResultSetReturn) nextReturn ).getSingleResult();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> cls) {
 		return null;
 	}
 
 
 	// ugh ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Query setLockMode(LockModeType lockMode) {
 		return null;
 	}
 
 	@Override
 	public LockModeType getLockMode() {
 		return null;
 	}
 
 
 	// unsupported hints/calls ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	protected void applyFirstResult(int firstResult) {
 	}
 
 	@Override
 	protected void applyMaxResults(int maxResults) {
 	}
 
 	@Override
 	protected boolean canApplyAliasSpecificLockModeHints() {
 		return false;
 	}
 
 	@Override
 	protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
 	}
 
 	@Override
 	protected boolean applyLockTimeoutHint(int timeout) {
 		return false;
 	}
 
 	@Override
 	protected boolean applyCommentHint(String comment) {
 		return false;
 	}
 
 	@Override
 	protected boolean applyFetchSizeHint(int fetchSize) {
 		return false;
 	}
 
 
 	// parameters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	protected boolean isJpaPositionalParameter(int position) {
 		return false;
 	}
 
+	public ProcedureCall getHibernateProcedureCall() {
+		return procedureCall;
+	}
+
 	private static class ParameterRegistrationImpl<T> implements ParameterRegistration<T> {
 		private final org.hibernate.procedure.ParameterRegistration<T> nativeParamRegistration;
 
 		private ParameterBind<T> bind;
 
 		private ParameterRegistrationImpl(org.hibernate.procedure.ParameterRegistration<T> nativeParamRegistration) {
 			this.nativeParamRegistration = nativeParamRegistration;
 		}
 
 		@Override
 		public String getName() {
 			return nativeParamRegistration.getName();
 		}
 
 		@Override
 		public Integer getPosition() {
 			return nativeParamRegistration.getPosition();
 		}
 
 		@Override
 		public Class<T> getParameterType() {
 			return nativeParamRegistration.getType();
 		}
 
 		@Override
 		public ParameterMode getMode() {
 			return nativeParamRegistration.getMode();
 		}
 
 		@Override
 		public boolean isBindable() {
 			return getMode() == ParameterMode.IN || getMode() == ParameterMode.INOUT;
 		}
 
 		@Override
 		public void bindValue(T value) {
 			bindValue( value, null );
 		}
 
 		@Override
 		public void bindValue(T value, TemporalType specifiedTemporalType) {
 			validateBinding( getParameterType(), value, specifiedTemporalType );
 
 			nativeParamRegistration.bindValue( value,specifiedTemporalType );
 
 			if ( bind == null ) {
 				bind = new ParameterBind<T>() {
 					@Override
 					public T getValue() {
 						return nativeParamRegistration.getBind().getValue();
 					}
 
 					@Override
 					public TemporalType getSpecifiedTemporalType() {
 						return nativeParamRegistration.getBind().getExplicitTemporalType();
 					}
 				};
 			}
 		}
 
 		@Override
 		public ParameterBind<T> getBind() {
 			return bind;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
index e7745f4081..369bbbe829 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
@@ -1,1688 +1,1714 @@
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
 package org.hibernate.jpa.spi;
 
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityExistsException;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.EntityTransaction;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.LockTimeoutException;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.OptimisticLockException;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PessimisticLockException;
 import javax.persistence.PessimisticLockScope;
 import javax.persistence.Query;
 import javax.persistence.QueryTimeoutException;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.SynchronizationType;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.TupleElement;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.lock.LockingStrategyException;
 import org.hibernate.dialect.lock.OptimisticEntityLockException;
 import org.hibernate.dialect.lock.PessimisticEntityLockException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateEntityManagerFactory;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.criteria.ValueHandlerFactory;
 import org.hibernate.jpa.criteria.compile.CompilableCriteria;
 import org.hibernate.jpa.criteria.compile.CriteriaCompiler;
 import org.hibernate.jpa.criteria.expression.CompoundSelectionImpl;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.QueryImpl;
 import org.hibernate.jpa.internal.StoredProcedureQueryImpl;
 import org.hibernate.jpa.internal.TransactionImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
+import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.transform.BasicTransformerAdapter;
 import org.hibernate.type.Type;
 
 
 /**
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
 	private static final long serialVersionUID = 78818181L;
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            AbstractEntityManagerImpl.class.getName());
 
 	private static final List<String> entityManagerSpecificProperties = new ArrayList<String>();
 
 	static {
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_SCOPE );
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_TIMEOUT );
 		entityManagerSpecificProperties.add( AvailableSettings.FLUSH_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_STORE_MODE );
 		entityManagerSpecificProperties.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private EntityManagerFactoryImpl entityManagerFactory;
 	protected transient TransactionImpl tx = new TransactionImpl( this );
 	protected PersistenceContextType persistenceContextType;
 	private SynchronizationType synchronizationType;
 	private PersistenceUnitTransactionType transactionType;
 	private Map<String, Object> properties;
 	private LockOptions lockOptions;
 
 	protected AbstractEntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType type,
 			SynchronizationType synchronizationType,
 			PersistenceUnitTransactionType transactionType,
 			Map properties) {
 		this.entityManagerFactory = entityManagerFactory;
 		this.persistenceContextType = type;
 		this.synchronizationType = synchronizationType;
 		this.transactionType = transactionType;
 
 		this.lockOptions = new LockOptions();
 		this.properties = new HashMap<String, Object>();
 		for ( String key : entityManagerSpecificProperties ) {
 			if ( entityManagerFactory.getProperties().containsKey( key ) ) {
 				this.properties.put( key, entityManagerFactory.getProperties().get( key ) );
 			}
 			if ( properties != null && properties.containsKey( key ) ) {
 				this.properties.put( key, properties.get( key ) );
 			}
 		}
 	}
 
 //	protected PersistenceUnitTransactionType transactionType() {
 //		return transactionType;
 //	}
 //
 //	protected SynchronizationType synchronizationType() {
 //		return synchronizationType;
 //	}
 //
 //	public boolean shouldAutoJoinTransactions() {
 //		// the Session should auto join only if using non-JTA transactions or if the synchronization type
 //		// was specified as SYNCHRONIZED
 //		return transactionType != PersistenceUnitTransactionType.JTA
 //				|| synchronizationType == SynchronizationType.SYNCHRONIZED;
 //	}
 
 	public PersistenceUnitTransactionType getTransactionType() {
 		return transactionType;
 	}
 
 	protected void postInit() {
 		//register in Sync if needed
 		if ( transactionType == PersistenceUnitTransactionType.JTA
 				&& synchronizationType == SynchronizationType.SYNCHRONIZED ) {
 			joinTransaction( false );
 		}
 
 		setDefaultProperties();
 		applyProperties();
 	}
 
 	private void applyProperties() {
 		getSession().setFlushMode( ConfigurationHelper.getFlushMode( properties.get( AvailableSettings.FLUSH_MODE ) ) );
 		setLockOptions( this.properties, this.lockOptions );
 		getSession().setCacheMode(
 				CacheModeHelper.interpretCacheMode(
 						currentCacheStoreMode(),
 						currentCacheRetrieveMode()
 				)
 		);
 	}
 
 	private Query applyProperties(Query query) {
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			query.setLockMode( getLockMode(lockOptions.getLockMode()));
 		}
 		Object queryTimeout;
 		if ( (queryTimeout = getProperties().get(QueryHints.SPEC_HINT_TIMEOUT)) != null ) {
 			query.setHint ( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
 		}
 		Object lockTimeout;
 		if( (lockTimeout = getProperties().get( AvailableSettings.LOCK_TIMEOUT ))!=null){
 			query.setHint( AvailableSettings.LOCK_TIMEOUT, lockTimeout );
 		}
 		return query;
 	}
 
 	private CacheRetrieveMode currentCacheRetrieveMode() {
 		return determineCacheRetrieveMode( properties );
 	}
 
 	private CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
 		return ( CacheRetrieveMode ) settings.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 	}
 
 	private CacheStoreMode currentCacheStoreMode() {
 		return determineCacheStoreMode( properties );
 	}
 
 	private CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
 		return ( CacheStoreMode ) properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
 	}
 
 	private void setLockOptions(Map<String, Object> props, LockOptions options) {
 		Object lockScope = props.get( AvailableSettings.LOCK_SCOPE );
 		if ( lockScope instanceof String && PessimisticLockScope.valueOf( ( String ) lockScope ) == PessimisticLockScope.EXTENDED ) {
 			options.setScope( true );
 		}
 		else if ( lockScope instanceof PessimisticLockScope ) {
 			boolean extended = PessimisticLockScope.EXTENDED.equals( lockScope );
 			options.setScope( extended );
 		}
 		else if ( lockScope != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_SCOPE + ": " + lockScope );
 		}
 
 		Object lockTimeout = props.get( AvailableSettings.LOCK_TIMEOUT );
 		int timeout = 0;
 		boolean timeoutSet = false;
 		if ( lockTimeout instanceof String ) {
 			timeout = Integer.parseInt( ( String ) lockTimeout );
 			timeoutSet = true;
 		}
 		else if ( lockTimeout instanceof Number ) {
 			timeout = ( (Number) lockTimeout ).intValue();
 			timeoutSet = true;
 		}
 		else if ( lockTimeout != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_TIMEOUT + ": " + lockTimeout );
 		}
 		if ( timeoutSet ) {
             if ( timeout == LockOptions.SKIP_LOCKED ) {
                 options.setTimeOut( LockOptions.SKIP_LOCKED );
             }
 			else if ( timeout < 0 ) {
 				options.setTimeOut( LockOptions.WAIT_FOREVER );
 			}
 			else if ( timeout == 0 ) {
 				options.setTimeOut( LockOptions.NO_WAIT );
 			}
 			else {
 				options.setTimeOut( timeout );
 			}
 		}
 	}
 
 	/**
 	 * Sets the default property values for the properties the entity manager supports and which are not already explicitly
 	 * set.
 	 */
 	private void setDefaultProperties() {
 		if ( properties.get( AvailableSettings.FLUSH_MODE ) == null ) {
 			properties.put( AvailableSettings.FLUSH_MODE, getSession().getFlushMode().toString() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_SCOPE ) == null ) {
 			this.properties.put( AvailableSettings.LOCK_SCOPE, PessimisticLockScope.EXTENDED.name() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_TIMEOUT ) == null ) {
 			properties.put( AvailableSettings.LOCK_TIMEOUT, LockOptions.WAIT_FOREVER );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE );
 		}
 	}
 
 	@Override
 	public Query createQuery(String jpaqlString) {
 		checkOpen();
 		try {
 			return applyProperties( new QueryImpl<Object>( internalGetSession().createQuery( jpaqlString ), this ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	protected abstract void checkOpen();
 
 	@Override
 	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
 		checkOpen();
 		try {
 			// do the translation
 			org.hibernate.Query hqlQuery = internalGetSession().createQuery( jpaqlString );
 
 			resultClassChecking( resultClass, hqlQuery );
 
 			// finally, build/return the query instance
 			return new QueryImpl<T>( hqlQuery, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	protected void resultClassChecking(Class resultClass, org.hibernate.Query hqlQuery) {
 		// make sure the query is a select -> HHH-7192
 		final SessionImplementor session = unwrap( SessionImplementor.class );
 		final HQLQueryPlan queryPlan = session.getFactory().getQueryPlanCache().getHQLQueryPlan(
 				hqlQuery.getQueryString(),
 				false,
 				session.getLoadQueryInfluencers().getEnabledFilters()
 		);
 		if ( queryPlan.getTranslators()[0].isManipulationStatement() ) {
 			throw new IllegalArgumentException( "Update/delete queries cannot be typed" );
 		}
 
 		// do some return type validation checking
 		if ( Object[].class.equals( resultClass ) ) {
 			// no validation needed
 		}
 		else if ( Tuple.class.equals( resultClass ) ) {
 			TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( hqlQuery );
 			hqlQuery.setResultTransformer( tupleTransformer  );
 		}
 		else {
 			final Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
 			if ( dynamicInstantiationClass != null ) {
 				if ( ! resultClass.isAssignableFrom( dynamicInstantiationClass ) ) {
 					throw new IllegalArgumentException(
 							"Mismatch in requested result type [" + resultClass.getName() +
 									"] and actual result type [" + dynamicInstantiationClass.getName() + "]"
 					);
 				}
 			}
 			else if ( hqlQuery.getReturnTypes().length == 1 ) {
 				// if we have only a single return expression, its java type should match with the requested type
 				if ( !resultClass.isAssignableFrom( hqlQuery.getReturnTypes()[0].getReturnedClass() ) ) {
 					throw new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									hqlQuery.getReturnTypes()[0].getReturnedClass() + "]"
 					);
 				}
 			}
 			else {
 				throw new IllegalArgumentException(
 						"Cannot create TypedQuery for query with more than one return using requested result type [" +
 								resultClass.getName() + "]"
 				);
 			}
 		}
 	}
 
 	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
 		private List<TupleElement<?>> tupleElements;
 		private Map<String,HqlTupleElementImpl> tupleElementsByAlias;
 
 		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
 			final Type[] resultTypes = hqlQuery.getReturnTypes();
 			final int tupleSize = resultTypes.length;
 
 			this.tupleElements = CollectionHelper.arrayList( tupleSize );
 
 			final String[] aliases = hqlQuery.getReturnAliases();
 			final boolean hasAliases = aliases != null && aliases.length > 0;
 			this.tupleElementsByAlias = hasAliases
 					? CollectionHelper.<String, HqlTupleElementImpl>mapOfSize( tupleSize )
 					: Collections.<String, HqlTupleElementImpl>emptyMap();
 
 			for ( int i = 0; i < tupleSize; i++ ) {
 				final HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(
 						i,
 						aliases == null ? null : aliases[i],
 						resultTypes[i]
 				);
 				tupleElements.add( tupleElement );
 				if ( hasAliases ) {
 					final String alias = aliases[i];
 					if ( alias != null ) {
 						tupleElementsByAlias.put( alias, tupleElement );
 					}
 				}
 			}
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			if ( tuple.length != tupleElements.size() ) {
 				throw new IllegalArgumentException(
 						"Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" +
 								tupleElements.size() + "]"
 				);
 			}
 			return new HqlTupleImpl( tuple );
 		}
 
 		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
 			private final int position;
 			private final String alias;
 			private final Type hibernateType;
 
 			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
 				this.position = position;
 				this.alias = alias;
 				this.hibernateType = hibernateType;
 			}
 
 			@Override
 			public Class getJavaType() {
 				return hibernateType.getReturnedClass();
 			}
 
 			@Override
 			public String getAlias() {
 				return alias;
 			}
 
 			public int getPosition() {
 				return position;
 			}
 
 			public Type getHibernateType() {
 				return hibernateType;
 			}
 		}
 
 		public class HqlTupleImpl implements Tuple {
 			private Object[] tuple;
 
 			public HqlTupleImpl(Object[] tuple) {
 				this.tuple = tuple;
 			}
 
 			@Override
 			public <X> X get(String alias, Class<X> type) {
 				return (X) get( alias );
 			}
 
 			@Override
 			public Object get(String alias) {
 				HqlTupleElementImpl tupleElement = tupleElementsByAlias.get( alias );
 				if ( tupleElement == null ) {
 					throw new IllegalArgumentException( "Unknown alias [" + alias + "]" );
 				}
 				return tuple[ tupleElement.getPosition() ];
 			}
 
 			@Override
 			public <X> X get(int i, Class<X> type) {
 				return (X) get( i );
 			}
 
 			@Override
 			public Object get(int i) {
 				if ( i < 0 ) {
 					throw new IllegalArgumentException( "requested tuple index must be greater than zero" );
 				}
 				if ( i > tuple.length ) {
 					throw new IllegalArgumentException( "requested tuple index exceeds actual tuple size" );
 				}
 				return tuple[i];
 			}
 
 			@Override
 			public Object[] toArray() {
 				// todo : make a copy?
 				return tuple;
 			}
 
 			@Override
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 
 			@Override
 			public <X> X get(TupleElement<X> tupleElement) {
 				if ( HqlTupleElementImpl.class.isInstance( tupleElement ) ) {
 					return get( ( (HqlTupleElementImpl) tupleElement ).getPosition(), tupleElement.getJavaType() );
 				}
 				else {
 					return get( tupleElement.getAlias(), tupleElement.getJavaType() );
 				}
 			}
 		}
 	}
 
 	@Override
 	public <T> QueryImpl<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			Options options) {
 		try {
 			org.hibernate.Query hqlQuery = internalGetSession().createQuery( jpaqlString );
 
 			if ( options.getValueHandlers() == null ) {
 				if ( options.getResultMetadataValidator() != null ) {
 					options.getResultMetadataValidator().validate( hqlQuery.getReturnTypes() );
 				}
 			}
 
 			// determine if we need a result transformer
 			List tupleElements = Tuple.class.equals( resultClass )
 					? ( ( CompoundSelectionImpl<Tuple> ) selection ).getCompoundSelectionItems()
 					: null;
 			if ( options.getValueHandlers() != null || tupleElements != null ) {
 				hqlQuery.setResultTransformer(
 						new CriteriaQueryTransformer( options.getValueHandlers(), tupleElements )
 				);
 			}
 			return new QueryImpl<T>( hqlQuery, this, options.getNamedParameterExplicitTypes() );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	private static class CriteriaQueryTransformer extends BasicTransformerAdapter {
 		private final List<ValueHandlerFactory.ValueHandler> valueHandlers;
 		private final List tupleElements;
 
 		private CriteriaQueryTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
 			// todo : should these 2 sizes match *always*?
 			this.valueHandlers = valueHandlers;
 			this.tupleElements = tupleElements;
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			final Object[] valueHandlerResult;
 			if ( valueHandlers == null ) {
 				valueHandlerResult = tuple;
 			}
 			else {
 				valueHandlerResult = new Object[tuple.length];
 				for ( int i = 0; i < tuple.length; i++ ) {
 					ValueHandlerFactory.ValueHandler valueHandler = valueHandlers.get( i );
 					valueHandlerResult[i] = valueHandler == null
 							? tuple[i]
 							: valueHandler.convert( tuple[i] );
 				}
 			}
 
 			return tupleElements == null
 					? valueHandlerResult.length == 1 ? valueHandlerResult[0] : valueHandlerResult
 					: new TupleImpl( tuple );
 
 		}
 
 		private class TupleImpl implements Tuple {
 			private final Object[] tuples;
 
 			private TupleImpl(Object[] tuples) {
 				if ( tuples.length != tupleElements.size() ) {
 					throw new IllegalArgumentException(
 							"Size mismatch between tuple result [" + tuples.length
 									+ "] and expected tuple elements [" + tupleElements.size() + "]"
 					);
 				}
 				this.tuples = tuples;
 			}
 
 			public <X> X get(TupleElement<X> tupleElement) {
 				int index = tupleElements.indexOf( tupleElement );
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Requested tuple element did not correspond to element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return ( X ) tuples[index];
 			}
 
 			public Object get(String alias) {
 				int index = -1;
 				if ( alias != null ) {
 					alias = alias.trim();
 					if ( alias.length() > 0 ) {
 						int i = 0;
 						for ( TupleElement selection : ( List<TupleElement> ) tupleElements ) {
 							if ( alias.equals( selection.getAlias() ) ) {
 								index = i;
 								break;
 							}
 							i++;
 						}
 					}
 				}
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Given alias [" + alias + "] did not correspond to an element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return tuples[index];
 			}
 
 			public <X> X get(String alias, Class<X> type) {
 				return ( X ) get( alias );
 			}
 
 			public Object get(int i) {
 				if ( i >= tuples.length ) {
 					throw new IllegalArgumentException(
 							"Given index [" + i + "] was outside the range of result tuple size [" + tuples.length + "] "
 					);
 				}
 				return tuples[i];
 			}
 
 			public <X> X get(int i, Class<X> type) {
 				return ( X ) get( i );
 			}
 
 			public Object[] toArray() {
 				return tuples;
 			}
 
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 		}
 	}
 
 	private CriteriaCompiler criteriaCompiler;
 
 	protected CriteriaCompiler criteriaCompiler() {
 		if ( criteriaCompiler == null ) {
 			criteriaCompiler = new CriteriaCompiler( this );
 		}
 		return criteriaCompiler;
 	}
 
 	@Override
 	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		checkOpen();
 		return (TypedQuery<T>) criteriaCompiler().compile( (CompilableCriteria) criteriaQuery );
 	}
 
 	@Override
 	public Query createQuery(CriteriaUpdate criteriaUpdate) {
 		checkOpen();
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaUpdate );
 	}
 
 	@Override
 	public Query createQuery(CriteriaDelete criteriaDelete) {
 		checkOpen();
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaDelete );
 	}
 
 	@Override
 	public Query createNamedQuery(String name) {
 		checkOpen();
 		try {
 			org.hibernate.Query namedQuery = internalGetSession().getNamedQuery( name );
 			try {
 				return new QueryImpl( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	@Override
 	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
 		checkOpen();
 		try {
 			/*
 			 * Get the named query.
 			 * If the named query is a SQL query, get the expected returned type from the query definition
 			 * or its associated result set mapping
 			 * If the named query is a HQL query, use getReturnType()
 			 */
 			org.hibernate.Query namedQuery = internalGetSession().getNamedQuery( name );
 			//TODO clean this up to avoid downcasting
 			final SessionFactoryImplementor factoryImplementor = entityManagerFactory.getSessionFactory();
 			final NamedSQLQueryDefinition queryDefinition = factoryImplementor.getNamedSQLQuery( name );
 			try {
 				if ( queryDefinition != null ) {
 					Class<?> actualReturnedClass;
 
 					final NativeSQLQueryReturn[] queryReturns;
 					if ( queryDefinition.getQueryReturns() != null ) {
 						queryReturns = queryDefinition.getQueryReturns();
 					}
 					else if ( queryDefinition.getResultSetRef() != null ) {
 						final ResultSetMappingDefinition rsMapping = factoryImplementor.getResultSetMapping(
 								queryDefinition.getResultSetRef()
 						);
 						queryReturns = rsMapping.getQueryReturns();
 					}
 					else {
 						throw new AssertionFailure( "Unsupported named query model. Please report the bug in Hibernate EntityManager");
 					}
 					if ( queryReturns.length > 1 ) {
 						throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
 					}
 					final NativeSQLQueryReturn nativeSQLQueryReturn = queryReturns[0];
 					if ( nativeSQLQueryReturn instanceof NativeSQLQueryRootReturn ) {
 						final String entityClassName = ( ( NativeSQLQueryRootReturn ) nativeSQLQueryReturn ).getReturnEntityName();
 						try {
 							actualReturnedClass = ReflectHelper.classForName( entityClassName, AbstractEntityManagerImpl.class );
 						}
 						catch ( ClassNotFoundException e ) {
 							throw new AssertionFailure( "Unable to instantiate class declared on named native query: " + name + " " + entityClassName );
 						}
 						if ( !resultClass.isAssignableFrom( actualReturnedClass ) ) {
 							throw buildIncompatibleException( resultClass, actualReturnedClass );
 						}
 					}
 					else {
 						//TODO support other NativeSQLQueryReturn type. For now let it go.
 					}
 				}
 				else {
 					resultClassChecking( resultClass, namedQuery );
 				}
 				return new QueryImpl<T>( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									actualResultClass + "]"
 					);
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString) {
 		checkOpen();
 		try {
 			SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString, Class resultClass) {
 		checkOpen();
 		try {
 			SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			q.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString, String resultSetMapping) {
 		checkOpen();
 		try {
-			SQLQuery q = internalGetSession().createSQLQuery( sqlString );
+			final SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			q.setResultSetMapping( resultSetMapping );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
 		checkOpen();
-		throw new NotYetImplementedException();
+		final ProcedureCallMemento memento = ( (SessionImplementor) internalGetSession() ).getFactory()
+				.getNamedQueryRepository().getNamedProcedureCallMemento( name );
+		if ( memento == null ) {
+			throw new IllegalArgumentException( "No @NamedStoredProcedureQuery was found with that name : " + name );
+		}
+		final ProcedureCall procedureCall = memento.makeProcedureCall( internalGetSession() );
+		final StoredProcedureQueryImpl jpaImpl = new StoredProcedureQueryImpl( procedureCall, this );
+		// apply hints
+		if ( memento.getHintsMap() != null ) {
+			for ( Map.Entry<String,Object> hintEntry : memento.getHintsMap().entrySet() ) {
+				jpaImpl.setHint( hintEntry.getKey(), hintEntry.getValue() );
+			}
+		}
+		return jpaImpl;
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
 		checkOpen();
 		try {
-			ProcedureCall procedureCall = internalGetSession().createStoredProcedureCall( procedureName );
-			return new StoredProcedureQueryImpl( procedureCall, this );
+			return new StoredProcedureQueryImpl(
+					internalGetSession().createStoredProcedureCall( procedureName ),
+					this
+			);
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
 		checkOpen();
 		try {
-			ProcedureCall procedureCall = internalGetSession().createStoredProcedureCall( procedureName, resultClasses );
-			return new StoredProcedureQueryImpl( procedureCall, this );
+			return new StoredProcedureQueryImpl(
+					internalGetSession().createStoredProcedureCall( procedureName, resultClasses ),
+					this
+			);
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
 		checkOpen();
-		throw new NotYetImplementedException();
+		try {
+			return new StoredProcedureQueryImpl(
+					internalGetSession().createStoredProcedureCall( procedureName, resultSetMappings ),
+					this
+			);
+		}
+		catch ( HibernateException he ) {
+			throw convert( he );
+		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		checkOpen();
 		try {
 			return ( T ) internalGetSession().load( entityClass, ( Serializable ) primaryKey );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey) {
 		checkOpen();
 		return find( entityClass, primaryKey, null, null );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		checkOpen();
 		return find( entityClass, primaryKey, null, properties );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
 		checkOpen();
 		return find( entityClass, primaryKey, lockModeType, null );
 	}
 
 	@Override
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 		Session session = internalGetSession();
 		CacheMode previousCacheMode = session.getCacheMode();
 		CacheMode cacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			session.setCacheMode( cacheMode );
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				return ( A ) session.get(
 						entityClass, ( Serializable ) primaryKey, 
 						lockOptions
 				);
 			}
 			else {
 				return ( A ) session.get( entityClass, ( Serializable ) primaryKey );
 			}
 		}
 		catch ( EntityNotFoundException ignored ) {
 			// DefaultLoadEventListener.returnNarrowedProxy may throw ENFE (see HHH-7861 for details),
 			// which find() should not throw.  Find() should return null if the entity was not found.
 			if ( LOG.isDebugEnabled() ) {
 				String entityName = entityClass != null ? entityClass.getName(): null;
 				String identifierValue = primaryKey != null ? primaryKey.toString() : null ;
 				LOG.ignoringEntityNotFound( entityName, identifierValue );
 			}
 			return null;
 		}
 		catch ( ObjectDeletedException e ) {
 			//the spec is silent about people doing remove() find() on the same PC
 			return null;
 		}
 		catch ( ObjectNotFoundException e ) {
 			//should not happen on the entity itself with get
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			session.setCacheMode( previousCacheMode );
 		}
 	}
 
 	public CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
 		CacheRetrieveMode retrieveMode = null;
 		CacheStoreMode storeMode = null;
 		if ( localProperties != null ) {
 			retrieveMode = determineCacheRetrieveMode( localProperties );
 			storeMode = determineCacheStoreMode( localProperties );
 		}
 		if ( retrieveMode == null ) {
 			// use the EM setting
 			retrieveMode = determineCacheRetrieveMode( this.properties );
 		}
 		if ( storeMode == null ) {
 			// use the EM setting
 			storeMode = determineCacheStoreMode( this.properties );
 		}
 		return CacheModeHelper.interpretCacheMode( storeMode, retrieveMode );
 	}
 
 	private void checkTransactionNeeded() {
 		if ( persistenceContextType == PersistenceContextType.TRANSACTION && !isTransactionInProgress() ) {
 			//no need to mark as rollback, no tx in progress
 			throw new TransactionRequiredException(
 					"no transaction is in progress for a TRANSACTION type persistence context"
 			);
 		}
 	}
 
 	@Override
 	public void persist(Object entity) {
 		checkOpen();
 		checkTransactionNeeded();
 		try {
 			internalGetSession().persist( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage() );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A merge(A entity) {
 		checkOpen();
 		checkTransactionNeeded();
 		try {
 			return ( A ) internalGetSession().merge( entity );
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw new IllegalArgumentException( sse );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public void remove(Object entity) {
 		checkOpen();
 		checkTransactionNeeded();
 		try {
 			internalGetSession().delete( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public void refresh(Object entity) {
 		refresh( entity, null, null );
 	}
 
 	@Override
 	public void refresh(Object entity, Map<String, Object> properties) {
 		refresh( entity, null, properties );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockModeType) {
 		refresh( entity, lockModeType, null );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 		checkTransactionNeeded();
 
 		Session session = internalGetSession();
 		CacheMode previousCacheMode = session.getCacheMode();
 		CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			session.setCacheMode( localCacheMode );
 			if ( !session.contains( entity ) ) {
 				throw new IllegalArgumentException( "Entity not managed" );
 			}
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				session.refresh( entity, lockOptions );
 			}
 			else {
 				session.refresh( entity );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			session.setCacheMode( previousCacheMode );
 		}
 	}
 
 	@Override
 	public boolean contains(Object entity) {
 		checkOpen();
 
 		try {
 			if ( entity != null
 					&& !( entity instanceof HibernateProxy )
 					&& internalGetSession().getSessionFactory().getClassMetadata( entity.getClass() ) == null ) {
 				throw new IllegalArgumentException( "Not an entity:" + entity.getClass() );
 			}
 			return internalGetSession().contains( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public LockModeType getLockMode(Object entity) {
 		checkOpen();
 
 		if ( !contains( entity ) ) {
 			throw new IllegalArgumentException( "entity not in the persistence context" );
 		}
 		return getLockModeType( internalGetSession().getCurrentLockMode( entity ) );
 	}
 
 	@Override
 	public void setProperty(String s, Object o) {
 		checkOpen();
 
 		if ( entityManagerSpecificProperties.contains( s ) ) {
 			properties.put( s, o );
 			applyProperties();
         }
 		else {
 			LOG.debugf("Trying to set a property which is not supported on entity manager level");
 		}
 	}
 
 	@Override
 	public Map<String, Object> getProperties() {
 		return Collections.unmodifiableMap( properties );
 	}
 
 	@Override
 	public void flush() {
 		checkOpen();
 
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 		try {
 			internalGetSession().flush();
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	/**
 	 * return a Session
 	 *
 	 * @throws IllegalStateException if the entity manager is closed
 	 */
 	public abstract Session getSession();
 
 	/**
 	 * Return a Session (even if the entity manager is closed).
 	 *
 	 * @return A session.
 	 * @deprecated Deprecated in favor of {@link #getRawSession()}
 	 */
 	@Deprecated
 	protected abstract Session getRawSession();
 
 	/**
 	 * Return a Session without any validation checks.
 	 *
 	 * @return A session.
 	 */
 	protected abstract Session internalGetSession();
 
 	@Override
 	public EntityTransaction getTransaction() {
 		if ( transactionType == PersistenceUnitTransactionType.JTA ) {
 			throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 		}
 		return tx;
 	}
 
 	@Override
 	public EntityManagerFactoryImpl getEntityManagerFactory() {
 		checkOpen();
 		return internalGetEntityManagerFactory();
 	}
 
 	protected EntityManagerFactoryImpl internalGetEntityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	@Override
 	public HibernateEntityManagerFactory getFactory() {
 		return entityManagerFactory;
 	}
 
 	@Override
 	public CriteriaBuilder getCriteriaBuilder() {
 		checkOpen();
 		return getEntityManagerFactory().getCriteriaBuilder();
 	}
 
 	@Override
 	public Metamodel getMetamodel() {
 		checkOpen();
 		return getEntityManagerFactory().getMetamodel();
 	}
 
 	@Override
 	public void setFlushMode(FlushModeType flushModeType) {
 		checkOpen();
 		if ( flushModeType == FlushModeType.AUTO ) {
 			internalGetSession().setFlushMode( FlushMode.AUTO );
 		}
 		else if ( flushModeType == FlushModeType.COMMIT ) {
 			internalGetSession().setFlushMode( FlushMode.COMMIT );
 		}
 		else {
 			throw new AssertionFailure( "Unknown FlushModeType: " + flushModeType );
 		}
 	}
 
 	@Override
 	public void clear() {
 		checkOpen();
 		try {
 			internalGetSession().clear();
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public void detach(Object entity) {
 		checkOpen();
 		try {
 			internalGetSession().evict( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * Hibernate can be set in various flush modes that are unknown to
 	 * JPA 2.0. This method can then return null.
 	 * If it returns null, do em.unwrap(Session.class).getFlushMode() to get the
 	 * Hibernate flush mode
 	 */
 	@Override
 	public FlushModeType getFlushMode() {
 		checkOpen();
 
 		FlushMode mode = internalGetSession().getFlushMode();
 		if ( mode == FlushMode.AUTO ) {
 			return FlushModeType.AUTO;
 		}
 		else if ( mode == FlushMode.COMMIT ) {
 			return FlushModeType.COMMIT;
 		}
 		else {
 			// otherwise this is an unknown mode for EJB3
 			return null;
 		}
 	}
 
 	public void lock(Object entity, LockModeType lockMode) {
 		lock( entity, lockMode, null );
 	}
 
 	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 
 		LockOptions lockOptions = null;
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 
 		try {
 			if ( !contains( entity ) ) {
 				throw new IllegalArgumentException( "entity not in the persistence context" );
 			}
 			lockOptions = getLockRequest( lockModeType, properties );
 			internalGetSession().buildLockRequest( lockOptions ).lock( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 	}
 
 	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = new LockOptions();
 		LockOptions.copy( this.lockOptions, lockOptions );
 		lockOptions.setLockMode( getLockMode( lockModeType ) );
 		if ( properties != null ) {
 			setLockOptions( properties, lockOptions );
 		}
 		return lockOptions;
 	}
 
 	@SuppressWarnings("deprecation")
 	private static LockModeType getLockModeType(LockMode lockMode) {
 		//TODO check that if we have UPGRADE_NOWAIT we have a timeout of zero?
 		return LockModeTypeHelper.getLockModeType( lockMode );
 	}
 
 
 	private static LockMode getLockMode(LockModeType lockMode) {
 		return LockModeTypeHelper.getLockMode( lockMode );
 	}
 
 	public boolean isTransactionInProgress() {
 		return ( ( SessionImplementor ) internalGetSession() ).isTransactionInProgress();
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) internalGetSession().getSessionFactory();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> clazz) {
 		checkOpen();
 
 		if ( Session.class.isAssignableFrom( clazz ) ) {
 			return ( T ) internalGetSession();
 		}
 		if ( SessionImplementor.class.isAssignableFrom( clazz ) ) {
 			return ( T ) internalGetSession();
 		}
 		if ( EntityManager.class.isAssignableFrom( clazz ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap " + clazz );
 	}
 
 	protected void markAsRollback() {
         LOG.debugf("Mark transaction for rollback");
 		if ( tx.isActive() ) {
 			tx.setRollbackOnly();
 		}
 		else {
 			//no explicit use of the tx. boundaries methods
 			if ( PersistenceUnitTransactionType.JTA == transactionType ) {
 				TransactionManager transactionManager = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
 				if ( transactionManager == null ) {
 					throw new PersistenceException(
 							"Using a JTA persistence context wo setting hibernate.transaction.manager_lookup_class"
 					);
 				}
 				try {
                     if ( transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION ) {
                         transactionManager.setRollbackOnly();
                     }
 				}
 				catch ( SystemException e ) {
 					throw new PersistenceException( "Unable to set the JTA transaction as RollbackOnly", e );
 				}
 			}
 		}
 	}
 
 	@Override
 	public boolean isJoinedToTransaction() {
 		checkOpen();
 
 		final SessionImplementor session = (SessionImplementor) internalGetSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		return isOpen() && transaction.getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	@Override
 	public void joinTransaction() {
 		checkOpen();
 		joinTransaction( true );
 	}
 
 	private void joinTransaction(boolean explicitRequest) {
 		if ( transactionType != PersistenceUnitTransactionType.JTA ) {
 			if ( explicitRequest ) {
 			    LOG.callingJoinTransactionOnNonJtaEntityManager();
 			}
 			return;
 		}
 
 		final SessionImplementor session = (SessionImplementor) internalGetSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		transaction.markForJoin();
 		transactionCoordinator.pulse();
 
 		LOG.debug( "Looking for a JTA transaction to join" );
 		if ( ! transactionCoordinator.isTransactionJoinable() ) {
 			if ( explicitRequest ) {
 				// if this is an explicit join request, log a warning so user can track underlying cause
 				// of subsequent exceptions/messages
 				LOG.unableToJoinTransaction(Environment.TRANSACTION_STRATEGY);
 			}
 		}
 
 		try {
 			if ( transaction.getJoinStatus() == JoinStatus.JOINED ) {
 				LOG.debug( "Transaction already joined" );
 				return; // noop
 			}
 
 			// join the transaction and then recheck the status
 			transaction.join();
 			if ( transaction.getJoinStatus() == JoinStatus.NOT_JOINED ) {
 				if ( explicitRequest ) {
 					throw new TransactionRequiredException( "No active JTA transaction on joinTransaction call" );
 				}
 				else {
 					LOG.debug( "Unable to join JTA transaction" );
 					return;
 				}
 			}
 			else if ( transaction.getJoinStatus() == JoinStatus.MARKED_FOR_JOINED ) {
 				throw new AssertionFailure( "Transaction MARKED_FOR_JOINED after isOpen() call" );
 			}
 
 			// register behavior changes
 			SynchronizationCallbackCoordinator callbackCoordinator = transactionCoordinator.getSynchronizationCallbackCoordinator();
 			callbackCoordinator.setManagedFlushChecker( new ManagedFlushCheckerImpl() );
 			callbackCoordinator.setExceptionMapper( new CallbackExceptionMapperImpl() );
 			callbackCoordinator.setAfterCompletionAction( new AfterCompletionActionImpl( session, transactionType ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * returns the underlying session
 	 */
 	public Object getDelegate() {
 		checkOpen();
 		return internalGetSession();
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		tx = new TransactionImpl( this );
 	}
 
 	@Override
 	public void handlePersistenceException(PersistenceException e) {
 		if ( e instanceof NoResultException ) {
 			return;
 		}
 		if ( e instanceof NonUniqueResultException ) {
 			return;
 		}
 		if ( e instanceof LockTimeoutException ) {
 			return;
 		}
 		if ( e instanceof QueryTimeoutException ) {
 			return;
 		}
 
 		try {
 			markAsRollback();
 		}
 		catch ( Exception ne ) {
 			//we do not want the subsequent exception to swallow the original one
             LOG.unableToMarkForRollbackOnPersistenceException(ne);
 		}
 	}
 
 	@Override
 	public void throwPersistenceException(PersistenceException e) {
 		handlePersistenceException( e );
 		throw e;
 	}
 
 	@Override
 	public RuntimeException convert(HibernateException e) {
 		//FIXME should we remove all calls to this method and use convert(RuntimeException) ?
 		return convert( e, null );
 	}
 
 	public RuntimeException convert(RuntimeException e) {
 		RuntimeException result = e;
 		if ( e instanceof HibernateException ) {
 			result = convert( ( HibernateException ) e );
 		}
 		else {
 			markAsRollback();
 		}
 		return result;
 	}
 
 	@Override
 	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
 		if ( e instanceof StaleStateException ) {
 			PersistenceException converted = wrapStaleStateException( ( StaleStateException ) e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof LockingStrategyException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.QueryTimeoutException ) {
 			QueryTimeoutException converted = new QueryTimeoutException(e.getMessage(), e);
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof ObjectNotFoundException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
         else if ( e instanceof org.hibernate.NonUniqueObjectException ) {
             EntityExistsException converted = new EntityExistsException( e.getMessage() );
             handlePersistenceException( converted );
             return converted;
         }
 		else if ( e instanceof org.hibernate.NonUniqueResultException ) {
 			NonUniqueResultException converted = new NonUniqueResultException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof UnresolvableObjectException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof QueryException ) {
 			return new IllegalArgumentException( e );
 		}
 		else if ( e instanceof TransientObjectException ) {
 			try {
 				markAsRollback();
 			}
 			catch ( Exception ne ) {
 				//we do not want the subsequent exception to swallow the original one
                 LOG.unableToMarkForRollbackOnTransientObjectException(ne);
 			}
 			return new IllegalStateException( e ); //Spec 3.2.3 Synchronization rules
 		}
 		else {
 			PersistenceException converted = new PersistenceException( e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 	}
 
 	@Override
 	public void throwPersistenceException(HibernateException e) {
 		throw convert( e );
 	}
 
 	@Override
 	public PersistenceException wrapStaleStateException(StaleStateException e) {
 		PersistenceException pe;
 		if ( e instanceof StaleObjectStateException ) {
 			StaleObjectStateException sose = ( StaleObjectStateException ) e;
 			Serializable identifier = sose.getIdentifier();
 			if ( identifier != null ) {
 				try {
 					Object entity = internalGetSession().load( sose.getEntityName(), identifier );
 					if ( entity instanceof Serializable ) {
 						//avoid some user errors regarding boundary crossing
 						pe = new OptimisticLockException( null, e, entity );
 					}
 					else {
 						pe = new OptimisticLockException( e );
 					}
 				}
 				catch ( EntityNotFoundException enfe ) {
 					pe = new OptimisticLockException( e );
 				}
 			}
 			else {
 				pe = new OptimisticLockException( e );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
 		final PersistenceException pe;
 		if ( e instanceof OptimisticEntityLockException ) {
 			final OptimisticEntityLockException lockException = (OptimisticEntityLockException) e;
 			pe = new OptimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			pe = new LockTimeoutException( e.getMessage(), e, null );
 		}
 		else if ( e instanceof PessimisticEntityLockException ) {
 			PessimisticEntityLockException lockException = (PessimisticEntityLockException) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 			else {
 				pe = new PessimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			org.hibernate.PessimisticLockException jdbcLockException = ( org.hibernate.PessimisticLockException ) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 			else {
 				pe = new PessimisticLockException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	private static class AfterCompletionActionImpl implements AfterCompletionAction {
 		private final SessionImplementor session;
 		private final PersistenceUnitTransactionType transactionType;
 
 		private AfterCompletionActionImpl(SessionImplementor session, PersistenceUnitTransactionType transactionType) {
 			this.session = session;
 			this.transactionType = transactionType;
 		}
 
 		@Override
 		public void doAction(TransactionCoordinator transactionCoordinator, int status) {
 			if ( session.isClosed() ) {
                 LOG.trace("Session was closed; nothing to do");
 				return;
 			}
 
 			final boolean successful = JtaStatusHelper.isCommitted( status );
 			if ( !successful && transactionType == PersistenceUnitTransactionType.JTA ) {
 				( (Session) session ).clear();
 			}
 			session.getTransactionCoordinator().resetJoinStatus();
 		}
 	}
 
 	private static class ManagedFlushCheckerImpl implements ManagedFlushChecker {
 		@Override
 		public boolean shouldDoManagedFlush(TransactionCoordinator coordinator, int jtaStatus) {
 			return ! coordinator.getTransactionContext().isClosed() &&
 					! coordinator.getTransactionContext().isFlushModeNever() &&
 					! JtaStatusHelper.isRollback( jtaStatus );
 		}
 	}
 
 	private class CallbackExceptionMapperImpl implements ExceptionMapper {
 		@Override
 		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
 			throw new PersistenceException( message, systemException );
 		}
 
 		@Override
 		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
 			if ( HibernateException.class.isInstance( failure ) ) {
 				throw convert( failure );
 			}
 			if ( PersistenceException.class.isInstance( failure ) ) {
 				throw failure;
 			}
 			throw new PersistenceException( message, failure );
 		}
 	}
 }
