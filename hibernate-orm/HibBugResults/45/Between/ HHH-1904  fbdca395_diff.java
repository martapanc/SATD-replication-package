diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index daf65689f8..719e3798d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -381,2005 +381,2003 @@ public class Configuration implements Serializable {
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
 		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
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
 	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 
 				Iterator subIter = table.getUniqueKeyIterator();
 				while ( subIter.hasNext() ) {
 					UniqueKey uk = (UniqueKey) subIter.next();
 					String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 					if (constraintString != null) script.add( constraintString );
 				}
 
 
 				subIter = table.getIndexIterator();
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
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 				);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							tableCatalog,
 							tableSchema
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
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
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
 												tableCatalog,
 												tableSchema
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
 									tableCatalog,
 									tableSchema
 							)
 					);
 				}
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
 		
 		// TEMPORARY
 		// Ensure the correct ClassLoader is used in commons-annotations.
 		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				Map defaults = reflectionManager.getDefaults();
 				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 				// Set default schema name if orm.xml declares it.
 				final String schema = (String) defaults.get( "schema" );
 				if ( StringHelper.isNotEmpty( schema ) ) {
 					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
 				}
 				// Set default catalog name if orm.xml declares it.
 				final String catalog = (String) defaults.get( "catalog" );
 				if ( StringHelper.isNotEmpty( catalog ) ) {
 					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
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
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
-			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
-				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
-						? "UK_" + table.getName() + "_" + uniqueIndexPerTable
+						? StringHelper.randomFixedLengthHex("UK_")
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 		
 		for(Table table : jpaIndexHoldersByTable.keySet()){
 			final List<JPAIndexHolder> jpaIndexHolders = jpaIndexHoldersByTable.get( table );
 			int uniqueIndexPerTable = 0;
 			for ( JPAIndexHolder holder : jpaIndexHolders ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "idx_"+table.getName()+"_" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns(), holder.getOrdering(), holder.isUnique() );
 			}
 		}
 		
 		Thread.currentThread().setContextClassLoader( tccl );
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
 				String dependentTable = quotedTableName(classMapping.getTable());
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
 			String dependentTable = quotedTableName(sp.getValue().getTable());
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
 
 	private String quotedTableName(Table table) {
 		return Table.qualify( table.getCatalog(), table.getQuotedSchema(), table.getQuotedName() );
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
 			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = (RuntimeException) e.getCause();
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
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames){
 		buildUniqueKeyFromColumnNames( table, keyName, columnNames, null, true );
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames, String[] orderings, boolean unique) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			String column = columnNames[index];
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( column, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( column ) );
 			}
 		}
 		if ( unique ) {
 			UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					uk.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 		else {
 			Index index = table.getOrCreateIndex( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					index.addColumn( column, order );
 					unbound.remove( column );
 				}
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
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append("'").append( column.getName() ).append( "', " );
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
 
 		LOG.debug( "Creating tables' unique integer identifiers" );
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		int uniqueInteger = 0;
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			Table table = (Table) itr.next();
 			table.setUniqueInteger( uniqueInteger++ );
 			secondPassCompileForeignKeys( table, done );
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
 			StringBuilder buf = new StringBuilder( "Following super classes referenced in extends not found: " );
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
 
 	protected void secondPassCompileForeignKeys(Table table, Set<ForeignKey> done) throws MappingException {
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
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
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
 		final ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
 				.applySettings( properties )
 				.build();
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
 	 * @return The value currently associated with that property name; may be null.
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
 			ErrorLogger errorLogger = new ErrorLogger( resourceName );
 			Document document = xmlHelper.createSAXReader( errorLogger,  entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errorLogger.hasErrors() ) {
 				throw new MappingException( "invalid configuration", errorLogger.getErrors().get( 0 ) );
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
 	 * @param serviceRegistry The registry of services to be used in building these settings.
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
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index f982ec33cb..ea3f476416 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,761 +1,776 @@
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
 package org.hibernate.internal.util;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.StringTokenizer;
+import java.util.UUID;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 	
 	/*public static boolean containsDigits(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			if ( Character.isDigit( string.charAt(i) ) ) return true;
 		}
 		return false;
 	}*/
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			char character = string.charAt(i);
 			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
 		}
 		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String joinWithQualifier(String[] values, String qualifier, String deliminator) {
 		int length = values.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * values[0].length() )
 				.append( qualify( qualifier, values[0] ) );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( deliminator ).append( qualify( qualifier, values[i] ) );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuilder buf = new StringBuilder();
 		if ( objects.hasNext() ) buf.append( objects.next() );
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
 		String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuilder buf = new StringBuilder( string.length() * times );
 		for ( int i = 0; i < times; i++ ) buf.append( string );
 		return buf.toString();
 	}
 
 	public static String repeat(String string, int times, String deliminator) {
 		StringBuilder buf = new StringBuilder(  ( string.length() * times ) + ( deliminator.length() * (times-1) ) )
 				.append( string );
 		for ( int i = 1; i < times; i++ ) {
 			buf.append( deliminator ).append( string );
 		}
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i =0; i<templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
 		return replace( template, placeholder, replacement, wholeWords, false );
 	}
 
 	public static String replace(String template,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return template;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			String beforePlaceholder = template.substring( 0, loc );
 			String afterPlaceholder = template.substring( loc + placeholder.length() );
 			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
 		}
 	}
 
 
 	public static String replace(String beforePlaceholder,
 								 String afterPlaceholder,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		final boolean actuallyReplace =
 				! wholeWords ||
 				afterPlaceholder.length() == 0 ||
 				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
 		boolean encloseInParens =
 				actuallyReplace &&
 				encloseInParensIfNecessary &&
 				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
 				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
 		StringBuilder buf = new StringBuilder( beforePlaceholder );
 		if ( encloseInParens ) {
 			buf.append( '(' );
 		}
 		buf.append( actuallyReplace ? replacement : placeholder );
 		if ( encloseInParens ) {
 			buf.append( ')' );
 		}
 		buf.append(
 				replace(
 						afterPlaceholder,
 						placeholder,
 						replacement,
 						wholeWords,
 						encloseInParensIfNecessary
 				)
 		);
 		return buf.toString();
 	}
 
 	public static char getLastNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static char getFirstNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = 0 ; i < str.length() ; i++ ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return template; // returnign null!
 		}
         int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return new StringBuilder( template.substring( 0, loc ) )
 					.append( replacement )
 					.append( template.substring( loc + placeholder.length() ) )
 					.toString();
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[ tokens.countTokens() ];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) return columns;
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase();
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) return "";
 		StringBuilder buf = new StringBuilder( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append(", ");
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[]{string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( int i = 0; i < replacements.length; i++ ) {
 			for ( int j = 0; j < strings.length; j++ ) {
 				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null)
 			return 0;
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( indx );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
 			throw new NullPointerException();
 		}
 		return new StringBuilder( prefix.length() + name.length() + 1 )
 				.append(prefix)
 				.append('.')
 				.append(name)
 				.toString();
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) return names;
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		int matchAt = -1;
 		for ( int i = 0; i < string.length(); i++ ) {
 			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
 			if ( curMatch >= 0 ) {
 				if ( matchAt == -1 ) { // first time we find match!
 					matchAt = curMatch;
 				}
 				else {
 					matchAt = Math.min( matchAt, curMatch );
 				}
 			}
 		}
 		return matchAt;
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot(description) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot(description) +
 			Integer.toString(unique) +
 			'_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 * @return The generated root alias.
 	 */
 	private static String generateAliasRoot(String description) {
 		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
 				.toLowerCase()
 		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
 				.replace( '$', '_' ); //classname may be an inner class
 		result = cleanAlias( result );
 		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
 			return result + "x"; //ick!
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
 	private static String cleanAlias(String alias) {
 		char[] chars = alias.toCharArray();
 		// short cut check...
 		if ( !Character.isLetter( chars[0] ) ) {
 			for ( int i = 1; i < chars.length; i++ ) {
 				// as soon as we encounter our first letter, return the substring
 				// from that position
 				if ( Character.isLetter( chars[i] ) ) {
 					return alias.substring( i );
 				}
 			}
 		}
 		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify(entityName);
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 	
 	public static String toUpperCase(String str) {
 		return str==null ? null : str.toUpperCase();
 	}
 	
 	public static String toLowerCase(String str) {
 		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length()>0 ){
 			filter += " and ";
 			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
 		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
 // Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
         else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
             name = name.substring( 1, name.length() - 1 );
         }
 
 		return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * 		AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null
 				&&
 					name.length() != 0
 				&& (
 					name.charAt( 0 ) == '`'
 					&&
 					name.charAt( name.length() - 1 ) == '`'
 					||
 					name.charAt( 0 ) == dialect.openQuote()
 					&&
 					name.charAt( name.length() - 1 ) == dialect.closeQuote()
 				);
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		return isQuoted( name, dialect ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[ names.length ];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 
 
 	public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
 
 	public static StringBuilder buildBatchFetchRestrictionFragment(
 			String alias,
 			String[] columnNames,
 			Dialect dialect) {
 		// the general idea here is to just insert a placeholder that we can easily find later...
 		if ( columnNames.length == 1 ) {
 			// non-composite key
 			return new StringBuilder( StringHelper.qualify( alias, columnNames[0] ) )
 					.append( " in (" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 		}
 		else {
 			// composite key - the form to use here depends on what the dialect supports.
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				// use : (col1, col2) in ( (?,?), (?,?), ... )
 				StringBuilder builder = new StringBuilder();
 				builder.append( "(" );
 				boolean firstPass = true;
 				String deliminator = "";
 				for ( String columnName : columnNames ) {
 					builder.append( deliminator ).append( StringHelper.qualify( alias, columnName ) );
 					if ( firstPass ) {
 						firstPass = false;
 						deliminator = ",";
 					}
 				}
 				builder.append( ") in (" );
 				builder.append( BATCH_ID_PLACEHOLDER );
 				builder.append( ")" );
 				return builder;
 			}
 			else {
 				// use : ( (col1 = ? and col2 = ?) or (col1 = ? and col2 = ?) or ... )
 				//		unfortunately most of this building needs to be held off until we know
 				//		the exact number of ids :(
 				return new StringBuilder( "(" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 			}
 		}
 	}
 
 	public static String expandBatchIdPlaceholder(
 			String sql,
 			Serializable[] ids,
 			String alias,
 			String[] keyColumnNames,
 			Dialect dialect) {
 		if ( keyColumnNames.length == 1 ) {
 			// non-composite
 			return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( "?", ids.length, "," ) );
 		}
 		else {
 			// composite
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				final String tuple = "(" + StringHelper.repeat( "?", keyColumnNames.length, "," );
 				return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( tuple, ids.length, "," ) );
 			}
 			else {
 				final String keyCheck = joinWithQualifier( keyColumnNames, alias, " and " );
 				return replace( sql, BATCH_ID_PLACEHOLDER, repeat( keyCheck, ids.length, " or " ) );
 			}
 		}
 	}
 	
 	/**
 	 * Takes a String s and returns a new String[1] with s as the only element.
 	 * If s is null or "", return String[0].
 	 * 
 	 * @param s
 	 * @return String[]
 	 */
 	public static String[] toArrayElement(String s) {
 		return ( s == null || s.length() == 0 ) ? new String[0] : new String[] { s };
 	}
+
+	// Oracle restricts identifier lengths to 30.  Rather than tie this to
+	// Dialect, simply restrict randomly-generated constrain names across
+	// the board.
+	private static final int MAX_NAME_LENGTH = 30;
+	public static String randomFixedLengthHex(String prefix) {
+		int length = MAX_NAME_LENGTH - prefix.length();
+		String s = UUID.randomUUID().toString();
+		s = s.replace( "-", "" );
+		if (s.length() > length) {
+			s = s.substring( 0, length );
+		}
+		return prefix + s;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 488ebaab33..05fbdd10aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,877 +1,855 @@
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
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 public class Table implements RelationalModel, Serializable {
 
 	private String name;
 	private String schema;
 	private String catalog;
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map foreignKeys = new LinkedHashMap();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private boolean quoted;
 	private boolean schemaQuoted;
 	private boolean catalogQuoted;
 	private List checkConstraints = new ArrayList();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables = false;
 	private String comment;
 
 	static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 	}
 
 	public Table() { }
 
 	public Table(String name) {
 		this();
 		setName( name );
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * returns quoted name as it would be in the mapping file.
 	 */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return quoted ?
 				dialect.openQuote() + name + dialect.closeQuote() :
 				name;
 	}
 
 	/**
 	 * returns quoted name as it is in the mapping file.
 	 */
 	public String getQuotedSchema() {
 		return schemaQuoted ?
 				"`" + schema + "`" :
 				schema;
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schemaQuoted ?
 				dialect.openQuote() + schema + dialect.closeQuote() :
 				schema;
 	}
 
 	public String getQuotedCatalog() {
 		return catalogQuoted ?
 				"`" + catalog + "`" :
 				catalog;
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalogQuoted ?
 				dialect.openQuote() + catalog + dialect.closeQuote() :
 				catalog;
 	}
 
 	public void setName(String name) {
 		if ( name.charAt( 0 ) == '`' ) {
 			quoted = true;
 			this.name = name.substring( 1, name.length() - 1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse = 0;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result
 			+ ((catalog == null) ? 0 : isCatalogQuoted() ? catalog.hashCode() : catalog.toLowerCase().hashCode());
 		result = prime * result + ((name == null) ? 0 : isQuoted() ? name.hashCode() : name.toLowerCase().hashCode());
 		result = prime * result
 			+ ((schema == null) ? 0 : isSchemaQuoted() ? schema.hashCode() : schema.toLowerCase().hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return isQuoted() ? name.equals(table.getName()) : name.equalsIgnoreCase(table.getName())
 			&& ((schema == null && table.getSchema() != null) ? false : (schema == null) ? true : isSchemaQuoted() ? schema.equals(table.getSchema()) : schema.equalsIgnoreCase(table.getSchema()))
 			&& ((catalog == null && table.getCatalog() != null) ? false : (catalog == null) ? true : isCatalogQuoted() ? catalog.equals(table.getCatalog()) : catalog.equalsIgnoreCase(table.getCatalog()));
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase()
 						.startsWith( columnInfo.getTypeName().toLowerCase() )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase() +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
 									String defaultSchema)
 			throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
-		int uniqueIndexInteger = 0;
-		
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( column.getName() );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
-					uniqueIndexInteger++;
 					UniqueKey uk = getOrCreateUniqueKey( 
-							"UK_" + name + "_" + uniqueIndexInteger);
+							StringHelper.randomFixedLengthHex("UK_"));
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.applyUniqueToColumn( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
 		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
 				.append( ' ' )
 				.append( name )
 				.append( " (" );
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Column column = (Column) itr.next();
 			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
 			buffer.append( column.getSqlType( dialect, mapping ) );
 			if ( column.isNullable() ) {
 				buffer.append( dialect.getNullColumnString() );
 			}
 			else {
 				buffer.append( " not null" );
 			}
 			if ( itr.hasNext() ) {
 				buffer.append( ", " );
 			}
 		}
 		buffer.append( ") " );
 		buffer.append( dialect.getCreateTemporaryTablePostfix() );
 		return buffer.toString();
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
-		int uniqueIndexInteger = 0;
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
-				uniqueIndexInteger++;
 				UniqueKey uk = getOrCreateUniqueKey( 
-						"uc_" + name + "_" + uniqueIndexInteger);
+						StringHelper.randomFixedLengthHex("UK_"));
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.applyUniqueToColumn( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			Iterator chiter = checkConstraints.iterator();
 			while ( chiter.hasNext() ) {
 				buf.append( ", check (" )
 						.append( chiter.next() )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
-		String keyName = "UK_" + uniqueColumnString( keyColumns.iterator() );
+		String keyName = StringHelper.randomFixedLengthHex("UK_");
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
 									   List referencedColumns) {
 		Object key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = (ForeignKey) foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			if ( keyName != null ) {
 				fk.setName( keyName );
 			}
 			else {
-				fk.setName( "FK" + uniqueColumnString( keyColumns.iterator(), referencedEntityName ) );
-				//TODO: add referencedClass to disambiguate to FKs on the same
-				//      columns, pointing to different tables
+				fk.setName( StringHelper.randomFixedLengthHex("FK_") );
 			}
 			fk.setTable( this );
 			foreignKeys.put( key, fk );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
-	public String uniqueColumnString(Iterator iterator) {
-		return uniqueColumnString( iterator, null );
-	}
-
-	public String uniqueColumnString(Iterator iterator, String referencedEntityName) {
-		int result = 0;
-		if ( referencedEntityName != null ) {
-			result += referencedEntityName.hashCode();
-		}
-		while ( iterator.hasNext() ) {
-			result += iterator.next().hashCode();
-		}
-		return ( Integer.toHexString( name.hashCode() ) + Integer.toHexString( result ) ).toUpperCase();
-	}
-
-
 
 	public String getSchema() {
 		return schema;
 	}
 
 	public void setSchema(String schema) {
 		if ( schema != null && schema.charAt( 0 ) == '`' ) {
 			schemaQuoted = true;
 			this.schema = schema.substring( 1, schema.length() - 1 );
 		}
 		else {
 			this.schema = schema;
 		}
 	}
 
 	public String getCatalog() {
 		return catalog;
 	}
 
 	public void setCatalog(String catalog) {
 		if ( catalog != null && catalog.charAt( 0 ) == '`' ) {
 			catalogQuoted = true;
 			this.catalog = catalog.substring( 1, catalog.length() - 1 );
 		}
 		else {
 			this.catalog = catalog;
 		}
 	}
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public boolean isSchemaQuoted() {
 		return schemaQuoted;
 	}
 	public boolean isCatalogQuoted() {
 		return catalogQuoted;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public void setQuoted(boolean quoted) {
 		this.quoted = quoted;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() + "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() + "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "comment on table " )
 						.append( tableName )
 						.append( " is '" )
 						.append( comment )
 						.append( "'" );
 				comments.add( buf.toString() );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					StringBuilder buf = new StringBuilder()
 							.append( "comment on column " )
 							.append( tableName )
 							.append( '.' )
 							.append( column.getQuotedName( dialect ) )
 							.append( " is '" )
 							.append( columnComment )
 							.append( "'" );
 					comments.add( buf.toString() );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java b/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
index 5685d986d8..0e33a4ff83 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
@@ -1,85 +1,145 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.test.constraint;
 
+import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
+import java.util.Iterator;
+
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
+import javax.persistence.OneToOne;
 import javax.persistence.Table;
+import javax.persistence.UniqueConstraint;
 
 import org.hibernate.mapping.Column;
+import org.hibernate.mapping.ForeignKey;
+import org.hibernate.mapping.UniqueKey;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
-/**
- * HHH-7797 re-wrote the way dialects handle unique constraints.  Test
- * variations of unique & not null to ensure the constraints are created
- * correctly for each dialect.
- * 
- * @author Brett Meyer
- */
-@TestForIssue( jiraKey = "HHH-7797" )
 public class ConstraintTest extends BaseCoreFunctionalTestCase {
 	
+	private static final int MAX_NAME_LENGTH = 30;
+	
+	private static final String EXPLICIT_FK_NAME = "fk_explicit";
+	
+	private static final String EXPLICIT_UK_NAME = "uk_explicit";
+	
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {
-				Entity1.class
+				DataPoint.class, DataPoint2.class
 		};
 	}
 	
 	@Test
-	public void testConstraints() {
-		Column column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+	@TestForIssue( jiraKey = "HHH-7797" )
+	public void testUniqueConstraints() {
+		Column column = (Column) configuration().getClassMapping( DataPoint.class.getName() )
 				.getProperty( "foo1" ).getColumnIterator().next();
 		assertFalse( column.isNullable() );
 		assertTrue( column.isUnique() );
 
-		column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+		column = (Column) configuration().getClassMapping( DataPoint.class.getName() )
 				.getProperty( "foo2" ).getColumnIterator().next();
 		assertTrue( column.isNullable() );
 		assertTrue( column.isUnique() );
 
-		column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+		column = (Column) configuration().getClassMapping( DataPoint.class.getName() )
 				.getProperty( "id" ).getColumnIterator().next();
 		assertFalse( column.isNullable() );
 		assertTrue( column.isUnique() );
 	}
 	
+	@Test
+	@TestForIssue( jiraKey = "HHH-1904" )
+	public void testConstraintNameLength() {
+		Iterator<org.hibernate.mapping.Table> tableItr = configuration().getTableMappings();
+		while (tableItr.hasNext()) {
+			org.hibernate.mapping.Table table = tableItr.next();
+			
+			Iterator fkItr = table.getForeignKeyIterator();
+			while (fkItr.hasNext()) {
+				ForeignKey fk = (ForeignKey) fkItr.next();
+				assertTrue( fk.getName().length() <= MAX_NAME_LENGTH );
+				
+				// ensure the randomly generated constraint name doesn't
+				// happen if explicitly given
+				Column column = fk.getColumn( 0 );
+				if ( column.getName().equals( "explicit" ) ) {
+					assertEquals( fk.getName(), EXPLICIT_FK_NAME );
+				}
+			}
+			
+			Iterator ukItr = table.getUniqueKeyIterator();
+			while (ukItr.hasNext()) {
+				UniqueKey uk = (UniqueKey) ukItr.next();
+				assertTrue( uk.getName().length() <= MAX_NAME_LENGTH );
+				
+				// ensure the randomly generated constraint name doesn't
+				// happen if explicitly given
+				Column column = uk.getColumn( 0 );
+				if ( column.getName().equals( "explicit" ) ) {
+					assertEquals( uk.getName(), EXPLICIT_UK_NAME );
+				}
+			}
+		}
+	}
+	
 	@Entity
-	@Table( name = "Entity1" )
-	public static class Entity1 {
+	@Table( name = "DataPoint", uniqueConstraints = {
+			@UniqueConstraint( name = EXPLICIT_UK_NAME, columnNames = { "explicit" } )
+	} )
+	public static class DataPoint {
 		@Id
 		@GeneratedValue
 		@javax.persistence.Column( nullable = false, unique = true)
 		public long id;
 		
 		@javax.persistence.Column( nullable = false, unique = true)
 		public String foo1;
 		
 		@javax.persistence.Column( nullable = true, unique = true)
 		public String foo2;
+		
+		public String explicit;
+	}
+	
+	@Entity
+	@Table( name = "DataPoint2" )
+	public static class DataPoint2 {
+		@Id
+		@GeneratedValue
+		public long id;
+		
+		@OneToOne
+		public DataPoint dp;
+		
+		@OneToOne
+		@org.hibernate.annotations.ForeignKey(name = EXPLICIT_FK_NAME)
+		public DataPoint explicit;
 	}
 }
\ No newline at end of file
