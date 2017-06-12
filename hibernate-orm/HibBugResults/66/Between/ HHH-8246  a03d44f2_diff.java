diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 1ce6542a96..875cc3d769 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1414 +1,1417 @@
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
-					QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings );
+					bindNamedStoredProcedureQuery( mappings, annotation );
 				}
 			}
 		}
 		{
 			final List<NamedStoredProcedureQueries> annotations =
 					(List<NamedStoredProcedureQueries>) defaults.get( NamedStoredProcedureQueries.class );
 			if ( annotations != null ) {
 				for ( NamedStoredProcedureQueries annotation : annotations ) {
-					for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
-						QueryBinder.bindNamedStoredProcedureQuery( queryAnnotation, mappings );
-					}
+					bindNamedStoredProcedureQueries( mappings, annotation );
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
 
 		// NamedStoredProcedureQuery handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		{
-			final NamedStoredProcedureQuery annotation = annotatedElement.getAnnotation( NamedStoredProcedureQuery.class );
-			if ( annotation != null ) {
-				QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings );
-			}
-		}
+		bindNamedStoredProcedureQuery( mappings, annotatedElement.getAnnotation( NamedStoredProcedureQuery.class ) );
 
 		// NamedStoredProcedureQueries handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		{
-			final NamedStoredProcedureQueries annotation = annotatedElement.getAnnotation( NamedStoredProcedureQueries.class );
-			if ( annotation != null ) {
-				for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
-					QueryBinder.bindNamedStoredProcedureQuery( queryAnnotation, mappings );
-				}
+		bindNamedStoredProcedureQueries(
+				mappings,
+				annotatedElement.getAnnotation( NamedStoredProcedureQueries.class )
+		);
+	}
+
+	private static void bindNamedStoredProcedureQueries(Mappings mappings, NamedStoredProcedureQueries annotation) {
+		if ( annotation != null ) {
+			for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
+				bindNamedStoredProcedureQuery( mappings, queryAnnotation );
 			}
 		}
 	}
 
+	private static void bindNamedStoredProcedureQuery(Mappings mappings, NamedStoredProcedureQuery annotation) {
+		if ( annotation != null ) {
+			QueryBinder.bindNamedStoredProcedureQuery( annotation, mappings );
+		}
+	}
+
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
 
 		Collection<XProperty> properties = propertyContainer.getProperties( accessType );
 		for ( XProperty p : properties ) {
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 671b0bbb28..83a1949894 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1306 +1,1306 @@
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
 import org.hibernate.cache.spi.GeneralDataRegion;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
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
 	protected Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	protected Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
 
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
 		namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
-
+		namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>(  );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java
index b50173c016..f8a9411c3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/NamedProcedureCallDefinition.java
@@ -1,237 +1,221 @@
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
 package org.hibernate.cfg.annotations;
 
 import javax.persistence.NamedStoredProcedureQuery;
 import javax.persistence.ParameterMode;
-import javax.persistence.QueryHint;
 import javax.persistence.StoredProcedureParameter;
 import java.util.ArrayList;
 import java.util.Collections;
-import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
-import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.internal.ParameterStrategy;
 import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
 import org.hibernate.procedure.internal.Util;
 
 import static org.hibernate.procedure.internal.ProcedureCallMementoImpl.ParameterMemento;
 
 /**
  * Holds all the information needed from a named procedure call declaration in order to create a
  * {@link org.hibernate.procedure.internal.ProcedureCallImpl}
  *
  * @author Steve Ebersole
  *
  * @see javax.persistence.NamedStoredProcedureQuery
  */
 public class NamedProcedureCallDefinition {
 	private final String registeredName;
 	private final String procedureName;
 	private final Class[] resultClasses;
 	private final String[] resultSetMappings;
 	private final ParameterDefinitions parameterDefinitions;
-	private final Map<String,Object> hints;
+	private final Map<String, Object> hints;
 
 	NamedProcedureCallDefinition(NamedStoredProcedureQuery annotation) {
 		this.registeredName = annotation.name();
 		this.procedureName = annotation.procedureName();
 		this.resultClasses = annotation.resultClasses();
 		this.resultSetMappings = annotation.resultSetMappings();
 		this.parameterDefinitions = new ParameterDefinitions( annotation.parameters() );
-		this.hints = extract( annotation.hints() );
+		this.hints = new QueryHintDefinition( annotation.hints() ).getHintsMap();
 
 		final boolean specifiesResultClasses = resultClasses != null && resultClasses.length > 0;
 		final boolean specifiesResultSetMappings = resultSetMappings != null && resultSetMappings.length > 0;
 
 		if ( specifiesResultClasses && specifiesResultSetMappings ) {
 			throw new MappingException(
 					String.format(
 							"NamedStoredProcedureQuery [%s] specified both resultClasses and resultSetMappings",
 							registeredName
 					)
 			);
 		}
 	}
 
-	private Map<String, Object> extract(QueryHint[] hints) {
-		if ( hints == null || hints.length == 0 ) {
-			return Collections.emptyMap();
-		}
-		final Map<String,Object> hintsMap = new HashMap<String, Object>();
-		for ( QueryHint hint : hints ) {
-			hintsMap.put( hint.name(), hint.value() );
-		}
-		return hintsMap;
-	}
-
 	public String getRegisteredName() {
 		return registeredName;
 	}
 
 	public String getProcedureName() {
 		return procedureName;
 	}
 
 	public ProcedureCallMemento toMemento(
 			final SessionFactoryImpl sessionFactory,
 			final Map<String,ResultSetMappingDefinition> resultSetMappingDefinitions) {
 		final List<NativeSQLQueryReturn> collectedQueryReturns = new ArrayList<NativeSQLQueryReturn>();
 		final Set<String> collectedQuerySpaces = new HashSet<String>();
 
 		final boolean specifiesResultClasses = resultClasses != null && resultClasses.length > 0;
 		final boolean specifiesResultSetMappings = resultSetMappings != null && resultSetMappings.length > 0;
 
 		if ( specifiesResultClasses ) {
 			Util.resolveResultClasses(
 					new Util.ResultClassesResolutionContext() {
 						@Override
 						public SessionFactoryImplementor getSessionFactory() {
 							return sessionFactory;
 						}
 
 						@Override
 						public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
 							Collections.addAll( collectedQueryReturns, queryReturns );
 						}
 
 						@Override
 						public void addQuerySpaces(String... spaces) {
 							Collections.addAll( collectedQuerySpaces, spaces );
 						}
 					},
 					resultClasses
 			);
 		}
 		else if ( specifiesResultSetMappings ) {
 			Util.resolveResultSetMappings(
 					new Util.ResultSetMappingResolutionContext() {
 						@Override
 						public SessionFactoryImplementor getSessionFactory() {
 							return sessionFactory;
 						}
 
 						@Override
 						public ResultSetMappingDefinition findResultSetMapping(String name) {
 							return resultSetMappingDefinitions.get( name );
 						}
 
 						@Override
 						public void addQueryReturns(NativeSQLQueryReturn... queryReturns) {
 							Collections.addAll( collectedQueryReturns, queryReturns );
 						}
 
 						@Override
 						public void addQuerySpaces(String... spaces) {
 							Collections.addAll( collectedQuerySpaces, spaces );
 						}
 					},
 					resultSetMappings
 			);
 		}
 
 		return new ProcedureCallMementoImpl(
 				procedureName,
 				collectedQueryReturns.toArray( new NativeSQLQueryReturn[ collectedQueryReturns.size() ] ),
 				parameterDefinitions.getParameterStrategy(),
 				parameterDefinitions.toMementos( sessionFactory ),
 				collectedQuerySpaces,
 				hints
 		);
 	}
 
 	static class ParameterDefinitions {
 		private final ParameterStrategy parameterStrategy;
 		private final ParameterDefinition[] parameterDefinitions;
 
 		ParameterDefinitions(StoredProcedureParameter[] parameters) {
 			if ( parameters == null || parameters.length == 0 ) {
 				parameterStrategy = ParameterStrategy.POSITIONAL;
 				parameterDefinitions = new ParameterDefinition[0];
 			}
 			else {
 				parameterStrategy = StringHelper.isNotEmpty( parameters[0].name() )
 						? ParameterStrategy.NAMED
 						: ParameterStrategy.POSITIONAL;
 				parameterDefinitions = new ParameterDefinition[ parameters.length ];
 				for ( int i = 0; i < parameters.length; i++ ) {
 					parameterDefinitions[i] = new ParameterDefinition( i, parameters[i] );
 				}
 			}
 		}
 
 		public ParameterStrategy getParameterStrategy() {
 			return parameterStrategy;
 		}
 
 		public List<ParameterMemento> toMementos(SessionFactoryImpl sessionFactory) {
 			final List<ParameterMemento> mementos = new ArrayList<ParameterMemento>();
 			for ( ParameterDefinition definition : parameterDefinitions ) {
-				definition.toMemento( sessionFactory );
+				mementos.add(definition.toMemento( sessionFactory ));
 			}
 			return mementos;
 		}
 	}
 
 	static class ParameterDefinition {
 		private final Integer position;
 		private final String name;
 		private final ParameterMode parameterMode;
 		private final Class type;
 
 		ParameterDefinition(int position, StoredProcedureParameter annotation) {
 			this.position = position;
 			this.name = normalize( annotation.name() );
 			this.parameterMode = annotation.mode();
 			this.type = annotation.type();
 		}
 
 		public ParameterMemento toMemento(SessionFactoryImpl sessionFactory) {
 			return new ParameterMemento(
 					position,
 					name,
 					parameterMode,
 					type,
 					sessionFactory.getTypeResolver().heuristicType( type.getName() )
 			);
 		}
 	}
 
 	private static String normalize(String name) {
 		return StringHelper.isNotEmpty( name ) ? name : null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
index 2c21637b04..28f8a0cdd9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
@@ -1,467 +1,349 @@
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
-import javax.persistence.LockModeType;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.NamedStoredProcedureQuery;
-import javax.persistence.QueryHint;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
-import org.hibernate.LockOptions;
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
-import org.hibernate.internal.util.LockModeConverter;
 
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
-		QueryHint[] hints = queryAnn.hints();
+		QueryHintDefinition hints = new QueryHintDefinition( queryAnn.hints() );
 		String queryName = queryAnn.query();
 		NamedQueryDefinition queryDefinition = new NamedQueryDefinitionBuilder( queryAnn.name() )
-				.setLockOptions( determineLockOptions( queryAnn, hints ) )
+				.setLockOptions( hints.determineLockOptions( queryAnn ) )
 				.setQuery( queryName )
-				.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
-				.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
-				.setTimeout( getTimeout( queryName, hints ) )
-				.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
-				.setFlushMode( getFlushMode( queryName, hints ) )
-				.setCacheMode( getCacheMode( queryName, hints ) )
-				.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
-				.setComment( getString( queryName, "org.hibernate.comment", hints ) )
+				.setCacheable( hints.getBoolean( queryName, QueryHints.CACHEABLE ) )
+				.setCacheRegion( hints.getString( queryName, QueryHints.CACHE_REGION ) )
+				.setTimeout( hints.getTimeout( queryName ) )
+				.setFetchSize( hints.getInteger( queryName, QueryHints.FETCH_SIZE ) )
+				.setFlushMode( hints.getFlushMode( queryName ) )
+				.setCacheMode( hints.getCacheMode( queryName ) )
+				.setReadOnly( hints.getBoolean( queryName, QueryHints.READ_ONLY ) )
+				.setComment( hints.getString( queryName, QueryHints.COMMENT ) )
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
 
-	private static LockOptions determineLockOptions(NamedQuery namedQueryAnnotation, QueryHint[] hints) {
-		LockModeType lockModeType = namedQueryAnnotation.lockMode();
-		Integer lockTimeoutHint = getInteger( namedQueryAnnotation.name(), "javax.persistence.lock.timeout", hints );
 
-		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) );
-		if ( lockTimeoutHint != null ) {
-			lockOptions.setTimeOut( lockTimeoutHint );
-		}
-
-		return lockOptions;
-	}
 
 	public static void bindNativeQuery(NamedNativeQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		String resultSetMapping = queryAnn.resultSetMapping();
-		QueryHint[] hints = queryAnn.hints();
+		QueryHintDefinition hints = new QueryHintDefinition( queryAnn.hints() );
 		String queryName = queryAnn.query();
 		
 		NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( queryAnn.name() )
 				.setQuery( queryName )
 				.setQuerySpaces( null )
-				.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
-				.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
-				.setTimeout( getTimeout( queryName, hints ) )
-				.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
-				.setFlushMode( getFlushMode( queryName, hints ) )
-				.setCacheMode( getCacheMode( queryName, hints ) )
-				.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
-				.setComment( getString( queryName, "org.hibernate.comment", hints ) )
+				.setCacheable( hints.getBoolean( queryName, QueryHints.CACHEABLE ) )
+				.setCacheRegion( hints.getString( queryName, QueryHints.CACHE_REGION ) )
+				.setTimeout( hints.getTimeout( queryName ) )
+				.setFetchSize( hints.getInteger( queryName, QueryHints.FETCH_SIZE ) )
+				.setFlushMode( hints.getFlushMode( queryName ) )
+				.setCacheMode( hints.getCacheMode( queryName ) )
+				.setReadOnly( hints.getBoolean( queryName, QueryHints.READ_ONLY ) )
+				.setComment( hints.getString( queryName, QueryHints.COMMENT ) )
 				.setParameterTypes( null )
-				.setCallable( getBoolean( queryName, "org.hibernate.callable", hints ) );
+				.setCallable( hints.getBoolean( queryName, QueryHints.CALLABLE ) );
 		
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
 
 	public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, Mappings mappings) {
 		if ( annotation == null ) {
 			return;
 		}
 
 		if ( BinderHelper.isEmptyAnnotationValue( annotation.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 
 		final NamedProcedureCallDefinition def = new NamedProcedureCallDefinition( annotation );
 		mappings.addNamedProcedureCallDefinition( def );
 		LOG.debugf( "Bound named stored procedure query : %s => %s", def.getRegisteredName(), def.getProcedureName() );
 	}
 
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
 
-	private static CacheMode getCacheMode(String query, QueryHint[] hints) {
-		for (QueryHint hint : hints) {
-			if ( "org.hibernate.cacheMode".equals( hint.name() ) ) {
-				if ( hint.value().equalsIgnoreCase( CacheMode.GET.toString() ) ) {
-					return CacheMode.GET;
-				}
-				else if ( hint.value().equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
-					return CacheMode.IGNORE;
-				}
-				else if ( hint.value().equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
-					return CacheMode.NORMAL;
-				}
-				else if ( hint.value().equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
-					return CacheMode.PUT;
-				}
-				else if ( hint.value().equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
-					return CacheMode.REFRESH;
-				}
-				else {
-					throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hint.name() );
-				}
-			}
-		}
-		return null;
-	}
-
-	private static FlushMode getFlushMode(String query, QueryHint[] hints) {
-		for (QueryHint hint : hints) {
-			if ( "org.hibernate.flushMode".equals( hint.name() ) ) {
-				if ( hint.value().equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
-					return FlushMode.ALWAYS;
-				}
-				else if ( hint.value().equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
-					return FlushMode.AUTO;
-				}
-				else if ( hint.value().equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
-					return FlushMode.COMMIT;
-				}
-				else if ( hint.value().equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
-					return FlushMode.MANUAL;
-				}
-				else if ( hint.value().equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
-					return FlushMode.MANUAL;
-				}
-				else {
-					throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hint.name() );
-				}
-			}
-		}
-		return null;
-	}
-
-	private static boolean getBoolean(String query, String hintName, QueryHint[] hints) {
-		for (QueryHint hint : hints) {
-			if ( hintName.equals( hint.name() ) ) {
-				if ( hint.value().equalsIgnoreCase( "true" ) ) {
-					return true;
-				}
-				else if ( hint.value().equalsIgnoreCase( "false" ) ) {
-					return false;
-				}
-				else {
-					throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hint.name() );
-				}
-			}
-		}
-		return false;
-	}
-
-	private static String getString(String query, String hintName, QueryHint[] hints) {
-		for (QueryHint hint : hints) {
-			if ( hintName.equals( hint.name() ) ) {
-				return hint.value();
-			}
-		}
-		return null;
-	}
-
-	private static Integer getInteger(String query, String hintName, QueryHint[] hints) {
-		for (QueryHint hint : hints) {
-			if ( hintName.equals( hint.name() ) ) {
-				try {
-					return Integer.decode( hint.value() );
-				}
-				catch (NumberFormatException nfe) {
-					throw new AnnotationException( "Not an integer in hint: " + query + ":" + hint.name(), nfe );
-				}
-			}
-		}
-		return null;
-	}
 
-	private static Integer getTimeout(String queryName, QueryHint[] hints) {
-		Integer timeout = getInteger( queryName, "javax.persistence.query.timeout", hints );
-
-		if ( timeout != null ) {
-			// convert milliseconds to seconds
-			timeout = (int)Math.round(timeout.doubleValue() / 1000.0 );
-		}
-		else {
-			// timeout is already in seconds
-			timeout = getInteger( queryName, "org.hibernate.timeout", hints );
-		}
-		return timeout;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java
new file mode 100644
index 0000000000..37ec6ad059
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java
@@ -0,0 +1,152 @@
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
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+import javax.persistence.LockModeType;
+import javax.persistence.NamedQuery;
+import javax.persistence.QueryHint;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockOptions;
+import org.hibernate.MappingException;
+import org.hibernate.annotations.QueryHints;
+import org.hibernate.internal.util.LockModeConverter;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+public class QueryHintDefinition {
+	private final Map<String, Object> hintsMap;
+
+	public QueryHintDefinition(final QueryHint[] hints) {
+		if ( hints == null || hints.length == 0 ) {
+			hintsMap = Collections.emptyMap();
+		}
+		else {
+			final Map<String, Object> hintsMap = new HashMap<String, Object>();
+			for ( QueryHint hint : hints ) {
+				hintsMap.put( hint.name(), hint.value() );
+			}
+			this.hintsMap = hintsMap;
+		}
+	}
+
+
+	public CacheMode getCacheMode(String query) {
+		String hitName = QueryHints.CACHE_MODE;
+		String value =(String) hintsMap.get( hitName );
+		if ( value == null ) {
+			return null;
+		}
+		try {
+			return CacheMode.interpretExternalSetting( value );
+		}
+		catch ( MappingException e ) {
+			throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hitName, e );
+		}
+	}
+
+	public FlushMode getFlushMode(String query) {
+		String hitName = QueryHints.FLUSH_MODE;
+		String value =(String)  hintsMap.get( hitName );
+		if ( value == null ) {
+			return null;
+		}
+		try {
+			return FlushMode.interpretExternalSetting( value );
+		}
+		catch ( MappingException e ) {
+			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hitName, e );
+		}
+	}
+
+	public boolean getBoolean(String query, String hintName) {
+		String value =(String)  hintsMap.get( hintName );
+		if ( value == null ) {
+			return false;
+		}
+		if ( value.equalsIgnoreCase( "true" ) ) {
+			return true;
+		}
+		else if ( value.equalsIgnoreCase( "false" ) ) {
+			return false;
+		}
+		else {
+			throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hintName );
+		}
+
+	}
+
+	public String getString(String query, String hintName) {
+		return (String) hintsMap.get( hintName );
+	}
+
+	public Integer getInteger(String query, String hintName) {
+		String value = (String) hintsMap.get( hintName );
+		if ( value == null ) {
+			return null;
+		}
+		try {
+			return Integer.decode( value );
+		}
+		catch ( NumberFormatException nfe ) {
+			throw new AnnotationException( "Not an integer in hint: " + query + ":" + hintName, nfe );
+		}
+	}
+
+	public Integer getTimeout(String queryName) {
+		Integer timeout = getInteger( queryName, QueryHints.TIMEOUT_JPA );
+
+		if ( timeout != null ) {
+			// convert milliseconds to seconds
+			timeout = (int) Math.round( timeout.doubleValue() / 1000.0 );
+		}
+		else {
+			// timeout is already in seconds
+			timeout = getInteger( queryName, QueryHints.TIMEOUT_HIBERNATE );
+		}
+		return timeout;
+	}
+
+	public LockOptions determineLockOptions(NamedQuery namedQueryAnnotation) {
+		LockModeType lockModeType = namedQueryAnnotation.lockMode();
+		Integer lockTimeoutHint = getInteger( namedQueryAnnotation.name(), "javax.persistence.lock.timeout" );
+
+		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) );
+		if ( lockTimeoutHint != null ) {
+			lockOptions.setTimeOut( lockTimeoutHint );
+		}
+
+		return lockOptions;
+	}
+
+	public Map<String, Object> getHintsMap() {
+		return hintsMap;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
index 9d80eeca41..c15a877eea 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
@@ -1,163 +1,175 @@
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
 package org.hibernate.cfg.annotations.reflection;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.lang.reflect.AnnotatedElement;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.EntityListeners;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQuery;
+import javax.persistence.NamedStoredProcedureQuery;
 import javax.persistence.SequenceGenerator;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.TableGenerator;
 
 import org.dom4j.Element;
 
 import org.hibernate.annotations.common.reflection.AnnotationReader;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.java.JavaMetadataProvider;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * MetadataProvider aware of the JPA Deployment descriptor
  *
  * @author Emmanuel Bernard
  */
+@SuppressWarnings("unchecked")
 public class JPAMetadataProvider implements MetadataProvider, Serializable {
 	private transient MetadataProvider delegate = new JavaMetadataProvider();
 	private transient Map<Object, Object> defaults;
 	private transient Map<AnnotatedElement, AnnotationReader> cache = new HashMap<AnnotatedElement, AnnotationReader>(100);
 
 	//all of the above can be safely rebuilt from XMLContext: only XMLContext this object is serialized
 	private XMLContext xmlContext = new XMLContext();
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		delegate = new JavaMetadataProvider();
 		cache = new HashMap<AnnotatedElement, AnnotationReader>(100);
 	}
 	@Override
 	public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
 		AnnotationReader reader = cache.get( annotatedElement );
 		if (reader == null) {
 			if ( xmlContext.hasContext() ) {
 				reader = new JPAOverriddenAnnotationReader( annotatedElement, xmlContext );
 			}
 			else {
 				reader = delegate.getAnnotationReader( annotatedElement );
 			}
 			cache.put(annotatedElement, reader);
 		}
 		return reader;
 	}
 	@Override
 	public Map<Object, Object> getDefaults() {
 		if ( defaults == null ) {
 			defaults = new HashMap<Object, Object>();
 			XMLContext.Default xmlDefaults = xmlContext.getDefault( null );
 
 			defaults.put( "schema", xmlDefaults.getSchema() );
 			defaults.put( "catalog", xmlDefaults.getCatalog() );
 			defaults.put( "delimited-identifier", xmlDefaults.getDelimitedIdentifier() );
 			List<Class> entityListeners = new ArrayList<Class>();
 			for ( String className : xmlContext.getDefaultEntityListeners() ) {
 				try {
 					entityListeners.add( ReflectHelper.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new IllegalStateException( "Default entity listener class not found: " + className );
 				}
 			}
 			defaults.put( EntityListeners.class, entityListeners );
 			for ( Element element : xmlContext.getAllDocuments() ) {
 				@SuppressWarnings( "unchecked" )
 				List<Element> elements = element.elements( "sequence-generator" );
 				List<SequenceGenerator> sequenceGenerators = ( List<SequenceGenerator> ) defaults.get( SequenceGenerator.class );
 				if ( sequenceGenerators == null ) {
 					sequenceGenerators = new ArrayList<SequenceGenerator>();
 					defaults.put( SequenceGenerator.class, sequenceGenerators );
 				}
 				for ( Element subelement : elements ) {
 					sequenceGenerators.add( JPAOverriddenAnnotationReader.buildSequenceGeneratorAnnotation( subelement ) );
 				}
 
 				elements = element.elements( "table-generator" );
 				List<TableGenerator> tableGenerators = ( List<TableGenerator> ) defaults.get( TableGenerator.class );
 				if ( tableGenerators == null ) {
 					tableGenerators = new ArrayList<TableGenerator>();
 					defaults.put( TableGenerator.class, tableGenerators );
 				}
 				for ( Element subelement : elements ) {
 					tableGenerators.add(
 							JPAOverriddenAnnotationReader.buildTableGeneratorAnnotation(
 									subelement, xmlDefaults
 							)
 					);
 				}
 
 				List<NamedQuery> namedQueries = ( List<NamedQuery> ) defaults.get( NamedQuery.class );
 				if ( namedQueries == null ) {
 					namedQueries = new ArrayList<NamedQuery>();
 					defaults.put( NamedQuery.class, namedQueries );
 				}
 				List<NamedQuery> currentNamedQueries = JPAOverriddenAnnotationReader.buildNamedQueries(
 						element, false, xmlDefaults
 				);
 				namedQueries.addAll( currentNamedQueries );
 
 				List<NamedNativeQuery> namedNativeQueries = ( List<NamedNativeQuery> ) defaults.get( NamedNativeQuery.class );
 				if ( namedNativeQueries == null ) {
 					namedNativeQueries = new ArrayList<NamedNativeQuery>();
 					defaults.put( NamedNativeQuery.class, namedNativeQueries );
 				}
 				List<NamedNativeQuery> currentNamedNativeQueries = JPAOverriddenAnnotationReader.buildNamedQueries(
 						element, true, xmlDefaults
 				);
 				namedNativeQueries.addAll( currentNamedNativeQueries );
 
 				List<SqlResultSetMapping> sqlResultSetMappings = ( List<SqlResultSetMapping> ) defaults.get(
 						SqlResultSetMapping.class
 				);
 				if ( sqlResultSetMappings == null ) {
 					sqlResultSetMappings = new ArrayList<SqlResultSetMapping>();
 					defaults.put( SqlResultSetMapping.class, sqlResultSetMappings );
 				}
 				List<SqlResultSetMapping> currentSqlResultSetMappings = JPAOverriddenAnnotationReader.buildSqlResultsetMappings(
 						element, xmlDefaults
 				);
 				sqlResultSetMappings.addAll( currentSqlResultSetMappings );
+
+				List<NamedStoredProcedureQuery> namedStoredProcedureQueries = (List<NamedStoredProcedureQuery>)defaults.get( NamedStoredProcedureQuery.class );
+				if(namedStoredProcedureQueries==null){
+					namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQuery>(  );
+					defaults.put( NamedStoredProcedureQuery.class, namedStoredProcedureQueries );
+				}
+				List<NamedStoredProcedureQuery> currentNamedStoredProcedureQueries = JPAOverriddenAnnotationReader.buildNamedStoreProcedureQueries(
+						element, xmlDefaults
+				);
+				namedStoredProcedureQueries.addAll( currentNamedStoredProcedureQueries );
 			}
 		}
 		return defaults;
 	}
 
 	public XMLContext getXMLContext() {
 		return xmlContext;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
index 8db6de8449..765dcdae85 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
@@ -1,2513 +1,2598 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011 by Red Hat Inc and/or its affiliates or by
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
 
 package org.hibernate.cfg.annotations.reflection;
 
 import java.beans.Introspector;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.AnnotatedElement;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.Access;
 import javax.persistence.AccessType;
 import javax.persistence.AssociationOverride;
 import javax.persistence.AssociationOverrides;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Basic;
 import javax.persistence.Cacheable;
 import javax.persistence.CascadeType;
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.ColumnResult;
 import javax.persistence.DiscriminatorColumn;
 import javax.persistence.DiscriminatorType;
 import javax.persistence.DiscriminatorValue;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.EntityListeners;
 import javax.persistence.EntityResult;
 import javax.persistence.EnumType;
 import javax.persistence.Enumerated;
 import javax.persistence.ExcludeDefaultListeners;
 import javax.persistence.ExcludeSuperclassListeners;
 import javax.persistence.FetchType;
 import javax.persistence.FieldResult;
 import javax.persistence.ForeignKey;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 import javax.persistence.IdClass;
 import javax.persistence.Index;
 import javax.persistence.Inheritance;
 import javax.persistence.InheritanceType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.Lob;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyClass;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyJoinColumn;
 import javax.persistence.MapKeyJoinColumns;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.MapsId;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
+import javax.persistence.NamedStoredProcedureQuery;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.OrderBy;
 import javax.persistence.OrderColumn;
+import javax.persistence.ParameterMode;
 import javax.persistence.PostLoad;
 import javax.persistence.PostPersist;
 import javax.persistence.PostRemove;
 import javax.persistence.PostUpdate;
 import javax.persistence.PrePersist;
 import javax.persistence.PreRemove;
 import javax.persistence.PreUpdate;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.PrimaryKeyJoinColumns;
 import javax.persistence.QueryHint;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 import javax.persistence.SequenceGenerator;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
+import javax.persistence.StoredProcedureParameter;
 import javax.persistence.Table;
 import javax.persistence.TableGenerator;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import javax.persistence.Transient;
 import javax.persistence.UniqueConstraint;
 import javax.persistence.Version;
 
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.Cascade;
 import org.hibernate.annotations.Columns;
 import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;
 import org.hibernate.annotations.common.annotationfactory.AnnotationFactory;
 import org.hibernate.annotations.common.reflection.AnnotationReader;
 import org.hibernate.annotations.common.reflection.Filter;
 import org.hibernate.annotations.common.reflection.ReflectionUtil;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Encapsulates the overriding of Java annotations from an EJB 3.0 descriptor.
  *
  * @author Paolo Perrotta
  * @author Davide Marchignoli
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class JPAOverriddenAnnotationReader implements AnnotationReader {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        JPAOverriddenAnnotationReader.class.getName());
 	private static final Map<Class, String> annotationToXml;
 	private static final String SCHEMA_VALIDATION = "Activate schema validation for more information";
 	private static final Filter FILTER = new Filter() {
 		public boolean returnStatic() {
 			return false;
 		}
 
 		public boolean returnTransient() {
 			return false;
 		}
 	};
 
 	static {
 		annotationToXml = new HashMap<Class, String>();
 		annotationToXml.put( Entity.class, "entity" );
 		annotationToXml.put( MappedSuperclass.class, "mapped-superclass" );
 		annotationToXml.put( Embeddable.class, "embeddable" );
 		annotationToXml.put( Table.class, "table" );
 		annotationToXml.put( SecondaryTable.class, "secondary-table" );
 		annotationToXml.put( SecondaryTables.class, "secondary-table" );
 		annotationToXml.put( PrimaryKeyJoinColumn.class, "primary-key-join-column" );
 		annotationToXml.put( PrimaryKeyJoinColumns.class, "primary-key-join-column" );
 		annotationToXml.put( IdClass.class, "id-class" );
 		annotationToXml.put( Inheritance.class, "inheritance" );
 		annotationToXml.put( DiscriminatorValue.class, "discriminator-value" );
 		annotationToXml.put( DiscriminatorColumn.class, "discriminator-column" );
 		annotationToXml.put( SequenceGenerator.class, "sequence-generator" );
 		annotationToXml.put( TableGenerator.class, "table-generator" );
 		annotationToXml.put( NamedQuery.class, "named-query" );
 		annotationToXml.put( NamedQueries.class, "named-query" );
 		annotationToXml.put( NamedNativeQuery.class, "named-native-query" );
 		annotationToXml.put( NamedNativeQueries.class, "named-native-query" );
 		annotationToXml.put( SqlResultSetMapping.class, "sql-result-set-mapping" );
 		annotationToXml.put( SqlResultSetMappings.class, "sql-result-set-mapping" );
 		annotationToXml.put( ExcludeDefaultListeners.class, "exclude-default-listeners" );
 		annotationToXml.put( ExcludeSuperclassListeners.class, "exclude-superclass-listeners" );
 		annotationToXml.put( AccessType.class, "access" );
 		annotationToXml.put( AttributeOverride.class, "attribute-override" );
 		annotationToXml.put( AttributeOverrides.class, "attribute-override" );
 		annotationToXml.put( AttributeOverride.class, "association-override" );
 		annotationToXml.put( AttributeOverrides.class, "association-override" );
 		annotationToXml.put( AttributeOverride.class, "map-key-attribute-override" );
 		annotationToXml.put( AttributeOverrides.class, "map-key-attribute-override" );
 		annotationToXml.put( Id.class, "id" );
 		annotationToXml.put( EmbeddedId.class, "embedded-id" );
 		annotationToXml.put( GeneratedValue.class, "generated-value" );
 		annotationToXml.put( Column.class, "column" );
 		annotationToXml.put( Columns.class, "column" );
 		annotationToXml.put( Temporal.class, "temporal" );
 		annotationToXml.put( Lob.class, "lob" );
 		annotationToXml.put( Enumerated.class, "enumerated" );
 		annotationToXml.put( Version.class, "version" );
 		annotationToXml.put( Transient.class, "transient" );
 		annotationToXml.put( Basic.class, "basic" );
 		annotationToXml.put( Embedded.class, "embedded" );
 		annotationToXml.put( ManyToOne.class, "many-to-one" );
 		annotationToXml.put( OneToOne.class, "one-to-one" );
 		annotationToXml.put( OneToMany.class, "one-to-many" );
 		annotationToXml.put( ManyToMany.class, "many-to-many" );
 		annotationToXml.put( JoinTable.class, "join-table" );
 		annotationToXml.put( JoinColumn.class, "join-column" );
 		annotationToXml.put( JoinColumns.class, "join-column" );
 		annotationToXml.put( MapKey.class, "map-key" );
 		annotationToXml.put( OrderBy.class, "order-by" );
 		annotationToXml.put( EntityListeners.class, "entity-listeners" );
 		annotationToXml.put( PrePersist.class, "pre-persist" );
 		annotationToXml.put( PreRemove.class, "pre-remove" );
 		annotationToXml.put( PreUpdate.class, "pre-update" );
 		annotationToXml.put( PostPersist.class, "post-persist" );
 		annotationToXml.put( PostRemove.class, "post-remove" );
 		annotationToXml.put( PostUpdate.class, "post-update" );
 		annotationToXml.put( PostLoad.class, "post-load" );
 		annotationToXml.put( CollectionTable.class, "collection-table" );
 		annotationToXml.put( MapKeyClass.class, "map-key-class" );
 		annotationToXml.put( MapKeyTemporal.class, "map-key-temporal" );
 		annotationToXml.put( MapKeyEnumerated.class, "map-key-enumerated" );
 		annotationToXml.put( MapKeyColumn.class, "map-key-column" );
 		annotationToXml.put( MapKeyJoinColumn.class, "map-key-join-column" );
 		annotationToXml.put( MapKeyJoinColumns.class, "map-key-join-column" );
 		annotationToXml.put( OrderColumn.class, "order-column" );
 		annotationToXml.put( Cacheable.class, "cacheable" );
 		annotationToXml.put( Index.class, "index" );
 		annotationToXml.put( ForeignKey.class, "foreign-key" );
 	}
 
 	private XMLContext xmlContext;
 	private String className;
 	private String propertyName;
 	private PropertyType propertyType;
 	private transient Annotation[] annotations;
 	private transient Map<Class, Annotation> annotationsMap;
 	private static final String WORD_SEPARATOR = "-";
 	private transient List<Element> elementsForProperty;
 	private AccessibleObject mirroredAttribute;
 	private final AnnotatedElement element;
 
 	private enum PropertyType {
 		PROPERTY,
 		FIELD,
 		METHOD
 	}
 
 	public JPAOverriddenAnnotationReader(AnnotatedElement el, XMLContext xmlContext) {
 		this.element = el;
 		this.xmlContext = xmlContext;
 		if ( el instanceof Class ) {
 			Class clazz = (Class) el;
 			className = clazz.getName();
 		}
 		else if ( el instanceof Field ) {
 			Field field = (Field) el;
 			className = field.getDeclaringClass().getName();
 			propertyName = field.getName();
 			propertyType = PropertyType.FIELD;
 			String expectedGetter = "get" + Character.toUpperCase( propertyName.charAt( 0 ) ) + propertyName.substring(
 					1
 			);
 			try {
 				mirroredAttribute = field.getDeclaringClass().getDeclaredMethod( expectedGetter );
 			}
 			catch ( NoSuchMethodException e ) {
 				//no method
 			}
 		}
 		else if ( el instanceof Method ) {
 			Method method = (Method) el;
 			className = method.getDeclaringClass().getName();
 			propertyName = method.getName();
 			if ( ReflectionUtil.isProperty(
 					method,
 					null, //this is yukky!! we'd rather get the TypeEnvironment()
 					FILTER
 			) ) {
 				if ( propertyName.startsWith( "get" ) ) {
 					propertyName = Introspector.decapitalize( propertyName.substring( "get".length() ) );
 				}
 				else if ( propertyName.startsWith( "is" ) ) {
 					propertyName = Introspector.decapitalize( propertyName.substring( "is".length() ) );
 				}
 				else {
 					throw new RuntimeException( "Method " + propertyName + " is not a property getter" );
 				}
 				propertyType = PropertyType.PROPERTY;
 				try {
 					mirroredAttribute = method.getDeclaringClass().getDeclaredField( propertyName );
 				}
 				catch ( NoSuchFieldException e ) {
 					//no method
 				}
 			}
 			else {
 				propertyType = PropertyType.METHOD;
 			}
 		}
 		else {
 			className = null;
 			propertyName = null;
 		}
 	}
 
 	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
 		initAnnotations();
 		return (T) annotationsMap.get( annotationType );
 	}
 
 	public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
 		initAnnotations();
 		return annotationsMap.containsKey( annotationType );
 	}
 
 	public Annotation[] getAnnotations() {
 		initAnnotations();
 		return annotations;
 	}
 
 	/*
 	 * The idea is to create annotation proxies for the xml configuration elements. Using this proxy annotations together
 	 * with the {@code JPAMetadataprovider} allows to handle xml configuration the same way as annotation configuration.
 	 */
 	private void initAnnotations() {
 		if ( annotations == null ) {
 			XMLContext.Default defaults = xmlContext.getDefault( className );
 			if ( className != null && propertyName == null ) {
 				//is a class
 				Element tree = xmlContext.getXMLTree( className );
 				Annotation[] annotations = getJavaAnnotations();
 				List<Annotation> annotationList = new ArrayList<Annotation>( annotations.length + 5 );
 				annotationsMap = new HashMap<Class, Annotation>( annotations.length + 5 );
 				for ( Annotation annotation : annotations ) {
 					if ( !annotationToXml.containsKey( annotation.annotationType() ) ) {
 						//unknown annotations are left over
 						annotationList.add( annotation );
 					}
 				}
 				addIfNotNull( annotationList, getEntity( tree, defaults ) );
 				addIfNotNull( annotationList, getMappedSuperclass( tree, defaults ) );
 				addIfNotNull( annotationList, getEmbeddable( tree, defaults ) );
 				addIfNotNull( annotationList, getTable( tree, defaults ) );
 				addIfNotNull( annotationList, getSecondaryTables( tree, defaults ) );
 				addIfNotNull( annotationList, getPrimaryKeyJoinColumns( tree, defaults, true ) );
 				addIfNotNull( annotationList, getIdClass( tree, defaults ) );
 				addIfNotNull( annotationList, getCacheable( tree, defaults ) );
 				addIfNotNull( annotationList, getInheritance( tree, defaults ) );
 				addIfNotNull( annotationList, getDiscriminatorValue( tree, defaults ) );
 				addIfNotNull( annotationList, getDiscriminatorColumn( tree, defaults ) );
 				addIfNotNull( annotationList, getSequenceGenerator( tree, defaults ) );
 				addIfNotNull( annotationList, getTableGenerator( tree, defaults ) );
 				addIfNotNull( annotationList, getNamedQueries( tree, defaults ) );
 				addIfNotNull( annotationList, getNamedNativeQueries( tree, defaults ) );
 				addIfNotNull( annotationList, getSqlResultSetMappings( tree, defaults ) );
 				addIfNotNull( annotationList, getExcludeDefaultListeners( tree, defaults ) );
 				addIfNotNull( annotationList, getExcludeSuperclassListeners( tree, defaults ) );
 				addIfNotNull( annotationList, getAccessType( tree, defaults ) );
 				addIfNotNull( annotationList, getAttributeOverrides( tree, defaults, true ) );
 				addIfNotNull( annotationList, getAssociationOverrides( tree, defaults, true ) );
 				addIfNotNull( annotationList, getEntityListeners( tree, defaults ) );
 				this.annotations = annotationList.toArray( new Annotation[annotationList.size()] );
 				for ( Annotation ann : this.annotations ) {
 					annotationsMap.put( ann.annotationType(), ann );
 				}
 				checkForOrphanProperties( tree );
 			}
 			else if ( className != null ) { //&& propertyName != null ) { //always true but less confusing
 				Element tree = xmlContext.getXMLTree( className );
 				Annotation[] annotations = getJavaAnnotations();
 				List<Annotation> annotationList = new ArrayList<Annotation>( annotations.length + 5 );
 				annotationsMap = new HashMap<Class, Annotation>( annotations.length + 5 );
 				for ( Annotation annotation : annotations ) {
 					if ( !annotationToXml.containsKey( annotation.annotationType() ) ) {
 						//unknown annotations are left over
 						annotationList.add( annotation );
 					}
 				}
 				preCalculateElementsForProperty( tree );
 				Transient transientAnn = getTransient( defaults );
 				if ( transientAnn != null ) {
 					annotationList.add( transientAnn );
 				}
 				else {
 					if ( defaults.canUseJavaAnnotations() ) {
 						Annotation annotation = getJavaAnnotation( Access.class );
 						addIfNotNull( annotationList, annotation );
 					}
 					getId( annotationList, defaults );
 					getEmbeddedId( annotationList, defaults );
 					getEmbedded( annotationList, defaults );
 					getBasic( annotationList, defaults );
 					getVersion( annotationList, defaults );
 					getAssociation( ManyToOne.class, annotationList, defaults );
 					getAssociation( OneToOne.class, annotationList, defaults );
 					getAssociation( OneToMany.class, annotationList, defaults );
 					getAssociation( ManyToMany.class, annotationList, defaults );
 					getElementCollection( annotationList, defaults );
 					addIfNotNull( annotationList, getSequenceGenerator( elementsForProperty, defaults ) );
 					addIfNotNull( annotationList, getTableGenerator( elementsForProperty, defaults ) );
 				}
 				processEventAnnotations( annotationList, defaults );
 				//FIXME use annotationsMap rather than annotationList this will be faster since the annotation type is usually known at put() time
 				this.annotations = annotationList.toArray( new Annotation[annotationList.size()] );
 				for ( Annotation ann : this.annotations ) {
 					annotationsMap.put( ann.annotationType(), ann );
 				}
 			}
 			else {
 				this.annotations = getJavaAnnotations();
 				annotationsMap = new HashMap<Class, Annotation>( annotations.length + 5 );
 				for ( Annotation ann : this.annotations ) {
 					annotationsMap.put( ann.annotationType(), ann );
 				}
 			}
 		}
 	}
 
 	private void checkForOrphanProperties(Element tree) {
 		Class clazz;
 		try {
 			clazz = ReflectHelper.classForName( className, this.getClass() );
 		}
 		catch ( ClassNotFoundException e ) {
 			return; //a primitive type most likely
 		}
 		Element element = tree != null ? tree.element( "attributes" ) : null;
 		//put entity.attributes elements
 		if ( element != null ) {
 			//precompute the list of properties
 			//TODO is it really useful...
 			Set<String> properties = new HashSet<String>();
 			for ( Field field : clazz.getFields() ) {
 				properties.add( field.getName() );
 			}
 			for ( Method method : clazz.getMethods() ) {
 				String name = method.getName();
 				if ( name.startsWith( "get" ) ) {
 					properties.add( Introspector.decapitalize( name.substring( "get".length() ) ) );
 				}
 				else if ( name.startsWith( "is" ) ) {
 					properties.add( Introspector.decapitalize( name.substring( "is".length() ) ) );
 				}
 			}
 			for ( Element subelement : (List<Element>) element.elements() ) {
 				String propertyName = subelement.attributeValue( "name" );
 				if ( !properties.contains( propertyName ) ) {
 					LOG.propertyNotFound( StringHelper.qualify( className, propertyName ) );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Adds {@code annotation} to the list (only if it's not null) and then returns it.
 	 *
 	 * @param annotationList The list of annotations.
 	 * @param annotation The annotation to add to the list.
 	 *
 	 * @return The annotation which was added to the list or {@code null}.
 	 */
 	private Annotation addIfNotNull(List<Annotation> annotationList, Annotation annotation) {
 		if ( annotation != null ) {
 			annotationList.add( annotation );
 		}
 		return annotation;
 	}
 
 	//TODO mutualize the next 2 methods
 	private Annotation getTableGenerator(List<Element> elementsForProperty, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			Element subelement = element != null ? element.element( annotationToXml.get( TableGenerator.class ) ) : null;
 			if ( subelement != null ) {
 				return buildTableGeneratorAnnotation( subelement, defaults );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( TableGenerator.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private Annotation getSequenceGenerator(List<Element> elementsForProperty, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			Element subelement = element != null ? element.element( annotationToXml.get( SequenceGenerator.class ) ) : null;
 			if ( subelement != null ) {
 				return buildSequenceGeneratorAnnotation( subelement );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( SequenceGenerator.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void processEventAnnotations(List<Annotation> annotationList, XMLContext.Default defaults) {
 		boolean eventElement = false;
 		for ( Element element : elementsForProperty ) {
 			String elementName = element.getName();
 			if ( "pre-persist".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PrePersist.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "pre-remove".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PreRemove.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "pre-update".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PreUpdate.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "post-persist".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PostPersist.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "post-remove".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PostRemove.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "post-update".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PostUpdate.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 			else if ( "post-load".equals( elementName ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( PostLoad.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				eventElement = true;
 			}
 		}
 		if ( !eventElement && defaults.canUseJavaAnnotations() ) {
 			Annotation ann = getJavaAnnotation( PrePersist.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PreRemove.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PreUpdate.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PostPersist.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PostRemove.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PostUpdate.class );
 			addIfNotNull( annotationList, ann );
 			ann = getJavaAnnotation( PostLoad.class );
 			addIfNotNull( annotationList, ann );
 		}
 	}
 
 	private EntityListeners getEntityListeners(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( "entity-listeners" ) : null;
 		if ( element != null ) {
 			List<Class> entityListenerClasses = new ArrayList<Class>();
 			for ( Element subelement : (List<Element>) element.elements( "entity-listener" ) ) {
 				String className = subelement.attributeValue( "class" );
 				try {
 					entityListenerClasses.add(
 							ReflectHelper.classForName(
 									XMLContext.buildSafeClassName( className, defaults ),
 									this.getClass()
 							)
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException(
 							"Unable to find " + element.getPath() + ".class: " + className, e
 					);
 				}
 			}
 			AnnotationDescriptor ad = new AnnotationDescriptor( EntityListeners.class );
 			ad.setValue( "value", entityListenerClasses.toArray( new Class[entityListenerClasses.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( EntityListeners.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private JoinTable overridesDefaultsInJoinTable(Annotation annotation, XMLContext.Default defaults) {
 		//no element but might have some default or some annotation
 		boolean defaultToJoinTable = !( isJavaAnnotationPresent( JoinColumn.class )
 				|| isJavaAnnotationPresent( JoinColumns.class ) );
 		final Class<? extends Annotation> annotationClass = annotation.annotationType();
 		defaultToJoinTable = defaultToJoinTable &&
 				( ( annotationClass == ManyToMany.class && StringHelper.isEmpty( ( (ManyToMany) annotation ).mappedBy() ) )
 						|| ( annotationClass == OneToMany.class && StringHelper.isEmpty( ( (OneToMany) annotation ).mappedBy() ) )
 						|| ( annotationClass == ElementCollection.class )
 				);
 		final Class<JoinTable> annotationType = JoinTable.class;
 		if ( defaultToJoinTable
 				&& ( StringHelper.isNotEmpty( defaults.getCatalog() )
 				|| StringHelper.isNotEmpty( defaults.getSchema() ) ) ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( annotationType );
 			if ( defaults.canUseJavaAnnotations() ) {
 				JoinTable table = getJavaAnnotation( annotationType );
 				if ( table != null ) {
 					ad.setValue( "name", table.name() );
 					ad.setValue( "schema", table.schema() );
 					ad.setValue( "catalog", table.catalog() );
 					ad.setValue( "uniqueConstraints", table.uniqueConstraints() );
 					ad.setValue( "joinColumns", table.joinColumns() );
 					ad.setValue( "inverseJoinColumns", table.inverseJoinColumns() );
 				}
 			}
 			if ( StringHelper.isEmpty( (String) ad.valueOf( "schema" ) )
 					&& StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 				ad.setValue( "schema", defaults.getSchema() );
 			}
 			if ( StringHelper.isEmpty( (String) ad.valueOf( "catalog" ) )
 					&& StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 				ad.setValue( "catalog", defaults.getCatalog() );
 			}
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( annotationType );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void getJoinTable(List<Annotation> annotationList, Element tree, XMLContext.Default defaults) {
 		addIfNotNull( annotationList, buildJoinTable( tree, defaults ) );
 	}
 
 	/*
 	 * no partial overriding possible
 	 */
 	private JoinTable buildJoinTable(Element tree, XMLContext.Default defaults) {
 		Element subelement = tree == null ? null : tree.element( "join-table" );
 		final Class<JoinTable> annotationType = JoinTable.class;
 		if ( subelement == null ) {
 			return null;
 		}
 		//ignore java annotation, an element is defined
 		AnnotationDescriptor annotation = new AnnotationDescriptor( annotationType );
 		copyStringAttribute( annotation, subelement, "name", false );
 		copyStringAttribute( annotation, subelement, "catalog", false );
 		if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 				&& StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) ) ) {
 			annotation.setValue( "catalog", defaults.getCatalog() );
 		}
 		copyStringAttribute( annotation, subelement, "schema", false );
 		if ( StringHelper.isNotEmpty( defaults.getSchema() )
 				&& StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) ) ) {
 			annotation.setValue( "schema", defaults.getSchema() );
 		}
 		buildUniqueConstraints( annotation, subelement );
 		buildIndex( annotation, subelement );
 		annotation.setValue( "joinColumns", getJoinColumns( subelement, false ) );
 		annotation.setValue( "inverseJoinColumns", getJoinColumns( subelement, true ) );
 		return AnnotationFactory.create( annotation );
 	}
 
 	/**
 	 * As per section 12.2 of the JPA 2.0 specification, the association
 	 * subelements (many-to-one, one-to-many, one-to-one, many-to-many,
 	 * element-collection) completely override the mapping for the specified
 	 * field or property.  Thus, any methods which might in some contexts merge
 	 * with annotations must not do so in this context.
 	 *
 	 * @see #getElementCollection(List, org.hibernate.cfg.annotations.reflection.XMLContext.Default)
 	 */
 	private void getAssociation(
 			Class<? extends Annotation> annotationType, List<Annotation> annotationList, XMLContext.Default defaults
 	) {
 		String xmlName = annotationToXml.get( annotationType );
 		for ( Element element : elementsForProperty ) {
 			if ( xmlName.equals( element.getName() ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( annotationType );
 				addTargetClass( element, ad, "target-entity", defaults );
 				getFetchType( ad, element );
 				getCascades( ad, element, defaults );
 				getJoinTable( annotationList, element, defaults );
 				buildJoinColumns( annotationList, element );
 				Annotation annotation = getPrimaryKeyJoinColumns( element, defaults, false );
 				addIfNotNull( annotationList, annotation );
 				copyBooleanAttribute( ad, element, "optional" );
 				copyBooleanAttribute( ad, element, "orphan-removal" );
 				copyStringAttribute( ad, element, "mapped-by", false );
 				getOrderBy( annotationList, element );
 				getMapKey( annotationList, element );
 				getMapKeyClass( annotationList, element, defaults );
 				getMapKeyColumn( annotationList, element );
 				getOrderColumn( annotationList, element );
 				getMapKeyTemporal( annotationList, element );
 				getMapKeyEnumerated( annotationList, element );
 				annotation = getMapKeyAttributeOverrides( element, defaults );
 				addIfNotNull( annotationList, annotation );
 				buildMapKeyJoinColumns( annotationList, element );
 				getAssociationId( annotationList, element );
 				getMapsId( annotationList, element );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				getAccessType( annotationList, element );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			Annotation annotation = getJavaAnnotation( annotationType );
 			if ( annotation != null ) {
 				annotationList.add( annotation );
 				annotation = overridesDefaultsInJoinTable( annotation, defaults );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( JoinColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( JoinColumns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( PrimaryKeyJoinColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( PrimaryKeyJoinColumns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKey.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( OrderBy.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Lob.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Enumerated.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Temporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Column.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Columns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyClass.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyTemporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyEnumerated.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyJoinColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyJoinColumns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( OrderColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Cascade.class );
 				addIfNotNull( annotationList, annotation );
 			}
 			else if ( isJavaAnnotationPresent( ElementCollection.class ) ) { //JPA2
 				annotation = overridesDefaultsInJoinTable( getJavaAnnotation( ElementCollection.class ), defaults );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKey.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( OrderBy.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Lob.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Enumerated.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Temporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Column.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( OrderColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyClass.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyTemporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyEnumerated.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyJoinColumn.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( MapKeyJoinColumns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( CollectionTable.class );
 				addIfNotNull( annotationList, annotation );
 			}
 		}
 	}
 
 	private void buildMapKeyJoinColumns(List<Annotation> annotationList, Element element) {
 		MapKeyJoinColumn[] joinColumns = getMapKeyJoinColumns( element );
 		if ( joinColumns.length > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKeyJoinColumns.class );
 			ad.setValue( "value", joinColumns );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private MapKeyJoinColumn[] getMapKeyJoinColumns(Element element) {
 		List<Element> subelements = element != null ? element.elements( "map-key-join-column" ) : null;
 		List<MapKeyJoinColumn> joinColumns = new ArrayList<MapKeyJoinColumn>();
 		if ( subelements != null ) {
 			for ( Element subelement : subelements ) {
 				AnnotationDescriptor column = new AnnotationDescriptor( MapKeyJoinColumn.class );
 				copyStringAttribute( column, subelement, "name", false );
 				copyStringAttribute( column, subelement, "referenced-column-name", false );
 				copyBooleanAttribute( column, subelement, "unique" );
 				copyBooleanAttribute( column, subelement, "nullable" );
 				copyBooleanAttribute( column, subelement, "insertable" );
 				copyBooleanAttribute( column, subelement, "updatable" );
 				copyStringAttribute( column, subelement, "column-definition", false );
 				copyStringAttribute( column, subelement, "table", false );
 				joinColumns.add( (MapKeyJoinColumn) AnnotationFactory.create( column ) );
 			}
 		}
 		return joinColumns.toArray( new MapKeyJoinColumn[joinColumns.size()] );
 	}
 
 	private AttributeOverrides getMapKeyAttributeOverrides(Element tree, XMLContext.Default defaults) {
 		List<AttributeOverride> attributes = buildAttributeOverrides( tree, "map-key-attribute-override" );
 		return mergeAttributeOverrides( defaults, attributes, false );
 	}
 
 	private Cacheable getCacheable(Element element, XMLContext.Default defaults){
 		if ( element != null ) {
 			String attValue = element.attributeValue( "cacheable" );
 			if ( attValue != null ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( Cacheable.class );
 				ad.setValue( "value", Boolean.valueOf( attValue ) );
 				return AnnotationFactory.create( ad );
 			}
 		}
 		if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( Cacheable.class );
 		}
 		else {
 			return null;
 		}
 	}
 	/**
 	 * Adds a @MapKeyEnumerated annotation to the specified annotationList if the specified element
 	 * contains a map-key-enumerated sub-element. This should only be the case for
 	 * element-collection, many-to-many, or one-to-many associations.
 	 */
 	private void getMapKeyEnumerated(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "map-key-enumerated" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKeyEnumerated.class );
 			EnumType value = EnumType.valueOf( subelement.getTextTrim() );
 			ad.setValue( "value", value );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	/**
 	 * Adds a @MapKeyTemporal annotation to the specified annotationList if the specified element
 	 * contains a map-key-temporal sub-element. This should only be the case for element-collection,
 	 * many-to-many, or one-to-many associations.
 	 */
 	private void getMapKeyTemporal(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "map-key-temporal" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKeyTemporal.class );
 			TemporalType value = TemporalType.valueOf( subelement.getTextTrim() );
 			ad.setValue( "value", value );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	/**
 	 * Adds an @OrderColumn annotation to the specified annotationList if the specified element
 	 * contains an order-column sub-element. This should only be the case for element-collection,
 	 * many-to-many, or one-to-many associations.
 	 */
 	private void getOrderColumn(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "order-column" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( OrderColumn.class );
 			copyStringAttribute( ad, subelement, "name", false );
 			copyBooleanAttribute( ad, subelement, "nullable" );
 			copyBooleanAttribute( ad, subelement, "insertable" );
 			copyBooleanAttribute( ad, subelement, "updatable" );
 			copyStringAttribute( ad, subelement, "column-definition", false );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	/**
 	 * Adds a @MapsId annotation to the specified annotationList if the specified element has the
 	 * maps-id attribute set. This should only be the case for many-to-one or one-to-one
 	 * associations.
 	 */
 	private void getMapsId(List<Annotation> annotationList, Element element) {
 		String attrVal = element.attributeValue( "maps-id" );
 		if ( attrVal != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapsId.class );
 			ad.setValue( "value", attrVal );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	/**
 	 * Adds an @Id annotation to the specified annotationList if the specified element has the id
 	 * attribute set to true. This should only be the case for many-to-one or one-to-one
 	 * associations.
 	 */
 	private void getAssociationId(List<Annotation> annotationList, Element element) {
 		String attrVal = element.attributeValue( "id" );
 		if ( "true".equals( attrVal ) ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Id.class );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void addTargetClass(Element element, AnnotationDescriptor ad, String nodeName, XMLContext.Default defaults) {
 		String className = element.attributeValue( nodeName );
 		if ( className != null ) {
 			Class clazz;
 			try {
 				clazz = ReflectHelper.classForName(
 						XMLContext.buildSafeClassName( className, defaults ), this.getClass()
 				);
 			}
 			catch ( ClassNotFoundException e ) {
 				throw new AnnotationException(
 						"Unable to find " + element.getPath() + " " + nodeName + ": " + className, e
 				);
 			}
 			ad.setValue( getJavaAttributeNameFromXMLOne( nodeName ), clazz );
 		}
 	}
 
 	/**
 	 * As per sections 12.2.3.23.9, 12.2.4.8.9 and 12.2.5.3.6 of the JPA 2.0
 	 * specification, the element-collection subelement completely overrides the
 	 * mapping for the specified field or property.  Thus, any methods which
 	 * might in some contexts merge with annotations must not do so in this
 	 * context.
 	 */
 	private void getElementCollection(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "element-collection".equals( element.getName() ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( ElementCollection.class );
 				addTargetClass( element, ad, "target-class", defaults );
 				getFetchType( ad, element );
 				getOrderBy( annotationList, element );
 				getOrderColumn( annotationList, element );
 				getMapKey( annotationList, element );
 				getMapKeyClass( annotationList, element, defaults );
 				getMapKeyTemporal( annotationList, element );
 				getMapKeyEnumerated( annotationList, element );
 				getMapKeyColumn( annotationList, element );
 				buildMapKeyJoinColumns( annotationList, element );
 				Annotation annotation = getColumn( element.element( "column" ), false, element );
 				addIfNotNull( annotationList, annotation );
 				getTemporal( annotationList, element );
 				getEnumerated( annotationList, element );
 				getLob( annotationList, element );
 				//Both map-key-attribute-overrides and attribute-overrides
 				//translate into AttributeOverride annotations, which need
 				//need to be wrapped in the same AttributeOverrides annotation.
 				List<AttributeOverride> attributes = new ArrayList<AttributeOverride>();
 				attributes.addAll( buildAttributeOverrides( element, "map-key-attribute-override" ) );
 				attributes.addAll( buildAttributeOverrides( element, "attribute-override" ) );
 				annotation = mergeAttributeOverrides( defaults, attributes, false );
 				addIfNotNull( annotationList, annotation );
 				annotation = getAssociationOverrides( element, defaults, false );
 				addIfNotNull( annotationList, annotation );
 				getCollectionTable( annotationList, element, defaults );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				getAccessType( annotationList, element );
 			}
 		}
 	}
 
 	private void getOrderBy(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "order-by" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( OrderBy.class );
 			copyStringElement( subelement, ad, "value" );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getMapKey(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "map-key" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKey.class );
 			copyStringAttribute( ad, subelement, "name", false );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getMapKeyColumn(List<Annotation> annotationList, Element element) {
 		Element subelement = element != null ? element.element( "map-key-column" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKeyColumn.class );
 			copyStringAttribute( ad, subelement, "name", false );
 			copyBooleanAttribute( ad, subelement, "unique" );
 			copyBooleanAttribute( ad, subelement, "nullable" );
 			copyBooleanAttribute( ad, subelement, "insertable" );
 			copyBooleanAttribute( ad, subelement, "updatable" );
 			copyStringAttribute( ad, subelement, "column-definition", false );
 			copyStringAttribute( ad, subelement, "table", false );
 			copyIntegerAttribute( ad, subelement, "length" );
 			copyIntegerAttribute( ad, subelement, "precision" );
 			copyIntegerAttribute( ad, subelement, "scale" );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getMapKeyClass(List<Annotation> annotationList, Element element, XMLContext.Default defaults) {
 		String nodeName = "map-key-class";
 		Element subelement = element != null ? element.element( nodeName ) : null;
 		if ( subelement != null ) {
 			String mapKeyClassName = subelement.attributeValue( "class" );
 			AnnotationDescriptor ad = new AnnotationDescriptor( MapKeyClass.class );
 			if ( StringHelper.isNotEmpty( mapKeyClassName ) ) {
 				Class clazz;
 				try {
 					clazz = ReflectHelper.classForName(
 							XMLContext.buildSafeClassName( mapKeyClassName, defaults ),
 							this.getClass()
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException(
 							"Unable to find " + element.getPath() + " " + nodeName + ": " + mapKeyClassName, e
 					);
 				}
 				ad.setValue( "value", clazz );
 			}
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getCollectionTable(List<Annotation> annotationList, Element element, XMLContext.Default defaults) {
 		Element subelement = element != null ? element.element( "collection-table" ) : null;
 		if ( subelement != null ) {
 			AnnotationDescriptor annotation = new AnnotationDescriptor( CollectionTable.class );
 			copyStringAttribute( annotation, subelement, "name", false );
 			copyStringAttribute( annotation, subelement, "catalog", false );
 			if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) ) ) {
 				annotation.setValue( "catalog", defaults.getCatalog() );
 			}
 			copyStringAttribute( annotation, subelement, "schema", false );
 			if ( StringHelper.isNotEmpty( defaults.getSchema() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) ) ) {
 				annotation.setValue( "schema", defaults.getSchema() );
 			}
 			JoinColumn[] joinColumns = getJoinColumns( subelement, false );
 			if ( joinColumns.length > 0 ) {
 				annotation.setValue( "joinColumns", joinColumns );
 			}
 			buildUniqueConstraints( annotation, subelement );
 			buildIndex( annotation, subelement );
 			annotationList.add( AnnotationFactory.create( annotation ) );
 		}
 	}
 
 	private void buildJoinColumns(List<Annotation> annotationList, Element element) {
 		JoinColumn[] joinColumns = getJoinColumns( element, false );
 		if ( joinColumns.length > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( JoinColumns.class );
 			ad.setValue( "value", joinColumns );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getCascades(AnnotationDescriptor ad, Element element, XMLContext.Default defaults) {
 		List<Element> elements = element != null ? element.elements( "cascade" ) : new ArrayList<Element>( 0 );
 		List<CascadeType> cascades = new ArrayList<CascadeType>();
 		for ( Element subelement : elements ) {
 			if ( subelement.element( "cascade-all" ) != null ) {
 				cascades.add( CascadeType.ALL );
 			}
 			if ( subelement.element( "cascade-persist" ) != null ) {
 				cascades.add( CascadeType.PERSIST );
 			}
 			if ( subelement.element( "cascade-merge" ) != null ) {
 				cascades.add( CascadeType.MERGE );
 			}
 			if ( subelement.element( "cascade-remove" ) != null ) {
 				cascades.add( CascadeType.REMOVE );
 			}
 			if ( subelement.element( "cascade-refresh" ) != null ) {
 				cascades.add( CascadeType.REFRESH );
 			}
 			if ( subelement.element( "cascade-detach" ) != null ) {
 				cascades.add( CascadeType.DETACH );
 			}
 		}
 		if ( Boolean.TRUE.equals( defaults.getCascadePersist() )
 				&& !cascades.contains( CascadeType.ALL ) && !cascades.contains( CascadeType.PERSIST ) ) {
 			cascades.add( CascadeType.PERSIST );
 		}
 		if ( cascades.size() > 0 ) {
 			ad.setValue( "cascade", cascades.toArray( new CascadeType[cascades.size()] ) );
 		}
 	}
 
 	private void getEmbedded(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "embedded".equals( element.getName() ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( Embedded.class );
 				annotationList.add( AnnotationFactory.create( ad ) );
 				Annotation annotation = getAttributeOverrides( element, defaults, false );
 				addIfNotNull( annotationList, annotation );
 				annotation = getAssociationOverrides( element, defaults, false );
 				addIfNotNull( annotationList, annotation );
 				getAccessType( annotationList, element );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			Annotation annotation = getJavaAnnotation( Embedded.class );
 			if ( annotation != null ) {
 				annotationList.add( annotation );
 				annotation = getJavaAnnotation( AttributeOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverrides.class );
 				addIfNotNull( annotationList, annotation );
 			}
 		}
 	}
 
 	private Transient getTransient(XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "transient".equals( element.getName() ) ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( Transient.class );
 				return AnnotationFactory.create( ad );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( Transient.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void getVersion(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "version".equals( element.getName() ) ) {
 				Annotation annotation = buildColumns( element );
 				addIfNotNull( annotationList, annotation );
 				getTemporal( annotationList, element );
 				AnnotationDescriptor basic = new AnnotationDescriptor( Version.class );
 				annotationList.add( AnnotationFactory.create( basic ) );
 				getAccessType( annotationList, element );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			//we have nothing, so Java annotations might occurs
 			Annotation annotation = getJavaAnnotation( Version.class );
 			if ( annotation != null ) {
 				annotationList.add( annotation );
 				annotation = getJavaAnnotation( Column.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Columns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Temporal.class );
 				addIfNotNull( annotationList, annotation );
 			}
 		}
 	}
 
 	private void getBasic(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "basic".equals( element.getName() ) ) {
 				Annotation annotation = buildColumns( element );
 				addIfNotNull( annotationList, annotation );
 				getAccessType( annotationList, element );
 				getTemporal( annotationList, element );
 				getLob( annotationList, element );
 				getEnumerated( annotationList, element );
 				AnnotationDescriptor basic = new AnnotationDescriptor( Basic.class );
 				getFetchType( basic, element );
 				copyBooleanAttribute( basic, element, "optional" );
 				annotationList.add( AnnotationFactory.create( basic ) );
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			//no annotation presence constraint, basic is the default
 			Annotation annotation = getJavaAnnotation( Basic.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( Lob.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( Enumerated.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( Temporal.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( Column.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( Columns.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( AttributeOverride.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( AttributeOverrides.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( AssociationOverride.class );
 			addIfNotNull( annotationList, annotation );
 			annotation = getJavaAnnotation( AssociationOverrides.class );
 			addIfNotNull( annotationList, annotation );
 		}
 	}
 
 	private void getEnumerated(List<Annotation> annotationList, Element element) {
 		Element subElement = element != null ? element.element( "enumerated" ) : null;
 		if ( subElement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Enumerated.class );
 			String enumerated = subElement.getTextTrim();
 			if ( "ORDINAL".equalsIgnoreCase( enumerated ) ) {
 				ad.setValue( "value", EnumType.ORDINAL );
 			}
 			else if ( "STRING".equalsIgnoreCase( enumerated ) ) {
 				ad.setValue( "value", EnumType.STRING );
 			}
 			else if ( StringHelper.isNotEmpty( enumerated ) ) {
 				throw new AnnotationException( "Unknown EnumType: " + enumerated + ". " + SCHEMA_VALIDATION );
 			}
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getLob(List<Annotation> annotationList, Element element) {
 		Element subElement = element != null ? element.element( "lob" ) : null;
 		if ( subElement != null ) {
 			annotationList.add( AnnotationFactory.create( new AnnotationDescriptor( Lob.class ) ) );
 		}
 	}
 
 	private void getFetchType(AnnotationDescriptor descriptor, Element element) {
 		String fetchString = element != null ? element.attributeValue( "fetch" ) : null;
 		if ( fetchString != null ) {
 			if ( "eager".equalsIgnoreCase( fetchString ) ) {
 				descriptor.setValue( "fetch", FetchType.EAGER );
 			}
 			else if ( "lazy".equalsIgnoreCase( fetchString ) ) {
 				descriptor.setValue( "fetch", FetchType.LAZY );
 			}
 		}
 	}
 
 	private void getEmbeddedId(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "embedded-id".equals( element.getName() ) ) {
 				if ( isProcessingId( defaults ) ) {
 					Annotation annotation = getAttributeOverrides( element, defaults, false );
 					addIfNotNull( annotationList, annotation );
 					annotation = getAssociationOverrides( element, defaults, false );
 					addIfNotNull( annotationList, annotation );
 					AnnotationDescriptor ad = new AnnotationDescriptor( EmbeddedId.class );
 					annotationList.add( AnnotationFactory.create( ad ) );
 					getAccessType( annotationList, element );
 				}
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			Annotation annotation = getJavaAnnotation( EmbeddedId.class );
 			if ( annotation != null ) {
 				annotationList.add( annotation );
 				annotation = getJavaAnnotation( Column.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Columns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( GeneratedValue.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Temporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( TableGenerator.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( SequenceGenerator.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverrides.class );
 				addIfNotNull( annotationList, annotation );
 			}
 		}
 	}
 
 	private void preCalculateElementsForProperty(Element tree) {
 		elementsForProperty = new ArrayList<Element>();
 		Element element = tree != null ? tree.element( "attributes" ) : null;
 		//put entity.attributes elements
 		if ( element != null ) {
 			for ( Element subelement : (List<Element>) element.elements() ) {
 				if ( propertyName.equals( subelement.attributeValue( "name" ) ) ) {
 					elementsForProperty.add( subelement );
 				}
 			}
 		}
 		//add pre-* etc from entity and pure entity listener classes
 		if ( tree != null ) {
 			for ( Element subelement : (List<Element>) tree.elements() ) {
 				if ( propertyName.equals( subelement.attributeValue( "method-name" ) ) ) {
 					elementsForProperty.add( subelement );
 				}
 			}
 		}
 	}
 
 	private void getId(List<Annotation> annotationList, XMLContext.Default defaults) {
 		for ( Element element : elementsForProperty ) {
 			if ( "id".equals( element.getName() ) ) {
 				boolean processId = isProcessingId( defaults );
 				if ( processId ) {
 					Annotation annotation = buildColumns( element );
 					addIfNotNull( annotationList, annotation );
 					annotation = buildGeneratedValue( element );
 					addIfNotNull( annotationList, annotation );
 					getTemporal( annotationList, element );
 					//FIXME: fix the priority of xml over java for generator names
 					annotation = getTableGenerator( element, defaults );
 					addIfNotNull( annotationList, annotation );
 					annotation = getSequenceGenerator( element, defaults );
 					addIfNotNull( annotationList, annotation );
 					AnnotationDescriptor id = new AnnotationDescriptor( Id.class );
 					annotationList.add( AnnotationFactory.create( id ) );
 					getAccessType( annotationList, element );
 				}
 			}
 		}
 		if ( elementsForProperty.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			Annotation annotation = getJavaAnnotation( Id.class );
 			if ( annotation != null ) {
 				annotationList.add( annotation );
 				annotation = getJavaAnnotation( Column.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Columns.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( GeneratedValue.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( Temporal.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( TableGenerator.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( SequenceGenerator.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AttributeOverrides.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverride.class );
 				addIfNotNull( annotationList, annotation );
 				annotation = getJavaAnnotation( AssociationOverrides.class );
 				addIfNotNull( annotationList, annotation );
 			}
 		}
 	}
 
 	private boolean isProcessingId(XMLContext.Default defaults) {
 		boolean isExplicit = defaults.getAccess() != null;
 		boolean correctAccess =
 				( PropertyType.PROPERTY.equals( propertyType ) && AccessType.PROPERTY.equals( defaults.getAccess() ) )
 						|| ( PropertyType.FIELD.equals( propertyType ) && AccessType.FIELD
 						.equals( defaults.getAccess() ) );
 		boolean hasId = defaults.canUseJavaAnnotations()
 				&& ( isJavaAnnotationPresent( Id.class ) || isJavaAnnotationPresent( EmbeddedId.class ) );
 		//if ( properAccessOnMetadataComplete || properOverridingOnMetadataNonComplete ) {
 		boolean mirrorAttributeIsId = defaults.canUseJavaAnnotations() &&
 				( mirroredAttribute != null &&
 						( mirroredAttribute.isAnnotationPresent( Id.class )
 								|| mirroredAttribute.isAnnotationPresent( EmbeddedId.class ) ) );
 		boolean propertyIsDefault = PropertyType.PROPERTY.equals( propertyType )
 				&& !mirrorAttributeIsId;
 		return correctAccess || ( !isExplicit && hasId ) || ( !isExplicit && propertyIsDefault );
 	}
 
 	private Columns buildColumns(Element element) {
 		List<Element> subelements = element.elements( "column" );
 		List<Column> columns = new ArrayList<Column>( subelements.size() );
 		for ( Element subelement : subelements ) {
 			columns.add( getColumn( subelement, false, element ) );
 		}
 		if ( columns.size() > 0 ) {
 			AnnotationDescriptor columnsDescr = new AnnotationDescriptor( Columns.class );
 			columnsDescr.setValue( "columns", columns.toArray( new Column[columns.size()] ) );
 			return AnnotationFactory.create( columnsDescr );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private GeneratedValue buildGeneratedValue(Element element) {
 		Element subElement = element != null ? element.element( "generated-value" ) : null;
 		if ( subElement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( GeneratedValue.class );
 			String strategy = subElement.attributeValue( "strategy" );
 			if ( "TABLE".equalsIgnoreCase( strategy ) ) {
 				ad.setValue( "strategy", GenerationType.TABLE );
 			}
 			else if ( "SEQUENCE".equalsIgnoreCase( strategy ) ) {
 				ad.setValue( "strategy", GenerationType.SEQUENCE );
 			}
 			else if ( "IDENTITY".equalsIgnoreCase( strategy ) ) {
 				ad.setValue( "strategy", GenerationType.IDENTITY );
 			}
 			else if ( "AUTO".equalsIgnoreCase( strategy ) ) {
 				ad.setValue( "strategy", GenerationType.AUTO );
 			}
 			else if ( StringHelper.isNotEmpty( strategy ) ) {
 				throw new AnnotationException( "Unknown GenerationType: " + strategy + ". " + SCHEMA_VALIDATION );
 			}
 			copyStringAttribute( ad, subElement, "generator", false );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void getTemporal(List<Annotation> annotationList, Element element) {
 		Element subElement = element != null ? element.element( "temporal" ) : null;
 		if ( subElement != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Temporal.class );
 			String temporal = subElement.getTextTrim();
 			if ( "DATE".equalsIgnoreCase( temporal ) ) {
 				ad.setValue( "value", TemporalType.DATE );
 			}
 			else if ( "TIME".equalsIgnoreCase( temporal ) ) {
 				ad.setValue( "value", TemporalType.TIME );
 			}
 			else if ( "TIMESTAMP".equalsIgnoreCase( temporal ) ) {
 				ad.setValue( "value", TemporalType.TIMESTAMP );
 			}
 			else if ( StringHelper.isNotEmpty( temporal ) ) {
 				throw new AnnotationException( "Unknown TemporalType: " + temporal + ". " + SCHEMA_VALIDATION );
 			}
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	private void getAccessType(List<Annotation> annotationList, Element element) {
 		if ( element == null ) {
 			return;
 		}
 		String access = element.attributeValue( "access" );
 		if ( access != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Access.class );
 			AccessType type;
 			try {
 				type = AccessType.valueOf( access );
 			}
 			catch ( IllegalArgumentException e ) {
 				throw new AnnotationException( access + " is not a valid access type. Check you xml confguration." );
 			}
 
 			if ( ( AccessType.PROPERTY.equals( type ) && this.element instanceof Method ) ||
 					( AccessType.FIELD.equals( type ) && this.element instanceof Field ) ) {
 				return;
 			}
 
 			ad.setValue( "value", type );
 			annotationList.add( AnnotationFactory.create( ad ) );
 		}
 	}
 
 	/**
 	 * @param mergeWithAnnotations Whether to use Java annotations for this
 	 * element, if present and not disabled by the XMLContext defaults.
 	 * In some contexts (such as an element-collection mapping) merging
 	 * with annotations is never allowed.
 	 */
 	private AssociationOverrides getAssociationOverrides(Element tree, XMLContext.Default defaults, boolean mergeWithAnnotations) {
 		List<AssociationOverride> attributes = buildAssociationOverrides( tree, defaults );
 		if ( mergeWithAnnotations && defaults.canUseJavaAnnotations() ) {
 			AssociationOverride annotation = getJavaAnnotation( AssociationOverride.class );
 			addAssociationOverrideIfNeeded( annotation, attributes );
 			AssociationOverrides annotations = getJavaAnnotation( AssociationOverrides.class );
 			if ( annotations != null ) {
 				for ( AssociationOverride current : annotations.value() ) {
 					addAssociationOverrideIfNeeded( current, attributes );
 				}
 			}
 		}
 		if ( attributes.size() > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( AssociationOverrides.class );
 			ad.setValue( "value", attributes.toArray( new AssociationOverride[attributes.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private List<AssociationOverride> buildAssociationOverrides(Element element, XMLContext.Default defaults) {
 		List<Element> subelements = element == null ? null : element.elements( "association-override" );
 		List<AssociationOverride> overrides = new ArrayList<AssociationOverride>();
 		if ( subelements != null && subelements.size() > 0 ) {
 			for ( Element current : subelements ) {
 				AnnotationDescriptor override = new AnnotationDescriptor( AssociationOverride.class );
 				copyStringAttribute( override, current, "name", true );
 				override.setValue( "joinColumns", getJoinColumns( current, false ) );
 				JoinTable joinTable = buildJoinTable( current, defaults );
 				if ( joinTable != null ) {
 					override.setValue( "joinTable", joinTable );
 				}
 				overrides.add( (AssociationOverride) AnnotationFactory.create( override ) );
 			}
 		}
 		return overrides;
 	}
 
 	private JoinColumn[] getJoinColumns(Element element, boolean isInverse) {
 		List<Element> subelements = element != null ?
 				element.elements( isInverse ? "inverse-join-column" : "join-column" ) :
 				null;
 		List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
 		if ( subelements != null ) {
 			for ( Element subelement : subelements ) {
 				AnnotationDescriptor column = new AnnotationDescriptor( JoinColumn.class );
 				copyStringAttribute( column, subelement, "name", false );
 				copyStringAttribute( column, subelement, "referenced-column-name", false );
 				copyBooleanAttribute( column, subelement, "unique" );
 				copyBooleanAttribute( column, subelement, "nullable" );
 				copyBooleanAttribute( column, subelement, "insertable" );
 				copyBooleanAttribute( column, subelement, "updatable" );
 				copyStringAttribute( column, subelement, "column-definition", false );
 				copyStringAttribute( column, subelement, "table", false );
 				joinColumns.add( (JoinColumn) AnnotationFactory.create( column ) );
 			}
 		}
 		return joinColumns.toArray( new JoinColumn[joinColumns.size()] );
 	}
 
 	private void addAssociationOverrideIfNeeded(AssociationOverride annotation, List<AssociationOverride> overrides) {
 		if ( annotation != null ) {
 			String overrideName = annotation.name();
 			boolean present = false;
 			for ( AssociationOverride current : overrides ) {
 				if ( current.name().equals( overrideName ) ) {
 					present = true;
 					break;
 				}
 			}
 			if ( !present ) {
 				overrides.add( annotation );
 			}
 		}
 	}
 
 	/**
 	 * @param mergeWithAnnotations Whether to use Java annotations for this
 	 * element, if present and not disabled by the XMLContext defaults.
 	 * In some contexts (such as an association mapping) merging with
 	 * annotations is never allowed.
 	 */
 	private AttributeOverrides getAttributeOverrides(Element tree, XMLContext.Default defaults, boolean mergeWithAnnotations) {
 		List<AttributeOverride> attributes = buildAttributeOverrides( tree, "attribute-override" );
 		return mergeAttributeOverrides( defaults, attributes, mergeWithAnnotations );
 	}
 
 	/**
 	 * @param mergeWithAnnotations Whether to use Java annotations for this
 	 * element, if present and not disabled by the XMLContext defaults.
 	 * In some contexts (such as an association mapping) merging with
 	 * annotations is never allowed.
 	 */
 	private AttributeOverrides mergeAttributeOverrides(XMLContext.Default defaults, List<AttributeOverride> attributes, boolean mergeWithAnnotations) {
 		if ( mergeWithAnnotations && defaults.canUseJavaAnnotations() ) {
 			AttributeOverride annotation = getJavaAnnotation( AttributeOverride.class );
 			addAttributeOverrideIfNeeded( annotation, attributes );
 			AttributeOverrides annotations = getJavaAnnotation( AttributeOverrides.class );
 			if ( annotations != null ) {
 				for ( AttributeOverride current : annotations.value() ) {
 					addAttributeOverrideIfNeeded( current, attributes );
 				}
 			}
 		}
 		if ( attributes.size() > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( AttributeOverrides.class );
 			ad.setValue( "value", attributes.toArray( new AttributeOverride[attributes.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private List<AttributeOverride> buildAttributeOverrides(Element element, String nodeName) {
 		List<Element> subelements = element == null ? null : element.elements( nodeName );
 		return buildAttributeOverrides( subelements, nodeName );
 	}
 
 	private List<AttributeOverride> buildAttributeOverrides(List<Element> subelements, String nodeName) {
 		List<AttributeOverride> overrides = new ArrayList<AttributeOverride>();
 		if ( subelements != null && subelements.size() > 0 ) {
 			for ( Element current : subelements ) {
 				if ( !current.getName().equals( nodeName ) ) {
 					continue;
 				}
 				AnnotationDescriptor override = new AnnotationDescriptor( AttributeOverride.class );
 				copyStringAttribute( override, current, "name", true );
 				Element column = current.element( "column" );
 				override.setValue( "column", getColumn( column, true, current ) );
 				overrides.add( (AttributeOverride) AnnotationFactory.create( override ) );
 			}
 		}
 		return overrides;
 	}
 
 	private Column getColumn(Element element, boolean isMandatory, Element current) {
 		//Element subelement = element != null ? element.element( "column" ) : null;
 		if ( element != null ) {
 			AnnotationDescriptor column = new AnnotationDescriptor( Column.class );
 			copyStringAttribute( column, element, "name", false );
 			copyBooleanAttribute( column, element, "unique" );
 			copyBooleanAttribute( column, element, "nullable" );
 			copyBooleanAttribute( column, element, "insertable" );
 			copyBooleanAttribute( column, element, "updatable" );
 			copyStringAttribute( column, element, "column-definition", false );
 			copyStringAttribute( column, element, "table", false );
 			copyIntegerAttribute( column, element, "length" );
 			copyIntegerAttribute( column, element, "precision" );
 			copyIntegerAttribute( column, element, "scale" );
 			return (Column) AnnotationFactory.create( column );
 		}
 		else {
 			if ( isMandatory ) {
 				throw new AnnotationException( current.getPath() + ".column is mandatory. " + SCHEMA_VALIDATION );
 			}
 			return null;
 		}
 	}
 
 	private void addAttributeOverrideIfNeeded(AttributeOverride annotation, List<AttributeOverride> overrides) {
 		if ( annotation != null ) {
 			String overrideName = annotation.name();
 			boolean present = false;
 			for ( AttributeOverride current : overrides ) {
 				if ( current.name().equals( overrideName ) ) {
 					present = true;
 					break;
 				}
 			}
 			if ( !present ) {
 				overrides.add( annotation );
 			}
 		}
 	}
 
 	private Access getAccessType(Element tree, XMLContext.Default defaults) {
 		String access = tree == null ? null : tree.attributeValue( "access" );
 		if ( access != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Access.class );
 			AccessType type;
 			try {
 				type = AccessType.valueOf( access );
 			}
 			catch ( IllegalArgumentException e ) {
 				throw new AnnotationException( access + " is not a valid access type. Check you xml confguration." );
 			}
 			ad.setValue( "value", type );
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() && isJavaAnnotationPresent( Access.class ) ) {
 			return getJavaAnnotation( Access.class );
 		}
 		else if ( defaults.getAccess() != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Access.class );
 			ad.setValue( "value", defaults.getAccess() );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private ExcludeSuperclassListeners getExcludeSuperclassListeners(Element tree, XMLContext.Default defaults) {
 		return (ExcludeSuperclassListeners) getMarkerAnnotation( ExcludeSuperclassListeners.class, tree, defaults );
 	}
 
 	private ExcludeDefaultListeners getExcludeDefaultListeners(Element tree, XMLContext.Default defaults) {
 		return (ExcludeDefaultListeners) getMarkerAnnotation( ExcludeDefaultListeners.class, tree, defaults );
 	}
 
 	private Annotation getMarkerAnnotation(
 			Class<? extends Annotation> clazz, Element element, XMLContext.Default defaults
 	) {
 		Element subelement = element == null ? null : element.element( annotationToXml.get( clazz ) );
 		if ( subelement != null ) {
 			return AnnotationFactory.create( new AnnotationDescriptor( clazz ) );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			//TODO wonder whether it should be excluded so that user can undone it
 			return getJavaAnnotation( clazz );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private SqlResultSetMappings getSqlResultSetMappings(Element tree, XMLContext.Default defaults) {
 		List<SqlResultSetMapping> results = buildSqlResultsetMappings( tree, defaults );
 		if ( defaults.canUseJavaAnnotations() ) {
 			SqlResultSetMapping annotation = getJavaAnnotation( SqlResultSetMapping.class );
 			addSqlResultsetMappingIfNeeded( annotation, results );
 			SqlResultSetMappings annotations = getJavaAnnotation( SqlResultSetMappings.class );
 			if ( annotations != null ) {
 				for ( SqlResultSetMapping current : annotations.value() ) {
 					addSqlResultsetMappingIfNeeded( current, results );
 				}
 			}
 		}
 		if ( results.size() > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( SqlResultSetMappings.class );
 			ad.setValue( "value", results.toArray( new SqlResultSetMapping[results.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
+	public static List<NamedStoredProcedureQuery> buildNamedStoreProcedureQueries(Element element, XMLContext.Default defaults) {
+		if ( element == null ) {
+			return new ArrayList<NamedStoredProcedureQuery>();
+		}
+		List namedStoredProcedureElements = element.elements( "named-stored-procedure-query" );
+		List<NamedStoredProcedureQuery> namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQuery>();
+		for ( Object obj : namedStoredProcedureElements ) {
+			Element subElement = (Element) obj;
+			AnnotationDescriptor ann = new AnnotationDescriptor( NamedStoredProcedureQuery.class );
+			copyStringAttribute( ann, subElement, "name", true );
+			copyStringAttribute( ann, subElement, "procedure-name", true );
+
+			List<Element> elements = subElement.elements( "parameter" );
+			List<StoredProcedureParameter> storedProcedureParameters = new ArrayList<StoredProcedureParameter>();
+
+			for ( Element parameterElement : elements ) {
+				AnnotationDescriptor parameterDescriptor = new AnnotationDescriptor( StoredProcedureParameter.class );
+				copyStringAttribute( parameterDescriptor, parameterElement, "name", false );
+				String modeValue = parameterElement.attributeValue( "mode" );
+				if ( modeValue == null ) {
+					parameterDescriptor.setValue( "mode", ParameterMode.IN );
+				}
+				else {
+					parameterDescriptor.setValue( "mode", ParameterMode.valueOf( modeValue.toUpperCase() ) );
+				}
+				String clazzName = parameterElement.attributeValue( "class" );
+				Class clazz;
+				try {
+					clazz = ReflectHelper.classForName(
+							XMLContext.buildSafeClassName( clazzName, defaults ),
+							JPAOverriddenAnnotationReader.class
+					);
+				}
+				catch ( ClassNotFoundException e ) {
+					throw new AnnotationException( "Unable to find entity-class: " + clazzName, e );
+				}
+				parameterDescriptor.setValue( "type", clazz );
+				storedProcedureParameters.add( (StoredProcedureParameter) AnnotationFactory.create( parameterDescriptor ) );
+			}
+
+			ann.setValue(
+					"parameters",
+					storedProcedureParameters.toArray( new StoredProcedureParameter[storedProcedureParameters.size()] )
+			);
+
+			elements = subElement.elements( "result-class" );
+			List<Class> returnClasses = new ArrayList<Class>();
+			for ( Element classElement : elements ) {
+				String clazzName = classElement.getTextTrim();
+				Class clazz;
+				try {
+					clazz = ReflectHelper.classForName(
+							XMLContext.buildSafeClassName( clazzName, defaults ),
+							JPAOverriddenAnnotationReader.class
+					);
+				}
+				catch ( ClassNotFoundException e ) {
+					throw new AnnotationException( "Unable to find entity-class: " + clazzName, e );
+				}
+				returnClasses.add( clazz );
+			}
+			ann.setValue( "resultClasses", returnClasses.toArray( new Class[returnClasses.size()] ) );
+
+
+			elements = subElement.elements( "result-set-mapping" );
+			List<String> resultSetMappings = new ArrayList<String>();
+			for ( Element resultSetMappingElement : elements ) {
+				resultSetMappings.add( resultSetMappingElement.getTextTrim() );
+			}
+			ann.setValue( "resultSetMappings", resultSetMappings.toArray( new String[resultSetMappings.size()] ) );
+			elements = subElement.elements( "hint" );
+			buildQueryHints( elements, ann );
+			namedStoredProcedureQueries.add( (NamedStoredProcedureQuery) AnnotationFactory.create( ann ) );
+		}
+		return namedStoredProcedureElements;
+
+	}
+
 	public static List<SqlResultSetMapping> buildSqlResultsetMappings(Element element, XMLContext.Default defaults) {
 		if ( element == null ) {
 			return new ArrayList<SqlResultSetMapping>();
 		}
 		List resultsetElementList = element.elements( "sql-result-set-mapping" );
 		List<SqlResultSetMapping> resultsets = new ArrayList<SqlResultSetMapping>();
 		Iterator it = resultsetElementList.listIterator();
 		while ( it.hasNext() ) {
 			Element subelement = (Element) it.next();
 			AnnotationDescriptor ann = new AnnotationDescriptor( SqlResultSetMapping.class );
 			copyStringAttribute( ann, subelement, "name", true );
 			List<Element> elements = subelement.elements( "entity-result" );
 			List<EntityResult> entityResults = new ArrayList<EntityResult>( elements.size() );
 			for ( Element entityResult : elements ) {
 				AnnotationDescriptor entityResultDescriptor = new AnnotationDescriptor( EntityResult.class );
 				String clazzName = entityResult.attributeValue( "entity-class" );
 				if ( clazzName == null ) {
 					throw new AnnotationException( "<entity-result> without entity-class. " + SCHEMA_VALIDATION );
 				}
 				Class clazz;
 				try {
 					clazz = ReflectHelper.classForName(
 							XMLContext.buildSafeClassName( clazzName, defaults ),
 							JPAOverriddenAnnotationReader.class
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to find entity-class: " + clazzName, e );
 				}
 				entityResultDescriptor.setValue( "entityClass", clazz );
 				copyStringAttribute( entityResultDescriptor, entityResult, "discriminator-column", false );
 				List<FieldResult> fieldResults = new ArrayList<FieldResult>();
 				for ( Element fieldResult : (List<Element>) entityResult.elements( "field-result" ) ) {
 					AnnotationDescriptor fieldResultDescriptor = new AnnotationDescriptor( FieldResult.class );
 					copyStringAttribute( fieldResultDescriptor, fieldResult, "name", true );
 					copyStringAttribute( fieldResultDescriptor, fieldResult, "column", true );
 					fieldResults.add( (FieldResult) AnnotationFactory.create( fieldResultDescriptor ) );
 				}
 				entityResultDescriptor.setValue(
 						"fields", fieldResults.toArray( new FieldResult[fieldResults.size()] )
 				);
 				entityResults.add( (EntityResult) AnnotationFactory.create( entityResultDescriptor ) );
 			}
 			ann.setValue( "entities", entityResults.toArray( new EntityResult[entityResults.size()] ) );
 
 			elements = subelement.elements( "column-result" );
 			List<ColumnResult> columnResults = new ArrayList<ColumnResult>( elements.size() );
 			for ( Element columnResult : elements ) {
 				AnnotationDescriptor columnResultDescriptor = new AnnotationDescriptor( ColumnResult.class );
 				copyStringAttribute( columnResultDescriptor, columnResult, "name", true );
 				columnResults.add( (ColumnResult) AnnotationFactory.create( columnResultDescriptor ) );
 			}
 			ann.setValue( "columns", columnResults.toArray( new ColumnResult[columnResults.size()] ) );
 			//FIXME there is never such a result-class, get rid of it?
 			String clazzName = subelement.attributeValue( "result-class" );
 			if ( StringHelper.isNotEmpty( clazzName ) ) {
 				Class clazz;
 				try {
 					clazz = ReflectHelper.classForName(
 							XMLContext.buildSafeClassName( clazzName, defaults ),
 							JPAOverriddenAnnotationReader.class
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to find entity-class: " + clazzName, e );
 				}
 				ann.setValue( "resultClass", clazz );
 			}
 			copyStringAttribute( ann, subelement, "result-set-mapping", false );
 			resultsets.add( (SqlResultSetMapping) AnnotationFactory.create( ann ) );
 		}
 		return resultsets;
 	}
 
 	private void addSqlResultsetMappingIfNeeded(SqlResultSetMapping annotation, List<SqlResultSetMapping> resultsets) {
 		if ( annotation != null ) {
 			String resultsetName = annotation.name();
 			boolean present = false;
 			for ( SqlResultSetMapping current : resultsets ) {
 				if ( current.name().equals( resultsetName ) ) {
 					present = true;
 					break;
 				}
 			}
 			if ( !present ) {
 				resultsets.add( annotation );
 			}
 		}
 	}
 
 	private NamedQueries getNamedQueries(Element tree, XMLContext.Default defaults) {
 		//TODO avoid the Proxy Creation (@NamedQueries) when possible
 		List<NamedQuery> queries = (List<NamedQuery>) buildNamedQueries( tree, false, defaults );
 		if ( defaults.canUseJavaAnnotations() ) {
 			NamedQuery annotation = getJavaAnnotation( NamedQuery.class );
 			addNamedQueryIfNeeded( annotation, queries );
 			NamedQueries annotations = getJavaAnnotation( NamedQueries.class );
 			if ( annotations != null ) {
 				for ( NamedQuery current : annotations.value() ) {
 					addNamedQueryIfNeeded( current, queries );
 				}
 			}
 		}
 		if ( queries.size() > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( NamedQueries.class );
 			ad.setValue( "value", queries.toArray( new NamedQuery[queries.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void addNamedQueryIfNeeded(NamedQuery annotation, List<NamedQuery> queries) {
 		if ( annotation != null ) {
 			String queryName = annotation.name();
 			boolean present = false;
 			for ( NamedQuery current : queries ) {
 				if ( current.name().equals( queryName ) ) {
 					present = true;
 					break;
 				}
 			}
 			if ( !present ) {
 				queries.add( annotation );
 			}
 		}
 	}
 
 	private NamedNativeQueries getNamedNativeQueries(Element tree, XMLContext.Default defaults) {
 		List<NamedNativeQuery> queries = (List<NamedNativeQuery>) buildNamedQueries( tree, true, defaults );
 		if ( defaults.canUseJavaAnnotations() ) {
 			NamedNativeQuery annotation = getJavaAnnotation( NamedNativeQuery.class );
 			addNamedNativeQueryIfNeeded( annotation, queries );
 			NamedNativeQueries annotations = getJavaAnnotation( NamedNativeQueries.class );
 			if ( annotations != null ) {
 				for ( NamedNativeQuery current : annotations.value() ) {
 					addNamedNativeQueryIfNeeded( current, queries );
 				}
 			}
 		}
 		if ( queries.size() > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( NamedNativeQueries.class );
 			ad.setValue( "value", queries.toArray( new NamedNativeQuery[queries.size()] ) );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void addNamedNativeQueryIfNeeded(NamedNativeQuery annotation, List<NamedNativeQuery> queries) {
 		if ( annotation != null ) {
 			String queryName = annotation.name();
 			boolean present = false;
 			for ( NamedNativeQuery current : queries ) {
 				if ( current.name().equals( queryName ) ) {
 					present = true;
 					break;
 				}
 			}
 			if ( !present ) {
 				queries.add( annotation );
 			}
 		}
 	}
 
+	private static void buildQueryHints(List<Element> elements, AnnotationDescriptor ann){
+		List<QueryHint> queryHints = new ArrayList<QueryHint>( elements.size() );
+		for ( Element hint : elements ) {
+			AnnotationDescriptor hintDescriptor = new AnnotationDescriptor( QueryHint.class );
+			String value = hint.attributeValue( "name" );
+			if ( value == null ) {
+				throw new AnnotationException( "<hint> without name. " + SCHEMA_VALIDATION );
+			}
+			hintDescriptor.setValue( "name", value );
+			value = hint.attributeValue( "value" );
+			if ( value == null ) {
+				throw new AnnotationException( "<hint> without value. " + SCHEMA_VALIDATION );
+			}
+			hintDescriptor.setValue( "value", value );
+			queryHints.add( (QueryHint) AnnotationFactory.create( hintDescriptor ) );
+		}
+		ann.setValue( "hints", queryHints.toArray( new QueryHint[queryHints.size()] ) );
+	}
+
 	public static List buildNamedQueries(Element element, boolean isNative, XMLContext.Default defaults) {
 		if ( element == null ) {
 			return new ArrayList();
 		}
 		List namedQueryElementList = isNative ?
 				element.elements( "named-native-query" ) :
 				element.elements( "named-query" );
 		List namedQueries = new ArrayList();
 		Iterator it = namedQueryElementList.listIterator();
 		while ( it.hasNext() ) {
 			Element subelement = (Element) it.next();
 			AnnotationDescriptor ann = new AnnotationDescriptor(
 					isNative ? NamedNativeQuery.class : NamedQuery.class
 			);
 			copyStringAttribute( ann, subelement, "name", false );
 			Element queryElt = subelement.element( "query" );
 			if ( queryElt == null ) {
 				throw new AnnotationException( "No <query> element found." + SCHEMA_VALIDATION );
 			}
 			copyStringElement( queryElt, ann, "query" );
 			List<Element> elements = subelement.elements( "hint" );
-			List<QueryHint> queryHints = new ArrayList<QueryHint>( elements.size() );
-			for ( Element hint : elements ) {
-				AnnotationDescriptor hintDescriptor = new AnnotationDescriptor( QueryHint.class );
-				String value = hint.attributeValue( "name" );
-				if ( value == null ) {
-					throw new AnnotationException( "<hint> without name. " + SCHEMA_VALIDATION );
-				}
-				hintDescriptor.setValue( "name", value );
-				value = hint.attributeValue( "value" );
-				if ( value == null ) {
-					throw new AnnotationException( "<hint> without value. " + SCHEMA_VALIDATION );
-				}
-				hintDescriptor.setValue( "value", value );
-				queryHints.add( (QueryHint) AnnotationFactory.create( hintDescriptor ) );
-			}
-			ann.setValue( "hints", queryHints.toArray( new QueryHint[queryHints.size()] ) );
+			buildQueryHints( elements, ann );
 			String clazzName = subelement.attributeValue( "result-class" );
 			if ( StringHelper.isNotEmpty( clazzName ) ) {
 				Class clazz;
 				try {
 					clazz = ReflectHelper.classForName(
 							XMLContext.buildSafeClassName( clazzName, defaults ),
 							JPAOverriddenAnnotationReader.class
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to find entity-class: " + clazzName, e );
 				}
 				ann.setValue( "resultClass", clazz );
 			}
 			copyStringAttribute( ann, subelement, "result-set-mapping", false );
 			namedQueries.add( AnnotationFactory.create( ann ) );
 		}
 		return namedQueries;
 	}
 
 	private TableGenerator getTableGenerator(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( annotationToXml.get( TableGenerator.class ) ) : null;
 		if ( element != null ) {
 			return buildTableGeneratorAnnotation( element, defaults );
 		}
 		else if ( defaults.canUseJavaAnnotations() && isJavaAnnotationPresent( TableGenerator.class ) ) {
 			TableGenerator tableAnn = getJavaAnnotation( TableGenerator.class );
 			if ( StringHelper.isNotEmpty( defaults.getSchema() )
 					|| StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 				AnnotationDescriptor annotation = new AnnotationDescriptor( TableGenerator.class );
 				annotation.setValue( "name", tableAnn.name() );
 				annotation.setValue( "table", tableAnn.table() );
 				annotation.setValue( "catalog", tableAnn.table() );
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) )
 						&& StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 					annotation.setValue( "catalog", defaults.getCatalog() );
 				}
 				annotation.setValue( "schema", tableAnn.table() );
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) )
 						&& StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 					annotation.setValue( "catalog", defaults.getSchema() );
 				}
 				annotation.setValue( "pkColumnName", tableAnn.pkColumnName() );
 				annotation.setValue( "valueColumnName", tableAnn.valueColumnName() );
 				annotation.setValue( "pkColumnValue", tableAnn.pkColumnValue() );
 				annotation.setValue( "initialValue", tableAnn.initialValue() );
 				annotation.setValue( "allocationSize", tableAnn.allocationSize() );
 				annotation.setValue( "uniqueConstraints", tableAnn.uniqueConstraints() );
 				return AnnotationFactory.create( annotation );
 			}
 			else {
 				return tableAnn;
 			}
 		}
 		else {
 			return null;
 		}
 	}
 
 	public static TableGenerator buildTableGeneratorAnnotation(Element element, XMLContext.Default defaults) {
 		AnnotationDescriptor ad = new AnnotationDescriptor( TableGenerator.class );
 		copyStringAttribute( ad, element, "name", false );
 		copyStringAttribute( ad, element, "table", false );
 		copyStringAttribute( ad, element, "catalog", false );
 		copyStringAttribute( ad, element, "schema", false );
 		copyStringAttribute( ad, element, "pk-column-name", false );
 		copyStringAttribute( ad, element, "value-column-name", false );
 		copyStringAttribute( ad, element, "pk-column-value", false );
 		copyIntegerAttribute( ad, element, "initial-value" );
 		copyIntegerAttribute( ad, element, "allocation-size" );
 		buildUniqueConstraints( ad, element );
 		if ( StringHelper.isEmpty( (String) ad.valueOf( "schema" ) )
 				&& StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 			ad.setValue( "schema", defaults.getSchema() );
 		}
 		if ( StringHelper.isEmpty( (String) ad.valueOf( "catalog" ) )
 				&& StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 			ad.setValue( "catalog", defaults.getCatalog() );
 		}
 		return AnnotationFactory.create( ad );
 	}
 
 	private SequenceGenerator getSequenceGenerator(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( annotationToXml.get( SequenceGenerator.class ) ) : null;
 		if ( element != null ) {
 			return buildSequenceGeneratorAnnotation( element );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( SequenceGenerator.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public static SequenceGenerator buildSequenceGeneratorAnnotation(Element element) {
 		if ( element != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( SequenceGenerator.class );
 			copyStringAttribute( ad, element, "name", false );
 			copyStringAttribute( ad, element, "sequence-name", false );
 			copyIntegerAttribute( ad, element, "initial-value" );
 			copyIntegerAttribute( ad, element, "allocation-size" );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private DiscriminatorColumn getDiscriminatorColumn(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( "discriminator-column" ) : null;
 		if ( element != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( DiscriminatorColumn.class );
 			copyStringAttribute( ad, element, "name", false );
 			copyStringAttribute( ad, element, "column-definition", false );
 			String value = element.attributeValue( "discriminator-type" );
 			DiscriminatorType type = DiscriminatorType.STRING;
 			if ( value != null ) {
 				if ( "STRING".equals( value ) ) {
 					type = DiscriminatorType.STRING;
 				}
 				else if ( "CHAR".equals( value ) ) {
 					type = DiscriminatorType.CHAR;
 				}
 				else if ( "INTEGER".equals( value ) ) {
 					type = DiscriminatorType.INTEGER;
 				}
 				else {
 					throw new AnnotationException(
 							"Unknown DiscrimiatorType in XML: " + value + " (" + SCHEMA_VALIDATION + ")"
 					);
 				}
 			}
 			ad.setValue( "discriminatorType", type );
 			copyIntegerAttribute( ad, element, "length" );
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( DiscriminatorColumn.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private DiscriminatorValue getDiscriminatorValue(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( "discriminator-value" ) : null;
 		if ( element != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( DiscriminatorValue.class );
 			copyStringElement( element, ad, "value" );
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( DiscriminatorValue.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private Inheritance getInheritance(Element tree, XMLContext.Default defaults) {
 		Element element = tree != null ? tree.element( "inheritance" ) : null;
 		if ( element != null ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( Inheritance.class );
 			Attribute attr = element.attribute( "strategy" );
 			InheritanceType strategy = InheritanceType.SINGLE_TABLE;
 			if ( attr != null ) {
 				String value = attr.getValue();
 				if ( "SINGLE_TABLE".equals( value ) ) {
 					strategy = InheritanceType.SINGLE_TABLE;
 				}
 				else if ( "JOINED".equals( value ) ) {
 					strategy = InheritanceType.JOINED;
 				}
 				else if ( "TABLE_PER_CLASS".equals( value ) ) {
 					strategy = InheritanceType.TABLE_PER_CLASS;
 				}
 				else {
 					throw new AnnotationException(
 							"Unknown InheritanceType in XML: " + value + " (" + SCHEMA_VALIDATION + ")"
 					);
 				}
 			}
 			ad.setValue( "strategy", strategy );
 			return AnnotationFactory.create( ad );
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( Inheritance.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private IdClass getIdClass(Element tree, XMLContext.Default defaults) {
 		Element element = tree == null ? null : tree.element( "id-class" );
 		if ( element != null ) {
 			Attribute attr = element.attribute( "class" );
 			if ( attr != null ) {
 				AnnotationDescriptor ad = new AnnotationDescriptor( IdClass.class );
 				Class clazz;
 				try {
 					clazz = ReflectHelper.classForName(
 							XMLContext.buildSafeClassName( attr.getValue(), defaults ),
 							this.getClass()
 					);
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to find id-class: " + attr.getValue(), e );
 				}
 				ad.setValue( "value", clazz );
 				return AnnotationFactory.create( ad );
 			}
 			else {
 				throw new AnnotationException( "id-class without class. " + SCHEMA_VALIDATION );
 			}
 		}
 		else if ( defaults.canUseJavaAnnotations() ) {
 			return getJavaAnnotation( IdClass.class );
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @param mergeWithAnnotations Whether to use Java annotations for this
 	 * element, if present and not disabled by the XMLContext defaults.
 	 * In some contexts (such as an association mapping) merging with
 	 * annotations is never allowed.
 	 */
 	private PrimaryKeyJoinColumns getPrimaryKeyJoinColumns(Element element, XMLContext.Default defaults, boolean mergeWithAnnotations) {
 		PrimaryKeyJoinColumn[] columns = buildPrimaryKeyJoinColumns( element );
 		if ( mergeWithAnnotations ) {
 			if ( columns.length == 0 && defaults.canUseJavaAnnotations() ) {
 				PrimaryKeyJoinColumn annotation = getJavaAnnotation( PrimaryKeyJoinColumn.class );
 				if ( annotation != null ) {
 					columns = new PrimaryKeyJoinColumn[] { annotation };
 				}
 				else {
 					PrimaryKeyJoinColumns annotations = getJavaAnnotation( PrimaryKeyJoinColumns.class );
 					columns = annotations != null ? annotations.value() : columns;
 				}
 			}
 		}
 		if ( columns.length > 0 ) {
 			AnnotationDescriptor ad = new AnnotationDescriptor( PrimaryKeyJoinColumns.class );
 			ad.setValue( "value", columns );
 			return AnnotationFactory.create( ad );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private Entity getEntity(Element tree, XMLContext.Default defaults) {
 		if ( tree == null ) {
 			return defaults.canUseJavaAnnotations() ? getJavaAnnotation( Entity.class ) : null;
 		}
 		else {
 			if ( "entity".equals( tree.getName() ) ) {
 				AnnotationDescriptor entity = new AnnotationDescriptor( Entity.class );
 				copyStringAttribute( entity, tree, "name", false );
 				if ( defaults.canUseJavaAnnotations()
 						&& StringHelper.isEmpty( (String) entity.valueOf( "name" ) ) ) {
 					Entity javaAnn = getJavaAnnotation( Entity.class );
 					if ( javaAnn != null ) {
 						entity.setValue( "name", javaAnn.name() );
 					}
 				}
 				return AnnotationFactory.create( entity );
 			}
 			else {
 				return null; //this is not an entity
 			}
 		}
 	}
 
 	private MappedSuperclass getMappedSuperclass(Element tree, XMLContext.Default defaults) {
 		if ( tree == null ) {
 			return defaults.canUseJavaAnnotations() ? getJavaAnnotation( MappedSuperclass.class ) : null;
 		}
 		else {
 			if ( "mapped-superclass".equals( tree.getName() ) ) {
 				AnnotationDescriptor entity = new AnnotationDescriptor( MappedSuperclass.class );
 				return AnnotationFactory.create( entity );
 			}
 			else {
 				return null; //this is not an entity
 			}
 		}
 	}
 
 	private Embeddable getEmbeddable(Element tree, XMLContext.Default defaults) {
 		if ( tree == null ) {
 			return defaults.canUseJavaAnnotations() ? getJavaAnnotation( Embeddable.class ) : null;
 		}
 		else {
 			if ( "embeddable".equals( tree.getName() ) ) {
 				AnnotationDescriptor entity = new AnnotationDescriptor( Embeddable.class );
 				return AnnotationFactory.create( entity );
 			}
 			else {
 				return null; //this is not an entity
 			}
 		}
 	}
 
 	private Table getTable(Element tree, XMLContext.Default defaults) {
 		Element subelement = tree == null ? null : tree.element( "table" );
 		if ( subelement == null ) {
 			//no element but might have some default or some annotation
 			if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 					|| StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 				AnnotationDescriptor annotation = new AnnotationDescriptor( Table.class );
 				if ( defaults.canUseJavaAnnotations() ) {
 					Table table = getJavaAnnotation( Table.class );
 					if ( table != null ) {
 						annotation.setValue( "name", table.name() );
 						annotation.setValue( "schema", table.schema() );
 						annotation.setValue( "catalog", table.catalog() );
 						annotation.setValue( "uniqueConstraints", table.uniqueConstraints() );
 						annotation.setValue( "indexes", table.indexes() );
 					}
 				}
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) )
 						&& StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 					annotation.setValue( "schema", defaults.getSchema() );
 				}
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) )
 						&& StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 					annotation.setValue( "catalog", defaults.getCatalog() );
 				}
 				return AnnotationFactory.create( annotation );
 			}
 			else if ( defaults.canUseJavaAnnotations() ) {
 				return getJavaAnnotation( Table.class );
 			}
 			else {
 				return null;
 			}
 		}
 		else {
 			//ignore java annotation, an element is defined
 			AnnotationDescriptor annotation = new AnnotationDescriptor( Table.class );
 			copyStringAttribute( annotation, subelement, "name", false );
 			copyStringAttribute( annotation, subelement, "catalog", false );
 			if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) ) ) {
 				annotation.setValue( "catalog", defaults.getCatalog() );
 			}
 			copyStringAttribute( annotation, subelement, "schema", false );
 			if ( StringHelper.isNotEmpty( defaults.getSchema() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) ) ) {
 				annotation.setValue( "schema", defaults.getSchema() );
 			}
 			buildUniqueConstraints( annotation, subelement );
 			buildIndex( annotation, subelement );
 			return AnnotationFactory.create( annotation );
 		}
 	}
 
 	private SecondaryTables getSecondaryTables(Element tree, XMLContext.Default defaults) {
 		List<Element> elements = tree == null ?
 				new ArrayList<Element>() :
 				(List<Element>) tree.elements( "secondary-table" );
 		List<SecondaryTable> secondaryTables = new ArrayList<SecondaryTable>( 3 );
 		for ( Element element : elements ) {
 			AnnotationDescriptor annotation = new AnnotationDescriptor( SecondaryTable.class );
 			copyStringAttribute( annotation, element, "name", false );
 			copyStringAttribute( annotation, element, "catalog", false );
 			if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) ) ) {
 				annotation.setValue( "catalog", defaults.getCatalog() );
 			}
 			copyStringAttribute( annotation, element, "schema", false );
 			if ( StringHelper.isNotEmpty( defaults.getSchema() )
 					&& StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) ) ) {
 				annotation.setValue( "schema", defaults.getSchema() );
 			}
 			buildUniqueConstraints( annotation, element );
 			buildIndex( annotation, element );
 			annotation.setValue( "pkJoinColumns", buildPrimaryKeyJoinColumns( element ) );
 			secondaryTables.add( (SecondaryTable) AnnotationFactory.create( annotation ) );
 		}
 		/*
 		 * You can't have both secondary table in XML and Java,
 		 * since there would be no way to "remove" a secondary table
 		 */
 		if ( secondaryTables.size() == 0 && defaults.canUseJavaAnnotations() ) {
 			SecondaryTable secTableAnn = getJavaAnnotation( SecondaryTable.class );
 			overridesDefaultInSecondaryTable( secTableAnn, defaults, secondaryTables );
 			SecondaryTables secTablesAnn = getJavaAnnotation( SecondaryTables.class );
 			if ( secTablesAnn != null ) {
 				for ( SecondaryTable table : secTablesAnn.value() ) {
 					overridesDefaultInSecondaryTable( table, defaults, secondaryTables );
 				}
 			}
 		}
 		if ( secondaryTables.size() > 0 ) {
 			AnnotationDescriptor descriptor = new AnnotationDescriptor( SecondaryTables.class );
 			descriptor.setValue( "value", secondaryTables.toArray( new SecondaryTable[secondaryTables.size()] ) );
 			return AnnotationFactory.create( descriptor );
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void overridesDefaultInSecondaryTable(
 			SecondaryTable secTableAnn, XMLContext.Default defaults, List<SecondaryTable> secondaryTables
 	) {
 		if ( secTableAnn != null ) {
 			//handle default values
 			if ( StringHelper.isNotEmpty( defaults.getCatalog() )
 					|| StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 				AnnotationDescriptor annotation = new AnnotationDescriptor( SecondaryTable.class );
 				annotation.setValue( "name", secTableAnn.name() );
 				annotation.setValue( "schema", secTableAnn.schema() );
 				annotation.setValue( "catalog", secTableAnn.catalog() );
 				annotation.setValue( "uniqueConstraints", secTableAnn.uniqueConstraints() );
 				annotation.setValue( "pkJoinColumns", secTableAnn.pkJoinColumns() );
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "schema" ) )
 						&& StringHelper.isNotEmpty( defaults.getSchema() ) ) {
 					annotation.setValue( "schema", defaults.getSchema() );
 				}
 				if ( StringHelper.isEmpty( (String) annotation.valueOf( "catalog" ) )
 						&& StringHelper.isNotEmpty( defaults.getCatalog() ) ) {
 					annotation.setValue( "catalog", defaults.getCatalog() );
 				}
 				secondaryTables.add( (SecondaryTable) AnnotationFactory.create( annotation ) );
 			}
 			else {
 				secondaryTables.add( secTableAnn );
 			}
 		}
 	}
 	private static void buildIndex(AnnotationDescriptor annotation, Element element){
 		List indexElementList = element.elements( "index" );
 		Index[] indexes = new Index[indexElementList.size()];
 		for(int i=0;i<indexElementList.size();i++){
 			Element subelement = (Element)indexElementList.get( i );
 			AnnotationDescriptor indexAnn = new AnnotationDescriptor( Index.class );
 			copyStringAttribute( indexAnn, subelement, "name", false );
 			copyStringAttribute( indexAnn, subelement, "column-list", true );
 			copyBooleanAttribute( indexAnn, subelement, "unique" );
 			indexes[i] = AnnotationFactory.create( indexAnn );
 		}
 		annotation.setValue( "indexes", indexes );
 	}
 	private static void buildUniqueConstraints(AnnotationDescriptor annotation, Element element) {
 		List uniqueConstraintElementList = element.elements( "unique-constraint" );
 		UniqueConstraint[] uniqueConstraints = new UniqueConstraint[uniqueConstraintElementList.size()];
 		int ucIndex = 0;
 		Iterator ucIt = uniqueConstraintElementList.listIterator();
 		while ( ucIt.hasNext() ) {
 			Element subelement = (Element) ucIt.next();
 			List<Element> columnNamesElements = subelement.elements( "column-name" );
 			String[] columnNames = new String[columnNamesElements.size()];
 			int columnNameIndex = 0;
 			Iterator it = columnNamesElements.listIterator();
 			while ( it.hasNext() ) {
 				Element columnNameElt = (Element) it.next();
 				columnNames[columnNameIndex++] = columnNameElt.getTextTrim();
 			}
 			AnnotationDescriptor ucAnn = new AnnotationDescriptor( UniqueConstraint.class );
 			copyStringAttribute( ucAnn, subelement, "name", false );
 			ucAnn.setValue( "columnNames", columnNames );
 			uniqueConstraints[ucIndex++] = AnnotationFactory.create( ucAnn );
 		}
 		annotation.setValue( "uniqueConstraints", uniqueConstraints );
 	}
 
 	private PrimaryKeyJoinColumn[] buildPrimaryKeyJoinColumns(Element element) {
 		if ( element == null ) {
 			return new PrimaryKeyJoinColumn[] { };
 		}
 		List pkJoinColumnElementList = element.elements( "primary-key-join-column" );
 		PrimaryKeyJoinColumn[] pkJoinColumns = new PrimaryKeyJoinColumn[pkJoinColumnElementList.size()];
 		int index = 0;
 		Iterator pkIt = pkJoinColumnElementList.listIterator();
 		while ( pkIt.hasNext() ) {
 			Element subelement = (Element) pkIt.next();
 			AnnotationDescriptor pkAnn = new AnnotationDescriptor( PrimaryKeyJoinColumn.class );
 			copyStringAttribute( pkAnn, subelement, "name", false );
 			copyStringAttribute( pkAnn, subelement, "referenced-column-name", false );
 			copyStringAttribute( pkAnn, subelement, "column-definition", false );
 			pkJoinColumns[index++] = AnnotationFactory.create( pkAnn );
 		}
 		return pkJoinColumns;
 	}
 
 	private static void copyStringAttribute(
 			AnnotationDescriptor annotation, Element element, String attributeName, boolean mandatory
 	) {
 		String attribute = element.attributeValue( attributeName );
 		if ( attribute != null ) {
 			String annotationAttributeName = getJavaAttributeNameFromXMLOne( attributeName );
 			annotation.setValue( annotationAttributeName, attribute );
 		}
 		else {
 			if ( mandatory ) {
 				throw new AnnotationException(
 						element.getName() + "." + attributeName + " is mandatory in XML overriding. " + SCHEMA_VALIDATION
 				);
 			}
 		}
 	}
 
 	private static void copyIntegerAttribute(AnnotationDescriptor annotation, Element element, String attributeName) {
 		String attribute = element.attributeValue( attributeName );
 		if ( attribute != null ) {
 			String annotationAttributeName = getJavaAttributeNameFromXMLOne( attributeName );
 			annotation.setValue( annotationAttributeName, attribute );
 			try {
 				int length = Integer.parseInt( attribute );
 				annotation.setValue( annotationAttributeName, length );
 			}
 			catch ( NumberFormatException e ) {
 				throw new AnnotationException(
 						element.getPath() + attributeName + " not parseable: " + attribute + " (" + SCHEMA_VALIDATION + ")"
 				);
 			}
 		}
 	}
 
 	private static String getJavaAttributeNameFromXMLOne(String attributeName) {
 		StringBuilder annotationAttributeName = new StringBuilder( attributeName );
 		int index = annotationAttributeName.indexOf( WORD_SEPARATOR );
 		while ( index != -1 ) {
 			annotationAttributeName.deleteCharAt( index );
 			annotationAttributeName.setCharAt(
 					index, Character.toUpperCase( annotationAttributeName.charAt( index ) )
 			);
 			index = annotationAttributeName.indexOf( WORD_SEPARATOR );
 		}
 		return annotationAttributeName.toString();
 	}
 
 	private static void copyStringElement(Element element, AnnotationDescriptor ad, String annotationAttribute) {
 		String discr = element.getTextTrim();
 		ad.setValue( annotationAttribute, discr );
 	}
 
 	private static void copyBooleanAttribute(AnnotationDescriptor descriptor, Element element, String attribute) {
 		String attributeValue = element.attributeValue( attribute );
 		if ( StringHelper.isNotEmpty( attributeValue ) ) {
 			String javaAttribute = getJavaAttributeNameFromXMLOne( attribute );
 			descriptor.setValue( javaAttribute, Boolean.parseBoolean( attributeValue ) );
 		}
 	}
 
 	private <T extends Annotation> T getJavaAnnotation(Class<T> annotationType) {
 		return element.getAnnotation( annotationType );
 	}
 
 	private <T extends Annotation> boolean isJavaAnnotationPresent(Class<T> annotationType) {
 		return element.isAnnotationPresent( annotationType );
 	}
 
 	private Annotation[] getJavaAnnotations() {
 		return element.getAnnotations();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java b/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
index 90866b06ad..bbe9fdb444 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
@@ -1,206 +1,207 @@
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
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.procedure.ProcedureCallMemento;
 
 /**
  * @author Steve Ebersole
  */
 public class NamedQueryRepository {
 	private static final Logger log = Logger.getLogger( NamedQueryRepository.class );
 
 	private final Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap;
 
 	private volatile Map<String, NamedQueryDefinition> namedQueryDefinitionMap;
 	private volatile Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap;
 	private volatile Map<String, ProcedureCallMemento> procedureCallMementoMap;
 
 	public NamedQueryRepository(
 			Iterable<NamedQueryDefinition> namedQueryDefinitions,
 			Iterable<NamedSQLQueryDefinition> namedSqlQueryDefinitions,
 			Iterable<ResultSetMappingDefinition> namedSqlResultSetMappings,
-			List<ProcedureCallMemento> namedProcedureCalls) {
+			Map<String, ProcedureCallMemento> namedProcedureCalls) {
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
+		this.procedureCallMementoMap = Collections.unmodifiableMap( namedProcedureCalls );
 	}
 
 
 	public NamedQueryDefinition getNamedQueryDefinition(String queryName) {
 		return namedQueryDefinitionMap.get( queryName );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQueryDefinition(String queryName) {
 		return namedSqlQueryDefinitionMap.get( queryName );
 	}
 
 	public ProcedureCallMemento getNamedProcedureCallMemento(String name) {
 		return procedureCallMementoMap.get( name );
 	}
 
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
 
 	public synchronized void registerNamedProcedureCallMemento(String name, ProcedureCallMemento memento) {
 		final Map<String, ProcedureCallMemento> copy = CollectionHelper.makeCopy( procedureCallMementoMap );
 		final ProcedureCallMemento previous = copy.put( name, memento );
 		if ( previous != null ) {
 			log.debugf(
 					"registering named procedure call definition [%s] overriding previously registered definition [%s]",
 					name,
 					previous
 			);
 		}
 
 		this.procedureCallMementoMap = Collections.unmodifiableMap( copy );
 	}
 
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
index 3f191d8a8b..eafe6aa75a 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1773 +1,1773 @@
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
 import java.util.List;
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
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
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
 import org.hibernate.procedure.ProcedureCallMemento;
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
 				cfg.getSqlResultSetMappings().values(),
 				toProcedureCallMementos( cfg.getNamedProcedureCallMap(), cfg.getSqlResultSetMappings() )
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
 
-	private List<ProcedureCallMemento> toProcedureCallMementos(
+	private Map<String, ProcedureCallMemento> toProcedureCallMementos(
 			Map<String, NamedProcedureCallDefinition> definitions,
 			Map<String, ResultSetMappingDefinition> resultSetMappingMap) {
-		final List<ProcedureCallMemento> rtn = new ArrayList<ProcedureCallMemento>();
+		final Map<String, ProcedureCallMemento> rtn = new HashMap<String, ProcedureCallMemento>();
 		if ( definitions != null ) {
-			for ( NamedProcedureCallDefinition definition : definitions.values() ) {
-				rtn.add( definition.toMemento( this, resultSetMappingMap ) );
+			for (String name : definitions.keySet()){
+				rtn.put( name,  definitions.get( name ).toMemento( this, resultSetMappingMap ));
 			}
 		}
 		return rtn;
 	}
 
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
 				metadata.getResultSetMappingDefinitions(),
-				null
+				new HashMap<String, ProcedureCallMemento>(  )
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
diff --git a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java
index 456198a494..90d29f5ff1 100644
--- a/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/procedure/internal/ProcedureCallMementoImpl.java
@@ -1,179 +1,179 @@
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
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.Session;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of ProcedureCallMemento
  *
  * @author Steve Ebersole
  */
 public class ProcedureCallMementoImpl implements ProcedureCallMemento {
 	private final String procedureName;
 	private final NativeSQLQueryReturn[] queryReturns;
 
 	private final ParameterStrategy parameterStrategy;
 	private final List<ParameterMemento> parameterDeclarations;
 
 	private final Set<String> synchronizedQuerySpaces;
 
-	private final Map<String,Object> hintsMap;
+	private final Map<String, Object> hintsMap;
 
 	/**
 	 * Constructs a ProcedureCallImpl
 	 *
 	 * @param procedureName The name of the procedure to be called
 	 * @param queryReturns The result mappings
 	 * @param parameterStrategy Are parameters named or positional?
 	 * @param parameterDeclarations The parameters registrations
 	 * @param synchronizedQuerySpaces Any query spaces to synchronize on execution
 	 * @param hintsMap Map of JPA query hints
 	 */
 	public ProcedureCallMementoImpl(
 			String procedureName,
 			NativeSQLQueryReturn[] queryReturns,
 			ParameterStrategy parameterStrategy,
 			List<ParameterMemento> parameterDeclarations,
 			Set<String> synchronizedQuerySpaces,
-			Map<String,Object> hintsMap) {
+			Map<String, Object> hintsMap) {
 		this.procedureName = procedureName;
 		this.queryReturns = queryReturns;
 		this.parameterStrategy = parameterStrategy;
 		this.parameterDeclarations = parameterDeclarations;
 		this.synchronizedQuerySpaces = synchronizedQuerySpaces;
 		this.hintsMap = hintsMap;
 	}
 
 	@Override
 	public ProcedureCall makeProcedureCall(Session session) {
 		return new ProcedureCallImpl( (SessionImplementor) session, this );
 	}
 
 	@Override
 	public ProcedureCall makeProcedureCall(SessionImplementor session) {
 		return new ProcedureCallImpl( session, this );
 	}
 
 	public String getProcedureName() {
 		return procedureName;
 	}
 
 	public NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns;
 	}
 
 	public ParameterStrategy getParameterStrategy() {
 		return parameterStrategy;
 	}
 
 	public List<ParameterMemento> getParameterDeclarations() {
 		return parameterDeclarations;
 	}
 
 	public Set<String> getSynchronizedQuerySpaces() {
 		return synchronizedQuerySpaces;
 	}
 
 	@Override
 	public Map<String, Object> getHintsMap() {
 		return hintsMap;
 	}
 
 	/**
 	 * A "disconnected" copy of the metadata for a parameter, that can be used in ProcedureCallMementoImpl.
 	 */
 	public static class ParameterMemento {
 		private final Integer position;
 		private final String name;
 		private final ParameterMode mode;
 		private final Class type;
 		private final Type hibernateType;
 
 		/**
 		 * Create the memento
 		 *
 		 * @param position The parameter position
 		 * @param name The parameter name
 		 * @param mode The parameter mode
 		 * @param type The Java type of the parameter
 		 * @param hibernateType The Hibernate Type.
 		 */
 		public ParameterMemento(int position, String name, ParameterMode mode, Class type, Type hibernateType) {
 			this.position = position;
 			this.name = name;
 			this.mode = mode;
 			this.type = type;
 			this.hibernateType = hibernateType;
 		}
 
 		public Integer getPosition() {
 			return position;
 		}
 
 		public String getName() {
 			return name;
 		}
 
 		public ParameterMode getMode() {
 			return mode;
 		}
 
 		public Class getType() {
 			return type;
 		}
 
 		public Type getHibernateType() {
 			return hibernateType;
 		}
 
 		/**
 		 * Build a ParameterMemento from the given parameter registration
 		 *
 		 * @param registration The parameter registration from a ProcedureCall
 		 *
 		 * @return The memento
 		 */
 		public static ParameterMemento fromRegistration(ParameterRegistrationImplementor registration) {
 			return new ParameterMemento(
 					registration.getPosition(),
 					registration.getName(),
 					registration.getMode(),
 					registration.getType(),
 					registration.getHibernateType()
 			);
 		}
 
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/QueryHints.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/QueryHints.java
index da1d05ea5b..86fe2e7e1f 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/QueryHints.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/QueryHints.java
@@ -1,109 +1,110 @@
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
 package org.hibernate.jpa;
 import java.util.HashSet;
 import java.util.Set;
+import static org.hibernate.annotations.QueryHints.*;
 
 /**
  * Defines the supported JPA query hints
  *
  * @author Steve Ebersole
  */
 public class QueryHints {
 	/**
 	 * The hint key for specifying a query timeout per Hibernate O/RM, which defines the timeout in seconds.
 	 *
 	 * @deprecated use {@link #SPEC_HINT_TIMEOUT} instead
 	 */
 	@Deprecated
-	public static final String HINT_TIMEOUT = "org.hibernate.timeout";
+	public static final String HINT_TIMEOUT = TIMEOUT_HIBERNATE;
 
 	/**
 	 * The hint key for specifying a query timeout per JPA, which defines the timeout in milliseconds
 	 */
-	public static final String SPEC_HINT_TIMEOUT = "javax.persistence.query.timeout";
+	public static final String SPEC_HINT_TIMEOUT = TIMEOUT_JPA;
 
 	/**
 	 * The hint key for specifying a comment which is to be embedded into the SQL sent to the database.
 	 */
-	public static final String HINT_COMMENT = "org.hibernate.comment";
+	public static final String HINT_COMMENT = COMMENT;
 
 	/**
 	 * The hint key for specifying a JDBC fetch size, used when executing the resulting SQL.
 	 */
-	public static final String HINT_FETCH_SIZE = "org.hibernate.fetchSize";
+	public static final String HINT_FETCH_SIZE = FETCH_SIZE;
 
 	/**
 	 * The hint key for specifying whether the query results should be cached for the next (cached) execution of the
 	 * "same query".
 	 */
-	public static final String HINT_CACHEABLE = "org.hibernate.cacheable";
+	public static final String HINT_CACHEABLE = CACHEABLE;
 
 	/**
 	 * The hint key for specifying the name of the cache region (within Hibernate's query result cache region)
 	 * to use for storing the query results.
 	 */
-	public static final String HINT_CACHE_REGION = "org.hibernate.cacheRegion";
+	public static final String HINT_CACHE_REGION = CACHE_REGION;
 
 	/**
 	 * The hint key for specifying that objects loaded into the persistence context as a result of this query execution
 	 * should be associated with the persistence context as read-only.
 	 */
-	public static final String HINT_READONLY = "org.hibernate.readOnly";
+	public static final String HINT_READONLY = READ_ONLY;
 
 	/**
 	 * The hint key for specifying the cache mode ({@link org.hibernate.CacheMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
-	public static final String HINT_CACHE_MODE = "org.hibernate.cacheMode";
+	public static final String HINT_CACHE_MODE = CACHE_MODE;
 
 	/**
 	 * The hint key for specifying the flush mode ({@link org.hibernate.FlushMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
-	public static final String HINT_FLUSH_MODE = "org.hibernate.flushMode";
+	public static final String HINT_FLUSH_MODE = FLUSH_MODE;
 
 	private static final Set<String> HINTS = buildHintsSet();
 
 	private static Set<String> buildHintsSet() {
 		HashSet<String> hints = new HashSet<String>();
 		hints.add( HINT_TIMEOUT );
 		hints.add( SPEC_HINT_TIMEOUT );
 		hints.add( HINT_COMMENT );
 		hints.add( HINT_FETCH_SIZE );
 		hints.add( HINT_CACHE_REGION );
 		hints.add( HINT_CACHEABLE );
 		hints.add( HINT_READONLY );
 		hints.add( HINT_CACHE_MODE );
 		hints.add( HINT_FLUSH_MODE );
 		return java.util.Collections.unmodifiableSet( hints );
 	}
 
 	public static Set<String> getDefinedHints() {
 		return HINTS;
 	}
 
 	protected QueryHints() {
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AbstractStoredProcedureTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AbstractStoredProcedureTest.java
new file mode 100644
index 0000000000..27ea7a3969
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AbstractStoredProcedureTest.java
@@ -0,0 +1,67 @@
+package org.hibernate.jpa.test.procedure;
+
+import java.util.List;
+import javax.persistence.EntityManager;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
+import org.hibernate.procedure.internal.ParameterStrategy;
+import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
+import org.hibernate.type.IntegerType;
+import org.hibernate.type.LongType;
+import org.hibernate.type.StringType;
+
+import org.junit.Test;
+
+import static org.junit.Assert.*;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+public abstract class AbstractStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
+	@Test
+	public void testNamedStoredProcedureBinding() {
+		EntityManager em = getOrCreateEntityManager();
+		SessionFactoryImplementor sf = em.getEntityManagerFactory().unwrap( SessionFactoryImplementor.class );
+		final ProcedureCallMementoImpl m1 = (ProcedureCallMementoImpl) sf.getNamedQueryRepository()
+				.getNamedProcedureCallMemento( "s1" );
+		assertNotNull( m1 );
+		assertEquals( "p1", m1.getProcedureName() );
+		assertEquals( ParameterStrategy.NAMED, m1.getParameterStrategy() );
+		List<ProcedureCallMementoImpl.ParameterMemento> list = m1.getParameterDeclarations();
+		assertEquals( 2, list.size() );
+		ProcedureCallMementoImpl.ParameterMemento memento = list.get( 0 );
+		assertEquals( "p11", memento.getName() );
+		assertEquals( javax.persistence.ParameterMode.IN, memento.getMode() );
+		assertEquals( IntegerType.INSTANCE, memento.getHibernateType() );
+		assertEquals( Integer.class, memento.getType() );
+
+		memento = list.get( 1 );
+		assertEquals( "p12", memento.getName() );
+		assertEquals( javax.persistence.ParameterMode.IN, memento.getMode() );
+		assertEquals( IntegerType.INSTANCE, memento.getHibernateType() );
+		assertEquals( Integer.class, memento.getType() );
+
+
+
+		final ProcedureCallMementoImpl m2 = (ProcedureCallMementoImpl) sf.getNamedQueryRepository()
+				.getNamedProcedureCallMemento( "s2" );
+		assertNotNull( m2 );
+		assertEquals( "p2", m2.getProcedureName() );
+		assertEquals( ParameterStrategy.POSITIONAL, m2.getParameterStrategy() );
+		list = m2.getParameterDeclarations();
+
+		memento = list.get( 0 );
+		assertEquals( Integer.valueOf( 0 ), memento.getPosition() );
+		assertEquals( javax.persistence.ParameterMode.INOUT, memento.getMode() );
+		assertEquals( StringType.INSTANCE, memento.getHibernateType() );
+		assertEquals( String.class, memento.getType() );
+
+		memento = list.get( 1 );
+		assertEquals( Integer.valueOf( 1 ), memento.getPosition() );
+		assertEquals( javax.persistence.ParameterMode.INOUT, memento.getMode() );
+		assertEquals( LongType.INSTANCE, memento.getHibernateType() );
+		assertEquals( Long.class, memento.getType() );
+
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AnnotationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AnnotationTest.java
new file mode 100644
index 0000000000..d9bdde87eb
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/AnnotationTest.java
@@ -0,0 +1,11 @@
+package org.hibernate.jpa.test.procedure;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+public class AnnotationTest extends AbstractStoredProcedureTest {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { User.class };
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/OrmTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/OrmTest.java
new file mode 100644
index 0000000000..0c12e35d55
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/OrmTest.java
@@ -0,0 +1,11 @@
+package org.hibernate.jpa.test.procedure;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+public class OrmTest extends AbstractStoredProcedureTest{
+	@Override
+	public String[] getEjb3DD() {
+		return new String[]{"org/hibernate/jpa/test/procedure/orm.xml"};
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/User.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/User.java
new file mode 100644
index 0000000000..9bcbd6101f
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/procedure/User.java
@@ -0,0 +1,77 @@
+package org.hibernate.jpa.test.procedure;
+
+import javax.persistence.Entity;
+import javax.persistence.EntityResult;
+import javax.persistence.FieldResult;
+import javax.persistence.Id;
+import javax.persistence.NamedStoredProcedureQueries;
+import javax.persistence.NamedStoredProcedureQuery;
+import javax.persistence.ParameterMode;
+import javax.persistence.SqlResultSetMapping;
+import javax.persistence.StoredProcedureParameter;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+@Entity
+@NamedStoredProcedureQueries(
+		{
+				@NamedStoredProcedureQuery(
+						name = "s1",
+						procedureName = "p1",
+						parameters = {
+								@StoredProcedureParameter(name = "p11",
+										mode = ParameterMode.IN,
+										type = Integer.class),
+								@StoredProcedureParameter(name = "p12",
+										mode = ParameterMode.IN,
+										type = Integer.class
+								)
+						},
+						resultClasses = { User.class }
+				),
+				@NamedStoredProcedureQuery(
+						name = "s2",
+						procedureName = "p2",
+						parameters = {
+								@StoredProcedureParameter(
+										mode = ParameterMode.INOUT,
+										type = String.class),
+								@StoredProcedureParameter(
+										mode = ParameterMode.INOUT,
+										type = Long.class)
+						},
+						resultSetMappings = { "srms" }
+
+				)
+		}
+)
+@SqlResultSetMapping(name = "srms",
+		entities = {
+				@EntityResult(entityClass = User.class, fields = {
+						@FieldResult(name = "id", column = "order_id"),
+						@FieldResult(name = "name", column = "order_item")
+				})
+		}
+)
+public class User {
+	@Id
+	private int id;
+	private String name;
+
+	public int getId() {
+		return id;
+	}
+
+	public void setId(int id) {
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
+}
diff --git a/hibernate-entitymanager/src/test/resources/org/hibernate/jpa/test/procedure/orm.xml b/hibernate-entitymanager/src/test/resources/org/hibernate/jpa/test/procedure/orm.xml
new file mode 100644
index 0000000000..684a7a5d71
--- /dev/null
+++ b/hibernate-entitymanager/src/test/resources/org/hibernate/jpa/test/procedure/orm.xml
@@ -0,0 +1,29 @@
+<?xml version="1.0" encoding="UTF-8"?>
+<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
+                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
+                 version="2.1"
+        >
+    <package>org.hibernate.jpa.test.procedure</package>
+    <entity class="User" metadata-complete="true">
+        <named-stored-procedure-query name="s1" procedure-name="p1">
+            <parameter class="java.lang.Integer" mode="IN" name="p11"/>
+            <parameter class="java.lang.Integer" mode="IN" name="p12"/>
+        </named-stored-procedure-query>
+        <named-stored-procedure-query name="s2" procedure-name="p2">
+            <parameter class="java.lang.String" mode="INOUT"/>
+            <parameter class="java.lang.Long" mode="INOUT"/>
+        </named-stored-procedure-query>
+        <sql-result-set-mapping name="srms">
+            <entity-result entity-class="User">
+                <field-result name="id" column="order_id"/>
+                <field-result name="name" column="order_item"/>
+            </entity-result>
+        </sql-result-set-mapping>
+        <attributes>
+            <id name="id">
+                <column name="fld_id"/>
+            </id>
+            <basic name="name"/>
+        </attributes>
+    </entity>
+</entity-mappings>
\ No newline at end of file
