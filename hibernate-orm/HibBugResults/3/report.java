File path: code/core/src/main/java/org/hibernate/cfg/SettingsFactory.java
Comment: 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value
Initial commit id: d8d6d82e
Final commit id: 91d44442
   Bugs between [       0]:

   Bugs after [      39]:
2c968538a5 HHH-9728 - Audit Settings to decide what should become a SessionFactoryServiceRegistry service
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
cd590470c0 HHH-8741 - More checkstyle cleanups
1e74abf511 HHH-8654 - Ability to get notified of interesting Session events
f073b979d4 HHH-8654 - Ability to get noitified of interesting Session events
fdcfeca511 HHH-4118 warn if hibernate.hbm2ddl.auto has unrecognized value
bcd6185809 HHH-4910 The collection cache is evicted if a related collection element is inserted, removed or updated
489ee4a734 HHH-7943 Added support for ehcache and infinispan strategies and OSGi services.  Numerous ehcache and infinispan CL fixes.
b6d740d6d2 HHH-7910 Transaction timeout can cause non-threadsafe session access by reaper thread
2ff69d24c4 HHH-7872 - Improved L2 cache storage of "reference" data
06b0faaf57 HHH-7746 - Investigate alternative batch loading algorithms
3e69b7bd53 HHH-7725 - Make handling multi-table bulk HQL operations more pluggable
c9fd71fe57 HHH-7683 - Optimize performance of AbstractLazyInitializer.prepareForPossibleSpecialSpecjInitialization()
4ad49a02c9 HHH-7556 - Clean up packages
a385792178 HHH-7305 - NPE in LogicalConnectionImpl when multi tenancy is used without providing a release mode manually
244623cce9 HHH-6822 - Split notions of (1) "naming" a SessionFactory and (2) specifying a JNDI name to which to bind it
6c7379c38f HHH-6817 Logging of strings containing the percent character broken
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
182150769a HHH-6371 - Develop metamodel binding creation using a push approach
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
92ad3eed80 HHH-6297 remove legacy cache api
ba44ae26cb HHH-6110 : Integrate new metamodel into persisters
360317eedf HHH-6200 - Split org.hibernate.hql package into api/spi/internal
c930ebcd7d HHH-6191 - repackage org.hibernate.cache per api/spi/internal split
16e86687c9 HHH-6150 - JBoss AS7 integration work
62da5aa5bc HHH-6097 - Review log levels, especially related to i18n messages
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes
3ff0288da5 HHH-5697 - Support for multi-tenancy
815baf4348 HHH-6051 - Create a sessionfactory scoped ServiceRegistry
19791a6c7d HHH-6026 - Migrate bytecode provider integrations to api/spi/internal split
82d2ef4b1f HHH-6025 - Remove cglib dependencies
ad5f88c2d6 HHH-5961 : Contextual LOB creator is used when the JDBC driver does not support JDBC4 Connection.createBlob()
0816d00e59 HHH-5986 - Refactor org.hibernate.util package for spi/internal split
73e85ee761 HHH-5781 - Refactor code in org.hibernate.jdbc to spi/internal and remove obsolete code
08d9fe2117 HHH-5949 - Migrate, complete and integrate TransactionFactory as a service
7262276fa9 HHH-5778 : Wire in new batch code
b006a6c3c5 HHH-5765 : Refactor JDBCContext/ConnectionManager spi/impl and to use new proxies
fda684a5a6 HHH-5765 : remove dialect and connection provider from Settings
3ca8216c7c HHH-5765 : Wire in SQLExceptionHelper for converting SQLExceptions

Start block index: 53
End block index: 319
	public Settings buildSettings(Properties props) {
		Settings settings = new Settings();
		
		//SessionFactory name:
		
		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
		settings.setSessionFactoryName(sessionFactoryName);

		//JDBC and connection settings:

		ConnectionProvider connections = createConnectionProvider(props);
		settings.setConnectionProvider(connections);

		//Interrogate JDBC metadata

		String databaseName = null;
		int databaseMajorVersion = 0;
		boolean metaSupportsScrollable = false;
		boolean metaSupportsGetGeneratedKeys = false;
		boolean metaSupportsBatchUpdates = false;
		boolean metaReportsDDLCausesTxnCommit = false;
		boolean metaReportsDDLInTxnSupported = true;

		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
		// The need for it is intended to be alleviated with 3.3 developement, thus it is
		// not defined as an Environment constant...
		// it is used to control whether we should consult the JDBC metadata to determine
		// certain Settings default values; it is useful to *not* do this when the database
		// may not be available (mainly in tools usage).
		boolean useJdbcMetadata = PropertiesHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", props, true );
		if ( useJdbcMetadata ) {
			try {
				Connection conn = connections.getConnection();
				try {
					DatabaseMetaData meta = conn.getMetaData();
					databaseName = meta.getDatabaseProductName();
					databaseMajorVersion = getDatabaseMajorVersion(meta);
					log.info("RDBMS: " + databaseName + ", version: " + meta.getDatabaseProductVersion() );
					log.info("JDBC driver: " + meta.getDriverName() + ", version: " + meta.getDriverVersion() );

					metaSupportsScrollable = meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();

					if ( Environment.jvmSupportsGetGeneratedKeys() ) {
						try {
							Boolean result = (Boolean) DatabaseMetaData.class.getMethod("supportsGetGeneratedKeys", null)
								.invoke(meta, null);
							metaSupportsGetGeneratedKeys = result.booleanValue();
						}
						catch (AbstractMethodError ame) {
							metaSupportsGetGeneratedKeys = false;
						}
						catch (Exception e) {
							metaSupportsGetGeneratedKeys = false;
						}
					}

				}
				finally {
					connections.closeConnection(conn);
				}
			}
			catch (SQLException sqle) {
				log.warn("Could not obtain connection metadata", sqle);
			}
			catch (UnsupportedOperationException uoe) {
				// user supplied JDBC connections
			}
		}
		settings.setDataDefinitionImplicitCommit( metaReportsDDLCausesTxnCommit );
		settings.setDataDefinitionInTransactionSupported( metaReportsDDLInTxnSupported );


		//SQL Dialect:
		Dialect dialect = determineDialect( props, databaseName, databaseMajorVersion );
		settings.setDialect(dialect);
		
		//use dialect default properties
		final Properties properties = new Properties();
		properties.putAll( dialect.getDefaultProperties() );
		properties.putAll(props);
		
		// Transaction settings:
		
		TransactionFactory transactionFactory = createTransactionFactory(properties);
		settings.setTransactionFactory(transactionFactory);
		settings.setTransactionManagerLookup( createTransactionManagerLookup(properties) );

		boolean flushBeforeCompletion = PropertiesHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
		log.info("Automatic flush during beforeCompletion(): " + enabledDisabled(flushBeforeCompletion) );
		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);

		boolean autoCloseSession = PropertiesHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
		log.info("Automatic session close at end of transaction: " + enabledDisabled(autoCloseSession) );
		settings.setAutoCloseSessionEnabled(autoCloseSession);

		//JDBC and connection settings:

		int batchSize = PropertiesHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
		if ( !metaSupportsBatchUpdates ) batchSize = 0;
		if (batchSize>0) log.info("JDBC batch size: " + batchSize);
		settings.setJdbcBatchSize(batchSize);
		boolean jdbcBatchVersionedData = PropertiesHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
		if (batchSize>0) log.info("JDBC batch updates for versioned data: " + enabledDisabled(jdbcBatchVersionedData) );
		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
		settings.setBatcherFactory( createBatcherFactory(properties, batchSize) );
		
		boolean useScrollableResultSets = PropertiesHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, metaSupportsScrollable);
		log.info("Scrollable result sets: " + enabledDisabled(useScrollableResultSets) );
		settings.setScrollableResultSetsEnabled(useScrollableResultSets);

		boolean wrapResultSets = PropertiesHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
		log.debug( "Wrap result sets: " + enabledDisabled(wrapResultSets) );
		settings.setWrapResultSetsEnabled(wrapResultSets);

		boolean useGetGeneratedKeys = PropertiesHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, metaSupportsGetGeneratedKeys);
		log.info("JDBC3 getGeneratedKeys(): " + enabledDisabled(useGetGeneratedKeys) );
		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);

		Integer statementFetchSize = PropertiesHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
		if (statementFetchSize!=null) log.info("JDBC result set fetch size: " + statementFetchSize);
		settings.setJdbcFetchSize(statementFetchSize);

		String releaseModeName = PropertiesHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
		log.info( "Connection release mode: " + releaseModeName );
		ConnectionReleaseMode releaseMode;
		if ( "auto".equals(releaseModeName) ) {
			releaseMode = transactionFactory.getDefaultReleaseMode();
		}
		else {
			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT && !connections.supportsAggressiveRelease() ) {
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

		Integer maxFetchDepth = PropertiesHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
		if (maxFetchDepth!=null) log.info("Maximum outer join fetch depth: " + maxFetchDepth);
		settings.setMaximumFetchDepth(maxFetchDepth);
		int batchFetchSize = PropertiesHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
		log.info("Default batch fetch size: " + batchFetchSize);
		settings.setDefaultBatchFetchSize(batchFetchSize);

		boolean comments = PropertiesHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
		log.info( "Generate SQL with comments: " + enabledDisabled(comments) );
		settings.setCommentsEnabled(comments);
		
		boolean orderUpdates = PropertiesHelper.getBoolean(Environment.ORDER_UPDATES, properties);
		log.info( "Order SQL updates by primary key: " + enabledDisabled(orderUpdates) );
		settings.setOrderUpdatesEnabled(orderUpdates);

		boolean orderInserts = PropertiesHelper.getBoolean(Environment.ORDER_INSERTS, properties);
		log.info( "Order SQL inserts for batching: " + enabledDisabled( orderInserts ) );
		settings.setOrderInsertsEnabled( orderInserts );
		
		//Query parser settings:
		
		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );

		Map querySubstitutions = PropertiesHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
		log.info("Query language substitutions: " + querySubstitutions);
		settings.setQuerySubstitutions(querySubstitutions);

		boolean jpaqlCompliance = PropertiesHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
		settings.setStrictJPAQLCompliance( jpaqlCompliance );
		log.info( "JPA-QL strict compliance: " + enabledDisabled( jpaqlCompliance ) );
		
		// Second-level / query cache:

		boolean useSecondLevelCache = PropertiesHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
		log.info( "Second-level cache: " + enabledDisabled(useSecondLevelCache) );
		settings.setSecondLevelCacheEnabled(useSecondLevelCache);

		boolean useQueryCache = PropertiesHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
		log.info( "Query cache: " + enabledDisabled(useQueryCache) );
		settings.setQueryCacheEnabled(useQueryCache);

		// The cache provider is needed when we either have second-level cache enabled
		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );

		boolean useMinimalPuts = PropertiesHelper.getBoolean(
				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
		);
		log.info( "Optimize cache for minimal puts: " + enabledDisabled(useMinimalPuts) );
		settings.setMinimalPutsEnabled(useMinimalPuts);

		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
		if ( StringHelper.isEmpty(prefix) ) prefix=null;
		if (prefix!=null) log.info("Cache region prefix: "+ prefix);
		settings.setCacheRegionPrefix(prefix);

		boolean useStructuredCacheEntries = PropertiesHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
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

		boolean showSql = PropertiesHelper.getBoolean(Environment.SHOW_SQL, properties);
		if (showSql) log.info("Echoing all SQL to stdout");
		settings.setShowSqlEnabled(showSql);

		boolean formatSql = PropertiesHelper.getBoolean(Environment.FORMAT_SQL, properties);
		settings.setFormatSqlEnabled(formatSql);
		
		boolean useStatistics = PropertiesHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
		log.info( "Statistics: " + enabledDisabled(useStatistics) );
		settings.setStatisticsEnabled(useStatistics);
		
		boolean useIdentifierRollback = PropertiesHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
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

		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
		log.info( "Default entity-mode: " + defaultEntityMode );
		settings.setDefaultEntityMode( defaultEntityMode );

		boolean namedQueryChecking = PropertiesHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
		log.info( "Named query checking : " + enabledDisabled( namedQueryChecking ) );
		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );

//		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
//		log.info( "Bytecode provider name : " + provider );
//		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
//		settings.setBytecodeProvider( bytecodeProvider );

		return settings;

	}
