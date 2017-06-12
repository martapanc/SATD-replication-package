diff --git a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
index 238d816183..856a28da67 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
@@ -1,4412 +1,4389 @@
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
 import org.hibernate.boot.model.source.internal.ConstraintSecondPass;
 import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
 import org.hibernate.boot.model.source.spi.AnyMappingSource;
 import org.hibernate.boot.model.source.spi.AttributePath;
 import org.hibernate.boot.model.source.spi.AttributeRole;
 import org.hibernate.boot.model.source.spi.AttributeSource;
 import org.hibernate.boot.model.source.spi.CascadeStyleSource;
 import org.hibernate.boot.model.source.spi.CollectionIdSource;
 import org.hibernate.boot.model.source.spi.ColumnSource;
 import org.hibernate.boot.model.source.spi.CompositeIdentifierSource;
 import org.hibernate.boot.model.source.spi.ConstraintSource;
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
-			entityDescriptor.setNodeName( entitySource.getXmlNodeName() );
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
 
 		for ( String tableName : entitySource.getSynchronizedTableNames() ) {
 			entityDescriptor.addSynchronizedTable( tableName );
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
-			property.setNodeName( "id" );
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
 
 		registerConstraintSecondPasses( mappingDocument, entitySource, entityTableXref );
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
 
 	private void registerConstraintSecondPasses(
 			MappingDocument mappingDocument,
 			EntitySource entitySource,
 			final EntityTableXref entityTableXref) {
 		if ( entitySource.getConstraints() == null ) {
 			return;
 		}
 
 		for ( ConstraintSource constraintSource : entitySource.getConstraints() ) {
 			final String logicalTableName = constraintSource.getTableName();
 			final Table table = entityTableXref.resolveTable( database.toIdentifier( logicalTableName ) );
 
 			mappingDocument.getMetadataCollector().addSecondPass(
 					new ConstraintSecondPass(
 							mappingDocument,
 							table,
 							constraintSource
 					)
 			);
 		}
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
 			if ( ColumnSource.class.isInstance( relationalValueSource ) ) {
 				final ColumnSource columnSource = (ColumnSource) relationalValueSource;
 				if ( EqualsHelper.equals( tableName, columnSource.getContainingTableName() ) ) {
 					continue;
 				}
 
 				if ( tableName != null ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Attribute [%s] referenced columns from multiple tables: %s, %s",
 									attributeName,
 									tableName,
 									columnSource.getContainingTableName()
 							),
 							mappingDocument.getOrigin()
 					);
 				}
 
 				tableName = columnSource.getContainingTableName();
 			}
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
 
-		final String xmlNodeName = determineXmlNodeName( embeddedSource, componentBinding.getOwner().getNodeName() );
-		componentBinding.setNodeName( xmlNodeName );
-		attribute.setNodeName( xmlNodeName );
+		if ( StringHelper.isNotEmpty( embeddedSource.getName() ) ) {
+			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
+		}
 
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
 				true,
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
 					),
 					mappingDocument
 			);
 		}
 		else {
 			Property propertyBinding = entityBinding.getReferencedProperty( referencedPropertyName );
 			if ( propertyBinding == null ) {
 				// attribute may just not have been processed yet - set up a delayed handler
 				registerDelayedPropertyReferenceHandler(
 						new DelayedPropertyReferenceHandlerImpl(
 								referencedEntityName,
 								referencedPropertyName,
 								isUnique,
 								sourceElementSynopsis,
 								mappingDocument.getOrigin()
 						),
 						mappingDocument
 				);
 			}
 			else {
 				log.tracef(
 						"Property [%s.%s] referenced by property-ref [%s] was available - no need for delayed handling",
 						referencedEntityName,
 						referencedPropertyName,
 						sourceElementSynopsis
 				);
 				if ( isUnique ) {
 					( (SimpleValue) propertyBinding.getValue() ).setAlternateUniqueKey( true );
 				}
 			}
 		}
 	}
 
 	private void registerDelayedPropertyReferenceHandler(
 			DelayedPropertyReferenceHandlerImpl handler,
 			MetadataBuildingContext buildingContext) {
 		log.tracef(
 				"Property [%s.%s] referenced by property-ref [%s] was not yet available - creating delayed handler",
 				handler.referencedEntityName,
 				handler.referencedPropertyName,
 				handler.sourceElementSynopsis
 		);
 		buildingContext.getMetadataCollector().addDelayedPropertyReferenceHandler( handler );
 	}
 
 	public void bindOneToOne(
 			final MappingDocument sourceDocument,
 			final SingularAttributeSourceOneToOne oneToOneSource,
 			final OneToOne oneToOneBinding) {
 		oneToOneBinding.setPropertyName( oneToOneSource.getName() );
 
 		relationalObjectBinder.bindFormulas(
 				sourceDocument,
 				oneToOneSource.getFormulaSources(),
 				oneToOneBinding
 		);
 
 
 		if ( oneToOneSource.isConstrained() ) {
 			if ( oneToOneSource.getCascadeStyleName() != null
 					&& oneToOneSource.getCascadeStyleName().contains( "delete-orphan" ) ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"one-to-one attribute [%s] cannot specify orphan delete cascading as it is constrained",
 								oneToOneSource.getAttributeRole().getFullPath()
 						),
 						sourceDocument.getOrigin()
 				);
 			}
 			oneToOneBinding.setConstrained( true );
 			oneToOneBinding.setForeignKeyType( ForeignKeyDirection.FROM_PARENT );
 		}
 		else {
 			oneToOneBinding.setForeignKeyType( ForeignKeyDirection.TO_PARENT );
 		}
 
 		oneToOneBinding.setLazy( oneToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED );
 		oneToOneBinding.setFetchMode(
 				oneToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT
 						? FetchMode.SELECT
 						: FetchMode.JOIN
 		);
 		oneToOneBinding.setUnwrapProxy( oneToOneSource.getFetchCharacteristics().isUnwrapProxies() );
 
 
 		if ( StringHelper.isNotEmpty( oneToOneSource.getReferencedEntityAttributeName() ) ) {
 			oneToOneBinding.setReferencedPropertyName( oneToOneSource.getReferencedEntityAttributeName() );
 			oneToOneBinding.setReferenceToPrimaryKey( false );
 		}
 		else {
 			oneToOneBinding.setReferenceToPrimaryKey( true );
 		}
 
 		// todo : probably need some reflection here if null
 		oneToOneBinding.setReferencedEntityName( oneToOneSource.getReferencedEntityName() );
 
 		if ( oneToOneSource.isEmbedXml() == Boolean.TRUE ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
 		}
 
 		if ( StringHelper.isNotEmpty( oneToOneSource.getExplicitForeignKeyName() ) ) {
 			oneToOneBinding.setForeignKeyName( oneToOneSource.getExplicitForeignKeyName() );
 		}
 
 		oneToOneBinding.setCascadeDeleteEnabled( oneToOneSource.isCascadeDeleteEnabled() );
 	}
 
 	private Property createManyToOneAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceManyToOne manyToOneSource,
 			ManyToOne manyToOneBinding,
 			String containingClassName) {
 		final String attributeName = manyToOneSource.getName();
 
 		final String referencedEntityName;
 		if ( manyToOneSource.getReferencedEntityName() != null ) {
 			referencedEntityName = manyToOneSource.getReferencedEntityName();
 		}
 		else {
 			Class reflectedPropertyClass = Helper.reflectedPropertyClass( sourceDocument, containingClassName, attributeName );
 			if ( reflectedPropertyClass != null ) {
 				referencedEntityName = reflectedPropertyClass.getName();
 			}
 			else {
 				prepareValueTypeViaReflection(
 						sourceDocument,
 						manyToOneBinding,
 						containingClassName,
 						attributeName,
 						manyToOneSource.getAttributeRole()
 				);
 				referencedEntityName = manyToOneBinding.getTypeName();
 			}
 		}
 
 		if ( manyToOneSource.isUnique() ) {
 			manyToOneBinding.markAsLogicalOneToOne();
 		}
 
 		bindManyToOneAttribute( sourceDocument, manyToOneSource, manyToOneBinding, referencedEntityName );
 
 		final String propertyRef = manyToOneBinding.getReferencedPropertyName();
 
 		if ( propertyRef != null ) {
 			handlePropertyReference(
 					sourceDocument,
 					manyToOneBinding.getReferencedEntityName(),
 					propertyRef,
 					true,
 					"<many-to-one name=\"" + manyToOneSource.getName() + "\"/>"
 			);
 		}
 
 		Property prop = new Property();
 		prop.setValue( manyToOneBinding );
 		bindProperty(
 				sourceDocument,
 				manyToOneSource,
 				prop
 		);
 
 		if ( StringHelper.isNotEmpty( manyToOneSource.getCascadeStyleName() ) ) {
 			// todo : would be better to delay this the end of binding (second pass, etc)
 			// in order to properly allow for a singular unique column for a many-to-one to
 			// also trigger a "logical one-to-one".  As-is, this can occasionally lead to
 			// false exceptions if the many-to-one column binding is delayed and the
 			// uniqueness is indicated on the <column/> rather than on the <many-to-one/>
 			//
 			// Ideally, would love to see a SimpleValue#validate approach, rather than a
 			// SimpleValue#isValid that is then handled at a higher level (Property, etc).
 			// The reason being that the current approach misses the exact reason
 			// a "validation" fails since it loses "context"
 			if ( manyToOneSource.getCascadeStyleName().contains( "delete-orphan" ) ) {
 				if ( !manyToOneBinding.isLogicalOneToOne() ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"many-to-one attribute [%s] specified delete-orphan but is not specified as unique; " +
 											"remove delete-orphan cascading or specify unique=\"true\"",
 									manyToOneSource.getAttributeRole().getFullPath()
 							),
 							sourceDocument.getOrigin()
 					);
 				}
 			}
 		}
 
 		return prop;
 	}
 
 	private void bindManyToOneAttribute(
 			final MappingDocument sourceDocument,
 			final SingularAttributeSourceManyToOne manyToOneSource,
 			ManyToOne manyToOneBinding,
 			String referencedEntityName) {
 		// NOTE : no type information to bind
 
 		manyToOneBinding.setReferencedEntityName( referencedEntityName );
 		if ( StringHelper.isNotEmpty( manyToOneSource.getReferencedEntityAttributeName() ) ) {
 			manyToOneBinding.setReferencedPropertyName( manyToOneSource.getReferencedEntityAttributeName() );
 			manyToOneBinding.setReferenceToPrimaryKey( false );
 		}
 		else {
 			manyToOneBinding.setReferenceToPrimaryKey( true );
 		}
 
 		manyToOneBinding.setLazy( manyToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED );
 		manyToOneBinding.setUnwrapProxy( manyToOneSource.getFetchCharacteristics().isUnwrapProxies() );
 		manyToOneBinding.setFetchMode(
 				manyToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT
 						? FetchMode.SELECT
 						: FetchMode.JOIN
 		);
 
 		if ( manyToOneSource.isEmbedXml() == Boolean.TRUE ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
 		}
 
 		manyToOneBinding.setIgnoreNotFound( manyToOneSource.isIgnoreNotFound() );
 
 		if ( StringHelper.isNotEmpty( manyToOneSource.getExplicitForeignKeyName() ) ) {
 			manyToOneBinding.setForeignKeyName( manyToOneSource.getExplicitForeignKeyName() );
 		}
 
 		final ManyToOneColumnBinder columnBinder = new ManyToOneColumnBinder(
 				sourceDocument,
 				manyToOneSource,
 				manyToOneBinding,
 				referencedEntityName
 		);
 		final boolean canBindColumnsImmediately = columnBinder.canProcessImmediately();
 		if ( canBindColumnsImmediately ) {
 			columnBinder.doSecondPass( null );
 		}
 		else {
 			sourceDocument.getMetadataCollector().addSecondPass( columnBinder );
 		}
 
 		if ( !manyToOneSource.isIgnoreNotFound() ) {
 			// we skip creating the FK here since this setting tells us there
 			// cannot be a suitable/proper FK
 			final ManyToOneFkSecondPass fkSecondPass = new ManyToOneFkSecondPass(
 					sourceDocument,
 					manyToOneSource,
 					manyToOneBinding,
 					referencedEntityName
 			);
 
 			if ( canBindColumnsImmediately && fkSecondPass.canProcessImmediately() ) {
 				fkSecondPass.doSecondPass( null );
 			}
 			else {
 				sourceDocument.getMetadataCollector().addSecondPass( fkSecondPass );
 			}
 		}
 
 		manyToOneBinding.setCascadeDeleteEnabled( manyToOneSource.isCascadeDeleteEnabled() );
 	}
 
 	private Property createAnyAssociationAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceAny anyMapping,
 			Any anyBinding,
 			String entityName) {
 		final String attributeName = anyMapping.getName();
 
 		bindAny( sourceDocument, anyMapping, anyBinding, anyMapping.getAttributeRole(), anyMapping.getAttributePath() );
 
 		prepareValueTypeViaReflection( sourceDocument, anyBinding, entityName, attributeName, anyMapping.getAttributeRole() );
 
 		anyBinding.createForeignKey();
 
 		Property prop = new Property();
 		prop.setValue( anyBinding );
 		bindProperty(
 				sourceDocument,
 				anyMapping,
 				prop
 		);
 
 		return prop;
 	}
 
 	private void bindAny(
 			MappingDocument sourceDocument,
 			final AnyMappingSource anyMapping,
 			Any anyBinding,
 			final AttributeRole attributeRole,
 			AttributePath attributePath) {
 		final TypeResolution keyTypeResolution = resolveType(
 				sourceDocument,
 				anyMapping.getKeySource().getTypeSource()
 		);
 		if ( keyTypeResolution != null ) {
 			anyBinding.setIdentifierType( keyTypeResolution.typeName );
 		}
 
 		final TypeResolution discriminatorTypeResolution = resolveType(
 				sourceDocument,
 				anyMapping.getDiscriminatorSource().getTypeSource()
 		);
 
 		if ( discriminatorTypeResolution != null ) {
 			anyBinding.setMetaType( discriminatorTypeResolution.typeName );
 			try {
 				final DiscriminatorType metaType = (DiscriminatorType) sourceDocument.getMetadataCollector()
 						.getTypeResolver()
 						.heuristicType( discriminatorTypeResolution.typeName );
 
 				final HashMap anyValueBindingMap = new HashMap();
 				for ( Map.Entry<String,String> discriminatorValueMappings : anyMapping.getDiscriminatorSource().getValueMappings().entrySet() ) {
 					try {
 						final Object discriminatorValue = metaType.stringToObject( discriminatorValueMappings.getKey() );
 						final String mappedEntityName = sourceDocument.qualifyClassName( discriminatorValueMappings.getValue() );
 
 						//noinspection unchecked
 						anyValueBindingMap.put( discriminatorValue, mappedEntityName );
 					}
 					catch (Exception e) {
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Unable to interpret <meta-value value=\"%s\" class=\"%s\"/> defined as part of <any/> attribute [%s]",
 										discriminatorValueMappings.getKey(),
 										discriminatorValueMappings.getValue(),
 										attributeRole.getFullPath()
 								),
 								e,
 								sourceDocument.getOrigin()
 						);
 					}
 
 				}
 				anyBinding.setMetaValues( anyValueBindingMap );
 			}
 			catch (ClassCastException e) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Specified meta-type [%s] for <any/> attribute [%s] did not implement DiscriminatorType",
 								discriminatorTypeResolution.typeName,
 								attributeRole.getFullPath()
 						),
 						e,
 						sourceDocument.getOrigin()
 				);
 			}
 		}
 
 		relationalObjectBinder.bindColumnOrFormula(
 				sourceDocument,
 				anyMapping.getDiscriminatorSource().getRelationalValueSource(),
 				anyBinding,
 				true,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineAnyDiscriminatorColumnName(
 								anyMapping.getDiscriminatorSource()
 						);
 					}
 				}
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				anyMapping.getKeySource().getRelationalValueSources(),
 				anyBinding,
 				true,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineAnyKeyColumnName(
 								anyMapping.getKeySource()
 						);
 					}
 				}
 		);
 	}
 
 	private void prepareValueTypeViaReflection(
 			MappingDocument sourceDocument,
 			Value value,
 			String containingClassName,
 			String propertyName,
 			AttributeRole attributeRole) {
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException(
 					String.format(
 							Locale.ENGLISH,
 							"Attribute mapping must define a name attribute: containingClassName=[%s], propertyName=[%s], role=[%s]",
 							containingClassName,
 							propertyName,
 							attributeRole.getFullPath()
 					),
 					sourceDocument.getOrigin()
 			);
 		}
 
 		try {
 			value.setTypeUsingReflection( containingClassName, propertyName );
 		}
 		catch (org.hibernate.MappingException ome) {
 			throw new MappingException(
 					String.format(
 							Locale.ENGLISH,
 							"Error calling Value#setTypeUsingReflection: containingClassName=[%s], propertyName=[%s], role=[%s]",
 							containingClassName,
 							propertyName,
 							attributeRole.getFullPath()
 					),
 					ome,
 					sourceDocument.getOrigin()
 			);
 		}
 	}
 
 	private void bindProperty(
 			MappingDocument mappingDocument,
 			AttributeSource propertySource,
 			Property property) {
 		property.setName( propertySource.getName() );
-		property.setNodeName( determineXmlNodeName( propertySource, null ) );
+
+		if ( StringHelper.isNotEmpty( propertySource.getName() ) ) {
+			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
+		}
 
 		property.setPropertyAccessorName(
 				StringHelper.isNotEmpty( propertySource.getPropertyAccessorName() )
 						? propertySource.getPropertyAccessorName()
 						: mappingDocument.getMappingDefaults().getImplicitPropertyAccessorName()
 		);
 
 		if ( propertySource instanceof CascadeStyleSource ) {
 			final CascadeStyleSource cascadeStyleSource = (CascadeStyleSource) propertySource;
 
 			property.setCascade(
 					StringHelper.isNotEmpty( cascadeStyleSource.getCascadeStyleName() )
 							? cascadeStyleSource.getCascadeStyleName()
 							: mappingDocument.getMappingDefaults().getImplicitCascadeStyleName()
 			);
 		}
 
 		property.setOptimisticLocked( propertySource.isIncludedInOptimisticLocking() );
 
 		if ( propertySource.isSingular() ) {
 			final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) propertySource;
 
 			property.setInsertable( singularAttributeSource.isInsertable() );
 			property.setUpdateable( singularAttributeSource.isUpdatable() );
 
 			// NOTE : Property#is refers to whether a property is lazy via bytecode enhancement (not proxies)
 			property.setLazy( singularAttributeSource.isBytecodeLazy() );
 
 			final GenerationTiming generationTiming = singularAttributeSource.getGenerationTiming();
 			if ( generationTiming == GenerationTiming.ALWAYS || generationTiming == GenerationTiming.INSERT ) {
 				// we had generation specified...
 				//   	HBM only supports "database generated values"
 				property.setValueGenerationStrategy( new GeneratedValueGeneration( generationTiming ) );
 
 				// generated properties can *never* be insertable...
 				if ( property.isInsertable() ) {
 					if ( singularAttributeSource.isInsertable() == null ) {
 						// insertable simply because that is the user did not specify
 						// anything; just override it
 						property.setInsertable( false );
 					}
 					else {
 						// the user specifically supplied insert="true",
 						// which constitutes an illegal combo
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Cannot specify both insert=\"true\" and generated=\"%s\" for property %s",
 										generationTiming.name().toLowerCase(Locale.ROOT),
 										propertySource.getName()
 								),
 								mappingDocument.getOrigin()
 						);
 					}
 				}
 
 				// properties generated on update can never be updatable...
 				if ( property.isUpdateable() && generationTiming == GenerationTiming.ALWAYS ) {
 					if ( singularAttributeSource.isUpdatable() == null ) {
 						// updatable only because the user did not specify
 						// anything; just override it
 						property.setUpdateable( false );
 					}
 					else {
 						// the user specifically supplied update="true",
 						// which constitutes an illegal combo
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Cannot specify both update=\"true\" and generated=\"%s\" for property %s",
 										generationTiming.name().toLowerCase(Locale.ROOT),
 										propertySource.getName()
 								),
 								mappingDocument.getOrigin()
 						);
 					}
 				}
 			}
 		}
 
 		property.setMetaAttributes( propertySource.getToolingHintContext().getMetaAttributeMap() );
 
 		if ( log.isDebugEnabled() ) {
 			final StringBuilder message = new StringBuilder()
 					.append( "Mapped property: " )
 					.append( propertySource.getName() )
 					.append( " -> [" );
 			final Iterator itr = property.getValue().getColumnIterator();
 			while ( itr.hasNext() ) {
 				message.append( ( (Selectable) itr.next() ).getText() );
 				if ( itr.hasNext() ) {
 					message.append( ", " );
 				}
 			}
 			message.append( "]" );
 			log.debug( message.toString() );
 		}
 	}
 
-	private String determineXmlNodeName(AttributeSource propertySource, String fallbackXmlNodeName) {
-		String nodeName = propertySource.getXmlNodeName();
-		if ( StringHelper.isNotEmpty( nodeName ) ) {
-			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
-		}
-		else {
-			nodeName = propertySource.getName();
-		}
-		if ( nodeName == null ) {
-			nodeName = fallbackXmlNodeName;
-		}
-
-		return nodeName;
-	}
-
 	private void bindComponent(
 			MappingDocument sourceDocument,
 			EmbeddableSource embeddableSource,
 			Component component,
 			String containingClassName,
 			String propertyName,
 			String xmlNodeName,
 			boolean isVirtual) {
 		final String fullRole = embeddableSource.getAttributeRoleBase().getFullPath();
 		final String explicitComponentClassName = extractExplicitComponentClassName( embeddableSource );
 
 		bindComponent(
 				sourceDocument,
 				fullRole,
 				embeddableSource,
 				component,
 				explicitComponentClassName,
 				containingClassName,
 				propertyName,
 				isVirtual,
 				embeddableSource.isDynamic(),
 				xmlNodeName
 		);
 	}
 
 	private String extractExplicitComponentClassName(EmbeddableSource embeddableSource) {
 		if ( embeddableSource.getTypeDescriptor() == null ) {
 			return null;
 		}
 
 		return embeddableSource.getTypeDescriptor().getName();
 	}
 
 	private void bindComponent(
 			MappingDocument sourceDocument,
 			String role,
 			EmbeddableSource embeddableSource,
 			Component componentBinding,
 			String explicitComponentClassName,
 			String containingClassName,
 			String propertyName,
 			boolean isVirtual,
 			boolean isDynamic,
 			String xmlNodeName) {
 
 		componentBinding.setMetaAttributes( embeddableSource.getToolingHintContext().getMetaAttributeMap() );
 
 		componentBinding.setRoleName( role );
 
 		componentBinding.setEmbedded( isVirtual );
 
 		// todo : better define the conditions in this if/else
 		if ( isDynamic ) {
 			// dynamic is represented as a Map
 			log.debugf( "Binding dynamic-component [%s]", role );
 			componentBinding.setDynamic( true );
 		}
 		else if ( isVirtual ) {
 			// virtual (what used to be called embedded) is just a conceptual composition...
 			// <properties/> for example
 			if ( componentBinding.getOwner().hasPojoRepresentation() ) {
 				log.debugf( "Binding virtual component [%s] to owner class [%s]", role, componentBinding.getOwner().getClassName() );
 				componentBinding.setComponentClassName( componentBinding.getOwner().getClassName() );
 			}
 			else {
 				log.debugf( "Binding virtual component [%s] as dynamic", role );
 				componentBinding.setDynamic( true );
 			}
 		}
 		else {
 			log.debugf( "Binding component [%s]", role );
 			if ( StringHelper.isNotEmpty( explicitComponentClassName ) ) {
 				log.debugf( "Binding component [%s] to explicitly specified class", role, explicitComponentClassName );
 				componentBinding.setComponentClassName( explicitComponentClassName );
 			}
 			else if ( componentBinding.getOwner().hasPojoRepresentation() ) {
 				log.tracef( "Attempting to determine component class by reflection %s", role );
 				final Class reflectedComponentClass;
 				if ( StringHelper.isNotEmpty( containingClassName ) && StringHelper.isNotEmpty( propertyName ) ) {
 					reflectedComponentClass = Helper.reflectedPropertyClass(
 							sourceDocument,
 							containingClassName,
 							propertyName
 					);
 				}
 				else {
 					reflectedComponentClass = null;
 				}
 
 				if ( reflectedComponentClass == null ) {
 					log.debugf(
 							"Unable to determine component class name via reflection, and explicit " +
 									"class name not given; role=[%s]",
 							role
 					);
 				}
 				else {
 					componentBinding.setComponentClassName( reflectedComponentClass.getName() );
 				}
 			}
 			else {
 				componentBinding.setDynamic( true );
 			}
 		}
 
 		String nodeName = xmlNodeName;
 		if ( StringHelper.isNotEmpty( nodeName ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
 		}
-		else {
-			nodeName = propertyName;
-		}
-		if ( nodeName == null ) {
-			nodeName = componentBinding.getOwner().getNodeName();
-		}
-		componentBinding.setNodeName( nodeName );
 
 		// todo : anything else to pass along?
 		bindAllCompositeAttributes(
 				sourceDocument,
 				embeddableSource,
 				componentBinding
 		);
 
 		if ( embeddableSource.getParentReferenceAttributeName() != null ) {
 			componentBinding.setParentProperty( embeddableSource.getParentReferenceAttributeName() );
 		}
 
 		if ( embeddableSource.isUnique() ) {
 			final ArrayList<Column> cols = new ArrayList<Column>();
 			final Iterator itr = componentBinding.getColumnIterator();
 			while ( itr.hasNext() ) {
 				final Object selectable = itr.next();
 				// skip formulas.  ugh, yes terrible naming of these methods :(
 				if ( !Column.class.isInstance( selectable ) ) {
 					continue;
 				}
 				cols.add( (Column) selectable );
 			}
 			// todo : we may need to delay this
 			componentBinding.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		if ( embeddableSource.getTuplizerClassMap() != null ) {
 			if ( embeddableSource.getTuplizerClassMap().size() > 1 ) {
 				DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
 			}
 			for ( Map.Entry<EntityMode,String> tuplizerEntry : embeddableSource.getTuplizerClassMap().entrySet() ) {
 				componentBinding.addTuplizer(
 						tuplizerEntry.getKey(),
 						tuplizerEntry.getValue()
 				);
 			}
 		}
 	}
 
 	private void prepareComponentType(
 			MappingDocument sourceDocument,
 			String fullRole,
 			Component componentBinding,
 			String explicitComponentClassName,
 			String containingClassName,
 			String propertyName,
 			boolean isVirtual,
 			boolean isDynamic) {
 	}
 
 	private void bindAllCompositeAttributes(
 			MappingDocument sourceDocument,
 			EmbeddableSource embeddableSource,
 			Component component) {
 
 		for ( AttributeSource attributeSource : embeddableSource.attributeSources() ) {
 			Property attribute = null;
 
 			if ( SingularAttributeSourceBasic.class.isInstance( attributeSource ) ) {
 				attribute = createBasicAttribute(
 						sourceDocument,
 						(SingularAttributeSourceBasic) attributeSource,
 						new SimpleValue( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 				attribute = createEmbeddedAttribute(
 						sourceDocument,
 						(SingularAttributeSourceEmbedded) attributeSource,
 						new Component( sourceDocument.getMetadataCollector(), component ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceManyToOne.class.isInstance( attributeSource ) ) {
 				attribute = createManyToOneAttribute(
 						sourceDocument,
 						(SingularAttributeSourceManyToOne) attributeSource,
 						new ManyToOne( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceOneToOne.class.isInstance( attributeSource ) ) {
 				attribute = createOneToOneAttribute(
 						sourceDocument,
 						(SingularAttributeSourceOneToOne) attributeSource,
 						new OneToOne( sourceDocument.getMetadataCollector(), component.getTable(), component.getOwner() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 				attribute = createAnyAssociationAttribute(
 						sourceDocument,
 						(SingularAttributeSourceAny) attributeSource,
 						new Any( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( PluralAttributeSource.class.isInstance( attributeSource ) ) {
 				attribute = createPluralAttribute(
 						sourceDocument,
 						(PluralAttributeSource) attributeSource,
 						component.getOwner()
 				);
 			}
 			else {
 				throw new AssertionFailure(
 						String.format(
 								Locale.ENGLISH,
 								"Unexpected AttributeSource sub-type [%s] as part of composite [%s]",
 								attributeSource.getClass().getName(),
 								attributeSource.getAttributeRole().getFullPath()
 						)
 
 				);
 			}
 
 			component.addProperty( attribute );
 		}
 	}
 
 	private static void bindSimpleValueType(
 			MappingDocument mappingDocument,
 			HibernateTypeSource typeSource,
 			SimpleValue simpleValue) {
 		if ( mappingDocument.getBuildingOptions().useNationalizedCharacterData() ) {
 			simpleValue.makeNationalized();
 		}
 
 		final TypeResolution typeResolution = resolveType( mappingDocument, typeSource );
 		if ( typeResolution == null ) {
 			// no explicit type info was found
 			return;
 		}
 
 		if ( CollectionHelper.isNotEmpty( typeResolution.parameters ) ) {
 			simpleValue.setTypeParameters( typeResolution.parameters );
 		}
 
 		if ( typeResolution.typeName != null ) {
 			simpleValue.setTypeName( typeResolution.typeName );
 		}
 	}
 
 	private static class TypeResolution {
 		private final String typeName;
 		private final Properties parameters;
 
 		public TypeResolution(String typeName, Properties parameters) {
 			this.typeName = typeName;
 			this.parameters = parameters;
 		}
 	}
 
 	private static TypeResolution resolveType(
 			MappingDocument sourceDocument,
 			HibernateTypeSource typeSource) {
 		if ( StringHelper.isEmpty( typeSource.getName() ) ) {
 			return null;
 		}
 
 		String typeName = typeSource.getName();
 		Properties typeParameters = new Properties();;
 
 		final TypeDefinition typeDefinition = sourceDocument.getMetadataCollector().getTypeDefinition( typeName );
 		if ( typeDefinition != null ) {
 			// the explicit name referred to a type-def
 			typeName = typeDefinition.getTypeImplementorClass().getName();
 			if ( typeDefinition.getParameters() != null ) {
 				typeParameters.putAll( typeDefinition.getParameters() );
 			}
 		}
 //		else {
 //			final BasicType basicType = sourceDocument.getMetadataCollector().getTypeResolver().basic( typeName );
 //			if ( basicType == null ) {
 //				throw new MappingException(
 //						String.format(
 //								Locale.ENGLISH,
 //								"Mapping named an explicit type [%s] which could not be resolved",
 //								typeName
 //						),
 //						sourceDocument.getOrigin()
 //				);
 //			}
 //		}
 
 		// parameters on the property mapping should override parameters in the type-def
 		if ( typeSource.getParameters() != null ) {
 			typeParameters.putAll( typeSource.getParameters() );
 		}
 
 		return new TypeResolution( typeName, typeParameters );
 	}
 
 	private Table bindEntityTableSpecification(
 			final MappingDocument mappingDocument,
 			TableSpecificationSource tableSpecSource,
 			Table denormalizedSuperTable,
 			final EntitySource entitySource,
 			PersistentClass entityDescriptor) {
 		final Namespace namespace = database.locateNamespace(
 				determineCatalogName( tableSpecSource ),
 				determineSchemaName( tableSpecSource )
 		);
 
 		final boolean isTable = TableSource.class.isInstance( tableSpecSource );
 		final boolean isAbstract = entityDescriptor.isAbstract() == null ? false : entityDescriptor.isAbstract();
 		final String subselect;
 		final Identifier logicalTableName;
 		final Table table;
 		if ( isTable ) {
 			final TableSource tableSource = (TableSource) tableSpecSource;
 
 			if ( StringHelper.isNotEmpty( tableSource.getExplicitTableName() ) ) {
 				logicalTableName = database.toIdentifier( tableSource.getExplicitTableName() );
 			}
 			else {
 				final ImplicitEntityNameSource implicitNamingSource = new ImplicitEntityNameSource() {
 					@Override
 					public EntityNaming getEntityNaming() {
 						return entitySource.getEntityNamingSource();
 					}
 
 					@Override
 					public MetadataBuildingContext getBuildingContext() {
 						return mappingDocument;
 					}
 				};
 				logicalTableName = mappingDocument.getBuildingOptions()
 						.getImplicitNamingStrategy()
 						.determinePrimaryTableName( implicitNamingSource );
 			}
 
 			if ( denormalizedSuperTable == null ) {
 				table = namespace.createTable( logicalTableName, isAbstract );
 			}
 			else {
 				table = namespace.createDenormalizedTable(
 						logicalTableName,
 						isAbstract,
 						denormalizedSuperTable
 				);
 			}
 		}
 		else {
 			final InLineViewSource inLineViewSource = (InLineViewSource) tableSpecSource;
 			subselect = inLineViewSource.getSelectStatement();
 			logicalTableName = database.toIdentifier( inLineViewSource.getLogicalName() );
 			if ( denormalizedSuperTable == null ) {
 				table = new Table( namespace, subselect, isAbstract );
 			}
 			else {
 				table = new DenormalizedTable( namespace, subselect, isAbstract, denormalizedSuperTable );
 			}
 			table.setName( logicalTableName.render() );
 		}
 
 		EntityTableXref superEntityTableXref = null;
 
 		if ( entitySource.getSuperType() != null ) {
 			//noinspection SuspiciousMethodCalls
 			final String superEntityName = ( (EntitySource) entitySource.getSuperType() ).getEntityNamingSource()
 					.getEntityName();
 			superEntityTableXref = mappingDocument.getMetadataCollector().getEntityTableXref( superEntityName );
 			if ( superEntityTableXref == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Unable to locate entity table xref for entity [%s] super-type [%s]",
 								entityDescriptor.getEntityName(),
 								superEntityName
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 		}
 
 		mappingDocument.getMetadataCollector().addEntityTableXref(
 				entitySource.getEntityNamingSource().getEntityName(),
 				logicalTableName,
 				table,
 				superEntityTableXref
 		);
 
 		if ( isTable ) {
 			final TableSource tableSource = (TableSource) tableSpecSource;
 			table.setRowId( tableSource.getRowId() );
 			table.setComment( tableSource.getComment() );
 			if ( StringHelper.isNotEmpty( tableSource.getCheckConstraint() ) ) {
 				table.addCheckConstraint( tableSource.getCheckConstraint() );
 			}
 		}
 
 		mappingDocument.getMetadataCollector().addTableNameBinding( logicalTableName, table );
 
 		return table;
 	}
 
 	private Identifier determineCatalogName(TableSpecificationSource tableSpecSource) {
 		if ( StringHelper.isNotEmpty( tableSpecSource.getExplicitCatalogName() ) ) {
 			return database.toIdentifier( tableSpecSource.getExplicitCatalogName() );
 		}
 		else {
 			return database.getDefaultNamespace().getName().getCatalog();
 		}
 	}
 
 	private Identifier determineSchemaName(TableSpecificationSource tableSpecSource) {
 		if ( StringHelper.isNotEmpty( tableSpecSource.getExplicitSchemaName() ) ) {
 			return database.toIdentifier( tableSpecSource.getExplicitSchemaName() );
 		}
 		else {
 			return database.getDefaultNamespace().getName().getSchema();
 		}
 	}
 
 	private static void bindCustomSql(
 			MappingDocument sourceDocument,
 			EntitySource entitySource,
 			PersistentClass entityDescriptor) {
 		if ( entitySource.getCustomSqlInsert() != null ) {
 			entityDescriptor.setCustomSQLInsert(
 					entitySource.getCustomSqlInsert().getSql(),
 					entitySource.getCustomSqlInsert().isCallable(),
 					entitySource.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 
 		if ( entitySource.getCustomSqlUpdate() != null ) {
 			entityDescriptor.setCustomSQLUpdate(
 					entitySource.getCustomSqlUpdate().getSql(),
 					entitySource.getCustomSqlUpdate().isCallable(),
 					entitySource.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 
 		if ( entitySource.getCustomSqlDelete() != null ) {
 			entityDescriptor.setCustomSQLDelete(
 					entitySource.getCustomSqlDelete().getSql(),
 					entitySource.getCustomSqlDelete().isCallable(),
 					entitySource.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 
 		entityDescriptor.setLoaderName( entitySource.getCustomLoaderName() );
 	}
 
 	private static void bindCustomSql(
 			MappingDocument sourceDocument,
 			SecondaryTableSource secondaryTableSource,
 			Join secondaryTable) {
 		if ( secondaryTableSource.getCustomSqlInsert() != null ) {
 			secondaryTable.setCustomSQLInsert(
 					secondaryTableSource.getCustomSqlInsert().getSql(),
 					secondaryTableSource.getCustomSqlInsert().isCallable(),
 					secondaryTableSource.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 
 		if ( secondaryTableSource.getCustomSqlUpdate() != null ) {
 			secondaryTable.setCustomSQLUpdate(
 					secondaryTableSource.getCustomSqlUpdate().getSql(),
 					secondaryTableSource.getCustomSqlUpdate().isCallable(),
 					secondaryTableSource.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 
 		if ( secondaryTableSource.getCustomSqlDelete() != null ) {
 			secondaryTable.setCustomSQLDelete(
 					secondaryTableSource.getCustomSqlDelete().getSql(),
 					secondaryTableSource.getCustomSqlDelete().isCallable(),
 					secondaryTableSource.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 	}
 
 	private void registerSecondPass(SecondPass secondPass, MetadataBuildingContext context) {
 		context.getMetadataCollector().addSecondPass( secondPass );
 	}
 
 
 
 	public static final class DelayedPropertyReferenceHandlerImpl implements InFlightMetadataCollector.DelayedPropertyReferenceHandler {
 		public final String referencedEntityName;
 		public final String referencedPropertyName;
 		public final boolean isUnique;
 		private final String sourceElementSynopsis;
 		public final Origin propertyRefOrigin;
 
 		public DelayedPropertyReferenceHandlerImpl(
 				String referencedEntityName,
 				String referencedPropertyName,
 				boolean isUnique,
 				String sourceElementSynopsis,
 				Origin propertyRefOrigin) {
 			this.referencedEntityName = referencedEntityName;
 			this.referencedPropertyName = referencedPropertyName;
 			this.isUnique = isUnique;
 			this.sourceElementSynopsis = sourceElementSynopsis;
 			this.propertyRefOrigin = propertyRefOrigin;
 		}
 
 		public void process(InFlightMetadataCollector metadataCollector) {
 			log.tracef(
 					"Performing delayed property-ref handling [%s, %s, %s]",
 					referencedEntityName,
 					referencedPropertyName,
 					sourceElementSynopsis
 			);
 
 			PersistentClass entityBinding = metadataCollector.getEntityBinding( referencedEntityName );
 			if ( entityBinding == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"property-ref [%s] referenced an unmapped entity [%s]",
 								sourceElementSynopsis,
 								referencedEntityName
 						),
 						propertyRefOrigin
 				);
 			}
 
 			Property propertyBinding = entityBinding.getReferencedProperty( referencedPropertyName );
 			if ( propertyBinding == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"property-ref [%s] referenced an unknown entity property [%s#%s]",
 								sourceElementSynopsis,
 								referencedEntityName,
 								referencedPropertyName
 						),
 						propertyRefOrigin
 				);
 			}
 
 			if ( isUnique ) {
 				( (SimpleValue) propertyBinding.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 	}
 
 
 	private abstract class AbstractPluralAttributeSecondPass implements SecondPass {
 		private final MappingDocument mappingDocument;
 		private final PluralAttributeSource pluralAttributeSource;
 		private final Collection collectionBinding;
 
 		protected AbstractPluralAttributeSecondPass(
 				MappingDocument mappingDocument,
 				PluralAttributeSource pluralAttributeSource,
 				Collection collectionBinding) {
 			this.mappingDocument = mappingDocument;
 			this.pluralAttributeSource = pluralAttributeSource;
 			this.collectionBinding = collectionBinding;
 		}
 
 		public MappingDocument getMappingDocument() {
 			return mappingDocument;
 		}
 
 		public PluralAttributeSource getPluralAttributeSource() {
 			return pluralAttributeSource;
 		}
 
 		public Collection getCollectionBinding() {
 			return collectionBinding;
 		}
 
 		@Override
 		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
 			bindCollectionTable();
 
 			bindCollectionKey();
 			bindCollectionIdentifier();
 			bindCollectionIndex();
 			bindCollectionElement();
 
 			createBackReferences();
 
 			collectionBinding.createAllKeys();
 
 			if ( debugEnabled ) {
 				log.debugf( "Mapped collection : " + getPluralAttributeSource().getAttributeRole().getFullPath() );
 				log.debugf( "   + table -> " + getCollectionBinding().getTable().getName() );
 				log.debugf( "   + key -> " + columns( getCollectionBinding().getKey() ) );
 				if ( getCollectionBinding().isIndexed() ) {
 					log.debugf( "   + index -> " + columns( ( (IndexedCollection) getCollectionBinding() ).getIndex() ) );
 				}
 				if ( getCollectionBinding().isOneToMany() ) {
 					log.debugf( "   + one-to-many -> " + ( (OneToMany) getCollectionBinding().getElement() ).getReferencedEntityName() );
 				}
 				else {
 					log.debugf( "   + element -> " + columns( getCollectionBinding().getElement() ) );
 				}
 			}
 		}
 
 		private String columns(Value value) {
 			final StringBuilder builder = new StringBuilder();
 			final Iterator<Selectable> selectableItr = value.getColumnIterator();
 			while ( selectableItr.hasNext() ) {
 				builder.append( selectableItr.next().getText() );
 				if ( selectableItr.hasNext() ) {
 					builder.append( ", " );
 				}
 			}
 			return builder.toString();
 		}
 
 		private void bindCollectionTable() {
 			// 2 main branches here:
 			//		1) one-to-many
 			//		2) everything else
 
 			if ( pluralAttributeSource.getElementSource() instanceof PluralAttributeElementSourceOneToMany ) {
 				// For one-to-many mappings, the "collection table" is the same as the table
 				// of the associated entity (the entity making up the collection elements).
 				// So lookup the associated entity and use its table here
 
 				final PluralAttributeElementSourceOneToMany elementSource =
 						(PluralAttributeElementSourceOneToMany) pluralAttributeSource.getElementSource();
 
 				final PersistentClass persistentClass = mappingDocument.getMetadataCollector()
 						.getEntityBinding( elementSource.getReferencedEntityName() );
 				if ( persistentClass == null ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Association [%s] references an unmapped entity [%s]",
 									pluralAttributeSource.getAttributeRole().getFullPath(),
 									pluralAttributeSource.getAttributeRole().getFullPath()
 							),
 							mappingDocument.getOrigin()
 					);
 				}
 
 				// even though <key/> defines a property-ref I do not see where legacy
 				// code ever attempts to use that to "adjust" the table in its use to
 				// the actual table the referenced property belongs to.
 				// todo : for correctness, though, we should look into that ^^
 				collectionBinding.setCollectionTable( persistentClass.getTable() );
 			}
 			else {
 				final TableSpecificationSource tableSpecSource = pluralAttributeSource.getCollectionTableSpecificationSource();
 				final Identifier logicalCatalogName = determineCatalogName( tableSpecSource );
 				final Identifier logicalSchemaName = determineSchemaName( tableSpecSource );
 				final Namespace namespace = database.locateNamespace( logicalCatalogName, logicalSchemaName );
 
 				final Table collectionTable;
 
 				if ( tableSpecSource instanceof TableSource ) {
 					final TableSource tableSource = (TableSource) tableSpecSource;
 					Identifier logicalName;
 
 					if ( StringHelper.isNotEmpty( tableSource.getExplicitTableName() ) ) {
 						logicalName = Identifier.toIdentifier(
 								tableSource.getExplicitTableName(),
 								mappingDocument.getMappingDefaults().shouldImplicitlyQuoteIdentifiers()
 						);
 					}
 					else {
 						final EntityNaming ownerEntityNaming = new EntityNamingSourceImpl(
 								collectionBinding.getOwner().getEntityName(),
 								collectionBinding.getOwner().getClassName(),
 								collectionBinding.getOwner().getJpaEntityName()
 						);
 						final ImplicitCollectionTableNameSource implicitNamingSource = new ImplicitCollectionTableNameSource() {
 							@Override
 							public Identifier getOwningPhysicalTableName() {
 								return collectionBinding.getOwner().getTable().getNameIdentifier();
 							}
 
 							@Override
 							public EntityNaming getOwningEntityNaming() {
 								return ownerEntityNaming;
 							}
 
 							@Override
 							public AttributePath getOwningAttributePath() {
 								return pluralAttributeSource.getAttributePath();
 							}
 
 							@Override
 							public MetadataBuildingContext getBuildingContext() {
 								return mappingDocument;
 							}
 						};
 						logicalName = mappingDocument.getBuildingOptions()
 								.getImplicitNamingStrategy()
 								.determineCollectionTableName( implicitNamingSource );
 					}
 
 					collectionTable = namespace.createTable( logicalName, false );
 				}
 				else {
 					collectionTable = new Table(
 							namespace,
 							( (InLineViewSource) tableSpecSource ).getSelectStatement(),
 							false
 					);
 				}
 
 				collectionBinding.setCollectionTable( collectionTable );
 			}
 
 
 			if ( debugEnabled ) {
 				log.debugf( "Mapping collection: %s -> %s", collectionBinding.getRole(), collectionBinding.getCollectionTable().getName() );
 			}
 
 			if ( pluralAttributeSource.getCollectionTableComment() != null ) {
 				collectionBinding.getCollectionTable().setComment( pluralAttributeSource.getCollectionTableComment() );
 			}
 			if ( pluralAttributeSource.getCollectionTableCheck() != null ) {
 				collectionBinding.getCollectionTable().addCheckConstraint( pluralAttributeSource.getCollectionTableCheck() );
 			}
 		}
 
 		protected void createBackReferences() {
 			if ( collectionBinding.isOneToMany()
 					&& !collectionBinding.isInverse()
 					&& !collectionBinding.getKey().isNullable() ) {
 				// for non-inverse one-to-many, with a not-null fk, add a backref!
 				String entityName = ( (OneToMany) collectionBinding.getElement() ).getReferencedEntityName();
 				PersistentClass referenced = mappingDocument.getMetadataCollector().getEntityBinding( entityName );
 				Backref prop = new Backref();
 				prop.setName( '_' + collectionBinding.getOwnerEntityName() + "." + pluralAttributeSource.getName() + "Backref" );
 				prop.setUpdateable( false );
 				prop.setSelectable( false );
 				prop.setCollectionRole( collectionBinding.getRole() );
 				prop.setEntityName( collectionBinding.getOwner().getEntityName() );
 				prop.setValue( collectionBinding.getKey() );
 				referenced.addProperty( prop );
 
 				log.debugf(
 						"Added virtual backref property [%s] : %s",
 						prop.getName(),
 						pluralAttributeSource.getAttributeRole().getFullPath()
 				);
 			}
 		}
 
 		protected void bindCollectionKey() {
 			final String propRef = getPluralAttributeSource().getKeySource().getReferencedPropertyName();
 			getCollectionBinding().setReferencedPropertyName( propRef );
 
 			final KeyValue keyVal;
 			if ( propRef == null ) {
 				keyVal = getCollectionBinding().getOwner().getIdentifier();
 			}
 			else {
 				keyVal = (KeyValue) getCollectionBinding().getOwner().getRecursiveProperty( propRef ).getValue();
 			}
 			final DependantValue key = new DependantValue(
 					mappingDocument.getMetadataCollector(),
 					getCollectionBinding().getCollectionTable(),
 					keyVal
 			);
 			key.setCascadeDeleteEnabled( getPluralAttributeSource().getKeySource().isCascadeDeleteEnabled() );
 
 			final ImplicitJoinColumnNameSource.Nature implicitNamingNature;
 			if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToMany
 					|| getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceOneToMany ) {
 				implicitNamingNature = ImplicitJoinColumnNameSource.Nature.ENTITY_COLLECTION;
 			}
 			else {
 				implicitNamingNature = ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION;
 			}
 
 			relationalObjectBinder.bindColumnsAndFormulas(
 					mappingDocument,
 					getPluralAttributeSource().getKeySource().getRelationalValueSources(),
 					key,
 					getPluralAttributeSource().getKeySource().areValuesNullableByDefault(),
 					new RelationalObjectBinder.ColumnNamingDelegate() {
 						@Override
 						public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 							// another case where HbmBinder was not adjusted to make use of NamingStrategy#foreignKeyColumnName
 							// when that was added in developing annotation binding :(
 //							return implicitNamingStrategy.determineJoinColumnName(
 //									new ImplicitJoinColumnNameSource() {
 //										private EntityNamingSourceImpl entityNamingSource;
 //										private Identifier referencedColumnName;
 //
 //										@Override
 //										public Nature getNature() {
 //											return implicitNamingNature;
 //										}
 //
 //										@Override
 //										public EntityNaming getEntityNaming() {
 //											if ( entityNamingSource == null ) {
 //												entityNamingSource = new EntityNamingSourceImpl(
 //														getCollectionBinding().getOwner().getEntityName(),
 //														getCollectionBinding().getOwner().getClassName(),
 //														getCollectionBinding().getOwner().getJpaEntityName()
 //												);
 //											}
 //											return entityNamingSource;
 //										}
 //
 //										@Override
 //										public AttributePath getAttributePath() {
 //											return getPluralAttributeSource().getAttributePath();
 //										}
 //
 //										@Override
 //										public Identifier getReferencedTableName() {
 //											return getCollectionBinding().getCollectionTable().getNameIdentifier();
 //										}
 //
 //										@Override
 //										public Identifier getReferencedColumnName() {
 //											if ( referencedColumnName == null ) {
 //												final Iterator<Selectable> selectableItr = keyVal.getColumnIterator();
 //												// assume there is just one, and that its a column...
 //												final Column column = (Column) selectableItr.next();
 //												referencedColumnName = getMappingDocument().getMetadataCollector()
 //														.getDatabase()
 //														.toIdentifier( column.getQuotedName() );
 //											}
 //											return referencedColumnName;
 //										}
 //
 //										@Override
 //										public MetadataBuildingContext getBuildingContext() {
 //											return context;
 //										}
 //									}
 //							);
 							return context.getMetadataCollector().getDatabase().toIdentifier( Collection.DEFAULT_KEY_COLUMN_NAME );
 						}
 					}
 			);
 
 			key.createForeignKey();
 			getCollectionBinding().setKey( key );
 
 			key.setNullable( getPluralAttributeSource().getKeySource().areValuesNullableByDefault() );
 			key.setUpdateable( getPluralAttributeSource().getKeySource().areValuesIncludedInUpdateByDefault() );
 		}
 
 		protected void bindCollectionIdentifier() {
 			final CollectionIdSource idSource = getPluralAttributeSource().getCollectionIdSource();
 			if ( idSource != null ) {
 				final IdentifierCollection idBagBinding = (IdentifierCollection) getCollectionBinding();
 				final SimpleValue idBinding = new SimpleValue(
 						mappingDocument.getMetadataCollector(),
 						idBagBinding.getCollectionTable()
 				);
 
 				bindSimpleValueType(
 						mappingDocument,
 						idSource.getTypeInformation(),
 						idBinding
 				);
 
 				relationalObjectBinder.bindColumn(
 						mappingDocument,
 						idSource.getColumnSource(),
 						idBinding,
 						false,
 						new RelationalObjectBinder.ColumnNamingDelegate() {
 							@Override
 							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 								return database.toIdentifier( IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME );
 							}
 						}
 				);
 
 				idBagBinding.setIdentifier( idBinding );
 
 				makeIdentifier(
 						mappingDocument,
 						new IdentifierGeneratorDefinition( idSource.getGeneratorName() ),
 						null,
 						idBinding
 				);
 			}
 		}
 
 		protected void bindCollectionIndex() {
 		}
 
 		protected void bindCollectionElement() {
 			if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceBasic ) {
 				final PluralAttributeElementSourceBasic elementSource =
 						(PluralAttributeElementSourceBasic) getPluralAttributeSource().getElementSource();
 				final SimpleValue elementBinding = new SimpleValue(
 						getMappingDocument().getMetadataCollector(),
 						getCollectionBinding().getCollectionTable()
 				);
 
 				bindSimpleValueType(
 						getMappingDocument(),
 						elementSource.getExplicitHibernateTypeSource(),
 						elementBinding
 				);
 
 				relationalObjectBinder.bindColumnsAndFormulas(
 						mappingDocument,
 						elementSource.getRelationalValueSources(),
 						elementBinding,
 						elementSource.areValuesNullableByDefault(),
 						new RelationalObjectBinder.ColumnNamingDelegate() {
 							@Override
 							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 //								return implicitNamingStrategy.determineBasicColumnName(
 //										elementSource
 //								);
 								return context.getMetadataCollector().getDatabase().toIdentifier( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 							}
 						}
 				);
 
 				getCollectionBinding().setElement( elementBinding );
 			}
 			else if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceEmbedded ) {
 				final PluralAttributeElementSourceEmbedded elementSource =
 						(PluralAttributeElementSourceEmbedded) getPluralAttributeSource().getElementSource();
 				final Component elementBinding = new Component(
 						getMappingDocument().getMetadataCollector(),
 						getCollectionBinding()
 				);
 
 				final EmbeddableSource embeddableSource = elementSource.getEmbeddableSource();
 				bindComponent(
 						mappingDocument,
 						embeddableSource,
 						elementBinding,
 						null,
 						embeddableSource.getAttributePathBase().getProperty(),
 						getPluralAttributeSource().getXmlNodeName(),
 						false
 				);
 
 				getCollectionBinding().setElement( elementBinding );
 			}
 			else if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceOneToMany ) {
 				final PluralAttributeElementSourceOneToMany elementSource =
 						(PluralAttributeElementSourceOneToMany) getPluralAttributeSource().getElementSource();
 				final OneToMany elementBinding = new OneToMany(
 						getMappingDocument().getMetadataCollector(),
 						getCollectionBinding().getOwner()
 				);
 				collectionBinding.setElement( elementBinding );
 
 				final PersistentClass referencedEntityBinding = mappingDocument.getMetadataCollector()
 						.getEntityBinding( elementSource.getReferencedEntityName() );
 				elementBinding.setReferencedEntityName( referencedEntityBinding.getEntityName() );
 				elementBinding.setAssociatedClass( referencedEntityBinding );
 
 				elementBinding.setIgnoreNotFound( elementSource.isIgnoreNotFound() );
 			}
 			else if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToMany ) {
 				final PluralAttributeElementSourceManyToMany elementSource =
 						(PluralAttributeElementSourceManyToMany) getPluralAttributeSource().getElementSource();
 				final ManyToOne elementBinding = new ManyToOne(
 						getMappingDocument().getMetadataCollector(),
 						getCollectionBinding().getCollectionTable()
 				);
 
 				relationalObjectBinder.bindColumnsAndFormulas(
 						getMappingDocument(),
 						elementSource.getRelationalValueSources(),
 						elementBinding,
 						false,
 						new RelationalObjectBinder.ColumnNamingDelegate() {
 							@Override
 							public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 //								return implicitNamingStrategy.determineJoinColumnName(
 //										new ImplicitJoinColumnNameSource() {
 //											private final PersistentClass pc = mappingDocument.getMetadataCollector()
 //													.getEntityBinding( elementSource.getReferencedEntityName() );
 //											private final EntityNaming referencedEntityNaming = new EntityNamingSourceImpl(
 //													pc
 //											);
 //											private Identifier referencedTableName;
 //											private Identifier referencedColumnName;
 //
 //											@Override
 //											public Nature getNature() {
 //												return Nature.ENTITY_COLLECTION;
 //											}
 //
 //											@Override
 //											public EntityNaming getEntityNaming() {
 //												return referencedEntityNaming;
 //											}
 //
 //											@Override
 //											public AttributePath getAttributePath() {
 //												// this is the mapped-by attribute, which we do not
 //												// know here
 //												return null;
 //											}
 //
 //											@Override
 //											public Identifier getReferencedTableName() {
 //												if ( referencedTableName == null ) {
 //													resolveTableAndColumn();
 //												}
 //												return referencedTableName;
 //											}
 //
 //											private void resolveTableAndColumn() {
 //												final Iterator itr;
 //
 //												if ( elementSource.getReferencedEntityAttributeName() == null ) {
 //													// refers to PK
 //													referencedTableName = pc.getIdentifier()
 //															.getTable()
 //															.getNameIdentifier();
 //													itr = pc.getIdentifier().getColumnIterator();
 //												}
 //												else {
 //													// refers to an attribute's column(s)
 //													final Property referencedAttribute = pc.getProperty( elementSource.getReferencedEntityAttributeName() );
 //													referencedTableName = referencedAttribute.getValue()
 //															.getTable()
 //															.getNameIdentifier();
 //													itr = referencedAttribute.getValue().getColumnIterator();
 //												}
 //
 //												// assume one and only one...
 //												referencedColumnName = context.getMetadataCollector()
 //														.getDatabase()
 //														.getJdbcEnvironment()
 //														.getIdentifierHelper()
 //														.toIdentifier( ( (Column) itr.next() ).getQuotedName() );
 //											}
 //
 //											@Override
 //											public Identifier getReferencedColumnName() {
 //												if ( referencedColumnName == null ) {
 //													resolveTableAndColumn();
 //												}
 //												return referencedColumnName;
 //											}
 //
 //											@Override
 //											public MetadataBuildingContext getBuildingContext() {
 //												return context;
 //											}
 //										}
 //								);
 								return context.getMetadataCollector().getDatabase().toIdentifier( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 							}
 						}
 				);
 
 				elementBinding.setLazy( elementSource.getFetchCharacteristics().getFetchTiming() != FetchTiming.IMMEDIATE );
 				elementBinding.setFetchMode(
 						elementSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT
 								? FetchMode.SELECT
 								: FetchMode.JOIN
 				);
 
 
 				elementBinding.setReferencedEntityName( elementSource.getReferencedEntityName() );
 				if ( StringHelper.isNotEmpty( elementSource.getReferencedEntityAttributeName() ) ) {
 					elementBinding.setReferencedPropertyName( elementSource.getReferencedEntityAttributeName() );
 					elementBinding.setReferenceToPrimaryKey( false );
 				}
 				else {
 					elementBinding.setReferenceToPrimaryKey( true );
 				}
 
 				getCollectionBinding().setElement( elementBinding );
 
 				getCollectionBinding().setManyToManyWhere( elementSource.getWhere() );
 				getCollectionBinding().setManyToManyOrdering( elementSource.getOrder() );
 
 				if ( !CollectionHelper.isEmpty( elementSource.getFilterSources() )
 						|| elementSource.getWhere() != null ) {
 					if ( getCollectionBinding().getFetchMode() == FetchMode.JOIN
 							&& elementBinding.getFetchMode() != FetchMode.JOIN ) {
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"many-to-many defining filter or where without join fetching is not " +
 												"valid within collection [%s] using join fetching",
 										getPluralAttributeSource().getAttributeRole().getFullPath()
 								),
 								getMappingDocument().getOrigin()
 						);
 					}
 				}
 
 				for ( FilterSource filterSource : elementSource.getFilterSources() ) {
 					if ( filterSource.getName() == null ) {
 						log.debugf(
 								"Encountered filter with no name associated with many-to-many [%s]; skipping",
 								getPluralAttributeSource().getAttributeRole().getFullPath()
 						);
 						continue;
 					}
 
 					if ( filterSource.getCondition() == null ) {
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"No filter condition found for filter [%s] associated with many-to-many [%s]",
 										filterSource.getName(),
 										getPluralAttributeSource().getAttributeRole().getFullPath()
 								),
 								getMappingDocument().getOrigin()
 						);
 					}
 
 					if ( debugEnabled ) {
 						log.debugf(
 								"Applying many-to-many filter [%s] as [%s] to collection [%s]",
 								filterSource.getName(),
 								filterSource.getCondition(),
 								getPluralAttributeSource().getAttributeRole().getFullPath()
 						);
 					}
 
 					getCollectionBinding().addManyToManyFilter(
 							filterSource.getName(),
 							filterSource.getCondition(),
 							filterSource.shouldAutoInjectAliases(),
 							filterSource.getAliasToTableMap(),
 							filterSource.getAliasToEntityMap()
 					);
 				}
 			}
 			else if ( getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToAny ) {
 				final PluralAttributeElementSourceManyToAny elementSource =
 						(PluralAttributeElementSourceManyToAny) getPluralAttributeSource().getElementSource();
 				final Any elementBinding = new Any(
 						getMappingDocument().getMetadataCollector(),
 						getCollectionBinding().getCollectionTable()
 				);
 				bindAny(
 						mappingDocument,
 						elementSource,
 						elementBinding,
 						getPluralAttributeSource().getAttributeRole().append( "element" ),
 						getPluralAttributeSource().getAttributePath().append( "element" )
 
 				);
 				getCollectionBinding().setElement( elementBinding );
 			}
 		}
 	}
 
 	private class PluralAttributeListSecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeListSecondPass(
 				MappingDocument sourceDocument,
 				IndexedPluralAttributeSource attributeSource,
 				org.hibernate.mapping.List collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 
 		@Override
 		public IndexedPluralAttributeSource getPluralAttributeSource() {
 			return (IndexedPluralAttributeSource) super.getPluralAttributeSource();
 		}
 
 		@Override
 		public org.hibernate.mapping.List getCollectionBinding() {
 			return (org.hibernate.mapping.List) super.getCollectionBinding();
 		}
 
 		@Override
 		protected void bindCollectionIndex() {
 			bindListOrArrayIndex(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 
 		@Override
 		protected void createBackReferences() {
 			super.createBackReferences();
 
 			createIndexBackRef(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 	}
 
 	private class PluralAttributeSetSecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeSetSecondPass(
 				MappingDocument sourceDocument,
 				PluralAttributeSource attributeSource,
 				Collection collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 	}
 
 	private class PluralAttributeMapSecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeMapSecondPass(
 				MappingDocument sourceDocument,
 				IndexedPluralAttributeSource attributeSource,
 				org.hibernate.mapping.Map collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 
 		@Override
 		public IndexedPluralAttributeSource getPluralAttributeSource() {
 			return (IndexedPluralAttributeSource) super.getPluralAttributeSource();
 		}
 
 		@Override
 		public org.hibernate.mapping.Map getCollectionBinding() {
 			return (org.hibernate.mapping.Map) super.getCollectionBinding();
 		}
 
 		@Override
 		protected void bindCollectionIndex() {
 			bindMapKey(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 
 		@Override
 		protected void createBackReferences() {
 			super.createBackReferences();
 
 			boolean indexIsFormula = false;
 			Iterator itr = getCollectionBinding().getIndex().getColumnIterator();
 			while ( itr.hasNext() ) {
 				if ( ( (Selectable) itr.next() ).isFormula() ) {
 					indexIsFormula = true;
 				}
 			}
 
 			if ( getCollectionBinding().isOneToMany()
 					&& !getCollectionBinding().getKey().isNullable()
 					&& !getCollectionBinding().isInverse()
 					&& !indexIsFormula ) {
 				final String entityName = ( (OneToMany) getCollectionBinding().getElement() ).getReferencedEntityName();
 				final PersistentClass referenced = getMappingDocument().getMetadataCollector().getEntityBinding( entityName );
 				final IndexBackref ib = new IndexBackref();
 				ib.setName( '_' + getCollectionBinding().getOwnerEntityName() + "." + getPluralAttributeSource().getName() + "IndexBackref" );
 				ib.setUpdateable( false );
 				ib.setSelectable( false );
 				ib.setCollectionRole( getCollectionBinding().getRole() );
 				ib.setEntityName( getCollectionBinding().getOwner().getEntityName() );
 				ib.setValue( getCollectionBinding().getIndex() );
 				referenced.addProperty( ib );
 			}
 		}
 	}
 
 	private class PluralAttributeBagSecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeBagSecondPass(
 				MappingDocument sourceDocument,
 				PluralAttributeSource attributeSource,
 				Collection collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 	}
 
 	private class PluralAttributeIdBagSecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeIdBagSecondPass(
 				MappingDocument sourceDocument,
 				PluralAttributeSource attributeSource,
 				Collection collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 	}
 
 	private class PluralAttributeArraySecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributeArraySecondPass(
 				MappingDocument sourceDocument,
 				IndexedPluralAttributeSource attributeSource,
 				Array collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 
 		@Override
 		public IndexedPluralAttributeSource getPluralAttributeSource() {
 			return (IndexedPluralAttributeSource) super.getPluralAttributeSource();
 		}
 
 		@Override
 		public Array getCollectionBinding() {
 			return (Array) super.getCollectionBinding();
 		}
 
 		@Override
 		protected void bindCollectionIndex() {
 			bindListOrArrayIndex(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 
 		@Override
 		protected void createBackReferences() {
 			super.createBackReferences();
 
 			createIndexBackRef(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 	}
 
 	private void createIndexBackRef(
 			MappingDocument mappingDocument,
 			IndexedPluralAttributeSource pluralAttributeSource,
 			IndexedCollection collectionBinding) {
 		if ( collectionBinding.isOneToMany()
 				&& !collectionBinding.getKey().isNullable()
 				&& !collectionBinding.isInverse() ) {
 			final String entityName = ( (OneToMany) collectionBinding.getElement() ).getReferencedEntityName();
 			final PersistentClass referenced = mappingDocument.getMetadataCollector().getEntityBinding( entityName );
 			final IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + collectionBinding.getOwnerEntityName() + "." + pluralAttributeSource.getName() + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( collectionBinding.getRole() );
 			ib.setEntityName( collectionBinding.getOwner().getEntityName() );
 			ib.setValue( collectionBinding.getIndex() );
 			referenced.addProperty( ib );
 		}
 	}
 
 	private class PluralAttributePrimitiveArraySecondPass extends AbstractPluralAttributeSecondPass {
 		public PluralAttributePrimitiveArraySecondPass(
 				MappingDocument sourceDocument,
 				IndexedPluralAttributeSource attributeSource,
 				PrimitiveArray collectionBinding) {
 			super( sourceDocument, attributeSource, collectionBinding );
 		}
 
 		@Override
 		public IndexedPluralAttributeSource getPluralAttributeSource() {
 			return (IndexedPluralAttributeSource) super.getPluralAttributeSource();
 		}
 
 		@Override
 		public PrimitiveArray getCollectionBinding() {
 			return (PrimitiveArray) super.getCollectionBinding();
 		}
 
 		@Override
 		protected void bindCollectionIndex() {
 			bindListOrArrayIndex(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 
 		@Override
 		protected void createBackReferences() {
 			super.createBackReferences();
 
 			createIndexBackRef(
 					getMappingDocument(),
 					getPluralAttributeSource(),
 					getCollectionBinding()
 			);
 		}
 	}
 
 	public void bindListOrArrayIndex(
 			MappingDocument mappingDocument,
 			final IndexedPluralAttributeSource attributeSource,
 			org.hibernate.mapping.List collectionBinding) {
 		final PluralAttributeSequentialIndexSource indexSource =
 				(PluralAttributeSequentialIndexSource) attributeSource.getIndexSource();
 
 		final SimpleValue indexBinding = new SimpleValue(
 				mappingDocument.getMetadataCollector(),
 				collectionBinding.getCollectionTable()
 		);
 
 		bindSimpleValueType(
 				mappingDocument,
 				indexSource.getTypeInformation(),
 				indexBinding
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				mappingDocument,
 				indexSource.getRelationalValueSources(),
 				indexBinding,
 				attributeSource.getElementSource() instanceof PluralAttributeElementSourceOneToMany,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 						return context.getBuildingOptions().getImplicitNamingStrategy().determineListIndexColumnName(
 								new ImplicitIndexColumnNameSource() {
 									@Override
 									public AttributePath getPluralAttributePath() {
 										return attributeSource.getAttributePath();
 									}
 
 									@Override
 									public MetadataBuildingContext getBuildingContext() {
 										return context;
 									}
 								}
 						);
 					}
 				}
 		);
 
 		collectionBinding.setIndex( indexBinding );
 		collectionBinding.setBaseIndex( indexSource.getBase() );
-		collectionBinding.setIndexNodeName( indexSource.getXmlNodeName() );
 	}
 
 	private void bindMapKey(
 			final MappingDocument mappingDocument,
 			final IndexedPluralAttributeSource pluralAttributeSource,
 			final org.hibernate.mapping.Map collectionBinding) {
 		if ( pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeySourceBasic ) {
 			final PluralAttributeMapKeySourceBasic mapKeySource =
 					(PluralAttributeMapKeySourceBasic) pluralAttributeSource.getIndexSource();
 			final SimpleValue value = new SimpleValue(
 					mappingDocument.getMetadataCollector(),
 					collectionBinding.getCollectionTable()
 			);
 			bindSimpleValueType(
 					mappingDocument,
 					mapKeySource.getTypeInformation(),
 					value
 			);
 			if ( !value.isTypeSpecified() ) {
 				throw new MappingException(
 						"map index element must specify a type: "
 								+ pluralAttributeSource.getAttributeRole().getFullPath(),
 						mappingDocument.getOrigin()
 				);
 			}
 
 			relationalObjectBinder.bindColumnsAndFormulas(
 					mappingDocument,
 					mapKeySource.getRelationalValueSources(),
 					value,
 					true,
 					new RelationalObjectBinder.ColumnNamingDelegate() {
 						@Override
 						public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 							return database.toIdentifier( IndexedCollection.DEFAULT_INDEX_COLUMN_NAME );
 						}
 					}
 			);
 
 			collectionBinding.setIndex( value );
-			collectionBinding.setIndexNodeName( mapKeySource.getXmlNodeName() );
 		}
 		else if ( pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeySourceEmbedded ) {
 			final PluralAttributeMapKeySourceEmbedded mapKeySource =
 					(PluralAttributeMapKeySourceEmbedded) pluralAttributeSource.getIndexSource();
 			final Component componentBinding = new Component(
 					mappingDocument.getMetadataCollector(),
 					collectionBinding
 			);
 			bindComponent(
 					mappingDocument,
 					mapKeySource.getEmbeddableSource(),
 					componentBinding,
 					null,
 					pluralAttributeSource.getName(),
 					mapKeySource.getXmlNodeName(),
 					false
 			);
 			collectionBinding.setIndex( componentBinding );
 		}
 		else if ( pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeyManyToManySource ) {
 			final PluralAttributeMapKeyManyToManySource mapKeySource =
 					(PluralAttributeMapKeyManyToManySource) pluralAttributeSource.getIndexSource();
 			final ManyToOne mapKeyBinding = new ManyToOne(
 					mappingDocument.getMetadataCollector(),
 					collectionBinding.getCollectionTable()
 			);
 
 			mapKeyBinding.setReferencedEntityName( mapKeySource.getReferencedEntityName() );
 
 			relationalObjectBinder.bindColumnsAndFormulas(
 					mappingDocument,
 					mapKeySource.getRelationalValueSources(),
 					mapKeyBinding,
 					true,
 					new RelationalObjectBinder.ColumnNamingDelegate() {
 						@Override
 						public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 							return implicitNamingStrategy.determineMapKeyColumnName(
 									new ImplicitMapKeyColumnNameSource() {
 										@Override
 										public AttributePath getPluralAttributePath() {
 											return pluralAttributeSource.getAttributePath();
 										}
 
 										@Override
 										public MetadataBuildingContext getBuildingContext() {
 											return context;
 										}
 									}
 							);
 						}
 					}
 			);
 			collectionBinding.setIndex( mapKeyBinding );
 		}
 		else if ( pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeyManyToAnySource ) {
 			final PluralAttributeMapKeyManyToAnySource mapKeySource =
 					(PluralAttributeMapKeyManyToAnySource) pluralAttributeSource.getIndexSource();
 			final Any mapKeyBinding = new Any(
 					mappingDocument.getMetadataCollector(),
 					collectionBinding.getCollectionTable()
 			);
 			bindAny(
 					mappingDocument,
 					mapKeySource,
 					mapKeyBinding,
 					pluralAttributeSource.getAttributeRole().append( "key" ),
 					pluralAttributeSource.getAttributePath().append( "key" )
 			);
 			collectionBinding.setIndex( mapKeyBinding );
 		}
 	}
 
 	private class ManyToOneColumnBinder implements ImplicitColumnNamingSecondPass {
 		private final MappingDocument mappingDocument;
 		private final SingularAttributeSourceManyToOne manyToOneSource;
 		private final ManyToOne manyToOneBinding;
 
 		private final String referencedEntityName;
 
 		private final boolean allColumnsNamed;
 
 		public ManyToOneColumnBinder(
 				MappingDocument mappingDocument,
 				SingularAttributeSourceManyToOne manyToOneSource,
 				ManyToOne manyToOneBinding,
 				String referencedEntityName) {
 			this.mappingDocument = mappingDocument;
 			this.manyToOneSource = manyToOneSource;
 			this.manyToOneBinding = manyToOneBinding;
 			this.referencedEntityName = referencedEntityName;
 
 			boolean allNamed = true;
 			for ( RelationalValueSource relationalValueSource : manyToOneSource.getRelationalValueSources() ) {
 				if ( relationalValueSource instanceof ColumnSource ) {
 					if ( ( (ColumnSource) relationalValueSource ).getName() == null ) {
 						allNamed = false;
 						break;
 					}
 				}
 			}
 			this.allColumnsNamed = allNamed;
 		}
 
 		public boolean canProcessImmediately() {
 			if ( allColumnsNamed ) {
 				return true;
 			}
 
 			final PersistentClass referencedEntityBinding = mappingDocument.getMetadataCollector()
 					.getEntityBinding( referencedEntityName );
 			if ( referencedEntityBinding == null ) {
 				return false;
 			}
 
 			// for implicit naming, we can do it immediately if the associated entity
 			// is bound and the reference is to its PK.  For property-refs, we'd have to
 			// be *sure* that the column(s) for the referenced property is fully bound
 			// and we just cannot know that in today's model
 
 			return manyToOneSource.getReferencedEntityAttributeName() == null;
 		}
 
 		@Override
 		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
 			if ( allColumnsNamed ) {
 				relationalObjectBinder.bindColumnsAndFormulas(
 						mappingDocument,
 						manyToOneSource.getRelationalValueSources(),
 						manyToOneBinding,
 						manyToOneSource.areValuesNullableByDefault(),
 						new RelationalObjectBinder.ColumnNamingDelegate() {
 							@Override
 							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 								throw new AssertionFailure( "Argh!!!" );
 							}
 						}
 				);
 			}
 			else {
 				// Otherwise we have some dependency resolution to do in order to perform
 				// implicit naming.  If we get here, we assume that there is only a single
 				// column making up the FK
 
 				final String referencedEntityAttributeName = manyToOneSource.getReferencedEntityAttributeName();
 
 				final PersistentClass referencedEntityBinding = mappingDocument.getMetadataCollector()
 						.getEntityBinding( referencedEntityName );
 
 				if ( referencedEntityBinding == null ) {
 					throw new AssertionFailure(
 							"Unable to locate referenced entity mapping [" + referencedEntityName +
 									"] in order to process many-to-one FK : " + manyToOneSource.getAttributeRole().getFullPath()
 					);
 				}
 
 				final EntityNaming entityNaming = new EntityNamingSourceImpl( referencedEntityBinding );
 
 				final Identifier referencedTableName;
 				final Identifier referencedColumnName;
 
 				if ( referencedEntityAttributeName == null ) {
 					referencedTableName = referencedEntityBinding.getTable().getNameIdentifier();
 					final Column referencedColumn = referencedEntityBinding.getTable()
 							.getPrimaryKey()
 							.getColumn( 0 );
 					referencedColumnName = mappingDocument.getMetadataCollector()
 							.getDatabase()
 							.getJdbcEnvironment()
 							.getIdentifierHelper()
 							.toIdentifier( referencedColumn.getQuotedName() );
 				}
 				else {
 					final Property referencedProperty = referencedEntityBinding.getReferencedProperty(
 							referencedEntityAttributeName
 					);
 					final SimpleValue value = (SimpleValue) referencedProperty.getValue();
 					referencedTableName = value.getTable().getNameIdentifier();
 					final Column referencedColumn = (Column) value.getColumnIterator().next();
 					referencedColumnName = mappingDocument.getMetadataCollector()
 							.getDatabase()
 							.getJdbcEnvironment()
 							.getIdentifierHelper()
 							.toIdentifier( referencedColumn.getQuotedName() );
 				}
 
 				relationalObjectBinder.bindColumnsAndFormulas(
 						mappingDocument,
 						manyToOneSource.getRelationalValueSources(),
 						manyToOneBinding,
 						manyToOneSource.areValuesNullableByDefault(),
 						new RelationalObjectBinder.ColumnNamingDelegate() {
 							@Override
 							public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
 								// NOTE : This sucks!!!  The problem is that the legacy HBMBinder routed this
 								// through the legacy NamingStrategy#propertyToColumName.
 								//
 								// Basically, when developing the AnnotationBinder and
 								// NamingStrategy#foreignKeyColumnName HbmBinder was never updated to
 								// utilize that new method.
 //								return implicitNamingStrategy.determineJoinColumnName(
 //										new ImplicitJoinColumnNameSource() {
 //											@Override
 //											public Nature getNature() {
 //												return Nature.ENTITY;
 //											}
 //
 //											@Override
 //											public EntityNaming getEntityNaming() {
 //												return entityNaming;
 //											}
 //
 //											@Override
 //											public AttributePath getAttributePath() {
 //												return manyToOneSource.getAttributePath();
 //											}
 //
 //											@Override
 //											public Identifier getReferencedTableName() {
 //												return referencedTableName;
 //											}
 //
 //											@Override
 //											public Identifier getReferencedColumnName() {
 //												return referencedColumnName;
 //											}
 //
 //											@Override
 //											public MetadataBuildingContext getBuildingContext() {
 //												return context;
 //											}
 //										}
 //								);
 								return implicitNamingStrategy.determineBasicColumnName(
 										new ImplicitBasicColumnNameSource() {
 											@Override
 											public AttributePath getAttributePath() {
 												return manyToOneSource.getAttributePath();
 											}
 
 											@Override
 											public boolean isCollectionElement() {
 												return false;
 											}
 
 											@Override
 											public MetadataBuildingContext getBuildingContext() {
 												return context;
 											}
 										}
 								);
 							}
 						}
 				);
 			}
 		}
 	}
 
 	private class ManyToOneFkSecondPass extends FkSecondPass {
 		private final MappingDocument mappingDocument;
 		private final ManyToOne manyToOneBinding;
 
 		private final String referencedEntityName;
 		private final String referencedEntityAttributeName;
 
 
 		public ManyToOneFkSecondPass(
 				MappingDocument mappingDocument,
 				SingularAttributeSourceManyToOne manyToOneSource,
 				ManyToOne manyToOneBinding,
 				String referencedEntityName) {
 			super( manyToOneBinding, null );
 
 			if ( referencedEntityName == null ) {
 				throw new MappingException(
 						"entity name referenced by many-to-one required [" + manyToOneSource.getAttributeRole().getFullPath() + "]",
 						mappingDocument.getOrigin()
 				);
 			}
 			this.mappingDocument = mappingDocument;
 			this.manyToOneBinding = manyToOneBinding;
 			this.referencedEntityName = referencedEntityName;
 
 			this.referencedEntityAttributeName = manyToOneSource.getReferencedEntityAttributeName();
 		}
 
 		@Override
 		public String getReferencedEntityName() {
 			return referencedEntityName;
 		}
 
 		@Override
 		public boolean isInPrimaryKey() {
 			return false;
 		}
 
 		@Override
 		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
 			if ( referencedEntityAttributeName == null ) {
 				manyToOneBinding.createForeignKey();
 			}
 			else {
 				manyToOneBinding.createPropertyRefConstraints( mappingDocument.getMetadataCollector().getEntityBindingMap() );
 			}
 		}
 
 		public boolean canProcessImmediately() {
 			// We can process the FK immediately if it is a reference to the associated
 			// entity's PK.
 			//
 			// There is an assumption here that the columns making up the FK have been bound.
 			// We assume the caller checks that
 			final PersistentClass referencedEntityBinding = mappingDocument.getMetadataCollector()
 					.getEntityBinding( referencedEntityName );
 			return referencedEntityBinding != null && referencedEntityAttributeName != null;
 
 		}
 	}
 
 	private class NaturalIdUniqueKeyBinderImpl implements NaturalIdUniqueKeyBinder {
 		private final MappingDocument mappingDocument;
 		private final PersistentClass entityBinding;
 		private final List<Property> attributeBindings = new ArrayList<Property>();
 
 		public NaturalIdUniqueKeyBinderImpl(MappingDocument mappingDocument, PersistentClass entityBinding) {
 			this.mappingDocument = mappingDocument;
 			this.entityBinding = entityBinding;
 		}
 
 		@Override
 		public void addAttributeBinding(Property attributeBinding) {
 			attributeBindings.add( attributeBinding );
 		}
 
 		@Override
 		public void process() {
 			log.debugf( "Binding natural-id UniqueKey for entity : " + entityBinding.getEntityName() );
 
 			final List<Identifier> columnNames = new ArrayList<Identifier>();
 
 			final UniqueKey uk = new UniqueKey();
 			uk.setTable( entityBinding.getTable() );
 			for ( Property attributeBinding : attributeBindings ) {
 				final Iterator itr = attributeBinding.getColumnIterator();
 				while ( itr.hasNext() ) {
 					final Object selectable = itr.next();
 					if ( Column.class.isInstance( selectable ) ) {
 						final Column column = (Column) selectable;
 						uk.addColumn( column );
 						columnNames.add(
 								mappingDocument.getMetadataCollector().getDatabase().toIdentifier( column.getQuotedName() )
 						);
 					}
 				}
 				uk.addColumns( attributeBinding.getColumnIterator() );
 			}
 
 			final Identifier ukName = mappingDocument.getBuildingOptions().getImplicitNamingStrategy().determineUniqueKeyName(
 					new ImplicitUniqueKeyNameSource() {
 						@Override
 						public Identifier getTableName() {
 							return entityBinding.getTable().getNameIdentifier();
 						}
 
 						@Override
 						public List<Identifier> getColumnNames() {
 							return columnNames;
 						}
 
 						@Override
 						public MetadataBuildingContext getBuildingContext() {
 							return mappingDocument;
 						}
 					}
 			);
 			uk.setName( ukName.render( mappingDocument.getMetadataCollector().getDatabase().getDialect() ) );
 
 			entityBinding.getTable().addUniqueKey( uk );
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index b07f89ea60..8ae1c7976d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -39,3310 +39,3308 @@ import javax.persistence.Id;
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
 import org.hibernate.annotations.DiscriminatorFormula;
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
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XMethod;
 import org.hibernate.annotations.common.reflection.XPackage;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.spi.InFlightMetadataCollector.EntityTableXref;
 import org.hibernate.boot.spi.MetadataBuildingContext;
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
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.PropertyPath;
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
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 /**
  * JSR 175 annotation binder which reads the annotations from classes, applies the
  * principles of the EJB3 spec and produces the Hibernate configuration-time metamodel
  * (the classes in the {@code org.hibernate.mapping} package)
  * <p/>
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
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public final class AnnotationBinder {
 	private static final CoreMessageLogger LOG = messageLogger( AnnotationBinder.class );
 
 	private AnnotationBinder() {
 	}
 
 	public static void bindDefaults(MetadataBuildingContext context) {
 		Map defaults = context.getBuildingOptions().getReflectionManager().getDefaults();
 
 		// id generators ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<SequenceGenerator> anns = ( List<SequenceGenerator> ) defaults.get( SequenceGenerator.class );
 			if ( anns != null ) {
 				for ( SequenceGenerator ann : anns ) {
 					IdentifierGeneratorDefinition idGen = buildIdGenerator( ann, context );
 					if ( idGen != null ) {
 						context.getMetadataCollector().addDefaultIdentifierGenerator( idGen );
 					}
 				}
 			}
 		}
 		{
 			List<TableGenerator> anns = ( List<TableGenerator> ) defaults.get( TableGenerator.class );
 			if ( anns != null ) {
 				for ( TableGenerator ann : anns ) {
 					IdentifierGeneratorDefinition idGen = buildIdGenerator( ann, context );
 					if ( idGen != null ) {
 						context.getMetadataCollector().addDefaultIdentifierGenerator( idGen );
 					}
 				}
 			}
 		}
 
 		// queries ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<NamedQuery> anns = ( List<NamedQuery> ) defaults.get( NamedQuery.class );
 			if ( anns != null ) {
 				for ( NamedQuery ann : anns ) {
 					QueryBinder.bindQuery( ann, context, true );
 				}
 			}
 		}
 		{
 			List<NamedNativeQuery> anns = ( List<NamedNativeQuery> ) defaults.get( NamedNativeQuery.class );
 			if ( anns != null ) {
 				for ( NamedNativeQuery ann : anns ) {
 					QueryBinder.bindNativeQuery( ann, context, true );
 				}
 			}
 		}
 
 		// result-set-mappings ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			List<SqlResultSetMapping> anns = ( List<SqlResultSetMapping> ) defaults.get( SqlResultSetMapping.class );
 			if ( anns != null ) {
 				for ( SqlResultSetMapping ann : anns ) {
 					QueryBinder.bindSqlResultSetMapping( ann, context, true );
 				}
 			}
 		}
 
 		// stored procs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		{
 			final List<NamedStoredProcedureQuery> annotations =
 					(List<NamedStoredProcedureQuery>) defaults.get( NamedStoredProcedureQuery.class );
 			if ( annotations != null ) {
 				for ( NamedStoredProcedureQuery annotation : annotations ) {
 					bindNamedStoredProcedureQuery( annotation, context, true );
 				}
 			}
 		}
 		{
 			final List<NamedStoredProcedureQueries> annotations =
 					(List<NamedStoredProcedureQueries>) defaults.get( NamedStoredProcedureQueries.class );
 			if ( annotations != null ) {
 				for ( NamedStoredProcedureQueries annotation : annotations ) {
 					bindNamedStoredProcedureQueries( annotation, context, true );
 				}
 			}
 		}
 	}
 
 	public static void bindPackage(String packageName, MetadataBuildingContext context) {
 		XPackage pckg;
 		try {
 			pckg = context.getBuildingOptions().getReflectionManager().packageForName( packageName );
 		}
 		catch (ClassLoadingException e) {
 			LOG.packageNotFound( packageName );
 			return;
 		}
 		catch ( ClassNotFoundException cnf ) {
 			LOG.packageNotFound( packageName );
 			return;
 		}
 
 		if ( pckg.isAnnotationPresent( SequenceGenerator.class ) ) {
 			SequenceGenerator ann = pckg.getAnnotation( SequenceGenerator.class );
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( ann, context );
 			context.getMetadataCollector().addIdentifierGenerator( idGen );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add sequence generator with name: {0}", idGen.getName() );
 			}
 		}
 
 		if ( pckg.isAnnotationPresent( TableGenerator.class ) ) {
 			TableGenerator ann = pckg.getAnnotation( TableGenerator.class );
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( ann, context );
 			context.getMetadataCollector().addIdentifierGenerator( idGen );
 		}
 
 		bindGenericGenerators( pckg, context );
 		bindQueries( pckg, context );
 		bindFilterDefs( pckg, context );
 		bindTypeDefs( pckg, context );
 		bindFetchProfiles( pckg, context );
 		BinderHelper.bindAnyMetaDefs( pckg, context );
 
 	}
 
 	private static void bindGenericGenerators(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		GenericGenerator defAnn = annotatedElement.getAnnotation( GenericGenerator.class );
 		GenericGenerators defsAnn = annotatedElement.getAnnotation( GenericGenerators.class );
 		if ( defAnn != null ) {
 			bindGenericGenerator( defAnn, context );
 		}
 		if ( defsAnn != null ) {
 			for ( GenericGenerator def : defsAnn.value() ) {
 				bindGenericGenerator( def, context );
 			}
 		}
 	}
 
 	private static void bindGenericGenerator(GenericGenerator def, MetadataBuildingContext context) {
 		context.getMetadataCollector().addIdentifierGenerator( buildIdGenerator( def, context ) );
 	}
 
 	private static void bindQueries(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		{
 			SqlResultSetMapping ann = annotatedElement.getAnnotation( SqlResultSetMapping.class );
 			QueryBinder.bindSqlResultSetMapping( ann, context, false );
 		}
 		{
 			SqlResultSetMappings ann = annotatedElement.getAnnotation( SqlResultSetMappings.class );
 			if ( ann != null ) {
 				for ( SqlResultSetMapping current : ann.value() ) {
 					QueryBinder.bindSqlResultSetMapping( current, context, false );
 				}
 			}
 		}
 		{
 			NamedQuery ann = annotatedElement.getAnnotation( NamedQuery.class );
 			QueryBinder.bindQuery( ann, context, false );
 		}
 		{
 			org.hibernate.annotations.NamedQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQuery.class
 			);
 			QueryBinder.bindQuery( ann, context );
 		}
 		{
 			NamedQueries ann = annotatedElement.getAnnotation( NamedQueries.class );
 			QueryBinder.bindQueries( ann, context, false );
 		}
 		{
 			org.hibernate.annotations.NamedQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQueries.class
 			);
 			QueryBinder.bindQueries( ann, context );
 		}
 		{
 			NamedNativeQuery ann = annotatedElement.getAnnotation( NamedNativeQuery.class );
 			QueryBinder.bindNativeQuery( ann, context, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQuery.class
 			);
 			QueryBinder.bindNativeQuery( ann, context );
 		}
 		{
 			NamedNativeQueries ann = annotatedElement.getAnnotation( NamedNativeQueries.class );
 			QueryBinder.bindNativeQueries( ann, context, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQueries.class
 			);
 			QueryBinder.bindNativeQueries( ann, context );
 		}
 
 		// NamedStoredProcedureQuery handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		bindNamedStoredProcedureQuery(
 				annotatedElement.getAnnotation( NamedStoredProcedureQuery.class ),
 				context,
 				false
 		);
 
 		// NamedStoredProcedureQueries handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		bindNamedStoredProcedureQueries(
 				annotatedElement.getAnnotation( NamedStoredProcedureQueries.class ),
 				context,
 				false
 		);
 	}
 
 	private static void bindNamedStoredProcedureQueries(NamedStoredProcedureQueries annotation, MetadataBuildingContext context, boolean isDefault) {
 		if ( annotation != null ) {
 			for ( NamedStoredProcedureQuery queryAnnotation : annotation.value() ) {
 				bindNamedStoredProcedureQuery( queryAnnotation, context, isDefault );
 			}
 		}
 	}
 
 	private static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, MetadataBuildingContext context, boolean isDefault) {
 		if ( annotation != null ) {
 			QueryBinder.bindNamedStoredProcedureQuery( annotation, context, isDefault );
 		}
 	}
 
 	private static IdentifierGeneratorDefinition buildIdGenerator(java.lang.annotation.Annotation ann, MetadataBuildingContext context) {
 		if ( ann == null ) {
 			return null;
 		}
 
 		IdentifierGeneratorDefinition.Builder definitionBuilder = new IdentifierGeneratorDefinition.Builder();
 
 		if ( context.getMappingDefaults().getImplicitSchemaName() != null ) {
 			definitionBuilder.addParam(
 					PersistentIdentifierGenerator.SCHEMA,
 					context.getMappingDefaults().getImplicitSchemaName()
 			);
 		}
 
 		if ( context.getMappingDefaults().getImplicitCatalogName() != null ) {
 			definitionBuilder.addParam(
 					PersistentIdentifierGenerator.CATALOG,
 					context.getMappingDefaults().getImplicitCatalogName()
 			);
 		}
 
 		if ( ann instanceof TableGenerator ) {
 			context.getBuildingOptions().getIdGenerationTypeInterpreter().interpretTableGenerator(
 					(TableGenerator) ann,
 					definitionBuilder
 			);
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add table generator with name: {0}", definitionBuilder.getName() );
 			}
 		}
 		else if ( ann instanceof SequenceGenerator ) {
 			context.getBuildingOptions().getIdGenerationTypeInterpreter().interpretSequenceGenerator(
 					(SequenceGenerator) ann,
 					definitionBuilder
 			);
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add sequence generator with name: {0}", definitionBuilder.getName() );
 			}
 		}
 		else if ( ann instanceof GenericGenerator ) {
 			GenericGenerator genGen = ( GenericGenerator ) ann;
 			definitionBuilder.setName( genGen.name() );
 			definitionBuilder.setStrategy( genGen.strategy() );
 			Parameter[] params = genGen.parameters();
 			for ( Parameter parameter : params ) {
 				definitionBuilder.addParam( parameter.name(), parameter.value() );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add generic generator with name: {0}", definitionBuilder.getName() );
 			}
 		}
 		else {
 			throw new AssertionFailure( "Unknown Generator annotation: " + ann );
 		}
 
 		return definitionBuilder.build();
 	}
 
 	/**
 	 * Bind a class having JSR175 annotations. Subclasses <b>have to</b> be bound after its parent class.
 	 *
 	 * @param clazzToProcess entity to bind as {@code XClass} instance
 	 * @param inheritanceStatePerClass Meta data about the inheritance relationships for all mapped classes
 	 *
 	 * @throws MappingException in case there is an configuration error
 	 */
 	public static void bindClass(
 			XClass clazzToProcess,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			MetadataBuildingContext context) throws MappingException {
 		//@Entity and @MappedSuperclass on the same class leads to a NPE down the road
 		if ( clazzToProcess.isAnnotationPresent( Entity.class )
 				&&  clazzToProcess.isAnnotationPresent( MappedSuperclass.class ) ) {
 			throw new AnnotationException( "An entity cannot be annotated with both @Entity and @MappedSuperclass: "
 					+ clazzToProcess.getName() );
 		}
 
 		//TODO: be more strict with secondarytable allowance (not for ids, not for secondary table join columns etc)
 		InheritanceState inheritanceState = inheritanceStatePerClass.get( clazzToProcess );
 		AnnotatedClassType classType = context.getMetadataCollector().getClassType( clazzToProcess );
 
 		//Queries declared in MappedSuperclass should be usable in Subclasses
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 			bindQueries( clazzToProcess, context );
 			bindTypeDefs( clazzToProcess, context );
 			bindFilterDefs( clazzToProcess, context );
 		}
 
 		if ( !isEntityClassType( clazzToProcess, classType ) ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding entity from annotated class: %s", clazzToProcess.getName() );
 		}
 
 		PersistentClass superEntity = getSuperEntity(
 				clazzToProcess,
 				inheritanceStatePerClass,
 				context,
 				inheritanceState
 		);
 
 		PersistentClass persistentClass = makePersistentClass( inheritanceState, superEntity, context );
 		Entity entityAnn = clazzToProcess.getAnnotation( Entity.class );
 		org.hibernate.annotations.Entity hibEntityAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.Entity.class
 		);
 		EntityBinder entityBinder = new EntityBinder(
 				entityAnn,
 				hibEntityAnn,
 				clazzToProcess,
 				persistentClass,
 				context
 		);
 		entityBinder.setInheritanceState( inheritanceState );
 
 		bindQueries( clazzToProcess, context );
 		bindFilterDefs( clazzToProcess, context );
 		bindTypeDefs( clazzToProcess, context );
 		bindFetchProfiles( clazzToProcess, context );
 		BinderHelper.bindAnyMetaDefs( clazzToProcess, context );
 
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
 				clazzToProcess,
 				context,
 				inheritanceState,
 				superEntity
 		);
 
 		final Ejb3DiscriminatorColumn discriminatorColumn;
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			discriminatorColumn = processSingleTableDiscriminatorProperties(
 					clazzToProcess,
 					context,
 					inheritanceState,
 					entityBinder
 			);
 		}
 		else if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			discriminatorColumn = processJoinedDiscriminatorProperties(
 					clazzToProcess,
 					context,
 					inheritanceState,
 					entityBinder
 			);
 		}
 		else {
 			discriminatorColumn = null;
 		}
 
 		entityBinder.setProxy( clazzToProcess.getAnnotation( Proxy.class ) );
 		entityBinder.setBatchSize( clazzToProcess.getAnnotation( BatchSize.class ) );
 		entityBinder.setWhere( clazzToProcess.getAnnotation( Where.class ) );
 	    entityBinder.setCache( determineCacheSettings( clazzToProcess, context ) );
 	    entityBinder.setNaturalIdCache( clazzToProcess, clazzToProcess.getAnnotation( NaturalIdCache.class ) );
 
 		bindFilters( clazzToProcess, entityBinder, context );
 
 		entityBinder.bindEntity();
 
 		if ( inheritanceState.hasTable() ) {
 			Check checkAnn = clazzToProcess.getAnnotation( Check.class );
 			String constraints = checkAnn == null
 					? null
 					: checkAnn.constraints();
 
 			EntityTableXref denormalizedTableXref = inheritanceState.hasDenormalizedTable()
 					? context.getMetadataCollector().getEntityTableXref( superEntity.getEntityName() )
 					: null;
 
 			entityBinder.bindTable(
 					schema,
 					catalog,
 					table,
 					uniqueConstraints,
 					constraints,
 					denormalizedTableXref
 			);
 		}
 		else {
 			if ( clazzToProcess.isAnnotationPresent( Table.class ) ) {
 				LOG.invalidTableAnnotation( clazzToProcess.getName() );
 			}
 
 			if ( inheritanceState.getType() == InheritanceType.SINGLE_TABLE ) {
 				// we at least need to properly set up the EntityTableXref
 				entityBinder.bindTableForDiscriminatedSubclass(
 						context.getMetadataCollector().getEntityTableXref( superEntity.getEntityName() )
 				);
 			}
 		}
 
 
 		PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(
 				clazzToProcess,
 				persistentClass,
 				entityBinder,
 				context,
 				inheritanceStatePerClass
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
 
 		// todo : sucks that this is separate from RootClass distinction
 		final boolean isInheritanceRoot = !inheritanceState.hasParents();
 		final boolean hasSubclasses = inheritanceState.hasSiblings();
 
 		if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			if ( inheritanceState.hasParents() ) {
 				onDeleteAppropriate = true;
 				final JoinedSubclass jsc = ( JoinedSubclass ) persistentClass;
 				SimpleValue key = new DependantValue( context.getMetadataCollector(), jsc.getTable(), jsc.getIdentifier() );
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
 				context.getMetadataCollector().addSecondPass( new JoinedSubclassFkSecondPass( jsc, inheritanceJoinedColumns, key, context ) );
 				context.getMetadataCollector().addSecondPass( new CreateKeySecondPass( jsc ) );
 			}
 
 			if ( isInheritanceRoot ) {
 				// the class we are processing is the root of the hierarchy, see if we had a discriminator column
 				// (it is perfectly valid for joined subclasses to not have discriminators).
 				if ( discriminatorColumn != null ) {
 					// we have a discriminator column
 					if ( hasSubclasses || !discriminatorColumn.isImplicit() ) {
 						bindDiscriminatorColumnToRootPersistentClass(
 								(RootClass) persistentClass,
 								discriminatorColumn,
 								entityBinder.getSecondaryTables(),
 								propertyHolder,
 								context
 						);
 						//bind it again since the type might have changed
 						entityBinder.bindDiscriminatorValue();
 					}
 				}
 			}
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			if ( isInheritanceRoot ) {
 				if ( hasSubclasses || !discriminatorColumn.isImplicit() ) {
 					bindDiscriminatorColumnToRootPersistentClass(
 							(RootClass) persistentClass,
 							discriminatorColumn,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							context
 					);
 					//bind it again since the type might have changed
 					entityBinder.bindDiscriminatorValue();
 				}
 			}
 		}
 
         if ( onDeleteAnn != null && !onDeleteAppropriate ) {
 			LOG.invalidOnDeleteAnnotation(propertyHolder.getEntityName());
 		}
 
 		// try to find class level generators
 		HashMap<String, IdentifierGeneratorDefinition> classGenerators = buildLocalGenerators( clazzToProcess, context );
 
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
 				context
 		);
 
 		if ( !isIdClass ) {
 			entityBinder.setWrapIdsInEmbeddedComponents( elementsToProcess.getIdPropertyCount() > 1 );
 		}
 
 		processIdPropertiesIfNotAlready(
 				inheritanceStatePerClass,
 				context,
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
 			context.getMetadataCollector().addSecondPass( new CreateKeySecondPass( rootClass ) );
 		}
 		else {
 			superEntity.addSubclass( ( Subclass ) persistentClass );
 		}
 
 		context.getMetadataCollector().addEntityBinding( persistentClass );
 
 		//Process secondary tables and complementary definitions (ie o.h.a.Table)
 		context.getMetadataCollector().addSecondPass(
 				new SecondaryTableSecondPass(
 						entityBinder,
 						propertyHolder,
 						clazzToProcess
 				)
 		);
 
 		//add process complementary Table definition (index & all)
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Table.class ) );
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Tables.class ) );
 		entityBinder.processComplementaryTableDefinitions( tabAnn );
 	}
 
 	/**
 	 * Process all discriminator-related metadata per rules for "single table" inheritance
 	 */
 	private static Ejb3DiscriminatorColumn processSingleTableDiscriminatorProperties(
 			XClass clazzToProcess,
 			MetadataBuildingContext context,
 			InheritanceState inheritanceState,
 			EntityBinder entityBinder) {
 		final boolean isRoot = !inheritanceState.hasParents();
 
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		javax.persistence.DiscriminatorColumn discAnn = clazzToProcess.getAnnotation(
 				javax.persistence.DiscriminatorColumn.class
 		);
 		DiscriminatorType discriminatorType = discAnn != null
 				? discAnn.discriminatorType()
 				: DiscriminatorType.STRING;
 
 		org.hibernate.annotations.DiscriminatorFormula discFormulaAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.DiscriminatorFormula.class
 		);
 		if ( isRoot ) {
 			discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
 					discriminatorType,
 					discAnn,
 					discFormulaAnn,
 					context
 			);
 		}
 		if ( discAnn != null && !isRoot ) {
 			LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 		}
 
 		final String discriminatorValue = clazzToProcess.isAnnotationPresent( DiscriminatorValue.class )
 				? clazzToProcess.getAnnotation( DiscriminatorValue.class ).value()
 				: null;
 		entityBinder.setDiscriminatorValue( discriminatorValue );
 
 		DiscriminatorOptions discriminatorOptions = clazzToProcess.getAnnotation( DiscriminatorOptions.class );
 		if ( discriminatorOptions != null) {
 			entityBinder.setForceDiscriminator( discriminatorOptions.force() );
 			entityBinder.setInsertableDiscriminator( discriminatorOptions.insert() );
 		}
 
 		return discriminatorColumn;
 	}
 
 	/**
 	 * Process all discriminator-related metadata per rules for "joined" inheritance
 	 */
 	private static Ejb3DiscriminatorColumn processJoinedDiscriminatorProperties(
 			XClass clazzToProcess,
 			MetadataBuildingContext context,
 			InheritanceState inheritanceState,
 			EntityBinder entityBinder) {
 		if ( clazzToProcess.isAnnotationPresent( DiscriminatorFormula.class ) ) {
 			throw new MappingException( "@DiscriminatorFormula on joined inheritance not supported at this time" );
 		}
 
 
 		// DiscriminatorValue handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final DiscriminatorValue discriminatorValueAnnotation = clazzToProcess.getAnnotation( DiscriminatorValue.class );
 		final String discriminatorValue = discriminatorValueAnnotation != null
 				? clazzToProcess.getAnnotation( DiscriminatorValue.class ).value()
 				: null;
 		entityBinder.setDiscriminatorValue( discriminatorValue );
 
 
 		// DiscriminatorColumn handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final DiscriminatorColumn discriminatorColumnAnnotation = clazzToProcess.getAnnotation( DiscriminatorColumn.class );
 		if ( !inheritanceState.hasParents() ) {
 			// we want to process the discriminator column if either:
 			//		1) There is an explicit DiscriminatorColumn annotation && we are not told to ignore them
 			//		2) There is not an explicit DiscriminatorColumn annotation && we are told to create them implicitly
 			final boolean generateDiscriminatorColumn;
 			if ( discriminatorColumnAnnotation != null ) {
 				if ( context.getBuildingOptions().ignoreExplicitDiscriminatorsForJoinedInheritance() ) {
 					LOG.debugf( "Ignoring explicit DiscriminatorColumn annotation on ", clazzToProcess.getName() );
 					generateDiscriminatorColumn = false;
 				}
 				else {
 					LOG.applyingExplicitDiscriminatorColumnForJoined(
 							clazzToProcess.getName(),
 							AvailableSettings.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 					);
 					generateDiscriminatorColumn = true;
 				}
 			}
 			else {
 				if ( context.getBuildingOptions().createImplicitDiscriminatorsForJoinedInheritance() ) {
 					LOG.debug( "Applying implicit DiscriminatorColumn using DiscriminatorColumn defaults" );
 					generateDiscriminatorColumn = true;
 				}
 				else {
 					LOG.debug( "Ignoring implicit (absent) DiscriminatorColumn" );
 					generateDiscriminatorColumn = false;
 				}
 			}
 
 			if ( generateDiscriminatorColumn ) {
 				final DiscriminatorType discriminatorType = discriminatorColumnAnnotation != null
 						? discriminatorColumnAnnotation.discriminatorType()
 						: DiscriminatorType.STRING;
 				return Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
 						discriminatorType,
 						discriminatorColumnAnnotation,
 						null,
 						context
 				);
 			}
 		}
 		else {
 			if ( discriminatorColumnAnnotation != null ) {
 				LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 			}
 		}
 
 		return null;
 	}
 
 	private static void processIdPropertiesIfNotAlready(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			MetadataBuildingContext context,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			HashMap<String, IdentifierGeneratorDefinition> classGenerators,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			boolean subclassAndSingleTableStrategy,
 			Set<String> idPropertiesIfIdClass) {
 		Set<String> missingIdProperties = new HashSet<String>( idPropertiesIfIdClass );
 		for ( PropertyData propertyAnnotatedElement : elementsToProcess.getElements() ) {
 			String propertyName = propertyAnnotatedElement.getPropertyName();
 			if ( !idPropertiesIfIdClass.contains( propertyName ) ) {
 				processElementAnnotations(
 						propertyHolder,
 						subclassAndSingleTableStrategy
 								? Nullability.FORCED_NULL
 								: Nullability.NO_CONSTRAINT,
 						propertyAnnotatedElement,
 						classGenerators,
 						entityBinder,
 						false,
 						false,
 						false,
 						context,
 						inheritanceStatePerClass
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
 			MetadataBuildingContext context) {
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
 			XClass compositeClass = context.getBuildingOptions().getReflectionManager().toXClass( idClass.value() );
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
 					context
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
 					context,
 					inheritanceStatePerClass
 			);
 			propertyHolder.setInIdClass( null );
 			inferredData = new PropertyPreloadedData(
 					propertyAccessor, PropertyPath.IDENTIFIER_MAPPER_PROPERTY, compositeClass
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
 					context,
 					inheritanceStatePerClass
 			);
 			entityBinder.setIgnoreIdAnnotations( ignoreIdAnnotations );
 			persistentClass.setIdentifierMapper( mapper );
 
 			//If id definition is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					classWithIdClass,
 					inheritanceStatePerClass,
 					context
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierMapper( mapper );
 			}
 			else {
 				//we are for sure on the entity
 				persistentClass.setDeclaredIdentifierMapper( mapper );
 			}
 
 			Property property = new Property();
 			property.setName( PropertyPath.IDENTIFIER_MAPPER_PROPERTY );
-			property.setNodeName( "id" );
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
 			MetadataBuildingContext context) {
 		if ( elementsToProcess.getIdPropertyCount() == 1 ) {
 			final PropertyData idPropertyOnBaseClass = getUniqueIdPropertyFromBaseClass(
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					context
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
 				final XClass idClass = context.getBuildingOptions().getReflectionManager().toXClass(
 						associatedClassWithIdClass.getAnnotation( IdClass.class ).value()
 				);
 				return idClass.equals( compositeClass );
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static Cache determineCacheSettings(XClass clazzToProcess, MetadataBuildingContext context) {
 		Cache cacheAnn = clazzToProcess.getAnnotation( Cache.class );
 		if ( cacheAnn != null ) {
 			return cacheAnn;
 		}
 
 		Cacheable cacheableAnn = clazzToProcess.getAnnotation( Cacheable.class );
 		SharedCacheMode mode = determineSharedCacheMode( context );
 		switch ( mode ) {
 			case ALL: {
 				cacheAnn = buildCacheMock( clazzToProcess.getName(), context );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheableAnn != null && cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), context );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheableAnn == null || cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), context );
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
 
 	private static SharedCacheMode determineSharedCacheMode(MetadataBuildingContext context) {
 		return context.getBuildingOptions().getSharedCacheMode();
 	}
 
 	private static Cache buildCacheMock(String region, MetadataBuildingContext context) {
 		return new LocalCacheAnnotationImpl( region, determineCacheConcurrencyStrategy( context ) );
 	}
 
 	private static CacheConcurrencyStrategy DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	private static CacheConcurrencyStrategy determineCacheConcurrencyStrategy(MetadataBuildingContext context) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY == null ) {
 			DEFAULT_CACHE_CONCURRENCY_STRATEGY = CacheConcurrencyStrategy.fromAccessType(
 					context.getBuildingOptions().getImplicitCacheAccessType()
 			);
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
 
 	private static PersistentClass makePersistentClass(
 			InheritanceState inheritanceState,
 			PersistentClass superEntity,
 			MetadataBuildingContext metadataBuildingContext) {
 		//we now know what kind of persistent entity it is
 		if ( !inheritanceState.hasParents() ) {
 			return new RootClass( metadataBuildingContext );
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			return new SingleTableSubclass( superEntity, metadataBuildingContext );
 		}
 		else if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			return new JoinedSubclass( superEntity, metadataBuildingContext );
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			return new UnionSubclass( superEntity, metadataBuildingContext );
 		}
 		else {
 			throw new AssertionFailure( "Unknown inheritance type: " + inheritanceState.getType() );
 		}
 	}
 
 	private static Ejb3JoinColumn[] makeInheritanceJoinColumns(
 			XClass clazzToProcess,
 			MetadataBuildingContext context,
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
 							jcAnn,
 							null,
 							superEntity.getIdentifier(),
 							null,
 							null,
 							context
 					);
 				}
 			}
 			else {
 				PrimaryKeyJoinColumn jcAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumn.class );
 				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
 				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						jcAnn,
 						null,
 						superEntity.getIdentifier(),
 						null,
 						null,
 						context
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
 
 	private static PersistentClass getSuperEntity(
 			XClass clazzToProcess,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			MetadataBuildingContext context,
 			InheritanceState inheritanceState) {
 		InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 				clazzToProcess, inheritanceStatePerClass
 		);
 		PersistentClass superEntity = superEntityState != null
 				? context.getMetadataCollector().getEntityBinding( superEntityState.getClazz().getName() )
 				: null;
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
 
 	private static void bindFilters(
 			XClass annotatedClass,
 			EntityBinder entityBinder,
 			MetadataBuildingContext context) {
 
 		bindFilters( annotatedClass, entityBinder );
 
 		XClass classToProcess = annotatedClass.getSuperclass();
 		while ( classToProcess != null ) {
 			AnnotatedClassType classType = context.getMetadataCollector().getClassType( classToProcess );
 			if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 				bindFilters( classToProcess, entityBinder );
 			}
 			else {
 				break;
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
 
 	private static void bindFilterDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		FilterDef defAnn = annotatedElement.getAnnotation( FilterDef.class );
 		FilterDefs defsAnn = annotatedElement.getAnnotation( FilterDefs.class );
 		if ( defAnn != null ) {
 			bindFilterDef( defAnn, context );
 		}
 		if ( defsAnn != null ) {
 			for ( FilterDef def : defsAnn.value() ) {
 				bindFilterDef( def, context );
 			}
 		}
 	}
 
 	private static void bindFilterDef(FilterDef defAnn, MetadataBuildingContext context) {
 		Map<String, org.hibernate.type.Type> params = new HashMap<String, org.hibernate.type.Type>();
 		for ( ParamDef param : defAnn.parameters() ) {
 			params.put( param.name(), context.getMetadataCollector().getTypeResolver().heuristicType( param.type() ) );
 		}
 		FilterDefinition def = new FilterDefinition( defAnn.name(), defAnn.defaultCondition(), params );
 		LOG.debugf( "Binding filter definition: %s", def.getFilterName() );
 		context.getMetadataCollector().addFilterDefinition( def );
 	}
 
 	private static void bindTypeDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		TypeDef defAnn = annotatedElement.getAnnotation( TypeDef.class );
 		TypeDefs defsAnn = annotatedElement.getAnnotation( TypeDefs.class );
 		if ( defAnn != null ) {
 			bindTypeDef( defAnn, context );
 		}
 		if ( defsAnn != null ) {
 			for ( TypeDef def : defsAnn.value() ) {
 				bindTypeDef( def, context );
 			}
 		}
 	}
 
 	private static void bindTypeDef(TypeDef defAnn, MetadataBuildingContext context) {
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
 			context.getMetadataCollector().addTypeDefinition(
 					new TypeDefinition(
 							defAnn.name(),
 							defAnn.typeClass(),
 							null,
 							params
 					)
 			);
 		}
 
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.defaultForType().getName() );
 			}
 			context.getMetadataCollector().addTypeDefinition(
 					new TypeDefinition(
 							defAnn.defaultForType().getName(),
 							defAnn.typeClass(),
 							new String[]{ defAnn.defaultForType().getName() },
 							params
 					)
 			);
 		}
 
 	}
 
 	private static void bindFetchProfiles(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		FetchProfile fetchProfileAnnotation = annotatedElement.getAnnotation( FetchProfile.class );
 		FetchProfiles fetchProfileAnnotations = annotatedElement.getAnnotation( FetchProfiles.class );
 		if ( fetchProfileAnnotation != null ) {
 			bindFetchProfile( fetchProfileAnnotation, context );
 		}
 		if ( fetchProfileAnnotations != null ) {
 			for ( FetchProfile profile : fetchProfileAnnotations.value() ) {
 				bindFetchProfile( profile, context );
 			}
 		}
 	}
 
 	private static void bindFetchProfile(FetchProfile fetchProfileAnnotation, MetadataBuildingContext context) {
 		for ( FetchProfile.FetchOverride fetch : fetchProfileAnnotation.fetchOverrides() ) {
 			org.hibernate.annotations.FetchMode mode = fetch.mode();
 			if ( !mode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 
 			context.getMetadataCollector().addSecondPass(
 					new VerifyFetchProfileReferenceSecondPass(
 							fetchProfileAnnotation.name(),
 							fetch,
 							context
 					)
 			);
 		}
 	}
 
 
 	private static void bindDiscriminatorColumnToRootPersistentClass(
 			RootClass rootClass,
 			Ejb3DiscriminatorColumn discriminatorColumn,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			MetadataBuildingContext context) {
 		if ( rootClass.getDiscriminator() == null ) {
 			if ( discriminatorColumn == null ) {
 				throw new AssertionFailure( "discriminator column should have been built" );
 			}
 			discriminatorColumn.setJoins( secondaryTables );
 			discriminatorColumn.setPropertyHolder( propertyHolder );
 			SimpleValue discriminatorColumnBinding = new SimpleValue( context.getMetadataCollector(), rootClass.getTable() );
 			rootClass.setDiscriminator( discriminatorColumnBinding );
 			discriminatorColumn.linkWithValue( discriminatorColumnBinding );
 			discriminatorColumnBinding.setTypeName( discriminatorColumn.getDiscriminatorTypeName() );
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
 	 *
 	 * @return the number of id properties found while iterating the elements of {@code annotatedClass} using
 	 *         the determined access strategy, {@code false} otherwise.
 	 */
 	static int addElementsOfClass(
 			List<PropertyData> elements,
 			AccessType defaultAccessType,
 			PropertyContainer propertyContainer,
 			MetadataBuildingContext context) {
 		int idPropertyCounter = 0;
 		AccessType accessType = defaultAccessType;
 
 		if ( propertyContainer.hasExplicitAccessStrategy() ) {
 			accessType = propertyContainer.getExplicitAccessStrategy();
 		}
 
 		Collection<XProperty> properties = propertyContainer.getProperties( accessType );
 		for ( XProperty p : properties ) {
 			final int currentIdPropertyCounter = addProperty(
 					propertyContainer,
 					p,
 					elements,
 					accessType.getType(),
 					context
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
 			MetadataBuildingContext context) {
 		final XClass declaringClass = propertyContainer.getDeclaringClass();
 		final XClass entity = propertyContainer.getEntityAtStake();
 		int idPropertyCounter = 0;
 		PropertyData propertyAnnotatedElement = new PropertyInferredData(
 				declaringClass,
 				property,
 				propertyAccessor,
 				context.getBuildingOptions().getReflectionManager()
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
 			if ( context.getBuildingOptions().isSpecjProprietarySyntaxEnabled() ) {
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
 										context.getBuildingOptions().getReflectionManager()
 								);
 								context.getMetadataCollector().addPropertyAnnotatedWithMapsIdSpecj(
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
 				context.getMetadataCollector().addToOneAndIdProperty( entity, propertyAnnotatedElement );
 			}
 			idPropertyCounter++;
 		}
 		else {
 			annElts.add( propertyAnnotatedElement );
 		}
 		if ( element.isAnnotationPresent( MapsId.class ) ) {
 			context.getMetadataCollector().addPropertyAnnotatedWithMapsId( entity, propertyAnnotatedElement );
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
 			HashMap<String, IdentifierGeneratorDefinition> classGenerators,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			boolean isComponentEmbedded,
 			boolean inSecondPass,
 			MetadataBuildingContext context,
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
 				propertyHolder,
 				nullability,
 				property,
 				inferredData,
 				entityBinder,
 				context
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
 		propertyBinder.setBuildingContext( context );
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
 					context
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
 			rootClass.setOptimisticLockStyle( OptimisticLockStyle.VERSION );
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
 						joinColumn.setExplicitTableName( join.getTable().getName() );
 					}
 				}
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindManyToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, false, forcePersist ),
 						joinColumns,
 						!mandatory,
 						ignoreNotFound,
 						onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, context ),
 						propertyHolder,
 						inferredData,
 						false,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						context
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
 						joinColumn.setExplicitTableName( join.getTable().getName() );
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
 						ToOneBinder.getTargetEntity( inferredData, context ),
 						propertyHolder,
 						inferredData,
 						ann.mappedBy(),
 						trueOneToOne,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						context
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
 						joinColumn.setExplicitTableName( join.getTable().getName() );
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
 						context
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
 							context
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
 							context
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( MapKeyType.class ),
 						context
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
 				collectionBinder.setBuildingContext( context );
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
 							context
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(),
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							context
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
 							context
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
 							context
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
 							context
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
 							context.getBuildingOptions().getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
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
 							context.getBuildingOptions().getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							context.getBuildingOptions().getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
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
 							context.getBuildingOptions().getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property,
 						context,
 						entityBinder,
 						collectionBinder,
 						propertyHolder,
 						inferredData,
 						mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, context ) );
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
 							isId,
 							propertyHolder,
 							property.getName(),
 							context
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
 								isId, propertyHolder, property.getName(), context
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
 							context,
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
 								isId, propertyHolder, property.getName(), context
 						);
 						propertyBinder.setReferencedEntityName( mapsIdProperty.getClassOrElementName() );
 					}
 
 					propertyBinder.makePropertyValueAndBind();
 
 				}
 				if ( isOverridden ) {
 					final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), context
 					);
 					Map<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 					final IdentifierGeneratorDefinition.Builder foreignGeneratorBuilder = new IdentifierGeneratorDefinition.Builder();
 					foreignGeneratorBuilder.setName( "Hibernate-local--foreign generator" );
 					foreignGeneratorBuilder.setStrategy( "foreign" );
 					foreignGeneratorBuilder.addParam( "property", mapsIdProperty.getPropertyName() );
 
 					final IdentifierGeneratorDefinition foreignGenerator = foreignGeneratorBuilder.build();
 					localGenerators.put( foreignGenerator.getName(), foreignGenerator );
 
 					BinderHelper.makeIdGenerator(
 							( SimpleValue ) propertyBinder.getValue(),
 							foreignGenerator.getStrategy(),
 							foreignGenerator.getName(),
 							context,
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
 								context
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
 
 		// Natural ID columns must reside in one single UniqueKey within the Table.
 		// For now, simply ensure consistent naming.
 		// TODO: AFAIK, there really isn't a reason for these UKs to be created
 		// on the secondPass.  This whole area should go away...
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
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
 			HashMap<String, IdentifierGeneratorDefinition> classGenerators,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext) {
 		if ( isIdentifierMapper ) {
 			throw new AnnotationException(
 					"@IdClass class should not have @Id nor @EmbeddedId properties: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		XClass returnedClass = inferredData.getClassOrElement();
 		XProperty property = inferredData.getProperty();
 		//clone classGenerator and override with local values
 		HashMap<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 		localGenerators.putAll( buildLocalGenerators( property, buildingContext ) );
 
 		//manage composite related metadata
 		//guess if its a component and find id data access (property, field etc)
 		final boolean isComponent = returnedClass.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( EmbeddedId.class );
 
 		GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 		String generatorType = generatedValue != null
 				? generatorType( generatedValue.strategy(), buildingContext, returnedClass )
 				: "assigned";
 		String generatorName = generatedValue != null
 				? generatedValue.generator()
 				: BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( isComponent ) {
 			//a component must not have any generator
 			generatorType = "assigned";
 		}
 		BinderHelper.makeIdGenerator( idValue, generatorType, generatorName, buildingContext, localGenerators );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Bind {0} on {1}", ( isComponent ? "@EmbeddedId" : "@Id" ), inferredData.getPropertyName() );
 		}
 	}
 
 	//TODO move that to collection binder?
 
 	private static void bindJoinedTableAssociation(
 			XProperty property,
 			MetadataBuildingContext buildingContext,
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
 				annJoins,
 				entityBinder.getSecondaryTables(),
 				propertyHolder,
 				inferredData.getPropertyName(),
 				mappedBy,
 				buildingContext
 		);
 		Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annInverseJoins,
 				entityBinder.getSecondaryTables(),
 				propertyHolder,
 				inferredData.getPropertyName(),
 				mappedBy,
 				buildingContext
 		);
 		associationTableBinder.setBuildingContext( buildingContext );
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
 			MetadataBuildingContext buildingContext,
 			boolean isComponentEmbedded,
 			boolean isId, //is a identifier
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			String referencedEntityName, //is a component who is overridden by a @MapsId
 			Ejb3JoinColumn[] columns) {
 		Component comp;
 		if ( referencedEntityName != null ) {
 			comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext );
 			SecondPass sp = new CopyIdentifierComponentSecondPass(
 					comp,
 					referencedEntityName,
 					columns,
 					buildingContext
 			);
 			buildingContext.getMetadataCollector().addSecondPass( sp );
 		}
 		else {
 			comp = fillComponent(
 					propertyHolder, inferredData, propertyAccessor, !isId, entityBinder,
 					isComponentEmbedded, isIdentifierMapper,
 					false, buildingContext, inheritanceStatePerClass
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
 		binder.setBuildingContext( buildingContext );
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
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		return fillComponent(
 				propertyHolder,
 				inferredData,
 				null,
 				propertyAccessor,
 				isNullable,
 				entityBinder,
 				isComponentEmbedded,
 				isIdentifierMapper,
 				inSecondPass,
 				buildingContext,
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
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 		Component comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext );
 		String subpath = BinderHelper.getPath( propertyHolder, inferredData );
 		LOG.tracev( "Binding component with path: {0}", subpath );
 		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(
 				comp,
 				subpath,
 				inferredData,
 				propertyHolder,
 				buildingContext
 		);
 
 
 		// propertyHolder here is the owner of the component property.  Tell it we are about to start the component...
 
 		propertyHolder.startingProperty( inferredData.getProperty() );
 
 		final XClass xClassProcessed = inferredData.getPropertyClass();
 		List<PropertyData> classElements = new ArrayList<PropertyData>();
 		XClass returnedClassOrElement = inferredData.getClassOrElement();
 
 		List<PropertyData> baseClassElements = null;
 		Map<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
 		XClass baseReturnedClassOrElement;
 		if ( baseInferredData != null ) {
 			baseClassElements = new ArrayList<PropertyData>();
 			baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 			bindTypeDefs( baseReturnedClassOrElement, buildingContext );
 			PropertyContainer propContainer = new PropertyContainer( baseReturnedClassOrElement, xClassProcessed );
 			addElementsOfClass( baseClassElements, propertyAccessor, propContainer, buildingContext );
 			for ( PropertyData element : baseClassElements ) {
 				orderedBaseClassElements.put( element.getPropertyName(), element );
 			}
 		}
 
 		//embeddable elements can have type defs
 		bindTypeDefs( returnedClassOrElement, buildingContext );
 		PropertyContainer propContainer = new PropertyContainer( returnedClassOrElement, xClassProcessed );
 		addElementsOfClass( classElements, propertyAccessor, propContainer, buildingContext );
 
 		//add elements of the embeddable superclass
 		XClass superClass = xClassProcessed.getSuperclass();
 		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
 			//FIXME: proper support of typevariables incl var resolved at upper levels
 			propContainer = new PropertyContainer( superClass, xClassProcessed );
 			addElementsOfClass( classElements, propertyAccessor, propContainer, buildingContext );
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
 					subHolder,
 					isNullable
 							? Nullability.NO_CONSTRAINT
 							: Nullability.FORCED_NOT_NULL,
 					propertyAnnotatedElement,
 					new HashMap<String, IdentifierGeneratorDefinition>(),
 					entityBinder,
 					isIdentifierMapper,
 					isComponentEmbedded,
 					inSecondPass,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 
 			XProperty property = propertyAnnotatedElement.getProperty();
 			if ( property.isAnnotationPresent( GeneratedValue.class ) &&
 					property.isAnnotationPresent( Id.class ) ) {
 				//clone classGenerator and override with local values
 				Map<String, IdentifierGeneratorDefinition> localGenerators = new HashMap<String, IdentifierGeneratorDefinition>();
 				localGenerators.putAll( buildLocalGenerators( property, buildingContext ) );
 
 				GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 				String generatorType = generatedValue != null
 						? generatorType( generatedValue.strategy(), buildingContext, property.getType() )
 						: "assigned";
 				String generator = generatedValue != null ? generatedValue.generator() : BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 				BinderHelper.makeIdGenerator(
 						( SimpleValue ) comp.getProperty( property.getName() ).getValue(),
 						generatorType,
 						generator,
 						buildingContext,
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
 			MetadataBuildingContext context) {
 		Component comp = new Component( context.getMetadataCollector(), propertyHolder.getPersistentClass() );
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
-		comp.setNodeName( inferredData.getPropertyName() );
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
 			MetadataBuildingContext buildingContext,
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
 					propertyHolder,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					false,
 					entityBinder,
 					isEmbedded,
 					isIdentifierMapper,
 					false,
 					buildingContext,
 					inheritanceStatePerClass
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
 			value.setBuildingContext( buildingContext );
 			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName, null );
 			value.setAccessType( propertyAccessor );
 			id = value.make();
 		}
 		rootClass.setIdentifier( id );
 		BinderHelper.makeIdGenerator(
 				id,
 				generatorType,
 				generatorName,
 				buildingContext,
 				Collections.<String, IdentifierGeneratorDefinition>emptyMap()
 		);
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
 					buildingContext
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
 
 	private static PropertyData getUniqueIdPropertyFromBaseClass(
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			MetadataBuildingContext context) {
 		List<PropertyData> baseClassElements = new ArrayList<PropertyData>();
 		XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 		PropertyContainer propContainer = new PropertyContainer(
 				baseReturnedClassOrElement, inferredData.getPropertyClass()
 		);
 		addElementsOfClass( baseClassElements, propertyAccessor, propContainer, context );
 		//Id properties are on top and there is only one
 		return baseClassElements.get( 0 );
 	}
 
 	private static void setupComponentTuplizer(XProperty property, Component component) {
 		if ( property == null ) {
 			return;
 		}
 		if ( property.isAnnotationPresent( Tuplizers.class ) ) {
 			for ( Tuplizer tuplizer : property.getAnnotation( Tuplizers.class ).value() ) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				//todo tuplizer.entityModeType
 				component.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( property.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = property.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			component.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 	}
 
 	private static void bindManyToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean optional,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean unique,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			MetadataBuildingContext context) {
 		//All FK columns should be in the same table
 		org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne( context.getMetadataCollector(), columns[0].getTable() );
 		// This is a @OneToOne mapped to a physical o.h.mapping.ManyToOne
 		if ( unique ) {
 			value.markAsLogicalOneToOne();
 		}
 		value.setReferencedEntityName( ToOneBinder.getReferenceEntityName( inferredData, targetEntity, context ) );
 		final XProperty property = inferredData.getProperty();
 		defineFetchingStrategy( value, property );
 		//value.setFetchMode( fetchMode );
 		value.setIgnoreNotFound( ignoreNotFound );
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		//value.setLazy( fetchMode != FetchMode.JOIN );
 		if ( !optional ) {
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setNullable( false );
 			}
 		}
 		if ( property.isAnnotationPresent( MapsId.class ) ) {
 			//read only
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setInsertable( false );
 				column.setUpdatable( false );
 			}
 		}
 		
 		final JoinColumn joinColumn = property.getAnnotation( JoinColumn.class );
 
 		//Make sure that JPA1 key-many-to-one columns are read only tooj
 		boolean hasSpecjManyToOne=false;
 		if ( context.getBuildingOptions().isSpecjProprietarySyntaxEnabled() ) {
 			String columnName = "";
 			for ( XProperty prop : inferredData.getDeclaringClass()
 					.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 				if ( prop.isAnnotationPresent( Id.class ) && prop.isAnnotationPresent( Column.class ) ) {
 					columnName = prop.getAnnotation( Column.class ).name();
 				}
 
 				if ( property.isAnnotationPresent( ManyToOne.class ) && joinColumn != null
 						&& ! BinderHelper.isEmptyAnnotationValue( joinColumn.name() )
 						&& joinColumn.name().equals( columnName )
 						&& !property.isAnnotationPresent( MapsId.class ) ) {
 				   hasSpecjManyToOne = true;
 					for ( Ejb3JoinColumn column : columns ) {
 						column.setInsertable( false );
 						column.setUpdatable( false );
 					}
 				}
 			}
 
 		}
 		value.setTypeName( inferredData.getClassOrElementName() );
 		final String propertyName = inferredData.getPropertyName();
 		value.setTypeUsingReflection( propertyHolder.getClassName(), propertyName );
 
 		if ( joinColumn != null
 				&& joinColumn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 			// not ideal...
 			value.setForeignKeyName( "none" );
 		}
 		else {
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && StringHelper.isNotEmpty( fk.name() ) ) {
 				value.setForeignKeyName( fk.name() );
 			}
 			else if ( joinColumn != null ) {
 				value.setForeignKeyName( StringHelper.nullIfEmpty( joinColumn.foreignKey().name() ) );
 			}
 		}
 
 		String path = propertyHolder.getPath() + "." + propertyName;
 		FkSecondPass secondPass = new ToOneFkSecondPass(
 				value, columns,
 				!optional && unique, //cannot have nullable and unique on certain DBs like Derby
 				propertyHolder.getEntityOwnerClassName(),
 				path,
 				context
 		);
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 		}
 		else {
 			context.getMetadataCollector().addSecondPass( secondPass );
 		}
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + "." + propertyName );
 		//PropertyBinder binder = new PropertyBinder();
 		propertyBinder.setName( propertyName );
 		propertyBinder.setValue( value );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else if (hasSpecjManyToOne) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else {
 			propertyBinder.setInsertable( columns[0].isInsertable() );
 			propertyBinder.setUpdatable( columns[0].isUpdatable() );
 		}
 		propertyBinder.setColumns( columns );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setCascade( cascadeStrategy );
 		propertyBinder.setProperty( property );
 		propertyBinder.setXToMany( true );
 		propertyBinder.makePropertyAndBind();
 	}
 
 	protected static void defineFetchingStrategy(ToOne toOne, XProperty property) {
 		LazyToOne lazy = property.getAnnotation( LazyToOne.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		ManyToOne manyToOne = property.getAnnotation( ManyToOne.class );
 		OneToOne oneToOne = property.getAnnotation( OneToOne.class );
 		FetchType fetchType;
 		if ( manyToOne != null ) {
 			fetchType = manyToOne.fetch();
 		}
 		else if ( oneToOne != null ) {
 			fetchType = oneToOne.fetch();
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @OneToMany nor @OneToOne"
 			);
 		}
 		if ( lazy != null ) {
 			toOne.setLazy( !( lazy.value() == LazyToOneOption.FALSE ) );
 			toOne.setUnwrapProxy( ( lazy.value() == LazyToOneOption.NO_PROXY ) );
 		}
 		else {
 			toOne.setLazy( fetchType == FetchType.LAZY );
 			toOne.setUnwrapProxy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				toOne.setFetchMode( FetchMode.JOIN );
 				toOne.setLazy( false );
 				toOne.setUnwrapProxy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				toOne.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				throw new AnnotationException( "Use of FetchMode.SUBSELECT not allowed on ToOne associations" );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			toOne.setFetchMode( getFetchMode( fetchType ) );
 		}
 	}
 
 	private static void bindOneToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] joinColumns,
 			boolean optional,
 			FetchMode fetchMode,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy,
 			boolean trueOneToOne,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			MetadataBuildingContext context) {
 		//column.getTable() => persistentClass.getTable()
 		final String propertyName = inferredData.getPropertyName();
 		LOG.tracev( "Fetching {0} with {1}", propertyName, fetchMode );
 		boolean mapToPK = true;
 		if ( !trueOneToOne ) {
 			//try to find a hidden true one to one (FK == PK columns)
 			KeyValue identifier = propertyHolder.getIdentifier();
 			if ( identifier == null ) {
 				//this is a @OneToOne in a @EmbeddedId (the persistentClass.identifier is not set yet, it's being built)
 				//by definition the PK cannot refers to itself so it cannot map to itself
 				mapToPK = false;
 			}
 			else {
 				Iterator idColumns = identifier.getColumnIterator();
 				List<String> idColumnNames = new ArrayList<String>();
 				org.hibernate.mapping.Column currentColumn;
 				if ( identifier.getColumnSpan() != joinColumns.length ) {
 					mapToPK = false;
 				}
 				else {
 					while ( idColumns.hasNext() ) {
 						currentColumn = ( org.hibernate.mapping.Column ) idColumns.next();
 						idColumnNames.add( currentColumn.getName() );
 					}
 					for ( Ejb3JoinColumn col : joinColumns ) {
 						if ( !idColumnNames.contains( col.getMappingColumn().getName() ) ) {
 							mapToPK = false;
 							break;
 						}
 					}
 				}
 			}
 		}
 		if ( trueOneToOne || mapToPK || !BinderHelper.isEmptyAnnotationValue( mappedBy ) ) {
 			//is a true one-to-one
 			//FIXME referencedColumnName ignored => ordering may fail.
 			OneToOneSecondPass secondPass = new OneToOneSecondPass(
 					mappedBy,
 					propertyHolder.getEntityName(),
 					propertyName,
 					propertyHolder,
 					inferredData,
 					targetEntity,
 					ignoreNotFound,
 					cascadeOnDelete,
 					optional,
 					cascadeStrategy,
 					joinColumns,
 					context
 			);
 			if ( inSecondPass ) {
 				secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 			}
 			else {
 				context.getMetadataCollector().addSecondPass(
 						secondPass,
 						BinderHelper.isEmptyAnnotationValue( mappedBy )
 				);
 			}
 		}
 		else {
 			//has a FK on the table
 			bindManyToOne(
 					cascadeStrategy, joinColumns, optional, ignoreNotFound, cascadeOnDelete,
 					targetEntity,
 					propertyHolder, inferredData, true, isIdentifierMapper, inSecondPass,
 					propertyBinder, context
 			);
 		}
 	}
 
 	private static void bindAny(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean cascadeOnDelete,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext) {
 		org.hibernate.annotations.Any anyAnn = inferredData.getProperty()
 				.getAnnotation( org.hibernate.annotations.Any.class );
 		if ( anyAnn == null ) {
 			throw new AssertionFailure(
 					"Missing @Any annotation: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		Any value = BinderHelper.buildAnyValue(
 				anyAnn.metaDef(),
 				columns,
 				anyAnn.metaColumn(),
 				inferredData,
 				cascadeOnDelete,
 				nullability,
 				propertyHolder,
 				entityBinder,
 				anyAnn.optional(),
 				buildingContext
 		);
 
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( value );
 
 		binder.setLazy( anyAnn.fetch() == FetchType.LAZY );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			binder.setInsertable( false );
 			binder.setUpdatable( false );
 		}
 		else {
 			binder.setInsertable( columns[0].isInsertable() );
 			binder.setUpdatable( columns[0].isUpdatable() );
 		}
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setCascade( cascadeStrategy );
 		Property prop = binder.makeProperty();
 		//composite FK columns are in the same table so its OK
 		propertyHolder.addProperty( prop, columns, inferredData.getDeclaringClass() );
 	}
 
 	private static String generatorType(
 			GenerationType generatorEnum,
 			final MetadataBuildingContext buildingContext,
 			final XClass javaTypeXClass) {
 		return buildingContext.getBuildingOptions().getIdGenerationTypeInterpreter().determineGeneratorName(
 				generatorEnum,
 				new IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext() {
 					Class javaType = null;
 					@Override
 					public Class getIdType() {
 						if ( javaType == null ) {
 							javaType = buildingContext.getBuildingOptions()
 									.getReflectionManager()
 									.toClass( javaTypeXClass );
 						}
 						return javaType;
 					}
 				}
 		);
 	}
 
 	private static EnumSet<CascadeType> convertToHibernateCascadeType(javax.persistence.CascadeType[] ejbCascades) {
 		EnumSet<CascadeType> hibernateCascadeSet = EnumSet.noneOf( CascadeType.class );
 		if ( ejbCascades != null && ejbCascades.length > 0 ) {
 			for ( javax.persistence.CascadeType cascade : ejbCascades ) {
 				switch ( cascade ) {
 					case ALL:
 						hibernateCascadeSet.add( CascadeType.ALL );
 						break;
 					case PERSIST:
 						hibernateCascadeSet.add( CascadeType.PERSIST );
 						break;
 					case MERGE:
 						hibernateCascadeSet.add( CascadeType.MERGE );
 						break;
 					case REMOVE:
 						hibernateCascadeSet.add( CascadeType.REMOVE );
 						break;
 					case REFRESH:
 						hibernateCascadeSet.add( CascadeType.REFRESH );
 						break;
 					case DETACH:
 						hibernateCascadeSet.add( CascadeType.DETACH );
 						break;
 				}
 			}
 		}
 
 		return hibernateCascadeSet;
 	}
 
 	private static String getCascadeStrategy(
 			javax.persistence.CascadeType[] ejbCascades,
 			Cascade hibernateCascadeAnnotation,
 			boolean orphanRemoval,
 			boolean forcePersist) {
 		EnumSet<CascadeType> hibernateCascadeSet = convertToHibernateCascadeType( ejbCascades );
 		CascadeType[] hibernateCascades = hibernateCascadeAnnotation == null ?
 				null :
 				hibernateCascadeAnnotation.value();
 
 		if ( hibernateCascades != null && hibernateCascades.length > 0 ) {
 			hibernateCascadeSet.addAll( Arrays.asList( hibernateCascades ) );
 		}
 
 		if ( orphanRemoval ) {
 			hibernateCascadeSet.add( CascadeType.DELETE_ORPHAN );
 			hibernateCascadeSet.add( CascadeType.REMOVE );
 		}
 		if ( forcePersist ) {
 			hibernateCascadeSet.add( CascadeType.PERSIST );
 		}
 
 		StringBuilder cascade = new StringBuilder();
 		for ( CascadeType aHibernateCascadeSet : hibernateCascadeSet ) {
 			switch ( aHibernateCascadeSet ) {
 				case ALL:
 					cascade.append( "," ).append( "all" );
 					break;
 				case SAVE_UPDATE:
 					cascade.append( "," ).append( "save-update" );
 					break;
 				case PERSIST:
 					cascade.append( "," ).append( "persist" );
 					break;
 				case MERGE:
 					cascade.append( "," ).append( "merge" );
 					break;
 				case LOCK:
 					cascade.append( "," ).append( "lock" );
 					break;
 				case REFRESH:
 					cascade.append( "," ).append( "refresh" );
 					break;
 				case REPLICATE:
 					cascade.append( "," ).append( "replicate" );
 					break;
 				case EVICT:
 				case DETACH:
 					cascade.append( "," ).append( "evict" );
 					break;
 				case DELETE:
 					cascade.append( "," ).append( "delete" );
 					break;
 				case DELETE_ORPHAN:
 					cascade.append( "," ).append( "delete-orphan" );
 					break;
 				case REMOVE:
 					cascade.append( "," ).append( "delete" );
 					break;
 			}
 		}
 		return cascade.length() > 0 ?
 				cascade.substring( 1 ) :
 				"none";
 	}
 
 	public static FetchMode getFetchMode(FetchType fetch) {
 		if ( fetch == FetchType.EAGER ) {
 			return FetchMode.JOIN;
 		}
 		else {
 			return FetchMode.SELECT;
 		}
 	}
 
 	private static HashMap<String, IdentifierGeneratorDefinition> buildLocalGenerators(XAnnotatedElement annElt, MetadataBuildingContext context) {
 		HashMap<String, IdentifierGeneratorDefinition> generators = new HashMap<String, IdentifierGeneratorDefinition>();
 		TableGenerator tabGen = annElt.getAnnotation( TableGenerator.class );
 		SequenceGenerator seqGen = annElt.getAnnotation( SequenceGenerator.class );
 		GenericGenerator genGen = annElt.getAnnotation( GenericGenerator.class );
 		if ( tabGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( tabGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( seqGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( seqGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( genGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( genGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		return generators;
 	}
 
 	public static boolean isDefault(XClass clazz, MetadataBuildingContext context) {
 		return context.getBuildingOptions().getReflectionManager().equals( clazz, void.class );
 	}
 
 	/**
 	 * For the mapped entities build some temporary data-structure containing information about the
 	 * inheritance status of a class.
 	 *
 	 * @param orderedClasses Order list of all annotated entities and their mapped superclasses
 	 *
 	 * @return A map of {@code InheritanceState}s keyed against their {@code XClass}.
 	 */
 	public static Map<XClass, InheritanceState> buildInheritanceStates(
 			List<XClass> orderedClasses,
 			MetadataBuildingContext buildingContext) {
 		ReflectionManager reflectionManager = buildingContext.getBuildingOptions().getReflectionManager();
 		Map<XClass, InheritanceState> inheritanceStatePerClass = new HashMap<XClass, InheritanceState>(
 				orderedClasses.size()
 		);
 		for ( XClass clazz : orderedClasses ) {
 			InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState(
 					clazz, inheritanceStatePerClass
 			);
 			InheritanceState state = new InheritanceState( clazz, inheritanceStatePerClass, buildingContext );
 			if ( superclassState != null ) {
 				//the classes are ordered thus preventing an NPE
 				//FIXME if an entity has subclasses annotated @MappedSperclass wo sub @Entity this is wrong
 				superclassState.setHasSiblings( true );
 				InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 						clazz, inheritanceStatePerClass
 				);
 				state.setHasParents( superEntityState != null );
 				final boolean nonDefault = state.getType() != null && !InheritanceType.SINGLE_TABLE
 						.equals( state.getType() );
 				if ( superclassState.getType() != null ) {
 					final boolean mixingStrategy = state.getType() != null && !state.getType()
 							.equals( superclassState.getType() );
 					if ( nonDefault && mixingStrategy ) {
 						LOG.invalidSubStrategy( clazz.getName() );
 					}
 					state.setType( superclassState.getType() );
 				}
 			}
 			inheritanceStatePerClass.put( clazz, state );
 		}
 		return inheritanceStatePerClass;
 	}
 
 	private static boolean hasAnnotationsOnIdClass(XClass idClass) {
 //		if(idClass.getAnnotation(Embeddable.class) != null)
 //			return true;
 
 		List<XProperty> properties = idClass.getDeclaredProperties( XClass.ACCESS_FIELD );
 		for ( XProperty property : properties ) {
 			if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent( OneToMany.class ) ||
 					property.isAnnotationPresent( ManyToOne.class ) || property.isAnnotationPresent( Id.class ) ||
 					property.isAnnotationPresent( GeneratedValue.class ) || property.isAnnotationPresent( OneToOne.class ) ||
 					property.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		List<XMethod> methods = idClass.getDeclaredMethods();
 		for ( XMethod method : methods ) {
 			if ( method.isAnnotationPresent( Column.class ) || method.isAnnotationPresent( OneToMany.class ) ||
 					method.isAnnotationPresent( ManyToOne.class ) || method.isAnnotationPresent( Id.class ) ||
 					method.isAnnotationPresent( GeneratedValue.class ) || method.isAnnotationPresent( OneToOne.class ) ||
 					method.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
index 969575586a..eba3f2b70b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
@@ -1,886 +1,883 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.AnyMetaDefs;
 import org.hibernate.annotations.MetaValue;
 import org.hibernate.annotations.SqlFragmentAlias;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XPackage;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SyntheticProperty;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.jboss.logging.Logger;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 /**
  * @author Emmanuel Bernard
  */
 public class BinderHelper {
 
 	public static final String ANNOTATION_STRING_DEFAULT = "";
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BinderHelper.class.getName());
 
 	private BinderHelper() {
 	}
 
 	static {
 		Set<String> primitiveNames = new HashSet<String>();
 		primitiveNames.add( byte.class.getName() );
 		primitiveNames.add( short.class.getName() );
 		primitiveNames.add( int.class.getName() );
 		primitiveNames.add( long.class.getName() );
 		primitiveNames.add( float.class.getName() );
 		primitiveNames.add( double.class.getName() );
 		primitiveNames.add( char.class.getName() );
 		primitiveNames.add( boolean.class.getName() );
 		PRIMITIVE_NAMES = Collections.unmodifiableSet( primitiveNames );
 	}
 
 	public static final Set<String> PRIMITIVE_NAMES;
 
 	/**
 	 * create a property copy reusing the same value
 	 */
 	public static Property shallowCopy(Property property) {
 		Property clone = new Property();
 		clone.setCascade( property.getCascade() );
 		clone.setInsertable( property.isInsertable() );
 		clone.setLazy( property.isLazy() );
 		clone.setName( property.getName() );
-		clone.setNodeName( property.getNodeName() );
 		clone.setNaturalIdentifier( property.isNaturalIdentifier() );
 		clone.setOptimisticLocked( property.isOptimisticLocked() );
 		clone.setOptional( property.isOptional() );
 		clone.setPersistentClass( property.getPersistentClass() );
 		clone.setPropertyAccessorName( property.getPropertyAccessorName() );
 		clone.setSelectable( property.isSelectable() );
 		clone.setUpdateable( property.isUpdateable() );
 		clone.setValue( property.getValue() );
 		return clone;
 	}
 
 // This is sooooooooo close in terms of not generating a synthetic property if we do not have to (where property ref
 // refers to a single property).  The sticking point is cases where the `referencedPropertyName` come from subclasses
 // or secondary tables.  Part of the problem is in PersistentClass itself during attempts to resolve the referenced
 // property; currently it only considers non-subclass and non-joined properties.  Part of the problem is in terms
 // of SQL generation.
 //	public static void createSyntheticPropertyReference(
 //			Ejb3JoinColumn[] columns,
 //			PersistentClass ownerEntity,
 //			PersistentClass associatedEntity,
 //			Value value,
 //			boolean inverse,
 //			Mappings mappings) {
 //		//associated entity only used for more precise exception, yuk!
 //		if ( columns[0].isImplicit() || StringHelper.isNotEmpty( columns[0].getMappedBy() ) ) return;
 //		int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, ownerEntity, mappings );
 //		PersistentClass associatedClass = columns[0].getPropertyHolder() != null ?
 //				columns[0].getPropertyHolder().getPersistentClass() :
 //				null;
 //		if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 //			//find properties associated to a certain column
 //			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), mappings );
 //			List<Property> properties = findPropertiesByColumns( columnOwner, columns, mappings );
 //
 //			if ( properties == null ) {
 //				//TODO use a ToOne type doing a second select
 //				StringBuilder columnsList = new StringBuilder();
 //				columnsList.append( "referencedColumnNames(" );
 //				for (Ejb3JoinColumn column : columns) {
 //					columnsList.append( column.getReferencedColumn() ).append( ", " );
 //				}
 //				columnsList.setLength( columnsList.length() - 2 );
 //				columnsList.append( ") " );
 //
 //				if ( associatedEntity != null ) {
 //					//overidden destination
 //					columnsList.append( "of " )
 //							.append( associatedEntity.getEntityName() )
 //							.append( "." )
 //							.append( columns[0].getPropertyName() )
 //							.append( " " );
 //				}
 //				else {
 //					if ( columns[0].getPropertyHolder() != null ) {
 //						columnsList.append( "of " )
 //								.append( columns[0].getPropertyHolder().getEntityName() )
 //								.append( "." )
 //								.append( columns[0].getPropertyName() )
 //								.append( " " );
 //					}
 //				}
 //				columnsList.append( "referencing " )
 //						.append( ownerEntity.getEntityName() )
 //						.append( " not mapped to a single property" );
 //				throw new AnnotationException( columnsList.toString() );
 //			}
 //
 //			final String referencedPropertyName;
 //
 //			if ( properties.size() == 1 ) {
 //				referencedPropertyName = properties.get(0).getName();
 //			}
 //			else {
 //				// Create a synthetic (embedded composite) property to use as the referenced property which
 //				// contains all the properties mapped to the referenced columns.  We need to make a shallow copy
 //				// of the properties to mark them as non-insertable/updatable.
 //
 //				// todo : what if the columns all match with an existing component?
 //
 //				StringBuilder propertyNameBuffer = new StringBuilder( "_" );
 //				propertyNameBuffer.append( associatedClass.getEntityName().replace( '.', '_' ) );
 //				propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName() );
 //				String syntheticPropertyName = propertyNameBuffer.toString();
 //				//create an embeddable component
 //
 //				//todo how about properties.size() == 1, this should be much simpler
 //				Component embeddedComp = columnOwner instanceof PersistentClass ?
 //						new Component( mappings, (PersistentClass) columnOwner ) :
 //						new Component( mappings, (Join) columnOwner );
 //				embeddedComp.setEmbedded( true );
 //				embeddedComp.setNodeName( syntheticPropertyName );
 //				embeddedComp.setComponentClassName( embeddedComp.getOwner().getClassName() );
 //				for (Property property : properties) {
 //					Property clone = BinderHelper.shallowCopy( property );
 //					clone.setInsertable( false );
 //					clone.setUpdateable( false );
 //					clone.setNaturalIdentifier( false );
 //					clone.setGeneration( property.getGeneration() );
 //					embeddedComp.addProperty( clone );
 //				}
 //				SyntheticProperty synthProp = new SyntheticProperty();
 //				synthProp.setName( syntheticPropertyName );
 //				synthProp.setNodeName( syntheticPropertyName );
 //				synthProp.setPersistentClass( ownerEntity );
 //				synthProp.setUpdateable( false );
 //				synthProp.setInsertable( false );
 //				synthProp.setValue( embeddedComp );
 //				synthProp.setPropertyAccessorName( "embedded" );
 //				ownerEntity.addProperty( synthProp );
 //				//make it unique
 //				TableBinder.createUniqueConstraint( embeddedComp );
 //
 //				referencedPropertyName = syntheticPropertyName;
 //			}
 //
 //			/**
 //			 * creating the property ref to the new synthetic property
 //			 */
 //			if ( value instanceof ToOne ) {
 //				( (ToOne) value ).setReferencedPropertyName( referencedPropertyName );
 //				mappings.addUniquePropertyReference( ownerEntity.getEntityName(), referencedPropertyName );
 //			}
 //			else if ( value instanceof Collection ) {
 //				( (Collection) value ).setReferencedPropertyName( referencedPropertyName );
 //				//not unique because we could create a mtm wo association table
 //				mappings.addPropertyReference( ownerEntity.getEntityName(), referencedPropertyName );
 //			}
 //			else {
 //				throw new AssertionFailure(
 //						"Do a property ref on an unexpected Value type: "
 //								+ value.getClass().getName()
 //				);
 //			}
 //			mappings.addPropertyReferencedAssociation(
 //					( inverse ? "inverse__" : "" ) + associatedClass.getEntityName(),
 //					columns[0].getPropertyName(),
 //					referencedPropertyName
 //			);
 //		}
 //	}
 
 	public static void createSyntheticPropertyReference(
 			Ejb3JoinColumn[] columns,
 			PersistentClass ownerEntity,
 			PersistentClass associatedEntity,
 			Value value,
 			boolean inverse,
 			MetadataBuildingContext context) {
 		//associated entity only used for more precise exception, yuk!
 		if ( columns[0].isImplicit() || StringHelper.isNotEmpty( columns[0].getMappedBy() ) ) return;
 		int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, ownerEntity, context );
 		PersistentClass associatedClass = columns[0].getPropertyHolder() != null ?
 				columns[0].getPropertyHolder().getPersistentClass() :
 				null;
 		if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 			/**
 			 * Create a synthetic property to refer to including an
 			 * embedded component value containing all the properties
 			 * mapped to the referenced columns
 			 * We need to shallow copy those properties to mark them
 			 * as non insertable / non updatable
 			 */
 			StringBuilder propertyNameBuffer = new StringBuilder( "_" );
 			propertyNameBuffer.append( associatedClass.getEntityName().replace( '.', '_' ) );
 			propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName().replace( '.', '_' ) );
 			String syntheticPropertyName = propertyNameBuffer.toString();
 			//find properties associated to a certain column
 			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), context );
 			List<Property> properties = findPropertiesByColumns( columnOwner, columns, context );
 			//create an embeddable component
                         Property synthProp = null;
 			if ( properties != null ) {
                         //todo how about properties.size() == 1, this should be much simpler
 				Component embeddedComp = columnOwner instanceof PersistentClass ?
 						new Component( context.getMetadataCollector(), (PersistentClass) columnOwner ) :
 						new Component( context.getMetadataCollector(), (Join) columnOwner );
 				embeddedComp.setEmbedded( true );
-				embeddedComp.setNodeName( syntheticPropertyName );
 				embeddedComp.setComponentClassName( embeddedComp.getOwner().getClassName() );
 				for (Property property : properties) {
 					Property clone = BinderHelper.shallowCopy( property );
 					clone.setInsertable( false );
 					clone.setUpdateable( false );
 					clone.setNaturalIdentifier( false );
 					clone.setValueGenerationStrategy( property.getValueGenerationStrategy() );
 					embeddedComp.addProperty( clone );
                                 }
                                     synthProp = new SyntheticProperty();
 				synthProp.setName( syntheticPropertyName );
-				synthProp.setNodeName( syntheticPropertyName );
 				synthProp.setPersistentClass( ownerEntity );
 				synthProp.setUpdateable( false );
 				synthProp.setInsertable( false );
 				synthProp.setValue( embeddedComp );
 				synthProp.setPropertyAccessorName( "embedded" );
 				ownerEntity.addProperty( synthProp );
                                 //make it unique
 				TableBinder.createUniqueConstraint( embeddedComp );
                             }
 			else {
 				//TODO use a ToOne type doing a second select
 				StringBuilder columnsList = new StringBuilder();
 				columnsList.append( "referencedColumnNames(" );
 				for (Ejb3JoinColumn column : columns) {
 					columnsList.append( column.getReferencedColumn() ).append( ", " );
 				}
 				columnsList.setLength( columnsList.length() - 2 );
 				columnsList.append( ") " );
 
 				if ( associatedEntity != null ) {
 					//overidden destination
 					columnsList.append( "of " )
 							.append( associatedEntity.getEntityName() )
 							.append( "." )
 							.append( columns[0].getPropertyName() )
 							.append( " " );
 				}
 				else {
 					if ( columns[0].getPropertyHolder() != null ) {
 						columnsList.append( "of " )
 								.append( columns[0].getPropertyHolder().getEntityName() )
 								.append( "." )
 								.append( columns[0].getPropertyName() )
 								.append( " " );
 					}
 				}
 				columnsList.append( "referencing " )
 						.append( ownerEntity.getEntityName() )
 						.append( " not mapped to a single property" );
 				throw new AnnotationException( columnsList.toString() );
 			}
 
 			/**
 			 * creating the property ref to the new synthetic property
 			 */
 			if ( value instanceof ToOne ) {
 				( (ToOne) value ).setReferencedPropertyName( syntheticPropertyName );
 				( (ToOne) value ).setReferenceToPrimaryKey( syntheticPropertyName == null );
 				context.getMetadataCollector().addUniquePropertyReference(
 						ownerEntity.getEntityName(),
 						syntheticPropertyName
 				);
 			}
 			else if ( value instanceof Collection ) {
 				( (Collection) value ).setReferencedPropertyName( syntheticPropertyName );
 				//not unique because we could create a mtm wo association table
 				context.getMetadataCollector().addPropertyReference(
 						ownerEntity.getEntityName(),
 						syntheticPropertyName
 				);
 			}
 			else {
 				throw new AssertionFailure(
 						"Do a property ref on an unexpected Value type: "
 								+ value.getClass().getName()
 				);
 			}
 			context.getMetadataCollector().addPropertyReferencedAssociation(
 					( inverse ? "inverse__" : "" ) + associatedClass.getEntityName(),
 					columns[0].getPropertyName(),
 					syntheticPropertyName
 			);
 		}
 	}
 
 
 	private static List<Property> findPropertiesByColumns(
 			Object columnOwner,
 			Ejb3JoinColumn[] columns,
 			MetadataBuildingContext context) {
 		Map<Column, Set<Property>> columnsToProperty = new HashMap<Column, Set<Property>>();
 		List<Column> orderedColumns = new ArrayList<Column>( columns.length );
 		Table referencedTable = null;
 		if ( columnOwner instanceof PersistentClass ) {
 			referencedTable = ( (PersistentClass) columnOwner ).getTable();
 		}
 		else if ( columnOwner instanceof Join ) {
 			referencedTable = ( (Join) columnOwner ).getTable();
 		}
 		else {
 			throw new AssertionFailure(
 					columnOwner == null ?
 							"columnOwner is null" :
 							"columnOwner neither PersistentClass nor Join: " + columnOwner.getClass()
 			);
 		}
 		//build the list of column names
 		for (Ejb3JoinColumn column1 : columns) {
 			Column column = new Column(
 					context.getMetadataCollector().getPhysicalColumnName(
 							referencedTable,
 							column1.getReferencedColumn()
 					)
 			);
 			orderedColumns.add( column );
 			columnsToProperty.put( column, new HashSet<Property>() );
 		}
 		boolean isPersistentClass = columnOwner instanceof PersistentClass;
 		Iterator it = isPersistentClass ?
 				( (PersistentClass) columnOwner ).getPropertyIterator() :
 				( (Join) columnOwner ).getPropertyIterator();
 		while ( it.hasNext() ) {
 			matchColumnsByProperty( (Property) it.next(), columnsToProperty );
 		}
 		if ( isPersistentClass ) {
 			matchColumnsByProperty( ( (PersistentClass) columnOwner ).getIdentifierProperty(), columnsToProperty );
 		}
 
 		//first naive implementation
 		//only check 1 columns properties
 		//TODO make it smarter by checking correctly ordered multi column properties
 		List<Property> orderedProperties = new ArrayList<Property>();
 		for (Column column : orderedColumns) {
 			boolean found = false;
 			for (Property property : columnsToProperty.get( column ) ) {
 				if ( property.getColumnSpan() == 1 ) {
 					orderedProperties.add( property );
 					found = true;
 					break;
 				}
 			}
 			if ( !found ) return null; //have to find it the hard way
 		}
 		return orderedProperties;
 	}
 
 	private static void matchColumnsByProperty(Property property, Map<Column, Set<Property>> columnsToProperty) {
 		if ( property == null ) return;
 		if ( "noop".equals( property.getPropertyAccessorName() )
 				|| "embedded".equals( property.getPropertyAccessorName() ) ) {
 			return;
 		}
 // FIXME cannot use subproperties becasue the caller needs top level properties
 //		if ( property.isComposite() ) {
 //			Iterator subProperties = ( (Component) property.getValue() ).getPropertyIterator();
 //			while ( subProperties.hasNext() ) {
 //				matchColumnsByProperty( (Property) subProperties.next(), columnsToProperty );
 //			}
 //		}
 		else {
 			Iterator columnIt = property.getColumnIterator();
 			while ( columnIt.hasNext() ) {
 				Object column = columnIt.next(); //can be a Formula so we don't cast
 				//noinspection SuspiciousMethodCalls
 				if ( columnsToProperty.containsKey( column ) ) {
 					columnsToProperty.get( column ).add( property );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Retrieve the property by path in a recursive way, including IndetifierProperty in the loop
 	 * If propertyName is null or empty, the IdentifierProperty is returned
 	 */
 	public static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
 		Property property = null;
 		Property idProperty = associatedClass.getIdentifierProperty();
 		String idName = idProperty != null ? idProperty.getName() : null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0
 					|| propertyName.equals( idName ) ) {
 				//default to id
 				property = idProperty;
 			}
 			else {
 				if ( propertyName.indexOf( idName + "." ) == 0 ) {
 					property = idProperty;
 					propertyName = propertyName.substring( idName.length() + 1 );
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch (MappingException e) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( associatedClass.getIdentifierMapper() == null ) return null;
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch (MappingException ee) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	/**
 	 * Retrieve the property by path in a recursive way
 	 */
 	public static Property findPropertyByName(Component component, String propertyName) {
 		Property property = null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0) {
 				// Do not expect to use a primary key for this case
 				return null;
 			}
 			else {
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = component.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch (MappingException e) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( component.getOwner().getIdentifierMapper() == null ) return null;
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = component.getOwner().getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch (MappingException ee) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	public static String getRelativePath(PropertyHolder propertyHolder, String propertyName) {
 		if ( propertyHolder == null ) return propertyName;
 		String path = propertyHolder.getPath();
 		String entityName = propertyHolder.getPersistentClass().getEntityName();
 		if ( path.length() == entityName.length() ) {
 			return propertyName;
 		}
 		else {
 			return StringHelper.qualify( path.substring( entityName.length() + 1 ), propertyName );
 		}
 	}
 
 	/**
 	 * Find the column owner (ie PersistentClass or Join) of columnName.
 	 * If columnName is null or empty, persistentClass is returned
 	 */
 	public static Object findColumnOwner(
 			PersistentClass persistentClass,
 			String columnName,
 			MetadataBuildingContext context) {
 		if ( StringHelper.isEmpty( columnName ) ) {
 			return persistentClass; //shortcut for implicit referenced column names
 		}
 		PersistentClass current = persistentClass;
 		Object result;
 		boolean found = false;
 		do {
 			result = current;
 			Table currentTable = current.getTable();
 			try {
 				context.getMetadataCollector().getPhysicalColumnName( currentTable, columnName );
 				found = true;
 			}
 			catch (MappingException me) {
 				//swallow it
 			}
 			Iterator joins = current.getJoinIterator();
 			while ( !found && joins.hasNext() ) {
 				result = joins.next();
 				currentTable = ( (Join) result ).getTable();
 				try {
 					context.getMetadataCollector().getPhysicalColumnName( currentTable, columnName );
 					found = true;
 				}
 				catch (MappingException me) {
 					//swallow it
 				}
 			}
 			current = current.getSuperclass();
 		}
 		while ( !found && current != null );
 		return found ? result : null;
 	}
 
 	/**
 	 * apply an id generator to a SimpleValue
 	 */
 	public static void makeIdGenerator(
 			SimpleValue id,
 			String generatorType,
 			String generatorName,
 			MetadataBuildingContext buildingContext,
 			Map<String, IdentifierGeneratorDefinition> localGenerators) {
 		Table table = id.getTable();
 		table.setIdentifierValue( id );
 		//generator settings
 		id.setIdentifierGeneratorStrategy( generatorType );
 
 		Properties params = new Properties();
 
 		//always settable
 		params.setProperty(
 				PersistentIdentifierGenerator.TABLE, table.getName()
 		);
 
 		final String implicitCatalogName = buildingContext.getBuildingOptions().getMappingDefaults().getImplicitCatalogName();
 		if ( implicitCatalogName != null ) {
 			params.put( PersistentIdentifierGenerator.CATALOG, implicitCatalogName );
 		}
 		final String implicitSchemaName = buildingContext.getBuildingOptions().getMappingDefaults().getImplicitSchemaName();
 		if ( implicitSchemaName != null ) {
 			params.put( PersistentIdentifierGenerator.SCHEMA, implicitSchemaName );
 		}
 
 		if ( id.getColumnSpan() == 1 ) {
 			params.setProperty(
 					PersistentIdentifierGenerator.PK,
 					( (org.hibernate.mapping.Column) id.getColumnIterator().next() ).getName()
 			);
 		}
 		// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 		params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, buildingContext.getObjectNameNormalizer() );
 
 		if ( !isEmptyAnnotationValue( generatorName ) ) {
 			//we have a named generator
 			IdentifierGeneratorDefinition gen = getIdentifierGenerator( generatorName, localGenerators, buildingContext );
 			if ( gen == null ) {
 				throw new AnnotationException( "Unknown Id.generator: " + generatorName );
 			}
 			//This is quite vague in the spec but a generator could override the generate choice
 			String identifierGeneratorStrategy = gen.getStrategy();
 			//yuk! this is a hack not to override 'AUTO' even if generator is set
 			final boolean avoidOverriding =
 					identifierGeneratorStrategy.equals( "identity" )
 							|| identifierGeneratorStrategy.equals( "seqhilo" )
 							|| identifierGeneratorStrategy.equals( MultipleHiLoPerTableGenerator.class.getName() );
 			if ( generatorType == null || !avoidOverriding ) {
 				id.setIdentifierGeneratorStrategy( identifierGeneratorStrategy );
 			}
 			//checkIfMatchingGenerator(gen, generatorType, generatorName);
 			for ( Object o : gen.getParameters().entrySet() ) {
 				Map.Entry elt = (Map.Entry) o;
 				params.setProperty( (String) elt.getKey(), (String) elt.getValue() );
 			}
 		}
 		if ( "assigned".equals( generatorType ) ) id.setNullValue( "undefined" );
 		id.setIdentifierGeneratorProperties( params );
 	}
 
 	public static IdentifierGeneratorDefinition getIdentifierGenerator(
 			String name,
 			Map<String, IdentifierGeneratorDefinition> localGenerators,
 			MetadataBuildingContext buildingContext) {
 		if ( localGenerators != null ) {
 			final IdentifierGeneratorDefinition result = localGenerators.get( name );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		return buildingContext.getMetadataCollector().getIdentifierGenerator( name );
 	}
 
 	public static boolean isEmptyAnnotationValue(String annotationString) {
 		return annotationString != null && annotationString.length() == 0;
 		//equivalent to (but faster) ANNOTATION_STRING_DEFAULT.equals( annotationString );
 	}
 
 	public static Any buildAnyValue(
 			String anyMetaDefName,
 			Ejb3JoinColumn[] columns,
 			javax.persistence.Column metaColumn,
 			PropertyData inferredData,
 			boolean cascadeOnDelete,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			EntityBinder entityBinder,
 			boolean optional,
 			MetadataBuildingContext context) {
 		//All FK columns should be in the same table
 		Any value = new Any( context.getMetadataCollector(), columns[0].getTable() );
 		AnyMetaDef metaAnnDef = inferredData.getProperty().getAnnotation( AnyMetaDef.class );
 
 		if ( metaAnnDef != null ) {
 			//local has precedence over general and can be mapped for future reference if named
 			bindAnyMetaDefs( inferredData.getProperty(), context );
 		}
 		else {
 			metaAnnDef = context.getMetadataCollector().getAnyMetaDef( anyMetaDefName );
 		}
 		if ( metaAnnDef != null ) {
 			value.setIdentifierType( metaAnnDef.idType() );
 			value.setMetaType( metaAnnDef.metaType() );
 
 			HashMap values = new HashMap();
 			org.hibernate.type.Type metaType = context.getMetadataCollector().getTypeResolver().heuristicType( value.getMetaType() );
 			for (MetaValue metaValue : metaAnnDef.metaValues()) {
 				try {
 					Object discrim = ( (org.hibernate.type.DiscriminatorType) metaType ).stringToObject( metaValue
 							.value() );
 					String entityName = metaValue.targetEntity().getName();
 					values.put( discrim, entityName );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException( "metaType was not a DiscriminatorType: "
 							+ metaType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException( "could not interpret metaValue", e );
 				}
 			}
 			if ( !values.isEmpty() ) value.setMetaValues( values );
 		}
 		else {
 			throw new AnnotationException( "Unable to find @AnyMetaDef for an @(ManyTo)Any mapping: "
 					+ StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() ) );
 		}
 
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		if ( !optional ) {
 			for (Ejb3JoinColumn column : columns) {
 				column.setNullable( false );
 			}
 		}
 
 		Ejb3Column[] metaColumns = Ejb3Column.buildColumnFromAnnotation(
 				new javax.persistence.Column[] { metaColumn },
 				null,
 				nullability,
 				propertyHolder,
 				inferredData,
 				entityBinder.getSecondaryTables(),
 				context
 		);
 
 		//set metaColumn to the right table
 		for (Ejb3Column column : metaColumns) {
 			column.setTable( value.getTable() );
 		}
 		//meta column
 		for (Ejb3Column column : metaColumns) {
 			column.linkWithValue( value );
 		}
 
 		//id columns
 		final String propertyName = inferredData.getPropertyName();
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + "." + propertyName );
 		for (Ejb3JoinColumn column : columns) {
 			column.linkWithValue( value );
 		}
 		return value;
 	}
 
 	public static void bindAnyMetaDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
 		AnyMetaDef defAnn = annotatedElement.getAnnotation( AnyMetaDef.class );
 		AnyMetaDefs defsAnn = annotatedElement.getAnnotation( AnyMetaDefs.class );
 		boolean mustHaveName = XClass.class.isAssignableFrom( annotatedElement.getClass() )
 				|| XPackage.class.isAssignableFrom( annotatedElement.getClass() );
 		if ( defAnn != null ) {
 			checkAnyMetaDefValidity( mustHaveName, defAnn, annotatedElement );
 			bindAnyMetaDef( defAnn, context );
 		}
 		if ( defsAnn != null ) {
 			for (AnyMetaDef def : defsAnn.value()) {
 				checkAnyMetaDefValidity( mustHaveName, def, annotatedElement );
 				bindAnyMetaDef( def, context );
 			}
 		}
 	}
 
 	private static void checkAnyMetaDefValidity(boolean mustHaveName, AnyMetaDef defAnn, XAnnotatedElement annotatedElement) {
 		if ( mustHaveName && isEmptyAnnotationValue( defAnn.name() ) ) {
 			String name = XClass.class.isAssignableFrom( annotatedElement.getClass() ) ?
 					( (XClass) annotatedElement ).getName() :
 					( (XPackage) annotatedElement ).getName();
 			throw new AnnotationException( "@AnyMetaDef.name cannot be null on an entity or a package: " + name );
 		}
 	}
 
 	private static void bindAnyMetaDef(AnyMetaDef defAnn, MetadataBuildingContext context) {
 		if ( isEmptyAnnotationValue( defAnn.name() ) ) {
 			return; //don't map not named definitions
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding Any Meta definition: %s", defAnn.name() );
 		}
 		context.getMetadataCollector().addAnyMetaDef( defAnn );
 	}
 
 	public static MappedSuperclass getMappedSuperclassOrNull(
 			XClass declaringClass,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			MetadataBuildingContext context) {
 		boolean retrieve = false;
 		if ( declaringClass != null ) {
 			final InheritanceState inheritanceState = inheritanceStatePerClass.get( declaringClass );
 			if ( inheritanceState == null ) {
 				throw new org.hibernate.annotations.common.AssertionFailure(
 						"Declaring class is not found in the inheritance state hierarchy: " + declaringClass
 				);
 			}
 			if ( inheritanceState.isEmbeddableSuperclass() ) {
 				retrieve = true;
 			}
 		}
 
 		if ( retrieve ) {
 			return context.getMetadataCollector().getMappedSuperclass(
 					context.getBuildingOptions().getReflectionManager().toClass( declaringClass )
 			);
 		}
 		else {
 			return null;
 		}
 	}
 
 	public static String getPath(PropertyHolder holder, PropertyData property) {
 		return StringHelper.qualify( holder.getPath(), property.getPropertyName() );
 	}
 
 	static PropertyData getPropertyOverriddenByMapperOrMapsId(
 			boolean isId,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			MetadataBuildingContext buildingContext) {
 		final XClass persistentXClass;
 		try {
 			 persistentXClass = buildingContext.getBuildingOptions().getReflectionManager()
 					.classForName( propertyHolder.getPersistentClass().getClassName() );
 		}
 		catch ( ClassLoadingException e ) {
 			throw new AssertionFailure( "PersistentClass name cannot be converted into a Class", e);
 		}
 		if ( propertyHolder.isInIdClass() ) {
 			PropertyData pd = buildingContext.getMetadataCollector().getPropertyAnnotatedWithIdAndToOne(
 					persistentXClass,
 					propertyName
 			);
 			if ( pd == null && buildingContext.getBuildingOptions().isSpecjProprietarySyntaxEnabled() ) {
 				pd = buildingContext.getMetadataCollector().getPropertyAnnotatedWithMapsId(
 						persistentXClass,
 						propertyName
 				);
 			}
 			return pd;
 		}
         String propertyPath = isId ? "" : propertyName;
         return buildingContext.getMetadataCollector().getPropertyAnnotatedWithMapsId( persistentXClass, propertyPath );
 	}
 	
 	public static Map<String,String> toAliasTableMap(SqlFragmentAlias[] aliases){
 		Map<String,String> ret = new HashMap<String,String>();
 		for (int i = 0; i < aliases.length; i++){
 			if (StringHelper.isNotEmpty(aliases[i].table())){
 				ret.put(aliases[i].alias(), aliases[i].table());
 }
 		}
 		return ret;
 	}
 	
 	public static Map<String,String> toAliasEntityMap(SqlFragmentAlias[] aliases){
 		Map<String,String> ret = new HashMap<String,String>();
 		for (int i = 0; i < aliases.length; i++){
 			if (aliases[i].entity() != void.class){
 				ret.put(aliases[i].alias(), aliases[i].entity().getName());
 			}
 		}
 		return ret;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
index 796d817680..8bf3e8cad8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
@@ -1,156 +1,155 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SimpleValue;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class CopyIdentifierComponentSecondPass implements SecondPass {
 	private static final Logger log = Logger.getLogger( CopyIdentifierComponentSecondPass.class );
 
 	private final String referencedEntityName;
 	private final Component component;
 	private final MetadataBuildingContext buildingContext;
 	private final Ejb3JoinColumn[] joinColumns;
 
 	public CopyIdentifierComponentSecondPass(
 			Component comp,
 			String referencedEntityName,
 			Ejb3JoinColumn[] joinColumns,
 			MetadataBuildingContext buildingContext) {
 		this.component = comp;
 		this.referencedEntityName = referencedEntityName;
 		this.buildingContext = buildingContext;
 		this.joinColumns = joinColumns;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		PersistentClass referencedPersistentClass = (PersistentClass) persistentClasses.get( referencedEntityName );
 		// TODO better error names
 		if ( referencedPersistentClass == null ) {
 			throw new AnnotationException( "Unknown entity name: " + referencedEntityName );
 		}
 		if ( ! ( referencedPersistentClass.getIdentifier() instanceof Component ) ) {
 			throw new AssertionFailure(
 					"Unexpected identifier type on the referenced entity when mapping a @MapsId: "
 							+ referencedEntityName
 			);
 		}
 		Component referencedComponent = (Component) referencedPersistentClass.getIdentifier();
 		Iterator<Property> properties = referencedComponent.getPropertyIterator();
 
 
 		//prepare column name structure
 		boolean isExplicitReference = true;
 		Map<String, Ejb3JoinColumn> columnByReferencedName = new HashMap<String, Ejb3JoinColumn>(joinColumns.length);
 		for (Ejb3JoinColumn joinColumn : joinColumns) {
 			final String referencedColumnName = joinColumn.getReferencedColumn();
 			if ( referencedColumnName == null || BinderHelper.isEmptyAnnotationValue( referencedColumnName ) ) {
 				break;
 			}
 			//JPA 2 requires referencedColumnNames to be case insensitive
 			columnByReferencedName.put( referencedColumnName.toLowerCase(Locale.ROOT), joinColumn );
 		}
 		//try default column orientation
 		int index = 0;
 		if ( columnByReferencedName.isEmpty() ) {
 			isExplicitReference = false;
 			for (Ejb3JoinColumn joinColumn : joinColumns) {
 				columnByReferencedName.put( "" + index, joinColumn );
 				index++;
 			}
 			index = 0;
 		}
 
 		while ( properties.hasNext() ) {
 			Property referencedProperty = properties.next();
 			if ( referencedProperty.isComposite() ) {
 				throw new AssertionFailure( "Unexpected nested component on the referenced entity when mapping a @MapsId: "
 						+ referencedEntityName);
 			}
 			else {
 				Property property = new Property();
 				property.setName( referencedProperty.getName() );
-				property.setNodeName( referencedProperty.getNodeName() );
 				//FIXME set optional?
 				//property.setOptional( property.isOptional() );
 				property.setPersistentClass( component.getOwner() );
 				property.setPropertyAccessorName( referencedProperty.getPropertyAccessorName() );
 				SimpleValue value = new SimpleValue( buildingContext.getMetadataCollector(), component.getTable() );
 				property.setValue( value );
 				final SimpleValue referencedValue = (SimpleValue) referencedProperty.getValue();
 				value.setTypeName( referencedValue.getTypeName() );
 				value.setTypeParameters( referencedValue.getTypeParameters() );
 				final Iterator<Selectable> columns = referencedValue.getColumnIterator();
 
 				if ( joinColumns[0].isNameDeferred() ) {
 					joinColumns[0].copyReferencedStructureAndCreateDefaultJoinColumns(
 						referencedPersistentClass,
 						columns,
 						value);
 				}
 				else {
 					//FIXME take care of Formula
 					while ( columns.hasNext() ) {
 						final Selectable selectable = columns.next();
 						if ( ! Column.class.isInstance( selectable ) ) {
 							log.debug( "Encountered formula definition; skipping" );
 							continue;
 						}
 						final Column column = (Column) selectable;
 						final Ejb3JoinColumn joinColumn;
 						String logicalColumnName = null;
 						if ( isExplicitReference ) {
 							final String columnName = column.getName();
 							logicalColumnName = buildingContext.getMetadataCollector().getLogicalColumnName(
 									referencedPersistentClass.getTable(),
 									columnName
 							);
 							//JPA 2 requires referencedColumnNames to be case insensitive
 							joinColumn = columnByReferencedName.get( logicalColumnName.toLowerCase(Locale.ROOT ) );
 						}
 						else {
 							joinColumn = columnByReferencedName.get( "" + index );
 							index++;
 						}
 						if ( joinColumn == null && ! joinColumns[0].isNameDeferred() ) {
 							throw new AnnotationException(
 									isExplicitReference ?
 											"Unable to find column reference in the @MapsId mapping: " + logicalColumnName :
 											"Implicit column reference in the @MapsId mapping fails, try to use explicit referenceColumnNames: " + referencedEntityName
 							);
 						}
 						final String columnName = joinColumn == null || joinColumn.isNameDeferred() ? "tata_" + column.getName() : joinColumn
 								.getName();
 						value.addColumn( new Column( columnName ) );
 						column.setValue( value );
 					}
 				}
 				component.addProperty( property );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 1203b80b70..3650d9ab09 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -1,1388 +1,1387 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.CollectionTable;
 import javax.persistence.ConstraintMode;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionType;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.FilterJoinTables;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.LazyCollection;
 import org.hibernate.annotations.LazyCollectionOption;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortComparator;
 import org.hibernate.annotations.SortNatural;
 import org.hibernate.annotations.SortType;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionPropertyHolder;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyInferredData;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 /**
  * Base class for binding different types of collections to Hibernate configuration objects.
  *
  * @author inger
  * @author Emmanuel Bernard
  */
 @SuppressWarnings({"unchecked", "serial"})
 public abstract class CollectionBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionBinder.class.getName());
 
 	private MetadataBuildingContext buildingContext;
 
 	protected Collection collection;
 	protected String propertyName;
 	PropertyHolder propertyHolder;
 	int batchSize;
 	private String mappedBy;
 	private XClass collectionType;
 	private XClass targetEntity;
 	private Ejb3JoinColumn[] inverseJoinColumns;
 	private String cascadeStrategy;
 	String cacheConcurrencyStrategy;
 	String cacheRegionName;
 	private boolean oneToMany;
 	protected IndexColumn indexColumn;
 	protected boolean cascadeDeleteEnabled;
 	protected String mapKeyPropertyName;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private Ejb3JoinColumn[] fkJoinColumns;
 	private boolean isExplicitAssociationTable;
 	private Ejb3Column[] elementColumns;
 	private boolean isEmbedded;
 	private XProperty property;
 	private boolean ignoreNotFound;
 	private TableBinder tableBinder;
 	private Ejb3Column[] mapKeyColumns;
 	private Ejb3JoinColumn[] mapKeyManyToManyColumns;
 	protected HashMap<String, IdentifierGeneratorDefinition> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	private boolean isSortedCollection;
 	private javax.persistence.OrderBy jpaOrderBy;
 	private OrderBy sqlOrderBy;
 	private Sort deprecatedSort;
 	private SortNatural naturalSort;
 	private SortComparator comparatorSort;
 
 	private String explicitType;
 	private final Properties explicitTypeParameters = new Properties();
 
 	protected MetadataBuildingContext getBuildingContext() {
 		return buildingContext;
 	}
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
 		this.hibernateExtensionMapping = hibernateExtensionMapping;
 	}
 
 	protected boolean isHibernateExtensionMapping() {
 		return hibernateExtensionMapping;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setCascadeStrategy(String cascadeStrategy) {
 		this.cascadeStrategy = cascadeStrategy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
 		this.inverseJoinColumns = inverseJoinColumns;
 	}
 
 	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
 		this.joinColumns = joinColumns;
 	}
 
 	private Ejb3JoinColumn[] joinColumns;
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	public void setBatchSize(BatchSize batchSize) {
 		this.batchSize = batchSize == null ? -1 : batchSize.size();
 	}
 
 	public void setJpaOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		this.jpaOrderBy = jpaOrderBy;
 	}
 
 	public void setSqlOrderBy(OrderBy sqlOrderBy) {
 		this.sqlOrderBy = sqlOrderBy;
 	}
 
 	public void setSort(Sort deprecatedSort) {
 		this.deprecatedSort = deprecatedSort;
 	}
 
 	public void setNaturalSort(SortNatural naturalSort) {
 		this.naturalSort = naturalSort;
 	}
 
 	public void setComparatorSort(SortComparator comparatorSort) {
 		this.comparatorSort = comparatorSort;
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			MetadataBuildingContext buildingContext) {
 		final CollectionBinder result;
 		if ( property.isArray() ) {
 			if ( property.getElementClass().isPrimitive() ) {
 				result = new PrimitiveArrayBinder();
 			}
 			else {
 				result = new ArrayBinder();
 			}
 		}
 		else if ( property.isCollection() ) {
 			//TODO consider using an XClass
 			Class returnedClass = property.getCollectionClass();
 			if ( java.util.Set.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( false );
 			}
 			else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( true );
 			}
 			else if ( java.util.Map.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( false );
 			}
 			else if ( java.util.SortedMap.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( true );
 			}
 			else if ( java.util.Collection.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else if ( java.util.List.class.equals( returnedClass ) ) {
 				if ( isIndexed ) {
 					if ( property.isAnnotationPresent( CollectionId.class ) ) {
 						throw new AnnotationException(
 								"List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: "
 								+ StringHelper.qualify( entityName, property.getName() ) );
 					}
 					result = new ListBinder();
 				}
 				else if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else {
 				throw new AnnotationException(
 						returnedClass.getName() + " collection not yet supported: "
 								+ StringHelper.qualify( entityName, property.getName() )
 				);
 			}
 		}
 		else {
 			throw new AnnotationException(
 					"Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: "
 							+ StringHelper.qualify( entityName, property.getName() )
 			);
 		}
 		result.setIsHibernateExtensionMapping( isHibernateExtensionMapping );
 
 		final CollectionType typeAnnotation = property.getAnnotation( CollectionType.class );
 		if ( typeAnnotation != null ) {
 			final String typeName = typeAnnotation.type();
 			// see if it names a type-def
 			final TypeDefinition typeDef = buildingContext.getMetadataCollector().getTypeDefinition( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeImplementorClass().getName();
 				result.explicitTypeParameters.putAll( typeDef.getParameters() );
 			}
 			else {
 				result.explicitType = typeName;
 				for ( Parameter param : typeAnnotation.parameters() ) {
 					result.explicitTypeParameters.setProperty( param.name(), param.value() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	protected CollectionBinder(boolean isSortedCollection) {
 		this.isSortedCollection = isSortedCollection;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		// NOTE: really really badly named.  This is actually NOT the collection-type, but rather the collection-element-type!
 		this.collectionType = collectionType;
 	}
 
 	public void setTargetEntity(XClass targetEntity) {
 		this.targetEntity = targetEntity;
 	}
 
 	protected abstract Collection createCollection(PersistentClass persistentClass);
 
 	public Collection getCollection() {
 		return collection;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	public void bind() {
 		this.collection = createCollection( propertyHolder.getPersistentClass() );
 		String role = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 		LOG.debugf( "Collection role: %s", role );
 		collection.setRole( role );
-		collection.setNodeName( propertyName );
 		collection.setMappedByProperty( mappedBy );
 
 		if ( property.isAnnotationPresent( MapKeyColumn.class )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		// set explicit type information
 		if ( explicitType != null ) {
 			final TypeDefinition typeDef = buildingContext.getMetadataCollector().getTypeDefinition( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeImplementorClass().getName() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		final OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 				? ! lockAnn.excluded()
 				: ! isMappedBy;
 		collection.setOptimisticLocked( includeInOptimisticLockChecks );
 
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
 
 		applySortingAndOrdering( collection );
 
 		//set cache
 		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
 			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
 			collection.setCacheRegionName( cacheRegionName );
 		}
 
 		//SQL overriding
 		SQLInsert sqlInsert = property.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = property.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = property.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = property.getAnnotation( SQLDeleteAll.class );
 		Loader loader = property.getAnnotation( Loader.class );
 		if ( sqlInsert != null ) {
 			collection.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase(Locale.ROOT) )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			buildingContext.getMetadataCollector().addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns,
 				mapKeyManyToManyColumns,
 				isEmbedded,
 				property,
 				collectionType,
 				ignoreNotFound,
 				oneToMany,
 				tableBinder,
 				buildingContext
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 
 		buildingContext.getMetadataCollector().addCollectionBinding( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setLazy( collection.isLazy() );
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, buildingContext ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, collection ) {
 			@Override
             public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses,
 						collType,
 						fkJoinColumns,
 						keyColumns,
 						inverseColumns,
 						elementColumns,
 						isEmbedded,
 						property,
 						unique,
 						assocTableBinder,
 						ignoreNotFound,
 						buildingContext
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder,
 					property,
 					propertyHolder,
 					buildingContext
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( buildingContext.getMetadataCollector(), collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String orderByFragment = buildOrderByClauseFromHql(
 					jpaOrderBy.value(),
 					associatedClass,
 					collection.getRole()
 			);
 			if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 				collection.setOrderBy( orderByFragment );
 			}
 		}
 
 		if ( buildingContext == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = buildingContext.getMetadataCollector().getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		if ( debugEnabled ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, buildingContext );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = buildingContext.getMetadataCollector().getEntityBinding( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 			else {
 				collection.addFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					collection.addFilter(filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(),
 						simpleFilterJoinTable.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilterJoinTable.aliases()), toAliasEntityMap(simpleFilterJoinTable.aliases()));
 					}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter(filter.name(), filter.condition(),
 							filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = buildingContext.getMetadataCollector().getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
 	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
 		if ( orderByFragment != null ) {
 			if ( orderByFragment.length() == 0 ) {
 				//order by id
 				return "id asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "id desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
 		if ( orderByFragment != null ) {
 			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
 			if ( orderByFragment.length() == 0 ) {
 				//order by element
 				return "$element$ asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "$element$ desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			MetadataBuildingContext buildingContext) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = buildingContext.getMetadataCollector().getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				buildingContext.getMetadataCollector().addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getReferencedProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( buildingContext.getMetadataCollector(), collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 
 		if ( property != null ) {
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			else {
 				final CollectionTable collectionTableAnn = property.getAnnotation( CollectionTable.class );
 				if ( collectionTableAnn != null ) {
 					if ( collectionTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						key.setForeignKeyName( "none" );
 					}
 					else {
 						key.setForeignKeyName( StringHelper.nullIfEmpty( collectionTableAnn.foreignKey().name() ) );
 					}
 				}
 				else {
 					final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 					if ( joinTableAnn != null ) {
 						if ( joinTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 							key.setForeignKeyName( "none" );
 						}
 						else {
 							key.setForeignKeyName( StringHelper.nullIfEmpty( joinTableAnn.foreignKey().name() ) );
 
 						}
 					}
 				}
 			}
 		}
 
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			MetadataBuildingContext buildingContext) throws MappingException {
 		if ( property == null ) {
 			throw new IllegalArgumentException( "null was passed for argument property" );
 		}
 
 		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if ( LOG.isDebugEnabled() ) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if ( isCollectionOfEntities && unique ) {
 				LOG.debugf("Binding a OneToMany: %s through an association table", path);
 			}
             else if (isCollectionOfEntities) {
 				LOG.debugf("Binding as ManyToMany: %s", path);
 			}
             else if (anyAnn != null) {
 				LOG.debugf("Binding a ManyToAny: %s", path);
 			}
             else {
 				LOG.debugf("Binding a collection of element: %s", path);
 			}
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				throw new AnnotationException(
 						"mappedBy reference an unknown target entity property: "
 								+ collType + "." + joinColumns[0].getMappedBy() + " in "
 								+ collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName()
 				);
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = buildingContext.getMetadataCollector().getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getClassName(),
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getClassName() : null,
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? collectionEntity.getJpaEntityName() : null,
 						collectionEntity != null ? buildingContext.getMetadataCollector().getLogicalTableName(
 								collectionEntity.getTable()
 						) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, buildingContext );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element = new ManyToOne( buildingContext.getMetadataCollector(),  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				element.setForeignKeyName( fk.name() );
 			}
 			else {
 				final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 				if ( joinTableAnn != null ) {
 					if ( joinTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						element.setForeignKeyName( "none" );
 					}
 					else {
 						element.setForeignKeyName( StringHelper.nullIfEmpty( joinTableAnn.inverseForeignKey().name() ) );
 					}
 				}
 			}
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", buildingContext.getBuildingOptions().getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue(
 					anyAnn.metaDef(),
 					inverseJoinColumns,
 					anyAnn.metaColumn(),
 					inferredData,
 					cascadeDeleteEnabled,
 					Nullability.NO_CONSTRAINT,
 					propertyHolder,
 					new EntityBinder(),
 					true,
 					buildingContext
 			);
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			CollectionPropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						null,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 			}
 			else {
 				elementClass = collType;
 				classType = buildingContext.getMetadataCollector().getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 
 				// 'parentPropertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				parentPropertyHolder.startingProperty( property );
 
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				// todo : force in the case of Convert annotation(s) with embedded paths (beyond key/value prefixes)?
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index ac239312db..01c9898eeb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1174 +1,1173 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import javax.persistence.Access;
 import javax.persistence.ConstraintMode;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.NamedEntityGraph;
 import javax.persistence.NamedEntityGraphs;
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
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.boot.model.naming.EntityNaming;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
 import org.hibernate.boot.model.naming.NamingStrategyHelper;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.InFlightMetadataCollector;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
     private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
 
 	private MetadataBuildingContext context;
 
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
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
 	// todo : we should defer to InFlightMetadataCollector.EntityTableXref for secondary table tracking;
 	//		atm we use both from here; HBM binding solely uses InFlightMetadataCollector.EntityTableXref
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
 			MetadataBuildingContext context) {
 		this.context = context;
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
-		persistentClass.setNodeName( name );
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
 					? context.getBuildingOptions().shouldImplicitlyForceDiscriminatorInSelect()
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
 		persistentClass.setOptimisticLockStyle( getVersioning( optimisticLockType ) );
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
 					persister = context.getClassLoaderAccess().classForName( entityAnn.persister() );
 				}
 				catch (ClassLoadingException e) {
 					throw new AnnotationException( "Could not find persister class: " + entityAnn.persister(), e );
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
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase(Locale.ROOT) )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT) )
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
 				FilterDefinition definition = context.getMetadataCollector().getFilterDefinition( filterName );
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
 			context.getMetadataCollector().addImport( name, persistentClass.getEntityName() );
 			String entityName = persistentClass.getEntityName();
 			if ( !entityName.equals( name ) ) {
 				context.getMetadataCollector().addImport( entityName, entityName );
 			}
 		}
 		catch (MappingException me) {
 			throw new AnnotationException( "Use of the same entity name twice: " + name, me );
 		}
 
 		processNamedEntityGraphs();
 	}
 
 	private void processNamedEntityGraphs() {
 		processNamedEntityGraph( annotatedClass.getAnnotation( NamedEntityGraph.class ) );
 		final NamedEntityGraphs graphs = annotatedClass.getAnnotation( NamedEntityGraphs.class );
 		if ( graphs != null ) {
 			for ( NamedEntityGraph graph : graphs.value() ) {
 				processNamedEntityGraph( graph );
 			}
 		}
 	}
 
 	private void processNamedEntityGraph(NamedEntityGraph annotation) {
 		if ( annotation == null ) {
 			return;
 		}
 		context.getMetadataCollector().addNamedEntityGraph(
 				new NamedEntityGraphDefinition( annotation, name, persistentClass.getEntityName() )
 		);
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
 
 	OptimisticLockStyle getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return OptimisticLockStyle.VERSION;
 			case NONE:
 				return OptimisticLockStyle.NONE;
 			case DIRTY:
 				return OptimisticLockStyle.DIRTY;
 			case ALL:
 				return OptimisticLockStyle.ALL;
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
 				final ReflectionManager reflectionManager = context.getBuildingOptions().getReflectionManager();
 				if ( AnnotationBinder.isDefault( reflectionManager.toXClass( proxy.proxyClass() ), context ) ) {
 					proxyClass = annotatedClass;
 				}
 				else {
 					proxyClass = reflectionManager.toXClass( proxy.proxyClass() );
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
 
 	private static class EntityTableNamingStrategyHelper implements NamingStrategyHelper {
 		private final String className;
 		private final String entityName;
 		private final String jpaEntityName;
 
 		private EntityTableNamingStrategyHelper(String className, String entityName, String jpaEntityName) {
 			this.className = className;
 			this.entityName = entityName;
 			this.jpaEntityName = jpaEntityName;
 		}
 
 		public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getImplicitNamingStrategy().determinePrimaryTableName(
 					new ImplicitEntityNameSource() {
 						private final EntityNaming entityNaming = new EntityNaming() {
 							@Override
 							public String getClassName() {
 								return className;
 							}
 
 							@Override
 							public String getEntityName() {
 								return entityName;
 							}
 
 							@Override
 							public String getJpaEntityName() {
 								return jpaEntityName;
 							}
 						};
 
 						@Override
 						public EntityNaming getEntityNaming() {
 							return entityNaming;
 						}
 
 						@Override
 						public MetadataBuildingContext getBuildingContext() {
 							return buildingContext;
 						}
 					}
 			);
 		}
 
 		@Override
 		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( explicitName );
 		}
 
 		@Override
 		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 					logicalName,
 					buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment()
 			);
 		}
 	}
 
 	public void bindTableForDiscriminatedSubclass(InFlightMetadataCollector.EntityTableXref superTableXref) {
 		if ( !SingleTableSubclass.class.isInstance( persistentClass ) ) {
 			throw new AssertionFailure(
 					"Was expecting a discriminated subclass [" + SingleTableSubclass.class.getName() +
 							"] but found [" + persistentClass.getClass().getName() + "] for entity [" +
 							persistentClass.getEntityName() + "]"
 			);
 		}
 
 		context.getMetadataCollector().addEntityTableXref(
 				persistentClass.getEntityName(),
 				context.getMetadataCollector().getDatabase().toIdentifier(
 						context.getMetadataCollector().getLogicalTableName(
 								superTableXref.getPrimaryTable()
 						)
 				),
 				superTableXref.getPrimaryTable(),
 				superTableXref
 		);
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			InFlightMetadataCollector.EntityTableXref denormalizedSuperTableXref) {
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper(
 				persistentClass.getClassName(),
 				persistentClass.getEntityName(),
 				name
 		);
 
 		final Identifier logicalName;
 		if ( StringHelper.isNotEmpty( tableName ) ) {
 			logicalName = namingStrategyHelper.handleExplicitName( tableName, context );
 		}
 		else {
 			logicalName = namingStrategyHelper.determineImplicitName( context );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				logicalName,
 				persistentClass.isAbstract(),
 				uniqueConstraints,
 				null,
 				constraints,
 				context,
 				this.subselect,
 				denormalizedSuperTableXref
 		);
 		final RowId rowId = annotatedClass.getAnnotation( RowId.class );
 		if ( rowId != null ) {
 			table.setRowId( rowId.value() );
 		}
 
 		context.getMetadataCollector().addEntityTableXref(
 				persistentClass.getEntityName(),
 				logicalName,
 				table,
 				denormalizedSuperTableXref
 		);
 
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
 					propertyHolder,
 					context
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
 						propertyHolder,
 						context
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
 								propertyHolder,
 								context
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
 								propertyHolder,
 								context
 						);
 					}
 				}
 			}
 		}
 
 		for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
 			joinColumn.forceNotNull();
 		}
 		bindJoinToPersistentClass( join, ejb3JoinColumns, context );
 	}
 
 	private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, MetadataBuildingContext buildingContext) {
 		SimpleValue key = new DependantValue( buildingContext.getMetadataCollector(), join.getTable(), persistentClass.getIdentifier() );
 		join.setKey( key );
 		setFKNameIfDefined( join );
 		key.setCascadeDeleteEnabled( false );
 		TableBinder.bindFk( persistentClass, null, ejb3JoinColumns, key, false, buildingContext );
 		join.createPrimaryKey();
 		join.createForeignKey();
 		persistentClass.addJoin( join );
 	}
 
 	private void setFKNameIfDefined(Join join) {
 		// just awful..
 
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null && !BinderHelper.isEmptyAnnotationValue( matchingTable.foreignKey().name() ) ) {
 			( (SimpleValue) join.getKey() ).setForeignKeyName( matchingTable.foreignKey().name() );
 		}
 		else {
 			javax.persistence.SecondaryTable jpaSecondaryTable = findMatchingSecondaryTable( join );
 			if ( jpaSecondaryTable != null ) {
 				if ( jpaSecondaryTable.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 					( (SimpleValue) join.getKey() ).setForeignKeyName( "none" );
 				}
 				else {
 					( (SimpleValue) join.getKey() ).setForeignKeyName( StringHelper.nullIfEmpty( jpaSecondaryTable.foreignKey().name() ) );
 				}
 			}
 		}
 	}
 
 	private SecondaryTable findMatchingSecondaryTable(Join join) {
 		final String nameToMatch = join.getTable().getQuotedName();
 
 		SecondaryTable secondaryTable = annotatedClass.getAnnotation( SecondaryTable.class );
 		if ( secondaryTable != null && nameToMatch.equals( secondaryTable.name() ) ) {
 			return secondaryTable;
 		}
 
 		SecondaryTables secondaryTables = annotatedClass.getAnnotation( SecondaryTables.class );
 		if ( secondaryTables != null ) {
 			for ( SecondaryTable secondaryTable2 : secondaryTables.value() ) {
 				if ( secondaryTable != null && nameToMatch.equals( secondaryTable.name() ) ) {
 					return secondaryTable;
 				}
 			}
 
 		}
 
 		return null;
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
 
 	private static class SecondaryTableNamingStrategyHelper implements NamingStrategyHelper {
 		@Override
 		public Identifier determineImplicitName(MetadataBuildingContext buildingContext) {
 			// should maybe throw an exception here
 			return null;
 		}
 
 		@Override
 		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( explicitName );
 		}
 
 		@Override
 		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 					logicalName,
 					buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment()
 			);
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
 
 		final Identifier logicalName;
 		if ( secondaryTable != null ) {
 			schema = secondaryTable.schema();
 			catalog = secondaryTable.catalog();
 			logicalName = context.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( secondaryTable.name() );
 			joinColumns = secondaryTable.pkJoinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( secondaryTable.uniqueConstraints() );
 		}
 		else if ( joinTable != null ) {
 			schema = joinTable.schema();
 			catalog = joinTable.catalog();
 			logicalName = context.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( joinTable.name() );
 			joinColumns = joinTable.joinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( joinTable.uniqueConstraints() );
 		}
 		else {
 			throw new AssertionFailure( "Both JoinTable and SecondaryTable are null" );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				logicalName,
 				false,
 				uniqueConstraintHolders,
 				null,
 				null,
 				context,
 				null,
 				null
 		);
 
 		final InFlightMetadataCollector.EntityTableXref tableXref = context.getMetadataCollector().getEntityTableXref( persistentClass.getEntityName() );
 		assert tableXref != null : "Could not locate EntityTableXref for entity [" + persistentClass.getEntityName() + "]";
 		tableXref.addSecondaryTable( logicalName, join );
 
 		if ( secondaryTable != null ) {
 			TableBinder.addIndexes( table, secondaryTable.indexes(), context );
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
 								matchingTable.sqlInsert().check().toString().toLowerCase(Locale.ROOT)
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlUpdate().check().toString().toLowerCase(Locale.ROOT)
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlDelete().check().toString().toLowerCase(Locale.ROOT)
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
 		TableBinder.addIndexes( persistentClass.getTable(), table.indexes(), context );
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
 		TableBinder.addIndexes( hibTable, table.indexes(), context );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
index 1ab233cd92..9e008ec493 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
@@ -1,433 +1,430 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionPropertyHolder;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.sql.Template;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.MapKeyClass;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Random;
 
 /**
  * Implementation to bind a Map
  *
  * @author Emmanuel Bernard
  */
 public class MapBinder extends CollectionBinder {
 	public MapBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	public boolean isMap() {
 		return true;
 	}
 
 	protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Map( getBuildingContext().getMetadataCollector(), persistentClass );
 	}
 
 	@Override
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, MapBinder.this.collection ) {
 			public void secondPass(Map persistentClasses, Map inheritedMetas)
 					throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, buildingContext
 				);
 				bindKeyFromAssociationTable(
 						collType, persistentClasses, mapKeyPropertyName, property, isEmbedded, buildingContext,
 						mapKeyColumns, mapKeyManyToManyColumns,
 						inverseColumns != null ? inverseColumns[0].getPropertyName() : null
 				);
 			}
 		};
 	}
 
 	private void bindKeyFromAssociationTable(
 			XClass collType,
 			Map persistentClasses,
 			String mapKeyPropertyName,
 			XProperty property,
 			boolean isEmbedded,
 			MetadataBuildingContext buildingContext,
 			Ejb3Column[] mapKeyColumns,
 			Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			String targetPropertyName) {
 		if ( mapKeyPropertyName != null ) {
 			//this is an EJB3 @MapKey
 			PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType.getName() );
 			if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
 			Property mapProperty = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
 			if ( mapProperty == null ) {
 				throw new AnnotationException(
 						"Map key property not found: " + collType + "." + mapKeyPropertyName
 				);
 			}
 			org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
 			Value indexValue = createFormulatedValue(
 					mapProperty.getValue(), map, targetPropertyName, associatedClass, buildingContext
 			);
 			map.setIndex( indexValue );
 		}
 		else {
 			//this is a true Map mapping
 			//TODO ugly copy/pastle from CollectionBinder.bindManyToManySecondPass
 			String mapKeyType;
 			Class target = void.class;
 			/*
 			 * target has priority over reflection for the map key type
 			 * JPA 2 has priority
 			 */
 			if ( property.isAnnotationPresent( MapKeyClass.class ) ) {
 				target = property.getAnnotation( MapKeyClass.class ).value();
 			}
 			if ( !void.class.equals( target ) ) {
 				mapKeyType = target.getName();
 			}
 			else {
 				mapKeyType = property.getMapKey().getName();
 			}
 			PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( mapKeyType );
 			boolean isIndexOfEntities = collectionEntity != null;
 			ManyToOne element = null;
 			org.hibernate.mapping.Map mapValue = (org.hibernate.mapping.Map) this.collection;
 			if ( isIndexOfEntities ) {
 				element = new ManyToOne( buildingContext.getMetadataCollector(), mapValue.getCollectionTable() );
 				mapValue.setIndex( element );
 				element.setReferencedEntityName( mapKeyType );
 				//element.setFetchMode( fetchMode );
 				//element.setLazy( fetchMode != FetchMode.JOIN );
 				//make the second join non lazy
 				element.setFetchMode( FetchMode.JOIN );
 				element.setLazy( false );
 				//does not make sense for a map key element.setIgnoreNotFound( ignoreNotFound );
 			}
 			else {
 				XClass keyXClass;
 				AnnotatedClassType classType;
 				if ( BinderHelper.PRIMITIVE_NAMES.contains( mapKeyType ) ) {
 					classType = AnnotatedClassType.NONE;
 					keyXClass = null;
 				}
 				else {
 					try {
 						keyXClass = buildingContext.getBuildingOptions().getReflectionManager().classForName( mapKeyType );
 					}
 					catch (ClassLoadingException e) {
 						throw new AnnotationException( "Unable to find class: " + mapKeyType, e );
 					}
 					classType = buildingContext.getMetadataCollector().getClassType( keyXClass );
 					//force in case of attribute override
 					boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 							|| property.isAnnotationPresent( AttributeOverrides.class );
 					if ( isEmbedded || attributeOverride ) {
 						classType = AnnotatedClassType.EMBEDDABLE;
 					}
 				}
 
 				CollectionPropertyHolder holder = PropertyHolderBuilder.buildPropertyHolder(
 						mapValue,
 						StringHelper.qualify( mapValue.getRole(), "mapkey" ),
 						keyXClass,
 						property,
 						propertyHolder,
 						buildingContext
 				);
 
 
 				// 'propertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				propertyHolder.startingProperty( property );
 				holder.prepare( property );
 
 				PersistentClass owner = mapValue.getOwner();
 				AccessType accessType;
 				// FIXME support @Access for collection of elements
 				// String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" )
 							? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					accessType = prop.getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 					EntityBinder entityBinder = new EntityBinder();
 
 					PropertyData inferredData;
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "index", keyXClass );
 					}
 					else {
 						//"key" is the JPA 2 prefix for map keys
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "key", keyXClass );
 					}
 
 					//TODO be smart with isNullable
 					Component component = AnnotationBinder.fillComponent(
 							holder,
 							inferredData,
 							accessType,
 							true,
 							entityBinder,
 							false,
 							false,
 							true,
 							buildingContext,
 							inheritanceStatePerClass
 					);
 					mapValue.setIndex( component );
 				}
 				else {
 					SimpleValueBinder elementBinder = new SimpleValueBinder();
 					elementBinder.setBuildingContext( buildingContext );
 					elementBinder.setReturnedClassName( mapKeyType );
 
 					Ejb3Column[] elementColumns = mapKeyColumns;
 					if ( elementColumns == null || elementColumns.length == 0 ) {
 						elementColumns = new Ejb3Column[1];
 						Ejb3Column column = new Ejb3Column();
 						column.setImplicit( false );
 						column.setNullable( true );
 						column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 						column.setLogicalColumnName( Collection.DEFAULT_KEY_COLUMN_NAME );
 						//TODO create an EMPTY_JOINS collection
 						column.setJoins( new HashMap<String, Join>() );
 						column.setBuildingContext( buildingContext );
 						column.bind();
 						elementColumns[0] = column;
 					}
 					//override the table
 					for (Ejb3Column column : elementColumns) {
 						column.setTable( mapValue.getCollectionTable() );
 					}
 					elementBinder.setColumns( elementColumns );
 					//do not call setType as it extract the type from @Type
 					//the algorithm generally does not apply for map key anyway
 					elementBinder.setKey(true);
 					elementBinder.setType(
 							property,
 							keyXClass,
 							this.collection.getOwnerEntityName(),
 							holder.keyElementAttributeConverterDefinition( keyXClass )
 					);
 					elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 					elementBinder.setAccessType( accessType );
 					mapValue.setIndex( elementBinder.make() );
 				}
 			}
 			//FIXME pass the Index Entity JoinColumns
 			if ( !collection.isOneToMany() ) {
 				//index column shoud not be null
 				for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
 					col.forceNotNull();
 				}
 			}
 			if ( isIndexOfEntities ) {
 				bindManytoManyInverseFk(
 						collectionEntity,
 						mapKeyManyToManyColumns,
 						element,
 						false, //a map key column has no unique constraint
 						buildingContext
 				);
 			}
 		}
 	}
 
 	protected Value createFormulatedValue(
 			Value value,
 			Collection collection,
 			String targetPropertyName,
 			PersistentClass associatedClass,
 			MetadataBuildingContext buildingContext) {
 		Value element = collection.getElement();
 		String fromAndWhere = null;
 		if ( !( element instanceof OneToMany ) ) {
 			String referencedPropertyName = null;
 			if ( element instanceof ToOne ) {
 				referencedPropertyName = ( (ToOne) element ).getReferencedPropertyName();
 			}
 			else if ( element instanceof DependantValue ) {
 				//TODO this never happen I think
 				if ( propertyName != null ) {
 					referencedPropertyName = collection.getReferencedPropertyName();
 				}
 				else {
 					throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 				}
 			}
 			Iterator referencedEntityColumns;
 			if ( referencedPropertyName == null ) {
 				referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
 			}
 			else {
 				Property referencedProperty = associatedClass.getRecursiveProperty( referencedPropertyName );
 				referencedEntityColumns = referencedProperty.getColumnIterator();
 			}
 			String alias = "$alias$";
 			StringBuilder fromAndWhereSb = new StringBuilder( " from " )
 					.append( associatedClass.getTable().getName() )
 							//.append(" as ") //Oracle doesn't support it in subqueries
 					.append( " " )
 					.append( alias ).append( " where " );
 			Iterator collectionTableColumns = element.getColumnIterator();
 			while ( collectionTableColumns.hasNext() ) {
 				Column colColumn = (Column) collectionTableColumns.next();
 				Column refColumn = (Column) referencedEntityColumns.next();
 				fromAndWhereSb.append( alias ).append( '.' ).append( refColumn.getQuotedName() )
 						.append( '=' ).append( colColumn.getQuotedName() ).append( " and " );
 			}
 			fromAndWhere = fromAndWhereSb.substring( 0, fromAndWhereSb.length() - 5 );
 		}
 
 		if ( value instanceof Component ) {
 			Component component = (Component) value;
 			Iterator properties = component.getPropertyIterator();
 			Component indexComponent = new Component( getBuildingContext().getMetadataCollector(), collection );
 			indexComponent.setComponentClassName( component.getComponentClassName() );
-			//TODO I don't know if this is appropriate
-			indexComponent.setNodeName( "index" );
 			while ( properties.hasNext() ) {
 				Property current = (Property) properties.next();
 				Property newProperty = new Property();
 				newProperty.setCascade( current.getCascade() );
 				newProperty.setValueGenerationStrategy( current.getValueGenerationStrategy() );
 				newProperty.setInsertable( false );
 				newProperty.setUpdateable( false );
 				newProperty.setMetaAttributes( current.getMetaAttributes() );
 				newProperty.setName( current.getName() );
-				newProperty.setNodeName( current.getNodeName() );
 				newProperty.setNaturalIdentifier( false );
 				//newProperty.setOptimisticLocked( false );
 				newProperty.setOptional( false );
 				newProperty.setPersistentClass( current.getPersistentClass() );
 				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
 				newProperty.setSelectable( current.isSelectable() );
 				newProperty.setValue(
 						createFormulatedValue(
 								current.getValue(), collection, targetPropertyName, associatedClass, buildingContext
 						)
 				);
 				indexComponent.addProperty( newProperty );
 			}
 			return indexComponent;
 		}
 		else if ( value instanceof SimpleValue ) {
 			SimpleValue sourceValue = (SimpleValue) value;
 			SimpleValue targetValue;
 			if ( value instanceof ManyToOne ) {
 				ManyToOne sourceManyToOne = (ManyToOne) sourceValue;
 				ManyToOne targetManyToOne = new ManyToOne( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
 				targetManyToOne.setFetchMode( FetchMode.DEFAULT );
 				targetManyToOne.setLazy( true );
 				//targetValue.setIgnoreNotFound( ); does not make sense for a map key
 				targetManyToOne.setReferencedEntityName( sourceManyToOne.getReferencedEntityName() );
 				targetValue = targetManyToOne;
 			}
 			else {
 				targetValue = new SimpleValue( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
 				targetValue.setTypeName( sourceValue.getTypeName() );
 				targetValue.setTypeParameters( sourceValue.getTypeParameters() );
 			}
 			Iterator columns = sourceValue.getColumnIterator();
 			Random random = new Random();
 			while ( columns.hasNext() ) {
 				Object current = columns.next();
 				Formula formula = new Formula();
 				String formulaString;
 				if ( current instanceof Column ) {
 					formulaString = ( (Column) current ).getQuotedName();
 				}
 				else if ( current instanceof Formula ) {
 					formulaString = ( (Formula) current ).getFormula();
 				}
 				else {
 					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
 				}
 				if ( fromAndWhere != null ) {
 					formulaString = Template.renderWhereStringTemplate( formulaString, "$alias$", new HSQLDialect() );
 					formulaString = "(select " + formulaString + fromAndWhere + ")";
 					formulaString = StringHelper.replace(
 							formulaString,
 							"$alias$",
 							"a" + random.nextInt( 16 )
 					);
 				}
 				formula.setFormula( formulaString );
 				targetValue.addFormula( formula );
 
 			}
 			return targetValue;
 		}
 		else {
 			throw new AssertionFailure( "Unknown type encounters for map key: " + value.getClass() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
index 43222d00aa..928b345403 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
@@ -1,490 +1,489 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.lang.annotation.Annotation;
 import java.util.Map;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.HibernateException;
 import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.ValueGenerationType;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.tuple.AnnotationValueGeneration;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.ValueGenerator;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class PropertyBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PropertyBinder.class.getName());
 
 	private MetadataBuildingContext buildingContext;
 
 	private String name;
 	private String returnedClassName;
 	private boolean lazy;
 	private AccessType accessType;
 	private Ejb3Column[] columns;
 	private PropertyHolder holder;
 	private Value value;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private String cascade;
 	private SimpleValueBinder simpleValueBinder;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private boolean embedded;
 	private EntityBinder entityBinder;
 	private boolean isXToMany;
 	private String referencedEntityName;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setEntityBinder(EntityBinder entityBinder) {
 		this.entityBinder = entityBinder;
 	}
 
 	/*
 			 * property can be null
 			 * prefer propertyName to property.getName() since some are overloaded
 			 */
 	private XProperty property;
 	private XClass returnedClass;
 	private boolean isId;
 	private Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private Property mappingProperty;
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		insertable = columns[0].isInsertable();
 		updatable = columns[0].isUpdatable();
 		//consistency is checked later when we know the property name
 		this.columns = columns;
 	}
 
 	public void setHolder(PropertyHolder holder) {
 		this.holder = holder;
 	}
 
 	public void setValue(Value value) {
 		this.value = value;
 	}
 
 	public void setCascade(String cascadeStrategy) {
 		this.cascade = cascadeStrategy;
 	}
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	private void validateBind() {
 		if ( property.isAnnotationPresent( Immutable.class ) ) {
 			throw new AnnotationException(
 					"@Immutable on property not allowed. " +
 							"Only allowed on entity level or on a collection."
 			);
 		}
 		if ( !declaringClassSet ) {
 			throw new AssertionFailure( "declaringClass has not been set before a bind" );
 		}
 	}
 
 	private void validateMake() {
 		//TODO check necessary params for a make
 	}
 
 	private Property makePropertyAndValue() {
 		validateBind();
 
 		LOG.debugf( "MetadataSourceProcessor property %s with lazy=%s", name, lazy );
 		final String containerClassName = holder.getClassName();
 		holder.startingProperty( property );
 
 		simpleValueBinder = new SimpleValueBinder();
 		simpleValueBinder.setBuildingContext( buildingContext );
 		simpleValueBinder.setPropertyName( name );
 		simpleValueBinder.setReturnedClassName( returnedClassName );
 		simpleValueBinder.setColumns( columns );
 		simpleValueBinder.setPersistentClassName( containerClassName );
 		simpleValueBinder.setType(
 				property,
 				returnedClass,
 				containerClassName,
 				holder.resolveAttributeConverterDefinition( property )
 		);
 		simpleValueBinder.setReferencedEntityName( referencedEntityName );
 		simpleValueBinder.setAccessType( accessType );
 		SimpleValue propertyValue = simpleValueBinder.make();
 		setValue( propertyValue );
 		return makeProperty();
 	}
 
 	//used when value is provided
 	public Property makePropertyAndBind() {
 		return bind( makeProperty() );
 	}
 
 	//used to build everything from scratch
 	public Property makePropertyValueAndBind() {
 		return bind( makePropertyAndValue() );
 	}
 
 	public void setXToMany(boolean xToMany) {
 		this.isXToMany = xToMany;
 	}
 
 	private Property bind(Property prop) {
 		if (isId) {
 			final RootClass rootClass = ( RootClass ) holder.getPersistentClass();
 			//if an xToMany, it as to be wrapped today.
 			//FIXME this pose a problem as the PK is the class instead of the associated class which is not really compliant with the spec
 			if ( isXToMany || entityBinder.wrapIdsInEmbeddedComponents() ) {
 				Component identifier = (Component) rootClass.getIdentifier();
 				if (identifier == null) {
 					identifier = AnnotationBinder.createComponent(
 							holder,
 							new PropertyPreloadedData(null, null, null),
 							true,
 							false,
 							buildingContext
 					);
 					rootClass.setIdentifier( identifier );
 					identifier.setNullValue( "undefined" );
 					rootClass.setEmbeddedIdentifier( true );
 					rootClass.setIdentifierMapper( identifier );
 				}
 				//FIXME is it good enough?
 				identifier.addProperty( prop );
 			}
 			else {
 				rootClass.setIdentifier( ( KeyValue ) getValue() );
 				if (embedded) {
 					rootClass.setEmbeddedIdentifier( true );
 				}
 				else {
 					rootClass.setIdentifierProperty( prop );
 					final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 							declaringClass,
 							inheritanceStatePerClass,
 							buildingContext
 					);
 					if (superclass != null) {
 						superclass.setDeclaredIdentifierProperty(prop);
 					}
 					else {
 						//we know the property is on the actual entity
 						rootClass.setDeclaredIdentifierProperty( prop );
 					}
 				}
 			}
 		}
 		else {
 			holder.addProperty( prop, columns, declaringClass );
 		}
 		return prop;
 	}
 
 	//used when the value is provided and the binding is done elsewhere
 	public Property makeProperty() {
 		validateMake();
 		LOG.debugf( "Building property %s", name );
 		Property prop = new Property();
 		prop.setName( name );
-		prop.setNodeName( name );
 		prop.setValue( value );
 		prop.setLazy( lazy );
 		prop.setCascade( cascade );
 		prop.setPropertyAccessorName( accessType.getType() );
 
 		if ( property != null ) {
 			prop.setValueGenerationStrategy( determineValueGenerationStrategy( property ) );
 		}
 
 		NaturalId naturalId = property != null ? property.getAnnotation( NaturalId.class ) : null;
 		if ( naturalId != null ) {
 			if ( ! entityBinder.isRootEntity() ) {
 				throw new AnnotationException( "@NaturalId only valid on root entity (or its @MappedSuperclasses)" );
 			}
 			if ( ! naturalId.mutable() ) {
 				updatable = false;
 			}
 			prop.setNaturalIdentifier( true );
 		}
 
 		// HHH-4635 -- needed for dialect-specific property ordering
 		Lob lob = property != null ? property.getAnnotation( Lob.class ) : null;
 		prop.setLob( lob != null );
 
 		prop.setInsertable( insertable );
 		prop.setUpdateable( updatable );
 
 		// this is already handled for collections in CollectionBinder...
 		if ( Collection.class.isInstance( value ) ) {
 			prop.setOptimisticLocked( ( (Collection) value ).isOptimisticLocked() );
 		}
 		else {
 			final OptimisticLock lockAnn = property != null
 					? property.getAnnotation( OptimisticLock.class )
 					: null;
 			if ( lockAnn != null ) {
 				//TODO this should go to the core as a mapping validation checking
 				if ( lockAnn.excluded() && (
 						property.isAnnotationPresent( javax.persistence.Version.class )
 								|| property.isAnnotationPresent( Id.class )
 								|| property.isAnnotationPresent( EmbeddedId.class ) ) ) {
 					throw new AnnotationException(
 							"@OptimisticLock.exclude=true incompatible with @Id, @EmbeddedId and @Version: "
 									+ StringHelper.qualify( holder.getPath(), name )
 					);
 				}
 			}
 			final boolean isOwnedValue = !isToOneValue( value ) || insertable; // && updatable as well???
 			final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 					? ! lockAnn.excluded()
 					: isOwnedValue;
 			prop.setOptimisticLocked( includeInOptimisticLockChecks );
 		}
 
 		LOG.tracev( "Cascading {0} with {1}", name, cascade );
 		this.mappingProperty = prop;
 		return prop;
 	}
 
 	private ValueGeneration determineValueGenerationStrategy(XProperty property) {
 		ValueGeneration valueGeneration = getValueGenerationFromAnnotations( property );
 
 		if ( valueGeneration == null ) {
 			return NoValueGeneration.INSTANCE;
 		}
 
 		final GenerationTiming when = valueGeneration.getGenerationTiming();
 
 		if ( valueGeneration.getValueGenerator() == null ) {
 			insertable = false;
 			if ( when == GenerationTiming.ALWAYS ) {
 				updatable = false;
 			}
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * Returns the value generation strategy for the given property, if any.
 	 */
 	private ValueGeneration getValueGenerationFromAnnotations(XProperty property) {
 		AnnotationValueGeneration<?> valueGeneration = null;
 
 		for ( Annotation annotation : property.getAnnotations() ) {
 			AnnotationValueGeneration<?> candidate = getValueGenerationFromAnnotation( property, annotation );
 
 			if ( candidate != null ) {
 				if ( valueGeneration != null ) {
 					throw new AnnotationException(
 							"Only one generator annotation is allowed:" + StringHelper.qualify(
 									holder.getPath(),
 									name
 							)
 					);
 				}
 				else {
 					valueGeneration = candidate;
 				}
 			}
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * In case the given annotation is a value generator annotation, the corresponding value generation strategy to be
 	 * applied to the given property is returned, {@code null} otherwise.
 	 */
 	private <A extends Annotation> AnnotationValueGeneration<A> getValueGenerationFromAnnotation(
 			XProperty property,
 			A annotation) {
 		ValueGenerationType generatorAnnotation = annotation.annotationType()
 				.getAnnotation( ValueGenerationType.class );
 
 		if ( generatorAnnotation == null ) {
 			return null;
 		}
 
 		Class<? extends AnnotationValueGeneration<?>> generationType = generatorAnnotation.generatedBy();
 		AnnotationValueGeneration<A> valueGeneration = instantiateAndInitializeValueGeneration(
 				annotation, generationType, property
 		);
 
 		if ( annotation.annotationType() == Generated.class &&
 				property.isAnnotationPresent( javax.persistence.Version.class ) &&
 				valueGeneration.getGenerationTiming() == GenerationTiming.INSERT ) {
 
 			throw new AnnotationException(
 					"@Generated(INSERT) on a @Version property not allowed, use ALWAYS (or NEVER): "
 							+ StringHelper.qualify( holder.getPath(), name )
 			);
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * Instantiates the given generator annotation type, initializing it with the given instance of the corresponding
 	 * generator annotation and the property's type.
 	 */
 	private <A extends Annotation> AnnotationValueGeneration<A> instantiateAndInitializeValueGeneration(
 			A annotation,
 			Class<? extends AnnotationValueGeneration<?>> generationType,
 			XProperty property) {
 
 		try {
 			// This will cause a CCE in case the generation type doesn't match the annotation type; As this would be a
 			// programming error of the generation type developer and thus should show up during testing, we don't
 			// check this explicitly; If required, this could be done e.g. using ClassMate
 			@SuppressWarnings( "unchecked" )
 			AnnotationValueGeneration<A> valueGeneration = (AnnotationValueGeneration<A>) generationType.newInstance();
 			valueGeneration.initialize(
 					annotation,
 					buildingContext.getBuildingOptions().getReflectionManager().toClass( property.getType() )
 			);
 
 			return valueGeneration;
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Exception occurred during processing of generator annotation:" + StringHelper.qualify(
 							holder.getPath(),
 							name
 					), e
 			);
 		}
 	}
 
 	private static class NoValueGeneration implements ValueGeneration {
 		/**
 		 * Singleton access
 		 */
 		public static final NoValueGeneration INSTANCE = new NoValueGeneration();
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return GenerationTiming.NEVER;
 		}
 
 		@Override
 		public ValueGenerator<?> getValueGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean referenceColumnInSql() {
 			return true;
 		}
 
 		@Override
 		public String getDatabaseGeneratedReferencedColumnValue() {
 			return null;
 		}
 	}
 
 	private boolean isToOneValue(Value value) {
 		return ToOne.class.isInstance( value );
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setReturnedClass(XClass returnedClass) {
 		this.returnedClass = returnedClass;
 	}
 
 	public SimpleValueBinder getSimpleValueBinder() {
 		return simpleValueBinder;
 	}
 
 	public Value getValue() {
 		return value;
 	}
 
 	public void setId(boolean id) {
 		this.isId = id;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index fb7c0230af..7351499968 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,707 +1,679 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Mapping for a collection. Subclasses specialize to particular collection styles.
  *
  * @author Gavin King
  */
 public abstract class Collection implements Fetchable, Value, Filterable {
 
 	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
 	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
 
 	private final MetadataImplementor metadata;
 	private PersistentClass owner;
 
 	private KeyValue key;
 	private Value element;
 	private Table collectionTable;
 	private String role;
 	private boolean lazy;
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 	private String referencedPropertyName;
-	private String nodeName;
-	private String elementNodeName;
 	private String mappedByProperty;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private FetchMode fetchMode;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private String typeName;
 	private Properties typeParameters;
 	private final java.util.List filters = new ArrayList();
 	private final java.util.List manyToManyFilters = new ArrayList();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private String customSQLDeleteAll;
 	private boolean customDeleteAllCallable;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private String loaderName;
 
 	protected Collection(MetadataImplementor metadata, PersistentClass owner) {
 		this.metadata = metadata;
 		this.owner = owner;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	public boolean isSet() {
 		return false;
 	}
 
 	public KeyValue getKey() {
 		return key;
 	}
 
 	public Value getElement() {
 		return element;
 	}
 
 	public boolean isIndexed() {
 		return false;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table table) {
 		this.collectionTable = table;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		if ( comparator == null && comparatorClassName != null ) {
 			try {
 				final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 						.getServiceRegistry()
 						.getService( ClassLoaderService.class );
 				setComparator( (Comparator) classLoaderService.classForName( comparatorClassName ).newInstance() );
 			}
 			catch (Exception e) {
 				throw new MappingException(
 						"Could not instantiate comparator class [" + comparatorClassName
 								+ "] for collection " + getRole()
 				);
 			}
 		}
 		return comparator;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public abstract CollectionType getDefaultCollectionType() throws MappingException;
 
 	public boolean isPrimitiveArray() {
 		return false;
 	}
 
 	public boolean isArray() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isOneToMany() {
 		return element instanceof OneToMany;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public String getOwnerEntityName() {
 		return owner.getEntityName();
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public void setElement(Value element) {
 		this.element = element;
 	}
 
 	public void setKey(KeyValue key) {
 		this.key = key;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setSorted(boolean sorted) {
 		this.sorted = sorted;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	/**
 	 * @param owner The owner
 	 *
 	 * @deprecated Inject the owner into constructor.
 	 */
 	@Deprecated
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrdering() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrdering(String orderFragment) {
 		this.manyToManyOrderBy = orderFragment;
 	}
 
 	public boolean isIdentified() {
 		return false;
 	}
 
 	public boolean hasOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public void setOrphanDelete(boolean orphanDelete) {
 		this.orphanDelete = orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int i) {
 		batchSize = i;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public void setCollectionPersisterClass(Class persister) {
 		this.collectionPersisterClass = persister;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		assert getKey() != null : "Collection key not bound : " + getRole();
 		assert getElement() != null : "Collection element not bound : " + getRole();
 
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 					"only inverse one-to-many associations may use on-delete=\"cascade\": "
 							+ getRole()
 			);
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection foreign key mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getKey().getType().getName()
 			);
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection element mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getElement().getType().getName()
 			);
 		}
 
 		checkColumnDuplication();
-
-		if ( elementNodeName != null && elementNodeName.startsWith( "@" ) ) {
-			throw new MappingException( "element node must not be an attribute: " + elementNodeName );
-		}
-		if ( elementNodeName != null && elementNodeName.equals( "." ) ) {
-			throw new MappingException( "element node must not be the parent: " + elementNodeName );
-		}
-		if ( nodeName != null && nodeName.indexOf( '@' ) > -1 ) {
-			throw new MappingException( "collection node must not be an attribute: " + elementNodeName );
-		}
 	}
 
 	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
 			throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable s = (Selectable) columns.next();
 			if ( !s.isFormula() ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException(
 							"Repeated column in mapping for collection: "
 									+ getRole()
 									+ " column: "
 									+ col.getName()
 					);
 				}
 			}
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
 		checkColumnDuplication( cols, getKey().getColumnIterator() );
 		if ( isIndexed() ) {
 			checkColumnDuplication(
 					cols, ( (IndexedCollection) this )
 							.getIndex()
 							.getColumnIterator()
 			);
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication(
 					cols, ( (IdentifierCollection) this )
 							.getIdentifier()
 							.getColumnIterator()
 			);
 		}
 		if ( !isOneToMany() ) {
 			checkColumnDuplication( cols, getElement().getColumnIterator() );
 		}
 	}
 
 	public Iterator<Selectable> getColumnIterator() {
 		return Collections.<Selectable>emptyList().iterator();
 	}
 
 	public int getColumnSpan() {
 		return 0;
 	}
 
 	public Type getType() throws MappingException {
 		return getCollectionType();
 	}
 
 	public CollectionType getCollectionType() {
 		if ( typeName == null ) {
 			return getDefaultCollectionType();
 		}
 		else {
 			return metadata.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName );
 		}
 	}
 
 	public boolean isNullable() {
 		return true;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public Table getTable() {
 		return owner.getTable();
 	}
 
 	public void createForeignKey() {
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return true;
 	}
 
 	private void createForeignKeys() throws MappingException {
 		// if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
 		if ( referencedPropertyName == null ) {
 			getElement().createForeignKey();
 			key.createForeignKeyOfEntity( getOwner().getEntityName() );
 		}
 		// }
 	}
 
 	abstract void createPrimaryKey();
 
 	public void createAllKeys() throws MappingException {
 		createForeignKeys();
 		if ( !isInverse() ) {
 			createPrimaryKey();
 		}
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName == null ? role : cacheRegionName;
 	}
 
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
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
 
 	public void setCustomSQLDeleteAll(
 			String customSQLDeleteAll,
 			boolean callable,
 			ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDeleteAll = customSQLDeleteAll;
 		this.customDeleteAllCallable = callable;
 		this.deleteAllCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public boolean isCustomDeleteAllCallable() {
 		return customDeleteAllCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public void addFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		filters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public void addManyToManyFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		manyToManyFilters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set<String> getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef;
 	}
 
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		this.typeName = typeName;
 	}
 
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 
 	public void setTypeParameters(java.util.Map parameterMap) {
 		if ( parameterMap instanceof Properties ) {
 			this.typeParameters = (Properties) parameterMap;
 		}
 		else {
 			this.typeParameters = new Properties();
 			typeParameters.putAll( parameterMap );
 		}
 	}
 
 	public boolean[] getColumnInsertability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
-	public String getNodeName() {
-		return nodeName;
-	}
-
-	public void setNodeName(String nodeName) {
-		this.nodeName = nodeName;
-	}
-
-	public String getElementNodeName() {
-		return elementNodeName;
-	}
-
-	public void setElementNodeName(String elementNodeName) {
-		this.elementNodeName = elementNodeName;
-	}
-
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public void setSubselectLoadable(boolean subqueryLoadable) {
 		this.subselectLoadable = subqueryLoadable;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public void setExtraLazy(boolean extraLazy) {
 		this.extraLazy = extraLazy;
 	}
 
 	public boolean hasOrder() {
 		return orderBy != null || manyToManyOrderBy != null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	public void setMappedByProperty(String mappedByProperty) {
 		this.mappedByProperty = mappedByProperty;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
index d2d26867f7..7c1e6dab8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
@@ -1,439 +1,430 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.ExportableProducer;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.CompositeNestedGeneratedValueGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeFactory;
 
 /**
  * The mapping for a component, composite element,
  * composite identifier, etc.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Component extends SimpleValue implements MetaAttributable {
 	private ArrayList<Property> properties = new ArrayList<Property>();
 	private String componentClassName;
 	private boolean embedded;
 	private String parentProperty;
 	private PersistentClass owner;
 	private boolean dynamic;
 	private Map metaAttributes;
-	private String nodeName;
 	private boolean isKey;
 	private String roleName;
 
 	private java.util.Map<EntityMode,String> tuplizerImpls;
 
 	public Component(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
 		this( metadata, owner.getTable(), owner );
 	}
 
 	public Component(MetadataImplementor metadata, Component component) throws MappingException {
 		this( metadata, component.getTable(), component.getOwner() );
 	}
 
 	public Component(MetadataImplementor metadata, Join join) throws MappingException {
 		this( metadata, join.getTable(), join.getPersistentClass() );
 	}
 
 	public Component(MetadataImplementor metadata, Collection collection) throws MappingException {
 		this( metadata, collection.getCollectionTable(), collection.getOwner() );
 	}
 
 	public Component(MetadataImplementor metadata, Table table, PersistentClass owner) throws MappingException {
 		super( metadata, table );
 		this.owner = owner;
 	}
 
 	public int getPropertySpan() {
 		return properties.size();
 	}
 
 	public Iterator getPropertyIterator() {
 		return properties.iterator();
 	}
 
 	public void addProperty(Property p) {
 		properties.add( p );
 	}
 
 	@Override
 	public void addColumn(Column column) {
 		throw new UnsupportedOperationException("Cant add a column to a component");
 	}
 
 	@Override
 	public int getColumnSpan() {
 		int n=0;
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property p = (Property) iter.next();
 			n+= p.getColumnSpan();
 		}
 		return n;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator<Selectable> getColumnIterator() {
 		Iterator[] iters = new Iterator[ getPropertySpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Property) iter.next() ).getColumnIterator();
 		}
 		return new JoinedIterator( iters );
 	}
 
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	public String getComponentClassName() {
 		return componentClassName;
 	}
 
 	public Class getComponentClass() throws MappingException {
 		final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 				.getServiceRegistry()
 				.getService( ClassLoaderService.class );
 		try {
 			return classLoaderService.classForName( componentClassName );
 		}
 		catch (ClassLoadingException e) {
 			throw new MappingException("component class not found: " + componentClassName, e);
 		}
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	public String getParentProperty() {
 		return parentProperty;
 	}
 
 	public void setComponentClassName(String componentClass) {
 		this.componentClassName = componentClass;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public void setParentProperty(String parentProperty) {
 		this.parentProperty = parentProperty;
 	}
 
 	public boolean isDynamic() {
 		return dynamic;
 	}
 
 	public void setDynamic(boolean dynamic) {
 		this.dynamic = dynamic;
 	}
 
 	@Override
 	public Type getType() throws MappingException {
 		// TODO : temporary initial step towards HHH-1907
 		final ComponentMetamodel metamodel = new ComponentMetamodel( this, getMetadata().getMetadataBuildingOptions() );
 		final TypeFactory factory = getMetadata().getTypeResolver().getTypeFactory();
 		return isEmbedded() ? factory.embeddedComponent( metamodel ) : factory.component( metamodel );
 	}
 
 	@Override
 	public void setTypeUsingReflection(String className, String propertyName)
 		throws MappingException {
 	}
 
 	@Override
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	@Override
 	public MetaAttribute getMetaAttribute(String attributeName) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(attributeName);
 	}
 
 	@Override
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	@Override
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 
 	@Override
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnInsertability();
 			if ( prop.isInsertable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 
 	@Override
 	public boolean[] getColumnUpdateability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnUpdateability();
 			if ( prop.isUpdateable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 	
-	public String getNodeName() {
-		return nodeName;
-	}
-	
-	public void setNodeName(String nodeName) {
-		this.nodeName = nodeName;
-	}
-	
 	public boolean isKey() {
 		return isKey;
 	}
 	
 	public void setKey(boolean isKey) {
 		this.isKey = isKey;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return componentClassName!=null;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
 			tuplizerImpls = new HashMap<EntityMode,String>();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		// todo : remove this once ComponentMetamodel is complete and merged
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return tuplizerImpls.get( mode );
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( prop.getName().equals(propertyName) ) {
 				return prop;
 			}
 		}
 		throw new MappingException("component property not found: " + propertyName);
 	}
 
 	public String getRoleName() {
 		return roleName;
 	}
 
 	public void setRoleName(String roleName) {
 		this.roleName = roleName;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + properties.toString() + ')';
 	}
 
 	private IdentifierGenerator builtIdentifierGenerator;
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		if ( builtIdentifierGenerator == null ) {
 			builtIdentifierGenerator = buildIdentifierGenerator(
 					identifierGeneratorFactory,
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					rootClass
 			);
 		}
 		return builtIdentifierGenerator;
 	}
 
 	private IdentifierGenerator buildIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		final boolean hasCustomGenerator = ! DEFAULT_ID_GEN_STRATEGY.equals( getIdentifierGeneratorStrategy() );
 		if ( hasCustomGenerator ) {
 			return super.createIdentifierGenerator(
 					identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass
 			);
 		}
 
 		final Class entityClass = rootClass.getMappedClass();
 		final Class attributeDeclarer; // what class is the declarer of the composite pk attributes
 		CompositeNestedGeneratedValueGenerator.GenerationContextLocator locator;
 
 		// IMPL NOTE : See the javadoc discussion on CompositeNestedGeneratedValueGenerator wrt the
 		//		various scenarios for which we need to account here
 		if ( rootClass.getIdentifierMapper() != null ) {
 			// we have the @IdClass / <composite-id mapped="true"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else if ( rootClass.getIdentifierProperty() != null ) {
 			// we have the "@EmbeddedId" / <composite-id name="idName"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else {
 			// we have the "straight up" embedded (again the hibernate term) component identifier
 			attributeDeclarer = entityClass;
 		}
 
 		locator = new StandardGenerationContextLocator( rootClass.getEntityName() );
 		final CompositeNestedGeneratedValueGenerator generator = new CompositeNestedGeneratedValueGenerator( locator );
 
 		Iterator itr = getPropertyIterator();
 		while ( itr.hasNext() ) {
 			final Property property = (Property) itr.next();
 			if ( property.getValue().isSimpleValue() ) {
 				final SimpleValue value = (SimpleValue) property.getValue();
 
 				if ( DEFAULT_ID_GEN_STRATEGY.equals( value.getIdentifierGeneratorStrategy() ) ) {
 					// skip any 'assigned' generators, they would have been handled by
 					// the StandardGenerationContextLocator
 					continue;
 				}
 
 				final IdentifierGenerator valueGenerator = value.createIdentifierGenerator(
 						identifierGeneratorFactory,
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						rootClass
 				);
 				generator.addGeneratedValuePlan(
 						new ValueGenerationPlan(
 								valueGenerator,
 								injector( property, attributeDeclarer )
 						)
 				);
 			}
 		}
 		return generator;
 	}
 
 	private Setter injector(Property property, Class attributeDeclarer) {
 		return property.getPropertyAccessStrategy( attributeDeclarer )
 				.buildPropertyAccess( attributeDeclarer, property.getName() )
 				.getSetter();
 	}
 
 	private Class resolveComponentClass() {
 		try {
 			return getComponentClass();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	public static class StandardGenerationContextLocator
 			implements CompositeNestedGeneratedValueGenerator.GenerationContextLocator {
 		private final String entityName;
 
 		public StandardGenerationContextLocator(String entityName) {
 			this.entityName = entityName;
 		}
 
 		@Override
 		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject) {
 			return session.getEntityPersister( entityName, incomingObject ).getIdentifier( incomingObject, session );
 		}
 	}
 
 	public static class ValueGenerationPlan implements CompositeNestedGeneratedValueGenerator.GenerationPlan {
 		private final IdentifierGenerator subGenerator;
 		private final Setter injector;
 
 		public ValueGenerationPlan(
 				IdentifierGenerator subGenerator,
 				Setter injector) {
 			this.subGenerator = subGenerator;
 			this.injector = injector;
 		}
 
 		@Override
 		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext) {
 			final Object generatedValue = subGenerator.generate( session, incomingObject );
 			injector.set( injectionContext, generatedValue, session.getFactory() );
 		}
 
 		@Override
 		public void registerExportables(Database database) {
 			if ( ExportableProducer.class.isInstance( subGenerator ) ) {
 				( (ExportableProducer) subGenerator ).registerExportables( database );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java b/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
index 999e43f4c9..6da5896f87 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
@@ -1,105 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.Iterator;
 
 import org.hibernate.MappingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.Mapping;
 
 /**
  * Indexed collections include Lists, Maps, arrays and
  * primitive arrays.
  * @author Gavin King
  */
 public abstract class IndexedCollection extends Collection {
 
 	public static final String DEFAULT_INDEX_COLUMN_NAME = "idx";
 
 	private Value index;
-	private String indexNodeName;
 
 	public IndexedCollection(MetadataImplementor metadata, PersistentClass owner) {
 		super( metadata, owner );
 	}
 
 	public Value getIndex() {
 		return index;
 	}
 	public void setIndex(Value index) {
 		this.index = index;
 	}
 	public final boolean isIndexed() {
 		return true;
 	}
 
 	void createPrimaryKey() {
 		if ( !isOneToMany() ) {
 			PrimaryKey pk = new PrimaryKey();
 			pk.addColumns( getKey().getColumnIterator() );
 			
 			// index should be last column listed
 			boolean isFormula = false;
 			Iterator iter = getIndex().getColumnIterator();
 			while ( iter.hasNext() ) {
 				if ( ( (Selectable) iter.next() ).isFormula() ) {
 					isFormula=true;
 				}
 			}
 			if (isFormula) {
 				//if it is a formula index, use the element columns in the PK
 				pk.addColumns( getElement().getColumnIterator() );
 			}
 			else {
 				pk.addColumns( getIndex().getColumnIterator() ); 
 			}
 			getCollectionTable().setPrimaryKey(pk);
 		}
 		else {
 			// don't create a unique key, 'cos some
 			// databases don't like a UK on nullable
 			// columns
 			/*ArrayList list = new ArrayList();
 			list.addAll( getKey().getConstraintColumns() );
 			list.addAll( getIndex().getConstraintColumns() );
 			getCollectionTable().createUniqueKey(list);*/
 		}
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		super.validate( mapping );
 
 		assert getElement() != null : "IndexedCollection index not bound : " + getRole();
 
 		if ( !getIndex().isValid(mapping) ) {
 			throw new MappingException(
 				"collection index mapping has wrong number of columns: " +
 				getRole() +
 				" type: " +
 				getIndex().getType().getName()
 			);
 		}
-		if ( indexNodeName!=null && !indexNodeName.startsWith("@") ) {
-			throw new MappingException("index node must be an attribute: " + indexNodeName );
-		}
 	}
 	
 	public boolean isList() {
 		return false;
 	}
-
-	public String getIndexNodeName() {
-		return indexNodeName;
-	}
-
-	public void setIndexNodeName(String indexNodeName) {
-		this.indexNodeName = indexNodeName;
-	}
-	
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
index 0c47d9687f..b71b88061b 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
@@ -1,929 +1,916 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
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
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.Alias;
 
 /**
  * Mapping for an entity.
  *
  * @author Gavin King
  */
 public abstract class PersistentClass implements AttributeContainer, Serializable, Filterable, MetaAttributable {
 	private static final Alias PK_ALIAS = new Alias( 15, "PK" );
 
 	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
 	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
 
 	private final MetadataBuildingContext metadataBuildingContext;
 
 	private String entityName;
 
 	private String className;
 	private transient Class mappedClass;
 
 	private String proxyInterfaceName;
 	private transient Class proxyInterface;
 
-	private String nodeName;
 	private String jpaEntityName;
 
 	private String discriminatorValue;
 	private boolean lazy;
 	private ArrayList properties = new ArrayList();
 	private ArrayList declaredProperties = new ArrayList();
 	private final ArrayList<Subclass> subclasses = new ArrayList<Subclass>();
 	private final ArrayList subclassProperties = new ArrayList();
 	private final ArrayList subclassTables = new ArrayList();
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private int batchSize = -1;
 	private boolean selectBeforeUpdate;
 	private java.util.Map metaAttributes;
 	private ArrayList<Join> joins = new ArrayList<Join>();
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
 
 	private java.util.Map tuplizerImpls;
 
 	private MappedSuperclass superMappedSuperclass;
 	private Component declaredIdentifierMapper;
 	private OptimisticLockStyle optimisticLockStyle;
 
 	public PersistentClass(MetadataBuildingContext metadataBuildingContext) {
 		this.metadataBuildingContext = metadataBuildingContext;
 	}
 
 	public ServiceRegistry getServiceRegistry() {
 		return metadataBuildingContext.getBuildingOptions().getServiceRegistry();
 	}
 
 	public String getClassName() {
 		return className;
 	}
 
 	public void setClassName(String className) {
 		this.className = className == null ? null : className.intern();
 		this.mappedClass = null;
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 		this.proxyInterface = null;
 	}
 
 	public Class getMappedClass() throws MappingException {
 		if ( className == null ) {
 			return null;
 		}
 
 		try {
 			if ( mappedClass == null ) {
 				mappedClass = metadataBuildingContext.getClassLoaderAccess().classForName( className );
 			}
 			return mappedClass;
 		}
 		catch (ClassLoadingException e) {
 			throw new MappingException( "entity class not found: " + className, e );
 		}
 	}
 
 	public Class getProxyInterface() {
 		if ( proxyInterfaceName == null ) {
 			return null;
 		}
 		try {
 			if ( proxyInterface == null ) {
 				proxyInterface = metadataBuildingContext.getClassLoaderAccess().classForName( proxyInterfaceName );
 			}
 			return proxyInterface;
 		}
 		catch (ClassLoadingException e) {
 			throw new MappingException( "proxy class not found: " + proxyInterfaceName, e );
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
 		while ( superclass != null ) {
 			if ( subclass.getEntityName().equals( superclass.getEntityName() ) ) {
 				throw new MappingException(
 						"Circular inheritance mapping detected: " +
 								subclass.getEntityName() +
 								" will have it self as superclass when extending " +
 								getEntityName()
 				);
 			}
 			superclass = superclass.getSuperclass();
 		}
 		subclasses.add( subclass );
 	}
 
 	public boolean hasSubclasses() {
 		return subclasses.size() > 0;
 	}
 
 	public int getSubclassSpan() {
 		int n = subclasses.size();
 		for ( Subclass subclass : subclasses ) {
 			n += subclass.getSubclassSpan();
 		}
 		return n;
 	}
 
 	/**
 	 * Iterate over subclasses in a special 'order', most derived subclasses
 	 * first.
 	 */
 	public Iterator getSubclassIterator() {
 		Iterator[] iters = new Iterator[subclasses.size() + 1];
 		Iterator iter = subclasses.iterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Subclass) iter.next() ).getSubclassIterator();
 		}
 		iters[i] = subclasses.iterator();
 		return new JoinedIterator( iters );
 	}
 
 	public Iterator getSubclassClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( new SingletonIterator( this ) );
 		Iterator iter = getSubclassIterator();
 		while ( iter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) iter.next();
 			iters.add( clazz.getSubclassClosureIterator() );
 		}
 		return new JoinedIterator( iters );
 	}
 
 	public Table getIdentityTable() {
 		return getRootTable();
 	}
 
 	public Iterator getDirectSubclasses() {
 		return subclasses.iterator();
 	}
 
 	@Override
 	public void addProperty(Property p) {
 		properties.add( p );
 		declaredProperties.add( p );
 		p.setPersistentClass( this );
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
 		subclassProperties.add( prop );
 	}
 
 	protected void addSubclassJoin(Join join) {
 		subclassJoins.add( join );
 	}
 
 	protected void addSubclassTable(Table subclassTable) {
 		subclassTables.add( subclassTable );
 	}
 
 	public Iterator getSubclassPropertyClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( getPropertyClosureIterator() );
 		iters.add( subclassProperties.iterator() );
 		for ( int i = 0; i < subclassJoins.size(); i++ ) {
 			Join join = (Join) subclassJoins.get( i );
 			iters.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator( iters );
 	}
 
 	public Iterator getSubclassJoinClosureIterator() {
 		return new JoinedIterator( getJoinClosureIterator(), subclassJoins.iterator() );
 	}
 
 	public Iterator getSubclassTableClosureIterator() {
 		return new JoinedIterator( getTableClosureIterator(), subclassTables.iterator() );
 	}
 
 	public boolean isClassOrSuperclassJoin(Join join) {
 		return joins.contains( join );
 	}
 
 	public boolean isClassOrSuperclassTable(Table closureTable) {
 		return getTable() == closureTable;
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
 		this.entityName = entityName == null ? null : entityName.intern();
 	}
 
 	public void createPrimaryKey() {
 		//Primary key constraint
 		PrimaryKey pk = new PrimaryKey();
 		Table table = getTable();
 		pk.setTable( table );
 		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
 		table.setPrimaryKey( pk );
 
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
 	 * @return The property iterator.
 	 *
 	 * @see #getReferencedProperty for a discussion of "referenceable"
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
 	 *
 	 * @return The property reference (never null).
 	 *
 	 * @throws MappingException If the property could not be found.
 	 */
 	public Property getReferencedProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getReferenceablePropertyIterator() );
 		}
 		catch (MappingException e) {
 			throw new MappingException(
 					"property-ref [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	public Property getRecursiveProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getPropertyIterator() );
 		}
 		catch (MappingException e) {
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
 				final String element = (String) st.nextElement();
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
 						catch (MappingException ignore) {
 							// ignore it...
 						}
 					}
 
 					if ( property == null ) {
 						property = getProperty( element, iter );
 					}
 				}
 				else {
 					//flat recursive algorithm
 					property = ( (Component) property.getValue() ).getProperty( element );
 				}
 			}
 		}
 		catch (MappingException e) {
 			throw new MappingException( "property [" + propertyPath + "] not found on entity [" + getEntityName() + "]" );
 		}
 
 		return property;
 	}
 
 	private Property getProperty(String propertyName, Iterator iterator) throws MappingException {
 		if ( iterator.hasNext() ) {
 			String root = StringHelper.root( propertyName );
 			while ( iterator.hasNext() ) {
 				Property prop = (Property) iterator.next();
 				if ( prop.getName().equals( root ) ) {
 					return prop;
 				}
 			}
 		}
 		throw new MappingException( "property [" + propertyName + "] not found on entity [" + getEntityName() + "]" );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyClosureIterator();
 		Property identifierProperty = getIdentifierProperty();
 		if ( identifierProperty != null
 				&& identifierProperty.getName().equals( StringHelper.root( propertyName ) ) ) {
 			return identifierProperty;
 		}
 		else {
 			return getProperty( propertyName, iter );
 		}
 	}
 
 	/**
 	 * @deprecated prefer {@link #getOptimisticLockStyle}
 	 */
 	@Deprecated
 	public int getOptimisticLockMode() {
 		return getOptimisticLockStyle().getOldCode();
 	}
 
 	/**
 	 * @deprecated prefer {@link #setOptimisticLockStyle}
 	 */
 	@Deprecated
 	public void setOptimisticLockMode(int optimisticLockMode) {
 		setOptimisticLockStyle( OptimisticLockStyle.interpretOldCode( optimisticLockMode ) );
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
 		this.optimisticLockStyle = optimisticLockStyle;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !prop.isValid( mapping ) ) {
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
 		HashSet<String> names = new HashSet<String>();
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !names.add( prop.getName() ) ) {
 				throw new MappingException( "Duplicate property mapping of " + prop.getName() + " found in " + getEntityName() );
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
 		return metaAttributes == null
 				? null
 				: (MetaAttribute) metaAttributes.get( name );
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
 		joins.add( join );
 		join.setPersistentClass( this );
 	}
 
 	public int getJoinClosureSpan() {
 		return joins.size();
 	}
 
 	public int getPropertyClosureSpan() {
 		int span = properties.size();
 		for ( Join join : joins ) {
 			span += join.getPropertySpan();
 		}
 		return span;
 	}
 
 	public int getJoinNumber(Property prop) {
 		int result = 1;
 		Iterator iter = getSubclassJoinClosureIterator();
 		while ( iter.hasNext() ) {
 			Join join = (Join) iter.next();
 			if ( join.containsProperty( prop ) ) {
 				return result;
 			}
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
 			Join join = (Join) joins.get( i );
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
 
 	public void addFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		filters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						this
 				)
 		);
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
 		this.loaderName = loaderName == null ? null : loaderName.intern();
 	}
 
 	public abstract java.util.Set getSynchronizedTables();
 
 	public void addSynchronizedTable(String table) {
 		synchronizedTables.add( table );
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
 		if ( getIdentifierMapper() == null ) {
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
 
-	public String getNodeName() {
-		return nodeName;
-	}
-
-	public void setNodeName(String nodeName) {
-		this.nodeName = nodeName;
-	}
-
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 
 	public boolean hasPojoRepresentation() {
 		return getClassName() != null;
 	}
 
-	public boolean hasDom4jRepresentation() {
-		return getNodeName() != null;
-	}
-
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public void setSubselectLoadableCollections(boolean hasSubselectCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectCollections;
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
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return (String) tuplizerImpls.get( mode );
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
 			Join join = (Join) joins.get( i );
 			iterators.add( join.getDeclaredPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	public void addMappedsuperclassProperty(Property p) {
 		properties.add( p );
 		p.setPersistentClass( this );
 	}
 
 	public MappedSuperclass getSuperMappedSuperclass() {
 		return superMappedSuperclass;
 	}
 
 	public void setSuperMappedSuperclass(MappedSuperclass superMappedSuperclass) {
 		this.superMappedSuperclass = superMappedSuperclass;
 	}
 
 	// End of @Mappedsuperclass support
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
index 9699d974dd..380c9972db 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
@@ -1,365 +1,356 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.StringTokenizer;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.PropertyAccessStrategy;
 import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a property as part of an entity or a component.
  *
  * @author Gavin King
  */
 public class Property implements Serializable, MetaAttributable {
 	private String name;
 	private Value value;
 	private String cascade;
 	private boolean updateable = true;
 	private boolean insertable = true;
 	private boolean selectable = true;
 	private boolean optimisticLocked = true;
 	private ValueGeneration valueGenerationStrategy;
 	private String propertyAccessorName;
 	private boolean lazy;
 	private boolean optional;
-	private String nodeName;
 	private java.util.Map metaAttributes;
 	private PersistentClass persistentClass;
 	private boolean naturalIdentifier;
 	private boolean lob;
 
 	public boolean isBackRef() {
 		return false;
 	}
 
 	/**
 	 * Does this property represent a synthetic property?  A synthetic property is one we create during
 	 * metamodel binding to represent a collection of columns but which does not represent a property
 	 * physically available on the entity.
 	 *
 	 * @return True if synthetic; false otherwise.
 	 */
 	public boolean isSynthetic() {
 		return false;
 	}
 
 	public Type getType() throws MappingException {
 		return value.getType();
 	}
 	
 	public int getColumnSpan() {
 		return value.getColumnSpan();
 	}
 	
 	public Iterator getColumnIterator() {
 		return value.getColumnIterator();
 	}
 	
 	public String getName() {
 		return name;
 	}
 	
 	public boolean isComposite() {
 		return value instanceof Component;
 	}
 
 	public Value getValue() {
 		return value;
 	}
 	
 	public boolean isPrimitive(Class clazz) {
 		return getGetter(clazz).getReturnType().isPrimitive();
 	}
 
 	public CascadeStyle getCascadeStyle() throws MappingException {
 		Type type = value.getType();
 		if ( type.isComponentType() ) {
 			return getCompositeCascadeStyle( (CompositeType) type, cascade );
 		}
 		else if ( type.isCollectionType() ) {
 			return getCollectionCascadeStyle( ( (Collection) value ).getElement().getType(), cascade );
 		}
 		else {
 			return getCascadeStyle( cascade );			
 		}
 	}
 
 	private static CascadeStyle getCompositeCascadeStyle(CompositeType compositeType, String cascade) {
 		if ( compositeType.isAnyType() ) {
 			return getCascadeStyle( cascade );
 		}
 		int length = compositeType.getSubtypes().length;
 		for ( int i=0; i<length; i++ ) {
 			if ( compositeType.getCascadeStyle(i) != CascadeStyles.NONE ) {
 				return CascadeStyles.ALL;
 			}
 		}
 		return getCascadeStyle( cascade );
 	}
 
 	private static CascadeStyle getCollectionCascadeStyle(Type elementType, String cascade) {
 		if ( elementType.isComponentType() ) {
 			return getCompositeCascadeStyle( (CompositeType) elementType, cascade );
 		}
 		else {
 			return getCascadeStyle( cascade );
 		}
 	}
 	
 	private static CascadeStyle getCascadeStyle(String cascade) {
 		if ( cascade==null || cascade.equals("none") ) {
 			return CascadeStyles.NONE;
 		}
 		else {
 			StringTokenizer tokens = new StringTokenizer(cascade, ", ");
 			CascadeStyle[] styles = new CascadeStyle[ tokens.countTokens() ] ;
 			int i=0;
 			while ( tokens.hasMoreTokens() ) {
 				styles[i++] = CascadeStyles.getCascadeStyle( tokens.nextToken() );
 			}
 			return new CascadeStyles.MultipleCascadeStyle(styles);
 		}		
 	}
 	
 	public String getCascade() {
 		return cascade;
 	}
 
 	public void setCascade(String cascade) {
 		this.cascade = cascade;
 	}
 
 	public void setName(String name) {
 		this.name = name==null ? null : name.intern();
 	}
 
 	public void setValue(Value value) {
 		this.value = value;
 	}
 
 	public boolean isUpdateable() {
 		// if the property mapping consists of all formulas,
 		// make it non-updateable
 		return updateable && !ArrayHelper.isAllFalse( value.getColumnUpdateability() );
 	}
 
 	public boolean isInsertable() {
 		// if the property mapping consists of all formulas, 
 		// make it non-insertable
 		final boolean[] columnInsertability = value.getColumnInsertability();
 		return insertable && (
 				columnInsertability.length==0 ||
 				!ArrayHelper.isAllFalse( columnInsertability )
 			);
 	}
 
 	public ValueGeneration getValueGenerationStrategy() {
 		return valueGenerationStrategy;
 	}
 
 	public void setValueGenerationStrategy(ValueGeneration valueGenerationStrategy) {
 		this.valueGenerationStrategy = valueGenerationStrategy;
 	}
 
 	public void setUpdateable(boolean mutable) {
 		this.updateable = mutable;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	public void setPropertyAccessorName(String string) {
 		propertyAccessorName = string;
 	}
 
 	/**
 	 * Approximate!
 	 */
 	boolean isNullable() {
 		return value==null || value.isNullable();
 	}
 
 	public boolean isBasicPropertyAccessor() {
 		return propertyAccessorName==null || "property".equals( propertyAccessorName );
 	}
 
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	public MetaAttribute getMetaAttribute(String attributeName) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(attributeName);
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getValue().isValid(mapping);
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + name + ')';
 	}
 	
 	public void setLazy(boolean lazy) {
 		this.lazy=lazy;
 	}
 	
 	public boolean isLazy() {
 		if ( value instanceof ToOne ) {
 			// both many-to-one and one-to-one are represented as a
 			// Property.  EntityPersister is relying on this value to
 			// determine "lazy fetch groups" in terms of field-level
 			// interception.  So we need to make sure that we return
 			// true here for the case of many-to-one and one-to-one
 			// with lazy="no-proxy"
 			//
 			// * impl note - lazy="no-proxy" currently forces both
 			// lazy and unwrap to be set to true.  The other case we
 			// are extremely interested in here is that of lazy="proxy"
 			// where lazy is set to true, but unwrap is set to false.
 			// thus we use both here under the assumption that this
 			// return is really only ever used during persister
 			// construction to determine the lazy property/field fetch
 			// groupings.  If that assertion changes then this check
 			// needs to change as well.  Partially, this is an issue with
 			// the overloading of the term "lazy" here...
 			ToOne toOneValue = ( ToOne ) value;
 			return toOneValue.isLazy() && toOneValue.isUnwrapProxy();
 		}
 		return lazy;
 	}
 	
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 	
 	public boolean isOptional() {
 		return optional || isNullable();
 	}
 	
 	public void setOptional(boolean optional) {
 		this.optional = optional;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return persistentClass;
 	}
 
 	public void setPersistentClass(PersistentClass persistentClass) {
 		this.persistentClass = persistentClass;
 	}
 
 	public boolean isSelectable() {
 		return selectable;
 	}
 	
 	public void setSelectable(boolean selectable) {
 		this.selectable = selectable;
 	}
 
-	public String getNodeName() {
-		return nodeName;
-	}
-
-	public void setNodeName(String nodeName) {
-		this.nodeName = nodeName;
-	}
-
 	public String getAccessorPropertyName( EntityMode mode ) {
 		return getName();
 	}
 
 	// todo : remove
 	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessStrategy( clazz ).buildPropertyAccess( clazz, name ).getGetter();
 	}
 
 	// todo : remove
 	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessStrategy( clazz ).buildPropertyAccess( clazz, name ).getSetter();
 	}
 
 	// todo : remove
 	public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
 		String accessName = getPropertyAccessorName();
 		if ( accessName == null ) {
 			if ( clazz == null || java.util.Map.class.equals( clazz ) ) {
 				accessName = "map";
 			}
 			else {
 				accessName = "property";
 			}
 		}
 
 		final EntityMode entityMode = clazz == null || java.util.Map.class.equals( clazz )
 				? EntityMode.MAP
 				: EntityMode.POJO;
 
 		return resolveServiceRegistry().getService( PropertyAccessStrategyResolver.class ).resolvePropertyAccessStrategy(
 				clazz,
 				accessName,
 				entityMode
 		);
 	}
 
 	protected ServiceRegistry resolveServiceRegistry() {
 		if ( getPersistentClass() != null ) {
 			return getPersistentClass().getServiceRegistry();
 		}
 		if ( getValue() != null ) {
 			return getValue().getServiceRegistry();
 		}
 		throw new HibernateException( "Could not resolve ServiceRegistry" );
 	}
 
 	public boolean isNaturalIdentifier() {
 		return naturalIdentifier;
 	}
 
 	public void setNaturalIdentifier(boolean naturalIdentifier) {
 		this.naturalIdentifier = naturalIdentifier;
 	}
 
 	public boolean isLob() {
 		return lob;
 	}
 
 	public void setLob(boolean lob) {
 		this.lob = lob;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index b9ed4c53b2..424154a1e1 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2251 +1,2223 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
 import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.sql.ordering.antlr.ColumnReference;
 import org.hibernate.sql.ordering.antlr.FormulaReference;
 import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
 import org.hibernate.sql.ordering.antlr.OrderByTranslation;
 import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.AnyType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
 			AbstractCollectionPersister.class.getName() );
 
 	// TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	// SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final boolean hasWhere;
 	protected final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	private final boolean hasOrder;
 	private final OrderByTranslation orderByTranslation;
 
 	private final boolean hasManyToManyOrder;
 	private final OrderByTranslation manyToManyOrderByTranslation;
 
 	private final int baseIndex;
 
-	private final String nodeName;
-	private final String elementNodeName;
-	private final String indexNodeName;
 	private String mappedByProperty;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	// types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	// columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	// private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	protected final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	// extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	protected final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			Collection collectionBinding,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			PersisterCreationContext creationContext) throws MappingException, CacheException {
 
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collectionBinding.isMap()
 					? StructuredMapCacheEntry.INSTANCE
 					: StructuredCollectionCacheEntry.INSTANCE;
 		}
 		else {
 			cacheEntryStructure = UnstructuredCacheEntry.INSTANCE;
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collectionBinding.getCollectionType();
 		role = collectionBinding.getRole();
 		entityName = collectionBinding.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collectionBinding.getLoaderName();
-		nodeName = collectionBinding.getNodeName();
 		isMutable = collectionBinding.isMutable();
 		mappedByProperty = collectionBinding.getMappedByProperty();
 
 		Table table = collectionBinding.getCollectionTable();
 		fetchMode = collectionBinding.getElement().getFetchMode();
 		elementType = collectionBinding.getElement().getType();
 		// isSet = collectionBinding.isSet();
 		// isSorted = collectionBinding.isSorted();
 		isPrimitiveArray = collectionBinding.isPrimitiveArray();
 		isArray = collectionBinding.isArray();
 		subselectLoadable = collectionBinding.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 				);
 
 		int spacesSize = 1 + collectionBinding.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collectionBinding.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collectionBinding.getWhere() ) ? "( " + collectionBinding.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
 				null;
 
 		hasOrphanDelete = collectionBinding.hasOrphanDelete();
 
 		int batch = collectionBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collectionBinding.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collectionBinding.getKey().getType();
 		iter = collectionBinding.getKey().getColumnIterator();
 		int keySpan = collectionBinding.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, table );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
-		String elemNode = collectionBinding.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
-			if ( elemNode == null ) {
-				elemNode = creationContext.getMetadata().getEntityBinding( entityName ).getNodeName();
-			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
-		elementNodeName = elemNode;
 
 		int elementSpan = collectionBinding.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collectionBinding.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias( dialect, table );
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName( dialect );
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr( dialect );
 				elementColumnReaderTemplates[j] = col.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		// workaround, for backward compatibility of sets with no
 		// not-null columns, assume all columns are used in the
 		// row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collectionBinding.isIndexed();
 		if ( hasIndex ) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collectionBinding;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias( dialect );
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName( dialect );
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
-
-			indexNodeName = indexedCollection.getIndexNodeName();
-
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
-			indexNodeName = null;
 		}
 
 		hasIdentifier = collectionBinding.isIdentified();
 		if ( hasIdentifier ) {
 			if ( collectionBinding.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collectionBinding;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					creationContext.getMetadata().getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 					);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			// unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		// GENERATE THE SQL:
 
 		// sqlSelectString = sqlSelectString();
 		// sqlSelectRowString = sqlSelectRowString();
 
 		if ( collectionBinding.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collectionBinding.getCustomSQLInsert();
 			insertCallable = collectionBinding.isCustomInsertCallable();
 			insertCheckStyle = collectionBinding.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLInsert(), insertCallable )
 					: collectionBinding.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collectionBinding.getCustomSQLUpdate();
 			updateCallable = collectionBinding.isCustomUpdateCallable();
 			updateCheckStyle = collectionBinding.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLUpdate(), insertCallable )
 					: collectionBinding.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collectionBinding.getCustomSQLDelete();
 			deleteCallable = collectionBinding.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collectionBinding.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collectionBinding.getCustomSQLDeleteAll();
 			deleteAllCallable = collectionBinding.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString( collectionBinding.isIndexed() && !collectionBinding.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collectionBinding.isLazy();
 		isExtraLazy = collectionBinding.isExtraLazy();
 
 		isInverse = collectionBinding.isInverse();
 
 		if ( collectionBinding.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collectionBinding ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; // elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 					);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 					);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { // not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 						);
 			}
 		}
 
 		hasOrder = collectionBinding.getOrderBy() != null;
 		if ( hasOrder ) {
 			orderByTranslation = Template.translateOrderBy(
 					collectionBinding.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collectionBinding
 		filterHelper = new FilterHelper( collectionBinding.getFilters(), factory);
 
 		// Handle any filters applied to this collectionBinding for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collectionBinding.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collectionBinding.getManyToManyWhere() ) ?
 				"( " + collectionBinding.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collectionBinding.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collectionBinding.getManyToManyOrdering(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	private class ColumnMapperImpl implements ColumnMapper {
 		@Override
 		public SqlValueReference[] map(String reference) {
 			final String[] columnNames;
 			final String[] formulaTemplates;
 
 			// handle the special "$element$" property name...
 			if ( "$element$".equals( reference ) ) {
 				columnNames = elementColumnNames;
 				formulaTemplates = elementFormulaTemplates;
 			}
 			else {
 				columnNames = elementPropertyMapping.toColumns( reference );
 				formulaTemplates = formulaTemplates( reference, columnNames.length );
 			}
 
 			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
 			int i = 0;
 			for ( final String columnName : columnNames ) {
 				if ( columnName == null ) {
 					// if the column name is null, it indicates that this index in the property value mapping is
 					// actually represented by a formula.
 //					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 					final String formulaTemplate = formulaTemplates[i];
 					result[i] = new FormulaReference() {
 						@Override
 						public String getFormulaFragment() {
 							return formulaTemplate;
 						}
 					};
 				}
 				else {
 					result[i] = new ColumnReference() {
 						@Override
 						public String getColumnName() {
 							return columnName;
 						}
 					};
 				}
 				i++;
 			}
 			return result;
 		}
 	}
 
 	private String[] formulaTemplates(String reference, int expectedSize) {
 		try {
 			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
 		}
 		catch (Exception e) {
 			return new String[expectedSize];
 		}
 	}
 
 	@Override
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for collection: %s", getRole() );
 			if ( getSQLInsertRowString() != null ) {
 				LOG.debugf( " Row insert: %s", getSQLInsertRowString() );
 			}
 			if ( getSQLUpdateRowString() != null ) {
 				LOG.debugf( " Row update: %s", getSQLUpdateRowString() );
 			}
 			if ( getSQLDeleteRowString() != null ) {
 				LOG.debugf( " Row delete: %s", getSQLDeleteRowString() );
 			}
 			if ( getSQLDeleteString() != null ) {
 				LOG.debugf( " One-shot delete: %s", getSQLDeleteString() );
 			}
 		}
 	}
 
 	@Override
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			// if there is a user-specified loader, return that
 			// TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getLoadQueryInfluencers().getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if ( subselect == null ) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	@Override
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	@Override
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	@Override
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	@Override
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	@Override
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	@Override
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	@Override
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	@Override
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise.  needed by arrays
 	 */
 	@Override
 	public Class getElementClass() {
 		return elementClass;
 	}
 
 	@Override
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	@Override
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
 			index = (Integer)index - baseIndex;
 		}
 		return index;
 	}
 
 	@Override
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	@Override
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role ); // an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( indexColumnIsSettable );
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
 			index = (Integer)index + baseIndex;
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( elementIsPureFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based element in the where condition" );
 		}
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsInPrimaryKey, session );
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( indexContainsFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based index in the where condition" );
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	@Override
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	@Override
 	public boolean isArray() {
 		return isArray;
 	}
 
 	@Override
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	@Override
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	@Override
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	@Override
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); // strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 				"max(" + getIndexColumnNames()[0] + ") + 1" : // lists, arrays
 				"count(" + getElementColumnNames()[0] + ")"; // sets, maps, bags
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn( selectValue )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	@Override
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	@Override
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	@Override
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 	}
 
 	@Override
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	@Override
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	@Override
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	@Override
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	@Override
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	@Override
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( st );
 						session.getJdbcCoordinator().afterStatementExecution();
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	protected BasicBatchKey recreateBatchKey;
 
 	@Override
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
 		try {
 			// create all the new entries
 			Iterator entries = collection.entries( this );
 			if ( entries.hasNext() ) {
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				collection.preInsert( this );
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 
 					final Object entry = entries.next();
 					if ( collection.entryExists( entry, i ) ) {
 						int offset = 1;
 						PreparedStatement st = null;
 						boolean callable = isInsertCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLInsertRowString();
 
 						if ( useBatch ) {
 							if ( recreateBatchKey == null ) {
 								recreateBatchKey = new BasicBatchKey(
 										getRole() + "#RECREATE",
 										expectation
 								);
 							}
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( recreateBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 
 							// TODO: copy/paste from insertRows()
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 							loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
 								session
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getJdbcCoordinator().getResourceRegistry().release( st );
 								session.getJdbcCoordinator().afterStatementExecution();
 							}
 						}
 
 					}
 					i++;
 				}
 
 				LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 			}
 			else {
 				LOG.debug( "Collection was empty" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not insert collection: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	@Override
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowDeleteEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Deleting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
 		boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 		final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 		try {
 			// delete all the deleted entries
 			Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 			if ( deletes.hasNext() ) {
 				int offset = 1;
 				int count = 0;
 				while ( deletes.hasNext() ) {
 					PreparedStatement st = null;
 					boolean callable = isDeleteCallable();
 					boolean useBatch = expectation.canBeBatched();
 					String sql = getSQLDeleteRowString();
 
 					if ( useBatch ) {
 						if ( deleteBatchKey == null ) {
 							deleteBatchKey = new BasicBatchKey(
 									getRole() + "#DELETE",
 									expectation
 									);
 						}
 						st = session
 								.getJdbcCoordinator()
 								.getBatch( deleteBatchKey )
 								.getBatchStatement( sql, callable );
 					}
 					else {
 						st = session
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						expectation.prepare( st );
 
 						Object entry = deletes.next();
 						int loc = offset;
 						if ( hasIdentifier ) {
 							writeIdentifier( st, entry, loc, session );
 						}
 						else {
 							loc = writeKey( st, id, loc, session );
 							if ( deleteByIndex ) {
 								writeIndexToWhere( st, entry, loc, session );
 							}
 							else {
 								writeElementToWhere( st, entry, loc, session );
 							}
 						}
 
 						if ( useBatch ) {
 							session
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
 						}
 					}
 
 					LOG.debugf( "Done deleting collection rows: %s deleted", count );
 				}
 			}
 			else {
 				LOG.debug( "No rows to delete" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not delete collection rows: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLDeleteRowString()
 			);
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	@Override
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
 		try {
 			// insert all the new entries
 			collection.preInsert( this );
 			Iterator entries = collection.entries( this );
 			Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 			boolean callable = isInsertCallable();
 			boolean useBatch = expectation.canBeBatched();
 			String sql = getSQLInsertRowString();
 			int i = 0;
 			int count = 0;
 			while ( entries.hasNext() ) {
 				int offset = 1;
 				Object entry = entries.next();
 				PreparedStatement st = null;
 				if ( collection.needsInserting( entry, i, elementType ) ) {
 
 					if ( useBatch ) {
 						if ( insertBatchKey == null ) {
 							insertBatchKey = new BasicBatchKey(
 									getRole() + "#INSERT",
 									expectation
 									);
 						}
 						if ( st == null ) {
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( insertBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 					}
 					else {
 						st = session
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						offset += expectation.prepare( st );
 						// TODO: copy/paste from recreate()
 						offset = writeKey( st, id, offset, session );
 						if ( hasIdentifier ) {
 							offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 						}
 						if ( hasIndex /* && !indexIsFormula */) {
 							offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 						}
 						writeElement( st, collection.getElement( entry ), offset, session );
 
 						if ( useBatch ) {
 							session.getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						collection.afterRowInsert( this, entry, i );
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
 						}
 					}
 				}
 				i++;
 			}
 			LOG.debugf( "Done inserting rows: %s inserted", count );
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not insert collection rows: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	@Override
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	@Override
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	@Override
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	@Override
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	@Override
 	public abstract boolean isManyToMany();
 
 	@Override
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	@Override
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	@Override
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	@Override
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	@Override
 	public String getName() {
 		return getRole();
 	}
 
 	@Override
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
 	@Override
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	@Override
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	@Override
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	/**
 	 * Process queued operations within the PersistentCollection.
 	 *
 	 * @param collection The collection
 	 * @param key The collection key
 	 * @param nextIndex The next index to write
 	 * @param session The session
 	 * @throws HibernateException
 	 *
 	 * @deprecated Use {@link #doProcessQueuedOps(org.hibernate.collection.spi.PersistentCollection, java.io.Serializable, org.hibernate.engine.spi.SessionImplementor)}
 	 */
 	@Deprecated
 	protected void doProcessQueuedOps(PersistentCollection collection, Serializable key,
 			int nextIndex, SessionImplementor session)
 			throws HibernateException {
 		doProcessQueuedOps( collection, key, session );
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException;
 
 	@Override
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(
 			String alias,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	@Override
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
-	@Override
-	public String getNodeName() {
-		return nodeName;
-	}
-
-	@Override
-	public String getElementNodeName() {
-		return elementNodeName;
-	}
-
-	@Override
-	public String getIndexNodeName() {
-		return indexNodeName;
-	}
-
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	@Override
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	@Override
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String[] rawAliases = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String[] result = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	@Override
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	@Override
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 *
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as composite" );
 				}
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "index";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getIndexType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes( this );
 					}
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new IllegalStateException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getElementType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
index 4ba1e1ef16..b7b17b077e 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
@@ -1,324 +1,318 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.collection;
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * A strategy for persisting a collection role. Defines a contract between
  * the persistence strategy and the actual persistent collection framework
  * and session. Does not define operations that are required for querying
  * collections, or loading by outer join.<br>
  * <br>
  * Implements persistence of a collection instance while the instance is
  * referenced in a particular role.<br>
  * <br>
  * This class is highly coupled to the <tt>PersistentCollection</tt>
  * hierarchy, since double dispatch is used to load and update collection
  * elements.<br>
  * <br>
  * May be considered an immutable view of the mapping object
  * <p/>
  * Unless a customer {@link org.hibernate.persister.spi.PersisterFactory} is used, it is expected
  * that implementations of CollectionDefinition define a constructor accepting the following arguments:<ol>
  *     <li>
  *         {@link org.hibernate.mapping.Collection} - The metadata about the collection to be handled
  *         by the persister
  *     </li>
  *     <li>
  *         {@link CollectionRegionAccessStrategy} - the second level caching strategy for this collection
  *     </li>
  *     <li>
  *         {@link org.hibernate.persister.spi.PersisterCreationContext} - access to additional
  *         information useful while constructing the persister.
  *     </li>
  * </ol>
  *
  * @see QueryableCollection
  * @see org.hibernate.collection.spi.PersistentCollection
  * @author Gavin King
  */
 public interface CollectionPersister extends CollectionDefinition {
 	/**
 	 * Initialize the given collection with the given key
 	 */
 	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
 	throws HibernateException;
 	/**
 	 * Is this collection role cacheable
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache
 	 */
 	public CollectionRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 	/**
 	 * Get the associated <tt>Type</tt>
 	 */
 	public CollectionType getCollectionType();
 	/**
 	 * Get the "key" type (the type of the foreign key)
 	 */
 	public Type getKeyType();
 	/**
 	 * Get the "index" type for a list or map (optional operation)
 	 */
 	public Type getIndexType();
 	/**
 	 * Get the "element" type
 	 */
 	public Type getElementType();
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass();
 	/**
 	 * Read the key from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the element from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readElement(
 		ResultSet rs,
 		Object owner,
 		String[] columnAliases,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the index from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the identifier from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIdentifier(
 		ResultSet rs,
 		String columnAlias,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Is this an array or primitive values?
 	 */
 	public boolean isPrimitiveArray();
 	/**
 	 * Is this an array?
 	 */
 	public boolean isArray();
 	/**
 	 * Is this a one-to-many association?
 	 */
 	public boolean isOneToMany();
 	/**
 	 * Is this a many-to-many association?  Note that this is mainly
 	 * a convenience feature as the single persister does not
 	 * conatin all the information needed to handle a many-to-many
 	 * itself, as internally it is looked at as two many-to-ones.
 	 */
 	public boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters);
 
 	/**
 	 * Is this an "indexed" collection? (list or map)
 	 */
 	public boolean hasIndex();
 	/**
 	 * Is this collection lazyily initialized?
 	 */
 	public boolean isLazy();
 	/**
 	 * Is this collection "inverse", so state changes are not
 	 * propogated to the database.
 	 */
 	public boolean isInverse();
 	/**
 	 * Completely remove the persistent state of the collection
 	 */
 	public void remove(Serializable id, SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * (Re)create the collection's persistent state
 	 */
 	public void recreate(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Delete the persistent state of any elements that were removed from
 	 * the collection
 	 */
 	public void deleteRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Update the persistent state of any elements that were modified
 	 */
 	public void updateRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Insert the persistent state of any new collection elements
 	 */
 	public void insertRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	
 	/**
 	 * Process queued operations within the PersistentCollection.
 	 */
 	public void processQueuedOps(
 			PersistentCollection collection,
 			Serializable key,
 			SessionImplementor session)
 			throws HibernateException;
 	
 	/**
 	 * Get the name of this collection role (the fully qualified class name,
 	 * extended by a "property path")
 	 */
 	public String getRole();
 	/**
 	 * Get the persister of the entity that "owns" this collection
 	 */
 	public EntityPersister getOwnerEntityPersister();
 	/**
 	 * Get the surrogate key generation strategy (optional operation)
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 	/**
 	 * Get the type of the surrogate key
 	 */
 	public Type getIdentifierType();
 	/**
 	 * Does this collection implement "orphan delete"?
 	 */
 	public boolean hasOrphanDelete();
 	/**
 	 * Is this an ordered collection? (An ordered collection is
 	 * ordered by the initialization operation, not by sorting
 	 * that happens in memory, as in the case of a sorted collection.)
 	 */
 	public boolean hasOrdering();
 
 	public boolean hasManyToManyOrdering();
 
 	/**
 	 * Get the "space" that holds the persistent state
 	 */
 	public Serializable[] getCollectionSpaces();
 
 	public CollectionMetadata getCollectionMetadata();
 
 	/**
 	 * Is cascade delete handled by the database-level
 	 * foreign key constraint definition?
 	 */
 	public abstract boolean isCascadeDeleteEnabled();
 	
 	/**
 	 * Does this collection cause version increment of the 
 	 * owning entity?
 	 */
 	public boolean isVersioned();
 	
 	/**
 	 * Can the elements of this collection change?
 	 */
 	public boolean isMutable();
 	
 	//public boolean isSubselectLoadable();
 	
-	public String getNodeName();
-	
-	public String getElementNodeName();
-	
-	public String getIndexNodeName();
-
 	public void postInstantiate() throws MappingException;
 	
 	public SessionFactoryImplementor getFactory();
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session);
 
 	/**
 	 * Generates the collection's key column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getKeyColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's index column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the index column alias generation.
 	 * @return The key column aliases, or null if not indexed.
 	 */
 	public String[] getIndexColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's element column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the element column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getElementColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's identifier column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String getIdentifierColumnAlias(String suffix);
 	
 	public boolean isExtraLazy();
 	public int getSize(Serializable key, SessionImplementor session);
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session);
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session);
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner);
 	public int getBatchSize();
 
 	/**
 	 * @return the name of the property this collection is mapped by
 	 */
 	public String getMappedByProperty();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
index 94b90f41e4..bf0828ad3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
@@ -1,115 +1,114 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple;
 
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.type.Type;
 
 /**
  * Represents a defined entity identifier property within the Hibernate
  * runtime-metamodel.
  *
  * @author Steve Ebersole
  */
 public class IdentifierProperty extends AbstractAttribute implements IdentifierAttribute {
 	private boolean virtual;
 	private boolean embedded;
 	private IdentifierValue unsavedValue;
 	private IdentifierGenerator identifierGenerator;
 	private boolean identifierAssignedByInsert;
 	private boolean hasIdentifierMapper;
 
 	/**
 	 * Construct a non-virtual identifier property.
 	 *
 	 * @param name The name of the property representing the identifier within
 	 * its owning entity.
 	 * @param node The node name to use for XML-based representation of this
 	 * property.
 	 * @param type The Hibernate Type for the identifier property.
 	 * @param embedded Is this an embedded identifier.
 	 * @param unsavedValue The value which, if found as the value on the identifier
 	 * property, represents new (i.e., un-saved) instances of the owning entity.
 	 * @param identifierGenerator The generator to use for id value generation.
 	 */
 	public IdentifierProperty(
 			String name,
-			String node,
 			Type type,
 			boolean embedded,
 			IdentifierValue unsavedValue,
 			IdentifierGenerator identifierGenerator) {
 		super( name, type );
 		this.virtual = false;
 		this.embedded = embedded;
 		this.hasIdentifierMapper = false;
 		this.unsavedValue = unsavedValue;
 		this.identifierGenerator = identifierGenerator;
 		this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
 	}
 
 	/**
 	 * Construct a virtual IdentifierProperty.
 	 *
 	 * @param type The Hibernate Type for the identifier property.
 	 * @param embedded Is this an embedded identifier.
 	 * @param unsavedValue The value which, if found as the value on the identifier
 	 * property, represents new (i.e., un-saved) instances of the owning entity.
 	 * @param identifierGenerator The generator to use for id value generation.
 	 */
 	public IdentifierProperty(
 			Type type,
 			boolean embedded,
 			boolean hasIdentifierMapper,
 			IdentifierValue unsavedValue,
 			IdentifierGenerator identifierGenerator) {
 		super( null, type );
 		this.virtual = true;
 		this.embedded = embedded;
 		this.hasIdentifierMapper = hasIdentifierMapper;
 		this.unsavedValue = unsavedValue;
 		this.identifierGenerator = identifierGenerator;
 		this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
 	}
 
 	@Override
 	public boolean isVirtual() {
 		return virtual;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	@Override
 	public IdentifierValue getUnsavedValue() {
 		return unsavedValue;
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	@Override
 	public boolean isIdentifierAssignedByInsert() {
 		return identifierAssignedByInsert;
 	}
 
 	@Override
 	public boolean hasIdentifierMapper() {
 		return hasIdentifierMapper;
 	}
 
 	@Override
 	public String toString() {
 		return "IdentifierAttribute(" + getName() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index 7d7da18a1a..4b770edf44 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,331 +1,330 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple;
 
 import java.lang.reflect.Constructor;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.PropertyAccess;
 import org.hibernate.property.access.spi.PropertyAccessStrategy;
 import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
 import org.hibernate.tuple.entity.EntityBasedAssociationAttribute;
 import org.hibernate.tuple.entity.EntityBasedBasicAttribute;
 import org.hibernate.tuple.entity.EntityBasedCompositionAttribute;
 import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public final class PropertyFactory {
 	private PropertyFactory() {
 	}
 
 	/**
 	 * Generates the attribute representation of the identifier for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 *
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierAttribute(
 			PersistentClass mappedEntity,
 			IdentifierGenerator generator) {
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 		);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 			);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
-					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 			);
 		}
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 *
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(
 			EntityPersister persister,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			Property property,
 			boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 				persister,
 				sessionFactory,
 				attributeNumber,
 				property.getName(),
 				property.getValue().getType(),
 				new BaselineAttributeInformation.Builder()
 						.setLazy( lazy )
 						.setInsertable( property.isInsertable() )
 						.setUpdateable( property.isUpdateable() )
 						.setValueGenerationStrategy( property.getValueGenerationStrategy() )
 						.setNullable( property.isOptional() )
 						.setDirtyCheckable( property.isUpdateable() && !lazy )
 						.setVersionable( property.isOptimisticLocked() )
 						.setCascadeStyle( property.getCascadeStyle() )
 						.createInformation(),
 				unsavedValue
 		);
 	}
 
 	public static enum NonIdentifierAttributeNature {
 		BASIC,
 		COMPOSITE,
 		ANY,
 		ENTITY,
 		COLLECTION
 	}
 
 	/**
 	 * Generate a non-identifier (and non-version) attribute based on the given mapped property from the given entity
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 *
 	 * @return The appropriate NonIdentifierProperty definition.
 	 */
 	public static NonIdentifierAttribute buildEntityBasedAttribute(
 			EntityPersister persister,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			Property property,
 			boolean lazyAvailable) {
 		final Type type = property.getValue().getType();
 
 		final NonIdentifierAttributeNature nature = decode( type );
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		boolean alwaysDirtyCheck = type.isAssociationType() &&
 				( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		switch ( nature ) {
 			case BASIC: {
 				return new EntityBasedBasicAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setValueGenerationStrategy( property.getValueGenerationStrategy() )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			case COMPOSITE: {
 				return new EntityBasedCompositionAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						(CompositeType) type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setValueGenerationStrategy( property.getValueGenerationStrategy() )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			case ENTITY:
 			case ANY:
 			case COLLECTION: {
 				return new EntityBasedAssociationAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						(AssociationType) type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setValueGenerationStrategy( property.getValueGenerationStrategy() )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			default: {
 				throw new HibernateException( "Internal error" );
 			}
 		}
 	}
 
 	private static NonIdentifierAttributeNature decode(Type type) {
 		if ( type.isAssociationType() ) {
 			AssociationType associationType = (AssociationType) type;
 
 			if ( type.isComponentType() ) {
 				// an any type is both an association and a composite...
 				return NonIdentifierAttributeNature.ANY;
 			}
 
 			return type.isCollectionType()
 					? NonIdentifierAttributeNature.COLLECTION
 					: NonIdentifierAttributeNature.ENTITY;
 		}
 		else {
 			if ( type.isComponentType() ) {
 				return NonIdentifierAttributeNature.COMPOSITE;
 			}
 
 			return NonIdentifierAttributeNature.BASIC;
 		}
 	}
 
 	/**
 	 * @deprecated See mainly {@link #buildEntityBasedAttribute}
 	 */
 	@Deprecated
 	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
 		final Type type = property.getValue().getType();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		boolean alwaysDirtyCheck = type.isAssociationType() &&
 				( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		return new StandardProperty(
 				property.getName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
 				property.getValueGenerationStrategy(),
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
 				property.getValue().getFetchMode()
 		);
 	}
 
 
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch (Throwable t) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		final PropertyAccessStrategyResolver propertyAccessStrategyResolver =
 				mappingProperty.getPersistentClass().getServiceRegistry().getService( PropertyAccessStrategyResolver.class );
 
 		final PropertyAccessStrategy propertyAccessStrategy = propertyAccessStrategyResolver.resolvePropertyAccessStrategy(
 				mappingProperty.getClass(),
 				mappingProperty.getPropertyAccessorName(),
 				EntityMode.POJO
 		);
 
 		final PropertyAccess propertyAccess = propertyAccessStrategy.buildPropertyAccess(
 				mappingProperty.getPersistentClass().getMappedClass(),
 				mappingProperty.getName()
 		);
 
 		return propertyAccess.getGetter();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index d6365d4b37..4d9c695250 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,858 +1,846 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
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
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
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
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(
 				final PersistentClass persistentClass,
 				final EntityRegionAccessStrategy cacheAccessStrategy,
 				final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				final PersisterCreationContext creationContext) {
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
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public EntityEntryFactory getEntityEntryFactory() {
 			return MutableEntityEntryFactory.INSTANCE;
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
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
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
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
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
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
 
         @Override
         public int[] resolveAttributeIndexes(String[] attributeNames) {
             return null;
         }
 
 		@Override
 		public boolean canUseReferenceCacheEntries() {
 			return false;
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(
 				Collection collectionBinding,
 				CollectionRegionAccessStrategy cacheAccessStrategy,
 				PersisterCreationContext creationContext) {
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
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
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
 
-		public String getNodeName() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public String getElementNodeName() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public String getIndexNodeName() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
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
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 
 		@Override
 		public String getMappedByProperty() {
 			return null;
 		}
 
 		@Override
 		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/reader/AuditedPropertiesReader.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/reader/AuditedPropertiesReader.java
index 3c97b7a00c..4700d270f4 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/reader/AuditedPropertiesReader.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/reader/AuditedPropertiesReader.java
@@ -1,756 +1,756 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.envers.configuration.internal.metadata.reader;
 
 import java.lang.annotation.Annotation;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
 import javax.persistence.JoinColumn;
 import javax.persistence.MapKey;
 import javax.persistence.OneToMany;
 import javax.persistence.Version;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.envers.AuditJoinTable;
 import org.hibernate.envers.AuditMappedBy;
 import org.hibernate.envers.AuditOverride;
 import org.hibernate.envers.AuditOverrides;
 import org.hibernate.envers.Audited;
 import org.hibernate.envers.ModificationStore;
 import org.hibernate.envers.NotAudited;
 import org.hibernate.envers.RelationTargetAuditMode;
 import org.hibernate.envers.configuration.internal.GlobalConfiguration;
 import org.hibernate.envers.configuration.internal.metadata.MetadataTools;
 import org.hibernate.envers.internal.tools.MappingTools;
 import org.hibernate.envers.internal.tools.ReflectionTools;
 import org.hibernate.envers.internal.tools.StringTools;
+import org.hibernate.loader.PropertyPath;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Value;
 
 import static org.hibernate.envers.internal.tools.Tools.newHashMap;
 import static org.hibernate.envers.internal.tools.Tools.newHashSet;
 
 /**
  * Reads persistent properties form a {@link PersistentPropertiesSource} and adds the ones that are audited to a
  * {@link AuditedPropertiesHolder}, filling all the auditing data.
  *
  * @author Adam Warski (adam at warski dot org)
  * @author Erik-Berndt Scheper
  * @author Hern&aacut;n Chanfreau
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  * @author Michal Skowronek (mskowr at o2 dot pl)
  * @author Lukasz Zuchowski (author at zuchos dot com)
  */
 public class AuditedPropertiesReader {
 	protected final ModificationStore defaultStore;
 	private final PersistentPropertiesSource persistentPropertiesSource;
 	private final AuditedPropertiesHolder auditedPropertiesHolder;
 	private final GlobalConfiguration globalCfg;
 	private final ReflectionManager reflectionManager;
 	private final String propertyNamePrefix;
 
 	private final Set<String> propertyAccessedPersistentProperties;
 	private final Set<String> fieldAccessedPersistentProperties;
 	// Mapping class field to corresponding <properties> element.
 	private final Map<String, String> propertiesGroupMapping;
 
 	private final Set<XProperty> overriddenAuditedProperties;
 	private final Set<XProperty> overriddenNotAuditedProperties;
 
 	private final Set<XClass> overriddenAuditedClasses;
 	private final Set<XClass> overriddenNotAuditedClasses;
 
 	public AuditedPropertiesReader(
 			ModificationStore defaultStore,
 			PersistentPropertiesSource persistentPropertiesSource,
 			AuditedPropertiesHolder auditedPropertiesHolder,
 			GlobalConfiguration globalCfg,
 			ReflectionManager reflectionManager,
 			String propertyNamePrefix) {
 		this.defaultStore = defaultStore;
 		this.persistentPropertiesSource = persistentPropertiesSource;
 		this.auditedPropertiesHolder = auditedPropertiesHolder;
 		this.globalCfg = globalCfg;
 		this.reflectionManager = reflectionManager;
 		this.propertyNamePrefix = propertyNamePrefix;
 
 		propertyAccessedPersistentProperties = newHashSet();
 		fieldAccessedPersistentProperties = newHashSet();
 		propertiesGroupMapping = newHashMap();
 
 		overriddenAuditedProperties = newHashSet();
 		overriddenNotAuditedProperties = newHashSet();
 
 		overriddenAuditedClasses = newHashSet();
 		overriddenNotAuditedClasses = newHashSet();
 	}
 
 	public void read() {
 		// First reading the access types for the persistent properties.
 		readPersistentPropertiesAccess();
 
 		if ( persistentPropertiesSource instanceof DynamicComponentSource ) {
 			addPropertiesFromDynamicComponent( (DynamicComponentSource) persistentPropertiesSource );
 		}
 		else {
 			// Retrieve classes and properties that are explicitly marked for auditing process by any superclass
 			// of currently mapped entity or itself.
 			final XClass clazz = persistentPropertiesSource.getXClass();
 			readAuditOverrides( clazz );
 
 			// Adding all properties from the given class.
 			addPropertiesFromClass( clazz );
 		}
 	}
 
 	/**
 	 * Recursively constructs sets of audited and not audited properties and classes which behavior has been overridden
 	 * using {@link AuditOverride} annotation.
 	 *
 	 * @param clazz Class that is being processed. Currently mapped entity shall be passed during first invocation.
 	 */
 	private void readAuditOverrides(XClass clazz) {
 		/* TODO: Code to remove with @Audited.auditParents - start. */
 		final Audited allClassAudited = clazz.getAnnotation( Audited.class );
 		if ( allClassAudited != null && allClassAudited.auditParents().length > 0 ) {
 			for ( Class c : allClassAudited.auditParents() ) {
 				final XClass parentClass = reflectionManager.toXClass( c );
 				checkSuperclass( clazz, parentClass );
 				if ( !overriddenNotAuditedClasses.contains( parentClass ) ) {
 					// If the class has not been marked as not audited by the subclass.
 					overriddenAuditedClasses.add( parentClass );
 				}
 			}
 		}
 		/* TODO: Code to remove with @Audited.auditParents - finish. */
 		final List<AuditOverride> auditOverrides = computeAuditOverrides( clazz );
 		for ( AuditOverride auditOverride : auditOverrides ) {
 			if ( auditOverride.forClass() != void.class ) {
 				final XClass overrideClass = reflectionManager.toXClass( auditOverride.forClass() );
 				checkSuperclass( clazz, overrideClass );
 				final String propertyName = auditOverride.name();
 				if ( !StringTools.isEmpty( propertyName ) ) {
 					// Override @Audited annotation on property level.
 					final XProperty property = getProperty( overrideClass, propertyName );
 					if ( auditOverride.isAudited() ) {
 						if ( !overriddenNotAuditedProperties.contains( property ) ) {
 							// If the property has not been marked as not audited by the subclass.
 							overriddenAuditedProperties.add( property );
 						}
 					}
 					else {
 						if ( !overriddenAuditedProperties.contains( property ) ) {
 							// If the property has not been marked as audited by the subclass.
 							overriddenNotAuditedProperties.add( property );
 						}
 					}
 				}
 				else {
 					// Override @Audited annotation on class level.
 					if ( auditOverride.isAudited() ) {
 						if ( !overriddenNotAuditedClasses.contains( overrideClass ) ) {
 							// If the class has not been marked as not audited by the subclass.
 							overriddenAuditedClasses.add( overrideClass );
 						}
 					}
 					else {
 						if ( !overriddenAuditedClasses.contains( overrideClass ) ) {
 							// If the class has not been marked as audited by the subclass.
 							overriddenNotAuditedClasses.add( overrideClass );
 						}
 					}
 				}
 			}
 		}
 		final XClass superclass = clazz.getSuperclass();
 		if ( !clazz.isInterface() && !Object.class.getName().equals( superclass.getName() ) ) {
 			readAuditOverrides( superclass );
 		}
 	}
 
 	/**
 	 * @param clazz Source class.
 	 *
 	 * @return List of @AuditOverride annotations applied at class level.
 	 */
 	private List<AuditOverride> computeAuditOverrides(XClass clazz) {
 		final AuditOverrides auditOverrides = clazz.getAnnotation( AuditOverrides.class );
 		final AuditOverride auditOverride = clazz.getAnnotation( AuditOverride.class );
 		if ( auditOverrides == null && auditOverride != null ) {
 			return Arrays.asList( auditOverride );
 		}
 		else if ( auditOverrides != null && auditOverride == null ) {
 			return Arrays.asList( auditOverrides.value() );
 		}
 		else if ( auditOverrides != null && auditOverride != null ) {
 			throw new MappingException(
 					"@AuditOverrides annotation should encapsulate all @AuditOverride declarations. " +
 							"Please revise Envers annotations applied to class " + clazz.getName() + "."
 			);
 		}
 		return Collections.emptyList();
 	}
 
 	/**
 	 * Checks whether one class is assignable from another. If not {@link MappingException} is thrown.
 	 *
 	 * @param child Subclass.
 	 * @param parent Superclass.
 	 */
 	private void checkSuperclass(XClass child, XClass parent) {
 		if ( !parent.isAssignableFrom( child ) ) {
 			throw new MappingException(
 					"Class " + parent.getName() + " is not assignable from " + child.getName() + ". " +
 							"Please revise Envers annotations applied to " + child.getName() + " type."
 			);
 		}
 	}
 
 	/**
 	 * Checks whether class contains property with a given name. If not {@link MappingException} is thrown.
 	 *
 	 * @param clazz Class.
 	 * @param propertyName Property name.
 	 *
 	 * @return Property object.
 	 */
 	private XProperty getProperty(XClass clazz, String propertyName) {
 		final XProperty property = ReflectionTools.getProperty( clazz, propertyName );
 		if ( property == null ) {
 			throw new MappingException(
 					"Property '" + propertyName + "' not found in class " + clazz.getName() + ". " +
 							"Please revise Envers annotations applied to class " + persistentPropertiesSource.getXClass() + "."
 			);
 		}
 		return property;
 	}
 
 	private void readPersistentPropertiesAccess() {
 		final Iterator<Property> propertyIter = persistentPropertiesSource.getPropertyIterator();
 		while ( propertyIter.hasNext() ) {
 			final Property property = propertyIter.next();
 			addPersistentProperty( property );
-			if ( "embedded".equals( property.getPropertyAccessorName() )
-					&& property.getName().equals( property.getNodeName() ) ) {
-				// If property name equals node name and embedded accessor type is used, processing component
-				// has been defined with <properties> tag. See HHH-6636 JIRA issue.
+			// See HHH-6636
+			if ( "embedded".equals( property.getPropertyAccessorName() ) && !PropertyPath.IDENTIFIER_MAPPER_PROPERTY.equals( property.getName() ) ) {
 				createPropertiesGroupMapping( property );
 			}
 		}
 	}
 
 	private void addPersistentProperty(Property property) {
 		if ( "field".equals( property.getPropertyAccessorName() ) ) {
 			fieldAccessedPersistentProperties.add( property.getName() );
 		}
 		else {
 			propertyAccessedPersistentProperties.add( property.getName() );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private void createPropertiesGroupMapping(Property property) {
 		final Component component = (Component) property.getValue();
 		final Iterator<Property> componentProperties = component.getPropertyIterator();
 		while ( componentProperties.hasNext() ) {
 			final Property componentProperty = componentProperties.next();
-			propertiesGroupMapping.put( componentProperty.getName(), component.getNodeName() );
+			propertiesGroupMapping.put( componentProperty.getName(), property.getName() );
 		}
 	}
 
 	/**
 	 * @param clazz Class which properties are currently being added.
 	 *
 	 * @return {@link Audited} annotation of specified class. If processed type hasn't been explicitly marked, method
 	 *         checks whether given class exists in {@link AuditedPropertiesReader#overriddenAuditedClasses} collection.
 	 *         In case of success, {@link Audited} configuration of currently mapped entity is returned, otherwise
 	 *         {@code null}. If processed type exists in {@link AuditedPropertiesReader#overriddenNotAuditedClasses}
 	 *         collection, the result is also {@code null}.
 	 */
 	private Audited computeAuditConfiguration(XClass clazz) {
 		Audited allClassAudited = clazz.getAnnotation( Audited.class );
 		// If processed class is not explicitly marked with @Audited annotation, check whether auditing is
 		// forced by any of its child entities configuration (@AuditedOverride.forClass).
 		if ( allClassAudited == null && overriddenAuditedClasses.contains( clazz ) ) {
 			// Declared audited parent copies @Audited.modStore and @Audited.targetAuditMode configuration from
 			// currently mapped entity.
 			allClassAudited = persistentPropertiesSource.getXClass().getAnnotation( Audited.class );
 			if ( allClassAudited == null ) {
 				// If parent class declares @Audited on the field/property level.
 				allClassAudited = DEFAULT_AUDITED;
 			}
 		}
 		else if ( allClassAudited != null && overriddenNotAuditedClasses.contains( clazz ) ) {
 			return null;
 		}
 		return allClassAudited;
 	}
 
 	private void addPropertiesFromDynamicComponent(DynamicComponentSource dynamicComponentSource) {
 		Audited audited = computeAuditConfiguration( dynamicComponentSource.getXClass() );
 		if ( !fieldAccessedPersistentProperties.isEmpty() ) {
 			throw new MappingException(
 					"Audited dynamic component cannot have properties with access=\"field\" for properties: " + fieldAccessedPersistentProperties + ". \n Change properties access=\"property\", to make it work)"
 			);
 		}
 		for ( String property : propertyAccessedPersistentProperties ) {
 			String accessType = AccessType.PROPERTY.getType();
 			if ( !auditedPropertiesHolder.contains( property ) ) {
 				final Value propertyValue = persistentPropertiesSource.getProperty( property ).getValue();
 				if ( propertyValue instanceof Component ) {
 					this.addFromComponentProperty(
 							new DynamicProperty( dynamicComponentSource, property ),
 							accessType,
 							(Component) propertyValue,
 							audited
 					);
 				}
 				else {
 					this.addFromNotComponentProperty(
 							new DynamicProperty( dynamicComponentSource, property ),
 							accessType,
 							audited
 					);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Recursively adds all audited properties of entity class and its superclasses.
 	 *
 	 * @param clazz Currently processed class.
 	 */
 	private void addPropertiesFromClass(XClass clazz) {
 		final Audited allClassAudited = computeAuditConfiguration( clazz );
 
 		//look in the class
 		addFromProperties(
 				clazz.getDeclaredProperties( "field" ),
 				"field",
 				fieldAccessedPersistentProperties,
 				allClassAudited
 		);
 		addFromProperties(
 				clazz.getDeclaredProperties( "property" ),
 				"property",
 				propertyAccessedPersistentProperties,
 				allClassAudited
 		);
 
 		if ( allClassAudited != null || !auditedPropertiesHolder.isEmpty() ) {
 			final XClass superclazz = clazz.getSuperclass();
 			if ( !clazz.isInterface() && !"java.lang.Object".equals( superclazz.getName() ) ) {
 				addPropertiesFromClass( superclazz );
 			}
 		}
 	}
 
 	private void addFromProperties(
 			Iterable<XProperty> properties,
 			String accessType,
 			Set<String> persistentProperties,
 			Audited allClassAudited) {
 		for ( XProperty property : properties ) {
 			// If this is not a persistent property, with the same access type as currently checked,
 			// it's not audited as well.
 			// If the property was already defined by the subclass, is ignored by superclasses
 			if ( persistentProperties.contains( property.getName() )
 					&& !auditedPropertiesHolder.contains( property.getName() ) ) {
 				final Value propertyValue = persistentPropertiesSource.getProperty( property.getName() ).getValue();
 				if ( propertyValue instanceof Component ) {
 					this.addFromComponentProperty( property, accessType, (Component) propertyValue, allClassAudited );
 				}
 				else {
 					this.addFromNotComponentProperty( property, accessType, allClassAudited );
 				}
 			}
 			else if ( propertiesGroupMapping.containsKey( property.getName() ) ) {
 				// Retrieve embedded component name based on class field.
 				final String embeddedName = propertiesGroupMapping.get( property.getName() );
 				if ( !auditedPropertiesHolder.contains( embeddedName ) ) {
 					// Manage properties mapped within <properties> tag.
 					final Value propertyValue = persistentPropertiesSource.getProperty( embeddedName ).getValue();
 					this.addFromPropertiesGroup(
 							embeddedName,
 							property,
 							accessType,
 							(Component) propertyValue,
 							allClassAudited
 					);
 				}
 			}
 		}
 	}
 
 	private void addFromPropertiesGroup(
 			String embeddedName,
 			XProperty property,
 			String accessType,
 			Component propertyValue,
 			Audited allClassAudited) {
 		final ComponentAuditingData componentData = new ComponentAuditingData();
 		final boolean isAudited = fillPropertyData( property, componentData, accessType, allClassAudited );
 		if ( isAudited ) {
 			// EntityPersister.getPropertyNames() returns name of embedded component instead of class field.
 			componentData.setName( embeddedName );
 			// Marking component properties as placed directly in class (not inside another component).
 			componentData.setBeanName( null );
 
 			final PersistentPropertiesSource componentPropertiesSource = new ComponentPropertiesSource(
 					reflectionManager,
 					propertyValue
 			);
 			final AuditedPropertiesReader audPropReader = new AuditedPropertiesReader(
 					ModificationStore.FULL, componentPropertiesSource, componentData, globalCfg, reflectionManager,
 					propertyNamePrefix + MappingTools.createComponentPrefix( embeddedName )
 			);
 			audPropReader.read();
 
 			auditedPropertiesHolder.addPropertyAuditingData( embeddedName, componentData );
 		}
 	}
 
 	private void addFromComponentProperty(
 			XProperty property,
 			String accessType,
 			Component propertyValue,
 			Audited allClassAudited) {
 		final ComponentAuditingData componentData = new ComponentAuditingData();
 		final boolean isAudited = fillPropertyData( property, componentData, accessType, allClassAudited );
 
 		final PersistentPropertiesSource componentPropertiesSource;
 		if ( propertyValue.isDynamic() ) {
 			componentPropertiesSource = new DynamicComponentSource( reflectionManager, propertyValue, property );
 		}
 		else {
 			componentPropertiesSource = new ComponentPropertiesSource( reflectionManager, propertyValue );
 		}
 
 		final ComponentAuditedPropertiesReader audPropReader = new ComponentAuditedPropertiesReader(
 				ModificationStore.FULL,
 				componentPropertiesSource,
 				componentData,
 				globalCfg,
 				reflectionManager,
 				propertyNamePrefix + MappingTools.createComponentPrefix( property.getName() )
 		);
 		audPropReader.read();
 
 		if ( isAudited ) {
 			// Now we know that the property is audited
 			auditedPropertiesHolder.addPropertyAuditingData( property.getName(), componentData );
 		}
 	}
 
 	private void addFromNotComponentProperty(XProperty property, String accessType, Audited allClassAudited) {
 		final PropertyAuditingData propertyData = new PropertyAuditingData();
 		final boolean isAudited = fillPropertyData( property, propertyData, accessType, allClassAudited );
 
 		if ( isAudited ) {
 			// Now we know that the property is audited
 			auditedPropertiesHolder.addPropertyAuditingData( property.getName(), propertyData );
 		}
 	}
 
 
 	/**
 	 * Checks if a property is audited and if yes, fills all of its data.
 	 *
 	 * @param property Property to check.
 	 * @param propertyData Property data, on which to set this property's modification store.
 	 * @param accessType Access type for the property.
 	 *
 	 * @return False if this property is not audited.
 	 */
 	private boolean fillPropertyData(
 			XProperty property,
 			PropertyAuditingData propertyData,
 			String accessType,
 			Audited allClassAudited) {
 
 		// check if a property is declared as not audited to exclude it
 		// useful if a class is audited but some properties should be excluded
 		final NotAudited unVer = property.getAnnotation( NotAudited.class );
 		if ( ( unVer != null
 				&& !overriddenAuditedProperties.contains( property ) )
 				|| overriddenNotAuditedProperties.contains( property ) ) {
 			return false;
 		}
 		else {
 			// if the optimistic locking field has to be unversioned and the current property
 			// is the optimistic locking field, don't audit it
 			if ( globalCfg.isDoNotAuditOptimisticLockingField() ) {
 				final Version jpaVer = property.getAnnotation( Version.class );
 				if ( jpaVer != null ) {
 					return false;
 				}
 			}
 		}
 
 		final String propertyName = propertyNamePrefix + property.getName();
 		if ( !this.checkAudited( property, propertyData,propertyName, allClassAudited, globalCfg.getModifiedFlagSuffix() ) ) {
 			return false;
 		}
 
 		propertyData.setName( propertyName );
 		propertyData.setBeanName( property.getName() );
 		propertyData.setAccessType( accessType );
 
 		addPropertyJoinTables( property, propertyData );
 		addPropertyAuditingOverrides( property, propertyData );
 		if ( !processPropertyAuditingOverrides( property, propertyData ) ) {
 			// not audited due to AuditOverride annotation
 			return false;
 		}
 		addPropertyMapKey( property, propertyData );
 		setPropertyAuditMappedBy( property, propertyData );
 		setPropertyRelationMappedBy( property, propertyData );
 
 		return true;
 	}
 
 
 	protected boolean checkAudited(
 			XProperty property,
 			PropertyAuditingData propertyData, String propertyName,
 			Audited allClassAudited, String modifiedFlagSuffix) {
 		// Checking if this property is explicitly audited or if all properties are.
 		Audited aud = ( property.isAnnotationPresent( Audited.class ) )
 				? property.getAnnotation( Audited.class )
 				: allClassAudited;
 		if ( aud == null
 				&& overriddenAuditedProperties.contains( property )
 				&& !overriddenNotAuditedProperties.contains( property ) ) {
 			// Assigning @Audited defaults. If anyone needs to customize those values in the future,
 			// appropriate fields shall be added to @AuditOverride annotation.
 			aud = DEFAULT_AUDITED;
 		}
 		if ( aud != null ) {
 			propertyData.setStore( aud.modStore() );
 			propertyData.setRelationTargetAuditMode( aud.targetAuditMode() );
 			propertyData.setUsingModifiedFlag( checkUsingModifiedFlag( aud ) );
 			if(aud.modifiedColumnName() != null && !"".equals(aud.modifiedColumnName())) {
 				propertyData.setModifiedFlagName(aud.modifiedColumnName());
 			}
 			else {
 				propertyData.setModifiedFlagName(
 						MetadataTools.getModifiedFlagPropertyName(propertyName, modifiedFlagSuffix)
 				);
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	protected boolean checkUsingModifiedFlag(Audited aud) {
 		return globalCfg.hasSettingForUsingModifiedFlag() ?
 				globalCfg.isGlobalWithModifiedFlag() : aud.withModifiedFlag();
 	}
 
 	private void setPropertyRelationMappedBy(XProperty property, PropertyAuditingData propertyData) {
 		final OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		if ( oneToMany != null && !"".equals( oneToMany.mappedBy() ) ) {
 			propertyData.setRelationMappedBy( oneToMany.mappedBy() );
 		}
 	}
 
 	private void setPropertyAuditMappedBy(XProperty property, PropertyAuditingData propertyData) {
 		final AuditMappedBy auditMappedBy = property.getAnnotation( AuditMappedBy.class );
 		if ( auditMappedBy != null ) {
 			propertyData.setAuditMappedBy( auditMappedBy.mappedBy() );
 			if ( !"".equals( auditMappedBy.positionMappedBy() ) ) {
 				propertyData.setPositionMappedBy( auditMappedBy.positionMappedBy() );
 			}
 		}
 	}
 
 	private void addPropertyMapKey(XProperty property, PropertyAuditingData propertyData) {
 		final MapKey mapKey = property.getAnnotation( MapKey.class );
 		if ( mapKey != null ) {
 			propertyData.setMapKey( mapKey.name() );
 		}
 	}
 
 	private void addPropertyJoinTables(XProperty property, PropertyAuditingData propertyData) {
 		// first set the join table based on the AuditJoinTable annotation
 		final AuditJoinTable joinTable = property.getAnnotation( AuditJoinTable.class );
 		if ( joinTable != null ) {
 			propertyData.setJoinTable( joinTable );
 		}
 		else {
 			propertyData.setJoinTable( DEFAULT_AUDIT_JOIN_TABLE );
 		}
 	}
 
 	/**
 	 * Add the {@link AuditOverride} annotations.
 	 *
 	 * @param property the property being processed
 	 * @param propertyData the Envers auditing data for this property
 	 */
 	private void addPropertyAuditingOverrides(XProperty property, PropertyAuditingData propertyData) {
 		final AuditOverride annotationOverride = property.getAnnotation( AuditOverride.class );
 		if ( annotationOverride != null ) {
 			propertyData.addAuditingOverride( annotationOverride );
 		}
 		final AuditOverrides annotationOverrides = property.getAnnotation( AuditOverrides.class );
 		if ( annotationOverrides != null ) {
 			propertyData.addAuditingOverrides( annotationOverrides );
 		}
 	}
 
 	/**
 	 * Process the {@link AuditOverride} annotations for this property.
 	 *
 	 * @param property the property for which the {@link AuditOverride}
 	 * annotations are being processed
 	 * @param propertyData the Envers auditing data for this property
 	 *
 	 * @return {@code false} if isAudited() of the override annotation was set to
 	 */
 	private boolean processPropertyAuditingOverrides(XProperty property, PropertyAuditingData propertyData) {
 		// if this property is part of a component, process all override annotations
 		if ( this.auditedPropertiesHolder instanceof ComponentAuditingData ) {
 			final List<AuditOverride> overrides = ( (ComponentAuditingData) this.auditedPropertiesHolder ).getAuditingOverrides();
 			for ( AuditOverride override : overrides ) {
 				if ( property.getName().equals( override.name() ) ) {
 					// the override applies to this property
 					if ( !override.isAudited() ) {
 						return false;
 					}
 					else {
 						if ( override.auditJoinTable() != null ) {
 							propertyData.setJoinTable( override.auditJoinTable() );
 						}
 					}
 				}
 			}
 
 		}
 		return true;
 	}
 
 	private static final Audited DEFAULT_AUDITED = new Audited() {
 		@Override
 		public ModificationStore modStore() {
 			return ModificationStore.FULL;
 		}
 
 		@Override
 		public RelationTargetAuditMode targetAuditMode() {
 			return RelationTargetAuditMode.AUDITED;
 		}
 
 		@Override
 		public Class[] auditParents() {
 			return new Class[0];
 		}
 
 		@Override
 		public boolean withModifiedFlag() {
 			return false;
 		}
 
 		@Override
 		public String modifiedColumnName() {
 			return "";
 		}
 
 		@Override
 		public Class<? extends Annotation> annotationType() {
 			return this.getClass();
 		}
 	};
 
 	private static final AuditJoinTable DEFAULT_AUDIT_JOIN_TABLE = new AuditJoinTable() {
 		@Override
 		public String name() {
 			return "";
 		}
 
 		@Override
 		public String schema() {
 			return "";
 		}
 
 		@Override
 		public String catalog() {
 			return "";
 		}
 
 		@Override
 		public JoinColumn[] inverseJoinColumns() {
 			return new JoinColumn[0];
 		}
 
 		@Override
 		public Class<? extends Annotation> annotationType() {
 			return this.getClass();
 		}
 	};
 
 	public static class ComponentPropertiesSource implements PersistentPropertiesSource {
 		private final XClass xclass;
 		private final Component component;
 
 		protected ComponentPropertiesSource(XClass xClazz, Component component) {
 			this.xclass = xClazz;
 			this.component = component;
 		}
 
 		public ComponentPropertiesSource(ReflectionManager reflectionManager, Component component) {
 			try {
 				this.xclass = reflectionManager.classForName( component.getComponentClassName() );
 			}
 			catch ( ClassLoadingException e ) {
 				throw new MappingException( e );
 			}
 
 			this.component = component;
 		}
 
 		@Override
 		@SuppressWarnings({ "unchecked" })
 		public Iterator<Property> getPropertyIterator() {
 			return component.getPropertyIterator();
 		}
 
 		@Override
 		public Property getProperty(String propertyName) {
 			return component.getProperty( propertyName );
 		}
 
 		@Override
 		public XClass getXClass() {
 			return xclass;
 		}
 	}
 
 	public static class DynamicComponentSource extends ComponentPropertiesSource {
 
 		private XProperty baseProperty;
 
 		public DynamicComponentSource(ReflectionManager reflectionManager, Component component, XProperty baseProperty) {
 			super( reflectionManager.toXClass( Map.class ), component );
 			this.baseProperty = baseProperty;
 		}
 	}
 
 }
