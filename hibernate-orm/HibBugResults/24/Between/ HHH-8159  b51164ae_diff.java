diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index aa940d9c27..ba368b43c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,2571 +1,2572 @@
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
 
 import org.jboss.logging.Logger;
 
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
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
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
-			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
+			rootClass.setOptimisticLockStyle( OptimisticLockStyle.VERSION );
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
 
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					column.addUniqueKey( column.getTable().getNaturalIdUniqueKeyName(), inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					column.addUniqueKey( column.getTable().getNaturalIdUniqueKeyName(), inSecondPass );
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
 				comp, subpath,
 				inferredData, propertyHolder, mappings
 		);
 
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
 			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 8422356dd3..0062fad513 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1,1691 +1,1690 @@
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
 
 import java.util.ArrayList;
-import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.MappingException;
-import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Array;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.Fetchable;
 import org.hibernate.mapping.Filterable;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierBag;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexBackref;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.Map;
 import org.hibernate.mapping.MetaAttribute;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.PrimitiveArray;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Set;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UnionSubclass;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.mapping.Value;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 /**
  * Walks an XML mapping document and produces the Hibernate configuration-time metamodel (the
  * classes in the <tt>mapping</tt> package)
  *
  * @author Gavin King
  */
 public final class HbmBinder {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HbmBinder.class.getName());
 
 	/**
 	 * Private constructor to disallow instantiation.
 	 */
 	private HbmBinder() {
 	}
 
 	/**
 	 * The main contract into the hbm.xml-based binder. Performs necessary binding operations
 	 * represented by the given DOM.
 	 *
 	 * @param metadataXml The DOM to be parsed and bound.
 	 * @param mappings Current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @param entityNames Any state
 	 *
 	 * @throws MappingException
 	 */
 	public static void bindRoot(
 			XmlDocument metadataXml,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			java.util.Set<String> entityNames) throws MappingException {
 
 		final Document doc = metadataXml.getDocumentTree();
 		final Element hibernateMappingElement = doc.getRootElement();
 
 		java.util.List<String> names = HbmBinder.getExtendsNeeded( metadataXml, mappings );
 		if ( !names.isEmpty() ) {
 			// classes mentioned in extends not available - so put it in queue
 			Attribute packageAttribute = hibernateMappingElement.attribute( "package" );
 			String packageName = packageAttribute == null ? null : packageAttribute.getValue();
 			for ( String name : names ) {
 				mappings.addToExtendsQueue( new ExtendsQueueEntry( name, packageName, metadataXml, entityNames ) );
 			}
 			return;
 		}
 
 		// get meta's from <hibernate-mapping>
 		inheritedMetas = getMetas( hibernateMappingElement, inheritedMetas, true );
 		extractRootAttributes( hibernateMappingElement, mappings );
 
 		Iterator rootChildren = hibernateMappingElement.elementIterator();
 		while ( rootChildren.hasNext() ) {
 			final Element element = (Element) rootChildren.next();
 			final String elementName = element.getName();
 
 			if ( "filter-def".equals( elementName ) ) {
 				parseFilterDef( element, mappings );
 			}
 			else if ( "fetch-profile".equals( elementName ) ) {
 				parseFetchProfile( element, mappings, null );
 			}
 			else if ( "identifier-generator".equals( elementName ) ) {
 				parseIdentifierGeneratorRegistration( element, mappings );
 			}
 			else if ( "typedef".equals( elementName ) ) {
 				bindTypeDef( element, mappings );
 			}
 			else if ( "class".equals( elementName ) ) {
 				RootClass rootclass = new RootClass();
 				bindRootClass( element, rootclass, mappings, inheritedMetas );
 				mappings.addClass( rootclass );
 			}
 			else if ( "subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "query".equals( elementName ) ) {
 				bindNamedQuery( element, null, mappings );
 			}
 			else if ( "sql-query".equals( elementName ) ) {
 				bindNamedSQLQuery( element, null, mappings );
 			}
 			else if ( "resultset".equals( elementName ) ) {
 				bindResultSetMappingDefinition( element, null, mappings );
 			}
 			else if ( "import".equals( elementName ) ) {
 				bindImport( element, mappings );
 			}
 			else if ( "database-object".equals( elementName ) ) {
 				bindAuxiliaryDatabaseObject( element, mappings );
 			}
 		}
 	}
 
 	private static void parseIdentifierGeneratorRegistration(Element element, Mappings mappings) {
 		String strategy = element.attributeValue( "name" );
 		if ( StringHelper.isEmpty( strategy ) ) {
 			throw new MappingException( "'name' attribute expected for identifier-generator elements" );
 		}
 		String generatorClassName = element.attributeValue( "class" );
 		if ( StringHelper.isEmpty( generatorClassName ) ) {
 			throw new MappingException( "'class' attribute expected for identifier-generator [identifier-generator@name=" + strategy + "]" );
 		}
 
 		try {
 			Class generatorClass = ReflectHelper.classForName( generatorClassName );
 			mappings.getIdentifierGeneratorFactory().register( strategy, generatorClass );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new MappingException( "Unable to locate identifier-generator class [name=" + strategy + ", class=" + generatorClassName + "]" );
 		}
 
 	}
 
 	private static void bindImport(Element importNode, Mappings mappings) {
 		String className = getClassName( importNode.attribute( "class" ), mappings );
 		Attribute renameNode = importNode.attribute( "rename" );
 		String rename = ( renameNode == null ) ?
 						StringHelper.unqualify( className ) :
 						renameNode.getValue();
 		LOG.debugf( "Import: %s -> %s", rename, className );
 		mappings.addImport( className, rename );
 	}
 
 	private static void bindTypeDef(Element typedefNode, Mappings mappings) {
 		String typeClass = typedefNode.attributeValue( "class" );
 		String typeName = typedefNode.attributeValue( "name" );
 		Iterator paramIter = typedefNode.elementIterator( "param" );
 		Properties parameters = new Properties();
 		while ( paramIter.hasNext() ) {
 			Element param = (Element) paramIter.next();
 			parameters.setProperty( param.attributeValue( "name" ), param.getTextTrim() );
 		}
 		mappings.addTypeDef( typeName, typeClass, parameters );
 	}
 
 	private static void bindAuxiliaryDatabaseObject(Element auxDbObjectNode, Mappings mappings) {
 		AuxiliaryDatabaseObject auxDbObject = null;
 		Element definitionNode = auxDbObjectNode.element( "definition" );
 		if ( definitionNode != null ) {
 			try {
 				auxDbObject = ( AuxiliaryDatabaseObject ) ReflectHelper
 						.classForName( definitionNode.attributeValue( "class" ) )
 						.newInstance();
 			}
 			catch( ClassNotFoundException e ) {
 				throw new MappingException(
 						"could not locate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 			catch( Throwable t ) {
 				throw new MappingException(
 						"could not instantiate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 		}
 		else {
 			auxDbObject = new SimpleAuxiliaryDatabaseObject(
 					auxDbObjectNode.elementTextTrim( "create" ),
 					auxDbObjectNode.elementTextTrim( "drop" )
 				);
 		}
 
 		Iterator dialectScopings = auxDbObjectNode.elementIterator( "dialect-scope" );
 		while ( dialectScopings.hasNext() ) {
 			Element dialectScoping = ( Element ) dialectScopings.next();
 			auxDbObject.addDialectScope( dialectScoping.attributeValue( "name" ) );
 		}
 
 		mappings.addAuxiliaryDatabaseObject( auxDbObject );
 	}
 
 	private static void extractRootAttributes(Element hmNode, Mappings mappings) {
 		Attribute schemaNode = hmNode.attribute( "schema" );
 		mappings.setSchemaName( ( schemaNode == null ) ? null : schemaNode.getValue() );
 
 		Attribute catalogNode = hmNode.attribute( "catalog" );
 		mappings.setCatalogName( ( catalogNode == null ) ? null : catalogNode.getValue() );
 
 		Attribute dcNode = hmNode.attribute( "default-cascade" );
 		mappings.setDefaultCascade( ( dcNode == null ) ? "none" : dcNode.getValue() );
 
 		Attribute daNode = hmNode.attribute( "default-access" );
 		mappings.setDefaultAccess( ( daNode == null ) ? "property" : daNode.getValue() );
 
 		Attribute dlNode = hmNode.attribute( "default-lazy" );
 		mappings.setDefaultLazy( dlNode == null || dlNode.getValue().equals( "true" ) );
 
 		Attribute aiNode = hmNode.attribute( "auto-import" );
 		mappings.setAutoImport( ( aiNode == null ) || "true".equals( aiNode.getValue() ) );
 
 		Attribute packNode = hmNode.attribute( "package" );
 		if ( packNode != null ) mappings.setDefaultPackage( packNode.getValue() );
 	}
 
 	/**
 	 * Responsible for performing the bind operation related to an &lt;class/&gt; mapping element.
 	 *
 	 * @param node The DOM Element for the &lt;class/&gt; element.
 	 * @param rootClass The mapping instance to which to bind the information.
 	 * @param mappings The current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @throws MappingException
 	 */
 	public static void bindRootClass(Element node, RootClass rootClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		bindClass( node, rootClass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <class>
 		bindRootPersistentClassCommonValues( node, inheritedMetas, mappings, rootClass );
 	}
 
 	private static void bindRootPersistentClassCommonValues(Element node,
 			java.util.Map inheritedMetas, Mappings mappings, RootClass entity)
 			throws MappingException {
 
 		// DB-OBJECTNAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( entity, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 		        entity.isAbstract() != null && entity.isAbstract()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class: %s -> %s", entity.getEntityName(), entity.getTable().getName() );
 		}
 
 		// MUTABLE
 		Attribute mutableNode = node.attribute( "mutable" );
 		entity.setMutable( ( mutableNode == null ) || mutableNode.getValue().equals( "true" ) );
 
 		// WHERE
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) entity.setWhere( whereNode.getValue() );
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) table.addCheckConstraint( chNode.getValue() );
 
 		// POLYMORPHISM
 		Attribute polyNode = node.attribute( "polymorphism" );
 		entity.setExplicitPolymorphism( ( polyNode != null )
 			&& polyNode.getValue().equals( "explicit" ) );
 
 		// ROW ID
 		Attribute rowidNode = node.attribute( "rowid" );
 		if ( rowidNode != null ) table.setRowId( rowidNode.getValue() );
 
 		Iterator subnodes = node.elementIterator();
 		while ( subnodes.hasNext() ) {
 
 			Element subnode = (Element) subnodes.next();
 			String name = subnode.getName();
 
 			if ( "id".equals( name ) ) {
 				// ID
 				bindSimpleId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "composite-id".equals( name ) ) {
 				// COMPOSITE-ID
 				bindCompositeId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "version".equals( name ) || "timestamp".equals( name ) ) {
 				// VERSION / TIMESTAMP
 				bindVersioningProperty( table, subnode, mappings, name, entity, inheritedMetas );
 			}
 			else if ( "discriminator".equals( name ) ) {
 				// DISCRIMINATOR
 				bindDiscriminatorProperty( table, entity, subnode, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				entity.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				entity.setCacheRegionName( subnode.attributeValue( "region" ) );
 				entity.setLazyPropertiesCacheable( !"non-lazy".equals( subnode.attributeValue( "include" ) ) );
 			}
 
 		}
 
 		// Primary key constraint
 		entity.createPrimaryKey();
 
 		createClassProperties( node, entity, mappings, inheritedMetas );
 	}
 
 	private static void bindSimpleId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 
 		SimpleValue id = new SimpleValue( mappings, entity.getTable() );
 		entity.setIdentifier( id );
 
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
 
 		if ( propertyName == null ) {
 			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		}
 		else {
 			bindSimpleValue( idNode, id, false, propertyName, mappings );
 		}
 
 		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 			if ( !id.isTypeSpecified() ) {
 				throw new MappingException( "must specify an identifier type: "
 					+ entity.getEntityName() );
 			}
 		}
 		else {
 			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 		}
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 			entity.setDeclaredIdentifierProperty( prop );
 		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 		Component id = new Component( mappings, entity );
 		entity.setIdentifier( id );
 		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 		if ( propertyName == null ) {
 			entity.setEmbeddedIdentifier( id.isEmbedded() );
 			if ( id.isEmbedded() ) {
 				// todo : what is the implication of this?
 				id.setDynamic( !entity.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 			entity.setDeclaredIdentifierProperty( prop );
 		}
 
 		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private static void bindVersioningProperty(Table table, Element subnode, Mappings mappings,
 			String name, RootClass entity, java.util.Map inheritedMetas) {
 
 		String propertyName = subnode.attributeValue( "name" );
 		SimpleValue val = new SimpleValue( mappings, table );
 		bindSimpleValue( subnode, val, false, propertyName, mappings );
 		if ( !val.isTypeSpecified() ) {
 			// this is either a <version/> tag with no type attribute,
 			// or a <timestamp/> tag
 			if ( "version".equals( name ) ) {
 				val.setTypeName( "integer" );
 			}
 			else {
 				if ( "db".equals( subnode.attributeValue( "source" ) ) ) {
 					val.setTypeName( "dbtimestamp" );
 				}
 				else {
 					val.setTypeName( "timestamp" );
 				}
 			}
 		}
 		Property prop = new Property();
 		prop.setValue( val );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getGeneration() == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		makeVersion( subnode, val );
 		entity.setVersion( prop );
 		entity.addProperty( prop );
 	}
 
 	private static void bindDiscriminatorProperty(Table table, RootClass entity, Element subnode,
 			Mappings mappings) {
 		SimpleValue discrim = new SimpleValue( mappings, table );
 		entity.setDiscriminator( discrim );
 		bindSimpleValue(
 				subnode,
 				discrim,
 				false,
 				RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
 				mappings
 			);
 		if ( !discrim.isTypeSpecified() ) {
 			discrim.setTypeName( "string" );
 			// ( (Column) discrim.getColumnIterator().next() ).setType(type);
 		}
 		entity.setPolymorphic( true );
 		final String explicitForceValue = subnode.attributeValue( "force" );
 		boolean forceDiscriminatorInSelects = explicitForceValue == null
 				? mappings.forceDiscriminatorInSelectsByDefault()
 				: "true".equals( explicitForceValue );
 		entity.setForceDiscriminator( forceDiscriminatorInSelects );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) ) {
 			entity.setDiscriminatorInsertable( false );
 		}
 	}
 
 	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		// transfer an explicitly defined entity name
 		// handle the lazy attribute
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean lazy = lazyNode == null ?
 				mappings.isDefaultLazy() :
 				"true".equals( lazyNode.getValue() );
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		persistentClass.setLazy( lazy );
 
 		String entityName = node.attributeValue( "entity-name" );
 		if ( entityName == null ) entityName = getClassName( node.attribute("name"), mappings );
 		if ( entityName==null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 		persistentClass.setEntityName( entityName );
 		persistentClass.setJpaEntityName( StringHelper.unqualify( entityName ) );
 
 		bindPojoRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindDom4jRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindMapRepresentation( node, persistentClass, mappings, inheritedMetas );
 
 		Iterator itr = node.elementIterator( "fetch-profile" );
 		while ( itr.hasNext() ) {
 			final Element profileElement = ( Element ) itr.next();
 			parseFetchProfile( profileElement, mappings, entityName );
 		}
 
 		bindPersistentClassCommonValues( node, persistentClass, mappings, inheritedMetas );
 	}
 
 	private static void bindPojoRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map metaTags) {
 
 		String className = getClassName( node.attribute( "name" ), mappings );
 		String proxyName = getClassName( node.attribute( "proxy" ), mappings );
 
 		entity.setClassName( className );
 
 		if ( proxyName != null ) {
 			entity.setProxyInterfaceName( proxyName );
 			entity.setLazy( true );
 		}
 		else if ( entity.isLazy() ) {
 			entity.setProxyInterfaceName( className );
 		}
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.POJO, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindDom4jRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = StringHelper.unqualify( entity.getEntityName() );
 		entity.setNodeName(nodeName);
 
 //		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 //		if ( tuplizer != null ) {
 //			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 //		}
 	}
 
 	private static void bindMapRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.MAP, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 * @return The tuplizer element, or null.
 	 */
 	private static Element locateTuplizerDefinition(Element container, EntityMode entityMode) {
 		Iterator itr = container.elements( "tuplizer" ).iterator();
 		while( itr.hasNext() ) {
 			final Element tuplizerElem = ( Element ) itr.next();
 			if ( entityMode.toString().equals( tuplizerElem.attributeValue( "entity-mode") ) ) {
 				return tuplizerElem;
 			}
 		}
 		return null;
 	}
 
 	private static void bindPersistentClassCommonValues(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		// DISCRIMINATOR
 		Attribute discriminatorNode = node.attribute( "discriminator-value" );
 		entity.setDiscriminatorValue( ( discriminatorNode == null )
 			? entity.getEntityName()
 			: discriminatorNode.getValue() );
 
 		// DYNAMIC UPDATE
 		Attribute dynamicNode = node.attribute( "dynamic-update" );
 		entity.setDynamicUpdate(
 				dynamicNode != null && "true".equals( dynamicNode.getValue() )
 		);
 
 		// DYNAMIC INSERT
 		Attribute insertNode = node.attribute( "dynamic-insert" );
 		entity.setDynamicInsert(
 				insertNode != null && "true".equals( insertNode.getValue() )
 		);
 
 		// IMPORT
 		mappings.addImport( entity.getEntityName(), entity.getEntityName() );
 		if ( mappings.isAutoImport() && entity.getEntityName().indexOf( '.' ) > 0 ) {
 			mappings.addImport(
 					entity.getEntityName(),
 					StringHelper.unqualify( entity.getEntityName() )
 				);
 		}
 
 		// BATCH SIZE
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) entity.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 
 		// SELECT BEFORE UPDATE
 		Attribute sbuNode = node.attribute( "select-before-update" );
 		if ( sbuNode != null ) entity.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
 
 		// OPTIMISTIC LOCK MODE
 		Attribute olNode = node.attribute( "optimistic-lock" );
-		entity.setOptimisticLockMode( getOptimisticLockMode( olNode ) );
+		entity.setOptimisticLockStyle( getOptimisticLockStyle( olNode ) );
 
 		entity.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName(
 						persisterNode
 								.getValue()
 				) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, entity );
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			entity.addSynchronizedTable( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Attribute abstractNode = node.attribute( "abstract" );
 		Boolean isAbstract = abstractNode == null
 				? null
 		        : "true".equals( abstractNode.getValue() )
 						? Boolean.TRUE
 	                    : "false".equals( abstractNode.getValue() )
 								? Boolean.FALSE
 	                            : null;
 		entity.setAbstract( isAbstract );
 	}
 
 	private static void handleCustomSQL(Element node, PersistentClass model)
 			throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "loader" );
 		if ( element != null ) {
 			model.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Join model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Collection model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete-all" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDeleteAll( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static boolean isCallable(Element e) throws MappingException {
 		return isCallable( e, true );
 	}
 
 	private static boolean isCallable(Element element, boolean supportsCallable)
 			throws MappingException {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
 		Attribute attr = element.attribute( "check" );
 		if ( attr == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table denormalizedSuperTable = unionSubclass.getSuperclass().getTable();
 		Table mytable = mappings.addDenormalizedTable(
 				schema,
 				catalog,
 				getClassTableName(unionSubclass, node, schema, catalog, denormalizedSuperTable, mappings ),
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName() );
 		}
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 		}
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model,
 			Element node,
 			String schema,
 			String catalog,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
 			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table mytable = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( joinedSubclass, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 				false
 			);
 		joinedSubclass.setTable( mytable );
 		bindComment(mytable, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, mytable, joinedSubclass.getIdentifier() );
 		joinedSubclass.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, joinedSubclass.getEntityName(), mappings );
 
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		joinedSubclass.createPrimaryKey();
 		joinedSubclass.createForeignKey();
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) mytable.addCheckConstraint( chNode.getValue() );
 
 		// properties
 		createClassProperties( node, joinedSubclass, mappings, inheritedMetas );
 
 	}
 
 	private static void bindJoin(Element node, Join join, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		PersistentClass persistentClass = join.getPersistentClass();
 		String path = persistentClass.getEntityName();
 
 		// TABLENAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 		Table primaryTable = persistentClass.getTable();
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( persistentClass, node, schema, catalog, primaryTable, mappings ),
 				getSubselect( node ),
 				false
 			);
 		join.setTable( table );
 		bindComment(table, node);
 
 		Attribute fetchNode = node.attribute( "fetch" );
 		if ( fetchNode != null ) {
 			join.setSequentialSelect( "select".equals( fetchNode.getValue() ) );
 		}
 
 		Attribute invNode = node.attribute( "inverse" );
 		if ( invNode != null ) {
 			join.setInverse( "true".equals( invNode.getValue() ) );
 		}
 
 		Attribute nullNode = node.attribute( "optional" );
 		if ( nullNode != null ) {
 			join.setOptional( "true".equals( nullNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, table, persistentClass.getIdentifier() );
 		join.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, persistentClass.getEntityName(), mappings );
 
 		// join.getKey().setType( new Type( lazz.getIdentifier() ) );
 		join.createPrimaryKey();
 		join.createForeignKey();
 
 		// PROPERTIES
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			Value value = null;
 			if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, true, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, true, propertyName, mappings );
 			}
 			else if ( "component".equals( name ) || "dynamic-component".equals( name ) ) {
 				String subpath = StringHelper.qualify( path, propertyName );
 				value = new Component( mappings, join );
 				bindComponent(
 						subnode,
 						(Component) value,
 						join.getPersistentClass().getClassName(),
 						propertyName,
 						subpath,
 						true,
 						false,
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 
 			if ( value != null ) {
 				Property prop = createProperty( value, propertyName, persistentClass
 					.getEntityName(), subnode, mappings, inheritedMetas );
 				prop.setOptional( join.isOptional() );
 				join.addProperty( prop );
 			}
 
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, join );
 
 	}
 
 	public static void bindColumns(final Element node, final SimpleValue simpleValue,
 			final boolean isNullable, final boolean autoColumn, final String propertyPath,
 			final Mappings mappings) throws MappingException {
 
 		Table table = simpleValue.getTable();
 
 		// COLUMN(S)
 		Attribute columnAttribute = node.attribute( "column" );
 		if ( columnAttribute == null ) {
 			Iterator itr = node.elementIterator();
 			int count = 0;
 			while ( itr.hasNext() ) {
 				Element columnElement = (Element) itr.next();
 				if ( columnElement.getName().equals( "column" ) ) {
 					Column column = new Column();
 					column.setValue( simpleValue );
 					column.setTypeIndex( count++ );
 					bindColumn( columnElement, column, isNullable );
 					final String columnName = columnElement.attributeValue( "name" );
 					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 							columnName, propertyPath
 					);
 					column.setName( mappings.getNamingStrategy().columnName(
 						columnName ) );
 					if ( table != null ) {
 						table.addColumn( column ); // table=null -> an association
 						                           // - fill it in later
 						//TODO fill in the mappings for table == null
 						mappings.addColumnBinding( logicalColumnName, column, table );
 					}
 
 
 					simpleValue.addColumn( column );
 					// column index
 					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
 					bindIndex( node.attribute( "index" ), table, column, mappings );
 					//column unique-key
 					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
 					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 				}
 				else if ( columnElement.getName().equals( "formula" ) ) {
 					Formula formula = new Formula();
 					formula.setFormula( columnElement.getText() );
 					simpleValue.addFormula( formula );
 				}
 			}
 
 			// todo : another GoodThing would be to go back after all parsing and see if all the columns
 			// (and no formulas) are contained in a defined unique key that only contains these columns.
 			// That too would mark this as a logical one-to-one
 			final Attribute uniqueAttribute = node.attribute( "unique" );
 			if ( uniqueAttribute != null
 					&& "true".equals( uniqueAttribute.getValue() )
 					&& ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 		}
 		else {
 			if ( node.elementIterator( "column" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <column> subelement" );
 			}
 			if ( node.elementIterator( "formula" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <formula> subelement" );
 			}
 
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			if ( column.isUnique() && ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 			final String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			column.setName( mappings.getNamingStrategy().columnName( columnName ) );
 			if ( table != null ) {
 				table.addColumn( column ); // table=null -> an association - fill
 				                           // it in later
 				//TODO fill in the mappings for table == null
 				mappings.addColumnBinding( logicalColumnName, column, table );
 			}
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmMetadataSourceProcessorImpl does not support the
 			 */
 			simpleValue.getTable().addColumn( column );
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 	}
 
 	private static void bindIndex(Attribute indexAttribute, Table table, Column column, Mappings mappings) {
 		if ( indexAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateIndex( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	private static void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column, Mappings mappings) {
 		if ( uniqueKeyAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateUniqueKey( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	// automatically makes a column with the default name if none is specifed by XML
 	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
 			String path, Mappings mappings) throws MappingException {
 		bindSimpleValueType( node, simpleValue, mappings );
 
 		bindColumnsOrFormula( node, simpleValue, path, isNullable, mappings );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
 	}
 
 	private static void bindSimpleValueType(Element node, SimpleValue simpleValue, Mappings mappings)
 			throws MappingException {
 		String typeName = null;
 
 		Properties parameters = new Properties();
 
 		Attribute typeNode = node.attribute( "type" );
         if ( typeNode == null ) {
             typeNode = node.attribute( "id-type" ); // for an any
         }
         else {
             typeName = typeNode.getValue();
         }
 
 		Element typeChild = node.element( "type" );
 		if ( typeName == null && typeChild != null ) {
 			typeName = typeChild.attribute( "name" ).getValue();
 			Iterator typeParameters = typeChild.elementIterator( "param" );
 
 			while ( typeParameters.hasNext() ) {
 				Element paramElement = (Element) typeParameters.next();
 				parameters.setProperty(
 						paramElement.attributeValue( "name" ),
 						paramElement.getTextTrim()
 					);
 			}
 		}
 
 		resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);
 	}
 
 	private static void resolveAndBindTypeDef(SimpleValue simpleValue,
 			Mappings mappings, String typeName, Properties parameters) {
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}else if (typeName!=null && !mappings.isInSecondPass()){
 			BasicType basicType=mappings.getTypeResolver().basic(typeName);
 			if (basicType==null) {
 				/*
 				 * If the referenced typeName isn't a basic-type, it's probably a typedef defined 
 				 * in a mapping file not read yet.
 				 * It should be solved by deferring the resolution and binding of this type until 
 				 * all mapping files are read - the second passes.
 				 * Fixes issue HHH-7300
 				 */
 				SecondPass resolveUserTypeMappingSecondPass=new ResolveUserTypeMappingSecondPass(simpleValue,typeName,mappings,parameters);
 				mappings.addSecondPass(resolveUserTypeMappingSecondPass);
 			}
 		}
 
 		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
 
 		if ( typeName != null ) simpleValue.setTypeName( typeName );
 	}
 
 	public static void bindProperty(
 			Element node,
 	        Property property,
 	        Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		String propName = node.attributeValue( "name" );
 		property.setName( propName );
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = propName;
 		property.setNodeName( nodeName );
 
 		// TODO:
 		//Type type = model.getValue().getType();
 		//if (type==null) throw new MappingException(
 		//"Could not determine a property type for: " + model.getName() );
 
 		Attribute accessNode = node.attribute( "access" );
 		if ( accessNode != null ) {
 			property.setPropertyAccessorName( accessNode.getValue() );
 		}
 		else if ( node.getName().equals( "properties" ) ) {
 			property.setPropertyAccessorName( "embedded" );
 		}
 		else {
 			property.setPropertyAccessorName( mappings.getDefaultAccess() );
 		}
 
 		Attribute cascadeNode = node.attribute( "cascade" );
 		property.setCascade( cascadeNode == null ? mappings.getDefaultCascade() : cascadeNode
 			.getValue() );
 
 		Attribute updateNode = node.attribute( "update" );
 		property.setUpdateable( updateNode == null || "true".equals( updateNode.getValue() ) );
 
 		Attribute insertNode = node.attribute( "insert" );
 		property.setInsertable( insertNode == null || "true".equals( insertNode.getValue() ) );
 
 		Attribute lockNode = node.attribute( "optimistic-lock" );
 		property.setOptimisticLocked( lockNode == null || "true".equals( lockNode.getValue() ) );
 
 		Attribute generatedNode = node.attribute( "generated" );
         String generationName = generatedNode == null ? null : generatedNode.getValue();
         PropertyGeneration generation = PropertyGeneration.parse( generationName );
 		property.setGeneration( generation );
 
         if ( generation == PropertyGeneration.ALWAYS || generation == PropertyGeneration.INSERT ) {
 	        // generated properties can *never* be insertable...
 	        if ( property.isInsertable() ) {
 		        if ( insertNode == null ) {
 			        // insertable simply because that is the user did not specify
 			        // anything; just override it
 					property.setInsertable( false );
 		        }
 		        else {
 			        // the user specifically supplied insert="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both insert=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
 
 	        // properties generated on update can never be updateable...
 	        if ( property.isUpdateable() && generation == PropertyGeneration.ALWAYS ) {
 		        if ( updateNode == null ) {
 			        // updateable only because the user did not specify
 			        // anything; just override it
 			        property.setUpdateable( false );
 		        }
 		        else {
 			        // the user specifically supplied update="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
         }
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
 			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
 			fetchable.setLazy( true );
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness( node, fetchable, mappings, "proxy", defaultLazy );
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
@@ -1935,1286 +1934,1288 @@ public final class HbmBinder {
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName(StringHelper.randomFixedLengthHex("UK_"));
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( debugEnabled ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
 				.setQuery( query )
 				.setCacheable( cacheable )
 				.setCacheRegion( region )
 				.setTimeout( timeout )
 				.setFetchSize( fetchSize )
 				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
 				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
 				.setReadOnly( readOnly )
 				.setComment( comment )
 				.setParameterTypes( getParameterTypes( queryElem ) )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
-	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
+	private static OptimisticLockStyle getOptimisticLockStyle(Attribute olAtt) throws MappingException {
+		if ( olAtt == null ) {
+			return OptimisticLockStyle.VERSION;
+		}
 
-		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
-		String olMode = olAtt.getValue();
+		final String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
-			return Versioning.OPTIMISTIC_LOCK_VERSION;
+			return OptimisticLockStyle.VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
-			return Versioning.OPTIMISTIC_LOCK_DIRTY;
+			return OptimisticLockStyle.DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
-			return Versioning.OPTIMISTIC_LOCK_ALL;
+			return OptimisticLockStyle.ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
-			return Versioning.OPTIMISTIC_LOCK_NONE;
+			return OptimisticLockStyle.NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.build
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 	
 	private static class ResolveUserTypeMappingSecondPass implements SecondPass{
 
 		private SimpleValue simpleValue;
 		private String typeName;
 		private Mappings mappings;
 		private Properties parameters;
 
 		public ResolveUserTypeMappingSecondPass(SimpleValue simpleValue,
 				String typeName, Mappings mappings, Properties parameters) {
 			this.simpleValue=simpleValue;
 			this.typeName=typeName;
 			this.parameters=parameters;
 			this.mappings=mappings;
 		}
 
 		@Override
 		public void doSecondPass(java.util.Map persistentClasses)
 				throws MappingException {
 			resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);		
 		}
 		
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index a29cbda603..defd4096f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1007 +1,1008 @@
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
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 
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
 import org.hibernate.annotations.DynamicInsert;
 import org.hibernate.annotations.DynamicUpdate;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.NaturalIdCache;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.OptimisticLocking;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.Polymorphism;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.RowId;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.SelectBeforeUpdate;
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
+import org.hibernate.engine.OptimisticLockStyle;
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
 	private List<Filter> filters = new ArrayList<Filter>();
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
 
 	@SuppressWarnings("SimplifiableConditionalExpression")
 	private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
 		{
 			final DynamicInsert dynamicInsertAnn = annotatedClass.getAnnotation( DynamicInsert.class );
 			this.dynamicInsert = dynamicInsertAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicInsert() )
 					: dynamicInsertAnn.value();
 		}
 
 		{
 			final DynamicUpdate dynamicUpdateAnn = annotatedClass.getAnnotation( DynamicUpdate.class );
 			this.dynamicUpdate = dynamicUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicUpdate() )
 					: dynamicUpdateAnn.value();
 		}
 
 		{
 			final SelectBeforeUpdate selectBeforeUpdateAnn = annotatedClass.getAnnotation( SelectBeforeUpdate.class );
 			this.selectBeforeUpdate = selectBeforeUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.selectBeforeUpdate() )
 					: selectBeforeUpdateAnn.value();
 		}
 
 		{
 			final OptimisticLocking optimisticLockingAnn = annotatedClass.getAnnotation( OptimisticLocking.class );
 			this.optimisticLockType = optimisticLockingAnn == null
 					? ( hibAnn == null ? OptimisticLockType.VERSION : hibAnn.optimisticLock() )
 					: optimisticLockingAnn.type();
 		}
 
 		{
 			final Polymorphism polymorphismAnn = annotatedClass.getAnnotation( Polymorphism.class );
 			this.polymorphismType = polymorphismAnn == null
 					? ( hibAnn == null ? PolymorphismType.IMPLICIT : hibAnn.polymorphism() )
 					: polymorphismAnn.type();
 		}
 
 		if ( hibAnn != null ) {
 			// used later in bind for logging
 			explicitHibernateEntityAnnotation = true;
 			//persister handled in bind
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
 
 	public boolean isRootEntity() {
 		// This is the best option I can think of here since PersistentClass is most likely not yet fully populated
 		return persistentClass instanceof RootClass;
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
-		persistentClass.setOptimisticLockMode( getVersioning( optimisticLockType ) );
+		persistentClass.setOptimisticLockStyle( getVersioning( optimisticLockType ) );
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
 
 		for ( Filter filter : filters ) {
 			String filterName = filter.name();
 			String cond = filter.condition();
 			if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 				FilterDefinition definition = mappings.getFilterDefinition( filterName );
 				cond = definition == null ? null : definition.getDefaultFilterCondition();
 				if ( StringHelper.isEmpty( cond ) ) {
 					throw new AnnotationException(
 							"no filter condition found for filter " + filterName + " in " + this.name
 					);
 				}
 			}
 			persistentClass.addFilter(filterName, cond, filter.deduceAliasInjectionPoints(), 
 					toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
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
 
-	int getVersioning(OptimisticLockType type) {
+	OptimisticLockStyle getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
-				return Versioning.OPTIMISTIC_LOCK_VERSION;
+				return OptimisticLockStyle.VERSION;
 			case NONE:
-				return Versioning.OPTIMISTIC_LOCK_NONE;
+				return OptimisticLockStyle.NONE;
 			case DIRTY:
-				return Versioning.OPTIMISTIC_LOCK_DIRTY;
+				return OptimisticLockStyle.DIRTY;
 			case ALL:
-				return Versioning.OPTIMISTIC_LOCK_ALL;
+				return OptimisticLockStyle.ALL;
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
 
 		if ( secondaryTable != null ) {
 			TableBinder.addIndexes( table, secondaryTable.indexes(), mappings );
 		}
 
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
 	
 	public void setNaturalIdCache(XClass clazzToProcess, NaturalIdCache naturalIdCacheAnn) {
 		if ( naturalIdCacheAnn != null ) {
 			if ( BinderHelper.isEmptyAnnotationValue( naturalIdCacheAnn.region() ) ) {
 				if (cacheRegion != null) {
 					naturalIdCacheRegion = cacheRegion + NATURAL_ID_CACHE_SUFFIX;
 				}
 				else {
 					naturalIdCacheRegion = clazzToProcess.getName() + NATURAL_ID_CACHE_SUFFIX;
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
 
 	public void addFilter(Filter filter) {
 		filters.add(filter);
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
 	public void processComplementaryTableDefinitions(javax.persistence.Table table) {
 		if ( table == null ) return;
 		TableBinder.addIndexes( persistentClass.getTable(), table.indexes(), mappings );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java
index 8b801167b2..b0211d708f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java
@@ -1,48 +1,78 @@
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
 package org.hibernate.engine;
 
 /**
  * Describes how an entity should be optimistically locked.
  *
  * @author Steve Ebersole
  */
 public enum OptimisticLockStyle {
 	/**
 	 * no optimistic locking
 	 */
-	NONE,
+	NONE( -1 ),
 	/**
 	 * use a dedicated version column
 	 */
-	VERSION,
+	VERSION( 0 ),
 	/**
 	 * dirty columns are compared
 	 */
-	DIRTY,
+	DIRTY( 1 ),
 	/**
 	 * all columns are compared
 	 */
-	ALL
+	ALL( 2 );
+
+	private final int oldCode;
+
+	private OptimisticLockStyle(int oldCode) {
+		this.oldCode = oldCode;
+	}
+
+	public int getOldCode() {
+		return oldCode;
+	}
+
+	public static OptimisticLockStyle interpretOldCode(int oldCode) {
+		switch ( oldCode ) {
+			case -1: {
+				return NONE;
+			}
+			case 0: {
+				return VERSION;
+			}
+			case 1: {
+				return DIRTY;
+			}
+			case 2: {
+				return ALL;
+			}
+			default: {
+				throw new IllegalArgumentException( "Illegal legacy optimistic lock style code :" + oldCode );
+			}
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/NonNullableTransientDependencies.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/NonNullableTransientDependencies.java
index 105933915d..07fd256e16 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/NonNullableTransientDependencies.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/NonNullableTransientDependencies.java
@@ -1,82 +1,105 @@
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
 package org.hibernate.engine.internal;
 
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Tracks non-nullable transient entities that would cause a particular entity insert to fail.
  *
  * @author Gail Badner
  */
 public class NonNullableTransientDependencies {
-
 	// Multiple property paths can refer to the same transient entity, so use Set<String>
 	// for the map value.
-	private final Map<Object,Set<String>> propertyPathsByTransientEntity =
-			new IdentityHashMap<Object,Set<String>>();
+	private final Map<Object,Set<String>> propertyPathsByTransientEntity = new IdentityHashMap<Object,Set<String>>();
 
-	/* package-protected */
 	void add(String propertyName, Object transientEntity) {
 		Set<String> propertyPaths = propertyPathsByTransientEntity.get( transientEntity );
 		if ( propertyPaths == null ) {
 			propertyPaths = new HashSet<String>();
 			propertyPathsByTransientEntity.put( transientEntity, propertyPaths );
 		}
 		propertyPaths.add( propertyName );
 	}
 
 	public Iterable<Object> getNonNullableTransientEntities() {
 		return propertyPathsByTransientEntity.keySet();
 	}
 
+	/**
+	 * Retrieve the paths that refer to the transient entity
+	 *
+	 * @param entity The transient entity
+	 *
+	 * @return The property paths
+	 */
 	public Iterable<String> getNonNullableTransientPropertyPaths(Object entity) {
 		return propertyPathsByTransientEntity.get( entity );
 	}
 
+	/**
+	 * Are there any paths currently tracked here?
+	 *
+	 * @return {@code true} indicates there are no path tracked here currently
+	 */
 	public boolean isEmpty() {
 		return propertyPathsByTransientEntity.isEmpty();
 	}
 
+	/**
+	 * Clean up any tracked references for the given entity, throwing an exception if there were any paths.
+	 *
+	 * @param entity The entity
+	 *
+	 * @throws IllegalStateException If the entity had tracked paths
+	 */
 	public void resolveNonNullableTransientEntity(Object entity) {
 		if ( propertyPathsByTransientEntity.remove( entity ) == null ) {
 			throw new IllegalStateException( "Attempt to resolve a non-nullable, transient entity that is not a dependency." );
 		}
 	}
 
+	/**
+	 * Build a loggable representation of the paths tracked here at the moment.
+	 *
+	 * @param session The session (used to resolve entity names)
+	 *
+	 * @return The loggable representation
+	 */
 	public String toLoggableString(SessionImplementor session) {
-		StringBuilder sb = new StringBuilder( getClass().getSimpleName() ).append( '[' );
+		final StringBuilder sb = new StringBuilder( getClass().getSimpleName() ).append( '[' );
 		for ( Map.Entry<Object,Set<String>> entry : propertyPathsByTransientEntity.entrySet() ) {
 			sb.append( "transientEntityName=" ).append( session.bestGuessEntityName( entry.getKey() ) );
 			sb.append( " requiredBy=" ).append( entry.getValue() );
 		}
 		sb.append( ']' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
index f13ea17765..6b990d64cc 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
@@ -1,215 +1,217 @@
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
 package org.hibernate.engine.internal;
 
 import java.util.Iterator;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Implements the algorithm for validating property values for illegal null values
  * 
  * @author Gavin King
  */
 public final class Nullability {
-	
 	private final SessionImplementor session;
 	private final boolean checkNullability;
 
+	/**
+	 * Constructs a Nullability
+	 *
+	 * @param session The session
+	 */
 	public Nullability(SessionImplementor session) {
 		this.session = session;
 		this.checkNullability = session.getFactory().getSettings().isCheckNullability();
 	}
 	/**
 	 * Check nullability of the class persister properties
 	 *
 	 * @param values entity properties
 	 * @param persister class persister
 	 * @param isUpdate whether it is intended to be updated or saved
-	 * @throws org.hibernate.PropertyValueException Break the nullability of one property
+	 *
+	 * @throws PropertyValueException Break the nullability of one property
 	 * @throws HibernateException error while getting Component values
 	 */
 	public void checkNullability(
 			final Object[] values,
 			final EntityPersister persister,
-			final boolean isUpdate) 
-	throws PropertyValueException, HibernateException {
+			final boolean isUpdate) throws HibernateException {
 		/*
 		 * Typically when Bean Validation is on, we don't want to validate null values
 		 * at the Hibernate Core level. Hence the checkNullability setting.
 		 */
 		if ( checkNullability ) {
 			/*
 			  * Algorithm
 			  * Check for any level one nullability breaks
 			  * Look at non null components to
 			  *   recursively check next level of nullability breaks
 			  * Look at Collections contraining component to
 			  *   recursively check next level of nullability breaks
 			  *
 			  *
 			  * In the previous implementation, not-null stuffs where checked
 			  * filtering by level one only updateable
 			  * or insertable columns. So setting a sub component as update="false"
 			  * has no effect on not-null check if the main component had good checkeability
 			  * In this implementation, we keep this feature.
 			  * However, I never see any documentation mentioning that, but it's for
 			  * sure a limitation.
 			  */
 
 			final boolean[] nullability = persister.getPropertyNullability();
 			final boolean[] checkability = isUpdate ?
 				persister.getPropertyUpdateability() :
 				persister.getPropertyInsertability();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 
 			for ( int i = 0; i < values.length; i++ ) {
 
 				if ( checkability[i] && values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 					final Object value = values[i];
 					if ( !nullability[i] && value == null ) {
 
 						//check basic level one nullablilty
 						throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								persister.getPropertyNames()[i]
 							);
 
 					}
 					else if ( value != null ) {
-
 						//values is not null and is checkable, we'll look deeper
-						String breakProperties = checkSubElementsNullability( propertyTypes[i], value );
+						final String breakProperties = checkSubElementsNullability( propertyTypes[i], value );
 						if ( breakProperties != null ) {
 							throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								buildPropertyPath( persister.getPropertyNames()[i], breakProperties )
 							);
 						}
 
 					}
 				}
 
 			}
 		}
 	}
 
 	/**
 	 * check sub elements-nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param propertyType type to check
 	 * @param value value to check
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
-	private String checkSubElementsNullability(final Type propertyType, final Object value) 
-	throws HibernateException {
+	private String checkSubElementsNullability(final Type propertyType, final Object value)
+			throws HibernateException {
 		//for non null args, check for components and elements containing components
 		if ( propertyType.isComponentType() ) {
 			return checkComponentNullability( value, (CompositeType) propertyType );
 		}
 		else if ( propertyType.isCollectionType() ) {
 
 			//persistent collections may have components
-			CollectionType collectionType = (CollectionType) propertyType;
-			Type collectionElementType = collectionType.getElementType( session.getFactory() );
+			final CollectionType collectionType = (CollectionType) propertyType;
+			final Type collectionElementType = collectionType.getElementType( session.getFactory() );
 			if ( collectionElementType.isComponentType() ) {
 				//check for all components values in the collection
 
-				CompositeType componentType = (CompositeType) collectionElementType;
-				Iterator iter = CascadingActions.getLoadedElementsIterator( session, collectionType, value );
-				while ( iter.hasNext() ) {
-					Object compValue = iter.next();
-					if (compValue != null) {
-						return checkComponentNullability(compValue, componentType);
+				final CompositeType componentType = (CompositeType) collectionElementType;
+				final Iterator itr = CascadingActions.getLoadedElementsIterator( session, collectionType, value );
+				while ( itr.hasNext() ) {
+					Object compValue = itr.next();
+					if ( compValue != null ) {
+						return checkComponentNullability( compValue, componentType );
 					}
 				}
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * check component nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param value component properties
 	 * @param compType component not-nullable type
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkComponentNullability(final Object value, final CompositeType compType)
-	throws HibernateException {
+			throws HibernateException {
 		/* will check current level if some of them are not null
 		 * or sublevels if they exist
 		 */
-		boolean[] nullability = compType.getPropertyNullability();
+		final boolean[] nullability = compType.getPropertyNullability();
 		if ( nullability!=null ) {
 			//do the test
 			final Object[] values = compType.getPropertyValues( value, EntityMode.POJO );
 			final Type[] propertyTypes = compType.getSubtypes();
 			for ( int i=0; i<values.length; i++ ) {
 				final Object subvalue = values[i];
 				if ( !nullability[i] && subvalue==null ) {
 					return compType.getPropertyNames()[i];
 				}
 				else if ( subvalue != null ) {
-					String breakProperties = checkSubElementsNullability( propertyTypes[i], subvalue );
+					final String breakProperties = checkSubElementsNullability( propertyTypes[i], subvalue );
 					if ( breakProperties != null ) {
 						return buildPropertyPath( compType.getPropertyNames()[i], breakProperties );
 					}
-	 			}
-	 		}
+				}
+			}
 		}
 		return null;
 	}
 
 	/**
-	 * Return a well formed property path.
-	 * Basically, it will return parent.child
+	 * Return a well formed property path. Basically, it will return parent.child
 	 *
 	 * @param parent parent in path
 	 * @param child child in path
+	 *
 	 * @return parent-child path
 	 */
 	private static String buildPropertyPath(String parent, String child) {
-		return new StringBuilder( parent.length() + child.length() + 1 )
-			.append(parent).append('.').append(child).toString();
+		return parent + '.' + child;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
index 1926cf3d84..751c74d9a5 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
@@ -1,136 +1,167 @@
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
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes the commonality regarding binding of parameter values into PreparedStatements as this logic is
  * used in many places.
  * <p/>
  * Ideally would like to move to the parameter handling as it is done in the hql.ast package.
  *
  * @author Steve Ebersole
  */
 public class ParameterBinder {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			ParameterBinder.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ParameterBinder.class.getName());
-
+	/**
+	 * Helper contract for dealing with named parameters and resolving their locations
+	 */
 	public static interface NamedParameterSource {
+		/**
+		 * Retrieve the locations for the given parameter name
+		 *
+		 * @param name The parameter name
+		 *
+		 * @return The locations
+		 */
 		public int[] getNamedParameterLocations(String name);
 	}
 
 	private ParameterBinder() {
 	}
 
+	/**
+	 * Perform parameter binding
+	 *
+	 * @param st The statement to bind parameters to
+	 * @param queryParameters The parameters
+	 * @param start The initial bind position
+	 * @param source The named parameter source, for resolving the locations of named parameters
+	 * @param session The session
+	 *
+	 * @return The next bind position after the last position we bound here.
+	 *
+	 * @throws SQLException Indicates a problem calling JDBC bind methods
+	 * @throws HibernateException Indicates a problem access bind values.
+	 */
 	public static int bindQueryParameters(
-	        final PreparedStatement st,
-	        final QueryParameters queryParameters,
-	        final int start,
-	        final NamedParameterSource source,
-	        SessionImplementor session) throws SQLException, HibernateException {
+			final PreparedStatement st,
+			final QueryParameters queryParameters,
+			final int start,
+			final NamedParameterSource source,
+			SessionImplementor session) throws SQLException, HibernateException {
 		int col = start;
 		col += bindPositionalParameters( st, queryParameters, col, session );
 		col += bindNamedParameters( st, queryParameters, col, source, session );
 		return col;
 	}
 
-	public static int bindPositionalParameters(
-	        final PreparedStatement st,
-	        final QueryParameters queryParameters,
-	        final int start,
-	        final SessionImplementor session) throws SQLException, HibernateException {
+	private static int bindPositionalParameters(
+			final PreparedStatement st,
+			final QueryParameters queryParameters,
+			final int start,
+			final SessionImplementor session) throws SQLException, HibernateException {
 		return bindPositionalParameters(
-		        st,
-		        queryParameters.getPositionalParameterValues(),
-		        queryParameters.getPositionalParameterTypes(),
-		        start,
-		        session
+				st,
+				queryParameters.getPositionalParameterValues(),
+				queryParameters.getPositionalParameterTypes(),
+				start,
+				session
 		);
 	}
 
-	public static int bindPositionalParameters(
-	        final PreparedStatement st,
-	        final Object[] values,
-	        final Type[] types,
-	        final int start,
-	        final SessionImplementor session) throws SQLException, HibernateException {
+	private static int bindPositionalParameters(
+			final PreparedStatement st,
+			final Object[] values,
+			final Type[] types,
+			final int start,
+			final SessionImplementor session) throws SQLException, HibernateException {
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( st, values[i], start + span, session );
 			span += types[i].getColumnSpan( session.getFactory() );
 		}
 		return span;
 	}
 
-	public static int bindNamedParameters(
-	        final PreparedStatement ps,
-	        final QueryParameters queryParameters,
-	        final int start,
-	        final NamedParameterSource source,
-	        final SessionImplementor session) throws SQLException, HibernateException {
+	private static int bindNamedParameters(
+			final PreparedStatement ps,
+			final QueryParameters queryParameters,
+			final int start,
+			final NamedParameterSource source,
+			final SessionImplementor session) throws SQLException, HibernateException {
 		return bindNamedParameters( ps, queryParameters.getNamedParameters(), start, source, session );
 	}
 
-	public static int bindNamedParameters(
-	        final PreparedStatement ps,
-	        final Map namedParams,
-	        final int start,
-	        final NamedParameterSource source,
-	        final SessionImplementor session) throws SQLException, HibernateException {
+	private static int bindNamedParameters(
+			final PreparedStatement ps,
+			final Map namedParams,
+			final int start,
+			final NamedParameterSource source,
+			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// assumes that types are all of span 1
-			Iterator iter = namedParams.entrySet().iterator();
+			final Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
-				Map.Entry e = ( Map.Entry ) iter.next();
-				String name = ( String ) e.getKey();
-				TypedValue typedval = (TypedValue) e.getValue();
+				final Map.Entry e = (Map.Entry) iter.next();
+				final String name = (String) e.getKey();
+				final TypedValue typedVal = (TypedValue) e.getValue();
 				int[] locations = source.getNamedParameterLocations( name );
-				for ( int i = 0; i < locations.length; i++ ) {
+				for ( int location : locations ) {
 					if ( debugEnabled ) {
-						LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locations[i] + start);
+						LOG.debugf(
+								"bindNamedParameters() %s -> %s [%s]",
+								typedVal.getValue(),
+								name,
+								location + start
+						);
 					}
-					typedval.getType().nullSafeSet( ps, typedval.getValue(), locations[i] + start, session );
+					typedVal.getType().nullSafeSet( ps, typedVal.getValue(), location + start, session );
 				}
 				result += locations.length;
 			}
 			return result;
 		}
-        return 0;
+		return 0;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index dc24d0a256..cb4122c0f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,2142 +1,1943 @@
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
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import org.jboss.logging.Logger;
 
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
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 import org.hibernate.type.CollectionType;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
- * </p>
+ * <p/>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			StatefulPersistenceContext.class.getName()
+	);
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
-
-   private static final boolean tracing = LOG.isTraceEnabled();
-
-	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
-
-	public static final int INIT_COLL_SIZE = 8;
+	private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
+	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	private EntityEntryContext entityEntryContext;
 //	private Map<Object,EntityEntry> entityEntries;
 
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
 
-	private int cascading = 0;
-	private int loadCounter = 0;
-	private boolean flushing = false;
+	private int cascading;
+	private int loadCounter;
+	private boolean flushing;
 
-	private boolean defaultReadOnly = false;
-	private boolean hasNonReadOnlyEntities = false;
+	private boolean defaultReadOnly;
+	private boolean hasNonReadOnlyEntities;
 
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
 		proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>( INIT_COLL_SIZE, .75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntryContext = new EntityEntryContext();
 //		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = new IdentityHashMap<Object,Object>( INIT_COLL_SIZE );
 
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
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(INIT_COLL_SIZE);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		return ( unownedCollections == null ) ? null : unownedCollections.remove( key );
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
 			if ( o == null ) {
 				//entry may be GCd
 				continue;
 			}
 			((HibernateProxy) o).getHibernateLazyInitializer().unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntryContext.clear();
 //		entityEntries.clear();
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
 		naturalIdXrefDelegate.clear();
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
-		entry.setStatus(status);
-		setHasNonReadOnlyEnties(status);
+		entry.setStatus( status );
+		setHasNonReadOnlyEnties( status );
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		entityEntryContext.downgradeLocks();
 //		// Downgrade locks
 //		for ( EntityEntry o : entityEntries.values() ) {
 //			o.setLockMode( LockMode.NONE );
 //		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
+	 * <p/>
+	 * {@inheritDoc}
 	 */
 	@Override
-	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
-	throws HibernateException {
+	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
-		Object cached = entitySnapshotsByKey.get(key);
-		if (cached!=null) {
-			return cached==NO_ROW ? null : (Object[]) cached;
+		final Object cached = entitySnapshotsByKey.get( key );
+		if ( cached != null ) {
+			return cached == NO_ROW ? null : (Object[]) cached;
 		}
 		else {
-			Object[] snapshot = persister.getDatabaseSnapshot( id, session );
-			entitySnapshotsByKey.put( key, snapshot==null ? NO_ROW : snapshot );
+			final Object[] snapshot = persister.getDatabaseSnapshot( id, session );
+			entitySnapshotsByKey.put( key, snapshot == null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
-	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
-	throws HibernateException {
+	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		persister = locateProperPersister( persister );
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = naturalIdHelper.findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					dbValue
 			);
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
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					naturalIdSnapshotSubSet
 			);
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	private EntityPersister locateProperPersister(EntityPersister persister) {
 		return session.getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
-	/**
-	 * Retrieve the cached database snapshot for the requested entity key.
-	 * <p/>
-	 * This differs from {@link #getDatabaseSnapshot} is two important respects:<ol>
-	 * <li>no snapshot is obtained from the database if not already cached</li>
-	 * <li>an entry of {@link #NO_ROW} here is interpretet as an exception</li>
-	 * </ol>
-	 * @param key The entity key for which to retrieve the cached snapshot
-	 * @return The cached snapshot
-	 * @throws IllegalStateException if the cached snapshot was == {@link #NO_ROW}.
-	 */
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
-		Object snapshot = entitySnapshotsByKey.get( key );
+		final Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
-			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
+			throw new IllegalStateException(
+					"persistence context reported no row snapshot for "
+							+ MessageHelper.infoString( key.getEntityName(), key.getIdentifier() )
+			);
 		}
-		return ( Object[] ) snapshot;
+		return (Object[]) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
-		entitiesByKey.put(key, entity);
-		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
+		entitiesByKey.put( key, entity );
+		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 	}
 
-	/**
-	 * Get the entity instance associated with the given
-	 * <tt>EntityKey</tt>
-	 */
 	@Override
 	public Object getEntity(EntityKey key) {
-		return entitiesByKey.get(key);
+		return entitiesByKey.get( key );
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
-	/**
-	 * Remove an entity from the session cache, also clear
-	 * up other state associated with the entity, all except
-	 * for the <tt>EntityEntry</tt>
-	 */
 	@Override
 	public Object removeEntity(EntityKey key) {
-		Object entity = entitiesByKey.remove(key);
-		Iterator iter = entitiesByUniqueKey.values().iterator();
-		while ( iter.hasNext() ) {
-			if ( iter.next()==entity ) iter.remove();
+		final Object entity = entitiesByKey.remove( key );
+		final Iterator itr = entitiesByUniqueKey.values().iterator();
+		while ( itr.hasNext() ) {
+			if ( itr.next() == entity ) {
+				itr.remove();
+			}
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
-		entitySnapshotsByKey.remove(key);
-		nullifiableEntityKeys.remove(key);
-		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
-		getBatchFetchQueue().removeSubselect(key);
+		entitySnapshotsByKey.remove( key );
+		nullifiableEntityKeys.remove( key );
+		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
+		getBatchFetchQueue().removeSubselect( key );
 		return entity;
 	}
 
-	/**
-	 * Get an entity cached by unique key
-	 */
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
-		return entitiesByUniqueKey.get(euk);
+		return entitiesByUniqueKey.get( euk );
 	}
 
-	/**
-	 * Add an entity to the cache by unique key
-	 */
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
-		entitiesByUniqueKey.put(euk, entity);
+		entitiesByUniqueKey.put( euk, entity );
 	}
 
-	/**
-	 * Retrieve the EntityEntry representation of the given entity.
-	 *
-	 * @param entity The entity for which to locate the EntityEntry.
-	 * @return The EntityEntry for the given entity.
-	 */
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntryContext.getEntityEntry( entity );
-//		return entityEntries.get(entity);
 	}
 
-	/**
-	 * Remove an entity entry from the session cache
-	 */
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntryContext.removeEntityEntry( entity );
-//		return entityEntries.remove(entity);
 	}
 
-	/**
-	 * Is there an EntityEntry for this instance?
-	 */
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntryContext.hasEntityEntry( entity );
-//		return entityEntries.containsKey(entity);
 	}
 
-	/**
-	 * Get the collection entry for a persistent collection
-	 */
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
-		return collectionEntries.get(coll);
+		return collectionEntries.get( coll );
 	}
 
-	/**
-	 * Adds an entity to the internal caches.
-	 */
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
 
-
-	/**
-	 * Generates an appropriate EntityEntry instance and adds it
-	 * to the event source's internal caches.
-	 */
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
 
-		EntityEntry e = new EntityEntry(
+		final EntityEntry e = new EntityEntry(
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
 
 		entityEntryContext.addEntityEntry( entity, e );
 //		entityEntries.put(entity, e);
 
-		setHasNonReadOnlyEnties(status);
+		setHasNonReadOnlyEnties( status );
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
-		return collectionEntries.containsKey(collection);
+		return collectionEntries.containsKey( collection );
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
-	/**
-	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
-	 *
-	 * @param value The possible proxy to be reassociated.
-	 * @return Whether the passed value represented an actual proxy which got initialized.
-	 * @throws MappingException
-	 */
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
-		if ( !Hibernate.isInitialized(value) ) {
-			HibernateProxy proxy = (HibernateProxy) value;
-			LazyInitializer li = proxy.getHibernateLazyInitializer();
-			reassociateProxy(li, proxy);
+		if ( !Hibernate.isInitialized( value ) ) {
+			final HibernateProxy proxy = (HibernateProxy) value;
+			final LazyInitializer li = proxy.getHibernateLazyInitializer();
+			reassociateProxy( li, proxy );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
-	/**
-	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
-	 * reset the identifier of the proxy
-	 */
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
-			HibernateProxy proxy = (HibernateProxy) value;
-			LazyInitializer li = proxy.getHibernateLazyInitializer();
-			li.setIdentifier(id);
-			reassociateProxy(li, proxy);
+			final HibernateProxy proxy = (HibernateProxy) value;
+			final LazyInitializer li = proxy.getHibernateLazyInitializer();
+			li.setIdentifier( id );
+			reassociateProxy( li, proxy );
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
 
-	/**
-	 * Get the entity instance underlying the given proxy, throwing
-	 * an exception if the proxy is uninitialized. If the given object
-	 * is not a proxy, simply return the argument.
-	 */
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
-			HibernateProxy proxy = (HibernateProxy) maybeProxy;
-			LazyInitializer li = proxy.getHibernateLazyInitializer();
+			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
+			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
-						"object was an uninitialized proxy for " +
-						li.getEntityName()
+						"object was an uninitialized proxy for " + li.getEntityName()
 				);
 			}
-			return li.getImplementation(); //unwrap the object
+			//unwrap the object and return
+			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
-	/**
-	 * Possibly unproxy the given reference and reassociate it with the current session.
-	 *
-	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
-	 * @return The unproxied instance.
-	 * @throws HibernateException
-	 */
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
-			HibernateProxy proxy = (HibernateProxy) maybeProxy;
-			LazyInitializer li = proxy.getHibernateLazyInitializer();
-			reassociateProxy(li, proxy);
-			return li.getImplementation(); //initialize + unwrap the object
+			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
+			final LazyInitializer li = proxy.getHibernateLazyInitializer();
+			reassociateProxy( li, proxy );
+			//initialize + unwrap the object and return it
+			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
-	/**
-	 * Attempts to check whether the given key represents an entity already loaded within the
-	 * current session.
-	 * @param object The entity reference against which to perform the uniqueness check.
-	 * @throws HibernateException
-	 */
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
-		Object entity = getEntity(key);
+		final Object entity = getEntity( key );
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
-	/**
-	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
-	 * and overwrite the registration of the old one. This breaks == and occurs only for
-	 * "class" proxies rather than "interface" proxies. Also init the proxy to point to
-	 * the given target implementation if necessary.
-	 *
-	 * @param proxy The proxy instance to be narrowed.
-	 * @param persister The persister for the proxied entity.
-	 * @param key The internal cache key for the proxied entity.
-	 * @param object (optional) the actual proxied entity instance.
-	 * @return An appropriately narrowed instance.
-	 * @throws HibernateException
-	 */
 	@Override
+	@SuppressWarnings("unchecked")
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
-		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
+		final boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			if ( object != null ) {
-				proxiesByKey.remove(key);
-				return object; //return the proxied object
+				proxiesByKey.remove( key );
+				//return the proxied object
+				return object;
 			}
 			else {
 				proxy = persister.createProxy( key.getIdentifier(), session );
-				Object proxyOrig = proxiesByKey.put(key, proxy); //overwrite old proxy
+				//overwrite old proxy
+				final Object proxyOrig = proxiesByKey.put( key, proxy );
 				if ( proxyOrig != null ) {
 					if ( ! ( proxyOrig instanceof HibernateProxy ) ) {
 						throw new AssertionFailure(
 								"proxy not of type HibernateProxy; it is " + proxyOrig.getClass()
 						);
 					}
 					// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
-					boolean readOnlyOrig = ( ( HibernateProxy ) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
-					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
+					final boolean readOnlyOrig = ( (HibernateProxy) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
+					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 				}
 				return proxy;
 			}
 		}
 		else {
 
 			if ( object != null ) {
-				LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
-				li.setImplementation(object);
+				final LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
+				li.setImplementation( object );
 			}
-
 			return proxy;
-
 		}
-
 	}
 
-	/**
-	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
-	 * third argument (the entity associated with the key) if no proxy exists. Init
-	 * the proxy to the target implementation, if necessary.
-	 */
 	@Override
-	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
-	throws HibernateException {
+	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
 		if ( !persister.hasProxy() ) {
 			return impl;
 		}
-		Object proxy = proxiesByKey.get( key );
+		final Object proxy = proxiesByKey.get( key );
 		return ( proxy != null ) ? narrowProxy( proxy, persister, key, impl ) : impl;
 	}
 
-	/**
-	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
-	 * argument (the entity associated with the key) if no proxy exists.
-	 * (slower than the form above)
-	 */
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
-		EntityEntry e = getEntry(impl);
+		final EntityEntry e = getEntry( impl );
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
-	/**
-	 * Get the entity that owns this persistent collection
-	 */
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		// todo : we really just need to add a split in the notions of:
 		//		1) collection key
 		//		2) collection owner key
 		// these 2 are not always the same.  Same is true in the case of ToOne associations with property-ref...
 		final EntityPersister ownerPersister = collectionPersister.getOwnerEntityPersister();
 		if ( ownerPersister.getIdentifierType().getReturnedClass().isInstance( key ) ) {
 			return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 		}
 
 		// we have a property-ref type mapping for the collection key.  But that could show up a few ways here...
 		//
 		//		1) The incoming key could be the entity itself...
 		if ( ownerPersister.isInstance( key ) ) {
 			final Serializable owenerId = ownerPersister.getIdentifier( key, session );
 			if ( owenerId == null ) {
 				return null;
 			}
 			return getEntity( session.generateEntityKey( owenerId, ownerPersister ) );
 		}
 
 		final CollectionType collectionType = collectionPersister.getCollectionType();
 
 		//		2) The incoming key is most likely the collection key which we need to resolve to the owner key
 		//			find the corresponding owner instance
 		//			a) try by EntityUniqueKey
 		if ( collectionType.getLHSPropertyName() != null ) {
-			Object owner = getEntity(
+			final Object owner = getEntity(
 					new EntityUniqueKey(
 							ownerPersister.getEntityName(),
 							collectionType.getLHSPropertyName(),
 							key,
 							collectionPersister.getKeyType(),
 							ownerPersister.getEntityMode(),
 							session.getFactory()
 					)
 			);
 			if ( owner != null ) {
 				return owner;
 			}
 
 			//		b) try by EntityKey, which means we need to resolve owner-key -> collection-key
 			//			IMPL NOTE : yes if we get here this impl is very non-performant, but PersistenceContext
 			//					was never designed to handle this case; adding that capability for real means splitting
 			//					the notions of:
 			//						1) collection key
 			//						2) collection owner key
 			// 					these 2 are not always the same (same is true in the case of ToOne associations with
 			// 					property-ref).  That would require changes to (at least) CollectionEntry and quite
 			//					probably changes to how the sql for collection initializers are generated
 			//
 			//			We could also possibly see if the referenced property is a natural id since we already have caching
 			//			in place of natural id snapshots.  BUt really its better to just do it the right way ^^ if we start
 			// 			going that route
 			final Serializable ownerId = ownerPersister.getIdByUniqueKey( key, collectionType.getLHSPropertyName(), session );
 			return getEntity( session.generateEntityKey( ownerId, ownerPersister ) );
 		}
 
 		// as a last resort this is what the old code did...
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
-	/**
-	 * Get the entity that owned this persistent collection when it was loaded
-	 *
-	 * @param collection The persistent collection
-	 * @return the owner, if its entity ID is available from the collection's loaded key
-	 * and the owner entity is in the persistence context; otherwise, returns null
-	 */
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
-		CollectionEntry ce = getCollectionEntry( collection );
+		final CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
-			return null; // early exit...
+			return null;
 		}
+
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
-		Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
+		final Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
-	/**
-	 * Get the ID for the entity that owned this persistent collection when it was loaded
-	 *
-	 * @param collection The persistent collection
-	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
-	 */
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
 
-	/**
-	 * add a collection we just loaded up (still needs initializing)
-	 */
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
-		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
-		addCollection(collection, ce, id);
+		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
+		addCollection( collection, ce, id );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
-	/**
-	 * add a detached uninitialized collection
-	 */
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
-		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
+		final CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
-	/**
-	 * Add a new collection (ie. a newly created one, just instantiated by the
-	 * application, with no database state or snapshot)
-	 * @param collection The collection to be associated with the persistence context
-	 */
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
-	throws HibernateException {
-		addCollection(collection, persister);
+			throws HibernateException {
+		addCollection( collection, persister );
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
-		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
-		PersistentCollection old = collectionsByKey.put( collectionKey, coll );
+		final CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
+		final PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
-				throw new AssertionFailure("bug adding collection twice");
+				throw new AssertionFailure( "bug adding collection twice" );
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
-		CollectionEntry ce = new CollectionEntry( persister, collection );
+		final CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
-	/**
-	 * add an (initialized) collection that was created by another session and passed
-	 * into update() (ie. one with a snapshot and existing state on the database)
-	 */
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
-	throws HibernateException {
+			throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
-			CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
+			final CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
-	/**
-	 * add a collection we just pulled out of the cache (does not need initializing)
-	 */
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
-	throws HibernateException {
-		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
-		ce.postInitialize(collection);
-		addCollection(collection, ce, id);
+			throws HibernateException {
+		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
+		ce.postInitialize( collection );
+		addCollection( collection, ce, id );
 		return ce;
 	}
 
-	/**
-	 * Get the collection instance associated with the <tt>CollectionKey</tt>
-	 */
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
-	/**
-	 * Register a collection for non-lazy loading at the end of the
-	 * two-phase load
-	 */
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
-		nonlazyCollections.add(collection);
+		nonlazyCollections.add( collection );
 	}
 
-	/**
-	 * Force initialization of all non-lazy collections encountered during
-	 * the current two-phase load (actually, this is a no-op, unless this
-	 * is the "outermost" load)
-	 */
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
-         if (tracing)
-			   LOG.trace( "Initializing non-lazy collections" );
+			if ( TRACE_ENABLED ) {
+				LOG.trace( "Initializing non-lazy collections" );
+			}
 
 			//do this work only at the very highest level of the load
-			loadCounter++; //don't let this method be called recursively
+			//don't let this method be called recursively
+			loadCounter++;
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
 
-
-	/**
-	 * Get the <tt>PersistentCollection</tt> object for an array
-	 */
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
-		return arrayHolders.get(array);
+		return arrayHolders.get( array );
 	}
 
-	/**
-	 * Register a <tt>PersistentCollection</tt> object for an array.
-	 * Associates a holder with an array - MUST be called after loading
-	 * array, since the array instance is not created until endLoad().
-	 */
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
-		return arrayHolders.remove(array);
+		return arrayHolders.remove( array );
 	}
 
-	/**
-	 * Get the snapshot of the pre-flush collection state
-	 */
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
-		return getCollectionEntry(coll).getSnapshot();
+		return getCollectionEntry( coll ).getSnapshot();
 	}
 
-	/**
-	 * Get the collection entry for a collection passed to filter,
-	 * which might be a collection wrapper, an array, or an unwrapped
-	 * collection. Return null if there is no entry.
-	 */
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
-			coll = getCollectionHolder(collection);
+			coll = getCollectionHolder( collection );
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
-				Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
+				final Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
-					PersistentCollection pc = wrappers.next();
-					if ( pc.isWrapper(collection) ) {
+					final PersistentCollection pc = wrappers.next();
+					if ( pc.isWrapper( collection ) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
-		return (coll == null) ? null : getCollectionEntry(coll);
+		return (coll == null) ? null : getCollectionEntry( coll );
 	}
 
-	/**
-	 * Get an existing proxy by key
-	 */
 	@Override
 	public Object getProxy(EntityKey key) {
-		return proxiesByKey.get(key);
+		return proxiesByKey.get( key );
 	}
 
-	/**
-	 * Add a proxy to the session cache
-	 */
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
-		proxiesByKey.put(key, proxy);
+		proxiesByKey.put( key, proxy );
 	}
 
-	/**
-	 * Remove a proxy from the session cache.
-	 * <p/>
-	 * Additionally, ensure that any load optimization references
-	 * such as batch or subselect loading get cleaned up as well.
-	 *
-	 * @param key The key of the entity proxy to be removed
-	 * @return The proxy reference.
-	 */
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
-	/**
-	 * Retrieve the set of EntityKeys representing nullifiable references
-	 */
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
 	public int getNumberOfManagedEntities() {
 		return entityEntryContext.getNumberOfManagedEntities();
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return null;
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
 		final boolean afterFlush = this.flushing && ! flushing;
 		this.flushing = flushing;
 		if ( afterFlush ) {
 			getNaturalIdHelper().cleanupFromSynchronizations();
 		}
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
 
-	/**
-	 * Returns a string representation of the object.
-	 *
-	 * @return a string representation of the object.
-	 */
 	@Override
-    public String toString() {
-		return new StringBuilder()
-				.append("PersistenceContext[entityKeys=")
-				.append(entitiesByKey.keySet())
-				.append(",collectionKeys=")
-				.append(collectionsByKey.keySet())
-				.append("]")
-				.toString();
+	public String toString() {
+		return "PersistenceContext[entityKeys=" + entitiesByKey.keySet()
+				+ ",collectionKeys=" + collectionsByKey.keySet() + "]";
 	}
 
 	@Override
 	public Entry<Object,EntityEntry>[] reentrantSafeEntityEntries() {
 		return entityEntryContext.reentrantSafeEntityEntries();
 	}
 
-	/**
-	 * Search <tt>this</tt> persistence context for an associated entity instance which is considered the "owner" of
-	 * the given <tt>childEntity</tt>, and return that owner's id value.  This is performed in the scenario of a
-	 * uni-directional, non-inverse one-to-many collection (which means that the collection elements do not maintain
-	 * a direct reference to the owner).
-	 * <p/>
-	 * As such, the processing here is basically to loop over every entity currently associated with this persistence
-	 * context and for those of the correct entity (sub) type to extract its collection role property value and see
-	 * if the child is contained within that collection.  If so, we have found the owner; if not, we go on.
-	 * <p/>
-	 * Also need to account for <tt>mergeMap</tt> which acts as a local copy cache managed for the duration of a merge
-	 * operation.  It represents a map of the detached entity instances pointing to the corresponding managed instance.
-	 *
-	 * @param entityName The entity name for the entity type which would own the child
-	 * @param propertyName The name of the property on the owning entity type which would name this child association.
-	 * @param childEntity The child entity instance for which to locate the owner instance id.
-	 * @param mergeMap A map of non-persistent instances from an on-going merge operation (possibly null).
-	 *
-	 * @return The id of the entityName instance which is said to own the child; null if an appropriate owner not
-	 * located.
-	 */
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
-		Object parent = parentsByChild.get( childEntity );
+		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
-//			final EntityEntry entityEntry = entityEntries.get( parent );
 			//there maybe more than one parent, filter by type
-			if ( 	persister.isSubclassEntityName(entityEntry.getEntityName() )
+			if ( persister.isSubclassEntityName( entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
-				parentsByChild.remove( childEntity ); // remove wrong entry
+				// remove wrong entry
+				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : reentrantSafeEntityEntries() ) {
-//		for ( Entry<Object,EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
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
-					Object unmergedInstance = mergeMap.get( entityEntryInstance );
-					Object unmergedChild = mergeMap.get( childEntity );
+					final Object unmergedInstance = mergeMap.get( entityEntryInstance );
+					final Object unmergedChild = mergeMap.get( childEntity );
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
-		Object collection = persister.getPropertyValue( potentialParent, property );
+		final Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
-	/**
-	 * Search the persistence context for an index of the child object,
-	 * given a collection role
-	 */
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
-
-		EntityPersister persister = session.getFactory()
-				.getEntityPersister(entity);
-		CollectionPersister cp = session.getFactory()
-				.getCollectionPersister(entity + '.' + property);
+		final EntityPersister persister = session.getFactory().getEntityPersister( entity );
+		final CollectionPersister cp = session.getFactory().getCollectionPersister( entity + '.' + property );
 
 	    // try cache lookup first
-	    Object parent = parentsByChild.get(childEntity);
-		if (parent != null) {
+		final Object parent = parentsByChild.get( childEntity );
+		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
-//			final EntityEntry entityEntry = entityEntries.get(parent);
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
-				Object index = getIndexInParent(property, childEntity, persister, cp, parent);
+				Object index = getIndexInParent( property, childEntity, persister, cp, parent );
 
 				if (index==null && mergeMap!=null) {
-					Object unmergedInstance = mergeMap.get(parent);
-					Object unmergedChild = mergeMap.get(childEntity);
-					if ( unmergedInstance!=null && unmergedChild!=null ) {
-						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
+					final Object unMergedInstance = mergeMap.get( parent );
+					final Object unMergedChild = mergeMap.get( childEntity );
+					if ( unMergedInstance != null && unMergedChild != null ) {
+						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 					}
 				}
-				if (index!=null) {
+				if ( index != null ) {
 					return index;
 				}
 			}
 			else {
-				parentsByChild.remove(childEntity); // remove wrong entry
+				// remove wrong entry
+				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//Not found in cache, proceed
 		for ( Entry<Object, EntityEntry> me : reentrantSafeEntityEntries() ) {
-			EntityEntry ee = me.getValue();
+			final EntityEntry ee = me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
-				Object instance = me.getKey();
-
-				Object index = getIndexInParent(property, childEntity, persister, cp, instance);
-
-				if (index==null && mergeMap!=null) {
-					Object unmergedInstance = mergeMap.get(instance);
-					Object unmergedChild = mergeMap.get(childEntity);
-					if ( unmergedInstance!=null && unmergedChild!=null ) {
-						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
+				final Object instance = me.getKey();
+
+				Object index = getIndexInParent( property, childEntity, persister, cp, instance );
+				if ( index==null && mergeMap!=null ) {
+					final Object unMergedInstance = mergeMap.get( instance );
+					final Object unMergedChild = mergeMap.get( childEntity );
+					if ( unMergedInstance != null && unMergedChild!=null ) {
+						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 					}
 				}
 
-				if (index!=null) return index;
+				if ( index != null ) {
+					return index;
+				}
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
-		Object collection = persister.getPropertyValue( potentialParent, property );
-		if ( collection!=null && Hibernate.isInitialized(collection) ) {
-			return collectionPersister.getCollectionType().indexOf(collection, childEntity);
+		final Object collection = persister.getPropertyValue( potentialParent, property );
+		if ( collection != null && Hibernate.isInitialized( collection ) ) {
+			return collectionPersister.getCollectionType().indexOf( collection, childEntity );
 		}
 		else {
 			return null;
 		}
 	}
 
-	/**
-	 * Record the fact that the association belonging to the keyed
-	 * entity is null.
-	 */
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
-		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
+		nullAssociations.add( new AssociationKey( ownerKey, propertyName ) );
 	}
 
-	/**
-	 * Is the association property belonging to the keyed entity null?
-	 */
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
-		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
+		return nullAssociations.contains( new AssociationKey( ownerKey, propertyName ) );
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
-			isReadOnly = ( ( HibernateProxy ) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
+			isReadOnly = ( (HibernateProxy) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
-			EntityEntry ee =  getEntry( entityOrProxy );
+			final EntityEntry ee =  getEntry( entityOrProxy );
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
-			HibernateProxy proxy = ( HibernateProxy ) object;
+			final HibernateProxy proxy = (HibernateProxy) object;
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
-			Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
+			final Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
-				setProxyReadOnly( ( HibernateProxy ) maybeProxy, readOnly );
+				setProxyReadOnly( (HibernateProxy) maybeProxy, readOnly );
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
-		EntityEntry entry = getEntry(entity);
-		if (entry == null) {
-			throw new TransientObjectException("Instance was not associated with this persistence context" );
+		final EntityEntry entry = getEntry( entity );
+		if ( entry == null ) {
+			throw new TransientObjectException( "Instance was not associated with this persistence context" );
 		}
-		entry.setReadOnly(readOnly, entity );
+		entry.setReadOnly( readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		final Object entity = entitiesByKey.remove( oldKey );
 		final EntityEntry oldEntry = entityEntryContext.removeEntityEntry( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
 		addEntity( newKey, entity );
 		addEntry(
 				entity,
-		        oldEntry.getStatus(),
-		        oldEntry.getLoadedState(),
-		        oldEntry.getRowId(),
-		        generatedId,
-		        oldEntry.getVersion(),
-		        oldEntry.getLockMode(),
-		        oldEntry.isExistsInDatabase(),
-		        oldEntry.getPersister(),
-		        oldEntry.isBeingReplicated(),
-		        oldEntry.isLoadedWithLazyPropertiesUnfetched()
+				oldEntry.getStatus(),
+				oldEntry.getLoadedState(),
+				oldEntry.getRowId(),
+				generatedId,
+				oldEntry.getVersion(),
+				oldEntry.getLockMode(),
+				oldEntry.isExistsInDatabase(),
+				oldEntry.getPersister(),
+				oldEntry.isBeingReplicated(),
+				oldEntry.isLoadedWithLazyPropertiesUnfetched()
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
-		if ( tracing ) LOG.trace( "Serializing persistent-context" );
+		if ( tracing ) {
+			LOG.trace( "Serializing persistence-context" );
+		}
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
-		Iterator itr = entitiesByKey.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
-			( ( EntityKey ) entry.getKey() ).serialize( oos );
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries" );
+		}
+		for ( Map.Entry<EntityKey,Object> entry : entitiesByKey.entrySet() ) {
+			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
-		itr = entitiesByUniqueKey.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
-			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries" );
+		}
+		for ( Map.Entry<EntityUniqueKey,Object> entry : entitiesByUniqueKey.entrySet() ) {
+			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
-		itr = proxiesByKey.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
-			( (EntityKey) entry.getKey() ).serialize( oos );
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries" );
+		}
+		for ( Map.Entry<EntityKey,Object> entry : proxiesByKey.entrySet() ) {
+			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
-		itr = entitySnapshotsByKey.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
-			( ( EntityKey ) entry.getKey() ).serialize( oos );
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries" );
+		}
+		for ( Map.Entry<EntityKey,Object> entry : entitySnapshotsByKey.entrySet() ) {
+			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		entityEntryContext.serialize( oos );
-//		oos.writeInt( entityEntries.size() );
-//		if ( tracing ) LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
-//		itr = entityEntries.entrySet().iterator();
-//		while ( itr.hasNext() ) {
-//			Map.Entry entry = ( Map.Entry ) itr.next();
-//			oos.writeObject( entry.getKey() );
-//			( ( EntityEntry ) entry.getValue() ).serialize( oos );
-//		}
 
 		oos.writeInt( collectionsByKey.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
-		itr = collectionsByKey.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
-			( ( CollectionKey ) entry.getKey() ).serialize( oos );
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries" );
+		}
+		for ( Map.Entry<CollectionKey,PersistentCollection> entry : collectionsByKey.entrySet() ) {
+			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
-		itr = collectionEntries.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries" );
+		}
+		for ( Map.Entry<PersistentCollection,CollectionEntry> entry : collectionEntries.entrySet() ) {
 			oos.writeObject( entry.getKey() );
-			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
+			entry.getValue().serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
-		itr = arrayHolders.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			Map.Entry entry = ( Map.Entry ) itr.next();
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries" );
+		}
+		for ( Map.Entry<Object,PersistentCollection> entry : arrayHolders.entrySet() ) {
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
-		if ( tracing ) LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
+		if ( tracing ) {
+			LOG.trace( "Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries" );
+		}
 		for ( EntityKey entry : nullifiableEntityKeys ) {
 			entry.serialize( oos );
 		}
 	}
 
+	/**
+	 * Used by the owning session to explicitly control deserialization of the persistence context.
+	 *
+	 * @param ois The stream from which the persistence context should be read
+	 * @param session The owning session
+	 *
+	 * @return The deserialized StatefulPersistenceContext
+	 *
+	 * @throws IOException deserialization errors.
+	 * @throws ClassNotFoundException deserialization errors.
+	 */
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
-		if ( tracing ) LOG.trace("Serializing persistent-context");
-		StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
+		if ( tracing ) {
+			LOG.trace( "Serializing persistent-context" );
+		}
+		final StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
 
 		// during deserialization, we need to reconnect all proxies and
 		// collections to this session, as well as the EntityEntry and
 		// CollectionEntry instances; these associations are transient
 		// because serialization is used for different things.
 
 		try {
 			rtn.defaultReadOnly = ois.readBoolean();
 			// todo : we can actually just determine this from the incoming EntityEntry-s
 			rtn.hasNonReadOnlyEntities = ois.readBoolean();
 
 			int count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] entitiesByKey entries" );
+			}
 			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] entitiesByUniqueKey entries" );
+			}
 			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] proxiesByKey entries" );
+			}
 			//noinspection unchecked
 			rtn.proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>(
 					count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count,
 					.75f,
 					1,
 					ConcurrentReferenceHashMap.ReferenceType.STRONG,
 					ConcurrentReferenceHashMap.ReferenceType.WEAK,
 					null
 			);
 			for ( int i = 0; i < count; i++ ) {
-				EntityKey ek = EntityKey.deserialize( ois, session );
-				Object proxy = ois.readObject();
+				final EntityKey ek = EntityKey.deserialize( ois, session );
+				final Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
-					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
+					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
-				} else {
-					if ( tracing ) LOG.trace("Encountered prunded proxy");
 				}
-				// otherwise, the proxy was pruned during the serialization process
+				else {
+					// otherwise, the proxy was pruned during the serialization process
+					if ( tracing ) {
+						LOG.trace( "Encountered pruned proxy" );
+					}
+				}
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] entitySnapshotsByKey entries" );
+			}
 			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			rtn.entityEntryContext = EntityEntryContext.deserialize( ois, rtn );
-//			count = ois.readInt();
-//			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
-//			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
-//			for ( int i = 0; i < count; i++ ) {
-//				Object entity = ois.readObject();
-//				EntityEntry entry = EntityEntry.deserialize( ois, rtn );
-//				rtn.entityEntries.put( entity, entry );
-//			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] collectionsByKey entries" );
+			}
 			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] collectionEntries entries" );
+			}
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
-				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
+				final PersistentCollection pc = (PersistentCollection) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] arrayHolders entries" );
+			}
 			rtn.arrayHolders = new IdentityHashMap<Object, PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
-			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
+			if ( tracing ) {
+				LOG.trace( "Starting deserialization of [" + count + "] nullifiableEntityKey entries" );
+			}
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
-		parentsByChild.put(child, parent);
+		parentsByChild.put( child, parent );
 	}
 
 	@Override
 	public void removeChildParent(Object child) {
-	   parentsByChild.remove(child);
+		parentsByChild.remove( child );
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
-				List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
+				final List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
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
 
 	private final NaturalIdXrefDelegate naturalIdXrefDelegate = new NaturalIdXrefDelegate( this );
 
 	private final NaturalIdHelper naturalIdHelper = new NaturalIdHelper() {
 		@Override
 		public void cacheNaturalIdCrossReferenceFromLoad(
 				EntityPersister persister,
 				Serializable id,
 				Object[] naturalIdValues) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 
 			// 'justAddedLocally' is meant to handle the case where we would get double stats jounaling
 			//	from a single load event.  The first put journal would come from the natural id resolution;
 			// the second comes from the entity loading.  In this condition, we want to avoid the multiple
 			// 'put' stats incrementing.
-			boolean justAddedLocally = naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
+			final boolean justAddedLocally = naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
 
 			if ( justAddedLocally && persister.hasNaturalIdCache() ) {
 				managedSharedCacheEntries( persister, id, naturalIdValues, null, CachedNaturalIdValueSource.LOAD );
 			}
 		}
 
 		@Override
 		public void manageLocalNaturalIdCrossReference(
 				EntityPersister persister,
 				Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
 
 			// cache
 			naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
 		}
 
 		@Override
 		public void manageSharedNaturalIdCrossReference(
 				EntityPersister persister,
 				final Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			if ( !persister.hasNaturalIdCache() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
 			final Object[] previousNaturalIdValues = previousState == null ? null : extractNaturalIdValues( previousState, persister );
 
 			managedSharedCacheEntries( persister, id, naturalIdValues, previousNaturalIdValues, source );
 		}
 
 		private void managedSharedCacheEntries(
 				EntityPersister persister,
 				final Serializable id,
 				Object[] naturalIdValues,
 				Object[] previousNaturalIdValues,
 				CachedNaturalIdValueSource source) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
 			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
 
 			final SessionFactoryImplementor factory = session.getFactory();
 
 			switch ( source ) {
 				case LOAD: {
-					if (naturalIdCacheAccessStrategy.get(naturalIdCacheKey, session.getTimestamp()) != null) {
-						return; // prevent identical re-cachings
+					if ( naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session.getTimestamp() ) != null ) {
+						// prevent identical re-cachings
+						return;
 					}
 					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
 							naturalIdCacheKey,
 							id,
 							session.getTimestamp(),
 							null
 					);
 
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().naturalIdCachePut(
 								naturalIdCacheAccessStrategy.getRegion()
 										.getName()
 						);
 					}
 
 					break;
 				}
 				case INSERT: {
 					final boolean put = naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, id );
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor()
 								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 					}
 
 					( (EventSource) session ).getActionQueue().registerProcess(
 							new AfterTransactionCompletionProcess() {
 								@Override
 								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 									if (success) {
 										final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, id );
 
 										if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 											factory.getStatisticsImplementor()
 												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 										}
 									}
 									else {
-										naturalIdCacheAccessStrategy.remove(naturalIdCacheKey);
+										naturalIdCacheAccessStrategy.remove( naturalIdCacheKey );
 									}
 								}
 							}
 					);
 
 					break;
 				}
 				case UPDATE: {
 					final NaturalIdCacheKey previousCacheKey = new NaturalIdCacheKey( previousNaturalIdValues, persister, session );
-					if (naturalIdCacheKey.equals(previousCacheKey)) {
-						return; // prevent identical re-caching, solves HHH-7309
+					if ( naturalIdCacheKey.equals( previousCacheKey ) ) {
+						// prevent identical re-caching, solves HHH-7309
+						return;
 					}
 					final SoftLock removalLock = naturalIdCacheAccessStrategy.lockItem( previousCacheKey, null );
 					naturalIdCacheAccessStrategy.remove( previousCacheKey );
 
 					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
 					final boolean put = naturalIdCacheAccessStrategy.update( naturalIdCacheKey, id );
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor()
 								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 					}
 
 					( (EventSource) session ).getActionQueue().registerProcess(
 							new AfterTransactionCompletionProcess() {
 								@Override
 								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 									naturalIdCacheAccessStrategy.unlockItem( previousCacheKey, removalLock );
 									if (success) {
 										final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, id, lock );
 
 										if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 											factory.getStatisticsImplementor()
 												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 										}
 									}
 									else {
 										naturalIdCacheAccessStrategy.unlockItem( naturalIdCacheKey, lock );
 									}
 								}
 							}
 					);
 
 					break;
 				}
+				default: {
+					LOG.debug( "Unexpected CachedNaturalIdValueSource [" + source + "]" );
+				}
 			}
 		}
 
 		@Override
 		public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return null;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = getNaturalIdValues( state, persister );
 
 			final Object[] localNaturalIdValues = naturalIdXrefDelegate.removeNaturalIdCrossReference( 
 					persister, 
 					id, 
 					naturalIdValues 
 			);
 
 			return localNaturalIdValues != null ? localNaturalIdValues : naturalIdValues;
 		}
 
 		@Override
 		public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			if ( ! persister.hasNaturalIdCache() ) {
 				// nothing to do
 				return;
 			}
 
 			// todo : couple of things wrong here:
 			//		1) should be using access strategy, not plain evict..
 			//		2) should prefer session-cached values if any (requires interaction from removeLocalNaturalIdCrossReference
 
 			persister = locateProperPersister( persister );
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
 			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
 			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 
 //			if ( sessionCachedNaturalIdValues != null
 //					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
 //				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session );
 //				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
 //			}
 		}
 
 		@Override
 		public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 			return naturalIdXrefDelegate.findCachedNaturalId( locateProperPersister( persister ), pk );
 		}
 
 		@Override
 		public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
 			return naturalIdXrefDelegate.findCachedNaturalIdResolution( locateProperPersister( persister ), naturalIdValues );
 		}
 
 		@Override
 		public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister) {
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			if ( state.length == naturalIdPropertyIndexes.length ) {
 				return state;
 			}
 
 			final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 			for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 				naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 			}
 			return naturalIdValues;
 		}
 
 		@Override
 		public Object[] extractNaturalIdValues(Object entity, EntityPersister persister) {
 			if ( entity == null ) {
 				throw new AssertionFailure( "Entity from which to extract natural id value(s) cannot be null" );
 			}
 			if ( persister == null ) {
 				throw new AssertionFailure( "Persister to use in extracting natural id value(s) cannot be null" );
 			}
 
 			final int[] naturalIdentifierProperties = persister.getNaturalIdentifierProperties();
 			final Object[] naturalIdValues = new Object[naturalIdentifierProperties.length];
 
 			for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 				naturalIdValues[i] = persister.getPropertyValue( entity, naturalIdentifierProperties[i] );
 			}
 
 			return naturalIdValues;
 		}
 
 		@Override
 		public Collection<Serializable> getCachedPkResolutions(EntityPersister entityPersister) {
 			return naturalIdXrefDelegate.getCachedPkResolutions( entityPersister );
 		}
 
 		@Override
 		public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 
 			final Object[] naturalIdValuesFromCurrentObjectState = extractNaturalIdValues( entity, persister );
 			final boolean changed = ! naturalIdXrefDelegate.sameAsCached(
 					persister,
 					pk,
 					naturalIdValuesFromCurrentObjectState
 			);
 
 			if ( changed ) {
 				final Object[] cachedNaturalIdValues = naturalIdXrefDelegate.findCachedNaturalId( persister, pk );
 				naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, pk, naturalIdValuesFromCurrentObjectState );
 				naturalIdXrefDelegate.stashInvalidNaturalIdReference( persister, cachedNaturalIdValues );
 
 				removeSharedNaturalIdCrossReference(
 						persister,
 						pk,
 						cachedNaturalIdValues
 				);
 			}
 		}
 
 		@Override
 		public void cleanupFromSynchronizations() {
 			naturalIdXrefDelegate.unStashInvalidNaturalIdReferences();
 		}
 
 		@Override
 		public void handleEviction(Object object, EntityPersister persister, Serializable identifier) {
 			naturalIdXrefDelegate.removeNaturalIdCrossReference(
 					persister,
 					identifier,
 					findCachedNaturalId( persister, identifier )
 			);
 		}
 	};
 
 	@Override
 	public NaturalIdHelper getNaturalIdHelper() {
 		return naturalIdHelper;
 	}
 
 	private Object[] getNaturalIdValues(Object[] state, EntityPersister persister) {
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 
 		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 			naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 		}
 
 		return naturalIdValues;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index 2cdf7f9c88..e8d2c6ca38 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,386 +1,400 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
- * Functionality relating to Hibernate's two-phase loading process,
- * that may be reused by persisters that do not use the Loader
- * framework
+ * Functionality relating to the Hibernate two-phase loading process, that may be reused by persisters
+ * that do not use the Loader framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			TwoPhaseLoad.class.getName()
+	);
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, TwoPhaseLoad.class.getName() );
-
-	private TwoPhaseLoad() {}
+	private TwoPhaseLoad() {
+	}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
+	 *
+	 * @param persister The persister for the hydrated entity
+	 * @param id The entity identifier
+	 * @param values The entity values
+	 * @param rowId The rowId for the entity
+	 * @param object An optional instance for the entity being loaded
+	 * @param lockMode The lock mode
+	 * @param lazyPropertiesAreUnFetched Whether properties defined as lazy are yet un-fetched
+	 * @param session The Session
 	 */
 	public static void postHydrate(
-		final EntityPersister persister,
-		final Serializable id,
-		final Object[] values,
-		final Object rowId,
-		final Object object,
-		final LockMode lockMode,
-		final boolean lazyPropertiesAreUnfetched,
-		final SessionImplementor session)
-	throws HibernateException {
-
-		Object version = Versioning.getVersion( values, persister );
+			final EntityPersister persister,
+			final Serializable id,
+			final Object[] values,
+			final Object rowId,
+			final Object object,
+			final LockMode lockMode,
+			final boolean lazyPropertiesAreUnFetched,
+			final SessionImplementor session) throws HibernateException {
+		final Object version = Versioning.getVersion( values, persister );
 		session.getPersistenceContext().addEntry(
 				object,
 				Status.LOADING,
 				values,
 				rowId,
 				id,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
-				lazyPropertiesAreUnfetched
+				lazyPropertiesAreUnFetched
 			);
 
 		if ( version != null && LOG.isTraceEnabled() ) {
-			String versionStr = persister.isVersioned()
+			final String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
-			LOG.tracev( "Version: {0}", versionStr );
+			LOG.tracef( "Version: %s", versionStr );
 		}
-
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
-	 *
+	 * <p/>
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
+	 *
+	 * @param entity The entity being loaded
+	 * @param readOnly Is the entity being loaded as read-only
+	 * @param session The Session
+	 * @param preLoadEvent The (re-used) pre-load event
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
-			final PreLoadEvent preLoadEvent,
-			final PostLoadEvent postLoadEvent) throws HibernateException {
+			final PreLoadEvent preLoadEvent) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
-		final EntityEntry entityEntry = persistenceContext.getEntry(entity);
+		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
-		final EntityPersister persister = entityEntry.getPersister();
-		final Serializable id = entityEntry.getId();
-
-//		persistenceContext.getNaturalIdHelper().startingLoad( persister, id );
-//		try {
-			doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent, postLoadEvent );
-//		}
-//		finally {
-//			persistenceContext.getNaturalIdHelper().endingLoad( persister, id );
-//		}
+		doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent );
 	}
 
 	private static void doInitializeEntity(
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean readOnly,
 			final SessionImplementor session,
-			final PreLoadEvent preLoadEvent,
-			final PostLoadEvent postLoadEvent) throws HibernateException {
+			final PreLoadEvent preLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
-		EntityPersister persister = entityEntry.getPersister();
-		Serializable id = entityEntry.getId();
-		Object[] hydratedState = entityEntry.getLoadedState();
+		final EntityPersister persister = entityEntry.getPersister();
+		final Serializable id = entityEntry.getId();
+		final Object[] hydratedState = entityEntry.getLoadedState();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Resolving associations for %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
-		Type[] types = persister.getPropertyTypes();
+		final Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!=BackrefPropertyAccessor.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
 			preLoadEvent.setEntity( entity ).setState( hydratedState ).setId( id ).setPersister( persister );
 
 			final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 					.getFactory()
 					.getServiceRegistry()
 					.getService( EventListenerRegistry.class )
 					.getEventListenerGroup( EventType.PRE_LOAD );
 			for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPreLoad( preLoadEvent );
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
 			if ( debugEnabled ) {
 				LOG.debugf(
 						"Adding entity to second-level cache: %s",
 						MessageHelper.infoString( persister, id, session.getFactory() )
 				);
 			}
 
-			Object version = Versioning.getVersion(hydratedState, persister);
-			CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
-			CacheKey cacheKey = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
+			final Object version = Versioning.getVersion( hydratedState, persister );
+			final CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
+			final CacheKey cacheKey = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				persister.getCacheAccessStrategy().update(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
-				boolean put = persister.getCacheAccessStrategy().putFromLoad(
+				final boolean put = persister.getCacheAccessStrategy().putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						useMinimalPuts( session, entityEntry )
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		if ( persister.hasNaturalIdentifier() ) {
 			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
 			);
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
-			Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
+			final Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
-				isReallyReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
+				isReallyReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
-			persistenceContext.setEntryStatus(entityEntry, Status.READ_ONLY);
+			persistenceContext.setEntryStatus( entityEntry, Status.READ_ONLY );
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
-					hydratedState,  //after setting values to object, entityMode
+					//after setting values to object
+					hydratedState,
 					session
 			);
-			persistenceContext.setEntryStatus(entityEntry, Status.MANAGED);
+			persistenceContext.setEntryStatus( entityEntry, Status.MANAGED );
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 		);
 
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 	}
 	
 	/**
 	 * PostLoad cannot occur during initializeEntity, as that call occurs *before*
 	 * the Set collections are added to the persistence context by Loader.
 	 * Without the split, LazyInitializationExceptions can occur in the Entity's
 	 * postLoad if it acts upon the collection.
-	 * 
-	 * 
+	 *
 	 * HHH-6043
 	 * 
-	 * @param entity
-	 * @param session
-	 * @param postLoadEvent
+	 * @param entity The entity
+	 * @param session The Session
+	 * @param postLoadEvent The (re-used) post-load event
 	 */
 	public static void postLoad(
 			final Object entity,
 			final SessionImplementor session,
 			final PostLoadEvent postLoadEvent) {
 		
 		if ( session.isEventSource() ) {
 			final PersistenceContext persistenceContext
 					= session.getPersistenceContext();
-			final EntityEntry entityEntry = persistenceContext.getEntry(entity);
-			final Serializable id = entityEntry.getId();
-			
-			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() )
-					.setPersister( entityEntry.getPersister() );
+			final EntityEntry entityEntry = persistenceContext.getEntry( entity );
+
+			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() ).setPersister( entityEntry.getPersister() );
 
-			final EventListenerGroup<PostLoadEventListener> listenerGroup
-					= session
-							.getFactory()
+			final EventListenerGroup<PostLoadEventListener> listenerGroup = session.getFactory()
 							.getServiceRegistry()
 							.getService( EventListenerRegistry.class )
 							.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
-		return ( session.getFactory().getSettings().isMinimalPutsEnabled() &&
-						session.getCacheMode()!=CacheMode.REFRESH ) ||
-				( entityEntry.getPersister().hasLazyProperties() &&
-						entityEntry.isLoadedWithLazyPropertiesUnfetched() &&
-						entityEntry.getPersister().isLazyPropertiesCacheable() );
+		return ( session.getFactory().getSettings().isMinimalPutsEnabled()
+				&& session.getCacheMode()!=CacheMode.REFRESH )
+				|| ( entityEntry.getPersister().hasLazyProperties()
+				&& entityEntry.isLoadedWithLazyPropertiesUnfetched()
+				&& entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
+	 *
+	 * @param key The entity key
+	 * @param object The entity instance
+	 * @param persister The entity persister
+	 * @param lockMode The lock mode
+	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
+	 * @param session The Session
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
-			final boolean lazyPropertiesAreUnfetched,
-			final SessionImplementor session
-	) {
+			final boolean lazyPropertiesAreUnFetched,
+			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				null,
 				lockMode,
 				true,
 				persister,
 				false,
-				lazyPropertiesAreUnfetched
-			);
+				lazyPropertiesAreUnFetched
+		);
 	}
 
+	/**
+	 * Same as {@link #addUninitializedEntity}, but here for an entity from the second level cache
+	 *
+	 * @param key The entity key
+	 * @param object The entity instance
+	 * @param persister The entity persister
+	 * @param lockMode The lock mode
+	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
+	 * @param version The version
+	 * @param session The Session
+	 */
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
-			final boolean lazyPropertiesAreUnfetched,
+			final boolean lazyPropertiesAreUnFetched,
 			final Object version,
-			final SessionImplementor session
-	) {
+			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
-				lazyPropertiesAreUnfetched
+				lazyPropertiesAreUnFetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/UnsavedValueFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/UnsavedValueFactory.java
index 23d8e5c517..e7a9f9c727 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/UnsavedValueFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/UnsavedValueFactory.java
@@ -1,139 +1,170 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 
 import org.hibernate.InstantiationException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.property.Getter;
 import org.hibernate.type.IdentifierType;
 import org.hibernate.type.PrimitiveType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
+ * Helper for dealing with unsaved value handling
+ *
  * @author Gavin King
  */
 public class UnsavedValueFactory {
-	
+
+	/**
+	 * Instantiate a class using the provided Constructor
+	 *
+	 * @param constructor The constructor
+	 *
+	 * @return The instantiated object
+	 *
+	 * @throws InstantiationException if something went wrong
+	 */
 	private static Object instantiate(Constructor constructor) {
 		try {
-			return constructor.newInstance( (Object[]) null );
+			return constructor.newInstance();
 		}
 		catch (Exception e) {
 			throw new InstantiationException( "could not instantiate test object", constructor.getDeclaringClass(), e );
 		}
 	}
 	
 	/**
 	 * Return an IdentifierValue for the specified unsaved-value. If none is specified, 
 	 * guess the unsaved value by instantiating a test instance of the class and
 	 * reading it's id property, or if that is not possible, using the java default
-	 * value for the type 
+	 * value for the type
+	 *
+	 * @param unsavedValue The mapping defined unsaved value
+	 * @param identifierGetter The getter for the entity identifier attribute
+	 * @param identifierType The mapping type for the identifier
+	 * @param constructor The constructor for the entity
+	 *
+	 * @return The appropriate IdentifierValue
 	 */
 	public static IdentifierValue getUnsavedIdentifierValue(
-			String unsavedValue, 
+			String unsavedValue,
 			Getter identifierGetter,
 			Type identifierType,
 			Constructor constructor) {
-		
 		if ( unsavedValue == null ) {
-			if ( identifierGetter!=null && constructor!=null ) {
+			if ( identifierGetter != null && constructor != null ) {
 				// use the id value of a newly instantiated instance as the unsaved-value
-				Serializable defaultValue = (Serializable) identifierGetter.get( instantiate(constructor) );
+				final Serializable defaultValue = (Serializable) identifierGetter.get( instantiate(constructor) );
 				return new IdentifierValue( defaultValue );
 			}
 			else if ( identifierGetter != null && (identifierType instanceof PrimitiveType) ) {
-				Serializable defaultValue = ( ( PrimitiveType ) identifierType ).getDefaultValue();
+				final Serializable defaultValue = ( (PrimitiveType) identifierType ).getDefaultValue();
 				return new IdentifierValue( defaultValue );
 			}
 			else {
 				return IdentifierValue.NULL;
 			}
 		}
 		else if ( "null".equals( unsavedValue ) ) {
 			return IdentifierValue.NULL;
 		}
 		else if ( "undefined".equals( unsavedValue ) ) {
 			return IdentifierValue.UNDEFINED;
 		}
 		else if ( "none".equals( unsavedValue ) ) {
 			return IdentifierValue.NONE;
 		}
 		else if ( "any".equals( unsavedValue ) ) {
 			return IdentifierValue.ANY;
 		}
 		else {
 			try {
-				return new IdentifierValue( ( Serializable ) ( ( IdentifierType ) identifierType ).stringToObject( unsavedValue ) );
+				return new IdentifierValue( (Serializable) ( (IdentifierType) identifierType ).stringToObject( unsavedValue ) );
 			}
 			catch ( ClassCastException cce ) {
 				throw new MappingException( "Bad identifier type: " + identifierType.getName() );
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Could not parse identifier unsaved-value: " + unsavedValue );
 			}
 		}
 	}
 
+	/**
+	 * Return an IdentifierValue for the specified unsaved-value. If none is specified,
+	 * guess the unsaved value by instantiating a test instance of the class and
+	 * reading it's version property value, or if that is not possible, using the java default
+	 * value for the type
+	 *
+	 * @param versionUnsavedValue The mapping defined unsaved value
+	 * @param versionGetter The version attribute getter
+	 * @param versionType The mapping type for the version
+	 * @param constructor The constructor for the entity
+	 *
+	 * @return The appropriate VersionValue
+	 */
 	public static VersionValue getUnsavedVersionValue(
 			String versionUnsavedValue, 
 			Getter versionGetter,
 			VersionType versionType,
 			Constructor constructor) {
 		
 		if ( versionUnsavedValue == null ) {
 			if ( constructor!=null ) {
-				Object defaultValue = versionGetter.get( instantiate(constructor) );
+				final Object defaultValue = versionGetter.get( instantiate( constructor ) );
 				// if the version of a newly instantiated object is not the same
 				// as the version seed value, use that as the unsaved-value
-				return versionType.isEqual( versionType.seed( null ), defaultValue ) ?
-						VersionValue.UNDEFINED :
-						new VersionValue( defaultValue );
+				return versionType.isEqual( versionType.seed( null ), defaultValue )
+						? VersionValue.UNDEFINED
+						: new VersionValue( defaultValue );
 			}
 			else {
 				return VersionValue.UNDEFINED;
 			}
 		}
 		else if ( "undefined".equals( versionUnsavedValue ) ) {
 			return VersionValue.UNDEFINED;
 		}
 		else if ( "null".equals( versionUnsavedValue ) ) {
 			return VersionValue.NULL;
 		}
 		else if ( "negative".equals( versionUnsavedValue ) ) {
 			return VersionValue.NEGATIVE;
 		}
 		else {
 			// this should not happen since the DTD prevents it
 			throw new MappingException( "Could not parse version unsaved-value: " + versionUnsavedValue );
 		}
-		
 	}
 
+	private UnsavedValueFactory() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
index 5cf6fdcd51..dd72581fea 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
@@ -1,188 +1,170 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.VersionType;
 
 /**
  * Utilities for dealing with optimistic locking values.
  *
  * @author Gavin King
  */
 public final class Versioning {
-
-	// todo : replace these constants with references to org.hibernate.annotations.OptimisticLockType enum
-
-	/**
-	 * Apply no optimistic locking
-	 */
-	public static final int OPTIMISTIC_LOCK_NONE = -1;
-
-	/**
-	 * Apply optimistic locking based on the defined version or timestamp
-	 * property.
-	 */
-	public static final int OPTIMISTIC_LOCK_VERSION = 0;
-
-	/**
-	 * Apply optimistic locking based on the a current vs. snapshot comparison
-	 * of <b>all</b> properties.
-	 */
-	public static final int OPTIMISTIC_LOCK_ALL = 2;
-
-	/**
-	 * Apply optimistic locking based on the a current vs. snapshot comparison
-	 * of <b>dirty</b> properties.
-	 */
-	public static final int OPTIMISTIC_LOCK_DIRTY = 1;
-
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, Versioning.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			Versioning.class.getName()
+	);
 
 	/**
 	 * Private constructor disallowing instantiation.
 	 */
-	private Versioning() {}
+	private Versioning() {
+	}
 
 	/**
 	 * Create an initial optimistic locking value according the {@link VersionType}
 	 * contract for the version property.
 	 *
 	 * @param versionType The version type.
 	 * @param session The originating session
 	 * @return The initial optimistic locking value
 	 */
 	private static Object seed(VersionType versionType, SessionImplementor session) {
-		Object seed = versionType.seed( session );
-		LOG.tracev( "Seeding: {0}", seed );
+		final Object seed = versionType.seed( session );
+		LOG.tracef( "Seeding: %s", seed );
 		return seed;
 	}
 
 	/**
 	 * Create an initial optimistic locking value according the {@link VersionType}
 	 * contract for the version property <b>if required</b> and inject it into
 	 * the snapshot state.
 	 *
 	 * @param fields The current snapshot state
 	 * @param versionProperty The index of the version property
 	 * @param versionType The version type
 	 * @param session The originating session
 	 * @return True if we injected a new version value into the fields array; false
 	 * otherwise.
 	 */
 	public static boolean seedVersion(
-	        Object[] fields,
-	        int versionProperty,
-	        VersionType versionType,
-	        SessionImplementor session) {
-		Object initialVersion = fields[versionProperty];
+			Object[] fields,
+			int versionProperty,
+			VersionType versionType,
+			SessionImplementor session) {
+		final Object initialVersion = fields[versionProperty];
 		if (
 			initialVersion==null ||
 			// This next bit is to allow for both unsaved-value="negative"
 			// and for "older" behavior where version number did not get
 			// seeded if it was already set in the object
 			// TODO: shift it into unsaved-value strategy
 			( (initialVersion instanceof Number) && ( (Number) initialVersion ).longValue()<0 )
 		) {
 			fields[versionProperty] = seed( versionType, session );
 			return true;
 		}
 		LOG.tracev( "Using initial version: {0}", initialVersion );
 		return false;
 	}
 
 
 	/**
 	 * Generate the next increment in the optimistic locking value according
 	 * the {@link VersionType} contract for the version property.
 	 *
 	 * @param version The current version
 	 * @param versionType The version type
 	 * @param session The originating session
 	 * @return The incremented optimistic locking value.
 	 */
+	@SuppressWarnings("unchecked")
 	public static Object increment(Object version, VersionType versionType, SessionImplementor session) {
-		Object next = versionType.next( version, session );
+		final Object next = versionType.next( version, session );
 		if ( LOG.isTraceEnabled() ) {
-			LOG.tracev( "Incrementing: {0} to {1}", versionType.toLoggableString( version, session.getFactory() ),
-					versionType.toLoggableString( next, session.getFactory() ) );
+			LOG.tracef(
+					"Incrementing: %s to %s",
+					versionType.toLoggableString( version, session.getFactory() ),
+					versionType.toLoggableString( next, session.getFactory() )
+			);
 		}
 		return next;
 	}
 
 	/**
 	 * Inject the optimistic locking value into the entity state snapshot.
 	 *
 	 * @param fields The state snapshot
 	 * @param version The optimistic locking value
 	 * @param persister The entity persister
 	 */
 	public static void setVersion(Object[] fields, Object version, EntityPersister persister) {
 		if ( !persister.isVersioned() ) {
 			return;
 		}
 		fields[ persister.getVersionProperty() ] = version;
 	}
 
 	/**
 	 * Extract the optimistic locking value out of the entity state snapshot.
 	 *
 	 * @param fields The state snapshot
 	 * @param persister The entity persister
 	 * @return The extracted optimistic locking value
 	 */
 	public static Object getVersion(Object[] fields, EntityPersister persister) {
 		if ( !persister.isVersioned() ) {
 			return null;
 		}
 		return fields[ persister.getVersionProperty() ];
 	}
 
 	/**
 	 * Do we need to increment the version number, given the dirty properties?
 	 *
 	 * @param dirtyProperties The array of property indexes which were deemed dirty
 	 * @param hasDirtyCollections Were any collections found to be dirty (structurally changed)
 	 * @param propertyVersionability An array indicating versionability of each property.
 	 * @return True if a version increment is required; false otherwise.
 	 */
 	public static boolean isVersionIncrementRequired(
 			final int[] dirtyProperties,
 			final boolean hasDirtyCollections,
 			final boolean[] propertyVersionability) {
 		if ( hasDirtyCollections ) {
 			return true;
 		}
-		for ( int i = 0; i < dirtyProperties.length; i++ ) {
-			if ( propertyVersionability[ dirtyProperties[i] ] ) {
+		for ( int dirtyProperty : dirtyProperties ) {
+			if ( propertyVersionability[dirtyProperty] ) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
index 1ac0388104..9b87aa8960 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
@@ -1,865 +1,899 @@
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
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
+import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents the state of "stuff" Hibernate is tracking, including (not exhaustive):
  * <ul>
  *     <li>entities</li>
  *     <li>collections</li>
  *     <li>snapshots</li>
  *     <li>proxies</li>
  * </ul>
  * <p/>
  * Often referred to as the "first level cache".
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"JavaDoc"})
 public interface PersistenceContext {
-	
+	/**
+	 * Marker object used to indicate (via reference checking) that no row was returned.
+	 */
+	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
+
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean isStateless();
 
 	/**
 	 * Get the session to which this persistence context is bound.
 	 *
 	 * @return The session.
 	 */
 	public SessionImplementor getSession();
 
 	/**
 	 * Retrieve this persistence context's managed load context.
 	 *
 	 * @return The load context
 	 */
 	public LoadContexts getLoadContexts();
 
 	/**
 	 * Add a collection which has no owner loaded
 	 *
 	 * @param key The collection key under which to add the collection
 	 * @param collection The collection to add
 	 */
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection);
 
 	/**
 	 * Take ownership of a previously unowned collection, if one.  This method returns {@code null} if no such
 	 * collection was previous added () or was previously removed.
 	 * <p/>
 	 * This should indicate the owner is being loaded and we are ready to "link" them.
 	 *
 	 * @param key The collection key for which to locate a collection collection
 	 *
 	 * @return The unowned collection, or {@code null}
 	 */
 	public PersistentCollection useUnownedCollection(CollectionKey key);
 
 	/**
 	 * Get the {@link BatchFetchQueue}, instantiating one if necessary.
 	 *
 	 * @return The batch fetch queue in effect for this persistence context
 	 */
 	public BatchFetchQueue getBatchFetchQueue();
 	
 	/**
 	 * Clear the state of the persistence context
 	 */
 	public void clear();
 
 	/**
 	 * @return false if we know for certain that all the entities are read-only
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean hasNonReadOnlyEntities();
 
 	/**
 	 * Set the status of an entry
 	 *
 	 * @param entry The entry for which to set the status
 	 * @param status The new status
 	 */
 	public void setEntryStatus(EntityEntry entry, Status status);
 
 	/**
 	 * Called after transactions end
 	 */
 	public void afterTransactionCompletion();
 
 	/**
 	 * Get the current state of the entity as known to the underlying database, or null if there is no
 	 * corresponding row
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The entity's (non-cached) snapshot
 	 *
 	 * @see #getCachedDatabaseSnapshot
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister);
 
 	/**
-	 * Get the current database state of the entity, using the cached state snapshot if one is available.
-	 *
-	 * @param key The entity key
-	 *
-	 * @return The entity's (non-cached) snapshot
+	 * Retrieve the cached database snapshot for the requested entity key.
+	 * <p/>
+	 * This differs from {@link #getDatabaseSnapshot} is two important respects:<ol>
+	 * <li>no snapshot is obtained from the database if not already cached</li>
+	 * <li>an entry of {@link #NO_ROW} here is interpretet as an exception</li>
+	 * </ol>
+	 * @param key The entity key for which to retrieve the cached snapshot
+	 * @return The cached snapshot
+	 * @throws IllegalStateException if the cached snapshot was == {@link #NO_ROW}.
 	 */
 	public Object[] getCachedDatabaseSnapshot(EntityKey key);
 
 	/**
 	 * Get the values of the natural id fields as known to the underlying database, or null if the entity has no
 	 * natural id or there is no corresponding row.
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The current (non-cached) snapshot of the entity's natural id state.
 	 */
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Add a canonical mapping from entity key to entity instance
 	 *
 	 * @param key The key under which to add an entity
 	 * @param entity The entity instance to add
 	 */
 	public void addEntity(EntityKey key, Object entity);
 
 	/**
 	 * Get the entity instance associated with the given key
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return The matching entity, or {@code null}
 	 */
 	public Object getEntity(EntityKey key);
 
 	/**
 	 * Is there an entity with the given key in the persistence context
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return {@code true} indicates an entity was found; otherwise {@code false}
 	 */
 	public boolean containsEntity(EntityKey key);
 
 	/**
 	 * Remove an entity.  Also clears up all other state associated with the entity aside from the {@link EntityEntry}
 	 *
 	 * @param key The key whose matching entity should be removed
 	 *
 	 * @return The matching entity
 	 */
 	public Object removeEntity(EntityKey key);
 
 	/**
 	 * Add an entity to the cache by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to add an entity
 	 * @param entity The entity instance
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity);
 
 	/**
 	 * Get an entity cached by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to look for an entity
 	 *
 	 * @return The located entity
 	 */
 	public Object getEntity(EntityUniqueKey euk);
 
 	/**
 	 * Retrieve the {@link EntityEntry} representation of the given entity.
 	 *
 	 * @param entity The entity instance for which to locate the corresponding entry
 	 * @return The entry
 	 */
 	public EntityEntry getEntry(Object entity);
 
 	/**
 	 * Remove an entity entry from the session cache
 	 *
 	 * @param entity The entity instance for which to remove the corresponding entry
 	 * @return The matching entry
 	 */
 	public EntityEntry removeEntry(Object entity);
 
 	/**
 	 * Is there an {@link EntityEntry} registration for this entity instance?
 	 *
 	 * @param entity The entity instance for which to check for an entry
 	 *
 	 * @return {@code true} indicates a matching entry was found.
 	 */
 	public boolean isEntryFor(Object entity);
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 *
 	 * @param coll The persistent collection instance for which to locate the collection entry
 	 *
 	 * @return The matching collection entry
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll);
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
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
 			boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it 
 	 * to the event source's internal caches.
 	 */
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
 			boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Is the given collection associated with this persistence context?
 	 */
 	public boolean containsCollection(PersistentCollection collection);
 	
 	/**
 	 * Is the given proxy associated with this persistence context?
 	 */
 	public boolean containsProxy(Object proxy);
 
 	/**
 	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
 	 *
 	 * @param value The possible proxy to be reassociated.
 	 * @return Whether the passed value represented an actual proxy which got initialized.
 	 * @throws MappingException
 	 */
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException;
 
 	/**
 	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
 	 * reset the identifier of the proxy 
 	 */
 	public void reassociateProxy(Object value, Serializable id) throws MappingException;
 
 	/**
 	 * Get the entity instance underlying the given proxy, throwing
 	 * an exception if the proxy is uninitialized. If the given object
 	 * is not a proxy, simply return the argument.
 	 */
 	public Object unproxy(Object maybeProxy) throws HibernateException;
 
 	/**
 	 * Possibly unproxy the given reference and reassociate it with the current session.
 	 *
 	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
 	 * @return The unproxied instance.
 	 * @throws HibernateException
 	 */
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException;
 
 	/**
 	 * Attempts to check whether the given key represents an entity already loaded within the
 	 * current session.
+	 *
 	 * @param object The entity reference against which to perform the uniqueness check.
+	 *
 	 * @throws HibernateException
 	 */
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException;
 
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
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException;
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * third argument (the entity associated with the key) if no proxy exists. Init
 	 * the proxy to the target implementation, if necessary.
 	 */
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 			throws HibernateException;
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	public Object proxyFor(Object impl) throws HibernateException;
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister)
 			throws MappingException;
 
 	/**
 	 * Get the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner if its entity ID is available from the collection's loaded key
 	 * and the owner entity is in the persistence context; otherwise, returns null
 	 */
 	Object getLoadedCollectionOwnerOrNull(PersistentCollection collection);
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection);
 
 	/**
 	 * add a collection we just loaded up (still needs initializing)
 	 */
 	public void addUninitializedCollection(CollectionPersister persister,
 			PersistentCollection collection, Serializable id);
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	public void addUninitializedDetachedCollection(CollectionPersister persister,
 			PersistentCollection collection);
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 			throws HibernateException;
 
 	/**
 	 * add an (initialized) collection that was created by another session and passed
 	 * into update() (ie. one with a snapshot and existing state on the database)
 	 */
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister,
 			PersistentCollection collection) throws HibernateException;
 
 	/**
 	 * add a collection we just pulled out of the cache (does not need initializing)
 	 */
 	public CollectionEntry addInitializedCollection(CollectionPersister persister,
 			PersistentCollection collection, Serializable id) throws HibernateException;
 
 	/**
 	 * Get the collection instance associated with the <tt>CollectionKey</tt>
 	 */
 	public PersistentCollection getCollection(CollectionKey collectionKey);
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	public void addNonLazyCollection(PersistentCollection collection);
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	public void initializeNonLazyCollections() throws HibernateException;
 
 	/**
 	 * Get the <tt>PersistentCollection</tt> object for an array
 	 */
 	public PersistentCollection getCollectionHolder(Object array);
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading 
 	 * array, since the array instance is not created until endLoad().
 	 */
 	public void addCollectionHolder(PersistentCollection holder);
 	
 	/**
 	 * Remove the mapping of collection to holder during eviction
 	 * of the owning entity
 	 */
 	public PersistentCollection removeCollectionHolder(Object array);
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	public Serializable getSnapshot(PersistentCollection coll);
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	public CollectionEntry getCollectionEntryOrNull(Object collection);
 
 	/**
 	 * Get an existing proxy by key
 	 */
 	public Object getProxy(EntityKey key);
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	public void addProxy(EntityKey key, Object proxy);
 
 	/**
-	 * Remove a proxy from the session cache
+	 * Remove a proxy from the session cache.
+	 * <p/>
+	 * Additionally, ensure that any load optimization references
+	 * such as batch or subselect loading get cleaned up as well.
+	 *
+	 * @param key The key of the entity proxy to be removed
+	 * @return The proxy reference.
 	 */
 	public Object removeProxy(EntityKey key);
 
 	/** 
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	public HashSet getNullifiableEntityKeys();
 
 	/**
 	 * Get the mapping from key value to entity instance
 	 */
 	public Map getEntitiesByKey();
 
 	/**
 	 * Provides access to the entity/EntityEntry combos associated with the persistence context in a manner that
 	 * is safe from reentrant access.  Specifically, it is safe from additions/removals while iterating.
 	 *
 	 * @return
 	 */
 	public Map.Entry<Object,EntityEntry>[] reentrantSafeEntityEntries();
 
 	/**
 	 * Get the mapping from entity instance to entity entry
 	 *
 	 * @deprecated Due to the introduction of EntityEntryContext and bytecode enhancement; only valid really for
 	 * sizing, see {@link #getNumberOfManagedEntities}.  For iterating the entity/EntityEntry combos, see
 	 * {@link #reentrantSafeEntityEntries}
 	 */
 	@Deprecated
 	public Map getEntityEntries();
 
 	public int getNumberOfManagedEntities();
 
 	/**
 	 * Get the mapping from collection instance to collection entry
 	 */
 	public Map getCollectionEntries();
 
 	/**
 	 * Get the mapping from collection key to collection instance
 	 */
 	public Map getCollectionsByKey();
 
 	/**
 	 * How deep are we cascaded?
 	 */
 	public int getCascadeLevel();
 	
 	/**
 	 * Called before cascading
 	 */
 	public int incrementCascadeLevel();
 
 	/**
 	 * Called after cascading
 	 */
 	public int decrementCascadeLevel();
 
 	/**
 	 * Is a flush cycle currently in process?
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean isFlushing();
 	
 	/**
 	 * Called before and after the flushcycle
 	 */
 	public void setFlushing(boolean flushing);
 
 	/**
 	 * Call this before begining a two-phase load
 	 */
 	public void beforeLoad();
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	public void afterLoad();
 	
 	/**
 	 * Is in a two-phase load? 
 	 */
 	public boolean isLoadFinished();
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	public String toString();
 
 	/**
-	 * Search the persistence context for an owner for the child object,
-	 * given a collection role
+	 * Search <tt>this</tt> persistence context for an associated entity instance which is considered the "owner" of
+	 * the given <tt>childEntity</tt>, and return that owner's id value.  This is performed in the scenario of a
+	 * uni-directional, non-inverse one-to-many collection (which means that the collection elements do not maintain
+	 * a direct reference to the owner).
+	 * <p/>
+	 * As such, the processing here is basically to loop over every entity currently associated with this persistence
+	 * context and for those of the correct entity (sub) type to extract its collection role property value and see
+	 * if the child is contained within that collection.  If so, we have found the owner; if not, we go on.
+	 * <p/>
+	 * Also need to account for <tt>mergeMap</tt> which acts as a local copy cache managed for the duration of a merge
+	 * operation.  It represents a map of the detached entity instances pointing to the corresponding managed instance.
+	 *
+	 * @param entityName The entity name for the entity type which would own the child
+	 * @param propertyName The name of the property on the owning entity type which would name this child association.
+	 * @param childEntity The child entity instance for which to locate the owner instance id.
+	 * @param mergeMap A map of non-persistent instances from an on-going merge operation (possibly null).
+	 *
+	 * @return The id of the entityName instance which is said to own the child; null if an appropriate owner not
+	 * located.
 	 */
-	public Serializable getOwnerId(String entity, String property, Object childObject, Map mergeMap);
+	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap);
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	public Object getIndexInOwner(String entity, String property, Object childObject, Map mergeMap);
 
 	/**
 	 * Record the fact that the association belonging to the keyed
 	 * entity is null.
 	 */
 	public void addNullProperty(EntityKey ownerKey, String propertyName);
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName);
 
 	/**
 	 * Will entities and proxies that are loaded into this persistence
 	 * context be made read-only by default?
 	 *
 	 * To determine the read-only/modifiable setting for a particular entity
 	 * or proxy:
 	 * @see PersistenceContext#isReadOnly(Object)
 	 * @see org.hibernate.Session#isReadOnly(Object) 
 	 *
 	 * @return true, loaded entities/proxies will be made read-only by default;
 	 *         false, loaded entities/proxies will be made modifiable by default.
 	 *
 	 * @see org.hibernate.Session#isDefaultReadOnly() 
 	 */
 	public boolean isDefaultReadOnly();
 
 	/**
 	 * Change the default for entities and proxies loaded into this persistence
 	 * context from modifiable to read-only mode, or from modifiable to read-only
 	 * mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the persistence context's current setting.
 	 *
 	 * To change the read-only/modifiable setting for a particular entity
 	 * or proxy that is already in this session:
 +	 * @see PersistenceContext#setReadOnly(Object,boolean)
 	 * @see org.hibernate.Session#setReadOnly(Object, boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see org.hibernate.Query#setReadOnly(boolean)
 	 *
 	 * @param readOnly true, the default for loaded entities/proxies is read-only;
 	 *                 false, the default for loaded entities/proxies is modifiable
 	 *
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 */
 	public void setDefaultReadOnly(boolean readOnly);
 
 	/**
 	 * Is the entity or proxy read-only?
 	 * <p/>
 	 * To determine the default read-only/modifiable setting used for entities and proxies that are loaded into the
 	 * session use {@link org.hibernate.Session#isDefaultReadOnly}
 	 *
 	 * @param entityOrProxy an entity or proxy
 	 *
 	 * @return {@code true} if the object is read-only; otherwise {@code false} to indicate that the object is
 	 * modifiable.
 	 */
 	public boolean isReadOnly(Object entityOrProxy);
 
 	/**
 	 * Set an unmodified persistent object to read-only mode, or a read-only
 	 * object to modifiable mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * If the entity or proxy already has the specified read-only/modifiable
 	 * setting, then this method does nothing.
 	 *
 	 * @param entityOrProxy an entity or proxy
 	 * @param readOnly if {@code true}, the entity or proxy is made read-only; otherwise, the entity or proxy is made
 	 * modifiable.
 	 *
 	 * @see org.hibernate.Session#setDefaultReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 * @see org.hibernate.Query#setReadOnly
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId);
 
 	/**
 	 * Add a child/parent relation to cache for cascading op
 	 *
 	 * @param child The child of the relationship
 	 * @param parent The parent of the relationship
 	 */
 	public void addChildParent(Object child, Object parent);
 
 	/**
 	 * Remove child/parent relation from cache
 	 *
 	 * @param child The child to be removed.
 	 */
 	public void removeChildParent(Object child);
 
 	/**
 	 * Register keys inserted during the current transaction
 	 *
 	 * @param persister The entity persister
 	 * @param id The id
 	 */
 	public void registerInsertedKey(EntityPersister persister, Serializable id);
 
 	/**
 	 * Allows callers to check to see if the identified entity was inserted during the current transaction.
 	 *
 	 * @param persister The entity persister
 	 * @param id The id
 	 *
 	 * @return True if inserted during this transaction, false otherwise.
 	 */
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id);
 
 	/**
 	 * Provides centralized access to natural-id-related functionality.
 	 */
 	public static interface NaturalIdHelper {
 		public static final Serializable INVALID_NATURAL_ID_REFERENCE = new Serializable() {};
 
 		/**
 		 * Given an array of "full entity state", extract the portions that represent the natural id
 		 * 
 		 * @param state The attribute state array
 		 * @param persister The persister representing the entity type.
 		 * 
 		 * @return The extracted natural id values
 		 */
 		public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister);
 
 		/**
 		 * Given an entity instance, extract the values that represent the natural id
 		 *
 		 * @param entity The entity instance
 		 * @param persister The persister representing the entity type.
 		 *
 		 * @return The extracted natural id values
 		 */
 		public Object[] extractNaturalIdValues(Object entity, EntityPersister persister);
 
 		/**
 		 * Performs processing related to creating natural-id cross-reference entries on load.
 		 * Handles both the local (transactional) and shared (second-level) caches.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param id The primary key value
 		 * @param naturalIdValues The natural id values
 		 */
 		public void cacheNaturalIdCrossReferenceFromLoad(
 				EntityPersister persister, 
 				Serializable id, 
 				Object[] naturalIdValues);
 
 		/**
 		 * Creates necessary local cross-reference entries.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param id The primary key value
 		 * @param state Generally the "full entity state array", though could also be the natural id values array
 		 * @param previousState Generally the "full entity state array", though could also be the natural id values array.  
 		 * 		Specifically represents the previous values on update, and so is only used with {@link CachedNaturalIdValueSource#UPDATE}
 		 * @param source Enumeration representing how these values are coming into cache.
 		 */
 		public void manageLocalNaturalIdCrossReference(
 				EntityPersister persister,
 				Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source);
 
 		/**
 		 * Cleans up local cross-reference entries.
 		 * 
 		 * @param persister The persister representing the entity type.
 		 * @param id The primary key value
 		 * @param state Generally the "full entity state array", though could also be the natural id values array
 		 * 
 		 * @return The local cached natural id values (could be different from given values).
 		 */
 		public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state);
 
 		/**
 		 * Creates necessary shared (second level cache) cross-reference entries.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param id The primary key value
 		 * @param state Generally the "full entity state array", though could also be the natural id values array
 		 * @param previousState Generally the "full entity state array", though could also be the natural id values array.  
 		 * 		Specifically represents the previous values on update, and so is only used with {@link CachedNaturalIdValueSource#UPDATE}
 		 * @param source Enumeration representing how these values are coming into cache.
 		 */
 		public void manageSharedNaturalIdCrossReference(
 				EntityPersister persister,
 				Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source);
 
 		/**
 		 * Cleans up local cross-reference entries.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param id The primary key value
 		 * @param naturalIdValues The natural id values array
 		 */
 		public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues);
 
 		/**
 		 * Given a persister and primary key, find the corresponding cross-referenced natural id values.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param pk The primary key value
 		 * 
 		 * @return The cross-referenced natural-id values, or {@code null}
 		 */
 		public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk);
 
 		/**
 		 * Given a persister and natural-id values, find the corresponding cross-referenced primary key. Will return
 		 * {@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE} if the given natural ids are known to
 		 * be invalid.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param naturalIdValues The natural id value(s)
 		 *
 		 * @return The corresponding cross-referenced primary key, 
 		 * 		{@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE},
 		 * 		or {@code null}. 
 		 */
 		public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues);
 
 		/**
 		 * Find all the locally cached primary key cross-reference entries for the given persister.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * 
 		 * @return The primary keys
 		 */
 		public Collection<Serializable> getCachedPkResolutions(EntityPersister persister);
 
 		/**
 		 * Part of the "load synchronization process".  Responsible for maintaining cross-reference entries
 		 * when natural-id values were found to have changed.  Also responsible for tracking the old values 
 		 * as no longer valid until the next flush because otherwise going to the database would just re-pull
 		 * the old values as valid.  In this last responsibility, {@link #cleanupFromSynchronizations} is
 		 * the inverse process called after flush to clean up those entries.
 		 *
 		 * @param persister The persister representing the entity type.
 		 * @param pk The primary key
 		 * @param entity The entity instance
 		 * 
 		 * @see #cleanupFromSynchronizations
 		 */
 		public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity);
 
 		/**
 		 * The clean up process of {@link #handleSynchronization}.  Responsible for cleaning up the tracking
 		 * of old values as no longer valid.
 		 */
 		public void cleanupFromSynchronizations();
 
 		/**
 		 * Called on {@link org.hibernate.Session#evict} to give a chance to clean up natural-id cross refs.
 		 *
 		 * @param object The entity instance.
 		 * @param persister The entity persister
 		 * @param identifier The entity identifier
 		 */
 		public void handleEviction(Object object, EntityPersister persister, Serializable identifier);
 	}
 
 	/**
 	 * Access to the natural-id helper for this persistence context
 	 * 
 	 * @return This persistence context's natural-id helper
 	 */
 	public NaturalIdHelper getNaturalIdHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 0b78c1bd63..784559c18d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -102,2001 +102,2001 @@ import org.hibernate.type.VersionType;
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				LOG.usingFollowOnLocking();
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuilder( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 		if ( queryParameters.isReadOnlyInitialized() ) {
 			// The read-only/modifiable mode for the query was explicitly set.
 			// Temporarily set the default read-only/modifiable setting to the query's setting.
 			persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 		}
 		else {
 			// The read-only/modifiable setting for the query was not initialized.
 			// Use the default read-only/modifiable from the persistence context instead.
 			queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 		}
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 				);
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to perform the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not perform sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 		boolean isDebugEnabled = LOG.isDebugEnabled();
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 		   if ( isDebugEnabled ) 
 			   LOG.debugf( "Result set row: %s", count );
 			Object result = getRowFromResultSet(
 					rs,
 					session,
 					queryParameters,
 					lockModesArray,
 					optionalObjectKey,
 					hydratedObjects,
 					keys,
 					returnProxies,
 					forcedResultTransformer
 			);
 			results.add( result );
 			if ( createSubselects ) {
 				subselectResultKeys.add(keys);
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		if ( LOG.isTraceEnabled() )
 		   LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 					);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
-				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
+				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 	        final EntityKey[] keys,
 	        final Loadable[] persisters,
 	        final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 	        final Object optionalOwner,
 	        final Serializable optionalKey,
 	        final CollectionPersister persister,
 	        final CollectionAliases descriptor,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		final Serializable collectionRowKey = (Serializable) persister.readKey(
 				rs,
 				descriptor.getSuffixedKeyAliases(),
 				session
 			);
 
 		if ( collectionRowKey != null ) {
 			// we found a collection element in the result set
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
 			}
 
 			Object owner = optionalOwner;
 			if ( owner == null ) {
 				owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 				if ( owner == null ) {
 					//TODO: This is assertion is disabled because there is a bug that means the
 					//	  original owner of a transient, uninitialized collection is not known
 					//	  if the collection is re-referenced by a different object associated
 					//	  with the current Session
 					//throw new AssertionFailure("bug loading unowned collection");
 				}
 			}
 
 			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, collectionRowKey );
 
 			if ( rowCollection != null ) {
 				rowCollection.readFrom( rs, persister, descriptor, owner );
 			}
 
 		}
 		else if ( optionalKey != null ) {
 			// we did not find a collection element in the result set, so we
 			// ensure that a collection is created with the owner's identifier,
 			// since what we have is an empty collection
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
 			}
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 	        final Serializable[] keys,
 	        final Object resultSetId,
 	        final SessionImplementor session) {
 
 		if ( keys != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( debugEnabled ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final ResultSet rs,
 	        final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final Object entity,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 	        final ResultSet rs,
 	        final Loadable[] persisters,
 	        final EntityKey[] keys,
 	        final Object optionalObject,
 	        final EntityKey optionalObjectKey,
 	        final LockMode[] lockModes,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 						);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 						);
 				}
 
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 			final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final EntityKey key,
 	        final Object object,
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
 			}
 		}
 	}
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final String rowIdAlias,
 	        final EntityKey key,
 	        final LockMode lockMode,
 	        final EntityKey optionalObjectKey,
 	        final Object optionalObject,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 			);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array!=null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 	        final ResultSet rs,
 	        final int i,
 	        final Object object,
 	        final String instanceEntityName,
 	        final EntityKey key,
 	        final String rowIdAlias,
 	        final LockMode lockMode,
 	        final Loadable rootPersister,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 				);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 					);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final String sql,
 	        final QueryParameters queryParameters,
 	        final LimitHandler limitHandler,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 	        final PreparedStatement statement,
 	        final QueryParameters queryParameters,
 	        final int startIndex,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final RowSelection selection,
 	        final LimitHandler limitHandler,
 	        final boolean autodiscovertypes,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 			   if ( LOG.isDebugEnabled() )
 			      LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			LOG.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
index 49e51a4dfd..940dab925d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
@@ -1,803 +1,803 @@
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
 package org.hibernate.loader.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessingContextImpl implements ResultSetProcessingContext {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessingContextImpl.class );
 
 	private final ResultSet resultSet;
 	private final SessionImplementor session;
 	private final LoadPlan loadPlan;
 	private final boolean readOnly;
 	private final QueryParameters queryParameters;
 	private final NamedParameterContext namedParameterContext;
 	private final LoadQueryAliasResolutionContext aliasResolutionContext;
 	private final boolean hadSubselectFetches;
 
 	private final EntityKey dictatedRootEntityKey;
 
 	private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
 
 	private Map<EntityPersister,Set<EntityKey>> subselectLoadableEntityKeyMap;
 	private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
 
 	public ResultSetProcessingContextImpl(
 			ResultSet resultSet,
 			SessionImplementor session,
 			LoadPlan loadPlan,
 			boolean readOnly,
 			boolean useOptionalEntityKey,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
 			LoadQueryAliasResolutionContext aliasResolutionContext,
 			boolean hadSubselectFetches) {
 		this.resultSet = resultSet;
 		this.session = session;
 		this.loadPlan = loadPlan;
 		this.readOnly = readOnly;
 		this.queryParameters = queryParameters;
 		this.namedParameterContext = namedParameterContext;
 		this.aliasResolutionContext = aliasResolutionContext;
 		this.hadSubselectFetches = hadSubselectFetches;
 
 		if ( useOptionalEntityKey ) {
 			this.dictatedRootEntityKey = ResultSetProcessorHelper.getOptionalObjectKey( queryParameters, session );
 			if ( this.dictatedRootEntityKey == null ) {
 				throw new HibernateException( "Unable to resolve optional entity-key" );
 			}
 		}
 		else {
 			this.dictatedRootEntityKey = null;
 		}
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	@Override
 	public EntityKey getDictatedRootEntityKey() {
 		return dictatedRootEntityKey;
 	}
 
 	private Map<EntityReference,IdentifierResolutionContext> identifierResolutionContextMap;
 
 	@Override
 	public IdentifierResolutionContext getIdentifierResolutionContext(final EntityReference entityReference) {
 		if ( identifierResolutionContextMap == null ) {
 			identifierResolutionContextMap = new HashMap<EntityReference, IdentifierResolutionContext>();
 		}
 		IdentifierResolutionContext context = identifierResolutionContextMap.get( entityReference );
 		if ( context == null ) {
 			context = new IdentifierResolutionContext() {
 				private Object hydratedForm;
 				private EntityKey entityKey;
 
 				@Override
 				public EntityReference getEntityReference() {
 					return entityReference;
 				}
 
 				@Override
 				public void registerHydratedForm(Object hydratedForm) {
 					if ( this.hydratedForm != null ) {
 						// this could be bad...
 					}
 					this.hydratedForm = hydratedForm;
 				}
 
 				@Override
 				public Object getHydratedForm() {
 					return hydratedForm;
 				}
 
 				@Override
 				public void registerEntityKey(EntityKey entityKey) {
 					if ( this.entityKey != null ) {
 						// again, could be trouble...
 					}
 					this.entityKey = entityKey;
 				}
 
 				@Override
 				public EntityKey getEntityKey() {
 					return entityKey;
 				}
 			};
 			identifierResolutionContextMap.put( entityReference, context );
 		}
 
 		return context;
 	}
 
 	@Override
 	public Set<IdentifierResolutionContext> getIdentifierResolutionContexts() {
 		return Collections.unmodifiableSet(
 				new HashSet<IdentifierResolutionContext>( identifierResolutionContextMap.values() )
 		);
 	}
 
 	@Override
 	public LoadQueryAliasResolutionContext getLoadQueryAliasResolutionContext() {
 		return aliasResolutionContext;
 	}
 
 	@Override
 	public void checkVersion(
 			ResultSet resultSet,
 			EntityPersister persister,
 			EntityAliases entityAliases,
 			EntityKey entityKey,
 			Object entityInstance) {
 		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
 
 		if ( version != null ) {
 			//null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			final Object currentVersion;
 			try {
 				currentVersion = versionType.nullSafeGet(
 						resultSet,
 						entityAliases.getSuffixedVersionAliases(),
 						session,
 						null
 				);
 			}
 			catch (SQLException e) {
 				throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 						e,
 						"Could not read version value from result set"
 				);
 			}
 
 			if ( !versionType.isEqual( version, currentVersion ) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor().optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
 			}
 		}
 	}
 
 	@Override
 	public String getConcreteEntityTypeName(
 			final ResultSet rs,
 			final EntityPersister persister,
 			final EntityAliases entityAliases,
 			final EntityKey entityKey) {
 
 		final Loadable loadable = (Loadable) persister;
 		if ( ! loadable.hasSubclasses() ) {
 			return persister.getEntityName();
 		}
 
 		final Object discriminatorValue;
 		try {
 			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
 					rs,
 					entityAliases.getSuffixedDiscriminatorAlias(),
 					session,
 					null
 			);
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read discriminator value from ResultSet"
 			);
 		}
 
 		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
 
 		if ( result == null ) {
 			// whoops! we got an instance of another class hierarchy branch
 			throw new WrongClassException(
 					"Discriminator: " + discriminatorValue,
 					entityKey.getIdentifier(),
 					persister.getEntityName()
 			);
 		}
 
 		return result;
 	}
 
 	@Override
 	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext) {
 		final Object existing = getSession().getEntityUsingInterceptor( entityKey );
 
 		if ( existing != null ) {
 			if ( !entityKeyContext.getEntityPersister().isInstance( existing ) ) {
 				throw new WrongClassException(
 						"loaded object was of wrong class " + existing.getClass(),
 						entityKey.getIdentifier(),
 						entityKeyContext.getEntityPersister().getEntityName()
 				);
 			}
 
 			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
 					? LockMode.NONE
 					: entityKeyContext.getLockMode();
 
 			if ( requestedLockMode != LockMode.NONE ) {
 				final LockMode currentLockMode = getSession().getPersistenceContext().getEntry( existing ).getLockMode();
 				final boolean isVersionCheckNeeded = entityKeyContext.getEntityPersister().isVersioned()
 						&& currentLockMode.lessThan( requestedLockMode );
 
 				// we don't need to worry about existing version being uninitialized because this block isn't called
 				// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
 				if ( isVersionCheckNeeded ) {
 					//we only check the version when *upgrading* lock modes
 					checkVersion(
 							resultSet,
 							entityKeyContext.getEntityPersister(),
 							aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
 							entityKey,
 							existing
 					);
 					//we need to upgrade the lock mode to the mode requested
 					getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
 				}
 			}
 
 			return existing;
 		}
 		else {
 			final String concreteEntityTypeName = getConcreteEntityTypeName(
 					resultSet,
 					entityKeyContext.getEntityPersister(),
 					aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
 					entityKey
 			);
 
 			final Object entityInstance = getSession().instantiate(
 					concreteEntityTypeName,
 					entityKey.getIdentifier()
 			);
 
 			//need to hydrate it.
 
 			// grab its state from the ResultSet and keep it in the Session
 			// (but don't yet initialize the object itself)
 			// note that we acquire LockMode.READ even if it was not requested
 			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
 					? LockMode.NONE
 					: entityKeyContext.getLockMode();
 			final LockMode acquiredLockMode = requestedLockMode == LockMode.NONE
 					? LockMode.READ
 					: requestedLockMode;
 
 			loadFromResultSet(
 					resultSet,
 					entityInstance,
 					concreteEntityTypeName,
 					entityKey,
 					aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
 					acquiredLockMode,
 					entityKeyContext.getEntityPersister(),
 					true,
 					entityKeyContext.getEntityPersister().getEntityMetamodel().getEntityType()
 			);
 
 			// materialize associations (and initialize the object) later
 			registerHydratedEntity( entityKeyContext.getEntityPersister(), entityKey, entityInstance );
 
 			return entityInstance;
 		}
 	}
 
 	@Override
 	public void loadFromResultSet(
 			ResultSet resultSet,
 			Object entityInstance,
 			String concreteEntityTypeName,
 			EntityKey entityKey,
 			EntityAliases entityAliases,
 			LockMode acquiredLockMode,
 			EntityPersister rootPersister,
 			boolean eagerFetch,
 			EntityType associationType) {
 
 		final Serializable id = entityKey.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getSession().getFactory().getEntityPersister( concreteEntityTypeName );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing object from ResultSet: {0}",
 					MessageHelper.infoString(
 							persister,
 							id,
 							getSession().getFactory()
 					)
 			);
 		}
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				entityKey,
 				entityInstance,
 				persister,
 				acquiredLockMode,
 				!eagerFetch,
 				session
 		);
 
 		// This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				entityAliases.getSuffixedPropertyAliases() :
 				entityAliases.getSuffixedPropertyAliases(persister);
 
 		final Object[] values;
 		try {
 			values = persister.hydrate(
 					resultSet,
 					id,
 					entityInstance,
 					(Loadable) rootPersister,
 					cols,
 					eagerFetch,
 					session
 			);
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity state from ResultSet : " + entityKey
 			);
 		}
 
 		final Object rowId;
 		try {
 			rowId = persister.hasRowId() ? resultSet.getObject( entityAliases.getRowIdAlias() ) : null;
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity row-id from ResultSet : " + entityKey
 			);
 		}
 
 		if ( associationType != null ) {
 			String ukName = associationType.getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, entityInstance ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, entityInstance );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				entityInstance,
 				acquiredLockMode,
 				!eagerFetch,
 				session
 		);
 
 	}
 
 	public void readCollectionElements(final Object[] row) {
 			LoadPlanVisitor.visit(
 					loadPlan,
 					new LoadPlanVisitationStrategyAdapter() {
 						@Override
 						public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 							readCollectionElement(
 									null,
 									null,
 									rootCollectionReturn.getCollectionPersister(),
 									aliasResolutionContext.resolveCollectionColumnAliases( rootCollectionReturn ),
 									resultSet,
 									session
 							);
 						}
 
 						@Override
 						public void startingCollectionFetch(CollectionFetch collectionFetch) {
 							// TODO: determine which element is the owner.
 							final Object owner = row[ 0 ];
 							readCollectionElement(
 									owner,
 									collectionFetch.getCollectionPersister().getCollectionType().getKeyOfOwner( owner, session ),
 									collectionFetch.getCollectionPersister(),
 									aliasResolutionContext.resolveCollectionColumnAliases( collectionFetch ),
 									resultSet,
 									session
 							);
 						}
 
 						private void readCollectionElement(
 								final Object optionalOwner,
 								final Serializable optionalKey,
 								final CollectionPersister persister,
 								final CollectionAliases descriptor,
 								final ResultSet rs,
 								final SessionImplementor session) {
 
 							try {
 								final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 								final Serializable collectionRowKey = (Serializable) persister.readKey(
 										rs,
 										descriptor.getSuffixedKeyAliases(),
 										session
 								);
 
 								if ( collectionRowKey != null ) {
 									// we found a collection element in the result set
 
 									if ( LOG.isDebugEnabled() ) {
 										LOG.debugf( "Found row of collection: %s",
 												MessageHelper.collectionInfoString( persister, collectionRowKey, session.getFactory() ) );
 									}
 
 									Object owner = optionalOwner;
 									if ( owner == null ) {
 										owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 										if ( owner == null ) {
 											//TODO: This is assertion is disabled because there is a bug that means the
 											//	  original owner of a transient, uninitialized collection is not known
 											//	  if the collection is re-referenced by a different object associated
 											//	  with the current Session
 											//throw new AssertionFailure("bug loading unowned collection");
 										}
 									}
 
 									PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 											.getCollectionLoadContext( rs )
 											.getLoadingCollection( persister, collectionRowKey );
 
 									if ( rowCollection != null ) {
 										rowCollection.readFrom( rs, persister, descriptor, owner );
 									}
 
 								}
 								else if ( optionalKey != null ) {
 									// we did not find a collection element in the result set, so we
 									// ensure that a collection is created with the owner's identifier,
 									// since what we have is an empty collection
 
 									if ( LOG.isDebugEnabled() ) {
 										LOG.debugf( "Result set contains (possibly empty) collection: %s",
 												MessageHelper.collectionInfoString( persister, optionalKey, session.getFactory() ) );
 									}
 
 									persistenceContext.getLoadContexts()
 											.getCollectionLoadContext( rs )
 											.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 								}
 
 								// else no collection element, but also no owner
 							}
 							catch ( SQLException sqle ) {
 								// TODO: would be nice to have the SQL string that failed...
 								throw session.getFactory().getSQLExceptionHelper().convert(
 										sqle,
 										"could not read next row of results"
 								);
 							}
 						}
 
 					}
 			);
 	}
 
 	@Override
 	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance) {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		currentRowHydratedEntityRegistrationList.add( new HydratedEntityRegistration( persister, entityKey, entityInstance ) );
 	}
 
 	/**
 	 * Package-protected
 	 */
 	void finishUpRow() {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 
 		// managing the running list of registrations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hydratedEntityRegistrationList == null ) {
 			hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		hydratedEntityRegistrationList.addAll( currentRowHydratedEntityRegistrationList );
 
 
 		// managing the map forms needed for subselect fetch generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hadSubselectFetches ) {
 			if ( subselectLoadableEntityKeyMap == null ) {
 				subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
 			}
 			for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
 				Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.persister );
 				if ( entityKeys == null ) {
 					entityKeys = new HashSet<EntityKey>();
 					subselectLoadableEntityKeyMap.put( registration.persister, entityKeys );
 				}
 				entityKeys.add( registration.key );
 			}
 		}
 
 		// release the currentRowHydratedEntityRegistrationList entries
 		currentRowHydratedEntityRegistrationList.clear();
 	}
 
 	/**
 	 * Package-protected
 	 *
 	 * @param afterLoadActionList List of after-load actions to perform
 	 */
 	void finishUp(List<AfterLoadAction> afterLoadActionList) {
 		initializeEntitiesAndCollections( afterLoadActionList );
 		createSubselects();
 
 		if ( hydratedEntityRegistrationList != null ) {
 			hydratedEntityRegistrationList.clear();
 			hydratedEntityRegistrationList = null;
 		}
 
 		if ( subselectLoadableEntityKeyMap != null ) {
 			subselectLoadableEntityKeyMap.clear();
 			subselectLoadableEntityKeyMap = null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(List<AfterLoadAction> afterLoadActionList) {
 		// for arrays, we should end the collection load before resolving the entities, since the
 		// actual array instances are not instantiated during loading
 		finishLoadingArrays();
 
 
 		// IMPORTANT: reuse the same event instances for performance!
 		final PreLoadEvent preLoadEvent;
 		final PostLoadEvent postLoadEvent;
 		if ( session.isEventSource() ) {
 			preLoadEvent = new PreLoadEvent( (EventSource) session );
 			postLoadEvent = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			preLoadEvent = null;
 			postLoadEvent = null;
 		}
 
 		// now finish loading the entities (2-phase load)
-		performTwoPhaseLoad( preLoadEvent, postLoadEvent );
+		performTwoPhaseLoad( preLoadEvent );
 
 		// now we can finalize loading collections
 		finishLoadingCollections();
 
 		// finally, perform post-load operations
 		postLoad( postLoadEvent, afterLoadActionList );
 	}
 
 	private void finishLoadingArrays() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingArray( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingArray(CollectionPersister persister) {
 						if ( persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
-	private void performTwoPhaseLoad(PreLoadEvent preLoadEvent, PostLoadEvent postLoadEvent) {
+	private void performTwoPhaseLoad(PreLoadEvent preLoadEvent) {
 		final int numberOfHydratedObjects = hydratedEntityRegistrationList == null
 				? 0
 				: hydratedEntityRegistrationList.size();
 		LOG.tracev( "Total objects hydrated: {0}", numberOfHydratedObjects );
 
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
-			TwoPhaseLoad.initializeEntity( registration.instance, readOnly, session, preLoadEvent, postLoadEvent );
+			TwoPhaseLoad.initializeEntity( registration.instance, readOnly, session, preLoadEvent );
 		}
 	}
 
 	private void finishLoadingCollections() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 						endLoadingCollection( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingCollection( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingCollection(CollectionPersister persister) {
 						if ( ! persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
 	private void postLoad(PostLoadEvent postLoadEvent, List<AfterLoadAction> afterLoadActionList) {
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
 			TwoPhaseLoad.postLoad( registration.instance, session, postLoadEvent );
 			if ( afterLoadActionList != null ) {
 				for ( AfterLoadAction afterLoadAction : afterLoadActionList ) {
 					afterLoadAction.afterLoad( session, registration.instance, (Loadable) registration.persister );
 				}
 			}
 		}
 	}
 
 	private void createSubselects() {
 		if ( subselectLoadableEntityKeyMap == null || subselectLoadableEntityKeyMap.size() <= 1 ) {
 			// if we only returned one entity, query by key is more efficient; so do nothing here
 			return;
 		}
 
 		final Map<String, int[]> namedParameterLocMap =
 				ResultSetProcessorHelper.buildNamedParameterLocMap( queryParameters, namedParameterContext );
 
 		for ( Map.Entry<EntityPersister, Set<EntityKey>> entry : subselectLoadableEntityKeyMap.entrySet() ) {
 			if ( ! entry.getKey().hasSubselectLoadableCollections() ) {
 				continue;
 			}
 
 			SubselectFetch subselectFetch = new SubselectFetch(
 					//getSQLString(),
 					null, // aliases[i],
 					(Loadable) entry.getKey(),
 					queryParameters,
 					entry.getValue(),
 					namedParameterLocMap
 			);
 
 			for ( EntityKey key : entry.getValue() ) {
 				session.getPersistenceContext().getBatchFetchQueue().addSubselect( key, subselectFetch );
 			}
 
 		}
 	}
 
 	private static class HydratedEntityRegistration {
 		private final EntityPersister persister;
 		private final EntityKey key;
 		private final Object instance;
 
 		private HydratedEntityRegistration(EntityPersister persister, EntityKey key, Object instance) {
 			this.persister = persister;
 			this.key = key;
 			this.instance = instance;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
index e8a299dd48..41f1bb2346 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
@@ -1,869 +1,883 @@
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
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.dialect.lock.OptimisticLockingStrategy;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.sql.Alias;
 
 /**
  * Mapping for an entity.
  *
  * @author Gavin King
  */
 public abstract class PersistentClass implements Serializable, Filterable, MetaAttributable {
 
 	private static final Alias PK_ALIAS = new Alias(15, "PK");
 
 	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
 	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
 
 	private String entityName;
 
 	private String className;
 	private String proxyInterfaceName;
 	
 	private String nodeName;
 	private String jpaEntityName;
 
 	private String discriminatorValue;
 	private boolean lazy;
 	private ArrayList properties = new ArrayList();
 	private ArrayList declaredProperties = new ArrayList();
 	private final ArrayList subclasses = new ArrayList();
 	private final ArrayList subclassProperties = new ArrayList();
 	private final ArrayList subclassTables = new ArrayList();
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private int batchSize=-1;
 	private boolean selectBeforeUpdate;
 	private java.util.Map metaAttributes;
 	private ArrayList joins = new ArrayList();
 	private final ArrayList subclassJoins = new ArrayList();
 	private final java.util.List filters = new ArrayList();
 	protected final java.util.Set synchronizedTables = new HashSet();
 	private String loaderName;
 	private Boolean isAbstract;
 	private boolean hasSubselectLoadableCollections;
 	private Component identifierMapper;
 
 	// Custom SQL
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 
 	private String temporaryIdTableName;
 	private String temporaryIdTableDDL;
 
 	private java.util.Map tuplizerImpls;
 
-	protected int optimisticLockMode;
 	private MappedSuperclass superMappedSuperclass;
 	private Component declaredIdentifierMapper;
+	private OptimisticLockStyle optimisticLockStyle;
 
 	public String getClassName() {
 		return className;
 	}
 
 	public void setClassName(String className) {
 		this.className = className==null ? null : className.intern();
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public Class getMappedClass() throws MappingException {
 		if (className==null) return null;
 		try {
 			return ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("entity class not found: " + className, cnfe);
 		}
 	}
 
 	public Class getProxyInterface() {
 		if (proxyInterfaceName==null) return null;
 		try {
 			return ReflectHelper.classForName( proxyInterfaceName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("proxy class not found: " + proxyInterfaceName, cnfe);
 		}
 	}
 	public boolean useDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	abstract int nextSubclassId();
 	public abstract int getSubclassId();
 	
 	public boolean useDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public void addSubclass(Subclass subclass) throws MappingException {
 		// inheritance cycle detection (paranoid check)
 		PersistentClass superclass = getSuperclass();
 		while (superclass!=null) {
 			if( subclass.getEntityName().equals( superclass.getEntityName() ) ) {
 				throw new MappingException(
 					"Circular inheritance mapping detected: " +
 					subclass.getEntityName() +
 					" will have it self as superclass when extending " +
 					getEntityName()
 				);
 			}
 			superclass = superclass.getSuperclass();
 		}
 		subclasses.add(subclass);
 	}
 
 	public boolean hasSubclasses() {
 		return subclasses.size() > 0;
 	}
 
 	public int getSubclassSpan() {
 		int n = subclasses.size();
 		Iterator iter = subclasses.iterator();
 		while ( iter.hasNext() ) {
 			n += ( (Subclass) iter.next() ).getSubclassSpan();
 		}
 		return n;
 	}
 	/**
 	 * Iterate over subclasses in a special 'order', most derived subclasses
 	 * first.
 	 */
 	public Iterator getSubclassIterator() {
 		Iterator[] iters = new Iterator[ subclasses.size() + 1 ];
 		Iterator iter = subclasses.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Subclass) iter.next() ).getSubclassIterator();
 		}
 		iters[i] = subclasses.iterator();
 		return new JoinedIterator(iters);
 	}
 
 	public Iterator getSubclassClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( new SingletonIterator(this) );
 		Iterator iter = getSubclassIterator();
 		while ( iter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass)  iter.next();
 			iters.add( clazz.getSubclassClosureIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	
 	public Table getIdentityTable() {
 		return getRootTable();
 	}
 	
 	public Iterator getDirectSubclasses() {
 		return subclasses.iterator();
 	}
 
 	public void addProperty(Property p) {
 		properties.add(p);
 		declaredProperties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public abstract Table getTable();
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public abstract boolean isMutable();
 	public abstract boolean hasIdentifierProperty();
 	public abstract Property getIdentifierProperty();
 	public abstract Property getDeclaredIdentifierProperty();
 	public abstract KeyValue getIdentifier();
 	public abstract Property getVersion();
 	public abstract Property getDeclaredVersion();
 	public abstract Value getDiscriminator();
 	public abstract boolean isInherited();
 	public abstract boolean isPolymorphic();
 	public abstract boolean isVersioned();
 	public abstract String getNaturalIdCacheRegionName();
 	public abstract String getCacheConcurrencyStrategy();
 	public abstract PersistentClass getSuperclass();
 	public abstract boolean isExplicitPolymorphism();
 	public abstract boolean isDiscriminatorInsertable();
 
 	public abstract Iterator getPropertyClosureIterator();
 	public abstract Iterator getTableClosureIterator();
 	public abstract Iterator getKeyClosureIterator();
 
 	protected void addSubclassProperty(Property prop) {
 		subclassProperties.add(prop);
 	}
 	protected void addSubclassJoin(Join join) {
 		subclassJoins.add(join);
 	}
 	protected void addSubclassTable(Table subclassTable) {
 		subclassTables.add(subclassTable);
 	}
 	public Iterator getSubclassPropertyClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( getPropertyClosureIterator() );
 		iters.add( subclassProperties.iterator() );
 		for ( int i=0; i<subclassJoins.size(); i++ ) {
 			Join join = (Join) subclassJoins.get(i);
 			iters.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	public Iterator getSubclassJoinClosureIterator() {
 		return new JoinedIterator( getJoinClosureIterator(), subclassJoins.iterator() );
 	}
 	public Iterator getSubclassTableClosureIterator() {
 		return new JoinedIterator( getTableClosureIterator(), subclassTables.iterator() );
 	}
 
 	public boolean isClassOrSuperclassJoin(Join join) {
 		return joins.contains(join);
 	}
 
 	public boolean isClassOrSuperclassTable(Table closureTable) {
 		return getTable()==closureTable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public abstract boolean hasEmbeddedIdentifier();
 	public abstract Class getEntityPersisterClass();
 	public abstract void setEntityPersisterClass(Class classPersisterClass);
 	public abstract Table getRootTable();
 	public abstract RootClass getRootClass();
 	public abstract KeyValue getKey();
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setEntityName(String entityName) {
 		this.entityName = entityName==null ? null : entityName.intern();
 	}
 
 	public void createPrimaryKey() {
 		//Primary key constraint
 		PrimaryKey pk = new PrimaryKey();
 		Table table = getTable();
 		pk.setTable(table);
 		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
 		table.setPrimaryKey(pk);
 
 		pk.addColumns( getKey().getColumnIterator() );
 	}
 
 	public abstract String getWhere();
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean hasSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	/**
 	 * Build an iterator of properties which are "referenceable".
 	 *
 	 * @see #getReferencedProperty for a discussion of "referenceable"
 	 * @return The property iterator.
 	 */
 	public Iterator getReferenceablePropertyIterator() {
 		return getPropertyClosureIterator();
 	}
 
 	/**
 	 * Given a property path, locate the appropriate referenceable property reference.
 	 * <p/>
 	 * A referenceable property is a property  which can be a target of a foreign-key
 	 * mapping (an identifier or explcitly named in a property-ref).
 	 *
 	 * @param propertyPath The property path to resolve into a property reference.
 	 * @return The property reference (never null).
 	 * @throws MappingException If the property could not be found.
 	 */
 	public Property getReferencedProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getReferenceablePropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property-ref [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	public Property getRecursiveProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getPropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	private Property getRecursiveProperty(String propertyPath, Iterator iter) throws MappingException {
 		Property property = null;
 		StringTokenizer st = new StringTokenizer( propertyPath, ".", false );
 		try {
 			while ( st.hasMoreElements() ) {
 				final String element = ( String ) st.nextElement();
 				if ( property == null ) {
 					Property identifierProperty = getIdentifierProperty();
 					if ( identifierProperty != null && identifierProperty.getName().equals( element ) ) {
 						// we have a mapped identifier property and the root of
 						// the incoming property path matched that identifier
 						// property
 						property = identifierProperty;
 					}
 					else if ( identifierProperty == null && getIdentifierMapper() != null ) {
 						// we have an embedded composite identifier
 						try {
 							identifierProperty = getProperty( element, getIdentifierMapper().getPropertyIterator() );
 							if ( identifierProperty != null ) {
 								// the root of the incoming property path matched one
 								// of the embedded composite identifier properties
 								property = identifierProperty;
 							}
 						}
 						catch( MappingException ignore ) {
 							// ignore it...
 						}
 					}
 
 					if ( property == null ) {
 						property = getProperty( element, iter );
 					}
 				}
 				else {
 					//flat recursive algorithm
 					property = ( ( Component ) property.getValue() ).getProperty( element );
 				}
 			}
 		}
 		catch ( MappingException e ) {
 			throw new MappingException( "property [" + propertyPath + "] not found on entity [" + getEntityName() + "]" );
 		}
 
 		return property;
 	}
 
 	private Property getProperty(String propertyName, Iterator iterator) throws MappingException {
 		while ( iterator.hasNext() ) {
 			Property prop = (Property) iterator.next();
 			if ( prop.getName().equals( StringHelper.root(propertyName) ) ) {
 				return prop;
 			}
 		}
 		throw new MappingException( "property [" + propertyName + "] not found on entity [" + getEntityName() + "]" );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyClosureIterator();
 		Property identifierProperty = getIdentifierProperty();
 		if ( identifierProperty != null
 				&& identifierProperty.getName().equals( StringHelper.root(propertyName) )
 				) {
 			return identifierProperty;
 		}
 		else {
 			return getProperty( propertyName, iter );
 		}
 	}
 
-	abstract public int getOptimisticLockMode();
+	@Deprecated
+	public int getOptimisticLockMode() {
+		return getOptimisticLockStyle().getOldCode();
+	}
 
+	@Deprecated
 	public void setOptimisticLockMode(int optimisticLockMode) {
-		this.optimisticLockMode = optimisticLockMode;
+		setOptimisticLockStyle( OptimisticLockStyle.interpretOldCode( optimisticLockMode ) );
+	}
+
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return optimisticLockStyle;
+	}
+
+	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
+		this.optimisticLockStyle = optimisticLockStyle;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !prop.isValid(mapping) ) {
 				throw new MappingException(
 						"property mapping has wrong number of columns: " +
 						StringHelper.qualify( getEntityName(), prop.getName() ) +
 						" type: " +
 						prop.getType().getName()
 					);
 			}
 		}
 		checkPropertyDuplication();
 		checkColumnDuplication();
 	}
 	
 	private void checkPropertyDuplication() throws MappingException {
 		HashSet names = new HashSet();
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !names.add( prop.getName() ) ) {
 				throw new MappingException( "Duplicate property mapping of " + prop.getName() + " found in " + getEntityName());
 			}
 		}
 	}
 
 	public boolean isDiscriminatorValueNotNull() {
 		return NOT_NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 	public boolean isDiscriminatorValueNull() {
 		return NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	public MetaAttribute getMetaAttribute(String name) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(name);
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityName() + ')';
 	}
 	
 	public Iterator getJoinIterator() {
 		return joins.iterator();
 	}
 
 	public Iterator getJoinClosureIterator() {
 		return joins.iterator();
 	}
 
 	public void addJoin(Join join) {
 		joins.add(join);
 		join.setPersistentClass(this);
 	}
 
 	public int getJoinClosureSpan() {
 		return joins.size();
 	}
 
 	public int getPropertyClosureSpan() {
 		int span = properties.size();
 		for ( int i=0; i<joins.size(); i++ ) {
 			Join join = (Join) joins.get(i);
 			span += join.getPropertySpan();
 		}
 		return span;
 	}
 
 	public int getJoinNumber(Property prop) {
 		int result=1;
 		Iterator iter = getSubclassJoinClosureIterator();
 		while ( iter.hasNext() ) {
 			Join join = (Join) iter.next();
 			if ( join.containsProperty(prop) ) return result;
 			result++;
 		}
 		return 0;
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class.  The returned
 	 * iterator only accounts for "normal" properties (i.e. non-identifier
 	 * properties).
 	 * <p/>
 	 * Differs from {@link #getUnjoinedPropertyIterator} in that the iterator
 	 * we return here will include properties defined as part of a join.
 	 *
 	 * @return An iterator over the "normal" properties.
 	 */
 	public Iterator getPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( properties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class <b>which
 	 * are not defined as part of a join</b>.  As with {@link #getPropertyIterator},
 	 * the returned iterator only accounts for non-identifier properties.
 	 *
 	 * @return An iterator over the non-joined "normal" properties.
 	 */
 	public Iterator getUnjoinedPropertyIterator() {
 		return properties.iterator();
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
 
 	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap,  this));
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public boolean isForceDiscriminator() {
 		return false;
 	}
 
 	public abstract boolean isJoinedSubclass();
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String loaderName) {
 		this.loaderName = loaderName==null ? null : loaderName.intern();
 	}
 
 	public abstract java.util.Set getSynchronizedTables();
 	
 	public void addSynchronizedTable(String table) {
 		synchronizedTables.add(table);
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	protected void checkColumnDuplication(Set distinctColumns, Iterator columns) 
 	throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable columnOrFormula = (Selectable) columns.next();
 			if ( !columnOrFormula.isFormula() ) {
 				Column col = (Column) columnOrFormula;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( 
 							"Repeated column in mapping for entity: " +
 							getEntityName() +
 							" column: " +
 							col.getName() + 
 							" (should be mapped with insert=\"false\" update=\"false\")"
 						);
 				}
 			}
 		}
 	}
 	
 	protected void checkPropertyColumnDuplication(Set distinctColumns, Iterator properties) 
 	throws MappingException {
 		while ( properties.hasNext() ) {
 			Property prop = (Property) properties.next();
 			if ( prop.getValue() instanceof Component ) { //TODO: remove use of instanceof!
 				Component component = (Component) prop.getValue();
 				checkPropertyColumnDuplication( distinctColumns, component.getPropertyIterator() );
 			}
 			else {
 				if ( prop.isUpdateable() || prop.isInsertable() ) {
 					checkColumnDuplication( distinctColumns, prop.getColumnIterator() );
 				}
 			}
 		}
 	}
 	
 	protected Iterator getNonDuplicatedPropertyIterator() {
 		return getUnjoinedPropertyIterator();
 	}
 	
 	protected Iterator getDiscriminatorColumnIterator() {
 		return EmptyIterator.INSTANCE;
 	}
 	
 	protected void checkColumnDuplication() {
 		HashSet cols = new HashSet();
 		if (getIdentifierMapper() == null ) {
 			//an identifier mapper => getKey will be included in the getNonDuplicatedPropertyIterator()
 			//and checked later, so it needs to be excluded
 			checkColumnDuplication( cols, getKey().getColumnIterator() );
 		}
 		checkColumnDuplication( cols, getDiscriminatorColumnIterator() );
 		checkPropertyColumnDuplication( cols, getNonDuplicatedPropertyIterator() );
 		Iterator iter = getJoinIterator();
 		while ( iter.hasNext() ) {
 			cols.clear();
 			Join join = (Join) iter.next();
 			checkColumnDuplication( cols, join.getKey().getColumnIterator() );
 			checkPropertyColumnDuplication( cols, join.getPropertyIterator() );
 		}
 	}
 	
 	public abstract Object accept(PersistentClassVisitor mv);
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 	
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 	
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return getClassName()!=null;
 	}
 
 	public boolean hasDom4jRepresentation() {
 		return getNodeName()!=null;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 	
 	public void setSubselectLoadableCollections(boolean hasSubselectCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectCollections;
 	}
 
 	public void prepareTemporaryTables(Mapping mapping, Dialect dialect) {
 		temporaryIdTableName = dialect.generateTemporaryTableName( getTable().getName() );
 		if ( dialect.supportsTemporaryTables() ) {
 			Table table = new Table();
 			table.setName( temporaryIdTableName );
 			Iterator itr = getTable().getPrimaryKey().getColumnIterator();
 			while( itr.hasNext() ) {
 				Column column = (Column) itr.next();
 				table.addColumn( column.clone()  );
 			}
 			temporaryIdTableDDL = table.sqlTemporaryTableCreateString( dialect, mapping );
 		}
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	public Component getIdentifierMapper() {
 		return identifierMapper;
 	}
 
 	public Component getDeclaredIdentifierMapper() {
 		return declaredIdentifierMapper;
 	}
 
 	public void setDeclaredIdentifierMapper(Component declaredIdentifierMapper) {
 		this.declaredIdentifierMapper = declaredIdentifierMapper;
 	}
 
 	public boolean hasIdentifierMapper() {
 		return identifierMapper != null;
 	}
 
 	public void setIdentifierMapper(Component handle) {
 		this.identifierMapper = handle;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
 			tuplizerImpls = new HashMap();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		if ( tuplizerImpls == null ) return null;
 		return ( String ) tuplizerImpls.get( mode );
 	}
 
 	public java.util.Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public boolean hasNaturalId() {
 		Iterator props = getRootClass().getPropertyIterator();
 		while ( props.hasNext() ) {
 			if ( ( (Property) props.next() ).isNaturalIdentifier() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public abstract boolean isLazyPropertiesCacheable();
 
 	// The following methods are added to support @MappedSuperclass in the metamodel
 	public Iterator getDeclaredPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( declaredProperties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getDeclaredPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	public void addMappedsuperclassProperty(Property p) {
 		properties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public MappedSuperclass getSuperMappedSuperclass() {
 		return superMappedSuperclass;
 	}
 
 	public void setSuperMappedSuperclass(MappedSuperclass superMappedSuperclass) {
 		this.superMappedSuperclass = superMappedSuperclass;
 	}
 
 	// End of @Mappedsuperclass support
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
index 59c5775cad..774aa95e5a 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
@@ -1,360 +1,355 @@
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
 
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.SingletonIterator;
 
 /**
  * The root class of an inheritance hierarchy
  * @author Gavin King
  */
 public class RootClass extends PersistentClass implements TableOwner {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, RootClass.class.getName());
 
 	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 
 	private Property identifierProperty; //may be final
 	private KeyValue identifier; //may be final
 	private Property version; //may be final
 	private boolean polymorphic;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String naturalIdCacheRegionName;
 	private boolean lazyPropertiesCacheable = true;
 	private Value discriminator; //may be final
 	private boolean mutable = true;
 	private boolean embeddedIdentifier = false; // may be final
 	private boolean explicitPolymorphism;
 	private Class entityPersisterClass;
 	private boolean forceDiscriminator = false;
 	private String where;
 	private Table table;
 	private boolean discriminatorInsertable = true;
 	private int nextSubclassId = 0;
 	private Property declaredIdentifierProperty;
 	private Property declaredVersion;
 
 	@Override
     int nextSubclassId() {
 		return ++nextSubclassId;
 	}
 
 	@Override
     public int getSubclassId() {
 		return 0;
 	}
 
 	public void setTable(Table table) {
 		this.table=table;
 	}
 	@Override
     public Table getTable() {
 		return table;
 	}
 
 	@Override
     public Property getIdentifierProperty() {
 		return identifierProperty;
 	}
 
 	@Override
     public Property getDeclaredIdentifierProperty() {
 		return declaredIdentifierProperty;
 	}
 
 	public void setDeclaredIdentifierProperty(Property declaredIdentifierProperty) {
 		this.declaredIdentifierProperty = declaredIdentifierProperty;
 	}
 
 	@Override
     public KeyValue getIdentifier() {
 		return identifier;
 	}
 	@Override
     public boolean hasIdentifierProperty() {
 		return identifierProperty!=null;
 	}
 
 	@Override
     public Value getDiscriminator() {
 		return discriminator;
 	}
 
 	@Override
     public boolean isInherited() {
 		return false;
 	}
 	@Override
     public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public void setPolymorphic(boolean polymorphic) {
 		this.polymorphic = polymorphic;
 	}
 
 	@Override
     public RootClass getRootClass() {
 		return this;
 	}
 
 	@Override
     public Iterator getPropertyClosureIterator() {
 		return getPropertyIterator();
 	}
 	@Override
     public Iterator getTableClosureIterator() {
 		return new SingletonIterator( getTable() );
 	}
 	@Override
     public Iterator getKeyClosureIterator() {
 		return new SingletonIterator( getKey() );
 	}
 
 	@Override
     public void addSubclass(Subclass subclass) throws MappingException {
 		super.addSubclass(subclass);
 		setPolymorphic(true);
 	}
 
 	@Override
     public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
     public Property getVersion() {
 		return version;
 	}
 
 	@Override
     public Property getDeclaredVersion() {
 		return declaredVersion;
 	}
 
 	public void setDeclaredVersion(Property declaredVersion) {
 		this.declaredVersion = declaredVersion;
 	}
 
 	public void setVersion(Property version) {
 		this.version = version;
 	}
 	@Override
     public boolean isVersioned() {
 		return version!=null;
 	}
 
 	@Override
     public boolean isMutable() {
 		return mutable;
 	}
 	@Override
     public boolean hasEmbeddedIdentifier() {
 		return embeddedIdentifier;
 	}
 
 	@Override
     public Class getEntityPersisterClass() {
 		return entityPersisterClass;
 	}
 
 	@Override
     public Table getRootTable() {
 		return getTable();
 	}
 
 	@Override
     public void setEntityPersisterClass(Class persister) {
 		this.entityPersisterClass = persister;
 	}
 
 	@Override
     public PersistentClass getSuperclass() {
 		return null;
 	}
 
 	@Override
     public KeyValue getKey() {
 		return getIdentifier();
 	}
 
 	public void setDiscriminator(Value discriminator) {
 		this.discriminator = discriminator;
 	}
 
 	public void setEmbeddedIdentifier(boolean embeddedIdentifier) {
 		this.embeddedIdentifier = embeddedIdentifier;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public void setIdentifier(KeyValue identifier) {
 		this.identifier = identifier;
 	}
 
 	public void setIdentifierProperty(Property identifierProperty) {
 		this.identifierProperty = identifierProperty;
 		identifierProperty.setPersistentClass(this);
 
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	@Override
     public boolean isDiscriminatorInsertable() {
 		return discriminatorInsertable;
 	}
 
 	public void setDiscriminatorInsertable(boolean insertable) {
 		this.discriminatorInsertable = insertable;
 	}
 
 	@Override
     public boolean isForceDiscriminator() {
 		return forceDiscriminator;
 	}
 
 	public void setForceDiscriminator(boolean forceDiscriminator) {
 		this.forceDiscriminator = forceDiscriminator;
 	}
 
 	@Override
     public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String string) {
 		where = string;
 	}
 
 	@Override
     public void validate(Mapping mapping) throws MappingException {
 		super.validate(mapping);
 		if ( !getIdentifier().isValid(mapping) ) {
 			throw new MappingException(
 				"identifier mapping has wrong number of columns: " +
 				getEntityName() +
 				" type: " +
 				getIdentifier().getType().getName()
 			);
 		}
 		checkCompositeIdentifier();
 	}
 
 	private void checkCompositeIdentifier() {
 		if ( getIdentifier() instanceof Component ) {
 			Component id = (Component) getIdentifier();
 			if ( !id.isDynamic() ) {
 				final Class idClass = id.getComponentClass();
 				final String idComponendClassName = idClass.getName();
                 if (idClass != null && !ReflectHelper.overridesEquals(idClass)) LOG.compositeIdClassDoesNotOverrideEquals( idComponendClassName );
                 if (!ReflectHelper.overridesHashCode(idClass)) LOG.compositeIdClassDoesNotOverrideHashCode( idComponendClassName );
                 if (!Serializable.class.isAssignableFrom(idClass)) throw new MappingException(
                                                                                               "Composite-id class must implement Serializable: "
                                                                                               + idComponendClassName);
 			}
 		}
 	}
 
 	@Override
     public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName==null ? getEntityName() : cacheRegionName;
 	}
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
 	}
 	
 	@Override
 	public String getNaturalIdCacheRegionName() {
 		return naturalIdCacheRegionName;
 	}
 	public void setNaturalIdCacheRegionName(String naturalIdCacheRegionName) {
 		this.naturalIdCacheRegionName = naturalIdCacheRegionName;
 	}
 	
 	@Override
     public boolean isLazyPropertiesCacheable() {
 		return lazyPropertiesCacheable;
 	}
 
 	public void setLazyPropertiesCacheable(boolean lazyPropertiesCacheable) {
 		this.lazyPropertiesCacheable = lazyPropertiesCacheable;
 	}
 
 	@Override
     public boolean isJoinedSubclass() {
 		return false;
 	}
 
 	@Override
     public java.util.Set getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public Set getIdentityTables() {
 		Set tables = new HashSet();
 		Iterator iter = getSubclassClosureIterator();
 		while ( iter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) iter.next();
 			if ( clazz.isAbstract() == null || !clazz.isAbstract().booleanValue() ) tables.add( clazz.getIdentityTable() );
 		}
 		return tables;
 	}
 
 	@Override
     public Object accept(PersistentClassVisitor mv) {
 		return mv.accept(this);
 	}
-
-	@Override
-    public int getOptimisticLockMode() {
-		return optimisticLockMode;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
index c45d4cee16..1133d500bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
@@ -1,303 +1,304 @@
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
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 
 /**
  * A sublass in a table-per-class-hierarchy mapping
  * @author Gavin King
  */
 public class Subclass extends PersistentClass {
 
 	private PersistentClass superclass;
 	private Class classPersisterClass;
 	private final int subclassId;
 	
 	public Subclass(PersistentClass superclass) {
 		this.superclass = superclass;
 		this.subclassId = superclass.nextSubclassId();
 	}
 
 	int nextSubclassId() {
 		return getSuperclass().nextSubclassId();
 	}
 	
 	public int getSubclassId() {
 		return subclassId;
 	}
 	
 	@Override
 	public String getNaturalIdCacheRegionName() {
 		return getSuperclass().getNaturalIdCacheRegionName();
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return getSuperclass().getCacheConcurrencyStrategy();
 	}
 
 	public RootClass getRootClass() {
 		return getSuperclass().getRootClass();
 	}
 
 	public PersistentClass getSuperclass() {
 		return superclass;
 	}
 
 	public Property getIdentifierProperty() {
 		return getSuperclass().getIdentifierProperty();
 	}
 
 	public Property getDeclaredIdentifierProperty() {
 		return null;
 	}
 
 	public KeyValue getIdentifier() {
 		return getSuperclass().getIdentifier();
 	}
 	public boolean hasIdentifierProperty() {
 		return getSuperclass().hasIdentifierProperty();
 	}
 	public Value getDiscriminator() {
 		return getSuperclass().getDiscriminator();
 	}
 	public boolean isMutable() {
 		return getSuperclass().isMutable();
 	}
 	public boolean isInherited() {
 		return true;
 	}
 	public boolean isPolymorphic() {
 		return true;
 	}
 
 	public void addProperty(Property p) {
 		super.addProperty(p);
 		getSuperclass().addSubclassProperty(p);
 	}
 
 	public void addMappedsuperclassProperty(Property p) {
 		super.addMappedsuperclassProperty( p );
 		getSuperclass().addSubclassProperty(p);
 	}
 
 	public void addJoin(Join j) {
 		super.addJoin(j);
 		getSuperclass().addSubclassJoin(j);
 	}
 
 	public Iterator getPropertyClosureIterator() {
 		return new JoinedIterator(
 				getSuperclass().getPropertyClosureIterator(),
 				getPropertyIterator()
 			);
 	}
 	public Iterator getTableClosureIterator() {
 		return new JoinedIterator(
 				getSuperclass().getTableClosureIterator(),
 				new SingletonIterator( getTable() )
 			);
 	}
 	public Iterator getKeyClosureIterator() {
 		return new JoinedIterator(
 				getSuperclass().getKeyClosureIterator(),
 				new SingletonIterator( getKey() )
 			);
 	}
 	protected void addSubclassProperty(Property p) {
 		super.addSubclassProperty(p);
 		getSuperclass().addSubclassProperty(p);
 	}
 	protected void addSubclassJoin(Join j) {
 		super.addSubclassJoin(j);
 		getSuperclass().addSubclassJoin(j);
 	}
 
 	protected void addSubclassTable(Table table) {
 		super.addSubclassTable(table);
 		getSuperclass().addSubclassTable(table);
 	}
 
 	public boolean isVersioned() {
 		return getSuperclass().isVersioned();
 	}
 	public Property getVersion() {
 		return getSuperclass().getVersion();
 	}
 
 	public Property getDeclaredVersion() {
 		return null;
 	}
 
 	public boolean hasEmbeddedIdentifier() {
 		return getSuperclass().hasEmbeddedIdentifier();
 	}
 	public Class getEntityPersisterClass() {
 		if (classPersisterClass==null) {
 			return getSuperclass().getEntityPersisterClass();
 		}
 		else {
 			return classPersisterClass;
 		}
 	}
 
 	public Table getRootTable() {
 		return getSuperclass().getRootTable();
 	}
 
 	public KeyValue getKey() {
 		return getSuperclass().getIdentifier();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return getSuperclass().isExplicitPolymorphism();
 	}
 
 	public void setSuperclass(PersistentClass superclass) {
 		this.superclass = superclass;
 	}
 
 	public String getWhere() {
 		return getSuperclass().getWhere();
 	}
 
 	public boolean isJoinedSubclass() {
 		return getTable()!=getRootTable();
 	}
 
 	public void createForeignKey() {
 		if ( !isJoinedSubclass() ) {
 			throw new AssertionFailure( "not a joined-subclass" );
 		}
 		getKey().createForeignKeyOfEntity( getSuperclass().getEntityName() );
 	}
 
 	public void setEntityPersisterClass(Class classPersisterClass) {
 		this.classPersisterClass = classPersisterClass;
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return getSuperclass().isLazyPropertiesCacheable();
 	}
 
 	public int getJoinClosureSpan() {
 		return getSuperclass().getJoinClosureSpan() + super.getJoinClosureSpan();
 	}
 
 	public int getPropertyClosureSpan() {
 		return getSuperclass().getPropertyClosureSpan() + super.getPropertyClosureSpan();
 	}
 
 	public Iterator getJoinClosureIterator() {
 		return new JoinedIterator(
 			getSuperclass().getJoinClosureIterator(),
 			super.getJoinClosureIterator()
 		);
 	}
 
 	public boolean isClassOrSuperclassJoin(Join join) {
 		return super.isClassOrSuperclassJoin(join) || getSuperclass().isClassOrSuperclassJoin(join);
 	}
 
 	public boolean isClassOrSuperclassTable(Table table) {
 		return super.isClassOrSuperclassTable(table) || getSuperclass().isClassOrSuperclassTable(table);
 	}
 
 	public Table getTable() {
 		return getSuperclass().getTable();
 	}
 
 	public boolean isForceDiscriminator() {
 		return getSuperclass().isForceDiscriminator();
 	}
 
 	public boolean isDiscriminatorInsertable() {
 		return getSuperclass().isDiscriminatorInsertable();
 	}
 
 	public java.util.Set getSynchronizedTables() {
 		HashSet result = new HashSet();
 		result.addAll(synchronizedTables);
 		result.addAll( getSuperclass().getSynchronizedTables() );
 		return result;
 	}
 
 	public Object accept(PersistentClassVisitor mv) {
 		return mv.accept(this);
 	}
 
 	public java.util.List getFilters() {
 		java.util.List filters = new ArrayList(super.getFilters());
 		filters.addAll(getSuperclass().getFilters());
 		return filters;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return super.hasSubselectLoadableCollections() || 
 			getSuperclass().hasSubselectLoadableCollections();
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		String impl = super.getTuplizerImplClassName( mode );
 		if ( impl == null ) {
 			impl = getSuperclass().getTuplizerImplClassName( mode );
 		}
 		return impl;
 	}
 
 	public Map getTuplizerMap() {
 		Map specificTuplizerDefs = super.getTuplizerMap();
 		Map superclassTuplizerDefs = getSuperclass().getTuplizerMap();
 		if ( specificTuplizerDefs == null && superclassTuplizerDefs == null ) {
 			return null;
 		}
 		else {
 			Map combined = new HashMap();
 			if ( superclassTuplizerDefs != null ) {
 				combined.putAll( superclassTuplizerDefs );
 			}
 			if ( specificTuplizerDefs != null ) {
 				combined.putAll( specificTuplizerDefs );
 			}
 			return java.util.Collections.unmodifiableMap( combined );
 		}
 	}
 
 	public Component getIdentifierMapper() {
 		return superclass.getIdentifierMapper();
 	}
-	
-	public int getOptimisticLockMode() {
-		return superclass.getOptimisticLockMode();
-	}
 
+	@Override
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return superclass.getOptimisticLockStyle();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 7d0b2717fe..16e5701076 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,987 +1,970 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final AbstractEntityPersister persister;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierAttribute;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final NonIdentifierAttribute[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 	private final boolean hasCacheableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final OptimisticLockStyle optimisticLockStyle;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityInstrumentationMetadata instrumentationMetadata;
 
 	public EntityMetamodel(
 			PersistentClass persistentClass,
 			AbstractEntityPersister persister,
 			SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.persister = persister;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierAttribute = PropertyFactory.buildIdentifierAttribute(
 				persistentClass,
 				sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = persistentClass.isVersioned();
 
 		instrumentationMetadata = persistentClass.hasPojoRepresentation()
 				? Environment.getBytecodeProvider().getEntityInstrumentationMetadata( persistentClass.getMappedClass() )
 				: new NonPojoInstrumentationMetadata( persistentClass.getEntityName() );
 
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new NonIdentifierAttribute[propertySpan];
 		List<Integer> naturalIdNumbers = new ArrayList<Integer>();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
 						persister,
 						sessionFactory,
 						i,
 						prop,
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildEntityBasedAttribute(
 						persister,
 						sessionFactory,
 						i,
 						prop,
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
-		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
+		optimisticLockStyle = persistentClass.getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
-	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
-		switch ( optimisticLockMode ) {
-			case Versioning.OPTIMISTIC_LOCK_NONE: {
-				return OptimisticLockStyle.NONE;
-			}
-			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
-				return OptimisticLockStyle.DIRTY;
-			}
-			case Versioning.OPTIMISTIC_LOCK_ALL: {
-				return OptimisticLockStyle.ALL;
-			}
-			default: {
-				return OptimisticLockStyle.VERSION;
-			}
-		}
-	}
-
 	public EntityMetamodel(
 			EntityBinding entityBinding,
 			AbstractEntityPersister persister,
 			SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.persister = persister;
 
 		name = entityBinding.getEntity().getName();
 
 		rootName = entityBinding.getHierarchyDetails().getRootEntityBinding().getEntity().getName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierAttribute = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		if ( entityBinding.getEntity().getClassReferenceUnresolved() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getValue();
 		}
 		instrumentationMetadata = Environment.getBytecodeProvider().getEntityInstrumentationMetadata( mappedClass );
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		BasicAttributeBinding rootEntityIdentifier = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
 		properties = new NonIdentifierAttribute[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getVersioningAttributeBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
 						persister,
 						entityBinding.getHierarchyDetails().getVersioningAttributeBinding(),
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, instrumentationMetadata.isInstrumented() );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = false; //See previous TODO and HHH-6354
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		hasSubclasses = entityBinding.hasSubEntityBindings();
 		polymorphic = entityBinding.isPolymorphic();
 
 		explicitPolymorphism = entityBinding.getHierarchyDetails().isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockStyle = entityBinding.getHierarchyDetails().getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			subclassEntityNames.add( subEntityBinding.getEntity().getName() );
 			if ( subEntityBinding.getEntity().getClassReference() != null ) {
 				entityNameByInheritenceClassMap.put(
 						subEntityBinding.getEntity().getClassReference(),
 						subEntityBinding.getEntity().getName() );
 			}
 		}
 		subclassEntityNames.add( name );
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().isComponent() ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.attributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 	
 	public boolean isNaturalIdentifierCached() {
 		return hasNaturalIdentifier() && hasCacheableNaturalId;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierAttribute;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public NonIdentifierAttribute[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index;
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	/**
 	 * Whether or not this class can be lazy (ie intercepted)
 	 */
 	public boolean isInstrumented() {
 		return instrumentationMetadata.isInstrumented();
 	}
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return instrumentationMetadata;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/NewCustomEntityMappingAnnotationsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/NewCustomEntityMappingAnnotationsTest.java
index 64d6c831e4..5e81af1c68 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/NewCustomEntityMappingAnnotationsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/NewCustomEntityMappingAnnotationsTest.java
@@ -1,58 +1,58 @@
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
 package org.hibernate.test.annotations.entity;
 
 import org.hibernate.mapping.RootClass;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 public class NewCustomEntityMappingAnnotationsTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Forest.class, Forest2.class };
 	}
 
 	@Override
 	protected String[] getAnnotatedPackages() {
 		return new String[] { Forest.class.getPackage().getName() };
 	}
 
 	@Test
 	public void testSameMappingValues() {
 		RootClass forest = (RootClass) configuration().getClassMapping( Forest.class.getName() );
 		RootClass forest2 = (RootClass) configuration().getClassMapping( Forest2.class.getName() );
 		assertEquals( forest.useDynamicInsert(), forest2.useDynamicInsert() );
 		assertEquals( forest.useDynamicUpdate(), forest2.useDynamicUpdate() );
 		assertEquals( forest.hasSelectBeforeUpdate(), forest2.hasSelectBeforeUpdate() );
-		assertEquals( forest.getOptimisticLockMode(), forest2.getOptimisticLockMode() );
+		assertEquals( forest.getOptimisticLockStyle(), forest2.getOptimisticLockStyle() );
 		assertEquals( forest.isExplicitPolymorphism(), forest2.isExplicitPolymorphism() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index a4b56b0193..64553579b4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,692 +1,692 @@
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
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
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
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
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
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
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
-				);
+			);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
-				);
+			);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
-					new PreLoadEvent( (EventSource) session ),
-					new PostLoadEvent( (EventSource) session )
-				);
+					new PreLoadEvent( (EventSource) session )
+			);
+			TwoPhaseLoad.postLoad( clone, session, new PostLoadEvent( (EventSource) session ) );
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
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
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
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return UnstructuredCacheEntry.INSTANCE;
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
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
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
 }
