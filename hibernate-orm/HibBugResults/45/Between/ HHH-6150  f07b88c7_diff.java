diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 1b48056de4..5fb38d4a2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,2028 +1,2028 @@
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
-                if (seqGen.initialValue() != 1) LOG.unsupportedInitialValue(Configuration.USE_NEW_ID_GENERATOR_MAPPINGS);
+                if (seqGen.initialValue() != 1) LOG.unsupportedInitialValue( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS);
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
 
         LOG.debugf( "Binding entity from annotated class: %s", clazzToProcess.getName() );
 
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
         if (discAnn != null && inheritanceState.hasParents()) LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 
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
 
-		if ( !properties.containsKey( Configuration.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
+		if ( !properties.containsKey( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
             LOG.trace("Given properties did not contain any default cache concurrency strategy setting");
 			return;
 		}
 
-		final String strategyName = properties.getProperty( Configuration.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
+		final String strategyName = properties.getProperty( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
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
             LOG.debugf( typeBindMessageF, defAnn.name() );
 			mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
 		}
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
             LOG.debugf( typeBindMessageF, defAnn.defaultForType().getName() );
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
 					else if ( property.isAnnotationPresent( org.hibernate.annotations.MapKey.class ) ) {
 						if ( isJPA2 == null ) {
 							isJPA2 = Boolean.FALSE;
 						}
 						keyColumns = property.getAnnotation( org.hibernate.annotations.MapKey.class ).columns();
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
 					else if ( property.isAnnotationPresent( org.hibernate.annotations.MapKeyManyToMany.class ) ) {
 						if ( isJPA2 == null ) {
 							isJPA2 = Boolean.FALSE;
 						}
 						joinKeyColumns = property.getAnnotation( org.hibernate.annotations.MapKeyManyToMany.class )
 								.joinColumns();
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
 				else if ( elementCollectionAnn != null
 						|| collectionOfElementsAnn != null //Hibernate legacy
 						) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = "";
 					final Class<?> targetElement = elementCollectionAnn != null ?
 							elementCollectionAnn.targetClass() :
 							collectionOfElementsAnn.targetElement();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 78999cd7a2..65d1d787cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,517 +1,531 @@
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
 package org.hibernate.cfg;
 
 /**
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 	/**
 	 * Names a {@literal JNDI} namespace into which the {@link org.hibernate.SessionFactory} should be bound.
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Names the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} to use for obtaining
 	 * JDBC connections.  Can either reference an instance of
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
 	 * reference to the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} implementation
 	 * class.
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 
 	/**
 	 * Names the {@literal JDBC} driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 
 	/**
 	 * Names the {@literal JDBC} connection url.
 	 */
 	public static final String URL ="hibernate.connection.url";
 
 	/**
 	 * Names the connection user.  This might mean one of 2 things in out-of-the-box Hibernate
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}: <ul>
 	 *     <li>The username used to pass along to creating the JDBC connection</li>
 	 *     <li>The username used to obtain a JDBC connection from a data source</li>
 	 * </ul>
 	 */
 	public static final String USER ="hibernate.connection.username";
 
 	/**
 	 * Names the connection password.  See usage discussion on {@link #USER}
 	 */
 	public static final String PASS ="hibernate.connection.password";
 
 	/**
 	 * Names the {@literal JDBC} transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 
 	/**
 	 * Names the {@literal JDBC} autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 
 	/**
 	 * Maximum number of inactive connections for the built-in Hibernate connection pool.
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 
 	/**
 	 * Names a {@link javax.sql.DataSource}.  Can either reference a {@link javax.sql.DataSource} instance or
 	 * a {@literal JNDI} name under which to locate the {@link javax.sql.DataSource}.
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 
 	/**
 	 * Names a prefix used to define arbitrary JDBC connection properties.  These properties are passed along to
 	 * the {@literal JDBC} provider when creating a connection.
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * Names the {@literal JNDI} {@link javax.naming.InitialContext} class.
 	 *
 	 * @see javax.naming.Context#INITIAL_CONTEXT_FACTORY
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 
 	/**
 	 * Names the {@literal JNDI} provider/connection url
 	 *
 	 * @see javax.naming.Context#PROVIDER_URL
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 
 	/**
 	 * Names a prefix used to define arbitrary {@literal JNDI} {@link javax.naming.InitialContext} properties.  These
 	 * properties are passed along to {@link javax.naming.InitialContext#InitialContext(java.util.Hashtable)}
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 
 	/**
 	 * Names the Hibernate {@literal SQL} {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * Names any additional {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} implementations to
 	 * register with the standard {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}.
 	 */
 	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	public static final String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	public static final String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	public static final String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	public static final String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 
 	/**
 	 * Names the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use for integrating
 	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
 	 * instance or the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation class
 	 * @since 4.0
 	 */
 	public static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
 
 	/**
 	 * Names the {@link org.hibernate.transaction.TransactionManagerLookup} implementation to use for obtaining
 	 * reference to the {@literal JTA} {@link javax.transaction.TransactionManager}
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 *
 	 * @deprecated See {@link #CACHE_REGION_FACTORY}
 	 */
 	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
 
 	/**
 	 * The {@link org.hibernate.cache.RegionFactory} implementation class
 	 */
 	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enable statistics collection
 	 */
 	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	public static final String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	public static final String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
     /**
      * The jacc context id of the deployment
      */
     public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
 	 */
 	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
 	 */
 	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
 	 */
 	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
 	 */
 	public static final String ENVIRONMENT_CLASSLOADER = "hibernate.classLoader.environment";
 
 
 	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	public static final String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.TransactionManager} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_TM = "hibernate.jta.cacheTransactionManager";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.UserTransaction} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_UT = "hibernate.jta.cacheUserTransaction";
+
+	/**
+	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
+	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
+	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
+	 */
+	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
+
+	/**
+	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
+	 * for AUTO, TABLE and SEQUENCE.
+	 * Default to false to keep backward compatibility.
+	 */
+	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index e388e200a8..fdadb187a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1179 +1,1169 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
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
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.impl.SessionFactoryImpl;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
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
 import org.hibernate.secure.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
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
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
-	/**
-	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
-	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
-	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
-	 */
-	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
+	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
-	/**
-	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
-	 * for AUTO, TABLE and SEQUENCE.
-	 * Default to false to keep backward compatibility.
-	 */
-	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
+	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
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
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
 
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
 	private boolean specjProprietarySyntaxEnabled;
 
 
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
         LOG.readingMappingsFromFile(xmlFile.getPath());
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
             LOG.unableToDeserializeCache(cachedFile.getPath(), e);
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
 
         LOG.readingMappingsFromFile(xmlFile.getPath());
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
             LOG.debugf("Writing cache file for: %s to: %s", xmlFile, cachedFile);
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
         } catch (Exception e) {
             LOG.unableToWriteCachedFile(cachedFile.getPath(), e.getMessage());
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
 
         LOG.readingCachedMappings(cachedFile);
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
         LOG.debugf("Mapping XML:\n%s", xml);
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
 
         LOG.debugf("Reading mapping document from URL : %s", urlExternalForm);
 
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
                 LOG.trace("Was unable to close input stream");
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
         LOG.debugf("Mapping Document:\n%s", doc);
 
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
         LOG.readingMappingsFromResource(resourceName);
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
         LOG.readingMappingsFromResource(resourceName);
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
         LOG.readingMappingsFromResource(mappingResourceName);
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
             LOG.unableToParseMetadata(packageName);
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
         LOG.searchingForMappingDocuments(jar.getName());
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
                     LOG.foundMappingDocument(ze.getName());
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
                 LOG.unableToCloseJar(ioe.getMessage());
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
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
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
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
@@ -2045,1455 +2035,1455 @@ public class Configuration implements Serializable {
 		}
 
         LOG.configuredSessionFactory(name);
         LOG.debugf("Properties: %s", properties);
 
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
             LOG.debugf("Session-factory config [%s] named resource [%s] for mapping", name, resourceName);
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named file [%s] for mapping", name, fileName);
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName);
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named package [%s] for mapping", name, packageName);
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named class [%s] for mapping", name, className);
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
         setProperty(Environment.JACC_CONTEXTID, contextId);
         LOG.jaccContextId(contextId);
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
 	 *
 	 * @return this for method chaining
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
 	public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
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
             LOG.debugf("Added %s with class %s", typeName, typeClass);
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
 			return getLogicalTableName( table.getQuotedSchema(), table.getCatalog(), table.getQuotedName() );
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
 			StringBuffer keyBuilder = new StringBuffer();
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
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
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
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
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
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 
 
 		private Boolean useNewGeneratorMappings;
 
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
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
-				final String booleanName = getConfigurationProperties().getProperty( USE_NEW_ID_GENERATOR_MAPPINGS );
+				final String booleanName = getConfigurationProperties().getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
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
                 if (old != null) LOG.duplicateGeneratorName(old.getName());
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
             if (old != null) LOG.duplicateGeneratorTable(name);
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
             if (old != null) LOG.duplicateJoins(persistentClass.getEntityName());
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
             LOG.debugf("Processing hbm.xml files");
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), CollectionHelper.EMPTY_MAP, entityNames );
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
             LOG.debugf("Process annotated classes");
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
index 6af822cafa..54b0593fb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,756 +1,756 @@
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
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
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
 	public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory();
 
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
-	 * {@link Configuration#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
+	 * {@link AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
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
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
index b29b63a850..632d22087f 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
@@ -1,287 +1,287 @@
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
 package org.hibernate.ejb.test;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Persistence;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * A base class for all ejb tests.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public abstract class BaseEntityManagerFunctionalTestCase extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( BaseEntityManagerFunctionalTestCase.class );
 
 	// IMPL NOTE : Here we use @Before and @After (instead of @BeforeClassOnce and @AfterClassOnce like we do in
 	// BaseCoreFunctionalTestCase) because the old HEM test methodology was to create an EMF for each test method.
 
 	private static final Dialect dialect = Dialect.getDialect();
 
 	private Ejb3Configuration ejb3Configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private EntityManagerFactory entityManagerFactory;
 
 	private EntityManager em;
 	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	protected EntityManagerFactory entityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Before
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void buildEntityManagerFactory() throws Exception {
 		log.trace( "Building session factory" );
 		ejb3Configuration = buildConfiguration();
 		ejb3Configuration.configure( getConfig() );
 		afterConfigurationBuilt( ejb3Configuration );
 		serviceRegistry = buildServiceRegistry( ejb3Configuration.getHibernateConfiguration() );
 		applyServices( serviceRegistry );
 		entityManagerFactory = ejb3Configuration.buildEntityManagerFactory( serviceRegistry );
 		afterEntityManagerFactoryBuilt();
 	}
 
 	protected Ejb3Configuration buildConfiguration() {
 		Ejb3Configuration ejb3Cfg = constructConfiguration();
 		addMappings( ejb3Cfg.getHibernateConfiguration() );
 		return ejb3Cfg;
 	}
 
 	protected Ejb3Configuration constructConfiguration() {
 		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
 		if ( createSchema() ) {
 			ejb3Configuration.getHibernateConfiguration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		ejb3Configuration
 				.getHibernateConfiguration()
-				.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
+				.setProperty( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return ejb3Configuration;
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource( mapping, getClass().getClassLoader() );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected Map getConfig() {
 		Map<Object, Object> config = loadProperties();
 		ArrayList<Class> classes = new ArrayList<Class>();
 
 		classes.addAll( Arrays.asList( getAnnotatedClasses() ) );
 		config.put( AvailableSettings.LOADED_CLASSES, classes );
 		for ( Map.Entry<Class, String> entry : getCachedClasses().entrySet() ) {
 			config.put( AvailableSettings.CLASS_CACHE_PREFIX + "." + entry.getKey().getName(), entry.getValue() );
 		}
 		for ( Map.Entry<String, String> entry : getCachedCollections().entrySet() ) {
 			config.put( AvailableSettings.COLLECTION_CACHE_PREFIX + "." + entry.getKey(), entry.getValue() );
 		}
 		if ( getEjb3DD().length > 0 ) {
 			ArrayList<String> dds = new ArrayList<String>();
 			dds.addAll( Arrays.asList( getEjb3DD() ) );
 			config.put( AvailableSettings.XML_FILE_NAMES, dds );
 		}
 
 		addConfigOptions( config );
 		return config;
 	}
 
 	protected void addConfigOptions(Map options) {
 	}
 
 	private Properties loadProperties() {
 		Properties props = new Properties();
 		InputStream stream = Persistence.class.getResourceAsStream( "/hibernate.properties" );
 		if ( stream != null ) {
 			try {
 				props.load( stream );
 			}
 			catch ( Exception e ) {
 				throw new RuntimeException( "could not load hibernate.properties" );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch ( IOException ignored ) {
 				}
 			}
 		}
 		return props;
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	public Map<Class, String> getCachedClasses() {
 		return new HashMap<Class, String>();
 	}
 
 	public Map<String, String> getCachedCollections() {
 		return new HashMap<String, String>();
 	}
 
 	public String[] getEjb3DD() {
 		return new String[] { };
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void afterConfigurationBuilt(Ejb3Configuration ejb3Configuration) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterEntityManagerFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 
 	@After
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void releaseResources() {
 		releaseUnclosedEntityManagers();
 
 		if ( entityManagerFactory != null ) {
 			entityManagerFactory.close();
 		}
 
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 
 	private void releaseUnclosedEntityManagers() {
 		releaseUnclosedEntityManager( this.em );
 
 		for ( EntityManager isolatedEm : isolatedEms ) {
 			releaseUnclosedEntityManager( isolatedEm );
 		}
 	}
 
 	private void releaseUnclosedEntityManager(EntityManager em) {
 		if ( em == null ) {
 			return;
 		}
 		if ( em.getTransaction().isActive() ) {
 			em.getTransaction().rollback();
             log.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
 		}
 		if ( em.isOpen() ) {
 			// as we open an EM before the test runs, it will still be open if the test uses a custom EM.
 			// or, the person may have forgotten to close. So, do not raise a "fail", but log the fact.
 			em.close();
             log.warn("The EntityManager is not closed. Closing it.");
 		}
 	}
 
 	protected EntityManager getOrCreateEntityManager() {
 		if ( em == null || !em.isOpen() ) {
 			em = entityManagerFactory.createEntityManager();
 		}
 		return em;
 	}
 
 	protected EntityManager createIsolatedEntityManager() {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager();
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createIsolatedEntityManager(Map props) {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager(props);
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createEntityManager(Map properties) {
 		// always reopen a new EM and close the existing one
 		if ( em != null && em.isOpen() ) {
 			em.close();
 		}
 		em = entityManagerFactory.createEntityManager( properties );
 		return em;
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
index 0017ec16aa..31ee8063e7 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
@@ -1,249 +1,248 @@
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
 package org.hibernate.ejb.test.lock;
 
 import javax.persistence.EntityManager;
 import javax.persistence.LockModeType;
 import java.util.Map;
 
 import org.hibernate.LockMode;
-import org.hibernate.cfg.Configuration;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.QueryImpl;
 import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;
 import org.hibernate.impl.SessionImpl;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class QueryLockingTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Lockable.class };
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	protected void addConfigOptions(Map options) {
-		options.put( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
+		options.put( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 	}
 
 	@Test
 	public void testOverallLockMode() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		QueryImpl jpaQuery = em.createQuery( "from Lockable l" ).unwrap( QueryImpl.class );
 
 		org.hibernate.impl.QueryImpl hqlQuery = (org.hibernate.impl.QueryImpl) jpaQuery.getHibernateQuery();
 		assertEquals( LockMode.NONE, hqlQuery.getLockOptions().getLockMode() );
 		assertNull( hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.NONE, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		// NOTE : LockModeType.READ should map to LockMode.OPTIMISTIC
 		jpaQuery.setLockMode( LockModeType.READ );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getLockMode() );
 		assertNull( hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		jpaQuery.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.PESSIMISTIC_WRITE );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getLockMode() );
 		assertEquals( LockMode.PESSIMISTIC_WRITE, hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.PESSIMISTIC_WRITE, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testPessimisticForcedIncrementOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class ).setLockMode( LockModeType.PESSIMISTIC_FORCE_INCREMENT ).getSingleResult();
 		assertFalse( reread.getVersion().equals( initial ) );
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testPessimisticForcedIncrementSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.PESSIMISTIC_FORCE_INCREMENT )
 				.getSingleResult();
 		assertFalse( reread.getVersion().equals( initial ) );
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticForcedIncrementOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class ).setLockMode( LockModeType.OPTIMISTIC_FORCE_INCREMENT ).getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		em.getTransaction().commit();
 		em.close();
 		assertFalse( reread.getVersion().equals( initial ) );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticForcedIncrementSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.OPTIMISTIC_FORCE_INCREMENT )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		em.getTransaction().commit();
 		em.close();
 		assertFalse( reread.getVersion().equals( initial ) );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class )
 				.setLockMode( LockModeType.OPTIMISTIC )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		assertTrue( em.unwrap( SessionImpl.class ).getActionQueue().hasBeforeTransactionActions() );
 		em.getTransaction().commit();
 		em.close();
 		assertEquals( initial, reread.getVersion() );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.OPTIMISTIC )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		assertTrue( em.unwrap( SessionImpl.class ).getActionQueue().hasBeforeTransactionActions() );
 		em.getTransaction().commit();
 		em.close();
 		assertEquals( initial, reread.getVersion() );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index 4730870b1a..03f16bb5df 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,412 +1,413 @@
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
 
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.cache.HashtableCacheProvider;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private Configuration configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
 		return session;
 	}
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void buildSessionFactory() {
 		configuration = buildConfiguration();
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 		return cfg;
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
 				.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
-		configuration.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
+		configuration.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( properties ).buildServiceRegistry();
 		applyServices( serviceRegistry );
 		return serviceRegistry;
 	}
 
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		serviceRegistry.destroy();
 
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 	}
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = Integer.valueOf( 0 );
 					}
 					l = Integer.valueOf( l.intValue() + 1 ) ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.doReturningWork(
 					new AbstractReturningWork<Integer>() {
 						@Override
 						public Integer execute(Connection connection) throws SQLException {
 							return connection.getTransactionIsolation();
 						}
 					}
 			);
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 }
