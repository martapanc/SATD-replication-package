diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 6a9b0ffa60..2f8d2794c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -698,1001 +698,1006 @@ public interface CoreMessageLogger extends BasicLogger {
 	@Message(value = "Query cache puts: %s", id = 215)
 	void queryCachePuts(long queryCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "RDMSOS2200Dialect version: 1.0", id = 218)
 	void rdmsOs2200Dialect();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from cache file: %s", id = 219)
 	void readingCachedMappings(File cachedFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from file: %s", id = 220)
 	void readingMappingsFromFile(String path);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from resource: %s", id = 221)
 	void readingMappingsFromResource(String resourceName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "read-only cache configured for mutable collection [%s]", id = 222)
 	void readOnlyCacheConfiguredForMutableCollection(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!",
 			id = 223)
 	void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace,
 											  String hibernateNamespace);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225)
 	void renamedProperty(Object propertyName,
 						 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Required a different provider: %s", id = 226)
 	void requiredDifferentProvider(String provider);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema export", id = 227)
 	void runningHbm2ddlSchemaExport();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema update", id = 228)
 	void runningHbm2ddlSchemaUpdate();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running schema validator", id = 229)
 	void runningSchemaValidator();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema export complete", id = 230)
 	void schemaExportComplete();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Schema export unsuccessful", id = 231)
 	void schemaExportUnsuccessful(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema update complete", id = 232)
 	void schemaUpdateComplete();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Scoping types to session factory %s after already scoped %s", id = 233)
 	void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor factory,
 														SessionFactoryImplementor factory2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Searching for mapping documents in jar: %s", id = 235)
 	void searchingForMappingDocuments(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache hits: %s", id = 237)
 	void secondLevelCacheHits(long secondLevelCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache misses: %s", id = 238)
 	void secondLevelCacheMisses(long secondLevelCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache puts: %s", id = 239)
 	void secondLevelCachePuts(long secondLevelCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Service properties: %s", id = 240)
 	void serviceProperties(Properties properties);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions closed: %s", id = 241)
 	void sessionsClosed(long sessionCloseCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions opened: %s", id = 242)
 	void sessionsOpened(long sessionOpenCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Setters of lazy classes cannot be final: %s.%s", id = 243)
 	void settersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244)
 	void sortAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Manipulation query [%s] resulted in [%s] split queries", id = 245)
 	void splitQueries(String sourceQuery,
 					  int length);
 
 //	@LogMessage(level = ERROR)
 //	@Message(value = "SQLException escaped proxy", id = 246)
 //	void sqlExceptionEscapedProxy(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "SQL Error: %s, SQLState: %s", id = 247)
 	void sqlWarning(int errorCode,
 					String sqlState);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting query cache at region: %s", id = 248)
 	void startingQueryCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting service at JNDI name: %s", id = 249)
 	void startingServiceAtJndiName(String boundName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting update timestamps cache at region: %s", id = 250)
 	void startingUpdateTimestampsCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Start time: %s", id = 251)
 	void startTime(long startTime);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements closed: %s", id = 252)
 	void statementsClosed(long closeStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements prepared: %s", id = 253)
 	void statementsPrepared(long prepareStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Stopping service", id = 255)
 	void stoppingService();
 
 	@LogMessage(level = INFO)
 	@Message(value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257)
 	void subResolverException(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Successful transactions: %s", id = 258)
 	void successfulTransactions(long committedTransactionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Synchronization [%s] was already registered", id = 259)
 	void synchronizationAlreadyRegistered(Synchronization synchronization);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception calling user Synchronization [%s] : %s", id = 260)
 	void synchronizationFailed(Synchronization synchronization,
 							   Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table found: %s", id = 261)
 	void tableFound(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table not found: %s", id = 262)
 	void tableNotFound(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "More than one table found: %s", id = 263)
 	void multipleTablesFound(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transactions: %s", id = 266)
 	void transactions(long transactionCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Transaction started on non-root session", id = 267)
 	void transactionStartedOnNonRootSession();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transaction strategy: %s", id = 268)
 	void transactionStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Type [%s] defined no registration keys; ignoring", id = 269)
 	void typeDefinedNoRegistrationKeys(BasicType type);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Type registration [%s] overrides previous : %s", id = 270)
 	void typeRegistrationOverridesPrevious(String key,
 										   Type old);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing Ejb3Configuration", id = 271)
 	void unableToAccessEjb3Configuration(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error while accessing session factory with JNDI name %s", id = 272)
 	void unableToAccessSessionFactory(String sfJNDIName,
 									  @Cause NamingException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error accessing type info result set : %s", id = 273)
 	void unableToAccessTypeInfoResultSet(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to apply constraints on DDL for %s", id = 274)
 	void unableToApplyConstraints(String className,
 								  @Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind Ejb3Configuration to JNDI", id = 276)
 	void unableToBindEjb3ConfigurationToJndi(@Cause JndiException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind factory to JNDI", id = 277)
 	void unableToBindFactoryToJndi(@Cause JndiException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not bind value '%s' to parameter: %s; %s", id = 278)
 	void unableToBindValueToParameter(String nullSafeToString,
 									  int index,
 									  String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to build enhancement metamodel for %s", id = 279)
 	void unableToBuildEnhancementMetamodel(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s",
 			id = 280)
 	void unableToBuildSessionFactoryUsingMBeanClasspath(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up callable statement", id = 281)
 	void unableToCleanUpCallableStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up prepared statement", id = 282)
 	void unableToCleanUpPreparedStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to cleanup temporary id table after use [%s]", id = 283)
 	void unableToCleanupTemporaryIdTable(Throwable t);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing connection", id = 284)
 	void unableToCloseConnection(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error closing InitialContext [%s]", id = 285)
 	void unableToCloseInitialContext(String string);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing input files: %s", id = 286)
 	void unableToCloseInputFiles(String name,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream", id = 287)
 	void unableToCloseInputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream for %s", id = 288)
 	void unableToCloseInputStreamForResource(String resourceName,
 											 @Cause IOException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to close iterator", id = 289)
 	void unableToCloseIterator(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close jar: %s", id = 290)
 	void unableToCloseJar(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing output file: %s", id = 291)
 	void unableToCloseOutputFile(String outputFile,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing output stream", id = 292)
 	void unableToCloseOutputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Problem closing pooled connection", id = 293)
 	void unableToClosePooledConnection(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session", id = 294)
 	void unableToCloseSession(@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session during rollback", id = 295)
 	void unableToCloseSessionDuringRollback(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing stream", id = 296)
 	void unableToCloseStream(@Cause IOException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close stream on hibernate.properties: %s", id = 297)
 	void unableToCloseStreamError(IOException error);
 
 	@Message(value = "JTA commit failed", id = 298)
 	String unableToCommitJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema update", id = 299)
 	void unableToCompleteSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema validation", id = 300)
 	void unableToCompleteSchemaValidation(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to configure SQLExceptionConverter : %s", id = 301)
 	void unableToConfigureSqlExceptionConverter(HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to construct current session context [%s]", id = 302)
 	void unableToConstructCurrentSessionContext(String impl,
 												@Cause Throwable e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303)
 	void unableToConstructSqlExceptionConverter(Throwable t);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not copy system properties, system properties will be ignored", id = 304)
 	void unableToCopySystemProperties();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not create proxy factory for:%s", id = 305)
 	void unableToCreateProxyFactory(String entityName,
 									@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error creating schema ", id = 306)
 	void unableToCreateSchema(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not deserialize cache file: %s : %s", id = 307)
 	void unableToDeserializeCache(String path,
 								  SerializationException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy cache: %s", id = 308)
 	void unableToDestroyCache(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy query cache: %s: %s", id = 309)
 	void unableToDestroyQueryCache(String region,
 								   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy update timestamps cache: %s: %s", id = 310)
 	void unableToDestroyUpdateTimestampsCache(String region,
 											  String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to determine lock mode value : %s -> %s", id = 311)
 	void unableToDetermineLockModeValue(String hintName,
 										Object value);
 
 	@Message(value = "Could not determine transaction status", id = 312)
 	String unableToDetermineTransactionStatus();
 
 	@Message(value = "Could not determine transaction status after commit", id = 313)
 	String unableToDetermineTransactionStatusAfterCommit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to drop temporary id table after use [%s]", id = 314)
 	void unableToDropTemporaryIdTable(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception executing batch [%s]", id = 315)
 	void unableToExecuteBatch(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error executing resolver [%s] : %s", id = 316)
 	void unableToExecuteResolver(DialectResolver abstractDialectResolver,
 								 String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318)
 	void unableToFindPersistenceXmlInClasspath();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not get database metadata", id = 319)
 	void unableToGetDatabaseMetadata(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320)
 	void unableToInstantiateConfiguredSchemaNameResolver(String resolverClassName,
 														 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to interpret specified optimizer [%s], falling back to noop", id = 321)
 	void unableToLocateCustomOptimizerClass(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322)
 	void unableToInstantiateOptimizer(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate UUID generation strategy class : %s", id = 325)
 	void unableToInstantiateUuidGenerationStrategy(Exception ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Cannot join transaction: do not override %s", id = 326)
 	void unableToJoinTransaction(String transactionStrategy);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error performing load command : %s", id = 327)
 	void unableToLoadCommand(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328)
 	void unableToLoadDerbyDriver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Problem loading properties from hibernate.properties", id = 329)
 	void unableToLoadProperties();
 
 	@Message(value = "Unable to locate config file: %s", id = 330)
 	String unableToLocateConfigFile(String path);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate configured schema name resolver class [%s] %s", id = 331)
 	void unableToLocateConfiguredSchemaNameResolver(String resolverClassName,
 													String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate MBeanServer on JMX service shutdown", id = 332)
 	void unableToLocateMBeanServer();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate requested UUID generation strategy class : %s", id = 334)
 	void unableToLocateUuidGenerationStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to log SQLWarnings : %s", id = 335)
 	void unableToLogSqlWarnings(SQLException sqle);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not log warnings", id = 336)
 	void unableToLogWarnings(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on PersistenceException: ", id = 337)
 	void unableToMarkForRollbackOnPersistenceException(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on TransientObjectException: ", id = 338)
 	void unableToMarkForRollbackOnTransientObjectException(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata: %s", id = 339)
 	void unableToObjectConnectionMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata: %s", id = 340)
 	void unableToObjectConnectionToQueryMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata : %s", id = 341)
 	void unableToObtainConnectionMetadata(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata : %s", id = 342)
 	void unableToObtainConnectionToQueryMetadata(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not obtain initial context", id = 343)
 	void unableToObtainInitialContext(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not parse the package-level metadata [%s]", id = 344)
 	void unableToParseMetadata(String packageName);
 
 	@Message(value = "JDBC commit failed", id = 345)
 	String unableToPerformJdbcCommit();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error during managed flush [%s]", id = 346)
 	void unableToPerformManagedFlush(String message);
 
 	@Message(value = "Unable to query java.sql.DatabaseMetaData", id = 347)
 	String unableToQueryDatabaseMetadata();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to read class: %s", id = 348)
 	void unableToReadClass(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not read column value from result set: %s; %s", id = 349)
 	void unableToReadColumnValueFromResultSet(String name,
 											  String message);
 
 	@Message(value = "Could not read a hi value - you need to populate the table: %s", id = 350)
 	String unableToReadHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not read or init a hi value", id = 351)
 	void unableToReadOrInitHiValue(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to release batch statement...", id = 352)
 	void unableToReleaseBatchStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not release a cache lock : %s", id = 353)
 	void unableToReleaseCacheLock(CacheException ce);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release initial context: %s", id = 354)
 	void unableToReleaseContext(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release created MBeanServer : %s", id = 355)
 	void unableToReleaseCreatedMBeanServer(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release isolated connection [%s]", id = 356)
 	void unableToReleaseIsolatedConnection(Throwable ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release type info result set", id = 357)
 	void unableToReleaseTypeInfoResultSet();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to erase previously added bag join fetch", id = 358)
 	void unableToRemoveBagJoinFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not resolve aggregate function [%s]; using standard definition", id = 359)
 	void unableToResolveAggregateFunction(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to resolve mapping file [%s]", id = 360)
 	void unableToResolveMappingFile(String xmlFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to retreive cache from JNDI [%s]: %s", id = 361)
 	void unableToRetrieveCache(String namespace,
 							   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to retrieve type info result set : %s", id = 362)
 	void unableToRetrieveTypeInfoResultSet(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback connection on exception [%s]", id = 363)
 	void unableToRollbackConnection(Exception ignore);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364)
 	void unableToRollbackIsolatedTransaction(Exception e,
 											 Exception ignore);
 
 	@Message(value = "JTA rollback failed", id = 365)
 	String unableToRollbackJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error running schema update", id = 366)
 	void unableToRunSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not set transaction to rollback only", id = 367)
 	void unableToSetTransactionToRollbackOnly(@Cause SystemException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while stopping service", id = 368)
 	void unableToStopHibernateService(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error stopping service [%s] : %s", id = 369)
 	void unableToStopService(Class class1,
 							 String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]",
 			id = 370)
 	void unableToSwitchToMethodUsingColumnIndex(Method method);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not synchronize database state with session: %s", id = 371)
 	void unableToSynchronizeDatabaseStateWithSession(HibernateException he);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not toggle autocommit", id = 372)
 	void unableToToggleAutoCommit(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to transform class: %s", id = 373)
 	void unableToTransformClass(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not unbind factory from JNDI", id = 374)
 	void unableToUnbindFactoryFromJndi(@Cause JndiException e);
 
 	@Message(value = "Could not update hi value in: %s", id = 375)
 	Object unableToUpdateHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not updateQuery hi value in: %s", id = 376)
 	void unableToUpdateQueryHiValue(String tableName,
 									@Cause SQLException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error wrapping result set", id = 377)
 	void unableToWrapResultSet(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported error writing cached file : %s: %s", id = 378)
 	void unableToWriteCachedFile(String path,
 								 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unexpected literal token type [%s] passed for numeric processing", id = 380)
 	void unexpectedLiteralTokenType(int type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JDBC driver did not return the expected number of row counts", id = 381)
 	void unexpectedRowCounts();
 
 	@LogMessage(level = WARN)
 	@Message(value = "unrecognized bytecode provider [%s], using javassist by default", id = 382)
 	void unknownBytecodeProvider(String providerName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383)
 	void unknownIngresVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Oracle major version [%s]", id = 384)
 	void unknownOracleVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385)
 	void unknownSqlServerVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet had no statement associated with it, but was not yet registered", id = 386)
 	void unregisteredResultSetWithoutStatement();
 
 	// Keep this at DEBUG level, rather than warn.  Numerous connection pool implementations can return a
 	// proxy/wrapper around the JDBC Statement, causing excessive logging here.  See HHH-8210.
 	@LogMessage(level = DEBUG)
 	@Message(value = "ResultSet's statement was not registered", id = 387)
 	void unregisteredStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 388)
 	void unsuccessful(String sql);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 389)
 	void unsuccessfulCreate(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390)
 	void unsupportedAfterStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391)
 	void unsupportedIngresVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392)
 	void unsupportedInitialValue(String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly",
 			id = 393)
 	void unsupportedMultiTableBulkHqlJpaql(int majorVersion,
 										   int minorVersion,
 										   int buildId);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394)
 	void unsupportedOracleVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395)
 	void unsupportedProperty(Object propertyName,
 							 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Updating schema", id = 396)
 	void updatingSchema();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using ASTQueryTranslatorFactory", id = 397)
 	void usingAstQueryTranslatorFactory();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398)
 	void usingDefaultIdGeneratorSegmentValue(String tableName,
 											 String segmentColumnName,
 											 String defaultToUse);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using default transaction strategy (direct JDBC transactions)", id = 399)
 	void usingDefaultTransactionStrategy();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using dialect: %s", id = 400)
 	void usingDialect(Dialect dialect);
 
 	@LogMessage(level = INFO)
 	@Message(value = "using driver [%s] at URL [%s]", id = 401)
 	void usingDriver(String driverClassName,
 					 String url);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using Hibernate built-in connection pool (not for production use!)", id = 402)
 	void usingHibernateBuiltInConnectionPool();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404)
 	void usingOldDtd();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using bytecode reflection optimizer", id = 406)
 	void usingReflectionOptimizer();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using java.io streams to persist binary types", id = 407)
 	void usingStreams();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408)
 	void usingTimestampWorkaround();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead",
 			id = 409)
 	void usingUuidHexGenerator(String name,
 							   String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Validator not found: ignoring", id = 410)
 	void validatorNotFound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Core {%s}", id = 412)
 	void version(String versionString);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warnings creating temp table : %s", id = 413)
 	void warningsCreatingTempTable(SQLWarning warning);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.",
 			id = 414)
 	void willNotRegisterListeners();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Write locks via update not supported for non-versioned entities [%s]", id = 416)
 	void writeLocksNotSupported(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Writing generated schema to file: %s", id = 417)
 	void writingGeneratedSchemaToFile(String outputFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Adding override for %s: %s", id = 418)
 	void addingOverrideFor(String name,
 						   String name2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s",
 			id = 419)
 	void resolvedSqlTypeDescriptorForDifferentSqlCode(String name,
 													  String valueOf,
 													  String name2,
 													  String valueOf2);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing un-released batch", id = 420)
 	void closingUnreleasedBatch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as %s is true", id = 421)
 	void disablingContextualLOBCreation(String nonContextualLobCreation);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as connection was null", id = 422)
 	void disablingContextualLOBCreationSinceConnectionNull();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4",
 			id = 423)
 	void disablingContextualLOBCreationSinceOldJdbcVersion(int jdbcMajorVersion);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424)
 	void disablingContextualLOBCreationSinceCreateClobFailed(Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not close session; swallowing exception[%s] as transaction completed", id = 425)
 	void unableToCloseSessionButSwallowingError(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "You should set hibernate.transaction.jta.platform if cache is enabled", id = 426)
 	void setManagerLookupClass();
 
 //	@LogMessage(level = WARN)
 //	@Message(value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427)
 //	void deprecatedTransactionManagerStrategy(String name,
 //											  String transactionManagerStrategy,
 //											  String name2,
 //											  String jtaPlatform);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting",
 			id = 428)
 	void legacyTransactionManagerStrategy(String name,
 										  String jtaPlatform);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Setting entity-identifier value binding where one already existed : %s.", id = 429)
 	void entityIdentifierValueBindingExists(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead",
 			id = 430)
 	void deprecatedDerbyDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to determine H2 database version, certain features may not work", id = 431)
 	void undeterminedH2Version();
 
 	@LogMessage(level = WARN)
 	@Message(value = "There were not column names specified for index %s on table %s", id = 432)
 	void noColumnsSpecifiedForIndex(String indexName, String tableName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache puts: %s", id = 433)
 	void timestampCachePuts(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache hits: %s", id = 434)
 	void timestampCacheHits(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache misses: %s", id = 435)
 	void timestampCacheMisses(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity manager factory name (%s) is already registered.  If entity manager will be clustered "+
 			"or passivated, specify a unique value for property '%s'", id = 436)
 	void entityManagerFactoryAlreadyRegistered(String emfName, String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n" +
 			"\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)" , id = 437)
 	void cannotResolveNonNullableTransientDependencies(String transientEntityString,
 													   Set<String> dependentEntityStrings,
 													   Set<String> nonNullableAssociationPaths);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache puts: %s", id = 438)
 	void naturalIdCachePuts(long naturalIdCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache hits: %s", id = 439)
 	void naturalIdCacheHits(long naturalIdCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache misses: %s", id = 440)
 	void naturalIdCacheMisses(long naturalIdCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Max NaturalId query time: %sms", id = 441)
 	void naturalIdMaxQueryTime(long naturalIdQueryExecutionMaxTime);
 	
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId queries executed to database: %s", id = 442)
 	void naturalIdQueriesExecuted(long naturalIdQueriesExecutionCount);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Dialect [%s] limits the number of elements in an IN predicate to %s entries.  " +
 					"However, the given parameter list [%s] contained %s entries, which will likely cause failures " +
 					"to execute the query in the database",
 			id = 443
 	)
 	void tooManyInExpressions(String dialectName, int limit, String paramName, int size);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Encountered request for locking however dialect reports that database prefers locking be done in a " +
 					"separate select (follow-on locking); results will be locked after initial query executes",
 			id = 444
 	)
 	void usingFollowOnLocking();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Alias-specific lock modes requested, which is not currently supported with follow-on locking; " +
 					"all acquired locks will be [%s]",
 			id = 445
 	)
 	void aliasSpecificLockingWithFollowOnLocking(LockMode lockMode);
 
 	/**
 	 * @see org.hibernate.internal.log.DeprecationLogger#logDeprecationOfEmbedXmlSupport()
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been " +
 					"removed, embed-xml attributes are no longer supported and should be removed from mappings.",
 			id = 446
 	)
 	void embedXmlAttributesNoLongerSupported();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead",
 			id = 447
 	)
 	void explicitSkipLockedLockCombo();
 
 	@LogMessage(level = INFO)
 	@Message( value = "'javax.persistence.validation.mode' named multiple values : %s", id = 448 )
 	void multipleValidationModes(String modes);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 449,
 			value = "@Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName " +
 					"using 'key'/'value' as required by spec; attempting to DoTheRightThing"
 	)
 	void nonCompliantMapConversion(String collectionRole);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 450,
 			value = "Encountered request for Service by non-primary service role [%s -> %s]; please update usage"
 	)
 	void alternateServiceRole(String requestedRole, String targetRole);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 451,
 			value = "Transaction afterCompletion called by a background thread; " +
 					"delaying afterCompletion processing until the original thread can handle it. [status=%s]"
 	)
 	void rollbackFromBackgroundThread(int status);
 	
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while loading a class or resource found during scanning", id = 452)
 	void unableToLoadScannedClassOrResource(@Cause Exception e);
 	
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while discovering OSGi service implementations : %s", id = 453)
 	void unableToDiscoverOsgiService(String service, @Cause Exception e);
 
 	/**
 	 * @deprecated Use {@link org.hibernate.internal.log.DeprecationLogger#deprecatedManyToManyOuterJoin} instead
 	 */
 	@Deprecated
 	@LogMessage(level = WARN)
 	@Message(value = "The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id = 454)
 	void deprecatedManyToManyOuterJoin();
 
 	/**
 	 * @deprecated Use {@link org.hibernate.internal.log.DeprecationLogger#deprecatedManyToManyFetch} instead
 	 */
 	@Deprecated
 	@LogMessage(level = WARN)
 	@Message(value = "The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id = 455)
 	void deprecatedManyToManyFetch();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Named parameters are used for a callable statement, but database metadata indicates named parameters are not supported.", id = 456)
 	void unsupportedNamedParameters();
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 457,
 			value = "Joined inheritance hierarchy [%1$s] defined explicit @DiscriminatorColumn.  Legacy Hibernate behavior " +
 					"was to ignore the @DiscriminatorColumn.  However, as part of issue HHH-6911 we now apply the " +
 					"explicit @DiscriminatorColumn.  If you would prefer the legacy behavior, enable the `%2$s` setting " +
 					"(%2$s=true)"
 	)
 	void applyingExplicitDiscriminatorColumnForJoined(String className, String overrideSetting);
 	
 	// 458-466 reserved for use by master (ORM 5.0.0)
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Creating pooled optimizer (lo) with [incrementSize=%s; returnClass=%s]", id = 467)
 	void creatingPooledLoOptimizer(int incrementSize, String name);
+
+	@LogMessage(level = WARN)
+	@Message(value = "Unable to interpret type [%s] as an AttributeConverter due to an exception : %s", id = 468)
+	void logBadHbmAttributeConverterType(String type, String exceptionMessage);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 5d4df55199..9c11633d0b 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,595 +1,626 @@
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
 
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
+import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
+import org.hibernate.internal.CoreLogging;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
-import org.jboss.logging.Logger;
-
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
-	private static final Logger log = Logger.getLogger( SimpleValue.class );
+	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final MetadataImplementor metadata;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	public SimpleValue(MetadataImplementor metadata, Table table) {
 		this( metadata );
 		this.table = table;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
+	@Override
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
-	
+
+	@Override
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
+	@Override
 	public int getColumnSpan() {
 		return columns.size();
 	}
+
+	@Override
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
+
 	public List getConstraintColumns() {
 		return columns;
 	}
+
 	public String getTypeName() {
 		return typeName;
 	}
-	public void setTypeName(String type) {
-		this.typeName = type;
+
+	public void setTypeName(String typeName) {
+		if ( typeName != null && typeName.startsWith( AttributeConverterTypeAdapter.NAME_PREFIX ) ) {
+			final String converterClassName = typeName.substring( AttributeConverterTypeAdapter.NAME_PREFIX.length() );
+			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
+					.getServiceRegistry()
+					.getService( ClassLoaderService.class );
+			try {
+				final Class<AttributeConverter> converterClass = cls.classForName( converterClassName );
+				attributeConverterDefinition = new AttributeConverterDefinition( converterClass.newInstance(), false );
+				return;
+			}
+			catch (Exception e) {
+				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
+			}
+		}
+
+		this.typeName = typeName;
 	}
+
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
+	@Override
 	public void createForeignKey() throws MappingException {}
 
+	@Override
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
+	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		final ConfigurationService cs = metadata.getMetadataBuildingOptions().getServiceRegistry()
 				.getService( ConfigurationService.class );
 
 		params.put(
 				AvailableSettings.PREFER_POOLED_VALUES_LO,
 				cs.getSetting( AvailableSettings.PREFER_POOLED_VALUES_LO, StandardConverters.BOOLEAN, false )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Object selectable = itr.next();
 			if ( selectable instanceof Formula ) {
 				// if there are *any* formulas, then the Value overall is
 				// considered nullable
 				return true;
 			}
 			else if ( !( (Column) selectable ).isNullable() ) {
 				// if there is a single non-nullable column, the Value
 				// overall is considered non-nullable.
 				return false;
 			}
 		}
 		// nullable by default
 		return true;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( attributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "Attribute types for a dynamic entity must be explicitly specified: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
 		final Class entityAttributeJavaType = attributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = attributeConverterDefinition.getDatabaseColumnType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
-		final String name = String.format(
+		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDefinition.getAttributeConverter().getClass().getName();
+		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
+				description,
 				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaType,
 				databaseColumnJavaType,
 				entityAttributeJavaTypeDescriptor
 		);
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition attributeConverterDefinition) {
 		this.attributeConverterDefinition = attributeConverterDefinition;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				Selectable column = columns.get(i);
 				if (column instanceof Column){
 					columnsNames[i] = ((Column) column).getName();
 				}
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							ReflectHelper.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, cnfe );
 		}
 	}
 
 	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
index cb0ad2e23e..636be5f5e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterTypeAdapter.java
@@ -1,80 +1,90 @@
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
 package org.hibernate.type.descriptor.converter;
 
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 import org.jboss.logging.Logger;
 
 /**
  * Adapts the Hibernate Type contract to incorporate JPA AttributeConverter calls.
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterTypeAdapter<T> extends AbstractSingleColumnStandardBasicType<T> {
 	private static final Logger log = Logger.getLogger( AttributeConverterTypeAdapter.class );
 
+	public static final String NAME_PREFIX = "converted::";
+
 	private final String name;
+	private final String description;
 
 	private final Class modelType;
 	private final Class jdbcType;
 	private final AttributeConverter<? extends T,?> attributeConverter;
 
 	public AttributeConverterTypeAdapter(
 			String name,
+			String description,
 			AttributeConverter<? extends T,?> attributeConverter,
 			SqlTypeDescriptor sqlTypeDescriptorAdapter,
 			Class modelType,
 			Class jdbcType,
 			JavaTypeDescriptor<T> entityAttributeJavaTypeDescriptor) {
 		super( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor );
 		this.name = name;
+		this.description = description;
 		this.modelType = modelType;
 		this.jdbcType = jdbcType;
 		this.attributeConverter = attributeConverter;
 
 		log.debug( "Created AttributeConverterTypeAdapter -> " + name );
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	public Class getModelType() {
 		return modelType;
 	}
 
 	public Class getJdbcType() {
 		return jdbcType;
 	}
 
 	public AttributeConverter<? extends T,?> getAttributeConverter() {
 		return attributeConverter;
 	}
+
+	@Override
+	public String toString() {
+		return description;
+	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java
new file mode 100644
index 0000000000..a5cfc1f5cf
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java
@@ -0,0 +1,70 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.envers.test.entities.converter;
+
+import org.hibernate.Session;
+import org.hibernate.SessionFactory;
+import org.hibernate.boot.Metadata;
+import org.hibernate.boot.MetadataSources;
+import org.hibernate.boot.internal.MetadataImpl;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.envers.test.AbstractEnversTest;
+import org.hibernate.mapping.PersistentClass;
+
+import org.hibernate.testing.TestForIssue;
+import org.junit.Test;
+
+import static org.junit.Assert.assertNotNull;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicModelingTest extends AbstractEnversTest {
+	@Test
+	@TestForIssue( jiraKey = "HHH-9042" )
+	public void testMetamodelBuilding() {
+		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		try {
+			Metadata metadata = new MetadataSources( ssr )
+					.addAttributeConverter( SexConverter.class )
+					.addAnnotatedClass( Person.class )
+					.buildMetadata();
+
+			( (MetadataImpl) metadata ).validate();
+
+			PersistentClass personBinding = metadata.getEntityBinding( Person.class.getName() );
+			assertNotNull( personBinding );
+
+			PersistentClass personAuditBinding = metadata.getEntityBinding( Person.class.getName() + "_AUD" );
+			assertNotNull( personAuditBinding );
+		}
+		finally {
+			StandardServiceRegistryBuilder.destroy( ssr );
+		}
+	}
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Person.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Person.java
new file mode 100644
index 0000000000..1f506f1e52
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Person.java
@@ -0,0 +1,47 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.envers.test.entities.converter;
+
+import javax.persistence.Convert;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+
+import org.hibernate.annotations.GenericGenerator;
+import org.hibernate.envers.Audited;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+@Audited
+public class Person {
+	@Id
+	@GeneratedValue( generator = "increment" )
+	@GenericGenerator( name = "increment", strategy="increment" )
+	private Long id;
+
+	@Convert(converter = SexConverter.class)
+	private Sex sex;
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Sex.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Sex.java
new file mode 100644
index 0000000000..1cb998973c
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/Sex.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.envers.test.entities.converter;
+
+/**
+ * @author Steve Ebersole
+ */
+public enum Sex {
+	MALE,
+	FEMALE;
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/SexConverter.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/SexConverter.java
new file mode 100644
index 0000000000..46b691981e
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/SexConverter.java
@@ -0,0 +1,68 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.envers.test.entities.converter;
+
+import javax.persistence.AttributeConverter;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SexConverter implements AttributeConverter<Sex, String> {
+
+	@Override
+	public String convertToDatabaseColumn(Sex attribute) {
+		if (attribute == null) {
+			return null;
+		}
+
+		switch (attribute) {
+			case MALE: {
+				return "M";
+			}
+			case FEMALE: {
+				return "F";
+			}
+			default: {
+				throw new IllegalArgumentException( "Unexpected Sex model value [" + attribute + "]" );
+			}
+		}
+	}
+
+	@Override
+	public Sex convertToEntityAttribute(String dbData) {
+		if (dbData == null) {
+			return null;
+		}
+
+		if ( "M".equals( dbData ) ) {
+			return Sex.MALE;
+		}
+		else if ( "F".equals( dbData ) ) {
+			return Sex.FEMALE;
+		}
+
+		throw new IllegalArgumentException( "Unexpected Sex db value [" + dbData + "]" );
+	}
+
+}
