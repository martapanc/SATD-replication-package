diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
index 74f31de8ce..fbdcc0a8cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
@@ -1,53 +1,67 @@
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
 package org.hibernate.metamodel.relational;
 
+import java.util.ArrayList;
 import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 
 /**
  * Represents a database and manages the named schema/catalog pairs defined within.
  *
  * @author Steve Ebersole
  */
 public class Database {
 	private Map<Schema.Name,Schema> schemaMap = new HashMap<Schema.Name, Schema>();
+	private final List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public Schema getSchema(Schema.Name name) {
 		Schema schema = schemaMap.get( name );
 		if ( schema == null ) {
 			schema = new Schema( name );
 			schemaMap.put( name, schema );
 		}
 		return schema;
 	}
 
 	public Schema getSchema(Identifier schema, Identifier catalog) {
 		return getSchema( new Schema.Name( schema, catalog ) );
 	}
 
 	public Schema getSchema(String schema, String catalog) {
 		return getSchema( new Schema.Name( Identifier.toIdentifier( schema ), Identifier.toIdentifier( catalog ) ) );
 	}
+
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+		if ( auxiliaryDatabaseObject == null ) {
+			throw new IllegalArgumentException( "Auxiliary database object is null." );
+		}
+		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
+	}
+
+	public Iterable<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
+		return auxiliaryDatabaseObjects;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index 6df7a03f28..0e3bcb9931 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,385 +1,385 @@
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
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
  * {@link org.hibernate.metamodel.source.spi.Binder}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor implements HbmBindingContext {
 	private final MetadataImplementor metadata;
 	private final JaxbRoot<XMLHibernateMapping> jaxbRoot;
 
 	private final XMLHibernateMapping hibernateMapping;
 
 	private final MappingDefaults mappingDefaults;
 	private final MetaAttributeContext metaAttributeContext;
 
 	private final boolean autoImport;
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, JaxbRoot<XMLHibernateMapping> jaxbRoot) {
 		this.metadata = metadata;
 		this.jaxbRoot = jaxbRoot;
 
 		this.hibernateMapping = jaxbRoot.getRoot();
 		this.mappingDefaults = new OverriddenMappingDefaults(
 				metadata.getMappingDefaults(),
 				hibernateMapping.getPackage(),
 				hibernateMapping.getSchema(),
 				hibernateMapping.getCatalog(),
 				null,
 				null,
 				hibernateMapping.getDefaultCascade(),
 				hibernateMapping.getDefaultAccess(),
 				hibernateMapping.isDefaultLazy()
 		);
 
 		autoImport = hibernateMapping.isAutoImport();
 
 		metaAttributeContext = extractMetaAttributes();
 	}
 
 	private MetaAttributeContext extractMetaAttributes() {
 		return hibernateMapping.getMeta() == null
 				? new MetaAttributeContext( metadata.getMetaAttributeContext() )
 				: HbmHelper.extractMetaAttributeContext( hibernateMapping.getMeta(), true, metadata.getMetaAttributeContext() );
 	}
 
 	@Override
 	public boolean isAutoImport() {
 		return autoImport;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return jaxbRoot.getOrigin();
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return metadata.getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return metadata.getOptions().getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return metadata.isGloballyQuotedIdentifiers();
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return metadata;
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return metadata.locateClassByName( name );
 	}
 
 	@Override
 	public JavaType makeJavaType(String className) {
 		return metadata.makeJavaType( className );
 	}
 
 	public void bindIndependentMetadata() {
 		bindDatabaseObjectDefinitions();
 		bindTypeDefinitions();
 	}
 
 	private void bindDatabaseObjectDefinitions() {
 		if ( hibernateMapping.getDatabaseObject() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : hibernateMapping.getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classLoaderService().classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							jaxbRoot.getOrigin()
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
 
 	private void bindTypeDefinitions() {
 		if ( hibernateMapping.getTypedef() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLTypedef typedef : hibernateMapping.getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition( new TypeDef( typedef.getName(), typedef.getClazz(), parameters ) );
 		}
 	}
 
 	public void bindTypeDependentMetadata() {
 		bindFilterDefinitions();
 		bindIdentifierGenerators();
 	}
 
 	private void bindFilterDefinitions() {
 		if(hibernateMapping.getFilterDef() == null){
 			return;
 		}
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : hibernateMapping.getFilterDef() ) {
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
 					throw new MappingException( "Unrecognized nested filter content", jaxbRoot.getOrigin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void bindIdentifierGenerators() {
 		if ( hibernateMapping.getIdentifierGenerator() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : hibernateMapping.getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void bindMappingMetadata(List<String> processedEntityNames) {
 		if ( hibernateMapping.getClazzOrSubclassOrJoinedSubclass() == null ) {
 			return;
 		}
 		for ( Object clazzOrSubclass : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( clazzOrSubclass ) ) {
 				XMLHibernateMapping.XMLClass clazz =
 						XMLHibernateMapping.XMLClass.class.cast( clazzOrSubclass );
 				new RootEntityBinder( this, clazz ).process( clazz );
 			}
 			else if ( XMLSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLJoinedSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLUnionSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"unknown type of class or subclass: " +
 								clazzOrSubclass.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	public void bindMappingDependentMetadata() {
 		bindFetchProfiles();
 		bindImports();
 		bindResultSetMappings();
 		bindNamedQueries();
 	}
 
 	private void bindFetchProfiles(){
 		if(hibernateMapping.getFetchProfile() == null){
 			return;
 		}
 		bindFetchProfiles( hibernateMapping.getFetchProfile(),null );
 	}
 
 	public void bindFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void bindImports() {
 		if ( hibernateMapping.getImport() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLImport importValue : hibernateMapping.getImport() ) {
 			String className = getClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void bindResultSetMappings() {
 		if ( hibernateMapping.getResultset() == null ) {
 			return;
 		}
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void bindNamedQueries() {
 		if ( hibernateMapping.getQueryOrSqlQuery() == null ) {
 			return;
 		}
 		for ( Object queryOrSqlQuery : hibernateMapping.getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	private ClassLoaderService classLoaderService;
 
 	private ClassLoaderService classLoaderService() {
 		if ( classLoaderService == null ) {
 			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz) {
 		return HbmHelper.extractEntityName( entityClazz, mappingDefaults.getPackageName() );
 	}
 
 	@Override
 	public String getClassName(String unqualifiedName) {
 		return HbmHelper.getClassName( unqualifiedName, mappingDefaults.getPackageName() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 5584f9f330..eb3d2dbe19 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,543 +1,531 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
-import org.apache.commons.collections.functors.FalsePredicate;
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
-import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.Type;
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
 	private ClassLoaderService classLoaderService;
 
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
 
-	// todo : keep as part of Database?
-	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
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
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
-		if ( auxiliaryDatabaseObject == null ) {
-			throw new IllegalArgumentException( "Auxiliary database object is null." );
-		}
-		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
-	}
-
-	@Override
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
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
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
 			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null || typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
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
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
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
 	public JavaType makeJavaType(String className) {
 		return new JavaType( className, classLoaderService() );
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
 	public MetaAttributeContext getMetaAttributeContext() {
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
 	public Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getExplicitType();
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
 	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
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
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
index 0ab0f2619e..8f1f931ea2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
@@ -1,76 +1,74 @@
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
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
 	public BasicServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
 
 	public void addNamedQuery(NamedQueryDefinition def);
 
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
-
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 }
