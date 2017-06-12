diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 0e7f48d3bc..25921052a5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,913 +1,913 @@
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
 
-import java.io.File;
-import java.io.FileNotFoundException;
-import java.io.InputStream;
-import java.net.URL;
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.HashMap;
-import java.util.List;
-import java.util.Map;
-import java.util.Properties;
-import javax.persistence.AttributeConverter;
-import javax.persistence.SharedCacheMode;
-
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.CompositeCustomType;
 import org.hibernate.type.CustomType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
+import javax.persistence.AttributeConverter;
+import javax.persistence.SharedCacheMode;
+import java.io.File;
+import java.io.FileNotFoundException;
+import java.io.InputStream;
+import java.net.URL;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+import java.util.Properties;
+
 /**
  * Represents one approach for bootstrapping Hibernate.  In fact, historically this was
  * <b>the</b> way to bootstrap Hibernate.
  * <p/>
  * The approach here is to define all configuration and mapping sources in one API
  * and to then build the {@link org.hibernate.SessionFactory} in one-shot.  The configuration
  * and mapping sources defined here are just held here until the SessionFactory is built.  This
  * is an important distinction from the legacy behavior of this class, where we would try to
  * incrementally build the mappings from sources as they were added.  The ramification of this
  * change in behavior is that users can add configuration and mapping sources here, but they can
  * no longer query the in-flight state of mappings ({@link org.hibernate.mapping.PersistentClass},
  * {@link org.hibernate.mapping.Collection}, etc) here.
  * <p/>
  * Note: Internally this class uses the new bootstrapping approach when asked to build the
  * SessionFactory.
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @see org.hibernate.SessionFactory
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration {
     private static final CoreMessageLogger log = CoreLogging.messageLogger( Configuration.class );
 
 	public static final String ARTEFACT_PROCESSING_ORDER = AvailableSettings.ARTIFACT_PROCESSING_ORDER;
 
 	private final BootstrapServiceRegistry bootstrapServiceRegistry;
 	private final MetadataSources metadataSources;
 
 	// used during processing mappings
 	private ImplicitNamingStrategy implicitNamingStrategy;
 	private PhysicalNamingStrategy physicalNamingStrategy;
 	private List<BasicType> basicTypes = new ArrayList<BasicType>();
 	private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
 	private Map<String, NamedQueryDefinition> namedQueries;
 	private Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
 	private Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
 
 	// used to build SF
 	private StandardServiceRegistryBuilder standardServiceRegistryBuilder;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 	private EntityTuplizerFactory entityTuplizerFactory;
 	private Interceptor interceptor;
 	private SessionFactoryObserver sessionFactoryObserver;
 	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 	private Properties properties;
 	private SharedCacheMode sharedCacheMode;
 
 	public Configuration() {
 		this( new BootstrapServiceRegistryBuilder().build() );
 	}
 
 	public Configuration(BootstrapServiceRegistry serviceRegistry) {
 		this.bootstrapServiceRegistry = serviceRegistry;
 		this.metadataSources = new MetadataSources( serviceRegistry );
 		reset();
 	}
 
 	public Configuration(MetadataSources metadataSources) {
 		this.bootstrapServiceRegistry = getBootstrapRegistry( metadataSources.getServiceRegistry() );
 		this.metadataSources = metadataSources;
 		reset();
 	}
 
 	private static BootstrapServiceRegistry getBootstrapRegistry(ServiceRegistry serviceRegistry) {
 		if ( BootstrapServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			return (BootstrapServiceRegistry) serviceRegistry;
 		}
 		else if ( StandardServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			final StandardServiceRegistry ssr = (StandardServiceRegistry) serviceRegistry;
 			return (BootstrapServiceRegistry) ssr.getParentServiceRegistry();
 		}
 
 		throw new HibernateException(
 				"No ServiceRegistry was passed to Configuration#buildSessionFactory " +
 						"and could not determine how to locate BootstrapServiceRegistry " +
 						"from Configuration instantiation"
 		);
 	}
 
 	protected void reset() {
 		implicitNamingStrategy = ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
 		physicalNamingStrategy = PhysicalNamingStrategyStandardImpl.INSTANCE;
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
 		namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>(  );
 
 		standardServiceRegistryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry );
 		entityTuplizerFactory = new EntityTuplizerFactory();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = new Properties(  );
 		properties.putAll( standardServiceRegistryBuilder.getSettings());
 	}
 
 
 	// properties/settings ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
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
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value currently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		Object o = properties.get( propertyName );
 		return o instanceof String ? (String) o : null;
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
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param properties The properties to add.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration addProperties(Properties properties) {
 		this.properties.putAll( properties );
 		return this;
 	}
 
 	public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
 		this.implicitNamingStrategy = implicitNamingStrategy;
 	}
 
 	public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
 		this.physicalNamingStrategy = physicalNamingStrategy;
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
 		return configure( StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		standardServiceRegistryBuilder.configure( resource );
 		// todo : still need to have StandardServiceRegistryBuilder handle the "other cfg.xml" elements.
 		//		currently it just reads the config properties
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
 	}
 
 	/**
 	 * Intended for internal testing use only!!!
 	 */
 	public StandardServiceRegistryBuilder getStandardServiceRegistryBuilder() {
 		return standardServiceRegistryBuilder;
 	}
 
 	//	private void doConfigure(JaxbHibernateConfiguration jaxbHibernateConfiguration) {
 //		standardServiceRegistryBuilder.configure( jaxbHibernateConfiguration );
 //
 //		for ( JaxbMapping jaxbMapping : jaxbHibernateConfiguration.getSessionFactory().getMapping() ) {
 //			if ( StringHelper.isNotEmpty( jaxbMapping.getClazz() ) ) {
 //				addResource( jaxbMapping.getClazz().replace( '.', '/' ) + ".hbm.xml" );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getFile() ) ) {
 //				addFile( jaxbMapping.getFile() );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getJar() ) ) {
 //				addJar( new File( jaxbMapping.getJar() ) );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getPackage() ) ) {
 //				addPackage( jaxbMapping.getPackage() );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getResource() ) ) {
 //				addResource( jaxbMapping.getResource() );
 //			}
 //		}
 //	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		standardServiceRegistryBuilder.configure( url );
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
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
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		standardServiceRegistryBuilder.configure( configFile );
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
 	}
 
 	/**
 	 * @deprecated No longer supported.
 	 */
 	@Deprecated
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		return this;
 	}
 
 
 	// MetadataSources ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Configuration registerTypeContributor(TypeContributor typeContributor) {
 		typeContributorRegistrations.add( typeContributor );
 		return this;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public Configuration registerTypeOverride(BasicType type) {
 		basicTypes.add( type );
 		return this;
 	}
 
 
 	public Configuration registerTypeOverride(UserType type, String[] keys) {
 		basicTypes.add( new CustomType( type, keys ) );
 		return this;
 	}
 
 	public Configuration registerTypeOverride(CompositeUserType type, String[] keys) {
 		basicTypes.add( new CompositeCustomType( type, keys ) );
 		return this;
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
 		metadataSources.addFile( xmlFile );
 		return this;
 	}
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.
 	 */
 	public Configuration addFile(File xmlFile) throws MappingException {
 		metadataSources.addFile( xmlFile );
 		return this;
 	}
 
 	/**
 	 * @deprecated No longer supported.
 	 */
 	@Deprecated
 	public void add(XmlDocument metadataXml) {
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
 		metadataSources.addCacheableFile( xmlFile );
 		return this;
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
 		metadataSources.addCacheableFileStrictly( xmlFile );
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
 		metadataSources.addCacheableFile( xmlFile );
 		return this;
 	}
 
 
 	/**
 	 * @deprecated No longer supported
 	 */
 	@Deprecated
 	public Configuration addXML(String xml) throws MappingException {
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
 		metadataSources.addURL( url );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 *
 	 * @deprecated Use addURL, addResource, addFile, etc. instead
 	 */
 	@Deprecated
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		metadataSources.addDocument( doc );
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
 		metadataSources.addInputStream( xmlInputStream );
 		return this;
 	}
 
 	/**
 	 * @deprecated This form (accepting a ClassLoader) is no longer supported.  Instead, add the ClassLoader
 	 * to the ClassLoaderService on the ServiceRegistry associated with this Configuration
 	 */
 	@Deprecated
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		return addResource( resourceName );
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
 		metadataSources.addResource( resourceName );
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
 		metadataSources.addClass( persistentClass );
 		return this;
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
 		metadataSources.addAnnotatedClass( annotatedClass );
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
 		metadataSources.addPackage( packageName );
 		return this;
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
 		metadataSources.addJar( jar );
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
 		metadataSources.addDirectory( dir );
 		return this;
 	}
 
 
 	// SessionFactory building ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
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
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
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
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * SessionFactory will be immutable, so changes made to this Configuration after building the
 	 * SessionFactory will not affect it.
 	 *
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		log.debug( "Building session factory using provided StandardServiceRegistry" );
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder( (StandardServiceRegistry) serviceRegistry );
 		if ( implicitNamingStrategy != null ) {
 			metadataBuilder.with( implicitNamingStrategy );
 		}
 		if ( physicalNamingStrategy != null ) {
 			metadataBuilder.with( physicalNamingStrategy );
 		}
 		if ( sharedCacheMode != null ) {
 			metadataBuilder.with( sharedCacheMode );
 		}
 		if ( !typeContributorRegistrations.isEmpty() ) {
 			for ( TypeContributor typeContributor : typeContributorRegistrations ) {
 				metadataBuilder.with( typeContributor );
 			}
 		}
 		if ( !basicTypes.isEmpty() ) {
 			for ( BasicType basicType : basicTypes ) {
 				metadataBuilder.with( basicType );
 			}
 		}
 
 		final Metadata metadata = metadataBuilder.build();
 
 		final SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
 		if ( interceptor != null && interceptor != EmptyInterceptor.INSTANCE ) {
 			sessionFactoryBuilder.with( interceptor );
 		}
 		if ( getSessionFactoryObserver() != null ) {
 			sessionFactoryBuilder.add( getSessionFactoryObserver() );
 		}
 		if ( entityNotFoundDelegate != null ) {
 			sessionFactoryBuilder.with( entityNotFoundDelegate );
 		}
 		if ( entityTuplizerFactory != null ) {
 			sessionFactoryBuilder.with( entityTuplizerFactory );
 		}
 
 		return sessionFactoryBuilder.build();
 	}
 
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		log.debug( "Building session factory using internal StandardServiceRegistryBuilder" );
 		standardServiceRegistryBuilder.applySettings( properties );
 		return buildSessionFactory( standardServiceRegistryBuilder.build() );
 	}
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// these just "pass through" to MetadataSources
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		metadataSources.addAuxiliaryDatabaseObject( object );
 	}
 
 	public Map getSqlFunctions() {
 		return metadataSources.getSqlFunctions();
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		metadataSources.addSqlFunction( functionName, function );
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
 		metadataSources.addAttributeConverter( attributeConverterClass, autoApply );
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
 		metadataSources.addAttributeConverter( attributeConverterClass );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter) {
 		metadataSources.addAttributeConverter( attributeConverter );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
 		metadataSources.addAttributeConverter( attributeConverter, autoApply );
 	}
 
 	public void addAttributeConverter(AttributeConverterDefinition definition) {
 		metadataSources.addAttributeConverter( definition );
 	}
 
 	/**
 	 * Sets the SharedCacheMode to use.
 	 *
 	 * Note that at the moment, only {@link javax.persistence.SharedCacheMode#ALL} has
 	 * any effect in terms of {@code hbm.xml} binding.
 	 *
 	 * @param sharedCacheMode The SharedCacheMode to use
 	 */
 	public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
 		this.sharedCacheMode = sharedCacheMode;
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// todo : decide about these
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public java.util.Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
 		return namedEntityGraphMap == null
 				? Collections.<NamedEntityGraphDefinition>emptyList()
 				: namedEntityGraphMap.values();
 	}
 
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
 		return namedProcedureCallMap;
 	}
 
 	/**
 	 * @deprecated Does nothing
 	 */
 	@Deprecated
 	public void buildMappings() {
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
 		return new String[0];
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
 		return new String[0];
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for method chaining
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 14bdb649f7..b93f2f2f42 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1116 +1,1114 @@
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
 package org.hibernate.dialect;
 
-import java.io.InputStream;
-import java.io.OutputStream;
-import java.sql.Blob;
-import java.sql.CallableStatement;
-import java.sql.Clob;
-import java.sql.NClob;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-import java.util.Properties;
-import java.util.Set;
-
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
-import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;
 import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.internal.StandardSequenceExporter;
 import org.hibernate.tool.schema.internal.StandardTableExporter;
 import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
 import org.hibernate.tool.schema.internal.TemporaryTableExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
-
 import org.jboss.logging.Logger;
 
+import java.io.InputStream;
+import java.io.OutputStream;
+import java.sql.Blob;
+import java.sql.CallableStatement;
+import java.sql.Clob;
+import java.sql.NClob;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+import java.util.Properties;
+import java.util.Set;
+
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Dialect.class.getName()
 	);
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 	private final UniqueDelegate uniqueDelegate;
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
 		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
 		// by default, nothing to do
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase(), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add( word );
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return SequenceStyleGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
 	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		if ( getQuerySequencesString() == null ) {
 			return SequenceInformationExtractorNoOpImpl.INSTANCE;
 		}
 		else {
 			return SequenceInformationExtractorLegacyImpl.INSTANCE;
 		}
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
@@ -1661,1148 +1659,1148 @@ public abstract class Dialect implements ConversionContext {
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 *
 	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 * <p/>
 	 * The maximum here should account for the fact that Hibernate often needs to append "uniqueing" information
 	 * to the end of generated aliases.  That "uniqueing" information will be added to the end of a identifier
 	 * generated to the length specified here; so be sure to leave some room (generally speaking 5 positions will
 	 * suffice).
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
 	private TemporaryTableExporter temporaryTableExporter = new TemporaryTableExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
 	public Exporter<Table> getTemporaryTableExporter() {
 		return temporaryTableExporter;
 	}
 
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied before the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied after the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied before the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied after the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase() );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
-	
+
 	/**
 	 * If {@link #supportsTupleDistinctCounts()} is true, does the Dialect require the tuple to be wrapped with parens?
-	 * 
+	 *
 	 * @return boolean
 	 */
 	public boolean requiresParensForTupleDistinctCounts() {
 		return false;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
-	
+
 	/**
 	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
 	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
-	 * 
+	 *
 	 * @param query The query to which to apply the hint.
 	 * @param hints The  hints to apply
 	 * @return The modified SQL
 	 */
 	public String getQueryHintString(String query, List<String> hints) {
 		return query;
 	}
-	
+
 	/**
 	 * Certain dialects support a subset of ScrollModes.  Provide a default to be used by Criteria and Query.
-	 * 
+	 *
 	 * @return ScrollMode
 	 */
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.SCROLL_INSENSITIVE;
 	}
-	
+
 	/**
 	 * Does this dialect support tuples in subqueries?  Ex:
 	 * delete from Table1 where (col1, col2) in (select col1, col2 from Table2)
-	 * 
+	 *
 	 * @return boolean
 	 */
 	public boolean supportsTuplesInSubqueries() {
 		return true;
 	}
 
 	public CallableStatementSupport getCallableStatementSupport() {
 		// most databases do not support returning cursors (ref_cursor)...
 		return StandardCallableStatementSupport.NO_REF_CURSOR_INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
new file mode 100644
index 0000000000..6f7a91dacc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
@@ -0,0 +1,231 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.dialect;
+import java.sql.CallableStatement;
+import java.sql.ResultSet;
+import java.sql.Types;
+
+import org.hibernate.HibernateException;
+import org.hibernate.JDBCException;
+
+import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.function.SQLFunctionTemplate;
+import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.type.StandardBasicTypes;
+import java.sql.SQLException;
+import org.hibernate.LockOptions;
+import java.util.Map;
+import org.hibernate.sql.ForUpdateFragment;
+/**
+ * A dialect for the Teradata database
+ *
+ */
+public class Teradata14Dialect extends TeradataDialect {
+	/**
+	 * Constructor
+	 */
+	public Teradata13Dialect() {
+		super();
+		//registerColumnType data types
+		registerColumnType( Types.BIGINT, "BIGINT" );
+		registerColumnType( Types.BINARY, "VARBYTE(100)" );
+		registerColumnType( Types.LONGVARBINARY, "VARBYTE(32000)" );
+		registerColumnType( Types.LONGVARCHAR, "VARCHAR(32000)" );
+
+		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE,DEFAULT_BATCH_SIZE );
+
+		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIME, "current_time" ) );
+		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.DATE, "current_date" ) );
+	}
+
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getAddColumnString() {
+		return "Add";
+	}
+
+	/**
+	 * Get the name of the database type associated with the given
+	 * <tt>java.sql.Types</tt> typecode.
+	 *
+	 * @param code <tt>java.sql.Types</tt> typecode
+	 * @param length the length or precision of the column
+	 * @param precision the precision of the column
+	 * @param scale the scale of the column
+	 *
+	 * @return the database type name
+	 *
+	 * @throws HibernateException
+	 */
+	 public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
+		/*
+		 * We might want a special case for 19,2. This is very common for money types
+		 * and here it is converted to 18,1
+		 */
+		float f = precision > 0 ? ( float ) scale / ( float ) precision : 0;
+		int p = ( precision > 38 ? 38 : precision );
+		int s = ( precision > 38 ? ( int ) ( 38.0 * f ) : ( scale > 38 ? 38 : scale ) );
+		return super.getTypeName( code, length, p, s );
+	}
+
+	@Override
+	public boolean areStringComparisonsCaseInsensitive() {
+		return false;
+	}
+
+	@Override
+	public String getIdentityColumnString() {
+		return "generated by default as identity not null";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "null";
+	}
+
+	@Override
+	public String getDropTemporaryTableString() {
+		return "drop temporary table";
+	}
+
+	@Override
+	public boolean supportsExpectedLobUsagePattern() {
+		return true;
+	}
+
+	@Override
+	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
+		return EXTRACTER;
+	}
+
+
+	@Override
+	public boolean supportsTupleDistinctCounts() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsExistsInSelect() {
+		return false;
+	}
+
+
+	@Override
+	public boolean supportsUnboundedLobLocatorMaterialization() {
+		return false;
+	}
+
+
+	@Override
+	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
+		statement.registerOutParameter(col, Types.REF);
+		col++;
+		return col;
+	}
+
+	@Override
+	public ResultSet getResultSet(CallableStatement cs) throws SQLException {
+		boolean isResultSet = cs.execute();
+		while (!isResultSet && cs.getUpdateCount() != -1) {
+			isResultSet = cs.getMoreResults();
+		}
+		return cs.getResultSet();
+	}
+
+	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+		/**
+		 * Extract the name of the violated constraint from the given SQLException.
+		 *
+		 * @param sqle The exception that was the result of the constraint violation.
+		 * @return The extracted constraint name.
+		 */
+		@Override
+		public String extractConstraintName(SQLException sqle) {
+			String constraintName = null;
+
+			int errorCode = sqle.getErrorCode();
+			if (errorCode == 27003) {
+				constraintName = extractUsingTemplate("Unique constraint (", ") violated.", sqle.getMessage());
+			} else if (errorCode == 2700) {
+				constraintName = extractUsingTemplate("Referential constraint", "violation:", sqle.getMessage());
+			} else if (errorCode == 5317) {
+				constraintName = extractUsingTemplate("Check constraint (", ") violated.", sqle.getMessage());
+			}
+
+			if (constraintName != null) {
+				int i = constraintName.indexOf('.');
+				if (i != -1) {
+					constraintName = constraintName.substring(i + 1);
+				}
+			}
+			return constraintName;
+		}
+	};
+
+	@Override
+	public String getWriteLockString(int timeout) {
+		String sMsg = " Locking row for write ";
+		if ( timeout == LockOptions.NO_WAIT ) {
+			return sMsg + " nowait ";
+		}
+		return sMsg;
+	}
+
+	@Override
+	public String getReadLockString(int timeout) {
+		String sMsg = " Locking row for read  ";
+		if ( timeout == LockOptions.NO_WAIT ) {
+			return sMsg + " nowait ";
+		}
+		return sMsg;
+	}
+
+	@Override
+	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
+		return new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString() + " " + sql;
+	}
+
+	@Override
+	public boolean useFollowOnLocking() {
+		return true;
+	}
+
+	@Override
+	public boolean isLockAppended() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsLockTimeouts() {
+		return false;
+	}
+}
+
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
index 5c01957402..942057aa64 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
@@ -1,273 +1,269 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for the Teradata database created by MCR as part of the
  * dialect certification process.
  *
  * @author Jay Nance
  */
 public class TeradataDialect extends Dialect {
+	
 	private static final int PARAM_LIST_SIZE_LIMIT = 1024;
 
 	/**
 	 * Constructor
 	 */
 	public TeradataDialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.NUMERIC, "NUMERIC($p,$s)" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.BIGINT, "NUMERIC(18,0)" );
 		registerColumnType( Types.BIT, "BYTEINT" );
 		registerColumnType( Types.TINYINT, "BYTEINT" );
 		registerColumnType( Types.VARBINARY, "VARBYTE($l)" );
 		registerColumnType( Types.BINARY, "BYTEINT" );
 		registerColumnType( Types.LONGVARCHAR, "LONG VARCHAR" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.DECIMAL, "DECIMAL" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
-		// hibernate seems to ignore this type...
-		registerColumnType( Types.BOOLEAN, "BYTEINT" );
+		registerColumnType( Types.BOOLEAN, "BYTEINT" );  // hibernate seems to ignore this type...
 		registerColumnType( Types.BLOB, "BLOB" );
 		registerColumnType( Types.CLOB, "CLOB" );
 
 		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "extract(year from ?1)" ) );
 		registerFunction( "length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "character_length(?1)" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 from ?2 for ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.STRING, "position(?1 in ?2)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.STRING, "?1 mod ?2" ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(255))" ) );
 
 		// bit_length feels a bit broken to me. We have to cast to char in order to
 		// pass when a numeric value is supplied. But of course the answers given will
 		// be wildly different for these two datatypes. 1234.5678 will be 9 bytes as
 		// a char string but will be 8 or 16 bytes as a true numeric.
 		// Jay Nance 2006-09-22
 		registerFunction(
 				"bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(cast(?1 as char))*4" )
 		);
 
 		// The preference here would be
 		//   SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp(?1)", false)
 		// but this appears not to work.
 		// Jay Nance 2006-09-22
 		registerFunction( "current_timestamp", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp" ) );
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_date" ) );
 		// IBID for current_time and current_date
 
 		registerKeyword( "password" );
 		registerKeyword( "type" );
 		registerKeyword( "title" );
 		registerKeyword( "year" );
 		registerKeyword( "month" );
 		registerKeyword( "summary" );
 		registerKeyword( "alias" );
 		registerKeyword( "value" );
 		registerKeyword( "first" );
 		registerKeyword( "role" );
 		registerKeyword( "account" );
 		registerKeyword( "class" );
 
 		// Tell hibernate to use getBytes instead of getBinaryStream
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		// No batch statements
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	/**
-	 * Teradata does not support <tt>FOR UPDATE</tt> syntax
-	 * <p/>
-	 * {@inheritDoc}
+	 * Does this dialect support the <tt>FOR UPDATE</tt> syntax?
+	 *
+	 * @return empty string ... Teradata does not support <tt>FOR UPDATE<tt> syntax
 	 */
-	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
-	@Override
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
-	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
-	@Override
 	public String getAddColumnString() {
 		return "Add Column";
 	}
 
-	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
-	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
-	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return " on commit preserve rows";
 	}
 
-	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return Boolean.TRUE;
 	}
 
-	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
-	@Override
-	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
-		// We might want a special case for 19,2. This is very common for money types
-		// and here it is converted to 18,1
-		final float f = precision > 0 ? (float) scale / (float) precision : 0;
-		final int p = ( precision > 18 ? 18 : precision );
-		final int s = ( precision > 18 ? (int) ( 18.0 * f ) : ( scale > 18 ? 18 : scale ) );
+	/**
+	 * Get the name of the database type associated with the given
+	 * <tt>java.sql.Types</tt> typecode.
+	 *
+	 * @param code <tt>java.sql.Types</tt> typecode
+	 * @param length the length or precision of the column
+	 * @param precision the precision of the column
+	 * @param scale the scale of the column
+	 *
+	 * @return the database type name
+	 *
+	 * @throws HibernateException
+	 */
+	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
+		/*
+		 * We might want a special case for 19,2. This is very common for money types
+		 * and here it is converted to 18,1
+		 */
+		float f = precision > 0 ? ( float ) scale / ( float ) precision : 0;
+		int p = ( precision > 18 ? 18 : precision );
+		int s = ( precision > 18 ? ( int ) ( 18.0 * f ) : ( scale > 18 ? 18 : scale ) );
 
 		return super.getTypeName( code, length, p, s );
 	}
 
-	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
-	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return false;
 	}
 
-	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
-	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
-	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String v = "null";
 
 		switch ( sqlType ) {
 			case Types.BIT:
 			case Types.TINYINT:
 			case Types.SMALLINT:
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.FLOAT:
 			case Types.REAL:
 			case Types.DOUBLE:
 			case Types.NUMERIC:
 			case Types.DECIMAL:
 				v = "cast(null as decimal)";
 				break;
 			case Types.CHAR:
 			case Types.VARCHAR:
 			case Types.LONGVARCHAR:
 				v = "cast(null as varchar(255))";
 				break;
 			case Types.DATE:
 			case Types.TIME:
 			case Types.TIMESTAMP:
 				v = "cast(null as timestamp)";
 				break;
 			case Types.BINARY:
 			case Types.VARBINARY:
 			case Types.LONGVARBINARY:
 			case Types.NULL:
 			case Types.OTHER:
 			case Types.JAVA_OBJECT:
 			case Types.DISTINCT:
 			case Types.STRUCT:
 			case Types.ARRAY:
 			case Types.BLOB:
 			case Types.CLOB:
 			case Types.REF:
 			case Types.DATALINK:
 			case Types.BOOLEAN:
 				break;
-			default:
-				break;
 		}
 		return v;
 	}
 
-	@Override
 	public String getCreateMultisetTableString() {
 		return "create multiset table ";
 	}
 
-	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
-	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
-	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
-	@Override
 	public boolean supportsBindAsCallableArgument() {
 		return false;
 	}
 
+	/* (non-Javadoc)
+		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
+		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
-}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/Select.java b/hibernate-core/src/main/java/org/hibernate/sql/Select.java
index 2b67c9b471..fad2daa509 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/Select.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Select.java
@@ -1,212 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.sql;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 
 /**
  * A simple SQL <tt>SELECT</tt> statement
  * @author Gavin King
  */
 public class Select {
 
 	private String selectClause;
 	private String fromClause;
 	private String outerJoinsAfterFrom;
 	private String whereClause;
 	private String outerJoinsAfterWhere;
 	private String orderByClause;
 	private String groupByClause;
 	private String comment;
 	private LockOptions lockOptions = new LockOptions();
 	public final Dialect dialect;
 
 	private int guesstimatedBufferSize = 20;
 	
 	public Select(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	/**
 	 * Construct an SQL <tt>SELECT</tt> statement from the given clauses
 	 */
 	public String toStatementString() {
 		StringBuilder buf = new StringBuilder(guesstimatedBufferSize);
 		if ( StringHelper.isNotEmpty(comment) ) {
 			buf.append("/* ").append(comment).append(" */ ");
 		}
 		
 		buf.append("select ").append(selectClause)
 				.append(" from ").append(fromClause);
 		
 		if ( StringHelper.isNotEmpty(outerJoinsAfterFrom) ) {
 			buf.append(outerJoinsAfterFrom);
 		}
 		
 		if ( StringHelper.isNotEmpty(whereClause) || StringHelper.isNotEmpty(outerJoinsAfterWhere) ) {
 			buf.append(" where " );
 			// the outerJoinsAfterWhere needs to come before where clause to properly
 			// handle dynamic filters
 			if ( StringHelper.isNotEmpty(outerJoinsAfterWhere) ) {
 				buf.append(outerJoinsAfterWhere);
 				if ( StringHelper.isNotEmpty(whereClause) ) {
 					buf.append( " and " );
 				}
 			}
 			if ( StringHelper.isNotEmpty( whereClause ) ) {
 				buf.append(whereClause);
 			}
 		}
 		
 		if ( StringHelper.isNotEmpty(groupByClause) ) {
 			buf.append(" group by ").append(groupByClause);
 		}
 		
 		if ( StringHelper.isNotEmpty(orderByClause) ) {
 			buf.append(" order by ").append(orderByClause);
 		}
 		
 		if (lockOptions.getLockMode()!=LockMode.NONE) {
-			buf.append( dialect.getForUpdateString(lockOptions) );
+			if (dialect.isLockAppended())
+				buf.append( dialect.getForUpdateString(lockOptions) );
+			else
+				buf.insert(0,dialect.getForUpdateString(lockOptions));
 		}
 		
 		return dialect.transformSelectString( buf.toString() );
 	}
 
 	/**
 	 * Sets the fromClause.
 	 * @param fromClause The fromClause to set
 	 */
 	public Select setFromClause(String fromClause) {
 		this.fromClause = fromClause;
 		this.guesstimatedBufferSize += fromClause.length();
 		return this;
 	}
 
 	public Select setFromClause(String tableName, String alias) {
 		this.fromClause = tableName + ' ' + alias;
 		this.guesstimatedBufferSize += fromClause.length();
 		return this;
 	}
 
 	public Select setOrderByClause(String orderByClause) {
 		this.orderByClause = orderByClause;
 		this.guesstimatedBufferSize += orderByClause.length();
 		return this;
 	}
 
 	public Select setGroupByClause(String groupByClause) {
 		this.groupByClause = groupByClause;
 		this.guesstimatedBufferSize += groupByClause.length();
 		return this;
 	}
 
 	public Select setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
 		this.outerJoinsAfterFrom = outerJoinsAfterFrom;
 
 		// strip off any leading 'and' token
 		String tmpOuterJoinsAfterWhere = outerJoinsAfterWhere.trim();
 		if ( tmpOuterJoinsAfterWhere.startsWith("and") ) {
 			tmpOuterJoinsAfterWhere = tmpOuterJoinsAfterWhere.substring(4);
 		}
 		this.outerJoinsAfterWhere = tmpOuterJoinsAfterWhere;
 
 		this.guesstimatedBufferSize += outerJoinsAfterFrom.length() + outerJoinsAfterWhere.length();
 		return this;
 	}
 
 
 	/**
 	 * Sets the selectClause.
 	 * @param selectClause The selectClause to set
 	 */
 	public Select setSelectClause(String selectClause) {
 		this.selectClause = selectClause;
 		this.guesstimatedBufferSize += selectClause.length();
 		return this;
 	}
 
 	public Select setSelectClause(SelectFragment selectFragment) {
 		setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		return this;
 	}
 
 	/**
 	 * Sets the whereClause.
 	 * @param whereClause The whereClause to set
 	 */
 	public Select setWhereClause(String whereClause) {
 		this.whereClause = whereClause;
 		this.guesstimatedBufferSize += whereClause.length();
 		return this;
 	}
 
 	public Select setComment(String comment) {
 		this.comment = comment;
 		this.guesstimatedBufferSize += comment.length();
 		return this;
 	}
 
 	/**
 	 * Get the current lock mode
 	 * @return LockMode
 	 * @deprecated Instead use getLockOptions
 	 */
 	public LockMode getLockMode() {
 		return lockOptions.getLockMode();
 	}
 
 	/**
 	 * Set the lock mode
 	 * @param lockMode
 	 * @return this object
 	 * @deprecated Instead use setLockOptions
 	 */
 	public Select setLockMode(LockMode lockMode) {
 		lockOptions.setLockMode(lockMode);
 		return this;
 	}
 
 	/**
 	 * Get the current lock options
 	 * @return LockOptions
 	 */
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 	/**
 	 * Set the lock options
 	 * @param lockOptions
 	 * @return this object
 	 */
 	public Select setLockOptions(LockOptions lockOptions) {
 		LockOptions.copy(lockOptions, this.lockOptions);
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java b/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
index d8d49aa73b..62f1b19b24 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
@@ -1,216 +1,219 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.sql;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 
 /**
  * An SQL <tt>SELECT</tt> statement with no table joins
  *
  * @author Gavin King
  */
 public class SimpleSelect {
 
 	public SimpleSelect(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	//private static final Alias DEFAULT_ALIAS = new Alias(10, null);
 
 	private String tableName;
 	private String orderBy;
 	private Dialect dialect;
 	private LockOptions lockOptions = new LockOptions( LockMode.READ);
 	private String comment;
 
 	private List columns = new ArrayList();
 	private Map aliases = new HashMap();
 	private List whereTokens = new ArrayList();
 
 	public SimpleSelect addColumns(String[] columnNames, String[] columnAliases) {
 		for ( int i=0; i<columnNames.length; i++ ) {
 			if ( columnNames[i]!=null  ) {
 				addColumn( columnNames[i], columnAliases[i] );
 			}
 		}
 		return this;
 	}
 
 	public SimpleSelect addColumns(String[] columns, String[] aliases, boolean[] ignore) {
 		for ( int i=0; i<ignore.length; i++ ) {
 			if ( !ignore[i] && columns[i]!=null ) {
 				addColumn( columns[i], aliases[i] );
 			}
 		}
 		return this;
 	}
 
 	public SimpleSelect addColumns(String[] columnNames) {
 		for ( int i=0; i<columnNames.length; i++ ) {
 			if ( columnNames[i]!=null ) addColumn( columnNames[i] );
 		}
 		return this;
 	}
 	public SimpleSelect addColumn(String columnName) {
 		columns.add(columnName);
 		//aliases.put( columnName, DEFAULT_ALIAS.toAliasString(columnName) );
 		return this;
 	}
 
 	public SimpleSelect addColumn(String columnName, String alias) {
 		columns.add(columnName);
 		aliases.put(columnName, alias);
 		return this;
 	}
 
 	public SimpleSelect setTableName(String tableName) {
 		this.tableName = tableName;
 		return this;
 	}
 
 	public SimpleSelect setLockOptions( LockOptions lockOptions ) {
 	   LockOptions.copy(lockOptions, this.lockOptions);
 		return this;
 	}
 
 	public SimpleSelect setLockMode(LockMode lockMode) {
 		this.lockOptions.setLockMode( lockMode );
 		return this;
 	}
 
 	public SimpleSelect addWhereToken(String token) {
 		whereTokens.add(token);
 		return this;
 	}
 	
 	private void and() {
 		if ( whereTokens.size()>0 ) {
 			whereTokens.add("and");
 		}
 	}
 
 	public SimpleSelect addCondition(String lhs, String op, String rhs) {
 		and();
 		whereTokens.add( lhs + ' ' + op + ' ' + rhs );
 		return this;
 	}
 
 	public SimpleSelect addCondition(String lhs, String condition) {
 		and();
 		whereTokens.add( lhs + ' ' + condition );
 		return this;
 	}
 
 	public SimpleSelect addCondition(String[] lhs, String op, String[] rhs) {
 		for ( int i=0; i<lhs.length; i++ ) {
 			addCondition( lhs[i], op, rhs[i] );
 		}
 		return this;
 	}
 
 	public SimpleSelect addCondition(String[] lhs, String condition) {
 		for ( int i=0; i<lhs.length; i++ ) {
 			if ( lhs[i]!=null ) addCondition( lhs[i], condition );
 		}
 		return this;
 	}
 
 	public String toStatementString() {
 		StringBuilder buf = new StringBuilder( 
 				columns.size()*10 + 
 				tableName.length() + 
 				whereTokens.size() * 10 + 
 				10 
 			);
 		
 		if ( comment!=null ) {
 			buf.append("/* ").append(comment).append(" */ ");
 		}
 		
 		buf.append("select ");
 		Set uniqueColumns = new HashSet();
 		Iterator iter = columns.iterator();
 		boolean appendComma = false;
 		while ( iter.hasNext() ) {
 			String col = (String) iter.next();
 			String alias = (String) aliases.get(col);
 			if ( uniqueColumns.add(alias==null ? col : alias) ) {
 				if (appendComma) buf.append(", ");
 				buf.append(col);
 				if ( alias!=null && !alias.equals(col) ) {
 					buf.append(" as ")
 						.append(alias);
 				}
 				appendComma = true;
 			}
 		}
 		
 		buf.append(" from ")
 			.append( dialect.appendLockHint(lockOptions, tableName) );
 		
 		if ( whereTokens.size() > 0 ) {
 			buf.append(" where ")
 				.append( toWhereClause() );
 		}
 		
 		if (orderBy!=null) buf.append(orderBy);
 		
 		if (lockOptions!=null) {
-			buf.append( dialect.getForUpdateString(lockOptions) );
+			if (dialect.isLockAppended())
+				buf.append( dialect.getForUpdateString(lockOptions) );
+			else
+				buf.insert(0,dialect.getForUpdateString(lockOptions));
 		}
 
 		return dialect.transformSelectString( buf.toString() );
 	}
 
 	public String toWhereClause() {
 		StringBuilder buf = new StringBuilder( whereTokens.size() * 5 );
 		Iterator iter = whereTokens.iterator();
 		while ( iter.hasNext() ) {
 			buf.append( iter.next() );
 			if ( iter.hasNext() ) buf.append(' ');
 		}
 		return buf.toString();
 	}
 
 	public SimpleSelect setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 		return this;
 	}
 
 	public SimpleSelect setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/Sky.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/Sky.java
index 4bb6175598..d9f5be26ed 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/Sky.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/Sky.java
@@ -1,29 +1,29 @@
 //$Id$
 package org.hibernate.test.annotations;
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.Transient;
 import javax.persistence.UniqueConstraint;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 @Table(name = "tbl_sky",
-		uniqueConstraints = {@UniqueConstraint(columnNames = {"month", "day"})}
+		uniqueConstraints = {@UniqueConstraint(columnNames = {"`month`", "`day`"})}
 )
 public class Sky implements Serializable {
 	@Id
 	protected Long id;
 	@Column(unique = true, columnDefinition = "varchar(250)", nullable = false)
 	protected String color;
-	@Column(nullable = false)
+	@Column(name="`day`",nullable = false)
 	protected String day;
-	@Column(name = "MONTH", nullable = false)
+	@Column(name = "`MONTH`", nullable = false)
 	protected String month;
 	@Transient
 	protected String area;
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/CharProperty.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/CharProperty.java
index 1029bf46a9..5c0a4452cf 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/CharProperty.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/CharProperty.java
@@ -1,58 +1,58 @@
 package org.hibernate.test.annotations.any;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.Column;
 
 @Entity
 @Table( name = "char_property" )
 public class CharProperty implements Property {
 	private Integer id;
 
 	private String name;
 
-    @Column(name = "`value`")
 	private Character value;
 
 	public CharProperty() {
 		super();
 	}
 
 	public CharProperty(String name, Character value) {
 		super();
 		this.name = name;
 		this.value = value;
 	}
 
 	public String asString() {
 		return Character.toString( value );
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
+	@Column(name = "`value`")
 	public Character getValue() {
 		return value;
 	}
 
 	public void setValue(Character value) {
 		this.value = value;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/IntegerProperty.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/IntegerProperty.java
index a05794743a..e89893c7b2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/IntegerProperty.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/IntegerProperty.java
@@ -1,57 +1,57 @@
 package org.hibernate.test.annotations.any;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.Column;
 
 @Entity
 @Table(name="int_property")
 public class IntegerProperty implements Property {
 	private Integer id;
 	private String name;
-    @Column(name = "`value`")
 	private Integer value;
 	
 	public IntegerProperty() {
 		super();
 	}
 
 	public IntegerProperty(String name, Integer value) {
 		super();
 		this.name = name;
 		this.value = value;
 	}
 
 	public String asString() {
 		return Integer.toString(value);
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
+	@Column(name = "`value`")
 	public Integer getValue() {
 		return value;
 	}
 
 	public void setValue(Integer value) {
 		this.value = value;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/LongProperty.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/LongProperty.java
index 76001abdde..8268d04a98 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/LongProperty.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/LongProperty.java
@@ -1,57 +1,57 @@
 package org.hibernate.test.annotations.any;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.Column;
 
 @Entity
 @Table(name = "long_property")
 public class LongProperty implements Property {
     private Integer id;
 
     private String name;
-    @Column(name = "`value`")
     private Long value;
 
     public LongProperty() {
         super();
     }
 
     public LongProperty(String name, Long value) {
         super();
         this.name = name;
         this.value = value;
     }
 
     public String asString() {
         return Long.toString(value);
     }
 
     public String getName() {
         return name;
     }
 
     @Id
     @GeneratedValue
     public Integer getId() {
         return id;
     }
 
     public void setId(Integer id) {
         this.id = id;
     }
 
+    @Column(name = "`value`")
     public Long getValue() {
         return value;
     }
 
     public void setValue(Long value) {
         this.value = value;
     }
 
     public void setName(String name) {
         this.name = name;
     }
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/StringProperty.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/StringProperty.java
index ca9dd0b43a..283a166cf5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/any/StringProperty.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/any/StringProperty.java
@@ -1,55 +1,55 @@
 package org.hibernate.test.annotations.any;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.Column;
 
 @Entity
 @Table(name="string_property")
 public class StringProperty implements Property {
 	private Integer id;
 	private String name;
-    @Column(name = "`value`")
 	private String value;
 
 	public StringProperty() {
 		super();
 	}
 
 	public StringProperty(String name, String value) {
 		super();
 		this.name = name;
 		this.value = value;
 	}
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String asString() {
 		return value;
 	}
 
+	@Column(name = "`value`")
 	public String getValue() {
 		return value;
 	}
 
 	public void setValue(String value) {
 		this.value = value;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/Bug.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/Bug.java
index 4eb06b5d24..3a8ca6ab85 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/Bug.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/Bug.java
@@ -1,34 +1,36 @@
 package org.hibernate.test.annotations.collectionelement;
+import javax.persistence.Column;
 import javax.persistence.Embeddable;
 
 @Embeddable
 public class Bug {
 
 	private String description;
 	private Person reportedBy;
 	private String summary;
 
+	@Column(name="`summary`")
 	public String getSummary() {
 		return summary;
 	}
 
 	public void setSummary(String summary) {
 		this.summary = summary;
 	}
 
 	public Person getReportedBy() {
 		return reportedBy;
 	}
 
 	public void setReportedBy(Person reportedBy) {
 		this.reportedBy = reportedBy;
 	}
 
 	public String getDescription(){
 		return description;
 	}
 
 	public void setDescription(String description) {
 		this.description = description;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/OrderByTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/OrderByTest.java
index 9ae1d29f6c..8cb940496d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/OrderByTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/OrderByTest.java
@@ -1,137 +1,144 @@
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
 package org.hibernate.test.annotations.collectionelement;
 
 import java.util.HashSet;
 import java.util.Iterator;
 
 import junit.framework.Assert;
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 public class OrderByTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testOrderByName() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		Products p = new Products();
 		HashSet<Widgets> set = new HashSet<Widgets>();
 
 		Widgets widget = new Widgets();
 		widget.setName("hammer");
 		set.add(widget);
 		s.persist(widget);
 
 		widget = new Widgets();
 		widget.setName("axel");
 		set.add(widget);
 		s.persist(widget);
 
 		widget = new Widgets();
 		widget.setName("screwdriver");
 		set.add(widget);
 		s.persist(widget);
 
 		p.setWidgets(set);
 		s.persist(p);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.clear();
 		p = (Products) s.get(Products.class,p.getId());
 		Assert.assertTrue("has three Widgets", p.getWidgets().size() == 3);
 		Iterator iter = p.getWidgets().iterator();
 		Assert.assertEquals( "axel", ((Widgets)iter.next()).getName() );
 		Assert.assertEquals( "hammer", ((Widgets)iter.next()).getName() );
 		Assert.assertEquals( "screwdriver", ((Widgets)iter.next()).getName() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "uses Teradata reserved word - summary"
+	)
 	public void testOrderByWithDottedNotation() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		BugSystem bs = new BugSystem();
 		HashSet<Bug> set = new HashSet<Bug>();
 
 		Bug bug = new Bug();
 		bug.setDescription("JPA-2 locking");
 		bug.setSummary("JPA-2 impl locking");
 		Person p = new Person();
 		p.setFirstName("Scott");
 		p.setLastName("Marlow");
 		bug.setReportedBy(p);
 		set.add(bug);
 
 		bug = new Bug();
 		bug.setDescription("JPA-2 annotations");
 		bug.setSummary("JPA-2 impl annotations");
 		p = new Person();
 		p.setFirstName("Emmanuel");
 		p.setLastName("Bernard");
 		bug.setReportedBy(p);
 		set.add(bug);
 
 		bug = new Bug();
 		bug.setDescription("Implement JPA-2 criteria");
 		bug.setSummary("JPA-2 impl criteria");
 		p = new Person();
 		p.setFirstName("Steve");
 		p.setLastName("Ebersole");
 		bug.setReportedBy(p);
 		set.add(bug);
 
 		bs.setBugs(set);
 		s.persist(bs);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.clear();
 		bs = (BugSystem) s.get(BugSystem.class,bs.getId());
 		Assert.assertTrue("has three bugs", bs.getBugs().size() == 3);
 		Iterator iter = bs.getBugs().iterator();
 		Assert.assertEquals( "Emmanuel", ((Bug)iter.next()).getReportedBy().getFirstName() );
 		Assert.assertEquals( "Steve", ((Bug)iter.next()).getReportedBy().getFirstName() );
 		Assert.assertEquals( "Scott", ((Bug)iter.next()).getReportedBy().getFirstName() );
 		tx.commit();
 		s.close();
 
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 			Products.class,
 			Widgets.class,
 			BugSystem.class
 		};
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/CorpType.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/CorpType.java
index a42ab32ab7..4c4c854a36 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/CorpType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/CorpType.java
@@ -1,34 +1,34 @@
 //$Id$
 package org.hibernate.test.annotations.embedded;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Column;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 public class CorpType {
 	private Integer id;
-    @Column(name = "`type`")
 	private String type;
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
+	@Column(name = "`type`")
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/BasicHibernateAnnotationsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/BasicHibernateAnnotationsTest.java
index f7c7e2dbb2..8fbbafc922 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/BasicHibernateAnnotationsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/BasicHibernateAnnotationsTest.java
@@ -1,738 +1,741 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNotSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.math.BigDecimal;
 import java.util.Currency;
 import java.util.Date;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * @author Emmanuel Bernard
  */
 public class BasicHibernateAnnotationsTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected boolean isCleanupTestDataRequired() {
 		return true;
 	}
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 	public void testEntity() throws Exception {
 		Forest forest = new Forest();
 		forest.setName( "Fontainebleau" );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( forest );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		forest = (Forest) s.get( Forest.class, forest.getId() );
 		assertNotNull( forest );
 		forest.setName( "Fontainebleau" );
 		//should not execute SQL update
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		forest = (Forest) s.get( Forest.class, forest.getId() );
 		assertNotNull( forest );
 		forest.setLength( 23 );
 		//should execute dynamic SQL update
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Forest.class, forest.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
+	@SkipForDialect(value = TeradataDialect.class , comment = "One transaction hangs the other")
 	public void testVersioning() throws Exception {
 		Forest forest = new Forest();
 		forest.setName( "Fontainebleau" );
 		forest.setLength( 33 );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( forest );
 		tx.commit();
 		s.close();
 
 		Session parallelSession = openSession();
 		Transaction parallelTx = parallelSession.beginTransaction();
 		s = openSession();
 		tx = s.beginTransaction();
 
 		forest = (Forest) parallelSession.get( Forest.class, forest.getId() );
 		Forest reloadedForest = (Forest) s.get( Forest.class, forest.getId() );
 		reloadedForest.setLength( 11 );
 		assertNotSame( forest, reloadedForest );
 		tx.commit();
 		s.close();
 
 		forest.setLength( 22 );
 		try {
 			parallelTx.commit();
 			fail( "All optimistic locking should have make it fail" );
 		}
 		catch (HibernateException e) {
 			if ( parallelTx != null ) parallelTx.rollback();
 		}
 		finally {
 			parallelSession.close();
 		}
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Forest.class, forest.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 	public void testPolymorphism() throws Exception {
 		Forest forest = new Forest();
 		forest.setName( "Fontainebleau" );
 		forest.setLength( 33 );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( forest );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query query = s.createQuery( "from java.lang.Object" );
 		assertEquals( 0, query.list().size() );
 		query = s.createQuery( "from Forest" );
 		assertTrue( 0 < query.list().size() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 	public void testType() throws Exception {
 		Forest f = new Forest();
 		f.setName( "Broceliande" );
 		String description = "C'est une enorme foret enchantee ou vivais Merlin et toute la clique";
 		f.setLongDescription( description );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( f );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		f = (Forest) s.get( Forest.class, f.getId() );
 		assertNotNull( f );
 		assertEquals( description, f.getLongDescription() );
 		s.delete( f );
 		tx.commit();
 		s.close();
 
 	}
 
 	/*
 	 * Test import of TypeDefs from MappedSuperclass and 
 	 * Embedded classes.
 	 * The classes 'Name' and 'FormalLastName' both embed the same 
 	 * component 'LastName'. This is to verify that processing the 
 	 * typedef defined in the component TWICE does not create any 
 	 * issues.  
 	 */
 	@Test
 	public void testImportTypeDefinitions() throws Exception {
 		LastName lastName = new LastName();
 		lastName.setName("reddy");
 				
 		Name name = new Name();
 		name.setFirstName("SHARATH");
 		name.setLastName(lastName);
 		
 		FormalLastName formalName = new FormalLastName();
 		formalName.setLastName(lastName);
 		formalName.setDesignation("Mr");
 				
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist(name);
 		s.persist(formalName);
 		tx.commit();
 		s.close();
 		 
 		s = openSession();
 		tx = s.beginTransaction();
 		name = (Name) s.get( Name.class, name.getId() );
 		assertNotNull( name );
 		assertEquals( "sharath", name.getFirstName() );
 		assertEquals( "REDDY", name.getLastName().getName() );
 		
 		formalName = (FormalLastName) s.get(FormalLastName.class, formalName.getId());
 		assertEquals( "REDDY", formalName.getLastName().getName() );
 		
 		s.delete(name);
 		s.delete(formalName);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonLazy() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Forest f = new Forest();
 		Tree t = new Tree();
 		t.setName( "Basic one" );
 		s.persist( f );
 		s.persist( t );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		f = (Forest) s.load( Forest.class, f.getId() );
 		t = (Tree) s.load( Tree.class, t.getId() );
 		assertFalse( "Default should be lazy", Hibernate.isInitialized( f ) );
 		assertTrue( "Tree is not lazy", Hibernate.isInitialized( t ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCache() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		ZipCode zc = new ZipCode();
 		zc.code = "92400";
 		s.persist( zc );
 		tx.commit();
 		s.close();
 		sessionFactory().getStatistics().clear();
 		sessionFactory().getStatistics().setStatisticsEnabled( true );
 		sessionFactory().evict( ZipCode.class );
 		s = openSession();
 		tx = s.beginTransaction();
 		s.get( ZipCode.class, zc.code );
 		assertEquals( 1, sessionFactory().getStatistics().getSecondLevelCachePutCount() );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.get( ZipCode.class, zc.code );
 		assertEquals( 1, sessionFactory().getStatistics().getSecondLevelCacheHitCount() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFilterOnCollection() {
 		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		
 		Topic topic = new Topic();
 		Narrative n1 = new Narrative();
 		n1.setState("published");
 		topic.addNarrative(n1);
 		
 		Narrative n2 = new Narrative();
 		n2.setState("draft");
 		topic.addNarrative(n2);
 		
 		s.persist(topic);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		topic = (Topic) s.load( Topic.class, topic.getId() );
 		
 		s.enableFilter("byState").setParameter("state", "published");
 		topic = (Topic) s.load( Topic.class, topic.getId() );
 		assertNotNull(topic); 
 		assertTrue(topic.getNarratives().size() == 1); 
 		assertEquals("published", topic.getNarratives().iterator().next().getState());
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from " + Narrative.class.getSimpleName() ).executeUpdate();
 		tx.commit();
 		s.close();
 	} 
 
 	@Test
 	public void testCascadedDeleteOfChildEntitiesBug2() {
 		// Relationship is one SoccerTeam to many Players.
 		// Create a SoccerTeam (parent) and three Players (child).
 		// Verify that the count of Players is correct.
 		// Clear the SoccerTeam reference Players.
 		// The orphanRemoval should remove the Players automatically.
 		// @OneToMany(mappedBy="name", orphanRemoval=true)
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		SoccerTeam team = new SoccerTeam();
 		int teamid = team.getId();
 		Player player1 = new Player();
 		player1.setName("Shalrie Joseph");
 		team.addPlayer(player1);
 
 		Player player2 = new Player();
 		player2.setName("Taylor Twellman");
 		team.addPlayer(player2);
 
 		Player player3 = new Player();
 		player3.setName("Steve Ralston");
 		team.addPlayer(player3);
 		s.persist(team);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		team = (SoccerTeam)s.merge(team);
 		int count = ( (Long) s.createQuery( "select count(*) from Player" ).iterate().next() ).intValue();
 		assertEquals("expected count of 3 but got = " + count, count, 3);
 
 		// clear references to players, this should orphan the players which should
 		// in turn trigger orphanRemoval logic.
 		team.getPlayers().clear();
 //		count = ( (Long) s.createQuery( "select count(*) from Player" ).iterate().next() ).intValue();
 //		assertEquals("expected count of 0 but got = " + count, count, 0);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		count = ( (Long) s.createQuery( "select count(*) from Player" ).iterate().next() ).intValue();
 		assertEquals("expected count of 0 but got = " + count, count, 0);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadedDeleteOfChildOneToOne() {
 		// create two single player teams (for one versus one match of soccer)
 		// and associate teams with players via the special OneVOne methods.
 		// Clear the Team reference to players, which should orphan the teams.
 		// Orphaning the team should delete the team. 
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		SoccerTeam team = new SoccerTeam();
 		team.setName("Shalrie's team");
 		Player player1 = new Player();
 		player1.setName("Shalrie Joseph");
 		team.setOneVonePlayer(player1);
 		player1.setOneVoneTeam(team);
 
 		s.persist(team);
 
 		SoccerTeam team2 = new SoccerTeam();
 		team2.setName("Taylor's team");
 		Player player2 = new Player();
 		player2.setName("Taylor Twellman");
 		team2.setOneVonePlayer(player2);
 		player2.setOneVoneTeam(team2);
 		s.persist(team2);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.clear();
 		team2 = (SoccerTeam)s.load(team2.getClass(), team2.getId());
 		team = (SoccerTeam)s.load(team.getClass(), team.getId());
 		int count = ( (Long) s.createQuery( "select count(*) from Player" ).iterate().next() ).intValue();
 		assertEquals("expected count of 2 but got = " + count, count, 2);
 
 		// clear references to players, this should orphan the players which should
 		// in turn trigger orphanRemoval logic.
 		team.setOneVonePlayer(null);
 		team2.setOneVonePlayer(null);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		count = ( (Long) s.createQuery( "select count(*) from Player" ).iterate().next() ).intValue();
 		assertEquals("expected count of 0 but got = " + count, count, 0);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFilter() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete Forest" ).executeUpdate();
 		Forest f1 = new Forest();
 		f1.setLength( 2 );
 		s.persist( f1 );
 		Forest f2 = new Forest();
 		f2.setLength( 20 );
 		s.persist( f2 );
 		Forest f3 = new Forest();
 		f3.setLength( 200 );
 		s.persist( f3 );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		s.enableFilter( "betweenLength" ).setParameter( "minLength", 5 ).setParameter( "maxLength", 50 );
 		long count = ( (Long) s.createQuery( "select count(*) from Forest" ).iterate().next() ).intValue();
 		assertEquals( 1, count );
 		s.disableFilter( "betweenLength" );
 		s.enableFilter( "minLength" ).setParameter( "minLength", 5 );
 		count = ( (Long) s.createQuery( "select count(*) from Forest" ).iterate().next() ).longValue();
 		assertEquals( 2l, count );
 		s.disableFilter( "minLength" );
 		tx.rollback();
 		s.close();
 	}
 	  
 	/**
 	 * Tests the functionality of inheriting @Filter and @FilterDef annotations
 	 * defined on a parent MappedSuperclass(s)
 	 */
 	@Test
 	public void testInheritFiltersFromMappedSuperclass() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete Drill" ).executeUpdate();
 		Drill d1 = new PowerDrill();
 		d1.setName("HomeDrill1");
 		d1.setCategory("HomeImprovment");
 		s.persist( d1 );
 		Drill d2 = new PowerDrill();
 		d2.setName("HomeDrill2");
 		d2.setCategory("HomeImprovement");
 		s.persist(d2);
 		Drill d3 = new PowerDrill();
 		d3.setName("HighPowerDrill");
 		d3.setCategory("Industrial");
 		s.persist( d3 );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		 
 		//We test every filter with 2 queries, the first on the base class of the 
 		//inheritance hierarchy (Drill), and the second on a subclass (PowerDrill)
 		s.enableFilter( "byName" ).setParameter( "name", "HomeDrill1");
 		long count = ( (Long) s.createQuery( "select count(*) from Drill" ).iterate().next() ).intValue();
 		assertEquals( 1, count );
 		count = ( (Long) s.createQuery( "select count(*) from PowerDrill" ).iterate().next() ).intValue();
 		assertEquals( 1, count );
 		s.disableFilter( "byName" );
 		
 		s.enableFilter( "byCategory" ).setParameter( "category", "Industrial" );
 		count = ( (Long) s.createQuery( "select count(*) from Drill" ).iterate().next() ).longValue();
 		assertEquals( 1, count );
 		count = ( (Long) s.createQuery( "select count(*) from PowerDrill" ).iterate().next() ).longValue();
 		assertEquals( 1, count );
 		s.disableFilter( "byCategory" );
 		
 		tx.rollback();
 		s.close();
 	}
 	
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 	public void testParameterizedType() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Forest f = new Forest();
 		f.setSmallText( "ThisIsASmallText" );
 		f.setBigText( "ThisIsABigText" );
 		s.persist( f );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Forest f2 = (Forest) s.get( Forest.class, f.getId() );
 		assertEquals( f.getSmallText().toLowerCase(), f2.getSmallText() );
 		assertEquals( f.getBigText().toUpperCase(), f2.getBigText() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 	public void testSerialized() throws Exception {
 		Forest forest = new Forest();
 		forest.setName( "Shire" );
 		Country country = new Country();
 		country.setName( "Middle Earth" );
 		forest.setCountry( country );
 		Set<Country> near = new HashSet<Country>();
 		country = new Country();
 		country.setName("Mordor");
 		near.add(country);
 		country = new Country();
 		country.setName("Gondor");
 		near.add(country);
 		country = new Country();
 		country.setName("Eriador");
 		near.add(country);
 		forest.setNear(near);
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( forest );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		forest = (Forest) s.get( Forest.class, forest.getId() );
 		assertNotNull( forest );
 		country = forest.getCountry();
 		assertNotNull( country );
 		assertEquals( country.getName(), forest.getCountry().getName() );
 		near = forest.getNear();
 		assertTrue("correct number of nearby countries", near.size() == 3);
 		for (Iterator iter = near.iterator(); iter.hasNext();) {
 			country = (Country)iter.next();
 			String name = country.getName();
 			assertTrue("found expected nearby country " + name,
 				(name.equals("Mordor") || name.equals("Gondor") || name.equals("Eriador")));
 		}
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeType() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Ransom r = new Ransom();
 		r.setKidnapperName( "Se7en" );
 		r.setDate( new Date() );
 		MonetaryAmount amount = new MonetaryAmount(
 				new BigDecimal( 100000 ),
 				Currency.getInstance( "EUR" )
 		);
 		r.setAmount( amount );
 		s.persist( r );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		r = (Ransom) s.get( Ransom.class, r.getId() );
 		assertNotNull( r );
 		assertNotNull( r.getAmount() );
 		assertTrue( 0 == new BigDecimal( 100000 ).compareTo( r.getAmount().getAmount() ) );
 		assertEquals( Currency.getInstance( "EUR" ), r.getAmount().getCurrency() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFormula() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		org.hibernate.test.annotations.entity.Flight airFrance = new Flight();
 		airFrance.setId( new Long( 747 ) );
 		airFrance.setMaxAltitude( 10000 );
 		s.persist( airFrance );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		airFrance = (Flight) s.get( Flight.class, airFrance.getId() );
 		assertNotNull( airFrance );
 		assertEquals( 10000000, airFrance.getMaxAltitudeInMilimeter() );
 		s.delete( airFrance );
 		tx.commit();
 		s.close();
 	}
 		
 	@Test
 	public void testTypeDefNameAndDefaultForTypeAttributes() {
 		ContactDetails contactDetails = new ContactDetails();
 		contactDetails.setLocalPhoneNumber(new PhoneNumber("999999"));
 		contactDetails.setOverseasPhoneNumber(
 				new OverseasPhoneNumber("041", "111111"));
 		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist(contactDetails);
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		contactDetails = 
 			(ContactDetails) s.get( ContactDetails.class, contactDetails.getId() );
 		assertNotNull( contactDetails );
 		assertEquals( "999999", contactDetails.getLocalPhoneNumber().getNumber() );
 		assertEquals( "041111111", contactDetails.getOverseasPhoneNumber().getNumber() );
 		s.delete(contactDetails);
 		tx.commit();
 		s.close();
 	
 	}
 	
 	@Test
 	public void testTypeDefWithoutNameAndDefaultForTypeAttributes() {
 		SessionFactory sf=null;
 		try {
 			Configuration config = new Configuration();
 			config.addAnnotatedClass(LocalContactDetails.class);
 			sf = config.buildSessionFactory( ServiceRegistryBuilder.buildServiceRegistry( config.getProperties() ) );
 			fail("Did not throw expected exception");
 		}
 		catch( AnnotationException ex ) {
 			assertEquals(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass org.hibernate.test.annotations.entity.PhoneNumberType", 
 					ex.getMessage());
 		} finally {
 			if( sf != null){
 				sf.close();
 			}
 		}
 	}
 
 	
 	/**
 	 * A custom type is used in the base class, but defined in the derived class. 
 	 * This would have caused an exception, because the base class is processed 
 	 * BEFORE the derived class, and the custom type is not yet defined. However, 
 	 * it works now because we are setting the typeName for SimpleValue in the second 
 	 * pass. 
 	 * 
 	 * 
 	 * @throws Exception
 	 */
 	@Test
 	public void testSetSimpleValueTypeNameInSecondPass() throws Exception {
 		Peugot derived = new Peugot();
 		derived.setName("sharath");
 		
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist(derived);
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		derived = (Peugot) s.get( Peugot.class, derived.getId() );
 		assertNotNull( derived );
 		assertEquals( "SHARATH", derived.getName() );
 		s.delete(derived);
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[]{
 				Forest.class,
 				Tree.class,
 				Ransom.class,
 				ZipCode.class,
 				Flight.class,
 				Name.class,
 				FormalLastName.class,
 				Car.class,
 				Peugot.class,
 				ContactDetails.class,
 				Topic.class,
 				Narrative.class,
 				Drill.class,
 				PowerDrill.class,
 				SoccerTeam.class,
 				Player.class
 		};
 	}
 
 	@Override
 	protected String[] getAnnotatedPackages() {
 		return new String[]{
 				"org.hibernate.test.annotations.entity"
 		};
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/CourseStudent.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/CourseStudent.java
index 4d34f8d9df..896f14576c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/CourseStudent.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/CourseStudent.java
@@ -1,57 +1,59 @@
 package org.hibernate.test.annotations.idmanytoone;
 
 import java.io.Serializable;
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.IdClass;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 
 /**
  * @author Alex Kalashnikov
  */
 @Entity
 @Table(name = "idmanytoone_course_student")
 public class CourseStudent implements Serializable {
 
     @Id
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "course_id")
     private Course course;
 
     @Id
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "student_id")
     private Student student;
 
+	@Column(name = "`value`")
     private String value;
 
     public CourseStudent() {
     }
 
     public Course getCourse() {
         return course;
     }
 
     public void setCourse(Course course) {
         this.course = course;
     }
 
     public Student getStudent() {
         return student;
     }
 
     public void setStudent(Student student) {
         this.student = student;
     }
 
     public String getValue() {
         return value;
     }
 
     public void setValue(String value) {
         this.value = value;
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/StoreCustomer.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/StoreCustomer.java
index b1e486c46c..4771eef898 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/StoreCustomer.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/idmanytoone/StoreCustomer.java
@@ -1,37 +1,37 @@
 //$Id$
 package org.hibernate.test.annotations.idmanytoone;
 import java.io.Serializable;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.IdClass;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
-@Table(name = "ABs")
+@Table(name = "`ABs`")
 @IdClass( StoreCustomerPK.class)
 public class StoreCustomer implements Serializable {
 	StoreCustomer() {}
 	@Id
     @ManyToOne(optional = false)
     @JoinColumn(name = "idA")
     public Store store;
 
     @Id
 	@ManyToOne(optional = false)
     @JoinColumn(name = "idB")
     public Customer customer;
 
 
     public StoreCustomer(Store store, Customer customer) {
 	this.store = store;
 	this.customer = customer;
     }
 
 
     private static final long serialVersionUID = -8295955012787627232L;
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/indexcoll/IndexedCollectionTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/indexcoll/IndexedCollectionTest.java
index b86d4f9ae7..626e9a4e85 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/indexcoll/IndexedCollectionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/indexcoll/IndexedCollectionTest.java
@@ -1,665 +1,676 @@
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
 package org.hibernate.test.annotations.indexcoll;
 
-import java.util.ArrayList;
-import java.util.Date;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
-
 import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
+import java.util.ArrayList;
+import java.util.Date;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test index collections
  *
  * @author Emmanuel Bernard
  */
 public class IndexedCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testJPA2DefaultMapColumns() throws Exception {
 		isDefaultKeyColumnPresent( Atmosphere.class.getName(), "gasesDef", "_KEY" );
 		isDefaultKeyColumnPresent( Atmosphere.class.getName(), "gasesPerKeyDef", "_KEY" );
 		isDefaultKeyColumnPresent( Atmosphere.class.getName(), "gasesDefLeg", "_KEY" );
 	}
 
 	@Test
 	public void testJPA2DefaultIndexColumns() throws Exception {
 		isDefaultKeyColumnPresent( Drawer.class.getName(), "dresses", "_ORDER" );
 	}
 
 	private void isDefaultKeyColumnPresent(String collectionOwner, String propertyName, String suffix) {
 		assertTrue( "Could not find " + propertyName + suffix,
 				isDefaultColumnPresent(collectionOwner, propertyName, suffix) );
 	}
 
 	private boolean isDefaultColumnPresent(String collectionOwner, String propertyName, String suffix) {
 		final Collection collection = metadata().getCollectionBinding( collectionOwner + "." + propertyName );
 		final Iterator columnIterator = collection.getCollectionTable().getColumnIterator();
 		boolean hasDefault = false;
 		while ( columnIterator.hasNext() ) {
 			Column column = (Column) columnIterator.next();
 			if ( (propertyName + suffix).equals( column.getName() ) ) hasDefault = true;
 		}
 		return hasDefault;
 	}
 
 	private void isNotDefaultKeyColumnPresent(String collectionOwner, String propertyName, String suffix) {
 		assertFalse( "Could not find " + propertyName + suffix,
 				isDefaultColumnPresent(collectionOwner, propertyName, suffix) );
 	}
 
 	@Test
 	public void testFkList() throws Exception {
 		Wardrobe w = new Wardrobe();
 		Drawer d1 = new Drawer();
 		Drawer d2 = new Drawer();
 		w.setDrawers( new ArrayList<Drawer>() );
 		w.getDrawers().add( d1 );
 		w.getDrawers().add( d2 );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( w );
 
 		s.flush();
 		s.clear();
 
 		w = (Wardrobe) s.get( Wardrobe.class, w.getId() );
 		assertNotNull( w );
 		assertNotNull( w.getDrawers() );
 		List<Drawer> result = w.getDrawers();
 		assertEquals( 2, result.size() );
 		assertEquals( d2.getId(), result.get( 1 ).getId() );
 		result.remove( d1 );
 		s.flush();
 		d1 = (Drawer) s.merge( d1 );
 		result.add( d1 );
 
 		s.flush();
 		s.clear();
 
 		w = (Wardrobe) s.get( Wardrobe.class, w.getId() );
 		assertNotNull( w );
 		assertNotNull( w.getDrawers() );
 		result = w.getDrawers();
 		assertEquals( 2, result.size() );
 		assertEquals( d1.getId(), result.get( 1 ).getId() );
 		s.delete( result.get( 0 ) );
 		s.delete( result.get( 1 ) );
 		s.delete( w );
 		s.flush();
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testJoinedTableList() throws Exception {
 		Wardrobe w = new Wardrobe();
 		w.setDrawers( new ArrayList<Drawer>() );
 		Drawer d = new Drawer();
 		w.getDrawers().add( d );
 		Dress d1 = new Dress();
 		Dress d2 = new Dress();
 		d.setDresses( new ArrayList<Dress>() );
 		d.getDresses().add( d1 );
 		d.getDresses().add( d2 );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( d1 );
 		s.persist( d2 );
 		s.persist( w );
 
 		s.flush();
 		s.clear();
 
 		d = (Drawer) s.get( Drawer.class, d.getId() );
 		assertNotNull( d );
 		assertNotNull( d.getDresses() );
 		List<Dress> result = d.getDresses();
 		assertEquals( 2, result.size() );
 		assertEquals( d2.getId(), result.get( 1 ).getId() );
 		result.remove( d1 );
 		s.flush();
 		d1 = (Dress) s.merge( d1 );
 		result.add( d1 );
 
 		s.flush();
 		s.clear();
 
 		d = (Drawer) s.get( Drawer.class, d.getId() );
 		assertNotNull( d );
 		assertNotNull( d.getDresses() );
 		result = d.getDresses();
 		assertEquals( 2, result.size() );
 		assertEquals( d1.getId(), result.get( 1 ).getId() );
 		s.delete( result.get( 0 ) );
 		s.delete( result.get( 1 ) );
 		s.delete( d );
 		s.flush();
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMapKey() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Software hibernate = new Software();
 		hibernate.setName( "Hibernate" );
 		Version v1 = new Version();
 		v1.setCodeName( "HumbaHumba" );
 		v1.setNumber( "1.0" );
 		v1.setSoftware( hibernate );
 		Version v2 = new Version();
 		v2.setCodeName( "Copacabana" );
 		v2.setNumber( "2.0" );
 		v2.setSoftware( hibernate );
 		Version v4 = new Version();
 		v4.setCodeName( "Dreamland" );
 		v4.setNumber( "4.0" );
 		v4.setSoftware( hibernate );
 		Map<String, Version> link = new HashMap<String, Version>();
 		link.put( v1.getCodeName(), v1 );
 		link.put( v2.getCodeName(), v2 );
 		link.put( v4.getCodeName(), v4 );
 		hibernate.setVersions( link );
 		s.persist( hibernate );
 		s.persist( v1 );
 		s.persist( v2 );
 		s.persist( v4 );
 
 		s.flush();
 		s.clear();
 
 		hibernate = (Software) s.get( Software.class, "Hibernate" );
 		assertEquals( 3, hibernate.getVersions().size() );
 		assertEquals( "1.0", hibernate.getVersions().get( "HumbaHumba" ).getNumber() );
 		assertEquals( "2.0", hibernate.getVersions().get( "Copacabana" ).getNumber() );
 		hibernate.getVersions().remove( v4.getCodeName() );
 
 		s.flush();
 		s.clear();
 
 		hibernate = (Software) s.get( Software.class, "Hibernate" );
 		assertEquals( "So effect on collection changes", 3, hibernate.getVersions().size() );
 		for ( Version v : hibernate.getVersions().values() ) {
 			s.delete( v );
 		}
 		s.delete( hibernate );
 
 		s.flush();
 
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testDefaultMapKey() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		AddressBook book = new AddressBook();
 		book.setOwner( "Emmanuel" );
 		AddressEntryPk helene = new AddressEntryPk( "Helene", "Michau" );
 		AddressEntry heleneEntry = new AddressEntry();
 		heleneEntry.setBook( book );
 		heleneEntry.setCity( "Levallois" );
 		heleneEntry.setStreet( "Louis Blanc" );
 		heleneEntry.setPerson( helene );
 		AddressEntryPk primeMinister = new AddressEntryPk( "Dominique", "Villepin" );
 		AddressEntry primeMinisterEntry = new AddressEntry();
 		primeMinisterEntry.setBook( book );
 		primeMinisterEntry.setCity( "Paris" );
 		primeMinisterEntry.setStreet( "Hotel Matignon" );
 		primeMinisterEntry.setPerson( primeMinister );
 		book.getEntries().put( helene, heleneEntry );
 		book.getEntries().put( primeMinister, primeMinisterEntry );
 		s.persist( book );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 2, book.getEntries().size() );
 		assertEquals( heleneEntry.getCity(), book.getEntries().get( helene ).getCity() );
 		AddressEntryPk fake = new AddressEntryPk( "Fake", "Fake" );
 		book.getEntries().put( fake, primeMinisterEntry );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 2, book.getEntries().size() );
 		assertNull( book.getEntries().get( fake ) );
 		s.delete( book );
 
 		s.flush();
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyToEntity() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		AlphabeticalDirectory m = new AlphabeticalDirectory();
 		m.setName( "M" );
 		AlphabeticalDirectory v = new AlphabeticalDirectory();
 		v.setName( "V" );
 		s.persist( m );
 		s.persist( v );
 
 		AddressBook book = new AddressBook();
 		book.setOwner( "Emmanuel" );
 		AddressEntryPk helene = new AddressEntryPk( "Helene", "Michau" );
 		AddressEntry heleneEntry = new AddressEntry();
 		heleneEntry.setBook( book );
 		heleneEntry.setCity( "Levallois" );
 		heleneEntry.setStreet( "Louis Blanc" );
 		heleneEntry.setPerson( helene );
 		heleneEntry.setDirectory( m );
 		AddressEntryPk primeMinister = new AddressEntryPk( "Dominique", "Villepin" );
 		AddressEntry primeMinisterEntry = new AddressEntry();
 		primeMinisterEntry.setBook( book );
 		primeMinisterEntry.setCity( "Paris" );
 		primeMinisterEntry.setStreet( "Hotel Matignon" );
 		primeMinisterEntry.setPerson( primeMinister );
 		primeMinisterEntry.setDirectory( v );
 		book.getEntries().put( helene, heleneEntry );
 		book.getEntries().put( primeMinister, primeMinisterEntry );
 		s.persist( book );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 2, book.getEntries().size() );
 		assertEquals( heleneEntry.getCity(), book.getEntries().get( helene ).getCity() );
 		assertEquals( "M", book.getEntries().get( helene ).getDirectory().getName() );
 
 		s.delete( book );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialect({HSQLDialect.class, H2Dialect.class})
 	public void testComponentSubPropertyMapKey() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		AddressBook book = new AddressBook();
 		book.setOwner( "Emmanuel" );
 		AddressEntryPk helene = new AddressEntryPk( "Helene", "Michau" );
 		AddressEntry heleneEntry = new AddressEntry();
 		heleneEntry.setBook( book );
 		heleneEntry.setCity( "Levallois" );
 		heleneEntry.setStreet( "Louis Blanc" );
 		heleneEntry.setPerson( helene );
 		AddressEntryPk primeMinister = new AddressEntryPk( "Dominique", "Villepin" );
 		AddressEntry primeMinisterEntry = new AddressEntry();
 		primeMinisterEntry.setBook( book );
 		primeMinisterEntry.setCity( "Paris" );
 		primeMinisterEntry.setStreet( "Hotel Matignon" );
 		primeMinisterEntry.setPerson( primeMinister );
 		book.getEntries().put( helene, heleneEntry );
 		book.getEntries().put( primeMinister, primeMinisterEntry );
 		s.persist( book );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 2, book.getLastNameEntries().size() );
 		assertEquals( heleneEntry.getCity(), book.getLastNameEntries().get( "Michau" ).getCity() );
 		AddressEntryPk fake = new AddressEntryPk( "Fake", "Fake" );
 		book.getEntries().put( fake, primeMinisterEntry );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 2, book.getEntries().size() );
 		assertNull( book.getEntries().get( fake ) );
 		s.delete( book );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "uses Teradata reserved word - title"
+	)
 	public void testMapKeyOnManyToMany() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		News airplane = new News();
 		airplane.setTitle( "Crash!" );
 		airplane.setDetail( "An airplaned crashed." );
 		s.persist( airplane );
 		Newspaper lemonde = new Newspaper();
 		lemonde.setName( "Lemonde" );
 		lemonde.getNews().put( airplane.getTitle(), airplane );
 		s.persist( lemonde );
 
 		s.flush();
 		s.clear();
 
 		lemonde = (Newspaper) s.get( Newspaper.class, lemonde.getId() );
 		assertEquals( 1, lemonde.getNews().size() );
 		News news = lemonde.getNews().get( airplane.getTitle() );
 		assertNotNull( news );
 		assertEquals( airplane.getTitle(), news.getTitle() );
 		s.delete( lemonde );
 		s.delete( news );
 
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "uses Teradata reserved word - title"
+	)
 	public void testMapKeyOnManyToManyOnId() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		News hibernate1 = new News();
 		hibernate1.setTitle( "#1 ORM solution in the Java world" );
 		hibernate1.setDetail( "Well, that's no news ;-)" );
 		s.persist( hibernate1 );
 		PressReleaseAgency schwartz = new PressReleaseAgency();
 		schwartz.setName( "Schwartz" );
 		schwartz.getProvidedNews().put( hibernate1.getId(), hibernate1 );
 		s.persist( schwartz );
 
 		s.flush();
 		s.clear();
 
 		schwartz = (PressReleaseAgency) s.get( PressReleaseAgency.class, schwartz.getId() );
 		assertEquals( 1, schwartz.getProvidedNews().size() );
 		News news = schwartz.getProvidedNews().get( hibernate1.getId() );
 		assertNotNull( news );
 		assertEquals( hibernate1.getTitle(), news.getTitle() );
 		s.delete( schwartz );
 		s.delete( news );
 
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyAndIdClass() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Painter picasso = new Painter();
 		Painting laVie = new Painting( "La Vie", "Picasso", 50, 20 );
 		picasso.getPaintings().put( "La Vie", laVie );
 		Painting famille = new Painting( "La Famille du Saltimbanque", "Picasso", 50, 20 );
 		picasso.getPaintings().put( "La Famille du Saltimbanque", famille );
 		s.persist( picasso );
 
 		s.flush();
 		s.clear();
 
 		picasso = (Painter) s.get( Painter.class, picasso.getId() );
 		Painting painting = picasso.getPaintings().get( famille.getName() );
 		assertNotNull( painting );
 		assertEquals( painting.getName(), famille.getName() );
 		s.delete( picasso );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testRealMap() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Atmosphere atm = new Atmosphere();
 		Atmosphere atm2 = new Atmosphere();
 		GasKey key = new GasKey();
 		key.setName( "O2" );
 		Gas o2 = new Gas();
 		o2.name = "oxygen";
 		atm.gases.put( "100%", o2 );
 		atm.gasesPerKey.put( key, o2 );
 		atm2.gases.put( "100%", o2 );
 		atm2.gasesPerKey.put( key, o2 );
 		s.persist( key );
 		s.persist( atm );
 		s.persist( atm2 );
 
 		s.flush();
 		s.clear();
 
 		atm = (Atmosphere) s.get( Atmosphere.class, atm.id );
 		key = (GasKey) s.get( GasKey.class, key.getName() );
 		assertEquals( 1, atm.gases.size() );
 		assertEquals( o2.name, atm.gases.get( "100%" ).name );
 		assertEquals( o2.name, atm.gasesPerKey.get( key ).name );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testTemporalKeyMap() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Atmosphere atm = new Atmosphere();
 		atm.colorPerDate.put( new Date(1234567000), "red" );
 		s.persist( atm );
 		
 		s.flush();
 		s.clear();
 
 		atm = (Atmosphere) s.get( Atmosphere.class, atm.id );
 		assertEquals( 1, atm.colorPerDate.size() );
 		final Date date = atm.colorPerDate.keySet().iterator().next();
 		final long diff = new Date( 1234567000 ).getTime() - date.getTime();
 		assertTrue( "24h diff max", diff > 0 && diff < 24*60*60*1000 );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testEnumKeyType() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Atmosphere atm = new Atmosphere();
 		atm.colorPerLevel.put( Atmosphere.Level.HIGH, "red" );
 		s.persist( atm );
 
 		s.flush();
 		s.clear();
 
 		atm = (Atmosphere) s.get( Atmosphere.class, atm.id );
 		assertEquals( 1, atm.colorPerLevel.size() );
 		assertEquals( "red", atm.colorPerLevel.get(Atmosphere.Level.HIGH) );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyEntityEntity() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		AddressBook book = new AddressBook();
 		s.persist( book );
 		AddressEntry entry = new AddressEntry();
 		entry.setCity( "Atlanta");
 		AddressEntryPk pk = new AddressEntryPk("Coca", "Cola" );
 		entry.setPerson( pk );
 		entry.setBook( book );
 		AlphabeticalDirectory ad = new AlphabeticalDirectory();
 		ad.setName( "C");
 		s.persist( ad );
 		entry.setDirectory( ad );
 		s.persist( entry );
 		book.getDirectoryEntries().put( ad, entry );
 
 		s.flush();
 		s.clear();
 
 		book = (AddressBook) s.get( AddressBook.class, book.getId() );
 		assertEquals( 1, book.getDirectoryEntries().size() );
 		assertEquals( "C", book.getDirectoryEntries().keySet().iterator().next().getName() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testEntityKeyElementTarget() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Atmosphere atm = new Atmosphere();
 		Gas o2 = new Gas();
 		o2.name = "oxygen";
 		atm.composition.put( o2, 94.3 );
 		s.persist( o2 );
 		s.persist( atm );
 
 		s.flush();
 		s.clear();
 
 		atm = (Atmosphere) s.get( Atmosphere.class, atm.id );
 		assertTrue( ! Hibernate.isInitialized( atm.composition ) );
 		assertEquals( 1, atm.composition.size() );
 		assertEquals( o2.name, atm.composition.keySet().iterator().next().name );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testSortedMap() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Training training = new Training();
 		Trainee trainee = new Trainee();
 		trainee.setName( "Jim" );
 		Trainee trainee2 = new Trainee();
 		trainee2.setName( "Emmanuel" );
 		s.persist( trainee );
 		s.persist( trainee2 );
 		training.getTrainees().put( "Jim", trainee );
 		training.getTrainees().put( "Emmanuel", trainee2 );
 		s.persist( training );
 
 		s.flush();
 		s.clear();
 
 		training = (Training) s.get( Training.class, training.getId() );
 		assertEquals( "Emmanuel", training.getTrainees().firstKey() );
 		assertEquals( "Jim", training.getTrainees().lastKey() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyLoad() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Software hibernate = new Software();
 		hibernate.setName( "Hibernate" );
 		Version v1 = new Version();
 		v1.setCodeName( "HumbaHumba" );
 		v1.setNumber( "1.0" );
 		v1.setSoftware( hibernate );
 		hibernate.addVersion( v1 );
 		s.persist( hibernate );
 		s.persist( v1 );
 
 		s.flush();
 		s.clear();
 
 		hibernate = (Software) s.get( Software.class, "Hibernate" );
 		assertEquals(1, hibernate.getVersions().size() );
 		Version v2 = new Version();
 		v2.setCodeName( "HumbaHumba2" );
 		v2.setNumber( "2.0" );
 		v2.setSoftware( hibernate );
 		hibernate.addVersion( v2 );
 		assertEquals( "One loaded persisted version, and one just added", 2, hibernate.getVersions().size() );
 
 		s.flush();
 		s.clear();
 
 		hibernate = (Software) s.get( Software.class, "Hibernate" );
 		for ( Version v : hibernate.getVersions().values() ) {
 			s.delete( v );
 		}
 		s.delete( hibernate );
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Wardrobe.class,
 				Drawer.class,
 				Dress.class,
 				Software.class,
 				Version.class,
 				AddressBook.class,
 				AddressEntry.class,
 				AddressEntryPk.class, //should be silently ignored
 				Newspaper.class,
 				News.class,
 				PressReleaseAgency.class,
 				Painter.class,
 				Painting.class,
 				Atmosphere.class,
 				Gas.class,
 				AlphabeticalDirectory.class,
 				GasKey.class,
 				Trainee.class,
 				Training.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/joined/Account.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/joined/Account.java
index 45d4db5258..8d200360bf 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/joined/Account.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/joined/Account.java
@@ -1,105 +1,105 @@
 /*
   * Hibernate, Relational Persistence for Idiomatic Java
   *
   * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-
   * party contributors as indicated by the @author tags or express 
   * copyright attribution statements applied by the authors.  
   * All third-party contributions are distributed under license by 
   * Red Hat, Inc.
   *
   * This copyrighted material is made available to anyone wishing to 
   * use, modify, copy, or redistribute it subject to the terms and 
   * conditions of the GNU Lesser General Public License, as published 
   * by the Free Software Foundation.
   *
   * This program is distributed in the hope that it will be useful,
   * but WITHOUT ANY WARRANTY; without even the implied warranty of 
   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
   * Lesser General Public License for more details.
   *
   * You should have received a copy of the GNU Lesser General Public 
   * License along with this distribution; if not, write to:
   * 
   * Free Software Foundation, Inc.
   * 51 Franklin Street, Fifth Floor
   * Boston, MA  02110-1301  USA
   */
 
 package org.hibernate.test.annotations.inheritance.joined;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Set;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 
 @Entity
-@Table(name = "ACCOUNT")
+@Table(name = "`ACCOUNT`")
 public class Account implements Serializable {
 	private static final long serialVersionUID = 1L;
 
 	@Id
 	@GeneratedValue
 	private int id;
 
 	@Column(name="fld_number")
 	private String number;
 	
 	@OneToMany(mappedBy="account")
 	private Set<Client> clients;
 	
 	private double balance;
 	
 	public Account() {
 	}
 
 	public int getId() {
 		return this.id;
 	}
 
 	@SuppressWarnings("unused")
 	private void setId(int id) {
 		this.id = id;
 	}
 
 	public String getNumber() {
 		return this.number;
 	}
 
 	public void setNumber(String number) {
 		this.number = number;
 	}
 
 	public double getBalance() {
 		return balance;
 	}
 
 	public void setBalance(double balance) {
 		this.balance = balance;
 	}
 
 	public void addClient(Client c) {
 		if (clients == null) {
 			clients = new HashSet<Client>();
 		}
 		clients.add(c);
 		c.setAccount(this);
 	}
 	
 	
 	public Set<Client> getClients() {
 		return clients;
 	}
 
 	public void setClients(Set<Client> clients) {
 		this.clients = clients;
 	}
 
 	
 	
 	
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytoonewithformula/ManyToOneWithFormulaTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytoonewithformula/ManyToOneWithFormulaTest.java
index d18378fe55..34a37b8a25 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytoonewithformula/ManyToOneWithFormulaTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytoonewithformula/ManyToOneWithFormulaTest.java
@@ -1,251 +1,254 @@
 /*
   * Hibernate, Relational Persistence for Idiomatic Java
   *
   * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-
   * party contributors as indicated by the @author tags or express
   * copyright attribution statements applied by the authors.
   * All third-party contributions are distributed under license by
   * Red Hat, Inc.
   *
   * This copyrighted material is made available to anyone wishing to
   * use, modify, copy, or redistribute it subject to the terms and
   * conditions of the GNU Lesser General Public License, as published
   * by the Free Software Foundation.
   *
   * This program is distributed in the hope that it will be useful,
   * but WITHOUT ANY WARRANTY; without even the implied warranty of
   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   * Lesser General Public License for more details.
   *
   * You should have received a copy of the GNU Lesser General Public
   * License along with this distribution; if not, write to:
   *
   * Free Software Foundation, Inc.
   * 51 Franklin Street, Fifth Floor
   * Boston, MA  02110-1301  USA
   */
 package org.hibernate.test.annotations.manytoonewithformula;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.SQLServer2005Dialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.SkipForDialects;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * @author Sharath Reddy
  */
 public class ManyToOneWithFormulaTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testManyToOneFromNonPk() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Menu menu = new Menu();
 		menu.setOrderNbr( "123" );
 		menu.setDefault( "F" );
 		s.persist( menu );
 		FoodItem foodItem = new FoodItem();
 		foodItem.setItem( "Mouse" );
 		foodItem.setOrder( menu );
 		s.persist( foodItem );
 		s.flush();
 		s.clear();
 		foodItem = ( FoodItem ) s.get( FoodItem.class, foodItem.getId() );
 		assertNotNull( foodItem.getOrder() );
 		assertEquals( "123", foodItem.getOrder().getOrderNbr() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneFromPk() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		Company company = new Company();
 		s.persist( company );
 
 		Person person = new Person();
 		person.setDefaultFlag( "T" );
 		person.setCompanyId( company.getId() );
 		s.persist( person );
 
 		s.flush();
 		s.clear();
 
 		company = ( Company ) s.get( Company.class, company.getId() );
 		assertNotNull( company.getDefaultContactPerson() );
 		assertEquals( person.getId(), company.getDefaultContactPerson().getId() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(value = { HSQLDialect.class }, comment = "The used join conditions does not work in HSQLDB. See HHH-4497")
 	public void testManyToOneToPkWithOnlyFormula() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		Language language = new Language();
 		language.setCode( "EN" );
 		language.setName( "English" );
 		s.persist( language );
 
 		Message msg = new Message();
 		msg.setLanguageCode( "en" );
 		msg.setLanguageName( "English" );
 		s.persist( msg );
 
 		s.flush();
 		s.clear();
 
 		msg = ( Message ) s.get( Message.class, msg.getId() );
 		assertNotNull( msg.getLanguage() );
 		assertEquals( "EN", msg.getLanguage().getCode() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testReferencedColumnNameBelongsToEmbeddedIdOfReferencedEntity() throws Exception {
 		Session session = openSession();
 		Transaction tx = session.beginTransaction();
 
 		Integer companyCode = 10;
 		Integer mfgCode = 100;
 		String contractNumber = "NSAR97841";
 		ContractId contractId = new ContractId(companyCode, 12457l, 1);
 
 		Manufacturer manufacturer = new Manufacturer(new ManufacturerId(
 				companyCode, mfgCode), "FORD");
 
 		Model model = new Model(new ModelId(companyCode, mfgCode, "FOCUS"),
 				"FORD FOCUS");
 
 		session.persist(manufacturer);
 		session.persist(model);
 
 		Contract contract = new Contract();
 		contract.setId(contractId);
 		contract.setContractNumber(contractNumber);
 		contract.setManufacturer(manufacturer);
 		contract.setModel(model);
 
 		session.persist(contract);
 
 		session.flush();
 		session.clear();
 
 		contract = (Contract) session.load(Contract.class, contractId);
 		assertEquals("NSAR97841", contract.getContractNumber());
 		assertEquals("FORD", contract.getManufacturer().getName());
 		assertEquals("FORD FOCUS", contract.getModel().getName());
 
 		tx.commit();
 		session.close();
 	}
 
 	@Test
 	@SkipForDialects( {
 			@SkipForDialect( value = { HSQLDialect.class }, comment = "The used join conditions does not work in HSQLDB. See HHH-4497." ), 
 			@SkipForDialect( value = { SQLServer2005Dialect.class } ),
 			@SkipForDialect( value = { Oracle8iDialect.class }, comment = "Oracle/DB2 do not support 'substring' function" ),
-			@SkipForDialect( value = { DB2Dialect.class }, comment = "Oracle/DB2 do not support 'substring' function" ) } )
+			@SkipForDialect( value = { DB2Dialect.class }, comment = "Oracle/DB2 do not support 'substring' function" ),
+			@SkipForDialect( value = {TeradataDialect.class }, comment = "Teradata doesn't support substring(?,?,?). \"substr\" would work." ),
+	} )
 	public void testManyToOneFromNonPkToNonPk() throws Exception {
 		// also tests usage of the stand-alone @JoinFormula annotation (i.e. not wrapped within @JoinColumnsOrFormulas)
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
         Product kit = new Product();
         kit.id = 1;
         kit.productIdnf = "KIT";
         kit.description = "Kit";
         s.persist(kit);
 
         Product kitkat = new Product();
         kitkat.id = 2;
         kitkat.productIdnf = "KIT_KAT";
         kitkat.description = "Chocolate";
         s.persist(kitkat);
 
         s.flush();
         s.clear();
 
         kit = (Product) s.get(Product.class, 1);
         kitkat = (Product) s.get(Product.class, 2);
         System.out.println(kitkat.description);
         assertNotNull(kitkat);
         assertEquals(kit, kitkat.getProductFamily());
         assertEquals(kit.productIdnf, kitkat.getProductFamily().productIdnf);
         assertEquals("KIT_KAT", kitkat.productIdnf.trim());
         assertEquals("Chocolate", kitkat.description.trim());
 
         tx.rollback();
 		s.close();
     }
 
     @Test
     @RequiresDialect(value = {SQLServer2005Dialect.class})
     public void testManyToOneFromNonPkToNonPkSqlServer() throws Exception {
         // also tests usage of the stand-alone @JoinFormula annotation (i.e. not wrapped within @JoinColumnsOrFormulas)
         Session s = openSession();
         Transaction tx = s.beginTransaction();
 
         ProductSqlServer kit = new ProductSqlServer();
         kit.id = 1;
         kit.productIdnf = "KIT";
         kit.description = "Kit";
         s.persist(kit);
 
         ProductSqlServer kitkat = new ProductSqlServer();
         kitkat.id = 2;
         kitkat.productIdnf = "KIT_KAT";
         kitkat.description = "Chocolate";
         s.persist(kitkat);
 
         s.flush();
         s.clear();
 
         kit = (ProductSqlServer) s.get(ProductSqlServer.class, 1);
         kitkat = (ProductSqlServer) s.get(ProductSqlServer.class, 2);
         System.out.println(kitkat.description);
         assertNotNull(kitkat);
         assertEquals(kit, kitkat.getProductFamily());
         assertEquals(kit.productIdnf, kitkat.getProductFamily().productIdnf);
         assertEquals("KIT_KAT", kitkat.productIdnf.trim());
         assertEquals("Chocolate", kitkat.description.trim());
 
         tx.rollback();
         s.close();
     }
 
 	@Override
 	protected java.lang.Class<?>[] getAnnotatedClasses() {
 		return new java.lang.Class[] {
 				Menu.class,
 				FoodItem.class,
 				Company.class,
 				Person.class,
 				Message.class,
 				Language.class,
 				Contract.class,
 				ContractId.class,
 				Model.class,
 				ModelId.class,
 				Manufacturer.class,
 				ManufacturerId.class,
 				Product.class,
                 ProductSqlServer.class
 		};
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/mappedsuperclass/intermediate/Account.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/mappedsuperclass/intermediate/Account.java
index 821ed37e2c..194d0fb8af 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/mappedsuperclass/intermediate/Account.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/mappedsuperclass/intermediate/Account.java
@@ -1,45 +1,45 @@
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
 package org.hibernate.test.annotations.mappedsuperclass.intermediate;
 import javax.persistence.Entity;
 import javax.persistence.Inheritance;
 import javax.persistence.InheritanceType;
 import javax.persistence.Table;
 
 /**
  * The intermediate entity in the hierarchy
  *
  * @author Saša Obradović
  */
 @Entity
-@Table(name = "ACCOUNT")
+@Table(name = "`ACCOUNT`")
 @Inheritance(strategy = InheritanceType.JOINED)
 public class Account extends AccountBase {
 	public Account() {
 	}
 
 	public Account(String accountNumber) {
 		super( accountNumber );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Area.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Area.java
index 679bd66763..77072e4c4b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Area.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Area.java
@@ -1,72 +1,72 @@
 //$Id$
 package org.hibernate.test.annotations.query;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.EntityResult;
 import javax.persistence.FieldResult;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 import javax.persistence.Table;
 
 /**
  * Example of a entity load incl a join fetching of an associated *ToOne entity
  *
  * @author Emmanuel Bernard
  */
 @Entity
 @NamedNativeQueries({
 @NamedNativeQuery(
 		name = "night&area", query = "select night.id as nid, night.night_duration, night.night_date, area.id as aid, "
 		+ "night.area_id, area.name from Night night, tbl_area area where night.area_id = area.id",
 		resultSetMapping = "joinMapping")
 		})
 @org.hibernate.annotations.NamedNativeQueries({
 @org.hibernate.annotations.NamedNativeQuery(
 		name = "night&areaCached",
 		query = "select night.id as nid, night.night_duration, night.night_date, area.id as aid, "
 				+ "night.area_id, area.name from Night night, tbl_area area where night.area_id = area.id",
 		resultSetMapping = "joinMapping")
 		})
 @SqlResultSetMappings(
 		@SqlResultSetMapping(name = "joinMapping", entities = {
 		@EntityResult(entityClass = org.hibernate.test.annotations.query.Night.class, fields = {
 		@FieldResult(name = "id", column = "nid"),
 		@FieldResult(name = "duration", column = "night_duration"),
 		@FieldResult(name = "date", column = "night_date"),
 		@FieldResult(name = "area", column = "area_id")
 				}),
 		@EntityResult(entityClass = org.hibernate.test.annotations.query.Area.class, fields = {
 		@FieldResult(name = "id", column = "aid"),
 		@FieldResult(name = "name", column = "name")
 				})
 				}
 		)
 )
 @Table(name = "tbl_area")
 public class Area {
 	private Integer id;
 	private String name;
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
-	@Column(unique = true)
+	@Column(unique = true, nullable=false)
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Dictionary.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Dictionary.java
index fe8a596e59..6542a5d1dc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Dictionary.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/Dictionary.java
@@ -1,65 +1,65 @@
 //$Id$
 package org.hibernate.test.annotations.query;
 import javax.persistence.DiscriminatorColumn;
 import javax.persistence.DiscriminatorValue;
 import javax.persistence.Entity;
 import javax.persistence.EntityResult;
 import javax.persistence.FieldResult;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.SqlResultSetMapping;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 @DiscriminatorColumn(name = "disc")
 @DiscriminatorValue("Dic")
 @SqlResultSetMapping(
 		name = "dictionary", entities = {
 @EntityResult(
 		entityClass = org.hibernate.test.annotations.query.Dictionary.class,
 		fields = {
 		@FieldResult(name = "id", column = "id"),
 		@FieldResult(name = "name", column = "name"),
 		@FieldResult(name = "editor", column = "editor")
 				},
 		discriminatorColumn = "`type`"
 )
 		}
 )
 @NamedNativeQuery(name = "all.dictionaries",
-		query = "select id, name, editor, disc as type from Dictionary",
+		query = "select id, name, editor, disc as \"type\" from Dictionary",
 		resultSetMapping = "dictionary")
 public class Dictionary {
 	private Integer id;
 	private String name;
 	private String editor;
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public String getEditor() {
 		return editor;
 	}
 
 	public void setEditor(String editor) {
 		this.editor = editor;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/QueryAndSQLTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/QueryAndSQLTest.java
index 9a4846683f..9d64452304 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/query/QueryAndSQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/query/QueryAndSQLTest.java
@@ -1,499 +1,500 @@
 //$Id$
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.test.annotations.query;
 
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.List;
 
 import org.hibernate.MappingException;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.stat.Statistics;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.annotations.A320;
 import org.hibernate.test.annotations.A320b;
 import org.hibernate.test.annotations.Plane;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Test named queries
  *
  * @author Emmanuel Bernard
  */
 public class QueryAndSQLTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected boolean isCleanupTestDataRequired() {
 		return true;
 	}
 	@Test
 	public void testNativeQueryWithFormulaAttribute() {
 		SQLFunction dateFunction = getDialect().getFunctions().get( "current_date" );
 		String dateFunctionRendered = dateFunction.render(
 				null,
 				java.util.Collections.EMPTY_LIST,
 				sessionFactory()
 		);
 
 		String sql = String.format(
 				"select t.table_name as {t.tableName}, %s as {t.daysOld} from ALL_TABLES t  where t.table_name = 'AUDIT_ACTIONS' ",
 				dateFunctionRendered
 		);
 		String sql2 = String.format(
 				"select table_name as t_name, %s as t_time from ALL_TABLES   where table_name = 'AUDIT_ACTIONS' ",
 				dateFunctionRendered
 		);
 
 
 		Session s = openSession();
 		s.beginTransaction();
 		s.createSQLQuery( sql ).addEntity( "t", AllTables.class ).list();
 		s.createSQLQuery( sql2 ).setResultSetMapping( "all" ).list();
 		SQLQuery q = s.createSQLQuery( sql2 );
 		q.addRoot( "t", AllTables.class ).addProperty( "tableName", "t_name" ).addProperty( "daysOld", "t_time" );
 		q.list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected(jiraKey = "HHH-2225")
 	public void testNativeQueryWithFormulaAttributeWithoutAlias() {
 		String sql = "select table_name , sysdate() from all_tables  where table_name = 'AUDIT_ACTIONS' ";
 		Session s = openSession();
 		s.beginTransaction();
 		s.createSQLQuery( sql ).addEntity( "t", AllTables.class ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPackageQueries() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Plane p = new Plane();
 		s.persist( p );
 		Query q = s.getNamedQuery( "plane.getAll" );
 		assertEquals( 1, q.list().size() );
 		s.delete( q.list().get( 0 ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassQueries() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Night n = new Night();
 		Calendar c = new GregorianCalendar();
 		c.set( 2000, 2, 2 );
 		Date now = c.getTime();
 		c.add( Calendar.MONTH, -1 );
 		Date aMonthAgo = c.getTime();
 		c.add( Calendar.MONTH, 2 );
 		Date inAMonth = c.getTime();
 		n.setDate( now );
 		n.setDuration( 14 );
 		s.persist( n );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.getNamedQuery( "night.moreRecentThan" );
 		q.setDate( "date", aMonthAgo );
 		assertEquals( 1, q.list().size() );
 		q = s.getNamedQuery( "night.moreRecentThan" );
 		q.setDate( "date", inAMonth );
 		assertEquals( 0, q.list().size() );
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		q = s.getNamedQuery( "night.duration" );
 		q.setParameter( "duration", 14l );
 		assertEquals( 1, q.list().size() );
 		assertEquals( 1, stats.getQueryCachePutCount() );
 		q = s.getNamedQuery( "night.duration" );
 		q.setParameter( "duration", 14l );
 		s.delete( q.list().get( 0 ) );
 		assertEquals( 1, stats.getQueryCacheHitCount() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSQLQuery() {
 		Night n = new Night();
 		Calendar c = new GregorianCalendar();
 		c.set( 2000, 2, 2 );
 		Date now = c.getTime();
 		c.add( Calendar.MONTH, -1 );
 		Date aMonthAgo = c.getTime();
 		c.add( Calendar.MONTH, 2 );
 		Date inAMonth = c.getTime();
 		n.setDate( now );
 		n.setDuration( 9999 );
 		Area area = new Area();
 		area.setName( "Monceau" );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( n );
 		s.persist( area );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		Query q = s.getNamedQuery( "night.getAll.bySQL" );
 		q.setParameter( 0, 9990 );
 		List result = q.list();
 		assertEquals( 1, result.size() );
 		Night n2 = (Night) result.get( 0 );
 		assertEquals( n2.getDuration(), n.getDuration() );
 		List areas = s.getNamedQuery( "getAreaByNative" ).list();
 		assertTrue( 1 == areas.size() );
 		assertEquals( area.getName(), ( (Area) areas.get( 0 ) ).getName() );
 		s.delete( areas.get( 0 ) );
 		s.delete( n2 );
 		tx.commit();
 		s.close();
 	}
 
 
 	/**
 	 * We are testing 2 things here:
 	 * 1. The query 'night.olderThan' is defined in a MappedSuperClass - Darkness.
 	 * We are verifying that queries defined in a MappedSuperClass are processed.
 	 * 2. There are 2 Entity classes that extend from Darkness - Night and Twilight.
 	 * We are verifying that this does not cause any issues.eg. Double processing of the
 	 * MappedSuperClass
 	 */
 	@Test
 	public void testImportQueryFromMappedSuperclass() {
 		Session s = openSession();
 		try {
 			s.getNamedQuery( "night.olderThan" );
 		}
 		catch ( MappingException ex ) {
 			fail( "Query imported from MappedSuperclass" );
 		}
 		s.close();
 	}
 
 	@Test
 	public void testSQLQueryWithManyToOne() {
 		cleanupCache();
 		Night n = new Night();
 		Calendar c = new GregorianCalendar();
 		c.set( 2000, 2, 2 );
 		Date now = c.getTime();
 		c.add( Calendar.MONTH, -1 );
 		Date aMonthAgo = c.getTime();
 		c.add( Calendar.MONTH, 2 );
 		Date inAMonth = c.getTime();
 		n.setDate( now );
 		n.setDuration( 9999 );
 		Area a = new Area();
 		a.setName( "Paris" );
 		n.setArea( a );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( a );
 		s.persist( n );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
+		stats.clear();
 		Query q = s.getNamedQuery( "night&areaCached" );
 		q.setCacheable( true );
 		List result = q.list();
 		assertEquals( 1, result.size() );
 		assertEquals( 1, stats.getQueryCachePutCount() );
 		q.setCacheable( true );
 		q.list();
 		assertEquals( 1, stats.getQueryCacheHitCount() );
 		Night n2 = (Night) ( (Object[]) result.get( 0 ) )[0];
 		assertEquals( n2.getDuration(), n.getDuration() );
 		s.delete( n2.getArea() );
 		s.delete( n2 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testImplicitNativeQuery() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		SpaceShip ship = new SpaceShip();
 		ship.setModel( "X-Wing" );
 		ship.setName( "YuBlue" );
 		ship.setSpeed( 2000 );
 		ship.setDimensions( new Dimensions() );
 		s.persist( ship );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		Query q = s.getNamedQuery( "implicitSample" );
 		List result = q.list();
 		assertEquals( 1, result.size() );
 		assertEquals( ship.getModel(), ( (SpaceShip) result.get( 0 ) ).getModel() );
 		s.delete( result.get( 0 ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNativeQueryAndCompositePKAndComponents() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		SpaceShip ship = new SpaceShip();
 		ship.setModel( "X-Wing" );
 		ship.setName( "YuBlue" );
 		ship.setSpeed( 2000 );
 		ship.setDimensions( new Dimensions() );
 		ship.getDimensions().setLength( 10 );
 		ship.getDimensions().setWidth( 5 );
 		Captain captain = new Captain();
 		captain.setFirstname( "Luke" );
 		captain.setLastname( "Skywalker" );
 		ship.setCaptain( captain );
 		s.persist( captain );
 		s.persist( ship );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		Query q = s.getNamedQuery( "compositekey" );
 		List result = q.list();
 		assertEquals( 1, result.size() );
 		Object[] row = (Object[]) result.get( 0 );
 		SpaceShip spaceShip = (SpaceShip) row[0];
 		assertEquals( ship.getModel(), spaceShip.getModel() );
 		assertNotNull( spaceShip.getDimensions() );
 		assertEquals( ship.getDimensions().getWidth(), spaceShip.getDimensions().getWidth() );
 		assertEquals( ship.getDimensions().getLength(), spaceShip.getDimensions().getLength() );
 		assertEquals( ship.getCaptain().getFirstname(), ship.getCaptain().getFirstname() );
 		assertEquals( ship.getCaptain().getLastname(), ship.getCaptain().getLastname() );
 		//FIXME vary depending on databases
 		assertTrue( row[1].toString().startsWith( "50" ) );
 		assertTrue( row[2].toString().startsWith( "500" ) );
 		s.delete( spaceShip.getCaptain() );
 		s.delete( spaceShip );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDiscriminator() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Dictionary dic = new Dictionary();
 		dic.setName( "Anglais-Francais" );
 		dic.setEditor( "Harrap's" );
 		SynonymousDictionary syn = new SynonymousDictionary();
 		syn.setName( "Synonymes de tous les temps" );
 		syn.setEditor( "Imagination edition" );
 		s.persist( dic );
 		s.persist( syn );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		List results = s.getNamedQuery( "all.dictionaries" ).list();
 		assertEquals( 2, results.size() );
 		assertTrue(
 				results.get( 0 ) instanceof SynonymousDictionary
 						|| results.get( 1 ) instanceof SynonymousDictionary
 		);
 		s.delete( results.get( 0 ) );
 		s.delete( results.get( 1 ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(value = { PostgreSQL81Dialect.class, PostgreSQLDialect.class },
 			comment = "postgresql jdbc driver does not implement the setQueryTimeout method")
 	public void testCache() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Plane plane = new Plane();
 		plane.setNbrOfSeats( 5 );
 		s.persist( plane );
 		tx.commit();
 		s.close();
 		sessionFactory().getStatistics().clear();
 		sessionFactory().getStatistics().setStatisticsEnabled( true );
 		s = openSession();
 		tx = s.beginTransaction();
 		Query query = s.getNamedQuery( "plane.byId" ).setParameter( "id", plane.getId() );
 		plane = (Plane) query.uniqueResult();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCachePutCount() );
 		plane = (Plane) s.getNamedQuery( "plane.byId" ).setParameter( "id", plane.getId() ).uniqueResult();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Plane.class, plane.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEntitySQLOverriding() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Chaos chaos = new Chaos();
 		chaos.setSize( 123l );
 		chaos.setId( 1l );
 
 		String lowerName = "hello";
 		String upperName = lowerName.toUpperCase();
 		assertFalse( lowerName.equals( upperName ) );
 
 		chaos.setName( "hello" );
 		chaos.setNickname( "NickName" );
 		s.persist( chaos );
 		s.flush();
 		s.clear();
 		s.getSessionFactory().evict( Chaos.class );
 
 		Chaos resultChaos = (Chaos) s.load( Chaos.class, chaos.getId() );
 		assertEquals( upperName, resultChaos.getName() );
 		assertEquals( "nickname", resultChaos.getNickname() );
 
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionSQLOverriding() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Chaos chaos = new Chaos();
 		chaos.setSize( 123l );
 		chaos.setId( 1l );
 
 		chaos.setName( "hello" );
 		s.persist( chaos );
 		CasimirParticle p = new CasimirParticle();
 		p.setId( 1l );
 		s.persist( p );
 		chaos.getParticles().add( p );
 		p = new CasimirParticle();
 		p.setId( 2l );
 		s.persist( p );
 		chaos.getParticles().add( p );
 		s.flush();
 		s.clear();
 		s.getSessionFactory().evict( Chaos.class );
 
 		Chaos resultChaos = (Chaos) s.load( Chaos.class, chaos.getId() );
 		assertEquals( 2, resultChaos.getParticles().size() );
 		resultChaos.getParticles().remove( resultChaos.getParticles().iterator().next() );
 		resultChaos.getParticles().remove( resultChaos.getParticles().iterator().next() );
 		s.flush();
 
 		s.clear();
 		resultChaos = (Chaos) s.load( Chaos.class, chaos.getId() );
 		assertEquals( 0, resultChaos.getParticles().size() );
 
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Darkness.class,
 				Plane.class,
 				A320.class,
 				A320b.class,
 				Night.class,
 				Twilight.class,
 				Area.class,
 				SpaceShip.class,
 				Dictionary.class,
 				SynonymousDictionary.class,
 				Captain.class,
 				Chaos.class,
 				CasimirParticle.class,
 				AllTables.class,
 				Attrset.class,
 				Attrvalue.class,
 				Employee.class,
 				Employeegroup.class
 		};
 	}
 
 	@Override
 	protected String[] getAnnotatedPackages() {
 		return new String[] {
 				"org.hibernate.test.annotations.query"
 		};
 	}
 
 	@Override
 	protected String[] getXmlFiles() {
 		return new String[] {
 				"org/hibernate/test/annotations/query/orm.xml"
 		};
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 		cfg.setImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Clothes.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Clothes.java
index c3d31ca4c2..128fa08896 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Clothes.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Clothes.java
@@ -1,68 +1,70 @@
 //$Id$
 package org.hibernate.test.annotations.referencedcolumnname;
 import javax.persistence.Entity;
+import javax.persistence.Column;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 public class Clothes {
 	private Integer id;
+	@Column(name = "type")
 	private String type;
 	private String flavor;
 
 	public Clothes() {
 	}
 
 	public Clothes(String type, String flavor) {
 		this.type = type;
 		this.flavor = flavor;
 	}
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 
 	public String getFlavor() {
 		return flavor;
 	}
 
 	public void setFlavor(String flavor) {
 		this.flavor = flavor;
 	}
 
 	public boolean equals(Object o) {
 		if ( this == o ) return true;
 		if ( !( o instanceof Clothes ) ) return false;
 
 		final Clothes clothes = (Clothes) o;
 
 		if ( !flavor.equals( clothes.flavor ) ) return false;
 		if ( !type.equals( clothes.type ) ) return false;
 
 		return true;
 	}
 
 	public int hashCode() {
 		int result;
 		result = type.hashCode();
 		result = 29 * result + flavor.hashCode();
 		return result;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Luggage.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Luggage.java
index 2876959098..d4980f08b7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Luggage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Luggage.java
@@ -1,89 +1,91 @@
 //$Id$
 package org.hibernate.test.annotations.referencedcolumnname;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Set;
 import javax.persistence.CascadeType;
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.OneToMany;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 public class Luggage implements Serializable {
 	private Integer id;
 	private String owner;
+	@Column(name = "`type`")
 	private String type;
 	private Set<Clothes> hasInside = new HashSet<Clothes>();
 
 	public Luggage() {
 	}
 
 	public Luggage(String owner, String type) {
 		this.owner = owner;
 		this.type = type;
 	}
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getOwner() {
 		return owner;
 	}
 
 	public void setOwner(String owner) {
 		this.owner = owner;
 	}
 
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 
 	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
 	@JoinColumns({
 	@JoinColumn(name = "lug_type", referencedColumnName = "type"),
 	@JoinColumn(name = "lug_owner", referencedColumnName = "owner")
 			})
 	public Set<Clothes> getHasInside() {
 		return hasInside;
 	}
 
 	public void setHasInside(Set<Clothes> hasInside) {
 		this.hasInside = hasInside;
 	}
 
 	public boolean equals(Object o) {
 		if ( this == o ) return true;
 		if ( !( o instanceof Luggage ) ) return false;
 
 		final Luggage luggage = (Luggage) o;
 
 		if ( !owner.equals( luggage.owner ) ) return false;
 		if ( !type.equals( luggage.type ) ) return false;
 
 		return true;
 	}
 
 	public int hashCode() {
 		int result;
 		result = owner.hashCode();
 		result = 29 * result + type.hashCode();
 		return result;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
index df6d0d6b5a..22cdd0b9f1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
@@ -1,298 +1,304 @@
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
 package org.hibernate.test.annotations.referencedcolumnname;
 
-import java.math.BigDecimal;
-import java.util.Iterator;
-
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
-
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
+import java.math.BigDecimal;
+import java.util.Iterator;
+
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Emmanuel Bernard
  */
 public class ReferencedColumnNameTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testManyToOne() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Postman pm = new Postman( "Bob", "A01" );
 		House house = new House();
 		house.setPostman( pm );
 		house.setAddress( "Rue des pres" );
 		s.persist( pm );
 		s.persist( house );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		house = (House) s.get( House.class, house.getId() );
 		assertNotNull( house.getPostman() );
 		assertEquals( "Bob", house.getPostman().getName() );
 		pm = house.getPostman();
 		s.delete( house );
 		s.delete( pm );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOneToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		Rambler rambler = new Rambler( "Emmanuel" );
 		Bag bag = new Bag( "0001", rambler );
 		rambler.getBags().add( bag );
 		s.persist( rambler );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 
 		bag = (Bag) s.createQuery( "select b from Bag b left join fetch b.owner" ).uniqueResult();
 		assertNotNull( bag );
 		assertNotNull( bag.getOwner() );
 
 		rambler = (Rambler) s.createQuery( "select r from Rambler r left join fetch r.bags" ).uniqueResult();
 		assertNotNull( rambler );
 		assertNotNull( rambler.getBags() );
 		assertEquals( 1, rambler.getBags().size() );
 		s.delete( rambler.getBags().iterator().next() );
 		s.delete( rambler );
 
 		tx.commit();
 		s.close();
 	}
 
 	@Test
+		@SkipForDialect(
+						value = TeradataDialect.class,
+						jiraKey = "HHH-8190",
+						comment = "uses Teradata reserved word - type"
+				)
 	public void testUnidirectionalOneToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		Clothes clothes = new Clothes( "underwear", "interesting" );
 		Luggage luggage = new Luggage( "Emmanuel", "Cabin Luggage" );
 		luggage.getHasInside().add( clothes );
 		s.persist( luggage );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 
 		luggage = (Luggage) s.createQuery( "select l from Luggage l left join fetch l.hasInside" ).uniqueResult();
 		assertNotNull( luggage );
 		assertNotNull( luggage.getHasInside() );
 		assertEquals( 1, luggage.getHasInside().size() );
 
 		s.delete( luggage.getHasInside().iterator().next() );
 		s.delete( luggage );
 
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		House whiteHouse = new House();
 		whiteHouse.setAddress( "1600 Pennsylvania Avenue, Washington" );
 		Inhabitant bill = new Inhabitant();
 		bill.setName( "Bill Clinton" );
 		Inhabitant george = new Inhabitant();
 		george.setName( "George W Bush" );
 		s.persist( george );
 		s.persist( bill );
 		whiteHouse.getHasInhabitants().add( bill );
 		whiteHouse.getHasInhabitants().add( george );
 		//bill.getLivesIn().add( whiteHouse );
 		//george.getLivesIn().add( whiteHouse );
 
 		s.persist( whiteHouse );
 		tx.commit();
 		s = openSession();
 		tx = s.beginTransaction();
 
 		whiteHouse = (House) s.get( House.class, whiteHouse.getId() );
 		assertNotNull( whiteHouse );
 		assertEquals( 2, whiteHouse.getHasInhabitants().size() );
 
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		bill = (Inhabitant) s.get( Inhabitant.class, bill.getId() );
 		assertNotNull( bill );
 		assertEquals( 1, bill.getLivesIn().size() );
 		assertEquals( whiteHouse.getAddress(), bill.getLivesIn().iterator().next().getAddress() );
 
 		whiteHouse = bill.getLivesIn().iterator().next();
 		s.delete( whiteHouse );
 		Iterator it = whiteHouse.getHasInhabitants().iterator();
 		while ( it.hasNext() ) {
 			s.delete( it.next() );
 		}
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneReferenceManyToOne() throws Exception {
 		Item item = new Item();
 		item.setId( 1 );
 		Vendor vendor = new Vendor();
 		vendor.setId( 1 );
 		ItemCost cost = new ItemCost();
 		cost.setCost( new BigDecimal(1) );
 		cost.setId( 1 );
 		cost.setItem( item );
 		cost.setVendor( vendor );
 		WarehouseItem wItem = new WarehouseItem();
 		wItem.setDefaultCost( cost );
 		wItem.setId( 1 );
 		wItem.setItem( item );
 		wItem.setQtyInStock( new BigDecimal(1) );
 		wItem.setVendor( vendor );
 		Session s = openSession( );
 		s.getTransaction().begin();
 		s.persist( item );
 		s.persist( vendor );
 		s.persist( cost );
 		s.persist( wItem );
 		s.flush();
 		s.clear();
 		wItem = (WarehouseItem) s.get(WarehouseItem.class, wItem.getId() );
 		assertNotNull( wItem.getDefaultCost().getItem() );
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneInsideComponentReferencedColumn() {
 		HousePlaces house = new HousePlaces();
 		house.places = new Places();
 
 		house.places.livingRoom = new Place();
 		house.places.livingRoom.name = "First";
 		house.places.livingRoom.owner = "mine";
 
 		house.places.kitchen = new Place();
 		house.places.kitchen.name = "Kitchen 1";
 
 		house.neighbourPlaces = new Places();
 		house.neighbourPlaces.livingRoom = new Place();
 		house.neighbourPlaces.livingRoom.name = "Neighbour";
 		house.neighbourPlaces.livingRoom.owner = "his";
 
 		house.neighbourPlaces.kitchen = new Place();
 		house.neighbourPlaces.kitchen.name = "His Kitchen";
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.save( house );
 		s.flush();
 
 		HousePlaces get = (HousePlaces) s.get( HousePlaces.class, house.id );
 		assertEquals( house.id, get.id );
 
 		HousePlaces uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.places.livingRoom.name='First'" )
 				.uniqueResult();
 		assertNotNull( uniqueResult );
 		assertEquals( uniqueResult.places.livingRoom.name, "First" );
 		assertEquals( uniqueResult.places.livingRoom.owner, "mine" );
 
 		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.places.livingRoom.owner=:owner" )
 				.setParameter( "owner", "mine" ).uniqueResult();
 		assertNotNull( uniqueResult );
 		assertEquals( uniqueResult.places.livingRoom.name, "First" );
 		assertEquals( uniqueResult.places.livingRoom.owner, "mine" );
 
 		assertNotNull( s.createCriteria( HousePlaces.class ).add( Restrictions.eq( "places.livingRoom.owner", "mine" ) )
 				.uniqueResult() );
 
 		// override
 		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.neighbourPlaces.livingRoom.owner='his'" )
 				.uniqueResult();
 		assertNotNull( uniqueResult );
 		assertEquals( uniqueResult.neighbourPlaces.livingRoom.name, "Neighbour" );
 		assertEquals( uniqueResult.neighbourPlaces.livingRoom.owner, "his" );
 
 		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.neighbourPlaces.livingRoom.name=:name" )
 				.setParameter( "name", "Neighbour" ).uniqueResult();
 		assertNotNull( uniqueResult );
 		assertEquals( uniqueResult.neighbourPlaces.livingRoom.name, "Neighbour" );
 		assertEquals( uniqueResult.neighbourPlaces.livingRoom.owner, "his" );
 
 		assertNotNull( s.createCriteria( HousePlaces.class )
 				.add( Restrictions.eq( "neighbourPlaces.livingRoom.owner", "his" ) ).uniqueResult() );
 
 		tx.rollback();
 	}
 
 	@Override
 	protected void configure(Configuration configuration) {
 		super.configure( configuration );
 		configuration.setImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				House.class,
 				Postman.class,
 				Bag.class,
 				Rambler.class,
 				Luggage.class,
 				Clothes.class,
 				Inhabitant.class,
 				Item.class,
 				ItemCost.class,
 				Vendor.class,
 				WarehouseItem.class,
 				Place.class,
 				HousePlaces.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/Dvd.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/Dvd.java
index 326c779417..a1a21c0b15 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/Dvd.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/Dvd.java
@@ -1,47 +1,48 @@
 //$Id$
 package org.hibernate.test.annotations.type;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 
 import org.hibernate.annotations.Columns;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.Type;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 public class Dvd {
 	private MyOid id;
 	private String title;
 
 	@Id
 	@GeneratedValue(generator = "custom-id")
 	@GenericGenerator(name = "custom-id", strategy = "org.hibernate.test.annotations.type.MyOidGenerator")
 	@Type(type = "org.hibernate.test.annotations.type.MyOidType")
 	@Columns(
 			columns = {
 			@Column(name = "high"),
 			@Column(name = "middle"),
 			@Column(name = "low"),
 			@Column(name = "other")
 					}
 	)
 	public MyOid getId() {
 		return id;
 	}
 
 	public void setId(MyOid id) {
 		this.id = id;
 	}
 
+	@Column(name="`title`")
 	public String getTitle() {
 		return title;
 	}
 
 	public void setTitle(String title) {
 		this.title = title;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Building.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Building.java
index a6a2a43a45..44f6d814c9 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Building.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Building.java
@@ -1,32 +1,32 @@
 package org.hibernate.test.annotations.uniqueconstraint;
 
 import javax.persistence.ManyToOne;
 import javax.persistence.MappedSuperclass;
 
 /**
  * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
  */
 @MappedSuperclass
 public class Building {
 
     public Long height;
 
     private Room room;
 
     public Long getHeight() {
         return height;
     }
 
     public void setHeight(Long height) {
         this.height = height;
     }
 
-    @ManyToOne
+    @ManyToOne(optional = false)
     public Room getRoom() {
         return room;
     }
 
     public void setRoom(Room room) {
         this.room = room;
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/House.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/House.java
index 2d6ffaf528..de2455d6ae 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/House.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/House.java
@@ -1,35 +1,37 @@
 package org.hibernate.test.annotations.uniqueconstraint;
 
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.persistence.UniqueConstraint;
+import javax.validation.constraints.NotNull;
 
 /**
  * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
  */
 @Entity
 @Table(uniqueConstraints = {@UniqueConstraint(name = "uniqueWithInherited", columnNames = {"room_id", "cost"} )})
 public class House extends Building {
-
+	@Column(nullable = false)
     public Long id;
-
+	@NotNull
     public Integer cost;
 
     @Id
     public Long getId() {
         return id;
     }
 
     public void setId(Long id) {
         this.id = id;
     }
 
     public Integer getCost() {
         return cost;
     }
 
     public void setCost(Integer cost) {
         this.cost = cost;
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Room.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Room.java
index 78057ad33f..b244814fce 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Room.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/Room.java
@@ -1,32 +1,33 @@
 package org.hibernate.test.annotations.uniqueconstraint;
 
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 /**
  * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
  */
 @Entity
 public class Room {
-
     private Long id;
 
     private String name;
 
     public String getName() {
         return name;
     }
 
     public void setName(String name) {
         this.name = name;
     }
 
     @Id
+	@Column(nullable = false)
     public Long getId() {
         return id;
     }
 
     public void setId(Long id) {
         this.id = id;
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/UniqueConstraintTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/UniqueConstraintTest.java
index a49fe0b42f..e4531a237f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/UniqueConstraintTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/uniqueconstraint/UniqueConstraintTest.java
@@ -1,58 +1,57 @@
 package org.hibernate.test.annotations.uniqueconstraint;
 
 import org.hibernate.JDBCException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.fail;
 
 /**
  * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
  * @author Brett Meyer
  */
 public class UniqueConstraintTest extends BaseCoreFunctionalTestCase {
 	
 	protected Class[] getAnnotatedClasses() {
         return new Class[]{
                 Room.class,
                 Building.class,
                 House.class
         };
     }
 
 	@Test
-    public void testUniquenessConstraintWithSuperclassProperty() throws Exception {
+	public void testUniquenessConstraintWithSuperclassProperty() throws Exception {
         Session s = openSession();
         Transaction tx = s.beginTransaction();
         Room livingRoom = new Room();
         livingRoom.setId(1l);
         livingRoom.setName("livingRoom");
         s.persist(livingRoom);
         s.flush();
         House house = new House();
         house.setId(1l);
         house.setCost(100);
         house.setHeight(1000l);
         house.setRoom(livingRoom);
         s.persist(house);
         s.flush();
         House house2 = new House();
         house2.setId(2l);
         house2.setCost(100);
         house2.setHeight(1001l);
         house2.setRoom(livingRoom);
         s.persist(house2);
         try {
             s.flush();
             fail("Database constraint non-existant");
         } catch(JDBCException e) {
             //success
         }
         tx.rollback();
         s.close();
     }
     
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/various/Vehicule.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/various/Vehicule.java
index 0dca018025..3e9c5b8128 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/various/Vehicule.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/various/Vehicule.java
@@ -1,82 +1,83 @@
 //$Id$
 package org.hibernate.test.annotations.various;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 import javax.persistence.InheritanceType;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.Index;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 @Inheritance(strategy = InheritanceType.JOINED)
 @org.hibernate.annotations.Table(appliesTo = "Vehicule",
 		indexes = {
 		@Index(name = "improbableindex", columnNames = {"registration", "Conductor_fk"}),
 		@Index(name = "secondone", columnNames = {"Conductor_fk"})
 				}
 )
 public class Vehicule {
 	@Id
 	@GeneratedValue(generator = "gen")
 	@GenericGenerator(name = "gen", strategy = "uuid")
 	private String id;
 	@Column(name = "registration")
 	private String registrationNumber;
 	@ManyToOne(optional = false)
 	@JoinColumn(name = "Conductor_fk")
 	@Index(name = "thirdone")
 	private Conductor currentConductor;
 	@Index(name = "year_idx")
+	@Column(name = "`year`")
 	private Integer year;
 	@ManyToOne(optional = true)
 	@Index(name = "forthone")
 	private Conductor previousConductor;
 
 	public String getId() {
 		return id;
 	}
 
 	public void setId(String id) {
 		this.id = id;
 	}
 
 	public String getRegistrationNumber() {
 		return registrationNumber;
 	}
 
 	public void setRegistrationNumber(String registrationNumber) {
 		this.registrationNumber = registrationNumber;
 	}
 
 	public Conductor getCurrentConductor() {
 		return currentConductor;
 	}
 
 	public void setCurrentConductor(Conductor currentConductor) {
 		this.currentConductor = currentConductor;
 	}
 
 	public Integer getYear() {
 		return year;
 	}
 
 	public void setYear(Integer year) {
 		this.year = year;
 	}
 
 	public Conductor getPreviousConductor() {
 		return previousConductor;
 	}
 
 	public void setPreviousConductor(Conductor previousConductor) {
 		this.previousConductor = previousConductor;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/CarModel.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/CarModel.java
index eae089e017..f0a13d42b5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/CarModel.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/CarModel.java
@@ -1,40 +1,42 @@
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
 package org.hibernate.test.annotations.xml.ejb3;
+import javax.persistence.Column;
 import java.util.Date;
 
 /**
  * @author Emmanuel Bernard
  */
 public class CarModel extends Model {
+	@Column(name="`year`")
 	private Date year;
 
 	public Date getYear() {
 		return year;
 	}
 
 	public void setYear(Date year) {
 		this.year = year;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/Ejb3XmlTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/Ejb3XmlTest.java
index 21a2308357..616259c488 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/Ejb3XmlTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/xml/ejb3/Ejb3XmlTest.java
@@ -1,154 +1,161 @@
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
 package org.hibernate.test.annotations.xml.ejb3;
 
 import java.util.Date;
 import java.util.List;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
+import org.hibernate.dialect.TeradataDialect;;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 import org.hibernate.testing.SkipForDialect;
+import org.hibernate.testing.SkipForDialects;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Emmanuel Bernard
  */
 public class Ejb3XmlTest extends BaseCoreFunctionalTestCase {
 	@Test
+	@SkipForDialects  ( {
 	@SkipForDialect(value = { PostgreSQL81Dialect.class, PostgreSQLDialect.class },
-			comment = "postgresql jdbc driver does not implement the setQueryTimeout method")
+	comment = "postgresql jdbc driver does not implement the setQueryTimeout method"),
+	@SkipForDialect(value = TeradataDialect.class ,
+	jiraKey = "HHH-8190",
+	comment = "uses Teradata reserved word - year")
+} )
 	public void testEjb3Xml() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		CarModel model = new CarModel();
 		model.setYear( new Date() );
 		Manufacturer manufacturer = new Manufacturer();
 		//s.persist( manufacturer );
 		model.setManufacturer( manufacturer );
 		manufacturer.getModels().add( model );
 		s.persist( model );
 		s.flush();
 		s.clear();
 
 		model.setYear( new Date() );
 		manufacturer = (Manufacturer) s.get( Manufacturer.class, manufacturer.getId() );
 		@SuppressWarnings("unchecked")
 		List<Model> cars = s.getNamedQuery( "allModelsPerManufacturer" )
 				.setParameter( "manufacturer", manufacturer )
 				.list();
 		assertEquals( 1, cars.size() );
 		for ( Model car : cars ) {
 			assertNotNull( car.getManufacturer() );
 			s.delete( manufacturer );
 			s.delete( car );
 		}
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testXMLEntityHandled() throws Exception {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Lighter l = new Lighter();
 		l.name = "Blue";
 		l.power = "400F";
 		s.persist( l );
 		s.flush();
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
 	public void testXmlDefaultOverriding() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Manufacturer manufacturer = new Manufacturer();
 		s.persist( manufacturer );
 		s.flush();
 		s.clear();
 
 		assertEquals( 1, s.getNamedQuery( "manufacturer.findAll" ).list().size() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings("unchecked")
 	public void testMapXMLSupport() throws Exception {
 		Session s = openSession();
 		SessionFactory sf = s.getSessionFactory();
 		Transaction tx = s.beginTransaction();
 
 		// Verify that we can persist an object with a couple Map mappings
 		VicePresident vpSales = new VicePresident();
 		vpSales.name = "Dwight";
 		Company company = new Company();
 		company.conferenceRoomExtensions.put( "8932", "x1234" );
 		company.organization.put( "sales", vpSales );
 		s.persist( company );
 		s.flush();
 		s.clear();
 
 		// For the element-collection, check that the orm.xml entries are honored.
 		// This includes: map-key-column/column/collection-table/join-column
 		BasicCollectionPersister confRoomMeta = (BasicCollectionPersister) sf.getCollectionMetadata( Company.class.getName() + ".conferenceRoomExtensions" );
 		assertEquals( "company_id", confRoomMeta.getKeyColumnNames()[0] );
 		assertEquals( "phone_extension", confRoomMeta.getElementColumnNames()[0] );
 		assertEquals( "room_number", confRoomMeta.getIndexColumnNames()[0] );
 		assertEquals( "phone_extension_lookup", confRoomMeta.getTableName() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				CarModel.class,
 				Manufacturer.class,
 				Model.class,
 				Light.class
 				//Lighter.class xml only entuty
 		};
 	}
 
 	@Override
 	protected String[] getXmlFiles() {
 		return new String[] {
 				"org/hibernate/test/annotations/xml/ejb3/orm.xml",
 				"org/hibernate/test/annotations/xml/ejb3/orm2.xml",
 				"org/hibernate/test/annotations/xml/ejb3/orm3.xml",
 				"org/hibernate/test/annotations/xml/ejb3/orm4.xml"
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java
index f5b864727c..0a39beb9be 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java
@@ -1,86 +1,90 @@
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
 package org.hibernate.test.event.collection.detached;
 
 import javax.persistence.CascadeType;
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Index;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
+import javax.persistence.Table;
 import java.util.ArrayList;
 import java.util.List;
 
 /**
  * @author Steve Ebersole
  */
 @Entity
+@Table(name="`Alias`")
 public class Alias implements Identifiable {
 	private Integer id;
 	private String alias;
 	private List<Character> characters = new ArrayList<Character>();
 
 	public Alias() {
 	}
 
 	public Alias(Integer id, String alias) {
 		this.id = id;
 		this.alias = alias;
 	}
 
 	@Id
 	@Override
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
+	@Column(name="`alias`")
 	public String getAlias() {
 		return alias;
 	}
 
 	public void setAlias(String alias) {
 		this.alias = alias;
 	}
 
 	@ManyToMany( cascade = CascadeType.ALL )
 	@JoinTable( name = "CHARACTER_ALIAS", indexes = @Index( columnList = "characters_id"))
 //	@JoinTable(
 //			name = "CHARACTER_ALIAS",
 //			joinColumns = @JoinColumn(name="ALIAS_ID", referencedColumnName="ID"),
 //			inverseJoinColumns = @JoinColumn(name="CHARACTER_ID", referencedColumnName="ID")
 //	)
 	public List<Character> getCharacters() {
 		return characters;
 	}
 
 	public void setCharacters(List<Character> characters) {
 		this.characters = characters;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extralazy/UserGroup.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/extralazy/UserGroup.hbm.xml
index d280af2b36..5a5edc21b0 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/extralazy/UserGroup.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/extralazy/UserGroup.hbm.xml
@@ -1,67 +1,67 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <!-- 
      
 -->
 
 <hibernate-mapping package="org.hibernate.test.extralazy">
 	
 	<class name="Group" table="groups">
 		<id name="name"/>
 		<map name="users" cascade="persist" 
 				table="group_user" lazy="extra">
 			<key column="groupName"/>
 			<map-key formula="lower(personName)" type="string"/>
 			<many-to-many column="personName" class="User"/>
 		</map>
 	</class>
 	
 	<class name="User" table="users">
 		<id name="name"/>
-		<property name="password"/>
+		<property name="password" column="`password`"/>
 		<map name="session" lazy="extra" 
 				cascade="persist,save-update,delete,delete-orphan">
 			<key column="userName" not-null="true"/>
 			<map-key column="name" type="string"/>
 			<one-to-many class="SessionAttribute"/>
 		</map>
 		<set name="documents" inverse="true" 
 				lazy="extra" cascade="all,delete-orphan">
 			<key column="owner"/>
 			<one-to-many class="Document"/>
 		</set>
 	</class>
 	
 	<class name="Document" table="documents">
-		<id name="title"/>
+		<id name="title" column="`title`"/>
 		<property name="content" type="text"/>
 		<many-to-one name="owner" not-null="true"/>
 	</class>
 	
 	<class name="SessionAttribute" table="session_attributes">
 		<id name="id" access="field">
 			<generator class="native"/>
 		</id>
 		<property name="name" not-null="true" 
 				insert="false" update="false"/>
 		<property name="stringData"/>
 		<property name="objectData"/>
 	</class>
 	
 	<sql-query name="userSessionData">
 		<return alias="u" class="User"/>
 		<return-join alias="s" property="u.session"/>
 		select 
-			lower(u.name) as {u.name}, lower(u.password) as {u.password}, 
+			lower(u.name) as {u.name}, lower(u."password") as {u.password}, 
 			lower(s.userName) as {s.key}, lower(s.name) as {s.index}, s.id as {s.element}, 
 			{s.element.*}
 		from users u 
 		join session_attributes s on lower(s.userName) = lower(u.name)
 		where u.name like :uname
 	</sql-query>	
 	
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/flush/Book.java b/hibernate-core/src/test/java/org/hibernate/test/flush/Book.java
index 600723dc77..88f027405c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/flush/Book.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/flush/Book.java
@@ -1,78 +1,80 @@
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
 package org.hibernate.test.flush;
 
 import javax.persistence.CascadeType;
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 
 import org.hibernate.annotations.GenericGenerator;
 
 /**
  * @author Steve Ebersole
  */
 @Entity
 public class Book {
 	private Long id;
 	private String title;
 	private Author author;
 
 	public Book() {
 	}
 
 	public Book(String title, Author author) {
 		this.title = title;
 		this.author = author;
 	}
 
 	@Id
 	@GeneratedValue( generator = "increment" )
 	@GenericGenerator( name = "increment", strategy = "increment" )
 	public Long getId() {
 		return id;
 	}
 
 	public void setId(Long id) {
 		this.id = id;
 	}
 
+	@Column(name="`title`")
 	public String getTitle() {
 		return title;
 	}
 
 	public void setTitle(String title) {
 		this.title = title;
 	}
 
 	@ManyToOne( cascade = CascadeType.ALL )
 	public Author getAuthor() {
 		return author;
 	}
 
 	public void setAuthor(Author author) {
 		this.author = author;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
index 404fa01327..975d28f2c2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
@@ -1,1092 +1,1091 @@
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
 package org.hibernate.test.hql;
 
-import java.math.BigDecimal;
-import java.math.BigInteger;
-import java.sql.Date;
-import java.sql.Time;
-import java.sql.Timestamp;
-import java.util.ArrayList;
-import java.util.Collection;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.CUBRIDDialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.DiscriminatorType;
 import org.hibernate.stat.QueryStatistics;
-import org.hibernate.transform.DistinctRootEntityResultTransformer;
-import org.hibernate.transform.Transformers;
-import org.hibernate.type.ComponentType;
-import org.hibernate.type.ManyToOneType;
-import org.hibernate.type.Type;
-
-import org.hibernate.testing.DialectChecks;
-import org.hibernate.testing.FailureExpected;
-import org.hibernate.testing.RequiresDialect;
-import org.hibernate.testing.RequiresDialectFeature;
-import org.hibernate.testing.SkipForDialect;
-import org.hibernate.testing.TestForIssue;
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.any.IntegerPropertyValue;
 import org.hibernate.test.any.PropertySet;
 import org.hibernate.test.any.PropertyValue;
 import org.hibernate.test.any.StringPropertyValue;
 import org.hibernate.test.cid.Customer;
 import org.hibernate.test.cid.LineItem;
 import org.hibernate.test.cid.LineItem.Id;
 import org.hibernate.test.cid.Order;
 import org.hibernate.test.cid.Product;
+import org.hibernate.testing.DialectChecks;
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.RequiresDialectFeature;
+import org.hibernate.testing.SkipForDialect;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.transform.DistinctRootEntityResultTransformer;
+import org.hibernate.transform.Transformers;
+import org.hibernate.type.ComponentType;
+import org.hibernate.type.ManyToOneType;
+import org.hibernate.type.Type;
+import org.jboss.logging.Logger;
 import org.junit.Test;
 
-import org.jboss.logging.Logger;
+import java.math.BigDecimal;
+import java.math.BigInteger;
+import java.sql.Date;
+import java.sql.Time;
+import java.sql.Timestamp;
+import java.util.ArrayList;
+import java.util.Collection;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertClassAssignability;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Tests the integration of the new AST parser into the loading of query results using
  * the Hibernate persisters and loaders.
  * <p/>
  * Also used to test the syntax of the resulting sql against the underlying
  * database, specifically for functionality not supported by the classic
  * parser.
  *
  * @author Steve
  */
 @SkipForDialect(
         value = CUBRIDDialect.class,
         comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" +
                 "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables"
 )
 public class ASTParserLoadingTest extends BaseCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( ASTParserLoadingTest.class );
 
 	private List<Long> createdAnimalIds = new ArrayList<Long>();
 	@Override
 	protected boolean isCleanupTestDataRequired() {
 		return true;
 	}
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"hql/Animal.hbm.xml",
 				"hql/FooBarCopy.hbm.xml",
 				"hql/SimpleEntityWithAssociation.hbm.xml",
 				"hql/CrazyIdFieldNames.hbm.xml",
 				"hql/Image.hbm.xml",
 				"hql/ComponentContainer.hbm.xml",
 				"hql/VariousKeywordPropertyEntity.hbm.xml",
 				"hql/Constructor.hbm.xml",
 				"batchfetch/ProductLine.hbm.xml",
 				"cid/Customer.hbm.xml",
 				"cid/Order.hbm.xml",
 				"cid/LineItem.hbm.xml",
 				"cid/Product.hbm.xml",
 				"any/Properties.hbm.xml",
 				"legacy/Commento.hbm.xml",
 				"legacy/Marelo.hbm.xml"
 		};
 	}
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] {
 				Department.class,
 				Employee.class,
 				Title.class
 		};
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.QUERY_TRANSLATOR, ASTQueryTranslatorFactory.class.getName() );
 	}
 
 	@Test
 	public void testSubSelectAsArithmeticOperand() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		// first a control
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) = 0" ).list();
 
 		// now as operands singly:
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) + 0 = 0" ).list();
 		s.createQuery( "from Zoo z where 0 + ( select count(*) from Zoo ) = 0" ).list();
 
 		// and doubly:
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) + ( select count(*) from Zoo ) = 0" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-8432" )
 	public void testExpandListParameter() {
 		final Object[] namesArray = new Object[] {
 				"ZOO 1", "ZOO 2", "ZOO 3", "ZOO 4", "ZOO 5", "ZOO 6", "ZOO 7",
 				"ZOO 8", "ZOO 9", "ZOO 10", "ZOO 11", "ZOO 12"
 		};
 		final Object[] citiesArray = new Object[] {
 				"City 1", "City 2", "City 3", "City 4", "City 5", "City 6", "City 7",
 				"City 8", "City 9", "City 10", "City 11", "City 12"
 		};
 
 		Session session = openSession();
 
 		session.getTransaction().begin();
 		Address address = new Address();
 		Zoo zoo = new Zoo( "ZOO 1", address );
 		address.setCity( "City 1" );
 		session.save( zoo );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		List result = session.createQuery( "FROM Zoo z WHERE z.name IN (?1) and z.address.city IN (?11)" )
 				.setParameterList( "1", namesArray )
 				.setParameterList( "11", citiesArray )
 				.list();
 		assertEquals( 1, result.size() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		zoo = (Zoo) session.get( Zoo.class, zoo.getId() );
 		session.delete( zoo );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8699")
 	// For now, restrict to H2.  Selecting w/ predicate functions cause issues for too many dialects.
 	@RequiresDialect(value = H2Dialect.class, jiraKey = "HHH-9052")
 	public void testBooleanPredicate() {
 		final Session session = openSession();
 
 		session.getTransaction().begin();
 		final Constructor constructor = new Constructor();
 		session.save( constructor );
 		session.getTransaction().commit();
 
 		session.clear();
 		Constructor.resetConstructorExecutionCount();
 
 		session.getTransaction().begin();
 		final Constructor result = (Constructor) session.createQuery(
 				"select new Constructor( c.id, c.id is not null, c.id = c.id, c.id + 1, concat( c.id, 'foo' ) ) from Constructor c where c.id = :id"
 		).setParameter( "id", constructor.getId() ).uniqueResult();
 		session.getTransaction().commit();
 
 		assertEquals( 1, Constructor.getConstructorExecutionCount() );
 		assertEquals( new Constructor( constructor.getId(), true, true, constructor.getId() + 1, constructor.getId() + "foo" ), result );
 
 		session.close();
 	}
 
 	@Test
 	public void testJpaTypeOperator() {
 		// just checking syntax here...
 		Session s = openSession();
 		s.beginTransaction();
 
 		///////////////////////////////////////////////////////////////
 		// where clause
 		// control
 		s.createQuery( "from Animal a where a.class = Dog" ).list();
         // test
 		s.createQuery( "from Animal a where type(a) = Dog" ).list();
 
 		///////////////////////////////////////////////////////////////
 		// select clause (at some point we should unify these)
 		// control
 		Query query = s.createQuery( "select a.class from Animal a where a.class = Dog" );
 		query.list(); // checks syntax
 		assertEquals( 1, query.getReturnTypes().length );
 		assertEquals( Integer.class, query.getReturnTypes()[0].getReturnedClass() ); // always integer for joined
         // test
 		query = s.createQuery( "select type(a) from Animal a where type(a) = Dog" );
 		query.list(); // checks syntax
 		assertEquals( 1, query.getReturnTypes().length );
 		assertEquals( DiscriminatorType.class, query.getReturnTypes()[0].getClass() );
 		assertEquals( Class.class, query.getReturnTypes()[0].getReturnedClass() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentJoins() {
 		Session s = openSession();
 		s.beginTransaction();
 		ComponentContainer root = new ComponentContainer(
 				new ComponentContainer.Address(
 						"123 Main",
 						"Anywhere",
 						"USA",
 						new ComponentContainer.Address.Zip( 12345, 6789 )
 				)
 		);
 		s.save( root );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List result = s.createQuery( "select a from ComponentContainer c join c.address a" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select a.zip from ComponentContainer c join c.address a" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.Zip.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select z from ComponentContainer c join c.address a join a.zip z" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.Zip.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select z.code from ComponentContainer c join c.address a join a.zip z" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( Integer.class.isInstance( result.get( 0 ) ) );
 		s.delete( root );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9642")
 	public void testLazyAssociationInComponent() {
 		Session session = openSession();
 		session.getTransaction().begin();
 
 		Address address = new Address();
 		Zoo zoo = new Zoo( "ZOO 1", address );
 		address.setCity( "City 1" );
 		StateProvince stateProvince = new StateProvince();
 		stateProvince.setName( "Illinois" );
 		session.save( stateProvince );
 		address.setStateProvince( stateProvince );
 		session.save( zoo );
 
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.getTransaction().begin();
 
 		zoo = (Zoo) session.createQuery( "from Zoo z" ).uniqueResult();
 		assertNotNull( zoo );
 		assertNotNull( zoo.getAddress() );
 		assertEquals( "City 1", zoo.getAddress().getCity() );
 		assertFalse( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
 		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
 		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
 
 		session.getTransaction().commit();
 		session.close();
 
 
 		session = openSession();
 		session.getTransaction().begin();
 
 		zoo = (Zoo) session.createQuery( "from Zoo z join fetch z.address.stateProvince" ).uniqueResult();
 		assertNotNull( zoo );
 		assertNotNull( zoo.getAddress() );
 		assertEquals( "City 1", zoo.getAddress().getCity() );
 		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
 		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
 
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.getTransaction().begin();
 
 		zoo = (Zoo) session.createQuery( "from Zoo z join fetch z.address a join fetch a.stateProvince" ).uniqueResult();
 		assertNotNull( zoo );
 		assertNotNull( zoo.getAddress() );
 		assertEquals( "City 1", zoo.getAddress().getCity() );
 		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
 		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
 
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.getTransaction().begin();
 
 		zoo.getAddress().setStateProvince( null );
 		session.delete( stateProvince );
 		session.delete( zoo );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testJPAQLQualifiedIdentificationVariablesControl() {
 		// just checking syntax here...
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from VariousKeywordPropertyEntity where type = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where value = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where key = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where entry = 'something'" ).list();
 
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.type = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.value = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.key = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.entry = 'something'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapKeyQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select distinct key(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object key = results.get(0);
 			assertTrue( String.class.isAssignableFrom( key.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select distinct KEY(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object key = results.get(0);
 			assertTrue( String.class.isAssignableFrom( key.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// in WHERE clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h where KEY(h.family) = 'son'" ).uniqueResult();
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h join h.family f where key(f) = 'son'" ).uniqueResult();
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapEntryQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select entry(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Map.Entry.class.isAssignableFrom( result.getClass() ) );
 			Map.Entry entry = (Map.Entry) result;
 			assertTrue( String.class.isAssignableFrom( entry.getKey().getClass() ) );
 			assertTrue( Human.class.isAssignableFrom( entry.getValue().getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select ENTRY(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Map.Entry.class.isAssignableFrom( result.getClass() ) );
 			Map.Entry entry = (Map.Entry) result;
 			assertTrue( String.class.isAssignableFrom( entry.getKey().getClass() ) );
 			assertTrue( Human.class.isAssignableFrom( entry.getValue().getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// not exactly sure of the syntax of ENTRY in the WHERE clause...
 
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapValueQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select value(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Human.class.isAssignableFrom( result.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select VALUE(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Human.class.isAssignableFrom( result.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// in WHERE clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h where VALUE(h.family) = :joe" ).setParameter( "joe", joe ).uniqueResult();
 			// ACTUALLY EXACTLY THE SAME AS:
 			// select count(*) from Human h where h.family = :joe
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h join h.family f where value(f) = :joe" ).setParameter( "joe", joe ).uniqueResult();
 			// ACTUALLY EXACTLY THE SAME AS:
 			// select count(*) from Human h join h.family f where f = :joe
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(
 			value = IngresDialect.class,
 			jiraKey = "HHH-4961",
 			comment = "Ingres does not support this scoping in 9.3"
 	)
 	public void testPaginationWithPolymorphicQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Steve", null, "Ebersole" ) );
 		s.save( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from java.lang.Object" ).setMaxResults( 2 ).list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( h );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2045" )
 	@RequiresDialect( H2Dialect.class )
 	public void testEmptyInList() {
 		Session session = openSession();
 		session.beginTransaction();
 		Human human = new Human();
 		human.setName( new Name( "Lukasz", null, "Antoniak" ) );
 		human.setNickName( "NONE" );
 		session.save( human );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		List results = session.createQuery( "from Human h where h.nickName in ()" ).list();
 		assertEquals( 0, results.size() );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		session.delete( human );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testComponentNullnessChecks() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Johnny", 'B', "Goode" ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( "Steve", null, "Ebersole" ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( "Bono", null, null ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( null, null, null ) );
 		s.save( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Human where name is null" ).list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "from Human where name is not null" ).list();
 		assertEquals( 3, results.size() );
 		String query =
 				( getDialect() instanceof DB2Dialect || getDialect() instanceof HSQLDialect ) ?
 						"from Human where cast(? as string) is null" :
 						"from Human where ? is null"
 				;
 		s.createQuery( query ).setParameter( 0, null ).list();
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete Human" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-4150" )
 	public void testSelectClauseCaseWithSum() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Human h1 = new Human();
 		h1.setBodyWeight( 74.0f );
 		h1.setDescription( "Me" );
 		s.persist( h1 );
 
 		Human h2 = new Human();
 		h2.setBodyWeight( 125.0f );
 		h2.setDescription( "big persion #1" );
 		s.persist( h2 );
 
 		Human h3 = new Human();
 		h3.setBodyWeight( 110.0f );
 		h3.setDescription( "big persion #2" );
 		s.persist( h3 );
 
 		s.flush();
 
 		Number count = (Number) s.createQuery( "select sum(case when bodyWeight > 100 then 1 else 0 end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 		count = (Number) s.createQuery( "select sum(case when bodyWeight > 100 then bodyWeight else 0 end) from Human" ).uniqueResult();
 		assertEquals( h2.getBodyWeight() + h3.getBodyWeight(), count.floatValue(), 0.001 );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-4150" )
 	public void testSelectClauseCaseWithCountDistinct() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Human h1 = new Human();
 		h1.setBodyWeight( 74.0f );
 		h1.setDescription( "Me" );
 		h1.setNickName( "Oney" );
 		s.persist( h1 );
 
 		Human h2 = new Human();
 		h2.setBodyWeight( 125.0f );
 		h2.setDescription( "big persion" );
 		h2.setNickName( "big #1" );
 		s.persist( h2 );
 
 		Human h3 = new Human();
 		h3.setBodyWeight( 110.0f );
 		h3.setDescription( "big persion" );
 		h3.setNickName( "big #2" );
 		s.persist( h3 );
 
 		s.flush();
 
 		Number count = (Number) s.createQuery( "select count(distinct case when bodyWeight > 100 then description else null end) from Human" ).uniqueResult();
 		assertEquals( 1, count.intValue() );
 		count = (Number) s.createQuery( "select count(case when bodyWeight > 100 then description else null end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 		count = (Number) s.createQuery( "select count(distinct case when bodyWeight > 100 then nickName else null end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testInvalidCollectionDereferencesFail() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		// control group...
 		s.createQuery( "from Animal a join a.offspring o where o.description = 'xyz'" ).list();
 		s.createQuery( "from Animal a join a.offspring o where o.father.description = 'xyz'" ).list();
 		s.createQuery( "from Animal a join a.offspring o order by o.description" ).list();
 		s.createQuery( "from Animal a join a.offspring o order by o.father.description" ).list();
 
 		try {
 			s.createQuery( "from Animal a where a.offspring.description = 'xyz'" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a where a.offspring.father.description = 'xyz'" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.father.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testConcatenation() {
 		// simple syntax checking...
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Human h where h.nickName = '1' || 'ov' || 'tha' || 'few'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testExpressionWithParamInFunction() {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal a where abs(a.bodyWeight-:param) < 2.0" ).setLong( "param", 1 ).list();
 		s.createQuery( "from Animal a where abs(:param - a.bodyWeight) < 2.0" ).setLong( "param", 1 ).list();
 		if ( ( getDialect() instanceof HSQLDialect ) || ( getDialect() instanceof DB2Dialect ) ) {
 			// HSQLDB and DB2 don't like the abs(? - ?) syntax. bit work if at least one parameter is typed...
 			s.createQuery( "from Animal where abs(cast(:x as long) - :y) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 			s.createQuery( "from Animal where abs(:x - cast(:y as long)) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 			s.createQuery( "from Animal where abs(cast(:x as long) - cast(:y as long)) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 		}
 		else {
 			s.createQuery( "from Animal where abs(:x - :y) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 		}
 
 		if ( getDialect() instanceof DB2Dialect ) {
 			s.createQuery( "from Animal where lower(upper(cast(:foo as string))) like 'f%'" ).setString( "foo", "foo" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where lower(upper(:foo)) like 'f%'" ).setString( "foo", "foo" ).list();
 		}
 		s.createQuery( "from Animal a where abs(abs(a.bodyWeight - 1.0 + :param) * abs(length('ffobar')-3)) = 3.0" ).setLong(
 				"param", 1
 		).list();
 		if ( getDialect() instanceof DB2Dialect ) {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(cast(:bar as string))) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(:bar)) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		
 		if ( getDialect() instanceof AbstractHANADialect ) {
 			s.createQuery( "from Animal where abs(cast(1 as double) - cast(:param as double)) = 1.0" )
 					.setLong( "param", 1 ).list();
 		}
 		else if ( !( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect
 				|| getDialect() instanceof MySQLDialect ) ) {
 			s.createQuery( "from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0" )
 					.setLong( "param", 1 ).list();
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCrazyIdFieldNames() {
 		MoreCrazyIdFieldNameStuffEntity top = new MoreCrazyIdFieldNameStuffEntity( "top" );
 		HeresAnotherCrazyIdFieldName next = new HeresAnotherCrazyIdFieldName( "next" );
 		top.setHeresAnotherCrazyIdFieldName( next );
 		MoreCrazyIdFieldNameStuffEntity other = new MoreCrazyIdFieldNameStuffEntity( "other" );
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( next );
 		s.save( top );
 		s.save( other );
 		s.flush();
 
 		List results = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null" ).list();
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertClassAssignability( HeresAnotherCrazyIdFieldName.class, result.getClass() );
 		assertSame( next, result );
 
 		results = s.createQuery( "select e.heresAnotherCrazyIdFieldName.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null" ).list();
 		assertEquals( 1, results.size() );
 		result = results.get( 0 );
 		assertClassAssignability( Long.class, result.getClass() );
 		assertEquals( next.getHeresAnotherCrazyIdFieldName(), result );
 
 		results = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e" ).list();
 		assertEquals( 1, results.size() );
 		Iterator itr = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e" ).iterate();
 		assertTrue( itr.hasNext() ); itr.next(); assertFalse( itr.hasNext() );
 
 		s.delete( top );
 		s.delete( next );
 		s.delete( other );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2257" )
 	public void testImplicitJoinsInDifferentClauses() {
 		// both the classic and ast translators output the same syntactically valid sql
 		// for all of these cases; the issue is that shallow (iterate) and
 		// non-shallow (list/scroll) queries return different results because the
 		// shallow skips the inner join which "weeds out" results from the non-shallow queries.
 		// The results were initially different depending upon the clause(s) in which the
 		// implicit join occurred
 		Session s = openSession();
 		s.beginTransaction();
 		SimpleEntityWithAssociation owner = new SimpleEntityWithAssociation( "owner" );
 		SimpleAssociatedEntity e1 = new SimpleAssociatedEntity( "thing one", owner );
 		SimpleAssociatedEntity e2 = new SimpleAssociatedEntity( "thing two" );
 		s.save( e1 );
 		s.save( e2 );
 		s.save( owner );
 		s.getTransaction().commit();
 		s.close();
 
 		checkCounts( "select e.owner from SimpleAssociatedEntity e", 1, "implicit-join in select clause" );
 		checkCounts( "select e.id, e.owner from SimpleAssociatedEntity e", 1, "implicit-join in select clause" );
 
 		// resolved to a "id short cut" when part of the order by clause -> no inner join = no weeding out...
 		checkCounts( "from SimpleAssociatedEntity e order by e.owner", 2, "implicit-join in order-by clause" );
 		// resolved to a "id short cut" when part of the group by clause -> no inner join = no weeding out...
 		checkCounts( "select e.owner.id, count(*) from SimpleAssociatedEntity e group by e.owner", 2, "implicit-join in select and group-by clauses" );
 
 	 	s = openSession();
 		s.beginTransaction();
 		s.delete( e1 );
 		s.delete( e2 );
 		s.delete( owner );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRowValueConstructorSyntaxInInList() {
 		Session s = openSession();
 		s.beginTransaction();
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		s.save( product );
 
 
 		Customer customer = new Customer();
 		customer.setCustomerId( "123456789" );
 		customer.setName( "My customer" );
 		customer.setAddress( "somewhere" );
 		s.save( customer );
 
 		Order order = customer.generateNewOrder( new BigDecimal( 1234 ) );
 		s.save( order );
 
 		LineItem li = order.generateLineItem( product, 5 );
 		s.save( li );
 		product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "1234" );
 		s.save( product );
 		li = order.generateLineItem( product, 10 );
 		s.save( li );
 
 		s.flush();
 		Query query = s.createQuery( "from LineItem l where l.id in (:idList)" );
 		List<Id> list = new ArrayList<Id>();
 		list.add( new Id( "123456789", order.getId().getOrderNumber(), "4321" ) );
 		list.add( new Id( "123456789", order.getId().getOrderNumber(), "1234" ) );
 		query.setParameterList( "idList", list );
 		assertEquals( 2, query.list().size() );
 
 		query = s.createQuery( "from LineItem l where l.id in :idList" );
 		query.setParameterList( "idList", list );
 		assertEquals( 2, query.list().size() );
 
 		s.getTransaction().rollback();
 		s.close();
 
 	}
 
 	private void checkCounts(String hql, int expected, String testCondition) {
 		Session s = openSession();
 		s.beginTransaction();
 		int count = determineCount( s.createQuery( hql ).list().iterator() );
 		assertEquals( "list() [" + testCondition + "]", expected, count );
 		count = determineCount( s.createQuery( hql ).iterate() );
 		assertEquals( "iterate() [" + testCondition + "]", expected, count );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2257" )
 	public void testImplicitSelectEntityAssociationInShallowQuery() {
 		// both the classic and ast translators output the same syntactically valid sql.
 		// the issue is that shallow and non-shallow queries return different
 		// results because the shallow skips the inner join which "weeds out" results
 		// from the non-shallow queries...
 		Session s = openSession();
 		s.beginTransaction();
 		SimpleEntityWithAssociation owner = new SimpleEntityWithAssociation( "owner" );
 		SimpleAssociatedEntity e1 = new SimpleAssociatedEntity( "thing one", owner );
 		SimpleAssociatedEntity e2 = new SimpleAssociatedEntity( "thing two" );
 		s.save( e1 );
 		s.save( e2 );
 		s.save( owner );
 		s.getTransaction().commit();
 		s.close();
 
 	 	s = openSession();
 		s.beginTransaction();
 		int count = determineCount( s.createQuery( "select e.id, e.owner from SimpleAssociatedEntity e" ).list().iterator() );
 		assertEquals( 1, count ); // thing two would be removed from the result due to the inner join
 		count = determineCount( s.createQuery( "select e.id, e.owner from SimpleAssociatedEntity e" ).iterate() );
 		assertEquals( 1, count );
 		s.getTransaction().commit();
 		s.close();
 
 	 	s = openSession();
 		s.beginTransaction();
 		s.delete( e1 );
 		s.delete( e2 );
 		s.delete( owner );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private int determineCount(Iterator iterator) {
 		int count = 0;
 		while( iterator.hasNext() ) {
 			count++;
 			iterator.next();
 		}
 		return count;
 	}
 
     @Test
     @TestForIssue( jiraKey = "HHH-6714" )
     public void testUnaryMinus(){
         Session s = openSession();
         s.beginTransaction();
         Human stliu = new Human();
         stliu.setIntValue( 26 );
 
         s.persist( stliu );
         s.getTransaction().commit();
         s.clear();
         s.beginTransaction();
         List list =s.createQuery( "from Human h where -(h.intValue - 100)=74" ).list();
         assertEquals( 1, list.size() );
         s.getTransaction().commit();
         s.close();
 
 
     }
 
 	@Test
 	public void testEntityAndOneToOneReturnedByQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Gail", null, "Badner" ) );
 		s.save( h );
 		User u = new User();
 		u.setUserName( "gbadner" );
 		u.setHuman( h );
 		s.save( u );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
@@ -1545,2019 +1544,2019 @@ public class ASTParserLoadingTest extends BaseCoreFunctionalTestCase {
 		// create some test data...
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		int parentCount = 30;
 		for ( int i = 0; i < parentCount; i++ ) {
 			Animal child1 = new Animal();
 			child1.setDescription( "collection fetch distinction (child1 - parent" + i + ")" );
 			s.persist( child1 );
 			Animal child2 = new Animal();
 			child2.setDescription( "collection fetch distinction (child2 - parent " + i + ")" );
 			s.persist( child2 );
 			Animal parent = new Animal();
 			parent.setDescription( "collection fetch distinction (parent" + i + ")" );
 			parent.setSerialNumber( "123-" + i );
 			parent.addOffspring( child1 );
 			parent.addOffspring( child2 );
 			s.persist( parent );
 		}
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		// Test simple distinction
 		List results;
 		results = s.createQuery( "select distinct p from Animal p inner join fetch p.offspring" ).list();
 		assertEquals( "duplicate list() returns", 30, results.size() );
 		// Test first/max
 		results = s.createQuery( "select p from Animal p inner join fetch p.offspring order by p.id" )
 				.setFirstResult( 5 )
 				.setMaxResults( 20 )
 				.list();
 		assertEquals( "duplicate returns", 20, results.size() );
 		Animal firstReturn = ( Animal ) results.get( 0 );
 		assertEquals( "firstResult not applied correctly", "123-5", firstReturn.getSerialNumber() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.createQuery( "delete Animal where mother is not null" ).executeUpdate();
 		s.createQuery( "delete Animal" ).executeUpdate();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInSubqueryFails() {
 		Session s = openSession();
 		try {
 			s.createQuery( "from Animal a where a.mother in (select m from Animal a1 inner join a1.mother as m join fetch m.mother)" ).list();
 			fail( "fetch join allowed in subquery" );
 		}
 		catch( QueryException expected ) {
 			// expected behavior
 		}
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1464" )
 	public void testQueryMetadataRetrievalWithFetching() {
 		// HHH-1464 : there was a problem due to the fact they we polled
 		// the shallow version of the query plan to get the metadata.
 		Session s = openSession();
 		Query query = s.createQuery( "from Animal a inner join fetch a.mother" );
 		assertEquals( 1, query.getReturnTypes().length );
 		assertNull( query.getReturnAliases() );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-429" )
 	@SuppressWarnings( {"unchecked"})
 	public void testSuperclassPropertyReferenceAfterCollectionIndexedAccess() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		Mammal tiger = new Mammal();
 		tiger.setDescription( "Tiger" );
 		s.persist( tiger );
 		Mammal mother = new Mammal();
 		mother.setDescription( "Tiger's mother" );
 		mother.setBodyWeight( 4.0f );
 		mother.addOffspring( tiger );
 		s.persist( mother );
 		Zoo zoo = new Zoo();
 		zoo.setName( "Austin Zoo" );
 		zoo.setMammals( new HashMap() );
 		zoo.getMammals().put( "tiger", tiger );
 		s.persist( zoo );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Zoo zoo where zoo.mammals['tiger'].mother.bodyWeight > 3.0f" ).list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( tiger );
 		s.delete( mother );
 		s.delete( zoo );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testJoinFetchCollectionOfValues() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "select h from Human as h join fetch h.nickNames" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testIntegerLiterals() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Foo where long = 1" ).list();
 		s.createQuery( "from Foo where long = " + Integer.MIN_VALUE ).list();
 		s.createQuery( "from Foo where long = " + Integer.MAX_VALUE ).list();
 		s.createQuery( "from Foo where long = 1L" ).list();
 		s.createQuery( "from Foo where long = " + (Long.MIN_VALUE + 1) + "L" ).list();
 		s.createQuery( "from Foo where long = " + Long.MAX_VALUE + "L" ).list();
 		s.createQuery( "from Foo where integer = " + (Long.MIN_VALUE + 1) ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testDecimalLiterals() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where bodyWeight > 100.0e-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.0E-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001f" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001F" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001d" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001D" ).list();
 		s.createQuery( "from Animal where bodyWeight > .001f" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100e-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > .01E-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 1e-38" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNakedPropertyRef() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where bodyWeight = bodyWeight" ).list();
 		s.createQuery( "select bodyWeight from Animal" ).list();
 		s.createQuery( "select max(bodyWeight) from Animal" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNakedComponentPropertyRef() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Human where name.first = 'Gavin'" ).list();
 		s.createQuery( "select name from Human" ).list();
 		s.createQuery( "select upper(h.name.first) from Human as h" ).list();
 		s.createQuery( "select upper(name.first) from Human" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNakedImplicitJoins() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where mother.father.id = 1" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNakedEntityAssociationReference() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		if ( getDialect() instanceof AbstractHANADialect ) {
 			s.createQuery( "from Animal where mother is null" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where mother = :mother" ).setParameter( "mother", null ).list();
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNakedMapIndex() throws Exception {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Zoo where mammals['dog'].description like '%black%'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testInvalidFetchSemantics() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		try {
 			s.createQuery( "select mother from Human a left join fetch a.mother mother" ).list();
 			fail( "invalid fetch semantic allowed!" );
 		}
 		catch( QueryException e ) {
 		}
 
 		try {
 			s.createQuery( "select mother from Human a left join fetch a.mother mother" ).list();
 			fail( "invalid fetch semantic allowed!" );
 		}
 		catch( QueryException e ) {
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testArithmetic() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("Melbourne Zoo");
 		s.persist(zoo);
 		s.createQuery("select 2*2*2*2*(2*2) from Zoo").uniqueResult();
 		s.createQuery("select 2 / (1+1) from Zoo").uniqueResult();
 		int result0 = ( (Integer) s.createQuery("select 2 - (1+1) from Zoo").uniqueResult() ).intValue();
 		int result1 = ( (Integer) s.createQuery("select 2 - 1 + 1 from Zoo").uniqueResult() ).intValue();
 		int result2 = ( (Integer) s.createQuery("select 2 * (1-1) from Zoo").uniqueResult() ).intValue();
 		int result3 = ( (Integer) s.createQuery("select 4 / (2 * 2) from Zoo").uniqueResult() ).intValue();
 		int result4 = ( (Integer) s.createQuery("select 4 / 2 * 2 from Zoo").uniqueResult() ).intValue();
 		int result5 = ( (Integer) s.createQuery("select 2 * (2/2) from Zoo").uniqueResult() ).intValue();
 		int result6 = ( (Integer) s.createQuery("select 2 * (2/2+1) from Zoo").uniqueResult() ).intValue();
 		assertEquals(result0, 0);
 		assertEquals(result1, 2);
 		assertEquals(result2, 0);
 		assertEquals(result3, 1);
 		assertEquals(result4, 4);
 		assertEquals(result5, 2);
 		assertEquals(result6, 4);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNestedCollectionFetch() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery("from Animal a left join fetch a.offspring o left join fetch o.offspring where a.mother.id = 1 order by a.description").list();
 		s.createQuery("from Zoo z left join fetch z.animals a left join fetch a.offspring where z.name ='MZ' order by a.description").list();
 		s.createQuery("from Human h left join fetch h.pets a left join fetch a.offspring where h.name.first ='Gavin' order by a.description").list();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(
 			value = IngresDialect.class,
 			jiraKey = "HHH-4973",
 			comment = "Ingres 9.3 does not support sub-selects in the select list"
 	)
 	@SuppressWarnings( {"unchecked"})
 	public void testSelectClauseSubselect() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("Melbourne Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo(zoo);
 		plat.setSerialNumber("plat123");
 		zoo.getMammals().put("Platypus", plat);
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist( zoo );
 
 		s.createQuery("select (select max(z.id) from a.zoo z) from Animal a").list();
 		s.createQuery("select (select max(z.id) from a.zoo z where z.name=:name) from Animal a")
 			.setParameter("name", "Melbourne Zoo").list();
 
 		s.delete( plat );
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testInitProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		s.persist( plat );
 		s.flush();
 		s.clear();
 		plat = (Mammal) s.load(Mammal.class, plat.getId() );
 		assertFalse( Hibernate.isInitialized(plat) );
 		Object plat2 = s.createQuery("from Animal a").uniqueResult();
 		assertSame( plat, plat2 );
 		assertTrue( Hibernate.isInitialized( plat ) );
 		s.delete( plat );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testSelectClauseImplicitJoin() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo( zoo );
 		plat.setSerialNumber( "plat123" );
 		zoo.getMammals().put( "Platypus", plat );
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist(zoo);
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select distinct a.zoo from Animal a where a.zoo is not null");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		zoo = (Zoo) q.list().get(0);
 		assertEquals( zoo.getMammals().size(), 1 );
 		assertEquals( zoo.getAnimals().size(), 1 );
 		s.clear();
 		s.delete(plat);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9305")
 	@SuppressWarnings( {"unchecked"})
 	public void testSelectClauseImplicitJoinOrderByJoinedProperty() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo( zoo );
 		plat.setSerialNumber( "plat123" );
 		zoo.getMammals().put( "Platypus", plat );
 		zoo.getAnimals().put("plat123", plat);
 		Zoo otherZoo = new Zoo();
 		otherZoo.setName("The Other Zoo");
 		otherZoo.setMammals( new HashMap() );
 		otherZoo.setAnimals( new HashMap() );
 		Mammal zebra = new Mammal();
 		zebra.setBodyWeight( 110f );
 		zebra.setDescription( "Zebra" );
 		zebra.setZoo( otherZoo );
 		zebra.setSerialNumber( "zebra123" );
 		otherZoo.getMammals().put( "Zebra", zebra );
 		otherZoo.getAnimals().put("zebra123", zebra);
 		Mammal elephant = new Mammal();
 		elephant.setBodyWeight( 550f );
 		elephant.setDescription( "Elephant" );
 		elephant.setZoo( otherZoo );
 		elephant.setSerialNumber( "elephant123" );
 		otherZoo.getMammals().put( "Elephant", elephant );
 		otherZoo.getAnimals().put( "elephant123", elephant );
 		s.persist( plat );
 		s.persist(zoo);
 		s.persist( zebra );
 		s.persist( elephant );
 		s.persist( otherZoo );
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select a.zoo from Animal a where a.zoo is not null order by a.zoo.name");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		List<Zoo> zoos = (List<Zoo>) q.list();
 		assertEquals( 3, zoos.size() );
 		assertEquals( otherZoo.getName(), zoos.get( 0 ).getName() );
 		assertEquals( 2, zoos.get( 0 ).getMammals().size() );
 		assertEquals( 2, zoos.get( 0 ).getAnimals().size() );
 		assertSame( zoos.get( 0 ), zoos.get( 1 ) );
 		assertEquals( zoo.getName(), zoos.get( 2 ).getName() );
 		assertEquals( 1, zoos.get( 2 ).getMammals().size() );
 		assertEquals( 1, zoos.get( 2 ).getAnimals().size() );
 		s.clear();
 		s.delete(plat);
 		s.delete( zebra );
 		s.delete( elephant );
 		s.delete(zoo);
 		s.delete( otherZoo );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testSelectClauseDistinctImplicitJoinOrderByJoinedProperty() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo( zoo );
 		plat.setSerialNumber( "plat123" );
 		zoo.getMammals().put( "Platypus", plat );
 		zoo.getAnimals().put("plat123", plat);
 		Zoo otherZoo = new Zoo();
 		otherZoo.setName("The Other Zoo");
 		otherZoo.setMammals( new HashMap() );
 		otherZoo.setAnimals( new HashMap() );
 		Mammal zebra = new Mammal();
 		zebra.setBodyWeight( 110f );
 		zebra.setDescription( "Zebra" );
 		zebra.setZoo( otherZoo );
 		zebra.setSerialNumber( "zebra123" );
 		otherZoo.getMammals().put( "Zebra", zebra );
 		otherZoo.getAnimals().put("zebra123", zebra);
 		Mammal elephant = new Mammal();
 		elephant.setBodyWeight( 550f );
 		elephant.setDescription( "Elephant" );
 		elephant.setZoo( otherZoo );
 		elephant.setSerialNumber( "elephant123" );
 		otherZoo.getMammals().put( "Elephant", elephant );
 		otherZoo.getAnimals().put( "elephant123", elephant );
 		s.persist( plat );
 		s.persist(zoo);
 		s.persist( zebra );
 		s.persist( elephant );
 		s.persist( otherZoo );
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select distinct a.zoo from Animal a where a.zoo is not null order by a.zoo.name");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		List<Zoo> zoos = (List<Zoo>) q.list();
 		assertEquals( 2, zoos.size() );
 		assertEquals( otherZoo.getName(), zoos.get( 0 ).getName() );
 		assertEquals( 2, zoos.get( 0 ).getMammals().size() );
 		assertEquals( 2, zoos.get( 0 ).getAnimals().size() );
 		assertEquals( zoo.getName(), zoos.get( 1 ).getName() );
 		assertEquals( 1, zoos.get( 1 ).getMammals().size() );
 		assertEquals( 1, zoos.get( 1 ).getAnimals().size() );
 		s.clear();
 		s.delete(plat);
 		s.delete( zebra );
 		s.delete( elephant );
 		s.delete(zoo);
 		s.delete( otherZoo );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testSelectClauseImplicitJoinWithIterate() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo(zoo);
 		plat.setSerialNumber("plat123");
 		zoo.getMammals().put("Platypus", plat);
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist(zoo);
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select distinct a.zoo from Animal a where a.zoo is not null");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		zoo = (Zoo) q
 			.iterate().next();
 		assertEquals( zoo.getMammals().size(), 1 );
 		assertEquals( zoo.getAnimals().size(), 1 );
 		s.clear();
 		s.delete(plat);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentOrderBy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Long id1 = ( Long ) s.save( genSimpleHuman( "John", "Jacob" ) );
 		Long id2 = ( Long ) s.save( genSimpleHuman( "Jingleheimer", "Schmidt" ) );
 
 		s.flush();
 
 		// the component is defined with the firstName column first...
 		List results = s.createQuery( "from Human as h order by h.name" ).list();
 		assertEquals( "Incorrect return count", 2, results.size() );
 
 		Human h1 = ( Human ) results.get( 0 );
 		Human h2 = ( Human ) results.get( 1 );
 
 		assertEquals( "Incorrect ordering", id2, h1.getId() );
 		assertEquals( "Incorrect ordering", id1, h2.getId() );
 
 		s.delete( h1 );
 		s.delete( h2 );
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOrderedWithCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		SimpleEntityWithAssociation first = new SimpleEntityWithAssociation();
 		first.setNegatedNumber( 1 );
 		s.save( first );
 		SimpleEntityWithAssociation second = new SimpleEntityWithAssociation();
 		second.setNegatedNumber(2);
 		s.save( second );
 		s.flush();
 
 		// Check order via SQL. Numbers are negated in the DB, so second comes first.
 		List listViaSql = s.createSQLQuery("select id from SIMPLE_1 order by negated_num").list();
 		assertEquals( 2, listViaSql.size() );
 		assertEquals( second.getId().longValue(), ((Number) listViaSql.get( 0 )).longValue() );
 		assertEquals( first.getId().longValue(), ((Number) listViaSql.get( 1 )).longValue() );
 
 		// Check order via HQL. Now first comes first b/c the read negates the DB negation.
 		List listViaHql = s.createQuery("from SimpleEntityWithAssociation order by negatedNumber").list();
 		assertEquals( 2, listViaHql.size() );
 		assertEquals(first.getId(), ((SimpleEntityWithAssociation)listViaHql.get(0)).getId());
 		assertEquals(second.getId(), ((SimpleEntityWithAssociation)listViaHql.get(1)).getId());
 
 		s.delete( first );
 		s.delete( second );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testHavingWithCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		SimpleEntityWithAssociation first = new SimpleEntityWithAssociation();
 		first.setNegatedNumber(5);
 		first.setName( "simple" );
 		s.save(first);
 		SimpleEntityWithAssociation second = new SimpleEntityWithAssociation();
 		second.setNegatedNumber( 10 );
 		second.setName("simple");
 		s.save(second);
 		SimpleEntityWithAssociation third = new SimpleEntityWithAssociation();
 		third.setNegatedNumber( 20 );
 		third.setName( "complex" );
 		s.save( third );
 		s.flush();
 
 		// Check order via HQL. Now first comes first b/c the read negates the DB negation.
 		Number r = (Number)s.createQuery("select sum(negatedNumber) from SimpleEntityWithAssociation " +
 				"group by name having sum(negatedNumber) < 20").uniqueResult();
 		assertEquals(r.intValue(), 15);
 
 		s.delete(first);
 		s.delete(second);
 		s.delete(third);
 		t.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testLoadSnapshotWithCustomColumnReadAndWrite() {
 		// Exercises entity snapshot load when select-before-update is true.
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		final double SIZE_IN_KB = 1536d;
 		final double SIZE_IN_MB = SIZE_IN_KB / 1024d;
 		Image image = new Image();
 		image.setName( "picture.gif" );
 		image.setSizeKb( SIZE_IN_KB );
 		s.persist( image );
 		s.flush();
 
 		// Value returned by Oracle is a Types.NUMERIC, which is mapped to a BigDecimalType;
 		// Cast returned value to Number then call Number.doubleValue() so it works on all dialects.
 		Double sizeViaSql = ( (Number)s.createSQLQuery("select size_mb from image").uniqueResult() ).doubleValue();
 		assertEquals(SIZE_IN_MB, sizeViaSql, 0.01d);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		final double NEW_SIZE_IN_KB = 2048d;
 		final double NEW_SIZE_IN_MB = NEW_SIZE_IN_KB / 1024d;
 		image.setSizeKb( NEW_SIZE_IN_KB );
 		s.update( image );
 		s.flush();
 
 		sizeViaSql = ( (Number)s.createSQLQuery("select size_mb from image").uniqueResult() ).doubleValue();
 		assertEquals(NEW_SIZE_IN_MB, sizeViaSql, 0.01d);
 
 		s.delete(image);
 		t.commit();
 		s.close();
 	}
 
 	private Human genSimpleHuman(String fName, String lName) {
 		Human h = new Human();
 		h.setName( new Name( fName, 'X', lName ) );
 
 		return h;
 	}
 
 	@Test
 	public void testCastInSelect() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 		Object bodyWeight = s.createQuery("select cast(bodyWeight as integer) from Animal").uniqueResult();
 		assertTrue( Integer.class.isInstance( bodyWeight ) );
 		assertEquals( 12, bodyWeight );
 
 		bodyWeight = s.createQuery("select cast(bodyWeight as big_decimal) from Animal").uniqueResult();
 		assertTrue( BigDecimal.class.isInstance( bodyWeight ) );
 		assertEquals( a.getBodyWeight(), ( (BigDecimal) bodyWeight ).floatValue(), .01 );
 
 		Object literal = s.createQuery("select cast(10000000 as big_integer) from Animal").uniqueResult();
 		assertTrue( BigInteger.class.isInstance( literal ) );
 		assertEquals( BigInteger.valueOf( 10000000 ), literal );
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNumericExpressionReturnTypes() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 
 		Object result;
 
 		// addition ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 + 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int + int", Integer.class.isInstance( result ) );
 		assertEquals( 2, result );
 
 		result = s.createQuery( "select 1 + 1L from Animal a" ).uniqueResult();
 		assertTrue( "int + long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int + BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1F from Animal a" ).uniqueResult();
 		assertTrue( "int + float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1D from Animal a" ).uniqueResult();
 		assertTrue( "int + double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int + BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1F + 1D from Animal a" ).uniqueResult();
 		assertTrue( "float + double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1F + 1BD from Animal a" ).uniqueResult();
 		assertTrue( "float + BigDecimal", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 2 ), result );
 
 		// subtraction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 - 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int - int", Integer.class.isInstance( result ) );
 		assertEquals( 0, result );
 
 		result = s.createQuery( "select 1 - 1L from Animal a" ).uniqueResult();
 		assertTrue( "int - long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int - BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1F from Animal a" ).uniqueResult();
 		assertTrue( "int - float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1D from Animal a" ).uniqueResult();
 		assertTrue( "int - double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int - BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1F - 1D from Animal a" ).uniqueResult();
 		assertTrue( "float - double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1F - 1BD from Animal a" ).uniqueResult();
 		assertTrue( "float - BigDecimal", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 0 ), result );
 
 		// multiplication ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 * 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int * int", Integer.class.isInstance( result ) );
 		assertEquals( 1, result );
 
 		result = s.createQuery( "select 1 * 1L from Animal a" ).uniqueResult();
 		assertTrue( "int * long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int * BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1F from Animal a" ).uniqueResult();
 		assertTrue( "int * float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1D from Animal a" ).uniqueResult();
 		assertTrue( "int * double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int * BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 1 ), result );
 
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testAliases() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 		String[] aliases1 = s.createQuery("select a.bodyWeight as abw, a.description from Animal a").getReturnAliases();
 		assertEquals( aliases1[0], "abw" );
 		assertEquals(aliases1[1], "1");
 		String[] aliases2 = s.createQuery("select count(*), avg(a.bodyWeight) as avg from Animal a").getReturnAliases();
 		assertEquals( aliases2[0], "0" );
 		assertEquals(aliases2[1], "avg");
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	@Test
     @SkipForDialect(
             value = CUBRIDDialect.class,
             comment = "As of version 8.4.1 CUBRID does not support temporary tables." +
                     " This test somehow calls MultiTableDeleteExecutor which raises an" +
                     " exception saying 'cannot doAfterTransactionCompletion multi-table" +
                     " deletes using dialect not supporting temp tables'."
     )
 	public void testParameterMixing() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery( "from Animal a where a.description = ? and a.bodyWeight = ? or a.bodyWeight = :bw" )
 				.setString( 0, "something" )
 				.setFloat( 1, 12345f )
 				.setFloat( "bw", 123f )
 				.list();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOrdinalParameters() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery( "from Animal a where a.description = ? and a.bodyWeight = ?" )
 				.setString( 0, "something" )
 				.setFloat( 1, 123f )
 				.list();
 		s.createQuery( "from Animal a where a.bodyWeight in (?, ?)" )
 				.setFloat( 0, 999f )
 				.setFloat( 1, 123f )
 				.list();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testIndexParams() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery( "from Zoo zoo where zoo.mammals[:name] = :id" )
 			.setParameter( "name", "Walrus" )
 			.setParameter( "id", Long.valueOf( 123 ) )
 			.list();
 		s.createQuery("from Zoo zoo where zoo.mammals[:name].bodyWeight > :w")
 			.setParameter("name", "Walrus")
 			.setParameter("w", new Float(123.32))
 			.list();
 		s.createQuery("from Zoo zoo where zoo.animals[:sn].mother.bodyWeight < :mw")
 			.setParameter("sn", "ant-123")
 			.setParameter("mw", new Float(23.32))
 			.list();
 		/*s.createQuery("from Zoo zoo where zoo.animals[:sn].description like :desc and zoo.animals[:sn].bodyWeight > :wmin and zoo.animals[:sn].bodyWeight < :wmax")
 			.setParameter("sn", "ant-123")
 			.setParameter("desc", "%big%")
 			.setParameter("wmin", new Float(123.32))
 			.setParameter("wmax", new Float(167.89))
 			.list();*/
 		/*s.createQuery("from Human where addresses[:type].city = :city and addresses[:type].country = :country")
 			.setParameter("type", "home")
 			.setParameter("city", "Melbourne")
 			.setParameter("country", "Australia")
 			.list();*/
 		t.commit();
 		s.close();
 	}
 
 	@Test
     @SkipForDialect( value = SybaseASE15Dialect.class, jiraKey = "HHH-6424")
 	public void testAggregation() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setBodyWeight( (float) 74.0 );
 		h.setHeightInches(120.5);
 		h.setDescription("Me");
 		h.setName( new Name("Gavin", 'A', "King") );
 		h.setNickName("Oney");
 		s.persist(h);
 		Double sum = (Double) s.createQuery("select sum(h.bodyWeight) from Human h").uniqueResult();
 		Double avg = (Double) s.createQuery("select avg(h.heightInches) from Human h").uniqueResult();	// uses custom read and write for column
 		assertEquals(sum.floatValue(), 74.0, 0.01);
 		assertEquals(avg.doubleValue(), 120.5, 0.01);
 		Long id = (Long) s.createQuery("select max(a.id) from Animal a").uniqueResult();
 		assertNotNull( id );
 		s.delete( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		h = new Human();
 		h.setFloatValue( 2.5F );
 		h.setIntValue( 1 );
 		s.persist( h );
 		Human h2 = new Human();
 		h2.setFloatValue( 2.5F );
 		h2.setIntValue( 2 );
 		s.persist( h2 );
 		Object[] results = (Object[]) s.createQuery( "select sum(h.floatValue), avg(h.floatValue), sum(h.intValue), avg(h.intValue) from Human h" )
 				.uniqueResult();
 		// spec says sum() on a float or double value should result in double
 		assertTrue( Double.class.isInstance( results[0] ) );
 		assertEquals( 5D, results[0] );
 		// avg() should return a double
 		assertTrue( Double.class.isInstance( results[1] ) );
 		assertEquals( 2.5D, results[1] );
 		// spec says sum() on short, int or long should result in long
 		assertTrue( Long.class.isInstance( results[2] ) );
 		assertEquals( 3L, results[2] );
 		// avg() should return a double
 		assertTrue( Double.class.isInstance( results[3] ) );
 		if (getDialect() instanceof SQLServer2008Dialect) assertEquals( 1.0D, results[3] );
 		else assertEquals( 1.5D, results[3] );
 		s.delete(h);
 		s.delete(h2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSelectClauseCase() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Human h = new Human();
 		h.setBodyWeight( (float) 74.0 );
 		h.setHeightInches( 120.5 );
 		h.setDescription("Me");
 		h.setName( new Name("Gavin", 'A', "King") );
 		h.setNickName("Oney");
 		s.persist(h);
 		String name = (String) s.createQuery("select case nickName when 'Oney' then 'gavin' when 'Turin' then 'christian' else nickName end from Human").uniqueResult();
 		assertEquals(name, "gavin");
 		String result = (String) s.createQuery("select case when bodyWeight > 100 then 'fat' else 'skinny' end from Human").uniqueResult();
 		assertEquals(result, "skinny");
 		s.delete(h);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(
 			value = IngresDialect.class,
 			jiraKey = "HHH-4976",
 			comment = "Ingres 9.3 does not support sub-selects in the select list"
 	)
 	public void testImplicitPolymorphism() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		s.save( product );
 
 		List list = s.createQuery("from java.lang.Comparable").list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createQuery("from java.lang.Object").list();
 		assertEquals( list.size(), 1 );
 
 		s.delete(product);
 
 		list = s.createQuery("from java.lang.Object").list();
 		assertEquals( list.size(), 0 );
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCoalesce() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("from Human h where coalesce(h.nickName, h.name.first, h.name.last) = 'max'").list();
 		session.createQuery("select nullif(nickName, '1e1') from Human").list();
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	public void testStr() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		Animal an = new Animal();
 		an.setBodyWeight(123.45f);
 		session.persist( an );
 		String str = (String) session.createQuery("select str(an.bodyWeight) from Animal an where str(an.bodyWeight) like '%1%'").uniqueResult();
-		if ( getDialect() instanceof DB2Dialect ) {
+		if ( getDialect() instanceof DB2Dialect || getDialect() instanceof TeradataDialect) {
 			assertTrue( str.startsWith("1.234") );
 		}
 		else //noinspection deprecation
 			if ( getDialect() instanceof SybaseDialect || getDialect() instanceof Sybase11Dialect || getDialect() instanceof SybaseASE15Dialect || getDialect() instanceof SybaseAnywhereDialect || getDialect() instanceof SQLServerDialect ) {
 			// str(val) on sybase assumes a default of 10 characters with no decimal point or decimal values
 			// str(val) on sybase result is right-justified
 			assertEquals( str.length(), 10 );
 			assertTrue( str.endsWith("123") );
 			str = (String) session.createQuery("select str(an.bodyWeight, 8, 3) from Animal an where str(an.bodyWeight, 8, 3) like '%1%'").uniqueResult();
 			assertEquals( str.length(), 8 );
 			assertTrue( str.endsWith( "123.450" ) );
 		}
 		else {
 			assertTrue( str.startsWith("123.4") );
 		}
 
 		//noinspection deprecation
-		if ( ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
+		if ( ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect || getDialect() instanceof TeradataDialect ) ) {
 			// In TransactSQL (the variant spoken by Sybase and SQLServer), the str() function
 			// is explicitly intended for numeric values only...
 			String dateStr1 = (String) session.createQuery("select str(current_date) from Animal").uniqueResult();
 			String dateStr2 = (String) session.createQuery("select str(year(current_date))||'-'||str(month(current_date))||'-'||str(day(current_date)) from Animal").uniqueResult();
 			System.out.println(dateStr1 + '=' + dateStr2);
 			if ( ! ( getDialect() instanceof Oracle8iDialect ) ) { //Oracle renders the name of the month :(
 				String[] dp1 = StringHelper.split("-", dateStr1);
 				String[] dp2 = StringHelper.split( "-", dateStr2 );
 				for (int i=0; i<3; i++) {
 					if ( dp1[i].startsWith( "0" ) ) {
 						dp1[i] = dp1[i].substring( 1 );
 					}
 					assertEquals( dp1[i], dp2[i] );
 				}
 			}
 		}
 		session.delete(an);
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SkipForDialect( value = { MySQLDialect.class, DB2Dialect.class } )
 	public void testCast() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("from Human h where h.nickName like 'G%'").list();
 		session.createQuery("from Animal a where cast(a.bodyWeight as string) like '1.%'").list();
 		session.createQuery("from Animal a where cast(a.bodyWeight as integer) = 1").list();
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	public void testExtract() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("select second(current_timestamp()), minute(current_timestamp()), hour(current_timestamp()) from Mammal m").list();
 		session.createQuery("select day(m.birthdate), month(m.birthdate), year(m.birthdate) from Mammal m").list();
 		if ( !(getDialect() instanceof DB2Dialect) ) { //no ANSI extract
 			session.createQuery("select extract(second from current_timestamp()), extract(minute from current_timestamp()), extract(hour from current_timestamp()) from Mammal m").list();
 			session.createQuery("select extract(day from m.birthdate), extract(month from m.birthdate), extract(year from m.birthdate) from Mammal m").list();
 		}
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SkipForDialect(
 			value = IngresDialect.class,
 			jiraKey = "HHH-4976",
 			comment = "Ingres 9.3 does not support sub-selects in the select list"
 	)
 	public void testOneToManyFilter() throws Throwable {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		session.save( product );
 
 		Customer customer = new Customer();
 		customer.setCustomerId( "123456789" );
 		customer.setName( "My customer" );
 		customer.setAddress( "somewhere" );
 		session.save( customer );
 
 		Order order = customer.generateNewOrder( new BigDecimal( 1234 ) );
 		session.save( order );
 
 		LineItem li = order.generateLineItem( product, 5 );
 		session.save( li );
 
 		session.flush();
 
 		assertEquals( session.createFilter( customer.getOrders(), "" ).list().size(), 1 );
 
 		assertEquals( session.createFilter( order.getLineItems(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( order.getLineItems(), "where this.quantity > :quantity" ).setInteger( "quantity", 5 ).list().size(), 0 );
 
 		session.delete(li);
 		session.delete(order);
 		session.delete(product);
 		session.delete(customer);
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testManyToManyFilter() throws Throwable {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Human human = new Human();
 		human.setName( new Name( "Steve", 'L', "Ebersole" ) );
 		session.save( human );
 
 		Human friend = new Human();
 		friend.setName( new Name( "John", 'Q', "Doe" ) );
 		friend.setBodyWeight( 11.0f );
 		session.save( friend );
 
 		human.setFriends( new ArrayList() );
 		friend.setFriends( new ArrayList() );
 		human.getFriends().add( friend );
 		friend.getFriends().add( human );
 
 		session.flush();
 
 		assertEquals( session.createFilter( human.getFriends(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.bodyWeight > ?" ).setFloat( 0, 10f ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.bodyWeight < ?" ).setFloat( 0, 10f ).list().size(), 0 );
 
 		session.delete(human);
 		session.delete(friend);
 
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testFilterWithCustomColumnReadAndWrite() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Human human = new Human();
 		human.setName( new Name( "Steve", 'L', "Ebersole" ) );
 		human.setHeightInches( 73d );
 		session.save( human );
 
 		Human friend = new Human();
 		friend.setName( new Name( "John", 'Q', "Doe" ) );
 		friend.setHeightInches( 50d );
 		session.save( friend );
 
 		human.setFriends( new ArrayList() );
 		friend.setFriends( new ArrayList() );
 		human.getFriends().add( friend );
 		friend.getFriends().add( human );
 
 		session.flush();
 
 		assertEquals( session.createFilter( human.getFriends(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches < ?" ).setDouble( 0, 51d ).list().size(), 1 );
 		assertEquals(
 				session.createFilter( human.getFriends(), "where this.heightInches > ?" )
 						.setDouble( 0, 51d )
 						.list()
 						.size(), 0
 		);
 		assertEquals(
 				session.createFilter( human.getFriends(), "where this.heightInches between 49 and 51" ).list().size(), 1
 		);
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches not between 49 and 51" ).list().size(), 0 );
 
 		session.delete( human );
 		session.delete( friend );
 
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnusedDeclaration"})
 	public void testSelectExpressions() {
 		createTestBaseData();
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Gavin", 'A', "King" ) );
 		h.setNickName("Oney");
 		h.setBodyWeight( 1.0f );
 		session.persist( h );
 		List results = session.createQuery("select 'found', lower(h.name.first) from Human h where lower(h.name.first) = 'gavin'").list();
 		results = session.createQuery("select 'found', lower(h.name.first) from Human h where concat(h.name.first, ' ', h.name.initial, ' ', h.name.last) = 'Gavin A King'").list();
 		results = session.createQuery("select 'found', lower(h.name.first) from Human h where h.name.first||' '||h.name.initial||' '||h.name.last = 'Gavin A King'").list();
 		results = session.createQuery("select a.bodyWeight + m.bodyWeight from Animal a join a.mother m").list();
 		results = session.createQuery("select 2.0 * (a.bodyWeight + m.bodyWeight) from Animal a join a.mother m").list();
 		results = session.createQuery("select sum(a.bodyWeight + m.bodyWeight) from Animal a join a.mother m").list();
 		results = session.createQuery("select sum(a.mother.bodyWeight * 2.0) from Animal a").list();
 		results = session.createQuery("select concat(h.name.first, ' ', h.name.initial, ' ', h.name.last) from Human h").list();
 		results = session.createQuery("select h.name.first||' '||h.name.initial||' '||h.name.last from Human h").list();
 		results = session.createQuery("select nickName from Human").list();
 		results = session.createQuery("select lower(nickName) from Human").list();
 		results = session.createQuery("select abs(bodyWeight*-1) from Human").list();
 		results = session.createQuery("select upper(h.name.first||' ('||h.nickName||')') from Human h").list();
 		results = session.createQuery("select abs(a.bodyWeight-:param) from Animal a").setParameter("param", new Float(2.0)).list();
 		results = session.createQuery("select abs(:param - a.bodyWeight) from Animal a").setParameter("param", new Float(2.0)).list();
 		results = session.createQuery("select lower(upper('foo')) from Animal").list();
 		results = session.createQuery("select lower(upper('foo') || upper('bar')) from Animal").list();
 		results = session.createQuery("select sum(abs(bodyWeight - 1.0) * abs(length('ffobar')-3)) from Animal").list();
 		session.delete(h);
 		txn.commit();
 		session.close();
 		destroyTestBaseData();
 	}
 
 	private void createTestBaseData() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Mammal m1 = new Mammal();
 		m1.setBodyWeight( 11f );
 		m1.setDescription( "Mammal #1" );
 
 		session.save( m1 );
 
 		Mammal m2 = new Mammal();
 		m2.setBodyWeight( 9f );
 		m2.setDescription( "Mammal #2" );
 		m2.setMother( m1 );
 
 		session.save( m2 );
 
 		txn.commit();
 		session.close();
 
 		createdAnimalIds.add( m1.getId() );
 		createdAnimalIds.add( m2.getId() );
 	}
 
 	private void destroyTestBaseData() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		for ( Long createdAnimalId : createdAnimalIds ) {
 			Animal animal = (Animal) session.load( Animal.class, createdAnimalId );
 			session.delete( animal );
 		}
 
 		txn.commit();
 		session.close();
 
 		createdAnimalIds.clear();
 	}
 
 	@Test
 	public void testImplicitJoin() throws Exception {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(0.5f);
 		a.setBodyWeight( 1.5f );
 		Animal b = new Animal();
 		Animal mother = new Animal();
 		mother.setBodyWeight(10.0f);
 		mother.addOffspring( a );
 		mother.addOffspring( b );
 		session.persist( a );
 		session.persist( b );
 		session.persist( mother );
 		List list = session.createQuery("from Animal a where a.mother.bodyWeight < 2.0 or a.mother.bodyWeight > 9.0").list();
 		assertEquals( list.size(), 2 );
 		list = session.createQuery("from Animal a where a.mother.bodyWeight > 2.0 and a.mother.bodyWeight > 9.0").list();
 		assertEquals( list.size(), 2 );
 		session.delete(b);
 		session.delete(a);
 		session.delete(mother);
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testFromOnly() throws Exception {
 		createTestBaseData();
 		Session session = openSession();
 		List results = session.createQuery( "from Animal" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		session.close();
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testSimpleSelect() throws Exception {
 		createTestBaseData();
 		Session session = openSession();
 		List results = session.createQuery( "select a from Animal as a" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		session.close();
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testEntityPropertySelect() throws Exception {
 		createTestBaseData();
 		Session session = openSession();
 		List results = session.createQuery( "select a.mother from Animal as a" ).list();
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		session.close();
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testWhere() throws Exception {
 		createTestBaseData();
 
 		Session session = openSession();
 		List results = session.createQuery( "from Animal an where an.bodyWeight > 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where not an.bodyWeight > 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where an.bodyWeight between 0 and 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where an.bodyWeight not between 0 and 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where sqrt(an.bodyWeight)/2 > 10" ).list();
 		assertEquals( "Incorrect result size", 0, results.size() );
 
 		results = session.createQuery( "from Animal an where (an.bodyWeight > 10 and an.bodyWeight < 100) or an.bodyWeight is null" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testEntityFetching() throws Exception {
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "from Animal an join fetch an.mother" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		Animal mother = ( ( Animal ) results.get( 0 ) ).getMother();
 		assertTrue( "fetch uninitialized", mother != null && Hibernate.isInitialized( mother ) );
 
 		results = session.createQuery( "select an from Animal an join fetch an.mother" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		mother = ( ( Animal ) results.get( 0 ) ).getMother();
 		assertTrue( "fetch uninitialized", mother != null && Hibernate.isInitialized( mother ) );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testCollectionFetching() throws Exception {
 		createTestBaseData();
 
 		Session session = openSession();
 		List results = session.createQuery( "from Animal an join fetch an.offspring" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		Collection os = ( ( Animal ) results.get( 0 ) ).getOffspring();
 		assertTrue( "fetch uninitialized", os != null && Hibernate.isInitialized( os ) && os.size() == 1 );
 
 		results = session.createQuery( "select an from Animal an join fetch an.offspring" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		os = ( ( Animal ) results.get( 0 ) ).getOffspring();
 		assertTrue( "fetch uninitialized", os != null && Hibernate.isInitialized( os ) && os.size() == 1 );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJoinFetchedCollectionOfJoinedSubclass() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "from Zoo z join fetch z.mammals" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Zoo );
 		Zoo zooRead = ( Zoo ) results.get( 0 );
 		assertEquals( zoo, zooRead );
 		assertTrue( Hibernate.isInitialized( zooRead.getMammals() ) );
 		Mammal mammalRead = ( Mammal ) zooRead.getMammals().get( "zebra" );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJoinedCollectionOfJoinedSubclass() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "from Zoo z join z.mammals m" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Object[] );
 		Object[] resultObjects = ( Object[] ) results.get( 0 );
 		Zoo zooRead = ( Zoo ) resultObjects[ 0 ];
 		Mammal mammalRead = ( Mammal ) resultObjects[ 1 ];
 		assertEquals( zoo, zooRead );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJoinedCollectionOfJoinedSubclassProjection() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "select z, m from Zoo z join z.mammals m" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Object[] );
 		Object[] resultObjects = ( Object[] ) results.get( 0 );
 		Zoo zooRead = ( Zoo ) resultObjects[ 0 ];
 		Mammal mammalRead = ( Mammal ) resultObjects[ 1 ];
 		assertEquals( zoo, zooRead );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	public void testProjectionQueries() throws Exception {
 		createTestBaseData();
 		Session session = openSession();
 		List results = session.createQuery( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id" ).list();
 		// mysql returns nulls in this group by
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Object[] );
 		assertEquals( "Incorrect return dimensions", 2, ( ( Object[] ) results.get( 0 ) ).length );
 		session.close();
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testStandardFunctions() throws Exception {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		Product p = new Product();
 		p.setDescription( "a product" );
 		p.setPrice( new BigDecimal( 1.0 ) );
 		p.setProductId( "abc123" );
 		session.persist(p);
 		Object[] result = (Object[]) session
 			.createQuery("select current_time(), current_date(), current_timestamp() from Product")
 			.uniqueResult();
 		assertTrue( result[0] instanceof Time );
 		assertTrue( result[1] instanceof Date );
 		assertTrue( result[2] instanceof Timestamp );
 		assertNotNull( result[0] );
 		assertNotNull( result[1] );
 		assertNotNull( result[2] );
 		session.delete(p);
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testDynamicInstantiationQueries() throws Exception {
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Animal.class );
 
 		Iterator iter = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		assertTrue( "Incorrect return type", iter.next() instanceof Animal );
 
 		results = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof List );
 		assertEquals( "Incorrect return type", ( (List) results.get( 0 ) ).size(), 2 );
 
 		results = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof List );
 		assertEquals( "Incorrect return type", ( (List) results.get( 0 ) ).size(), 2 );
 
 		iter = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		Object obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof List );
 		assertEquals( "Incorrect return type", ( (List) obj ).size(), 2 );
 
 		iter = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof List );
 		assertEquals( "Incorrect return type", ( (List) obj ).size(), 2 );
 
 		results = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) results.get( 0 ) ).size(), 2 );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("0") );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("1") );
 
 		results = session.createQuery( "select new map(an.description as descr, an.bodyWeight as bw) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) results.get( 0 ) ).size(), 2 );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("descr") );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("bw") );
 
 		iter = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) obj ).size(), 2 );
 
 		ScrollableResults sr = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		obj = sr.get(0);
 		assertTrue( "Incorrect return type", obj instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) obj ).size(), 2 );
 		sr.close();
 
 		sr = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Animal );
 		sr.close();
 
 		// caching...
 		QueryStatistics stats = sessionFactory().getStatistics().getQueryStatistics( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 		results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" )
 				.setCacheable( true )
 				.list();
 		assertEquals( "incorrect result size", 2, results.size() );
 		assertClassAssignability( Animal.class, results.get( 0 ).getClass() );
 		long initCacheHits = stats.getCacheHitCount();
 		results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" )
 				.setCacheable( true )
 				.list();
 		assertEquals( "dynamic intantiation query not served from cache", initCacheHits + 1, stats.getCacheHitCount() );
 		assertEquals( "incorrect result size", 2, results.size() );
 		assertClassAssignability( Animal.class, results.get( 0 ).getClass() );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9305")
 	public void testDynamicInstantiationWithToOneQueries() throws Exception {
 		final Employee employee1 = new Employee();
 		employee1.setFirstName( "Jane" );
 		employee1.setLastName( "Doe" );
 		final Title title1 = new Title();
 		title1.setDescription( "Jane's description" );
 		final Department dept1 = new Department();
 		dept1.setDeptName( "Jane's department" );
 		employee1.setTitle( title1 );
 		employee1.setDepartment( dept1 );
 
 		final Employee employee2 = new Employee();
 		employee2.setFirstName( "John" );
 		employee2.setLastName( "Doe" );
 		final Title title2 = new Title();
 		title2.setDescription( "John's title" );
 		employee2.setTitle( title2 );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( title1 );
 		s.persist( dept1 );
 		s.persist( employee1 );
 		s.persist( title2 );
 		s.persist( employee2 );
 		s.getTransaction().commit();
 		s.close();
 
 		// There are 2 to-one associations: Employee.title and Employee.department.
 		// It appears that adding an explicit join for one of these to-one associations keeps ANSI joins
 		// at the beginning of the FROM clause, avoiding failures on DBs that cannot handle cross joins
 		// interleaved with ANSI joins (e.g., PostgreSql).
 
 		s = openSession();
 		s.getTransaction().begin();
 		List results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e inner join e.title"
 		).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, t.id, t.description, e.department, e.firstName) from Employee e inner join e.title t"
 		).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e inner join e.department"
 		).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, d, e.firstName) from Employee e inner join e.department d"
 		).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e left outer join e.department"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, d, e.firstName) from Employee e left outer join e.department d"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e left outer join e.department inner join e.title"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, t.id, t.description, d, e.firstName) from Employee e left outer join e.department d inner join e.title t"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e left outer join e.department left outer join e.title"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, t.id, t.description, d, e.firstName) from Employee e left outer join e.department d left outer join e.title t"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e left outer join e.department order by e.title.description"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		results = session.createQuery(
 				"select new Employee(e.id, e.lastName, e.title.id, e.title.description, e.department, e.firstName) from Employee e left outer join e.department d order by e.title.description"
 		).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Employee.class );
 		s.getTransaction().commit();
 
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		s.delete( employee1 );
 		s.delete( title1 );
 		s.delete( dept1 );
 		s.delete( employee2 );
 		s.delete( title2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testCachedJoinedAndJoinFetchedManyToOne() throws Exception {
 		Animal a = new Animal();
 		a.setDescription( "an animal" );
 
 		Animal mother = new Animal();
 		mother.setDescription( "a mother" );
 		mother.addOffspring( a );
 		a.setMother( mother );
 
 		Animal offspring1 = new Animal();
 		offspring1.setDescription( "offspring1" );
 		a.addOffspring( offspring1 );
 		offspring1.setMother( a );
 
 		Animal offspring2 = new Animal();
 		offspring2.setDescription( "offspring2" );
 		a.addOffspring( offspring2 );
 		offspring2.setMother( a );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.save( mother );
 		s.save( a );
 		s.save( offspring1 );
 		s.save( offspring2 );
 		t.commit();
 		s.close();
 
 		sessionFactory().getCache().evictQueryRegions();
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List list = s.createQuery( "from Animal a left join fetch a.mother" ).setCacheable( true ).list();
 		assertEquals( 0, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "select a from Animal a left join fetch a.mother" ).setCacheable( true ).list();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "select a, m from Animal a left join a.mother m" ).setCacheable( true ).list();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 2, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "from Animal" ).list();
 		for(Object obj : list){
 			s.delete( obj );
 		}
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnusedDeclaration"})
 	public void testCachedJoinedAndJoinFetchedOneToMany() throws Exception {
 		Animal a = new Animal();
 		a.setDescription( "an animal" );
 		Animal mother = new Animal();
 		mother.setDescription( "a mother" );
 		mother.addOffspring( a );
 		a.setMother( mother );
 		Animal offspring1 = new Animal();
 		offspring1.setDescription( "offspring1" );
 		Animal offspring2 = new Animal();
 		offspring1.setDescription( "offspring2" );
 		a.addOffspring( offspring1 );
 		offspring1.setMother( a );
 		a.addOffspring( offspring2 );
 		offspring2.setMother( a );
 
 		sessionFactory().getCache().evictQueryRegions();
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.save( mother );
 		s.save( a );
 		s.save( offspring1 );
 		s.save( offspring2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List list = s.createQuery( "from Animal a left join fetch a.offspring" ).setCacheable( true ).list();
 		assertEquals( 0, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "select a from Animal a left join fetch a.offspring" ).setCacheable( true ).list();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "select a, o from Animal a left join a.offspring o" ).setCacheable( true ).list();
 		assertEquals( 1, sessionFactory().getStatistics().getQueryCacheHitCount() );
 		assertEquals( 2, sessionFactory().getStatistics().getQueryCachePutCount() );
 		list = s.createQuery( "from Animal" ).list();
 		for ( Object obj : list ) {
 			s.delete( obj );
 		}
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testIllegalMixedTransformerQueries() {
 		Session session = openSession();
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(QueryException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).iterate();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(HibernateException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).scroll();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(HibernateException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		session.close();
 	}
 
 	private Query getSelectNewQuery(Session session) {
 		return session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 	}
 
 	@Test
 	public void testResultTransformerScalarQueries() throws Exception {
 		createTestBaseData();
 
 		String query = "select an.description as description, an.bodyWeight as bodyWeight from Animal an order by bodyWeight desc";
 
 		Session session = openSession();
 
 		List results = session.createQuery( query )
 		.setResultTransformer(Transformers.aliasToBean(Animal.class)).list();
 		assertEquals( "Incorrect result size", results.size(), 2 );
 		assertTrue( "Incorrect return type", results.get(0) instanceof Animal );
 		Animal firstAnimal = (Animal) results.get(0);
 		Animal secondAnimal = (Animal) results.get(1);
 		assertEquals("Mammal #1", firstAnimal.getDescription());
 		assertEquals("Mammal #2", secondAnimal.getDescription());
 		assertFalse(session.contains(firstAnimal));
 		session.close();
 
 		session = openSession();
 
 		Iterator iter = session.createQuery( query )
 	     .setResultTransformer(Transformers.aliasToBean(Animal.class)).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		assertTrue( "Incorrect return type", iter.next() instanceof Animal );
 
 		session.close();
 
 		session = openSession();
 
 		ScrollableResults sr = session.createQuery( query )
 			     .setResultTransformer(Transformers.aliasToBean(Animal.class)).scroll();
 
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Animal );
 		assertFalse( session.contains( sr.get( 0 ) ) );
 		sr.close();
 
 		session.close();
 
 		session = openSession();
 
 		results = session.createQuery( "select a from Animal a, Animal b order by a.id" )
 				.setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE )
 				.list();
 		assertEquals( "Incorrect result size", 2, results.size());
 		assertTrue( "Incorrect return type", results.get(0) instanceof Animal );
 		firstAnimal = (Animal) results.get(0);
 		secondAnimal = (Animal) results.get(1);
 		assertEquals( "Mammal #1", firstAnimal.getDescription() );
 		assertEquals( "Mammal #2", secondAnimal.getDescription() );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testResultTransformerEntityQueries() throws Exception {
 		createTestBaseData();
 
 		String query = "select an as an from Animal an order by bodyWeight desc";
 
 		Session session = openSession();
 
 		List results = session.createQuery( query )
 		.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
 		assertEquals( "Incorrect result size", results.size(), 2 );
 		assertTrue( "Incorrect return type", results.get(0) instanceof Map );
 		Map map = ((Map) results.get(0));
 		assertEquals(1, map.size());
 		Animal firstAnimal = (Animal) map.get("an");
 		map = ((Map) results.get(1));
 		Animal secondAnimal = (Animal) map.get("an");
 		assertEquals( "Mammal #1", firstAnimal.getDescription() );
 		assertEquals("Mammal #2", secondAnimal.getDescription());
 		assertTrue(session.contains(firstAnimal));
 		assertSame( firstAnimal, session.get( Animal.class, firstAnimal.getId() ) );
 		session.close();
 
 		session = openSession();
 
 		Iterator iter = session.createQuery( query )
 	     .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		map = (Map) iter.next();
 		firstAnimal = (Animal) map.get("an");
 		assertEquals( "Mammal #1", firstAnimal.getDescription() );
 		assertTrue( "Incorrect result size", iter.hasNext() );
 
 		session.close();
 
 		session = openSession();
 
 		ScrollableResults sr = session.createQuery( query )
 				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).scroll();
 
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Map );
 		assertFalse( session.contains( sr.get( 0 ) ) );
 		sr.close();
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	@Test
 	public void testEJBQLFunctions() throws Exception {
 		Session session = openSession();
 
 		String hql = "from Animal a where a.description = concat('1', concat('2','3'), '4'||'5')||'0'";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where substring(a.description, 1, 3) = 'cat'";
 		session.createQuery(hql).list();
 
 		hql = "select substring(a.description, 1, 3) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where lower(a.description) = 'cat'";
 		session.createQuery(hql).list();
 
 		hql = "select lower(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where upper(a.description) = 'CAT'";
 		session.createQuery(hql).list();
 
 		hql = "select upper(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where length(a.description) = 5";
 		session.createQuery(hql).list();
 
 		hql = "select length(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		//note: postgres and db2 don't have a 3-arg form, it gets transformed to 2-args
 		hql = "from Animal a where locate('abc', a.description, 2) = 2";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where locate('abc', a.description) = 2";
 		session.createQuery(hql).list();
 
 		hql = "select locate('cat', a.description, 2) from Animal a";
 		session.createQuery(hql).list();
 
 		if ( !( getDialect() instanceof DB2Dialect ) ) {
 			hql = "from Animal a where trim(trailing '_' from a.description) = 'cat'";
 			session.createQuery(hql).list();
 
 			hql = "select trim(trailing '_' from a.description) from Animal a";
 			session.createQuery(hql).list();
 
 			hql = "from Animal a where trim(leading '_' from a.description) = 'cat'";
 			session.createQuery(hql).list();
 
 			hql = "from Animal a where trim(both from a.description) = 'cat'";
 			session.createQuery(hql).list();
 		}
 
 		if ( !(getDialect() instanceof HSQLDialect) ) { //HSQL doesn't like trim() without specification
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/Animal.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/hql/Animal.hbm.xml
index 2519bfc421..a680c96415 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/Animal.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/Animal.hbm.xml
@@ -1,159 +1,159 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping SYSTEM "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
 <hibernate-mapping
 	package="org.hibernate.test.hql"
 	default-access="field">
 
 	<class name="Animal">
 		<id name="id">
 			<generator class="native"/>
 		</id>
 		<property name="description"/>
 		<property name="bodyWeight" column="body_weight"/>
 		<many-to-one name="mother" column="mother_id"/>
 		<many-to-one name="father" column="father_id"/>
 		<many-to-one name="zoo" column="zoo_id"/>
 		<property name="serialNumber"/>
 		<set name="offspring" order-by="father_id">
 			<key column="mother_id"/>
 			<one-to-many class="Animal"/>
 		</set>
 		<joined-subclass name="Reptile">
 			<key column="animal"/>
 			<property name="bodyTemperature"/>
 			<joined-subclass name="Lizard">
 				<key column="reptile"/>
 			</joined-subclass>
 		</joined-subclass>
 		<joined-subclass name="Mammal">
 			<key column="animal"/>
 			<property name="pregnant"/>
 			<property name="birthdate" type="date"/>
 			<joined-subclass name="DomesticAnimal">
 				<key column="mammal"/>
 				<many-to-one name="owner"/>
 				<joined-subclass name="Cat">
 					<key column="mammal"/>
 				</joined-subclass>
 				<joined-subclass name="Dog">
 					<key column="mammal"/>
 				</joined-subclass>
 			</joined-subclass>
 			<joined-subclass name="Human">
 				<key column="mammal"/>
 				<component name="name">
 					<property name="first" column="name_first"/>
 					<property name="initial" column="name_initial"/>
 					<property name="last" column="name_last"/>
 				</component>
 				<property name="nickName"/>
 				<property name="heightInches">
 					<column name="height_centimeters" 
 						not-null="true" 
 						read="height_centimeters / 2.54E0"
 						write="? * 2.54E0"/>
 				</property>   
 				<property name="intValue"/>
 				<property name="floatValue"/>
 				<property name="bigDecimalValue"/>
 				<property name="bigIntegerValue"/>
 
 				<bag name="friends">
 					<key column="human1"/>
 					<many-to-many column="human2" class="Human"/>
 				</bag>
 				<map name="family">
 					<key column="human1"/>
 					<map-key column="relationship" type="string"/>
 					<many-to-many column="human2" class="Human"/>
 				</map>
 				<bag name="pets" inverse="true">
 					<key column="owner"/>
 					<one-to-many class="DomesticAnimal"/>
 				</bag>
 				<set name="nickNames" lazy="false" table="human_nick_names" sort="natural">
 					<key column="human"/>
 					<element column="nick_name" type="string" not-null="true"/>
 				</set>
 				<map name="addresses" table="addresses">
 					<key column="human"/>
 					<map-key type="string" column="`type`"/>
 					<composite-element class="Address">
 						<property name="street"/>
 						<property name="city"/>
 						<property name="postalCode"/>
 						<property name="country"/>
                         <many-to-one name="stateProvince" column="state_prov_id" class="StateProvince"/>
 					</composite-element>
 				</map>
 			</joined-subclass>
 		</joined-subclass>
 	</class>
 
 	<class name="User" table="`User`">
 		<id name="id">
 			<generator class="foreign">
 				<param name="property">human</param>
 			</generator>
 		</id>
 		<property name="userName"/>
 		<one-to-one name="human" constrained="true"/>
 		<list name="permissions">
 			<key column="userId"/>
 			<list-index column="permissionId"/>
 			<element type="string" column="permissionName"/>
 		</list>
 	</class>
 
 	<class name="Zoo" discriminator-value="Z">
 		<id name="id">
 			<generator class="native"/>
 		</id>
 		<discriminator column="zooType" type="character"/>
 		<property name="name" type="string"/>
         <property name="classification" type="org.hibernate.test.hql.ClassificationType"/>
         <map name="directors">
 			<key column="directorZoo_id"/>
-			<index type="string" column="title"/>
+			<index type="string" column="`title`"/>
 			<many-to-many class="Human"/>
 		</map>
         <map name="mammals">
 			<key column="mammalZoo_id"/>
 			<index type="string" column="name"/>
 			<one-to-many class="Mammal"/>
 		</map>
 		<map name="animals" inverse="true">
 			<key column="zoo_id"/>
 			<index type="string" column="serialNumber"/>
 			<one-to-many class="Animal"/>
 		</map>
         <component name="address" class="Address">
             <property name="street"/>
             <property name="city"/>
             <property name="postalCode"/>
             <property name="country"/>
             <many-to-one name="stateProvince" column="state_prov_id" class="StateProvince"/>
         </component>
 		<subclass name="PettingZoo" discriminator-value="P"/>
 	</class>
 
     <class name="StateProvince">
         <id name="id">
             <generator class="native"/>
         </id>
         <property name="name"/>
         <property name="isoCode"/>
     </class>
 
 	<class name="Joiner">
 		<id name="id">
 			<generator class="native"/>
 		</id>
 		<property name="name"/>
 		<join table="JOINED">
 			<key column="ID"/>
 			<property name="joinedName"/>
 		</join>
 	</class>
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNamesAsColumns.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNamesAsColumns.hbm.xml
index 85fa4019d3..12005342d1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNamesAsColumns.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNamesAsColumns.hbm.xml
@@ -1,39 +1,39 @@
 <?xml version="1.0"?>
 
 <!DOCTYPE hibernate-mapping SYSTEM "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
 
 <hibernate-mapping package="org.hibernate.test.hql">
 
     <class name="EntityWithFunctionAsColumnHolder" table="ENTITY_WITH_FN_AS_COL_HOLDER">
         <id name="id" column="ID" type="long">
             <generator class="increment"/>
         </id>
         <many-to-one name="nextHolder" cascade="all"/>
         <set name="entityWithArgFunctionAsColumns" inverse="false" lazy="true" cascade="all-delete-orphan"
              order-by="lower,lower( upper )">
             <key column="HOLDER_ID"/>
             <one-to-many class="EntityWithArgFunctionAsColumn"/>
         </set>
         <set name="entityWithNoArgFunctionAsColumns" inverse="false" lazy="true" cascade="all-delete-orphan"
                 order-by="current_date, `current_date`">
             <key column="HOLDER_ID"/>
             <one-to-many class="EntityWithNoArgFunctionAsColumn"/>
         </set>
     </class>
 
     <class name="EntityWithArgFunctionAsColumn" table="ENTITY_WITH_ARG_FN_AS_COL">
         <id name="id" column="ID" type="long">
             <generator class="increment"/>
         </id>
-        <property name="lower" column="lower" type="int"/>
-        <property name="upper" column="upper" type="string"/>
+        <property name="lower" column="`lower`" type="int"/>
+        <property name="upper" column="`upper`" type="string"/>
     </class>
 
     <class name="EntityWithNoArgFunctionAsColumn" table="ENTITY_WITH_NOARG_FN_AS_COL">
         <id name="id" column="ID" type="long">
             <generator class="increment"/>
         </id>
         <property name="currentDate" column="`current_date`" type="string"/>
     </class>
 
 </hibernate-mapping>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
index 8705bc3819..78484091a1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
@@ -1,1548 +1,1550 @@
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
 package org.hibernate.test.hql;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 import org.junit.Test;
 
 import org.hibernate.QueryException;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
 import org.hibernate.hql.internal.ast.DetailedSemanticException;
 import org.hibernate.hql.internal.ast.QuerySyntaxException;
 import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.tree.ConstructorNode;
 import org.hibernate.hql.internal.ast.tree.DotNode;
 import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
 import org.hibernate.hql.internal.ast.tree.IndexNode;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.type.CalendarDateType;
 import org.hibernate.type.DoubleType;
 import org.hibernate.type.StringType;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests cases where the AST based query translator and the 'classic' query translator generate identical SQL.
  *
  * @author Gavin King
  */
 public class HQLTest extends QueryTranslatorTestCase {
 	@Override
 	public boolean createSchema() {
 		return false;
 	}
 
 	@Override
 	public boolean rebuildSessionFactoryOnError() {
 		return false;
 	}
 
 	@Override
 	protected void prepareTest() throws Exception {
 		super.prepareTest();
 		SelectClause.VERSION2_SQL = true;
 		DotNode.regressionStyleJoinSuppression = true;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = new DotNode.IllegalCollectionDereferenceExceptionBuilder() {
 			public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 				throw new QueryException( "illegal syntax near collection: " + propertyName );
 			}
 		};
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = true;
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		SelectClause.VERSION2_SQL = false;
 		DotNode.regressionStyleJoinSuppression = false;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = DotNode.DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = false;
 		super.cleanupTest();
 	}
 
 	@Test
 	public void testModulo() {
 		assertTranslation( "from Animal a where a.bodyWeight % 2 = 0" );
 	}
 
 	@Test
 	public void testInvalidCollectionDereferencesFail() {
 		// should fail with the same exceptions (because of the DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER injection)
 		assertTranslation( "from Animal a where a.offspring.description = 'xyz'" );
 		assertTranslation( "from Animal a where a.offspring.father.description = 'xyz'" );
 	}
 	
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
     public void testRowValueConstructorSyntaxInInList2() {
         assertTranslation( "from LineItem l where l.id in (:idList)" );
 		assertTranslation( "from LineItem l where l.id in :idList" );
     }
 
 	@Test
 	@SkipForDialect( value = { Oracle8iDialect.class, AbstractHANADialect.class } )
     public void testRowValueConstructorSyntaxInInListBeingTranslated() {
 		QueryTranslatorImpl translator = createNewQueryTranslator("from LineItem l where l.id in (?)");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (('a1',1,'b1'),('a2',2,'b2'))");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in (?)");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in ?");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (select a1 from Animal a1 left join a1.offspring o where a1.id = 1)");
 		assertInExist("do not translate sub-queries", true, translator);
     }
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsRowValueConstructorSyntaxInInListCheck.class )
     public void testRowValueConstructorSyntaxInInList() {
 		QueryTranslatorImpl translator = createNewQueryTranslator("from LineItem l where l.id in (?)");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (('a1',1,'b1'),('a2',2,'b2'))");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true,translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in (?)");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in ?");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (select a1 from Animal a1 left join a1.offspring o where a1.id = 1)");
 		assertInExist("do not translate sub-queries", true, translator);
     }
 
 	private void assertInExist( String message, boolean expected, QueryTranslatorImpl translator ) {
 		AST ast = translator.getSqlAST().getWalker().getAST();
 		QueryNode queryNode = (QueryNode) ast;
 		AST whereNode = ASTUtil.findTypeInChildren( queryNode, HqlTokenTypes.WHERE );
 		AST inNode = whereNode.getFirstChild();
 		assertEquals( message, expected, inNode != null && inNode.getType() == HqlTokenTypes.IN );
 	}
     
 	@Test
 	public void testSubComponentReferences() {
 		assertTranslation( "select c.address.zip.code from ComponentContainer c" );
 		assertTranslation( "select c.address.zip from ComponentContainer c" );
 		assertTranslation( "select c.address from ComponentContainer c" );
 	}
 
 	@Test
 	public void testManyToAnyReferences() {
 		assertTranslation( "from PropertySet p where p.someSpecificProperty.id is not null" );
 		assertTranslation( "from PropertySet p join p.generalProperties gp where gp.id is not null" );
 	}
 
 	@Test
 	public void testJoinFetchCollectionOfValues() {
 		assertTranslation( "select h from Human as h join fetch h.nickNames" );
 	}
 	
 	@Test
 	public void testCollectionMemberDeclarations2() {
 		assertTranslation( "from Customer c, in(c.orders) o" );
 		assertTranslation( "from Customer c, in(c.orders) as o" );
 		assertTranslation( "select c.name from Customer c, in(c.orders) as o where c.id = o.id.customerId" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testCollectionMemberDeclarations(){
 		// both these two query translators throw exeptions for this HQL since
 		// IN asks an alias, but the difference is that the error message from AST
 		// contains the error token location (by lines and columns), which is hardly 
 		// to get from Classic query translator --stliu
 		assertTranslation( "from Customer c, in(c.orders)" ); 
 	}
 
 	@Test
 	public void testCollectionJoinsInSubselect() {
 		// caused by some goofiness in FromElementFactory that tries to
 		// handle correlated subqueries (but fails miserably) even though this
 		// is not a correlated subquery.  HHH-1248
 		assertTranslation(
 				"select a.id, a.description" +
 				" from Animal a" +
 				"       left join a.offspring" +
 				" where a in (" +
 				"       select a1 from Animal a1" +
 				"           left join a1.offspring o" +
 				"       where a1.id=1" +
 		        ")"
 		);
 		assertTranslation(
 				"select h.id, h.description" +
 		        " from Human h" +
 				"      left join h.friends" +
 				" where h in (" +
 				"      select h1" +
 				"      from Human h1" +
 				"          left join h1.friends f" +
 				"      where h1.id=1" +
 				")"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2045" )
 	public void testEmptyInList() {
 		assertTranslation( "select a from Animal a where a.description in ()" );
 	}
 
 	@Test
 	public void testDateTimeArithmeticReturnTypesAndParameterGuessing() {
 		QueryTranslatorImpl translator = createNewQueryTranslator( "select o.orderDate - o.orderDate from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate + 2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate -2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 
 		translator = createNewQueryTranslator( "from Order o where o.orderDate > ?" );
 		assertEquals( "incorrect expected param type", CalendarDateType.INSTANCE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 		translator = createNewQueryTranslator( "select o.orderDate + ? from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 		assertEquals( "incorrect expected param type", DoubleType.INSTANCE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 	}
 
 	@Test
 	public void testReturnMetadata() {
 		HQLQueryPlan plan = createQueryPlan( "from Animal a" );
 		check( plan.getReturnMetadata(), false, true );
 
 		plan = createQueryPlan( "select a as animal from Animal a" );
 		check( plan.getReturnMetadata(), false, false );
 
 		plan = createQueryPlan( "from java.lang.Object" );
 		check( plan.getReturnMetadata(), true, true );
 
 		plan = createQueryPlan( "select o as entity from java.lang.Object o" );
 		check( plan.getReturnMetadata(), true, false );
 	}
 
 	private void check(
 			ReturnMetadata returnMetadata,
 	        boolean expectingEmptyTypes,
 	        boolean expectingEmptyAliases) {
 		assertNotNull( "null return metadata", returnMetadata );
 		assertNotNull( "null return metadata - types", returnMetadata );
 		assertEquals( "unexpected return size", 1, returnMetadata.getReturnTypes().length );
 		if ( expectingEmptyTypes ) {
 			assertNull( "non-empty types", returnMetadata.getReturnTypes()[0] );
 		}
 		else {
 			assertNotNull( "empty types", returnMetadata.getReturnTypes()[0] );
 		}
 		if ( expectingEmptyAliases ) {
 			assertNull( "non-empty aliases", returnMetadata.getReturnAliases() );
 		}
 		else {
 			assertNotNull( "empty aliases", returnMetadata.getReturnAliases() );
 			assertNotNull( "empty aliases", returnMetadata.getReturnAliases()[0] );
 		}
 	}
 
 	@Test
 	public void testImplicitJoinsAlongWithCartesianProduct() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo, Foo foo2" );
 		assertTranslation( "select foo.foo.foo from Foo foo, Foo foo2" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSubselectBetween() {
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) between :min and :max");
 		assertTranslation("from Animal x where (select max(a.description) from Animal a) like 'big%'");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) is not null");
 		assertTranslation("from Animal x where exists (select max(a.bodyWeight) from Animal a)");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) in (1,2,3)");
 	}
 
 	@Test
 	public void testFetchOrderBy() {
 		assertTranslation("from Animal a left outer join fetch a.offspring where a.mother.id = :mid order by a.description");
 	}
 
 	@Test
 	public void testCollectionOrderBy() {
 		assertTranslation("from Animal a join a.offspring o order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring o order by o.description");
 		assertTranslation("from Animal a join a.offspring o order by a.description, o.description");
 	}
 
 	@Test
 	public void testExpressionWithParamInFunction() {
 		assertTranslation("from Animal a where abs(a.bodyWeight-:param) < 2.0");
 		assertTranslation("from Animal a where abs(:param - a.bodyWeight) < 2.0");
 		assertTranslation("from Animal where abs(:x - :y) < 2.0");
 		assertTranslation("from Animal where lower(upper(:foo)) like 'f%'");
-		if ( ! ( getDialect() instanceof SybaseDialect ) &&  ! ( getDialect() instanceof Sybase11Dialect ) &&  ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
+		if ( ! ( getDialect() instanceof SybaseDialect ) &&  ! ( getDialect() instanceof Sybase11Dialect ) &&  ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SQLServerDialect ) && ! ( getDialect() instanceof TeradataDialect ) ) {
 			// Transact-SQL dialects (except SybaseAnywhereDialect) map the length function -> len; 
 			// classic translator does not consider that *when nested*;
 			// SybaseAnywhereDialect supports the length function
 
 			assertTranslation("from Animal a where abs(abs(a.bodyWeight - 1.0 + :param) * abs(length('ffobar')-3)) = 3.0");
 		}
-		if ( !( getDialect() instanceof MySQLDialect ) && ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && !( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
+		if ( !( getDialect() instanceof MySQLDialect ) && ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && !( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect ) && ! ( getDialect() instanceof TeradataDialect ) ) {
 			assertTranslation("from Animal where lower(upper('foo') || upper(:bar)) like 'f%'");
 		}
-		if ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect ) {
+		if ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect || getDialect() instanceof TeradataDialect) {
 			return;
 		}
 		if ( getDialect() instanceof AbstractHANADialect ) {
 			// HANA returns
 			// ...al0_7_.mammal where [abs(cast(1 as float(19))-cast(? as float(19)))=1.0]
 			return;
 		}
 		assertTranslation("from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0");
 	}
 
 	@Test
 	public void testCompositeKeysWithPropertyNamedId() {
 		assertTranslation( "select e.id.id from EntityWithCrazyCompositeKey e" );
 		assertTranslation( "select max(e.id.id) from EntityWithCrazyCompositeKey e" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testMaxindexHqlFunctionInElementAccessor() {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - this is perfect SQL, but fairly different from the old parser
 		//              tested : HSQLDB (1.8), Oracle8i
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" );
 		assertTranslation( "select c from Container c where c.manyToMany[ maxIndex(c.manyToMany) ].count = 2" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testMultipleElementAccessorOperators() throws Exception {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - Yes, this is all hosed ;)
 		assertTranslation( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" );
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Parser output mismatch" )
 	public void testKeyManyToOneJoin() {
 		//TODO: new parser generates unnecessary joins (though the query results are correct)
 		assertTranslation( "from Order o left join fetch o.lineItems li left join fetch li.product p" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Parser output mismatch" )
 	public void testDuplicateExplicitJoin() throws Exception {
 		//very minor issue with select clause:
 		assertTranslation( "from Animal a join a.mother m1 join a.mother m2" );
 		assertTranslation( "from Zoo zoo join zoo.animals an join zoo.mammals m" );
 		assertTranslation( "from Zoo zoo join zoo.mammals an join zoo.mammals m" );
 	}
 
 	@Test
 	public void testIndexWithExplicitJoin() throws Exception {
 		//TODO: broken on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo join zoo.animals an where zoo.mammals[ index(an) ] = an" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals[ index(dog) ] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where dog = zoo.mammals[ index(dog) ]" );
 	}
 
 	@Test
 	public void testOneToManyMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.id = 1234" );
 		assertTranslation( "from Zoo zoo where zoo.animals['1234'].description like '%black%'" );
 	}
 
 	@Test
 	public void testExplicitJoinMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo, Dog dog where zoo.mammals['dog'] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals['dog'] = dog" );
 	}
 
 	@Test
 	public void testIndexFunction() throws Exception {
 		// Instead of doing the pre-processor trick like the existing QueryTranslator, this
 		// is handled by MethodNode.
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		//TODO: broken on dialects with theta-style outerjoins:
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where index(dog) = 'dog'" );
 		assertTranslation( "from Zoo zoo join zoo.animals an where index(an) = '1234'" );
 	}
 
 	@Test
 	public void testSelectCollectionOfValues() throws Exception {
 		//TODO: broken on dialects with theta-style joins
 		///old parser had a bug where the collection element was not included in return types!
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "select baz, date from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
 	@Test
 	public void testCollectionOfValues() throws Exception {
 		//old parser had a bug where the collection element was not returned!
 		//TODO: broken on dialects with theta-style joins
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-719" )
     public void testHHH719() throws Exception {
         assertTranslation("from Baz b order by org.bazco.SpecialFunction(b.id)");
         assertTranslation("from Baz b order by anypackage.anyFunction(b.id)");
     }
 
 	@Test
 	public void testParameterListExpansion() {
 		assertTranslation( "from Animal as animal where animal.id in (:idList_1, :idList_2)" );
 	}
 
 	@Test
 	public void testComponentManyToOneDereferenceShortcut() {
 		assertTranslation( "from Zoo z where z.address.stateProvince.id is null" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-770" )
 	public void testNestedCollectionImplicitJoins() {
 		assertTranslation( "select h.friends.offspring from Human h" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-557" )
 	public void testExplicitJoinsInSubquery() {
 		assertTranslation(
 		        "from org.hibernate.test.hql.Animal as animal " +
 		        "where animal.id in (" +
 		        "        select a.id " +
 		        "        from org.hibernate.test.hql.Animal as a " +
 		        "               left join a.mother as mo" +
 		        ")"
 		);
 	}
 
 	@Test
 	public void testImplicitJoinsInGroupBy() {
 		assertTranslation(
 		        "select o.mother.bodyWeight, count(distinct o) " +
 		        "from Animal an " +
 		        "   join an.offspring as o " +
 		        "group by o.mother.bodyWeight"
 		);
 	}
 
 	@Test
 	public void testCrazyIdFieldNames() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		// only regress against non-scalar forms as there appears to be a bug in the classic translator
 		// in regards to this issue also.  Specifically, it interprets the wrong return type, though it gets
 		// the sql "correct" :/
 
 		String hql = "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, null );
 
 	    hql = "select e.heresAnotherCrazyIdFieldName.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, null );
 
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSizeFunctionAndProperty() {
 		assertTranslation("from Animal a where a.offspring.size > 0");
 		assertTranslation("from Animal a join a.offspring where a.offspring.size > 1");
 		assertTranslation("from Animal a where size(a.offspring) > 0");
 		assertTranslation("from Animal a join a.offspring o where size(a.offspring) > 1");
 		assertTranslation("from Animal a where size(a.offspring) > 1 and size(a.offspring) < 100");
 
 		assertTranslation("from Human a where a.family.size > 0");
 		assertTranslation("from Human a join a.family where a.family.size > 1");
 		assertTranslation("from Human a where size(a.family) > 0");
 		assertTranslation("from Human a join a.family o where size(a.family) > 1");
 		assertTranslation("from Human a where a.family.size > 0 and a.family.size < 100");
 	}
 
 	@Test
 	public void testFromOnly() throws Exception {
 		// 2004-06-21 [jsd] This test now works with the new AST based QueryTranslatorImpl.
 		assertTranslation( "from Animal" );
 		assertTranslation( "from Model" );
 	}
 
 	@Test
 	public void testJoinPathEndingInValueCollection() {
 		assertTranslation( "select h from Human as h join h.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-242" )
 	public void testSerialJoinPathEndingInValueCollection() {
 		assertTranslation( "select h from Human as h join h.friends as f join f.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-281" )
 	public void testImplicitJoinContainedByCollectionFunction() {
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.addresses)" );
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.father.addresses)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.nickNames)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.father.nickNames)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-276" )
 	public void testImpliedJoinInSubselectFrom() {
 		assertTranslation( "from Animal a where exists( from a.mother.offspring )" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-276" )
 	public void testSubselectImplicitJoins() {
 		assertTranslation( "from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count )" );
 	}
 
 	@Test
 	public void testCollectionOfValuesSize() throws Exception {
 		//SQL *was* missing a comma
 		assertTranslation( "select size(baz.stringDateMap) from org.hibernate.test.legacy.Baz baz" );
 	}
 
 	@Test
 	public void testCollectionFunctions() throws Exception {
 		//these are both broken, a join that belongs in the subselect finds its way into the main query
 		assertTranslation( "from Zoo zoo where size(zoo.animals) > 100" );
 		assertTranslation( "from Zoo zoo where maxindex(zoo.mammals) = 'dog'" );
 	}
 
 	@Test
 	public void testImplicitJoinInExplicitJoin() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother ggm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother.mother gggm" );
 	}
 
 	@Test
 	public void testImpliedManyToManyProperty() throws Exception {
 		//missing a table join (SQL correct for a one-to-many, not for a many-to-many)
 		assertTranslation( "select c from ContainerX c where c.manyToMany[0].name = 's'" );
 	}
 
 	@Test
 	public void testCollectionSize() throws Exception {
 		assertTranslation( "select size(zoo.animals) from Zoo zoo" );
 	}
 
 	@Test
 	public void testFetchCollectionOfValues() throws Exception {
 		assertTranslation( "from Baz baz left join fetch baz.stringSet" );
 	}
 
 	@Test
 	public void testFetchList() throws Exception {
 		assertTranslation( "from User u join fetch u.permissions" );
 	}
 
 	@Test
 	public void testCollectionFetchWithExplicitThetaJoin() {
 		assertTranslation( "select m from Master m1, Master m left join fetch m.details where m.name=m1.name" );
 	}
 
 	@Test
 	public void testListElementFunctionInWhere() throws Exception {
 		assertTranslation( "from User u where 'read' in elements(u.permissions)" );
 		assertTranslation( "from User u where 'write' <> all elements(u.permissions)" );
 	}
 
 	@Test
 	public void testManyToManyMaxElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Human human where 5 = maxelement(human.friends)" );
 	}
 
 	@Test
 	public void testCollectionIndexFunctionsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 = maxindex(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 2 = minindex(zoo.animals)" );
 	}
 
 	@Test
 	public void testCollectionIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all indices(zoo.animals)" );
 	}
 
 	@Test
 	public void testIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists indices(zoo.animals)" );
 	}
 
 	@Test
 	public void testCollectionElementInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all elements(zoo.animals)" );
 	}
 
 	@Test
 	public void testElementsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists elements(zoo.animals)" );
 	}
 
 	@Test
 	public void testNull() throws Exception {
 		assertTranslation( "from Human h where h.nickName is null" );
 		assertTranslation( "from Human h where h.nickName is not null" );
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testSubstitutions() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		replacements.put("yes", "'Y'");
 		assertTranslation( "from Human h where h.pregnant = true", replacements );
 		assertTranslation( "from Human h where h.pregnant = yes", replacements );
 		assertTranslation( "from Human h where h.pregnant = foo", replacements );
 	}
 
 	@Test
 	public void testWhere() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > 10" );
 		// 2004-06-26 [jsd] This one requires NOT GT => LE transform.
 		assertTranslation( "from Animal an where not an.bodyWeight > 10" );
 		assertTranslation( "from Animal an where an.bodyWeight between 0 and 10" );
 		assertTranslation( "from Animal an where an.bodyWeight not between 0 and 10" );
 		assertTranslation( "from Animal an where sqrt(an.bodyWeight)/2 > 10" );
 		// 2004-06-27 [jsd] Recognize 'is null' properly.  Generate 'and' and 'or' as well.
 		assertTranslation( "from Animal an where (an.bodyWeight > 10 and an.bodyWeight < 100) or an.bodyWeight is null" );
 	}
 
 	@Test
 	public void testEscapedQuote() throws Exception {
 		assertTranslation( "from Human h where h.nickName='1 ov''tha''few'");
 	}
 
 	@Test
 	public void testCaseWhenElse() {
 		assertTranslation(
 				"from Human h where case when h.nickName='1ovthafew' then 'Gavin' when h.nickName='turin' then 'Christian' else h.nickName end = h.name.first"
 		);
 	}
 
 	@Test
 	public void testCaseExprWhenElse() {
 		assertTranslation( "from Human h where case h.nickName when '1ovthafew' then 'Gavin' when 'turin' then 'Christian' else h.nickName end = h.name.first" );
 	}
 
 	@Test
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public void testInvalidHql() throws Exception {
 		Exception newException = compileBadHql( "from Animal foo where an.bodyWeight > 10", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.name from Animal foo", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal foo where an.verybogus > 10", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.boguspropertyname from Animal foo", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.name", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal an where (((an.bodyWeight > 10 and an.bodyWeight < 100)) or an.bodyWeight is null", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal an where an.bodyWeight is null where an.bodyWeight is null", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from where name='foo'", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from NonexistentClass where name='foo'", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select new FOO_BOGUS_Animal(an.description, an.bodyWeight) from Animal an", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select new Animal(an.description, an.bodyWeight, 666) from Animal an", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 
 	}
 
 	@Test
 	public void testWhereBetween() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight between 1 and 10" );
 	}
 
 	@Test
 	public void testConcatenation() {
 		if ( getDialect() instanceof MySQLDialect || getDialect() instanceof SybaseDialect
 				|| getDialect() instanceof Sybase11Dialect
 				|| getDialect() instanceof SybaseASE15Dialect
 				|| getDialect() instanceof SybaseAnywhereDialect
 				|| getDialect() instanceof SQLServerDialect 
 				|| getDialect() instanceof IngresDialect) {
 			// SybaseASE15Dialect and SybaseAnywhereDialect support '||'
 			// MySQL uses concat(x, y, z)
 			// SQL Server replaces '||' with '+'
 			//
 			// this is syntax checked in {@link ASTParserLoadingTest#testConcatenation} 
 			// Ingres supports both "||" and '+' but IngresDialect originally
 			// uses '+' operator; updated Ingres9Dialect to use "||".
 			return;
 		}
 		assertTranslation("from Human h where h.nickName = '1' || 'ov' || 'tha' || 'few'");
 	}
 
 	@Test
 	public void testWhereLike() throws Exception {
 		assertTranslation( "from Animal a where a.description like '%black%'" );
 		assertTranslation( "from Animal an where an.description like '%fat%'" );
 		assertTranslation( "from Animal an where lower(an.description) like '%fat%'" );
 	}
 
 	@Test
 	public void testWhereIn() throws Exception {
 		assertTranslation( "from Animal an where an.description in ('fat', 'skinny')" );
 	}
 
 	@Test
 	public void testLiteralInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(-5)" );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	@Test
 	public void testExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3-5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3/5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3+5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3*5)" );
 		SQLFunction concat = sessionFactory().getSqlFunctionRegistry().findSQLFunction( "concat");
 		List list = new ArrayList();
 		list.add("'fat'");
 		list.add("'skinny'");
 		assertTranslation(
 				"from Animal an where an.description = " +
 						concat.render( StringType.INSTANCE, list, sessionFactory() )
 		);
 	}
 
 	@Test
 	public void testNotOrWhereClause() {
 		assertTranslation( "from Simple s where 'foo'='bar' or not 'foo'='foo'" );
 		assertTranslation( "from Simple s where 'foo'='bar' or not ('foo'='foo')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' )" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' )" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' ) or not ('x'='y')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' ) and not ('x'='y')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' ) and 'x'='y'" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' ) or 'x'='y'" );
 		assertTranslation( "from Simple s where 'foo'='bar' and 'foo'='foo' or not 'x'='y'" );
 		assertTranslation( "from Simple s where 'foo'='bar' or 'foo'='foo' and not 'x'='y'" );
 		assertTranslation( "from Simple s where ('foo'='bar' and 'foo'='foo') or 'x'='y'" );
 		assertTranslation( "from Simple s where ('foo'='bar' or 'foo'='foo') and 'x'='y'" );
 		assertTranslation( "from Simple s where not( upper( s.name ) ='yada' or 1=2 or 'foo'='bar' or not('foo'='foo') or 'foo' like 'bar' )" );
 	}
 
 	@Test
 	public void testComplexExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs((3-5)/4)" );
 	}
 
 	@Test
 	public void testStandardFunctions() throws Exception {
 		assertTranslation( "from Animal where current_date = current_time" );
 		assertTranslation( "from Animal a where upper(a.description) = 'FAT'" );
 		assertTranslation( "select lower(a.description) from Animal a" );
 	}
 
 	@Test
 	public void testOrderBy() throws Exception {
 		assertTranslation( "from Animal an order by an.bodyWeight" );
 		assertTranslation( "from Animal an order by an.bodyWeight asc" );
 		assertTranslation( "from Animal an order by an.bodyWeight desc" );
 		assertTranslation( "from Animal an order by sqrt(an.bodyWeight*4)/2" );
 		assertTranslation( "from Animal an order by an.mother.bodyWeight" );
 		assertTranslation( "from Animal an order by an.bodyWeight, an.description" );
 		assertTranslation( "from Animal an order by an.bodyWeight asc, an.description desc" );
 		if ( getDialect() instanceof HSQLDialect || getDialect() instanceof DB2Dialect ) {
 			assertTranslation( "from Human h order by sqrt(h.bodyWeight), year(h.birthdate)" );
 		}
 	}
 
 	@Test
 	public void testGroupByFunction() {
 		if ( getDialect() instanceof Oracle8iDialect ) return; // the new hiearchy...
 		if ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect ) return;
+		if ( getDialect() instanceof TeradataDialect) return;
 		if ( ! H2Dialect.class.isInstance( getDialect() ) ) {
 			// H2 has no year function
 			assertTranslation( "select count(*) from Human h group by year(h.birthdate)" );
 			assertTranslation( "select count(*) from Human h group by year(sysdate)" );
 		}
 		assertTranslation( "select count(*) from Human h group by trunc( sqrt(h.bodyWeight*4)/2 )" );
 	}
 
 	@Test
 	public void testPolymorphism() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Mammal" );
 		assertTranslation( "from Dog" );
 		assertTranslation( "from Mammal m where m.pregnant = false and m.bodyWeight > 10", replacements );
 		assertTranslation( "from Dog d where d.pregnant = false and d.bodyWeight > 10", replacements );
 	}
 
 	private Map buildTrueFalseReplacementMapForDialect() {
 		HashMap replacements = new HashMap();
 		try {
 			String dialectTrueRepresentation = getDialect().toBooleanValueString( true );
 			// if this call succeeds, then the dialect is saying to represent true/false as int values...
 			Integer.parseInt( dialectTrueRepresentation );
 			replacements.put( "true", "1" );
 			replacements.put( "false", "0" );
 		}
 		catch( NumberFormatException nfe ) {
 			// the Integer#parseInt call failed...
 		}
 		return replacements;
 	}
 
 	@Test
 	public void testTokenReplacement() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Mammal m where m.pregnant = false and m.bodyWeight > 10", replacements );
 	}
 
 	@Test
 	public void testProduct() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Animal, Animal" );
 		assertTranslation( "from Animal x, Animal y where x.bodyWeight = y.bodyWeight" );
 		assertTranslation( "from Animal x, Mammal y where x.bodyWeight = y.bodyWeight and not y.pregnant = true", replacements );
 		assertTranslation( "from Mammal, Mammal" );
 	}
 
 	@Test
 	public void testJoinedSubclassProduct() throws Exception {
 		assertTranslation( "from PettingZoo, PettingZoo" ); //product of two subclasses
 	}
 
 	@Test
 	public void testProjectProduct() throws Exception {
 		assertTranslation( "select x from Human x, Human y where x.nickName = y.nickName" );
 		assertTranslation( "select x, y from Human x, Human y where x.nickName = y.nickName" );
 	}
 
 	@Test
 	public void testExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo" );
 		assertTranslation( "from Animal an left outer join an.mother mo" );
 		assertTranslation( "from Animal an left outer join fetch an.mother" );
 	}
 
 	@Test
 	public void testMultipleExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join mo.mother gm" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join mo.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother m inner join an.father f" );
 		assertTranslation( "from Animal an left join fetch an.mother m left join fetch an.father f" );
 	}
 
 	@Test
 	public void testMultipleExplicitJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join an.offspring os" );
 	}
 
 	@Test
 	public void testExplicitEntityJoinsWithRestriction() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testIdProperty() throws Exception {
 		assertTranslation( "from Animal a where a.mother.id = 12" );
 	}
 
 	@Test
 	public void testSubclassAssociation() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.nickName = 'Gavin'" );
 		assertTranslation( "from DomesticAnimal da left join fetch da.owner" );
 		assertTranslation( "from Human h join h.pets p where p.pregnant = 1" );
 		assertTranslation( "from Human h join h.pets p where p.bodyWeight > 100" );
 		assertTranslation( "from Human h left join fetch h.pets" );
 	}
 
 	@Test
 	public void testExplicitCollectionJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.offspring os" );
 	}
 
 	@Test
 	public void testExplicitOuterJoinFetch() throws Exception {
 		assertTranslation( "from Animal an left outer join fetch an.offspring" );
 	}
 
 	@Test
 	public void testExplicitOuterJoinFetchWithSelect() throws Exception {
 		assertTranslation( "select an from Animal an left outer join fetch an.offspring" );
 	}
 
 	@Test
 	public void testExplicitJoins() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Zoo zoo join zoo.mammals mam where mam.pregnant = true and mam.description like '%white%'", replacements );
 		assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%white%'" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-559" )
     public void testMultibyteCharacterConstant() throws Exception {
         assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%\u4e2d%'" );
     }
 
 	@Test
 	public void testImplicitJoins() throws Exception {
 		// Two dots...
 		assertTranslation( "from Animal an where an.mother.bodyWeight > ?" );
 		assertTranslation( "from Animal an where an.mother.bodyWeight > 10" );
 		assertTranslation( "from Dog dog where dog.mother.bodyWeight > 10" );
 		// Three dots...
 		assertTranslation( "from Animal an where an.mother.mother.bodyWeight > 10" );
 		// The new QT doesn't throw an exception here, so this belongs in ASTQueryTranslator test. [jsd]
 //		assertTranslation( "from Animal an where an.offspring.mother.bodyWeight > 10" );
 		// Is not null (unary postfix operator)
 		assertTranslation( "from Animal an where an.mother is not null" );
 		// ID property shortut (no implicit join)
 		assertTranslation( "from Animal an where an.mother.id = 123" );
 	}
 
 	@Test
 	public void testImplicitJoinInSelect() {
 		assertTranslation( "select foo, foo.long from Foo foo" );
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo" );
 		assertTranslation( "select foo, foo.foo from Foo foo" );
 		assertTranslation( "select foo.foo from Foo foo where foo.foo is not null" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSelectExpressions() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select an.mother.mother from Animal an" );
 		assertTranslation( "select an.mother.mother.mother from Animal an" );
 		assertTranslation( "select an.mother.mother.bodyWeight from Animal an" );
 		assertTranslation( "select an.mother.zoo.id from Animal an" );
 		assertTranslation( "select user.human.zoo.id from User user" );
 		assertTranslation( "select u.userName, u.human.name.first from User u" );
 		assertTranslation( "select u.human.name.last, u.human.name.first from User u" );
 		assertTranslation( "select bar.baz.name from Bar bar" );
 		assertTranslation( "select bar.baz.name, bar.baz.count from Bar bar" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSelectStandardFunctionsNoParens() throws Exception {
 		assertTranslation( "select current_date, current_time, current_timestamp from Animal" );
 	}
 
 	@Test
 	public void testMapIndex() throws Exception {
 		assertTranslation( "from User u where u.permissions['hibernate']='read'" );
 	}
 
 	@Test
 	public void testNamedParameters() throws Exception {
 		assertTranslation( "from Animal an where an.mother.bodyWeight > :weight" );
 	}
 
 	@Test
 	@SkipForDialect( Oracle8iDialect.class )
 	public void testClassProperty() throws Exception {
 		assertTranslation( "from Animal a where a.mother.class = Reptile" );
 	}
 
 	@Test
 	public void testComponent() throws Exception {
 		assertTranslation( "from Human h where h.name.first = 'Gavin'" );
 	}
 
 	@Test
 	public void testSelectEntity() throws Exception {
 		assertTranslation( "select an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 		assertTranslation( "select mo, an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testValueAggregate() {
 		assertTranslation( "select max(p), min(p) from User u join u.permissions p" );
 	}
 
 	@Test
 	public void testAggregation() throws Exception {
 		assertTranslation( "select count(an) from Animal an" );
 		assertTranslation( "select count(*) from Animal an" );
 		assertTranslation( "select count(distinct an) from Animal an" );
 		assertTranslation( "select count(distinct an.id) from Animal an" );
 		assertTranslation( "select count(all an.id) from Animal an" );
 	}
 
 	@Test
 	public void testSelectProperty() throws Exception {
 		assertTranslation( "select an.bodyWeight, mo.bodyWeight from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testSelectEntityProperty() throws Exception {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select an.mother from Animal an" );
 		assertTranslation( "select an, an.mother from Animal an" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSelectDistinctAll() throws Exception {
 		assertTranslation( "select distinct an.description, an.bodyWeight from Animal an" );
 		assertTranslation( "select all an from Animal an" );
 	}
 
 	@Test
 	public void testSelectAssociatedEntityId() throws Exception {
 		assertTranslation( "select an.mother.id from Animal an" );
 	}
 
 	@Test
 	public void testGroupBy() throws Exception {
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id" );
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id having max(an.bodyWeight)>1.0" );
 	}
 
 	@Test
 	public void testGroupByMultiple() throws Exception {
 		assertTranslation( "select s.id, s.count, count(t), max(t.date) from org.hibernate.test.legacy.Simple s, org.hibernate.test.legacy.Simple t where s.count = t.count group by s.id, s.count order by s.count" );
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		assertTranslation( "from Human h join h.friends f where f.nickName = 'Gavin'" );
 		assertTranslation( "from Human h join h.friends f where f.bodyWeight > 100" );
 	}
 
 	@Test
 	public void testManyToManyElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Human human where human in elements(human.friends)" );
 		assertTranslation( "from Human human where human = some elements(human.friends)" );
 	}
 
 	@Test
 	public void testManyToManyElementFunctionInWhere2() throws Exception {
 		assertTranslation( "from Human h1, Human h2 where h2 in elements(h1.family)" );
 		assertTranslation( "from Human h1, Human h2 where 'father' in indices(h1.family)" );
 	}
 
 	@Test
 	public void testManyToManyFetch() throws Exception {
 		assertTranslation( "from Human h left join fetch h.friends" );
 	}
 
 	@Test
 	public void testManyToManyIndexAccessor() throws Exception {
 		assertTranslation( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" );
 		assertTranslation( "select s from ContainerX c, Simple s where c.manyToMany[2] = s" );
 		assertTranslation( "from ContainerX c, Simple s where c.manyToMany[2] = s" );
 	}
 
 	@Test
 	public void testSelectNew() throws Exception {
 		assertTranslation( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 		assertTranslation( "select new org.hibernate.test.hql.Animal(an.description, an.bodyWeight) from Animal an" );
 	}
 
 	@Test
 	public void testSimpleCorrelatedSubselect() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select o.bodyWeight from a.offspring o)" );
 		assertTranslation( "from Animal a where a = (from a.offspring o)" );
 	}
 
 	@Test
 	public void testSimpleUncorrelatedSubselect() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select an.bodyWeight from Animal an)" );
 		assertTranslation( "from Animal a where a = (from Animal an)" );
 	}
 
 	@Test
 	public void testSimpleCorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a = (select o from a.offspring o)" );
 		assertTranslation( "from Animal a where a in (select o from a.offspring o)" );
 	}
 
 	@Test
 	public void testSimpleUncorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a = (select an from Animal an)" );
 		assertTranslation( "from Animal a where a in (select an from Animal an)" );
 	}
 
 	@Test
 	public void testUncorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select max(an.bodyWeight) from Animal an)" );
 	}
 
 	@Test
 	public void testCorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight > (select max(o.bodyWeight) from a.offspring o)" );
 	}
 
 	@Test
 	public void testManyToManyJoinInSubselect() throws Exception {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo from Foo foo where foo in (select elt from Baz baz join baz.fooArray elt)" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testImplicitJoinInSubselect() throws Exception {
 		assertTranslation( "from Animal a where a = (select an.mother from Animal an)" );
 		assertTranslation( "from Animal a where a.id = (select an.mother.id from Animal an)" );
 	}
 
 	@Test
 	public void testManyToOneSubselect() {
 		//TODO: the join in the subselect also shows up in the outer query!
 		assertTranslation( "from Animal a where 'foo' in (select m.description from a.mother m)" );
 	}
 
 	@Test
 	public void testPositionalParameters() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > ?" );
 	}
 
 	@Test
 	public void testKeywordPropertyName() throws Exception {
 		assertTranslation( "from Glarch g order by g.order asc" );
 		assertTranslation( "select g.order from Glarch g where g.order = 3" );
 	}
 
 	@Test
 	public void testJavaConstant() throws Exception {
 		assertTranslation( "from org.hibernate.test.legacy.Category c where c.name = org.hibernate.test.legacy.Category.ROOT_CATEGORY" );
 		assertTranslation( "from org.hibernate.test.legacy.Category c where c.id = org.hibernate.test.legacy.Category.ROOT_ID" );
 		// todo : additional desired functionality
 		//assertTranslation( "from Category c where c.name = Category.ROOT_CATEGORY" );
 		//assertTranslation( "select c.name, Category.ROOT_ID from Category as c");
 	}
 
 	@Test
 	public void testClassName() throws Exception {
 		// The Zoo reference is OK; Zoo is discriminator-based;
 		// the old parser could handle these correctly
 		//
 		// However, the Animal one ares not; Animal is joined subclassing;
 		// the old parser does not handle thee correctly.  The new parser
 		// previously did not handle them correctly in that same way.  So they
 		// used to pass regression even though the output was bogus SQL...
 		//
 		// I have moved the Animal ones (plus duplicating the Zoo one)
 		// to ASTParserLoadingTest for syntax checking.
 		assertTranslation( "from Zoo zoo where zoo.class = PettingZoo" );
 //		assertTranslation( "from DomesticAnimal an where an.class = Dog" );
 //		assertTranslation( "from Animal an where an.class = Dog" );
 	}
 
 	@Test
 	public void testSelectDialectFunction() throws Exception {
 		// From SQLFunctionsTest.testDialectSQLFunctions...
 		if ( getDialect() instanceof HSQLDialect ) {
 			assertTranslation( "select mod(s.count, 2) from org.hibernate.test.legacy.Simple as s where s.id = 10" );
 			//assertTranslation( "from org.hibernate.test.legacy.Simple as s where mod(s.count, 2) = 0" );
 		}
 		assertTranslation( "select upper(human.name.first) from Human human" );
 		assertTranslation( "from Human human where lower(human.name.first) like 'gav%'" );
 		assertTranslation( "select upper(a.description) from Animal a" );
 		assertTranslation( "select max(a.bodyWeight) from Animal a" );
 	}
 
 	@Test
 	public void testTwoJoins() throws Exception {
 		assertTranslation( "from Human human join human.friends, Human h join h.mother" );
 		assertTranslation( "from Human human join human.friends f, Animal an join an.mother m where f=m" );
 		assertTranslation( "from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo" );
 	}
 
 	@Test
 	public void testToOneToManyManyJoinSequence() throws Exception {
 		assertTranslation( "from Dog d join d.owner h join h.friends f where f.name.first like 'joe%'" );
 	}
 
 	@Test
 	public void testToOneToManyJoinSequence() throws Exception {
 		assertTranslation( "from Animal a join a.mother m join m.offspring" );
 		assertTranslation( "from Dog d join d.owner m join m.offspring" );
 		assertTranslation( "from Animal a join a.mother m join m.offspring o where o.bodyWeight > a.bodyWeight" );
 	}
 
 	@Test
 	public void testSubclassExplicitJoin() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.nickName = 'gavin'" );
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.bodyWeight > 0" );
 	}
 
 	@Test
 	public void testMultipleExplicitCollectionJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.offspring os join os.offspring gc" );
 		assertTranslation( "from Animal an left outer join an.offspring os left outer join os.offspring gc" );
 	}
 
 	@Test
 	public void testSelectDistinctComposite() throws Exception {
 		// This is from CompositeElementTest.testHandSQL.
 		assertTranslation( "select distinct p from org.hibernate.test.compositeelement.Parent p join p.children c where c.name like 'Child%'" );
 	}
 
 	@Test
 	public void testDotComponent() throws Exception {
 		// from FumTest.testListIdentifiers()
 		assertTranslation( "select fum.id from org.hibernate.test.legacy.Fum as fum where not fum.fum='FRIEND'" );
 	}
 
 	@Test
 	public void testOrderByCount() throws Exception {
 		assertTranslation( "from Animal an group by an.zoo.id order by an.zoo.id, count(*)" );
 	}
 
 	@Test
 	public void testHavingCount() throws Exception {
 		assertTranslation( "from Animal an group by an.zoo.id having count(an.zoo.id) > 1" );
 	}
 
 	@Test
 	public void selectWhereElements() throws Exception {
 		assertTranslation( "select foo from Foo foo, Baz baz where foo in elements(baz.fooArray)" );
 	}
 
 	@Test
 	public void testCollectionOfComponents() throws Exception {
 		assertTranslation( "from Baz baz inner join baz.components comp where comp.name='foo'" );
 	}
 
 	@Test
 	public void testNestedComponentIsNull() {
 		// From MapTest...
 		assertTranslation( "from Commento c where c.marelo.commento.mcompr is null" );
 	}
 
 	@Test
 	public void testOneToOneJoinedFetch() throws Exception {
 		// From OneToOneTest.testOneToOneOnSubclass
 		assertTranslation( "from org.hibernate.test.onetoone.joined.Person p join fetch p.address left join fetch p.mailingAddress" );
 	}
 
 	@Test
 	public void testSubclassImplicitJoin() throws Exception {
 		assertTranslation( "from DomesticAnimal da where da.owner.nickName like 'Gavin%'" );
 		assertTranslation( "from DomesticAnimal da where da.owner.nickName = 'gavin'" );
 		assertTranslation( "from DomesticAnimal da where da.owner.bodyWeight > 0" );
 	}
 
 	@Test
 	public void testComponent2() throws Exception {
 		assertTranslation( "from Dog dog where dog.owner.name.first = 'Gavin'" );
 	}
 
 	@Test
 	public void testOneToOne() throws Exception {
 		assertTranslation( "from User u where u.human.nickName='Steve'" );
 		assertTranslation( "from User u where u.human.name.first='Steve'" );
 	}
 
 	@Test
 	public void testSelectClauseImplicitJoin() throws Exception {
 		//assertTranslation( "select d.owner.mother from Dog d" ); //bug in old qt
 		assertTranslation( "select d.owner.mother.description from Dog d" );
 		//assertTranslation( "select d.owner.mother from Dog d, Dog h" );
 	}
 
 	@Test
 	public void testFromClauseImplicitJoin() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner.mother m where m.bodyWeight > 10" );
 	}
 
 	@Test
 	public void testJoinedSubclassWithOrCondition() {
 		assertTranslation( "from Animal an where (an.bodyWeight > 10 and an.bodyWeight < 100) or an.bodyWeight is null" );
 	}
 
 	@Test
 	public void testImplicitJoinInFrom() {
 		assertTranslation( "from Human h join h.mother.mother.offspring o" );
 	}
 
 	@Test
 	@SkipForDialect( Oracle8iDialect.class )
 	public void testDuplicateImplicitJoinInSelect() {
 		// This test causes failures on theta-join dialects because the SQL is different.  The old parser
 		// duplicates the condition, whereas the new parser does not.  The queries are semantically the
 		// same however.
 
 // the classic translator handles this incorrectly; the explicit join and the implicit ones should create separate physical SQL joins...
 //		assertTranslation( "select an.mother.bodyWeight from Animal an join an.mother m where an.mother.bodyWeight > 10" );
 		assertTranslation( "select an.mother.bodyWeight from Animal an where an.mother.bodyWeight > 10" );
 		//assertTranslation("select an.mother from Animal an where an.mother.bodyWeight is not null");
 		assertTranslation( "select an.mother.bodyWeight from Animal an order by an.mother.bodyWeight" );
 	}
 
 	@Test
 	public void testConstructorNode() throws Exception {
 		ConstructorNode n = new ConstructorNode();
 		assertNull( n.getFromElement() );
 		assertFalse( n.isReturnableEntity() );
 	}
 
 	@Test
 	public void testIndexNode() throws Exception {
 		IndexNode n = new IndexNode();
 		Exception ex = null;
 		try {
 			n.setScalarColumn( 0 );
 		}
 		catch ( UnsupportedOperationException e ) {
 			ex = e;
 		}
 		assertNotNull( ex );
 	}
 
 	@Test
 	public void testExceptions() throws Exception {
 		DetailedSemanticException dse = new DetailedSemanticException( "test" );
 		dse.printStackTrace();
 		dse.printStackTrace( new PrintWriter( new StringWriter() ) );
 		QuerySyntaxException qse = QuerySyntaxException.convert( new RecognitionException( "test" ), "from bozo b where b.clown = true" );
 		assertNotNull( qse.getMessage() );
 	}
 
 	@Test
 	public void testSelectProperty2() throws Exception {
 		assertTranslation( "select an, mo.bodyWeight from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 		assertTranslation( "select an, mo, an.bodyWeight, mo.bodyWeight from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testSubclassWhere() throws Exception {
 		// TODO: The classic QT generates lots of extra parens, etc.
 		assertTranslation( "from PettingZoo pz1, PettingZoo pz2 where pz1.id = pz2.id" );
 		assertTranslation( "from PettingZoo pz1, PettingZoo pz2 where pz1.id = pz2" );
 		assertTranslation( "from PettingZoo pz where pz.id > 0 " );
 	}
 
 	@Test
 	public void testNestedImplicitJoinsInSelect() throws Exception {
 		// NOTE: This test is not likely to generate the exact SQL because of the where clause.  The synthetic
 		// theta style joins come out differently in the new QT.
 		// From FooBarTest.testQuery()
 		// Missing the foo2_ join, and foo3_ should include subclasses, but it doesn't.
 //		assertTranslation("select foo.foo.foo.foo.string from org.hibernate.test.legacy.Foo foo where foo.foo.foo = 'bar'");
 		assertTranslation( "select foo.foo.foo.foo.string from org.hibernate.test.legacy.Foo foo" );
 	}
 
 	@Test
 	public void testNestedComponent() throws Exception {
 		// From FooBarTest.testQuery()
 		//an extra set of parens in new SQL
 		assertTranslation( "from org.hibernate.test.legacy.Foo foo where foo.component.subcomponent.name='bar'" );
 	}
 
 	@Test
 	public void testNull2() throws Exception {
 		//old parser generates useless extra parens
 		assertTranslation( "from Human h where not( h.nickName is null )" );
 		assertTranslation( "from Human h where not( h.nickName is not null )" );
 	}
 
 	@Test
 	public void testUnknownFailureFromMultiTableTest() {
 		assertTranslation( "from Lower s where s.yetanother.name='name'" );
 	}
 
 	@Test
 	public void testJoinInSubselect() throws Exception {
 		//new parser uses ANSI-style inner join syntax
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "from Animal a where a in (select m from Animal an join an.mother m)" );
 		assertTranslation( "from Animal a where a in (select o from Animal an join an.offspring o)" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testJoinedSubclassImplicitJoin() throws Exception {
 		// From MultiTableTest.testQueries()
 		// TODO: This produces the proper from clause now, but the parens in the where clause are different.
 		assertTranslation( "from org.hibernate.test.legacy.Lower s where s.yetanother.name='name'" );
 	}
 
 	@Test
 	public void testProjectProductJoinedSubclass() throws Exception {
 		// TODO: The old QT generates the discriminator and the theta join in a strange order, and with two extra sets of parens, this is okay, right?
 		assertTranslation( "select zoo from Zoo zoo, PettingZoo pz where zoo=pz" );
 		assertTranslation( "select zoo, pz from Zoo zoo, PettingZoo pz where zoo=pz" );
 	}
 
 	@Test
 	public void testCorrelatedSubselect1() throws Exception {
 		// The old translator generates the theta join before the condition in the sub query.
 		// TODO: Decide if we want to bother generating the theta join in the same order (non simple).
 		assertTranslation( "from Animal a where exists (from a.offspring o where o.bodyWeight>10)" );
 	}
 
 	@Test
 	public void testOuterAliasInSubselect() {
 		assertTranslation( "from Human h where h = (from Animal an where an = h)" );
 	}
 
 	@Test
 	public void testFetch() throws Exception {
 		assertTranslation( "from Zoo zoo left join zoo.mammals" );
 		assertTranslation( "from Zoo zoo left join fetch zoo.mammals" );
 	}
 
 	@Test
 	public void testOneToManyElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 'dog' in indices(zoo.mammals)" );
 		assertTranslation( "from Zoo zoo, Dog dog where dog in elements(zoo.mammals)" );
 	}
 
 	@Test
 	public void testManyToManyInJoin() throws Exception {
 		assertTranslation( "select x.id from Human h1 join h1.family x" );
 		//assertTranslation("select index(h2) from Human h1 join h1.family h2");
 	}
 
 	@Test
 	public void testManyToManyInSubselect() throws Exception {
 		assertTranslation( "from Human h1, Human h2 where h2 in (select x.id from h1.family x)" );
 		assertTranslation( "from Human h1, Human h2 where 'father' in indices(h1.family)" );
 	}
 
 	@Test
 	public void testOneToManyIndexAccess() throws Exception {
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'] is not null" );
 	}
 
 	@Test
 	public void testImpliedSelect() throws Exception {
 		assertTranslation( "select zoo from Zoo zoo" );
 		assertTranslation( "from Zoo zoo" );
 		assertTranslation( "from Zoo zoo join zoo.mammals m" );
 		assertTranslation( "from Zoo" );
 		assertTranslation( "from Zoo zoo join zoo.mammals" );
 	}
 
 	@Test
 	public void testVectorSubselect() {
 		assertTranslation( "from Animal a where ('foo', 'bar') in (select m.description, m.bodyWeight from a.mother m)" );
 	}
 
 	@Test
 	public void testWierdSubselectImplicitJoinStuff() {
 		//note that the new qt used to eliminate unnecessary join, but no more
 		assertTranslation("from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count ) and s.other.count > 0");
 	}
 
 	@Test
 	public void testCollectionsInSelect2() throws Exception {
 		// This one looks okay now, it just generates extra parens in the where clause.
 		assertTranslation( "select foo.string from Bar bar left join bar.baz.fooArray foo where bar.string = foo.string" );
 	}
 
 	@Test
 	public void testAssociationPropertyWithoutAlias() throws Exception {
 		// The classic translator doesn't do this right, so don't bother asserting.
 		compileWithAstQueryTranslator("from Animal where zoo is null", false);
 	}
 
 	private void compileWithAstQueryTranslator(String hql, boolean scalar) {
 		Map replacements = new HashMap();
 		QueryTranslatorFactory ast = new ASTQueryTranslatorFactory();
 		SessionFactoryImplementor factory = getSessionFactoryImplementor();
 		QueryTranslator newQueryTranslator = ast.createQueryTranslator( hql, hql, Collections.EMPTY_MAP, factory, null );
 		newQueryTranslator.compile( replacements, scalar );
 	}
 
 	@Test
 	public void testComponentNoAlias() throws Exception {
 		// The classic translator doesn't do this right, so don't bother asserting.
 		compileWithAstQueryTranslator( "from Human where name.first = 'Gavin'", false);
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hqlfetchscroll/HQLScrollFetchTest.java b/hibernate-core/src/test/java/org/hibernate/test/hqlfetchscroll/HQLScrollFetchTest.java
index 872a631bf7..038d4806b8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hqlfetchscroll/HQLScrollFetchTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hqlfetchscroll/HQLScrollFetchTest.java
@@ -1,357 +1,359 @@
 package org.hibernate.test.hqlfetchscroll;
 
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertNotNull;
-import static org.junit.Assert.assertNull;
-import static org.junit.Assert.assertSame;
-import static org.junit.Assert.assertTrue;
-import static org.junit.Assert.fail;
-
-import java.util.ArrayList;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Set;
-
 import org.hibernate.Hibernate;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
+import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
-import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.SQLServerDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.junit.Test;
 
+import java.util.ArrayList;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Set;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
 @SkipForDialect( value = { Oracle8iDialect.class, AbstractHANADialect.class },
 		comment = "Oracle/HANA do not support the identity column used in the mapping. Extended by NoIdentityHQLScrollFetchTest" )
 public class HQLScrollFetchTest extends BaseCoreFunctionalTestCase {
 	private static final String QUERY = "select p from Parent p join fetch p.children c";
 
 	@Test
 	public void testNoScroll() {
 		Session s = openSession();
 		List list = s.createQuery( QUERY ).setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE ).list();
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
-	@SkipForDialect( { SQLServerDialect.class,  Oracle8iDialect.class, H2Dialect.class, DB2Dialect.class, AbstractHANADialect.class } )
+	@SkipForDialect( { SQLServerDialect.class,  Oracle8iDialect.class, H2Dialect.class, DB2Dialect.class,
+            AbstractHANADialect.class, TeradataDialect.class } )
 	public void testScroll() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
 	public void testIncompleteScrollFirstResult() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testIncompleteScrollSecondResult() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		results.next();
 		p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		s.close();
 	}
 
 	@Test
 	public void testIncompleteScrollFirstResultInTransaction() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testIncompleteScrollSecondResultInTransaction() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		results.next();
 		p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283")
 	public void testIncompleteScroll() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		// get the other parent entity from the persistence context along with its first child
 		// retrieved from the resultset.
 		Parent pOther = null;
 		Child cOther = null;
 		for ( Object entity : ( (SessionImplementor) s ).getPersistenceContext().getEntitiesByKey().values() ) {
 			if ( Parent.class.isInstance( entity ) ) {
 				if ( entity != p ) {
 					if ( pOther != null ) {
 						fail( "unexpected parent found." );
 					}
 					pOther = (Parent) entity;
 				}
 			}
 			else if ( Child.class.isInstance( entity ) ) {
 				if ( ! p.getChildren().contains( entity ) ) {
 					if ( cOther != null ) {
 						fail( "unexpected child entity found" );
 					}
 					cOther = (Child) entity;
 				}
 			}
 			else {
 				fail( "unexpected type of entity." );
 			}
 		}
 		// check that the same second parent is obtained by calling Session.get()
 		assertNull( pOther );
 		assertNull( cOther );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testIncompleteScrollLast() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		results.next();
 		Parent p = (Parent) results.get( 0 );
 		assertResultFromOneUser( p );
 		results.last();
 		// get the other parent entity from the persistence context.
 		// since the result set was scrolled to the end, the other parent entity's collection has been
 		// properly initialized.
 		Parent pOther = null;
 		Set childrenOther = new HashSet();
 		for ( Object entity : ( ( SessionImplementor) s ).getPersistenceContext().getEntitiesByKey().values() ) {
 			if ( Parent.class.isInstance( entity ) ) {
 				if ( entity != p ) {
 					if ( pOther != null ) {
 						fail( "unexpected parent found." );
 					}
 					pOther = (Parent) entity;
 				}
 			}
 			else if ( Child.class.isInstance( entity ) ) {
 				if ( ! p.getChildren().contains( entity ) ) {
 					childrenOther.add( entity );
 				}
 			}
 			else {
 				fail( "unexpected type of entity." );
 			}
 		}
 		// check that the same second parent is obtained by calling Session.get()
 		assertNotNull( pOther );
 		assertSame( pOther, s.get( Parent.class, pOther.getId() ) );
 		// access pOther's collection; should be completely loaded
 		assertTrue( Hibernate.isInitialized( pOther.getChildren() ) );
 		assertEquals( childrenOther, pOther.getChildren() );
 		assertResultFromOneUser( pOther );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testScrollOrderParentAsc() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc" ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testScrollOrderParentDesc() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name desc" ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testScrollOrderParentAscChildrenAsc() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc, c.name asc" ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1283" )
 	public void testScrollOrderParentAscChildrenDesc() {
 		Session s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by p.name asc, c.name desc" ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		assertResultFromAllUsers( list );
 		s.close();
 	}
 
 	@Test
 	public void testScrollOrderChildrenDesc() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p0 = new Parent( "parent0" );
 		s.save( p0 );
 		t.commit();
 		s.close();
 		s = openSession();
 		ScrollableResults results = s.createQuery( QUERY + " order by c.name desc" ).scroll();
 		List list = new ArrayList();
 		while ( results.next() ) {
 			list.add( results.get( 0 ) );
 		}
 		try {
 			assertResultFromAllUsers( list );
 			fail( "should have failed because data is ordered incorrectly." );
 		}
 		catch ( AssertionError ex ) {
 			// expected
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Test
 	public void testListOrderChildrenDesc() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p0 = new Parent( "parent0" );
 		s.save( p0 );
 		t.commit();
 		s.close();
 		s = openSession();
 		List results = s.createQuery( QUERY + " order by c.name desc" ).list();
 		try {
 			assertResultFromAllUsers( results );
 			fail( "should have failed because data is ordered incorrectly." );
 		}
 		catch ( AssertionError ex ) {
 			// expected
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	private void assertResultFromOneUser(Parent parent) {
 		assertEquals(
 					"parent " + parent + " has incorrect collection(" + parent.getChildren() + ").",
 					3,
 					parent.getChildren().size()
 		);
 	}
 
 	private void assertResultFromAllUsers(List list) {
 		assertEquals( "list is not correct size: ", 2, list.size() );
 		for ( Object aList : list ) {
 			assertResultFromOneUser( (Parent) aList );
 		}
 	}
 
 	@Override
 	protected void prepareTest() throws Exception {
 	    Session s = openSession();
 	    Transaction t = s.beginTransaction();
 	    Child child_1_1 = new Child( "achild1-1");
 	    Child child_1_2 = new Child( "ychild1-2");
 	    Child child_1_3 = new Child( "dchild1-3");
 	    Child child_2_1 = new Child( "bchild2-1");
 	    Child child_2_2 = new Child( "cchild2-2");
 	    Child child_2_3 = new Child( "zchild2-3");
 	
 	    s.save( child_1_1 );
 	    s.save( child_2_1 );
 	    s.save( child_1_2 );
 	    s.save( child_2_2 );
 	    s.save( child_1_3 );
 	    s.save( child_2_3 );
 	
 	    s.flush();
 	
 	    Parent p1 = new Parent( "parent1" );
 	    p1.addChild( child_1_1 );
 	    p1.addChild( child_1_2 );
 	    p1.addChild( child_1_3 );
 	    s.save( p1 );
 	
 	    Parent p2 = new Parent( "parent2" );
 	    p2.addChild( child_2_1 );
 	    p2.addChild( child_2_2 );
 	    p2.addChild( child_2_3 );
 	    s.save( p2 );
 	
 	    t.commit();
 	    s.close();
 	}
 	
 	@Override
 	protected void cleanupTest() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		List list = s.createQuery( "from Parent" ).list();
 		for ( Iterator i = list.iterator(); i.hasNext(); ) {
 			s.delete( i.next() );
 		}
 		t.commit();
 		s.close();
 	}
 
 	public String[] getMappings() {
 		return new String[] { "hqlfetchscroll/ParentChild.hbm.xml" };
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/idgen/identity/joinedSubClass/Super.java b/hibernate-core/src/test/java/org/hibernate/test/idgen/identity/joinedSubClass/Super.java
index 41f320e828..91d32f96f0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/idgen/identity/joinedSubClass/Super.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/idgen/identity/joinedSubClass/Super.java
@@ -1,48 +1,48 @@
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
 package org.hibernate.test.idgen.identity.joinedSubClass;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 
 import static javax.persistence.GenerationType.IDENTITY;
 import static javax.persistence.InheritanceType.JOINED;
 
 /**
  * @author Andrey Vlasov
  * @author Steve Ebersole
  */
 @Entity
 @Inheritance(strategy = JOINED)
 public class Super {
 	@Id
 	@GeneratedValue(strategy = IDENTITY)
 	private Long id;
 
-	@Column
+	@Column(name="`value`")
 	private Long value;
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml
index 92e4b6182a..75ebf71f00 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml
@@ -1,72 +1,72 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <!--
 
   This mapping demonstrates
 
      (1) use of lazy properties - this feature requires buildtime
          bytecode instrumentation; we don't think this is a very
          necessary feature, but provide it for completeleness; if
          Hibernate encounters uninstrumented classes, lazy property
          fetching will be silently disabled, to enable testing
 
      (2) use of a formula to define a "derived property"
 
 -->
 
 <hibernate-mapping package="org.hibernate.test.instrument.domain" default-access="field">
 
     <class name="Folder" table="folders">
     	<id name="id">
     		<generator class="increment"/>
     	</id>
     	<property name="name" not-null="true" length="50"/>
     	<many-to-one name="parent"/>
     	<bag name="subfolders" inverse="true" cascade="save-update">
     		<key column="parent"/>
     		<one-to-many class="Folder"/>
     	</bag>
     	<bag name="documents" inverse="true" cascade="all-delete-orphan">
     		<key column="folder"/>
     		<one-to-many class="Document"/>
     	</bag>
 	</class>
 
 	<class name="Owner" table="owners" lazy="false">
    		<id name="id">
     		<generator class="increment"/>
     	</id>
     	<property name="name" not-null="true" length="50"/>
     </class>
 
 	<class name="Document" table="documents">
    		<id name="id">
     		<generator class="increment"/>
     	</id>
     	<property name="name" not-null="true" length="50"/>
     	<property name="upperCaseName" formula="upper(name)" lazy="true"/>
-    	<property name="summary" not-null="true" length="200" lazy="true"/>
+    	<property name="summary" column="`summary`" not-null="true" length="200" lazy="true"/>
     	<many-to-one name="folder" not-null="true" lazy="no-proxy"/>
     	<many-to-one name="owner" not-null="true" lazy="no-proxy" fetch="select"/>
     	<property name="text" not-null="true" length="2000" lazy="true"/>
     	<property name="lastTextModification" not-null="true" lazy="true" access="field"/>
     	<property name="sizeKb" lazy="true">
     		<column name="size_mb"
     			read="size_mb * 1024.0"
     			write="? / cast( 1024.0 as float )"/>
     	</property>
     </class>
 
     <class name="Entity" table="entity">
         <id name="id" column="ID" type="long">
             <generator class="increment"/>
         </id>
         <property name="name" column="NAME" type="string" lazy="true"/>
         <many-to-one name="child" column="PRNT_ID" class="Entity" lazy="proxy" cascade="all" />
         <many-to-one name="sibling" column="RIGHT_ID" class="Entity" lazy="no-proxy" cascade="all" />
     </class>
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/DestinationEntity.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/DestinationEntity.java
index 56963ab3f4..e90dfe16a4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/DestinationEntity.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/DestinationEntity.java
@@ -1,99 +1,99 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.test.jpa.ql;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.Table;
 
 /**
  * @author Janario Oliveira
  */
 @Entity
 @Table(name = "destination_entity")
 @NamedNativeQueries({
 		@NamedNativeQuery(name = "DestinationEntity.insertSelect", query = "insert into destination_entity(id, from_id, fullNameFrom) "
 				+ " select fe.id, fe.id, fe.name||fe.lastName from from_entity fe where fe.id in (:ids)"),
 		@NamedNativeQuery(name = "DestinationEntity.insert", query = "insert into destination_entity(id, from_id, fullNameFrom) "
 				+ "values (:generatedId, :fromId, :fullName)"),
 		@NamedNativeQuery(name = "DestinationEntity.update", query = "update destination_entity set from_id=:idFrom, fullNameFrom=:fullName"
 				+ " where id in (:ids)"),
 		@NamedNativeQuery(name = "DestinationEntity.delete", query = "delete from destination_entity where id in (:ids)"),
-		@NamedNativeQuery(name = "DestinationEntity.selectIds", query = "select id, from_id, fullNameFrom from destination_entity where id in (:ids)") })
+		@NamedNativeQuery(name = "DestinationEntity.selectIds", query = "select id, from_id, fullNameFrom from destination_entity where id in (:ids) order by id") })
 public class DestinationEntity {
 
 	@Id
 	@GeneratedValue
 	Integer id;
 	@ManyToOne(optional = false)
 	@JoinColumn(name = "from_id")
 	FromEntity from;
 	@Column(nullable = false)
 	String fullNameFrom;
 
 	public DestinationEntity() {
 	}
 
 	public DestinationEntity(FromEntity from, String fullNameFrom) {
 		this.from = from;
 		this.fullNameFrom = fullNameFrom;
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ( ( from == null ) ? 0 : from.hashCode() );
 		result = prime * result + ( ( fullNameFrom == null ) ? 0 : fullNameFrom.hashCode() );
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object obj) {
 		if ( this == obj )
 			return true;
 		if ( obj == null )
 			return false;
 		if ( getClass() != obj.getClass() )
 			return false;
 		DestinationEntity other = (DestinationEntity) obj;
 		if ( from == null ) {
 			if ( other.from != null )
 				return false;
 		}
 		else if ( !from.equals( other.from ) )
 			return false;
 		if ( fullNameFrom == null ) {
 			if ( other.fullNameFrom != null )
 				return false;
 		}
 		else if ( !fullNameFrom.equals( other.fullNameFrom ) )
 			return false;
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomSQL.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomSQL.hbm.xml
index 5cacb1fa51..93e2960a4d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomSQL.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomSQL.hbm.xml
@@ -1,104 +1,104 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 <hibernate-mapping default-lazy="false" package="org.hibernate.test.legacy">
-	<class name="Role">
+	<class name="Role" table="`Role`">
 
 		<id name="id" type="long">
 			<generator class="native"/>
 		</id>
 		
 		<property name="name" type="string"/>
 		
 		<set name="interventions" lazy="true" cascade="all">
 			<key column="role_id"/>
 			<one-to-many class="Intervention"/>
 			<sql-insert callable="false">/* max comment */
 				update Intervention set role_id=? where id=?</sql-insert>
 			<sql-delete callable="false">update Intervention set role_id=null where role_id=? and id=?</sql-delete>
 			<sql-delete-all callable="false">update Intervention set role_id=null where role_id=?</sql-delete-all>
 		</set>
 		
 		<list name="bunchOfStrings">			
 			<key column="GROUPID"/>
 			<index column="posn"/>
 		    <element column="NAME" type="string"/>
 			<sql-insert callable="true">{ ? = call createRoleBunchOfStrings(?, ?, ?)}</sql-insert>
 			<sql-update callable="true">{ ? = call updateRoleBunchOfStrings(?, ?, ?)}</sql-update>
 			<sql-delete callable="true">{ ? = call deleteRoleBunchOfString(?, ?)}</sql-delete>
 			<sql-delete-all callable="true">{ ? = call deleteAllRoleBunchOfString(?)}</sql-delete-all>
 		</list>
 		
 <!--		<sql-load   callable="true">{ call loadPatient (?)}</sql-load>
 		<sql-insert callable="true">{call createPatient (?, ?, ?, ?)}</sql-insert>
 		<sql-delete callable="true">{? = call deletePatient (?)}</sql-delete> 
 		<sql-update callable="true">{? = call updatePatient (?, ?, ?, ?)}</sql-update> -->
 <!--	<sql-insert callable="true">insert </sql-insert> -->
 <!--		<sql-delete>delete from Role where values (?, upper(?)) /** i did this */</sql-insert>   -->
-		<sql-insert>insert into Role (name, id) values (?, upper(?)) /** i did this */</sql-insert>  
+		<sql-insert>insert into "Role" (name, id) values (?, upper(?)) /** i did this */</sql-insert>  
 <!--		<sql-update>update</sql-update>-->
-		<sql-delete>delete from Role where id=?</sql-delete>
+		<sql-delete>delete from "Role" where id=?</sql-delete>
 		
 	</class>	
 	
 	<class name="Resource" table="ecruoser">
 		<id name="id" type="string">
 			<generator class="uuid.hex"/>
 		</id>
 		
 		<discriminator column="discriminator" type="string"/>
 		
 		<property name="name" type="string"/>
 		<property name="userCode" type="string"/>
 		
 		<subclass name="Drug">
 			
 		</subclass>
 	</class>
 	
 	<class name="Party">
 		<id name="id" type="string">
 			<generator class="uuid.hex"/>
 		</id>
 		<discriminator column="discriminator" type="string"/>
 		<join table="partyjointable">
 			<key column="partyid"/>
      		<property name="name" column="xname" type="string"/>
 	    	<property name="address" type="string"/>
 			<sql-insert callable="true">{ call createJoinTable(?, ?, ?) }</sql-insert>			
 			<sql-update callable="true">{ ? = call updateJoinTable(?, ?, ?) }</sql-update> <!-- xname, address, partyid -->
 			<sql-delete callable="true">{ ? = call deleteJoinTable(?) }</sql-delete> <!-- partyid -->
 		</join>
 							
 		<subclass name="Person">
 			<property name="givenName" type="string"/>			
 			<property name="lastName" type="string"/>		
 			<property name="nationalID" unique="true" type="string"/>
 		</subclass>		
 
 		<subclass name="Company">
 		   <property name="president" type="string"/>
 		</subclass>				
 	</class>
 	
 	<class name="Intervention">
 		<id name="id" type="string">
 			<generator class="uuid.hex"/>
 		</id>
 		
 		<version name="version" type="long"/>
 		
 		<property name="description" type="string"/>
 		
 		<joined-subclass name="Medication">
 			<key column="interventionid"/>
 			<many-to-one name="prescribedDrug" class="org.hibernate.test.legacy.Drug"/>			
 			<sql-insert>insert into /** put weird comments here */ Medication (prescribedDrug, interventionid) values (?, ?)</sql-insert> 
 		</joined-subclass>
 		
 	</class>
 	
 	
 	
 </hibernate-mapping>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
index c56710181b..7b25f8b6c1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
@@ -1,1621 +1,1622 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.legacy;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.sql.Time;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Set;
 import java.util.SortedSet;
 import java.util.TimeZone;
 import java.util.TreeMap;
 import java.util.TreeSet;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Example;
 import org.hibernate.criterion.MatchMode;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyDialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SAPDBDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 public class FooBarTest extends LegacyTestCase {
 	private static final Logger log = Logger.getLogger( FooBarTest.class );
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/FooBar.hbm.xml",
 			"legacy/Baz.hbm.xml",
 			"legacy/Qux.hbm.xml",
 			"legacy/Glarch.hbm.xml",
 			"legacy/Fum.hbm.xml",
 			"legacy/Fumm.hbm.xml",
 			"legacy/Fo.hbm.xml",
 			"legacy/One.hbm.xml",
 			"legacy/Many.hbm.xml",
 			"legacy/Immutable.hbm.xml",
 			"legacy/Fee.hbm.xml",
 			"legacy/Vetoer.hbm.xml",
 			"legacy/Holder.hbm.xml",
 			"legacy/Location.hbm.xml",
 			"legacy/Stuff.hbm.xml",
 			"legacy/Container.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/XY.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testSaveOrUpdateCopyAny() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		One one = new One();
 		bar.setObject(one);
 		s.save(bar);
 		GlarchProxy g = bar.getComponent().getGlarch();
 		bar.getComponent().setGlarch(null);
 		s.delete(g);
 		s.flush();
 		assertTrue( s.contains(one) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Bar bar2 = (Bar) s.merge( bar );
 		s.flush();
 		s.delete(bar2);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRefreshProxy() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 		s.flush();
 		s.clear();
 		GlarchProxy gp = (GlarchProxy) s.load(Glarch.class, gid);
 		gp.getName(); //force init
 		s.refresh(gp);
 		s.delete(gp);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsCircularCascadeDeleteCheck.class,
 			comment = "db/dialect does not support circular cascade delete constraints"
 	)
 	public void testOnCascadeDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.subs = new ArrayList();
 		Baz sub = new Baz();
 		sub.superBaz = baz;
 		baz.subs.add(sub);
 		s.save(baz);
 		s.flush();
 		assertTrue( s.createQuery("from Baz").list().size()==2 );
 		s.getTransaction().commit();
 		s.beginTransaction();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.beginTransaction();
 		assertTrue( s.createQuery("from Baz").list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRemoveFromIdbag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setByteBag( new ArrayList() );
 		byte[] bytes = { 12, 13 };
 		baz.getByteBag().add( new byte[] { 10, 45 } );
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add( new byte[] { 1, 11 } );
 		baz.getByteBag().add( new byte[] { 12 } );
 		s.save(baz);
 		s.flush();
 		baz.getByteBag().remove(bytes);
 		s.flush();
 		baz.getByteBag().add(bytes);
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		BarProxy b = new Bar();
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load(Qux.class, q.getKey() );
 		b = (BarProxy) s.load( Foo.class, b.getKey() );
 		b.getKey();
 		assertFalse( Hibernate.isInitialized(b) );
 		b.getBarString();
 		assertTrue( Hibernate.isInitialized(b) );
 		BarProxy b2 = (BarProxy) s.load( Bar.class, b.getKey() );
 		Qux q2 = (Qux) s.load( Qux.class, q.getKey() );
 		assertTrue( "loaded same object", q==q2 );
 		assertTrue( "loaded same object", b==b2 );
 		assertTrue( Math.round( b.getFormula() ) == b.getInt() / 2 );
 		s.delete(q2);
 		s.delete( b2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testJoin() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		foo.setJoinedProp("foo");
 		s.save( foo );
 		s.flush();
 		foo.setJoinedProp("bar");
 		s.flush();
 		String fid = foo.getKey();
 		s.delete( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		foo2.setJoinedProp("foo");
 		s.save(foo2);
 		s.createQuery( "select foo.id from Foo foo where foo.joinedProp = 'foo'" ).list();
 		assertNull( s.get(Foo.class, fid) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testDereferenceLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evictCollection("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz.setFooSet(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		s.delete(baz);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testMoveLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Baz baz2 = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		s.save(baz2);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evictCollection("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		baz2.setFooSet( baz.getFooSet() );
 		baz.setFooSet(null);
 		assertFalse( Hibernate.isInitialized( baz2.getFooSet() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		assertTrue( Hibernate.isInitialized( baz2.getFooSet() ) ); //fooSet has batching enabled
 		assertTrue( baz2.getFooSet().size()==1 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCriteriaCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz bb = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( bb == null );
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz b = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( Hibernate.isInitialized( b.getTopGlarchez() ) );
 		assertTrue( b.getTopGlarchez().size() == 0 );
 		s.delete( b );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQuery() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		foo.setFoo(foo2);
 
 		List list = s.createQuery( "from Foo foo inner join fetch foo.foo" ).list();
 		Foo foof = (Foo) list.get(0);
 		assertTrue( Hibernate.isInitialized( foof.getFoo() ) );
 
 		s.createQuery( "from Baz baz left outer join fetch baz.fooToGlarch" ).list();
 
 		list = s.createQuery( "select foo, bar from Foo foo left outer join foo.foo bar where foo = ?" )
 				.setParameter( 0, foo, s.getTypeHelper().entity(Foo.class) )
 				.list();
 		Object[] row1 = (Object[]) list.get(0);
 		assertTrue( row1[0]==foo && row1[1]==foo2 );
 
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo.foo.foo.string = 'bar'" ).list();
 		if ( ! (getDialect() instanceof HSQLDialect) )
 			s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo = foo.foo.foo" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo = 'bar' and foo.foo.foo.foo = 'baz'" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo.string = 'a' and foo.foo.string = 'b'" )
 				.list();
 
 		s.createQuery( "from Bar bar, foo in elements(bar.baz.fooArray)" ).list();
 
 		//s.find("from Baz as baz where baz.topComponents[baz].name = 'bazzz'");
 
 		if ( (getDialect() instanceof DB2Dialect) && !(getDialect() instanceof DerbyDialect) ) {
 			s.createQuery( "from Foo foo where lower( foo.foo.string ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where lower( (foo.foo.string || 'foo') || 'bar' ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where repeat( (foo.foo.string || 'foo') || 'bar', 2 ) = 'foo'" ).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null and repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null or repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 		}
 		if (getDialect() instanceof SybaseDialect) {
 			s.createQuery( "select baz from Baz as baz join baz.fooArray foo group by baz order by sum(foo.float)" )
 					.iterate();
 		}
 
 		s.createQuery( "from Foo as foo where foo.component.glarch.name is not null" ).list();
 		s.createQuery( "from Foo as foo left outer join foo.component.glarch as glarch where glarch.name = 'foo'" )
 				.list();
 
 		list = s.createQuery( "from Foo" ).list();
 		assertTrue( list.size()==2 && list.get(0) instanceof FooProxy );
 		list = s.createQuery( "from Foo foo left outer join foo.foo" ).list();
 		assertTrue( list.size()==2 && ( (Object[]) list.get(0) )[0] instanceof FooProxy );
 
 		s.createQuery("from Bar, Bar").list();
 		s.createQuery("from Foo, Bar").list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join baz.fooSet" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join fetch baz.fooSet foo left join fetch foo.foo" )
 				.list();
 
 		list = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' and foo.boolean = true order by foo.string asc, foo.component.count desc"
 		).list();
 		assertTrue( "empty query", list.size()==0 );
 		Iterator iter = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' order by foo.string asc, foo.component.count desc"
 		).iterate();
 		assertTrue( "empty iterator", !iter.hasNext() );
 
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		foo.getFoo().setFoo(foo);
 		foo.setString("fizard");
 		//The following test is disabled for databases with no subselects...also for Interbase (not sure why).
 		if (
 				!(getDialect() instanceof MySQLDialect) &&
 				!(getDialect() instanceof HSQLDialect) &&
 				!(getDialect() instanceof MckoiDialect) &&
 				!(getDialect() instanceof SAPDBDialect) &&
 				!(getDialect() instanceof PointbaseDialect) &&
 				!(getDialect() instanceof DerbyDialect)
 		)  {
 			// && !db.equals("weblogic") {
 			if ( !( getDialect() instanceof InterbaseDialect ) ) {
 				list = s.createQuery( "from Foo foo where ? = some elements(foo.component.importantDates)" )
 						.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 						.list();
 				assertTrue( "component query", list.size()==2 );
 			}
 			if( !( getDialect() instanceof TimesTenDialect)) {
 				list = s.createQuery( "from Foo foo where size(foo.component.importantDates) = 3" ).list(); //WAS: 4
 				assertTrue( "component query", list.size()==2 );
 				list = s.createQuery( "from Foo foo where 0 = size(foo.component.importantDates)" ).list();
 				assertTrue( "component query", list.size()==0 );
 			}
 			list = s.createQuery( "from Foo foo where exists elements(foo.component.importantDates)" ).list();
 			assertTrue( "component query", list.size()==2 );
 			s.createQuery( "from Foo foo where not exists (from Bar bar where bar.id = foo.id)" ).list();
 
 			s.createQuery(
 					"select foo.foo from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "select foo.foo from Foo foo where foo = some(from Foo x where (x.long > foo.foo.long))" )
 					.list();
 			if ( !( getDialect() instanceof TimesTenDialect)) {
 				s.createQuery(
 						"select foo.foo from Foo foo where foo.long = some( select max(x.long) from Foo x where (x.long > foo.foo.long) group by x.foo )"
 				).list();
 			}
 			s.createQuery(
 					"from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long) and foo.foo.string='baz'"
 			).list();
 			s.createQuery(
 					"from Foo foo where foo.foo.string='baz' and foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)" ).list();
 
 			s.createQuery(
 					"select foo.string, foo.date, foo.foo.string, foo.id from Foo foo, Baz baz where foo in elements(baz.fooArray) and foo.string like 'foo'"
 			).iterate();
 		}
 		list = s.createQuery( "from Foo foo where foo.component.count is null order by foo.component.count" ).list();
 		assertTrue( "component query", list.size()==0 );
 		list = s.createQuery( "from Foo foo where foo.component.name='foo'" ).list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery(
 				"select distinct foo.component.name, foo.component.name from Foo foo where foo.component.name='foo'"
 		).list();
 		assertTrue( "component query", list.size()==1 );
 		list = s.createQuery( "select distinct foo.component.name, foo.id from Foo foo where foo.component.name='foo'" )
 				.list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.id=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "id query", list.size()==1 );
 		list = s.createQuery( "from Foo foo where foo.key=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "named id query", list.size()==1 );
 		assertTrue( "id query", list.get(0)==foo );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.string='fizard'" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		list = s.createQuery( "from Foo foo where foo.component.subcomponent.name='bar'" ).list();
 		assertTrue( "components of components", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.foo.id=?" )
 				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "by id query", list.size()==1 );
 		assertTrue( "by id returned object", list.get(0)==foo.getFoo() );
 
 		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), s.getTypeHelper().entity(Foo.class) ).list();
 
 		assertTrue( !s.createQuery( "from Bar bar where bar.string='a string' or bar.string='a string'" )
 				.iterate()
 				.hasNext() );
 
 		iter = s.createQuery( "select foo.component.name, elements(foo.component.importantDates) from Foo foo where foo.foo.id=?" )
 				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			i++;
 			Object[] row = (Object[]) iter.next();
 			assertTrue( row[0] instanceof String && ( row[1]==null || row[1] instanceof Date ) );
 		}
 		assertTrue(i==3); //WAS: 4
 		iter = s.createQuery( "select max( elements(foo.component.importantDates) ) from Foo foo group by foo.id" )
 				.iterate();
 		assertTrue( iter.next() instanceof Date );
 
 		list = s.createQuery(
 				"select foo.foo.foo.foo from Foo foo, Foo foo2 where"
 						+ " foo = foo2.foo and not not ( not foo.string='fizard' )"
 						+ " and foo2.string between 'a' and (foo.foo.string)"
-						+ ( ( getDialect() instanceof HSQLDialect || getDialect() instanceof InterbaseDialect || getDialect() instanceof TimesTenDialect ) ?
+						+ ( ( getDialect() instanceof HSQLDialect || getDialect() instanceof InterbaseDialect || getDialect() instanceof TimesTenDialect || getDialect() instanceof TeradataDialect) ?
 						" and ( foo2.string in ( 'fiz', 'blah') or 1=1 )"
 						:
 						" and ( foo2.string in ( 'fiz', 'blah', foo.foo.string, foo.string, foo2.string ) )"
 				)
 		).list();
 		assertTrue( "complex query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo );
 		foo.setString("from BoogieDown  -tinsel town  =!@#$^&*())");
 		list = s.createQuery( "from Foo foo where foo.string='from BoogieDown  -tinsel town  =!@#$^&*())'" ).list();
 		assertTrue( "single quotes", list.size()==1 );
 		list = s.createQuery( "from Foo foo where not foo.string='foo''bar'" ).list();
 		assertTrue( "single quotes", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.component.glarch.next is null" ).list();
 		assertTrue( "query association in component", list.size()==2 );
 		Bar bar = new Bar();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		bar.setBaz(baz);
 		baz.setManyToAny( new ArrayList() );
 		baz.getManyToAny().add(bar);
 		baz.getManyToAny().add(foo);
 		s.save(bar);
 		s.save(baz);
 		list = s.createQuery(
-				" from Bar bar where bar.baz.count=667 and bar.baz.count!=123 and not bar.baz.name='1-E-1'"
+				" from Bar bar where bar.baz.count=667 and bar.baz.count<>123 and not bar.baz.name='1-E-1'"
 		).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 		list = s.createQuery( " from Bar i where i.baz.name='Bazza'" ).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 
 		Iterator rs = s.createQuery( "select count(distinct foo.foo) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo.foo.boolean) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(*), foo.int from Foo foo group by foo.int" ).iterate();
 		assertTrue( "count(*) group by", ( (Object[]) rs.next() )[0].equals( new Long(3) ) );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select sum(foo.foo.int) from Foo foo" ).iterate();
 		assertTrue( "sum", ( (Long) rs.next() ).longValue()==4 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo) from Foo foo where foo.id=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( "id query count", ( (Long) rs.next() ).longValue()==1 );
 		assertTrue( !rs.hasNext() );
 
 		s.createQuery( "from Foo foo where foo.boolean = ?" )
 				.setParameter( 0, new Boolean(true), StandardBasicTypes.BOOLEAN )
 				.list();
 
 		s.createQuery( "select new Foo(fo.x) from Fo fo" ).list();
 		s.createQuery( "select new Foo(fo.integer) from Foo fo" ).list();
 
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test 2")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 
 		rs = s.createQuery( "select new Foo(fo.x) from Foo fo" ).iterate();
 		assertTrue( "projection iterate (results)", rs.hasNext() );
 		assertTrue( "projection iterate (return check)", Foo.class.isAssignableFrom( rs.next().getClass() ) );
 
 		ScrollableResults sr = s.createQuery("select new Foo(fo.x) from Foo fo").scroll();
 		assertTrue( "projection scroll (results)", sr.next() );
 		assertTrue( "projection scroll (return check)", Foo.class.isAssignableFrom( sr.get(0).getClass() ) );
 
 		list = s.createQuery( "select foo.long, foo.component.name, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		int count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select avg(foo.float), max(foo.component.name), count(distinct foo.id) from Foo foo" )
 				.list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Double );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Long );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select foo.long, foo.component, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof FooComponent );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 
 		s.save( new Holder("ice T") );
 		s.save( new Holder("ice cube") );
 
 		assertTrue( s.createQuery( "from java.lang.Object as o" ).list().size()==15 );
 		assertTrue( s.createQuery( "from Named" ).list().size()==7 );
 		assertTrue( s.createQuery( "from Named n where n.name is not null" ).list().size()==4 );
 		iter = s.createQuery( "from Named n" ).iterate();
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Named );
 		}
 
 		s.save( new Holder("bar") );
 		iter = s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).iterate();
 		int cnt = 0;
 		while ( iter.hasNext() ) {
 			Object[] row = (Object[]) iter.next();
 			if ( row[0]!=row[1] ) cnt++;
 		}
 		if ( !(getDialect() instanceof HSQLDialect) ) {
 			assertTrue(cnt==2);
 			assertTrue( s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).list().size()==7 );
 		}
 
 		Query qu = s.createQuery("from Named n where n.name = :name");
 		qu.getReturnTypes();
 		qu.getNamedParameters();
 
 		iter = s.createQuery( "from java.lang.Object" ).iterate();
 		int c = 0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			c++;
 		}
 		assertTrue(c==16);
 
 		s.createQuery( "select baz.code, min(baz.count) from Baz baz group by baz.code" ).iterate();
 
 		iter = s.createQuery( "selecT baz from Baz baz where baz.stringDateMap['foo'] is not null or baz.stringDateMap['bar'] = ?" )
 				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 				.iterate();
 		assertFalse( iter.hasNext() );
 		list = s.createQuery( "select baz from Baz baz where baz.stringDateMap['now'] is not null" ).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery(
 				"select baz from Baz baz where baz.stringDateMap['now'] is not null and baz.stringDateMap['big bang'] < baz.stringDateMap['now']"
 		).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery( "select index(date) from Baz baz join baz.stringDateMap date" ).list();
 		System.out.println(list);
 		assertTrue( list.size()==2 );
 
 		s.createQuery(
 				"from Foo foo where foo.integer not between 1 and 5 and foo.string not in ('cde', 'abc') and foo.string is not null and foo.integer<=3"
 		).list();
 
 		s.createQuery( "from Baz baz inner join baz.collectionComponent.nested.foos foo where foo.string is null" )
 				.list();
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			s.createQuery(
 					"from Baz baz inner join baz.fooSet where '1' in (from baz.fooSet foo where foo.string is not null)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'a' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'b' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 		}
 
 		s.createQuery( "from Foo foo join foo.foo where foo.foo in ('1','2','3')" ).list();
 		if ( !(getDialect() instanceof HSQLDialect) )
 			s.createQuery( "from Foo foo left join foo.foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.long from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') or foo.foo.long in (1,2,3)" )
 				.list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') group by foo.foo.long" )
 				.list();
 
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo2.foo where foo1.string is not null" )
 				.list();
 		s.createQuery( "from Foo foo1 left join foo1.foo.foo where foo1.string is not null" ).list();
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo1.foo.foo foo3 where foo1.string is not null" )
 				.list();
 
 		s.createQuery( "select foo.formula from Foo foo where foo.formula > 0" ).list();
 
 		int len = s.createQuery( "from Foo as foo join foo.foo as foo2 where foo2.id >'a' or foo2.id <'a'" ).list().size();
 		assertTrue(len==2);
 
 		for ( Object entity : s.createQuery( "from Holder" ).list() ) {
 			s.delete( entity );
 		}
 
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery("from Baz baz left outer join fetch baz.manyToAny").uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getManyToAny() ) );
 		assertTrue( baz.getManyToAny().size()==2 );
 		BarProxy barp = (BarProxy) baz.getManyToAny().get(0);
 		s.createQuery( "from Baz baz join baz.manyToAny" ).list();
 		assertTrue( s.createQuery( "select baz from Baz baz join baz.manyToAny a where index(a) = 0" ).list().size()==1 );
 
 		FooProxy foop = (FooProxy) s.get( Foo.class, foo.getKey() );
 		assertTrue( foop == baz.getManyToAny().get(1) );
 
 		barp.setBaz(baz);
 		assertTrue(
 				s.createQuery( "select bar from Bar bar where bar.baz.stringDateMap['now'] is not null" ).list().size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar join bar.baz b where b.stringDateMap['big bang'] < b.stringDateMap['now'] and b.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar where bar.baz.stringDateMap['big bang'] < bar.baz.stringDateMap['now'] and bar.baz.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 
 		list = s.createQuery( "select foo.string, foo.component, foo.id from Bar foo" ).list();
 		assertTrue ( ( (FooComponent) ( (Object[]) list.get(0) )[1] ).getName().equals("foo") );
 		list = s.createQuery( "select elements(baz.components) from Baz baz" ).list();
 		assertTrue( list.size()==2 );
 		list = s.createQuery( "select bc.name from Baz baz join baz.components bc" ).list();
 		assertTrue( list.size()==2 );
 		//list = s.find("select bc from Baz baz join baz.components bc");
 
 		s.createQuery("from Foo foo where foo.integer < 10 order by foo.string").setMaxResults(12).list();
 
 		s.delete(barp);
 		s.delete(baz);
 		s.delete( foop.getFoo() );
 		s.delete(foop);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeDeleteDetached() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		list.add( new Fee() );
 		baz.setFees( list );
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertFalse( Hibernate.isInitialized( baz.getFees() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( baz );
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = new Baz();
 		list = new ArrayList();
 		list.add( new Fee() );
 		list.add( new Fee() );
 		baz.setFees(list);
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		Hibernate.initialize( baz.getFees() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertTrue( baz.getFees().size() == 2 );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testForeignKeys() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Foo foo = new Foo();
 		List bag = new ArrayList();
 		bag.add(foo);
 		baz.setIdFooBag(bag);
 		baz.setFoo(foo);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonlazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.createCriteria(Baz.class)
 			//.setComment("criteria test")
 			.setFetchMode( "stringDateMap", FetchMode.JOIN )
 			.uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFooToGlarch() ) );
 		assertTrue( Hibernate.isInitialized( baz.getFooComponentToFoo() ) );
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( Hibernate.isInitialized( baz.getStringDateMap() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testReuseDeletedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.flush();
 		s.delete(baz);
 		Baz baz2 = new Baz();
 		baz2.setStringArray( new String[] {"x-y-z"} );
 		s.save(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		baz2.setStringSet( baz.getStringSet() );
 		baz2.setStringArray( baz.getStringArray() );
 		baz2.setFooArray( baz.getFooArray() );
 
 		s = openSession();
 		s.beginTransaction();
 		s.update(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		assertTrue( baz2.getStringArray().length==3 );
 		assertTrue( baz2.getStringSet().size()==3 );
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPropertyRef() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Holder h = new Holder();
 		h.setName("foo");
 		Holder h2 = new Holder();
 		h2.setName("bar");
 		h.setOtherHolder(h2);
 		Serializable hid = s.save(h);
 		Qux q = new Qux();
 		q.setHolder(h2);
 		Serializable qid = s.save(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		h = (Holder) s.load(Holder.class, hid);
 		assertEquals( h.getName(), "foo");
 		assertEquals( h.getOtherHolder().getName(), "bar");
 		Object[] res = (Object[]) s.createQuery( "from Holder h join h.otherHolder oh where h.otherHolder.name = 'bar'" )
 				.list()
 				.get(0);
 		assertTrue( res[0]==h );
 		q = (Qux) s.get(Qux.class, qid);
 		assertTrue( q.getHolder() == h.getOtherHolder() );
 		s.delete(h);
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryCollectionOfValues() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) {
 			s.createFilter( baz.getFooArray(), "where size(this.bytes) > 0" ).list();
 			s.createFilter( baz.getFooArray(), "where 0 in elements(this.bytes)" ).list();
 		}
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Baz baz join baz.fooSet foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.fooArray foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.topGlarchez g where index(g) = 'A'" ).list();
 		s.createQuery( "select index(g) from Baz baz join baz.topGlarchez g" ).list();
 
 		assertTrue( s.createQuery( "from Baz baz left join baz.stringSet" ).list().size()==3 );
 		baz = (Baz) s.createQuery( "from Baz baz join baz.stringSet str where str='foo'" ).list().get(0);
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		baz = (Baz) s.createQuery( "from Baz baz left join fetch baz.stringSet" ).list().get(0);
 		assertTrue( Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( s.createQuery( "from Baz baz join baz.stringSet string where string='foo'" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Baz baz inner join baz.components comp where comp.name='foo'" ).list().size()==1 );
 		//List bss = s.find("select baz, ss from Baz baz inner join baz.stringSet ss");
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee is not null" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp join comp.fee fee where fee.count > 0" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee.count is not null" ).list();
 
 		s.delete(baz);
 		s.delete( s.get(Glarch.class, gid) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBatchLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		SortedSet stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("bar");
 		Set fooSet = new HashSet();
 		for (int i=0; i<3; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz.setFooSet(fooSet);
 		baz.setStringSet(stringSet);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		fooSet = new HashSet();
 		for (int i=0; i<2; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz2.setFooSet(fooSet);
 		s.save(baz2);
 		Baz baz3 = new Baz();
 		stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("baz");
 		baz3.setStringSet(stringSet);
 		s.save(baz3);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz3 = (Baz) s.load( Baz.class, baz3.getCode() );
 		assertFalse( Hibernate.isInitialized(baz.getFooSet()) || Hibernate.isInitialized(baz2.getFooSet()) || Hibernate.isInitialized(baz3.getFooSet()) );
 		assertFalse( Hibernate.isInitialized(baz.getStringSet()) || Hibernate.isInitialized(baz2.getStringSet()) || Hibernate.isInitialized(baz3.getStringSet()) );
 		assertTrue( baz.getFooSet().size()==3 );
 		assertTrue( Hibernate.isInitialized(baz.getFooSet()) && Hibernate.isInitialized(baz2.getFooSet()) && Hibernate.isInitialized(baz3.getFooSet()));
 		assertTrue( baz2.getFooSet().size()==2 );
 		assertTrue( baz3.getStringSet().contains("baz") );
 		assertTrue( Hibernate.isInitialized(baz.getStringSet()) && Hibernate.isInitialized(baz2.getStringSet()) && Hibernate.isInitialized(baz3.getStringSet()));
 		assertTrue( baz.getStringSet().size()==2 && baz2.getStringSet().size()==0 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(baz3);
 		Iterator iter = new JoinedIterator( new Iterator[] { baz.getFooSet().iterator(), baz2.getFooSet().iterator() } );
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag( fooBag );
 		s.save(baz);
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( fooBag == baz.getFooBag() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized( bag ) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLateCollectionAdd() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List l = new ArrayList();
 		baz.setStringList(l);
 		l.add( "foo" );
 		Serializable id = s.save(baz);
 		l.add("bar");
 		s.flush();
 		l.add( "baz" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( baz.getStringList().size() == 3 && baz.getStringList().contains( "bar" ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testUpdate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		foo = (Foo) SerializationHelper.deserialize( SerializationHelper.serialize(foo) );
 
 		s = openSession();
 		s.beginTransaction();
 		FooProxy foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		foo2.setString("dirty");
 		foo2.setBoolean( new Boolean( false ) );
 		foo2.setBytes( new byte[] {1, 2, 3} );
 		foo2.setDate( null );
 		foo2.setShort( new Short( "69" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again" );
 		s.update(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again 2" );
 		s.update( foo2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo3 = new Foo();
 		s.load( foo3, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "update", foo2.equalsFoo(foo3) );
 		s.delete( foo3 );
 		doDelete( s, "from Glarch" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testListRemove() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		List stringList = new ArrayList();
 		List feeList = new ArrayList();
 		b.setFees(feeList);
 		b.setStringList(stringList);
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		stringList.add("foo");
 		stringList.add("bar");
 		stringList.add("baz");
 		stringList.add("glarch");
 		s.save(b);
 		s.flush();
 		stringList.remove(1);
 		feeList.remove(1);
 		s.flush();
 		s.evict(b);
 		s.refresh(b);
 		assertTrue( b.getFees().size()==3 );
 		stringList = b.getStringList();
 		assertTrue(
 			stringList.size()==3 &&
 			"baz".equals( stringList.get(1) ) &&
 			"foo".equals( stringList.get(0) )
 		);
 		s.delete(b);
 		doDelete( s, "from Fee" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollectionDupe() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag(fooBag);
 		s.save( baz );
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( fooBag ) );
 		assertTrue( fooBag == baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized(bag) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( bag ) );
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size()==2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSortables() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		b.setName("name");
 		SortedSet ss = new TreeSet();
 		ss.add( new Sortable("foo") );
 		ss.add( new Sortable("bar") );
 		ss.add( new Sortable("baz") );
 		b.setSortablez(ss);
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Criteria cr = s.createCriteria(Baz.class);
 		cr.setFetchMode( "topGlarchez", FetchMode.SELECT );
 		List result = cr
 			.addOrder( Order.asc("name") )
 			.list();
 		assertTrue( result.size()==1 );
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz left join fetch baz.sortablez order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.delete(b);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchList() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		s.flush();
 		List list = new ArrayList();
 		for ( int i=0; i<5; i++ ) {
 			Fee fee = new Fee();
 			list.add(fee);
 		}
 		baz.setFees(list);
 		list = s.createQuery( "from Foo foo, Baz baz left join fetch baz.fees" ).list();
 		assertTrue( Hibernate.isInitialized( ( (Baz) ( (Object[]) list.get(0) )[1] ).getFees() ) );
 		s.delete(foo);
 		s.delete(foo2);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBagOneToMany() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		baz.setBazez(list);
 		list.add( new Baz() );
 		s.save(baz);
 		s.flush();
 		list.add( new Baz() );
 		s.flush();
 		list.add( 0, new Baz() );
 		s.flush();
 		s.delete( list.remove(1) );
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryLockMode() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		s.flush();
 		bar.setString("changed");
 		Baz baz = new Baz();
 		baz.setFoo(bar);
 		s.save(baz);
 		Query q = s.createQuery("from Foo foo, Bar bar");
 		if ( supportsLockingNullableSideOfJoin( getDialect() ) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		Object[] result = (Object[]) q.uniqueResult();
 		Object b = result[0];
 		assertTrue( s.getCurrentLockMode(b)==LockMode.WRITE && s.getCurrentLockMode( result[1] )==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.NONE );
 		s.createQuery( "from Foo foo" ).list();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		q = s.createQuery("from Foo foo");
 		q.setLockMode( "foo", LockMode.READ );
 		q.list();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.READ );
 		s.evict( baz );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		s.delete( s.load( Baz.class, baz.getCode() ) );
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		q = s.createQuery("from Foo foo, Bar bar, Bar bar2");
 		if ( supportsLockingNullableSideOfJoin( getDialect() ) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		q.setLockMode("bar2", LockMode.READ);
 		result = (Object[]) q.list().get(0);
 		if ( supportsLockingNullableSideOfJoin( getDialect() ) ) {
 			assertTrue( s.getCurrentLockMode( result[0] )==LockMode.UPGRADE && s.getCurrentLockMode( result[1] )==LockMode.UPGRADE );
 		}
 		s.delete( result[0] );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToManyBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Serializable id = s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		baz.getFooBag().add( new Foo() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( !Hibernate.isInitialized( baz.getFooBag() ) );
 		assertTrue( baz.getFooBag().size()==1 );
 		if ( !(getDialect() instanceof HSQLDialect) ) assertTrue( Hibernate.isInitialized( baz.getFooBag().iterator().next() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testIdBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		List l = new ArrayList();
 		List l2 = new ArrayList();
 		baz.setIdFooBag(l);
 		baz.setByteBag(l2);
 		l.add( new Foo() );
 		l.add( new Bar() );
 		byte[] bytes = "ffo".getBytes();
 		l2.add(bytes);
 		l2.add( "foo".getBytes() );
 		s.flush();
 		l.add( new Foo() );
 		l.add( new Bar() );
 		l2.add( "bar".getBytes() );
 		s.flush();
 		s.delete( l.remove(3) );
 		bytes[1]='o';
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==3 );
 		assertTrue( baz.getByteBag().size()==3 );
 		bytes = "foobar".getBytes();
 		Iterator iter = baz.getIdFooBag().iterator();
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		baz.setIdFooBag(null);
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add(bytes);
 		assertTrue( baz.getByteBag().size()==5 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==0 );
 		assertTrue( baz.getByteBag().size()==5 );
 		baz.getIdFooBag().add( new Foo() );
 		iter = baz.getByteBag().iterator();
 		iter.next();
 		iter.remove();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==1 );
 		assertTrue( baz.getByteBag().size()==4 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private boolean isOuterJoinFetchingDisabled() {
 		return new Integer(0).equals( sessionFactory().getSettings().getMaximumFetchDepth() );
 	}
 
 	@Test
 	public void testForceOuterJoin() throws Exception {
 		if ( isOuterJoinFetchingDisabled() ) {
 			return;
 		}
 
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		FooComponent fc = new FooComponent();
 		fc.setGlarch(g);
 		FooProxy f = new Foo();
 		FooProxy f2 = new Foo();
 		f.setComponent(fc);
 		f.setFoo(f2);
 		s.save(f2);
 		Serializable id = s.save(f);
 		Serializable gid = s.getIdentifier( f.getComponent().getGlarch() );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evict(Foo.class);
 
 		s = openSession();
 		s.beginTransaction();
 		f = (FooProxy) s.load(Foo.class, id);
 		assertFalse( Hibernate.isInitialized(f) );
 		assertTrue( Hibernate.isInitialized( f.getComponent().getGlarch() ) ); //outer-join="true"
 		assertFalse( Hibernate.isInitialized( f.getFoo() ) ); //outer-join="auto"
 		assertEquals( s.getIdentifier( f.getComponent().getGlarch() ), gid );
 		s.delete(f);
 		s.delete( f.getFoo() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmptyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable id = s.save( new Baz() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz baz = (Baz) s.load(Baz.class, id);
 		Set foos = baz.getFooSet();
 		assertTrue( foos.size() == 0 );
 		Foo foo = new Foo();
 		foos.add( foo );
 		s.save(foo);
 		s.flush();
 		s.delete(foo);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testOneToOneGenerator() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		X x = new X();
 		Y y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		x.getXxs().add( new X.XX(x) );
 		Serializable id = s.save(y);
 		assertEquals( id, s.save(x) );
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		s.getTransaction().commit();
 		s.close();
 		assertEquals( new Long(x.getId()), y.getId() );
 
 		s = openSession();
 		s.beginTransaction();
 		x = new X();
 		y = new Y();
 		x.setY(y);
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
index 875ca2598e..29d3af3eb4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
@@ -1,1254 +1,1260 @@
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
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.type.StandardBasicTypes;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 
 public class ParentChildTest extends LegacyTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/ParentChild.hbm.xml",
 			"legacy/FooBar.hbm.xml",
 		 	"legacy/Baz.hbm.xml",
 		 	"legacy/Qux.hbm.xml",
 		 	"legacy/Glarch.hbm.xml",
 		 	"legacy/Fum.hbm.xml",
 		 	"legacy/Fumm.hbm.xml",
 		 	"legacy/Fo.hbm.xml",
 		 	"legacy/One.hbm.xml",
 		 	"legacy/Many.hbm.xml",
 		 	"legacy/Immutable.hbm.xml",
 		 	"legacy/Fee.hbm.xml",
 		 	"legacy/Vetoer.hbm.xml",
 		 	"legacy/Holder.hbm.xml",
 		 	"legacy/Simple.hbm.xml",
 		 	"legacy/Container.hbm.xml",
 		 	"legacy/Circular.hbm.xml",
 		 	"legacy/Stuff.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testReplicate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Container baz = new Container();
 		Contained f = new Contained();
 		List list = new ArrayList();
 		list.add(baz);
 		f.setBag(list);
 		List list2 = new ArrayList();
 		list2.add(f);
 		baz.setBag(list2);
 		s.save(f);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.OVERWRITE);
 		// HHH-2378
 		SessionImpl x = (SessionImpl)s;
 		EntityEntry entry = x.getPersistenceContext().getEntry( baz );
 		assertNull(entry.getVersion());
 		// ~~~~~~~
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.IGNORE);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.delete(f);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryOneToOne() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable id = s.save( new Parent() );
 		assertTrue( s.createQuery( "from Parent p left join fetch p.child" ).list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Parent p = (Parent) s.createQuery("from Parent p left join fetch p.child").uniqueResult();
 		assertTrue( p.getChild()==null );
 		s.createQuery( "from Parent p join p.child c where c.x > 0" ).list();
 		s.createQuery( "from Child c join c.parent p where p.x > 0" ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.get(Parent.class, id) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testProxyReuse() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		FooProxy foo = new Foo();
 		FooProxy foo2 = new Foo();
 		Serializable id = s.save(foo);
 		Serializable id2 = s.save(foo2);
 		foo2.setInt( 1234567 );
 		foo.setInt(1234);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		foo2 = (FooProxy) s.load(Foo.class, id2);
 		assertFalse( Hibernate.isInitialized( foo ) );
 		Hibernate.initialize( foo2 );
 		Hibernate.initialize(foo);
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		assertTrue( foo2.getComponent().getImportantDates().length == 4 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo.setKey( "xyzid" );
 		foo.setFloat( new Float( 1.2f ) );
 		foo2.setKey( (String) id ); //intentionally id, not id2!
 		foo2.setFloat( new Float( 1.3f ) );
 		foo2.getDependent().setKey( null );
 		foo2.getComponent().getSubcomponent().getFee().setKey(null);
 		assertFalse( foo2.getKey().equals( id ) );
 		s.save( foo );
 		s.update( foo2 );
 		assertEquals( foo2.getKey(), id );
 		assertTrue( foo2.getInt() == 1234567 );
 		assertEquals( foo.getKey(), "xyzid" );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		assertTrue( foo.getInt() == 1234567 );
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		String feekey = foo.getDependent().getKey();
 		String fookey = foo.getKey();
 		s.delete( foo );
 		s.delete( s.get(Foo.class, id2) );
 		s.delete( s.get(Foo.class, "xyzid") );
 // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
 		assertEquals( 2, doDelete( s, "from java.lang.Object" ) );
 		t.commit();
 		s.close();
 		
 		//to account for new id rollback shit
 		foo.setKey(fookey);
 		foo.getDependent().setKey( feekey );
 		foo.getComponent().setGlarch( null );
 		foo.getComponent().setSubcomponent(null);
 		
 		s = openSession();
 		t = s.beginTransaction();
 		//foo.getComponent().setGlarch(null); //no id property!
 		s.replicate( foo, ReplicationMode.OVERWRITE );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Foo refoo = (Foo) s.get(Foo.class, id);
 		assertEquals( feekey, refoo.getDependent().getKey() );
 		s.delete(refoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplexCriteria() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		Map topGlarchez = new HashMap();
 		baz.setTopGlarchez(topGlarchez);
 		Glarch g1 = new Glarch();
 		g1.setName("g1");
 		s.save(g1);
 		Glarch g2 = new Glarch();
 		g2.setName("g2");
 		s.save(g2);
 		g1.setProxyArray( new GlarchProxy[] {g2} );
 		topGlarchez.put( new Character('1'),g1 );
 		topGlarchez.put( new Character('2'), g2);
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		s.save(foo1);
 		s.save(foo2);
 		baz.getFooSet().add(foo1);
 		baz.getFooSet().add(foo2);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		LockMode lockMode = supportsLockingNullableSideOfJoin( getDialect() ) ? LockMode.UPGRADE : LockMode.READ;
 
 		Criteria crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") )
 			.createCriteria("proxyArray")
 				.add( Restrictions.eqProperty("name", "name") )
 				.add( Restrictions.eq("name", "g2") )
 				.add( Restrictions.gt("x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") )
 			.add( Restrictions.eq("string", "a string") )
 			.add( Restrictions.lt("integer", new Integer(-665) ) );
 		crit.createCriteria("fooArray")
 				// this is the bit causing the problems; creating the criteria on fooArray does not add it to FROM,
 				// and so restriction below leads to an invalid reference.
 			.add( Restrictions.eq("string", "a string") )
 			.setLockMode(lockMode);
 
 		List list = crit.list();
 		assertTrue( list.size()==2 );
 		
 		s.createCriteria(Glarch.class).setLockMode(LockMode.UPGRADE).list();
 		s.createCriteria(Glarch.class).setLockMode(Criteria.ROOT_ALIAS, LockMode.UPGRADE).list();
 		
 		g2.setName(null);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		
 		list = s.createCriteria(Baz.class).add( Restrictions.isEmpty("fooSet") ).list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.isNotEmpty("fooSet") ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.sizeEq("fooSet", 2) ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 		
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class)
 			.setLockMode(lockMode);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.gt( "x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 		list = crit.list();
 
 		assertTrue( list.size()==4 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 
 		list = s.createCriteria(Baz.class)
 			.createCriteria("fooSet")
 				.createCriteria("foo")
 					.createCriteria("component.glarch")
 						.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		list = s.createCriteria(Baz.class)
 			.createAlias("fooSet", "foo")
 			.createAlias("foo.foo", "foo2")
 			.setLockMode("foo2", lockMode)
 			.add( Restrictions.isNull("foo2.component.glarch") )
 			.createCriteria("foo2.component.glarch")
 				.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 
 		list = crit.list();
 		assertTrue( list.size()==2 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 		
 		s.createCriteria(Child.class).setFetchMode("parent", FetchMode.JOIN).list();
 
 		doDelete( s, "from Glarch g" );
 		s.delete( s.get(Foo.class, foo1.getKey() ) );
 		s.delete( s.get(Foo.class, foo2.getKey() ) );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testArrayHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b left join fetch b.fooArray").uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testArrayCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria(Baz.class).createCriteria( "fooArray" ).uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b").uniqueResult();
 		assertFalse( Hibernate.isInitialized( baz.getFoo() ) );
 		assertTrue( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria( Baz.class ).uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneGet() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.get( Baz.class, baz.getCode() );
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhere() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getParts().add(p1);
 		baz.getParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.parts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhereManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setMoreParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getMoreParts().add(p1);
 		baz.getMoreParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.moreParts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getMoreParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setName("s");
 		s1.setCount(0);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		Container c = new Container();
 		Contained cd = new Contained();
 		List bag = new ArrayList();
 		bag.add(cd);
 		c.setBag(bag);
 		List l = new ArrayList();
 		l.add(s1);
 		l.add(s3);
 		l.add(s2);
 		c.setOneToMany(l);
 		l = new ArrayList();
 		l.add(s1);
 		l.add(null);
 		l.add(s2);
 		c.setManyToMany(l);
 		s.save(c);
 		Container cx = new Container();
 		s.save(cx);
 		Simple sx = new Simple( Long.valueOf(5) );
 		sx.setCount(5);
 		sx.setName("s");
 		s.save( sx );
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.oneToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.oneToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.manyToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.oneToMany[2 - 2].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.manyToMany[(3+1)/4-1].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		if ( ! ( getDialect() instanceof MySQLDialect ) && !(getDialect() instanceof org.hibernate.dialect.TimesTenDialect) ) {
 			assertTrue(
 					s.createQuery( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" )
 							.list()
 							.size() == 1
 			);
 		}
 		assertTrue( s.contains(cd) );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) )  {
 			s.createFilter( c.getBag(), "where 0 in elements(this.bag)" ).list();
 			s.createFilter( c.getBag(), "where 0 in elements(this.lazyBag)" ).list();
 		}
 		s.createQuery( "select count(comp.name) from ContainerX c join c.components comp" ).list();
 		s.delete(cd);
 		s.delete(c);
 		s.delete(s1);
 		s.delete(s2);
 		s.delete(s3);
 		s.delete(cx);
 		s.delete(sx);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		Child c = new Child();
 		c.setParent(p);
 		p.setChild(c);
 		s.save(p);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 parent", p!=null );
 		c.setCount(32);
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 update", p.getCount()==66 );
 		assertTrue( "1-1 update", c.getCount()==32 );
 		assertTrue(
 			"1-1 query",
 				s.createQuery( "from Child c where c.parent.count=66" ).list().size()==1
 		);
 		assertTrue(
 			"1-1 query",
 			( (Object[]) s.createQuery( "from Parent p join p.child c where p.count=66" ).list().get(0) ).length==2
 		);
 		s.createQuery( "select c, c.parent from Child c order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent from Child c where c.parent.count=66 order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent, c.parent.count from Child c order by c.parent.count" ).iterate();
 		List result = s.createQuery( "FROM Parent AS p WHERE p.count = ?" )
 				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
 				.list();
 		assertEquals( "1-1 query", 1, result.size() );
 		s.delete(c); s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentNullChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		s.save(p);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( p.getChild()==null );
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( "null 1-1 update", p.getCount()==66 );
 		assertTrue( p.getChild()==null );
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		c.setManyToMany( new ArrayList() );
 		c.setBag( new ArrayList() );
 		Simple s1 = new Simple( Long.valueOf(12) );
 		Simple s2 = new Simple( Long.valueOf(-1) );
 		s1.setCount(123); s2.setCount(654);
 		Contained c1 = new Contained();
 		c1.setBag( new ArrayList() );
 		c1.getBag().add(c);
 		c.getBag().add(c1);
 		c.getManyToMany().add(s1);
 		c.getManyToMany().add(s2);
 		Serializable cid = s.save(c);
 		s.save( s1 );
 		s.save( s2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==1 );
 		assertTrue( c.getManyToMany().size()==2 );
 		c1 = (Contained) c.getBag().iterator().next();
 		assertTrue( c.getBag().size()==1 );
 		c.getBag().remove(c1);
 		c1.getBag().remove(c);
 		assertTrue( c.getManyToMany().remove(0)!=null );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==0 );
 		assertTrue( c.getManyToMany().size()==1 );
 		c1 = (Contained) s.load( Contained.class, new Long(c1.getId()) );
 		assertTrue( c1.getBag().size()==0 );
 		assertEquals( 1, doDelete( s, "from ContainerX c" ) );
 		assertEquals( 1, doDelete( s, "from Contained" ) );
 		assertEquals( 2, doDelete( s, "from Simple" ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testContainer() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Simple x = new Simple( Long.valueOf(1) );
 		x.setCount(123);
 		Simple y = new Simple( Long.valueOf(0) );
 		y.setCount(456);
 		s.save( x );
 		s.save( y );
 		List o2m = new ArrayList();
 		o2m.add(x); o2m.add(null); o2m.add(y);
 		List m2m = new ArrayList();
 		m2m.add(x); m2m.add(null); m2m.add(y);
 		c.setOneToMany(o2m); c.setManyToMany(m2m);
 		List comps = new ArrayList();
 		Container.ContainerInnerClass ccic = new Container.ContainerInnerClass();
 		ccic.setName("foo");
 		ccic.setSimple(x);
 		comps.add(ccic);
 		comps.add(null);
 		ccic = new Container.ContainerInnerClass();
 		ccic.setName("bar");
 		ccic.setSimple(y);
 		comps.add(ccic);
 		HashSet compos = new HashSet();
 		compos.add(ccic);
 		c.setComposites(compos);
 		c.setComponents(comps);
 		One one = new One();
 		Many many = new Many();
 		HashSet manies = new HashSet();
 		manies.add(many);
 		one.setManies(manies);
 		many.setOne(one);
 		ccic.setMany(many);
 		ccic.setOne(one);
 		s.save(one);
 		s.save(many);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Long count = (Long) s.createQuery("select count(*) from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").uniqueResult();
 		assertTrue( count.intValue()==1 );
 		List res = s.createQuery(
 				"select c, s from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'"
 		).list();
 		assertTrue(res.size()==1);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		ccic = (Container.ContainerInnerClass) c.getComponents().get(2);
 		assertTrue( ccic.getMany().getOne()==ccic.getOne() );
 		assertTrue( c.getComponents().size()==3 );
 		assertTrue( c.getComposites().size()==1 );
 		assertTrue( c.getOneToMany().size()==3 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(2)!=null );
 		for ( int i=0; i<3; i++ ) {
 			assertTrue( c.getManyToMany().get(i) == c.getOneToMany().get(i) );
 		}
 		Object o1 = c.getOneToMany().get(0);
 		Object o2 = c.getOneToMany().remove(2);
 		c.getOneToMany().set(0, o2);
 		c.getOneToMany().set(1, o1);
 		o1 = c.getComponents().remove(2);
 		c.getComponents().set(0, o1);
 		c.getManyToMany().set( 0, c.getManyToMany().get(2) );
 		Container.ContainerInnerClass ccic2 = new Container.ContainerInnerClass();
 		ccic2.setName("foo");
 		ccic2.setOne(one);
 		ccic2.setMany(many);
 		ccic2.setSimple( (Simple) s.load(Simple.class, new Long(0) ) );
 		c.getComposites().add(ccic2);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( c.getOneToMany().size()==2 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(1)!=null );
 		( (Container.ContainerInnerClass) c.getComponents().get(0) ).setName("a different name");
 		( (Container.ContainerInnerClass) c.getComposites().iterator().next() ).setName("once again");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( ( (Container.ContainerInnerClass) c.getComponents().get(0) ).getName().equals("a different name") );
 		Iterator iter = c.getComposites().iterator();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( ( (Container.ContainerInnerClass) iter.next() ).getName().equals("once again") ) found = true;
 		}
 		assertTrue(found);
 		c.getOneToMany().clear();
 		c.getManyToMany().clear();
 		c.getComposites().clear();
 		c.getComponents().clear();
 		doDelete( s, "from Simple" );
 		doDelete( s, "from Many" );
 		doDelete( s, "from One" );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		assertTrue( c.getComponents().size()==0 );
 		assertTrue( c.getComposites().size()==0 );
 		assertTrue( c.getOneToMany().size()==0 );
 		assertTrue( c.getManyToMany().size()==0 );
 		s.delete(c);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeCompositeElements() throws Exception {
 		Container c = new Container();
 		List list = new ArrayList();
 		c.setCascades(list);
 		Container.ContainerInnerClass cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		Session s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 
 		c = new Container();
 		s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		list = new ArrayList();
 		c.setCascades(list);
 		cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBag() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Contained c1 = new Contained();
 		Contained c2 = new Contained();
 		c.setBag( new ArrayList() );
 		c.getBag().add(c1);
 		c.getBag().add(c2);
 		c1.getBag().add(c);
 		c2.getBag().add(c);
 		s.save(c);
 		c.getBag().add(c2);
 		c2.getBag().add(c);
 		c.getLazyBag().add(c1);
 		c1.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		c.getLazyBag().size();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c3 = new Contained();
 		//c.getBag().add(c3);
 		//c3.getBag().add(c);
 		c.getLazyBag().add(c3);
 		c3.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c4 = new Contained();
 		c.getLazyBag().add(c4);
 		c4.getLazyBag().add(c);
 		assertTrue( c.getLazyBag().size()==3 ); //forces initialization
 		//s.save(c4);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Iterator i = c.getBag().iterator();
 		int j=0;
 		while ( i.hasNext() ) {
 			assertTrue( i.next()!=null );
 			j++;
 		}
 		assertTrue(j==3);
 		assertTrue( c.getLazyBag().size()==3 );
 		s.delete(c);
 		c.getBag().remove(c2);
 		Iterator iter = c.getBag().iterator();
 		j=0;
 		while ( iter.hasNext() ) {
 			j++;
 			s.delete( iter.next() );
 		}
 		assertTrue(j==2);
 		s.delete( s.load(Contained.class, new Long( c4.getId() ) ) );
 		s.delete( s.load(Contained.class, new Long( c3.getId() ) ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCircularCascade() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Circular c = new Circular();
 		c.setClazz(Circular.class);
 		c.setOther( new Circular() );
 		c.getOther().setOther( new Circular() );
 		c.getOther().getOther().setOther(c);
 		c.setAnyEntity( c.getOther() );
 		String id = (String) s.save(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		c.getOther().getOther().setClazz(Foo.class);
 		tx.commit();
 		s.close();
 		c.getOther().setClazz(Qux.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		c.getOther().getOther().setClazz(Bar.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		assertTrue( c.getOther().getOther().getClazz()==Bar.class);
 		assertTrue( c.getOther().getClazz()==Qux.class);
 		assertTrue( c.getOther().getOther().getOther()==c);
 		assertTrue( c.getAnyEntity()==c.getOther() );
 		assertEquals( 3, doDelete( s, "from Universe" ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteEmpty() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		assertEquals( 0, doDelete( s, "from Simple" ) );
 		assertEquals( 0, doDelete( s, "from Universe" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLocking() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setCount(1);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		Simple s4 = new Simple( Long.valueOf(4) );
 		s4.setCount(4);
 		Simple s5 = new Simple( Long.valueOf(5) );
 		s5.setCount(5);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		s.save( s4 );
 		s.save( s5 );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.WRITE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ || s.getCurrentLockMode(s1)==LockMode.NONE ); //depends if cache is enabled
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.get(Simple.class, new Long(4), LockMode.UPGRADE_NOWAIT);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 		s5 = (Simple) s.get(Simple.class, new Long(5), LockMode.UPGRADE_SKIPLOCKED);
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.UPGRADE );
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.load(Simple.class, new Long(4), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 		s5 = (Simple) s.load(Simple.class, new Long(5), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s.lock(s1, LockMode.UPGRADE_NOWAIT);
 		s.lock(s4, LockMode.NONE);
 		s.lock(s5, LockMode.UPGRADE_SKIPLOCKED);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		tx.commit();
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
 		s.lock(s1, LockMode.READ); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ );
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE_NOWAIT); //upgrade
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE_NOWAIT );
 		s.lock(s4, LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 
 		s4.setName("s4");
 		s.flush();
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
 		s.delete(s1); s.delete(s2); s.delete(s3); s.delete(s4); s.delete(s5);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testObjectType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Parent g = new Parent();
 		Foo foo = new Foo();
 		g.setAny(foo);
 		s.save(g);
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (Parent) s.load( Parent.class, new Long( g.getId() ) );
 		assertTrue( g.getAny()!=null && g.getAny() instanceof FooProxy );
 		s.delete( g.getAny() );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
-	@Test
+	 @Test
 	public void testLoadAfterNonExists() throws HibernateException, SQLException {
 		Session session = openSession();
 		if ( ( getDialect() instanceof MySQLDialect ) || ( getDialect() instanceof IngresDialect ) ) {
 			session.doWork(
 					new AbstractWork() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							connection.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
 						}
 					}
 			);
 		}
 		session.getTransaction().begin();
 
 		// First, prime the fixture session to think the entity does not exist
 		try {
 			session.load( Simple.class, new Long(-1) );
 			fail();
 		}
 		catch(ObjectNotFoundException onfe) {
+			if (  getDialect() instanceof TeradataDialect ){
+				session.getTransaction().rollback();
+				session.getTransaction().begin();
+			}
 			// this is correct
 		}
 
 		// Next, lets create that entity "under the covers"
 		Session anotherSession = sessionFactory().openSession();
 		anotherSession.beginTransaction();
 		Simple myNewSimple = new Simple( Long.valueOf(-1) );
 		myNewSimple.setName("My under the radar Simple entity");
 		myNewSimple.setAddress("SessionCacheTest.testLoadAfterNonExists");
 		myNewSimple.setCount(1);
 		myNewSimple.setDate( new Date() );
 		myNewSimple.setPay( Float.valueOf( 100000000 ) );
 		anotherSession.save( myNewSimple );
 		anotherSession.getTransaction().commit();
 		anotherSession.close();
 
 		// Now, lets make sure the original session can see the created row...
 		session.clear();
 		try {
 			Simple dummy = (Simple) session.get( Simple.class, Long.valueOf(-1) );
 			assertNotNull("Unable to locate entity Simple with id = -1", dummy);
 			session.delete( dummy );
 		}
 		catch(ObjectNotFoundException onfe) {
 			fail("Unable to locate entity Simple with id = -1");
 		}
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
index 6956140ef3..c93c429bd4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
@@ -1,700 +1,701 @@
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
 package org.hibernate.test.legacy;
 
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
+import org.hibernate.dialect.TeradataDialect;
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 import org.hibernate.Query;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.dialect.function.SQLFunction;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 
 public class SQLFunctionsTest extends LegacyTestCase {
 	private static final Logger log = Logger.getLogger( SQLFunctionsTest.class );
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/AltSimple.hbm.xml",
 			"legacy/Broken.hbm.xml",
 			"legacy/Blobber.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testDialectSQLFunctions() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Iterator iter = s.createQuery( "select max(s.count) from Simple s" ).iterate();
 
 		if ( getDialect() instanceof MySQLDialect ) assertTrue( iter.hasNext() && iter.next()==null );
 
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple Dialect Function Test");
 		simple.setAddress("Simple Address");
 		simple.setPay( Float.valueOf(45.8f) );
 		simple.setCount(2);
 		s.save( simple );
 
 		// Test to make sure allocating an specified object operates correctly.
 		assertTrue(
 				s.createQuery( "select new org.hibernate.test.legacy.S(s.count, s.address) from Simple s" ).list().size() == 1
 		);
 
 		// Quick check the base dialect functions operate correctly
 		assertTrue(
 				s.createQuery( "select max(s.count) from Simple s" ).list().size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select count(*) from Simple s" ).list().size() == 1
 		);
 
 		if ( getDialect() instanceof Oracle9iDialect ) {
 			// Check Oracle Dialect mix of dialect functions - no args (no parenthesis and single arg functions
 			List rset = s.createQuery( "select s.name, sysdate, trunc(s.pay), round(s.pay) from Simple s" ).list();
 			assertNotNull("Name string should have been returned",(((Object[])rset.get(0))[0]));
 			assertNotNull("Todays Date should have been returned",(((Object[])rset.get(0))[1]));
 			assertEquals("trunc(45.8) result was incorrect ", Float.valueOf(45), ( (Object[]) rset.get(0) )[2] );
 			assertEquals("round(45.8) result was incorrect ", Float.valueOf(46), ( (Object[]) rset.get(0) )[3] );
 
 			simple.setPay(new Float(-45.8));
 			s.update(simple);
 
 			// Test type conversions while using nested functions (Float to Int).
 			rset = s.createQuery( "select abs(round(s.pay)) from Simple s" ).list();
 			assertEquals("abs(round(-45.8)) result was incorrect ", Float.valueOf( 46 ), rset.get(0));
 
 			// Test a larger depth 3 function example - Not a useful combo other than for testing
 			assertTrue(
 					s.createQuery( "select trunc(round(sysdate)) from Simple s" ).list().size() == 1
 			);
 
 			// Test the oracle standard NVL funtion as a test of multi-param functions...
 			simple.setPay(null);
 			s.update(simple);
 			Integer value = (Integer) s.createQuery(
 					"select MOD( NVL(s.pay, 5000), 2 ) from Simple as s where s.id = 10"
 			).list()
 					.get(0);
 			assertTrue( 0 == value.intValue() );
 		}
 
 		if ( (getDialect() instanceof HSQLDialect) ) {
 			// Test the hsql standard MOD funtion as a test of multi-param functions...
 			Integer value = (Integer) s.createQuery( "select MOD(s.count, 2) from Simple as s where s.id = 10" )
 					.list()
 					.get(0);
 			assertTrue( 0 == value.intValue() );
 		}
 
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSetProperties() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		Query q = s.createQuery("from Simple s where s.name=:name and s.count=:count");
 		q.setProperties(simple);
 		assertTrue( q.list().get(0)==simple );
 		//misuse of "Single" as a propertyobject, but it was the first testclass i found with a collection ;)
 		Single single = new Single() { // trivial hack to test properties with arrays.
 			String[] getStuff() { return (String[]) getSeveral().toArray(new String[getSeveral().size()]); }
 		};
 
 		List l = new ArrayList();
 		l.add("Simple 1");
 		l.add("Slimeball");
 		single.setSeveral(l);
 		q = s.createQuery("from Simple s where s.name in (:several)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
 		q = s.createQuery("from Simple s where s.name in :several");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
 		q = s.createQuery("from Simple s where s.name in (:stuff)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
 		q = s.createQuery("from Simple s where s.name in :stuff");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSetPropertiesMap() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		Map parameters = new HashMap();
 		parameters.put("name", simple.getName());
 		parameters.put("count", new Integer(simple.getCount()));
 
 		Query q = s.createQuery("from Simple s where s.name=:name and s.count=:count");
 		q.setProperties((parameters));
 		assertTrue( q.list().get(0)==simple );
 
 		List l = new ArrayList();
 		l.add("Simple 1");
 		l.add("Slimeball");
 		parameters.put("several", l);
 		q = s.createQuery("from Simple s where s.name in (:several)");
 		q.setProperties(parameters);
 		assertTrue( q.list().get(0)==simple );
 
 
 		parameters.put("stuff", l.toArray(new String[0]));
 		q = s.createQuery("from Simple s where s.name in (:stuff)");
 		q.setProperties(parameters);
 		assertTrue( q.list().get(0)==simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testBroken() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Broken b = new Fixed();
 		b.setId( new Long(123));
 		b.setOtherId("foobar");
 		s.save(b);
 		s.flush();
 		b.setTimestamp( new Date() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(b);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		b = (Broken) s.load( Broken.class, b );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete(b);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNothinToUpdate() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCachedQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName( "Simple 1" );
 		Long id = (Long) s.save( simple );
 		assertEquals( Long.valueOf( 10 ), id );
 		assertEquals( Long.valueOf( 10 ), simple.getId() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setString("name", "Simple 2");
 		q.setCacheable(true);
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCachedQueryRegion() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSQLFunctions() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 
 		if ( getDialect() instanceof DB2Dialect) {
 			s.createQuery( "from Simple s where repeat('foo', 3) = 'foofoofoo'" ).list();
 			s.createQuery( "from Simple s where repeat(s.name, 3) = 'foofoofoo'" ).list();
 			s.createQuery( "from Simple s where repeat( lower(s.name), 3 + (1-1) / 2) = 'foofoofoo'" ).list();
 		}
 
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.name ) ='SIMPLE 1'" ).list().size()==1
 		);
 		if ( !(getDialect() instanceof HSQLDialect) ) {
 			assertTrue(
 					s.createQuery(
 							"from Simple s where not( upper( s.name ) ='yada' or 1=2 or 'foo'='bar' or not('foo'='foo') or 'foo' like 'bar' )"
 					).list()
 							.size()==1
 			);
 		}
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof SybaseDialect) && !(getDialect() instanceof SQLServerDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof InterbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) { //My SQL has a funny concatenation operator
 			assertTrue(
 					s.createQuery( "from Simple s where lower( s.name || ' foo' ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 		if ( (getDialect() instanceof SybaseDialect) ) {
 			assertTrue(
 					s.createQuery( "from Simple s where lower( s.name + ' foo' ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 		if ( (getDialect() instanceof MckoiDialect) || (getDialect() instanceof TimesTenDialect)) {
 			assertTrue(
 					s.createQuery( "from Simple s where lower( concat(s.name, ' foo') ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 
 		Simple other = new Simple( Long.valueOf(20) );
 		other.setName("Simple 2");
 		other.setCount(12);
 		simple.setOther(other);
 		s.save( other );
 		//s.find("from Simple s where s.name ## 'cat|rat|bag'");
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.other.name ) ='SIMPLE 2'" ).list().size()==1
 		);
 		assertTrue(
 				s.createQuery( "from Simple s where not ( upper( s.other.name ) ='SIMPLE 2' )" ).list().size()==0
 		);
 		assertTrue(
 				s.createQuery(
 						"select distinct s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2"
 				).list()
 						.size()==1
 		);
 		assertTrue(
 				s.createQuery(
 						"select s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2 order by s.other.count"
 				).list()
 						.size()==1
 		);
 		Simple min = new Simple( Long.valueOf(30) );
 		min.setCount(-1);
 		s.save( min );
 		if ( ! (getDialect() instanceof MySQLDialect) && ! (getDialect() instanceof HSQLDialect) ) { //My SQL has no subqueries
 			assertTrue(
 					s.createQuery( "from Simple s where s.count > ( select min(sim.count) from Simple sim )" )
 							.list()
 							.size()==2
 			);
 			t.commit();
 			t = s.beginTransaction();
 			assertTrue(
 					s.createQuery(
 							"from Simple s where s = some( select sim from Simple sim where sim.count>=0 ) and s.count >= 0"
 					).list()
 							.size()==2
 			);
 			assertTrue(
 					s.createQuery(
 							"from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count ) and s.other.count > 0"
 					).list()
 							.size()==1
 			);
 		}
 
 		Iterator iter = s.createQuery( "select sum(s.count) from Simple s group by s.count having sum(s.count) > 10" )
 				.iterate();
 		assertTrue( iter.hasNext() );
 		assertEquals( Long.valueOf(12), iter.next() );
 		assertTrue( !iter.hasNext() );
 		if ( ! (getDialect() instanceof MySQLDialect) ) {
 			iter = s.createQuery( "select s.count from Simple s group by s.count having s.count = 12" ).iterate();
 			assertTrue( iter.hasNext() );
 		}
 
 		s.createQuery(
 				"select s.id, s.count, count(t), max(t.date) from Simple s, Simple t where s.count = t.count group by s.id, s.count order by s.count"
 		).iterate();
 
 		Query q = s.createQuery("from Simple s");
 		q.setMaxResults(10);
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Simple s");
 		q.setMaxResults(1);
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s");
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Simple s where s.name = ?");
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name = ? and upper(s.name) = ?");
 		q.setString(1, "SIMPLE 1");
 		q.setString(0, "Simple 1");
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("from Simple s where s.name = :foo and upper(s.name) = :bar or s.count=:count or s.count=:count + 1");
 		q.setParameter("bar", "SIMPLE 1");
 		q.setString("foo", "Simple 1");
 		q.setInteger("count", 69);
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("select s.id from Simple s");
 		q.setFirstResult(1);
 		q.setMaxResults(2);
 		iter = q.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Long );
 			i++;
 		}
 		assertTrue(i==2);
 		q = s.createQuery("select all s, s.other from Simple s where s = :s");
 		q.setParameter("s", simple);
 		assertTrue( q.list().size()==1 );
 
 
 		q = s.createQuery("from Simple s where s.name in (:name_list) and s.count > :count");
 		HashSet set = new HashSet();
 		set.add("Simple 1"); set.add("foo");
 		q.setParameterList( "name_list", set );
 		q.setParameter("count", Integer.valueOf( -1 ) );
 		assertTrue( q.list().size()==1 );
 
 		ScrollableResults sr = s.createQuery("from Simple s").scroll();
 		sr.next();
 		sr.get(0);
 		sr.close();
 
 		s.delete(other);
 		s.delete(simple);
 		s.delete(min);
 		t.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testBlobClob() throws Exception {
 		// Sybase does not support ResultSet.getBlob(String)
-		if ( getDialect() instanceof SybaseDialect || getDialect() instanceof Sybase11Dialect || getDialect() instanceof SybaseASE15Dialect || getDialect() instanceof SybaseAnywhereDialect ) {
+		if ( getDialect() instanceof SybaseDialect || getDialect() instanceof Sybase11Dialect || getDialect() instanceof SybaseASE15Dialect || getDialect() instanceof SybaseAnywhereDialect  || getDialect() instanceof TeradataDialect) {
 			return;
 		}
 		Session s = openSession();
 		s.beginTransaction();
 		Blobber b = new Blobber();
 		b.setBlob( s.getLobHelper().createBlob( "foo/bar/baz".getBytes() ) );
 		b.setClob( s.getLobHelper().createClob("foo/bar/baz") );
 		s.save(b);
 		//s.refresh(b);
 		//assertTrue( b.getClob() instanceof ClobImpl );
 		s.flush();
 
 		s.refresh(b);
 		//b.getBlob().setBytes( 2, "abc".getBytes() );
 		b.getClob().getSubString(2, 3);
 		//b.getClob().setString(2, "abc");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		Blobber b2 = new Blobber();
 		s.save(b2);
 		b2.setBlob( b.getBlob() );
 		b.setBlob(null);
 		//assertTrue( b.getClob().getSubString(1, 3).equals("fab") );
 		b.getClob().getSubString(1, 6);
 		//b.getClob().setString(1, "qwerty");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		b.setClob( s.getLobHelper().createClob("xcvfxvc xcvbx cvbx cvbx cvbxcvbxcvbxcvb") );
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		assertTrue( b.getClob().getSubString(1, 7).equals("xcvfxvc") );
 		//b.getClob().setString(5, "1234567890");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSqlFunctionAsAlias() throws Exception {
 		String functionName = locateAppropriateDialectFunctionNameForAliasTest();
 		if (functionName == null) {
             log.info("Dialect does not list any no-arg functions");
 			return;
 		}
 
         log.info("Using function named [" + functionName + "] for 'function as alias' test");
 		String query = "select " + functionName + " from Simple as " + functionName + " where " + functionName + ".id = 10";
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List result = s.createQuery( query ).list();
 		assertTrue( result.size() == 1 );
 		assertTrue(result.get(0) instanceof Simple);
 		s.delete( result.get(0) );
 		t.commit();
 		s.close();
 	}
 
 	private String locateAppropriateDialectFunctionNameForAliasTest() {
 		for (Iterator itr = getDialect().getFunctions().entrySet().iterator(); itr.hasNext(); ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final SQLFunction function = (SQLFunction) entry.getValue();
 			if ( !function.hasArguments() && !function.hasParenthesesIfNoArguments() ) {
 				return (String) entry.getKey();
 			}
 		}
 		return null;
 	}
 
 	@Test
 	public void testCachedQueryOnInsert() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s");
 		List list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Simple simple2 = new Simple( Long.valueOf(12) );
 		simple2.setCount(133);
 		s.save( simple2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		Iterator i = list.iterator();
 		while ( i.hasNext() ) s.delete( i.next() );
 		t.commit();
 		s.close();
 
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java b/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
index c3f2cc66f2..f00944be9f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
@@ -1,196 +1,203 @@
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
 package org.hibernate.test.lob;
 import java.sql.Blob;
 
 import junit.framework.AssertionFailedError;
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Assert;
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertNotNull;
 
 /**
  * Tests lazy materialization of data mapped by
  * {@link org.hibernate.type.BlobType}, as well as bounded and unbounded
  * materialization and mutation.
  *
  * @author Steve Ebersole
  */
 @RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 public class BlobLocatorTest extends BaseCoreFunctionalTestCase {
 	private static final long BLOB_SIZE = 10000L;
 
 	public String[] getMappings() {
 		return new String[] { "lob/LobMappings.hbm.xml" };
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-6637",
+			comment = "Teradata requires locator to be used in same session where it was created/retrieved"
+	)
 	public void testBoundedBlobLocatorAccess() throws Throwable {
 		byte[] original = buildByteArray( BLOB_SIZE, true );
 		byte[] changed = buildByteArray( BLOB_SIZE, false );
 		byte[] empty = new byte[] {};
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setBlobLocator( s.getLobHelper().createBlob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test mutation via setting the new clob data...
 		if ( getDialect().supportsLobValueChangePropogation() ) {
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 			entity.getBlobLocator().truncate( 1 );
 			entity.getBlobLocator().setBytes( 1, changed );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 			assertNotNull( entity.getBlobLocator() );
 			Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 			assertEquals( changed, extractData( entity.getBlobLocator() ) );
 			entity.getBlobLocator().truncate( 1 );
 			entity.getBlobLocator().setBytes( 1, original );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// test mutation via supplying a new clob locator instance...
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 		assertNotNull( entity.getBlobLocator() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 		entity.setBlobLocator( s.getLobHelper().createBlob( changed ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test empty blob
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( changed, extractData( entity.getBlobLocator() ) );
 		entity.setBlobLocator( s.getLobHelper().createBlob( empty ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		if ( entity.getBlobLocator() != null) {
 			Assert.assertEquals( empty.length, entity.getBlobLocator().length() );
 			assertEquals( empty, extractData( entity.getBlobLocator() ) );
 		}
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck.class,
 			comment = "database/driver does not support materializing a LOB locator outside the owning transaction"
 	)
 	public void testUnboundedBlobLocatorAccess() throws Throwable {
 		// Note: unbounded mutation of the underlying lob data is completely
 		// unsupported; most databases would not allow such a construct anyway.
 		// Thus here we are only testing materialization...
 
 		byte[] original = buildByteArray( BLOB_SIZE, true );
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setBlobLocator( Hibernate.getLobCreator( s ).createBlob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		// load the entity with the clob locator, and close the session/transaction;
 		// at that point it is unbounded...
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public static byte[] extractData(Blob blob) throws Exception {
 		return blob.getBytes( 1, ( int ) blob.length() );
 	}
 
 
 	public static byte[] buildByteArray(long size, boolean on) {
 		byte[] data = new byte[(int)size];
 		data[0] = mask( on );
 		for ( int i = 0; i < size; i++ ) {
 			data[i] = mask( on );
 			on = !on;
 		}
 		return data;
 	}
 
 	private static byte mask(boolean on) {
 		return on ? ( byte ) 1 : ( byte ) 0;
 	}
 
 	public static void assertEquals(byte[] val1, byte[] val2) {
 		if ( !ArrayHelper.isEquals( val1, val2 ) ) {
 			throw new AssertionFailedError( "byte arrays did not match" );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java b/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
index 95c5add9c8..38757534ea 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
@@ -1,188 +1,195 @@
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
 package org.hibernate.test.lob;
 
 import java.sql.Clob;
 
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.dialect.SybaseASE157Dialect;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.type.descriptor.java.DataHelper;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * Tests lazy materialization of data mapped by
  * {@link org.hibernate.type.ClobType} as well as bounded and unbounded
  * materialization and mutation.
  *
  * @author Steve Ebersole
  */
 @RequiresDialectFeature(
 		value = DialectChecks.SupportsExpectedLobUsagePattern.class,
 		comment = "database/driver does not support expected LOB usage pattern"
 )
 public class ClobLocatorTest extends BaseCoreFunctionalTestCase {
 	private static final int CLOB_SIZE = 10000;
 
 	public String[] getMappings() {
 		return new String[] { "lob/LobMappings.hbm.xml" };
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-6637",
+			comment = "Teradata requires locator to be used in same session where it was created/retrieved"
+	)
 	public void testBoundedClobLocatorAccess() throws Throwable {
 		String original = buildString( CLOB_SIZE, 'x' );
 		String changed = buildString( CLOB_SIZE, 'y' );
 		String empty = "";
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setClobLocator( s.getLobHelper().createClob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test mutation via setting the new clob data...
 		if ( getDialect().supportsLobValueChangePropogation() ) {
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 			entity.getClobLocator().truncate( 1 );
 			entity.getClobLocator().setString( 1, changed );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 			assertNotNull( entity.getClobLocator() );
 			assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 			assertEquals( changed, extractData( entity.getClobLocator() ) );
 			entity.getClobLocator().truncate( 1 );
 			entity.getClobLocator().setString( 1, original );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// test mutation via supplying a new clob locator instance...
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
 		assertNotNull( entity.getClobLocator() );
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 		entity.setClobLocator( s.getLobHelper().createClob( changed ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test empty clob
 		if ( !(getDialect() instanceof SybaseASE157Dialect) ) { // Skip for Sybase. HHH-6425
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 			assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 			assertEquals( changed, extractData( entity.getClobLocator() ) );
 			entity.setClobLocator( s.getLobHelper().createClob( empty ) );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
 			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 			if ( entity.getClobLocator() != null) {
 				assertEquals( empty.length(), entity.getClobLocator().length() );
 				assertEquals( empty, extractData( entity.getClobLocator() ) );
 			}
 			s.delete( entity );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck.class,
 			comment = "database/driver does not support materializing a LOB locator outside the owning transaction"
 	)
 	public void testUnboundedClobLocatorAccess() throws Throwable {
 		// Note: unbounded mutation of the underlying lob data is completely
 		// unsupported; most databases would not allow such a construct anyway.
 		// Thus here we are only testing materialization...
 
 		String original = buildString( CLOB_SIZE, 'x' );
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setClobLocator( s.getLobHelper().createClob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		// load the entity with the clob locator, and close the session/transaction;
 		// at that point it is unbounded...
 		s = openSession();
 		s.beginTransaction();
 		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public static String extractData(Clob clob) throws Exception {
 		return DataHelper.extractString( clob.getCharacterStream() );
 	}
 
 	public static String buildString(int size, char baseChar) {
 		StringBuilder buff = new StringBuilder();
 		for( int i = 0; i < size; i++ ) {
 			buff.append( baseChar );
 		}
 		return buff.toString();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/locking/A.java b/hibernate-core/src/test/java/org/hibernate/test/locking/A.java
index 98a36c84af..2d3e941162 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/locking/A.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/locking/A.java
@@ -1,67 +1,69 @@
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
 package org.hibernate.test.locking;
 
+import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.annotations.GenericGenerator;
 
 /**
  * @author Steve Ebersole
  */
 @Entity
 @Table( name = "T_LOCK_A" )
 public class A {
 	private Long id;
 	private String value;
 
 	public A() {
 	}
 
 	public A(String value) {
 		this.value = value;
 	}
 
 	@Id
 	@GeneratedValue( generator = "increment" )
 	@GenericGenerator( name = "increment", strategy = "increment" )
 	public Long getId() {
 		return id;
 	}
 
 	public void setId(Long id) {
 		this.id = id;
 	}
 
+	@Column(name="`value`")
 	public String getValue() {
 		return value;
 	}
 
 	public void setValue(String value) {
 		this.value = value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java b/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
index 25aa8acd36..620b2b1645 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
@@ -1,246 +1,250 @@
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
 package org.hibernate.test.locking;
 
 import java.util.concurrent.TimeoutException;
 
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialects;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.Session;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.exception.GenericJDBCException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.async.Executable;
 import org.hibernate.testing.async.TimedExecutor;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * Make sure that directly specifying lock modes, even though deprecated, continues to work until removed.
  *
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-5275")
+@SkipForDialects( {
 @SkipForDialect(value=SybaseASE15Dialect.class, strictMatching=true,
-		comment = "skip this test on Sybase ASE 15.5, but run it on 15.7, see HHH-6820")
+		comment = "skip this test on Sybase ASE 15.5, but run it on 15.7, see HHH-6820"),
+})
 public class LockModeTest extends BaseCoreFunctionalTestCase {
 	private Long id;
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return  new Class[] { A.class };
 	}
 
 	@Override
 	public void prepareTest() throws Exception {
 		Session session = sessionFactory().openSession();
 		session.beginTransaction();
 		id = (Long) session.save( new A( "it" ) );
 		session.getTransaction().commit();
 		session.close();
 	}
 	@Override
 	protected boolean isCleanupTestDataRequired(){return true;}
 
 	@Test
 	@SuppressWarnings( {"deprecation"})
 	public void testLoading() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.get( A.class, id, LockMode.PESSIMISTIC_WRITE );
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testLegacyCriteria() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createCriteria( A.class )
 					.setLockMode( LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testLegacyCriteriaAliasSpecific() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createCriteria( A.class )
 					.setLockMode( "this", LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testQuery() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createQuery( "from A a" )
 					.setLockMode( "a", LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testQueryUsingLockOptions() {
 		// todo : need an association here to make sure the alias-specific lock modes are applied correctly
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		s1.createQuery( "from A a" )
 				.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
 				.uniqueResult();
 		s1.createQuery( "from A a" )
 				.setLockOptions( new LockOptions().setAliasSpecificLockMode( "a", LockMode.PESSIMISTIC_WRITE ) )
 				.uniqueResult();
 		s1.getTransaction().commit();
 		s1.close();
 	}
 
 	private void nowAttemptToUpdateRow() {
 		// here we just need to open a new connection (database session and transaction) and make sure that
 		// we are not allowed to acquire exclusive locks to that row and/or write to that row.  That may take
 		// one of two forms:
 		//		1) either the get-with-lock or the update fails immediately with a sql error
 		//		2) either the get-with-lock or the update blocks indefinitely (in real world, it would block
 		//			until the txn in the calling method completed.
 		// To be able to cater to the second type, we run this block in a separate thread to be able to "time it out"
 
 		try {
 			new TimedExecutor( 10*1000, 1*1000 ).execute(
 					new Executable() {
 						Session s;
 
 						@Override
 						public void execute() {
 							s = sessionFactory().openSession();
 							s.beginTransaction();
 							try {
 								// load with write lock to deal with databases that block (wait indefinitely) direct attempts
 								// to write a locked row
 								A it = (A) s.get(
 										A.class,
 										id,
 										new LockOptions( LockMode.PESSIMISTIC_WRITE ).setTimeOut( LockOptions.NO_WAIT )
 								);
 								it.setValue( "changed" );
 								s.flush();
 								fail( "Pessimistic lock not obtained/held" );
 							}
 							catch ( Exception e ) {
 								// grr, exception can be any number of types based on database
 								// 		see HHH-6887
 								if ( LockAcquisitionException.class.isInstance( e )
 										|| GenericJDBCException.class.isInstance( e )
 										|| PessimisticLockException.class.isInstance( e ) ) {
 									// "ok"
 								}
 								else {
 									fail( "Unexpected error type testing pessimistic locking : " + e.getClass().getName() );
 								}
 							}
 							finally {
 								shutDown();
 							}
 						}
 
 						private void shutDown() {
 							try {
 								s.getTransaction().rollback();
 								s.close();
 							}
 							catch (Exception ignore) {
 							}
 							s = null;
 						}
 
 						@Override
 						public void timedOut() {
 							s.cancelQuery();
 							shutDown();
 						}
 					}
 			);
 		}
 		catch (TimeoutException e) {
 			// timeout is ok, see rule #2 above
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/map/UserGroup.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/map/UserGroup.hbm.xml
index a2eac87aa0..860ebdfdf6 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/map/UserGroup.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/map/UserGroup.hbm.xml
@@ -1,53 +1,53 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <!-- 
      
 -->
 
 <hibernate-mapping package="org.hibernate.test.map">
 	
 	<class name="Group" table="groups">
 		<id name="name"/>
 		<map name="users" cascade="persist" table="group_user">
 			<key column="groupName"/>
 			<map-key formula="lower(personName)" type="string"/>
 			<many-to-many column="personName" class="User"/>
 		</map>
 	</class>
 	
 	<class name="User" table="users">
 		<id name="name"/>
-		<property name="password"/>
+		<property name="password" column="`password`"/>
 		<map name="session" cascade="persist,save-update,delete,delete-orphan">
 			<key column="userName" not-null="true"/>
 			<map-key formula="lower(name)" type="string"/>
 			<one-to-many class="SessionAttribute"/>
 		</map>
 	</class>
 	
 	<class name="SessionAttribute" table="session_attributes">
 		<id name="id" access="field">
 			<generator class="native"/>
 		</id>
 		<property name="name" not-null="true" update="false"/>
 		<property name="stringData"/>
 		<property name="objectData"/>
 	</class>
 	
 	<sql-query name="userSessionData">
 		<return alias="u" class="User"/>
 		<return-join alias="s" property="u.session"/>
 		select 
-			lower(u.name) as {u.name}, lower(u.password) as {u.password}, 
+			lower(u.name) as {u.name}, lower(u."password") as {u.password}, 
 			lower(s.userName) as {s.key}, lower(s.name) as {s.index}, s.id as {s.element}, 
 			{s.element.*}
 		from users u 
 		join session_attributes s on lower(s.userName) = lower(u.name)
 		where u.name like :uname
 	</sql-query>	
 	
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/onetoone/formula/Person.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/onetoone/formula/Person.hbm.xml
index 6b179d5dd5..d6b03a7d2c 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/onetoone/formula/Person.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/onetoone/formula/Person.hbm.xml
@@ -1,33 +1,33 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <hibernate-mapping package="org.hibernate.test.onetoone.formula">
 
 	<class name="Person">
 		<id name="name"/>
 		<one-to-one name="address" cascade="all" constrained="false">
 			<formula>name</formula>
 			<formula>'HOME'</formula>
 		</one-to-one>
 		<one-to-one name="mailingAddress" constrained="false">
 			<formula>name</formula>
 			<formula>'MAILING'</formula>
 		</one-to-one>
 	</class>
 	
 	<class name="Address" batch-size="2" 
 			check="addressType in ('MAILING', 'HOME', 'BUSINESS')">
 		<composite-id>
 			<key-many-to-one name="person" 
 					column="personName"/>
 			<key-property name="type" 
 					column="addressType"/>
 		</composite-id>
-		<property name="street" type="text"/>
+		<property name="street"/>
 		<property name="state"/>
 		<property name="zip"/>
 	</class>
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/querycache/Enrolment.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/querycache/Enrolment.hbm.xml
index 2bf484077d..447c76afe3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/Enrolment.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/Enrolment.hbm.xml
@@ -1,90 +1,90 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <hibernate-mapping package="org.hibernate.test.querycache">
 	
 	<class name="Course">
 		<id name="courseCode">
 			<generator class="assigned"/>
 		</id>
 		<property name="description"/>
         <set name="courseMeetings" inverse="true" cascade="all-delete-orphan" lazy="false">
             <key column="courseCode"/>
             <one-to-many class="CourseMeeting"/>
         </set>
 	</class>
 
     <class name="CourseMeeting">
         <composite-id name="id" class="CourseMeetingId">
             <key-property name="courseCode"/>
-            <key-property name="day"/>
+            <key-property name="day" column="`day`"/>
             <key-property name="period"/>
             <key-property name="location"/>
         </composite-id>
         <many-to-one name="course" insert="false" update="false" lazy="false">
             <column name="courseCode"/>
         </many-to-one>
     </class>
 
 	<class name="Student">
 		<id name="studentNumber">
 		    <column name="studentId"/>
 			<generator class="assigned"/>
 		</id>
         <component name="name">
             <property name="first" column="name_first" not-null="true"/>
             <property name="middle" column="name_middle" not-null="false"/>
             <property name="last" column="name_last" not-null="true"/>
         </component>
 		<set name="enrolments" inverse="true" cascade="delete">
 			<key column="studentId"/>
 			<one-to-many class="Enrolment"/>
 		</set>
         <map name="addresses" table="addresses" cascade="all,delete" lazy="true">
             <key column="studentNumber"/>
             <map-key column="addressType" type="string"/>
             <one-to-many class="Address"/>
         </map>
         <many-to-one name="preferredCourse" column="preferredCourseCode" lazy="proxy"/>
         <list name="secretCodes" lazy="false">
             <key>
                 <column name="studentNumber"/>
             </key>
             <index column="i"/>
             <element column="secretCode" type="int"/>
         </list>
 	</class>
 
     <class name="Address">
         <id name="id">
 			<generator class="increment"/>
 		</id>
         <property name="addressType"/>
         <property name="street"/>
         <property name="city"/>
         <property name="stateProvince"/>
         <property name="postalCode"/>
         <property name="country"/>
         <many-to-one name="student" class="Student" column="studentNumber" not-null="false"/>
     </class>
 
 	<class name="Enrolment">
 		<composite-id>
 			<key-property name="studentNumber">
 				<column name="studentId"/>
 			</key-property>
 			<key-property name="courseCode"/>
 		</composite-id>
 		<many-to-one name="student" insert="false" update="false" lazy="proxy">
 			<column name="studentId"/>
 		</many-to-one>
 		<many-to-one name="course" insert="false" update="false" lazy="false">
 			<column name="courseCode"/>
 		</many-to-one>
 		<property name="semester" type="short" not-null="true"/>
 		<property name="year" column="`year`" type="short" not-null="true"/>
 	</class>
 
 </hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/Employee.java b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/Employee.java
index d75f730c75..d5e6882369 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/Employee.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/Employee.java
@@ -1,55 +1,57 @@
 // $Id: Employee.java 5899 2005-02-24 20:08:04Z steveebersole $
 package org.hibernate.test.subclassfilter;
+import javax.persistence.Column;
 import java.util.HashSet;
 import java.util.Set;
 
 /**
  * Implementation of Employee.
  *
  * @author Steve Ebersole
  */
 public class Employee extends Person {
+	@Column(name="`title`")
     private String title;
 	private String department;
 	private Employee manager;
 	private Set minions = new HashSet();
 
 	public Employee() {
 	}
 
 	public Employee(String name) {
 		super( name );
 	}
 
 	public String getTitle() {
 		return title;
 	}
 
 	public void setTitle(String title) {
 		this.title = title;
 	}
 
 	public String getDepartment() {
 		return department;
 	}
 
 	public void setDepartment(String department) {
 		this.department = department;
 	}
 
 	public Employee getManager() {
 		return manager;
 	}
 
 	public void setManager(Employee manager) {
 		this.manager = manager;
 	}
 
 	public Set getMinions() {
 		return minions;
 	}
 
 	public void setMinions(Set minions) {
 		this.minions = minions;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/UnionSubclassFilterTest.java b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/UnionSubclassFilterTest.java
index d79bf7aefb..46052ba641 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/UnionSubclassFilterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/UnionSubclassFilterTest.java
@@ -1,147 +1,154 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.subclassfilter;
 
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 public class UnionSubclassFilterTest extends BaseCoreFunctionalTestCase {
 	public final String[] getMappings() {
 		return new String[] { "subclassfilter/union-subclass.hbm.xml" };
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "uses Teradata reserved word - title"
+	)
 	@SuppressWarnings( {"unchecked"})
 	public void testFiltersWithUnionSubclass() {
 		Session s = openSession();
 		s.enableFilter( "region" ).setParameter( "userRegion", "US" );
 		Transaction t = s.beginTransaction();
 
 		prepareTestData( s );
 		s.clear();
 
 		List results;
 		Iterator itr;
 
 		results = s.createQuery( "from Person" ).list();
 		assertEquals( "Incorrect qry result count", 4, results.size() );
 		s.clear();
 
 		results = s.createQuery( "from Employee" ).list();
 		assertEquals( "Incorrect qry result count", 2, results.size() );
 		s.clear();
 
 		results = new ArrayList( new HashSet( s.createQuery( "from Person as p left join fetch p.minions" ).list() ) );
 		assertEquals( "Incorrect qry result count", 4, results.size() );
 		itr = results.iterator();
 		while ( itr.hasNext() ) {
 			// find john
 			final Person p = ( Person ) itr.next();
 			if ( p.getName().equals( "John Doe" ) ) {
 				Employee john = ( Employee ) p;
 				assertEquals( "Incorrect fecthed minions count", 1, john.getMinions().size() );
 				break;
 			}
 		}
 		s.clear();
 
 		results = new ArrayList( new HashSet( s.createQuery( "from Employee as p left join fetch p.minions" ).list() ) );
 		assertEquals( "Incorrect qry result count", 2, results.size() );
 		itr = results.iterator();
 		while ( itr.hasNext() ) {
 			// find john
 			final Person p = ( Person ) itr.next();
 			if ( p.getName().equals( "John Doe" ) ) {
 				Employee john = ( Employee ) p;
 				assertEquals( "Incorrect fecthed minions count", 1, john.getMinions().size() );
 				break;
 			}
 		}
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		for ( Object entity : s.createQuery( "from Person" ).list() ) {
 			s.delete( entity );
 		}
 		t.commit();
 		s.close();
 
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private void prepareTestData(Session s) {
 		Employee john = new Employee( "John Doe" );
 		john.setCompany( "JBoss" );
 		john.setDepartment( "hr" );
 		john.setTitle( "hr guru" );
 		john.setRegion( "US" );
 
 		Employee polli = new Employee( "Polli Wog" );
 		polli.setCompany( "JBoss" );
 		polli.setDepartment( "hr" );
 		polli.setTitle( "hr novice" );
 		polli.setRegion( "US" );
 		polli.setManager( john );
 		john.getMinions().add( polli );
 
 		Employee suzie = new Employee( "Suzie Q" );
 		suzie.setCompany( "JBoss" );
 		suzie.setDepartment( "hr" );
 		suzie.setTitle( "hr novice" );
 		suzie.setRegion( "EMEA" );
 		suzie.setManager( john );
 		john.getMinions().add( suzie );
 
 		Customer cust = new Customer( "John Q Public" );
 		cust.setCompany( "Acme" );
 		cust.setRegion( "US" );
 		cust.setContactOwner( john );
 
 		Person ups = new Person( "UPS guy" );
 		ups.setCompany( "UPS" );
 		ups.setRegion( "US" );
 
 		s.save( john );
 		s.save( cust );
 		s.save( ups );
 
 		s.flush();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/discrim-subclass.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/discrim-subclass.hbm.xml
index ce24c680ac..1ac087d660 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/discrim-subclass.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/discrim-subclass.hbm.xml
@@ -1,44 +1,44 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <hibernate-mapping package="org.hibernate.test.subclassfilter">
 
 	<class name="Person" discriminator-value="0" table="SPerson">
 
 		<id name="id" column="person_id">
 			<generator class="increment"/>
 		</id>
 
 		<discriminator type="int"
 			formula="CASE WHEN company is null THEN 0 WHEN company = 'JBoss' THEN 1 ELSE 2 END"/>
 
 		<property name="name"/>
 		<property name="company"/>
 		<property name="region"/>
 
 		<subclass name="Employee" discriminator-value="1">
-			<property name="title"/>
+			<property name="title" column="`title`"/>
 			<property name="department" column="dept"/>
 			<many-to-one name="manager" column="mgr_id" class="Employee" cascade="none"/>
 			<set name="minions" inverse="true" lazy="true" cascade="all">
 				<key column="mgr_id"/>
 				<one-to-many class="Employee"/>
 				<filter name="region" condition="region = :userRegion"/>
 			</set>
 		</subclass>
 
 		<subclass name="Customer" discriminator-value="2">
 			<many-to-one name="contactOwner" class="Employee"/>
 		</subclass>
 
 		<filter name="region" condition="region = :userRegion"/>
 
 	</class>
 
 	<filter-def name="region">
 		<filter-param name="userRegion" type="string"/>
     </filter-def>
 
 </hibernate-mapping>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/joined-subclass.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/joined-subclass.hbm.xml
index 6c481161ba..6393ec31ed 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/joined-subclass.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/subclassfilter/joined-subclass.hbm.xml
@@ -1,43 +1,43 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 <hibernate-mapping package="org.hibernate.test.subclassfilter">
 
 	<class name="Person" table="JPerson">
 
 		<id name="id" column="person_id">
 			<generator class="increment"/>
 		</id>
 
 		<property name="name"/>
 		<property name="company"/>
 		<property name="region"/>
 
 		<joined-subclass name="Employee" table="JEmployee">
 			<key column="person_id"/>
 
-			<property name="title"/>
+			<property name="title" column="`title`"/>
 			<property name="department" column="dept"/>
 			<many-to-one name="manager" class="Employee" column="mgr_id" cascade="none"/>
 			<set name="minions" inverse="true" cascade="all" lazy="true">
 				<key column="mgr_id"/>
 				<one-to-many class="Employee"/>
 			</set>
         </joined-subclass>
 
 		<joined-subclass name="Customer" table="JCustomer">
 			<key column="person_id"/>
 
 			<many-to-one name="contactOwner" class="Employee"/>
 		</joined-subclass>
 
 		<filter name="region" condition="region = :userRegion"/>
 	</class>
 
 	<filter-def name="region">
 		<filter-param name="userRegion" type="string"/>
     </filter-def>
 
 </hibernate-mapping>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/unionsubclass2/UnionSubclassTest.java b/hibernate-core/src/test/java/org/hibernate/test/unionsubclass2/UnionSubclassTest.java
index 9059834764..d2868c4142 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/unionsubclass2/UnionSubclassTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/unionsubclass2/UnionSubclassTest.java
@@ -1,255 +1,272 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.unionsubclass2;
 
 import java.math.BigDecimal;
 import java.util.List;
 
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 public class UnionSubclassTest extends BaseCoreFunctionalTestCase {
 	protected String[] getMappings() {
 		return new String[] { "unionsubclass2/Person.hbm.xml" };
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "SQL uses Teradata reserved word: title"
+	)
 	public void testUnionSubclass() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		
 		Employee mark = new Employee();
 		mark.setName("Mark");
 		mark.setTitle("internal sales");
 		mark.setSex('M');
 		mark.setAddress("buckhead");
 		mark.setZip("30305");
 		mark.setCountry("USA");
 		
 		Customer joe = new Customer();
 		joe.setName("Joe");
 		joe.setAddress("San Francisco");
 		joe.setZip("XXXXX");
 		joe.setCountry("USA");
 		joe.setComments("Very demanding");
 		joe.setSex('M');
 		joe.setSalesperson(mark);
 		
 		Person yomomma = new Person();
 		yomomma.setName("mum");
 		yomomma.setSex('F');
 		
 		s.save(yomomma);
 		s.save(mark);
 		s.save(joe);
 		
 		assertEquals( s.createQuery("from java.io.Serializable").list().size(), 0 );
 		
 		assertEquals( s.createQuery("from Person").list().size(), 3 );
 		assertEquals( s.createQuery("from Person p where p.class = Customer").list().size(), 1 );
 		assertEquals( s.createQuery("from Person p where p.class = Person").list().size(), 1 );
 		assertEquals( s.createQuery("from Person p where type(p) in :who").setParameter("who", Customer.class).list().size(), 1 );
 		assertEquals( s.createQuery("from Person p where type(p) in :who").setParameterList("who", new Class[] {Customer.class, Person.class}).list().size(), 2 );
 		s.clear();
 
 		List customers = s.createQuery("from Customer c left join fetch c.salesperson").list();
 		for ( Object customer : customers ) {
 			Customer c = (Customer) customer;
 			assertTrue( Hibernate.isInitialized( c.getSalesperson() ) );
 			assertEquals( c.getSalesperson().getName(), "Mark" );
 		}
 		assertEquals( customers.size(), 1 );
 		s.clear();
 		
 		customers = s.createQuery("from Customer").list();
 		for ( Object customer : customers ) {
 			Customer c = (Customer) customer;
 			assertFalse( Hibernate.isInitialized( c.getSalesperson() ) );
 			assertEquals( c.getSalesperson().getName(), "Mark" );
 		}
 		assertEquals( customers.size(), 1 );
 		s.clear();
 		
 
 		mark = (Employee) s.get( Employee.class, Long.valueOf( mark.getId() ) );
 		joe = (Customer) s.get( Customer.class, Long.valueOf( joe.getId() ) );
 		
  		mark.setZip("30306");
 		assertEquals( s.createQuery("from Person p where p.address.zip = '30306'").list().size(), 1 );
 
         s.createCriteria( Person.class ).add(
                 Restrictions.in( "address", new Address[] { mark.getAddress(),
                         joe.getAddress() } ) ).list();
 		
 		s.delete(mark);
 		s.delete(joe);
 		s.delete(yomomma);
 		assertTrue( s.createQuery("from Person").list().isEmpty() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "SQL uses Teradata reserved word: title"
+	)
 	public void testQuerySubclassAttribute() {
 		if ( getDialect() instanceof HSQLDialect ) {
 			return; // TODO : why??
 		}
 		
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Person p = new Person();
 		p.setName("Emmanuel");
 		p.setSex('M');
 		s.persist(p);
 		Employee q = new Employee();
 		q.setName("Steve");
 		q.setSex('M');
 		q.setTitle("Mr");
 		q.setSalary( new BigDecimal(1000) );
 		s.persist(q);
 
 		List result = s.createQuery("from Person where salary > 100").list();
 		assertEquals( result.size(), 1 );
 		assertSame( result.get(0), q );
 		
 		result = s.createQuery("from Person where salary > 100 or name like 'E%'").list();
 		assertEquals( result.size(), 2 );		
 
 		result = s.createCriteria(Person.class)
 			.add( Property.forName("salary").gt( new BigDecimal(100) ) )
 			.list();
 		assertEquals( result.size(), 1 );
 		assertSame( result.get(0), q );
 
 		result = s.createQuery("select salary from Person where salary > 100").list();
 		assertEquals( result.size(), 1 );
 		assertEquals( ( (BigDecimal) result.get(0) ).intValue(), 1000 );
 		
 		s.delete(p);
 		s.delete(q);
 		t.commit();
 		s.close();
 	}
 
 	@Test
+	@SkipForDialect(
+			value = TeradataDialect.class,
+			jiraKey = "HHH-8190",
+			comment = "SQL uses Teradata reserved word: title"
+	)
 	public void testCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		final double HEIGHT_INCHES = 73;
 		final double HEIGHT_CENTIMETERS = HEIGHT_INCHES * 2.54d;
 		Person p = new Person();
 		p.setName("Emmanuel");
 		p.setSex('M');
 		p.setHeightInches(HEIGHT_INCHES);
 		s.persist(p);
 		final double PASSWORD_EXPIRY_WEEKS = 4;
 		final double PASSWORD_EXPIRY_DAYS = PASSWORD_EXPIRY_WEEKS * 7d;
 		Employee e = new Employee();
 		e.setName("Steve");
 		e.setSex('M');
 		e.setTitle("Mr");		
 		e.setPasswordExpiryDays(PASSWORD_EXPIRY_DAYS);
 		s.persist(e);
 		s.flush();
 		
 		// Test value conversion during insert
 		// Value returned by Oracle native query is a Types.NUMERIC, which is mapped to a BigDecimalType;
 		// Cast returned value to Number then call Number.doubleValue() so it works on all dialects.
 		Double heightViaSql =
 				( (Number)s.createSQLQuery("select height_centimeters from UPerson where name='Emmanuel'").uniqueResult() ).doubleValue();
 		assertEquals(HEIGHT_CENTIMETERS, heightViaSql, 0.01d);
 		Double expiryViaSql =
 				( (Number)s.createSQLQuery("select pwd_expiry_weeks from UEmployee where person_id=?")
 						.setLong(0, e.getId())
 						.uniqueResult()
 				).doubleValue();
 		assertEquals(PASSWORD_EXPIRY_WEEKS, expiryViaSql, 0.01d);
 		
 		// Test projection
 		Double heightViaHql = (Double)s.createQuery("select p.heightInches from Person p where p.name = 'Emmanuel'").uniqueResult();
 		assertEquals(HEIGHT_INCHES, heightViaHql, 0.01d);
 		Double expiryViaHql = (Double)s.createQuery("select e.passwordExpiryDays from Employee e where e.name = 'Steve'").uniqueResult();
 		assertEquals(PASSWORD_EXPIRY_DAYS, expiryViaHql, 0.01d);
 		
 		// Test restriction and entity load via criteria
 		p = (Person)s.createCriteria(Person.class)
 			.add(Restrictions.between("heightInches", HEIGHT_INCHES - 0.01d, HEIGHT_INCHES + 0.01d))
 			.uniqueResult();
 		assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01d);
 		e = (Employee)s.createCriteria(Employee.class)
 			.add(Restrictions.between("passwordExpiryDays", PASSWORD_EXPIRY_DAYS - 0.01d, PASSWORD_EXPIRY_DAYS + 0.01d))
 			.uniqueResult();
 		assertEquals(PASSWORD_EXPIRY_DAYS, e.getPasswordExpiryDays(), 0.01d);
 		
 		// Test predicate and entity load via HQL
 		p = (Person)s.createQuery("from Person p where p.heightInches between ? and ?")
 			.setDouble(0, HEIGHT_INCHES - 0.01d)
 			.setDouble(1, HEIGHT_INCHES + 0.01d)
 			.uniqueResult();
 		assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01d);
 		e = (Employee)s.createQuery("from Employee e where e.passwordExpiryDays between ? and ?")
 			.setDouble(0, PASSWORD_EXPIRY_DAYS - 0.01d)
 			.setDouble(1, PASSWORD_EXPIRY_DAYS + 0.01d)
 			.uniqueResult();
 		assertEquals(PASSWORD_EXPIRY_DAYS, e.getPasswordExpiryDays(), 0.01d);
 		
 		// Test update
 		p.setHeightInches(1);
 		e.setPasswordExpiryDays(7);
 		s.flush();
 		heightViaSql =
 				( (Number)s.createSQLQuery("select height_centimeters from UPerson where name='Emmanuel'").uniqueResult() )
 						.doubleValue();
 		assertEquals(2.54d, heightViaSql, 0.01d);
 		expiryViaSql =
 				( (Number)s.createSQLQuery("select pwd_expiry_weeks from UEmployee where person_id=?")
 						.setLong(0, e.getId())
 						.uniqueResult()
 				).doubleValue();
 		assertEquals(1d, expiryViaSql, 0.01d);
 		s.delete(p);
 		s.delete(e);
 		t.commit();
 		s.close();
 		
 	}
 	
 	
 }
 
