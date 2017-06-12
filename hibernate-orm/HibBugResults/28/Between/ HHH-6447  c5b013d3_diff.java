diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index 22bdb43b35..4f217d01eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,997 +1,1019 @@
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
 
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.UnknownInheritanceTypeException;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
 import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.AttributeBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.TupleRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final EntityClass entityClass;
 	private final Hierarchical superType;
 	private final AnnotationBindingContext bindingContext;
 
 	private final Schema.Name schemaName;
 
 	public EntityBinder(EntityClass entityClass, Hierarchical superType, AnnotationBindingContext bindingContext) {
 		this.entityClass = entityClass;
 		this.superType = superType;
 		this.bindingContext = bindingContext;
 		this.schemaName = determineSchemaName();
 	}
 
 	public EntityBinding bind(List<String> processedEntityNames) {
 		if ( processedEntityNames.contains( entityClass.getName() ) ) {
 			return bindingContext.getMetadataImplementor().getEntityBinding( entityClass.getName() );
 		}
 
 		final EntityBinding entityBinding = createEntityBinding();
 
 		bindingContext.getMetadataImplementor().addEntity( entityBinding );
 		processedEntityNames.add( entityBinding.getEntity().getName() );
 
 		return entityBinding;
 	}
 
 	private Schema.Name determineSchemaName() {
 		String schema = bindingContext.getMappingDefaults().getSchemaName();
 		String catalog = bindingContext.getMappingDefaults().getCatalogName();
 
 		final AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			final AnnotationValue schemaValue = tableAnnotation.value( "schema" );
 			if ( schemaValue != null ) {
 				schema = schemaValue.asString();
 			}
 
 			final AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
 			if ( catalogValue != null ) {
 				catalog = catalogValue.asString();
 			}
 		}
 
 		if ( bindingContext.isGloballyQuotedIdentifiers() ) {
 			schema = StringHelper.quote( schema );
 			catalog = StringHelper.quote( catalog );
 		}
 		return new Schema.Name( schema, catalog );
 	}
 
 	private EntityBinding createEntityBinding() {
 		final EntityBinding entityBinding = buildBasicEntityBinding();
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindEmbeddedAttributes( entityBinding );
 
 		bindTableUniqueConstraints( entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding buildBasicEntityBinding() {
 		switch ( entityClass.getInheritanceType() ) {
 			case NO_INHERITANCE: {
 				return doRootEntityBindingCreation();
 			}
 			case SINGLE_TABLE: {
 				return doRootEntityBindingCreation();
 				//return doDiscriminatedSubclassBindingCreation();
 			}
 			case JOINED: {
 				return doJoinedSubclassBindingCreation();
 			}
 			case TABLE_PER_CLASS: {
 				return doUnionSubclassBindingCreation();
 			}
 			default: {
 				throw new UnknownInheritanceTypeException( "Unknown InheritanceType : " + entityClass.getInheritanceType() );
 			}
 		}
 	}
 
 	private EntityBinding doRootEntityBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
-		entityBinding.setInheritanceType( InheritanceType.NO_INHERITANCE );
+
+//temporary (for discrim subclass)
+//		entityBinding.setInheritanceType( InheritanceType.NO_INHERITANCE );
+		entityBinding.setInheritanceType( superType == null ? InheritanceType.NO_INHERITANCE : InheritanceType.SINGLE_TABLE );
+		if ( superType != null ) {
+			EntityBinding superBinding = bindingContext.getMetadataImplementor().getEntityBinding( superType.getName() );
+			if ( superBinding == null ) {
+				throw new AssertionFailure( "EntityBinding for super-type [" + superType.getName() + "] not yet bound" );
+			}
+			entityBinding.setSuperEntityBinding( superBinding );
+		}
 
 		doBasicEntityBinding( entityBinding );
 
 		// technically the rest of these binds should only apply to root entities, but they are really available on all
 		// because we do not currently subtype EntityBinding
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6400
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 			polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 		}
 		entityBinding.setExplicitPolymorphism( polymorphism == PolymorphismType.EXPLICIT );
 
 		// see HHH-6401
 		OptimisticLockType optimisticLockType = OptimisticLockType.VERSION;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 			optimisticLockType = OptimisticLockType.valueOf(
 					hibernateEntityAnnotation.value( "optimisticLock" )
 							.asEnum()
 			);
 		}
 		entityBinding.setOptimisticLockStyle( OptimisticLockStyle.valueOf( optimisticLockType.name() ) );
 
 		final AnnotationInstance hibernateImmutableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		final boolean mutable = hibernateImmutableAnnotation == null
 				&& hibernateEntityAnnotation != null
 				&& hibernateEntityAnnotation.value( "mutable" ) != null
 				&& hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 		entityBinding.setMutable( mutable );
 
 		final AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		entityBinding.setWhereFilter(
 				whereAnnotation != null && whereAnnotation.value( "clause" ) != null
 						? whereAnnotation.value( "clause" ).asString()
 						: null
 		);
 
 		final AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 		entityBinding.setRowId(
 				rowIdAnnotation != null && rowIdAnnotation.value() != null
 						? rowIdAnnotation.value().asString()
 						: null
 		);
 
 		entityBinding.setCaching( interpretCaching( entityClass, bindingContext ) );
 
 		bindPrimaryTable( entityBinding );
 		bindId( entityBinding );
 
 		if ( entityClass.getInheritanceType() == InheritanceType.SINGLE_TABLE ) {
 			bindDiscriminatorColumn( entityBinding );
 		}
 
 		// todo : version
 
 		return entityBinding;
 	}
 
 	private Caching interpretCaching(ConfiguredClass configuredClass, AnnotationBindingContext bindingContext) {
 		final AnnotationInstance hibernateCacheAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( hibernateCacheAnnotation != null ) {
 			final AccessType accessType = hibernateCacheAnnotation.value( "usage" ) == null
 					? bindingContext.getMappingDefaults().getCacheAccessType()
 					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() )
 					.toAccessType();
 			return new Caching(
 					hibernateCacheAnnotation.value( "region" ) == null
 							? configuredClass.getName()
 							: hibernateCacheAnnotation.value( "region" ).asString(),
 					accessType,
 					hibernateCacheAnnotation.value( "include" ) != null
 							&& "all".equals( hibernateCacheAnnotation.value( "include" ).asString() )
 			);
 		}
 
 		final AnnotationInstance jpaCacheableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( jpaCacheableAnnotation != null && jpaCacheableAnnotation.value() != null ) {
 			cacheable = jpaCacheableAnnotation.value().asBoolean();
 		}
 
 		final boolean doCaching;
 		switch ( bindingContext.getMetadataImplementor().getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				doCaching = true;
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				doCaching = cacheable;
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				doCaching = jpaCacheableAnnotation == null || cacheable;
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				doCaching = false;
 				break;
 			}
 		}
 
 		if ( !doCaching ) {
 			return null;
 		}
 
 		return new Caching(
 				configuredClass.getName(),
 				bindingContext.getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	private EntityBinding doDiscriminatedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.SINGLE_TABLE );
 
+		entityBinding.setSuperEntityBinding(
+				bindingContext.getMetadataImplementor().getEntityBinding( superType.getName() )
+		);
+
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind discriminator-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doJoinedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.JOINED );
 
+		entityBinding.setSuperEntityBinding(
+				bindingContext.getMetadataImplementor().getEntityBinding( superType.getName() )
+		);
+
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind join-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doUnionSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.TABLE_PER_CLASS );
 
+		entityBinding.setSuperEntityBinding(
+				bindingContext.getMetadataImplementor().getEntityBinding( superType.getName() )
+		);
+
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind union-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private void doBasicEntityBinding(EntityBinding entityBinding) {
 		entityBinding.setEntityMode( EntityMode.POJO );
 
 		final Entity entity = new Entity(
 				entityClass.getName(),
 				entityClass.getName(),
 				bindingContext.makeClassReference( entityClass.getName() ),
 				superType
 		);
 		entityBinding.setEntity( entity );
 
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.ENTITY
 		);
 
 		final AnnotationValue explicitJpaEntityName = jpaEntityAnnotation.value( "name" );
 		if ( explicitJpaEntityName == null ) {
 			entityBinding.setJpaEntityName( entityClass.getName() );
 		}
 		else {
 			entityBinding.setJpaEntityName( explicitJpaEntityName.asString() );
 		}
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6397
 		entityBinding.setDynamicInsert(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean()
 		);
 
 		// see HHH-6398
 		entityBinding.setDynamicUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean()
 		);
 
 		// see HHH-6399
 		entityBinding.setSelectBeforeUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean()
 		);
 
 		// Custom sql loader
 		final AnnotationInstance sqlLoaderAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.LOADER
 		);
 		if ( sqlLoaderAnnotation != null ) {
 			entityBinding.setCustomLoaderName( sqlLoaderAnnotation.value( "namedQuery" ).asString() );
 		}
 
 		// Custom sql insert
 		final AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBinding.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		// Custom sql update
 		final AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBinding.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		// Custom sql delete
 		final AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBinding.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		// Batch size
 		final AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 		entityBinding.setBatchSize( batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt() );
 
 		// Proxy generation
 		final boolean lazy;
 		final Value<Class<?>> proxyInterfaceType;
 		final AnnotationInstance hibernateProxyAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		if ( hibernateProxyAnnotation != null ) {
 			lazy = hibernateProxyAnnotation.value( "lazy" ) == null
 					|| hibernateProxyAnnotation.value( "lazy" ).asBoolean();
 			if ( lazy ) {
 				final AnnotationValue proxyClassValue = hibernateProxyAnnotation.value( "proxyClass" );
 				if ( proxyClassValue == null ) {
 					proxyInterfaceType = entity.getClassReferenceUnresolved();
 				}
 				else {
 					proxyInterfaceType = bindingContext.makeClassReference( proxyClassValue.asString() );
 				}
 			}
 			else {
 				proxyInterfaceType = null;
 			}
 		}
 		else {
 			lazy = true;
 			proxyInterfaceType = entity.getClassReferenceUnresolved();
 		}
 		entityBinding.setLazy( lazy );
 		entityBinding.setProxyInterfaceType( proxyInterfaceType );
 
 		// Custom persister
 		final Class<? extends EntityPersister> entityPersisterClass;
 		final AnnotationInstance persisterAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PERSISTER
 		);
 		if ( persisterAnnotation == null || persisterAnnotation.value( "impl" ) == null ) {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				entityPersisterClass = bindingContext.locateClassByName(
 						hibernateEntityAnnotation.value( "persister" )
 								.asString()
 				);
 			}
 			else {
 				entityPersisterClass = null;
 			}
 		}
 		else {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				// todo : error?
 			}
 			entityPersisterClass = bindingContext.locateClassByName( persisterAnnotation.value( "impl" ).asString() );
 		}
 		entityBinding.setCustomEntityPersisterClass( entityPersisterClass );
 
 		// Custom tuplizer
 		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
 		if ( pojoTuplizerAnnotation != null ) {
 			final Class<? extends EntityTuplizer> tuplizerClass =
 					bindingContext.locateClassByName( pojoTuplizerAnnotation.value( "impl" ).asString() );
 			entityBinding.setCustomEntityTuplizerClass( tuplizerClass );
 		}
 
 		// table synchronizations
 		final AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 		if ( synchronizeAnnotation != null ) {
 			final String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			entityBinding.addSynchronizedTableNames( Arrays.asList( tableNames ) );
 		}
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSqlAnnotation) {
 		if ( customSqlAnnotation == null ) {
 			return null;
 		}
 
 		final String sql = customSqlAnnotation.value( "sql" ).asString();
 		final boolean isCallable = customSqlAnnotation.value( "callable" ) != null
 				&& customSqlAnnotation.value( "callable" ).asBoolean();
 
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlAnnotation.value( "check" ) == null
 				? isCallable
 				? ExecuteUpdateResultCheckStyle.NONE
 				: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.valueOf( customSqlAnnotation.value( "check" ).asEnum() );
 
 		return new CustomSQL( sql, isCallable, checkStyle );
 	}
 
 	private void bindPrimaryTable(EntityBinding entityBinding) {
 		final Schema schema = bindingContext.getMetadataImplementor().getDatabase().getSchema( schemaName );
 
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 
 		String tableName = null;
 		if ( tableAnnotation != null ) {
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = bindingContext.getNamingStrategy().tableName( explicitTableName );
 			}
 		}
 
 		// no explicit table name given, let's use the entity name as table name (taking inheritance into consideration
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = bindingContext.getNamingStrategy().classToTableName( entityClass.getClassNameForTable() );
 		}
 
 		if ( bindingContext.isGloballyQuotedIdentifiers() && !Identifier.isQuoted( tableName ) ) {
 			tableName = StringHelper.quote( tableName );
 		}
 		org.hibernate.metamodel.relational.Table table = schema.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private AnnotationInstance locatePojoTuplizerAnnotation() {
 		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.TUPLIZERS
 		);
 		if ( tuplizersAnnotation == null ) {
 			return null;
 		}
 
 		AnnotationInstance[] annotations = JandexHelper.getValue(
 				tuplizersAnnotation,
 				"value",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
 			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
 				return tuplizerAnnotation;
 			}
 		}
 
 		return null;
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				entityClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 		bindSingleMappedAttribute( entityBinding, entityBinding.getEntity(), discriminatorAttribute );
 
 		if ( !( discriminatorAttribute.getColumnValues() instanceof DiscriminatorColumnValues ) ) {
 			throw new AssertionFailure( "Expected discriminator column values" );
 		}
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
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
 				uniqueKey.addColumn( table.locateOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( entityClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				bindEmbeddedIdAnnotation( entityBinding );
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
 	private void bindEmbeddedIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.EMBEDDED_ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = entityClass.getMappedAttribute( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		SingularAttribute attribute = entityBinding.getEntity().locateOrCreateComponentAttribute( idName );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize(
 				new ColumnRelationalStateImpl(
 						(SimpleAttribute) idAttribute,
 						bindingContext.getMetadataImplementor()
 				)
 		);
 		bindSingleIdGeneratedValue( entityBinding, idName );
 
 		TupleRelationalStateImpl state = new TupleRelationalStateImpl();
 		EmbeddableClass embeddableClass = entityClass.getEmbeddedClasses().get( idName );
 		for ( SimpleAttribute attr : embeddableClass.getSimpleAttributes() ) {
 			state.addValueState( new ColumnRelationalStateImpl( attr, bindingContext.getMetadataImplementor() ) );
 		}
 		attributeBinding.initialize( state );
 		Map<String, String> parameterMap = new HashMap<String, String>( 1 );
 		parameterMap.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
 		IdGenerator generator = new IdGenerator( "NAME", "assigned", parameterMap );
 		entityBinding.getEntityIdentifier().setIdGenerator( generator );
 		// entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		// we know we are dealing w/ a single @Id, but potentially it is defined in a mapped super class
 		ConfiguredClass configuredClass = entityClass;
 		EntityClass superEntity = entityClass.getEntityParent();
 		Hierarchical container = entityBinding.getEntity();
 		Iterator<SimpleAttribute> iter = null;
 		while ( configuredClass != null && configuredClass != superEntity ) {
 			iter = configuredClass.getIdAttributes().iterator();
 			if ( iter.hasNext() ) {
 				break;
 			}
 			configuredClass = configuredClass.getParent();
 			container = container.getSuperType();
 		}
 
 		// if we could not find the attribute our assumptions were wrong
 		if ( iter == null || !iter.hasNext() ) {
 			throw new AnnotationException(
 					String.format(
 							"Unable to find id attribute for class %s",
 							entityClass.getName()
 					)
 			);
 		}
 
 		// now that we have the id attribute we can create the attribute and binding
 		MappedAttribute idAttribute = iter.next();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( idAttribute.getName() );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize(
 				new ColumnRelationalStateImpl(
 						(SimpleAttribute) idAttribute,
 						bindingContext.getMetadataImplementor()
 				)
 		);
 		bindSingleIdGeneratedValue( entityBinding, idAttribute.getName() );
 
 		if ( !attribute.isTypeResolved() ) {
 			attribute.resolveType(
 					bindingContext.makeJavaType(
 							attributeBinding.getHibernateTypeDescriptor()
 									.getJavaTypeName()
 					)
 			);
 		}
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							entityClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValue( generatedValueAnn, "generator", String.class );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = bindingContext.getMetadataImplementor().getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s referring an undefined generator [%s]",
 								entityClass.getName(),
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
 				bindingContext.getMetadataImplementor().getOptions().useNewIdentifierGenerators()
 		);
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							entityClass.getName(),
 							idName
 					)
 			);
 		}
 		if ( idGenerator == null ) {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 //        entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		// collect attribute overrides as we map the attributes
 		Map<String, AttributeOverride> attributeOverrideMap = new HashMap<String, AttributeOverride>();
 
 		// bind the attributes of this entity
 		AttributeContainer entity = entityBinding.getEntity();
 		bindAttributes( entityBinding, entity, entityClass, attributeOverrideMap );
 
 		// bind potential mapped super class attributes
 		attributeOverrideMap.putAll( entityClass.getAttributeOverrideMap() );
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindAttributes( entityBinding, superTypeContainer, parent, attributeOverrideMap );
 			addNewOverridesToMap( parent, attributeOverrideMap );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void addNewOverridesToMap(ConfiguredClass parent, Map<String, AttributeOverride> attributeOverrideMap) {
 		Map<String, AttributeOverride> overrides = parent.getAttributeOverrideMap();
 		for ( Map.Entry<String, AttributeOverride> entry : overrides.entrySet() ) {
 			if ( !attributeOverrideMap.containsKey( entry.getKey() ) ) {
 				attributeOverrideMap.put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private boolean containsMappedSuperclassAttributes(ConfiguredClass parent) {
 		return parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) );
 	}
 
 	private void bindAttributes(
 			EntityBinding entityBinding,
 			AttributeContainer attributeContainer,
 			ConfiguredClass configuredClass,
 			Map<String, AttributeOverride> attributeOverrideMap) {
 		for ( SimpleAttribute simpleAttribute : configuredClass.getSimpleAttributes() ) {
 			String attributeName = simpleAttribute.getName();
 
 			// if there is a override apply it
 			AttributeOverride override = attributeOverrideMap.get( attributeName );
 			if ( override != null ) {
 				simpleAttribute = SimpleAttribute.createSimpleAttribute( simpleAttribute, override.getColumnValues() );
 			}
 
 			bindSingleMappedAttribute(
 					entityBinding,
 					attributeContainer,
 					simpleAttribute
 			);
 		}
 		for ( AssociationAttribute associationAttribute : configuredClass.getAssociationAttributes() ) {
 			bindAssociationAttribute(
 					entityBinding,
 					attributeContainer,
 					associationAttribute
 			);
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding) {
 		AttributeContainer entity = entityBinding.getEntity();
 		bindEmbeddedAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class embeddables
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindEmbeddedAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void bindEmbeddedAttributes(
 			EntityBinding entityBinding,
 			AttributeContainer attributeContainer,
 			ConfiguredClass configuredClass) {
 		for ( Map.Entry<String, EmbeddableClass> entry : configuredClass.getEmbeddedClasses().entrySet() ) {
 			String attributeName = entry.getKey();
 			EmbeddableClass embeddedClass = entry.getValue();
 			SingularAttribute componentAttribute = attributeContainer.locateOrCreateComponentAttribute( attributeName );
 			// we have to resolve the type, if the attribute was just created
 			if ( !componentAttribute.isTypeResolved() ) {
 				Component c = new Component(
 						attributeName,
 						embeddedClass.getName(),
 						new Value<Class<?>>( embeddedClass.getConfiguredClass() ),
 						null
 				);
 				componentAttribute.resolveType( c );
 			}
 			for ( SimpleAttribute simpleAttribute : embeddedClass.getSimpleAttributes() ) {
 				bindSingleMappedAttribute(
 						entityBinding,
 						componentAttribute.getAttributeContainer(),
 						simpleAttribute
 				);
 			}
 			for ( AssociationAttribute associationAttribute : embeddedClass.getAssociationAttributes() ) {
 				bindAssociationAttribute(
 						entityBinding,
 						componentAttribute.getAttributeContainer(),
 						associationAttribute
 				);
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(
 			EntityBinding entityBinding,
 			AttributeContainer container,
 			AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
 				Attribute attribute = entityBinding.getEntity()
 						.locateOrCreateSingularAttribute( associationAttribute.getName() );
 				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
 						attribute
 				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( entityClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, bindingContext.getMetadataImplementor()
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
 
 	private void bindSingleMappedAttribute(
 			EntityBinding entityBinding,
 			AttributeContainer container,
 			SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
 		ColumnValues columnValues = simpleAttribute.getColumnValues();
 
 		String attributeName = simpleAttribute.getName();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( attributeName );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attribute );
 			entityDiscriminator.setDiscriminatorValue( ( (DiscriminatorColumnValues) columnValues ).getDiscriminatorValue() );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attribute );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 		}
 
 		attributeBinding.setInsertable( simpleAttribute.isInsertable() );
 		attributeBinding.setUpdatable( simpleAttribute.isUpdatable() );
 		attributeBinding.setGeneration( simpleAttribute.getPropertyGeneration() );
 		attributeBinding.setLazy( simpleAttribute.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( simpleAttribute.isOptimisticLockable() );
 		HibernateTypeDescriptor hibernateTypeDescriptor= attributeBinding.getHibernateTypeDescriptor();
 		hibernateTypeDescriptor.setExplicitTypeName( simpleAttribute.getExplicitHibernateTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( simpleAttribute.getExplicitHibernateTypeParameters() );
 		hibernateTypeDescriptor.setJavaTypeName( simpleAttribute.getJavaType().getName() );
 
 //		attributeBinding.setPropertyAccessorName(
 //				Helper.getPropertyAccessorName(
 //						simpleAttribute.getPropertyAccessorName(),
 //						false,
 //						bindingContext.getMappingDefaults().getPropertyAccessorName()
 //				)
 //		);
 
 		final TableSpecification valueSource = attributeBinding.getEntityBinding().getBaseTable();
 		String columnName = simpleAttribute.getColumnValues()
 				.getName()
 				.isEmpty() ? attribute.getName() : simpleAttribute.getColumnValues().getName();
 		Column column = valueSource.locateOrCreateColumn( columnName );
 		column.setNullable( columnValues.isNullable() );
 		column.setDefaultValue( null ); // todo
 		column.setSqlType( null ); // todo
 		Size size = new Size(
 				columnValues.getPrecision(),
 				columnValues.getScale(),
 				columnValues.getLength(),
 				Size.LobMultiplier.NONE
 		);
 		column.setSize( size );
 		column.setDatatype( null ); // todo : ???
 		column.setReadFragment( simpleAttribute.getCustomReadFragment() );
 		column.setWriteFragment( simpleAttribute.getCustomWriteFragment() );
 		column.setUnique( columnValues.isUnique() );
 		column.setCheckCondition( simpleAttribute.getCheckCondition() );
 		column.setComment( null ); // todo
 
 		attributeBinding.setValue( column );
 
 		if ( ! attribute.isTypeResolved() ) {
 			attribute.resolveType( bindingContext.makeJavaType( attributeBinding.getHibernateTypeDescriptor().getJavaTypeName() ) );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index f05293ab26..c7df43caf6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,599 +1,595 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
 import org.hibernate.metamodel.source.annotations.AnnotationProcessor;
 import org.hibernate.metamodel.source.hbm.HbmSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
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
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
-	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
+
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			sourceProcessors = new SourceProcessor[] {
 					new HbmSourceProcessorImpl( this ),
 					new AnnotationProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
 					new AnnotationProcessor( this ),
 					new HbmSourceProcessorImpl( this )
 			};
 		}
 
 		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
 					@Override
 					public PersisterClassResolver initialize() {
 						return serviceRegistry.getService( PersisterClassResolver.class );
 					}
 				}
 		);
 
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( sourceProcessors, metadataSources );
 		bindIndependentMetadata( sourceProcessors, metadataSources );
 		bindTypeDependentMetadata( sourceProcessors, metadataSources );
 		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( sourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
 	private void prepare(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingDependentMetadata( metadataSources );
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
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
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
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
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
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return classLoaderService.getValue();
 	}
 
 	private PersisterClassResolver persisterClassResolverService() {
 		return persisterClassResolverService.getValue();
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
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return name;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
-		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
-		if ( rootEntityBinding == null ) {
-			EntityBinding entityBinding = entityBindingMap.get( entityName );
-			if ( entityBinding == null ) {
-				throw new IllegalStateException( "Unknown entity binding: " + entityName );
-			}
-			if ( entityBinding.isRoot() ) {
-				rootEntityBinding = entityBinding;
-			}
-			else {
-				if ( entityBinding.getEntity().getSuperType() == null ) {
-					throw new IllegalStateException( "Entity binding has no root: " + entityName );
-				}
-				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
-			}
-			rootEntityBindingMap.put( entityName, rootEntityBinding );
+		EntityBinding binding = entityBindingMap.get( entityName );
+		if ( binding == null ) {
+			throw new IllegalStateException( "Unknown entity binding: " + entityName );
 		}
-		return rootEntityBinding;
+
+		do {
+			if ( binding.isRoot() ) {
+				return binding;
+			}
+			binding = binding.getSuperEntityBinding();
+		} while ( binding != null );
+
+		throw new AssertionFailure( "Entity binding has no root: " + entityName );
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
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
     }
 
     public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
        this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
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
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getResolvedTypeMapping();
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
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
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
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
