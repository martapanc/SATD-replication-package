diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index a9a0e777d7..b778a4c426 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,289 +1,285 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlQueryElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
-import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
  * {@link org.hibernate.metamodel.source.SourceProcessor}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor {
 	private final MetadataImplementor metadata;
 	private final MappingDocument mappingDocument;
 
 	private Value<ClassLoaderService> classLoaderService = new Value<ClassLoaderService>(
 			new Value.DeferredInitializer<ClassLoaderService>() {
 				@Override
 				public ClassLoaderService initialize() {
 					return metadata.getServiceRegistry().getService( ClassLoaderService.class );
 				}
 			}
 	);
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, MappingDocument mappingDocument) {
 		this.metadata = metadata;
 		this.mappingDocument = mappingDocument;
 	}
 
 	private XMLHibernateMapping mappingRoot() {
 		return mappingDocument.getMappingRoot();
 	}
 
 	private Origin origin() {
 		return mappingDocument.getOrigin();
 	}
 
 	private HbmBindingContext bindingContext() {
 		return mappingDocument.getMappingLocalBindingContext();
 	}
 
 	private <T> Class<T> classForName(String name) {
 		return classLoaderService.getValue().classForName( bindingContext().qualifyClassName( name ) );
 	}
 
 	public void processIndependentMetadata() {
 		processDatabaseObjectDefinitions();
 		processTypeDefinitions();
 	}
 
 	private void processDatabaseObjectDefinitions() {
 		if ( mappingRoot().getDatabaseObject() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : mappingRoot().getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							origin()
 					);
 				}
 			}
 			else {
 				Set<String> dialectScopes = new HashSet<String>();
 				if ( databaseObjectElement.getDialectScope() != null ) {
 					for ( XMLHibernateMapping.XMLDatabaseObject.XMLDialectScope dialectScope : databaseObjectElement.getDialectScope() ) {
 						dialectScopes.add( dialectScope.getName() );
 					}
 				}
 				auxiliaryDatabaseObject = new BasicAuxiliaryDatabaseObjectImpl(
 						databaseObjectElement.getCreate(),
 						databaseObjectElement.getDrop(),
 						dialectScopes
 				);
 			}
-			metadata.addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
+			metadata.getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 	}
 
 	private void processTypeDefinitions() {
 		if ( mappingRoot().getTypedef() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLTypedef typedef : mappingRoot().getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition(
 					new TypeDef(
 							typedef.getName(),
 							typedef.getClazz(),
 							parameters
 					)
 			);
 		}
 	}
 
 	public void processTypeDependentMetadata() {
 		processFilterDefinitions();
 		processIdentifierGenerators();
 	}
 
 	private void processFilterDefinitions() {
 		if ( mappingRoot().getFilterDef() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : mappingRoot().getFilterDef() ) {
 			final String name = filterDefinition.getName();
 			final Map<String,Type> parameters = new HashMap<String, Type>();
 			String condition = null;
 			for ( Object o : filterDefinition.getContent() ) {
 				if ( o instanceof String ) {
 					// represents the condition
 					if ( condition != null ) {
 						// log?
 					}
 					condition = (String) o;
 				}
 				else if ( o instanceof XMLHibernateMapping.XMLFilterDef.XMLFilterParam ) {
 					final XMLHibernateMapping.XMLFilterDef.XMLFilterParam paramElement = (XMLHibernateMapping.XMLFilterDef.XMLFilterParam) o;
 					// todo : should really delay this resolution until later to allow typedef names
 					parameters.put(
 							paramElement.getName(),
 							metadata.getTypeResolver().heuristicType( paramElement.getType() )
 					);
 				}
 				else {
 					throw new MappingException( "Unrecognized nested filter content", origin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void processIdentifierGenerators() {
 		if ( mappingRoot().getIdentifierGenerator() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : mappingRoot().getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void processMappingDependentMetadata() {
 		processFetchProfiles();
 		processImports();
 		processResultSetMappings();
 		processNamedQueries();
 	}
 
 	private void processFetchProfiles(){
 		if ( mappingRoot().getFetchProfile() == null ) {
 			return;
 		}
 
 		processFetchProfiles( mappingRoot().getFetchProfile(), null );
 	}
 
 	public void processFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							origin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void processImports() {
 		if ( mappingRoot().getImport() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLImport importValue : mappingRoot().getImport() ) {
 			String className = mappingDocument.getMappingLocalBindingContext().qualifyClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void processResultSetMappings() {
 		if ( mappingRoot().getResultset() == null ) {
 			return;
 		}
 
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void processNamedQueries() {
 		if ( mappingRoot().getQueryOrSqlQuery() == null ) {
 			return;
 		}
 
 		for ( Object queryOrSqlQuery : mappingRoot().getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), origin()
 				);
 			}
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index e56033cd5e..293c688729 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,592 +1,593 @@
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
+import org.hibernate.metamodel.source.annotations.AnnotationsSourceProcessor;
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
 
 	private final Database database = new Database();
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
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
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			sourceProcessors = new SourceProcessor[] {
 					new HbmSourceProcessorImpl( this ),
 					new AnnotationsSourceProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
 					new AnnotationsSourceProcessor( this ),
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
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
 		if ( rootEntityBinding == null ) {
 			EntityBinding entityBinding = entityBindingMap.get( entityName );
 			if ( entityBinding == null ) {
 				throw new IllegalStateException( "Unknown entity binding: " + entityName );
 			}
 			if ( entityBinding.isRoot() ) {
 				rootEntityBinding = entityBinding;
 			}
 			else {
 				if ( entityBinding.getEntity().getSuperType() == null ) {
 					throw new IllegalStateException( "Entity binding has no root: " + entityName );
 				}
 				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
 			}
 			rootEntityBindingMap.put( entityName, rootEntityBinding );
 		}
 		return rootEntityBinding;
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
