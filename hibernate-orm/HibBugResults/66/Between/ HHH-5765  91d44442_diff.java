diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index d8051cd5e3..da55a6c0ee 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -843,2995 +843,2995 @@ public class Configuration implements Serializable {
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
 					log.info( "Found mapping document in jar: " + ze.getName() );
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
 				log.error("could not close jar", ioe);
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
 		log.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
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
 
 		applyConstraintsToDDL();
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
 		log.debug( "processing fk mappings (*ToOne and JoinedSubclass)" );
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
 			sb.append( ") on table " ).append( table.getName() ).append( ": " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void applyConstraintsToDDL() {
 		boolean applyOnDdl = getProperties().getProperty(
 				"hibernate.validator.apply_to_ddl",
 				"true"
 		)
 				.equalsIgnoreCase( "true" );
 
 		if ( !applyOnDdl ) {
 			return; // nothing to do in this case
 		}
 		applyHibernateValidatorLegacyConstraintsOnDDL();
 		applyBeanValidationConstraintsOnDDL();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyHibernateValidatorLegacyConstraintsOnDDL() {
 		//TODO search for the method only once and cache it?
 		Constructor validatorCtr = null;
 		Method applyMethod = null;
 		try {
 			Class classValidator = ReflectHelper.classForName(
 					"org.hibernate.validator.ClassValidator", this.getClass()
 			);
 			Class messageInterpolator = ReflectHelper.classForName(
 					"org.hibernate.validator.MessageInterpolator", this.getClass()
 			);
 			validatorCtr = classValidator.getDeclaredConstructor(
 					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
 			);
 			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( !isValidatorNotPresentLogged ) {
 				log.info( "Hibernate Validator not found: ignoring" );
 			}
 			isValidatorNotPresentLogged = true;
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new AnnotationException( e );
 		}
 		if ( applyMethod != null ) {
 			for ( PersistentClass persistentClazz : classes.values() ) {
 				//integrate the validate framework
 				String className = persistentClazz.getClassName();
 				if ( StringHelper.isNotEmpty( className ) ) {
 					try {
 						Object validator = validatorCtr.newInstance(
 								ReflectHelper.classForName( className ), null, null, null, reflectionManager
 						);
 						applyMethod.invoke( validator, persistentClazz );
 					}
 					catch ( Exception e ) {
 						log.warn( "Unable to apply constraints on DDL for " + className, e );
 					}
 				}
 			}
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyBeanValidationConstraintsOnDDL() {
 		BeanValidationActivator.applyDDL( classes.values(), getProperties() );
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		log.debug( "processing extends queue" );
 		processExtendsQueue();
 
 		log.debug( "processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		log.debug( "processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		log.debug( "processing association property references" );
 
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
 
 		log.debug( "processing foreign key constraints" );
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		log.debug( "processing extends queue" );
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
 				if ( log.isDebugEnabled() ) {
 					log.debug( "resolving reference to class: " + referencedEntityName );
 				}
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
 	public SessionFactory buildSessionFactory(ServicesRegistry serviceRegistry) throws HibernateException {
 		log.debug( "Preparing to build session factory with filters : " + filterDefinitions );
 
 		secondPassCompile();
 		if ( ! metadataSourceQueue.isEmpty() ) {
 			log.warn( "mapping metadata cache was not completely processed" );
 		}
 
 		enableLegacyHibernateValidator();
 		enableBeanValidation();
 		enableHibernateSearch();
 
 		validate();
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
-		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ).getConnectionProvider() );
+		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ) );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				getInitializedEventListeners(),
 				sessionFactoryObserver
 			);
 	}
 
 	private static final String LEGACY_VALIDATOR_EVENT_LISTENER = "org.hibernate.validator.event.ValidateEventListener";
 
 	private void enableLegacyHibernateValidator() {
 		//add validator events if the jar is available
 		boolean enableValidatorListeners = !"false".equalsIgnoreCase(
 				getProperty(
 						"hibernate.validator.autoregister_listeners"
 				)
 		);
 		Class validateEventListenerClass = null;
 		try {
 			validateEventListenerClass = ReflectHelper.classForName( LEGACY_VALIDATOR_EVENT_LISTENER, Configuration.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			//validator is not present
 			log.debug( "Legacy Validator not present in classpath, ignoring event listener registration" );
 		}
 		if ( enableValidatorListeners && validateEventListenerClass != null ) {
 			//TODO so much duplication
 			Object validateEventListener;
 			try {
 				validateEventListener = validateEventListenerClass.newInstance();
 			}
 			catch ( Exception e ) {
 				throw new AnnotationException( "Unable to load Validator event listener", e );
 			}
 			{
 				boolean present = false;
 				PreInsertEventListener[] listeners = getEventListeners().getPreInsertEventListeners();
 				if ( listeners != null ) {
 					for ( Object eventListener : listeners ) {
 						//not isAssignableFrom since the user could subclass
 						present = present || validateEventListenerClass == eventListener.getClass();
 					}
 					if ( !present ) {
 						int length = listeners.length + 1;
 						PreInsertEventListener[] newListeners = new PreInsertEventListener[length];
 						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
 						newListeners[length - 1] = ( PreInsertEventListener ) validateEventListener;
 						getEventListeners().setPreInsertEventListeners( newListeners );
 					}
 				}
 				else {
 					getEventListeners().setPreInsertEventListeners(
 							new PreInsertEventListener[] { ( PreInsertEventListener ) validateEventListener }
 					);
 				}
 			}
 
 			//update event listener
 			{
 				boolean present = false;
 				PreUpdateEventListener[] listeners = getEventListeners().getPreUpdateEventListeners();
 				if ( listeners != null ) {
 					for ( Object eventListener : listeners ) {
 						//not isAssignableFrom since the user could subclass
 						present = present || validateEventListenerClass == eventListener.getClass();
 					}
 					if ( !present ) {
 						int length = listeners.length + 1;
 						PreUpdateEventListener[] newListeners = new PreUpdateEventListener[length];
 						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
 						newListeners[length - 1] = ( PreUpdateEventListener ) validateEventListener;
 						getEventListeners().setPreUpdateEventListeners( newListeners );
 					}
 				}
 				else {
 					getEventListeners().setPreUpdateEventListeners(
 							new PreUpdateEventListener[] { ( PreUpdateEventListener ) validateEventListener }
 					);
 				}
 			}
 		}
 	}
 
 	private void enableBeanValidation() {
 		BeanValidationActivator.activateBeanValidation( getEventListeners(), getProperties() );
 	}
 
 	private static final String SEARCH_EVENT_LISTENER_REGISTERER_CLASS = "org.hibernate.cfg.search.HibernateSearchEventListenerRegister";
 
 	/**
 	 * Tries to automatically register Hibernate Search event listeners by locating the
 	 * appropriate bootstrap class and calling the <code>enableHibernateSearch</code> method.
 	 */
 	private void enableHibernateSearch() {
 		// load the bootstrap class
 		Class searchStartupClass;
 		try {
 			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
 		}
 		catch ( ClassNotFoundException e ) {
 			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
 			// try loading deprecated HibernateSearchEventListenerRegister
 			try {
 				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				log.debug( "Search not present in classpath, ignoring event listener registration." );
 				return;
 			}
 		}
 
 		// call the method for registering the listeners
 		try {
 			Object searchStartupInstance = searchStartupClass.newInstance();
 			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
 					SEARCH_STARTUP_METHOD,
 					EventListeners.class,
 					Properties.class
 			);
 			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
 		}
 		catch ( InstantiationException e ) {
 			log.debug( "Unable to instantiate {}, ignoring event listener registration.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( IllegalAccessException e ) {
 			log.debug( "Unable to instantiate {}, ignoring event listener registration.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( NoSuchMethodException e ) {
 			log.debug( "Method enableHibernateSearch() not found in {}.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( InvocationTargetException e ) {
 			log.debug( "Unable to execute {}, ignoring event listener registration.", SEARCH_STARTUP_METHOD );
 		}
 	}
 
 	private EventListeners getInitializedEventListeners() {
 		EventListeners result = (EventListeners) eventListeners.shallowCopy();
 		result.initializeListeners( this );
 		return result;
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
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory() built}
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
 			log.debug( name + "=" + value );
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
 		log.info( "configuring from resource: " + resource );
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
 		log.info( "Configuration resource: " + resource );
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
 		log.info( "configuring from url: " + url.toString() );
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
 		log.info( "configuring from file: " + configFile.getName() );
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
 				log.warn( "could not close input stream for: " + resourceName, ioe );
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
 		log.info( "configuring from XML document" );
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
 
 		log.info( "Configured SessionFactory: " + name );
 		log.debug( "properties: " + properties );
 
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
 			else if ( "listener".equals( subelementName ) ) {
 				parseListener( subelement );
 			}
 			else if ( "event".equals( subelementName ) ) {
 				parseEvent( subelement );
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
 			log.debug( "session-factory config [{}] named resource [{}] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			log.debug( "session-factory config [{}] named file [{}] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			log.debug( "session-factory config [{}] named jar file [{}] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			log.debug( "session-factory config [{}] named package [{}] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			log.debug( "session-factory config [{}] named class [{}] for mapping", name, className );
 
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
 		log.info( "JACC contextID: " + contextId );
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
 
 	private void parseEvent(Element element) {
 		String type = element.attributeValue( "type" );
 		List listeners = element.elements();
 		String[] listenerClasses = new String[ listeners.size() ];
 		for ( int i = 0; i < listeners.size() ; i++ ) {
 			listenerClasses[i] = ( (Element) listeners.get( i ) ).attributeValue( "class" );
 		}
 		log.debug( "Event listeners: " + type + "=" + StringHelper.toString( listenerClasses ) );
 		setListeners( type, listenerClasses );
 	}
 
 	private void parseListener(Element element) {
 		String type = element.attributeValue( "type" );
 		if ( type == null ) {
 			throw new MappingException( "No type specified for listener" );
 		}
 		String impl = element.attributeValue( "class" );
 		log.debug( "Event listener: " + type + "=" + impl );
 		setListeners( type, new String[]{impl} );
 	}
 
 	public void setListener(String type, String listener) {
 		String[] listeners = null;
 		if ( listener != null ) {
 			listeners = (String[]) Array.newInstance( String.class, 1 );
 			listeners[0] = listener;
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListeners(String type, String[] listenerClasses) {
 		Object[] listeners = null;
 		if ( listenerClasses != null ) {
 			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), listenerClasses.length );
 			for ( int i = 0; i < listeners.length ; i++ ) {
 				try {
 					listeners[i] = ReflectHelper.classForName( listenerClasses[i] ).newInstance();
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"Unable to instantiate specified event (" + type + ") listener class: " + listenerClasses[i],
 							e
 						);
 				}
 			}
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListener(String type, Object listener) {
 		Object[] listeners = null;
 		if ( listener != null ) {
 			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), 1 );
 			listeners[0] = listener;
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListeners(String type, Object[] listeners) {
 		if ( "auto-flush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setAutoFlushEventListeners( new AutoFlushEventListener[]{} );
 			}
 			else {
 				eventListeners.setAutoFlushEventListeners( (AutoFlushEventListener[]) listeners );
 			}
 		}
 		else if ( "merge".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setMergeEventListeners( new MergeEventListener[]{} );
 			}
 			else {
 				eventListeners.setMergeEventListeners( (MergeEventListener[]) listeners );
 			}
 		}
 		else if ( "create".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPersistEventListeners( new PersistEventListener[]{} );
 			}
 			else {
 				eventListeners.setPersistEventListeners( (PersistEventListener[]) listeners );
 			}
 		}
 		else if ( "create-onflush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPersistOnFlushEventListeners( new PersistEventListener[]{} );
 			}
 			else {
 				eventListeners.setPersistOnFlushEventListeners( (PersistEventListener[]) listeners );
 			}
 		}
 		else if ( "delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setDeleteEventListeners( new DeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setDeleteEventListeners( (DeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "dirty-check".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setDirtyCheckEventListeners( new DirtyCheckEventListener[]{} );
 			}
 			else {
 				eventListeners.setDirtyCheckEventListeners( (DirtyCheckEventListener[]) listeners );
 			}
 		}
 		else if ( "evict".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setEvictEventListeners( new EvictEventListener[]{} );
 			}
 			else {
 				eventListeners.setEvictEventListeners( (EvictEventListener[]) listeners );
 			}
 		}
 		else if ( "flush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setFlushEventListeners( new FlushEventListener[]{} );
 			}
 			else {
 				eventListeners.setFlushEventListeners( (FlushEventListener[]) listeners );
 			}
 		}
 		else if ( "flush-entity".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setFlushEntityEventListeners( new FlushEntityEventListener[]{} );
 			}
 			else {
 				eventListeners.setFlushEntityEventListeners( (FlushEntityEventListener[]) listeners );
 			}
 		}
 		else if ( "load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setLoadEventListeners( new LoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setLoadEventListeners( (LoadEventListener[]) listeners );
 			}
 		}
 		else if ( "load-collection".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setInitializeCollectionEventListeners(
 						new InitializeCollectionEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setInitializeCollectionEventListeners(
 						(InitializeCollectionEventListener[]) listeners
 					);
 			}
 		}
 		else if ( "lock".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setLockEventListeners( new LockEventListener[]{} );
 			}
 			else {
 				eventListeners.setLockEventListeners( (LockEventListener[]) listeners );
 			}
 		}
 		else if ( "refresh".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setRefreshEventListeners( new RefreshEventListener[]{} );
 			}
 			else {
 				eventListeners.setRefreshEventListeners( (RefreshEventListener[]) listeners );
 			}
 		}
 		else if ( "replicate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setReplicateEventListeners( new ReplicateEventListener[]{} );
 			}
 			else {
 				eventListeners.setReplicateEventListeners( (ReplicateEventListener[]) listeners );
 			}
 		}
 		else if ( "save-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setSaveOrUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setSaveOrUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "save".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setSaveEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setSaveEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreLoadEventListeners( new PreLoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreLoadEventListeners( (PreLoadEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreUpdateEventListeners( new PreUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreUpdateEventListeners( (PreUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreDeleteEventListeners( new PreDeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreDeleteEventListeners( (PreDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreInsertEventListeners( new PreInsertEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreInsertEventListeners( (PreInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-recreate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionRecreateEventListeners( new PreCollectionRecreateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionRecreateEventListeners( (PreCollectionRecreateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-remove".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionRemoveEventListeners( new PreCollectionRemoveEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionRemoveEventListeners( ( PreCollectionRemoveEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionUpdateEventListeners( new PreCollectionUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionUpdateEventListeners( ( PreCollectionUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostLoadEventListeners( new PostLoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostLoadEventListeners( (PostLoadEventListener[]) listeners );
 			}
 		}
 		else if ( "post-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostUpdateEventListeners( new PostUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostUpdateEventListeners( (PostUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostDeleteEventListeners( new PostDeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostDeleteEventListeners( (PostDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "post-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostInsertEventListeners( new PostInsertEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostInsertEventListeners( (PostInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitUpdateEventListeners(
 						new PostUpdateEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setPostCommitUpdateEventListeners( (PostUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitDeleteEventListeners(
 						new PostDeleteEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setPostCommitDeleteEventListeners( (PostDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitInsertEventListeners(
 						new PostInsertEventListener[]{}
 				);
 			}
 			else {
 				eventListeners.setPostCommitInsertEventListeners( (PostInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-recreate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionRecreateEventListeners( new PostCollectionRecreateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionRecreateEventListeners( (PostCollectionRecreateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-remove".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionRemoveEventListeners( new PostCollectionRemoveEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionRemoveEventListeners( ( PostCollectionRemoveEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionUpdateEventListeners( new PostCollectionUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionUpdateEventListeners( ( PostCollectionUpdateEventListener[]) listeners );
 			}
 		}
 		else {
 			throw new MappingException("Unrecognized listener type [" + type + "]");
 		}
 	}
 
 	public EventListeners getEventListeners() {
 		return eventListeners;
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
-	public Settings buildSettings(ConnectionProvider connectionProvider) {
+	public Settings buildSettings(JdbcServices jdbcServices) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
-		return buildSettingsInternal( clone, connectionProvider );
+		return buildSettingsInternal( clone, jdbcServices );
 	}
 
-	public Settings buildSettings(Properties props, ConnectionProvider connectionProvider) throws HibernateException {
-		return buildSettingsInternal( props, connectionProvider );
+	public Settings buildSettings(Properties props, JdbcServices jdbcServices) throws HibernateException {
+		return buildSettingsInternal( props, jdbcServices );
 	}
 
-	private Settings buildSettingsInternal(Properties props, ConnectionProvider connectionProvider) {
-		final Settings settings = settingsFactory.buildSettings( props, connectionProvider );
+	private Settings buildSettingsInternal(Properties props, JdbcServices jdbcServices) {
+		final Settings settings = settingsFactory.buildSettings( props, jdbcServices );
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
 				if ( existing.equals( entityName ) ) {
 					log.info( "duplicate import: {} -> {}", entityName, rename );
 				}
 				else {
 					throw new DuplicateMappingException(
 							"duplicate import: " + rename + " refers to both " + entityName +
 									" and " + existing + " (try using auto-import=\"false\")",
 							"import",
 							rename
 					);
 				}
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
 			log.debug( "Added " + typeName + " with class " + typeClass );
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
 				final String booleanName = getConfigurationProperties().getProperty( USE_NEW_ID_GENERATOR_MAPPINGS );
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
 				if ( old != null ) {
 					log.warn( "duplicate generator name {}", old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				log.warn( "duplicate generator table: {}", name );
 			}
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
 			if ( old != null ) {
 				log.warn( "duplicate joins for class: {}", persistentClass.getEntityName() );
 			}
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index 89b456467c..28006cf688 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,825 +1,825 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.Timestamp;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Version;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.util.ConfigHelper;
 
 
 /**
  * Provides access to configuration info passed in <tt>Properties</tt> objects.
  * <br><br>
  * Hibernate has two property scopes:
  * <ul>
  * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
  * instantiated. Each instance might have different property values. If no
  * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
  * <li><b>System-level</b> properties are shared by all factory instances and are always
  * determined by the <tt>Environment</tt> properties.
  * </ul>
  * The only system-level properties are
  * <ul>
  * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
  * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
  * </ul>
  * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
  * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
  * properties override properties specified in <tt>hibernate.properties</tt>.<br>
  * <br>
  * The <tt>SessionFactory</tt> is controlled by the following properties.
  * Properties may be either be <tt>System</tt> properties, properties
  * defined in a resource named <tt>/hibernate.properties</tt> or an instance of
  * <tt>java.util.Properties</tt> passed to
  * <tt>Configuration.buildSessionFactory()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.cache.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.cache.CacheProvider</tt>
  *   subclass (if not specified EHCache is used)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</tt>
  *   subclass (if not specified hueristics are used)</td>
  * </tr>
  * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
  * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
  * <tr>
  *   <td><tt>hibernate.connection.url</tt></td>
  *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.driver_class</tt></td>
  *   <td>classname of JDBC driver</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.isolation</tt></td>
  *   <td>JDBC transaction isolation level (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  *   <td><tt>hibernate.connection.pool_size</tt></td>
  *   <td>the maximum size of the connection pool (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.datasource</tt></td>
  *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.max_fetch_depth</tt></td>
  *   <td>maximum depth of outer join fetching</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.batch_size</tt></td>
  *   <td>enable use of JDBC2 batch API for drivers which support it</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
  *   <td>set the JDBC fetch size</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
  *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
  *   this property when using user supplied connections)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
  *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve
  *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
  *   <td>enable auto DDL export</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_schema</tt></td>
  *   <td>use given schema name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_catalog</tt></td>
  *   <td>use given catalog name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.session_factory_name</tt></td>
  *   <td>If set, the factory attempts to bind this name to itself in the
  *   JNDI context. This name is also used to support cross JVM <tt>
  *   Session</tt> (de)serialization.</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
  *   <td>classname of <tt>org.hibernate.transaction.TransactionManagerLookup</tt>
  *   implementor</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.factory_class</tt></td>
  *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
  *   (Defaults to <tt>JDBCTransactionFactory</tt>.)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
  * </tr>
  * </table>
  *
  * @see org.hibernate.SessionFactory
  * @author Gavin King
  */
 public final class Environment {
 	/**
 	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 	/**
 	 * JDBC driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 	/**
 	 * JDBC transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 	/**
 	 * JDBC URL
 	 */
 	public static final String URL ="hibernate.connection.url";
 	/**
 	 * JDBC user
 	 */
 	public static final String USER ="hibernate.connection.username";
 	/**
 	 * JDBC password
 	 */
 	public static final String PASS ="hibernate.connection.password";
 	/**
 	 * JDBC autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 	/**
 	 * Maximum number of inactive connections for Hibernate's connection pool
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 	/**
 	 * <tt>java.sql.Datasource</tt> JNDI name
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 	/**
 	 * prefix for arbitrary JDBC connection properties
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 	/**
 	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 	/**
 	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 	/**
 	 * JNDI name to bind to <tt>SessionFactory</tt>
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Hibernate SQL {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
-	 * {@link org.hibernate.dialect.resolver.DialectResolver} classes to register with the
-	 * {@link org.hibernate.dialect.resolver.DialectFactory}
+	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} classes to register with the
+	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}
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
 	 * <tt>TransactionFactory</tt> implementor to use for creating <tt>Transaction</tt>s
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 	/**
 	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
 	 */
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 */
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
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
 	 * The maximum number of strong references maintained by {@link org.hibernate.util.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.util.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
 	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
 	private static final Logger log = LoggerFactory.getLogger(Environment.class);
 
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param props The specified properties.
 	 */
 	public static void verifyProperties(Properties props) {
 		Iterator iter = props.keySet().iterator();
 		Map propertiesToAdd = new HashMap();
 		while ( iter.hasNext() ) {
 			final Object propertyName = iter.next();
 			Object newPropertyName = OBSOLETE_PROPERTIES.get( propertyName );
 			if ( newPropertyName != null ) {
 				log.warn( "Usage of obsolete property: " + propertyName + " no longer supported, use: " + newPropertyName );
 			}
 			newPropertyName = RENAMED_PROPERTIES.get( propertyName );
 			if ( newPropertyName != null ) {
 				log.warn( "Property [" + propertyName + "] has been renamed to [" + newPropertyName + "]; update your properties appropriately" );
 				if ( ! props.containsKey( newPropertyName ) ) {
 					propertiesToAdd.put( newPropertyName, props.get( propertyName ) );
 				}
 			}
 		}
 		props.putAll(propertiesToAdd);
 	}
 
 	static {
 
 		log.info( "Hibernate " + Version.getVersionString() );
 
 		RENAMED_PROPERTIES.put( "hibernate.cglib.use_reflection_optimizer", USE_REFLECTION_OPTIMIZER );
 
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_NONE), "NONE" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
 
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream("/hibernate.properties");
 			try {
 				GLOBAL_PROPERTIES.load(stream);
 				log.info( "loaded properties from resource hibernate.properties: " + ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, PASS) );
 			}
 			catch (Exception e) {
 				log.error("problem loading properties from hibernate.properties");
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
 					log.error("could not close stream on hibernate.properties", ioe);
 				}
 			}
 		}
 		catch (HibernateException he) {
 			log.info("hibernate.properties not found");
 		}
 
 		try {
 			GLOBAL_PROPERTIES.putAll( System.getProperties() );
 		}
 		catch (SecurityException se) {
 			log.warn("could not copy system properties, system properties will be ignored");
 		}
 
 		verifyProperties(GLOBAL_PROPERTIES);
 
 		ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
 
 		if (ENABLE_BINARY_STREAMS) {
 			log.info("using java.io streams to persist binary types");
 		}
 		if (ENABLE_REFLECTION_OPTIMIZER) {
 			log.info("using bytecode reflection optimizer");
 		}
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		boolean getGeneratedKeysSupport;
 		try {
 			Statement.class.getMethod("getGeneratedKeys", null);
 			getGeneratedKeysSupport = true;
 		}
 		catch (NoSuchMethodException nsme) {
 			getGeneratedKeysSupport = false;
 		}
 		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
 		if (!JVM_SUPPORTS_GET_GENERATED_KEYS) {
 			log.info("JVM does not support Statement.getGeneratedKeys()");
 		}
 
 		boolean linkedHashSupport;
 		try {
 			Class.forName("java.util.LinkedHashSet");
 			linkedHashSupport = true;
 		}
 		catch (ClassNotFoundException cnfe) {
 			linkedHashSupport = false;
 		}
 		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
 		if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) {
 			log.info("JVM does not support LinkedHasMap, LinkedHashSet - ordered maps and sets disabled");
 		}
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
 		if (JVM_HAS_TIMESTAMP_BUG) {
 			log.info("using workaround for JVM bug in java.sql.Timestamp");
 		}
 
 		Timestamp t = new Timestamp(0);
 		t.setNanos(5 * 1000000);
 		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
 		if (JVM_HAS_JDK14_TIMESTAMP) {
 			log.info("using JDK 1.4 java.sql.Timestamp handling");
 		}
 		else {
 			log.info("using pre JDK 1.4 java.sql.Timestamp handling");
 		}
 	}
 
 	public static BytecodeProvider getBytecodeProvider() {
 		return BYTECODE_PROVIDER_INSTANCE;
 	}
 
 	/**
 	 * Does this JVM's implementation of {@link java.sql.Timestamp} have a bug in which the following is true:<code>
 	 * new java.sql.Timestamp( x ).getTime() != x
 	 * </code>
 	 * <p/>
 	 * NOTE : IBM JDK 1.3.1 the only known JVM to exhibit this behavior.
 	 *
 	 * @return True if the JVM's {@link Timestamp} implementa
 	 */
 	public static boolean jvmHasTimestampBug() {
 		return JVM_HAS_TIMESTAMP_BUG;
 	}
 
 	/**
 	 * Does this JVM handle {@link java.sql.Timestamp} in the JDK 1.4 compliant way wrt to nano rolling>
 	 *
 	 * @return True if the JDK 1.4 (JDBC3) specification for {@link java.sql.Timestamp} nano rolling is adhered to.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	public static boolean jvmHasJDK14Timestamp() {
 		return JVM_HAS_JDK14_TIMESTAMP;
 	}
 
 	/**
 	 * Does this JVM support {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap} are available.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 * @see java.util.LinkedHashSet
 	 * @see java.util.LinkedHashMap
 	 */
 	public static boolean jvmSupportsLinkedHashCollections() {
 		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	}
 
 	/**
 	 * Does this JDK/JVM define the JDBC {@link Statement} interface with a 'getGeneratedKeys' method?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if generated keys can be retrieved via Statement; false otherwise.
 	 *
 	 * @see Statement
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	public static boolean jvmSupportsGetGeneratedKeys() {
 		return JVM_SUPPORTS_GET_GENERATED_KEYS;
 	}
 
 	/**
 	 * Should we use streams to bind binary types to JDBC IN parameters?
 	 *
 	 * @return True if streams should be used for binary data handling; false otherwise.
 	 *
 	 * @see #USE_STREAMS_FOR_BINARY
 	 */
 	public static boolean useStreamsForBinary() {
 		return ENABLE_BINARY_STREAMS;
 	}
 
 	/**
 	 * Should we use reflection optimization?
 	 *
 	 * @return True if reflection optimization should be used; false otherwise.
 	 *
 	 * @see #USE_REFLECTION_OPTIMIZER
 	 * @see #getBytecodeProvider()
 	 * @see BytecodeProvider#getReflectionOptimizer
 	 */
 	public static boolean useReflectionOptimizer() {
 		return ENABLE_REFLECTION_OPTIMIZER;
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private Environment() {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Return <tt>System</tt> properties, extended by any properties specified
 	 * in <tt>hibernate.properties</tt>.
 	 * @return Properties
 	 */
 	public static Properties getProperties() {
 		Properties copy = new Properties();
 		copy.putAll(GLOBAL_PROPERTIES);
 		return copy;
 	}
 
 	/**
 	 * Get the name of a JDBC transaction isolation level
 	 *
 	 * @see java.sql.Connection
 	 * @param isolation as defined by <tt>java.sql.Connection</tt>
 	 * @return a human-readable name
 	 */
 	public static String isolationLevelToString(int isolation) {
 		return (String) ISOLATION_LEVELS.get( new Integer(isolation) );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
 		log.info( "Bytecode provider name : " + provider );
 		return buildBytecodeProvider( provider );
 	}
 
 	private static BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 		else if ( "cglib".equals( providerName ) ) {
 			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
 		}
 
 		log.warn( "unrecognized bytecode provider [" + providerName + "], using javassist by default" );
 		return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index b6321e8b7b..41440e08c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,456 +1,395 @@
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
-import java.sql.Connection;
-import java.sql.DatabaseMetaData;
-import java.sql.ResultSet;
-import java.sql.SQLException;
 import java.util.Map;
 import java.util.Properties;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
-import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.resolver.DialectFactory;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.exception.SQLExceptionConverterFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.BatchingBatcherFactory;
 import org.hibernate.jdbc.NonBatchingBatcherFactory;
 import org.hibernate.jdbc.util.SQLStatementLogger;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.transaction.TransactionFactoryFactory;
 import org.hibernate.transaction.TransactionManagerLookup;
 import org.hibernate.transaction.TransactionManagerLookupFactory;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 	private static final Logger log = LoggerFactory.getLogger( SettingsFactory.class );
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
-	public Settings buildSettings(Properties props, ConnectionProvider connections) {
+	public Settings buildSettings(Properties props, JdbcServices jdbcServices) {
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 
-		boolean metaSupportsScrollable = false;
-		boolean metaSupportsGetGeneratedKeys = false;
-		boolean metaSupportsBatchUpdates = false;
-		boolean metaReportsDDLCausesTxnCommit = false;
-		boolean metaReportsDDLInTxnSupported = true;
-		Dialect dialect = null;
-
-		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
-		// The need for it is intended to be alleviated with future development, thus it is
-		// not defined as an Environment constant...
-		//
-		// it is used to control whether we should consult the JDBC metadata to determine
-		// certain Settings default values; it is useful to *not* do this when the database
-		// may not be available (mainly in tools usage).
-		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", props, true );
-		if ( useJdbcMetadata ) {
-			try {
-				Connection conn = connections.getConnection();
-				try {
-					DatabaseMetaData meta = conn.getMetaData();
-					log.info( "Database ->\n" +
-							"       name : " + meta.getDatabaseProductName() + '\n' +
-							"    version : " +  meta.getDatabaseProductVersion() + '\n' +
-							"      major : " + meta.getDatabaseMajorVersion() + '\n' +
-							"      minor : " + meta.getDatabaseMinorVersion()
-					);
-					log.info( "Driver ->\n" +
-							"       name : " + meta.getDriverName() + '\n' +
-							"    version : " + meta.getDriverVersion() + '\n' +
-							"      major : " + meta.getDriverMajorVersion() + '\n' +
-							"      minor : " + meta.getDriverMinorVersion()
-					);
-
-					dialect = DialectFactory.buildDialect( props, conn );
-
-					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
-					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
-					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
-					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
-					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
-				}
-				catch ( SQLException sqle ) {
-					log.warn( "Could not obtain connection metadata", sqle );
-				}
-				finally {
-					connections.closeConnection( conn );
-				}
-			}
-			catch ( SQLException sqle ) {
-				log.warn( "Could not obtain connection to query metadata", sqle );
-				dialect = DialectFactory.buildDialect( props );
-			}
-			catch ( UnsupportedOperationException uoe ) {
-				// user supplied JDBC connections
-				dialect = DialectFactory.buildDialect( props );
-			}
-		}
-		else {
-			dialect = DialectFactory.buildDialect( props );
-		}
+		Dialect dialect = jdbcServices.getDialect();
+		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
-		settings.setDataDefinitionImplicitCommit( metaReportsDDLCausesTxnCommit );
-		settings.setDataDefinitionInTransactionSupported( metaReportsDDLInTxnSupported );
+		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
+		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 		settings.setDialect( dialect );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( dialect.getDefaultProperties() );
 		properties.putAll( props );
 
 		settings.setJdbcSupport( new JdbcSupport( ! ConfigurationHelper.getBoolean( Environment.NON_CONTEXTUAL_LOB_CREATION, properties ) ) );
 
 		// Transaction settings:
 
 		TransactionFactory transactionFactory = createTransactionFactory(properties);
 		settings.setTransactionFactory(transactionFactory);
 		settings.setTransactionManagerLookup( createTransactionManagerLookup(properties) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
 		log.info("Automatic flush during beforeCompletion(): " + enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
 		log.info("Automatic session close at end of transaction: " + enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
-		if ( !metaSupportsBatchUpdates ) batchSize = 0;
+		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
 		if (batchSize>0) log.info("JDBC batch size: " + batchSize);
 		settings.setJdbcBatchSize(batchSize);
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
 		if (batchSize>0) log.info("JDBC batch updates for versioned data: " + enabledDisabled(jdbcBatchVersionedData) );
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 		settings.setBatcherFactory( createBatcherFactory(properties, batchSize) );
 
-		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, metaSupportsScrollable);
+		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
 		log.info("Scrollable result sets: " + enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
 		log.debug( "Wrap result sets: " + enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
-		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, metaSupportsGetGeneratedKeys);
+		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		log.info("JDBC3 getGeneratedKeys(): " + enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
 		if (statementFetchSize!=null) log.info("JDBC result set fetch size: " + statementFetchSize);
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
 		log.info( "Connection release mode: " + releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = transactionFactory.getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
-			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT && !connections.supportsAggressiveRelease() ) {
+			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
+					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {			
 				log.warn( "Overriding release mode as connection provider does not support 'after_statement'" );
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
 		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
 		if (defaultSchema!=null) log.info("Default schema: " + defaultSchema);
 		if (defaultCatalog!=null) log.info("Default catalog: " + defaultCatalog);
 		settings.setDefaultSchemaName(defaultSchema);
 		settings.setDefaultCatalogName(defaultCatalog);
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
 		if (maxFetchDepth!=null) log.info("Maximum outer join fetch depth: " + maxFetchDepth);
 		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		log.info("Default batch fetch size: " + batchFetchSize);
 		settings.setDefaultBatchFetchSize(batchFetchSize);
 
 		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
 		log.info( "Generate SQL with comments: " + enabledDisabled(comments) );
 		settings.setCommentsEnabled(comments);
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
 		log.info( "Order SQL updates by primary key: " + enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled(orderUpdates);
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
 		log.info( "Order SQL inserts for batching: " + enabledDisabled( orderInserts ) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
 		log.info("Query language substitutions: " + querySubstitutions);
 		settings.setQuerySubstitutions(querySubstitutions);
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 		log.info( "JPA-QL strict compliance: " + enabledDisabled( jpaqlCompliance ) );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
 		log.info( "Second-level cache: " + enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
 		log.info( "Query cache: " + enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled(useQueryCache);
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		log.info( "Optimize cache for minimal puts: " + enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled(useMinimalPuts);
 
 		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
 		if ( StringHelper.isEmpty(prefix) ) prefix=null;
 		if (prefix!=null) log.info("Cache region prefix: "+ prefix);
 		settings.setCacheRegionPrefix(prefix);
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
 		log.info( "Structured second-level cache entries: " + enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
 
 		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//SQL Exception converter:
 
 		SQLExceptionConverter sqlExceptionConverter;
 		try {
 			sqlExceptionConverter = SQLExceptionConverterFactory.buildSQLExceptionConverter( dialect, properties );
 		}
 		catch(HibernateException e) {
 			log.warn("Error building SQLExceptionConverter; using minimal converter");
 			sqlExceptionConverter = SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter();
 		}
 		settings.setSQLExceptionConverter(sqlExceptionConverter);
 
 		//Statistics and logging:
 
 		boolean showSql = ConfigurationHelper.getBoolean(Environment.SHOW_SQL, properties);
 		if (showSql) log.info("Echoing all SQL to stdout");
 //		settings.setShowSqlEnabled(showSql);
 
 		boolean formatSql = ConfigurationHelper.getBoolean(Environment.FORMAT_SQL, properties);
 //		settings.setFormatSqlEnabled(formatSql);
 
 		settings.setSqlStatementLogger( new SQLStatementLogger( showSql, formatSql ) );
 
 		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
 		log.info( "Statistics: " + enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled(useStatistics);
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
 		log.info( "Deleted entity synthetic identifier rollback: " + enabledDisabled(useIdentifierRollback) );
 		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
 		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
 		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
 		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
 		if ( "create-drop".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema(true);
 			settings.setAutoDropSchema(true);
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
 		log.info( "Default entity-mode: " + defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
 		log.info( "Named query checking : " + enabledDisabled( namedQueryChecking ) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
 		log.info( "Check Nullability in Core (should be disabled when Bean Validation is on): " + enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 		else if ( "cglib".equals( providerName ) ) {
 			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
 		}
 		else {
 			log.debug( "using javassist as bytecode provider by default" );
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
 		log.info("Query cache factory: " + queryCacheFactoryClassName);
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		String regionFactoryClassName = ConfigurationHelper.getString( Environment.CACHE_REGION_FACTORY, properties, null );
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		log.info( "Cache region factory : " + regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException nsme ) {
 				// no constructor accepting Properties found, try no arg constructor
 				log.debug(
 						regionFactoryClassName + " did not provide constructor accepting java.util.Properties; " +
 								"attempting no-arg constructor."
 				);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
 		log.info("Query translator: " + className);
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 
 	protected BatcherFactory createBatcherFactory(Properties properties, int batchSize) {
 		String batcherClass = properties.getProperty(Environment.BATCH_STRATEGY);
 		if (batcherClass==null) {
 			return batchSize == 0
 					? new NonBatchingBatcherFactory()
 					: new BatchingBatcherFactory();
 		}
 		else {
 			log.info("Batcher factory: " + batcherClass);
 			try {
 				return (BatcherFactory) ReflectHelper.classForName(batcherClass).newInstance();
 			}
 			catch (Exception cnfe) {
 				throw new HibernateException("could not instantiate BatcherFactory: " + batcherClass, cnfe);
 			}
 		}
 	}
 
 	protected TransactionFactory createTransactionFactory(Properties properties) {
 		return TransactionFactoryFactory.buildTransactionFactory(properties);
 	}
 
 	protected TransactionManagerLookup createTransactionManagerLookup(Properties properties) {
 		return TransactionManagerLookupFactory.getTransactionManagerLookup(properties);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java
deleted file mode 100644
index 57e5206b19..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java
+++ /dev/null
@@ -1,79 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.resolver;
-
-import java.sql.DatabaseMetaData;
-import java.sql.SQLException;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-import org.hibernate.dialect.Dialect;
-import org.hibernate.exception.JDBCConnectionException;
-import org.hibernate.JDBCException;
-
-/**
- * A templated resolver impl which delegates to the {@link #resolveDialectInternal} method
- * and handles any thrown {@link SQLException}s.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractDialectResolver implements DialectResolver {
-	private static final Logger log = LoggerFactory.getLogger( AbstractDialectResolver.class );
-
-	/**
-	 * {@inheritDoc}
-	 * <p/>
-	 * Here we template the resolution, delegating to {@link #resolveDialectInternal} and handling
-	 * {@link java.sql.SQLException}s properly.
-	 */
-	public final Dialect resolveDialect(DatabaseMetaData metaData) {
-		try {
-			return resolveDialectInternal( metaData );
-		}
-		catch ( SQLException sqlException ) {
-			JDBCException jdbcException = BasicSQLExceptionConverter.INSTANCE.convert( sqlException );
-			if ( jdbcException instanceof JDBCConnectionException ) {
-				throw jdbcException;
-			}
-			else {
-				log.warn( BasicSQLExceptionConverter.MSG + " : " + sqlException.getMessage() );
-				return null;
-			}
-		}
-		catch ( Throwable t ) {
-			log.warn( "Error executing resolver [" + this + "] : " + t.getMessage() );
-			return null;
-		}
-	}
-
-	/**
-	 * Perform the actual resolution without caring about handling {@link SQLException}s.
-	 *
-	 * @param metaData The database metadata
-	 * @return The resolved dialect, or null if we could not resolve.
-	 * @throws SQLException Indicates problems accessing the metadata.
-	 */
-	protected abstract Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException;
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java
deleted file mode 100644
index 49de0758a1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java
+++ /dev/null
@@ -1,165 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.resolver;
-
-import java.sql.Connection;
-import java.sql.SQLException;
-import java.sql.DatabaseMetaData;
-import java.util.Properties;
-
-import org.hibernate.HibernateException;
-import org.hibernate.dialect.resolver.DialectResolver;
-import org.hibernate.dialect.resolver.DialectResolverSet;
-import org.hibernate.dialect.resolver.StandardDialectResolver;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.cfg.Environment;
-import org.hibernate.util.ReflectHelper;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-/**
- * A factory for generating Dialect instances.
- *
- * @author Steve Ebersole
- * @author Tomoto Shimizu Washio
- */
-public class DialectFactory {
-	private static final Logger log = LoggerFactory.getLogger( DialectFactory.class );
-
-	private static DialectResolverSet DIALECT_RESOLVERS = new DialectResolverSet();
-
-	static {
-		// register the standard dialect resolver
-		DIALECT_RESOLVERS.addResolver( new StandardDialectResolver() );
-
-		// register resolvers set via Environment property
-		String userSpecifedResolverSetting = Environment.getProperties().getProperty( Environment.DIALECT_RESOLVERS );
-		if ( userSpecifedResolverSetting != null ) {
-			String[] userSpecifedResolvers = userSpecifedResolverSetting.split( "\\s+" );
-			for ( int i = 0; i < userSpecifedResolvers.length; i++ ) {
-				registerDialectResolver( userSpecifedResolvers[i] );
-			}
-		}
-	}
-
-	/*package*/ static void registerDialectResolver(String resolverName) {
-		try {
-			DialectResolver resolver = ( DialectResolver ) ReflectHelper.classForName( resolverName ).newInstance();
-			DIALECT_RESOLVERS.addResolverAtFirst( resolver );
-		}
-		catch ( ClassNotFoundException cnfe ) {
-			log.warn( "Dialect resolver class not found: " + resolverName );
-		}
-		catch ( Exception e ) {
-			log.warn( "Could not instantiate dialect resolver class", e );
-		}
-	}
-
-	/**
-	 * Builds an appropriate Dialect instance.
-	 * <p/>
-	 * If a dialect is explicitly named in the incoming properties, it is used. Otherwise, it is
-	 * determined by dialect resolvers based on the passed connection.
-	 * <p/>
-	 * An exception is thrown if a dialect was not explicitly set and no resolver could make
-	 * the determination from the given connection.
-	 *
-	 * @param properties The configuration properties.
-	 * @param connection The configured connection.
-	 * @return The appropriate dialect instance.
-	 * @throws HibernateException No dialect specified and no resolver could make the determination.
-	 */
-	public static Dialect buildDialect(Properties properties, Connection connection) throws HibernateException {
-		String dialectName = properties.getProperty( Environment.DIALECT );
-		if ( dialectName == null ) {
-			return determineDialect( connection );
-		}
-		else {
-			return constructDialect( dialectName );
-		}
-	}
-
-	public static  Dialect buildDialect(Properties properties) {
-		String dialectName = properties.getProperty( Environment.DIALECT );
-		if ( dialectName == null ) {
-			throw new HibernateException( "'hibernate.dialect' must be set when no Connection available" );
-		}
-		return constructDialect( dialectName );
-	}
-
-	/**
-	 * Determine the appropriate Dialect to use given the connection.
-	 *
-	 * @param connection The configured connection.
-	 * @return The appropriate dialect instance.
-	 *
-	 * @throws HibernateException No connection given or no resolver could make
-	 * the determination from the given connection.
-	 */
-	private static Dialect determineDialect(Connection connection) {
-		if ( connection == null ) {
-			throw new HibernateException( "Connection cannot be null when 'hibernate.dialect' not set" );
-		}
-
-		try {
-			final DatabaseMetaData databaseMetaData = connection.getMetaData();
-			final Dialect dialect = DIALECT_RESOLVERS.resolveDialect( databaseMetaData );
-
-			if ( dialect == null ) {
-				throw new HibernateException(
-						"Unable to determine Dialect to use [name=" + databaseMetaData.getDatabaseProductName() +
-								", majorVersion=" + databaseMetaData.getDatabaseMajorVersion() +
-								"]; user must register resolver or explicitly set 'hibernate.dialect'"
-				);
-			}
-
-			return dialect;
-		}
-		catch ( SQLException sqlException ) {
-			throw new HibernateException(
-					"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
-					sqlException
-			);
-		}
-	}
-
-	/**
-	 * Returns a dialect instance given the name of the class to use.
-	 *
-	 * @param dialectName The name of the dialect class.
-	 *
-	 * @return The dialect instance.
-	 */
-	public static Dialect constructDialect(String dialectName) {
-		try {
-			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
-		}
-		catch ( ClassNotFoundException cnfe ) {
-			throw new HibernateException( "Dialect class not found: " + dialectName, cnfe );
-		}
-		catch ( Exception e ) {
-			throw new HibernateException( "Could not instantiate dialect class", e );
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java
deleted file mode 100644
index 31162e171c..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java
+++ /dev/null
@@ -1,50 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.resolver;
-
-import java.sql.Connection;
-import java.sql.DatabaseMetaData;
-
-import org.hibernate.dialect.Dialect;
-import org.hibernate.exception.JDBCConnectionException;
-
-/**
- * Contract for determining the {@link Dialect} to use based on a JDBC {@link Connection}.
- * 
- * @author Tomoto Shimizu Washio
- * @author Steve Ebersole
- */
-public interface DialectResolver {
-	/**
-	 * Determine the {@link Dialect} to use based on the given JDBC {@link DatabaseMetaData}.  Implementations are
-	 * expected to return the {@link Dialect} instance to use, or null if the {@link DatabaseMetaData} does not match
-	 * the criteria handled by this impl.
-	 * 
-	 * @param metaData The JDBC metadata.
-	 * @return The dialect to use, or null.
-	 * @throws JDBCConnectionException Indicates a 'non transient connection problem', which indicates that
-	 * we should stop resolution attempts.
-	 */
-	public Dialect resolveDialect(DatabaseMetaData metaData) throws JDBCConnectionException;
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java
deleted file mode 100644
index 9ad308d436..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java
+++ /dev/null
@@ -1,91 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.resolver;
-
-import java.sql.DatabaseMetaData;
-import java.util.ArrayList;
-import java.util.Iterator;
-import java.util.List;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-import org.hibernate.dialect.Dialect;
-import org.hibernate.exception.JDBCConnectionException;
-
-/**
- * A {@link DialectResolver} implementation which coordinates resolution by delegating to its
- * registered sub-resolvers.  Sub-resolvers may be registered by calling either {@link #addResolver} or
- * {@link #addResolverAtFirst}.
- *
- * @author Tomoto Shimizu Washio
- */
-public class DialectResolverSet implements DialectResolver {
-	private static Logger log = LoggerFactory.getLogger( DialectResolverSet.class );
-
-	private List resolvers = new ArrayList();
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Dialect resolveDialect(DatabaseMetaData metaData) {
-		Iterator i = resolvers.iterator();
-		while ( i.hasNext() ) {
-			final DialectResolver resolver = ( DialectResolver ) i.next();
-			try {
-				Dialect dialect = resolver.resolveDialect( metaData );
-				if ( dialect != null ) {
-					return dialect;
-				}
-			}
-			catch ( JDBCConnectionException e ) {
-				throw e;
-			}
-			catch ( Throwable t ) {
-				log.info( "sub-resolver threw unexpected exception, continuing to next : " + t.getMessage() );
-			}
-		}
-		return null;
-	}
-
-	/**
-	 * Add a resolver at the end of the underlying resolver list.  The resolver added by this method is at lower
-	 * priority than any other existing resolvers.
-	 *
-	 * @param resolver The resolver to add.
-	 */
-	public void addResolver(DialectResolver resolver) {
-		resolvers.add( resolver );
-	}
-
-	/**
-	 * Add a resolver at the beginning of the underlying resolver list.  The resolver added by this method is at higher
-	 * priority than any other existing resolvers.
-	 *
-	 * @param resolver The resolver to add.
-	 */
-	public void addResolverAtFirst(DialectResolver resolver) {
-		resolvers.add( 0, resolver );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java
deleted file mode 100644
index 5d61dff205..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java
+++ /dev/null
@@ -1,137 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.resolver;
-
-import java.sql.DatabaseMetaData;
-import java.sql.SQLException;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.HSQLDialect;
-import org.hibernate.dialect.H2Dialect;
-import org.hibernate.dialect.MySQLDialect;
-import org.hibernate.dialect.PostgreSQLDialect;
-import org.hibernate.dialect.DerbyDialect;
-import org.hibernate.dialect.Ingres10Dialect;
-import org.hibernate.dialect.Ingres9Dialect;
-import org.hibernate.dialect.IngresDialect;
-import org.hibernate.dialect.SQLServerDialect;
-import org.hibernate.dialect.InformixDialect;
-import org.hibernate.dialect.DB2Dialect;
-import org.hibernate.dialect.Oracle10gDialect;
-import org.hibernate.dialect.Oracle9iDialect;
-import org.hibernate.dialect.Oracle8iDialect;
-import org.hibernate.dialect.SybaseAnywhereDialect;
-import org.hibernate.dialect.SybaseASE15Dialect;
-
-/**
- * The standard Hibernate resolver.
- *
- * @author Steve Ebersole
- */
-public class StandardDialectResolver extends AbstractDialectResolver{
-	private static final Logger log = LoggerFactory.getLogger( StandardDialectResolver.class );
-
-	protected Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
-		String databaseName = metaData.getDatabaseProductName();
-		int databaseMajorVersion = metaData.getDatabaseMajorVersion();
-
-		if ( "HSQL Database Engine".equals( databaseName ) ) {
-			return new HSQLDialect();
-		}
-
-		if ( "H2".equals( databaseName ) ) {
-			return new H2Dialect();
-		}
-
-		if ( "MySQL".equals( databaseName ) ) {
-			return new MySQLDialect();
-		}
-
-		if ( "PostgreSQL".equals( databaseName ) ) {
-			return new PostgreSQLDialect();
-		}
-
-		if ( "Apache Derby".equals( databaseName ) ) {
-			return new DerbyDialect();
-		}
-
-		if ( "ingres".equalsIgnoreCase( databaseName ) ) {
-            switch( databaseMajorVersion ) {
-                case 9:
-                    int databaseMinorVersion = metaData.getDatabaseMinorVersion();
-                    if (databaseMinorVersion > 2) {
-                        return new Ingres9Dialect();
-                    }
-                    return new IngresDialect();
-                case 10:
-                    log.warn( "Ingres " + databaseMajorVersion + " is not yet fully supported; using Ingres 9.3 dialect" );
-                    return new Ingres10Dialect();
-                default:
-                    log.warn( "Unknown Ingres major version [" + databaseMajorVersion + "] using Ingres 9.2 dialect" );
-            }
-			return new IngresDialect();
-		}
-
-		if ( databaseName.startsWith( "Microsoft SQL Server" ) ) {
-			return new SQLServerDialect();
-		}
-
-		if ( "Sybase SQL Server".equals( databaseName ) || "Adaptive Server Enterprise".equals( databaseName ) ) {
-			return new SybaseASE15Dialect();
-		}
-
-		if ( databaseName.startsWith( "Adaptive Server Anywhere" ) ) {
-			return new SybaseAnywhereDialect();
-		}
-
-		if ( "Informix Dynamic Server".equals( databaseName ) ) {
-			return new InformixDialect();
-		}
-
-		if ( databaseName.startsWith( "DB2/" ) ) {
-			return new DB2Dialect();
-		}
-
-		if ( "Oracle".equals( databaseName ) ) {
-			switch ( databaseMajorVersion ) {
-				case 11:
-					log.warn( "Oracle 11g is not yet fully supported; using 10g dialect" );
-					return new Oracle10gDialect();
-				case 10:
-					return new Oracle10gDialect();
-				case 9:
-					return new Oracle9iDialect();
-				case 8:
-					return new Oracle8iDialect();
-				default:
-					log.warn( "unknown Oracle major version [" + databaseMajorVersion + "]" );
-			}
-		}
-
-		return null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index 2b9b6c64bf..69b7901273 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,368 +1,369 @@
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
 package org.hibernate.engine;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Transaction;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
 import org.hibernate.jdbc.Batcher;
 import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 
 /**
  * Defines the internal contract between the <tt>Session</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt> or <tt>EntityPersister</tt>.
  *
  * @see org.hibernate.Session the interface to the application
  * @see org.hibernate.impl.SessionImpl the actual implementation
  * @author Gavin King
  */
 public interface SessionImplementor extends Serializable {
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	public Interceptor getInterceptor();
 	
 	/**
 	 * Enable/disable automatic cache clearing from after transaction
 	 * completion (for EJB3)
 	 */
 	public void setAutoClear(boolean enabled);
 		
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	public boolean isTransactionInProgress();
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	public void initializeCollection(PersistentCollection collection, boolean writing) 
 	throws HibernateException;
 	
 	/**
 	 * Load an instance without checking if it was deleted. 
 	 * 
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or 
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 * 
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies 
 	 * (but might return an existing proxy); if it does not exist, return 
 	 * <tt>null</tt>.
 	 * 
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) 
 	throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * System time before the start of the transaction
 	 */
 	public long getTimestamp();
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	public SessionFactoryImplementor getFactory();
 	/**
 	 * Get the prepared statement <tt>Batcher</tt> for this session
 	 */
 	public Batcher getBatcher();
 	
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	public List list(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a criteria query
 	 */
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode);
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(CriteriaImpl criteria);
 	
 	/**
 	 * Execute a filter
 	 */
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Iterate a filter
 	 */
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 	
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Notify the session that the transaction completed, so we no longer
 	 * own the old locks. (Also we should release cache softlocks.) May
 	 * be called multiple times during the transaction completion process.
 	 * Also called after an autocommit, in which case the second argument
 	 * is null.
 	 */
 	public void afterTransactionCompletion(boolean successful, Transaction tx);
 	
 	/**
 	 * Notify the session that the transaction is about to complete
 	 */
 	public void beforeTransactionCompletion(Transaction tx);
 
 	/**
 	 * Return the identifier of the persistent object, or null if 
 	 * not associated with the session
 	 */
 	public Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	public String bestGuessEntityName(Object object);
 	
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	public String guessEntityName(Object entity) throws HibernateException;
 	
 	/** 
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	public Object instantiate(String entityName, Serializable id) throws HibernateException;
 	
 	/**
 	 * Execute an SQL Query
 	 */
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) 
 	throws HibernateException;
 	
 	/**
 	 * Execute an SQL Query
 	 */
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) 
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The result list.
 	 * @throws HibernateException
 	 */
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The resulting scrollable result.
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Retreive the currently set value for a filter parameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter parameter value.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Object getFilterParameterValue(String filterParameterName);
 
 	/**
 	 * Retreive the type for a given filter parrameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter param type
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Type getFilterParameterType(String filterParameterName);
 
 	/**
 	 * Return the currently enabled filters.  The filter map is keyed by filter
 	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Map getEnabledFilters();
 	
 	public int getDontFlushFromFind();
 	
 	/**
 	 * Retrieves the configured event listeners from this event source.
 	 *
 	 * @return The configured event listeners.
 	 */
 	public EventListeners getListeners();
 	
 	//TODO: temporary
 	
 	/**
 	 * Get the persistence context for this session
 	 */
 	public PersistenceContext getPersistenceContext();
 	
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 	
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException;
 
 
 	/**
 	 * Return changes to this session that have not been flushed yet.
 	 *
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException;
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException;	
 
 	// copied from Session:
 	
 	public EntityMode getEntityMode();
 	public CacheMode getCacheMode();
 	public void setCacheMode(CacheMode cm);
 	public boolean isOpen();
 	public boolean isConnected();
 	public FlushMode getFlushMode();
 	public void setFlushMode(FlushMode fm);
 	public Connection connection();
 	public void flush();
 	
 	/**
 	 * Get a Query instance for a named query or named native SQL query
 	 */
 	public Query getNamedQuery(String name);
 	/**
 	 * Get a Query instance for a named native SQL query
 	 */
 	public Query getNamedSQLQuery(String name);
 	
 	public boolean isEventSource();
 
 	public void afterScrollOperation();
 
 	/**
 	 * Get the <i>internal</i> fetch profile currently associated with this session.
 	 *
 	 * @return The current internal fetch profile, or null if none currently associated.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public String getFetchProfile();
 
 	/**
 	 * Set the current <i>internal</i> fetch profile for this session.
 	 *
 	 * @param name The internal fetch profile name to use
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public void setFetchProfile(String name);
 
 	public JDBCContext getJDBCContext();
 
 	/**
 	 * Determine whether the session is closed.  Provided seperately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synch
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return True if the session is closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 * should never be null.
 	 */
 	public LoadQueryInfluencers getLoadQueryInfluencers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
index 61708ae6d6..a0c77f26d2 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
@@ -1,281 +1,281 @@
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
 package org.hibernate.hql.ast.exec;
 
 import java.sql.PreparedStatement;
 import java.sql.Connection;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.List;
 import java.util.Collections;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.BulkOperationCleanupAction;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.transaction.Isolater;
 import org.hibernate.engine.transaction.IsolatedWork;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.SqlGenerator;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InsertSelect;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.util.JDBCExceptionReporter;
 import org.hibernate.util.StringHelper;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of AbstractStatementExecutor.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStatementExecutor implements StatementExecutor {
 	private static final Logger LOG = LoggerFactory.getLogger( AbstractStatementExecutor.class );
 
 	private final Logger log;
 	private final HqlSqlWalker walker;
 	private List idSelectParameterSpecifications = Collections.EMPTY_LIST;
 
 	public AbstractStatementExecutor(HqlSqlWalker walker, Logger log) {
 		this.walker = walker;
 		this.log = log;
 	}
 
 	protected HqlSqlWalker getWalker() {
 		return walker;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return walker.getSessionFactoryHelper().getFactory();
 	}
 
 	protected List getIdSelectParameterSpecifications() {
 		return idSelectParameterSpecifications;
 	}
 
 	protected abstract Queryable[] getAffectedQueryables();
 
 	protected String generateIdInsertSelect(Queryable persister, String tableAlias, AST whereClause) {
 		Select select = new Select( getFactory().getDialect() );
 		SelectFragment selectFragment = new SelectFragment()
 				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 
 		String rootTableName = persister.getTableName();
 		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
 		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
 
 		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
 		if ( whereJoinFragment == null ) {
 			whereJoinFragment = "";
 		}
 		else {
 			whereJoinFragment = whereJoinFragment.trim();
 			if ( whereJoinFragment.startsWith( "and" ) ) {
 				whereJoinFragment = whereJoinFragment.substring( 4 );
 			}
 		}
 
 		String userWhereClause = "";
 		if ( whereClause.getNumberOfChildren() != 0 ) {
 			// If a where clause was specified in the update/delete query, use it to limit the
 			// returned ids here...
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( getFactory() );
 				sqlGenerator.whereClause( whereClause );
 				userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
 				idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
 			}
 			catch ( RecognitionException e ) {
 				throw new HibernateException( "Unable to generate id select for DML operation", e );
 			}
 			if ( whereJoinFragment.length() > 0 ) {
 				whereJoinFragment += " and ";
 			}
 		}
 
 		select.setWhereClause( whereJoinFragment + userWhereClause );
 
 		InsertSelect insert = new InsertSelect( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
 		}
 		insert.setTableName( persister.getTemporaryIdTableName() );
 		insert.setSelect( select );
 		return insert.toStatementString();
 	}
 
 	protected String generateIdSubselect(Queryable persister) {
 		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
 			        " from " + persister.getTemporaryIdTableName();
 	}
 
 	protected void createTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
 		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
 		IsolatedWork work = new IsolatedWork() {
 			public void doWork(Connection connection) throws HibernateException {
 				try {
 					Statement statement = connection.createStatement();
 					try {
 						statement.executeUpdate( persister.getTemporaryIdTableDDL() );
 						JDBCExceptionReporter.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
 					}
 					finally {
 						try {
 							statement.close();
 						}
 						catch( Throwable ignore ) {
 							// ignore
 						}
 					}
 				}
 				catch( Exception e ) {
 					log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
 				}
 			}
 		};
 		if ( shouldIsolateTemporaryTableDDL() ) {
 			if ( getFactory().getSettings().isDataDefinitionInTransactionSupported() ) {
 				Isolater.doIsolatedWork( work, session );
 			}
 			else {
 				Isolater.doNonTransactedWork( work, session );
 			}
 		}
 		else {
 			work.doWork( session.getJDBCContext().getConnectionManager().getConnection() );
 			session.getJDBCContext().getConnectionManager().afterStatement();
 		}
 	}
 
 	private static JDBCExceptionReporter.WarningHandler CREATION_WARNING_HANDLER = new JDBCExceptionReporter.WarningHandlerLoggingSupport() {
 		public boolean doProcess() {
 			return LOG.isDebugEnabled();
 		}
 
 		public void prepare(SQLWarning warning) {
 			LOG.debug( "Warnings creating temp table", warning );
 		}
 
 		@Override
 		protected void logWarning(String description, String message) {
 			LOG.debug( description );
 			LOG.debug( message );
 		}
 	};
 
 	protected void dropTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		if ( getFactory().getDialect().dropTemporaryTableAfterUse() ) {
 			IsolatedWork work = new IsolatedWork() {
 				public void doWork(Connection connection) throws HibernateException {
-					final String command = session.getFactory().getSettings().getDialect().getDropTemporaryTableString()
+					final String command = session.getFactory().getDialect().getDropTemporaryTableString()
 							+ ' ' + persister.getTemporaryIdTableName();
 					try {
 						Statement statement = connection.createStatement();
 						try {
 							statement = connection.createStatement();
 							statement.executeUpdate( command );
 						}
 						finally {
 							try {
 								statement.close();
 							}
 							catch( Throwable ignore ) {
 								// ignore
 							}
 						}
 					}
 					catch( Exception e ) {
 						log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
 					}
 				}
 			};
 
 			if ( shouldIsolateTemporaryTableDDL() ) {
 				if ( getFactory().getSettings().isDataDefinitionInTransactionSupported() ) {
 					Isolater.doIsolatedWork( work, session );
 				}
 				else {
 					Isolater.doNonTransactedWork( work, session );
 				}
 			}
 			else {
 				work.doWork( session.getJDBCContext().getConnectionManager().getConnection() );
 				session.getJDBCContext().getConnectionManager().afterStatement();
 			}
 		}
 		else {
 			// at the very least cleanup the data :)
 			PreparedStatement ps = null;
 			try {
 				ps = session.getBatcher().prepareStatement( "delete from " + persister.getTemporaryIdTableName() );
 				ps.executeUpdate();
 			}
 			catch( Throwable t ) {
 				log.warn( "unable to cleanup temporary id table after use [" + t + "]" );
 			}
 			finally {
 				if ( ps != null ) {
 					try {
 						session.getBatcher().closeStatement( ps );
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 		}
 	}
 
 	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getAffectedQueryables() );
 
 		if ( session.isEventSource() ) {
 			( ( EventSource ) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	protected boolean shouldIsolateTemporaryTableDDL() {
 		Boolean dialectVote = getFactory().getDialect().performTemporaryTableDDLInIsolation();
 		if ( dialectVote != null ) {
 			return dialectVote.booleanValue();
 		}
 		else {
 			return getFactory().getSettings().isDataDefinitionImplicitCommit();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 9baccd21ff..0b7d5e145f 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1336 +1,1339 @@
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
 package org.hibernate.impl;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import javax.transaction.TransactionManager;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.context.CurrentSessionContext;
 import org.hibernate.context.JTASessionContext;
 import org.hibernate.context.ManagedSessionContext;
 import org.hibernate.context.ThreadLocalSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.event.EventListeners;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.PersisterFactory;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServicesRegistry;
 import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.EmptyIterator;
 import org.hibernate.util.ReflectHelper;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.classic.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl implements SessionFactory, SessionFactoryImplementor {
 
 	private static final Logger log = LoggerFactory.getLogger(SessionFactoryImpl.class);
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map collectionPersisters;
 	private final transient Map collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map identifierGenerators;
 	private final transient Map namedQueries;
 	private final transient Map namedSqlQueries;
 	private final transient Map sqlResultSetMappings;
 	private final transient Map filters;
 	private final transient Map fetchProfiles;
 	private final transient Map imports;
 	private final transient Interceptor interceptor;
 	private final transient ServicesRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient TransactionManager transactionManager;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map queryCaches;
 	private final transient Map allCacheRegions = new HashMap();
 	private final transient Statistics statistics;
 	private final transient EventListeners eventListeners;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient EntityNotFoundDelegate entityNotFoundDelegate;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserver observer;
 	private final transient HashMap entityNameResolvers = new HashMap();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 
 	public SessionFactoryImpl(
 			Configuration cfg,
 	        Mapping mapping,
 			ServicesRegistry serviceRegistry,
 	        Settings settings,
 	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
 		log.info("building session factory");
 
 		this.statistics = new ConcurrentStatisticsImpl( this );
 		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
 		log.debug( "Statistics initialized [enabled={}]}", settings.isStatisticsEnabled() );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
 		this.serviceRegistry = serviceRegistry;
 		this.settings = settings;
-		this.sqlFunctionRegistry = new SQLFunctionRegistry(settings.getDialect(), cfg.getSqlFunctions());
+		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
         this.eventListeners = listeners;
 		this.observer = observer != null ? observer : new SessionFactoryObserver() {
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 			public void sessionFactoryClosed(SessionFactory factory) {
 			}
 		};
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		if ( log.isDebugEnabled() ) {
 			log.debug("Session factory constructed with filter configurations : " + filters);
 		}
 
 		if ( log.isDebugEnabled() ) {
 			log.debug(
 					"instantiating session factory with properties: " + properties
 			);
 		}
 
 		// Caches
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
-						settings.getDialect(),
+						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
-			model.prepareTemporaryTables( mapping, settings.getDialect() );
+			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					log.trace( "Building cache for entity data [" + model.getEntityName() + "]" );
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = PersisterFactory.createClassPersister( model, accessStrategy, this, mapping );
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				log.trace( "Building cache for collection data [" + model.getRole() + "]" );
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = PersisterFactory.createCollectionPersister( cfg, model, accessStrategy, this) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap( cfg.getSqlResultSetMappings() );
 		imports = new HashMap( cfg.getImports() );
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryObjectFactory.addInstance(uuid, name, this, properties);
 
 		log.debug("instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( getJdbcServices(), cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( getJdbcServices(), cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( getJdbcServices(), cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( getJdbcServices(), cfg );
 		}
 
 		if ( settings.getTransactionManagerLookup()!=null ) {
 			log.debug("obtaining JTA TransactionManager");
 			transactionManager = settings.getTransactionManagerLookup().getTransactionManager(properties);
 		}
 		else {
 			if ( settings.getTransactionFactory().isTransactionManagerRequired() ) {
 				throw new HibernateException("The chosen transaction strategy requires access to the JTA TransactionManager");
 			}
 			transactionManager = null;
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new HashMap();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			Map errors = checkNamedQueries();
 			if ( !errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = ( String ) iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
 					if ( iterator.hasNext() ) {
 						failingQueries.append( ", " );
 					}
 					log.error( "Error in named query: " + queryName, e );
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// EntityNotFoundDelegate
 		EntityNotFoundDelegate entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 		if ( entityNotFoundDelegate == null ) {
 			entityNotFoundDelegate = new EntityNotFoundDelegate() {
 				public void handleEntityNotFound(String entityName, Serializable id) {
 					throw new ObjectNotFoundException( id, entityName );
 				}
 			};
 		}
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			Iterator fetches = mappingProfile.getFetches().iterator();
 			while ( fetches.hasNext() ) {
 				final org.hibernate.mapping.FetchProfile.Fetch mappingFetch =
 						( org.hibernate.mapping.FetchProfile.Fetch ) fetches.next();
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizerMapping() == null ) {
 			return;
 		}
 		Iterator itr = persister.getEntityMetamodel().getTuplizerMapping().iterateTuplizers();
 		while ( itr.hasNext() ) {
 			final EntityTuplizer tuplizer = ( EntityTuplizer ) itr.next();
 			registerEntityNameResolvers( tuplizer );
 		}
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( int i = 0; i < resolvers.length; i++ ) {
 			registerEntityNameResolver( resolvers[i], tuplizer.getEntityMode() );
 		}
 	}
 
 	public void registerEntityNameResolver(EntityNameResolver resolver, EntityMode entityMode) {
 		LinkedHashSet resolversForMode = ( LinkedHashSet ) entityNameResolvers.get( entityMode );
 		if ( resolversForMode == null ) {
 			resolversForMode = new LinkedHashSet();
 			entityNameResolvers.put( entityMode, resolversForMode );
 		}
 		resolversForMode.add( resolver );
 	}
 
 	public Iterator iterateEntityNameResolvers(EntityMode entityMode) {
 		Set actualEntityNameResolvers = ( Set ) entityNameResolvers.get( entityMode );
 		return actualEntityNameResolvers == null
 				? EmptyIterator.INSTANCE
 				: actualEntityNameResolvers.iterator();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map checkNamedQueries() throws HibernateException {
 		Map errors = new HashMap();
 
 		// Check named HQL queries
 		log.debug("Checking " + namedQueries.size() + " named HQL queries");
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named query: " + queryName);
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		log.debug("Checking " + namedSqlQueries.size() + " named SQL queries");
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named SQL query: " + queryName);
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = ( ResultSetMappingDefinition ) sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		return errors;
 	}
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, this );
 	}
 
 	private SessionImpl openSession(
 		Connection connection,
 	    boolean autoClose,
 	    long timestamp,
 	    Interceptor sessionLocalInterceptor
 	) {
 		return new SessionImpl(
 		        connection,
 		        this,
 		        autoClose,
 		        timestamp,
 		        sessionLocalInterceptor == null ? interceptor : sessionLocalInterceptor,
 		        settings.getDefaultEntityMode(),
 		        settings.isFlushBeforeCompletionEnabled(),
 		        settings.isAutoCloseSessionEnabled(),
 		        settings.getConnectionReleaseMode()
 			);
 	}
 
 	public org.hibernate.classic.Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public org.hibernate.classic.Session openSession(Interceptor sessionLocalInterceptor)
 	throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public org.hibernate.classic.Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public org.hibernate.classic.Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public org.hibernate.classic.Session openTemporarySession() throws HibernateException {
 		return new SessionImpl(
 				null,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        false,
 		        false,
 		        ConnectionReleaseMode.AFTER_STATEMENT
 			);
 	}
 
 	public org.hibernate.classic.Session openSession(
 			final Connection connection,
 	        final boolean flushBeforeCompletionEnabled,
 	        final boolean autoCloseSessionEnabled,
 	        final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
 		return new SessionImpl(
 				connection,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        flushBeforeCompletionEnabled,
 		        autoCloseSessionEnabled,
 		        connectionReleaseMode
 			);
 	}
 
 	public org.hibernate.classic.Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = (EntityPersister) entityPersisters.get(entityName);
 		if (result==null) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = (CollectionPersister) collectionPersisters.get(role);
 		if (result==null) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	public Dialect getDialect() {
-		return settings.getDialect();
+		if ( serviceRegistry == null ) {
+			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
+		}
+		return serviceRegistry.getService( JdbcServices.class ).getDialect();
 	}
 
 	public Interceptor getInterceptor()
 	{
 		return interceptor;
 	}
 
 	public TransactionFactory getTransactionFactory() {
 		return settings.getTransactionFactory();
 	}
 
 	public TransactionManager getTransactionManager() {
 		return transactionManager;
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return settings.getSQLExceptionConverter();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	// from javax.naming.Referenceable
 	public Reference getReference() throws NamingException {
 		log.debug("Returning a Reference to the SessionFactory");
 		return new Reference(
 			SessionFactoryImpl.class.getName(),
 		    new StringRefAddr("uuid", uuid),
 		    SessionFactoryObjectFactory.class.getName(),
 		    null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
 		log.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
 			if (result==null) {
 				throw new InvalidObjectException("Could not find a SessionFactory named: " + name);
 			}
 			else {
 				log.debug("resolved SessionFactory by name");
 			}
 		}
 		else {
 			log.debug("resolved SessionFactory by uid");
 		}
 		return result;
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return (NamedQueryDefinition) namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return (NamedSQLQueryDefinition) namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return (ResultSetMappingDefinition) sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		log.trace("deserializing");
 		in.defaultReadObject();
 		log.debug("deserialized: " + uuid);
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		log.debug("serializing: " + uuid);
 		out.defaultWriteObject();
 		log.trace("serialized");
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return (CollectionMetadata) collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return (ClassMetadata) classMetadata.get(entityName);
 	}
 
 	/**
 	 * Return the names of all persistent (mapped) classes that extend or implement the
 	 * given class or interface, accounting for implicit/explicit polymorphism settings
 	 * and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList results = new ArrayList();
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			//test this entity to see if we must query it
 			EntityPersister testPersister = (EntityPersister) iter.next();
 			if ( testPersister instanceof Queryable ) {
 				Queryable testQueryable = (Queryable) testPersister;
 				String testClassName = testQueryable.getEntityName();
 				boolean isMappedClass = className.equals(testClassName);
 				if ( testQueryable.isExplicitPolymorphism() ) {
 					if ( isMappedClass ) {
 						return new String[] {className}; //NOTE EARLY EXIT
 					}
 				}
 				else {
 					if (isMappedClass) {
 						results.add(testClassName);
 					}
 					else {
 						final Class mappedClass = testQueryable.getMappedClass( EntityMode.POJO );
 						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
 							final boolean assignableSuperclass;
 							if ( testQueryable.isInherited() ) {
 								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass( EntityMode.POJO);
 								assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
 							}
 							else {
 								assignableSuperclass = false;
 							}
 							if ( !assignableSuperclass ) {
 								results.add( testClassName );
 							}
 						}
 					}
 				}
 			}
 		}
 		return (String[]) results.toArray( new String[ results.size() ] );
 	}
 
 	public String getImportedClassName(String className) {
 		String result = (String) imports.get(className);
 		if (result==null) {
 			try {
 				ReflectHelper.classForName(className);
 				return className;
 			}
 			catch (ClassNotFoundException cnfe) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister(className).getPropertyType(propertyName);
 	}
 
 	private JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return serviceRegistry.getService( JdbcServices.class ).getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionfactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			log.trace( "already closed" );
 			return;
 		}
 
 		log.info("closing");
 
 		isClosed = true;
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		if ( settings.isQueryCacheEnabled() )  {
 			queryCache.destroy();
 
 			iter = queryCaches.values().iterator();
 			while ( iter.hasNext() ) {
 				QueryCache cache = (QueryCache) iter.next();
 				cache.destroy();
 			}
 			updateTimestampsCache.destroy();
 		}
 
 		settings.getRegionFactory().stop();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryObjectFactory.removeInstance(uuid, name, properties);
 
 		observer.sessionFactoryClosed( this );
 		eventListeners.destroyListeners();
 	}
 
 	private class CacheImpl implements Cache {
 		public boolean containsEntity(Class entityClass, Serializable identifier) {
 			return containsEntity( entityClass.getName(), identifier );
 		}
 
 		public boolean containsEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( identifier, p ) );
 		}
 
 		public void evictEntity(Class entityClass, Serializable identifier) {
 			evictEntity( entityClass.getName(), identifier );
 		}
 
 		public void evictEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( 
 							"evicting second-level cache: " +
 									MessageHelper.infoString( p, identifier, SessionFactoryImpl.this )
 					);
 				}
 				p.getCacheAccessStrategy().evict( buildCacheKey( identifier, p ) );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable identifier, EntityPersister p) {
 			return new CacheKey(
 					identifier,
 					p.getIdentifierType(),
 					p.getRootEntityName(),
 					EntityMode.POJO,
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictEntityRegion(Class entityClass) {
 			evictEntityRegion( entityClass.getName() );
 		}
 
 		public void evictEntityRegion(String entityName) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getEntityName() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictEntityRegions() {
 			Iterator entityNames = entityPersisters.keySet().iterator();
 			while ( entityNames.hasNext() ) {
 				evictEntityRegion( ( String ) entityNames.next() );
 			}
 		}
 
 		public boolean containsCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( ownerIdentifier, p ) );
 		}
 
 		public void evictCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug(
 							"evicting second-level cache: " +
 									MessageHelper.collectionInfoString(p, ownerIdentifier, SessionFactoryImpl.this)
 					);
 				}
 				CacheKey cacheKey = buildCacheKey( ownerIdentifier, p );
 				p.getCacheAccessStrategy().evict( cacheKey );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable ownerIdentifier, CollectionPersister p) {
 			return new CacheKey(
 					ownerIdentifier,
 					p.getKeyType(),
 					p.getRole(),
 					EntityMode.POJO,
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictCollectionRegion(String role) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getRole() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictCollectionRegions() {
 			Iterator collectionRoles = collectionPersisters.keySet().iterator();
 			while ( collectionRoles.hasNext() ) {
 				evictCollectionRegion( ( String ) collectionRoles.next() );
 			}
 		}
 
 		public boolean containsQuery(String regionName) {
 			return queryCaches.get( regionName ) != null;
 		}
 
 		public void evictDefaultQueryRegion() {
 			if ( settings.isQueryCacheEnabled() ) {
 				queryCache.clear();
 			}
 		}
 
 		public void evictQueryRegion(String regionName) {
 			if ( regionName == null ) {
 				throw new NullPointerException(
 						"Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)"
 				);
 			}
 			else {
 				synchronized ( allCacheRegions ) {
 					if ( settings.isQueryCacheEnabled() ) {
 						QueryCache namedQueryCache = ( QueryCache ) queryCaches.get( regionName );
 						if ( namedQueryCache != null ) {
 							namedQueryCache.clear();
 							// TODO : cleanup entries in queryCaches + allCacheRegions ?
 						}
 					}
 				}
 			}
 		}
 
 		public void evictQueryRegions() {
 			synchronized ( allCacheRegions ) {
 				Iterator regions = queryCaches.values().iterator();
 				while ( regions.hasNext() ) {
 					QueryCache cache = ( QueryCache ) regions.next();
 					cache.clear();
 					// TODO : cleanup entries in queryCaches + allCacheRegions ?
 				}
 			}
 		}
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.clear();
 		}
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return updateTimestampsCache;
 	}
 
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		synchronized ( allCacheRegions ) {
 			QueryCache currentQueryCache = ( QueryCache ) queryCaches.get( regionName );
 			if ( currentQueryCache == null ) {
 				currentQueryCache = settings.getQueryCacheFactory().getQueryCache( regionName, updateTimestampsCache, settings, properties );
 				queryCaches.put( regionName, currentQueryCache );
 				allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 			}
 			return currentQueryCache;
 		}
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		synchronized ( allCacheRegions ) {
 			return ( Region ) allCacheRegions.get( regionName );
 		}
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		synchronized ( allCacheRegions ) {
 			return new HashMap( allCacheRegions );
 		}
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return statistics;
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return (StatisticsImplementor) statistics;
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = ( FilterDefinition ) filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public BatcherFactory getBatcherFactory() {
 		return settings.getBatcherFactory();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return (IdentifierGenerator) identifierGenerators.get(rootEntityName);
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatability
 		if ( impl == null && transactionManager != null ) {
 			impl = "jta";
 		}
 
 		if ( impl == null ) {
 			return null;
 		}
 		else if ( "jta".equals( impl ) ) {
 			if ( settings.getTransactionFactory().areCallbacksLocalToHibernateTransactions() ) {
 				log.warn( "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()" );
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = ReflectHelper.classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( new Object[] { this } );
 			}
 			catch( Throwable t ) {
 				log.error( "Unable to construct current session context [" + impl + "]", t );
 				return null;
 			}
 		}
 	}
 
 	public EventListeners getEventListeners()
 	{
 		return eventListeners;
 	}
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return ( FetchProfile ) fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		String name = null;
 		if ( isNamed ) {
 			name = ois.readUTF();
 		}
 		Object result = SessionFactoryObjectFactory.getInstance( uuid );
 		if ( result == null ) {
 			log.trace( "could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name" );
 			if ( isNamed ) {
 				result = SessionFactoryObjectFactory.getNamedInstance( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java
rename to hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
index 3d347f1e62..d0f151fbf1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
@@ -1,77 +1,79 @@
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
-package org.hibernate.dialect.resolver;
+package org.hibernate.service.jdbc.dialect.internal;
 
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.HibernateException;
 
 /**
  * Intended as support for custom resolvers.
  *
  * @author Steve Ebersole
  */
 public class BasicDialectResolver extends AbstractDialectResolver {
+	// TODO: should this disappear???
+
 	public static final int VERSION_INSENSITIVE_VERSION = -9999;
 
 	private final String matchingName;
 	private final int matchingVersion;
 	private final Class dialectClass;
 
 	public BasicDialectResolver(String matchingName, Class dialectClass) {
 		this( matchingName, VERSION_INSENSITIVE_VERSION, dialectClass );
 	}
 
 	public BasicDialectResolver(String matchingName, int matchingVersion, Class dialectClass) {
 		this.matchingName = matchingName;
 		this.matchingVersion = matchingVersion;
 		this.dialectClass = dialectClass;
 	}
 
 	protected final Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 		final String databaseName = metaData.getDatabaseProductName();
 		final int databaseMajorVersion = metaData.getDatabaseMajorVersion();
 
 		if ( matchingName.equalsIgnoreCase( databaseName )
 				&& ( matchingVersion == VERSION_INSENSITIVE_VERSION || matchingVersion == databaseMajorVersion ) ) {
 			try {
 				return ( Dialect ) dialectClass.newInstance();
 			}
 			catch ( HibernateException e ) {
 				// conceivable that the dialect ctor could throw HibernateExceptions, so don't re-wrap
 				throw e;
 			}
 			catch ( Throwable t ) {
 				throw new HibernateException(
 						"Could not instantiate specified Dialect class [" + dialectClass.getName() + "]",
 						t
 				);
 			}
 		}
 
 		return null;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java b/hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java
index 812be6d674..8c527bff39 100644
--- a/hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java
@@ -1,109 +1,109 @@
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
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.DatabaseMetaData;
 
-import org.hibernate.dialect.resolver.AbstractDialectResolver;
-import org.hibernate.dialect.resolver.BasicDialectResolver;
+import org.hibernate.service.jdbc.dialect.internal.BasicDialectResolver;
 import org.hibernate.HibernateException;
+import org.hibernate.service.jdbc.dialect.internal.AbstractDialectResolver;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class TestingDialects {
 
 	public static class MyDialect1 extends Dialect {
 	}
 
 	public static class MyDialect21 extends Dialect {
 	}
 
 	public static class MyDialect22 extends Dialect {
 	}
 
 	public static class MySpecialDB2Dialect extends Dialect {
 	}
 
 	public static class MyDialectResolver1 extends AbstractDialectResolver {
 		protected Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 			String databaseName = metaData.getDatabaseProductName();
 			int databaseMajorVersion = metaData.getDatabaseMajorVersion();
 			if ( "MyDatabase1".equals( databaseName ) ) {
 				return new MyDialect1();
 			}
 			if ( "MyDatabase2".equals( databaseName ) ) {
 				if ( databaseMajorVersion >= 2 ) {
 					return new MyDialect22();
 				}
 				if ( databaseMajorVersion >= 1 ) {
 					return new MyDialect21();
 				}
 			}
 			return null;
 		}
 	}
 
 	public static class MyDialectResolver2 extends BasicDialectResolver {
 		public MyDialectResolver2() {
 			super( "MyTrickyDatabase1", MyDialect1.class );
 		}
 	}
 
 	public static class ErrorDialectResolver1 extends AbstractDialectResolver {
 		public Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 			String databaseName = metaData.getDatabaseProductName();
 			if ( databaseName.equals( "ConnectionErrorDatabase1" ) ) {
 				throw new SQLException( "Simulated connection error", "08001" );
 			}
 			else {
 				throw new SQLException();
 			}
 		}
 	}
 
 	public static class ErrorDialectResolver2 extends AbstractDialectResolver {
 		public Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 			String databaseName = metaData.getDatabaseProductName();
 			if ( databaseName.equals( "ErrorDatabase1" ) ) {
 				throw new SQLException();
 			}
 			if ( databaseName.equals( "ErrorDatabase2" ) ) {
 				throw new HibernateException( "This is a trap!" );
 			}
 			return null;
 		}
 	}
 
 	public static class MyOverridingDialectResolver1 extends BasicDialectResolver {
 		public MyOverridingDialectResolver1() {
 			super( "DB2/MySpecialPlatform", MySpecialDB2Dialect.class );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
index c4d2547c7f..5a034fce86 100644
--- a/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
@@ -1,188 +1,218 @@
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
 package org.hibernate.dialect.resolver;
 
 import java.util.Properties;
 import java.sql.Connection;
 
 import junit.framework.TestSuite;
 import junit.framework.TestCase;
 import junit.framework.Test;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.DerbyDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.InformixDialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.Oracle10gDialect;
 import org.hibernate.dialect.TestingDialects;
 import org.hibernate.dialect.Mocks;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.cfg.Environment;
+import org.hibernate.service.jdbc.dialect.internal.DialectFactoryImpl;
+import org.hibernate.service.jdbc.dialect.internal.DialectResolverSet;
+import org.hibernate.service.jdbc.dialect.internal.StandardDialectResolver;
+import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
+import org.hibernate.test.common.ServiceRegistryHolder;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class DialectFactoryTest extends TestCase {
+	private ServiceRegistryHolder serviceRegistryHolder;
+
 	public DialectFactoryTest(String name) {
 		super( name );
 	}
 
+	protected void setUp() {
+		serviceRegistryHolder = new ServiceRegistryHolder( Environment.getProperties() );
+	}
+
+	protected void tearDown() {
+		if ( serviceRegistryHolder != null ) {
+			serviceRegistryHolder.destroy();
+		}
+	}
+
 	public static Test suite() {
 		return new TestSuite( DialectFactoryTest.class );
 	}
 
+	// TODO: is it still possible to build a dialect using a class name???
+	/*
 	public void testBuildDialectByClass() {
 		assertEquals(
 				HSQLDialect.class,
 				DialectFactory.constructDialect( "org.hibernate.dialect.HSQLDialect" ).getClass()
 		);
 
 		try {
 			DialectFactory.constructDialect( "org.hibernate.dialect.NoSuchDialect" );
 			fail();
 		}
 		catch ( HibernateException e ) {
 			assertEquals( "unexpected exception type", e.getCause().getClass(), ClassNotFoundException.class );
 		}
 
 		try {
 			DialectFactory.constructDialect( "java.lang.Object" );
 			fail();
 		}
 		catch ( HibernateException e ) {
 			assertEquals( "unexpected exception type", e.getCause().getClass(), ClassCastException.class );
 		}
 	}
+    */
 
 	public void testBuildDialectByProperties() {
 		Properties props = new Properties();
 
 		try {
-			DialectFactory.buildDialect( props, null );
+			getDialectFactoryImpl( new StandardDialectResolver() ).buildDialect( props, null );
 			fail();
 		}
 		catch ( HibernateException e ) {
 			assertNull( e.getCause() );
 		}
 
 		props.setProperty( Environment.DIALECT, "org.hibernate.dialect.HSQLDialect" );
-		assertTrue( DialectFactory.buildDialect( props, null ) instanceof HSQLDialect );
+		assertTrue( getDialectFactoryImpl( new StandardDialectResolver() ).buildDialect( props, null ) instanceof HSQLDialect );
+	}
+
+	private DialectFactoryImpl getDialectFactoryImpl(DialectResolver dialectResolver) {
+		DialectFactoryImpl dialectFactoryImpl = new DialectFactoryImpl();
+		dialectFactoryImpl.setClassLoaderService( serviceRegistryHolder.getClassLoaderService() );
+		dialectFactoryImpl.setDialectResolver( dialectResolver );
+		return dialectFactoryImpl;
 	}
 
 	public void testPreregisteredDialects() {
-		testDetermination( "HSQL Database Engine", HSQLDialect.class );
-		testDetermination( "H2", H2Dialect.class );
-		testDetermination( "MySQL", MySQLDialect.class );
-		testDetermination( "PostgreSQL", PostgreSQLDialect.class );
-		testDetermination( "Apache Derby", DerbyDialect.class );
-		testDetermination( "Ingres", IngresDialect.class );
-		testDetermination( "ingres", IngresDialect.class );
-		testDetermination( "INGRES", IngresDialect.class );
-		testDetermination( "Microsoft SQL Server Database", SQLServerDialect.class );
-		testDetermination( "Microsoft SQL Server", SQLServerDialect.class );
-		testDetermination( "Sybase SQL Server", SybaseASE15Dialect.class );
-		testDetermination( "Adaptive Server Enterprise", SybaseASE15Dialect.class );
-		testDetermination( "Adaptive Server Anywhere", SybaseAnywhereDialect.class );
-		testDetermination( "Informix Dynamic Server", InformixDialect.class );
-		testDetermination( "DB2/NT", DB2Dialect.class );
-		testDetermination( "DB2/LINUX", DB2Dialect.class );
-		testDetermination( "DB2/6000", DB2Dialect.class );
-		testDetermination( "DB2/HPUX", DB2Dialect.class );
-		testDetermination( "DB2/SUN", DB2Dialect.class );
-		testDetermination( "DB2/LINUX390", DB2Dialect.class );
-		testDetermination( "DB2/AIX64", DB2Dialect.class );
-		testDetermination( "Oracle", 8, Oracle8iDialect.class );
-		testDetermination( "Oracle", 9, Oracle9iDialect.class );
-		testDetermination( "Oracle", 10, Oracle10gDialect.class );
-		testDetermination( "Oracle", 11, Oracle10gDialect.class );
+		DialectResolver resolver = new StandardDialectResolver();
+		testDetermination( "HSQL Database Engine", HSQLDialect.class, resolver );
+		testDetermination( "H2", H2Dialect.class, resolver );
+		testDetermination( "MySQL", MySQLDialect.class, resolver );
+		testDetermination( "PostgreSQL", PostgreSQLDialect.class, resolver );
+		testDetermination( "Apache Derby", DerbyDialect.class, resolver );
+		testDetermination( "Ingres", IngresDialect.class, resolver );
+		testDetermination( "ingres", IngresDialect.class, resolver );
+		testDetermination( "INGRES", IngresDialect.class, resolver );
+		testDetermination( "Microsoft SQL Server Database", SQLServerDialect.class, resolver );
+		testDetermination( "Microsoft SQL Server", SQLServerDialect.class, resolver );
+		testDetermination( "Sybase SQL Server", SybaseASE15Dialect.class, resolver );
+		testDetermination( "Adaptive Server Enterprise", SybaseASE15Dialect.class, resolver );
+		testDetermination( "Adaptive Server Anywhere", SybaseAnywhereDialect.class, resolver );
+		testDetermination( "Informix Dynamic Server", InformixDialect.class, resolver );
+		testDetermination( "DB2/NT", DB2Dialect.class, resolver );
+		testDetermination( "DB2/LINUX", DB2Dialect.class, resolver );
+		testDetermination( "DB2/6000", DB2Dialect.class, resolver );
+		testDetermination( "DB2/HPUX", DB2Dialect.class, resolver );
+		testDetermination( "DB2/SUN", DB2Dialect.class, resolver );
+		testDetermination( "DB2/LINUX390", DB2Dialect.class, resolver );
+		testDetermination( "DB2/AIX64", DB2Dialect.class, resolver );
+		testDetermination( "Oracle", 8, Oracle8iDialect.class, resolver );
+		testDetermination( "Oracle", 9, Oracle9iDialect.class, resolver );
+		testDetermination( "Oracle", 10, Oracle10gDialect.class, resolver );
+		testDetermination( "Oracle", 11, Oracle10gDialect.class, resolver );
 	}
 
 	public void testCustomDialects() {
-		DialectFactory.registerDialectResolver( TestingDialects.MyDialectResolver1.class.getName() );
-		DialectFactory.registerDialectResolver( TestingDialects.MyDialectResolver2.class.getName() );
-		DialectFactory.registerDialectResolver( TestingDialects.ErrorDialectResolver1.class.getName() );
-		DialectFactory.registerDialectResolver( TestingDialects.ErrorDialectResolver2.class.getName() );
-		DialectFactory.registerDialectResolver( TestingDialects.MyOverridingDialectResolver1.class.getName() );
-		DialectFactory.registerDialectResolver( "org.hibernate.dialect.NoSuchDialectResolver" );
-		DialectFactory.registerDialectResolver( "java.lang.Object" );
-
-
-		testDetermination( "MyDatabase1", TestingDialects.MyDialect1.class );
-		testDetermination( "MyDatabase2", 1, TestingDialects.MyDialect21.class );
-		testDetermination( "MyTrickyDatabase1", TestingDialects.MyDialect1.class );
+		DialectResolverSet resolvers = new DialectResolverSet();
+		resolvers.addResolver( new TestingDialects.MyDialectResolver1() );
+		resolvers.addResolver( new TestingDialects.MyDialectResolver2() );
+		resolvers.addResolver( new TestingDialects.ErrorDialectResolver1() );
+		resolvers.addResolver( new TestingDialects.ErrorDialectResolver2() );
+		resolvers.addResolver( new TestingDialects.MyOverridingDialectResolver1() );
+		//DialectFactory.registerDialectResolver( "org.hibernate.dialect.NoSuchDialectResolver" );
+		//DialectFactory.registerDialectResolver( "java.lang.Object" );
+
+		testDetermination( "MyDatabase1", TestingDialects.MyDialect1.class, resolvers );
+		testDetermination( "MyDatabase2", 1, TestingDialects.MyDialect21.class, resolvers );
+		testDetermination( "MyTrickyDatabase1", TestingDialects.MyDialect1.class, resolvers );
 
 		// This should be mapped to DB2Dialect by default, but actually it will be
 		// my custom dialect because I have registered MyOverridingDialectResolver1.
-		testDetermination( "DB2/MySpecialPlatform", TestingDialects.MySpecialDB2Dialect.class );
+		testDetermination( "DB2/MySpecialPlatform", TestingDialects.MySpecialDB2Dialect.class, resolvers );
 
 		try {
-			testDetermination( "ErrorDatabase1", Void.TYPE );
+			testDetermination( "ErrorDatabase1", Void.TYPE, resolvers );
 			fail();
 		}
 		catch ( HibernateException e ) {
 //			log.info( "Expected SQL error in resolveDialect and ignored", e );
 		}
 
 		try {
-			testDetermination( "ErrorDatabase2", Void.TYPE );
+			testDetermination( "ErrorDatabase2", Void.TYPE, resolvers );
 			fail();
 		}
 		catch ( HibernateException e ) {
 //			log.info( "Expected runtime error in resolveDialect", e );
 		}
 	}
 
 	public void testDialectNotFound() {
 		Properties properties = new Properties();
 		try {
-			DialectFactory.buildDialect( properties, Mocks.createConnection( "NoSuchDatabase", 666 ) );
+			getDialectFactoryImpl( new StandardDialectResolver() ).buildDialect( properties, Mocks.createConnection( "NoSuchDatabase", 666 ) );
 			fail();
 		}
 		catch ( HibernateException e ) {
 			assertNull( e.getCause() );
 		}
 	}
 
-	private void testDetermination(String databaseName, Class clazz) {
-		testDetermination( databaseName, -9999, clazz );
+	private void testDetermination(String databaseName, Class clazz, DialectResolver resolver) {
+		testDetermination( databaseName, -9999, clazz, resolver );
 	}
 
-	private void testDetermination(String databaseName, int databaseMajorVersion, Class clazz) {
+	private void testDetermination(String databaseName, int databaseMajorVersion, Class clazz, DialectResolver resolver) {
+		DialectFactoryImpl dialectFactoryImpl = getDialectFactoryImpl( new StandardDialectResolver() );
+		dialectFactoryImpl.setDialectResolver( resolver );
 		Properties properties = new Properties();
 		Connection conn = Mocks.createConnection( databaseName, databaseMajorVersion );
-		assertEquals( clazz, DialectFactory.buildDialect( properties, conn ).getClass() );
+		assertEquals( clazz, dialectFactoryImpl.buildDialect( properties, conn ).getClass() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
index d0962bd1f4..37066e3dac 100644
--- a/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
@@ -1,121 +1,124 @@
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
 package org.hibernate.dialect.resolver;
 
 import java.sql.SQLException;
 
 import junit.framework.TestSuite;
 import junit.framework.Test;
 import junit.framework.TestCase;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.TestingDialects;
 import org.hibernate.dialect.Mocks;
 import org.hibernate.exception.JDBCConnectionException;
+import org.hibernate.service.jdbc.dialect.internal.BasicDialectResolver;
+import org.hibernate.service.jdbc.dialect.internal.DialectResolverSet;
+import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class DialectResolverTest extends TestCase {
 
 	public DialectResolverTest(String name) {
 		super( name );
 	}
 
 	public void testDialects() throws Exception {
 		DialectResolverSet resolvers = new DialectResolverSet();
 
 		resolvers.addResolverAtFirst( new TestingDialects.MyDialectResolver1() );
 		resolvers.addResolverAtFirst( new TestingDialects.MyDialectResolver2() );
 
 		testDetermination( resolvers, "MyDatabase1", 1, TestingDialects.MyDialect1.class );
 		testDetermination( resolvers, "MyDatabase1", 2, TestingDialects.MyDialect1.class );
 		testDetermination( resolvers, "MyDatabase2", 0, null );
 		testDetermination( resolvers, "MyDatabase2", 1, TestingDialects.MyDialect21.class );
 		testDetermination( resolvers, "MyDatabase2", 2, TestingDialects.MyDialect22.class );
 		testDetermination( resolvers, "MyDatabase2", 3, TestingDialects.MyDialect22.class );
 		testDetermination( resolvers, "MyDatabase3", 1, null );
 		testDetermination( resolvers, "MyTrickyDatabase1", 1, TestingDialects.MyDialect1.class );
 	}
 
 	public void testErrorAndOrder() throws Exception {
 		DialectResolverSet resolvers = new DialectResolverSet();
 		resolvers.addResolverAtFirst( new TestingDialects.MyDialectResolver1() );
 		resolvers.addResolver( new TestingDialects.ErrorDialectResolver1() );
 		resolvers.addResolverAtFirst( new TestingDialects.ErrorDialectResolver1() );
 		resolvers.addResolver( new TestingDialects.MyDialectResolver2() );
 
 		// Non-connection errors are suppressed.
 		testDetermination( resolvers, "MyDatabase1", 1, TestingDialects.MyDialect1.class );
 		testDetermination( resolvers, "MyTrickyDatabase1", 1, TestingDialects.MyDialect1.class );
 		testDetermination( resolvers, "NoSuchDatabase", 1, null );
 
 		// Connection errors are reported
 		try {
 			testDetermination( resolvers, "ConnectionErrorDatabase1", 1, null );
 			fail();
 		}
 		catch ( JDBCConnectionException e ) {
 			// expected
 		}
 	}
 
 	public void testBasicDialectResolver() throws Exception {
 		DialectResolverSet resolvers = new DialectResolverSet();
 		// Simulating MyDialectResolver1 by BasicDialectResolvers
 		resolvers.addResolver( new BasicDialectResolver( "MyDatabase1", TestingDialects.MyDialect1.class ) );
 		resolvers.addResolver( new BasicDialectResolver( "MyDatabase2", 1, TestingDialects.MyDialect21.class ) );
 		resolvers.addResolver( new BasicDialectResolver( "MyDatabase2", 2, TestingDialects.MyDialect22.class ) );
 		resolvers.addResolver( new BasicDialectResolver( "ErrorDatabase1", Object.class ) );
 		testDetermination( resolvers, "MyDatabase1", 1, TestingDialects.MyDialect1.class );
 
 		testDetermination( resolvers, "MyDatabase1", 2, TestingDialects.MyDialect1.class );
 		testDetermination( resolvers, "MyDatabase2", 0, null );
 		testDetermination( resolvers, "MyDatabase2", 1, TestingDialects.MyDialect21.class );
 		testDetermination( resolvers, "MyDatabase2", 2, TestingDialects.MyDialect22.class );
 		testDetermination( resolvers, "ErrorDatabase1", 0, null );
 	}
 
 
 	private void testDetermination(
 			DialectResolver resolver,
 			String databaseName,
 			int version,
 			Class dialectClass) throws SQLException {
 		Dialect dialect = resolver.resolveDialect( Mocks.createConnection( databaseName, version ).getMetaData() );
 		if ( dialectClass == null ) {
 			assertEquals( null, dialect );
 		}
 		else {
 			assertEquals( dialectClass, dialect.getClass() );
 		}
 	}
 
 	public static Test suite() {
 		return new TestSuite( DialectResolverTest.class );
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
index d029b99e1a..2ce1305f9f 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
@@ -1,117 +1,117 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.TransactionalDataRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Base class for tests of EntityRegion and CollectionRegion implementations.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityCollectionRegionTestCase extends AbstractRegionImplTestCase {
 
    /**
     * Create a new EntityCollectionRegionTestCaseBase.
     * 
     * @param name
     */
    public AbstractEntityCollectionRegionTestCase(String name) {
       super(name);
    }
 
    /**
     * Creates a Region backed by an PESSIMISTIC locking JBoss Cache, and then ensures that it
     * handles calls to buildAccessStrategy as expected when all the various {@link AccessType}s are
     * passed as arguments.
     */
    public void testSupportedAccessTypes() throws Exception {
       supportedAccessTypeTest();
    }
 
    private void supportedAccessTypeTest() throws Exception {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
       String entityCfg = "entity";
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, entityCfg);
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       supportedAccessTypeTest(regionFactory, cfg.getProperties());
    }
 
    /**
     * Creates a Region using the given factory, and then ensure that it handles calls to
     * buildAccessStrategy as expected when all the various {@link AccessType}s are passed as
     * arguments.
     */
    protected abstract void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties);
 
    /**
     * Test that the Region properly implements {@link TransactionalDataRegion#isTransactionAware()}.
     * 
     * @throws Exception
     */
    public void testIsTransactionAware() throws Exception {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
       assertTrue("Region is transaction-aware", region.isTransactionAware());
       CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
       cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
       // Make it non-transactional
       cfg.getProperties().remove(Environment.TRANSACTION_MANAGER_STRATEGY);
       regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
       assertFalse("Region is not transaction-aware", region.isTransactionAware());
       CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
    }
 
    public void testGetCacheDataDescription() throws Exception {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
       CacheDataDescription cdd = region.getCacheDataDescription();
       assertNotNull(cdd);
       CacheDataDescription expected = getCacheDataDescription();
       assertEquals(expected.isMutable(), cdd.isMutable());
       assertEquals(expected.isVersioned(), cdd.isVersioned());
       assertEquals(expected.getVersionComparator(), cdd.getVersionComparator());
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index 1cb2e9661f..7f959ab380 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,202 +1,202 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Set;
 
 import org.hibernate.cache.GeneralDataRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
    protected static final String KEY = "Key";
 
    protected static final String VALUE1 = "value1";
    protected static final String VALUE2 = "value2";
 
    public AbstractGeneralDataRegionTestCase(String name) {
       super(name);
    }
 
    @Override
    protected void putInRegion(Region region, Object key, Object value) {
       ((GeneralDataRegion) region).put(key, value);
    }
 
    @Override
    protected void removeFromRegion(Region region, Object key) {
       ((GeneralDataRegion) region).evict(key);
    }
 
    /**
     * Test method for {@link QueryResultsRegion#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvict() throws Exception {
       evictOrRemoveTest();
    }
 
    private void evictOrRemoveTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       CacheAdapter localCache = getInfinispanCache(regionFactory);
       boolean invalidation = localCache.isClusteredInvalidation();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       cfg = createConfiguration();
       regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
 
       GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       assertNull("local is clean", localRegion.get(KEY));
       assertNull("remote is clean", remoteRegion.get(KEY));
 
       localRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, localRegion.get(KEY));
 
       // allow async propagation
       sleep(250);
       Object expected = invalidation ? null : VALUE1;
       assertEquals(expected, remoteRegion.get(KEY));
 
       localRegion.evict(KEY);
 
       // allow async propagation
       sleep(250);
       assertEquals(null, localRegion.get(KEY));
       assertEquals(null, remoteRegion.get(KEY));
    }
 
    protected abstract String getStandardRegionName(String regionPrefix);
 
    /**
     * Test method for {@link QueryResultsRegion#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvictAll() throws Exception {
       evictOrRemoveAllTest("entity");
    }
 
    private void evictOrRemoveAllTest(String configName) throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       CacheAdapter localCache = getInfinispanCache(regionFactory);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       cfg = createConfiguration();
       regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       CacheAdapter remoteCache = getInfinispanCache(regionFactory);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       Set keys = localCache.keySet();
       assertEquals("No valid children in " + keys, 0, getValidKeyCount(keys));
 
       keys = remoteCache.keySet();
       assertEquals("No valid children in " + keys, 0, getValidKeyCount(keys));
 
       assertNull("local is clean", localRegion.get(KEY));
       assertNull("remote is clean", remoteRegion.get(KEY));
 
       localRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, localRegion.get(KEY));
 
       // Allow async propagation
       sleep(250);
 
       remoteRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, remoteRegion.get(KEY));
 
       // Allow async propagation
       sleep(250);
 
       localRegion.evictAll();
 
       // allow async propagation
       sleep(250);
       // This should re-establish the region root node in the optimistic case
       assertNull(localRegion.get(KEY));
       assertEquals("No valid children in " + keys, 0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       // This only adds a node in the case of optimistic locking
       assertEquals(null, remoteRegion.get(KEY));
       assertEquals("No valid children in " + keys, 0, getValidKeyCount(remoteCache.keySet()));
 
       assertEquals("local is clean", null, localRegion.get(KEY));
       assertEquals("remote is clean", null, remoteRegion.get(KEY));
    }
 
    protected Configuration createConfiguration() {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, false, true);
       return cfg;
    }
 
    protected void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
    }
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
index 5b6c30ca7b..fbcb986521 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
@@ -1,589 +1,589 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.collection;
 
 import java.util.concurrent.BrokenBarrierException;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.CyclicBarrier;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.Lock;
 
 import junit.extensions.TestSetup;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestSuite;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.access.PutFromLoadValidator;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.common.ServiceRegistryHolder;
 import org.hibernate.util.ComparableComparator;
 import org.infinispan.Cache;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 import javax.transaction.TransactionManager;
 
 /**
  * Base class for tests of CollectionRegionAccessStrategy impls.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractCollectionRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY_BASE = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
 
    protected static int testCount;
 
    protected static Configuration localCfg;
    protected static InfinispanRegionFactory localRegionFactory;
    protected CacheAdapter localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
    protected CacheAdapter remoteCache;
 
    protected CollectionRegion localCollectionRegion;
    protected CollectionRegionAccessStrategy localAccessStrategy;
 
    protected CollectionRegion remoteCollectionRegion;
    protected CollectionRegionAccessStrategy remoteAccessStrategy;
 
    protected boolean invalidation;
    protected boolean synchronous;
 
    protected Exception node1Exception;
    protected Exception node2Exception;
 
    protected AssertionFailedError node1Failure;
    protected AssertionFailedError node2Failure;
 
    public static Test getTestSetup(Class testClass, String configName) {
       TestSuite suite = new TestSuite(testClass);
       return new AccessStrategyTestSetup(suite, configName);
    }
 
    public static Test getTestSetup(Test test, String configName) {
       return new AccessStrategyTestSetup(test, configName);
    }
 
    /**
     * Create a new TransactionalAccessTestCase.
     * 
     * @param name
     */
    public AbstractCollectionRegionAccessStrategyTestCase(String name) {
       super(name);
    }
 
    protected abstract AccessType getAccessType();
 
    protected void setUp() throws Exception {
       super.setUp();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       localCollectionRegion = localRegionFactory.buildCollectionRegion(REGION_NAME, localCfg.getProperties(),
                getCacheDataDescription());
       localCache = ((BaseRegion) localCollectionRegion).getCacheAdapter();
       localAccessStrategy = localCollectionRegion.buildAccessStrategy(getAccessType());
       invalidation = localCache.isClusteredInvalidation();
       synchronous = localCache.isSynchronous();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteCollectionRegion = remoteRegionFactory.buildCollectionRegion(REGION_NAME, remoteCfg.getProperties(),
                getCacheDataDescription());
       remoteCache = ((BaseRegion) remoteCollectionRegion).getCacheAdapter();
       remoteAccessStrategy = remoteCollectionRegion.buildAccessStrategy(getAccessType());
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected void tearDown() throws Exception {
 
       super.tearDown();
 
       try {
          localCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
          remoteCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging remote cache", e);
       }
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected static Configuration createConfiguration(String configName, String configResource) {
       Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName);
       return cfg;
    }
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
    protected boolean isUsingInvalidation() {
       return invalidation;
    }
 
    protected boolean isSynchronous() {
       return synchronous;
    }
 
    /**
     * This is just a setup test where we assert that the cache config is as we expected.
     */
    public abstract void testCacheConfiguration();
 
    /**
     * Test method for {@link CollectionRegionAccessStrategy#getRegion()}.
     */
    public void testGetRegion() {
       assertEquals("Correct region", localCollectionRegion, localAccessStrategy.getRegion());
    }
 
    public void testPutFromLoadRemoveDoesNotProduceStaleData() throws Exception {
       final CountDownLatch pferLatch = new CountDownLatch(1);
       final CountDownLatch removeLatch = new CountDownLatch(1);
       TransactionManager tm = DualNodeJtaTransactionManagerImpl.getInstance("test1234");
       PutFromLoadValidator validator = new PutFromLoadValidator(tm) {
          @Override
          public boolean acquirePutFromLoadLock(Object key) {
             boolean acquired = super.acquirePutFromLoadLock(key);
             try {
                removeLatch.countDown();
                pferLatch.await(2, TimeUnit.SECONDS);
             } catch (InterruptedException e) {
                log.debug("Interrupted");
                Thread.currentThread().interrupt();
             } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException("Error", e);
             }
             return acquired;
          }
       };
       final TransactionalAccessDelegate delegate = new TransactionalAccessDelegate((CollectionRegionImpl) localCollectionRegion, validator);
 
       Callable<Void> pferCallable = new Callable<Void>() {
          public Void call() throws Exception {
             delegate.putFromLoad("k1", "v1", 0, null);
             return null;
          }
       };
 
       Callable<Void> removeCallable = new Callable<Void>() {
          public Void call() throws Exception {
             removeLatch.await();
             delegate.remove("k1");
             pferLatch.countDown();
             return null;
          }
       };
 
       ExecutorService executorService = Executors.newCachedThreadPool();
       Future<Void> pferFuture = executorService.submit(pferCallable);
       Future<Void> removeFuture = executorService.submit(removeCallable);
 
       pferFuture.get();
       removeFuture.get();
 
       assertFalse(localCache.containsKey("k1"));
    }
 
    /**
     * Test method for
     * {@link CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)}
     * .
     */
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    /**
     * Test method for
     * {@link CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)}
     * .
     */
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    /**
     * Simulate 2 nodes, both start, tx do a get, experience a cache miss, then 'read from db.' First
     * does a putFromLoad, then an evict (to represent a change). Second tries to do a putFromLoad
     * with stale data (i.e. it took longer to read from the db). Both commit their tx. Then both
     * start a new tx and get. First should see the updated data; second should either see the
     * updated data (isInvalidation()( == false) or null (isInvalidation() == true).
     * 
     * @param useMinimalAPI
     * @throws Exception
     */
    private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch writeLatch1 = new CountDownLatch(1);
       final CountDownLatch writeLatch2 = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread node1 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertEquals("node1 starts clean", null, localAccessStrategy.get(KEY, txTimestamp));
 
                writeLatch1.await();
 
                if (useMinimalAPI) {
                   localAccessStrategy.putFromLoad(KEY, VALUE2, txTimestamp, new Integer(2), true);
                } else {
                   localAccessStrategy.putFromLoad(KEY, VALUE2, txTimestamp, new Integer(2));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                // Let node2 write
                writeLatch2.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       Thread node2 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node2 starts clean", remoteAccessStrategy.get(KEY, txTimestamp));
 
                // Let node1 write
                writeLatch1.countDown();
                // Wait for node1 to finish
                writeLatch2.await();
 
                // Let the first PFER propagate
                sleep(200);
 
                if (useMinimalAPI) {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node2 caught exception", e);
                node2Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node2Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       node1.setDaemon(true);
       node2.setDaemon(true);
 
       node1.start();
       node2.start();
 
       assertTrue("Threads completed", completionLatch.await(2, TimeUnit.SECONDS));
 
       if (node1Failure != null)
          throw node1Failure;
       if (node2Failure != null)
          throw node2Failure;
 
       assertEquals("node1 saw no exceptions", null, node1Exception);
       assertEquals("node2 saw no exceptions", null, node2Exception);
 
       // let the final PFER propagate
       sleep(100);
 
       long txTimestamp = System.currentTimeMillis();
       String msg1 = "Correct node1 value";
       String msg2 = "Correct node2 value";
       Object expected1 = null;
       Object expected2 = null;
       if (isUsingInvalidation()) {
          // PFER does not generate any invalidation, so each node should
          // succeed. We count on database locking and Hibernate removing
          // the collection on any update to prevent the situation we have
          // here where the caches have inconsistent data
          expected1 = VALUE2;
          expected2 = VALUE1;
       } else {
          // the initial VALUE2 should prevent the node2 put
          expected1 = VALUE2;
          expected2 = VALUE2;
       }
 
       assertEquals(msg1, expected1, localAccessStrategy.get(KEY, txTimestamp));
       assertEquals(msg2, expected2, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for {@link CollectionRegionAccessStrategy#remove(java.lang.Object)}.
     */
    public void testRemove() {
       evictOrRemoveTest(false);
    }
 
    /**
     * Test method for {@link CollectionRegionAccessStrategy#removeAll()}.
     */
    public void testRemoveAll() {
       evictOrRemoveAllTest(false);
    }
 
    /**
     * Test method for {@link CollectionRegionAccessStrategy#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvict() {
       evictOrRemoveTest(true);
    }
 
    /**
     * Test method for {@link CollectionRegionAccessStrategy#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvictAll() {
       evictOrRemoveAllTest(true);
    }
 
    private void evictOrRemoveTest(boolean evict) {
 
       final String KEY = KEY_BASE + testCount++;
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       if (evict)
          localAccessStrategy.evict(KEY);
       else
          localAccessStrategy.remove(KEY);
 
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
 
       final String KEY = KEY_BASE + testCount++;
 
       assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       if (evict)
          localAccessStrategy.evictAll();
       else
          localAccessStrategy.removeAll();
 
       // This should re-establish the region root node
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       assertEquals(1, getValidKeyCount(remoteCache.keySet()));
 
       // Wait for async propagation of the putFromLoad
       sleep(250);
 
       assertEquals("local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy.get(KEY, System
                .currentTimeMillis()));
       assertEquals("remote is correct", VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    private void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
 
    }
 
    private static class AccessStrategyTestSetup extends TestSetup {
 
       private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
 
       private final String configResource;
       private final String configName;
       private String preferIPv4Stack;
       private ServiceRegistryHolder serviceRegistryHolder;
 
       public AccessStrategyTestSetup(Test test, String configName) {
          this(test, configName, null);
       }
 
       public AccessStrategyTestSetup(Test test, String configName, String configResource) {
          super(test);
          this.configName = configName;
          this.configResource = configResource;
       }
 
       @Override
       protected void setUp() throws Exception {
          super.setUp();
 
          // Try to ensure we use IPv4; otherwise cluster formation is very slow
          preferIPv4Stack = System.getProperty(PREFER_IPV4STACK);
          System.setProperty(PREFER_IPV4STACK, "true");
 
          serviceRegistryHolder = new ServiceRegistryHolder( Environment.getProperties() );
 
          localCfg = createConfiguration(configName, configResource);
          localRegionFactory = CacheTestUtil.startRegionFactory(
-				 serviceRegistryHolder.getJdbcServicesImpl().getConnectionProvider(),
+				 serviceRegistryHolder.getJdbcServicesImpl(),
 				 localCfg
 		 );
 
          remoteCfg = createConfiguration(configName, configResource);
          remoteRegionFactory = CacheTestUtil.startRegionFactory(
-				 serviceRegistryHolder.getJdbcServicesImpl().getConnectionProvider(),
+				 serviceRegistryHolder.getJdbcServicesImpl(),
 				 remoteCfg
 		 );
       }
 
       @Override
       protected void tearDown() throws Exception {
          try {
             super.tearDown();
          } finally {
             if (preferIPv4Stack == null)
                System.clearProperty(PREFER_IPV4STACK);
             else
                System.setProperty(PREFER_IPV4STACK, preferIPv4Stack);
          }
 
 		  try {
             if (localRegionFactory != null)
                localRegionFactory.stop();
 
             if (remoteRegionFactory != null)
                remoteRegionFactory.stop();
 		  }
 		  finally {
             if ( serviceRegistryHolder != null ) {
                serviceRegistryHolder.destroy(); 
             }
 		  }
       }
 
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
index 755de34c89..70ce3a30c0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
@@ -1,127 +1,127 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.collection;
 
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * TransactionalExtraAPITestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TransactionalExtraAPITestCase extends AbstractNonFunctionalTestCase {
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
    
    private static CollectionRegionAccessStrategy localAccessStrategy;
    
    public TransactionalExtraAPITestCase(String name) {
       super(name);
    }
 
    protected void setUp() throws Exception {
        super.setUp();
        
        if (getCollectionAccessStrategy() == null) {
            Configuration cfg = createConfiguration();
            InfinispanRegionFactory rf  = CacheTestUtil.startRegionFactory(
-				   getConnectionProvider(), cfg, getCacheTestSupport()
+				   getJdbcServices(), cfg, getCacheTestSupport()
 		   );
            
            // Sleep a bit to avoid concurrent FLUSH problem
            avoidConcurrentFlush();
            
            CollectionRegion localCollectionRegion = rf.buildCollectionRegion(REGION_NAME, cfg.getProperties(), null);
            setCollectionAccessStrategy(localCollectionRegion.buildAccessStrategy(getAccessType()));
        }
    }
 
    protected void tearDown() throws Exception {
        
        super.tearDown();
    }
    
    protected Configuration createConfiguration() {
        Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
        cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, getCacheConfigName());
        return cfg;
    }
    
    protected String getCacheConfigName() {
        return "entity";
    }
    
    protected AccessType getAccessType() {
        return AccessType.TRANSACTIONAL;
    }
    
    protected CollectionRegionAccessStrategy getCollectionAccessStrategy() {
        return localAccessStrategy;
    }
    
    protected void setCollectionAccessStrategy(CollectionRegionAccessStrategy strategy) {
        localAccessStrategy = strategy;
    }
 
    /**
     * Test method for {@link TransactionalAccess#lockItem(java.lang.Object, java.lang.Object)}.
     */
    public void testLockItem() {
        assertNull(getCollectionAccessStrategy().lockItem(KEY, new Integer(1)));
    }
 
    /**
     * Test method for {@link TransactionalAccess#lockRegion()}.
     */
    public void testLockRegion() {
        assertNull(getCollectionAccessStrategy().lockRegion());
    }
 
    /**
     * Test method for {@link TransactionalAccess#unlockItem(java.lang.Object, org.hibernate.cache.access.SoftLock)}.
     */
    public void testUnlockItem() {
        getCollectionAccessStrategy().unlockItem(KEY, new MockSoftLock());
    }
 
    /**
     * Test method for {@link TransactionalAccess#unlockRegion(org.hibernate.cache.access.SoftLock)}.
     */
    public void testUnlockRegion() {
        getCollectionAccessStrategy().unlockItem(KEY, new MockSoftLock());
    }
    
    public static class MockSoftLock implements SoftLock {
        
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
index be99245d70..06320be78a 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
@@ -1,699 +1,699 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.entity;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.extensions.TestSetup;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestSuite;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.common.ServiceRegistryHolder;
 import org.hibernate.util.ComparableComparator;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 /**
  * Base class for tests of EntityRegionAccessStrategy impls.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY_BASE = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
 
    protected static int testCount;
 
    protected static Configuration localCfg;
    protected static InfinispanRegionFactory localRegionFactory;
    protected CacheAdapter localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
    protected CacheAdapter remoteCache;
 
    protected boolean invalidation;
    protected boolean synchronous;
 
    protected EntityRegion localEntityRegion;
    protected EntityRegionAccessStrategy localAccessStrategy;
 
    protected EntityRegion remoteEntityRegion;
    protected EntityRegionAccessStrategy remoteAccessStrategy;
 
    protected Exception node1Exception;
    protected Exception node2Exception;
 
    protected AssertionFailedError node1Failure;
    protected AssertionFailedError node2Failure;
 
    public static Test getTestSetup(Class testClass, String configName) {
       TestSuite suite = new TestSuite(testClass);
       return new AccessStrategyTestSetup(suite, configName);
    }
 
    public static Test getTestSetup(Test test, String configName) {
       return new AccessStrategyTestSetup(test, configName);
    }
 
    /**
     * Create a new TransactionalAccessTestCase.
     * 
     * @param name
     */
    public AbstractEntityRegionAccessStrategyTestCase(String name) {
       super(name);
    }
 
    protected abstract AccessType getAccessType();
 
    protected void setUp() throws Exception {
       super.setUp();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       localEntityRegion = localRegionFactory.buildEntityRegion(REGION_NAME, localCfg
                .getProperties(), getCacheDataDescription());
       localAccessStrategy = localEntityRegion.buildAccessStrategy(getAccessType());
 
       localCache = ((BaseRegion) localEntityRegion).getCacheAdapter();
 
       invalidation = localCache.isClusteredInvalidation();
       synchronous = localCache.isSynchronous();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteEntityRegion = remoteRegionFactory.buildEntityRegion(REGION_NAME, remoteCfg
                .getProperties(), getCacheDataDescription());
       remoteAccessStrategy = remoteEntityRegion.buildAccessStrategy(getAccessType());
 
       remoteCache = ((BaseRegion) remoteEntityRegion).getCacheAdapter();
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected void tearDown() throws Exception {
 
       super.tearDown();
 
       try {
          localCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
          remoteCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging remote cache", e);
       }
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected static Configuration createConfiguration(String configName) {
       Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName);
       return cfg;
    }
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
    protected boolean isUsingInvalidation() {
       return invalidation;
    }
 
    protected boolean isSynchronous() {
       return synchronous;
    }
 
    protected void assertThreadsRanCleanly() {
       if (node1Failure != null)
          throw node1Failure;
       if (node2Failure != null)
          throw node2Failure;
 
       if (node1Exception != null) {
          log.error("node1 saw an exception", node1Exception);
          assertEquals("node1 saw no exceptions", null, node1Exception);
       }
 
       if (node2Exception != null) {
          log.error("node2 saw an exception", node2Exception);
          assertEquals("node2 saw no exceptions", null, node2Exception);
       }
    }
 
    /**
     * This is just a setup test where we assert that the cache config is as we expected.
     */
    public abstract void testCacheConfiguration();
 
    /**
     * Test method for {@link TransactionalAccess#getRegion()}.
     */
    public void testGetRegion() {
       assertEquals("Correct region", localEntityRegion, localAccessStrategy.getRegion());
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)}
     * .
     */
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)}
     * .
     */
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    /**
     * Simulate 2 nodes, both start, tx do a get, experience a cache miss, then 'read from db.' First
     * does a putFromLoad, then an update. Second tries to do a putFromLoad with stale data (i.e. it
     * took longer to read from the db). Both commit their tx. Then both start a new tx and get.
     * First should see the updated data; second should either see the updated data (isInvalidation()
     * == false) or null (isInvalidation() == true).
     * 
     * @param useMinimalAPI
     * @throws Exception
     */
    private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch writeLatch1 = new CountDownLatch(1);
       final CountDownLatch writeLatch2 = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread node1 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node1 starts clean", localAccessStrategy.get(KEY, txTimestamp));
 
                writeLatch1.await();
 
                if (useMinimalAPI) {
                   localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                localAccessStrategy.update(KEY, VALUE2, new Integer(2), new Integer(1));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                // Let node2 write
                writeLatch2.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       Thread node2 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node1 starts clean", remoteAccessStrategy.get(KEY, txTimestamp));
 
                // Let node1 write
                writeLatch1.countDown();
                // Wait for node1 to finish
                writeLatch2.await();
 
                if (useMinimalAPI) {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node2 caught exception", e);
                node2Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node2Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       node1.setDaemon(true);
       node2.setDaemon(true);
 
       node1.start();
       node2.start();
 
       assertTrue("Threads completed", completionLatch.await(2, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE2, localAccessStrategy.get(KEY, txTimestamp));
 
       if (isUsingInvalidation()) {
          // no data version to prevent the PFER; we count on db locks preventing this
          assertEquals("Expected node2 value", VALUE1, remoteAccessStrategy.get(KEY, txTimestamp));
       } else {
          // The node1 update is replicated, preventing the node2 PFER
          assertEquals("Correct node2 value", VALUE2, remoteAccessStrategy.get(KEY, txTimestamp));
       }
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#insert(java.lang.Object, java.lang.Object, java.lang.Object)}.
     */
    public void testInsert() throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread inserter = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("Correct initial value", localAccessStrategy.get(KEY, txTimestamp));
 
                localAccessStrategy.insert(KEY, VALUE1, new Integer(1));
 
                readLatch.countDown();
                commitLatch.await();
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       Thread reader = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                readLatch.await();
 //               Object expected = !isBlockingReads() ? null : VALUE1;
                Object expected = null;
 
                assertEquals("Correct initial value", expected, localAccessStrategy.get(KEY,
                         txTimestamp));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                commitLatch.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       inserter.setDaemon(true);
       reader.setDaemon(true);
       inserter.start();
       reader.start();
 
       assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE1, localAccessStrategy.get(KEY, txTimestamp));
       Object expected = isUsingInvalidation() ? null : VALUE1;
       assertEquals("Correct node2 value", expected, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#update(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)}
     * .
     */
    public void testUpdate() throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       // Set up initial state
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
 
       // Let the async put propagate
       sleep(250);
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread updater = new Thread("testUpdate-updater") {
 
          public void run() {
             boolean readerUnlocked = false;
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, get initial value");
                assertEquals("Correct initial value", VALUE1, localAccessStrategy.get(KEY, txTimestamp));
                log.debug("Now update value");
                localAccessStrategy.update(KEY, VALUE2, new Integer(2), new Integer(1));
                log.debug("Notify the read latch");
                readLatch.countDown();
                readerUnlocked = true;
                log.debug("Await commit");
                commitLatch.await();
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                if (!readerUnlocked) readLatch.countDown();
                log.debug("Completion latch countdown");
                completionLatch.countDown();
             }
          }
       };
 
       Thread reader = new Thread("testUpdate-reader") {
 
          public void run() {
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, await read latch");
                readLatch.await();
                log.debug("Read latch acquired, verify local access strategy");
 
                // This won't block w/ mvc and will read the old value
                Object expected = VALUE1;
                assertEquals("Correct value", expected, localAccessStrategy.get(KEY, txTimestamp));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                commitLatch.countDown();
                log.debug("Completion latch countdown");
                completionLatch.countDown();
             }
          }
       };
 
       updater.setDaemon(true);
       reader.setDaemon(true);
       updater.start();
       reader.start();
 
       // Should complete promptly
       assertTrue(completionLatch.await(2, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE2, localAccessStrategy.get(KEY, txTimestamp));
       Object expected = isUsingInvalidation() ? null : VALUE2;
       assertEquals("Correct node2 value", expected, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for {@link TransactionalAccess#remove(java.lang.Object)}.
     */
    public void testRemove() {
       evictOrRemoveTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#removeAll()}.
     */
    public void testRemoveAll() {
       evictOrRemoveAllTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * EntityRegionAccessStrategy API.
     */
    public void testEvict() {
       evictOrRemoveTest(true);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * EntityRegionAccessStrategy API.
     */
    public void testEvictAll() {
       evictOrRemoveAllTest(true);
    }
 
    private void evictOrRemoveTest(boolean evict) {
       final String KEY = KEY_BASE + testCount++;
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       if (evict)
          localAccessStrategy.evict(KEY);
       else
          localAccessStrategy.remove(KEY);
 
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
       final String KEY = KEY_BASE + testCount++;
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       if (evict) {
          log.debug("Call evict all locally");
          localAccessStrategy.evictAll();
       } else {
          localAccessStrategy.removeAll();
       }
 
       // This should re-establish the region root node in the optimistic case
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(1, getValidKeyCount(remoteCache.keySet()));
 
       // Wait for async propagation
       sleep(250);
 
       assertEquals("local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy
                .get(KEY, System.currentTimeMillis()));
       assertEquals("remote is correct", VALUE1, remoteAccessStrategy.get(KEY, System
                .currentTimeMillis()));
    }
 
    protected void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
    }
 
    private static class AccessStrategyTestSetup extends TestSetup {
 
       private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
       private final String configName;
       private String preferIPv4Stack;
       private ServiceRegistryHolder serviceRegistryHolder;
 
       public AccessStrategyTestSetup(Test test, String configName) {
          super(test);
          this.configName = configName;
       }
 
       @Override
       protected void setUp() throws Exception {
          try {
             super.tearDown();
          } finally {
             if (preferIPv4Stack == null)
                System.clearProperty(PREFER_IPV4STACK);
             else
                System.setProperty(PREFER_IPV4STACK, preferIPv4Stack);
          }
 
          // Try to ensure we use IPv4; otherwise cluster formation is very slow
          preferIPv4Stack = System.getProperty(PREFER_IPV4STACK);
          System.setProperty(PREFER_IPV4STACK, "true");
 
 		 serviceRegistryHolder = new ServiceRegistryHolder( Environment.getProperties() );
 
          localCfg = createConfiguration(configName);
          localRegionFactory = CacheTestUtil.startRegionFactory(
-				 serviceRegistryHolder.getJdbcServicesImpl().getConnectionProvider(),
+				 serviceRegistryHolder.getJdbcServicesImpl(),
 				 localCfg
 		 );
 
          remoteCfg = createConfiguration(configName);
          remoteRegionFactory = CacheTestUtil.startRegionFactory(
-				 serviceRegistryHolder.getJdbcServicesImpl().getConnectionProvider(),
+				 serviceRegistryHolder.getJdbcServicesImpl(),
 				 remoteCfg
 		 );
       }
 
       @Override
       protected void tearDown() throws Exception {
          super.tearDown();
 
 		  try {
             if (localRegionFactory != null)
                localRegionFactory.stop();
 
             if (remoteRegionFactory != null)
                remoteRegionFactory.stop();
 		  }
 		  finally {
             if ( serviceRegistryHolder != null ) {
                serviceRegistryHolder.destroy();
             }
 		  }
       }
 
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
index 3079be28e5..0aac7c9792 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
@@ -1,149 +1,149 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Tests for the "extra API" in EntityRegionAccessStrategy;.
  * <p>
  * By "extra API" we mean those methods that are superfluous to the 
  * function of the JBC integration, where the impl is a no-op or a static
  * false return value, UnsupportedOperationException, etc.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TransactionalExtraAPITestCase extends AbstractNonFunctionalTestCase {
 
    public TransactionalExtraAPITestCase(String name) {
       super(name);
    }
    
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
    
    private static EntityRegionAccessStrategy localAccessStrategy;
    
    private static boolean optimistic;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        if (getEntityAccessStrategy() == null) {
            Configuration cfg = createConfiguration();
            InfinispanRegionFactory rf  = CacheTestUtil.startRegionFactory(
-				   getConnectionProvider(), cfg, getCacheTestSupport()
+				   getJdbcServices(), cfg, getCacheTestSupport()
 		   );
            
            // Sleep a bit to avoid concurrent FLUSH problem
            avoidConcurrentFlush();
            
            EntityRegion localEntityRegion = rf.buildEntityRegion(REGION_NAME, cfg.getProperties(), null);
            setEntityRegionAccessStrategy(localEntityRegion.buildAccessStrategy(getAccessType()));
        }
    }
 
    protected void tearDown() throws Exception {
        
        super.tearDown();
    }
    
    protected Configuration createConfiguration() {
        Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
        cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, getCacheConfigName());
        return cfg;
    }
    
    protected String getCacheConfigName() {
        return "entity";
    }
    
    protected AccessType getAccessType() {
        return AccessType.TRANSACTIONAL;
    }
    
    protected EntityRegionAccessStrategy getEntityAccessStrategy() {
        return localAccessStrategy;
    }
    
    protected void setEntityRegionAccessStrategy(EntityRegionAccessStrategy strategy) {
        localAccessStrategy = strategy;
    }
 
    /**
     * Test method for {@link TransactionalAccess#lockItem(java.lang.Object, java.lang.Object)}.
     */
    public void testLockItem() {
        assertNull(getEntityAccessStrategy().lockItem(KEY, new Integer(1)));
    }
 
    /**
     * Test method for {@link TransactionalAccess#lockRegion()}.
     */
    public void testLockRegion() {
        assertNull(getEntityAccessStrategy().lockRegion());
    }
 
    /**
     * Test method for {@link TransactionalAccess#unlockItem(java.lang.Object, org.hibernate.cache.access.SoftLock)}.
     */
    public void testUnlockItem() {
        getEntityAccessStrategy().unlockItem(KEY, new MockSoftLock());
    }
 
    /**
     * Test method for {@link TransactionalAccess#unlockRegion(org.hibernate.cache.access.SoftLock)}.
     */
    public void testUnlockRegion() {
        getEntityAccessStrategy().unlockItem(KEY, new MockSoftLock());
    }
 
    /**
     * Test method for {@link TransactionalAccess#afterInsert(java.lang.Object, java.lang.Object, java.lang.Object)}.
     */
    public void testAfterInsert() {
        assertFalse("afterInsert always returns false", getEntityAccessStrategy().afterInsert(KEY, VALUE1, new Integer(1)));
    }
 
    /**
     * Test method for {@link TransactionalAccess#afterUpdate(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, org.hibernate.cache.access.SoftLock)}.
     */
    public void testAfterUpdate() {
        assertFalse("afterInsert always returns false", getEntityAccessStrategy().afterUpdate(KEY, VALUE2, new Integer(1), new Integer(2), new MockSoftLock()));
    }
    
    public static class MockSoftLock implements SoftLock {
        
    }
 
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index a9b49acec0..e1180116d9 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,303 +1,303 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.query;
 
 import java.util.Properties;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 
 /**
  * Tests of QueryResultRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
    // protected static final String REGION_NAME = "test/" + StandardQueryCache.class.getName();
 
    /**
     * Create a new EntityRegionImplTestCase.
     * 
     * @param name
     */
    public QueryRegionImplTestCase(String name) {
       super(name);
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildQueryResultsRegion(regionName, properties);
    }
 
    @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + StandardQueryCache.class.getName();
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("local-query"));
    }
    
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildCustomQueryCacheConfiguration("test", "replicated-query");
    }
 
    public void testPutDoesNotBlockGet() throws Exception {
       putDoesNotBlockGetTest();
    }
 
    private void putDoesNotBlockGetTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport());
+			  getJdbcServices(), cfg, getCacheTestSupport());
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg
                .getProperties());
 
       region.put(KEY, VALUE1);
       assertEquals(VALUE1, region.get(KEY));
 
       final CountDownLatch readerLatch = new CountDownLatch(1);
       final CountDownLatch writerLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(1);
       final ExceptionHolder holder = new ExceptionHolder();
 
       Thread reader = new Thread() {
          public void run() {
             try {
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, get value for key");
                assertTrue(VALUE2.equals(region.get(KEY)) == false);
                BatchModeTransactionManager.getInstance().commit();
             } catch (AssertionFailedError e) {
                holder.a1 = e;
                rollback();
             } catch (Exception e) {
                holder.e1 = e;
                rollback();
             } finally {
                readerLatch.countDown();
             }
          }
       };
 
       Thread writer = new Thread() {
          public void run() {
             try {
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Put value2");
                region.put(KEY, VALUE2);
                log.debug("Put finished for value2, await writer latch");
                writerLatch.await();
                log.debug("Writer latch finished");
                BatchModeTransactionManager.getInstance().commit();
                log.debug("Transaction committed");
             } catch (Exception e) {
                holder.e2 = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       reader.setDaemon(true);
       writer.setDaemon(true);
 
       writer.start();
       assertFalse("Writer is blocking", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
       // Start the reader
       reader.start();
       assertTrue("Reader finished promptly", readerLatch.await(1000000000, TimeUnit.MILLISECONDS));
 
       writerLatch.countDown();
       assertTrue("Reader finished promptly", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
       assertEquals(VALUE2, region.get(KEY));
 
       if (holder.a1 != null)
          throw holder.a1;
       else if (holder.a2 != null)
          throw holder.a2;
 
       assertEquals("writer saw no exceptions", null, holder.e1);
       assertEquals("reader saw no exceptions", null, holder.e2);
    }
 
    public void testGetDoesNotBlockPut() throws Exception {
       getDoesNotBlockPutTest();
    }
 
    private void getDoesNotBlockPutTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg
                .getProperties());
 
       region.put(KEY, VALUE1);
       assertEquals(VALUE1, region.get(KEY));
 
       // final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
       final CacheAdapter jbc = getInfinispanCache(regionFactory);
 
       final CountDownLatch blockerLatch = new CountDownLatch(1);
       final CountDownLatch writerLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(1);
       final ExceptionHolder holder = new ExceptionHolder();
 
       Thread blocker = new Thread() {
 
          public void run() {
             // Fqn toBlock = new Fqn(rootFqn, KEY);
             GetBlocker blocker = new GetBlocker(blockerLatch, KEY);
             try {
                jbc.addListener(blocker);
 
                BatchModeTransactionManager.getInstance().begin();
                region.get(KEY);
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                holder.e1 = e;
                rollback();
             } finally {
                jbc.removeListener(blocker);
             }
          }
       };
 
       Thread writer = new Thread() {
 
          public void run() {
             try {
                writerLatch.await();
 
                BatchModeTransactionManager.getInstance().begin();
                region.put(KEY, VALUE2);
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                holder.e2 = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       blocker.setDaemon(true);
       writer.setDaemon(true);
 
       boolean unblocked = false;
       try {
          blocker.start();
          writer.start();
 
          assertFalse("Blocker is blocking", completionLatch.await(100, TimeUnit.MILLISECONDS));
          // Start the writer
          writerLatch.countDown();
          assertTrue("Writer finished promptly", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
          blockerLatch.countDown();
          unblocked = true;
 
          if (IsolationLevel.REPEATABLE_READ.equals(jbc.getConfiguration().getIsolationLevel())) {
             assertEquals(VALUE1, region.get(KEY));
          } else {
             assertEquals(VALUE2, region.get(KEY));
          }
 
          if (holder.a1 != null)
             throw holder.a1;
          else if (holder.a2 != null)
             throw holder.a2;
 
          assertEquals("blocker saw no exceptions", null, holder.e1);
          assertEquals("writer saw no exceptions", null, holder.e2);
       } finally {
          if (!unblocked)
             blockerLatch.countDown();
       }
    }
 
    @Listener
    public class GetBlocker {
 
       private CountDownLatch latch;
       // private Fqn fqn;
       private Object key;
 
       GetBlocker(CountDownLatch latch, Object key) {
          this.latch = latch;
          this.key = key;
       }
 
       @CacheEntryVisited
       public void nodeVisisted(CacheEntryVisitedEvent event) {
          if (event.isPre() && event.getKey().equals(key)) {
             try {
                latch.await();
             } catch (InterruptedException e) {
                log.error("Interrupted waiting for latch", e);
             }
          }
       }
    }
 
    private class ExceptionHolder {
       Exception e1;
       Exception e2;
       AssertionFailedError a1;
       AssertionFailedError a2;
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index 8d2c572e12..afd8dfc663 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,214 +1,214 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.timestamp;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.ClassLoaderAwareCache;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.AccountHolder;
 import org.hibernate.test.cache.infinispan.functional.classloader.SelectedClassnameClassLoader;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.infinispan.AdvancedCache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
 import org.infinispan.notifications.cachelistener.event.Event;
 
 import javax.transaction.TransactionManager;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
    public TimestampsRegionImplTestCase(String name) {
       super(name);
    }
 
     @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("timestamps"));
    }
 
    public void testClearTimestampsRegionInIsolated() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg, getCacheTestSupport()
+			  getJdbcServices(), cfg, getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       Configuration cfg2 = createConfiguration();
       InfinispanRegionFactory regionFactory2 = CacheTestUtil.startRegionFactory(
-			  getConnectionProvider(), cfg2, getCacheTestSupport()
+			  getJdbcServices(), cfg2, getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       TimestampsRegionImpl region = (TimestampsRegionImpl) regionFactory.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg.getProperties());
       TimestampsRegionImpl region2 = (TimestampsRegionImpl) regionFactory2.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 //      QueryResultsRegion region2 = regionFactory2.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 
 //      ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //      Thread.currentThread().setContextClassLoader(cl.getParent());
 //      log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
       region.getCacheAdapter().withFlags(FlagAdapter.FORCE_SYNCHRONOUS).put(acct, "boo");
 
 //      region.put(acct, "boo");
 //
 //      region.evictAll();
 
 //      Account acct = new Account();
 //      acct.setAccountHolder(new AccountHolder());
 
 
 
    }
 
    @Override
    protected Configuration createConfiguration() {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", MockInfinispanRegionFactory.class, false, true);
       return cfg;
    }
 
    public static class MockInfinispanRegionFactory extends InfinispanRegionFactory {
 
       public MockInfinispanRegionFactory() {
       }
 
       public MockInfinispanRegionFactory(Properties props) {
          super(props);
       }
 
 //      @Override
 //      protected TimestampsRegionImpl createTimestampsRegion(CacheAdapter cacheAdapter, String regionName) {
 //         return new MockTimestampsRegionImpl(cacheAdapter, regionName, getTransactionManager(), this);
 //      }
 
       @Override
       protected ClassLoaderAwareCache createCacheWrapper(AdvancedCache cache) {
          return new ClassLoaderAwareCache(cache, Thread.currentThread().getContextClassLoader()) {
             @Override
             public void addListener(Object listener) {
                super.addListener(new MockClassLoaderAwareListener(listener, this));
             }
          };
       }
 
       //      @Override
 //      protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
 //         try {
 //            EmbeddedCacheManager manager = new DefaultCacheManager(InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
 //            org.infinispan.config.Configuration ispnCfg = new org.infinispan.config.Configuration();
 //            ispnCfg.setCacheMode(org.infinispan.config.Configuration.CacheMode.REPL_SYNC);
 //            manager.defineConfiguration("timestamps", ispnCfg);
 //            return manager;
 //         } catch (IOException e) {
 //            throw new CacheException("Unable to create default cache manager", e);
 //         }
 //      }
 
       @Listener      
       public static class MockClassLoaderAwareListener extends ClassLoaderAwareCache.ClassLoaderAwareListener {
          MockClassLoaderAwareListener(Object listener, ClassLoaderAwareCache cache) {
             super(listener, cache);
          }
 
          @CacheEntryActivated
          @CacheEntryCreated
          @CacheEntryEvicted
          @CacheEntryInvalidated
          @CacheEntryLoaded
          @CacheEntryModified
          @CacheEntryPassivated
          @CacheEntryRemoved
          @CacheEntryVisited
          public void event(Event event) throws Throwable {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
             String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
             SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
             Thread.currentThread().setContextClassLoader(visible);
             super.event(event);
             Thread.currentThread().setContextClassLoader(cl);            
          }
       }
    }
 
 //   @Listener
 //   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
 //
 //      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
 //         super(cacheAdapter, name, transactionManager, factory);
 //      }
 //
 //      @CacheEntryModified
 //      public void nodeModified(CacheEntryModifiedEvent event) {
 ////         ClassLoader cl = Thread.currentThread().getContextClassLoader();
 ////         String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
 ////         String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
 ////         SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
 ////         Thread.currentThread().setContextClassLoader(visible);
 //         super.nodeModified(event);
 ////         Thread.currentThread().setContextClassLoader(cl);
 //      }
 //   }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
index 152854f26d..42a9c5c43b 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
@@ -1,150 +1,151 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.util;
 
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServicesRegistry;
 
 /**
  * Utilities for cache testing.
  * 
  * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
  */
 public class CacheTestUtil {
 
    public static Configuration buildConfiguration(String regionPrefix, Class regionFactory, boolean use2ndLevel, boolean useQueries) {
       Configuration cfg = new Configuration();
       cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
       cfg.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
       cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, BatchModeTransactionManagerLookup.class.getName());
 
       cfg.setProperty(Environment.CACHE_REGION_FACTORY, regionFactory.getName());
       cfg.setProperty(Environment.CACHE_REGION_PREFIX, regionPrefix);
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, String.valueOf(use2ndLevel));
       cfg.setProperty(Environment.USE_QUERY_CACHE, String.valueOf(useQueries));
 
       return cfg;
    }
 
    public static Configuration buildLocalOnlyConfiguration(String regionPrefix, boolean use2ndLevel, boolean useQueries) {
       Configuration cfg = buildConfiguration(regionPrefix, InfinispanRegionFactory.class, use2ndLevel, useQueries);
       cfg.setProperty(InfinispanRegionFactory.INFINISPAN_CONFIG_RESOURCE_PROP,
                InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
       return cfg;
    }
    
    public static Configuration buildCustomQueryCacheConfiguration(String regionPrefix, String queryCacheName) {
       Configuration cfg = buildConfiguration(regionPrefix, InfinispanRegionFactory.class, true, true);
       cfg.setProperty(InfinispanRegionFactory.QUERY_CACHE_RESOURCE_PROP, queryCacheName);
       return cfg;
    }
 
-   public static InfinispanRegionFactory startRegionFactory(ConnectionProvider connectionProvider,
+   public static InfinispanRegionFactory startRegionFactory(JdbcServices jdbcServices,
 															Configuration cfg)
 		   throws ClassNotFoundException, InstantiationException, IllegalAccessException {
 
-      Settings settings = cfg.buildSettings( connectionProvider );
+      Settings settings = cfg.buildSettings( jdbcServices );
       Properties properties = cfg.getProperties();
 
       String factoryType = cfg.getProperty(Environment.CACHE_REGION_FACTORY);
       Class factoryClass = Thread.currentThread().getContextClassLoader().loadClass(factoryType);
       InfinispanRegionFactory regionFactory = (InfinispanRegionFactory) factoryClass.newInstance();
 
       regionFactory.start(settings, properties);
 
       return regionFactory;
    }
 
-   public static InfinispanRegionFactory startRegionFactory(ConnectionProvider connectionProvider,
+   public static InfinispanRegionFactory startRegionFactory(JdbcServices jdbcServices,
 															Configuration cfg,
 															CacheTestSupport testSupport)
             throws ClassNotFoundException, InstantiationException, IllegalAccessException {
-      InfinispanRegionFactory factory = startRegionFactory(connectionProvider, cfg);
+      InfinispanRegionFactory factory = startRegionFactory(jdbcServices, cfg);
       testSupport.registerFactory(factory);
       return factory;
    }
 
    public static void stopRegionFactory(InfinispanRegionFactory factory, CacheTestSupport testSupport) {
       factory.stop();
       testSupport.unregisterFactory(factory);
    }
 
    /**
     * Supports easy creation of a TestSuite where a subclass' "FailureExpected" version of a base
     * test is included in the suite, while the base test is excluded. E.g. test class FooTestCase
     * includes method testBar(), while test class SubFooTestCase extends FooTestCase includes method
     * testBarFailureExcluded(). Passing SubFooTestCase.class to this method will return a suite that
     * does not include testBar().
     * 
     * FIXME Move this to UnitTestCase
     */
    public static TestSuite createFailureExpectedSuite(Class testClass) {
 
       TestSuite allTests = new TestSuite(testClass);
       Set failureExpected = new HashSet();
       Enumeration tests = allTests.tests();
       while (tests.hasMoreElements()) {
          Test t = (Test) tests.nextElement();
          if (t instanceof TestCase) {
             String name = ((TestCase) t).getName();
             if (name.endsWith("FailureExpected"))
                failureExpected.add(name);
          }
       }
 
       TestSuite result = new TestSuite();
       tests = allTests.tests();
       while (tests.hasMoreElements()) {
          Test t = (Test) tests.nextElement();
          if (t instanceof TestCase) {
             String name = ((TestCase) t).getName();
             if (!failureExpected.contains(name + "FailureExpected")) {
                result.addTest(t);
             }
          }
       }
 
       return result;
    }
 
    /**
     * Prevent instantiation.
     */
    private CacheTestUtil() {
    }
 
 }
