diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index d13c3d17bf..bbea1b6e98 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1714 +1,1715 @@
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
+		javax.persistence.Table tabAnn = null;
 		if ( clazzToProcess.isAnnotationPresent( javax.persistence.Table.class ) ) {
-			javax.persistence.Table tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
+			tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
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
-
+		entityBinder.processComplementaryTableDefinitions( tabAnn );
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
 				}
 				else {
 					//if @IndexColumn is not there, the generated IndexColumn is an implicit column and not used.
 					//so we can leave the legacy processing as the default
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( org.hibernate.annotations.IndexColumn.class ),
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
index d45c6f11d0..4a74fdb18b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
@@ -1,101 +1,147 @@
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
+import java.util.ArrayList;
+import java.util.List;
 import java.util.Map;
+import java.util.StringTokenizer;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.MappingException;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Table;
 
 /**
  * @author Emmanuel Bernard
  */
 public class IndexOrUniqueKeySecondPass implements SecondPass {
 	private Table table;
 	private final String indexName;
 	private final String[] columns;
+	private final String[] ordering;
 	private final Mappings mappings;
 	private final Ejb3Column column;
 	private final boolean unique;
 
 	/**
 	 * Build an index
 	 */
 	public IndexOrUniqueKeySecondPass(Table table, String indexName, String[] columns, Mappings mappings) {
 		this.table = table;
 		this.indexName = indexName;
 		this.columns = columns;
+		this.ordering = null;
 		this.mappings = mappings;
 		this.column = null;
 		this.unique = false;
 	}
+	//used for the new JPA 2.1 @Index
+	public IndexOrUniqueKeySecondPass(Table table, String indexName, String columnList, Mappings mappings, boolean unique) {
+		this.table = table;
+		StringTokenizer tokenizer = new StringTokenizer( columnList, "," );
+		List<String> tmp = new ArrayList<String>();
+		while ( tokenizer.hasMoreElements() ) {
+			tmp.add( tokenizer.nextToken().trim() );
+		}
+		this.indexName = StringHelper.isNotEmpty( indexName ) ? indexName : "IDX_" + table.uniqueColumnString( tmp.iterator() );
+		this.columns = new String[tmp.size()];
+		this.ordering = new String[tmp.size()];
+		initializeColumns(columns, ordering, tmp);
+		this.mappings = mappings;
+		this.column = null;
+		this.unique = unique;
+	}
+
+	private void initializeColumns(String[] columns, String[] ordering, List<String> list) {
+		for ( int i = 0, size = list.size(); i < size; i++ ) {
+			final String description = list.get( i );
+			final String tmp = description.toLowerCase();
+			if ( tmp.endsWith( " desc" ) ) {
+				columns[i] = description.substring( 0, description.length() - 5 );
+				ordering[i] = "desc";
+			}
+			else if ( tmp.endsWith( " asc" ) ) {
+				columns[i] = description.substring( 0, description.length() - 4 );
+				ordering[i] = "asc";
+			}
+			else {
+				columns[i] = description;
+				ordering[i] = null;
+			}
+		}
+	}
+
 
 	/**
 	 * Build an index
 	 */
 	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, Mappings mappings) {
 		this( indexName, column, mappings, false );
 	}
 
 	/**
 	 * Build an index if unique is false or a Unique Key if unique is true
 	 */
 	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, Mappings mappings, boolean unique) {
 		this.indexName = indexName;
 		this.column = column;
 		this.columns = null;
 		this.mappings = mappings;
 		this.unique = unique;
+		this.ordering = null;
 	}
-
+	@Override
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		if ( columns != null ) {
-			for (String columnName : columns) {
-				addConstraintToColumn( columnName );
+			for ( int i = 0; i < columns.length; i++ ) {
+				final String order = ordering != null ? ordering[i] : null;
+				addConstraintToColumn( columns[i], order );
 			}
 		}
 		if ( column != null ) {
 			this.table = column.getTable();
-			addConstraintToColumn( mappings.getLogicalColumnName( column.getMappingColumn().getQuotedName(), table ) );
+			addConstraintToColumn( mappings.getLogicalColumnName( column.getMappingColumn().getQuotedName(), table ), null );
 		}
 	}
 
-	private void addConstraintToColumn(String columnName) {
+	private void addConstraintToColumn(final String columnName, final String ordering) {
 		Column column = table.getColumn(
 				new Column(
 						mappings.getPhysicalColumnName( columnName, table )
 				)
 		);
 		if ( column == null ) {
 			throw new AnnotationException(
 					"@Index references a unknown column: " + columnName
 			);
 		}
 		if ( unique )
-			table.getOrCreateUniqueKey( indexName ).addColumn( column );
+			table.getOrCreateUniqueKey( indexName ).addColumn( column, ordering );
 		else
-			table.getOrCreateIndex( indexName ).addColumn( column );
+			table.getOrCreateIndex( indexName ).addColumn( column, ordering );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index eca4a64e24..a29cbda603 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1000 +1,1007 @@
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
 
-		//no check constraints available on joins
+		if ( secondaryTable != null ) {
+			TableBinder.addIndexes( table, secondaryTable.indexes(), mappings );
+		}
+
+			//no check constraints available on joins
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
-
+	public void processComplementaryTableDefinitions(javax.persistence.Table table) {
+		if ( table == null ) return;
+		TableBinder.addIndexes( persistentClass.getTable(), table.indexes(), mappings );
+	}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
index 64dfb89c30..8a80098ab3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
@@ -1,569 +1,584 @@
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
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import javax.persistence.UniqueConstraint;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Index;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexOrUniqueKeySecondPass;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 /**
  * Table related operations
  *
  * @author Emmanuel Bernard
  */
 @SuppressWarnings("unchecked")
 public class TableBinder {
 	//TODO move it to a getter/setter strategy
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableBinder.class.getName());
 
 	private String schema;
 	private String catalog;
 	private String name;
 	private boolean isAbstract;
 	private List<UniqueConstraintHolder> uniqueConstraints;
 //	private List<String[]> uniqueConstraints;
 	String constraints;
 	Table denormalizedSuperTable;
 	Mappings mappings;
 	private String ownerEntityTable;
 	private String associatedEntityTable;
 	private String propertyName;
 	private String ownerEntity;
 	private String associatedEntity;
 	private boolean isJPA2ElementCollection;
 
 	public void setSchema(String schema) {
 		this.schema = schema;
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = catalog;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setAbstract(boolean anAbstract) {
 		isAbstract = anAbstract;
 	}
 
 	public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
 		this.uniqueConstraints = TableBinder.buildUniqueConstraintHolders( uniqueConstraints );
 	}
 
 	public void setConstraints(String constraints) {
 		this.constraints = constraints;
 	}
 
 	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
 		this.denormalizedSuperTable = denormalizedSuperTable;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setJPA2ElementCollection(boolean isJPA2ElementCollection) {
 		this.isJPA2ElementCollection = isJPA2ElementCollection;
 	}
 
 	private static class AssociationTableNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private AssociationTableNameSource(String explicitName, String logicalName) {
 			this.explicitName = explicitName;
 			this.logicalName = logicalName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	// only bind association table currently
 	public Table bind() {
 		//logicalName only accurate for assoc table...
 		final String unquotedOwnerTable = StringHelper.unquote( ownerEntityTable );
 		final String unquotedAssocTable = StringHelper.unquote( associatedEntityTable );
 
 		//@ElementCollection use ownerEntity_property instead of the cleaner ownerTableName_property
 		// ownerEntity can be null when the table name is explicitly set
 		final String ownerObjectName = isJPA2ElementCollection && ownerEntity != null ?
 				StringHelper.unqualify( ownerEntity ) : unquotedOwnerTable;
 		final ObjectNameSource nameSource = buildNameContext(
 				ownerObjectName,
 				unquotedAssocTable );
 
 		final boolean ownerEntityTableQuoted = StringHelper.isQuoted( ownerEntityTable );
 		final boolean associatedEntityTableQuoted = StringHelper.isQuoted( associatedEntityTable );
 		final ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper = new ObjectNameNormalizer.NamingStrategyHelper() {
 			public String determineImplicitName(NamingStrategy strategy) {
 
 				final String strategyResult = strategy.collectionTableName(
 						ownerEntity,
 						ownerObjectName,
 						associatedEntity,
 						unquotedAssocTable,
 						propertyName
 
 				);
 				return ownerEntityTableQuoted || associatedEntityTableQuoted
 						? StringHelper.quote( strategyResult )
 						: strategyResult;
 			}
 
 			public String handleExplicitName(NamingStrategy strategy, String name) {
 				return strategy.tableName( name );
 			}
 		};
 
 		return buildAndFillTable(
 				schema,
 				catalog,
 				nameSource,
 				namingStrategyHelper,
 				isAbstract,
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperTable,
 				mappings,
 				null
 		);
 	}
 
 	private ObjectNameSource buildNameContext(String unquotedOwnerTable, String unquotedAssocTable) {
 		String logicalName = mappings.getNamingStrategy().logicalCollectionTableName(
 				name,
 				unquotedOwnerTable,
 				unquotedAssocTable,
 				propertyName
 		);
 		if ( StringHelper.isQuoted( ownerEntityTable ) || StringHelper.isQuoted( associatedEntityTable ) ) {
 			logicalName = StringHelper.quote( logicalName );
 		}
 
 		return new AssociationTableNameSource( name, logicalName );
 	}
 
 	public static Table buildAndFillTable(
 			String schema,
 			String catalog,
 			ObjectNameSource nameSource,
 			ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper,
 			boolean isAbstract,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings,
 			String subselect) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 
 		String realTableName = mappings.getObjectNameNormalizer().normalizeDatabaseIdentifier(
 				nameSource.getExplicitName(),
 				namingStrategyHelper
 		);
 
 		final Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					subselect,
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					subselect,
 					isAbstract
 			);
 		}
 
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraintHolders( table, uniqueConstraints );
 		}
 
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 
 		// logicalName is null if we are in the second pass
 		final String logicalName = nameSource.getLogicalName();
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	/**
 	 * @deprecated Use {@link #buildAndFillTable} instead.
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static Table fillTable(
 			String schema,
 			String catalog,
 			String realTableName,
 			String logicalName,
 			boolean isAbstract,
 			List uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 		Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					null, //subselect
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					null, //subselect
 					isAbstract
 			);
 		}
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraints( table, uniqueConstraints );
 		}
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 		//logicalName is null if we are in the second pass
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	public static void bindFk(
 			PersistentClass referencedEntity,
 			PersistentClass destinationEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		PersistentClass associatedClass;
 		if ( destinationEntity != null ) {
 			//overridden destination
 			associatedClass = destinationEntity;
 		}
 		else {
 			associatedClass = columns[0].getPropertyHolder() == null
 					? null
 					: columns[0].getPropertyHolder().getPersistentClass();
 		}
 		final String mappedByProperty = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedByProperty ) ) {
 			/**
 			 * Get the columns of the mapped-by property
 			 * copy them and link the copy to the actual value
 			 */
 			LOG.debugf( "Retrieving property %s.%s", associatedClass.getEntityName(), mappedByProperty );
 
 			final Property property = associatedClass.getRecursiveProperty( columns[0].getMappedBy() );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				Collection collection = ( (Collection) property.getValue() );
 				Value element = collection.getElement();
 				if ( element == null ) {
 					throw new AnnotationException(
 							"Illegal use of mappedBy on both sides of the relationship: "
 									+ associatedClass.getEntityName() + "." + mappedByProperty
 					);
 				}
 				mappedByColumns = element.getColumnIterator();
 			}
 			else {
 				mappedByColumns = property.getValue().getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 		}
 		else if ( columns[0].isImplicit() ) {
 			/**
 			 * if columns are implicit, then create the columns based on the
 			 * referenced entity id columns
 			 */
 			Iterator idColumns;
 			if ( referencedEntity instanceof JoinedSubclass ) {
 				idColumns = referencedEntity.getKey().getColumnIterator();
 			}
 			else {
 				idColumns = referencedEntity.getIdentifier().getColumnIterator();
 			}
 			while ( idColumns.hasNext() ) {
 				Column column = (Column) idColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingDefaultColumnNaming( column, referencedEntity, value );
 			}
 		}
 		else {
 			int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, referencedEntity, mappings );
 
 			if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 				String referencedPropertyName;
 				if ( value instanceof ToOne ) {
 					referencedPropertyName = ( (ToOne) value ).getReferencedPropertyName();
 				}
 				else if ( value instanceof DependantValue ) {
 					String propertyName = columns[0].getPropertyName();
 					if ( propertyName != null ) {
 						Collection collection = (Collection) referencedEntity.getRecursiveProperty( propertyName )
 								.getValue();
 						referencedPropertyName = collection.getReferencedPropertyName();
 					}
 					else {
 						throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 					}
 
 				}
 				else {
 					throw new AssertionFailure(
 							"Do a property ref on an unexpected Value type: "
 									+ value.getClass().getName()
 					);
 				}
 				if ( referencedPropertyName == null ) {
 					throw new AssertionFailure(
 							"No property ref found while expected"
 					);
 				}
 				Property synthProp = referencedEntity.getReferencedProperty( referencedPropertyName );
 				if ( synthProp == null ) {
 					throw new AssertionFailure(
 							"Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName
 					);
 				}
 				linkJoinColumnWithValueOverridingNameIfImplicit(
 						referencedEntity, synthProp.getColumnIterator(), columns, value
 				);
 
 			}
 			else {
 				if ( Ejb3JoinColumn.NO_REFERENCE == fkEnum ) {
 					//implicit case, we hope PK and FK columns are in the same order
 					if ( columns.length != referencedEntity.getIdentifier().getColumnSpan() ) {
 						throw new AnnotationException(
 								"A Foreign key refering " + referencedEntity.getEntityName()
 										+ " from " + associatedClass.getEntityName()
 										+ " has the wrong number of column. should be " + referencedEntity.getIdentifier()
 										.getColumnSpan()
 						);
 					}
 					linkJoinColumnWithValueOverridingNameIfImplicit(
 							referencedEntity,
 							referencedEntity.getIdentifier().getColumnIterator(),
 							columns,
 							value
 					);
 				}
 				else {
 					//explicit referencedColumnName
 					Iterator idColItr = referencedEntity.getKey().getColumnIterator();
 					org.hibernate.mapping.Column col;
 					Table table = referencedEntity.getTable(); //works cause the pk has to be on the primary table
 					if ( !idColItr.hasNext() ) {
 						LOG.debug( "No column in the identifier!" );
 					}
 					while ( idColItr.hasNext() ) {
 						boolean match = false;
 						//for each PK column, find the associated FK column.
 						col = (org.hibernate.mapping.Column) idColItr.next();
 						for (Ejb3JoinColumn joinCol : columns) {
 							String referencedColumn = joinCol.getReferencedColumn();
 							referencedColumn = mappings.getPhysicalColumnName( referencedColumn, table );
 							//In JPA 2 referencedColumnName is case insensitive
 							if ( referencedColumn.equalsIgnoreCase( col.getQuotedName() ) ) {
 								//proper join column
 								if ( joinCol.isNameDeferred() ) {
 									joinCol.linkValueUsingDefaultColumnNaming(
 											col, referencedEntity, value
 									);
 								}
 								else {
 									joinCol.linkWithValue( value );
 								}
 								joinCol.overrideFromReferencedColumnIfNecessary( col );
 								match = true;
 								break;
 							}
 						}
 						if ( !match ) {
 							throw new AnnotationException(
 									"Column name " + col.getName() + " of "
 											+ referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName"
 							);
 						}
 					}
 				}
 			}
 		}
 		value.createForeignKey();
 		if ( unique ) {
 			createUniqueConstraint( value );
 		}
 	}
 
 	public static void linkJoinColumnWithValueOverridingNameIfImplicit(
 			PersistentClass referencedEntity,
 			Iterator columnIterator,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value) {
 		for (Ejb3JoinColumn joinCol : columns) {
 			Column synthCol = (Column) columnIterator.next();
 			if ( joinCol.isNameDeferred() ) {
 				//this has to be the default value
 				joinCol.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity, value );
 			}
 			else {
 				joinCol.linkWithValue( value );
 				joinCol.overrideFromReferencedColumnIfNecessary( synthCol );
 			}
 		}
 	}
 
 	public static void createUniqueConstraint(Value value) {
 		Iterator iter = value.getColumnIterator();
 		ArrayList cols = new ArrayList();
 		while ( iter.hasNext() ) {
 			cols.add( iter.next() );
 		}
 		value.getTable().createUniqueKey( cols );
 	}
 
 	public static void addIndexes(Table hibTable, Index[] indexes, Mappings mappings) {
 		for (Index index : indexes) {
 			//no need to handle inSecondPass here since it is only called from EntityBinder
 			mappings.addSecondPass(
 					new IndexOrUniqueKeySecondPass( hibTable, index.name(), index.columnNames(), mappings )
 			);
 		}
 	}
 
+	public static void addIndexes(Table hibTable, javax.persistence.Index[] indexes, Mappings mappings) {
+		for ( javax.persistence.Index index : indexes ) {
+			//no need to handle inSecondPass here since it is only called from EntityBinder
+			mappings.addSecondPass(
+					new IndexOrUniqueKeySecondPass(
+							hibTable,
+							index.name(),
+							index.columnList(),
+							mappings,
+							index.unique()
+					)
+			);
+		}
+	}
+
 	/**
 	 * @deprecated Use {@link #buildUniqueConstraintHolders} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
 		List<String[]> result = new ArrayList<String[]>();
 		if ( constraintsArray.length != 0 ) {
 			for (UniqueConstraint uc : constraintsArray) {
 				result.add( uc.columnNames() );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Build a list of {@link org.hibernate.cfg.UniqueConstraintHolder} instances given a list of
 	 * {@link UniqueConstraint} annotations.
 	 *
 	 * @param annotations The {@link UniqueConstraint} annotations.
 	 *
 	 * @return The built {@link org.hibernate.cfg.UniqueConstraintHolder} instances.
 	 */
 	public static List<UniqueConstraintHolder> buildUniqueConstraintHolders(UniqueConstraint[] annotations) {
 		List<UniqueConstraintHolder> result;
 		if ( annotations == null || annotations.length == 0 ) {
 			result = java.util.Collections.emptyList();
 		}
 		else {
 			result = new ArrayList<UniqueConstraintHolder>( CollectionHelper.determineProperSizing( annotations.length ) );
 			for ( UniqueConstraint uc : annotations ) {
 				result.add(
 						new UniqueConstraintHolder()
 								.setName( uc.name() )
 								.setColumns( uc.columnNames() )
 				);
 			}
 		}
 		return result;
 	}
 
 	public void setDefaultName(
 			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
 			String propertyName
 	) {
 		this.ownerEntity = ownerEntity;
 		this.ownerEntityTable = ownerEntityTable;
 		this.associatedEntity = associatedEntity;
 		this.associatedEntityTable = associatedEntityTable;
 		this.propertyName = propertyName;
 		this.name = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
index ff32adf499..2e34aa859f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverriddenAnnotationReader.java
@@ -1265,1227 +1265,1228 @@ public class JPAOverriddenAnnotationReader implements AnnotationReader {
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
 			List<QueryHint> queryHints = new ArrayList<QueryHint>( elements.size() );
 			for ( Element hint : elements ) {
 				AnnotationDescriptor hintDescriptor = new AnnotationDescriptor( QueryHint.class );
 				String value = hint.attributeValue( "name" );
 				if ( value == null ) {
 					throw new AnnotationException( "<hint> without name. " + SCHEMA_VALIDATION );
 				}
 				hintDescriptor.setValue( "name", value );
 				value = hint.attributeValue( "value" );
 				if ( value == null ) {
 					throw new AnnotationException( "<hint> without value. " + SCHEMA_VALIDATION );
 				}
 				hintDescriptor.setValue( "value", value );
 				queryHints.add( (QueryHint) AnnotationFactory.create( hintDescriptor ) );
 			}
 			ann.setValue( "hints", queryHints.toArray( new QueryHint[queryHints.size()] ) );
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
+						annotation.setValue( "indexes", table.indexes() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
index 852b7c7854..15c2ae9646 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
@@ -1,109 +1,108 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Index;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * DB2 does not allow unique constraints on nullable columns.  Rather than
  * forcing "not null", use unique *indexes* instead.
  * 
  * @author Brett Meyer
  */
 public class DB2UniqueDelegate extends DefaultUniqueDelegate {
 	
 	public DB2UniqueDelegate( Dialect dialect ) {
 		super( dialect );
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 		if ( hasNullable( uniqueKey ) ) {
 			return org.hibernate.mapping.Index.buildSqlCreateIndexString(
 					dialect, uniqueKey.getName(), uniqueKey.getTable(),
-					uniqueKey.columnIterator(), true, defaultCatalog,
+					uniqueKey.columnIterator(), uniqueKey.getColumnOrderMap(), true, defaultCatalog,
 					defaultSchema );
 		} else {
 			return super.applyUniquesOnAlter(
 					uniqueKey, defaultCatalog, defaultSchema );
 		}
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( UniqueKey uniqueKey ) {
 		if ( hasNullable( uniqueKey ) ) {
 			return Index.buildSqlCreateIndexString(
 					dialect, uniqueKey.getName(), uniqueKey.getTable(),
 					uniqueKey.getColumns(), true );
 		} else {
 			return super.applyUniquesOnAlter( uniqueKey );
 		}
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 		if ( hasNullable( uniqueKey ) ) {
 			return org.hibernate.mapping.Index.buildSqlDropIndexString(
 					dialect, uniqueKey.getTable(), uniqueKey.getName(),
 					defaultCatalog, defaultSchema );
 		} else {
 			return super.dropUniquesOnAlter(
 					uniqueKey, defaultCatalog, defaultSchema );
 		}
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( UniqueKey uniqueKey ) {
 		if ( hasNullable( uniqueKey ) ) {
 			return Index.buildSqlDropIndexString(
 					dialect, uniqueKey.getTable(), uniqueKey.getName() );
 		} else {
 			return super.dropUniquesOnAlter( uniqueKey );
 		}
 	}
 	
 	private boolean hasNullable( org.hibernate.mapping.UniqueKey uniqueKey ) {
-		Iterator iter = uniqueKey.getColumnIterator();
+		Iterator<org.hibernate.mapping.Column> iter = uniqueKey.columnIterator();
 		while ( iter.hasNext() ) {
-			if ( ( ( org.hibernate.mapping.Column ) iter.next() ).isNullable() ) {
+			if ( iter.next().isNullable() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 	
 	private boolean hasNullable( UniqueKey uniqueKey ) {
-		Iterator iter = uniqueKey.getColumns().iterator();
-		while ( iter.hasNext() ) {
-			if ( ( ( Column ) iter.next() ).isNullable() ) {
+		for ( Column column : uniqueKey.getColumns() ) {
+			if ( column.isNullable() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
index 09e1d7e75d..cf67311f01 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
@@ -1,148 +1,151 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * The default UniqueDelegate implementation for most dialects.  Uses
  * separate create/alter statements to apply uniqueness to a column.
  * 
  * @author Brett Meyer
  */
 public class DefaultUniqueDelegate implements UniqueDelegate {
 	
 	protected final Dialect dialect;
 	
 	public DefaultUniqueDelegate( Dialect dialect ) {
 		this.dialect = dialect;
 	}
 	
 	@Override
 	public String applyUniqueToColumn( org.hibernate.mapping.Column column ) {
 		return "";
 	}
 	
 	@Override
 	public String applyUniqueToColumn( Column column ) {
 		return "";
 	}
 
 	@Override
 	public String applyUniquesToTable( org.hibernate.mapping.Table table ) {
 		return "";
 	}
 
 	@Override
 	public String applyUniquesToTable( Table table ) {
 		return "";
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 		// Do this here, rather than allowing UniqueKey/Constraint to do it.
 		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " add constraint " )
 				.append( uniqueKey.getName() )
 				.append( uniqueConstraintSql( uniqueKey ) )
 				.toString();
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( UniqueKey uniqueKey  ) {
 		// Do this here, rather than allowing UniqueKey/Constraint to do it.
 		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
 				.append( " add constraint " )
 				.append( uniqueKey.getName() )
 				.append( uniqueConstraintSql( uniqueKey ) )
 				.toString();
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 		// Do this here, rather than allowing UniqueKey/Constraint to do it.
 		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " drop constraint " )
 				.append( dialect.quote( uniqueKey.getName() ) )
 				.toString();
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( UniqueKey uniqueKey  ) {
 		// Do this here, rather than allowing UniqueKey/Constraint to do it.
 		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
 				.append( " drop constraint " )
 				.append( dialect.quote( uniqueKey.getName() ) )
 				.toString();
 	}
 	
 	@Override
 	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey ) {
 		StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
-		Iterator columnIterator = uniqueKey.getColumnIterator();
+		Iterator<org.hibernate.mapping.Column> columnIterator = uniqueKey.columnIterator();
 		while ( columnIterator.hasNext() ) {
 			org.hibernate.mapping.Column column
-					= (org.hibernate.mapping.Column) columnIterator.next();
+					= columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
+			if ( uniqueKey.getColumnOrderMap().containsKey( column ) ) {
+				sb.append( " " ).append( uniqueKey.getColumnOrderMap().get( column ) );
+			}
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
 		
 		return sb.append( ')' ).toString();
 	}
 	
 	@Override
 	public String uniqueConstraintSql( UniqueKey uniqueKey ) {
 		StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
 		Iterator columnIterator = uniqueKey.getColumns().iterator();
 		while ( columnIterator.hasNext() ) {
 			org.hibernate.mapping.Column column
 					= (org.hibernate.mapping.Column) columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
 		
 		return sb.append( ')' ).toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java b/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
index 9fff9398e7..be846eaf46 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
@@ -1,136 +1,136 @@
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
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 
 /**
  * A relational constraint.
  *
  * @author Gavin King
  */
 public abstract class Constraint implements RelationalModel, Serializable {
 
 	private String name;
-	private final List columns = new ArrayList();
+	private final List<Column> columns = new ArrayList<Column>();
 	private Table table;
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
-	public Iterator getColumnIterator() {
-		return columns.iterator();
-	}
-
 	public void addColumn(Column column) {
 		if ( !columns.contains( column ) ) columns.add( column );
 	}
 
 	public void addColumns(Iterator columnIterator) {
 		while ( columnIterator.hasNext() ) {
 			Selectable col = (Selectable) columnIterator.next();
 			if ( !col.isFormula() ) addColumn( (Column) col );
 		}
 	}
 
 	/**
 	 * @param column
 	 * @return true if this constraint already contains a column with same name.
 	 */
 	public boolean containsColumn(Column column) {
 		return columns.contains( column );
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Column getColumn(int i) {
-		return (Column) columns.get( i );
+		return  columns.get( i );
+	}
+	//todo duplicated method, remove one
+	public Iterator<Column> getColumnIterator() {
+		return columns.iterator();
 	}
 
-	public Iterator columnIterator() {
+	public Iterator<Column> columnIterator() {
 		return columns.iterator();
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public boolean isGenerated(Dialect dialect) {
 		return true;
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( isGenerated( dialect ) ) {
 			return new StringBuilder()
 					.append( "alter table " )
 					.append( getTable().getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 					.append( " drop constraint " )
 					.append( dialect.quote( getName() ) )
 					.toString();
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		if ( isGenerated( dialect ) ) {
 			String constraintString = sqlConstraintString( dialect, getName(), defaultCatalog, defaultSchema );
 			StringBuilder buf = new StringBuilder( "alter table " )
 					.append( getTable().getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 					.append( constraintString );
 			return buf.toString();
 		}
 		else {
 			return null;
 		}
 	}
 
 	public List getColumns() {
 		return columns;
 	}
 
 	public abstract String sqlConstraintString(Dialect d, String constraintName, String defaultCatalog,
 											   String defaultSchema);
 
 	public String toString() {
 		return getClass().getName() + '(' + getTable().getName() + getColumns() + ") as " + name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Index.java b/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
index da70576a05..85df62150e 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
@@ -1,165 +1,192 @@
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
+import java.util.Collections;
+import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A relational table index
  *
  * @author Gavin King
  */
 public class Index implements RelationalModel, Serializable {
 
 	private Table table;
-	private List columns = new ArrayList();
+	private List<Column> columns = new ArrayList<Column>();
+	private java.util.Map<Column, String> columnOrderMap = new HashMap<Column, String>(  );
 	private String name;
 
 	public String sqlCreateString(Dialect dialect, Mapping mapping, String defaultCatalog, String defaultSchema)
 			throws HibernateException {
 		return buildSqlCreateIndexString(
 				dialect,
 				getName(),
 				getTable(),
 				getColumnIterator(),
+				columnOrderMap,
 				false,
 				defaultCatalog,
 				defaultSchema
 		);
 	}
 
 	public static String buildSqlDropIndexString(
 			Dialect dialect,
 			Table table,
 			String name,
 			String defaultCatalog,
 			String defaultSchema
 	) {
 		return "drop index " +
 				StringHelper.qualify(
 						table.getQualifiedName( dialect, defaultCatalog, defaultSchema ),
 						name
 				);
 	}
 
 	public static String buildSqlCreateIndexString(
 			Dialect dialect,
 			String name,
 			Table table,
-			Iterator columns,
+			Iterator<Column> columns,
+			java.util.Map<Column, String> columnOrderMap,
 			boolean unique,
 			String defaultCatalog,
 			String defaultSchema
 	) {
 		StringBuilder buf = new StringBuilder( "create" )
 				.append( unique ?
 						" unique" :
 						"" )
 				.append( " index " )
 				.append( dialect.qualifyIndexName() ?
 						name :
 						StringHelper.unqualify( name ) )
 				.append( " on " )
 				.append( table.getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
-		Iterator iter = columns;
-		while ( iter.hasNext() ) {
-			buf.append( ( (Column) iter.next() ).getQuotedName( dialect ) );
-			if ( iter.hasNext() ) buf.append( ", " );
+		while ( columns.hasNext() ) {
+			Column column = columns.next();
+			buf.append( column.getQuotedName( dialect ) );
+			if ( columnOrderMap.containsKey( column ) ) {
+				buf.append( " " ).append( columnOrderMap.get( column ) );
+			}
+			if ( columns.hasNext() ) buf.append( ", " );
 		}
 		buf.append( ")" );
 		return buf.toString();
 	}
 
+	public static String buildSqlCreateIndexString(
+			Dialect dialect,
+			String name,
+			Table table,
+			Iterator<Column> columns,
+			boolean unique,
+			String defaultCatalog,
+			String defaultSchema
+	) {
+		return buildSqlCreateIndexString( dialect, name, table, columns, Collections.EMPTY_MAP, unique, defaultCatalog, defaultSchema );
+	}
+
 
 	// Used only in Table for sqlCreateString (but commented out at the moment)
 	public String sqlConstraintString(Dialect dialect) {
 		StringBuilder buf = new StringBuilder( " index (" );
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			buf.append( ( (Column) iter.next() ).getQuotedName( dialect ) );
 			if ( iter.hasNext() ) buf.append( ", " );
 		}
 		return buf.append( ')' ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return "drop index " +
 				StringHelper.qualify(
 						table.getQualifiedName( dialect, defaultCatalog, defaultSchema ),
 						name
 				);
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
-	public Iterator getColumnIterator() {
+	public Iterator<Column> getColumnIterator() {
 		return columns.iterator();
 	}
 
 	public void addColumn(Column column) {
 		if ( !columns.contains( column ) ) columns.add( column );
 	}
 
+	public void addColumn(Column column, String order) {
+		addColumn( column );
+		if ( StringHelper.isNotEmpty( order ) ) {
+			columnOrderMap.put( column, order );
+		}
+	}
+
 	public void addColumns(Iterator extraColumns) {
 		while ( extraColumns.hasNext() ) addColumn( (Column) extraColumns.next() );
 	}
 
 	/**
 	 * @param column
 	 * @return true if this constraint already contains a column with same name.
 	 */
 	public boolean containsColumn(Column column) {
 		return columns.contains( column );
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public String toString() {
 		return getClass().getName() + "(" + getName() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index c30ca994c6..2f9aeccfe1 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,879 +1,880 @@
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
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 public class Table implements RelationalModel, Serializable {
 
 	private String name;
 	private String schema;
 	private String catalog;
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
-	private Map indexes = new LinkedHashMap();
+	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map foreignKeys = new LinkedHashMap();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private boolean quoted;
 	private boolean schemaQuoted;
 	private boolean catalogQuoted;
 	private List checkConstraints = new ArrayList();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables = false;
 	private String comment;
 
 	static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 	}
 
 	public Table() { }
 
 	public Table(String name) {
 		this();
 		setName( name );
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * returns quoted name as it would be in the mapping file.
 	 */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return quoted ?
 				dialect.openQuote() + name + dialect.closeQuote() :
 				name;
 	}
 
 	/**
 	 * returns quoted name as it is in the mapping file.
 	 */
 	public String getQuotedSchema() {
 		return schemaQuoted ?
 				"`" + schema + "`" :
 				schema;
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schemaQuoted ?
 				dialect.openQuote() + schema + dialect.closeQuote() :
 				schema;
 	}
 
 	public String getQuotedCatalog() {
 		return catalogQuoted ?
 				"`" + catalog + "`" :
 				catalog;
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalogQuoted ?
 				dialect.openQuote() + catalog + dialect.closeQuote() :
 				catalog;
 	}
 
 	public void setName(String name) {
 		if ( name.charAt( 0 ) == '`' ) {
 			quoted = true;
 			this.name = name.substring( 1, name.length() - 1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
-	public Iterator getIndexIterator() {
+	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
-	public Iterator getUniqueKeyIterator() {
+	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
-	Map getUniqueKeys() {
+	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse = 0;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result
 			+ ((catalog == null) ? 0 : isCatalogQuoted() ? catalog.hashCode() : catalog.toLowerCase().hashCode());
 		result = prime * result + ((name == null) ? 0 : isQuoted() ? name.hashCode() : name.toLowerCase().hashCode());
 		result = prime * result
 			+ ((schema == null) ? 0 : isSchemaQuoted() ? schema.hashCode() : schema.toLowerCase().hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return isQuoted() ? name.equals(table.getName()) : name.equalsIgnoreCase(table.getName())
 			&& ((schema == null && table.getSchema() != null) ? false : (schema == null) ? true : isSchemaQuoted() ? schema.equals(table.getSchema()) : schema.equalsIgnoreCase(table.getSchema()))
 			&& ((catalog == null && table.getCatalog() != null) ? false : (catalog == null) ? true : isCatalogQuoted() ? catalog.equals(table.getCatalog()) : catalog.equalsIgnoreCase(table.getCatalog()));
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase()
 						.startsWith( columnInfo.getTypeName().toLowerCase() )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase() +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
 									String defaultSchema)
 			throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( column.getName() );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					UniqueKey uk = getOrCreateUniqueKey( 
 							column.getQuotedName( dialect ) + '_' );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.applyUniqueToColumn( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
 		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
 				.append( ' ' )
 				.append( name )
 				.append( " (" );
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Column column = (Column) itr.next();
 			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
 			buffer.append( column.getSqlType( dialect, mapping ) );
 			if ( column.isNullable() ) {
 				buffer.append( dialect.getNullColumnString() );
 			}
 			else {
 				buffer.append( " not null" );
 			}
 			if ( itr.hasNext() ) {
 				buffer.append( ", " );
 			}
 		}
 		buffer.append( ") " );
 		buffer.append( dialect.getCreateTemporaryTablePostfix() );
 		return buffer.toString();
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				UniqueKey uk = getOrCreateUniqueKey( 
 						col.getQuotedName( dialect ) + '_' );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.applyUniqueToColumn( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			Iterator chiter = checkConstraints.iterator();
 			while ( chiter.hasNext() ) {
 				buf.append( ", check (" )
 						.append( chiter.next() )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
-		Index index = (Index) indexes.get( indexName );
+		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
-		return (Index) indexes.get( indexName );
+		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
-		Index current = (Index) indexes.get( index.getName() );
+		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = "UK" + uniqueColumnString( keyColumns.iterator() );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
 									   List referencedColumns) {
 		Object key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = (ForeignKey) foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			if ( keyName != null ) {
 				fk.setName( keyName );
 			}
 			else {
 				fk.setName( "FK" + uniqueColumnString( keyColumns.iterator(), referencedEntityName ) );
 				//TODO: add referencedClass to disambiguate to FKs on the same
 				//      columns, pointing to different tables
 			}
 			fk.setTable( this );
 			foreignKeys.put( key, fk );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	public String uniqueColumnString(Iterator iterator) {
 		return uniqueColumnString( iterator, null );
 	}
 
 	public String uniqueColumnString(Iterator iterator, String referencedEntityName) {
 		int result = 0;
 		if ( referencedEntityName != null ) {
 			result += referencedEntityName.hashCode();
 		}
 		while ( iterator.hasNext() ) {
 			result += iterator.next().hashCode();
 		}
 		return ( Integer.toHexString( name.hashCode() ) + Integer.toHexString( result ) ).toUpperCase();
 	}
 
 
+
 	public String getSchema() {
 		return schema;
 	}
 
 	public void setSchema(String schema) {
 		if ( schema != null && schema.charAt( 0 ) == '`' ) {
 			schemaQuoted = true;
 			this.schema = schema.substring( 1, schema.length() - 1 );
 		}
 		else {
 			this.schema = schema;
 		}
 	}
 
 	public String getCatalog() {
 		return catalog;
 	}
 
 	public void setCatalog(String catalog) {
 		if ( catalog != null && catalog.charAt( 0 ) == '`' ) {
 			catalogQuoted = true;
 			this.catalog = catalog.substring( 1, catalog.length() - 1 );
 		}
 		else {
 			this.catalog = catalog;
 		}
 	}
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public boolean isSchemaQuoted() {
 		return schemaQuoted;
 	}
 	public boolean isCatalogQuoted() {
 		return catalogQuoted;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public void setQuoted(boolean quoted) {
 		this.quoted = quoted;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() + "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() + "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "comment on table " )
 						.append( tableName )
 						.append( " is '" )
 						.append( comment )
 						.append( "'" );
 				comments.add( buf.toString() );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					StringBuilder buf = new StringBuilder()
 							.append( "comment on column " )
 							.append( tableName )
 							.append( '.' )
 							.append( column.getQuotedName( dialect ) )
 							.append( " is '" )
 							.append( columnComment )
 							.append( "'" );
 					comments.add( buf.toString() );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
index e5613817b9..8dbceb47a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
@@ -1,60 +1,75 @@
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
+import java.util.*;
+import java.util.Map;
+
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
+import org.hibernate.internal.util.StringHelper;
 
 /**
  * A relational unique key constraint
  *
  * @author Brett Meyer
  */
 public class UniqueKey extends Constraint {
+	private java.util.Map<Column, String> columnOrderMap = new HashMap<Column, String>(  );
 
 	@Override
     public String sqlConstraintString(
 			Dialect dialect,
 			String constraintName,
 			String defaultCatalog,
 			String defaultSchema) {
 //		return dialect.getUniqueDelegate().uniqueConstraintSql( this );
 		// Not used.
 		return "";
 	}
 
 	@Override
     public String sqlCreateString(Dialect dialect, Mapping p,
     		String defaultCatalog, String defaultSchema) {
 		return dialect.getUniqueDelegate().applyUniquesOnAlter(
 				this, defaultCatalog, defaultSchema );
 	}
 
 	@Override
     public String sqlDropString(Dialect dialect, String defaultCatalog,
     		String defaultSchema) {
 		return dialect.getUniqueDelegate().dropUniquesOnAlter(
 				this, defaultCatalog, defaultSchema );
 	}
 
+	public void addColumn(Column column, String order) {
+		addColumn( column );
+		if ( StringHelper.isNotEmpty( order ) ) {
+			columnOrderMap.put( column, order );
+		}
+	}
+
+	public Map<Column, String> getColumnOrderMap() {
+		return columnOrderMap;
+	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/Car.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/Car.java
new file mode 100644
index 0000000000..a0bea61957
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/Car.java
@@ -0,0 +1,45 @@
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
+package org.hibernate.test.annotations.index.jpa;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Index;
+import javax.persistence.Table;
+
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+@Entity
+@Table( indexes = {@Index( unique = true, columnList = "brand, producer")
+, @Index( name = "Car_idx", columnList = "since DESC")})
+public class Car {
+	@Id
+	long id;
+	String brand;
+	String producer;
+	long since;
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/IndexTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/IndexTest.java
new file mode 100644
index 0000000000..021ddcf26c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/IndexTest.java
@@ -0,0 +1,74 @@
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
+package org.hibernate.test.annotations.index.jpa;
+
+import java.util.Iterator;
+
+import org.junit.Test;
+
+import static org.junit.Assert.*;
+
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.Index;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.UniqueKey;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+public class IndexTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Car.class };
+	}
+
+	@Test
+	public void testBasicIndex() {
+		PersistentClass carClass = configuration().getClassMapping( Car.class.getName() );
+		Iterator itr = carClass.getTable().getUniqueKeyIterator();
+		assertTrue( itr.hasNext() );
+		UniqueKey uk = (UniqueKey) itr.next();
+		assertFalse( itr.hasNext() );
+		assertTrue( StringHelper.isNotEmpty( uk.getName() ) );
+		assertEquals( 2, uk.getColumnSpan() );
+		Column column = (Column) uk.getColumns().get( 0 );
+		assertEquals( "brand", column.getName() );
+		column = (Column) uk.getColumns().get( 1 );
+		assertEquals( "producer", column.getName() );
+		assertSame( carClass.getTable(), uk.getTable() );
+
+
+		itr = carClass.getTable().getIndexIterator();
+		assertTrue( itr.hasNext() );
+		Index index = (Index)itr.next();
+		assertFalse( itr.hasNext() );
+		assertEquals( "Car_idx", index.getName() );
+		assertEquals( 1, index.getColumnSpan() );
+		column = index.getColumnIterator().next();
+		assertEquals( "since", column.getName() );
+		assertSame( carClass.getTable(), index.getTable() );
+	}
+}
diff --git a/hibernate-core/src/test/resources/hibernate.properties b/hibernate-core/src/test/resources/hibernate.properties
index 3d95e5b98e..cae1c6c221 100644
--- a/hibernate-core/src/test/resources/hibernate.properties
+++ b/hibernate-core/src/test/resources/hibernate.properties
@@ -1,39 +1,40 @@
 #
 # Hibernate, Relational Persistence for Idiomatic Java
 #
 # Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 # indicated by the @author tags or express copyright attribution
 # statements applied by the authors.  All third-party contributions are
 # distributed under license by Red Hat Inc.
 #
 # This copyrighted material is made available to anyone wishing to use, modify,
 # copy, or redistribute it subject to the terms and conditions of the GNU
 # Lesser General Public License, as published by the Free Software Foundation.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 # or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 # for more details.
 #
 # You should have received a copy of the GNU Lesser General Public License
 # along with this distribution; if not, write to:
 # Free Software Foundation, Inc.
 # 51 Franklin Street, Fifth Floor
 # Boston, MA  02110-1301  USA
 
 hibernate.dialect org.hibernate.dialect.H2Dialect
 hibernate.connection.driver_class org.h2.Driver
 hibernate.connection.url jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE
 hibernate.connection.username sa
 
 hibernate.connection.pool_size 5
 
 hibernate.show_sql false
+hibernate.format_sql true
 
 hibernate.max_fetch_depth 5
 
 hibernate.cache.region_prefix hibernate.test
 hibernate.cache.region.factory_class org.hibernate.testing.cache.CachingRegionFactory
 
 # NOTE: hibernate.jdbc.batch_versioned_data should be set to false when testing with Oracle
 hibernate.jdbc.batch_versioned_data true
\ No newline at end of file
