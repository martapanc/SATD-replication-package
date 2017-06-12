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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.AttributeConverter;
import javax.persistence.SharedCacheMode;

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
