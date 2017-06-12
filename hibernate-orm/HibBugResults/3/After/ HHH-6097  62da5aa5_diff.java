diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 9ca34a3a67..315494b442 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1733 +1,1733 @@
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
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.Cascade;
 import org.hibernate.annotations.CascadeType;
 import org.hibernate.annotations.Check;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionOfElements;
 import org.hibernate.annotations.Columns;
 import org.hibernate.annotations.DiscriminatorOptions;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForceDiscriminator;
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
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cfg.annotations.CollectionBinder;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.cfg.annotations.MapKeyColumnDelegator;
 import org.hibernate.cfg.annotations.MapKeyJoinColumnDelegator;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.annotations.PropertyBinder;
 import org.hibernate.cfg.annotations.QueryBinder;
 import org.hibernate.cfg.annotations.SimpleValueBinder;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Versioning;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
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
 import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
 import org.hibernate.persister.entity.SingleTableEntityPersister;
 import org.hibernate.persister.entity.UnionSubclassEntityPersister;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AnnotationBinder.class.getName());
 
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
             LOG.packageNotFound(packageName);
 			return;
 		}
 		if ( pckg.isAnnotationPresent( SequenceGenerator.class ) ) {
 			SequenceGenerator ann = pckg.getAnnotation( SequenceGenerator.class );
 			IdGenerator idGen = buildIdGenerator( ann, mappings );
 			mappings.addGenerator( idGen );
             LOG.trace("Add sequence generator with name: " + idGen.getName());
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
             LOG.trace("Add table generator with name: " + idGen.getName());
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
                 if (seqGen.initialValue() != 1) LOG.unsupportedInitialValue(Configuration.USE_NEW_ID_GENERATOR_MAPPINGS);
 				idGen.addParam( SequenceHiLoGenerator.MAX_LO, String.valueOf( seqGen.allocationSize() - 1 ) );
                 LOG.trace("Add sequence generator with name: " + idGen.getName());
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
             LOG.trace("Add generic generator with name: " + idGen.getName());
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
 
         LOG.bindingEntityFromClass( clazzToProcess.getName() );
 
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
         } else if (clazzToProcess.isAnnotationPresent(Table.class)) LOG.invalidTableAnnotation(clazzToProcess.getName());
 
 
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
 			if ( persistentClass.getEntityPersisterClass() == null ) {
 				persistentClass.getRootClass().setEntityPersisterClass( JoinedSubclassEntityPersister.class );
 			}
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
 			if ( inheritanceState.hasParents() ) {
 				if ( persistentClass.getEntityPersisterClass() == null ) {
 					persistentClass.getRootClass().setEntityPersisterClass( SingleTableEntityPersister.class );
 				}
 			}
 			else {
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
 			if ( inheritanceState.hasParents() ) {
 				if ( persistentClass.getEntityPersisterClass() == null ) {
 					persistentClass.getRootClass().setEntityPersisterClass( UnionSubclassEntityPersister.class );
 				}
 			}
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
-        if (discAnn != null && inheritanceState.hasParents()) LOG.invalidDescriminatorAnnotation(clazzToProcess.getName());
+        if (discAnn != null && inheritanceState.hasParents()) LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 
 		String discrimValue = clazzToProcess.isAnnotationPresent( DiscriminatorValue.class ) ?
 				clazzToProcess.getAnnotation( DiscriminatorValue.class ).value() :
 				null;
 		entityBinder.setDiscriminatorValue( discrimValue );
 
 		if ( clazzToProcess.isAnnotationPresent( ForceDiscriminator.class ) ) {
             LOG.deprecatedForceDescriminatorAnnotation();
 			entityBinder.setForceDiscriminator( true );
 		}
 
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
             LOG.debugf("No value specified for 'javax.persistence.sharedCache.mode'; using UNSPECIFIED");
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
                     LOG.debugf("Unable to resolve given mode name [%s]; using UNSPECIFIED : %s", value, e);
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
             LOG.trace("Default cache concurrency strategy already defined");
 			return;
 		}
 
 		if ( !properties.containsKey( Configuration.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
             LOG.trace("Given properties did not contain any default cache concurrency strategy setting");
 			return;
 		}
 
 		final String strategyName = properties.getProperty( Configuration.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
         LOG.trace("Discovered default cache concurrency strategy via config [" + strategyName + "]");
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.parse( strategyName );
 		if ( strategy == null ) {
             LOG.trace("Discovered default cache concurrency strategy specified nothing");
 			return;
 		}
 
         LOG.debugf("Setting default cache concurrency strategy via config [%s]", strategy.name());
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
             LOG.trace("Subclass joined column(s) created");
 		}
 		else {
             if (clazzToProcess.isAnnotationPresent(PrimaryKeyJoinColumns.class)
                 || clazzToProcess.isAnnotationPresent(PrimaryKeyJoinColumn.class)) LOG.invalidPrimaryKeyJoinColumnAnnotation();
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
             if (AnnotatedClassType.NONE.equals(classType)
                 && clazzToProcess.isAnnotationPresent(org.hibernate.annotations.Entity.class))
                 LOG.missingEntityAnnotation(clazzToProcess.getName());
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
         LOG.bindingFilterDefinition( def.getFilterName() );
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
 
 		if ( !BinderHelper.isEmptyAnnotationValue( defAnn.name() ) ) {
             LOG.bindingTypeDefinition( defAnn.name() );
 			mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
 		}
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
             LOG.bindingTypeDefinition( defAnn.defaultForType().getName() );
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
             LOG.trace("Setting discriminator for entity " + rootClass.getEntityName());
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
 
         LOG.trace("Processing annotations of " + propertyHolder.getEntityName() + "." + inferredData.getPropertyName());
 
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
             LOG.trace(inferredData.getPropertyName() + " is a version property");
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
             LOG.trace("Version name: " + rootClass.getVersion().getName() + ", unsavedValue: "
                       + ((SimpleValue)rootClass.getVersion().getValue()).getNullValue());
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
 				//@PKJC must be constrained
 				final boolean mandatory = !ann.optional() || forcePersist || trueOneToOne;
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
 					|| property.isAnnotationPresent( CollectionOfElements.class ) //legacy Hibernate
 					|| property.isAnnotationPresent( ElementCollection.class )
 					|| property.isAnnotationPresent( ManyToAny.class ) ) {
 				OneToMany oneToManyAnn = property.getAnnotation( OneToMany.class );
 				ManyToMany manyToManyAnn = property.getAnnotation( ManyToMany.class );
 				ElementCollection elementCollectionAnn = property.getAnnotation( ElementCollection.class );
 				CollectionOfElements collectionOfElementsAnn = property.getAnnotation( CollectionOfElements.class ); //legacy hibernate
 
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
 							propertyHolder,
 							inferredData,
 							mappings
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( CollectionOfElements.class )
 								|| property.isAnnotationPresent( org.hibernate.annotations.MapKey.class )
 								|| property.isAnnotationPresent( MapKeyType.class )
 
 						// || property.isAnnotationPresent( ManyToAny.class )
 				);
 				collectionBinder.setIndexColumn( indexColumn );
 				collectionBinder.setMapKey( property.getAnnotation( MapKey.class ) );
 				collectionBinder.setPropertyName( inferredData.getPropertyName() );
 				BatchSize batchAnn = property.getAnnotation( BatchSize.class );
 				collectionBinder.setBatchSize( batchAnn );
 				javax.persistence.OrderBy ejb3OrderByAnn = property.getAnnotation( javax.persistence.OrderBy.class );
 				OrderBy orderByAnn = property.getAnnotation( OrderBy.class );
 				collectionBinder.setEjb3OrderBy( ejb3OrderByAnn );
 				collectionBinder.setSqlOrderBy( orderByAnn );
 				Sort sortAnn = property.getAnnotation( Sort.class );
 				collectionBinder.setSort( sortAnn );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
index b0d652f2b8..8e789d405b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
@@ -1,280 +1,288 @@
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
 
 // $Id$
 
 package org.hibernate.cfg;
 
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 import java.util.TreeMap;
 import javax.persistence.Access;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Transient;
 import org.hibernate.AnnotationException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.Target;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.internal.util.StringHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * A helper class to keep the {@code XProperty}s of a class ordered by access type.
  *
  * @author Hardy Ferentschik
  */
 class PropertyContainer {
 
     static {
         System.setProperty("jboss.i18n.generate-proxies", "true");
     }
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PropertyContainer.class.getName());
 
 	private final AccessType explicitClassDefinedAccessType;
 
 	/**
 	 * Constains the properties which must be returned in case the class is accessed via {@code AccessType.FIELD}. Note,
 	 * this does not mean that all {@code XProperty}s in this map are fields. Due to JPA access rules single properties
 	 * can have different access type than the overall class access type.
 	 */
 	private final TreeMap<String, XProperty> fieldAccessMap;
 
 	/**
 	 * Constains the properties which must be returned in case the class is accessed via {@code AccessType.Property}. Note,
 	 * this does not mean that all {@code XProperty}s in this map are properties/methods. Due to JPA access rules single properties
 	 * can have different access type than the overall class access type.
 	 */
 	private final TreeMap<String, XProperty> propertyAccessMap;
 
 	/**
 	 * The class for which this container is created.
 	 */
 	private final XClass xClass;
 	private final XClass entityAtStake;
 
 	PropertyContainer(XClass clazz, XClass entityAtStake) {
 		this.xClass = clazz;
 		this.entityAtStake = entityAtStake;
 
 		explicitClassDefinedAccessType = determineClassDefinedAccessStrategy();
 
 		// first add all properties to field and property map
 		fieldAccessMap = initProperties( AccessType.FIELD );
 		propertyAccessMap = initProperties( AccessType.PROPERTY );
 
 		considerExplicitFieldAndPropertyAccess();
 	}
 
 	public XClass getEntityAtStake() {
 		return entityAtStake;
 	}
 
 	public XClass getDeclaringClass() {
 		return xClass;
 	}
 
 	public AccessType getExplicitAccessStrategy() {
 		return explicitClassDefinedAccessType;
 	}
 
 	public boolean hasExplicitAccessStrategy() {
 		return !explicitClassDefinedAccessType.equals( AccessType.DEFAULT );
 	}
 
 	public Collection<XProperty> getProperties(AccessType accessType) {
 		assertTypesAreResolvable( accessType );
 		if ( AccessType.DEFAULT == accessType || AccessType.PROPERTY == accessType ) {
 			return Collections.unmodifiableCollection( propertyAccessMap.values() );
 		}
 		else {
 			return Collections.unmodifiableCollection( fieldAccessMap.values() );
 		}
 	}
 
 	private void assertTypesAreResolvable(AccessType access) {
 		Map<String, XProperty> xprops;
 		if ( AccessType.PROPERTY.equals( access ) || AccessType.DEFAULT.equals( access ) ) {
 			xprops = propertyAccessMap;
 		}
 		else {
 			xprops = fieldAccessMap;
 		}
 		for ( XProperty property : xprops.values() ) {
 			if ( !property.isTypeResolved() && !discoverTypeWithoutReflection( property ) ) {
 				String msg = "Property " + StringHelper.qualify( xClass.getName(), property.getName() ) +
 						" has an unbound type and no explicit target entity. Resolve this Generic usage issue" +
 						" or set an explicit target attribute (eg @OneToMany(target=) or use an explicit @Type";
 				throw new AnnotationException( msg );
 			}
 		}
 	}
 
 	private void considerExplicitFieldAndPropertyAccess() {
 		for ( XProperty property : fieldAccessMap.values() ) {
 			Access access = property.getAnnotation( Access.class );
 			if ( access == null ) {
 				continue;
 			}
 
 			// see "2.3.2 Explicit Access Type" of JPA 2 spec
 			// the access type for this property is explicitly set to AccessType.FIELD, hence we have to
 			// use field access for this property even if the default access type for the class is AccessType.PROPERTY
 			AccessType accessType = AccessType.getAccessStrategy( access.value() );
-            if (accessType == AccessType.FIELD) propertyAccessMap.put(property.getName(), property);
-            else LOG.annotationHasNoEffect(AccessType.FIELD);
+            if (accessType == AccessType.FIELD) {
+				propertyAccessMap.put(property.getName(), property);
+			}
+            else {
+				LOG.debug( "Placing @Access(AccessType.FIELD) on a field does not have any effect." );
+			}
 		}
 
 		for ( XProperty property : propertyAccessMap.values() ) {
 			Access access = property.getAnnotation( Access.class );
 			if ( access == null ) {
 				continue;
 			}
 
 			AccessType accessType = AccessType.getAccessStrategy( access.value() );
 
 			// see "2.3.2 Explicit Access Type" of JPA 2 spec
 			// the access type for this property is explicitly set to AccessType.PROPERTY, hence we have to
 			// return use method access even if the default class access type is AccessType.FIELD
-            if (accessType == AccessType.PROPERTY) fieldAccessMap.put(property.getName(), property);
-            else LOG.annotationHasNoEffect(AccessType.PROPERTY);
+            if (accessType == AccessType.PROPERTY) {
+				fieldAccessMap.put(property.getName(), property);
+			}
+            else {
+				LOG.debug( "Placing @Access(AccessType.PROPERTY) on a field does not have any effect." );
+			}
 		}
 	}
 
 	/**
 	 * Retrieves all properties from the {@code xClass} with the specified access type. This method does not take
 	 * any jpa access rules/annotations into account yet.
 	 *
 	 * @param access The access type - {@code AccessType.FIELD}  or {@code AccessType.Property}
 	 *
 	 * @return A maps of the properties with the given access type keyed against their property name
 	 */
 	private TreeMap<String, XProperty> initProperties(AccessType access) {
 		if ( !( AccessType.PROPERTY.equals( access ) || AccessType.FIELD.equals( access ) ) ) {
 			throw new IllegalArgumentException( "Acces type has to be AccessType.FIELD or AccessType.Property" );
 		}
 
 		//order so that property are used in the same order when binding native query
 		TreeMap<String, XProperty> propertiesMap = new TreeMap<String, XProperty>();
 		List<XProperty> properties = xClass.getDeclaredProperties( access.getType() );
 		for ( XProperty property : properties ) {
 			if ( mustBeSkipped( property ) ) {
 				continue;
 			}
 			propertiesMap.put( property.getName(), property );
 		}
 		return propertiesMap;
 	}
 
 	private AccessType determineClassDefinedAccessStrategy() {
 		AccessType classDefinedAccessType;
 
 		AccessType hibernateDefinedAccessType = AccessType.DEFAULT;
 		AccessType jpaDefinedAccessType = AccessType.DEFAULT;
 
 		org.hibernate.annotations.AccessType accessType = xClass.getAnnotation( org.hibernate.annotations.AccessType.class );
 		if ( accessType != null ) {
 			hibernateDefinedAccessType = AccessType.getAccessStrategy( accessType.value() );
 		}
 
 		Access access = xClass.getAnnotation( Access.class );
 		if ( access != null ) {
 			jpaDefinedAccessType = AccessType.getAccessStrategy( access.value() );
 		}
 
 		if ( hibernateDefinedAccessType != AccessType.DEFAULT
 				&& jpaDefinedAccessType != AccessType.DEFAULT
 				&& hibernateDefinedAccessType != jpaDefinedAccessType ) {
 			throw new MappingException(
 					"@AccessType and @Access specified with contradicting values. Use of @Access only is recommended. "
 			);
 		}
 
 		if ( hibernateDefinedAccessType != AccessType.DEFAULT ) {
 			classDefinedAccessType = hibernateDefinedAccessType;
 		}
 		else {
 			classDefinedAccessType = jpaDefinedAccessType;
 		}
 		return classDefinedAccessType;
 	}
 
 	private static boolean discoverTypeWithoutReflection(XProperty p) {
 		if ( p.isAnnotationPresent( OneToOne.class ) && !p.getAnnotation( OneToOne.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( OneToMany.class ) && !p.getAnnotation( OneToMany.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToOne.class ) && !p.getAnnotation( ManyToOne.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToMany.class ) && !p.getAnnotation( ManyToMany.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToAny.class ) ) {
 			if ( !p.isCollection() && !p.isArray() ) {
 				throw new AnnotationException( "@ManyToAny used on a non collection non array property: " + p.getName() );
 			}
 			return true;
 		}
 		else if ( p.isAnnotationPresent( Type.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( Target.class ) ) {
 			return true;
 		}
 		return false;
 	}
 
 	private static boolean mustBeSkipped(XProperty property) {
 		//TODO make those hardcoded tests more portable (through the bytecode provider?)
 		return property.isAnnotationPresent( Transient.class )
 				|| "net.sf.cglib.transform.impl.InterceptFieldCallback".equals( property.getType().getName() )
 				|| "org.hibernate.bytecode.internal.javassist.FieldHandler".equals( property.getType().getName() );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 5a475ccc43..37786ccaf5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,330 +1,365 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.ServiceRegistry;
 
 import org.jboss.logging.Logger;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
-        LOG.autoFlush(enabledDisabled(flushBeforeCompletion));
+        LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
-        LOG.autoSessionClose(enabledDisabled(autoCloseSession));
+        LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
-		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
-		if (batchSize>0) LOG.jdbcBatchSize(batchSize);
+		if ( !meta.supportsBatchUpdates() ) {
+			batchSize = 0;
+		}
+		if ( batchSize > 0 ) {
+			LOG.debugf( "JDBC batch size: %s", batchSize );
+		}
 		settings.setJdbcBatchSize(batchSize);
+
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
-        if (batchSize > 0) LOG.jdbcBatchUpdates(enabledDisabled(jdbcBatchVersionedData));
+        if ( batchSize > 0 ) {
+			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
+		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
-		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
-        LOG.scrollabelResultSets(enabledDisabled(useScrollableResultSets));
+		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
+				Environment.USE_SCROLLABLE_RESULTSET,
+				properties,
+				meta.supportsScrollableResults()
+		);
+        LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
-        LOG.wrapResultSets(enabledDisabled(wrapResultSets));
+        LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
-        LOG.jdbc3GeneratedKeys(enabledDisabled(useGetGeneratedKeys));
+        LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
-        if (statementFetchSize != null) LOG.jdbcResultSetFetchSize(statementFetchSize);
+        if (statementFetchSize != null) {
+			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
+		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
-        LOG.connectionReleaseMode(releaseModeName);
+        LOG.debugf( "Connection release mode: %s", releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
-		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
-		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
-        if (defaultSchema != null) LOG.defaultSchema(defaultSchema);
-        if (defaultCatalog != null) LOG.defaultCatalog(defaultCatalog);
-		settings.setDefaultSchemaName(defaultSchema);
-		settings.setDefaultCatalogName(defaultCatalog);
+		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
+		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
+        if ( defaultSchema != null ) {
+			LOG.debugf( "Default schema: %s", defaultSchema );
+		}
+        if (defaultCatalog != null) {
+			LOG.debugf( "Default catalog: %s", defaultCatalog );
+		}
+		settings.setDefaultSchemaName( defaultSchema );
+		settings.setDefaultCatalogName( defaultCatalog );
+
+		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
+        if ( maxFetchDepth != null ) {
+			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
+		}
+		settings.setMaximumFetchDepth( maxFetchDepth );
 
-		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
-        if (maxFetchDepth != null) LOG.maxOuterJoinFetchDepth(maxFetchDepth);
-		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
-        LOG.defaultBatchFetchSize(batchFetchSize);
-		settings.setDefaultBatchFetchSize(batchFetchSize);
+        LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
+		settings.setDefaultBatchFetchSize( batchFetchSize );
 
-		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
-        LOG.generateSqlWithComments(enabledDisabled(comments));
-		settings.setCommentsEnabled(comments);
+		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
+        LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
+		settings.setCommentsEnabled( comments );
 
-		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
-        LOG.orderSqlUpdatesByPrimaryKey(enabledDisabled(orderUpdates));
-		settings.setOrderUpdatesEnabled(orderUpdates);
+		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
+        LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
+		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
-        LOG.orderSqlInsertsForBatching(enabledDisabled(orderInserts));
+        LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
-        Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
-        LOG.queryLanguageSubstitutions(querySubstitutions);
-		settings.setQuerySubstitutions(querySubstitutions);
+        Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
+        LOG.debugf( "Query language substitutions: %s", querySubstitutions );
+		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
+		LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
-        LOG.jpaQlStrictCompliance(enabledDisabled(jpaqlCompliance));
 
 		// Second-level / query cache:
 
-		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
-        LOG.secondLevelCache(enabledDisabled(useSecondLevelCache));
-		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
+		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
+        LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
+		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
-        LOG.queryCache(enabledDisabled(useQueryCache));
-		settings.setQueryCacheEnabled(useQueryCache);
+        LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
+		settings.setQueryCacheEnabled( useQueryCache );
+		if (useQueryCache) {
+			settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
+		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
-        LOG.optimizeCacheForMinimalInputs(enabledDisabled(useMinimalPuts));
-		settings.setMinimalPutsEnabled(useMinimalPuts);
+        LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
+		settings.setMinimalPutsEnabled( useMinimalPuts );
 
-		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
-		if ( StringHelper.isEmpty(prefix) ) prefix=null;
-        if (prefix != null) LOG.cacheRegionPrefix(prefix);
-		settings.setCacheRegionPrefix(prefix);
+		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
+		if ( StringHelper.isEmpty(prefix) ) {
+			prefix=null;
+		}
+        if (prefix != null) {
+			LOG.debugf( "Cache region prefix: %s", prefix );
+		}
+		settings.setCacheRegionPrefix( prefix );
 
-		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
-        LOG.structuredSecondLevelCacheEntries(enabledDisabled(useStructuredCacheEntries));
-		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
+		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
+        LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
+		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
-		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//Statistics and logging:
 
-		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
-		LOG.statistics( enabledDisabled(useStatistics) );
-		settings.setStatisticsEnabled(useStatistics);
+		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
+		LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
+		settings.setStatisticsEnabled( useStatistics );
 
-		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
-        LOG.deletedEntitySyntheticIdentifierRollback(enabledDisabled(useIdentifierRollback));
-		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
+		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
+        LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
+		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
-		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
-		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
-		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
-		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
-		if ( "create-drop".equals(autoSchemaExport) ) {
-			settings.setAutoCreateSchema(true);
-			settings.setAutoDropSchema(true);
+		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
+		if ( "validate".equals(autoSchemaExport) ) {
+			settings.setAutoValidateSchema( true );
+		}
+		if ( "update".equals(autoSchemaExport) ) {
+			settings.setAutoUpdateSchema( true );
+		}
+		if ( "create".equals(autoSchemaExport) ) {
+			settings.setAutoCreateSchema( true );
+		}
+		if ( "create-drop".equals( autoSchemaExport ) ) {
+			settings.setAutoCreateSchema( true );
+			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
-        LOG.defaultEntityMode(defaultEntityMode);
+        LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
-        LOG.namedQueryChecking(enabledDisabled(namedQueryChecking));
+        LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
-        LOG.checkNullability(enabledDisabled(checkNullability));
+        LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
-		LOG.debug( "multi-tenancy strategy : " + multiTenancyStrategy );
+		LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debugf("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
-        LOG.queryCacheFactory(queryCacheFactoryClassName);
+        LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
-		catch (Exception cnfe) {
-			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
+		catch (Exception e) {
+			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
-        LOG.cacheRegionFactory( regionFactoryClassName );
+        LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
-			catch ( NoSuchMethodException nsme ) {
+			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
-                LOG.constructorWithPropertiesNotFound(regionFactoryClassName);
+                LOG.debugf(
+						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
+						regionFactoryClassName
+				);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
-        LOG.queryTranslator( className );
+        LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
-		catch (Exception cnfe) {
-			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
+		catch (Exception e) {
+			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index 335fdffdec..f303765bef 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,930 +1,930 @@
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
 
 import javax.persistence.Access;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
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
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Versioning;
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
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
 
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
 			if(forceDiscriminator != null) {
 				rootClass.setForceDiscriminator( forceDiscriminator );
 			}
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
 					ExecuteUpdateResultCheckStyle.parse( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDeleteAll.check().toString().toLowerCase() )
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
 				persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( annotatedClass.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = annotatedClass.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
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
         } else if (filters.size() > 0) LOG.filterAnnotationOnSubclass(persistentClass.getEntityName());
         LOG.debugf("Import with entity name %s", name);
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
             LOG.bindEntityOnTable( persistentClass.getEntityName(), table.getName() );
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
-        LOG.addingSecondaryTableToEntity( persistentClass.getEntityName(), join.getTable().getName() );
+		LOG.debugf( "Adding secondary table to entity %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null ) {
 			join.setSequentialSelect( FetchMode.JOIN != matchingTable.fetch() );
 			join.setInverse( matchingTable.inverse() );
 			join.setOptional( matchingTable.optional() );
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlInsert().sql() ) ) {
 				join.setCustomSQLInsert( matchingTable.sqlInsert().sql().trim(),
 						matchingTable.sqlInsert().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlInsert().check().toString().toLowerCase() )
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlUpdate().check().toString().toLowerCase() )
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlDelete().check().toString().toLowerCase() )
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
 
 	public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
 		org.hibernate.cache.access.AccessType accessType = strategy.toAccessType();
 		return accessType == null ? null : accessType.getName();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index d7cd094a6d..7f28b07812 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -1,1849 +1,1634 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 
-import static org.jboss.logging.Logger.Level.ERROR;
-import static org.jboss.logging.Logger.Level.INFO;
-import static org.jboss.logging.Logger.Level.WARN;
+import javax.naming.InvalidNameException;
+import javax.naming.NameNotFoundException;
+import javax.naming.NamingException;
+import javax.transaction.Synchronization;
+import javax.transaction.SystemException;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.util.Hashtable;
-import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
-import javax.naming.InvalidNameException;
-import javax.naming.NameNotFoundException;
-import javax.naming.NamingException;
-import javax.transaction.Synchronization;
-import javax.transaction.SystemException;
 
-import org.hibernate.EntityMode;
+import org.jboss.logging.BasicLogger;
+import org.jboss.logging.Cause;
+import org.jboss.logging.LogMessage;
+import org.jboss.logging.Message;
+import org.jboss.logging.MessageLogger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
-import org.hibernate.cfg.AccessType;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.loading.CollectionLoadContext;
 import org.hibernate.engine.loading.EntityLoadContext;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.service.jdbc.dialect.internal.AbstractDialectResolver;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
-import org.jboss.logging.BasicLogger;
-import org.jboss.logging.Cause;
-import org.jboss.logging.LogMessage;
-import org.jboss.logging.Message;
-import org.jboss.logging.MessageLogger;
+
+import static org.jboss.logging.Logger.Level.ERROR;
+import static org.jboss.logging.Logger.Level.INFO;
+import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * The jboss-logging {@link MessageLogger} for the hibernate-core module.  It reserves message ids ranging from
  * 00001 to 10000 inclusively.
  * <p/>
  * New messages must be added after the last message defined to ensure message codes are unique.
  */
 @MessageLogger( projectCode = "HHH" )
 public interface CoreMessageLogger extends BasicLogger {
 
-    @LogMessage( level = INFO )
-    @Message( value = "Adding secondary table to entity %s -> %s", id = 1 )
-    void addingSecondaryTableToEntity( String entity,
-                                       String table );
-
     @LogMessage( level = WARN )
     @Message( value = "Already session bound on call to bind(); make sure you clean up your sessions!", id = 2 )
     void alreadySessionBound();
 
-    @LogMessage( level = WARN )
-    @Message( value = "Placing @Access(AccessType.%s) on a field does not have any effect.", id = 3 )
-    void annotationHasNoEffect( AccessType type );
-
-    @LogMessage( level = WARN )
-    @Message( value = "Attempt to map column [%s] to no target column after explicit target column(s) named for FK [name=%s]", id = 4 )
-    void attemptToMapColumnToNoTargetColumn( String loggableString,
-                                             String name );
-
     @LogMessage( level = INFO )
     @Message( value = "Autocommit mode: %s", id = 6 )
     void autoCommitMode( boolean autocommit );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Automatic flush during beforeCompletion(): %s", id = 7 )
-    void autoFlush( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()", id = 8 )
     void autoFlushWillNotWork();
 
     @LogMessage( level = INFO )
-    @Message( value = "Automatic session close at end of transaction: %s", id = 9 )
-    void autoSessionClose( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "On release of batch it still contained JDBC statements", id = 10 )
     void batchContainedStatementsOnRelease();
 
     @LogMessage( level = INFO )
-    @Message( value = "Batcher factory: %s", id = 11 )
-    void batcherFactory( String batcherClass );
-
-    @LogMessage( level = INFO )
     @Message( value = "Bind entity %s on table %s", id = 12 )
     void bindEntityOnTable( String entity,
                             String table );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding Any Meta definition: %s", id = 13 )
     void bindingAnyMetaDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding entity from annotated class: %s", id = 14 )
     void bindingEntityFromClass( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding filter definition: %s", id = 15 )
     void bindingFilterDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding named native query: %s => %s", id = 16 )
     void bindingNamedNativeQuery( String name,
                                   String query );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding named query: %s => %s", id = 17 )
     void bindingNamedQuery( String name,
                             String query );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding result set mapping: %s", id = 18 )
     void bindingResultSetMapping( String mapping );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding type definition: %s", id = 19 )
     void bindingTypeDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Building session factory", id = 20 )
     void buildingSessionFactory();
 
     @LogMessage( level = INFO )
     @Message( value = "Bytecode provider name : %s", id = 21 )
     void bytecodeProvider( String provider );
 
     @LogMessage( level = WARN )
     @Message( value = "c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id = 22 )
     void c3p0ProviderClassNotFound( String c3p0ProviderClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "I/O reported cached file could not be found : %s : %s", id = 23 )
     void cachedFileNotFound( String path,
                              FileNotFoundException error );
 
     @LogMessage( level = INFO )
     @Message( value = "Cache provider: %s", id = 24 )
     void cacheProvider( String name );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Cache region factory : %s", id = 25 )
-    void cacheRegionFactory( String regionFactoryClassName );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Cache region prefix: %s", id = 26 )
-    void cacheRegionPrefix( String prefix );
-
     @LogMessage( level = WARN )
     @Message( value = "Calling joinTransaction() on a non JTA EntityManager", id = 27 )
     void callingJoinTransactionOnNonJtaEntityManager();
 
-    @Message( value = "CGLIB Enhancement failed: %s", id = 28 )
-    String cglibEnhancementFailed( String entityName );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Check Nullability in Core (should be disabled when Bean Validation is on): %s", id = 29 )
-    void checkNullability( String enabledDisabled );
-
     @LogMessage( level = INFO )
     @Message( value = "Cleaning up connection pool [%s]", id = 30 )
     void cleaningUpConnectionPool( String url );
 
     @LogMessage( level = INFO )
     @Message( value = "Closing", id = 31 )
     void closing();
 
     @LogMessage( level = INFO )
     @Message( value = "Collections fetched (minimize this): %s", id = 32 )
     void collectionsFetched( long collectionFetchCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections loaded: %s", id = 33 )
     void collectionsLoaded( long collectionLoadCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections recreated: %s", id = 34 )
     void collectionsRecreated( long collectionRecreateCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections removed: %s", id = 35 )
     void collectionsRemoved( long collectionRemoveCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections updated: %s", id = 36 )
     void collectionsUpdated( long collectionUpdateCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Columns: %s", id = 37 )
     void columns( Set keySet );
 
     @LogMessage( level = WARN )
     @Message( value = "Composite-id class does not override equals(): %s", id = 38 )
     void compositeIdClassDoesNotOverrideEquals( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Composite-id class does not override hashCode(): %s", id = 39 )
     void compositeIdClassDoesNotOverrideHashCode( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuration resource: %s", id = 40 )
     void configurationResource( String resource );
 
     @LogMessage( level = INFO )
     @Message( value = "Configured SessionFactory: %s", id = 41 )
     void configuredSessionFactory( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from file: %s", id = 42 )
     void configuringFromFile( String file );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from resource: %s", id = 43 )
     void configuringFromResource( String resource );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from URL: %s", id = 44 )
     void configuringFromUrl( URL url );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from XML document", id = 45 )
     void configuringFromXmlDocument();
 
     @LogMessage( level = INFO )
     @Message( value = "Connection properties: %s", id = 46 )
     void connectionProperties( Properties connectionProps );
 
     @LogMessage( level = INFO )
-    @Message( value = "Connection release mode: %s", id = 47 )
-    void connectionReleaseMode( String releaseModeName );
-
-    @LogMessage( level = INFO )
     @Message( value = "Connections obtained: %s", id = 48 )
     void connectionsObtained( long connectCount );
 
-    @LogMessage( level = INFO )
-    @Message( value = "%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.", id = 49 )
-    void constructorWithPropertiesNotFound( String regionFactoryClassName );
-
     @LogMessage( level = ERROR )
     @Message( value = "Container is providing a null PersistenceUnitRootUrl: discovery impossible", id = 50 )
     void containerProvidingNullPersistenceUnitRootUrl();
 
     @LogMessage( level = WARN )
     @Message( value = "Ignoring bag join fetch [%s] due to prior collection join fetch", id = 51 )
     void containsJoinFetchedCollection( String role );
 
-    @Message( value = "Could not close connection", id = 52 )
-    Object couldNotCloseConnection();
-
     @LogMessage( level = INFO )
     @Message( value = "Creating subcontext: %s", id = 53 )
     void creatingSubcontextInfo( String intermediateContextName );
 
     @LogMessage( level = INFO )
     @Message( value = "Database ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s", id = 54 )
     void database( String databaseProductName,
                    String databaseProductVersion,
                    int databaseMajorVersion,
                    int databaseMinorVersion );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Default batch fetch size: %s", id = 55 )
-    void defaultBatchFetchSize( int batchFetchSize );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Default catalog: %s", id = 56 )
-    void defaultCatalog( String defaultCatalog );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Default entity-mode: %s", id = 57 )
-    void defaultEntityMode( EntityMode defaultEntityMode );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Default schema: %s", id = 58 )
-    void defaultSchema( String defaultSchema );
-
     @LogMessage( level = WARN )
     @Message( value = "Defining %s=true ignored in HEM", id = 59 )
     void definingFlushBeforeCompletionIgnoredInHem( String flushBeforeCompletion );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Deleted entity synthetic identifier rollback: %s", id = 60 )
-    void deletedEntitySyntheticIdentifierRollback( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "Per HHH-5451 support for cglib as a bytecode provider has been deprecated.", id = 61 )
     void deprecated();
 
     @LogMessage( level = WARN )
     @Message( value = "@ForceDiscriminator is deprecated use @DiscriminatorOptions instead.", id = 62 )
     void deprecatedForceDescriminatorAnnotation();
 
     @LogMessage( level = WARN )
     @Message( value = "The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead", id = 63 )
     void deprecatedOracle9Dialect();
 
     @LogMessage( level = WARN )
     @Message( value = "The OracleDialect dialect has been deprecated; use Oracle8iDialect instead", id = 64 )
     void deprecatedOracleDialect();
 
     @LogMessage( level = WARN )
     @Message( value = "DEPRECATED : use {} instead with custom {} implementation", id = 65 )
     void deprecatedUuidGenerator( String name,
                                   String name2 );
 
-    @LogMessage( level = WARN )
-    @Message( value = "Dialect resolver class not found: %s", id = 66 )
-    void dialectResolverNotFound( String resolverName );
-
     @LogMessage( level = INFO )
     @Message( value = "Disallowing insert statement comment for select-identity due to Oracle driver bug", id = 67 )
     void disallowingInsertStatementComment();
 
     @LogMessage( level = INFO )
     @Message( value = "Driver ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s", id = 68 )
     void driver( String driverProductName,
                  String driverProductVersion,
                  int driverMajorVersion,
                  int driverMinorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate generator name %s", id = 69 )
     void duplicateGeneratorName( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate generator table: %s", id = 70 )
     void duplicateGeneratorTable( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Duplicate import: %s -> %s", id = 71 )
     void duplicateImport( String entityName,
                           String rename );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate joins for class: %s", id = 72 )
     void duplicateJoins( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "entity-listener duplication, first event definition will be used: %s", id = 73 )
     void duplicateListener( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Found more than one <persistence-unit-metadata>, subsequent ignored", id = 74 )
     void duplicateMetadata();
 
     @LogMessage( level = INFO )
-    @Message( value = "Echoing all SQL to stdout", id = 75 )
-    void echoingSql();
-
-    @LogMessage( level = INFO )
     @Message( value = "Entities deleted: %s", id = 76 )
     void entitiesDeleted( long entityDeleteCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities fetched (minimize this): %s", id = 77 )
     void entitiesFetched( long entityFetchCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities inserted: %s", id = 78 )
     void entitiesInserted( long entityInsertCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities loaded: %s", id = 79 )
     void entitiesLoaded( long entityLoadCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities updated: %s", id = 80 )
     void entitiesUpdated( long entityUpdateCount );
 
     @LogMessage( level = WARN )
     @Message( value = "@org.hibernate.annotations.Entity used on a non root entity: ignored for %s", id = 81 )
     void entityAnnotationOnNonRoot( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Entity Manager closed by someone else (%s must not be used)", id = 82 )
     void entityManagerClosedBySomeoneElse( String autoCloseSession );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Hibernate EntityManager %s", id = 83 )
-    void entityManagerVersion( String versionString );
-
     @LogMessage( level = WARN )
     @Message( value = "Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names", id = 84 )
     void entityMappedAsNonAbstract( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "%s %s found", id = 85 )
     void exceptionHeaderFound( String exceptionHeader,
                                String metaInfOrmXml );
 
     @LogMessage( level = INFO )
     @Message( value = "%s No %s found", id = 86 )
     void exceptionHeaderNotFound( String exceptionHeader,
                                   String metaInfOrmXml );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception in interceptor afterTransactionCompletion()", id = 87 )
     void exceptionInAfterTransactionCompletionInterceptor( @Cause Throwable e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception in interceptor beforeTransactionCompletion()", id = 88 )
     void exceptionInBeforeTransactionCompletionInterceptor( @Cause Throwable e );
 
     @LogMessage( level = INFO )
     @Message( value = "Sub-resolver threw unexpected exception, continuing to next : %s", id = 89 )
     void exceptionInSubResolver( String message );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Executing import script: %s", id = 90 )
-    void executingImportScript( String name );
-
     @LogMessage( level = ERROR )
     @Message( value = "Expected type: %s, actual value: %s", id = 91 )
     void expectedType( String name,
                        String string );
 
     @LogMessage( level = WARN )
     @Message( value = "An item was expired by the cache while it was locked (increase your cache timeout): %s", id = 92 )
     void expired( Object key );
 
     @LogMessage( level = INFO )
-    @Message( value = "Exporting generated schema to database", id = 93 )
-    void exportingGeneratedSchemaToDatabase();
-
-    @LogMessage( level = INFO )
     @Message( value = "Bound factory to JNDI name: %s", id = 94 )
     void factoryBoundToJndiName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Factory name: %s", id = 95 )
     void factoryName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "A factory was renamed from name: %s", id = 96 )
     void factoryRenamedFromName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Unbound factory from JNDI name: %s", id = 97 )
     void factoryUnboundFromJndiName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "A factory was unbound from name: %s", id = 98 )
     void factoryUnboundFromName( String name );
 
     @LogMessage( level = ERROR )
     @Message( value = "an assertion failure occured" + " (this may indicate a bug in Hibernate, but is more likely due"
                       + " to unsafe use of the session): %s", id = 99 )
     void failed( Throwable throwable );
 
     @LogMessage( level = WARN )
     @Message( value = "Fail-safe cleanup (collections) : %s", id = 100 )
     void failSafeCollectionsCleanup( CollectionLoadContext collectionLoadContext );
 
     @LogMessage( level = WARN )
     @Message( value = "Fail-safe cleanup (entities) : %s", id = 101 )
     void failSafeEntitiesCleanup( EntityLoadContext entityLoadContext );
 
     @LogMessage( level = INFO )
     @Message( value = "Fetching database metadata", id = 102 )
     void fetchingDatabaseMetadata();
 
     @LogMessage( level = WARN )
     @Message( value = "@Filter not allowed on subclasses (ignored): %s", id = 103 )
     void filterAnnotationOnSubclass( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "firstResult/maxResults specified with collection fetch; applying in memory!", id = 104 )
     void firstOrMaxResultsSpecifiedWithCollectionFetch();
 
     @LogMessage( level = INFO )
     @Message( value = "Flushes: %s", id = 105 )
     void flushes( long flushCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Forcing container resource cleanup on transaction completion", id = 106 )
     void forcingContainerResourceCleanup();
 
     @LogMessage( level = INFO )
     @Message( value = "Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences", id = 107 )
     void forcingTableUse();
 
     @LogMessage( level = INFO )
     @Message( value = "Foreign keys: %s", id = 108 )
     void foreignKeys( Set keySet );
 
     @LogMessage( level = INFO )
     @Message( value = "Found mapping document in jar: %s", id = 109 )
     void foundMappingDocument( String name );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Generate SQL with comments: %s", id = 111 )
-    void generateSqlWithComments( String enabledDisabled );
-
     @LogMessage( level = ERROR )
     @Message( value = "Getters of lazy classes cannot be final: %s.%s", id = 112 )
     void gettersOfLazyClassesCannotBeFinal( String entityName,
                                             String name );
 
     @LogMessage( level = WARN )
     @Message( value = "GUID identifier generated: %s", id = 113 )
     void guidGenerated( String result );
 
     @LogMessage( level = INFO )
     @Message( value = "Handling transient entity in delete processing", id = 114 )
     void handlingTransientEntity();
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate connection pool size: %s", id = 115 )
     void hibernateConnectionPoolSize( int poolSize );
 
     @LogMessage( level = WARN )
     @Message( value = "Config specified explicit optimizer of [%s], but [%s=%s; honoring optimizer setting", id = 116 )
     void honoringOptimizerSetting( String none,
                                    String incrementParam,
                                    int incrementSize );
 
     @LogMessage( level = INFO )
     @Message( value = "HQL: %s, time: %sms, rows: %s", id = 117 )
     void hql( String hql,
               Long valueOf,
               Long valueOf2 );
 
     @LogMessage( level = WARN )
     @Message( value = "HSQLDB supports only READ_UNCOMMITTED isolation", id = 118 )
     void hsqldbSupportsOnlyReadCommittedIsolation();
 
     @LogMessage( level = WARN )
     @Message( value = "On EntityLoadContext#clear, hydratingEntities contained [%s] entries", id = 119 )
     void hydratingEntitiesCount( int size );
 
     @LogMessage( level = WARN )
     @Message( value = "Ignoring unique constraints specified on table generator [%s]", id = 120 )
     void ignoringTableGeneratorConstraints( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Ignoring unrecognized query hint [%s]", id = 121 )
     void ignoringUnrecognizedQueryHint( String hintName );
 
     @LogMessage( level = ERROR )
     @Message( value = "IllegalArgumentException in class: %s, getter method of property: %s", id = 122 )
     void illegalPropertyGetterArgument( String name,
                                         String propertyName );
 
     @LogMessage( level = ERROR )
     @Message( value = "IllegalArgumentException in class: %s, setter method of property: %s", id = 123 )
     void illegalPropertySetterArgument( String name,
                                         String propertyName );
 
     @LogMessage( level = WARN )
     @Message( value = "@Immutable used on a non root entity: ignored for %s", id = 124 )
     void immutableAnnotationOnNonRoot( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Mapping metadata cache was not completely processed", id = 125 )
     void incompleteMappingMetadataCacheProcessing();
 
     @LogMessage( level = INFO )
     @Message( value = "Indexes: %s", id = 126 )
     void indexes( Set keySet );
 
     @LogMessage( level = WARN )
     @Message( value = "InitialContext did not implement EventContext", id = 127 )
     void initialContextDidNotImplementEventContext();
 
     @LogMessage( level = WARN )
     @Message( value = "InitialContext did not implement EventContext", id = 128 )
     void initialContextDoesNotImplementEventContext();
 
     @LogMessage( level = INFO )
-    @Message( value = "Instantiated TransactionManagerLookup", id = 129 )
-    void instantiatedTransactionManagerLookup();
-
-    @LogMessage( level = INFO )
     @Message( value = "Instantiating explicit connection provider: %s", id = 130 )
-    void instantiatingExplicitConnectinProvider( String providerClassName );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Instantiating TransactionManagerLookup: %s", id = 131 )
-    void instantiatingTransactionManagerLookup( String tmLookupClass );
+    void instantiatingExplicitConnectionProvider(String providerClassName);
 
     @LogMessage( level = ERROR )
     @Message( value = "Array element type error\n%s", id = 132 )
     void invalidArrayElementType( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s", id = 133 )
-    void invalidDescriminatorAnnotation( String className );
+    void invalidDiscriminatorAnnotation(String className);
 
     @LogMessage( level = ERROR )
     @Message( value = "Application attempted to edit read only item: %s", id = 134 )
     void invalidEditOfReadOnlyItem( Object key );
 
     @LogMessage( level = ERROR )
     @Message( value = "Invalid JNDI name: %s", id = 135 )
     void invalidJndiName( String name,
                           @Cause InvalidNameException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Inapropriate use of @OnDelete on entity, annotation ignored: %s", id = 136 )
     void invalidOnDeleteAnnotation( String entityName );
 
     @LogMessage( level = WARN )
     @Message( value = "Root entity should not hold an PrimaryKeyJoinColum(s), will be ignored", id = 137 )
     void invalidPrimaryKeyJoinColumnAnnotation();
 
     @LogMessage( level = WARN )
     @Message( value = "Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s", id = 138 )
     void invalidSubStrategy( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s", id = 139 )
     void invalidTableAnnotation( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "JACC contextID: %s", id = 140 )
     void jaccContextId( String contextId );
 
     @LogMessage( level = INFO )
     @Message( value = "java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]", id = 141 )
     void JavaSqlTypesMappedSameCodeMultipleTimes( int code,
                                                   String old,
                                                   String name );
 
     @Message( value = "Javassist Enhancement failed: %s", id = 142 )
     String javassistEnhancementFailed( String entityName );
 
-    @LogMessage( level = INFO )
-    @Message( value = "JDBC3 getGeneratedKeys(): %s", id = 143 )
-    void jdbc3GeneratedKeys( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "%s = false breaks the EJB3 specification", id = 144 )
     void jdbcAutoCommitFalseBreaksEjb3Spec( String autocommit );
 
-    @LogMessage( level = INFO )
-    @Message( value = "JDBC batch size: %s", id = 145 )
-    void jdbcBatchSize( int batchSize );
-
-    @LogMessage( level = INFO )
-    @Message( value = "JDBC batch updates for versioned data: %s", id = 146 )
-    void jdbcBatchUpdates( String enabledDisabled );
-
-    @Message( value = "JDBC begin failed", id = 147 )
-    String jdbcBeginFailed();
-
     @LogMessage( level = WARN )
     @Message( value = "No JDBC Driver class was specified by property %s", id = 148 )
     void jdbcDriverNotSpecified( String driver );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC isolation level: %s", id = 149 )
     void jdbcIsolationLevel( String isolationLevelToString );
 
-    @LogMessage( level = INFO )
-    @Message( value = "JDBC result set fetch size: %s", id = 150 )
-    void jdbcResultSetFetchSize( Integer statementFetchSize );
-
     @Message( value = "JDBC rollback failed", id = 151 )
     String jdbcRollbackFailed();
 
     @Message( value = "JDBC URL was not specified by property %s", id = 152 )
     String jdbcUrlNotSpecified( String url );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC version : %s.%s", id = 153 )
     void jdbcVersion( int jdbcMajorVersion,
                       int jdbcMinorVersion );
 
     @LogMessage( level = INFO )
     @Message( value = "JNDI InitialContext properties:%s", id = 154 )
     void jndiInitialContextProperties( Hashtable hash );
 
     @LogMessage( level = ERROR )
     @Message( value = "JNDI name %s does not handle a session factory reference", id = 155 )
     void jndiNameDoesNotHandleSessionFactoryReference( String sfJNDIName,
                                                        @Cause ClassCastException e );
 
     @LogMessage( level = INFO )
-    @Message( value = "JPA-QL strict compliance: %s", id = 156 )
-    void jpaQlStrictCompliance( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "Lazy property fetching available for: %s", id = 157 )
     void lazyPropertyFetchingAvailable( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts", id = 159 )
     void loadingCollectionKeyNotFound( CollectionKey collectionKey );
 
     @LogMessage( level = WARN )
     @Message( value = "On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries", id = 160 )
     void localLoadingCollectionKeysCount( int size );
 
     @LogMessage( level = INFO )
     @Message( value = "Logging statistics....", id = 161 )
     void loggingStatistics();
 
     @LogMessage( level = INFO )
     @Message( value = "*** Logical connection closed ***", id = 162 )
     void logicalConnectionClosed();
 
     @LogMessage( level = INFO )
     @Message( value = "Logical connection releasing its physical connection", id = 163 )
     void logicalConnectionReleasingPhysicalConnection();
 
-    @LogMessage( level = WARN )
-    @Message( value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 164 )
-    void managerLookupClassShouldBeSet();
-
     @LogMessage( level = INFO )
     @Message( value = "Mapping class: %s -> %s", id = 165 )
     void mappingClass( String entityName,
                        String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping class join: %s -> %s", id = 166 )
     void mappingClassJoin( String entityName,
                            String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping collection: %s -> %s", id = 167 )
     void mappingCollection( String name1,
                             String name2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping joined-subclass: %s -> %s", id = 168 )
     void mappingJoinedSubclass( String entityName,
                                 String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping Package %s", id = 169 )
     void mappingPackage( String packageName );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping subclass: %s -> %s", id = 170 )
     void mappingSubclass( String entityName,
                           String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping union-subclass: %s -> %s", id = 171 )
     void mappingUnionSubclass( String entityName,
                                String name );
 
     @LogMessage( level = INFO )
-    @Message( value = "Maximum outer join fetch depth: %s", id = 172 )
-    void maxOuterJoinFetchDepth( Integer maxFetchDepth );
-
-    @LogMessage( level = INFO )
     @Message( value = "Max query time: %sms", id = 173 )
     void maxQueryTime( long queryExecutionMaxTime );
 
     @LogMessage( level = WARN )
     @Message( value = "Function template anticipated %s arguments, but %s arguments encountered", id = 174 )
     void missingArguments( int anticipatedNumberOfArguments,
                            int numberOfArguments );
 
     @LogMessage( level = WARN )
     @Message( value = "Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s", id = 175 )
     void missingEntityAnnotation( String className );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Named query checking : %s", id = 176 )
-    void namedQueryChecking( String enabledDisabled );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error in named query: %s", id = 177 )
     void namedQueryError( String queryName,
                           @Cause HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Naming exception occurred accessing factory: %s", id = 178 )
     void namingExceptionAccessingFactory( NamingException exception );
 
     @LogMessage( level = WARN )
     @Message( value = "Narrowing proxy to %s - this operation breaks ==", id = 179 )
     void narrowingProxy( Class concreteProxyClass );
 
     @LogMessage( level = WARN )
     @Message( value = "FirstResult/maxResults specified on polymorphic query; applying in memory!", id = 180 )
     void needsLimit();
 
     @LogMessage( level = WARN )
     @Message( value = "No appropriate connection provider encountered, assuming application will be supplying connections", id = 181 )
     void noAppropriateConnectionProvider();
 
     @LogMessage( level = INFO )
     @Message( value = "No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)", id = 182 )
     void noDefaultConstructor( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "no persistent classes found for query class: %s", id = 183 )
     void noPersistentClassesFound( String query );
 
     @LogMessage( level = ERROR )
     @Message( value = "No session factory with JNDI name %s", id = 184 )
     void noSessionFactoryWithJndiName( String sfJNDIName,
                                        @Cause NameNotFoundException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Not binding factory to JNDI, no JNDI name configured", id = 185 )
     void notBindingFactoryToJndi();
 
     @LogMessage( level = INFO )
-    @Message( value = "Obtaining TransactionManager", id = 186 )
-    void obtainingTransactionManager();
-
-    @LogMessage( level = INFO )
     @Message( value = "Optimistic lock failures: %s", id = 187 )
     void optimisticLockFailures( long optimisticFailureCount );
 
-    @LogMessage( level = INFO )
-    @Message( value = "Optimize cache for minimal puts: %s", id = 188 )
-    void optimizeCacheForMinimalInputs( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "@OrderBy not allowed for an indexed collection, annotation ignored.", id = 189 )
     void orderByAnnotationIndexedCollection();
 
-    @LogMessage( level = INFO )
-    @Message( value = "Order SQL inserts for batching: %s", id = 191 )
-    void orderSqlInsertsForBatching( String enabledDisabled );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Order SQL updates by primary key: %s", id = 192 )
-    void orderSqlUpdatesByPrimaryKey( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "Overriding %s is dangerous, this might break the EJB3 specification implementation", id = 193 )
     void overridingTransactionStrategyDangerous( String transactionStrategy );
 
     @LogMessage( level = WARN )
     @Message( value = "Package not found or wo package-info.java: %s", id = 194 )
     void packageNotFound( String packageName );
 
     @LogMessage( level = WARN )
     @Message( value = "Parameter position [%s] occurred as both JPA and Hibernate positional parameter", id = 195 )
     void parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter( Integer position );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error parsing XML (%s) : %s", id = 196 )
     void parsingXmlError( int lineNumber,
                           String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error parsing XML: %s(%s) %s", id = 197 )
     void parsingXmlErrorForFile( String file,
                                  int lineNumber,
                                  String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Warning parsing XML (%s) : %s", id = 198 )
     void parsingXmlWarning( int lineNumber,
                             String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Warning parsing XML: %s(%s) %s", id = 199 )
     void parsingXmlWarningForFile( String file,
                                    int lineNumber,
                                    String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Persistence provider caller does not implement the EJB3 spec correctly."
                       + "PersistenceUnitInfo.getNewTempClassLoader() is null.", id = 200 )
     void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 
     @LogMessage( level = INFO )
     @Message( value = "Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended", id = 201 )
     void pooledOptimizerReportedInitialValue( IntegralDataTypeHolder value );
 
     @LogMessage( level = ERROR )
     @Message( value = "PreparedStatement was already in the batch, [%s].", id = 202 )
     void preparedStatementAlreadyInBatch( String sql );
 
     @LogMessage( level = WARN )
     @Message( value = "processEqualityExpression() : No expression to process!", id = 203 )
     void processEqualityExpression();
 
     @LogMessage( level = INFO )
     @Message( value = "Processing PersistenceUnitInfo [\n\tname: %s\n\t...]", id = 204 )
     void processingPersistenceUnitInfoName( String persistenceUnitName );
 
     @LogMessage( level = INFO )
     @Message( value = "Loaded properties from resource hibernate.properties: %s", id = 205 )
     void propertiesLoaded( Properties maskOut );
 
     @LogMessage( level = INFO )
     @Message( value = "hibernate.properties not found", id = 206 )
     void propertiesNotFound();
 
     @LogMessage( level = WARN )
     @Message( value = "Property %s not found in class but described in <mapping-file/> (possible typo error)", id = 207 )
     void propertyNotFound( String property );
 
     @LogMessage( level = WARN )
     @Message( value = "%s has been deprecated in favor of %s; that provider will be used instead.", id = 208 )
     void providerClassDeprecated( String providerClassName,
                                   String actualProviderClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id = 209 )
     void proxoolProviderClassNotFound( String proxoolProviderClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "Queries executed to database: %s", id = 210 )
     void queriesExecuted( long queryExecutionCount );
 
     @LogMessage( level = INFO )
-    @Message( value = "Query cache: %s", id = 211 )
-    void queryCache( String enabledDisabled );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Query cache factory: %s", id = 212 )
-    void queryCacheFactory( String queryCacheFactoryClassName );
-
-    @LogMessage( level = INFO )
     @Message( value = "Query cache hits: %s", id = 213 )
     void queryCacheHits( long queryCacheHitCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache misses: %s", id = 214 )
     void queryCacheMisses( long queryCacheMissCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache puts: %s", id = 215 )
     void queryCachePuts( long queryCachePutCount );
 
     @LogMessage( level = INFO )
-    @Message( value = "Query language substitutions: %s", id = 216 )
-    void queryLanguageSubstitutions( Map querySubstitutions );
-
-    @LogMessage( level = INFO )
-    @Message( value = "Query translator: %s", id = 217 )
-    void queryTranslator( String className );
-
-    @LogMessage( level = INFO )
     @Message( value = "RDMSOS2200Dialect version: 1.0", id = 218 )
     void rdmsOs2200Dialect();
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from cache file: %s", id = 219 )
     void readingCachedMappings( File cachedFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from file: %s", id = 220 )
     void readingMappingsFromFile( String path );
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from resource: %s", id = 221 )
     void readingMappingsFromResource( String resourceName );
 
     @LogMessage( level = WARN )
     @Message( value = "read-only cache configured for mutable collection [%s]", id = 222 )
     void readOnlyCacheConfiguredForMutableCollection( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!", id = 223 )
     void recognizedObsoleteHibernateNamespace( String oldHibernateNamespace,
                                                String hibernateNamespace );
 
     @LogMessage( level = WARN )
-    @Message( value = "Reconnecting the same connection that is already connected; should this connection have been disconnected?", id = 224 )
-    void reconnectingConnectedConnection();
-
-    @LogMessage( level = WARN )
     @Message( value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225 )
     void renamedProperty( Object propertyName,
                           Object newPropertyName );
 
     @LogMessage( level = INFO )
     @Message( value = "Required a different provider: %s", id = 226 )
     void requiredDifferentProvider( String provider );
 
     @LogMessage( level = INFO )
     @Message( value = "Running hbm2ddl schema export", id = 227 )
     void runningHbm2ddlSchemaExport();
 
     @LogMessage( level = INFO )
     @Message( value = "Running hbm2ddl schema update", id = 228 )
     void runningHbm2ddlSchemaUpdate();
 
     @LogMessage( level = INFO )
     @Message( value = "Running schema validator", id = 229 )
     void runningSchemaValidator();
 
     @LogMessage( level = INFO )
     @Message( value = "Schema export complete", id = 230 )
     void schemaExportComplete();
 
     @LogMessage( level = ERROR )
     @Message( value = "Schema export unsuccessful", id = 231 )
     void schemaExportUnsuccessful( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Schema update complete", id = 232 )
     void schemaUpdateComplete();
 
     @LogMessage( level = WARN )
     @Message( value = "Scoping types to session factory %s after already scoped %s", id = 233 )
     void scopingTypesToSessionFactoryAfterAlreadyScoped( SessionFactoryImplementor factory,
                                                          SessionFactoryImplementor factory2 );
 
     @LogMessage( level = INFO )
-    @Message( value = "Scrollable result sets: %s", id = 234 )
-    void scrollabelResultSets( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "Searching for mapping documents in jar: %s", id = 235 )
     void searchingForMappingDocuments( String name );
 
     @LogMessage( level = INFO )
-    @Message( value = "Second-level cache: %s", id = 236 )
-    void secondLevelCache( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "Second level cache hits: %s", id = 237 )
     void secondLevelCacheHits( long secondLevelCacheHitCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Second level cache misses: %s", id = 238 )
     void secondLevelCacheMisses( long secondLevelCacheMissCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Second level cache puts: %s", id = 239 )
     void secondLevelCachePuts( long secondLevelCachePutCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Service properties: %s", id = 240 )
     void serviceProperties( Properties properties );
 
     @LogMessage( level = INFO )
     @Message( value = "Sessions closed: %s", id = 241 )
     void sessionsClosed( long sessionCloseCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Sessions opened: %s", id = 242 )
     void sessionsOpened( long sessionOpenCount );
 
     @LogMessage( level = ERROR )
     @Message( value = "Setters of lazy classes cannot be final: %s.%s", id = 243 )
     void settersOfLazyClassesCannotBeFinal( String entityName,
                                             String name );
 
     @LogMessage( level = WARN )
     @Message( value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244 )
     void sortAnnotationIndexedCollection();
 
     @LogMessage( level = WARN )
     @Message( value = "Manipulation query [%s] resulted in [%s] split queries", id = 245 )
     void splitQueries( String sourceQuery,
                        int length );
 
     @LogMessage( level = ERROR )
     @Message( value = "SQLException escaped proxy", id = 246 )
     void sqlExceptionEscapedProxy( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "SQL Error: %s, SQLState: %s", id = 247 )
     void sqlWarning( int errorCode,
                      String sqlState );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting query cache at region: %s", id = 248 )
     void startingQueryCache( String region );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting service at JNDI name: %s", id = 249 )
     void startingServiceAtJndiName( String boundName );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting update timestamps cache at region: %s", id = 250 )
     void startingUpdateTimestampsCache( String region );
 
     @LogMessage( level = INFO )
     @Message( value = "Start time: %s", id = 251 )
     void startTime( long startTime );
 
     @LogMessage( level = INFO )
     @Message( value = "Statements closed: %s", id = 252 )
     void statementsClosed( long closeStatementCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Statements prepared: %s", id = 253 )
     void statementsPrepared( long prepareStatementCount );
 
     @LogMessage( level = INFO )
-    @Message( value = "Statistics: %s", id = 254 )
-    void statistics( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "Stopping service", id = 255 )
     void stoppingService();
 
     @LogMessage( level = INFO )
-    @Message( value = "Structured second-level cache entries: %s", id = 256 )
-    void structuredSecondLevelCacheEntries( String enabledDisabled );
-
-    @LogMessage( level = INFO )
     @Message( value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257 )
     void subResolverException( String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Successful transactions: %s", id = 258 )
     void successfulTransactions( long committedTransactionCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Synchronization [%s] was already registered", id = 259 )
     void synchronizationAlreadyRegistered( Synchronization synchronization );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception calling user Synchronization [%s] : %s", id = 260 )
     void synchronizationFailed( Synchronization synchronization,
                                 Throwable t );
 
     @LogMessage( level = INFO )
     @Message( value = "Table found: %s", id = 261 )
     void tableFound( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Table not found: %s", id = 262 )
     void tableNotFound( String name );
 
-    @Message( value = "TransactionFactory class not found: %s", id = 263 )
-    String transactionFactoryClassNotFound( String strategyClassName );
-
-    @LogMessage( level = INFO )
-    @Message( value = "No TransactionManagerLookup configured (in JTA environment, use of read-write or transactional second-level cache is not recommended)", id = 264 )
-    void transactionManagerLookupNotConfigured();
-
-    @LogMessage( level = WARN )
-    @Message( value = "Transaction not available on beforeCompletion: assuming valid", id = 265 )
-    void transactionNotAvailableOnBeforeCompletion();
-
     @LogMessage( level = INFO )
     @Message( value = "Transactions: %s", id = 266 )
     void transactions( long transactionCount );
 
     @LogMessage( level = WARN )
     @Message( value = "Transaction started on non-root session", id = 267 )
     void transactionStartedOnNonRootSession();
 
     @LogMessage( level = INFO )
     @Message( value = "Transaction strategy: %s", id = 268 )
     void transactionStrategy( String strategyClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "Type [%s] defined no registration keys; ignoring", id = 269 )
     void typeDefinedNoRegistrationKeys( BasicType type );
 
     @LogMessage( level = INFO )
     @Message( value = "Type registration [%s] overrides previous : %s", id = 270 )
     void typeRegistrationOverridesPrevious( String key,
                                             Type old );
 
     @LogMessage( level = WARN )
     @Message( value = "Naming exception occurred accessing Ejb3Configuration", id = 271 )
     void unableToAccessEjb3Configuration( @Cause NamingException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error while accessing session factory with JNDI name %s", id = 272 )
     void unableToAccessSessionFactory( String sfJNDIName,
                                        @Cause NamingException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Error accessing type info result set : %s", id = 273 )
     void unableToAccessTypeInfoResultSet( String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to apply constraints on DDL for %s", id = 274 )
     void unableToApplyConstraints( String className,
                                    @Cause Exception e );
 
-    @Message( value = "JTA transaction begin failed", id = 275 )
-    String unableToBeginJtaTransaction();
-
     @LogMessage( level = WARN )
     @Message( value = "Could not bind Ejb3Configuration to JNDI", id = 276 )
     void unableToBindEjb3ConfigurationToJndi( @Cause NamingException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not bind factory to JNDI", id = 277 )
     void unableToBindFactoryToJndi( @Cause NamingException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not bind value '%s' to parameter: %s; %s", id = 278 )
     void unableToBindValueToParameter( String nullSafeToString,
                                        int index,
                                        String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to build enhancement metamodel for %s", id = 279 )
     void unableToBuildEnhancementMetamodel( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s", id = 280 )
     void unableToBuildSessionFactoryUsingMBeanClasspath( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to clean up callable statement", id = 281 )
     void unableToCleanUpCallableStatement( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to clean up prepared statement", id = 282 )
     void unableToCleanUpPreparedStatement( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to cleanup temporary id table after use [%s]", id = 283 )
     void unableToCleanupTemporaryIdTable( Throwable t );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing connection", id = 284 )
     void unableToCloseConnection( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error closing InitialContext [%s]", id = 285 )
     void unableToCloseInitialContext( String string );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing input files: %s", id = 286 )
     void unableToCloseInputFiles( String name,
                                   @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not close input stream", id = 287 )
     void unableToCloseInputStream( @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not close input stream for %s", id = 288 )
     void unableToCloseInputStreamForResource( String resourceName,
                                               @Cause IOException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to close iterator", id = 289 )
     void unableToCloseIterator( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close jar: %s", id = 290 )
     void unableToCloseJar( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing output file: %s", id = 291 )
     void unableToCloseOutputFile( String outputFile,
                                   @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "IOException occurred closing output stream", id = 292 )
     void unableToCloseOutputStream( @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Problem closing pooled connection", id = 293 )
     void unableToClosePooledConnection( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close session", id = 294 )
     void unableToCloseSession( @Cause HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close session during rollback", id = 295 )
     void unableToCloseSessionDuringRollback( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "IOException occurred closing stream", id = 296 )
     void unableToCloseStream( @Cause IOException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close stream on hibernate.properties: %s", id = 297 )
     void unableToCloseStreamError( IOException error );
 
     @Message( value = "JTA commit failed", id = 298 )
     String unableToCommitJta();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not complete schema update", id = 299 )
     void unableToCompleteSchemaUpdate( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not complete schema validation", id = 300 )
     void unableToCompleteSchemaValidation( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to configure SQLExceptionConverter : %s", id = 301 )
     void unableToConfigureSqlExceptionConverter( HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to construct current session context [%s]", id = 302 )
     void unableToConstructCurrentSessionContext( String impl,
                                                  @Cause Throwable e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303 )
     void unableToConstructSqlExceptionConverter( Throwable t );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not copy system properties, system properties will be ignored", id = 304 )
     void unableToCopySystemProperties();
 
     @LogMessage( level = WARN )
     @Message( value = "Could not create proxy factory for:%s", id = 305 )
     void unableToCreateProxyFactory( String entityName,
                                      @Cause HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error creating schema ", id = 306 )
     void unableToCreateSchema( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not deserialize cache file: %s : %s", id = 307 )
     void unableToDeserializeCache( String path,
                                    SerializationException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy cache: %s", id = 308 )
     void unableToDestroyCache( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy query cache: %s: %s", id = 309 )
     void unableToDestroyQueryCache( String region,
                                     String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy update timestamps cache: %s: %s", id = 310 )
     void unableToDestroyUpdateTimestampsCache( String region,
                                                String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to determine lock mode value : %s -> %s", id = 311 )
     void unableToDetermineLockModeValue( String hintName,
                                          Object value );
 
     @Message( value = "Could not determine transaction status", id = 312 )
     String unableToDetermineTransactionStatus();
 
     @Message( value = "Could not determine transaction status after commit", id = 313 )
     String unableToDetermineTransactionStatusAfterCommit();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to drop temporary id table after use [%s]", id = 314 )
     void unableToDropTemporaryIdTable( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception executing batch [%s]", id = 315 )
     void unableToExecuteBatch( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Error executing resolver [%s] : %s", id = 316 )
     void unableToExecuteResolver( AbstractDialectResolver abstractDialectResolver,
                                   String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to find %s on the classpath. Hibernate Search is not enabled.", id = 317 )
     void unableToFindListenerClass( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318 )
     void unableToFindPersistenceXmlInClasspath();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not get database metadata", id = 319 )
     void unableToGetDatabaseMetadata( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320 )
     void unableToInstantiateConfiguredSchemaNameResolver( String resolverClassName,
                                                           String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not instantiate dialect resolver class : %s", id = 321 )
     void unableToInstantiateDialectResolver( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322 )
     void unableToInstantiateOptimizer( String type );
 
     @Message( value = "Failed to instantiate TransactionFactory", id = 323 )
     String unableToInstantiateTransactionFactory();
 
     @Message( value = "Failed to instantiate TransactionManagerLookup '%s'", id = 324 )
     String unableToInstantiateTransactionManagerLookup( String tmLookupClass );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate UUID generation strategy class : %s", id = 325 )
     void unableToInstantiateUuidGenerationStrategy( Exception ignore );
 
     @LogMessage( level = WARN )
     @Message( value = "Cannot join transaction: do not override %s", id = 326 )
     void unableToJoinTransaction( String transactionStrategy );
 
     @LogMessage( level = INFO )
     @Message( value = "Error performing load command : %s", id = 327 )
     void unableToLoadCommand( HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328 )
     void unableToLoadDerbyDriver( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Problem loading properties from hibernate.properties", id = 329 )
     void unableToloadProperties();
 
     @Message( value = "Unable to locate config file: %s", id = 330 )
     String unableToLocateConfigFile( String path );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate configured schema name resolver class [%s] %s", id = 331 )
     void unableToLocateConfiguredSchemaNameResolver( String resolverClassName,
                                                      String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate MBeanServer on JMX service shutdown", id = 332 )
     void unableToLocateMBeanServer();
 
     @LogMessage( level = INFO )
     @Message( value = "Could not locate 'java.sql.NClob' class; assuming JDBC 3", id = 333 )
     void unableToLocateNClobClass();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate requested UUID generation strategy class : %s", id = 334 )
     void unableToLocateUuidGenerationStrategy( String strategyClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to log SQLWarnings : %s", id = 335 )
     void unableToLogSqlWarnings( SQLException sqle );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not log warnings", id = 336 )
     void unableToLogWarnings( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to mark for rollback on PersistenceException: ", id = 337 )
     void unableToMarkForRollbackOnPersistenceException( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to mark for rollback on TransientObjectException: ", id = 338 )
     void unableToMarkForRollbackOnTransientObjectException( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection metadata: %s", id = 339 )
     void unableToObjectConnectionMetadata( SQLException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection to query metadata: %s", id = 340 )
     void unableToObjectConnectionToQueryMetadata( SQLException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection metadata : %s", id = 341 )
     void unableToObtainConnectionMetadata( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection to query metadata : %s", id = 342 )
     void unableToObtainConnectionToQueryMetadata( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not obtain initial context", id = 343 )
     void unableToObtainInitialContext( @Cause NamingException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not parse the package-level metadata [%s]", id = 344 )
     void unableToParseMetadata( String packageName );
 
     @Message( value = "JDBC commit failed", id = 345 )
     String unableToPerformJdbcCommit();
 
     @LogMessage( level = ERROR )
     @Message( value = "Error during managed flush [%s]", id = 346 )
     void unableToPerformManagedFlush( String message );
 
     @Message( value = "Unable to query java.sql.DatabaseMetaData", id = 347 )
     String unableToQueryDatabaseMetadata();
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to read class: %s", id = 348 )
     void unableToReadClass( String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not read column value from result set: %s; %s", id = 349 )
     void unableToReadColumnValueFromResultSet( String name,
                                                String message );
 
     @Message( value = "Could not read a hi value - you need to populate the table: %s", id = 350 )
     String unableToReadHiValue( String tableName );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not read or init a hi value", id = 351 )
     void unableToReadOrInitHiValue( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to release batch statement...", id = 352 )
     void unableToReleaseBatchStatement();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not release a cache lock : %s", id = 353 )
     void unableToReleaseCacheLock( CacheException ce );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to release initial context: %s", id = 354 )
     void unableToReleaseContext( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to release created MBeanServer : %s", id = 355 )
     void unableToReleaseCreatedMBeanServer( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to release isolated connection [%s]", id = 356 )
     void unableToReleaseIsolatedConnection( Throwable ignore );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to release type info result set", id = 357 )
     void unableToReleaseTypeInfoResultSet();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to erase previously added bag join fetch", id = 358 )
     void unableToRemoveBagJoinFetch();
 
     @LogMessage( level = INFO )
     @Message( value = "Could not resolve aggregate function {}; using standard definition", id = 359 )
     void unableToResolveAggregateFunction( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to resolve mapping file [%s]", id = 360 )
     void unableToResolveMappingFile( String xmlFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to retreive cache from JNDI [%s]: %s", id = 361 )
     void unableToRetrieveCache( String namespace,
                                 String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to retrieve type info result set : %s", id = 362 )
     void unableToRetrieveTypeInfoResultSet( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to rollback connection on exception [%s]", id = 363 )
     void unableToRollbackConnection( Exception ignore );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364 )
     void unableToRollbackIsolatedTransaction( Exception e,
                                               Exception ignore );
 
     @Message( value = "JTA rollback failed", id = 365 )
     String unableToRollbackJta();
 
     @LogMessage( level = ERROR )
     @Message( value = "Error running schema update", id = 366 )
     void unableToRunSchemaUpdate( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not set transaction to rollback only", id = 367 )
     void unableToSetTransactionToRollbackOnly( @Cause SystemException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Exception while stopping service", id = 368 )
     void unableToStopHibernateService( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error stopping service [%s] : %s", id = 369 )
     void unableToStopService( Class class1,
                               String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]", id = 370 )
     void unableToSwitchToMethodUsingColumnIndex( Method method );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not synchronize database state with session: %s", id = 371 )
     void unableToSynchronizeDatabaseStateWithSession( HibernateException he );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not toggle autocommit", id = 372 )
     void unableToToggleAutoCommit( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to transform class: %s", id = 373 )
     void unableToTransformClass( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not unbind factory from JNDI", id = 374 )
     void unableToUnbindFactoryFromJndi( @Cause NamingException e );
 
     @Message( value = "Could not update hi value in: %s", id = 375 )
     Object unableToUpdateHiValue( String tableName );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not updateQuery hi value in: %s", id = 376 )
     void unableToUpdateQueryHiValue( String tableName,
                                      @Cause SQLException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error wrapping result set", id = 377 )
     void unableToWrapResultSet( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "I/O reported error writing cached file : %s: %s", id = 378 )
     void unableToWriteCachedFile( String path,
                                   String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unbinding factory from JNDI name: %s", id = 379 )
     void unbindingFactoryFromJndiName( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Unexpected literal token type [%s] passed for numeric processing", id = 380 )
     void unexpectedLiteralTokenType( int type );
 
     @LogMessage( level = WARN )
     @Message( value = "JDBC driver did not return the expected number of row counts", id = 381 )
     void unexpectedRowCounts();
 
     @LogMessage( level = WARN )
     @Message( value = "unrecognized bytecode provider [%s], using javassist by default", id = 382 )
     void unknownBytecodeProvider( String providerName );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383 )
     void unknownIngresVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Oracle major version [%s]", id = 384 )
     void unknownOracleVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385 )
     void unknownSqlServerVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "ResultSet had no statement associated with it, but was not yet registered", id = 386 )
     void unregisteredResultSetWithoutStatement();
 
     @LogMessage( level = WARN )
     @Message( value = "ResultSet's statement was not registered", id = 387 )
     void unregisteredStatement();
 
     @LogMessage( level = ERROR )
     @Message( value = "Unsuccessful: %s", id = 388 )
     void unsuccessful( String sql );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unsuccessful: %s", id = 389 )
     void unsuccessfulCreate( String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390 )
     void unsupportedAfterStatement();
 
     @LogMessage( level = WARN )
     @Message( value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391 )
     void unsupportedIngresVersion();
 
     @LogMessage( level = WARN )
     @Message( value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392 )
     void unsupportedInitialValue( String propertyName );
 
     @LogMessage( level = WARN )
     @Message( value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly", id = 393 )
     void unsupportedMultiTableBulkHqlJpaql( int majorVersion,
                                             int minorVersion,
                                             int buildId );
 
     @LogMessage( level = WARN )
     @Message( value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394 )
     void unsupportedOracleVersion();
 
     @LogMessage( level = WARN )
     @Message( value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395 )
     void unsupportedProperty( Object propertyName,
                               Object newPropertyName );
 
     @LogMessage( level = INFO )
     @Message( value = "Updating schema", id = 396 )
     void updatingSchema();
 
     @LogMessage( level = INFO )
     @Message( value = "Using ASTQueryTranslatorFactory", id = 397 )
     void usingAstQueryTranslatorFactory();
 
     @LogMessage( level = INFO )
     @Message( value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398 )
     void usingDefaultIdGeneratorSegmentValue( String tableName,
                                               String segmentColumnName,
                                               String defaultToUse );
 
     @LogMessage( level = INFO )
     @Message( value = "Using default transaction strategy (direct JDBC transactions)", id = 399 )
     void usingDefaultTransactionStrategy();
 
     @LogMessage( level = INFO )
     @Message( value = "Using dialect: %s", id = 400 )
     void usingDialect( Dialect dialect );
 
     @LogMessage( level = INFO )
     @Message( value = "using driver [%s] at URL [%s]", id = 401 )
     void usingDriver( String driverClassName,
                       String url );
 
     @LogMessage( level = INFO )
     @Message( value = "Using Hibernate built-in connection pool (not for production use!)", id = 402 )
     void usingHibernateBuiltInConnectionPool();
 
     @LogMessage( level = ERROR )
     @Message( value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404 )
     void usingOldDtd();
 
     @LogMessage( level = INFO )
     @Message( value = "Using bytecode reflection optimizer", id = 406 )
     void usingReflectionOptimizer();
 
     @LogMessage( level = INFO )
     @Message( value = "Using java.io streams to persist binary types", id = 407 )
     void usingStreams();
 
     @LogMessage( level = INFO )
     @Message( value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408 )
     void usingTimestampWorkaround();
 
     @LogMessage( level = WARN )
     @Message( value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead", id = 409 )
     void usingUuidHexGenerator( String name,
                                 String name2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate Validator not found: ignoring", id = 410 )
     void validatorNotFound();
 
-    @LogMessage( level = WARN )
-    @Message( value = "Value mapping mismatch as part of FK [table=%s, name=%s] while adding source column [%s]", id = 411 )
-    void valueMappingMismatch( String loggableString,
-                               String name,
-                               String loggableString2 );
-
     @LogMessage( level = INFO )
     @Message( value = "Hibernate %s", id = 412 )
     void version( String versionString );
 
     @LogMessage( level = WARN )
     @Message( value = "Warnings creating temp table : %s", id = 413 )
     void warningsCreatingTempTable( SQLWarning warning );
 
     @LogMessage( level = INFO )
     @Message( value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.", id = 414 )
     void willNotRegisterListeners();
 
-    @LogMessage( level = INFO )
-    @Message( value = "Wrap result sets: %s", id = 415 )
-    void wrapResultSets( String enabledDisabled );
-
     @LogMessage( level = WARN )
     @Message( value = "Write locks via update not supported for non-versioned entities [%s]", id = 416 )
     void writeLocksNotSupported( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "Writing generated schema to file: %s", id = 417 )
     void writingGeneratedSchemaToFile( String outputFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Adding override for %s: %s", id = 418 )
     void addingOverrideFor( String name,
                             String name2 );
 
     @LogMessage( level = WARN )
     @Message( value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s", id = 419 )
     void resolvedSqlTypeDescriptorForDifferentSqlCode( String name,
                                                        String valueOf,
                                                        String name2,
                                                        String valueOf2 );
 
     @LogMessage( level = WARN )
     @Message( value = "Closing un-released batch", id = 420 )
     void closingUnreleasedBatch();
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as %s is true", id = 421 )
     void disablingContextualLOBCreation( String nonContextualLobCreation );
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as connection was null", id = 422 )
     void disablingContextualLOBCreationSinceConnectionNull();
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4", id = 423 )
     void disablingContextualLOBCreationSinceOldJdbcVersion( int jdbcMajorVersion );
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424 )
     void disablingContextualLOBCreationSinceCreateClobFailed( Throwable t );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not close session; swallowing exception as transaction completed", id = 425 )
     void unableToCloseSessionButSwallowingError( HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 426 )
     void setManagerLookupClass();
 
     @LogMessage( level = WARN )
     @Message( value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427 )
     void deprecatedTransactionManagerStrategy( String name,
                                                String transactionManagerStrategy,
                                                String name2,
                                                String jtaPlatform );
 
     @LogMessage( level = INFO )
     @Message( value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting", id = 428 )
     void legacyTransactionManagerStrategy( String name,
                                            String jtaPlatform );
 
     @LogMessage( level = WARN )
     @Message( value = "Setting entity-identifier value binding where one already existed : %s.", id = 429 )
 	void entityIdentifierValueBindingExists(String name);
 
     @LogMessage( level = WARN )
     @Message( value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead", id = 430 )
     void deprecatedDerbyDialect();
 
 	@LogMessage( level = WARN )
 	@Message( value = "Unable to determine H2 database version, certain features may not work", id = 431 )
 	void undeterminedH2Version();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java
index 2f1b770e80..4a046abe14 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java
@@ -1,128 +1,139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.Logger.Level;
 
 /**
  * Models the notion of a foreign key.
  * <p/>
  * Note that this need not mean a physical foreign key; we just mean a relationship between 2 table
  * specifications.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class ForeignKey extends AbstractConstraint implements Constraint, Exportable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AbstractConstraint.class.getName());
 
 	private final TableSpecification targetTable;
 	private List<Column> targetColumns;
 	private ReferentialAction deleteRule = ReferentialAction.NO_ACTION;
 	public ReferentialAction updateRule = ReferentialAction.NO_ACTION;
 
 	protected ForeignKey(TableSpecification sourceTable, TableSpecification targetTable, String name) {
 		super( sourceTable, name );
 		this.targetTable = targetTable;
 	}
 
 	protected ForeignKey(TableSpecification sourceTable, TableSpecification targetTable) {
 		this( sourceTable, targetTable, null );
 	}
 
 	public TableSpecification getSourceTable() {
 		return getTable();
 	}
 
 	public TableSpecification getTargetTable() {
 		return targetTable;
 	}
 
 	public Iterable<Column> getSourceColumns() {
 		return getColumns();
 	}
 
 	public Iterable<Column> getTargetColumns() {
 		return targetColumns == null
 				? getTargetTable().getPrimaryKey().getColumns()
 				: targetColumns;
 	}
 
 	@Override
 	public void addColumn(Column column) {
 		addColumnMapping( column, null );
 	}
 
 	public void addColumnMapping(Column sourceColumn, Column targetColumn) {
 		if ( targetColumn == null ) {
 			if ( targetColumns != null ) {
-				if (LOG.isEnabled( Level.WARN )) LOG.attemptToMapColumnToNoTargetColumn(sourceColumn.toLoggableString(), getName());
+				LOG.debugf(
+						"Attempt to map column [%s] to no target column after explicit target column(s) named for FK [name=%s]",
+						sourceColumn.toLoggableString(),
+						getName()
+				);
 			}
 		}
 		else {
 			if ( targetColumns == null ) {
-				if (!internalColumnAccess().isEmpty()) LOG.valueMappingMismatch(getTable().toLoggableString(), getName(), sourceColumn.toLoggableString());
+				if (!internalColumnAccess().isEmpty()) {
+					LOG.debugf(
+							"Value mapping mismatch as part of FK [table=%s, name=%s] while adding source column [%s]",
+							getTable().toLoggableString(),
+							getName(),
+							sourceColumn.toLoggableString()
+					);
+				}
 				targetColumns = new ArrayList<Column>();
 			}
 			targetColumns.add( targetColumn );
 		}
 		internalColumnAccess().add( sourceColumn );
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return getSourceTable().getLoggableValueQualifier() + ".FK-" + getName();
 	}
 
 	public ReferentialAction getDeleteRule() {
 		return deleteRule;
 	}
 
 	public void setDeleteRule(ReferentialAction deleteRule) {
 		this.deleteRule = deleteRule;
 	}
 
 	public ReferentialAction getUpdateRule() {
 		return updateRule;
 	}
 
 	public void setUpdateRule(ReferentialAction updateRule) {
 		this.updateRule = updateRule;
 	}
 
 	public static enum ReferentialAction {
 		NO_ACTION,
 		CASCADE,
 		SET_NULL,
 		SET_DEFAULT,
 		RESTRICT
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index 2730776ca2..620fe1f104 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,285 +1,285 @@
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
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Instantiates and configures an appropriate {@link ConnectionProvider}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class ConnectionProviderInitiator implements BasicServiceInitiator<ConnectionProvider> {
 	public static final ConnectionProviderInitiator INSTANCE = new ConnectionProviderInitiator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        ConnectionProviderInitiator.class.getName());
 	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 	public static final String C3P0_PROVIDER_CLASS_NAME =
 			"org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";
 
 	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 	public static final String PROXOOL_PROVIDER_CLASS_NAME =
 			"org.hibernate.service.jdbc.connections.internal.ProxoolConnectionProvider";
 
 	public static final String INJECTION_DATA = "hibernate.connection_provider.injection_data";
 
 	// mapping from legacy connection provider name to actual
 	// connection provider that will be used
 	private static final Map<String,String> LEGACY_CONNECTION_PROVIDER_MAPPING;
 
 	static {
 		LEGACY_CONNECTION_PROVIDER_MAPPING = new HashMap<String,String>( 5 );
 
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.DatasourceConnectionProvider",
 				DatasourceConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.DriverManagerConnectionProvider",
 				DriverManagerConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.UserSuppliedConnectionProvider",
 				UserSuppliedConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.C3P0ConnectionProvider",
 				C3P0_PROVIDER_CLASS_NAME
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.ProxoolConnectionProvider",
 				PROXOOL_PROVIDER_CLASS_NAME
 		);
 	}
 
 	@Override
 	public Class<ConnectionProvider> getServiceInitiated() {
 		return ConnectionProvider.class;
 	}
 
 	@Override
 	public ConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		if ( MultiTenancyStrategy.determineMultiTenancyStrategy( configurationValues ) != MultiTenancyStrategy.NONE ) {
 			// nothing to do, but given the separate hierarchies have to handle this here.
 		}
 
 		final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 
 		ConnectionProvider connectionProvider = null;
 		String providerClassName = getConfiguredConnectionProviderName( configurationValues );
 		if ( providerClassName != null ) {
 			connectionProvider = instantiateExplicitConnectionProvider( providerClassName, classLoaderService );
 		}
 		else if ( configurationValues.get( Environment.DATASOURCE ) != null ) {
 			connectionProvider = new DatasourceConnectionProviderImpl();
 		}
 
 		if ( connectionProvider == null ) {
 			if ( c3p0ConfigDefined( configurationValues ) && c3p0ProviderPresent( classLoaderService ) ) {
 				connectionProvider = instantiateExplicitConnectionProvider( C3P0_PROVIDER_CLASS_NAME,
 						classLoaderService
 				);
 			}
 		}
 
 		if ( connectionProvider == null ) {
 			if ( proxoolConfigDefined( configurationValues ) && proxoolProviderPresent( classLoaderService ) ) {
 				connectionProvider = instantiateExplicitConnectionProvider( PROXOOL_PROVIDER_CLASS_NAME,
 						classLoaderService
 				);
 			}
 		}
 
 		if ( connectionProvider == null ) {
 			if ( configurationValues.get( Environment.URL ) != null ) {
 				connectionProvider = new DriverManagerConnectionProviderImpl();
 			}
 		}
 
 		if ( connectionProvider == null ) {
             LOG.noAppropriateConnectionProvider();
 			connectionProvider = new UserSuppliedConnectionProviderImpl();
 		}
 
 
 		final Map injectionData = (Map) configurationValues.get( INJECTION_DATA );
 		if ( injectionData != null && injectionData.size() > 0 ) {
 			final ConnectionProvider theConnectionProvider = connectionProvider;
 			new BeanInfoHelper( connectionProvider.getClass() ).applyToBeanInfo(
 					connectionProvider,
 					new BeanInfoHelper.BeanInfoDelegate() {
 						public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 							PropertyDescriptor[] descritors = beanInfo.getPropertyDescriptors();
 							for ( int i = 0, size = descritors.length; i < size; i++ ) {
 								String propertyName = descritors[i].getName();
 								if ( injectionData.containsKey( propertyName ) ) {
 									Method method = descritors[i].getWriteMethod();
 									method.invoke(
 											theConnectionProvider,
 											injectionData.get( propertyName )
 									);
 								}
 							}
 						}
 					}
 			);
 		}
 
 		return connectionProvider;
 	}
 
 	private String getConfiguredConnectionProviderName( Map configurationValues ) {
 		String providerClassName = ( String ) configurationValues.get( Environment.CONNECTION_PROVIDER );
 		if ( LEGACY_CONNECTION_PROVIDER_MAPPING.containsKey( providerClassName ) ) {
 			String actualProviderClassName = LEGACY_CONNECTION_PROVIDER_MAPPING.get( providerClassName );
             LOG.providerClassDeprecated(providerClassName, actualProviderClassName);
 			providerClassName = actualProviderClassName;
 		}
 		return providerClassName;
 	}
 
 	private ConnectionProvider instantiateExplicitConnectionProvider(
 			String providerClassName,
 			ClassLoaderService classLoaderService) {
 		try {
-            LOG.instantiatingExplicitConnectinProvider(providerClassName);
+            LOG.instantiatingExplicitConnectionProvider( providerClassName );
 			return (ConnectionProvider) classLoaderService.classForName( providerClassName ).newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate connection provider [" + providerClassName + "]", e );
 		}
 	}
 
 	private boolean c3p0ProviderPresent(ClassLoaderService classLoaderService) {
 		try {
 			classLoaderService.classForName( C3P0_PROVIDER_CLASS_NAME );
 		}
 		catch ( Exception e ) {
             LOG.c3p0ProviderClassNotFound(C3P0_PROVIDER_CLASS_NAME);
 			return false;
 		}
 		return true;
 	}
 
 	private static boolean c3p0ConfigDefined(Map configValues) {
 		for ( Object key : configValues.keySet() ) {
 			if ( String.class.isInstance( key )
 					&& ( (String) key ).startsWith( C3P0_CONFIG_PREFIX ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean proxoolProviderPresent(ClassLoaderService classLoaderService) {
 		try {
 			classLoaderService.classForName( PROXOOL_PROVIDER_CLASS_NAME );
 		}
 		catch ( Exception e ) {
             LOG.proxoolProviderClassNotFound(PROXOOL_PROVIDER_CLASS_NAME);
 			return false;
 		}
 		return true;
 	}
 
 	private static boolean proxoolConfigDefined(Map configValues) {
 		for ( Object key : configValues.keySet() ) {
 			if ( String.class.isInstance( key )
 					&& ( (String) key ).startsWith( PROXOOL_CONFIG_PREFIX ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Build the connection properties capable of being passed to the {@link java.sql.DriverManager#getConnection}
 	 * forms taking {@link Properties} argument.  We seek out all keys in the passed map which start with
 	 * {@code hibernate.connection.}, using them to create a new {@link Properties} instance.  The keys in this
 	 * new {@link Properties} have the {@code hibernate.connection.} prefix trimmed.
 	 *
 	 * @param properties The map from which to build the connection specific properties.
 	 *
 	 * @return The connection properties.
 	 */
 	public static Properties getConnectionProperties(Map<?,?> properties) {
 		Properties result = new Properties();
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( ! ( String.class.isInstance( entry.getKey() ) ) || ! String.class.isInstance( entry.getValue() ) ) {
 				continue;
 			}
 			final String key = (String) entry.getKey();
 			final String value = (String) entry.getValue();
 			if ( key.startsWith( Environment.CONNECTION_PREFIX ) ) {
 				if ( SPECIAL_PROPERTIES.contains( key ) ) {
 					if ( Environment.USER.equals( key ) ) {
 						result.setProperty( "user", value );
 					}
 				}
 				else {
 					result.setProperty(
 							key.substring( Environment.CONNECTION_PREFIX.length() + 1 ),
 							value
 					);
 				}
 			}
 		}
 		return result;
 	}
 
 	private static final Set<String> SPECIAL_PROPERTIES;
 
 	static {
 		SPECIAL_PROPERTIES = new HashSet<String>();
 		SPECIAL_PROPERTIES.add( Environment.DATASOURCE );
 		SPECIAL_PROPERTIES.add( Environment.URL );
 		SPECIAL_PROPERTIES.add( Environment.CONNECTION_PROVIDER );
 		SPECIAL_PROPERTIES.add( Environment.POOL_SIZE );
 		SPECIAL_PROPERTIES.add( Environment.ISOLATION );
 		SPECIAL_PROPERTIES.add( Environment.DRIVER );
 		SPECIAL_PROPERTIES.add( Environment.USER );
 
 	}
 }
