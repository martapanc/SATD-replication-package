diff --git a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
index 79024e2eb1..f225e45dbb 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
@@ -1,1996 +1,1998 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot.model.source.internal.hbm;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.boot.MappingException;
 import org.hibernate.boot.jaxb.Origin;
 import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
 import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
 import org.hibernate.boot.model.Caching;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TruthValue;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.naming.EntityNaming;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
 import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
 import org.hibernate.boot.model.naming.ImplicitIdentifierColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitIndexColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitMapKeyColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
 import org.hibernate.boot.model.naming.ObjectNameNormalizer;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
 import org.hibernate.boot.model.source.spi.AnyMappingSource;
 import org.hibernate.boot.model.source.spi.AttributePath;
 import org.hibernate.boot.model.source.spi.AttributeRole;
 import org.hibernate.boot.model.source.spi.AttributeSource;
 import org.hibernate.boot.model.source.spi.CascadeStyleSource;
 import org.hibernate.boot.model.source.spi.CollectionIdSource;
 import org.hibernate.boot.model.source.spi.ColumnSource;
 import org.hibernate.boot.model.source.spi.CompositeIdentifierSource;
 import org.hibernate.boot.model.source.spi.EmbeddableSource;
 import org.hibernate.boot.model.source.spi.EntitySource;
 import org.hibernate.boot.model.source.spi.FilterSource;
 import org.hibernate.boot.model.source.spi.HibernateTypeSource;
 import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
 import org.hibernate.boot.model.source.spi.IdentifierSourceAggregatedComposite;
 import org.hibernate.boot.model.source.spi.IdentifierSourceNonAggregatedComposite;
 import org.hibernate.boot.model.source.spi.IdentifierSourceSimple;
 import org.hibernate.boot.model.source.spi.InLineViewSource;
 import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
 import org.hibernate.boot.model.source.spi.NaturalIdMutability;
 import org.hibernate.boot.model.source.spi.Orderable;
 import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceBasic;
 import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceEmbedded;
 import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToAny;
 import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToMany;
 import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceOneToMany;
 import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
 import org.hibernate.boot.model.source.spi.PluralAttributeMapKeyManyToAnySource;
 import org.hibernate.boot.model.source.spi.PluralAttributeMapKeyManyToManySource;
 import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySourceBasic;
 import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySourceEmbedded;
 import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
 import org.hibernate.boot.model.source.spi.PluralAttributeSource;
 import org.hibernate.boot.model.source.spi.PluralAttributeSourceArray;
 import org.hibernate.boot.model.source.spi.RelationalValueSource;
 import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
 import org.hibernate.boot.model.source.spi.SecondaryTableSource;
 import org.hibernate.boot.model.source.spi.SingularAttributeSource;
 import org.hibernate.boot.model.source.spi.SingularAttributeSourceAny;
 import org.hibernate.boot.model.source.spi.SingularAttributeSourceBasic;
 import org.hibernate.boot.model.source.spi.SingularAttributeSourceEmbedded;
 import org.hibernate.boot.model.source.spi.SingularAttributeSourceManyToOne;
 import org.hibernate.boot.model.source.spi.SingularAttributeSourceOneToOne;
 import org.hibernate.boot.model.source.spi.Sortable;
 import org.hibernate.boot.model.source.spi.TableSource;
 import org.hibernate.boot.model.source.spi.TableSpecificationSource;
 import org.hibernate.boot.model.source.spi.VersionAttributeSource;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.InFlightMetadataCollector;
 import org.hibernate.boot.spi.InFlightMetadataCollector.EntityTableXref;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
 import org.hibernate.cfg.FkSecondPass;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Array;
 import org.hibernate.mapping.AttributeContainer;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdentifierBag;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexBackref;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.PrimitiveArray;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Set;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.SyntheticProperty;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UnionSubclass;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.mapping.Value;
 import org.hibernate.tuple.GeneratedValueGeneration;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.ForeignKeyDirection;
 
 /**
  * Responsible for coordinating the binding of all information inside entity tags ({@code <class/>}, etc).
  *
  * @author Steve Ebersole
  */
 public class ModelBinder {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( ModelBinder.class );
 	private static final boolean debugEnabled = log.isDebugEnabled();
 
 	private final MetadataBuildingContext metadataBuildingContext;
 
 	private final Database database;
 	private final ObjectNameNormalizer objectNameNormalizer;
 	private final ImplicitNamingStrategy implicitNamingStrategy;
 	private final RelationalObjectBinder relationalObjectBinder;
 
 	public static ModelBinder prepare(MetadataBuildingContext context) {
 		return new ModelBinder( context );
 	}
 
 	public ModelBinder(final MetadataBuildingContext context) {
 		this.metadataBuildingContext = context;
 
 		this.database = context.getMetadataCollector().getDatabase();
 		this.objectNameNormalizer = new ObjectNameNormalizer() {
 			@Override
 			protected MetadataBuildingContext getBuildingContext() {
 				return context;
 			}
 		};
 		this.implicitNamingStrategy = context.getBuildingOptions().getImplicitNamingStrategy();
 		this.relationalObjectBinder = new RelationalObjectBinder( context );
 	}
 
 	public void finishUp(MetadataBuildingContext context) {
 	}
 
 	public void bindEntityHierarchy(EntityHierarchySourceImpl hierarchySource) {
 		final RootClass rootEntityDescriptor = new RootClass( metadataBuildingContext );
 		bindRootEntity( hierarchySource, rootEntityDescriptor );
 		hierarchySource.getRoot()
 				.getLocalMetadataBuildingContext()
 				.getMetadataCollector()
 				.addEntityBinding( rootEntityDescriptor );
 
 		switch ( hierarchySource.getHierarchyInheritanceType() ) {
 			case NO_INHERITANCE: {
 				// nothing to do
 				break;
 			}
 			case DISCRIMINATED: {
 				bindDiscriminatorSubclassEntities( hierarchySource.getRoot(), rootEntityDescriptor );
 				break;
 			}
 			case JOINED: {
 				bindJoinedSubclassEntities( hierarchySource.getRoot(), rootEntityDescriptor );
 				break;
 			}
 			case UNION: {
 				bindUnionSubclassEntities( hierarchySource.getRoot(), rootEntityDescriptor );
 				break;
 			}
 		}
 	}
 
 	private void bindRootEntity(EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
 		final MappingDocument mappingDocument = hierarchySource.getRoot().sourceMappingDocument();
 
 		bindBasicEntityValues(
 				mappingDocument,
 				hierarchySource.getRoot(),
 				rootEntityDescriptor
 		);
 
 		final Table primaryTable = bindEntityTableSpecification(
 				mappingDocument,
 				hierarchySource.getRoot().getPrimaryTable(),
 				null,
 				hierarchySource.getRoot(),
 				rootEntityDescriptor
 		);
 
 		rootEntityDescriptor.setTable( primaryTable );
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Mapping class: %s -> %s", rootEntityDescriptor.getEntityName(), primaryTable.getName() );
 		}
 
 		rootEntityDescriptor.setOptimisticLockStyle( hierarchySource.getOptimisticLockStyle() );
 		rootEntityDescriptor.setMutable( hierarchySource.isMutable() );
 		rootEntityDescriptor.setWhere( hierarchySource.getWhere() );
 		rootEntityDescriptor.setExplicitPolymorphism( hierarchySource.isExplicitPolymorphism() );
 
 		bindEntityIdentifier(
 				mappingDocument,
 				hierarchySource,
 				rootEntityDescriptor
 		);
 
 		if ( hierarchySource.getVersionAttributeSource() != null ) {
 			bindEntityVersion(
 					mappingDocument,
 					hierarchySource,
 					rootEntityDescriptor
 			);
 		}
 
 		if ( hierarchySource.getDiscriminatorSource() != null ) {
 			bindEntityDiscriminator(
 					mappingDocument,
 					hierarchySource,
 					rootEntityDescriptor
 			);
 		}
 
 		applyCaching( mappingDocument, hierarchySource.getCaching(), rootEntityDescriptor );
 
 		// Primary key constraint
 		rootEntityDescriptor.createPrimaryKey();
 
 		bindAllEntityAttributes(
 				mappingDocument,
 				hierarchySource.getRoot(),
 				rootEntityDescriptor
 		);
 
 		if ( hierarchySource.getNaturalIdCaching() != null ) {
 			if ( hierarchySource.getNaturalIdCaching().getRequested() == TruthValue.TRUE ) {
 				rootEntityDescriptor.setNaturalIdCacheRegionName( hierarchySource.getNaturalIdCaching().getRegion() );
 			}
 		}
 	}
 
 	private void applyCaching(MappingDocument mappingDocument, Caching caching, RootClass rootEntityDescriptor) {
 		if ( caching == null || caching.getRequested() == TruthValue.UNKNOWN ) {
 			// see if JPA's SharedCacheMode indicates we should implicitly apply caching
 			//
 			// here we only really look for ALL.  Ideally we could look at NONE too as a means
 			// to selectively disable all caching, but historically hbm.xml mappings were processed
 			// outside this concept and whether to cache or not was defined wholly by what
 			// is defined in the mapping document.  So for backwards compatibility we
 			// do not consider ENABLE_SELECTIVE nor DISABLE_SELECTIVE here.
 			//
 			// Granted, ALL was not historically considered either, but I have a practical
 			// reason for wanting to support this... our legacy tests built using
 			// Configuration applied a similar logic but that capability is no longer
 			// accessible from Configuration
 			switch ( mappingDocument.getBuildingOptions().getSharedCacheMode() ) {
 				case ALL: {
 					caching = new Caching(
 							null,
 							mappingDocument.getBuildingOptions().getImplicitCacheAccessType(),
 							false,
 							TruthValue.UNKNOWN
 					);
 					break;
 				}
 				case NONE: {
 					// Ideally we'd disable all caching...
 					break;
 				}
 				case ENABLE_SELECTIVE: {
 					// this is default behavior for hbm.xml
 					break;
 				}
 				case DISABLE_SELECTIVE: {
 					// really makes no sense for hbm.xml
 					break;
 				}
 				default: {
 					// null or UNSPECIFIED, nothing to do.  IMO for hbm.xml this is equivalent
 					// to ENABLE_SELECTIVE
 					break;
 				}
 			}
 		}
 
 		if ( caching == null || caching.getRequested() == TruthValue.FALSE ) {
 			return;
 		}
 
 		if ( caching.getAccessType() != null ) {
 			rootEntityDescriptor.setCacheConcurrencyStrategy( caching.getAccessType().getExternalName() );
 		}
 		else {
 			rootEntityDescriptor.setCacheConcurrencyStrategy( mappingDocument.getBuildingOptions().getImplicitCacheAccessType().getExternalName() );
 		}
 		rootEntityDescriptor.setCacheRegionName( caching.getRegion() );
 		rootEntityDescriptor.setLazyPropertiesCacheable( caching.isCacheLazyProperties() );
 		rootEntityDescriptor.setCachingExplicitlyRequested( caching.getRequested() != TruthValue.UNKNOWN );
 	}
 
 	private void bindEntityIdentifier(
 			MappingDocument mappingDocument,
 			EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 		switch ( hierarchySource.getIdentifierSource().getNature() ) {
 			case SIMPLE: {
 				bindSimpleEntityIdentifier(
 						mappingDocument,
 						hierarchySource,
 						rootEntityDescriptor
 				);
 				break;
 			}
 			case AGGREGATED_COMPOSITE: {
 				bindAggregatedCompositeEntityIdentifier(
 						mappingDocument,
 						hierarchySource,
 						rootEntityDescriptor
 				);
 				break;
 			}
 			case NON_AGGREGATED_COMPOSITE: {
 				bindNonAggregatedCompositeEntityIdentifier(
 						mappingDocument,
 						hierarchySource,
 						rootEntityDescriptor
 				);
 				break;
 			}
 			default: {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Unexpected entity identifier nature [%s] for entity %s",
 								hierarchySource.getIdentifierSource().getNature(),
 								hierarchySource.getRoot().getEntityNamingSource().getEntityName()
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 		}
 	}
 
 	private void bindBasicEntityValues(
 			MappingDocument sourceDocument,
 			AbstractEntitySourceImpl entitySource,
 			PersistentClass entityDescriptor) {
 		entityDescriptor.setEntityName( entitySource.getEntityNamingSource().getEntityName() );
 		entityDescriptor.setJpaEntityName( entitySource.getEntityNamingSource().getJpaEntityName() );
 		entityDescriptor.setClassName( entitySource.getEntityNamingSource().getClassName() );
 
 		entityDescriptor.setDiscriminatorValue(
 				entitySource.getDiscriminatorMatchValue() != null
 						? entitySource.getDiscriminatorMatchValue()
 						: entityDescriptor.getEntityName()
 		);
 
 		// NOTE : entitySource#isLazy already accounts for MappingDefaults#areEntitiesImplicitlyLazy
 		if ( StringHelper.isNotEmpty( entitySource.getProxy() ) ) {
 			final String qualifiedProxyName = sourceDocument.qualifyClassName( entitySource.getProxy() );
 			entityDescriptor.setProxyInterfaceName( qualifiedProxyName );
 			entityDescriptor.setLazy( true );
 		}
 		else if ( entitySource.isLazy() ) {
 			entityDescriptor.setProxyInterfaceName( entityDescriptor.getClassName() );
 			entityDescriptor.setLazy( true );
 		}
 		else {
 			entityDescriptor.setProxyInterfaceName( null );
 			entityDescriptor.setLazy( false );
 		}
 
 		entityDescriptor.setAbstract( entitySource.isAbstract() );
 
 		sourceDocument.getMetadataCollector().addImport(
 				entitySource.getEntityNamingSource().getEntityName(),
 				entitySource.getEntityNamingSource().getEntityName()
 		);
 
 		if ( sourceDocument.getMappingDefaults().isAutoImportEnabled() && entitySource.getEntityNamingSource().getEntityName().indexOf( '.' ) > 0 ) {
 			sourceDocument.getMetadataCollector().addImport(
 					StringHelper.unqualify( entitySource.getEntityNamingSource().getEntityName() ),
 					entitySource.getEntityNamingSource().getEntityName()
 			);
 		}
 
 		if ( entitySource.getTuplizerClassMap() != null ) {
 			if ( entitySource.getTuplizerClassMap().size() > 1 ) {
 				DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
 			}
 			for ( Map.Entry<EntityMode,String> tuplizerEntry : entitySource.getTuplizerClassMap().entrySet() ) {
 				entityDescriptor.addTuplizer(
 						tuplizerEntry.getKey(),
 						tuplizerEntry.getValue()
 				);
 			}
 		}
 
 		if ( StringHelper.isNotEmpty( entitySource.getXmlNodeName() ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
 		}
 
 		entityDescriptor.setDynamicInsert( entitySource.isDynamicInsert() );
 		entityDescriptor.setDynamicUpdate( entitySource.isDynamicUpdate() );
 		entityDescriptor.setBatchSize( entitySource.getBatchSize() );
 		entityDescriptor.setSelectBeforeUpdate( entitySource.isSelectBeforeUpdate() );
 
 		if ( StringHelper.isNotEmpty( entitySource.getCustomPersisterClassName() ) ) {
 			try {
 				entityDescriptor.setEntityPersisterClass(
 						sourceDocument.getClassLoaderAccess().classForName( entitySource.getCustomPersisterClassName() )
 				);
 			}
 			catch (ClassLoadingException e) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Unable to load specified persister class : %s",
 								entitySource.getCustomPersisterClassName()
 						),
 						e,
 						sourceDocument.getOrigin()
 				);
 			}
 		}
 
 		bindCustomSql( sourceDocument, entitySource, entityDescriptor );
 
 		final JdbcEnvironment jdbcEnvironment = sourceDocument.getMetadataCollector().getDatabase().getJdbcEnvironment();
 
 		for ( String tableName : entitySource.getSynchronizedTableNames() ) {
 			final Identifier physicalTableName = sourceDocument.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 					jdbcEnvironment.getIdentifierHelper().toIdentifier( tableName ),
 					jdbcEnvironment
 			);
 			entityDescriptor.addSynchronizedTable( physicalTableName.render( jdbcEnvironment.getDialect() ) );
 		}
 
 		for ( FilterSource filterSource : entitySource.getFilterSources() ) {
 			String condition = filterSource.getCondition();
 			if ( condition == null ) {
 				final FilterDefinition filterDefinition = sourceDocument.getMetadataCollector().getFilterDefinition( filterSource.getName() );
 				if ( filterDefinition != null ) {
 					condition = filterDefinition.getDefaultFilterCondition();
 				}
 			}
 
 			entityDescriptor.addFilter(
 					filterSource.getName(),
 					condition,
 					filterSource.shouldAutoInjectAliases(),
 					filterSource.getAliasToTableMap(),
 					filterSource.getAliasToEntityMap()
 			);
 		}
 
 		for ( JaxbHbmNamedQueryType namedQuery : entitySource.getNamedQueries() ) {
 			NamedQueryBinder.processNamedQuery(
 					sourceDocument,
 					namedQuery,
 					entitySource.getEntityNamingSource().getEntityName() + "."
 			);
 		}
 		for ( JaxbHbmNamedNativeQueryType namedQuery : entitySource.getNamedNativeQueries() ) {
 			NamedQueryBinder.processNamedNativeQuery(
 					sourceDocument,
 					namedQuery,
 					entitySource.getEntityNamingSource().getEntityName() + "."
 			);
 		}
 
 		entityDescriptor.setMetaAttributes( entitySource.getToolingHintContext().getMetaAttributeMap() );
 	}
 
 	private void bindDiscriminatorSubclassEntities(
 			AbstractEntitySourceImpl entitySource,
 			PersistentClass superEntityDescriptor) {
 		for ( IdentifiableTypeSource subType : entitySource.getSubTypes() ) {
 			final SingleTableSubclass subEntityDescriptor = new SingleTableSubclass( superEntityDescriptor, metadataBuildingContext );
 			bindDiscriminatorSubclassEntity( (SubclassEntitySourceImpl) subType, subEntityDescriptor );
 			superEntityDescriptor.addSubclass( subEntityDescriptor );
 			entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding( subEntityDescriptor );
 		}
 	}
 
 	private void bindDiscriminatorSubclassEntity(
 			SubclassEntitySourceImpl entitySource,
 			SingleTableSubclass entityDescriptor) {
 
 		bindBasicEntityValues(
 				entitySource.sourceMappingDocument(),
 				entitySource,
 				entityDescriptor
 		);
 
 		final String superEntityName = ( (EntitySource) entitySource.getSuperType() ).getEntityNamingSource()
 				.getEntityName();
 		final EntityTableXref superEntityTableXref = entitySource.getLocalMetadataBuildingContext()
 				.getMetadataCollector()
 				.getEntityTableXref( superEntityName );
 		if ( superEntityTableXref == null ) {
 			throw new MappingException(
 					String.format(
 							Locale.ENGLISH,
 							"Unable to locate entity table xref for entity [%s] super-type [%s]",
 							entityDescriptor.getEntityName(),
 							superEntityName
 					),
 					entitySource.origin()
 			);
 		}
 
 		entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityTableXref(
 				entitySource.getEntityNamingSource().getEntityName(),
 				database.toIdentifier(
 						entitySource.getLocalMetadataBuildingContext().getMetadataCollector().getLogicalTableName(
 								entityDescriptor.getTable()
 						)
 				),
 				entityDescriptor.getTable(),
 				superEntityTableXref
 		);
 
 		bindAllEntityAttributes(
 				entitySource.sourceMappingDocument(),
 				entitySource,
 				entityDescriptor
 		);
 
 		bindDiscriminatorSubclassEntities( entitySource, entityDescriptor );
 	}
 
 	private void bindJoinedSubclassEntities(
 			AbstractEntitySourceImpl entitySource,
 			PersistentClass superEntityDescriptor) {
 		for ( IdentifiableTypeSource subType : entitySource.getSubTypes() ) {
 			final JoinedSubclass subEntityDescriptor = new JoinedSubclass( superEntityDescriptor, metadataBuildingContext );
 			bindJoinedSubclassEntity( (JoinedSubclassEntitySourceImpl) subType, subEntityDescriptor );
 			superEntityDescriptor.addSubclass( subEntityDescriptor );
 			entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding( subEntityDescriptor );
 		}
 	}
 
 	private void bindJoinedSubclassEntity(
 			JoinedSubclassEntitySourceImpl entitySource,
 			JoinedSubclass entityDescriptor) {
 		MappingDocument mappingDocument = entitySource.sourceMappingDocument();
 
 		bindBasicEntityValues(
 				mappingDocument,
 				entitySource,
 				entityDescriptor
 		);
 
 		final Table primaryTable = bindEntityTableSpecification(
 				mappingDocument,
 				entitySource.getPrimaryTable(),
 				null,
 				entitySource,
 				entityDescriptor
 		);
 
 		entityDescriptor.setTable( primaryTable );
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Mapping joined-subclass: %s -> %s", entityDescriptor.getEntityName(), primaryTable.getName() );
 		}
 
 		// KEY
 		final SimpleValue keyBinding = new DependantValue(
 				mappingDocument.getMetadataCollector(),
 				primaryTable,
 				entityDescriptor.getIdentifier()
 		);
 		if ( mappingDocument.getBuildingOptions().useNationalizedCharacterData() ) {
 			keyBinding.makeNationalized();
 		}
 		entityDescriptor.setKey( keyBinding );
 		keyBinding.setCascadeDeleteEnabled( entitySource.isCascadeDeleteEnabled() );
 		relationalObjectBinder.bindColumns(
 				mappingDocument,
 				entitySource.getPrimaryKeyColumnSources(),
 				keyBinding,
 				false,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					int count = 0;
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						final Column column = primaryTable.getPrimaryKey().getColumn( count++ );
 						return database.toIdentifier( column.getQuotedName() );
 					}
 				}
 		);
 		keyBinding.setForeignKeyName( entitySource.getExplicitForeignKeyName() );
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		entityDescriptor.createPrimaryKey();
 		entityDescriptor.createForeignKey();
 
 		// todo : tooling hints
 
 		bindAllEntityAttributes(
 				entitySource.sourceMappingDocument(),
 				entitySource,
 				entityDescriptor
 		);
 
 		bindJoinedSubclassEntities( entitySource, entityDescriptor );
 	}
 
 	private void bindUnionSubclassEntities(
 			EntitySource entitySource,
 			PersistentClass superEntityDescriptor) {
 		for ( IdentifiableTypeSource subType : entitySource.getSubTypes() ) {
 			final UnionSubclass subEntityDescriptor = new UnionSubclass( superEntityDescriptor, metadataBuildingContext );
 			bindUnionSubclassEntity( (SubclassEntitySourceImpl) subType, subEntityDescriptor );
 			superEntityDescriptor.addSubclass( subEntityDescriptor );
 			entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding( subEntityDescriptor );
 		}
 	}
 
 	private void bindUnionSubclassEntity(
 			SubclassEntitySourceImpl entitySource,
 			UnionSubclass entityDescriptor) {
 		MappingDocument mappingDocument = entitySource.sourceMappingDocument();
 
 		bindBasicEntityValues(
 				mappingDocument,
 				entitySource,
 				entityDescriptor
 		);
 
 		final Table primaryTable = bindEntityTableSpecification(
 				mappingDocument,
 				entitySource.getPrimaryTable(),
 				entityDescriptor.getSuperclass().getTable(),
 				entitySource,
 				entityDescriptor
 		);
 		entityDescriptor.setTable( primaryTable );
 
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Mapping union-subclass: %s -> %s", entityDescriptor.getEntityName(), primaryTable.getName() );
 		}
 
 		// todo : tooling hints
 
 		bindAllEntityAttributes(
 				entitySource.sourceMappingDocument(),
 				entitySource,
 				entityDescriptor
 		);
 
 		bindUnionSubclassEntities( entitySource, entityDescriptor );
 	}
 
 	private void bindSimpleEntityIdentifier(
 			MappingDocument sourceDocument,
 			final EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 		final IdentifierSourceSimple idSource = (IdentifierSourceSimple) hierarchySource.getIdentifierSource();
 
 		final SimpleValue idValue = new SimpleValue(
 				sourceDocument.getMetadataCollector(),
 				rootEntityDescriptor.getTable()
 		);
 		rootEntityDescriptor.setIdentifier( idValue );
 
 		bindSimpleValueType(
 				sourceDocument,
 				idSource.getIdentifierAttributeSource().getTypeInformation(),
 				idValue
 		);
 
 		final String propertyName = idSource.getIdentifierAttributeSource().getName();
 		if ( propertyName == null || !rootEntityDescriptor.hasPojoRepresentation() ) {
 			if ( !idValue.isTypeSpecified() ) {
 				throw new MappingException(
 						"must specify an identifier type: " + rootEntityDescriptor.getEntityName(),
 						sourceDocument.getOrigin()
 				);
 			}
 		}
 		else {
 			idValue.setTypeUsingReflection( rootEntityDescriptor.getClassName(), propertyName );
 		}
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				( (RelationalValueSourceContainer) idSource.getIdentifierAttributeSource() ).getRelationalValueSources(),
 				idValue,
 				false,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 						context.getBuildingOptions().getImplicitNamingStrategy().determineIdentifierColumnName(
 								new ImplicitIdentifierColumnNameSource() {
 									@Override
 									public EntityNaming getEntityNaming() {
 										return hierarchySource.getRoot().getEntityNamingSource();
 									}
 
 									@Override
 									public AttributePath getIdentifierAttributePath() {
 										return idSource.getIdentifierAttributeSource().getAttributePath();
 									}
 
 									@Override
 									public MetadataBuildingContext getBuildingContext() {
 										return context;
 									}
 								}
 						);
 						return database.toIdentifier( propertyName );
 					}
 				}
 		);
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( idValue );
 			bindProperty(
 					sourceDocument,
 					idSource.getIdentifierAttributeSource(),
 					prop
 			);
 			rootEntityDescriptor.setIdentifierProperty( prop );
 			rootEntityDescriptor.setDeclaredIdentifierProperty( prop );
 		}
 
 		makeIdentifier(
 				sourceDocument,
 				idSource.getIdentifierGeneratorDescriptor(),
 				idSource.getUnsavedValue(),
 				idValue
 		);
 	}
 
 	private void makeIdentifier(
 			final MappingDocument sourceDocument,
 			IdentifierGeneratorDefinition generator,
 			String unsavedValue,
 			SimpleValue identifierValue) {
 		if ( generator != null ) {
 			String generatorName = generator.getStrategy();
 			Properties params = new Properties();
 
 			// see if the specified generator name matches a registered <identifier-generator/>
 			IdentifierGeneratorDefinition generatorDef = sourceDocument.getMetadataCollector().getIdentifierGenerator( generatorName );
 			if ( generatorDef != null ) {
 				generatorName = generatorDef.getStrategy();
 				params.putAll( generatorDef.getParameters() );
 			}
 
 			identifierValue.setIdentifierGeneratorStrategy( generatorName );
 
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, objectNameNormalizer);
 
 			if ( database.getDefaultNamespace().getPhysicalName().getSchema() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						database.getDefaultNamespace().getPhysicalName().getSchema().render( database.getDialect() )
 				);
 			}
 			if ( database.getDefaultNamespace().getPhysicalName().getCatalog() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						database.getDefaultNamespace().getPhysicalName().getCatalog().render( database.getDialect() )
 				);
 			}
 
 			params.putAll( generator.getParameters() );
 
 			identifierValue.setIdentifierGeneratorProperties( params );
 		}
 
 		identifierValue.getTable().setIdentifierValue( identifierValue );
 
 		if ( StringHelper.isNotEmpty( unsavedValue ) ) {
 			identifierValue.setNullValue( unsavedValue );
 		}
 		else {
 			if ( "assigned".equals( identifierValue.getIdentifierGeneratorStrategy() ) ) {
 				identifierValue.setNullValue( "undefined" );
 			}
 			else {
 				identifierValue.setNullValue( null );
 			}
 		}
 	}
 
 	private void bindAggregatedCompositeEntityIdentifier(
 			MappingDocument mappingDocument,
 			EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 
 		// an aggregated composite-id is a composite-id that defines a singular
 		// (composite) attribute as part of the entity to represent the id.
 
 		final IdentifierSourceAggregatedComposite identifierSource
 				= (IdentifierSourceAggregatedComposite) hierarchySource.getIdentifierSource();
 
 		final Component cid = new Component( mappingDocument.getMetadataCollector(), rootEntityDescriptor );
 		cid.setKey( true );
 		rootEntityDescriptor.setIdentifier( cid );
 
 		final String idClassName = extractIdClassName( identifierSource );
 
 		final String idPropertyName = identifierSource.getIdentifierAttributeSource().getName();
 		final String pathPart = idPropertyName == null ? "<id>" : idPropertyName;
 
 		bindComponent(
 				mappingDocument,
 				hierarchySource.getRoot().getAttributeRoleBase().append( pathPart ).getFullPath(),
 				identifierSource.getEmbeddableSource(),
 				cid,
 				idClassName,
 				rootEntityDescriptor.getClassName(),
 				idPropertyName,
 				idClassName == null && idPropertyName == null,
 				identifierSource.getEmbeddableSource().isDynamic(),
 				identifierSource.getIdentifierAttributeSource().getXmlNodeName()
 		);
 
 		finishBindingCompositeIdentifier(
 				mappingDocument,
 				rootEntityDescriptor,
 				identifierSource,
 				cid,
 				idPropertyName
 		);
 	}
 
 	private String extractIdClassName(IdentifierSourceAggregatedComposite identifierSource) {
 		if ( identifierSource.getEmbeddableSource().getTypeDescriptor() == null ) {
 			return null;
 		}
 
 		return identifierSource.getEmbeddableSource().getTypeDescriptor().getName();
 	}
 
 	private static final String ID_MAPPER_PATH_PART = '<' + PropertyPath.IDENTIFIER_MAPPER_PROPERTY + '>';
 
 	private void bindNonAggregatedCompositeEntityIdentifier(
 			MappingDocument mappingDocument,
 			EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 		final IdentifierSourceNonAggregatedComposite identifierSource
 				= (IdentifierSourceNonAggregatedComposite) hierarchySource.getIdentifierSource();
 
 		final Component cid = new Component( mappingDocument.getMetadataCollector(), rootEntityDescriptor );
 		cid.setKey( true );
 		rootEntityDescriptor.setIdentifier( cid );
 
 		final String idClassName = extractIdClassName( identifierSource );
 
 		bindComponent(
 				mappingDocument,
 				hierarchySource.getRoot().getAttributeRoleBase().append( "<id>" ).getFullPath(),
 				identifierSource.getEmbeddableSource(),
 				cid,
 				idClassName,
 				rootEntityDescriptor.getClassName(),
 				null,
 				idClassName == null,
 				false,
 				null
 		);
 
 		if ( idClassName != null ) {
 			// we also need to bind the "id mapper".  ugh, terrible name.  Basically we need to
 			// create a virtual (embedded) composite for the non-aggregated attributes on the entity
 			// itself.
 			final Component mapper = new Component( mappingDocument.getMetadataCollector(), rootEntityDescriptor );
 			bindComponent(
 					mappingDocument,
 					hierarchySource.getRoot().getAttributeRoleBase().append( ID_MAPPER_PATH_PART ).getFullPath(),
 					identifierSource.getEmbeddableSource(),
 					mapper,
 					rootEntityDescriptor.getClassName(),
 					null,
 					null,
 					true,
 					false,
 					null
 			);
 
 			rootEntityDescriptor.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName( PropertyPath.IDENTIFIER_MAPPER_PROPERTY );
 			property.setUpdateable( false );
 			property.setInsertable( false );
 			property.setValue( mapper );
 			property.setPropertyAccessorName( "embedded" );
 			rootEntityDescriptor.addProperty( property );
 		}
 
 		finishBindingCompositeIdentifier( mappingDocument, rootEntityDescriptor, identifierSource, cid, null );
 	}
 
 	private String extractIdClassName(IdentifierSourceNonAggregatedComposite identifierSource) {
 		if ( identifierSource.getIdClassSource() == null ) {
 			return null;
 		}
 
 		if ( identifierSource.getIdClassSource().getTypeDescriptor() == null ) {
 			return null;
 		}
 
 		return identifierSource.getIdClassSource().getTypeDescriptor().getName();
 	}
 
 	private void finishBindingCompositeIdentifier(
 			MappingDocument sourceDocument,
 			RootClass rootEntityDescriptor,
 			CompositeIdentifierSource identifierSource,
 			Component cid,
 			String propertyName) {
 		if ( propertyName == null ) {
 			rootEntityDescriptor.setEmbeddedIdentifier( cid.isEmbedded() );
 			if ( cid.isEmbedded() ) {
 				// todo : what is the implication of this?
 				cid.setDynamic( !rootEntityDescriptor.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( cid );
 			bindProperty(
 					sourceDocument,
 					( (IdentifierSourceAggregatedComposite) identifierSource ).getIdentifierAttributeSource(),
 					prop
 			);
 			rootEntityDescriptor.setIdentifierProperty( prop );
 			rootEntityDescriptor.setDeclaredIdentifierProperty( prop );
 		}
 
 		makeIdentifier(
 				sourceDocument,
 				identifierSource.getIdentifierGeneratorDescriptor(),
 				null,
 				cid
 		);
 	}
 
 	private void bindEntityVersion(
 			MappingDocument sourceDocument,
 			EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 		final VersionAttributeSource versionAttributeSource = hierarchySource.getVersionAttributeSource();
 
 		final SimpleValue versionValue = new SimpleValue(
 				sourceDocument.getMetadataCollector(),
 				rootEntityDescriptor.getTable()
 		);
 
+		versionValue.makeVersion();
+
 		bindSimpleValueType(
 				sourceDocument,
 				versionAttributeSource.getTypeInformation(),
 				versionValue
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				versionAttributeSource.getRelationalValueSources(),
 				versionValue,
 				false,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineBasicColumnName( versionAttributeSource );
 					}
 				}
 		);
 
 		Property prop = new Property();
 		prop.setValue( versionValue );
 		bindProperty(
 				sourceDocument,
 				versionAttributeSource,
 				prop
 		);
 
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getValueGenerationStrategy() != null ) {
 			if ( prop.getValueGenerationStrategy().getGenerationTiming() == GenerationTiming.INSERT ) {
 				throw new MappingException(
 						"'generated' attribute cannot be 'insert' for version/timestamp property",
 						sourceDocument.getOrigin()
 				);
 			}
 		}
 
 		if ( versionAttributeSource.getUnsavedValue() != null ) {
 			versionValue.setNullValue( versionAttributeSource.getUnsavedValue() );
 		}
 		else {
 			versionValue.setNullValue( "undefined" );
 		}
 
 		rootEntityDescriptor.setVersion( prop );
 		rootEntityDescriptor.addProperty( prop );
 	}
 
 	private void bindEntityDiscriminator(
 			MappingDocument sourceDocument,
 			final EntityHierarchySourceImpl hierarchySource,
 			RootClass rootEntityDescriptor) {
 		final SimpleValue discriminatorValue = new SimpleValue(
 				sourceDocument.getMetadataCollector(),
 				rootEntityDescriptor.getTable()
 		);
 		rootEntityDescriptor.setDiscriminator( discriminatorValue );
 
 		String typeName = hierarchySource.getDiscriminatorSource().getExplicitHibernateTypeName();
 		if ( typeName == null ) {
 			typeName = "string";
 		}
 		bindSimpleValueType(
 				sourceDocument,
 				new HibernateTypeSourceImpl( typeName ),
 				discriminatorValue
 		);
 
 		relationalObjectBinder.bindColumnOrFormula(
 				sourceDocument,
 				hierarchySource.getDiscriminatorSource().getDiscriminatorRelationalValueSource(),
 				discriminatorValue,
 				false,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineDiscriminatorColumnName(
 								hierarchySource.getDiscriminatorSource()
 						);
 					}
 				}
 		);
 
 		rootEntityDescriptor.setPolymorphic( true );
 		rootEntityDescriptor.setDiscriminatorInsertable( hierarchySource.getDiscriminatorSource().isInserted() );
 
 		// todo : currently isForced() is defined as boolean, not Boolean
 		//		although it has always been that way (DTD too)
 		final boolean force = hierarchySource.getDiscriminatorSource().isForced()
 				|| sourceDocument.getBuildingOptions().shouldImplicitlyForceDiscriminatorInSelect();
 		rootEntityDescriptor.setForceDiscriminator( force );
 	}
 
 	private void bindAllEntityAttributes(
 			MappingDocument mappingDocument,
 			EntitySource entitySource,
 			PersistentClass entityDescriptor) {
 		final EntityTableXref entityTableXref = mappingDocument.getMetadataCollector().getEntityTableXref(
 				entityDescriptor.getEntityName()
 		);
 		if ( entityTableXref == null ) {
 			throw new AssertionFailure(
 					String.format(
 							Locale.ENGLISH,
 							"Unable to locate EntityTableXref for entity [%s] : %s",
 							entityDescriptor.getEntityName(),
 							mappingDocument.getOrigin()
 					)
 			);
 		}
 
 		// make sure we bind secondary tables first!
 		for ( SecondaryTableSource secondaryTableSource : entitySource.getSecondaryTableMap().values() ) {
 			final Join secondaryTableJoin = new Join();
 			secondaryTableJoin.setPersistentClass( entityDescriptor );
 			bindSecondaryTable(
 					mappingDocument,
 					secondaryTableSource,
 					secondaryTableJoin,
 					entityTableXref
 			);
 			entityDescriptor.addJoin( secondaryTableJoin );
 		}
 
 		for ( AttributeSource attributeSource : entitySource.attributeSources() ) {
 			if ( PluralAttributeSource.class.isInstance( attributeSource ) ) {
 				// plural attribute
 				final Property attribute = createPluralAttribute(
 						mappingDocument,
 						(PluralAttributeSource) attributeSource,
 						entityDescriptor
 				);
 				entityDescriptor.addProperty( attribute );
 			}
 			else {
 				// singular attribute
 				if ( SingularAttributeSourceBasic.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceBasic basicAttributeSource = (SingularAttributeSourceBasic) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, basicAttributeSource.getName(), basicAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createBasicAttribute(
 							mappingDocument,
 							basicAttributeSource,
 							new SimpleValue( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							basicAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceEmbedded embeddedAttributeSource = (SingularAttributeSourceEmbedded) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, embeddedAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createEmbeddedAttribute(
 							mappingDocument,
 							(SingularAttributeSourceEmbedded) attributeSource,
 							new Component( mappingDocument.getMetadataCollector(), table, entityDescriptor ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							embeddedAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceManyToOne.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceManyToOne manyToOneAttributeSource = (SingularAttributeSourceManyToOne) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, manyToOneAttributeSource.getName(), manyToOneAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createManyToOneAttribute(
 							mappingDocument,
 							manyToOneAttributeSource,
 							new ManyToOne( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							manyToOneAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceOneToOne.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceOneToOne oneToOneAttributeSource = (SingularAttributeSourceOneToOne) attributeSource;
 					final Table table = entityDescriptor.getTable();
 					final Property attribute = createOneToOneAttribute(
 							mappingDocument,
 							oneToOneAttributeSource,
 							new OneToOne( mappingDocument.getMetadataCollector(), table, entityDescriptor ),
 							entityDescriptor.getClassName()
 					);
 					entityDescriptor.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							oneToOneAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceAny anyAttributeSource = (SingularAttributeSourceAny) attributeSource;
 					final Identifier tableName = determineTable(
 							mappingDocument,
 							anyAttributeSource.getName(),
 							anyAttributeSource.getKeySource().getRelationalValueSources()
 					);
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createAnyAssociationAttribute(
 							mappingDocument,
 							anyAttributeSource,
 							new Any( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getEntityName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							anyAttributeSource.getNaturalIdMutability()
 					);
 				}
 			}
 		}
 	}
 
 	private void handleNaturalIdBinding(
 			MappingDocument mappingDocument,
 			PersistentClass entityBinding,
 			Property attributeBinding,
 			NaturalIdMutability naturalIdMutability) {
 		if ( naturalIdMutability == NaturalIdMutability.NOT_NATURAL_ID ) {
 			return;
 		}
 
 		attributeBinding.setNaturalIdentifier( true );
 
 		if ( naturalIdMutability == NaturalIdMutability.IMMUTABLE ) {
 			attributeBinding.setUpdateable( false );
 		}
 
 		NaturalIdUniqueKeyBinder ukBinder = mappingDocument.getMetadataCollector().locateNaturalIdUniqueKeyBinder(
 				entityBinding.getEntityName()
 		);
 
 		if ( ukBinder == null ) {
 			ukBinder = new NaturalIdUniqueKeyBinderImpl( mappingDocument, entityBinding );
 			mappingDocument.getMetadataCollector().registerNaturalIdUniqueKeyBinder(
 					entityBinding.getEntityName(),
 					ukBinder
 			);
 		}
 
 		ukBinder.addAttributeBinding( attributeBinding );
 	}
 
 	private Property createPluralAttribute(
 			MappingDocument sourceDocument,
 			PluralAttributeSource attributeSource,
 			PersistentClass entityDescriptor) {
 		final Collection collectionBinding;
 
 		if ( attributeSource instanceof PluralAttributeSourceListImpl ) {
 			collectionBinding = new org.hibernate.mapping.List( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeListSecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(org.hibernate.mapping.List) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceSetImpl ) {
 			collectionBinding = new Set( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeSetSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceMapImpl ) {
 			collectionBinding = new org.hibernate.mapping.Map( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeMapSecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(org.hibernate.mapping.Map) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceBagImpl ) {
 			collectionBinding = new Bag( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeBagSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceIdBagImpl ) {
 			collectionBinding = new IdentifierBag( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeIdBagSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceArrayImpl ) {
 			final PluralAttributeSourceArray arraySource = (PluralAttributeSourceArray) attributeSource;
 			collectionBinding = new Array( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			( (Array) collectionBinding ).setElementClassName(
 					sourceDocument.qualifyClassName( arraySource.getElementClass() )
 			);
 
 			registerSecondPass(
 					new PluralAttributeArraySecondPass(
 							sourceDocument,
 							arraySource,
 							(Array) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourcePrimitiveArrayImpl ) {
 			collectionBinding = new PrimitiveArray( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributePrimitiveArraySecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(PrimitiveArray) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else {
 			throw new AssertionFailure(
 					"Unexpected PluralAttributeSource type : " + attributeSource.getClass().getName()
 			);
 		}
 
 		sourceDocument.getMetadataCollector().addCollectionBinding( collectionBinding );
 
 		final Property attribute = new Property();
 		attribute.setValue( collectionBinding );
 		bindProperty(
 				sourceDocument,
 				attributeSource,
 				attribute
 		);
 
 		return attribute;
 	}
 
 	private void bindCollectionMetadata(MappingDocument mappingDocument, PluralAttributeSource source, Collection binding) {
 		binding.setRole( source.getAttributeRole().getFullPath() );
 		binding.setInverse( source.isInverse() );
 		binding.setMutable( source.isMutable() );
 		binding.setOptimisticLocked( source.isIncludedInOptimisticLocking() );
 
 		if ( source.getCustomPersisterClassName() != null ) {
 			binding.setCollectionPersisterClass(
 					mappingDocument.getClassLoaderAccess().classForName(
 							mappingDocument.qualifyClassName( source.getCustomPersisterClassName() )
 					)
 			);
 		}
 
 		applyCaching( mappingDocument, source.getCaching(), binding );
 
 		// bind the collection type info
 		String typeName = source.getTypeInformation().getName();
 		Map typeParameters = new HashMap();
 		if ( typeName != null ) {
 			// see if there is a corresponding type-def
 			final TypeDefinition typeDef = mappingDocument.getMetadataCollector().getTypeDefinition( typeName );
 			if ( typeDef != null ) {
 				typeName = typeDef.getTypeImplementorClass().getName();
 				if ( typeDef.getParameters() != null ) {
 					typeParameters.putAll( typeDef.getParameters() );
 				}
 			}
 			else {
 				// it could be a unqualified class name, in which case we should qualify
 				// it with the implicit package name for this context, if one.
 				typeName = mappingDocument.qualifyClassName( typeName );
 			}
 		}
 		if ( source.getTypeInformation().getParameters() != null ) {
 			typeParameters.putAll( source.getTypeInformation().getParameters() );
 		}
 
 		binding.setTypeName( typeName );
 		binding.setTypeParameters( typeParameters );
 
 		if ( source.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED ) {
 			binding.setLazy( true );
 			binding.setExtraLazy( source.getFetchCharacteristics().isExtraLazy() );
 		}
 		else {
 			binding.setLazy( false );
 		}
 
 		switch ( source.getFetchCharacteristics().getFetchStyle() ) {
 			case SELECT: {
 				binding.setFetchMode( FetchMode.SELECT );
 				break;
 			}
 			case JOIN: {
 				binding.setFetchMode( FetchMode.JOIN );
 				break;
 			}
 			case BATCH: {
 				binding.setFetchMode( FetchMode.SELECT );
 				binding.setBatchSize( source.getFetchCharacteristics().getBatchSize() );
 				break;
 			}
 			case SUBSELECT: {
 				binding.setFetchMode( FetchMode.SELECT );
 				binding.setSubselectLoadable( true );
 				// todo : this could totally be done using a "symbol map" approach
 				binding.getOwner().setSubselectLoadableCollections( true );
 				break;
 			}
 			default: {
 				throw new AssertionFailure( "Unexpected FetchStyle : " + source.getFetchCharacteristics().getFetchStyle().name() );
 			}
 		}
 
 		for ( String name : source.getSynchronizedTableNames() ) {
 			binding.getSynchronizedTables().add( name );
 		}
 
 		binding.setWhere( source.getWhere() );
 		binding.setLoaderName( source.getCustomLoaderName() );
 		if ( source.getCustomSqlInsert() != null ) {
 			binding.setCustomSQLInsert(
 					source.getCustomSqlInsert().getSql(),
 					source.getCustomSqlInsert().isCallable(),
 					source.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlUpdate() != null ) {
 			binding.setCustomSQLUpdate(
 					source.getCustomSqlUpdate().getSql(),
 					source.getCustomSqlUpdate().isCallable(),
 					source.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlDelete() != null ) {
 			binding.setCustomSQLDelete(
 					source.getCustomSqlDelete().getSql(),
 					source.getCustomSqlDelete().isCallable(),
 					source.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlDeleteAll() != null ) {
 			binding.setCustomSQLDeleteAll(
 					source.getCustomSqlDeleteAll().getSql(),
 					source.getCustomSqlDeleteAll().isCallable(),
 					source.getCustomSqlDeleteAll().getCheckStyle()
 			);
 		}
 
 		if ( source instanceof Sortable ) {
 			final Sortable sortable = (Sortable) source;
 			if ( sortable.isSorted() ) {
 				binding.setSorted( true );
 				if ( ! sortable.getComparatorName().equals( "natural" ) ) {
 					binding.setComparatorClassName( sortable.getComparatorName() );
 				}
 			}
 			else {
 				binding.setSorted( false );
 			}
 		}
 
 		if ( source instanceof Orderable ) {
 			if ( ( (Orderable) source ).isOrdered() ) {
 				binding.setOrderBy( ( (Orderable) source ).getOrder() );
 			}
 		}
 
 		final String cascadeStyle = source.getCascadeStyleName();
 		if ( cascadeStyle != null && cascadeStyle.contains( "delete-orphan" ) ) {
 			binding.setOrphanDelete( true );
 		}
 
 		for ( FilterSource filterSource : source.getFilterSources() ) {
 			String condition = filterSource.getCondition();
 			if ( condition == null ) {
 				final FilterDefinition filterDefinition = mappingDocument.getMetadataCollector().getFilterDefinition( filterSource.getName() );
 				if ( filterDefinition != null ) {
 					condition = filterDefinition.getDefaultFilterCondition();
 				}
 			}
 
 			binding.addFilter(
 					filterSource.getName(),
 					condition,
 					filterSource.shouldAutoInjectAliases(),
 					filterSource.getAliasToTableMap(),
 					filterSource.getAliasToEntityMap()
 			);
 		}
 	}
 
 	private void applyCaching(MappingDocument mappingDocument, Caching caching, Collection collection) {
 		if ( caching == null || caching.getRequested() == TruthValue.UNKNOWN ) {
 			// see if JPA's SharedCacheMode indicates we should implicitly apply caching
 			switch ( mappingDocument.getBuildingOptions().getSharedCacheMode() ) {
 				case ALL: {
 					caching = new Caching(
 							null,
 							mappingDocument.getBuildingOptions().getImplicitCacheAccessType(),
 							false,
 							TruthValue.UNKNOWN
 					);
 					break;
 				}
 				case NONE: {
 					// Ideally we'd disable all caching...
 					break;
 				}
 				case ENABLE_SELECTIVE: {
 					// this is default behavior for hbm.xml
 					break;
 				}
 				case DISABLE_SELECTIVE: {
 					// really makes no sense for hbm.xml
 					break;
 				}
 				default: {
 					// null or UNSPECIFIED, nothing to do.  IMO for hbm.xml this is equivalent
 					// to ENABLE_SELECTIVE
 					break;
 				}
 			}
 		}
 
 		if ( caching == null || caching.getRequested() == TruthValue.FALSE ) {
 			return;
 		}
 
 		if ( caching.getAccessType() != null ) {
 			collection.setCacheConcurrencyStrategy( caching.getAccessType().getExternalName() );
 		}
 		else {
 			collection.setCacheConcurrencyStrategy( mappingDocument.getBuildingOptions().getImplicitCacheAccessType().getExternalName() );
 		}
 		collection.setCacheRegionName( caching.getRegion() );
 //		collection.setCachingExplicitlyRequested( caching.getRequested() != TruthValue.UNKNOWN );
 	}
 
 	private Identifier determineTable(
 			MappingDocument sourceDocument,
 			String attributeName,
 			RelationalValueSourceContainer relationalValueSourceContainer) {
 		return determineTable( sourceDocument, attributeName, relationalValueSourceContainer.getRelationalValueSources() );
 	}
 
 	private Identifier determineTable(
 			MappingDocument mappingDocument,
 			SingularAttributeSourceEmbedded embeddedAttributeSource) {
 		Identifier tableName = null;
 		for ( AttributeSource attributeSource : embeddedAttributeSource.getEmbeddableSource().attributeSources() ) {
 			final Identifier determinedName;
 			if ( RelationalValueSourceContainer.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable(
 						mappingDocument,
 						embeddedAttributeSource.getAttributeRole().getFullPath(),
 						(RelationalValueSourceContainer) attributeSource
 
 				);
 			}
 			else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable( mappingDocument, (SingularAttributeSourceEmbedded) attributeSource );
 			}
 			else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable(
 						mappingDocument,
 						attributeSource.getAttributeRole().getFullPath(),
 						( (SingularAttributeSourceAny) attributeSource ).getKeySource().getRelationalValueSources()
 				);
 			}
 			else {
 				continue;
 			}
 
 			if (  EqualsHelper.equals( tableName, determinedName ) ) {
 				continue;
 			}
 
 			if ( tableName != null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Attribute [%s] referenced columns from multiple tables: %s, %s",
 								embeddedAttributeSource.getAttributeRole().getFullPath(),
 								tableName,
 								determinedName
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 
 			tableName = determinedName;
 		}
 
 		return tableName;
 	}
 
 	private Identifier determineTable(
 			MappingDocument mappingDocument,
 			String attributeName,
 			List<RelationalValueSource> relationalValueSources) {
 		String tableName = null;
 		for ( RelationalValueSource relationalValueSource : relationalValueSources ) {
 			// We need to get the containing table name for both columns and formulas,
 			// particularly when a column/formula is for a property on a secondary table.
 			if ( EqualsHelper.equals( tableName, relationalValueSource.getContainingTableName() ) ) {
 				continue;
 			}
 
 			if ( tableName != null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Attribute [%s] referenced columns from multiple tables: %s, %s",
 								attributeName,
 								tableName,
 								relationalValueSource.getContainingTableName()
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 
 			tableName = relationalValueSource.getContainingTableName();
 		}
 
 		return database.toIdentifier( tableName );
 	}
 
 	private void bindSecondaryTable(
 			MappingDocument mappingDocument,
 			SecondaryTableSource secondaryTableSource,
 			Join secondaryTableJoin,
 			final EntityTableXref entityTableXref) {
 		final PersistentClass persistentClass = secondaryTableJoin.getPersistentClass();
 
 		final Identifier catalogName = determineCatalogName( secondaryTableSource.getTableSource() );
 		final Identifier schemaName = determineSchemaName( secondaryTableSource.getTableSource() );
 		final Namespace namespace = database.locateNamespace( catalogName, schemaName );
 
 		Table secondaryTable;
 		final Identifier logicalTableName;
 
 		if ( TableSource.class.isInstance( secondaryTableSource.getTableSource() ) ) {
 			final TableSource tableSource = (TableSource) secondaryTableSource.getTableSource();
 			logicalTableName = database.toIdentifier( tableSource.getExplicitTableName() );
 			secondaryTable = namespace.locateTable( logicalTableName );
 			if ( secondaryTable == null ) {
 				secondaryTable = namespace.createTable( logicalTableName, false );
 			}
 			else {
 				secondaryTable.setAbstract( false );
 			}
 
 			secondaryTable.setComment( tableSource.getComment() );
 		}
 		else {
 			final InLineViewSource inLineViewSource = (InLineViewSource) secondaryTableSource.getTableSource();
 			secondaryTable = new Table(
 					namespace,
 					inLineViewSource.getSelectStatement(),
 					false
 			);
 			logicalTableName = Identifier.toIdentifier( inLineViewSource.getLogicalName() );
 		}
 
 		secondaryTableJoin.setTable( secondaryTable );
 		entityTableXref.addSecondaryTable( mappingDocument, logicalTableName, secondaryTableJoin );
 
 		bindCustomSql(
 				mappingDocument,
 				secondaryTableSource,
 				secondaryTableJoin
 		);
 
 		secondaryTableJoin.setSequentialSelect( secondaryTableSource.getFetchStyle() == FetchStyle.SELECT );
 		secondaryTableJoin.setInverse( secondaryTableSource.isInverse() );
 		secondaryTableJoin.setOptional( secondaryTableSource.isOptional() );
 
 		if ( log.isDebugEnabled() ) {
 			log.debugf(
 					"Mapping entity secondary-table: %s -> %s",
 					persistentClass.getEntityName(),
 					secondaryTable.getName()
 			);
 		}
 
 		final SimpleValue keyBinding = new DependantValue(
 				mappingDocument.getMetadataCollector(),
 				secondaryTable,
 				persistentClass.getIdentifier()
 		);
 		if ( mappingDocument.getBuildingOptions().useNationalizedCharacterData() ) {
 			keyBinding.makeNationalized();
 		}
 		secondaryTableJoin.setKey( keyBinding );
 
 		keyBinding.setCascadeDeleteEnabled( secondaryTableSource.isCascadeDeleteEnabled() );
 
 		// NOTE : no Type info to bind...
 
 		relationalObjectBinder.bindColumns(
 				mappingDocument,
 				secondaryTableSource.getPrimaryKeyColumnSources(),
 				keyBinding,
 				secondaryTableSource.isOptional(),
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					int count = 0;
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						final Column correspondingColumn = entityTableXref.getPrimaryTable().getPrimaryKey().getColumn( count++ );
 						return database.toIdentifier( correspondingColumn.getQuotedName() );
 					}
 				}
 		);
 
 		keyBinding.setForeignKeyName( secondaryTableSource.getExplicitForeignKeyName() );
 
 		secondaryTableJoin.createPrimaryKey();
 		secondaryTableJoin.createForeignKey();
 	}
 
 	private Property createEmbeddedAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceEmbedded embeddedSource,
 			Component componentBinding,
 			String containingClassName) {
 		final String attributeName = embeddedSource.getName();
 
 		bindComponent(
 				sourceDocument,
 				embeddedSource.getEmbeddableSource(),
 				componentBinding,
 				containingClassName,
 				attributeName,
 				embeddedSource.getXmlNodeName(),
 				embeddedSource.isVirtualAttribute()
 		);
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				componentBinding,
 				componentBinding.getComponentClassName(),
 				attributeName,
 				embeddedSource.getAttributeRole()
 		);
 
 		componentBinding.createForeignKey();
 
 		final Property attribute;
 		if ( embeddedSource.isVirtualAttribute() ) {
 			attribute = new SyntheticProperty() {
 				@Override
 				public String getPropertyAccessorName() {
 					return "embedded";
 				}
 			};
 		}
 		else {
 			attribute = new Property();
 		}
 		attribute.setValue( componentBinding );
 		bindProperty(
 				sourceDocument,
 				embeddedSource,
 				attribute
 		);
 
 		if ( StringHelper.isNotEmpty( embeddedSource.getXmlNodeName() ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
 		}
 
 		return attribute;
 	}
 
 	private Property createBasicAttribute(
 			MappingDocument sourceDocument,
 			final SingularAttributeSourceBasic attributeSource,
 			SimpleValue value,
 			String containingClassName) {
 		final String attributeName = attributeSource.getName();
 
 		bindSimpleValueType(
 				sourceDocument,
 				attributeSource.getTypeInformation(),
 				value
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				attributeSource.getRelationalValueSources(),
 				value,
 				attributeSource.areValuesNullableByDefault(),
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineBasicColumnName( attributeSource );
 					}
 				}
 		);
 
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				value,
 				containingClassName,
 				attributeName,
 				attributeSource.getAttributeRole()
 		);
 
 //		// this is done here 'cos we might only know the type here (ugly!)
 //		// TODO: improve this a lot:
 //		if ( value instanceof ToOne ) {
 //			ToOne toOne = (ToOne) value;
 //			String propertyRef = toOne.getReferencedEntityAttributeName();
 //			if ( propertyRef != null ) {
 //				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 //			}
 //			toOne.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
 //		}
 //		else if ( value instanceof Collection ) {
 //			Collection coll = (Collection) value;
 //			String propertyRef = coll.getReferencedEntityAttributeName();
 //			// not necessarily a *unique* property reference
 //			if ( propertyRef != null ) {
 //				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 //			}
 //		}
 
 		value.createForeignKey();
 
 		Property property = new Property();
 		property.setValue( value );
 		bindProperty(
 				sourceDocument,
 				attributeSource,
 				property
 		);
 
 		return property;
 	}
 
 	private Property createOneToOneAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceOneToOne oneToOneSource,
 			OneToOne oneToOneBinding,
 			String containingClassName) {
 		bindOneToOne( sourceDocument, oneToOneSource, oneToOneBinding );
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				oneToOneBinding,
 				containingClassName,
 				oneToOneSource.getName(),
 				oneToOneSource.getAttributeRole()
 		);
 
 		final String propertyRef = oneToOneBinding.getReferencedPropertyName();
 		if ( propertyRef != null ) {
 			handlePropertyReference(
 					sourceDocument,
 					oneToOneBinding.getReferencedEntityName(),
 					propertyRef,
 					true,
 					"<one-to-one name=\"" + oneToOneSource.getName() + "\"/>"
 			);
 		}
 
 		oneToOneBinding.createForeignKey();
 
 		Property prop = new Property();
 		prop.setValue( oneToOneBinding );
 		bindProperty(
 				sourceDocument,
 				oneToOneSource,
 				prop
 		);
 
 		return prop;
 	}
 
 	private void handlePropertyReference(
 			MappingDocument mappingDocument,
 			String referencedEntityName,
 			String referencedPropertyName,
 			boolean isUnique,
 			String sourceElementSynopsis) {
 		PersistentClass entityBinding = mappingDocument.getMetadataCollector().getEntityBinding( referencedEntityName );
 		if ( entityBinding == null ) {
 			// entity may just not have been processed yet - set up a delayed handler
 			registerDelayedPropertyReferenceHandler(
 					new DelayedPropertyReferenceHandlerImpl(
 							referencedEntityName,
 							referencedPropertyName,
 							isUnique,
 							sourceElementSynopsis,
 							mappingDocument.getOrigin()
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 7d8af79e6e..e01b689a08 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,555 +1,561 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.io.Serializable;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.Nationalized;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.CharacterArrayNClobType;
 import org.hibernate.type.CharacterNCharType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.PrimitiveCharacterArrayNClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.StringNVarcharType;
 import org.hibernate.type.WrappedMaterializedBlobType;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private MetadataBuildingContext buildingContext;
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private String defaultType = "";
 	private Properties typeParameters = new Properties();
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 	private XProperty xproperty;
 	private AccessType accessType;
 
 	private AttributeConverterDescriptor attributeConverterDescriptor;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
+		if ( isVersion && simpleValue != null ) {
+			simpleValue.makeVersion();
+		}
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 
 		if ( defaultType.length() == 0 ) {
 			defaultType = returnedClassName;
 		}
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
 
 	public void setType(XProperty property, XClass returnedClass, String declaringClassName, AttributeConverterDescriptor attributeConverterDescriptor) {
 		if ( returnedClass == null ) {
 			// we cannot guess anything
 			return;
 		}
 
 		XClass returnedClassOrElement = returnedClass;
 		boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		this.xproperty = property;
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 		isNationalized = property.isAnnotationPresent( Nationalized.class )
 				|| buildingContext.getBuildingOptions().useNationalizedCharacterData();
 
 		Type annType = null;
 		if ( (!key && property.isAnnotationPresent( Type.class ))
 				|| (key && property.isAnnotationPresent( MapKeyType.class )) ) {
 			if ( key ) {
 				MapKeyType ann = property.getAnnotation( MapKeyType.class );
 				annType = ann.value();
 			}
 			else {
 				annType = property.getAnnotation( Type.class );
 			}
 		}
 
 		if ( annType != null ) {
 			setExplicitType( annType );
 			type = explicitType;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
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
 			explicitType = type;
 		}
 		else if ( !key && property.isAnnotationPresent( Lob.class ) ) {
 			isLob = true;
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.NCLOB.getName()
 						: StandardBasicTypes.CLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.NClob.class ) ) {
 				type = StandardBasicTypes.NCLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.MATERIALIZED_NCLOB.getName()
 						: StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = isNationalized
 						? CharacterArrayNClobType.class.getName()
 						: CharacterArrayClobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = isNationalized
 						? PrimitiveCharacterArrayNClobType.class.getName()
 						: PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 			defaultType = type;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Enumerated.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) ) {
 			final Class attributeJavaType = buildingContext.getBuildingOptions().getReflectionManager().toClass( returnedClassOrElement );
 			if ( !Enum.class.isAssignableFrom( attributeJavaType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"Attribute [%s.%s] was annotated as enumerated, but its java type is not an enum [%s]",
 								declaringClassName,
 								xproperty.getName(),
 								attributeJavaType.getName()
 						)
 				);
 			}
 			type = EnumType.class.getName();
 			explicitType = type;
 		}
 		else if ( isNationalized ) {
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				// nvarchar
 				type = StringNVarcharType.INSTANCE.getName();
 				explicitType = type;
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Character.class ) ) {
 				if ( isArray ) {
 					// nvarchar
 					type = StringNVarcharType.INSTANCE.getName();
 				}
 				else {
 					// nchar
 					type = CharacterNCharType.INSTANCE.getName();
 				}
 				explicitType = type;
 			}
 		}
 
 		// implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set beforeQuery SimpleValueBinder.setType" );
 		}
 
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 			}
 		}
 
 		defaultType = BinderHelper.isEmptyAnnotationValue( type ) ? returnedClassName : type;
 		this.typeParameters = typeParameters;
 
 		applyAttributeConverter( property, attributeConverterDescriptor );
 	}
 
 	private void applyAttributeConverter(XProperty property, AttributeConverterDescriptor attributeConverterDescriptor) {
 		if ( attributeConverterDescriptor == null ) {
 			return;
 		}
 
 		LOG.debugf( "Starting applyAttributeConverter [%s:%s]", persistentClassName, property.getName() );
 
 		if ( property.isAnnotationPresent( Id.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Id attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( isVersion ) {
 			LOG.debugf( "Skipping AttributeConverter checks for version attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( !key && property.isAnnotationPresent( Temporal.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Temporal attribute [%s]", property.getName() );
 			return;
 		}
 		if ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for map-key annotated as MapKeyTemporal [%s]", property.getName() );
 			return;
 		}
 
 		if ( !key && property.isAnnotationPresent( Enumerated.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Enumerated attribute [%s]", property.getName() );
 			return;
 		}
 		if ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for map-key annotated as MapKeyEnumerated [%s]", property.getName() );
 			return;
 		}
 
 		if ( isAssociation() ) {
 			LOG.debugf( "Skipping AttributeConverter checks for association attribute [%s]", property.getName() );
 			return;
 		}
 
 		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
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
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
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
 		simpleValue = new SimpleValue( buildingContext.getMetadataCollector(), table );
+		if ( isVersion ) {
+			simpleValue.makeVersion();
+		}
 		if ( isNationalized ) {
 			simpleValue.makeNationalized();
 		}
 		if ( isLob ) {
 			simpleValue.makeLob();
 		}
 
 		linkWithValue();
 
 		boolean isInSecondPass = buildingContext.getMetadataCollector().isInSecondPass();
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			buildingContext.getMetadataCollector().addSecondPass( new SetSimpleValueTypeSecondPass( this ) );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !buildingContext.getMetadataCollector().isInSecondPass() && referencedEntityName != null ) {
 			buildingContext.getMetadataCollector().addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, (Ejb3JoinColumn[]) columns, simpleValue
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
 		LOG.debugf( "Starting fillSimpleValue for %s", propertyName );
                 
 		if ( attributeConverterDescriptor != null ) {
 			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
 										"remove @Type or specify @Convert(disableConversion = true)",
 								persistentClassName,
 								propertyName
 						)
 				);
 			}
 			LOG.debugf(
 					"Applying JPA AttributeConverter [%s] to [%s:%s]",
 					attributeConverterDescriptor,
 					persistentClassName,
 					propertyName
 			);
 			simpleValue.setJpaAttributeConverterDescriptor( attributeConverterDescriptor );
 		}
 		else {
 			String type;
 			TypeDefinition typeDef;
 
 			if ( !BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				type = explicitType;
 				typeDef = buildingContext.getMetadataCollector().getTypeDefinition( type );
 			}
 			else {
 				// try implicit type
 				TypeDefinition implicitTypeDef = buildingContext.getMetadataCollector().getTypeDefinition( returnedClassName );
 				if ( implicitTypeDef != null ) {
 					typeDef = implicitTypeDef;
 					type = returnedClassName;
 				}
 				else {
 					typeDef = buildingContext.getMetadataCollector().getTypeDefinition( defaultType );
 					type = defaultType;
 				}
 			}
 
 			if ( typeDef != null ) {
 				type = typeDef.getTypeImplementorClass().getName();
 				simpleValue.setTypeParameters( typeDef.getParametersAsProperties() );
 			}
 			if ( typeParameters != null && typeParameters.size() != 0 ) {
 				//explicit type params takes precedence over type def params
 				simpleValue.setTypeParameters( typeParameters );
 			}
 			simpleValue.setTypeName( type );
 		}
 
 		if ( persistentClassName != null || attributeConverterDescriptor != null ) {
 			try {
 				simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 			}
 			catch (Exception e) {
 				throw new MappingException(
 						String.format(
 								Locale.ROOT,
 								"Unable to determine basic type mapping via reflection [%s -> %s]",
 								persistentClassName,
 								propertyName
 						),
 						e
 				);
 			}
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 		
 		if ( simpleValue.getTypeName() != null && simpleValue.getTypeName().length() > 0
 				&& simpleValue.getMetadata().getTypeResolver().basic( simpleValue.getTypeName() ) == null ) {
 			try {
 				Class typeClass = buildingContext.getClassLoaderAccess().classForName( simpleValue.getTypeName() );
 
 				if ( typeClass != null && DynamicParameterizedType.class.isAssignableFrom( typeClass ) ) {
 					Properties parameters = simpleValue.getTypeParameters();
 					if ( parameters == null ) {
 						parameters = new Properties();
 					}
 					parameters.put( DynamicParameterizedType.IS_DYNAMIC, Boolean.toString( true ) );
 					parameters.put( DynamicParameterizedType.RETURNED_CLASS, returnedClassName );
 					parameters.put( DynamicParameterizedType.IS_PRIMARY_KEY, Boolean.toString( key ) );
 
 					parameters.put( DynamicParameterizedType.ENTITY, persistentClassName );
 					parameters.put( DynamicParameterizedType.XPROPERTY, xproperty );
 					parameters.put( DynamicParameterizedType.PROPERTY, xproperty.getName() );
 					parameters.put( DynamicParameterizedType.ACCESS_TYPE, accessType.getType() );
 					simpleValue.setTypeParameters( parameters );
 				}
 			}
 			catch (ClassLoadingException e) {
 				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), e );
 			}
 		}
 
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 
 	public AccessType getAccessType() {
 		return accessType;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/compare/RowVersionComparator.java b/hibernate-core/src/main/java/org/hibernate/internal/util/compare/RowVersionComparator.java
new file mode 100644
index 0000000000..a48d0d4969
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/compare/RowVersionComparator.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.internal.util.compare;
+
+import java.util.Comparator;
+
+/**
+ * @author Gail Badner
+ */
+public final class RowVersionComparator implements Comparator<byte[]> {
+	public static RowVersionComparator INSTANCE = new RowVersionComparator();
+
+	private RowVersionComparator() {
+	}
+
+	@Override
+	@SuppressWarnings({ "unchecked" })
+	public int compare(byte[] o1, byte[] o2) {
+		final int lengthToCheck = Math.min( o1.length, o2.length );
+
+		for ( int i = 0 ; i < lengthToCheck ; i++ ) {
+			// must do an unsigned int comparison
+			final int comparison = ComparableComparator.INSTANCE.compare(
+						Byte.toUnsignedInt( o1[i] ),
+						Byte.toUnsignedInt( o2[i] )
+			);
+			if ( comparison != 0 ) {
+				return comparison;
+			}
+		}
+		return o1.length - o2.length;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 6da536e8b0..3411cfa703 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,708 +1,724 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.type.BinaryType;
+import org.hibernate.type.RowVersionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.LobTypeMappings;
 import org.hibernate.type.descriptor.sql.NationalizedTypeMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final MetadataImplementor metadata;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties typeParameters;
+	private boolean isVersion;
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private String foreignKeyDefinition;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDescriptor attributeConverterDescriptor;
 	private Type type;
 
 	public SimpleValue(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	public SimpleValue(MetadataImplementor metadata, Table table) {
 		this( metadata );
 		this.table = table;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	@Override
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) {
 			columns.add(column);
 		}
 		column.setValue(this);
 		column.setTypeIndex( columns.size() - 1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add( formula );
 	}
 
 	@Override
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	@Override
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 
 	public List getConstraintColumns() {
 		return columns;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		if ( typeName != null && typeName.startsWith( AttributeConverterTypeAdapter.NAME_PREFIX ) ) {
 			final String converterClassName = typeName.substring( AttributeConverterTypeAdapter.NAME_PREFIX.length() );
 			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			try {
 				final Class<AttributeConverter> converterClass = cls.classForName( converterClassName );
 				attributeConverterDescriptor = new AttributeConverterDescriptorNonAutoApplicableImpl( converterClass.newInstance() );
 				return;
 			}
 			catch (Exception e) {
 				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
 			}
 		}
 
 		this.typeName = typeName;
 	}
 
+	public void makeVersion() {
+		this.isVersion = true;
+	}
+
+	public boolean isVersion() {
+		return isVersion;
+	}
 	public void makeNationalized() {
 		this.isNationalized = true;
 	}
 
 	public boolean isNationalized() {
 		return isNationalized;
 	}
 
 	public void makeLob() {
 		this.isLob = true;
 	}
 
 	public boolean isLob() {
 		return isLob;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	@Override
 	public void createForeignKey() throws MappingException {}
 
 	@Override
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName, getForeignKeyDefinition() );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	private IdentifierGenerator identifierGenerator;
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 
 		if ( identifierGenerator != null ) {
 			return identifierGenerator;
 		}
 
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
 				if ( iter.hasNext() ) {
 					tables.append(", ");
 				}
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
 		final ConfigurationService cs = metadata.getMetadataBuildingOptions().getServiceRegistry()
 				.getService( ConfigurationService.class );
 
 		params.put(
 				AvailableSettings.PREFER_POOLED_VALUES_LO,
 				cs.getSetting( AvailableSettings.PREFER_POOLED_VALUES_LO, StandardConverters.BOOLEAN, false )
 		);
 		if ( cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER ) != null ) {
 			params.put(
 					AvailableSettings.PREFERRED_POOLED_OPTIMIZER,
 					cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER )
 			);
 		}
 
 		identifierGeneratorFactory.setDialect( dialect );
 		identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 
 		return identifierGenerator;
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
 		return IdentityGenerator.class.isAssignableFrom(identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy ));
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
 	
 	public String getForeignKeyDefinition() {
 		return foreignKeyDefinition;
 	}
 
 	public void setForeignKeyDefinition(String foreignKeyDefinition) {
 		this.foreignKeyDefinition = foreignKeyDefinition;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Object selectable = itr.next();
 			if ( selectable instanceof Formula ) {
 				// if there are *any* formulas, then the Value overall is
 				// considered nullable
 				return true;
 			}
 			else if ( !( (Column) selectable ).isNullable() ) {
 				// if there is a single non-nullable column, the Value
 				// overall is considered non-nullable.
 				return false;
 			}
 		}
 		// nullable by default
 		return true;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = metadata.getTypeResolver().heuristicType( typeName, typeParameters );
+		// if this is a byte[] version/timestamp, then we need to use RowVersionType
+		// instead of BinaryType (HHH-10413)
+		if ( isVersion && BinaryType.class.isInstance( result ) ) {
+			log.debug( "version is BinaryType; changing to RowVersionType" );
+			result = RowVersionType.INSTANCE;
+		}
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( attributeConverterDescriptor == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "Attribute types for a dynamic entity must be explicitly specified: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName, metadata.getMetadataBuildingOptions().getServiceRegistry().getService( ClassLoaderService.class ) ).getName();
 			// todo : to fully support isNationalized here we need do the process hinted at above
 			// 		essentially, much of the logic from #buildAttributeConverterTypeAdapter wrt resolving
 			//		a (1) SqlTypeDescriptor, a (2) JavaTypeDescriptor and dynamically building a BasicType
 			// 		combining them.
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
 		final Class entityAttributeJavaType = attributeConverterDescriptor.getDomainType();
 		final Class databaseColumnJavaType = attributeConverterDescriptor.getJdbcType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		if ( isLob() ) {
 			if ( LobTypeMappings.INSTANCE.hasCorrespondingLobCode( jdbcTypeCode ) ) {
 				jdbcTypeCode = LobTypeMappings.INSTANCE.getCorrespondingLobCode( jdbcTypeCode );
 			}
 			else {
 				if ( Serializable.class.isAssignableFrom( entityAttributeJavaType ) ) {
 					jdbcTypeCode = Types.BLOB;
 				}
 				else {
 					throw new IllegalArgumentException(
 							String.format(
 									Locale.ROOT,
 									"JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent, and Java type is not Serializable (to use BLOB)",
 									jdbcTypeCode,
 									JdbcTypeNameMapper.getTypeName( jdbcTypeCode )
 							)
 					);
 				}
 			}
 		}
 		if ( isNationalized() ) {
 			jdbcTypeCode = NationalizedTypeMappings.INSTANCE.getCorrespondingNationalizedCode( jdbcTypeCode );
 		}
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDescriptor.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaType,
 				databaseColumnJavaType,
 				entityAttributeJavaTypeDescriptor
 		);
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
 
 	public void copyTypeFrom( SimpleValue sourceValue ) {
 		setTypeName( sourceValue.getTypeName() );
 		setTypeParameters( sourceValue.getTypeParameters() );
 
 		type = sourceValue.type;
 		attributeConverterDescriptor = sourceValue.attributeConverterDescriptor;
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
 
 	public void setJpaAttributeConverterDescriptor(AttributeConverterDescriptor attributeConverterDescriptor) {
 		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				Selectable column = columns.get(i);
 				if (column instanceof Column){
 					columnsNames[i] = ((Column) column).getName();
 				}
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							classLoaderService.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, e );
 		}
 	}
 
 	private static final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
index 3496f35c6c..72f1a6fe01 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
@@ -1,167 +1,168 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 /**
  * A registry of {@link BasicType} instances
  *
  * @author Steve Ebersole
  */
 public class BasicTypeRegistry implements Serializable {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( BasicTypeRegistry.class );
 
 	// TODO : analyze these sizing params; unfortunately this seems to be the only way to give a "concurrencyLevel"
 	private Map<String, BasicType> registry = new ConcurrentHashMap<String, BasicType>( 100, .75f, 1 );
 	private boolean locked;
 
 	public BasicTypeRegistry() {
 		register( BooleanType.INSTANCE );
 		register( NumericBooleanType.INSTANCE );
 		register( TrueFalseType.INSTANCE );
 		register( YesNoType.INSTANCE );
 
 		register( ByteType.INSTANCE );
 		register( CharacterType.INSTANCE );
 		register( ShortType.INSTANCE );
 		register( IntegerType.INSTANCE );
 		register( LongType.INSTANCE );
 		register( FloatType.INSTANCE );
 		register( DoubleType.INSTANCE );
 		register( BigDecimalType.INSTANCE );
 		register( BigIntegerType.INSTANCE );
 
 		register( StringType.INSTANCE );
 		register( StringNVarcharType.INSTANCE );
 		register( CharacterNCharType.INSTANCE );
 		register( UrlType.INSTANCE );
 
 		register( DurationType.INSTANCE );
 		register( InstantType.INSTANCE );
 		register( LocalDateTimeType.INSTANCE );
 		register( LocalDateType.INSTANCE );
 		register( LocalTimeType.INSTANCE );
 		register( OffsetDateTimeType.INSTANCE );
 		register( OffsetTimeType.INSTANCE );
 		register( ZonedDateTimeType.INSTANCE );
 
 		register( DateType.INSTANCE );
 		register( TimeType.INSTANCE );
 		register( TimestampType.INSTANCE );
 		register( DbTimestampType.INSTANCE );
 		register( CalendarType.INSTANCE );
 		register( CalendarDateType.INSTANCE );
 
 		register( LocaleType.INSTANCE );
 		register( CurrencyType.INSTANCE );
 		register( TimeZoneType.INSTANCE );
 		register( ClassType.INSTANCE );
 		register( UUIDBinaryType.INSTANCE );
 		register( UUIDCharType.INSTANCE );
 
 		register( BinaryType.INSTANCE );
 		register( WrapperBinaryType.INSTANCE );
+		register( RowVersionType.INSTANCE );
 		register( ImageType.INSTANCE );
 		register( CharArrayType.INSTANCE );
 		register( CharacterArrayType.INSTANCE );
 		register( TextType.INSTANCE );
 		register( NTextType.INSTANCE );
 		register( BlobType.INSTANCE );
 		register( MaterializedBlobType.INSTANCE );
 		register( ClobType.INSTANCE );
 		register( NClobType.INSTANCE );
 		register( MaterializedClobType.INSTANCE );
 		register( MaterializedNClobType.INSTANCE );
 		register( SerializableType.INSTANCE );
 
 		register( ObjectType.INSTANCE );
 
 		//noinspection unchecked
 		register( new AdaptedImmutableType( DateType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( TimeType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( TimestampType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( DbTimestampType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( CalendarType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( CalendarDateType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( BinaryType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( SerializableType.INSTANCE ) );
 	}
 
 	/**
 	 * Constructor version used during shallow copy
 	 *
 	 * @param registeredTypes The type map to copy over
 	 */
 	@SuppressWarnings({"UnusedDeclaration"})
 	private BasicTypeRegistry(Map<String, BasicType> registeredTypes) {
 		registry.putAll( registeredTypes );
 		locked = true;
 	}
 
 	public void register(BasicType type) {
 		register( type, type.getRegistrationKeys() );
 	}
 
 	public void register(BasicType type, String[] keys) {
 		if ( locked ) {
 			throw new HibernateException( "Can not alter TypeRegistry at this time" );
 		}
 
 		if ( type == null ) {
 			throw new HibernateException( "Type to register cannot be null" );
 		}
 
 		if ( keys == null || keys.length == 0 ) {
 			LOG.typeDefinedNoRegistrationKeys( type );
 			return;
 		}
 
 		for ( String key : keys ) {
 			// be safe...
 			if ( key == null ) {
 				continue;
 			}
 			LOG.debugf( "Adding type registration %s -> %s", key, type );
 			final Type old = registry.put( key, type );
 			if ( old != null && old != type ) {
 				LOG.typeRegistrationOverridesPrevious( key, old );
 			}
 		}
 	}
 
 	public void register(UserType type, String[] keys) {
 		register( new CustomType( type, keys ) );
 	}
 
 	public void register(CompositeUserType type, String[] keys) {
 		register( new CompositeCustomType( type, keys ) );
 	}
 
 	public BasicType getRegisteredType(String key) {
 		return registry.get( key );
 	}
 
 	public BasicTypeRegistry shallowCopy() {
 		return new BasicTypeRegistry( this.registry );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java b/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
index 22d1c8794e..2d06e44420 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
@@ -1,58 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.util.Comparator;
 
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
 import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;
 
 /**
  * A type that maps between a {@link java.sql.Types#VARBINARY VARBINARY} and {@code byte[]}
  *
+ * Implementation of the {@link VersionType} interface should be considered deprecated.
+ * For binary entity versions/timestamps, {@link RowVersionType} should be used instead.
+ *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BinaryType
 		extends AbstractSingleColumnStandardBasicType<byte[]>
 		implements VersionType<byte[]> {
 
 	public static final BinaryType INSTANCE = new BinaryType();
 
 	public String getName() {
 		return "binary";
 	}
 
 	public BinaryType() {
 		super( VarbinaryTypeDescriptor.INSTANCE, PrimitiveByteArrayTypeDescriptor.INSTANCE );
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName(), "byte[]", byte[].class.getName() };
 	}
 
+	/**
+	 * Generate an initial version.
+	 *
+	 * @param session The session from which this request originates.
+	 * @return an instance of the type
+	 * @deprecated use {@link RowVersionType} for binary entity versions/timestamps
+	 */
 	@Override
+	@Deprecated
 	public byte[] seed(SharedSessionContractImplementor session) {
 		// Note : simply returns null for seed() and next() as the only known
 		// 		application of binary types for versioning is for use with the
 		// 		TIMESTAMP datatype supported by Sybase and SQL Server, which
 		// 		are completely db-generated values...
 		return null;
 	}
 
+	/**
+	 * Increment the version.
+	 *
+	 * @param session The session from which this request originates.
+	 * @param current the current version
+	 * @return an instance of the type
+	 * @deprecated use {@link RowVersionType} for binary entity versions/timestamps
+	 */
 	@Override
+	@Deprecated
 	public byte[] next(byte[] current, SharedSessionContractImplementor session) {
 		return current;
 	}
 
+	/**
+	 * Get a comparator for version values.
+	 *
+	 * @return The comparator to use to compare different version values.
+	 * @deprecated use {@link RowVersionType} for binary entity versions/timestamps
+	 */
 	@Override
+	@Deprecated
 	public Comparator<byte[]> getComparator() {
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.getComparator();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/RowVersionType.java b/hibernate-core/src/main/java/org/hibernate/type/RowVersionType.java
new file mode 100644
index 0000000000..c91ed0e3b3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/RowVersionType.java
@@ -0,0 +1,62 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.type;
+
+import java.util.Comparator;
+
+import org.hibernate.engine.spi.SharedSessionContractImplementor;
+import org.hibernate.internal.util.compare.RowVersionComparator;
+import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
+import org.hibernate.type.descriptor.java.RowVersionTypeDescriptor;
+import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;
+
+/**
+ * A type that maps between a {@link java.sql.Types#VARBINARY VARBINARY} and {@code byte[]}
+ * specifically for entity versions/timestamps.
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ * @author Gail Badner
+ */
+public class RowVersionType
+		extends AbstractSingleColumnStandardBasicType<byte[]>
+		implements VersionType<byte[]> {
+
+	public static final RowVersionType INSTANCE = new RowVersionType();
+
+	public String getName() {
+		return "row_version";
+	}
+
+	public RowVersionType() {
+		super( VarbinaryTypeDescriptor.INSTANCE, RowVersionTypeDescriptor.INSTANCE );
+	}
+
+	@Override
+	public String[] getRegistrationKeys() {
+		return new String[] { getName() };
+	}
+
+	@Override
+	public byte[] seed(SharedSessionContractImplementor session) {
+		// Note : simply returns null for seed() and next() as the only known
+		// 		application of binary types for versioning is for use with the
+		// 		TIMESTAMP datatype supported by Sybase and SQL Server, which
+		// 		are completely db-generated values...
+		return null;
+	}
+
+	@Override
+	public byte[] next(byte[] current, SharedSessionContractImplementor session) {
+		return current;
+	}
+
+	@Override
+	public Comparator<byte[]> getComparator() {
+		return getJavaTypeDescriptor().getComparator();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
index a0e0f12b59..5a798efbf6 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
@@ -1,333 +1,341 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Centralizes access to the standard set of basic {@link Type types}.
  * <p/>
  * Type mappings can be adjusted per {@link org.hibernate.SessionFactory}.  These adjusted mappings can be accessed
  * from the {@link org.hibernate.TypeHelper} instance obtained via {@link org.hibernate.SessionFactory#getTypeHelper()}
  *
  * @see BasicTypeRegistry
  * @see org.hibernate.TypeHelper
  * @see org.hibernate.SessionFactory#getTypeHelper()
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public final class StandardBasicTypes {
 	private StandardBasicTypes() {
 	}
 
 	private static final Set<SqlTypeDescriptor> SQL_TYPE_DESCRIPTORS = new HashSet<SqlTypeDescriptor>();
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#BIT BIT}.
 	 *
 	 * @see BooleanType
 	 */
 	public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#INTEGER INTEGER}.
 	 *
 	 * @see NumericBooleanType
 	 */
 	public static final NumericBooleanType NUMERIC_BOOLEAN = NumericBooleanType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#CHAR CHAR(1)} (using 'T'/'F').
 	 *
 	 * @see TrueFalseType
 	 */
 	public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#CHAR CHAR(1)} (using 'Y'/'N').
 	 *
 	 * @see YesNoType
 	 */
 	public static final YesNoType YES_NO = YesNoType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Byte} to JDBC {@link java.sql.Types#TINYINT TINYINT}.
 	 */
 	public static final ByteType BYTE = ByteType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Short} to JDBC {@link java.sql.Types#SMALLINT SMALLINT}.
 	 *
 	 * @see ShortType
 	 */
 	public static final ShortType SHORT = ShortType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Integer} to JDBC {@link java.sql.Types#INTEGER INTEGER}.
 	 *
 	 * @see IntegerType
 	 */
 	public static final IntegerType INTEGER = IntegerType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Long} to JDBC {@link java.sql.Types#BIGINT BIGINT}.
 	 *
 	 * @see LongType
 	 */
 	public static final LongType LONG = LongType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Float} to JDBC {@link java.sql.Types#FLOAT FLOAT}.
 	 *
 	 * @see FloatType
 	 */
 	public static final FloatType FLOAT = FloatType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Double} to JDBC {@link java.sql.Types#DOUBLE DOUBLE}.
 	 *
 	 * @see DoubleType
 	 */
 	public static final DoubleType DOUBLE = DoubleType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.math.BigInteger} to JDBC {@link java.sql.Types#NUMERIC NUMERIC}.
 	 *
 	 * @see BigIntegerType
 	 */
 	public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.math.BigDecimal} to JDBC {@link java.sql.Types#NUMERIC NUMERIC}.
 	 *
 	 * @see BigDecimalType
 	 */
 	public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Character} to JDBC {@link java.sql.Types#CHAR CHAR(1)}.
 	 *
 	 * @see CharacterType
 	 */
 	public static final CharacterType CHARACTER = CharacterType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see StringType
 	 */
 	public static final StringType STRING = StringType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.net.URL} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see UrlType
 	 */
 	public static final UrlType URL = UrlType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Time}) to JDBC
 	 * {@link java.sql.Types#TIME TIME}.
 	 *
 	 * @see TimeType
 	 */
 	public static final TimeType TIME = TimeType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Date}) to JDBC
 	 * {@link java.sql.Types#DATE DATE}.
 	 *
 	 * @see TimeType
 	 */
 	public static final DateType DATE = DateType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Timestamp}) to JDBC
 	 * {@link java.sql.Types#TIMESTAMP TIMESTAMP}.
 	 *
 	 * @see TimeType
 	 */
 	public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Calendar} to JDBC
 	 * {@link java.sql.Types#TIMESTAMP TIMESTAMP}.
 	 *
 	 * @see CalendarType
 	 */
 	public static final CalendarType CALENDAR = CalendarType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Calendar} to JDBC
 	 * {@link java.sql.Types#DATE DATE}.
 	 *
 	 * @see CalendarDateType
 	 */
 	public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Class} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see ClassType
 	 */
 	public static final ClassType CLASS = ClassType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Locale} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see LocaleType
 	 */
 	public static final LocaleType LOCALE = LocaleType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Currency} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CurrencyType
 	 */
 	public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.TimeZone} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see TimeZoneType
 	 */
 	public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.UUID} to JDBC {@link java.sql.Types#BINARY BINARY}.
 	 *
 	 * @see UUIDBinaryType
 	 */
 	public static final UUIDBinaryType UUID_BINARY = UUIDBinaryType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.UUID} to JDBC {@link java.sql.Types#CHAR CHAR}.
 	 *
 	 * @see UUIDCharType
 	 */
 	public static final UUIDCharType UUID_CHAR = UUIDCharType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 *
 	 * @see BinaryType
 	 */
 	public static final BinaryType BINARY = BinaryType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Byte Byte[]} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 *
 	 * @see WrapperBinaryType
 	 */
 	public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
 
 	/**
+	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#VARBINARY VARBINARY},
+	 * specifically for entity versions/timestamps.
+	 *
+	 * @see RowVersionType
+	 */
+	public static final RowVersionType ROW_VERSION = RowVersionType.INSTANCE;
+
+	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#LONGVARBINARY LONGVARBINARY}.
 	 *
 	 * @see ImageType
 	 * @see #MATERIALIZED_BLOB
 	 */
 	public static final ImageType IMAGE = ImageType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.sql.Blob} to JDBC {@link java.sql.Types#BLOB BLOB}.
 	 *
 	 * @see BlobType
 	 * @see #MATERIALIZED_BLOB
 	 */
 	public static final BlobType BLOB = BlobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#BLOB BLOB}.
 	 *
 	 * @see MaterializedBlobType
 	 * @see #MATERIALIZED_BLOB
 	 * @see #IMAGE
 	 */
 	public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code char[]} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CharArrayType
 	 */
 	public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Character Character[]} to JDBC
 	 * {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CharacterArrayType
 	 */
 	public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#LONGVARCHAR LONGVARCHAR}.
 	 * <p/>
 	 * Similar to a {@link #MATERIALIZED_CLOB}
 	 *
 	 * @see TextType
 	 */
 	public static final TextType TEXT = TextType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#LONGNVARCHAR LONGNVARCHAR}.
 	 * <p/>
 	 * Similar to a {@link #MATERIALIZED_NCLOB}
 	 *
 	 * @see NTextType
 	 */
 	public static final NTextType NTEXT = NTextType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.sql.Clob} to JDBC {@link java.sql.Types#CLOB CLOB}.
 	 *
 	 * @see ClobType
 	 * @see #MATERIALIZED_CLOB
 	 */
 	public static final ClobType CLOB = ClobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.sql.NClob} to JDBC {@link java.sql.Types#NCLOB NCLOB}.
 	 *
 	 * @see NClobType
 	 * @see #MATERIALIZED_NCLOB
 	 */
 	public static final NClobType NCLOB = NClobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#CLOB CLOB}.
 	 *
 	 * @see MaterializedClobType
 	 * @see #MATERIALIZED_CLOB
 	 * @see #TEXT
 	 */
 	public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#NCLOB NCLOB}.
 	 *
 	 * @see MaterializedNClobType
 	 * @see #MATERIALIZED_CLOB
 	 * @see #NTEXT
 	 */
 	public static final MaterializedNClobType MATERIALIZED_NCLOB = MaterializedNClobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.io.Serializable} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 * <p/>
 	 * See especially the discussion wrt {@link ClassLoader} determination on {@link SerializableType}
 	 *
 	 * @see SerializableType
 	 */
 	public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/RowVersionTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/RowVersionTypeDescriptor.java
new file mode 100644
index 0000000000..08cb0f049e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/RowVersionTypeDescriptor.java
@@ -0,0 +1,131 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.type.descriptor.java;
+
+import java.io.ByteArrayInputStream;
+import java.io.InputStream;
+import java.sql.Blob;
+import java.sql.SQLException;
+import java.util.Arrays;
+import java.util.Comparator;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.BinaryStream;
+import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
+import org.hibernate.internal.util.compare.RowVersionComparator;
+import org.hibernate.type.descriptor.WrapperOptions;
+
+/**
+ * Descriptor for {@code byte[]} handling specifically used for specifically for entity versions/timestamps.
+ *
+ * @author Steve Ebersole
+ * @author Gail Badner
+ */
+public class RowVersionTypeDescriptor extends AbstractTypeDescriptor<byte[]> {
+	public static final RowVersionTypeDescriptor INSTANCE = new RowVersionTypeDescriptor();
+
+	@SuppressWarnings({ "unchecked" })
+	public RowVersionTypeDescriptor() {
+		super( byte[].class, ArrayMutabilityPlan.INSTANCE );
+	}
+
+	@Override
+	public boolean areEqual(byte[] one, byte[] another) {
+		return one == another
+				|| ( one != null && another != null && Arrays.equals( one, another ) );
+	}
+
+	@Override
+	public int extractHashCode(byte[] bytes) {
+		int hashCode = 1;
+		for ( byte aByte : bytes ) {
+			hashCode = 31 * hashCode + aByte;
+		}
+		return hashCode;
+	}
+
+	public String toString(byte[] bytes) {
+		final StringBuilder buf = new StringBuilder( bytes.length * 2 );
+		for ( byte aByte : bytes ) {
+			final String hexStr = Integer.toHexString( aByte - Byte.MIN_VALUE );
+			if ( hexStr.length() == 1 ) {
+				buf.append( '0' );
+			}
+			buf.append( hexStr );
+		}
+		return buf.toString();
+	}
+
+	@Override
+	public String extractLoggableRepresentation(byte[] value) {
+		return (value == null) ? super.extractLoggableRepresentation( null ) : Arrays.toString( value );
+	}
+
+	public byte[] fromString(String string) {
+		if ( string == null ) {
+			return null;
+		}
+		if ( string.length() % 2 != 0 ) {
+			throw new IllegalArgumentException( "The string is not a valid string representation of a binary content." );
+		}
+		byte[] bytes = new byte[string.length() / 2];
+		for ( int i = 0; i < bytes.length; i++ ) {
+			final String hexStr = string.substring( i * 2, (i + 1) * 2 );
+			bytes[i] = (byte) (Integer.parseInt(hexStr, 16) + Byte.MIN_VALUE);
+		}
+		return bytes;
+	}
+
+	@Override
+	@SuppressWarnings({ "unchecked" })
+	public Comparator<byte[]> getComparator() {
+		return RowVersionComparator.INSTANCE;
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	public <X> X unwrap(byte[] value, Class<X> type, WrapperOptions options) {
+		if ( value == null ) {
+			return null;
+		}
+		if ( byte[].class.isAssignableFrom( type ) ) {
+			return (X) value;
+		}
+		if ( InputStream.class.isAssignableFrom( type ) ) {
+			return (X) new ByteArrayInputStream( value );
+		}
+		if ( BinaryStream.class.isAssignableFrom( type ) ) {
+			return (X) new BinaryStreamImpl( value );
+		}
+		if ( Blob.class.isAssignableFrom( type ) ) {
+			return (X) options.getLobCreator().createBlob( value );
+		}
+
+		throw unknownUnwrap( type );
+	}
+
+	public <X> byte[] wrap(X value, WrapperOptions options) {
+		if ( value == null ) {
+			return null;
+		}
+		if ( byte[].class.isInstance( value ) ) {
+			return (byte[]) value;
+		}
+		if ( InputStream.class.isInstance( value ) ) {
+			return DataHelper.extractBytes( (InputStream) value );
+		}
+		if ( Blob.class.isInstance( value ) || DataHelper.isNClob( value.getClass() ) ) {
+			try {
+				return DataHelper.extractBytes( ( (Blob) value ).getBinaryStream() );
+			}
+			catch ( SQLException e ) {
+				throw new HibernateException( "Unable to access lob stream", e );
+			}
+		}
+
+		throw unknownWrap( value.getClass() );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/util/RowVersionComparatorTest.java b/hibernate-core/src/test/java/org/hibernate/test/util/RowVersionComparatorTest.java
new file mode 100644
index 0000000000..42f3e018c7
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/util/RowVersionComparatorTest.java
@@ -0,0 +1,139 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.util;
+
+import org.junit.Test;
+
+import org.hibernate.internal.util.compare.RowVersionComparator;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
+/**
+ * @author Gail Badner
+ */
+public class RowVersionComparatorTest extends BaseUnitTestCase {
+
+	@Test
+	public void testNull() {
+		try {
+			RowVersionComparator.INSTANCE.compare( null, null );
+			fail( "should have thrown NullPointerException" );
+		}
+		catch ( NullPointerException ex ) {
+			// expected
+		}
+
+		try {
+			RowVersionComparator.INSTANCE.compare( null, new byte[] { 1 } );
+			fail( "should have thrown NullPointerException" );
+		}
+		catch ( NullPointerException ex ) {
+			// expected
+		}
+
+		try {
+			RowVersionComparator.INSTANCE.compare( new byte[] { 1 }, null );
+			fail( "should have thrown NullPointerException" );
+		}
+		catch ( NullPointerException ex ) {
+			// expected
+		}
+	}
+
+	@Test
+	public void testArraysSameLength() {
+		assertEquals(
+				0,
+				RowVersionComparator.INSTANCE.compare(
+						new byte[] {},
+						new byte[] {}
+				)
+		);
+		assertEquals(
+				0,
+				RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1 },
+						new byte[] { 1 }
+				)
+		);
+		assertEquals(
+				0,
+				RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1, 2 },
+						new byte[] { 1, 2 }
+				)
+		);
+		assertTrue(
+				RowVersionComparator.INSTANCE.compare(
+						new byte[] { 0, 2 },
+						new byte[] { 1, 2 }
+				) < 0
+		);
+
+		assertTrue(
+				RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1, 1 },
+						new byte[] { 1, 2 }
+				) < 0
+		);
+
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 2, 2 },
+						new byte[] { 1, 2 }
+				) > 0
+		);
+
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 2, 2 },
+						new byte[] { 2, 1 }
+				) > 0
+		);
+	}
+
+	@Test
+	public void testArraysDifferentLength() {
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] {},
+						new byte[] { 1 }
+				) < 0
+		);
+
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1 },
+						new byte[] {}
+				) > 0
+		);
+
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1 },
+						new byte[] { 1, 2 }
+				) < 0
+		);
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1, 2 },
+						new byte[] { 1 }
+				) > 0
+		);
+
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 2 },
+						new byte[] { 1, 2 }
+				) > 0
+		);
+		assertTrue( RowVersionComparator.INSTANCE.compare(
+						new byte[] { 1, 2 },
+						new byte[] { 2 }
+				) < 0
+		);
+
+	}
+
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampComparisonAnnotationsTest.java b/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampComparisonAnnotationsTest.java
new file mode 100644
index 0000000000..f369c76925
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampComparisonAnnotationsTest.java
@@ -0,0 +1,100 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.version.sybase;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Table;
+import javax.persistence.Version;
+
+import org.junit.Test;
+
+import org.hibernate.Session;
+import org.hibernate.annotations.Generated;
+import org.hibernate.annotations.GenerationTime;
+import org.hibernate.annotations.Source;
+import org.hibernate.annotations.SourceType;
+import org.hibernate.annotations.Type;
+import org.hibernate.dialect.SybaseASE15Dialect;
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.type.RowVersionType;
+import org.hibernate.type.VersionType;
+
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Gail Badner
+ */
+@RequiresDialect( SybaseASE15Dialect.class )
+public class SybaseTimestampComparisonAnnotationsTest extends BaseCoreFunctionalTestCase {
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-10413" )
+	public void testComparableTimestamps() {
+		final VersionType versionType =
+				sessionFactory().getEntityPersister( Thing.class.getName() ).getVersionType();
+		assertSame( RowVersionType.INSTANCE, versionType );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		Thing thing = new Thing();
+		thing.name = "n";
+		s.persist( thing );
+		s.getTransaction().commit();
+		s.close();
+
+		byte[] previousVersion = thing.version;
+		for ( int i = 0 ; i < 20 ; i++ ) {
+			try {
+				Thread.sleep(1000);                 //1000 milliseconds is one second.
+			} catch(InterruptedException ex) {
+				Thread.currentThread().interrupt();
+			}
+
+			s = openSession();
+			s.getTransaction().begin();
+			thing.name = "n" + i;
+			thing = (Thing) s.merge( thing );
+			s.getTransaction().commit();
+			s.close();
+
+			assertTrue( versionType.compare( previousVersion, thing.version ) < 0 );
+			previousVersion = thing.version;
+		}
+
+		s = openSession();
+		s.getTransaction().begin();
+		s.delete( thing );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] { Thing.class };
+	}
+
+	@Entity
+	@Table(name="thing")
+	public static class Thing {
+		@Id
+		private long id;
+
+		@Version
+		@Generated(GenerationTime.ALWAYS)
+		@Column(name = "ver", columnDefinition = "timestamp")
+		private byte[] version;
+
+		private String name;
+
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampVersioningTest.java b/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampVersioningTest.java
index e593a56612..2df937e4e6 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampVersioningTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/version/sybase/SybaseTimestampVersioningTest.java
@@ -1,219 +1,265 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.version.sybase;
 
+import javax.persistence.OptimisticLockException;
+
 import org.junit.Test;
 
-import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.type.BinaryType;
+import org.hibernate.type.RowVersionType;
+import org.hibernate.type.VersionType;
 
 import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Implementation of VersionTest.
  *
  * @author Steve Ebersole
  */
 @RequiresDialect( SybaseASE15Dialect.class )
 public class SybaseTimestampVersioningTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "version/sybase/User.hbm.xml" };
 	}
 
 	@Test
 	public void testLocking() throws Throwable {
 		// First, create the needed row...
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User steve = new User( "steve" );
 		s.persist( steve );
 		t.commit();
 		s.close();
 
 		// next open two sessions, and try to update from each "simultaneously"...
 		Session s1 = null;
 		Session s2 = null;
 		Transaction t1 = null;
 		Transaction t2 = null;
 		try {
 			s1 = sessionFactory().openSession();
 			t1 = s1.beginTransaction();
 			s2 = sessionFactory().openSession();
 			t2 = s2.beginTransaction();
 
 			User user1 = ( User ) s1.get( User.class, steve.getId() );
 			User user2 = ( User ) s2.get( User.class, steve.getId() );
 
 			user1.setUsername( "se" );
 			t1.commit();
 			t1 = null;
 
 			user2.setUsername( "steve-e" );
 			try {
 				t2.commit();
 				fail( "optimistic lock check did not fail" );
 			}
-			catch( HibernateException e ) {
+			catch( OptimisticLockException e ) {
 				// expected...
 				try {
 					t2.rollback();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		catch( Throwable error ) {
 			if ( t1 != null ) {
 				try {
 					t1.rollback();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 			if ( t2 != null ) {
 				try {
 					t2.rollback();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 			throw error;
 		}
 		finally {
 			if ( s1 != null ) {
 				try {
 					s1.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 			if ( s2 != null ) {
 				try {
 					s2.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 
 		// lastly, clean up...
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.load( User.class, steve.getId() ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testCollectionVersion() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User steve = new User( "steve" );
 		s.persist( steve );
 		Group admin = new Group( "admin" );
 		s.persist( admin );
 		t.commit();
 		s.close();
 
 		byte[] steveTimestamp = steve.getTimestamp();
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		admin = ( Group ) s.get( Group.class, admin.getId() );
 		steve.getGroups().add( admin );
 		admin.getUsers().add( steve );
 		t.commit();
 		s.close();
 
 		assertFalse(
 				"owner version not incremented", BinaryType.INSTANCE.isEqual(
 				steveTimestamp, steve.getTimestamp()
 		)
 		);
 
 		steveTimestamp = steve.getTimestamp();
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		steve.getGroups().clear();
 		t.commit();
 		s.close();
 
 		assertFalse(
 				"owner version not incremented", BinaryType.INSTANCE.isEqual(
 				steveTimestamp, steve.getTimestamp()
 		)
 		);
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.load( User.class, steve.getId() ) );
 		s.delete( s.load( Group.class, admin.getId() ) );
 		t.commit();
 		s.close();
 	}
 
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testCollectionNoVersion() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User steve = new User( "steve" );
 		s.persist( steve );
 		Permission perm = new Permission( "silly", "user", "rw" );
 		s.persist( perm );
 		t.commit();
 		s.close();
 
 		byte[] steveTimestamp = steve.getTimestamp();
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		perm = ( Permission ) s.get( Permission.class, perm.getId() );
 		steve.getPermissions().add( perm );
 		t.commit();
 		s.close();
 
 		assertTrue(
 				"owner version was incremented", BinaryType.INSTANCE.isEqual(
 				steveTimestamp, steve.getTimestamp()
 		)
 		);
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		steve.getPermissions().clear();
 		t.commit();
 		s.close();
 
 		assertTrue(
 				"owner version was incremented", BinaryType.INSTANCE.isEqual(
 				steveTimestamp, steve.getTimestamp()
 		)
 		);
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.load( User.class, steve.getId() ) );
 		s.delete( s.load( Permission.class, perm.getId() ) );
 		t.commit();
 		s.close();
 	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-10413" )
+	public void testComparableTimestamps() {
+		final VersionType versionType =
+				sessionFactory().getEntityPersister( User.class.getName() ).getVersionType();
+		assertSame( RowVersionType.INSTANCE, versionType );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		User user = new User();
+		user.setUsername( "n" );
+		s.persist( user );
+		s.getTransaction().commit();
+		s.close();
+
+		byte[] previousTimestamp = user.getTimestamp();
+		for ( int i = 0 ; i < 20 ; i++ ) {
+			try {
+				Thread.sleep(1000);                 //1000 milliseconds is one second.
+			} catch(InterruptedException ex) {
+				Thread.currentThread().interrupt();
+			}
+
+			s = openSession();
+			s.getTransaction().begin();
+			user.setUsername( "n" + i );
+			user = (User) s.merge( user );
+			s.getTransaction().commit();
+			s.close();
+
+			assertTrue( versionType.compare( previousTimestamp, user.getTimestamp() ) < 0 );
+			previousTimestamp = user.getTimestamp();
+		}
+
+		s = openSession();
+		s.getTransaction().begin();
+		s.delete( user );
+		s.getTransaction().commit();
+		s.close();
+	}
 }
