diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index fa841f5c64..f8a5235ac2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -310,2004 +310,2015 @@ public class Configuration implements Serializable {
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
 												defaultCatalog,
 												defaultSchema
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
 									defaultCatalog,
 									defaultSchema
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
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
 		LOG.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
-				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
+				Map defaults = reflectionManager.getDefaults();
+				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
+				// Set default schema name if orm.xml declares it.
+				final String schema = (String) defaults.get( "schema" );
+				if ( StringHelper.isNotEmpty( schema ) ) {
+					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
+				}
+				// Set default catalog name if orm.xml declares it.
+				final String catalog = (String) defaults.get( "catalog" );
+				if ( StringHelper.isNotEmpty( catalog ) ) {
+					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
+				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
 		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = classMapping.getTable().getQuotedName();
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
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
 			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
 			while ( it.hasNext() ) {
 				final FkSecondPass pass = it.next();
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch ( RecoverableException e ) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = ( RuntimeException ) e.getCause();
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
 		Set done = new HashSet();
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
 			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
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
 
 	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
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
 	 * @return The build {@link SessionFactory}
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
 	 * Rterieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
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
 	 * @return The value curently associated with that property name; may be null.
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
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
index 39d6036fa5..515a398d85 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
@@ -1,161 +1,163 @@
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
 package org.hibernate.cfg.annotations.reflection;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.lang.reflect.AnnotatedElement;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.EntityListeners;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQuery;
 import javax.persistence.SequenceGenerator;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.TableGenerator;
 
 import org.dom4j.Element;
 
 import org.hibernate.annotations.common.reflection.AnnotationReader;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.java.JavaMetadataProvider;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * MetadataProvider aware of the JPA Deployment descriptor
  *
  * @author Emmanuel Bernard
  */
 public class JPAMetadataProvider implements MetadataProvider, Serializable {
 	private transient MetadataProvider delegate = new JavaMetadataProvider();
 	private transient Map<Object, Object> defaults;
 	private transient Map<AnnotatedElement, AnnotationReader> cache = new HashMap<AnnotatedElement, AnnotationReader>(100);
 
 	//all of the above can be safely rebuilt from XMLContext: only XMLContext this object is serialized
 	private XMLContext xmlContext = new XMLContext();
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		delegate = new JavaMetadataProvider();
 		cache = new HashMap<AnnotatedElement, AnnotationReader>(100);
 	}
 
 	public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
 		AnnotationReader reader = cache.get( annotatedElement );
 		if (reader == null) {
 			if ( xmlContext.hasContext() ) {
 				reader = new JPAOverridenAnnotationReader( annotatedElement, xmlContext );
 			}
 			else {
 				reader = delegate.getAnnotationReader( annotatedElement );
 			}
 			cache.put(annotatedElement, reader);
 		}
 		return reader;
 	}
 
 	public Map<Object, Object> getDefaults() {
 		if ( defaults == null ) {
 			defaults = new HashMap<Object, Object>();
 			XMLContext.Default xmlDefaults = xmlContext.getDefault( null );
 
+			defaults.put( "schema", xmlDefaults.getSchema() );
+			defaults.put( "catalog", xmlDefaults.getCatalog() );
 			defaults.put( "delimited-identifier", xmlDefaults.getDelimitedIdentifier() );
 			List<Class> entityListeners = new ArrayList<Class>();
 			for ( String className : xmlContext.getDefaultEntityListeners() ) {
 				try {
 					entityListeners.add( ReflectHelper.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new IllegalStateException( "Default entity listener class not found: " + className );
 				}
 			}
 			defaults.put( EntityListeners.class, entityListeners );
 			for ( Element element : xmlContext.getAllDocuments() ) {
 				@SuppressWarnings( "unchecked" )
 				List<Element> elements = element.elements( "sequence-generator" );
 				List<SequenceGenerator> sequenceGenerators = ( List<SequenceGenerator> ) defaults.get( SequenceGenerator.class );
 				if ( sequenceGenerators == null ) {
 					sequenceGenerators = new ArrayList<SequenceGenerator>();
 					defaults.put( SequenceGenerator.class, sequenceGenerators );
 				}
 				for ( Element subelement : elements ) {
 					sequenceGenerators.add( JPAOverridenAnnotationReader.buildSequenceGeneratorAnnotation( subelement ) );
 				}
 
 				elements = element.elements( "table-generator" );
 				List<TableGenerator> tableGenerators = ( List<TableGenerator> ) defaults.get( TableGenerator.class );
 				if ( tableGenerators == null ) {
 					tableGenerators = new ArrayList<TableGenerator>();
 					defaults.put( TableGenerator.class, tableGenerators );
 				}
 				for ( Element subelement : elements ) {
 					tableGenerators.add(
 							JPAOverridenAnnotationReader.buildTableGeneratorAnnotation(
 									subelement, xmlDefaults
 							)
 					);
 				}
 
 				List<NamedQuery> namedQueries = ( List<NamedQuery> ) defaults.get( NamedQuery.class );
 				if ( namedQueries == null ) {
 					namedQueries = new ArrayList<NamedQuery>();
 					defaults.put( NamedQuery.class, namedQueries );
 				}
 				List<NamedQuery> currentNamedQueries = JPAOverridenAnnotationReader.buildNamedQueries(
 						element, false, xmlDefaults
 				);
 				namedQueries.addAll( currentNamedQueries );
 
 				List<NamedNativeQuery> namedNativeQueries = ( List<NamedNativeQuery> ) defaults.get( NamedNativeQuery.class );
 				if ( namedNativeQueries == null ) {
 					namedNativeQueries = new ArrayList<NamedNativeQuery>();
 					defaults.put( NamedNativeQuery.class, namedNativeQueries );
 				}
 				List<NamedNativeQuery> currentNamedNativeQueries = JPAOverridenAnnotationReader.buildNamedQueries(
 						element, true, xmlDefaults
 				);
 				namedNativeQueries.addAll( currentNamedNativeQueries );
 
 				List<SqlResultSetMapping> sqlResultSetMappings = ( List<SqlResultSetMapping> ) defaults.get(
 						SqlResultSetMapping.class
 				);
 				if ( sqlResultSetMappings == null ) {
 					sqlResultSetMappings = new ArrayList<SqlResultSetMapping>();
 					defaults.put( SqlResultSetMapping.class, sqlResultSetMappings );
 				}
 				List<SqlResultSetMapping> currentSqlResultSetMappings = JPAOverridenAnnotationReader.buildSqlResultsetMappings(
 						element, xmlDefaults
 				);
 				sqlResultSetMappings.addAll( currentSqlResultSetMappings );
 			}
 		}
 		return defaults;
 	}
 
 	public XMLContext getXMLContext() {
 		return xmlContext;
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/HibernateSequenceTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/HibernateSequenceTest.java
new file mode 100644
index 0000000000..014f9b9b2f
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/HibernateSequenceTest.java
@@ -0,0 +1,64 @@
+package org.hibernate.test.annotations.id.sequences;
+
+import org.junit.Assert;
+import org.junit.Test;
+
+import org.hibernate.Session;
+import org.hibernate.Transaction;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.id.IdentifierGenerator;
+import org.hibernate.id.enhanced.SequenceStyleGenerator;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.test.annotations.id.sequences.entities.HibernateSequenceEntity;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@TestForIssue(jiraKey = "HHH-6068")
+public class HibernateSequenceTest extends BaseCoreFunctionalTestCase {
+	private static final String SCHEMA_NAME = "OTHER_SCHEMA";
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] {
+				HibernateSequenceEntity.class
+		};
+	}
+
+	@Override
+	protected void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.addResource( "org/hibernate/test/annotations/id/sequences/orm.xml" );
+		cfg.setProperty(
+				Environment.URL, cfg.getProperty( Environment.URL ) + ";INIT=CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME
+		);
+	}
+
+	@Test
+	public void testHibernateSequenceSchema() {
+		EntityPersister persister = sessionFactory().getEntityPersister( HibernateSequenceEntity.class.getName() );
+		IdentifierGenerator generator = persister.getIdentifierGenerator();
+		Assert.assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
+		SequenceStyleGenerator seqGenerator = (SequenceStyleGenerator) generator;
+		Assert.assertEquals(
+				Table.qualify( null, SCHEMA_NAME, SequenceStyleGenerator.DEF_SEQUENCE_NAME ),
+				seqGenerator.getDatabaseStructure().getName()
+		);
+	}
+
+	@Test
+	public void testHibernateSequenceNextVal() {
+		Session session = openSession();
+		Transaction txn = session.beginTransaction();
+		HibernateSequenceEntity entity = new HibernateSequenceEntity();
+		entity.setText( "sample text" );
+		session.save( entity );
+		txn.commit();
+		session.close();
+		Assert.assertNotNull( entity.getId() );
+	}
+}
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/entities/HibernateSequenceEntity.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/entities/HibernateSequenceEntity.java
new file mode 100644
index 0000000000..6d2b840594
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/entities/HibernateSequenceEntity.java
@@ -0,0 +1,34 @@
+package org.hibernate.test.annotations.id.sequences.entities;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@Entity
+public class HibernateSequenceEntity {
+	@Id
+	@GeneratedValue(strategy = GenerationType.SEQUENCE)
+	private Long id;
+
+	private String text;
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getText() {
+		return text;
+	}
+
+	public void setText(String text) {
+		this.text = text;
+	}
+}
diff --git a/hibernate-core/src/matrix/resources/org/hibernate/test/annotations/id/sequences/orm.xml b/hibernate-core/src/matrix/resources/org/hibernate/test/annotations/id/sequences/orm.xml
new file mode 100644
index 0000000000..86d9dfcb9b
--- /dev/null
+++ b/hibernate-core/src/matrix/resources/org/hibernate/test/annotations/id/sequences/orm.xml
@@ -0,0 +1,12 @@
+<?xml version="1.0" encoding="UTF-8" ?>
+<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
+                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
+                 xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm
+                                     http://java.sun.com/xml/ns/persistence/orm_2_0.xsd"
+                 version="2.0">
+    <persistence-unit-metadata>
+        <persistence-unit-defaults>
+            <schema>OTHER_SCHEMA</schema>
+        </persistence-unit-defaults>
+    </persistence-unit-metadata>
+</entity-mappings>
\ No newline at end of file
