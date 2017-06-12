diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 1a8af3fe23..f9c8fbec42 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -858,2043 +858,2044 @@ public class Configuration implements Serializable {
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
         LOG.trace("Starting secondPassCompile() processing");
 
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
         LOG.debugf("Processing fk mappings (*ToOne and JoinedSubclass)");
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
             if (!isValidatorNotPresentLogged) LOG.validatorNotFound();
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
                         LOG.unableToApplyConstraints(className, e);
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
         LOG.debugf("Processing extends queue");
 		processExtendsQueue();
 
         LOG.debugf("Processing collection mappings");
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
         LOG.debugf("Processing native query and ResultSetMapping mappings");
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
         LOG.debugf("Processing association property references");
 
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
 
         LOG.debugf("Processing foreign key constraints");
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
         LOG.debugf("Processing extends queue");
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
                 LOG.debugf("Resolving reference to class: %s", referencedEntityName);
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
         LOG.debugf("Preparing to build session factory with filters : %s", filterDefinitions);
 
 		secondPassCompile();
         if (!metadataSourceQueue.isEmpty()) LOG.incompleteMappingMetadataCacheProcessing();
 
 // todo : processing listeners for validator and search (and envers) requires HHH-5562
 		enableLegacyHibernateValidator( serviceRegistry );
 		enableBeanValidation( serviceRegistry );
 //		enableHibernateSearch();
 
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
 		final ServiceRegistry serviceRegistry =  new BasicServiceRegistryImpl( properties );
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	private static final String LEGACY_VALIDATOR_EVENT_LISTENER = "org.hibernate.validator.event.ValidateEventListener";
 
 	private void enableLegacyHibernateValidator(ServiceRegistry serviceRegistry) {
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class )
 				.attachEventListenerRegistration( new LegacyHibernateValidatorEventListenerRegistration( properties ) );
 	}
 
 	public static class LegacyHibernateValidatorEventListenerRegistration implements EventListenerRegistration {
 		private final Properties configurationProperties;
 
 		public LegacyHibernateValidatorEventListenerRegistration(Properties configurationProperties) {
 			this.configurationProperties = configurationProperties;
 		}
 
 		@Override
-		public void apply(ServiceRegistryImplementor serviceRegistry, Configuration configuration, Map<?, ?> configValues) {
+		public void apply(
+				EventListenerRegistry eventListenerRegistry,
+				Configuration configuration, Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
+		) {
 			boolean loadLegacyValidator = ConfigurationHelper.getBoolean( "hibernate.validator.autoregister_listeners", configurationProperties, false );
 
 			Class validateEventListenerClass = null;
 			try {
 				validateEventListenerClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( LEGACY_VALIDATOR_EVENT_LISTENER );
 			}
 			catch ( Exception ignored) {
 			}
 
 			if ( ! loadLegacyValidator || validateEventListenerClass == null) {
 				LOG.debugf( "Skipping legacy validator loading" );
 				return;
 			}
 
 			final Object validateEventListener;
 			try {
 				validateEventListener = validateEventListenerClass.newInstance();
 			}
 			catch ( Exception e ) {
 				throw new AnnotationException( "Unable to load Validator event listener", e );
 			}
 
 			EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 			// todo : duplication strategy
 
 			listenerRegistry.appendListeners( EventType.PRE_INSERT, (PreInsertEventListener) validateEventListener );
 			listenerRegistry.appendListeners( EventType.PRE_UPDATE, (PreUpdateEventListener) validateEventListener );
 		}
 	}
 
 	private void enableBeanValidation(ServiceRegistry serviceRegistry) {
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
 				new EventListenerRegistration() {
 					@Override
 					public void apply(
-							ServiceRegistryImplementor serviceRegistry,
+							EventListenerRegistry eventListenerRegistry,
 							Configuration configuration,
-							Map<?, ?> configValues) {
-						BeanValidationActivator.activateBeanValidation(
-								serviceRegistry.getService( EventListenerRegistry.class ),
-								getProperties()
-						);
+							Map<?, ?> configValues,
+							ServiceRegistryImplementor serviceRegistry) {
+						BeanValidationActivator.activateBeanValidation( eventListenerRegistry, getProperties() );
 					}
 				}
 		);
 	}
 
 	private static final String SEARCH_EVENT_LISTENER_REGISTERER_CLASS = "org.hibernate.cfg.search.HibernateSearchEventListenerRegister";
 
 	/**
 	 * Tries to automatically register Hibernate Search event listeners by locating the
 	 * appropriate bootstrap class and calling the <code>enableHibernateSearch</code> method.
 	 */
 //	private void enableHibernateSearch() {
 //		// load the bootstrap class
 //		Class searchStartupClass;
 //		try {
 //			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
 //		}
 //		catch ( ClassNotFoundException e ) {
 //			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
 //			// try loading deprecated HibernateSearchEventListenerRegister
 //			try {
 //				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
 //			}
 //			catch ( ClassNotFoundException cnfe ) {
 //                LOG.debugf("Search not present in classpath, ignoring event listener registration.");
 //				return;
 //			}
 //		}
 //
 //		// call the method for registering the listeners
 //		try {
 //			Object searchStartupInstance = searchStartupClass.newInstance();
 //			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
 //					SEARCH_STARTUP_METHOD,
 //					EventListeners.class,
 //					Properties.class
 //			);
 //			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
 //		}
 //		catch ( InstantiationException e ) {
 //            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
 //		}
 //		catch ( IllegalAccessException e ) {
 //            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
 //		}
 //		catch ( NoSuchMethodException e ) {
 //            LOG.debugf("Method %s() not found in %s", SEARCH_STARTUP_METHOD, SEARCH_STARTUP_CLASS);
 //		}
 //		catch ( InvocationTargetException e ) {
 //            LOG.debugf("Unable to execute %s, ignoring event listener registration.", SEARCH_STARTUP_METHOD);
 //		}
 //	}
 
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
             LOG.debugf("%s=%s", name, value);
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
         LOG.configuringFromResource(resource);
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
         LOG.configurationResource(resource);
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
         LOG.configuringFromUrl(url);
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
         LOG.configuringFromFile(configFile.getName());
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
                 LOG.unableToCloseInputStreamForResource(resourceName, ioe);
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
 
         LOG.configuredSessionFactory(name);
         LOG.debugf("Properties: %s", properties);
 
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
             LOG.debugf("Session-factory config [%s] named resource [%s] for mapping", name, resourceName);
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named file [%s] for mapping", name, fileName);
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName);
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named package [%s] for mapping", name, packageName);
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named class [%s] for mapping", name, className);
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
         LOG.jaccContextId(contextId);
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
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
index 4078637e3d..7c1def1353 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
@@ -1,175 +1,174 @@
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
 package org.hibernate.cfg.beanvalidation;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
-import org.hibernate.event.EventListeners;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
  * This class has no hard dependency on Bean Validation APIs
  * It must use reflection every time BV is required.
  * @author Emmanuel Bernard
  */
 public class BeanValidationActivator {
 	private static final String BV_DISCOVERY_CLASS = "javax.validation.Validation";
 	private static final String TYPE_SAFE_ACTIVATOR_CLASS = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
 	private static final String TYPE_SAFE_DDL_METHOD = "applyDDL";
 	private static final String TYPE_SAFE_ACTIVATOR_METHOD = "activateBeanValidation";
 	private static final String MODE_PROPERTY = "javax.persistence.validation.mode";
 
 	public static void activateBeanValidation(EventListenerRegistry listenerRegistry, Properties properties) {
 		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
 
 		try {
 			//load Validation
 			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( modes.contains( ValidationMode.CALLBACK ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 
 		//de-activate not-null tracking at the core level when Bean Validation
 		// is present unless the user really asks for it
 		//Note that if BV is not present, the behavior is backward compatible
 		if ( properties.getProperty( Environment.CHECK_NULLABILITY ) == null ) {
 			properties.setProperty( Environment.CHECK_NULLABILITY, "false" );
 		}
 
 		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) return;
 
 		try {
 			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
 			Method activateBeanValidation =
 					activator.getMethod( TYPE_SAFE_ACTIVATOR_METHOD, EventListenerRegistry.class, Properties.class );
 			activateBeanValidation.invoke( null, listenerRegistry, properties );
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 	}
 
 	public static void applyDDL(Collection<PersistentClass> persistentClasses, Properties properties) {
 		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
 		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) return;
 		try {
 			//load Validation
 			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( modes.contains( ValidationMode.DDL ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 		try {
 			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
 			Method applyDDL =
 					activator.getMethod( TYPE_SAFE_DDL_METHOD, Collection.class, Properties.class );
 			applyDDL.invoke( null, persistentClasses, properties );
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 	}
 
 	private static enum ValidationMode {
 		AUTO,
 		CALLBACK,
 		NONE,
 		DDL;
 
 		public static Set<ValidationMode> getModes(Object modeProperty) {
 			Set<ValidationMode> modes = new HashSet<ValidationMode>(3);
 			if (modeProperty == null) {
 				modes.add(ValidationMode.AUTO);
 			}
 			else {
 				final String[] modesInString = modeProperty.toString().split( "," );
 				for ( String modeInString : modesInString ) {
 					modes.add( getMode(modeInString) );
 				}
 			}
 			if ( modes.size() > 1 && ( modes.contains( ValidationMode.AUTO ) || modes.contains( ValidationMode.NONE ) ) ) {
 				StringBuilder message = new StringBuilder( "Incompatible validation modes mixed: " );
 				for (ValidationMode mode : modes) {
 					message.append( mode ).append( ", " );
 				}
 				throw new HibernateException( message.substring( 0, message.length() - 2 ) );
 			}
 			return modes;
 		}
 
 		private static ValidationMode getMode(String modeProperty) {
 			if (modeProperty == null || modeProperty.length() == 0) {
 				return AUTO;
 			}
 			else {
 				try {
 					return valueOf( modeProperty.trim().toUpperCase() );
 				}
 				catch ( IllegalArgumentException e ) {
 					throw new HibernateException( "Unknown validation mode in " + MODE_PROPERTY + ": " + modeProperty.toString() );
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java b/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
index d74a4b43cc..6cc9a875d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
@@ -1,39 +1,45 @@
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
 package org.hibernate.event;
 
 import java.util.Map;
 
 import org.hibernate.cfg.Configuration;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Contract for performing event listener registration.  This is completely a work in progress for now.  The
  * expectation is that this gets tied in with the "service locator" pattern defined by HHH-5562
  *
  * @author Steve Ebersole
  */
 public interface EventListenerRegistration {
-	public void apply(ServiceRegistryImplementor serviceRegistry, Configuration configuration, Map<?,?> configValues);
+	public void apply(
+			EventListenerRegistry eventListenerRegistry,
+			Configuration configuration,
+			Map<?, ?> configValues,
+			ServiceRegistryImplementor serviceRegistry
+	);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
index 89e64d9b2a..fcf0c39b7c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
@@ -1,443 +1,444 @@
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
 package org.hibernate.service.event.internal;
 
 import java.lang.reflect.Array;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.event.def.DefaultDeleteEventListener;
 import org.hibernate.event.def.DefaultDirtyCheckEventListener;
 import org.hibernate.event.def.DefaultEvictEventListener;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.event.def.DefaultInitializeCollectionEventListener;
 import org.hibernate.event.def.DefaultLoadEventListener;
 import org.hibernate.event.def.DefaultLockEventListener;
 import org.hibernate.event.def.DefaultMergeEventListener;
 import org.hibernate.event.def.DefaultPersistEventListener;
 import org.hibernate.event.def.DefaultPersistOnFlushEventListener;
 import org.hibernate.event.def.DefaultPostLoadEventListener;
 import org.hibernate.event.def.DefaultPreLoadEventListener;
 import org.hibernate.event.def.DefaultRefreshEventListener;
 import org.hibernate.event.def.DefaultReplicateEventListener;
 import org.hibernate.event.def.DefaultSaveEventListener;
 import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
 import org.hibernate.event.def.DefaultUpdateEventListener;
-import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.event.spi.DuplicationStrategy;
 import org.hibernate.service.event.spi.EventListenerRegistrationException;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.StandardServiceInitiators.EventListenerRegistrationService;
 
 import static org.hibernate.event.EventType.AUTO_FLUSH;
 import static org.hibernate.event.EventType.DELETE;
 import static org.hibernate.event.EventType.DIRTY_CHECK;
 import static org.hibernate.event.EventType.EVICT;
 import static org.hibernate.event.EventType.FLUSH;
 import static org.hibernate.event.EventType.FLUSH_ENTITY;
 import static org.hibernate.event.EventType.INIT_COLLECTION;
 import static org.hibernate.event.EventType.LOAD;
 import static org.hibernate.event.EventType.LOCK;
 import static org.hibernate.event.EventType.MERGE;
 import static org.hibernate.event.EventType.PERSIST;
 import static org.hibernate.event.EventType.PERSIST_ONFLUSH;
 import static org.hibernate.event.EventType.POST_COLLECTION_RECREATE;
 import static org.hibernate.event.EventType.POST_COLLECTION_REMOVE;
 import static org.hibernate.event.EventType.POST_COLLECTION_UPDATE;
 import static org.hibernate.event.EventType.POST_COMMIT_DELETE;
 import static org.hibernate.event.EventType.POST_COMMIT_INSERT;
 import static org.hibernate.event.EventType.POST_COMMIT_UPDATE;
 import static org.hibernate.event.EventType.POST_DELETE;
 import static org.hibernate.event.EventType.POST_INSERT;
 import static org.hibernate.event.EventType.POST_LOAD;
 import static org.hibernate.event.EventType.POST_UPDATE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_RECREATE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_REMOVE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_UPDATE;
 import static org.hibernate.event.EventType.PRE_DELETE;
 import static org.hibernate.event.EventType.PRE_INSERT;
 import static org.hibernate.event.EventType.PRE_LOAD;
 import static org.hibernate.event.EventType.PRE_UPDATE;
 import static org.hibernate.event.EventType.REFRESH;
 import static org.hibernate.event.EventType.REPLICATE;
 import static org.hibernate.event.EventType.SAVE;
 import static org.hibernate.event.EventType.SAVE_UPDATE;
 import static org.hibernate.event.EventType.UPDATE;
 
 /**
  * @author Steve Ebersole
  */
 public class EventListenerRegistryImpl implements EventListenerRegistry {
 	private Map<Class,Object> listenerClassToInstanceMap = new HashMap<Class, Object>();
 
 	private Map<EventType,EventListenerGroupImpl> registeredEventListenersMap = prepareListenerMap();
 
 	@SuppressWarnings({ "unchecked" })
 	public <T> EventListenerGroupImpl<T> getEventListenerGroup(EventType<T> eventType) {
 		EventListenerGroupImpl<T> listeners = registeredEventListenersMap.get( eventType );
 		if ( listeners == null ) {
 			throw new HibernateException( "Unable to find listeners for type [" + eventType.eventName() + "]" );
 		}
 		return listeners;
 	}
 
 	@Override
 	public void addDuplicationStrategy(DuplicationStrategy strategy) {
 		for ( EventListenerGroupImpl group : registeredEventListenersMap.values() ) {
 			group.addDuplicationStrategy( strategy );
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, Class<T>... listenerClasses) {
 		setListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T[] resolveListenerInstances(EventType<T> type, Class<T>... listenerClasses) {
 		T[] listeners = (T[]) Array.newInstance( type.baseListenerInterface(), listenerClasses.length );
 		for ( int i = 0; i < listenerClasses.length; i++ ) {
 			listeners[i] = resolveListenerInstance( listenerClasses[i] );
 		}
 		return listeners;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T resolveListenerInstance(Class<T> listenerClass) {
 		T listenerInstance = (T) listenerClassToInstanceMap.get( listenerClass );
 		if ( listenerInstance == null ) {
 			listenerInstance = instantiateListener( listenerClass );
 			listenerClassToInstanceMap.put( listenerClass, listenerInstance );
 		}
 		return listenerInstance;
 	}
 
 	private <T> T instantiateListener(Class<T> listenerClass) {
 		try {
 			return listenerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new EventListenerRegistrationException(
 					"Unable to instantiate specified event listener class: " + listenerClass.getName(),
 					e
 			);
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, T... listeners) {
 		EventListenerGroupImpl<T> registeredListeners = getEventListenerGroup( type );
 		registeredListeners.clear();
 		if ( listeners != null ) {
 			for ( int i = 0, max = listeners.length; i < max; i++ ) {
 				registeredListeners.appendListener( listeners[i] );
 			}
 		}
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, Class<T>... listenerClasses) {
 		appendListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).appendListeners( listeners );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, Class<T>... listenerClasses) {
 		prependListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).prependListeners( listeners );
 	}
 
 	private static Map<EventType,EventListenerGroupImpl> prepareListenerMap() {
 		final Map<EventType,EventListenerGroupImpl> workMap = new HashMap<EventType, EventListenerGroupImpl>();
 
 		// auto-flush listeners
 		prepareListeners(
 				AUTO_FLUSH,
 				new DefaultAutoFlushEventListener(),
 				workMap
 		);
 
 		// create listeners
 		prepareListeners(
 				PERSIST,
 				new DefaultPersistEventListener(),
 				workMap
 		);
 
 		// create-onflush listeners
 		prepareListeners(
 				PERSIST_ONFLUSH,
 				new DefaultPersistOnFlushEventListener(),
 				workMap
 		);
 
 		// delete listeners
 		prepareListeners(
 				DELETE,
 				new DefaultDeleteEventListener(),
 				workMap
 		);
 
 		// dirty-check listeners
 		prepareListeners(
 				DIRTY_CHECK,
 				new DefaultDirtyCheckEventListener(),
 				workMap
 		);
 
 		// evict listeners
 		prepareListeners(
 				EVICT,
 				new DefaultEvictEventListener(),
 				workMap
 		);
 
 		// flush listeners
 		prepareListeners(
 				FLUSH,
 				new DefaultFlushEventListener(),
 				workMap
 		);
 
 		// flush-entity listeners
 		prepareListeners(
 				FLUSH_ENTITY,
 				new DefaultFlushEntityEventListener(),
 				workMap
 		);
 
 		// load listeners
 		prepareListeners(
 				LOAD,
 				new DefaultLoadEventListener(),
 				workMap
 		);
 
 		// load-collection listeners
 		prepareListeners(
 				INIT_COLLECTION,
 				new DefaultInitializeCollectionEventListener(),
 				workMap
 		);
 
 		// lock listeners
 		prepareListeners(
 				LOCK,
 				new DefaultLockEventListener(),
 				workMap
 		);
 
 		// merge listeners
 		prepareListeners(
 				MERGE,
 				new DefaultMergeEventListener(),
 				workMap
 		);
 
 		// pre-collection-recreate listeners
 		prepareListeners(
 				PRE_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// pre-collection-remove listeners
 		prepareListeners(
 				PRE_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// pre-collection-update listeners
 		prepareListeners(
 				PRE_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// pre-delete listeners
 		prepareListeners(
 				PRE_DELETE,
 				workMap
 		);
 
 		// pre-insert listeners
 		prepareListeners(
 				PRE_INSERT,
 				workMap
 		);
 
 		// pre-load listeners
 		prepareListeners(
 				PRE_LOAD,
 				new DefaultPreLoadEventListener(),
 				workMap
 		);
 
 		// pre-update listeners
 		prepareListeners(
 				PRE_UPDATE,
 				workMap
 		);
 
 		// post-collection-recreate listeners
 		prepareListeners(
 				POST_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// post-collection-remove listeners
 		prepareListeners(
 				POST_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// post-collection-update listeners
 		prepareListeners(
 				POST_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// post-commit-delete listeners
 		prepareListeners(
 				POST_COMMIT_DELETE,
 				workMap
 		);
 
 		// post-commit-insert listeners
 		prepareListeners(
 				POST_COMMIT_INSERT,
 				workMap
 		);
 
 		// post-commit-update listeners
 		prepareListeners(
 				POST_COMMIT_UPDATE,
 				workMap
 		);
 
 		// post-delete listeners
 		prepareListeners(
 				POST_DELETE,
 				workMap
 		);
 
 		// post-insert listeners
 		prepareListeners(
 				POST_INSERT,
 				workMap
 		);
 
 		// post-load listeners
 		prepareListeners(
 				POST_LOAD,
 				new DefaultPostLoadEventListener(),
 				workMap
 		);
 
 		// post-update listeners
 		prepareListeners(
 				POST_UPDATE,
 				workMap
 		);
 
 		// update listeners
 		prepareListeners(
 				UPDATE,
 				new DefaultUpdateEventListener(),
 				workMap
 		);
 
 		// refresh listeners
 		prepareListeners(
 				REFRESH,
 				new DefaultRefreshEventListener(),
 				workMap
 		);
 
 		// replicate listeners
 		prepareListeners(
 				REPLICATE,
 				new DefaultReplicateEventListener(),
 				workMap
 		);
 
 		// save listeners
 		prepareListeners(
 				SAVE,
 				new DefaultSaveEventListener(),
 				workMap
 		);
 
 		// save-update listeners
 		prepareListeners(
 				SAVE_UPDATE,
 				new DefaultSaveOrUpdateEventListener(),
 				workMap
 		);
 
 		return Collections.unmodifiableMap( workMap );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, Map<EventType,EventListenerGroupImpl> map) {
 		prepareListeners( type, null, map );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, T defaultListener, Map<EventType,EventListenerGroupImpl> map) {
 		final EventListenerGroupImpl<T> listeners = new EventListenerGroupImpl<T>( type );
 		if ( defaultListener != null ) {
 			listeners.appendListener( defaultListener );
 		}
 		map.put( type, listeners  );
 	}
 
 	public static EventListenerRegistryImpl buildEventListenerRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration,
 			ServiceRegistryImplementor serviceRegistry) {
 		final EventListenerRegistryImpl registry = new EventListenerRegistryImpl();
 
-		final EventListenerRegistrationService registrationService =  serviceRegistry.getService( EventListenerRegistrationService.class );
+		final EventListenerRegistrationService registrationService =  serviceRegistry.getService(
+				EventListenerRegistrationService.class
+		);
 		for ( EventListenerRegistration registration : registrationService.getEventListenerRegistrations() ) {
-			registration.apply( serviceRegistry, configuration, null );
+			registration.apply( registry, configuration, null, serviceRegistry );
 		}
 
 		return registry;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
index 66c3dd97a1..0479485c34 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
@@ -1,53 +1,53 @@
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
 package org.hibernate.service.event.internal;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * Service initiator for {@link EventListenerRegistry}
  *
  * @author Steve Ebersole
  */
 public class EventListenerServiceInitiator implements SessionFactoryServiceInitiator<EventListenerRegistry> {
 	public static final EventListenerServiceInitiator INSTANCE = new EventListenerServiceInitiator();
 
 	@Override
 	public Class<EventListenerRegistry> getServiceInitiated() {
 		return EventListenerRegistry.class;
 	}
 
 	@Override
 	public EventListenerRegistry initiateService(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration,
 			ServiceRegistryImplementor registry) {
-		return new EventListenerRegistryImpl();
+		return EventListenerRegistryImpl.buildEventListenerRegistry( sessionFactory, configuration, registry );
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
index 47cf0a92f9..34a79ebf52 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
@@ -1,127 +1,128 @@
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
 package org.hibernate.test.events;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.Destructible;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.Initializable;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 
 /**
  * CallbackTest implementation
  *
  * @author Steve Ebersole
  */
 public class CallbackTest extends BaseCoreFunctionalTestCase {
 	private TestingObserver observer = new TestingObserver();
 	private TestingListener listener = new TestingListener();
 
 	public String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setSessionFactoryObserver( observer );
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
 				new EventListenerRegistration() {
 					@Override
 					public void apply(
-							ServiceRegistryImplementor serviceRegistry,
+							EventListenerRegistry eventListenerRegistry,
 							Configuration configuration,
-							Map<?, ?> configValues) {
+							Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
+					) {
 						serviceRegistry.getService( EventListenerRegistry.class ).setListeners( EventType.DELETE, listener );
 					}
 				}
 		);
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-5913", message = "Need to figure out how to initialize/destroy event listeners now")
 	public void testCallbacks() {
 		assertEquals( "observer not notified of creation", 1, observer.creationCount );
 		assertEquals( "listener not notified of creation", 1, listener.initCount );
 
 		sessionFactory().close();
 
 		assertEquals( "observer not notified of close", 1, observer.closedCount );
 		assertEquals( "listener not notified of close", 1, listener.destoryCount );
 	}
 
 	private static class TestingObserver implements SessionFactoryObserver {
 		private int creationCount = 0;
 		private int closedCount = 0;
 
 		public void sessionFactoryCreated(SessionFactory factory) {
 			creationCount++;
 		}
 
 		public void sessionFactoryClosed(SessionFactory factory) {
 			closedCount++;
 		}
 	}
 
 	private static class TestingListener implements DeleteEventListener, Initializable, Destructible {
 		private int initCount = 0;
 		private int destoryCount = 0;
 
 		public void initialize(Configuration cfg) {
 			initCount++;
 		}
 
 		public void cleanup() {
 			destoryCount++;
 		}
 
 		public void onDelete(DeleteEvent event) throws HibernateException {
 		}
 
 		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
index c861279a7f..0680a7e167 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,173 +1,173 @@
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
 package org.hibernate.test.jpa;
 
 import javax.persistence.EntityNotFoundException;
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.event.def.DefaultPersistEventListener;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * An abstract test for all JPA spec related tests.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractJPATest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/Part.hbm.xml", "jpa/Item.hbm.xml", "jpa/MyEntity.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		cfg.setEntityNotFoundDelegate( new JPAEntityNotFoundDelegate() );
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
 				new EventListenerRegistration() {
 					@Override
 					public void apply(
-							ServiceRegistryImplementor serviceRegistry,
+							EventListenerRegistry eventListenerRegistry,
 							Configuration configuration,
-							Map<?, ?> configValues) {
-						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+							Map<?, ?> configValues,
+							ServiceRegistryImplementor serviceRegistry) {
 						eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
 						eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
 					}
 				}
 		);
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		// no second level caching
 		return null;
 	}
 
 
 	// mimic specific exception aspects of the JPA environment ~~~~~~~~~~~~~~~~
 
 	private static class JPAEntityNotFoundDelegate implements EntityNotFoundDelegate {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);			
 		}
 	}
 
 	// mimic specific event aspects of the JPA environment ~~~~~~~~~~~~~~~~~~~~
 
 	protected PersistEventListener[] buildPersistEventListeners() {
 		return new PersistEventListener[] { new JPAPersistEventListener() };
 	}
 
 	protected PersistEventListener[] buildPersisOnFlushEventListeners() {
 		return new PersistEventListener[] { new JPAPersistOnFlushEventListener() };
 	}
 
 	protected AutoFlushEventListener[] buildAutoFlushEventListeners() {
 		return new AutoFlushEventListener[] { JPAAutoFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEventListener[] buildFlushEventListeners() {
 		return new FlushEventListener[] { JPAFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEntityEventListener[] buildFlushEntityEventListeners() {
 		return new FlushEntityEventListener[] { new JPAFlushEntityEventListener() };
 	}
 
 	public static class JPAPersistEventListener extends DefaultPersistEventListener {
 		// overridden in JPA impl for entity callbacks...
 	}
 
 	public static class JPAPersistOnFlushEventListener extends JPAPersistEventListener {
 		@Override
         protected CascadingAction getCascadeAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 	}
 
 	public static class JPAAutoFlushEventListener extends DefaultAutoFlushEventListener {
 		// not sure why EM code has this ...
 		public static final AutoFlushEventListener INSTANCE = new JPAAutoFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEventListener extends DefaultFlushEventListener {
 		// not sure why EM code has this ...
 		public static final FlushEventListener INSTANCE = new JPAFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEntityEventListener extends DefaultFlushEntityEventListener {
 		// in JPA, used mainly for preUpdate callbacks...
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
index 970d336a37..e42fa71373 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
@@ -1,194 +1,194 @@
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
 package org.hibernate.test.keymanytoone.bidir.component;
 
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.def.DefaultLoadEventListener;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"unchecked"})
 public class EagerKeyManyToOneTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "keymanytoone/bidir/component/EagerMapping.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
 
 		// (they are different service registries!)
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
 				new EventListenerRegistration() {
 					@Override
 					public void apply(
-							ServiceRegistryImplementor serviceRegistry,
+							EventListenerRegistry eventListenerRegistry,
 							Configuration configuration,
-							Map<?, ?> configValues) {
-						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+							Map<?, ?> configValues,
+							ServiceRegistryImplementor serviceRegistry) {
 						eventListenerRegistry.prependListeners( EventType.LOAD, new CustomLoadListener() );
 					}
 				}
 		);
 	}
 
 	@Test
 	public void testSaveCascadedToKeyManyToOne() {
 		sessionFactory().getStatistics().clear();
 
 		// test cascading a save to an association with a key-many-to-one which refers to a
 		// just saved entity
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.flush();
 		assertEquals( 2, sessionFactory().getStatistics().getEntityInsertCount() );
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadingStrategies() {
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		cust = ( Customer ) s.createQuery( "from Customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders as o join fetch o.id.customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createCriteria( Customer.class ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2277")
 	public void testLoadEntityWithEagerFetchingToKeyManyToOneReferenceBackToSelf() {
 		sessionFactory().getStatistics().clear();
 
 		// long winded method name to say that this is a test specifically for HHH-2277 ;)
 		// essentially we have a bidirectional association where one side of the
 		// association is actually part of a composite PK.
 		//
 		// The way these are mapped causes the problem because both sides
 		// are defined as eager which leads to the infinite loop; if only
 		// one side is marked as eager, then all is ok.  In other words the
 		// problem arises when both pieces of instance data are coming from
 		// the same result set.  This is because no "entry" can be placed
 		// into the persistence context for the association with the
 		// composite key because we are in the process of trying to build
 		// the composite-id instance
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			cust = ( Customer ) s.get( Customer.class, cust.getId() );
 		}
 		catch( OverflowCondition overflow ) {
 			fail( "get()/load() caused overflow condition" );
 		}
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private static class OverflowCondition extends RuntimeException {
 	}
 
 	private static class CustomLoadListener extends DefaultLoadEventListener {
 		private int internalLoadCount = 0;
 		public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
 			if ( LoadEventListener.INTERNAL_LOAD_EAGER.getName().equals( loadType.getName() ) ) {
 				internalLoadCount++;
 				if ( internalLoadCount > 10 ) {
 					throw new OverflowCondition();
 				}
 			}
 			super.onLoad( event, loadType );
 			internalLoadCount--;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
index fec2b3c744..5d8e6fc447 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
@@ -1,171 +1,173 @@
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
 package org.hibernate.ejb.event;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Mappings;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.secure.JACCPreDeleteEventListener;
 import org.hibernate.secure.JACCPreInsertEventListener;
 import org.hibernate.secure.JACCPreLoadEventListener;
 import org.hibernate.secure.JACCPreUpdateEventListener;
 import org.hibernate.secure.JACCSecurityListener;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.event.spi.DuplicationStrategy;
 import org.hibernate.service.event.spi.EventListenerGroup;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Prepare the HEM-specific event listeners.
  * 
  * @todo : tie this in with the "service locator" pattern defined by HHH-5562
  * 
  * @author Steve Ebersole
  */
 public class JpaEventListenerRegistration implements EventListenerRegistration {
 	private static final DuplicationStrategy JPA_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					HibernateEntityManagerEventListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	private static final DuplicationStrategy JACC_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					JACCSecurityListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
-	public void apply(ServiceRegistryImplementor serviceRegistry, Configuration configuration, Map<?, ?> configValues) {
+	public void apply(
+			EventListenerRegistry eventListenerRegistry,
+			Configuration configuration, Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
+	) {
 		boolean isSecurityEnabled = configValues.containsKey( AvailableSettings.JACC_ENABLED );
 
 		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 		eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
 		eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
 		// op listeners
 		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, EJB3AutoFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.DELETE, new EJB3DeleteEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new EJB3FlushEntityEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH, EJB3FlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.MERGE, new EJB3MergeEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST, new EJB3PersistEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new EJB3PersistOnFlushEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE, new EJB3SaveEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new EJB3SaveOrUpdateEventListener() );
 
 		// pre op listeners
 		if ( isSecurityEnabled ) {
 			eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener() );
 		}
 
 		// post op listeners
 		eventListenerRegistry.prependListeners( EventType.POST_DELETE, new EJB3PostDeleteEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_INSERT, new EJB3PostInsertEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_LOAD, new EJB3PostLoadEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new EJB3PostUpdateEventListener() );
 
 		for ( Map.Entry<?,?> entry : configValues.entrySet() ) {
 			if ( ! String.class.isInstance( entry.getKey() ) ) {
 				continue;
 			}
 			final String propertyName = (String) entry.getKey();
 			if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
 				continue;
 			}
 			final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
 			final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			eventListenerGroup.clear();
 			for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
 				eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
 			}
 		}
 
 		// todo : we may need to account for callback handlers previously set (shared across EMFs)
 
 		final EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
 		Iterator classes = configuration.getClassMappings();
 		ReflectionManager reflectionManager = configuration.getReflectionManager();
 		while ( classes.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) classes.next();
 			if ( clazz.getClassName() == null ) {
 				//we can have non java class persisted by hibernate
 				continue;
 			}
 			try {
 				callbackHandler.add( reflectionManager.classForName( clazz.getClassName(), this.getClass() ), reflectionManager );
 			}
 			catch (ClassNotFoundException e) {
 				throw new MappingException( "entity class not found: " + clazz.getNodeName(), e );
 			}
 		}
 
 		for ( EventType eventType : EventType.values() ) {
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( Object listener : eventListenerGroup.listeners() ) {
 				if ( CallbackHandlerConsumer.class.isInstance( listener ) ) {
 					( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
 				}
 			}
 		}
 	}
 
 	private Object instantiate(String listenerImpl, ServiceRegistryImplementor serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
 		}
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java
index d60207994d..e0845a19b3 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java
@@ -1,59 +1,61 @@
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
 package org.hibernate.envers.event;
 
 import java.util.Map;
 
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Mappings;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * See "transitory" notes on {@link EventListenerRegistration}
  * 
  * @author Steve Ebersole
  */
 public class EnversEventListenerRegistration implements EventListenerRegistration {
 	@Override
-	public void apply(ServiceRegistryImplementor serviceRegistry, Configuration configuration, Map<?, ?> configValues) {
+	public void apply(
+			EventListenerRegistry eventListenerRegistry,
+			Configuration configuration, Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
+	) {
 		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 		listenerRegistry.addDuplicationStrategy( EnversListenerDuplicationStrategy.INSTANCE );
 
 		// todo : build AuditConfiguration; requires massive changes...
 //		AuditConfiguration auditConfiguration = AuditConfiguration.getFor( ??? )
 		AuditConfiguration auditConfiguration = null;
 
 		// create/add listeners
 		listenerRegistry.appendListeners( EventType.POST_DELETE, new EnversPostDeleteEventListenerImpl( auditConfiguration ) );
 		listenerRegistry.appendListeners( EventType.POST_INSERT, new EnversPostInsertEventListenerImpl( auditConfiguration ) );
 		listenerRegistry.appendListeners( EventType.POST_UPDATE, new EnversPostUpdateEventListenerImpl( auditConfiguration ) );
 		listenerRegistry.appendListeners( EventType.POST_COLLECTION_RECREATE, new EnversPostCollectionRecreateEventListenerImpl( auditConfiguration ) );
 		listenerRegistry.appendListeners( EventType.PRE_COLLECTION_REMOVE, new EnversPreCollectionRemoveEventListenerImpl( auditConfiguration ) );
 		listenerRegistry.appendListeners( EventType.PRE_COLLECTION_UPDATE, new EnversPreCollectionUpdateEventListenerImpl( auditConfiguration ) );
 	}
 }
