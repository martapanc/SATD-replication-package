diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 1b792fc609..cc6802ff51 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1281 +1,1282 @@
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
+
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
new file mode 100644
index 0000000000..371f43a546
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
@@ -0,0 +1,124 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.cfg;
+
+import javax.persistence.AttributeConverter;
+import java.lang.reflect.ParameterizedType;
+import java.lang.reflect.Type;
+import java.lang.reflect.TypeVariable;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.AssertionFailure;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AttributeConverterDefinition {
+	private static final Logger log = Logger.getLogger( AttributeConverterDefinition.class );
+
+	private final AttributeConverter attributeConverter;
+	private final boolean autoApply;
+	private final Class entityAttributeType;
+	private final Class databaseColumnType;
+
+	public AttributeConverterDefinition(AttributeConverter attributeConverter, boolean autoApply) {
+		this.attributeConverter = attributeConverter;
+		this.autoApply = autoApply;
+
+		final Class attributeConverterClass = attributeConverter.getClass();
+		final ParameterizedType attributeConverterSignature = extractAttributeConverterParameterizedType( attributeConverterClass );
+
+		if ( attributeConverterSignature.getActualTypeArguments().length < 2 ) {
+			throw new AnnotationException(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] did not retain parameterized type information"
+			);
+		}
+
+		if ( attributeConverterSignature.getActualTypeArguments().length > 2 ) {
+			throw new AnnotationException(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] specified more than 2 parameterized types"
+			);
+		}
+		entityAttributeType = (Class) attributeConverterSignature.getActualTypeArguments()[0];
+		if ( entityAttributeType == null ) {
+			throw new AnnotationException(
+					"Could not determine 'entity attribute' type from given AttributeConverter [" +
+							attributeConverterClass.getName() + "]"
+			);
+		}
+
+		databaseColumnType = (Class) attributeConverterSignature.getActualTypeArguments()[1];
+		if ( databaseColumnType == null ) {
+			throw new AnnotationException(
+					"Could not determine 'database column' type from given AttributeConverter [" +
+							attributeConverterClass.getName() + "]"
+			);
+		}
+	}
+
+	private ParameterizedType extractAttributeConverterParameterizedType(Class attributeConverterClass) {
+		for ( Type type : attributeConverterClass.getGenericInterfaces() ) {
+			if ( ParameterizedType.class.isInstance( type ) ) {
+				final ParameterizedType parameterizedType = (ParameterizedType) type;
+				if ( AttributeConverter.class.equals( parameterizedType.getRawType() ) ) {
+					return parameterizedType;
+				}
+			}
+		}
+
+		throw new AssertionFailure(
+				"Could not extract ParameterizedType representation of AttributeConverter definition " +
+						"from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]"
+		);
+	}
+
+	public AttributeConverter getAttributeConverter() {
+		return attributeConverter;
+	}
+
+	public boolean isAutoApply() {
+		return autoApply;
+	}
+
+	public Class getEntityAttributeType() {
+		return entityAttributeType;
+	}
+
+	public Class getDatabaseColumnType() {
+		return databaseColumnType;
+	}
+
+	private static Class extractType(TypeVariable typeVariable) {
+		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
+		if ( boundTypes == null || boundTypes.length != 1 ) {
+			return null;
+		}
+
+		return (Class) boundTypes[0];
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index c6f6e94701..272241b070 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1261 +1,1265 @@
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
+import java.util.concurrent.ConcurrentHashMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
+import javax.persistence.AttributeConverter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
+import org.hibernate.AssertionFailure;
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
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
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
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
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
  * NOTE : This will be replaced by use of {@link ServiceRegistryBuilder} and
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
 
+	private ConcurrentHashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 
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
 		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
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
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 
 				if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					Iterator subIter = table.getUniqueKeyIterator();
 					while ( subIter.hasNext() ) {
 						UniqueKey uk = (UniqueKey) subIter.next();
 						String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 						if (constraintString != null) script.add( constraintString );
 					}
 				}
 
 
 				Iterator subIter = table.getIndexIterator();
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
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 				);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							tableCatalog,
 							tableSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												tableCatalog,
 												tableSchema
 											)
 									);
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
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
@@ -1453,2096 +1457,2158 @@ public class Configuration implements Serializable {
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
 			String dependentTable = sp.getValue().getTable().getQuotedName();
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
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
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
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
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
 
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
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
 		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder()
 				.applySettings( properties )
 				.buildServiceRegistry();
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
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 		LOG.jaccContextId( contextId );
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
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
 		sqlFunctions.put( functionName, function );
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
 
+	/**
+	 * Adds the AttributeConverter Class to this Configuration.
+	 *
+	 * @param attributeConverterClass The AttributeConverter class.
+	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
+	 * by its "entity attribute" parameterized type?
+	 */
+	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
+		final AttributeConverter attributeConverter;
+		try {
+			attributeConverter = attributeConverterClass.newInstance();
+		}
+		catch (Exception e) {
+			throw new AnnotationException(
+					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]"
+			);
+		}
+		addAttributeConverter( attributeConverter, autoApply );
+	}
+
+	/**
+	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
+	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
+	 * {@link #addAttributeConverter(Class, boolean)} form
+	 *
+	 * @param attributeConverter The AttributeConverter instance.
+	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
+	 * by its "entity attribute" parameterized type?
+	 */
+	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
+		if ( attributeConverterDefinitionsByClass == null ) {
+			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
+		}
+
+		final Object old = attributeConverterDefinitionsByClass.put(
+				attributeConverter.getClass(),
+				new AttributeConverterDefinition( attributeConverter, autoApply )
+		);
+
+		if ( old != null ) {
+			throw new AssertionFailure(
+					"AttributeConverter class [" + attributeConverter.getClass() + "] registered multiple times"
+			);
+		}
+	}
+
 
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
 			return tables.get(key);
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
 
+		@Override
+		public AttributeConverterDefinition locateAttributeConverter(Class converterClass) {
+			if ( attributeConverterDefinitionsByClass == null ) {
+				return null;
+			}
+			return attributeConverterDefinitionsByClass.get( converterClass );
+		}
+
+		@Override
+		public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
+			if ( attributeConverterDefinitionsByClass == null ) {
+				return Collections.emptyList();
+			}
+			return attributeConverterDefinitionsByClass.values();
+		}
+
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
index e77172f547..92a9cf9136 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,759 +1,777 @@
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
 
+import javax.persistence.AttributeConverter;
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
+	 * Locate the AttributeConverterDefinition corresponding to the given AttributeConverter Class.
+	 *
+	 * @param attributeConverterClass The AttributeConverter Class for which to get the definition
+	 *
+	 * @return The corresponding AttributeConverter definition; will return {@code null} if no corresponding
+	 * definition found.
+	 */
+	public AttributeConverterDefinition locateAttributeConverter(Class attributeConverterClass);
+
+	/**
+	 * All all AttributeConverter definitions
+	 *
+	 * @return The collection of all AttributeConverter definitions.
+	 */
+	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
+
+	/**
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
 	 * Return the property annotated with @ToOne and @Id if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData property);
 
 	public boolean forceDiscriminatorInSelectsByDefault();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index c9a321b138..24f7219df7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,372 +1,591 @@
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
 
 import java.io.Serializable;
+import java.lang.reflect.TypeVariable;
 import java.sql.Types;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Properties;
+import javax.persistence.AttributeConverter;
+import javax.persistence.Convert;
+import javax.persistence.Converts;
 import javax.persistence.Enumerated;
+import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.annotations.common.util.ReflectHelper;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.WrappedMaterializedBlobType;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 
+	private AttributeConverterDefinition attributeConverterDefinition;
+
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
 	public void setType(XProperty property, XClass returnedClass) {
 		if ( returnedClass == null ) {
 			return;
 		} //we cannot guess anything
 		XClass returnedClassOrElement = returnedClass;
 		boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
 		}
 		else if ( property.isAnnotationPresent( Lob.class ) ) {
-
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = "clob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( mappings.getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				//typeParameters = new Properties();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 		}
 		//implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 				typeParameters = new Properties();
 				typeParameters.setProperty( EnumType.ENUM, returnedClassOrElement.getName() );
 				String schema = columns[0].getTable().getSchema();
 				schema = schema == null ? "" : schema;
 				String catalog = columns[0].getTable().getCatalog();
 				catalog = catalog == null ? "" : catalog;
 				typeParameters.setProperty( EnumType.SCHEMA, schema );
 				typeParameters.setProperty( EnumType.CATALOG, catalog );
 				typeParameters.setProperty( EnumType.TABLE, columns[0].getTable().getName() );
 				typeParameters.setProperty( EnumType.COLUMN, columns[0].getName() );
 				javax.persistence.EnumType enumType = getEnumType( property );
 				if ( enumType != null ) {
 					if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.INTEGER ) );
 					}
 					else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.VARCHAR ) );
 					}
 					else {
 						throw new AssertionFailure( "Unknown EnumType: " + enumType );
 					}
 				}
 			}
 		}
 		explicitType = type;
 		this.typeParameters = typeParameters;
 		Type annType = property.getAnnotation( Type.class );
 		setExplicitType( annType );
+
+		applyAttributeConverter( property );
+	}
+
+	private void applyAttributeConverter(XProperty property) {
+		final boolean canBeConverted = ! property.isAnnotationPresent( Id.class )
+				&& ! isVersion
+				&& ! isAssociation()
+				&& ! property.isAnnotationPresent( Temporal.class )
+				&& ! property.isAnnotationPresent( Enumerated.class );
+
+		if ( canBeConverted ) {
+			// @Convert annotations take precedence
+			final Convert convertAnnotation = locateConvertAnnotation( property );
+			if ( convertAnnotation != null ) {
+				if ( ! convertAnnotation.disableConversion() ) {
+					attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
+				}
+			}
+			else {
+				attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
+			}
+		}
+	}
+
+	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
+		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
+		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
+			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
+				return attributeConverterDefinition;
+			}
+		}
+		return null;
+	}
+
+	private boolean isAssociation() {
+		// todo : this information is only known to caller(s), need to pass that information in somehow.
+		// or, is this enough?
+		return referencedEntityName != null;
+	}
+
+	@SuppressWarnings("unchecked")
+	private Convert locateConvertAnnotation(XProperty property) {
+		// first look locally on the property for @Convert
+		Convert localConvertAnnotation = property.getAnnotation( Convert.class );
+		if ( localConvertAnnotation != null ) {
+			return localConvertAnnotation;
+		}
+
+		if ( persistentClassName == null ) {
+			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
+			return null;
+		}
+
+		final XClass owner;
+		try {
+			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
+			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
+		}
+		catch (ClassNotFoundException e) {
+			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
+		}
+
+		return lookForEntityDefinedConvertAnnotation( property, owner );
+	}
+
+	private Convert lookForEntityDefinedConvertAnnotation(XProperty property, XClass owner) {
+		if ( owner == null ) {
+			// we have hit the root of the entity hierarchy
+			return null;
+		}
+
+		{
+			Convert convertAnnotation = owner.getAnnotation( Convert.class );
+			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
+				return convertAnnotation;
+			}
+		}
+
+		{
+			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
+			if ( convertsAnnotation != null ) {
+				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
+					if ( isMatch( convertAnnotation, property ) ) {
+						return convertAnnotation;
+					}
+				}
+			}
+		}
+
+		// finally, look on superclass
+		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
+	}
+
+	@SuppressWarnings("unchecked")
+	private boolean isMatch(Convert convertAnnotation, XProperty property) {
+		return property.getName().equals( convertAnnotation.attributeName() )
+				&& isTypeMatch( convertAnnotation.converter(), property );
+	}
+
+	private boolean isTypeMatch(Class<? extends AttributeConverter> attributeConverterClass, XProperty property) {
+		return areTypeMatch(
+				extractEntityAttributeType( attributeConverterClass ),
+				mappings.getReflectionManager().toClass( property.getType() )
+		);
+	}
+
+	private Class extractEntityAttributeType(Class<? extends AttributeConverter> attributeConverterClass) {
+		// this is duplicated in SimpleValue...
+		final TypeVariable[] attributeConverterTypeInformation = attributeConverterClass.getTypeParameters();
+		if ( attributeConverterTypeInformation == null || attributeConverterTypeInformation.length < 2 ) {
+			throw new AnnotationException(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] did not retain parameterized type information"
+			);
+		}
+
+		if ( attributeConverterTypeInformation.length > 2 ) {
+			LOG.debug(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] specified more than 2 parameterized types"
+			);
+		}
+		final Class entityAttributeJavaType = extractType( attributeConverterTypeInformation[0] );
+		if ( entityAttributeJavaType == null ) {
+			throw new AnnotationException(
+					"Could not determine 'entity attribute' type from given AttributeConverter [" +
+							attributeConverterClass.getName() + "]"
+			);
+		}
+		return entityAttributeJavaType;
+	}
+
+	private Class extractType(TypeVariable typeVariable) {
+		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
+		if ( boundTypes == null || boundTypes.length != 1 ) {
+			return null;
+		}
+
+		return (Class) boundTypes[0];
+	}
+
+	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
+		if ( converterDefinedType == null ) {
+			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
+		}
+		if ( propertyType == null ) {
+			throw new AnnotationException( "Property defined java type cannot be null" );
+		}
+
+		return converterDefinedType.equals( propertyType )
+				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
+	}
+
+	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
+		if ( converterDefinedType.isPrimitive() ) {
+			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
+		}
+		else if ( propertyType.isPrimitive() ) {
+			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
+		}
+		return false;
+	}
+
+	private static Class getWrapperEquivalent(Class primitive) {
+		if ( ! primitive.isPrimitive() ) {
+			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
+		}
+
+		if ( boolean.class.equals( primitive ) ) {
+			return Boolean.class;
+		}
+		else if ( char.class.equals( primitive ) ) {
+			return Character.class;
+		}
+		else if ( byte.class.equals( primitive ) ) {
+			return Byte.class;
+		}
+		else if ( short.class.equals( primitive ) ) {
+			return Short.class;
+		}
+		else if ( int.class.equals( primitive ) ) {
+			return Integer.class;
+		}
+		else if ( long.class.equals( primitive ) ) {
+			return Long.class;
+		}
+		else if ( float.class.equals( primitive ) ) {
+			return Float.class;
+		}
+		else if ( double.class.equals( primitive ) ) {
+			return Double.class;
+		}
+
+		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
 	}
 
 	private javax.persistence.EnumType getEnumType(XProperty property) {
 		javax.persistence.EnumType enumType = null;
 		if ( key ) {
 			MapKeyEnumerated enumAnn = property.getAnnotation( MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = property.getAnnotation( Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
 	}
 
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
 		LOG.debugf( "building SimpleValue for %s", propertyName );
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( mappings, table );
 
 		linkWithValue();
 
 		boolean isInSecondPass = mappings.isInSecondPass();
 		SetSimpleValueTypeSecondPass secondPass = new SetSimpleValueTypeSecondPass( this );
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			mappings.addSecondPass( secondPass );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !mappings.isInSecondPass() && referencedEntityName != null ) {
 			mappings.addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, ( Ejb3JoinColumn[] ) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
-
 		LOG.debugf( "Setting SimpleValue typeName for %s", propertyName );
 
-		String type = BinderHelper.isEmptyAnnotationValue( explicitType ) ? returnedClassName : explicitType;
-		org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
-		if ( typeDef != null ) {
-			type = typeDef.getTypeClass();
-			simpleValue.setTypeParameters( typeDef.getParameters() );
+		if ( attributeConverterDefinition != null ) {
+			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
+				throw new AnnotationException(
+						String.format(
+								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
+										"remove @Type or specify @Convert(disableConversion = true)",
+								persistentClassName,
+								propertyName
+						)
+				);
+			}
+			simpleValue.setJpaAttributeConverterDefinition( attributeConverterDefinition );
 		}
-		if ( typeParameters != null && typeParameters.size() != 0 ) {
-			//explicit type params takes precedence over type def params
-			simpleValue.setTypeParameters( typeParameters );
+		else {
+			String type = BinderHelper.isEmptyAnnotationValue( explicitType ) ? returnedClassName : explicitType;
+			org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
+			if ( typeDef != null ) {
+				type = typeDef.getTypeClass();
+				simpleValue.setTypeParameters( typeDef.getParameters() );
+			}
+			if ( typeParameters != null && typeParameters.size() != 0 ) {
+				//explicit type params takes precedence over type def params
+				simpleValue.setTypeParameters( typeParameters );
+			}
+			simpleValue.setTypeName( type );
 		}
-		simpleValue.setTypeName( type );
-		if ( persistentClassName != null ) {
+
+		if ( persistentClassName != null || attributeConverterDefinition != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 37e9467e05..a74d06a9e2 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,354 +1,507 @@
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
+
+import javax.persistence.AttributeConverter;
+import java.lang.reflect.TypeVariable;
+import java.sql.CallableStatement;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
+import org.jboss.logging.Logger;
+
+import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
+import org.hibernate.type.descriptor.ValueBinder;
+import org.hibernate.type.descriptor.ValueExtractor;
+import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
+import org.hibernate.type.descriptor.sql.BasicBinder;
+import org.hibernate.type.descriptor.sql.BasicExtractor;
+import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
+import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
+import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
+	private static final Logger log = Logger.getLogger( SimpleValue.class );
+
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List columns = new ArrayList();
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
+	private AttributeConverterDefinition jpaAttributeConverterDefinition;
+	private Type type;
+
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
-		if (typeName==null) {
-			throw new MappingException("No type name");
+		if ( type != null ) {
+			return type;
 		}
-		Type result = mappings.getTypeResolver().heuristicType(typeName, typeParameters);
-		if (result==null) {
+
+		if ( typeName == null ) {
+			throw new MappingException( "No type name" );
+		}
+
+		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
+		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
-			if(table != null){
+			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
-			if(columns!=null && columns.size()>0) {
+			if ( columns!=null && columns.size()>0 ) {
 				msg += ", for columns: " + columns;
 			}
-			throw new MappingException(msg);
+			throw new MappingException( msg );
 		}
+
 		return result;
 	}
 
+	@SuppressWarnings("unchecked")
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
-		if (typeName==null) {
-			if (className==null) {
-				throw new MappingException("you must specify types for a dynamic entity: " + propertyName);
+		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
+		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
+
+		if ( typeName != null ) {
+			// assume either (a) explicit type was specified or (b) determine was already performed
+			return;
+		}
+
+		if ( type != null ) {
+			return;
+		}
+
+		if ( jpaAttributeConverterDefinition == null ) {
+			// this is here to work like legacy.  This should change when we integrate with metamodel to
+			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
+			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
+			if ( className == null ) {
+				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
+			}
+			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
+			return;
+		}
+
+		// we had an AttributeConverter...
+
+		// todo : we should validate the number of columns present
+		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
+		//		then we can "play them against each other" in terms of determining proper typing
+		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
+
+		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
+		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
+		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
+		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
+		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
+		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
+		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
+		// and use that type-code to resolve the SqlTypeDescriptor to use.
+		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
+		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
+		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
+
+		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
+		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
+		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
+		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
+				jpaAttributeConverterDefinition.getAttributeConverter(),
+				sqlTypeDescriptor
+		);
+
+		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
+		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
+			@Override
+			public String getName() {
+				return name;
 			}
-			typeName = ReflectHelper.reflectedPropertyClass(className, propertyName).getName();
+		};
+		log.debug( "Created : " + name );
+
+		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
+	}
+
+	private Class extractType(TypeVariable typeVariable) {
+		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
+		if ( boundTypes == null || boundTypes.length != 1 ) {
+			return null;
 		}
+
+		return (Class) boundTypes[0];
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
+
+	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
+		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
+	}
+
+	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
+		private final AttributeConverter converter;
+		private final SqlTypeDescriptor delegate;
+
+		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
+			this.converter = converter;
+			this.delegate = delegate;
+		}
+
+		@Override
+		public int getSqlType() {
+			return delegate.getSqlType();
+		}
+
+		@Override
+		public boolean canBeRemapped() {
+			return delegate.canBeRemapped();
+		}
+
+		@Override
+		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
+			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
+				@Override
+				@SuppressWarnings("unchecked")
+				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
+				}
+			};
+		}
+
+		@Override
+		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
+			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
+			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+				@Override
+				@SuppressWarnings("unchecked")
+				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
+				}
+
+				@Override
+				@SuppressWarnings("unchecked")
+				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
+						throws SQLException {
+					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
+				}
+			};
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
index d3463c847b..64b6467684 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
@@ -1,60 +1,59 @@
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
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractSingleColumnStandardBasicType<T>
 		extends AbstractStandardBasicType<T>
 		implements SingleColumnType<T> {
 
 	public AbstractSingleColumnStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		super( sqlTypeDescriptor, javaTypeDescriptor );
 	}
 
+	@Override
 	public final int sqlType() {
 		return getSqlTypeDescriptor().getSqlType();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( settable[0] ) {
 			nullSafeSet( st, value, index, session );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
index 761ac20cc7..032dc07878 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
@@ -1,73 +1,107 @@
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
 package org.hibernate.type.descriptor;
 
 import java.lang.reflect.Field;
-import java.sql.Types;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * TODO : javadoc
+ * (Badly named) helper for dealing with standard JDBC types as defined by {@link java.sql.Types}
  *
  * @author Steve Ebersole
  */
 public class JdbcTypeNameMapper {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcTypeNameMapper.class.getName());
 	private static Map<Integer,String> JDBC_TYPE_MAP = buildJdbcTypeMap();
 
 	private static Map<Integer, String> buildJdbcTypeMap() {
 		HashMap<Integer, String> map = new HashMap<Integer, String>();
-		Field[] fields = Types.class.getFields();
+		Field[] fields = java.sql.Types.class.getFields();
 		if ( fields == null ) {
 			throw new HibernateException( "Unexpected problem extracting JDBC type mapping codes from java.sql.Types" );
 		}
 		for ( Field field : fields ) {
 			try {
 				final int code = field.getInt( null );
 				String old = map.put( code, field.getName() );
-                if (old != null) LOG.JavaSqlTypesMappedSameCodeMultipleTimes(code, old, field.getName());
+                if ( old != null ) {
+					LOG.JavaSqlTypesMappedSameCodeMultipleTimes( code, old, field.getName() );
+				}
 			}
 			catch ( IllegalAccessException e ) {
 				throw new HibernateException( "Unable to access JDBC type mapping [" + field.getName() + "]", e );
 			}
 		}
 		return Collections.unmodifiableMap( map );
 	}
 
-	public static String getTypeName(Integer code) {
-		String name = JDBC_TYPE_MAP.get( code );
+	/**
+	 * Determine whether the given JDBC type code represents a standard JDBC type ("standard" being those defined on
+	 * {@link java.sql.Types}).
+	 *
+	 * NOTE : {@link java.sql.Types#OTHER} is also "filtered out" as being non-standard.
+	 *
+	 * @param typeCode The JDBC type code to check
+	 *
+	 * @return {@code true} to indicate the type code is a standard type code; {@code false} otherwise.
+	 */
+	public static boolean isStandardTypeCode(int typeCode) {
+		return isStandardTypeCode( Integer.valueOf( typeCode ) );
+	}
+
+	/**
+	 * Same as call to {@link #isStandardTypeCode(int)}
+	 *
+	 * @see #isStandardTypeCode(int)
+	 */
+	public static boolean isStandardTypeCode(Integer typeCode) {
+		return JDBC_TYPE_MAP.containsKey( typeCode );
+	}
+
+	/**
+	 * Get the type name as in the static field names defined on {@link java.sql.Types}.  If a type code is not
+	 * recognized, it is reported as {@code UNKNOWN(?)} where '?' is replace with the given type code.
+	 *
+	 * Intended as useful for logging purposes...
+	 *
+	 * @param typeCode The type code to find the name for.
+	 *
+	 * @return The type name.
+	 */
+	public static String getTypeName(Integer typeCode) {
+		String name = JDBC_TYPE_MAP.get( typeCode );
 		if ( name == null ) {
-			return "UNKNOWN(" + code + ")";
+			return "UNKNOWN(" + typeCode + ")";
 		}
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
index a88c4a004c..8a92be9e6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
@@ -1,123 +1,113 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.Serializable;
 import java.util.Comparator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.compare.ComparableComparator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 
 /**
  * Abstract adapter for Java type descriptors.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractTypeDescriptor<T> implements JavaTypeDescriptor<T>, Serializable {
 	private final Class<T> type;
 	private final MutabilityPlan<T> mutabilityPlan;
 	private final Comparator<T> comparator;
 
 	/**
 	 * Initialize a type descriptor for the given type.  Assumed immutable.
 	 *
 	 * @param type The Java type.
 	 *
 	 * @see #AbstractTypeDescriptor(Class, MutabilityPlan)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	protected AbstractTypeDescriptor(Class<T> type) {
 		this( type, (MutabilityPlan<T>) ImmutableMutabilityPlan.INSTANCE );
 	}
 
 	/**
 	 * Initialize a type descriptor for the given type.  Assumed immutable.
 	 *
 	 * @param type The Java type.
 	 * @param mutabilityPlan The plan for handling mutability aspects of the java type.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	protected AbstractTypeDescriptor(Class<T> type, MutabilityPlan<T> mutabilityPlan) {
 		this.type = type;
 		this.mutabilityPlan = mutabilityPlan;
 		this.comparator = Comparable.class.isAssignableFrom( type )
 				? (Comparator<T>) ComparableComparator.INSTANCE
 				: null;
+
+		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public MutabilityPlan<T> getMutabilityPlan() {
 		return mutabilityPlan;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<T> getJavaTypeClass() {
 		return type;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int extractHashCode(T value) {
 		return value.hashCode();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean areEqual(T one, T another) {
 		return EqualsHelper.equals( one, another );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Comparator<T> getComparator() {
 		return comparator;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String extractLoggableRepresentation(T value) {
 		return (value == null) ? "null" : value.toString();
 	}
 
 	protected HibernateException unknownUnwrap(Class conversionType) {
 		throw new HibernateException(
 				"Unknown unwrap conversion requested: " + type.getName() + " to " + conversionType.getName()
 		);
 	}
 
 	protected HibernateException unknownWrap(Class conversionType) {
 		throw new HibernateException(
 				"Unknown wrap conversion requested: " + conversionType.getName() + " to " + type.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
index 39fcac2023..450d7ddffe 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
@@ -1,149 +1,149 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.BinaryStream;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@code Byte[]} handling.
  *
  * @author Steve Ebersole
  */
 public class ByteArrayTypeDescriptor extends AbstractTypeDescriptor<Byte[]> {
 	public static final ByteArrayTypeDescriptor INSTANCE = new ByteArrayTypeDescriptor();
 
 	@SuppressWarnings({ "unchecked" })
 	public ByteArrayTypeDescriptor() {
 		super( Byte[].class, ArrayMutabilityPlan.INSTANCE );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public String toString(Byte[] bytes) {
 		final StringBuilder buf = new StringBuilder();
 		for ( Byte aByte : bytes ) {
 			final String hexStr = Integer.toHexString( aByte.byteValue() - Byte.MIN_VALUE );
 			if ( hexStr.length() == 1 ) {
 				buf.append( '0' );
 			}
 			buf.append( hexStr );
 		}
 		return buf.toString();
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public Byte[] fromString(String string) {
 		if ( string == null ) {
 			return null;
 		}
 		if ( string.length() % 2 != 0 ) {
 			throw new IllegalArgumentException( "The string is not a valid string representation of a binary content." );
 		}
 		Byte[] bytes = new Byte[string.length() / 2];
 		for ( int i = 0; i < bytes.length; i++ ) {
 			final String hexStr = string.substring( i * 2, (i + 1) * 2 );
 			bytes[i] = Byte.valueOf( (byte) (Integer.parseInt(hexStr, 16) + Byte.MIN_VALUE) );
 		}
 		return bytes;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Byte[] value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Byte[].class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( byte[].class.isAssignableFrom( type ) ) {
 			return (X) unwrapBytes( value );
 		}
 		if ( InputStream.class.isAssignableFrom( type ) ) {
 			return (X) new ByteArrayInputStream( unwrapBytes( value ) );
 		}
 		if ( BinaryStream.class.isAssignableFrom( type ) ) {
 			return (X) new BinaryStreamImpl( unwrapBytes( value ) );
 		}
 		if ( Blob.class.isAssignableFrom( type ) ) {
 			return (X) options.getLobCreator().createBlob( unwrapBytes( value ) );
 		}
 
 		throw unknownUnwrap( type );
 	}
 
 	public <X> Byte[] wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Byte[].class.isInstance( value ) ) {
 			return (Byte[]) value;
 		}
 		if ( byte[].class.isInstance( value ) ) {
 			return wrapBytes( (byte[]) value );
 		}
 		if ( InputStream.class.isInstance( value ) ) {
 			return wrapBytes( DataHelper.extractBytes( (InputStream) value ) );
 		}
 		if ( Blob.class.isInstance( value ) || DataHelper.isNClob( value.getClass() ) ) {
 			try {
 				return wrapBytes( DataHelper.extractBytes( ( (Blob) value ).getBinaryStream() ) );
 			}
 			catch ( SQLException e ) {
 				throw new HibernateException( "Unable to access lob stream", e );
 			}
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	private Byte[] wrapBytes(byte[] bytes) {
 		if ( bytes == null ) {
 			return null;
 		}
 		final Byte[] result = new Byte[bytes.length];
 		for ( int i = 0; i < bytes.length; i++ ) {
 			result[i] = Byte.valueOf( bytes[i] );
 		}
 		return result;
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	private byte[] unwrapBytes(Byte[] bytes) {
 		if ( bytes == null ) {
 			return null;
 		}
 		final byte[] result = new byte[bytes.length];
 		for ( int i = 0; i < bytes.length; i++ ) {
 			result[i] = bytes[i].byteValue();
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
index fae1cc5d32..8d77e0cc04 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
@@ -1,135 +1,135 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.Reader;
 import java.io.StringReader;
 import java.sql.Clob;
 import java.util.Arrays;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@code Character[]} handling.
  *
  * @author Steve Ebersole
  */
 public class CharacterArrayTypeDescriptor extends AbstractTypeDescriptor<Character[]> {
 	public static final CharacterArrayTypeDescriptor INSTANCE = new CharacterArrayTypeDescriptor();
 
 	@SuppressWarnings({ "unchecked" })
 	public CharacterArrayTypeDescriptor() {
 		super( Character[].class, ArrayMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Character[] value) {
 		return new String( unwrapChars( value ) );
 	}
 
 	public Character[] fromString(String string) {
 		return wrapChars( string.toCharArray() );
 	}
 
 	@Override
 	public boolean areEqual(Character[] one, Character[] another) {
 		return one == another
 				|| ( one != null && another != null && Arrays.equals( one, another ) );
 	}
 
 	@Override
 	public int extractHashCode(Character[] chars) {
 		int hashCode = 1;
 		for ( Character aChar : chars ) {
 			hashCode = 31 * hashCode + aChar;
 		}
 		return hashCode;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Character[] value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Character[].class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( String.class.isAssignableFrom( type ) ) {
 			return (X) new String( unwrapChars( value ) );
 		}
 		if ( Clob.class.isAssignableFrom( type ) ) {
 			return (X) options.getLobCreator().createClob( new String( unwrapChars( value ) ) );
 		}
 		if ( Reader.class.isAssignableFrom( type ) ) {
 			return (X) new StringReader( new String( unwrapChars( value ) ) );
 		}
 		if ( CharacterStream.class.isAssignableFrom( type ) ) {
 			return (X) new CharacterStreamImpl( new String( unwrapChars( value ) ) );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	public <X> Character[] wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Character[].class.isInstance( value ) ) {
 			return (Character[]) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return wrapChars( ( (String) value ).toCharArray() );
 		}
 		if ( Clob.class.isInstance( value ) ) {
 			return wrapChars( DataHelper.extractString( ( (Clob) value ) ).toCharArray() );
 		}
 		if ( Reader.class.isInstance( value ) ) {
 			return wrapChars( DataHelper.extractString( (Reader) value ).toCharArray() );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	private Character[] wrapChars(char[] chars) {
 		if ( chars == null ) {
 			return null;
 		}
 		final Character[] result = new Character[chars.length];
 		for ( int i = 0; i < chars.length; i++ ) {
 			result[i] = Character.valueOf( chars[i] );
 		}
 		return result;
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	private char[] unwrapChars(Character[] chars) {
 		if ( chars == null ) {
 			return null;
 		}
 		final char[] result = new char[chars.length];
 		for ( int i = 0; i < chars.length; i++ ) {
 			result[i] = chars[i].charValue();
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
index c357932361..0c0ab2ec02 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
@@ -1,84 +1,84 @@
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
 package org.hibernate.type.descriptor.java;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Class} handling.
  *
  * @author Steve Ebersole
  */
 public class ClassTypeDescriptor extends AbstractTypeDescriptor<Class> {
 	public static final ClassTypeDescriptor INSTANCE = new ClassTypeDescriptor();
 
 	public ClassTypeDescriptor() {
 		super( Class.class );
 	}
 
 	public String toString(Class value) {
 		return value.getName();
 	}
 
 	public Class fromString(String string) {
 		if ( string == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.classForName( string );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Unable to locate named class " + string );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Class value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Class.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( String.class.isAssignableFrom( type ) ) {
 			return (X) toString( value );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	public <X> Class wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Class.class.isInstance( value ) ) {
 			return (Class) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return fromString( (String)value );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
index 8566e3ae41..0b849a71ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
@@ -1,70 +1,73 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.util.Currency;
 
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Currency} handling.
  *
  * @author Steve Ebersole
  */
 public class CurrencyTypeDescriptor extends AbstractTypeDescriptor<Currency> {
 	public static final CurrencyTypeDescriptor INSTANCE = new CurrencyTypeDescriptor();
 
 	public CurrencyTypeDescriptor() {
 		super( Currency.class );
 	}
 
+	@Override
 	public String toString(Currency value) {
 		return value.getCurrencyCode();
 	}
 
+	@Override
 	public Currency fromString(String string) {
 		return Currency.getInstance( string );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Currency value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( String.class.isAssignableFrom( type ) ) {
 			return (X) value.getCurrencyCode();
 		}
 		throw unknownUnwrap( type );
 	}
 
+	@Override
 	public <X> Currency wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return Currency.getInstance( (String) value );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
index 87a006212d..1882c725cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
@@ -1,144 +1,144 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Date} handling.
  *
  * @author Steve Ebersole
  */
 public class DateTypeDescriptor extends AbstractTypeDescriptor<Date> {
 	public static final DateTypeDescriptor INSTANCE = new DateTypeDescriptor();
 	public static final String DATE_FORMAT = "dd MMMM yyyy";
 
 	public static class DateMutabilityPlan extends MutableMutabilityPlan<Date> {
 		public static final DateMutabilityPlan INSTANCE = new DateMutabilityPlan();
 
 		public Date deepCopyNotNull(Date value) {
 			return new Date( value.getTime() );
 		}
 	}
 
 	public DateTypeDescriptor() {
 		super( Date.class, DateMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Date value) {
 		return new SimpleDateFormat( DATE_FORMAT ).format( value );
 	}
 
 	public Date fromString(String string) {
 		try {
 			return new SimpleDateFormat(DATE_FORMAT).parse( string );
 		}
 		catch ( ParseException pe) {
 			throw new HibernateException( "could not parse date string" + string, pe );
 		}
 	}
 
 	@Override
 	public boolean areEqual(Date one, Date another) {
 		if ( one == another) {
 			return true;
 		}
 		if ( one == null || another == null ) {
 			return false;
 		}
 
 		return one.getTime() == another.getTime();
 	}
 
 	@Override
 	public int extractHashCode(Date value) {
 		Calendar calendar = java.util.Calendar.getInstance();
 		calendar.setTime( value );
 		return CalendarTypeDescriptor.INSTANCE.extractHashCode( calendar );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
 			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
 					? ( java.sql.Date ) value
 					: new java.sql.Date( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
 			final java.sql.Time rtn = java.sql.Time.class.isInstance( value )
 					? ( java.sql.Time ) value
 					: new java.sql.Time( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Timestamp.class.isAssignableFrom( type ) ) {
 			final java.sql.Timestamp rtn = java.sql.Timestamp.class.isInstance( value )
 					? ( java.sql.Timestamp ) value
 					: new java.sql.Timestamp( value.getTime() );
 			return (X) rtn;
 		}
 		if ( Date.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( Calendar.class.isAssignableFrom( type ) ) {
 			final GregorianCalendar cal = new GregorianCalendar();
 			cal.setTimeInMillis( value.getTime() );
 			return (X) cal;
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.getTime() );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public <X> Date wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Date.class.isInstance( value ) ) {
 			return (Date) value;
 		}
 
 		if ( Long.class.isInstance( value ) ) {
 			return new Date( ( (Long) value ).longValue() );
 		}
 
 		if ( Calendar.class.isInstance( value ) ) {
 			return new Date( ( (Calendar) value ).getTimeInMillis() );
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
index 8b9a7ab7e8..6c7faae29e 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
@@ -1,63 +1,56 @@
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
 package org.hibernate.type.descriptor.java;
+
 import java.io.Serializable;
 
 /**
  * Mutability plan for immutable objects
  *
  * @author Steve Ebersole
  */
 public class ImmutableMutabilityPlan<T> implements MutabilityPlan<T> {
 	public static final ImmutableMutabilityPlan INSTANCE = new ImmutableMutabilityPlan();
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public T deepCopy(T value) {
 		return value;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Serializable disassemble(T value) {
 		return (Serializable) value;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public T assemble(Serializable cached) {
 		return (T) cached;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
index 9221550b91..9fc5316edc 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
@@ -1,38 +1,41 @@
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
 package org.hibernate.type.descriptor.java;
+
 import java.util.Comparator;
 
 /**
- * TODO : javadoc
+ * Comparator for things that cannot be compared (in a way we know about).
  *
  * @author Steve Ebersole
  */
 public class IncomparableComparator implements Comparator {
 	public static final IncomparableComparator INSTANCE = new IncomparableComparator();
 
+	@Override
+	@SuppressWarnings("ComparatorMethodParameterNotUsed")
 	public int compare(Object o1, Object o2) {
 		return 0;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java
new file mode 100644
index 0000000000..48e94a1dca
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java
@@ -0,0 +1,112 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.java;
+
+import java.io.Serializable;
+import java.util.Comparator;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.hibernate.HibernateException;
+import org.hibernate.type.descriptor.WrapperOptions;
+
+/**
+ * Basically a map from {@link Class} -> {@link JavaTypeDescriptor}
+ *
+ * @author Steve Ebersole
+ */
+public class JavaTypeDescriptorRegistry {
+	public static final JavaTypeDescriptorRegistry INSTANCE = new JavaTypeDescriptorRegistry();
+
+	private ConcurrentHashMap<Class,JavaTypeDescriptor> descriptorsByClass = new ConcurrentHashMap<Class, JavaTypeDescriptor>();
+
+	/**
+	 * Adds the given descriptor to this registry
+	 *
+	 * @param descriptor The descriptor to add.
+	 */
+	public void addDescriptor(JavaTypeDescriptor descriptor) {
+		descriptorsByClass.put( descriptor.getJavaTypeClass(), descriptor );
+	}
+
+	@SuppressWarnings("unchecked")
+	public <T> JavaTypeDescriptor<T> getDescriptor(Class<T> cls) {
+		if ( cls == null ) {
+			throw new IllegalArgumentException( "Class passed to locate Java type descriptor cannot be null" );
+		}
+
+		JavaTypeDescriptor<T> descriptor = descriptorsByClass.get( cls );
+		if ( descriptor != null ) {
+			return descriptor;
+		}
+
+		// find the first "assignable" match
+		for ( Map.Entry<Class,JavaTypeDescriptor> entry : descriptorsByClass.entrySet() ) {
+			if ( cls.isAssignableFrom( entry.getKey() ) ) {
+				return entry.getValue();
+			}
+		}
+
+		// we could not find one; warn the user (as stuff is likely to break later) and create a fallback instance...
+		if ( Serializable.class.isAssignableFrom( cls ) ) {
+			return new SerializableTypeDescriptor( cls );
+		}
+		else {
+			return new FallbackJavaTypeDescriptor<T>( cls );
+		}
+	}
+
+	public static class FallbackJavaTypeDescriptor<T> extends AbstractTypeDescriptor<T> {
+
+		@SuppressWarnings("unchecked")
+		protected FallbackJavaTypeDescriptor(Class<T> type) {
+			// MutableMutabilityPlan would be the "safest" option, but we do not necessarily know how to deepCopy etc...
+			super( type, ImmutableMutabilityPlan.INSTANCE );
+		}
+
+		@Override
+		public String toString(T value) {
+			return value == null ? "<null>" : value.toString();
+		}
+
+		@Override
+		public T fromString(String string) {
+			throw new HibernateException(
+					"Not known how to convert String to given type [" + getJavaTypeClass().getName() + "]"
+			);
+		}
+
+		@Override
+		@SuppressWarnings("unchecked")
+		public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
+			return (X) value;
+		}
+
+		@Override
+		@SuppressWarnings("unchecked")
+		public <X> T wrap(X value, WrapperOptions options) {
+			return (T) value;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
index 4e96131ce4..bc52ffba83 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
@@ -1,165 +1,165 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link java.sql.Date} handling.
  *
  * @author Steve Ebersole
  */
 public class JdbcDateTypeDescriptor extends AbstractTypeDescriptor<Date> {
 	public static final JdbcDateTypeDescriptor INSTANCE = new JdbcDateTypeDescriptor();
 	public static final String DATE_FORMAT = "dd MMMM yyyy";
 
 	public static class DateMutabilityPlan extends MutableMutabilityPlan<Date> {
 		public static final DateMutabilityPlan INSTANCE = new DateMutabilityPlan();
 
 		public Date deepCopyNotNull(Date value) {
 			return java.sql.Date.class.isInstance( value )
 					? new java.sql.Date( value.getTime() )
 					: new Date( value.getTime() );
 		}
 	}
 
 	public JdbcDateTypeDescriptor() {
 		super( Date.class, DateMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Date value) {
 		return new SimpleDateFormat( DATE_FORMAT ).format( value );
 	}
 
 	public Date fromString(String string) {
 		try {
 			return new Date( new SimpleDateFormat(DATE_FORMAT).parse( string ).getTime() );
 		}
 		catch ( ParseException pe) {
 			throw new HibernateException( "could not parse date string" + string, pe );
 		}
 	}
 
 	@Override
 	public boolean areEqual(Date one, Date another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null ) {
 			return false;
 		}
 
 		if ( one.getTime() == another.getTime() ) {
 			return true;
 		}
 
 		Calendar calendar1 = Calendar.getInstance();
 		Calendar calendar2 = Calendar.getInstance();
 		calendar1.setTime( one );
 		calendar2.setTime( another );
 
 		return calendar1.get( Calendar.MONTH ) == calendar2.get( Calendar.MONTH )
 				&& calendar1.get( Calendar.DAY_OF_MONTH ) == calendar2.get( Calendar.DAY_OF_MONTH )
 				&& calendar1.get( Calendar.YEAR ) == calendar2.get( Calendar.YEAR );
 	}
 
 	@Override
 	public int extractHashCode(Date value) {
 		Calendar calendar = Calendar.getInstance();
 		calendar.setTime( value );
 		int hashCode = 1;
 		hashCode = 31 * hashCode + calendar.get( Calendar.MONTH );
 		hashCode = 31 * hashCode + calendar.get( Calendar.DAY_OF_MONTH );
 		hashCode = 31 * hashCode + calendar.get( Calendar.YEAR );
 		return hashCode;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
 			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
 					? ( java.sql.Date ) value
 					: new java.sql.Date( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
 			final java.sql.Time rtn = java.sql.Time.class.isInstance( value )
 					? ( java.sql.Time ) value
 					: new java.sql.Time( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Timestamp.class.isAssignableFrom( type ) ) {
 			final java.sql.Timestamp rtn = java.sql.Timestamp.class.isInstance( value )
 					? ( java.sql.Timestamp ) value
 					: new java.sql.Timestamp( value.getTime() );
 			return (X) rtn;
 		}
 		if ( Date.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( Calendar.class.isAssignableFrom( type ) ) {
 			final GregorianCalendar cal = new GregorianCalendar();
 			cal.setTimeInMillis( value.getTime() );
 			return (X) cal;
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.getTime() );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public <X> Date wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Date.class.isInstance( value ) ) {
 			return (Date) value;
 		}
 
 		if ( Long.class.isInstance( value ) ) {
 			return new java.sql.Date( ( (Long) value ).longValue() );
 		}
 
 		if ( Calendar.class.isInstance( value ) ) {
 			return new java.sql.Date( ( (Calendar) value ).getTimeInMillis() );
 		}
 
 		if ( java.util.Date.class.isInstance( value ) ) {
 			return new java.sql.Date( ( (java.util.Date) value ).getTime() );
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
index aa175902cd..d68fd029a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
@@ -1,168 +1,168 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.sql.Time;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Time} handling.
  *
  * @author Steve Ebersole
  */
 public class JdbcTimeTypeDescriptor extends AbstractTypeDescriptor<Date> {
 	public static final JdbcTimeTypeDescriptor INSTANCE = new JdbcTimeTypeDescriptor();
 	public static final String TIME_FORMAT = "HH:mm:ss";
 
 	public static class TimeMutabilityPlan extends MutableMutabilityPlan<Date> {
 		public static final TimeMutabilityPlan INSTANCE = new TimeMutabilityPlan();
 
 		public Date deepCopyNotNull(Date value) {
 			return Time.class.isInstance( value )
 					? new Time( value.getTime() )
 					: new Date( value.getTime() );
 		}
 	}
 
 	public JdbcTimeTypeDescriptor() {
 		super( Date.class, TimeMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Date value) {
 		return new SimpleDateFormat( TIME_FORMAT ).format( value );
 	}
 
 	public java.util.Date fromString(String string) {
 		try {
 			return new Time( new SimpleDateFormat( TIME_FORMAT ).parse( string ).getTime() );
 		}
 		catch ( ParseException pe ) {
 			throw new HibernateException( "could not parse time string" + string, pe );
 		}
 	}
 
 	@Override
 	public int extractHashCode(Date value) {
 		Calendar calendar = Calendar.getInstance();
 		calendar.setTime( value );
 		int hashCode = 1;
 		hashCode = 31 * hashCode + calendar.get( Calendar.HOUR_OF_DAY );
 		hashCode = 31 * hashCode + calendar.get( Calendar.MINUTE );
 		hashCode = 31 * hashCode + calendar.get( Calendar.SECOND );
 		hashCode = 31 * hashCode + calendar.get( Calendar.MILLISECOND );
 		return hashCode;
 	}
 
 	@Override
 	public boolean areEqual(Date one, Date another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null ) {
 			return false;
 		}
 
 		if ( one.getTime() == another.getTime() ) {
 			return true;
 		}
 
 		Calendar calendar1 = Calendar.getInstance();
 		Calendar calendar2 = Calendar.getInstance();
 		calendar1.setTime( one );
 		calendar2.setTime( another );
 
 		return calendar1.get( Calendar.HOUR_OF_DAY ) == calendar2.get( Calendar.HOUR_OF_DAY )
 				&& calendar1.get( Calendar.MINUTE ) == calendar2.get( Calendar.MINUTE )
 				&& calendar1.get( Calendar.SECOND ) == calendar2.get( Calendar.SECOND )
 				&& calendar1.get( Calendar.MILLISECOND ) == calendar2.get( Calendar.MILLISECOND );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Time.class.isAssignableFrom( type ) ) {
 			final Time rtn = Time.class.isInstance( value )
 					? ( Time ) value
 					: new Time( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
 			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
 					? ( java.sql.Date ) value
 					: new java.sql.Date( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Timestamp.class.isAssignableFrom( type ) ) {
 			final java.sql.Timestamp rtn = java.sql.Timestamp.class.isInstance( value )
 					? ( java.sql.Timestamp ) value
 					: new java.sql.Timestamp( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.util.Date.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( Calendar.class.isAssignableFrom( type ) ) {
 			final GregorianCalendar cal = new GregorianCalendar();
 			cal.setTimeInMillis( value.getTime() );
 			return (X) cal;
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.getTime() );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public <X> Date wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Time.class.isInstance( value ) ) {
 			return (Time) value;
 		}
 
 		if ( Long.class.isInstance( value ) ) {
 			return new Time( ( (Long) value ).longValue() );
 		}
 
 		if ( Calendar.class.isInstance( value ) ) {
 			return new Time( ( (Calendar) value ).getTimeInMillis() );
 		}
 
 		if ( Date.class.isInstance( value ) ) {
 			return (Date) value;
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
index 92d32a58ed..bdb8732b2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
@@ -1,178 +1,177 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.sql.Timestamp;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Timestamp} handling.
  *
  * @author Steve Ebersole
  */
 public class JdbcTimestampTypeDescriptor extends AbstractTypeDescriptor<Date> {
 	public static final JdbcTimestampTypeDescriptor INSTANCE = new JdbcTimestampTypeDescriptor();
 	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
 
 	public static class TimestampMutabilityPlan extends MutableMutabilityPlan<Date> {
 		public static final TimestampMutabilityPlan INSTANCE = new TimestampMutabilityPlan();
 
 		public Date deepCopyNotNull(Date value) {
 			if ( value instanceof Timestamp ) {
 				Timestamp orig = (Timestamp) value;
 				Timestamp ts = new Timestamp( orig.getTime() );
 				ts.setNanos( orig.getNanos() );
 				return ts;
 			}
 			else {
-				Date orig = value;
-				return new Date( orig.getTime() );
+				return new Date( value.getTime() );
 			}
 		}
 	}
 
 	public JdbcTimestampTypeDescriptor() {
 		super( Date.class, TimestampMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Date value) {
 		return new SimpleDateFormat( TIMESTAMP_FORMAT ).format( value );
 	}
 
 	public Date fromString(String string) {
 		try {
 			return new Timestamp( new SimpleDateFormat( TIMESTAMP_FORMAT ).parse( string ).getTime() );
 		}
 		catch ( ParseException pe) {
 			throw new HibernateException( "could not parse timestamp string" + string, pe );
 		}
 	}
 
 	@Override
 	public boolean areEqual(Date one, Date another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null) {
 			return false;
 		}
 
 		long t1 = one.getTime();
 		long t2 = another.getTime();
 
 		boolean oneIsTimestamp = Timestamp.class.isInstance( one );
 		boolean anotherIsTimestamp = Timestamp.class.isInstance( another );
 
 		int n1 = oneIsTimestamp ? ( (Timestamp) one ).getNanos() : 0;
 		int n2 = anotherIsTimestamp ? ( (Timestamp) another ).getNanos() : 0;
 
 		if ( t1 != t2 ) {
 			return false;
 		}
 
 		if ( oneIsTimestamp && anotherIsTimestamp ) {
 			// both are Timestamps
 			int nn1 = n1 % 1000000;
 			int nn2 = n2 % 1000000;
 			return nn1 == nn2;
 		}
 		else {
 			// at least one is a plain old Date
 			return true;
 		}
 	}
 
 	@Override
 	public int extractHashCode(Date value) {
 		return Long.valueOf( value.getTime() / 1000 ).hashCode();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Timestamp.class.isAssignableFrom( type ) ) {
 			final Timestamp rtn = Timestamp.class.isInstance( value )
 					? ( Timestamp ) value
 					: new Timestamp( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
 			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
 					? ( java.sql.Date ) value
 					: new java.sql.Date( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
 			final java.sql.Time rtn = java.sql.Time.class.isInstance( value )
 					? ( java.sql.Time ) value
 					: new java.sql.Time( value.getTime() );
 			return (X) rtn;
 		}
 		if ( Date.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( Calendar.class.isAssignableFrom( type ) ) {
 			final GregorianCalendar cal = new GregorianCalendar();
 			cal.setTimeInMillis( value.getTime() );
 			return (X) cal;
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.getTime() );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public <X> Date wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Timestamp.class.isInstance( value ) ) {
 			return (Timestamp) value;
 		}
 
 		if ( Long.class.isInstance( value ) ) {
 			return new Timestamp( ( (Long) value ).longValue() );
 		}
 
 		if ( Calendar.class.isInstance( value ) ) {
 			return new Timestamp( ( (Calendar) value ).getTimeInMillis() );
 		}
 
 		if ( Date.class.isInstance( value ) ) {
 			return (Date) value;
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
index 4a3f02772d..646662ac00 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
@@ -1,95 +1,95 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.util.Comparator;
 import java.util.Locale;
 import java.util.StringTokenizer;
 
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@link Locale} handling.
  *
  * @author Steve Ebersole
  */
 public class LocaleTypeDescriptor extends AbstractTypeDescriptor<Locale> {
 	public static final LocaleTypeDescriptor INSTANCE = new LocaleTypeDescriptor();
 
 	public static class LocaleComparator implements Comparator<Locale> {
 		public static final LocaleComparator INSTANCE = new LocaleComparator();
 
 		public int compare(Locale o1, Locale o2) {
 			return o1.toString().compareTo( o2.toString() );
 		}
 	}
 
 	public LocaleTypeDescriptor() {
 		super( Locale.class );
 	}
 
 	@Override
 	public Comparator<Locale> getComparator() {
 		return LocaleComparator.INSTANCE;
 	}
 
 	public String toString(Locale value) {
 		return value.toString();
 	}
 
 	public Locale fromString(String string) {
 		StringTokenizer tokens = new StringTokenizer( string, "_" );
 		String language = tokens.hasMoreTokens() ? tokens.nextToken() : "";
 		String country = tokens.hasMoreTokens() ? tokens.nextToken() : "";
 		// Need to account for allowable '_' within the variant
 		String variant = "";
 		String sep = "";
 		while ( tokens.hasMoreTokens() ) {
 			variant += sep + tokens.nextToken();
 			sep = "_";
 		}
 		return new Locale( language, country, variant );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Locale value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( String.class.isAssignableFrom( type ) ) {
 			return (X) value.toString();
 		}
 		throw unknownUnwrap( type );
 	}
 
 	public <X> Locale wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return fromString( (String) value );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
index abd9aeac01..2224d40a19 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
@@ -1,72 +1,74 @@
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
 package org.hibernate.type.descriptor.java;
+
 import java.io.Serializable;
 
 /**
- * TODO : javadoc
+ * Describes the mutability aspects of a Java type.  The term mutability refers to the fact that generally speaking
+ * the aspects described by this contract are defined by whether the Java type's internal state is mutable or not.
  *
  * @author Steve Ebersole
  */
 public interface MutabilityPlan<T> extends Serializable {
 	/**
 	 * Can the internal state of instances of <tt>T</tt> be changed?
 	 *
 	 * @return True if the internal state can be changed; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Return a deep copy of the value.
 	 *
 	 * @param value The value to deep copy
 	 *
 	 * @return The deep copy.
 	 */
 	public T deepCopy(T value);
 
 	/**
 	 * Return a "disassembled" representation of the value.  This is used to push values onto the
 	 * second level cache.  Compliment to {@link #assemble}
 	 *
 	 * @param value The value to disassemble
 	 *
 	 * @return The disassembled value.
 	 *
 	 * @see #assemble
 	 */
 	public Serializable disassemble(T value);
 
 	/**
 	 * Assemble a previously {@linkplain #disassemble disassembled} value.  This is used when pulling values from the
 	 * second level cache.  Compliment to {@link #disassemble}
 	 *
 	 * @param cached The disassembled state
 	 *
 	 * @return The re-assembled value.
 	 *
 	 * @see #disassemble
 	 */
 	public T assemble(Serializable cached);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
index 4365250dc9..d405c71cd4 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
@@ -1,61 +1,56 @@
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
 package org.hibernate.type.descriptor.java;
+
 import java.io.Serializable;
 
 /**
  * Mutability plan for mutable objects
  *
  * @author Steve Ebersole
  */
 public abstract class MutableMutabilityPlan<T> implements MutabilityPlan<T> {
-	
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isMutable() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Serializable disassemble(T value) {
 		return (Serializable) deepCopy( value );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public T assemble(Serializable cached) {
 		return (T) deepCopy( (T) cached );
 	}
 
+	@Override
 	public final T deepCopy(T value) {
 		return value == null ? null : deepCopyNotNull( value );
 	}
 
 	protected abstract T deepCopyNotNull(T value);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
index 21e54cd0df..33bbbc8b5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
@@ -1,111 +1,111 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.Reader;
 import java.io.StringReader;
 import java.sql.Clob;
 import java.util.Arrays;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for {@code char[]} handling.
  *
  * @author Steve Ebersole
  */
 public class PrimitiveCharacterArrayTypeDescriptor extends AbstractTypeDescriptor<char[]> {
 	public static final PrimitiveCharacterArrayTypeDescriptor INSTANCE = new PrimitiveCharacterArrayTypeDescriptor();
 
 	@SuppressWarnings({ "unchecked" })
 	protected PrimitiveCharacterArrayTypeDescriptor() {
 		super( char[].class, ArrayMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(char[] value) {
 		return new String( value );
 	}
 
 	public char[] fromString(String string) {
 		return string.toCharArray();
 	}
 
 	@Override
 	public boolean areEqual(char[] one, char[] another) {
 		return one == another
 				|| ( one != null && another != null && Arrays.equals( one, another ) );
 	}
 
 	@Override
 	public int extractHashCode(char[] chars) {
 		int hashCode = 1;
 		for ( char aChar : chars ) {
 			hashCode = 31 * hashCode + aChar;
 		}
 		return hashCode;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(char[] value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( char[].class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( String.class.isAssignableFrom( type ) ) {
 			return (X) new String( value );
 		}
 		if ( Clob.class.isAssignableFrom( type ) ) {
 			return (X) options.getLobCreator().createClob( new String( value ) );
 		}
 		if ( Reader.class.isAssignableFrom( type ) ) {
 			return (X) new StringReader( new String( value ) );
 		}
 		if ( CharacterStream.class.isAssignableFrom( type ) ) {
 			return (X) new CharacterStreamImpl( new String( value ) );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	public <X> char[] wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( char[].class.isInstance( value ) ) {
 			return (char[]) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return ( (String) value ).toCharArray();
 		}
 		if ( Clob.class.isInstance( value ) ) {
 			return DataHelper.extractString( ( (Clob) value ) ).toCharArray();
 		}
 		if ( Reader.class.isInstance( value ) ) {
 			return DataHelper.extractString( ( (Reader) value ) ).toCharArray();
 		}
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
index 346798c0c5..47a6a40e88 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
@@ -1,133 +1,133 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.io.Serializable;
 
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.type.descriptor.BinaryStream;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
- * TODO : javadoc
+ * Descriptor for general {@link Serializable} handling.
  *
  * @author Steve Ebersole
  */
 public class SerializableTypeDescriptor<T extends Serializable> extends AbstractTypeDescriptor<T> {
 
 	// unfortunately the param types cannot be the same so use something other than 'T' here to make that obvious
 	public static class SerializableMutabilityPlan<S extends Serializable> extends MutableMutabilityPlan<S> {
 		private final Class<S> type;
 
 		public static final SerializableMutabilityPlan<Serializable> INSTANCE
 				= new SerializableMutabilityPlan<Serializable>( Serializable.class );
 
 		public SerializableMutabilityPlan(Class<S> type) {
 			this.type = type;
 		}
 
 		@Override
         @SuppressWarnings({ "unchecked" })
 		public S deepCopyNotNull(S value) {
 			return (S) SerializationHelper.clone( value );
 		}
 
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public SerializableTypeDescriptor(Class<T> type) {
 		super(
 				type,
 				Serializable.class.equals( type )
 						? (MutabilityPlan<T>) SerializableMutabilityPlan.INSTANCE
 						: new SerializableMutabilityPlan<T>( type )
 		);
 	}
 
 	public String toString(T value) {
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.toString( toBytes( value ) );
 	}
 
 	public T fromString(String string) {
 		return fromBytes( PrimitiveByteArrayTypeDescriptor.INSTANCE.fromString( string ) );
 	}
 
 	@Override
 	public boolean areEqual(T one, T another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null ) {
 			return false;
 		}
 		return one.equals( another )
 				|| PrimitiveByteArrayTypeDescriptor.INSTANCE.areEqual( toBytes( one ), toBytes( another ) );
 	}
 
 	@Override
 	public int extractHashCode(T value) {
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.extractHashCode( toBytes( value ) );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( byte[].class.isAssignableFrom( type ) ) {
 			return (X) toBytes( value );
 		}
 		if ( InputStream.class.isAssignableFrom( type ) ) {
 			return (X) new ByteArrayInputStream( toBytes( value ) );
 		}
 		if ( BinaryStream.class.isAssignableFrom( type ) ) {
 			return (X) new BinaryStreamImpl( toBytes( value ) );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	public <X> T wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( byte[].class.isInstance( value ) ) {
 			return fromBytes( (byte[]) value );
 		}
 		if ( InputStream.class.isInstance( value ) ) {
 			return fromBytes( DataHelper.extractBytes( (InputStream) value ) );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 
 	protected byte[] toBytes(T value) {
 		return SerializationHelper.serialize( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected T fromBytes(byte[] bytes) {
 		return (T) SerializationHelper.deserialize( bytes, getJavaTypeClass().getClassLoader() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
index cf2103d90e..ff2080f645 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIGINT BIGINT} handling.
  *
  * @author Steve Ebersole
  */
 public class BigIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final BigIntTypeDescriptor INSTANCE = new BigIntTypeDescriptor();
 
+	public BigIntTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.BIGINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setLong( index, javaTypeDescriptor.unwrap( value, Long.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getLong( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getLong( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
index 55bcaefaa2..631a937d03 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
@@ -1,39 +1,43 @@
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
 package org.hibernate.type.descriptor.sql;
 import java.sql.Types;
 
 /**
  * Descriptor for {@link java.sql.Types#BINARY BINARY} handling.
  *
  * @author Steve Ebersole
  */
 public class BinaryTypeDescriptor extends VarbinaryTypeDescriptor {
 	public static final BinaryTypeDescriptor INSTANCE = new BinaryTypeDescriptor();
 
+	public BinaryTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	@Override
 	public int getSqlType() {
 		return Types.BINARY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
index a60fa08d4a..5111c21e2f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
@@ -1,79 +1,85 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIT BIT} handling.
  * <p/>
  * Note that JDBC is very specific about its use of the type BIT to mean a single binary digit, whereas
  * SQL defines BIT having a parameterized length.
  *
  * @author Steve Ebersole
  */
 public class BitTypeDescriptor implements SqlTypeDescriptor {
 	public static final BitTypeDescriptor INSTANCE = new BitTypeDescriptor();
 
+	public BitTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	public int getSqlType() {
 		return Types.BIT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
index 1cda8b54a1..2f6280c328 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
@@ -1,141 +1,149 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.BinaryStream;
-import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BLOB BLOB} handling.
  *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public abstract class BlobTypeDescriptor implements SqlTypeDescriptor {
 
-	private BlobTypeDescriptor() {}
+	private BlobTypeDescriptor() {
+	}
+
+	@Override
+	public int getSqlType() {
+		return Types.BLOB;
+	}
+
+	@Override
+	public boolean canBeRemapped() {
+		return true;
+	}
+
+	@Override
+	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+			@Override
+			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( rs.getBlob( name ), options );
+			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBlob( index ), options );
+			}
+		};
+	}
+
+	protected abstract <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
+
+	public <X> BasicBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return getBlobBinder( javaTypeDescriptor );
+	}
 
 	public static final BlobTypeDescriptor DEFAULT =
 			new BlobTypeDescriptor() {
+				{
+					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+				}
+
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else if ( byte[].class.isInstance( value ) ) {
 								// performance shortcut for binding BLOB data in byte[] format
 								PRIMITIVE_ARRAY_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								BLOB_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						public void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor BLOB_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBlob( index, javaTypeDescriptor.unwrap( value, Blob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor STREAM_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final BinaryStream binaryStream = javaTypeDescriptor.unwrap( value, BinaryStream.class, options );
 							st.setBinaryStream( index, binaryStream.getInputStream(), binaryStream.getLength() );
 						}
 					};
 				}
 			};
 
-	protected abstract <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
-
-	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
-			@Override
-			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
-				return javaTypeDescriptor.wrap( rs.getBlob( name ), options );
-			}
-
-			@Override
-			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
-				return javaTypeDescriptor.wrap( statement.getBlob( index ), options );
-			}
-		};
-	}
-
-	public int getSqlType() {
-		return Types.BLOB;
-	}
-
-	@Override
-	public boolean canBeRemapped() {
-		return true;
-	}
-
-	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-		return getBlobBinder( javaTypeDescriptor );
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
index afa54c280f..a9cb2b889c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
@@ -1,76 +1,80 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link java.sql.Types#BOOLEAN BOOLEAN} handling.
  *
  * @author Steve Ebersole
  */
 public class BooleanTypeDescriptor implements SqlTypeDescriptor {
 	public static final BooleanTypeDescriptor INSTANCE = new BooleanTypeDescriptor();
 
+	public BooleanTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	public int getSqlType() {
 		return Types.BOOLEAN;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
index b28e614900..8eb12bacd3 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
@@ -1,39 +1,43 @@
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
 package org.hibernate.type.descriptor.sql;
 import java.sql.Types;
 
 /**
  * Descriptor for {@link Types#CHAR CHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class CharTypeDescriptor extends VarcharTypeDescriptor {
 	public static final CharTypeDescriptor INSTANCE = new CharTypeDescriptor();
 
+	public CharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	@Override
 	public int getSqlType() {
 		return Types.CHAR;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
index 696df0e78e..2acadc8fbf 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
@@ -1,118 +1,126 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#CLOB CLOB} handling.
  *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public abstract class ClobTypeDescriptor implements SqlTypeDescriptor {
+	@Override
+	public int getSqlType() {
+		return Types.CLOB;
+	}
+
+	@Override
+	public boolean canBeRemapped() {
+		return true;
+	}
+
+	@Override
+	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+			@Override
+			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( rs.getClob( name ), options );
+			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getClob( index ), options );
+			}
+		};
+	}
+
+
+	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
+
+	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return getClobBinder( javaTypeDescriptor );
+	}
 
 	public static final ClobTypeDescriptor DEFAULT =
 			new ClobTypeDescriptor() {
+				{
+					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+				}
+
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								CLOB_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor CLOB_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setClob( index, javaTypeDescriptor.unwrap( value, Clob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor STREAM_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
 							st.setCharacterStream( index, characterStream.getReader(), characterStream.getLength() );
 						}
 					};
 				}
 			};
 
-	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
-
-	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-		return getClobBinder( javaTypeDescriptor );
-	}
-
-	public int getSqlType() {
-		return Types.CLOB;
-	}
-
-	@Override
-	public boolean canBeRemapped() {
-		return true;
-	}
-
-	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
-			@Override
-			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
-				return javaTypeDescriptor.wrap( rs.getClob( name ), options );
-			}
-
-			@Override
-			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
-				return javaTypeDescriptor.wrap( statement.getClob( index ), options );
-			}
-		};
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
index 4bb7a702e2..6c64e7c9e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
@@ -1,77 +1,84 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.Date;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DATE DATE} handling.
  *
  * @author Steve Ebersole
  */
 public class DateTypeDescriptor implements SqlTypeDescriptor {
 	public static final DateTypeDescriptor INSTANCE = new DateTypeDescriptor();
 
+	public DateTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.DATE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDate( index, javaTypeDescriptor.unwrap( value, Date.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDate( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getDate( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
index 397c96f269..a168100960 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
@@ -1,77 +1,84 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.math.BigDecimal;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DECIMAL DECIMAL} handling.
  *
  * @author Steve Ebersole
  */
 public class DecimalTypeDescriptor implements SqlTypeDescriptor {
 	public static final DecimalTypeDescriptor INSTANCE = new DecimalTypeDescriptor();
 
+	public DecimalTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.DECIMAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBigDecimal( index, javaTypeDescriptor.unwrap( value, BigDecimal.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBigDecimal( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBigDecimal( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
index 13b6a1a045..0adf43cf3d 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DOUBLE DOUBLE} handling.
  *
  * @author Steve Ebersole
  */
 public class DoubleTypeDescriptor implements SqlTypeDescriptor {
 	public static final DoubleTypeDescriptor INSTANCE = new DoubleTypeDescriptor();
 
+	public DoubleTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.DOUBLE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDouble( index, javaTypeDescriptor.unwrap( value, Double.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDouble( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getDouble( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
index fc6b63c6d3..4aade7e399 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
@@ -1,38 +1,44 @@
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
 package org.hibernate.type.descriptor.sql;
+
 import java.sql.Types;
 
 /**
  * Descriptor for {@link Types#FLOAT FLOAT} handling.
  *
  * @author Steve Ebersole
  */
 public class FloatTypeDescriptor extends RealTypeDescriptor {
 	public static final FloatTypeDescriptor INSTANCE = new FloatTypeDescriptor();
 
+	public FloatTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.FLOAT;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
index 8834e2d598..f93ed0c0ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#INTEGER INTEGER} handling.
  *
  * @author Steve Ebersole
  */
 public class IntegerTypeDescriptor implements SqlTypeDescriptor {
 	public static final IntegerTypeDescriptor INSTANCE = new IntegerTypeDescriptor();
 
+	public IntegerTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.INTEGER;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setInt( index, javaTypeDescriptor.unwrap( value, Integer.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getInt( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getInt( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeFamilyInformation.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeFamilyInformation.java
new file mode 100644
index 0000000000..670cf7b3b4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeFamilyInformation.java
@@ -0,0 +1,75 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.sql.Types;
+import java.util.concurrent.ConcurrentHashMap;
+
+/**
+ * Information pertaining to JDBC type families.
+ *
+ * @author Steve Ebersole
+ */
+public class JdbcTypeFamilyInformation {
+	public static final JdbcTypeFamilyInformation INSTANCE = new JdbcTypeFamilyInformation();
+
+	// todo : make Family non-enum so it can be expanded by Dialects?
+
+	public static enum Family {
+		BINARY( Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY ),
+		NUMERIC( Types.BIGINT, Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.NUMERIC, Types.REAL, Types.SMALLINT, Types.TINYINT ),
+		CHARACTER( Types.CHAR, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR ),
+		DATETIME( Types.DATE, Types.TIME, Types.TIMESTAMP ),
+		CLOB( Types.CLOB, Types.NCLOB );
+
+		private final int[] typeCodes;
+
+		@SuppressWarnings("UnnecessaryBoxing")
+		private Family(int... typeCodes) {
+			this.typeCodes = typeCodes;
+
+			for ( int typeCode : typeCodes ) {
+				JdbcTypeFamilyInformation.INSTANCE.typeCodeToFamilyMap.put( Integer.valueOf( typeCode ), this );
+			}
+		}
+
+		public int[] getTypeCodes() {
+			return typeCodes;
+		}
+	}
+
+	private ConcurrentHashMap<Integer,Family> typeCodeToFamilyMap = new ConcurrentHashMap<Integer, Family>();
+
+	/**
+	 * Will return {@code null} if no match is found.
+	 *
+	 * @param typeCode The JDBC type code.
+	 *
+	 * @return The family of datatypes the type code belongs to, or {@code null} if it belongs to no known families.
+	 */
+	@SuppressWarnings("UnnecessaryBoxing")
+	public Family locateJdbcTypeFamilyByTypeCode(int typeCode) {
+		return typeCodeToFamilyMap.get( Integer.valueOf( typeCode ) );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java
new file mode 100644
index 0000000000..05bf217c34
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java
@@ -0,0 +1,134 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.math.BigDecimal;
+import java.sql.Blob;
+import java.sql.Clob;
+import java.sql.Date;
+import java.sql.Ref;
+import java.sql.Struct;
+import java.sql.Time;
+import java.sql.Timestamp;
+import java.sql.Types;
+import java.util.Calendar;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.mapping.Array;
+
+/**
+ * Presents recommended {@literal JDCB typecode <-> Java Class} mappings.  Currently the recommendations
+ * contained here come from the JDBC spec itself, as outlined at <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html#1034737"/>
+ * Eventually, the plan is to have {@link org.hibernate.dialect.Dialect} contribute this information.
+ *
+ * @author Steve Ebersole
+ */
+public class JdbcTypeJavaClassMappings {
+	private static final Logger log = Logger.getLogger( JdbcTypeJavaClassMappings.class );
+
+	private static final ConcurrentHashMap<Class, Integer> javaClassToJdbcTypeCodeMap = buildJdbcJavaClassMappings();
+	private static final ConcurrentHashMap<Integer, Class> jdbcTypeCodeToJavaClassMap = transpose( javaClassToJdbcTypeCodeMap );
+
+	public static final JdbcTypeJavaClassMappings INSTANCE = new JdbcTypeJavaClassMappings();
+
+	private JdbcTypeJavaClassMappings() {
+	}
+
+	@SuppressWarnings("UnnecessaryUnboxing")
+	public int determineJdbcTypeCodeForJavaClass(Class cls) {
+		Integer typeCode = JdbcTypeJavaClassMappings.javaClassToJdbcTypeCodeMap.get( cls );
+		if ( typeCode != null ) {
+			return typeCode.intValue();
+		}
+
+		int specialCode = cls.hashCode();
+		log.debug(
+				"JDBC type code mapping not known for class [" + cls.getName() + "]; using custom code [" + specialCode + "]"
+		);
+		return specialCode;
+	}
+
+	@SuppressWarnings("UnnecessaryUnboxing")
+	public Class determineJavaClassForJdbcTypeCode(int typeCode) {
+		Class cls = jdbcTypeCodeToJavaClassMap.get( Integer.valueOf( typeCode ) );
+		if ( cls != null ) {
+			return cls;
+		}
+
+		log.debugf(
+				"Java Class mapping not known for JDBC type code [%s]; using java.lang.Object",
+				typeCode
+		);
+		return Object.class;
+	}
+
+
+	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private static ConcurrentHashMap<Class, Integer> buildJdbcJavaClassMappings() {
+		ConcurrentHashMap<Class, Integer> jdbcJavaClassMappings = new ConcurrentHashMap<Class, Integer>();
+
+		// these mappings are the ones outlined specifically in the spec
+		jdbcJavaClassMappings.put( String.class, Types.VARCHAR );
+		jdbcJavaClassMappings.put( BigDecimal.class, Types.NUMERIC );
+		jdbcJavaClassMappings.put( Boolean.class, Types.BIT );
+		jdbcJavaClassMappings.put( Integer.class, Types.INTEGER );
+		jdbcJavaClassMappings.put( Long.class, Types.BIGINT );
+		jdbcJavaClassMappings.put( Float.class, Types.REAL );
+		jdbcJavaClassMappings.put( Double.class, Types.DOUBLE );
+		jdbcJavaClassMappings.put( byte[].class, Types.LONGVARBINARY );
+		jdbcJavaClassMappings.put( Date.class, Types.DATE );
+		jdbcJavaClassMappings.put( Time.class, Types.TIME );
+		jdbcJavaClassMappings.put( Timestamp.class, Types.TIMESTAMP );
+		jdbcJavaClassMappings.put( Blob.class, Types.BLOB );
+		jdbcJavaClassMappings.put( Clob.class, Types.CLOB );
+		jdbcJavaClassMappings.put( Array.class, Types.ARRAY );
+		jdbcJavaClassMappings.put( Struct.class, Types.STRUCT );
+		jdbcJavaClassMappings.put( Ref.class, Types.REF );
+		jdbcJavaClassMappings.put( Class.class, Types.JAVA_OBJECT );
+
+		// additional "common sense" registrations
+		jdbcJavaClassMappings.put( Character.class, Types.CHAR );
+		jdbcJavaClassMappings.put( char[].class, Types.VARCHAR );
+		jdbcJavaClassMappings.put( Character[].class, Types.VARCHAR );
+		jdbcJavaClassMappings.put( Byte[].class, Types.LONGVARBINARY );
+		jdbcJavaClassMappings.put( Date.class, Types.TIMESTAMP );
+		jdbcJavaClassMappings.put( Calendar.class, Types.TIMESTAMP );
+
+		return jdbcJavaClassMappings;
+	}
+
+	private static ConcurrentHashMap<Integer, Class> transpose(ConcurrentHashMap<Class, Integer> javaClassToJdbcTypeCodeMap) {
+		final ConcurrentHashMap<Integer, Class> transposed = new ConcurrentHashMap<Integer, Class>();
+
+		for ( Map.Entry<Class,Integer> entry : javaClassToJdbcTypeCodeMap.entrySet() ) {
+			transposed.put( entry.getValue(), entry.getKey() );
+		}
+
+		return transposed;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongNVarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongNVarcharTypeDescriptor.java
new file mode 100644
index 0000000000..9cbd4db667
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongNVarcharTypeDescriptor.java
@@ -0,0 +1,44 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.sql.Types;
+
+/**
+ * Descriptor for {@link Types#LONGNVARCHAR LONGNVARCHAR} handling.
+ *
+ * @author Steve Ebersole
+ */
+public class LongNVarcharTypeDescriptor extends NVarcharTypeDescriptor {
+	public static final LongNVarcharTypeDescriptor INSTANCE = new LongNVarcharTypeDescriptor();
+
+	public LongNVarcharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
+	public int getSqlType() {
+		return Types.LONGNVARCHAR;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
index 41fabafbd3..d338dd15a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
@@ -1,39 +1,44 @@
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
 package org.hibernate.type.descriptor.sql;
+
 import java.sql.Types;
 
 /**
  * Descriptor for {@link Types#LONGVARBINARY LONGVARBINARY} handling.
  *
  * @author Steve Ebersole
  */
 public class LongVarbinaryTypeDescriptor extends VarbinaryTypeDescriptor {
 	public static final LongVarbinaryTypeDescriptor INSTANCE = new LongVarbinaryTypeDescriptor();
 
+	public LongVarbinaryTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	@Override
 	public int getSqlType() {
 		return Types.LONGVARBINARY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
index 7aa1666751..b7adc870a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
@@ -1,39 +1,44 @@
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
 package org.hibernate.type.descriptor.sql;
+
 import java.sql.Types;
 
 /**
  * Descriptor for {@link Types#LONGVARCHAR LONGVARCHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class LongVarcharTypeDescriptor extends VarcharTypeDescriptor {
 	public static final LongVarcharTypeDescriptor INSTANCE = new LongVarcharTypeDescriptor();
 
+	public LongVarcharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	@Override
 	public int getSqlType() {
 		return Types.LONGVARCHAR;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NCharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NCharTypeDescriptor.java
new file mode 100644
index 0000000000..58b450699c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NCharTypeDescriptor.java
@@ -0,0 +1,44 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.sql.Types;
+
+/**
+ * Descriptor for {@link Types#NCHAR NCHAR} handling.
+ *
+ * @author Steve Ebersole
+ */
+public class NCharTypeDescriptor extends NVarcharTypeDescriptor {
+	public static final NCharTypeDescriptor INSTANCE = new NCharTypeDescriptor();
+
+	public NCharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
+	public int getSqlType() {
+		return Types.NCHAR;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java
new file mode 100644
index 0000000000..108927d929
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java
@@ -0,0 +1,125 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.sql.CallableStatement;
+import java.sql.NClob;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.type.descriptor.CharacterStream;
+import org.hibernate.type.descriptor.ValueBinder;
+import org.hibernate.type.descriptor.ValueExtractor;
+import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+
+/**
+ * Descriptor for {@link Types#NCLOB NCLOB} handling.
+ *
+ * @author Steve Ebersole
+ * @author Gail Badner
+ */
+public abstract class NClobTypeDescriptor implements SqlTypeDescriptor {
+	@Override
+	public int getSqlType() {
+		return Types.NCLOB;
+	}
+
+	@Override
+	public boolean canBeRemapped() {
+		return true;
+	}
+
+	@Override
+	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+			@Override
+			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( rs.getNClob( name ), options );
+			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getNClob( index ), options );
+			}
+		};
+	}
+
+
+	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
+
+	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return getClobBinder( javaTypeDescriptor );
+	}
+
+	public static final ClobTypeDescriptor DEFAULT =
+			new ClobTypeDescriptor() {
+				{
+					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+				}
+
+				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+					return new BasicBinder<X>( javaTypeDescriptor, this ) {
+						@Override
+						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
+							if ( options.useStreamForLobBinding() ) {
+								STREAM_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
+							}
+							else {
+								CLOB_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
+							}
+						}
+					};
+				}
+			};
+
+	public static final ClobTypeDescriptor CLOB_BINDING =
+			new ClobTypeDescriptor() {
+				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+					return new BasicBinder<X>( javaTypeDescriptor, this ) {
+						@Override
+						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+								throws SQLException {
+							st.setNClob( index, javaTypeDescriptor.unwrap( value, NClob.class, options ) );
+						}
+					};
+				}
+			};
+
+	public static final ClobTypeDescriptor STREAM_BINDING =
+			new ClobTypeDescriptor() {
+				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+					return new BasicBinder<X>( javaTypeDescriptor, this ) {
+						@Override
+						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+								throws SQLException {
+							final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
+							st.setCharacterStream( index, characterStream.getReader(), characterStream.getLength() );
+						}
+					};
+				}
+			};
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java
new file mode 100644
index 0000000000..d1dfe1a363
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java
@@ -0,0 +1,83 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.sql.CallableStatement;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.type.descriptor.ValueBinder;
+import org.hibernate.type.descriptor.ValueExtractor;
+import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+
+/**
+ * Descriptor for {@link Types#NVARCHAR NVARCHAR} handling.
+ *
+ * @author Steve Ebersole
+ */
+public class NVarcharTypeDescriptor implements SqlTypeDescriptor {
+	public static final NVarcharTypeDescriptor INSTANCE = new NVarcharTypeDescriptor();
+
+	public NVarcharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
+	public int getSqlType() {
+		return Types.NVARCHAR;
+	}
+
+	@Override
+	public boolean canBeRemapped() {
+		return true;
+	}
+
+	@Override
+	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return new BasicBinder<X>( javaTypeDescriptor, this ) {
+			@Override
+			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
+				st.setNString( index, javaTypeDescriptor.unwrap( value, String.class, options ) );
+			}
+		};
+	}
+
+	@Override
+	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+			@Override
+			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( rs.getNString( name ), options );
+			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getNString( index ), options );
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
index a73384cfef..60a1eb871d 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
@@ -1,39 +1,44 @@
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
 package org.hibernate.type.descriptor.sql;
+
 import java.sql.Types;
 
 /**
  * Descriptor for {@link Types#NUMERIC NUMERIC} handling.
  *
  * @author Steve Ebersole
  */
 public class NumericTypeDescriptor extends DecimalTypeDescriptor {
 	public static final NumericTypeDescriptor INSTANCE = new NumericTypeDescriptor();
 
+	public NumericTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	@Override
 	public int getSqlType() {
 		return Types.NUMERIC;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
index 85c2a15559..eb859d51ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#REAL REAL} handling.
  *
  * @author Steve Ebersole
  */
 public class RealTypeDescriptor implements SqlTypeDescriptor {
 	public static final RealTypeDescriptor INSTANCE = new RealTypeDescriptor();
 
+	public RealTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.REAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setFloat( index, javaTypeDescriptor.unwrap( value, Float.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getFloat( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getFloat( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
index 0786ea242c..454bcc2dcc 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#SMALLINT SMALLINT} handling.
  *
  * @author Steve Ebersole
  */
 public class SmallIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final SmallIntTypeDescriptor INSTANCE = new SmallIntTypeDescriptor();
 
+	public SmallIntTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.SMALLINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setShort( index, javaTypeDescriptor.unwrap( value, Short.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getShort( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getShort( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
index de8ae5279b..a38371f48c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
@@ -1,58 +1,77 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.io.Serializable;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for the <tt>SQL</tt>/<tt>JDBC</tt> side of a value mapping.
+ * <p/>
+ * NOTE : Implementations should be registered with the {@link SqlTypeDescriptor}.  The built-in Hibernate
+ * implementations register themselves on construction.
  *
  * @author Steve Ebersole
  */
 public interface SqlTypeDescriptor extends Serializable {
 	/**
 	 * Return the {@linkplain java.sql.Types JDBC type-code} for the column mapped by this type.
 	 *
 	 * @return typeCode The JDBC type-code
 	 */
 	public int getSqlType();
 
 	/**
 	 * Is this descriptor available for remapping?
 	 *
 	 * @return {@code true} indicates this descriptor can be remapped; otherwise, {@code false}
 	 *
 	 * @see org.hibernate.type.descriptor.WrapperOptions#remapSqlTypeDescriptor
 	 * @see org.hibernate.dialect.Dialect#remapSqlTypeDescriptor
 	 */
 	public boolean canBeRemapped();
 
+	/**
+	 * Get the binder (setting JDBC in-going parameter values) capable of handling values of the type described by the
+	 * passed descriptor.
+	 *
+	 * @param javaTypeDescriptor The descriptor describing the types of Java values to be bound
+	 *
+	 * @return The appropriate binder.
+	 */
 	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor);
 
+	/**
+	 * Get the extractor (pulling out-going values from JDBC objects) capable of handling values of the type described
+	 * by the passed descriptor.
+	 *
+	 * @param javaTypeDescriptor The descriptor describing the types of Java values to be extracted
+	 *
+	 * @return The appropriate extractor
+	 */
 	public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java
new file mode 100644
index 0000000000..6744c3c770
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java
@@ -0,0 +1,152 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type.descriptor.sql;
+
+import java.io.Serializable;
+import java.sql.CallableStatement;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.type.descriptor.JdbcTypeNameMapper;
+import org.hibernate.type.descriptor.ValueBinder;
+import org.hibernate.type.descriptor.ValueExtractor;
+import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+
+/**
+ * Basically a map from JDBC type code (int) -> {@link SqlTypeDescriptor}
+ *
+ * @author Steve Ebersole
+ */
+public class SqlTypeDescriptorRegistry {
+	public static final SqlTypeDescriptorRegistry INSTANCE = new SqlTypeDescriptorRegistry();
+
+	private static final Logger log = Logger.getLogger( SqlTypeDescriptorRegistry.class );
+
+	private ConcurrentHashMap<Integer,SqlTypeDescriptor> descriptorMap = new ConcurrentHashMap<Integer, SqlTypeDescriptor>();
+
+	@SuppressWarnings("UnnecessaryBoxing")
+	public void addDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
+		descriptorMap.put( Integer.valueOf( sqlTypeDescriptor.getSqlType() ), sqlTypeDescriptor );
+	}
+
+	@SuppressWarnings("UnnecessaryBoxing")
+	public SqlTypeDescriptor getDescriptor(int jdbcTypeCode) {
+		SqlTypeDescriptor descriptor = descriptorMap.get( Integer.valueOf( jdbcTypeCode ) );
+		if ( descriptor != null ) {
+			return descriptor;
+		}
+
+		if ( JdbcTypeNameMapper.isStandardTypeCode( jdbcTypeCode ) ) {
+			log.debugf(
+					"A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry",
+					jdbcTypeCode
+			);
+		}
+
+		// see if the typecode is part of a known type family...
+		JdbcTypeFamilyInformation.Family family = JdbcTypeFamilyInformation.INSTANCE.locateJdbcTypeFamilyByTypeCode( jdbcTypeCode );
+		if ( family != null ) {
+			for ( int potentialAlternateTypeCode : family.getTypeCodes() ) {
+				if ( potentialAlternateTypeCode != jdbcTypeCode ) {
+					final SqlTypeDescriptor potentialAlternateDescriptor = descriptorMap.get( Integer.valueOf( potentialAlternateTypeCode ) );
+					if ( potentialAlternateDescriptor != null ) {
+						// todo : add a SqlTypeDescriptor.canBeAssignedFrom method...
+						return potentialAlternateDescriptor;
+					}
+
+					if ( JdbcTypeNameMapper.isStandardTypeCode( potentialAlternateTypeCode ) ) {
+						log.debugf(
+								"A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry",
+								potentialAlternateTypeCode
+						);
+					}
+				}
+			}
+		}
+
+		// finally, create a new descriptor mapping to getObject/setObject for this type code...
+		final ObjectSqlTypeDescriptor fallBackDescriptor = new ObjectSqlTypeDescriptor( jdbcTypeCode );
+		addDescriptor( fallBackDescriptor );
+		return fallBackDescriptor;
+	}
+
+	public static class ObjectSqlTypeDescriptor implements SqlTypeDescriptor {
+		private final int jdbcTypeCode;
+
+		public ObjectSqlTypeDescriptor(int jdbcTypeCode) {
+			this.jdbcTypeCode = jdbcTypeCode;
+		}
+
+		@Override
+		public int getSqlType() {
+			return jdbcTypeCode;
+		}
+
+		@Override
+		public boolean canBeRemapped() {
+			return true;
+		}
+
+		@Override
+		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
+			if ( Serializable.class.isAssignableFrom( javaTypeDescriptor.getJavaTypeClass() ) ) {
+				return VarbinaryTypeDescriptor.INSTANCE.getBinder( javaTypeDescriptor );
+			}
+
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
+				@Override
+				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					st.setObject( index, value, jdbcTypeCode );
+				}
+			};
+		}
+
+		@Override
+		@SuppressWarnings("unchecked")
+		public ValueExtractor getExtractor(JavaTypeDescriptor javaTypeDescriptor) {
+			if ( Serializable.class.isAssignableFrom( javaTypeDescriptor.getJavaTypeClass() ) ) {
+				return VarbinaryTypeDescriptor.INSTANCE.getExtractor( javaTypeDescriptor );
+			}
+
+			return new BasicExtractor( javaTypeDescriptor, this ) {
+				@Override
+				protected Object doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+					return rs.getObject( name );
+				}
+
+				@Override
+				protected Object doExtract(CallableStatement statement, int index, WrapperOptions options)
+						throws SQLException {
+					return statement.getObject( index );
+				}
+			};
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
index 2b1b1b034c..5a279d8177 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
@@ -1,77 +1,84 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Time;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIME TIME} handling.
  *
  * @author Steve Ebersole
  */
 public class TimeTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimeTypeDescriptor INSTANCE = new TimeTypeDescriptor();
 
+	public TimeTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.TIME;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTime( index, javaTypeDescriptor.unwrap( value, Time.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTime( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getTime( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
index 4401becfc8..bf36bd66f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
@@ -1,77 +1,84 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Timestamp;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIMESTAMP TIMESTAMP} handling.
  *
  * @author Steve Ebersole
  */
 public class TimestampTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimestampTypeDescriptor INSTANCE = new TimestampTypeDescriptor();
 
+	public TimestampTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.TIMESTAMP;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTimestamp( index, javaTypeDescriptor.unwrap( value, Timestamp.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTimestamp( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getTimestamp( index ), options );
 			}
 		};
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
index 8418a28526..a5955c73f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
@@ -1,79 +1,86 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TINYINT TINYINT} handling.
  * <p/>
  * Note that <tt>JDBC</tt> states that TINYINT should be mapped to either byte or short, but points out
  * that using byte can in fact lead to loss of data.
  *
  * @author Steve Ebersole
  */
 public class TinyIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final TinyIntTypeDescriptor INSTANCE = new TinyIntTypeDescriptor();
 
+	public TinyIntTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.TINYINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setByte( index, javaTypeDescriptor.unwrap( value, Byte.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getByte( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getByte( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
index 7c7d28a807..8ed51fd617 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
@@ -1,76 +1,80 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARBINARY VARBINARY} handling.
  *
  * @author Steve Ebersole
  */
 public class VarbinaryTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarbinaryTypeDescriptor INSTANCE = new VarbinaryTypeDescriptor();
 
+	public VarbinaryTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
 	public int getSqlType() {
 		return Types.VARBINARY;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBytes( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBytes( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
index 50d5f3c5a9..edb98150d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
@@ -1,76 +1,83 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARCHAR VARCHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class VarcharTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarcharTypeDescriptor INSTANCE = new VarcharTypeDescriptor();
 
+	public VarcharTypeDescriptor() {
+		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+	}
+
+	@Override
 	public int getSqlType() {
 		return Types.VARCHAR;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
+	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setString( index, javaTypeDescriptor.unwrap( value, String.class, options ) );
 			}
 		};
 	}
 
+	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getString( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getString( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/type/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/type/AttributeConverterTest.java
new file mode 100644
index 0000000000..4405018174
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/type/AttributeConverterTest.java
@@ -0,0 +1,143 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type;
+
+import javax.persistence.AttributeConverter;
+import javax.persistence.Convert;
+import javax.persistence.Converter;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import java.sql.Clob;
+import java.sql.Types;
+
+import org.hibernate.IrrelevantEntity;
+import org.hibernate.cfg.AttributeConverterDefinition;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.mapping.SimpleValue;
+import org.hibernate.type.descriptor.java.StringTypeDescriptor;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertSame;
+
+/**
+ * Tests the principle of adding "AttributeConverter" to the mix of {@link Type} resolution
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConverterTest extends BaseUnitTestCase {
+	@Test
+	public void testBasicOperation() {
+		Configuration cfg = new Configuration();
+		SimpleValue simpleValue = new SimpleValue( cfg.createMappings() );
+		simpleValue.setJpaAttributeConverterDefinition(
+				new AttributeConverterDefinition( new StringClobConverter(), true )
+		);
+		simpleValue.setTypeUsingReflection( IrrelevantEntity.class.getName(), "name" );
+
+		Type type = simpleValue.getType();
+		assertNotNull( type );
+		assertTyping( BasicType.class, type );
+		AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
+		assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
+		assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
+	}
+
+	@Test
+	public void testNormalOperation() {
+		Configuration cfg = new Configuration();
+		cfg.addAttributeConverter( StringClobConverter.class, true );
+		cfg.addAnnotatedClass( Tester.class );
+		cfg.addAnnotatedClass( Tester2.class );
+		cfg.buildMappings();
+
+		{
+			PersistentClass tester = cfg.getClassMapping( Tester.class.getName() );
+			Property nameProp = tester.getProperty( "name" );
+			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
+			Type type = nameValue.getType();
+			assertNotNull( type );
+			assertTyping( BasicType.class, type );
+			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
+			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
+			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
+		}
+
+		{
+			PersistentClass tester = cfg.getClassMapping( Tester2.class.getName() );
+			Property nameProp = tester.getProperty( "name" );
+			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
+			Type type = nameValue.getType();
+			assertNotNull( type );
+			assertTyping( BasicType.class, type );
+			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
+			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
+			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
+		}
+	}
+
+
+	@Entity
+	public static class Tester {
+		@Id
+		private Long id;
+		private String name;
+	}
+
+	@Entity
+	public static class Tester2 {
+		@Id
+		private Long id;
+		@Convert(disableConversion = true)
+		private String name;
+	}
+
+	@Entity
+	public static class Tester3 {
+		@Id
+		private Long id;
+		@org.hibernate.annotations.Type( type = "string" )
+		private String name;
+	}
+
+	@Converter( autoApply = true )
+	public static class StringClobConverter implements AttributeConverter<String,Clob> {
+		@Override
+		public Clob convertToDatabaseColumn(String attribute) {
+			return null;
+		}
+
+		@Override
+		public String convertToEntityAttribute(Clob dbData) {
+			return null;
+		}
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/ExtraAssertions.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/ExtraAssertions.java
index d8256e86c3..f80b801194 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/ExtraAssertions.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/ExtraAssertions.java
@@ -1,40 +1,54 @@
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
 package org.hibernate.testing.junit4;
 
 import org.junit.Assert;
 
 /**
  * @author Steve Ebersole
  */
 public class ExtraAssertions {
 	public static void assertClassAssignability(Class expected, Class actual) {
 		if ( ! expected.isAssignableFrom( actual ) ) {
 			Assert.fail(
 					"Expected class [" + expected.getName() + "] was not assignable from actual [" +
 							actual.getName() + "]"
 			);
 		}
 	}
+
+	@SuppressWarnings("unchecked")
+	public static <T> T assertTyping(Class<T> expectedType, Object value) {
+		if ( ! expectedType.isInstance( value ) ) {
+			Assert.fail(
+					String.format(
+							"Expecting value of type [%s], but found [%s]",
+							expectedType.getName(),
+							value == null ? "<null>" : value
+					)
+			);
+		}
+		return (T) value;
+	}
 }
