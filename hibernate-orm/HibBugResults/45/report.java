File path: code/core/src/main/java/org/hibernate/cfg/Configuration.java
Comment: cky workaround for MySQL bug:
Initial commit id: d8d6d82e
Final commit id: 377c3000
   Bugs between [       0]:

   Bugs after [      31]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
23794bf294 HHH-9792 - Clean up missed Configuration methods
a92ddea9ca HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc) - Gunnar's feedback
027840018b HHH-8191 Support Teradata 14.0
63a0f03c5a HHH-9654 - Adjust envers for 5.0 APIs + JAXB
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
52f2c3a002 HHH-9388 HHH-7079 : Default collection table and foreign key names are incorrect in some cases; deprecate NamingStrategy
1cba98022e HHH-9388 HHH-7079 : Default collection table and foreign key names are incorrect in some cases; deprecate NamingStrategy
d023c7c95c HHH-8816 - Unable to instantiate AttributeConverter: root cause of exception hidden
b70148a85a HHH-6911 - Write DiscriminatorValue to DiscriminatorColumn when combined with InheritanceType#JOINED
5329bba1ea HHH-6911 - Write DiscriminatorValue to DiscriminatorColumn when combined with InheritanceType#JOINED
d430846076 HHH-5065 improved exception message on Configuration#getRootClassMapping
cd590470c0 HHH-8741 - More checkstyle cleanups
dd44ad459a HHH-7927 Enabling globally_quoted_identifiers breaks schema validation if TableGenerator is used
2060e95c40 HHH-8537 @UniqueConstraint naming non-existent column leads to NPE
498735aa37 HHH-8478 - AttributeConverters need to be applied to JPQL and Criteria queries
2bb866a616 HHH-8534 - Metamodel#managedType(SomeMappedSuperclass.class) returns null
580af7e61b HHH-8496 TableCatalog and TableSchema arguments mistaken in DB update-script.
7444c6c139 HHH-8297 Typo in error message: "contains phyical column name"
9348c23e00 HHH-8469 - Application of JPA 2.1 AttributeConverters
580a71331c HHH-8390 generate FK after UK
1825a4762c HHH-8211 Checkstyle and FindBugs fix-ups
5ea40ce3f5 HHH-8266 Binding of named-stored-procedure XML element tries to create duplicate
a03d44f290 HHH-8246 Implement XML binding of NamedStoredProcedureQuery
14993a4637 HHH-8223 - Implement @NamedEntityGraph binding
8c95a6077a HHH-8222 - Implement @NamedStoredProcedureQuery binding
9030fa015e HHH-8217 Make generated constraint names short and non-random
04fe84994d HHH-7995 Added support for TypeContributors in OSGi.  Integrated with envers Conflicts: 	hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java 	hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java 	hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiPersistenceProvider.java 	hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java

Start block index: 933
End block index: 1058
	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
			throws HibernateException {
		secondPassCompile();

		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );

		ArrayList script = new ArrayList( 50 );

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
					Iterator subiter = table.sqlAlterStrings(
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

				Iterator comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
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
									tableInfo.getForeignKeyMetadata( fk.getName() ) == null && (
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

			}

			/*//broken, 'cos we don't generate these with names in SchemaExport
			subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
					if ( tableInfo==null || tableInfo.getIndexMetadata( index.getFilterName() ) == null ) {
						script.add( index.sqlCreateString(dialect, mapping) );
					}
				}
			}
			//broken, 'cos we don't generate these with names in SchemaExport
			subIter = table.getUniqueKeyIterator();
			while ( subIter.hasNext() ) {
				UniqueKey uk = (UniqueKey) subIter.next();
				if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
					script.add( uk.sqlCreateString(dialect, mapping) );
				}
			}*/
		}

		iter = iterateGenerators( dialect );
		while ( iter.hasNext() ) {
			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
			Object key = generator.generatorKey();
			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
				String[] lines = generator.sqlCreateStrings( dialect );
				for ( int i = 0; i < lines.length ; i++ ) {
					script.add( lines[i] );
				}
			}
		}

		return ArrayHelper.toStringArray( script );
	}
