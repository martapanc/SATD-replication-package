3/report.java
Satd-method: public Settings buildSettings(Properties props) {
********************************************
********************************************
3/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildSettings(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isMinimalPutsEnabledByDefault
* setDefaultEntityMode
* setDefaultSchemaName
* setQueryTranslatorFactory
* invoke
* getMethod
* setSessionFactoryName
* setDefaultCatalogName
* setCommentsEnabled
* getDriverVersion
* setFlushBeforeCompletionEnabled
* supportsBatchUpdates
* setGetGeneratedKeysEnabled
* setQueryCacheEnabled
* closeConnection
* setSecondLevelCacheEnabled
* getInt
* setStructuredCacheEntriesEnabled
* setDataDefinitionImplicitCommit
* supportsResultSetType
* getDefaultProperties
* getMetaData
* setMaximumFetchDepth
* setConnectionReleaseMode
* setAutoDropSchema
* setDefaultBatchFetchSize
* getRegionFactory
* buildSQLExceptionConverter
* getDatabaseProductVersion
* dataDefinitionIgnoredInTransactions
* setTransactionFactory
* getBoolean
* setStatisticsEnabled
* getDriverName
* setTransactionManagerLookup
* setWrapResultSetsEnabled
* setRegionFactory
* toMap
* setShowSqlEnabled
* jvmSupportsGetGeneratedKeys
* setDialect
* setMinimalPutsEnabled
* setStrictJPAQLCompliance
* setNamedQueryStartupCheckingEnabled
* getInteger
* setFormatSqlEnabled
* setScrollableResultSetsEnabled
* setOrderInsertsEnabled
* setBytecodeProvider
* setAutoCreateSchema
* setAutoCloseSessionEnabled
* setIdentifierRollbackEnabled
* info
* setJdbcBatchVersionedData
* setJdbcFetchSize
* setCacheRegionPrefix
* supportsAggressiveRelease
* warn
* setConnectionProvider
* setJdbcBatchSize
* booleanValue
* setAutoValidateSchema
* setDataDefinitionInTransactionSupported
* dataDefinitionCausesTransactionCommit
* setQueryCacheFactory
* getConnection
* buildMinimalSQLExceptionConverter
* getDatabaseProductName
* putAll
* getDefaultReleaseMode
* setOrderUpdatesEnabled
* getProperty
* debug
* setAutoUpdateSchema
* getString
* parse
* setBatcherFactory
* setQuerySubstitutions
* setSQLExceptionConverter
********************************************
********************************************
3/Between/ HHH-5765  88543c7a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public Settings buildSettings(Properties props) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildSettings(
-		Settings settings = buildSettings( copy );
+		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ).getConnectionProvider() );
-	public Settings buildSettings() {
+	public Settings buildSettings(ConnectionProvider connectionProvider) {
-	public Settings buildSettings(Properties props) throws HibernateException {
+	public Settings buildSettings(Properties props, ConnectionProvider connectionProvider) throws HibernateException {
-		final Settings settings = settingsFactory.buildSettings( props );
+		final Settings settings = settingsFactory.buildSettings( props, connectionProvider );
-	public Settings buildSettings(Properties props) {
+	public Settings buildSettings(Properties props, ConnectionProvider connections) {
-	public Settings buildSettings() throws HibernateException {
+	public Settings buildSettings(ConnectionProvider connectionProvider) throws HibernateException {
-			return settingsFactory.buildSettings( cfg.getProperties() );
+			return settingsFactory.buildSettings( cfg.getProperties(), connectionProvider );
-      Settings settings = cfg.buildSettings();
+      Settings settings = cfg.buildSettings( connectionProvider );

Lines added containing method: 8. Lines removed containing method: 8. Tot = 16
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isMinimalPutsEnabledByDefault
* setDefaultEntityMode
* setDefaultSchemaName
* setQueryTranslatorFactory
* invoke
* getMethod
* setSessionFactoryName
* setDefaultCatalogName
* setCommentsEnabled
* getDriverVersion
* setFlushBeforeCompletionEnabled
* supportsBatchUpdates
* setGetGeneratedKeysEnabled
* setQueryCacheEnabled
* closeConnection
* setSecondLevelCacheEnabled
* getInt
* setStructuredCacheEntriesEnabled
* setDataDefinitionImplicitCommit
* supportsResultSetType
* getDefaultProperties
* getMetaData
* setMaximumFetchDepth
* setConnectionReleaseMode
* setAutoDropSchema
* setDefaultBatchFetchSize
* getRegionFactory
* buildSQLExceptionConverter
* getDatabaseProductVersion
* dataDefinitionIgnoredInTransactions
* setTransactionFactory
* getBoolean
* setStatisticsEnabled
* getDriverName
* setTransactionManagerLookup
* setWrapResultSetsEnabled
* setRegionFactory
* toMap
* setShowSqlEnabled
* jvmSupportsGetGeneratedKeys
* setDialect
* setMinimalPutsEnabled
* setStrictJPAQLCompliance
* setNamedQueryStartupCheckingEnabled
* getInteger
* setFormatSqlEnabled
* setScrollableResultSetsEnabled
* setOrderInsertsEnabled
* setBytecodeProvider
* setAutoCreateSchema
* setAutoCloseSessionEnabled
* setIdentifierRollbackEnabled
* info
* setJdbcBatchVersionedData
* setJdbcFetchSize
* setCacheRegionPrefix
* supportsAggressiveRelease
* warn
* setConnectionProvider
* setJdbcBatchSize
* booleanValue
* setAutoValidateSchema
* setDataDefinitionInTransactionSupported
* dataDefinitionCausesTransactionCommit
* setQueryCacheFactory
* getConnection
* buildMinimalSQLExceptionConverter
* getDatabaseProductName
* putAll
* getDefaultReleaseMode
* setOrderUpdatesEnabled
* getProperty
* debug
* setAutoUpdateSchema
* getString
* parse
* setBatcherFactory
* setQuerySubstitutions
* setSQLExceptionConverter
—————————
Method found in diff:	public final void setCommentsEnabled(String commentsEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setFlushBeforeCompletionEnabled(String enabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setGetGeneratedKeysEnabled(String getGeneratedKeysEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setQueryCacheEnabled(String queryCacheEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public void closeConnection(Connection conn) throws SQLException;
-	public void closeConnection(Connection conn) throws SQLException;

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public final void setSecondLevelCacheEnabled(String secondLevelCacheEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setMaximumFetchDepth(String maximumFetchDepth) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RegionFactory getRegionFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setShowSqlEnabled(String showSqlEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean jvmSupportsGetGeneratedKeys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setDialect(String dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setMinimalPutsEnabled(String minimalPutsEnabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setAutoCloseSessionEnabled(String enabled) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setJdbcFetchSize(String jdbcFetchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setCacheRegionPrefix(String cacheRegionPrefix) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public boolean supportsAggressiveRelease();
-	public boolean supportsAggressiveRelease();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public final void setJdbcBatchSize(String jdbcBatchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public Connection getConnection() throws SQLException;
-	public Connection getConnection() throws SQLException;

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void setQuerySubstitutions(String querySubstitutions) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
3/Between/ HHH-5765  91d44442_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildSettings(
-		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ).getConnectionProvider() );
+		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ) );
-	public Settings buildSettings(ConnectionProvider connectionProvider) {
+	public Settings buildSettings(JdbcServices jdbcServices) {
-	public Settings buildSettings(Properties props, ConnectionProvider connectionProvider) throws HibernateException {
+	public Settings buildSettings(Properties props, JdbcServices jdbcServices) throws HibernateException {
-		final Settings settings = settingsFactory.buildSettings( props, connectionProvider );
+		final Settings settings = settingsFactory.buildSettings( props, jdbcServices );
-	public Settings buildSettings(Properties props, ConnectionProvider connections) {
+	public Settings buildSettings(Properties props, JdbcServices jdbcServices) {
-      Settings settings = cfg.buildSettings( connectionProvider );
+      Settings settings = cfg.buildSettings( jdbcServices );

Lines added containing method: 6. Lines removed containing method: 6. Tot = 12
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isMinimalPutsEnabledByDefault
* setDefaultEntityMode
* setDefaultSchemaName
* setQueryTranslatorFactory
* invoke
* getMethod
* setSessionFactoryName
* setDefaultCatalogName
* setCommentsEnabled
* getDriverVersion
* setFlushBeforeCompletionEnabled
* supportsBatchUpdates
* setGetGeneratedKeysEnabled
* setQueryCacheEnabled
* closeConnection
* setSecondLevelCacheEnabled
* getInt
* setStructuredCacheEntriesEnabled
* setDataDefinitionImplicitCommit
* supportsResultSetType
* getDefaultProperties
* getMetaData
* setMaximumFetchDepth
* setConnectionReleaseMode
* setAutoDropSchema
* setDefaultBatchFetchSize
* getRegionFactory
* buildSQLExceptionConverter
* getDatabaseProductVersion
* dataDefinitionIgnoredInTransactions
* setTransactionFactory
* getBoolean
* setStatisticsEnabled
* getDriverName
* setTransactionManagerLookup
* setWrapResultSetsEnabled
* setRegionFactory
* toMap
* setShowSqlEnabled
* jvmSupportsGetGeneratedKeys
* setDialect
* setMinimalPutsEnabled
* setStrictJPAQLCompliance
* setNamedQueryStartupCheckingEnabled
* getInteger
* setFormatSqlEnabled
* setScrollableResultSetsEnabled
* setOrderInsertsEnabled
* setBytecodeProvider
* setAutoCreateSchema
* setAutoCloseSessionEnabled
* setIdentifierRollbackEnabled
* info
* setJdbcBatchVersionedData
* setJdbcFetchSize
* setCacheRegionPrefix
* supportsAggressiveRelease
* warn
* setConnectionProvider
* setJdbcBatchSize
* booleanValue
* setAutoValidateSchema
* setDataDefinitionInTransactionSupported
* dataDefinitionCausesTransactionCommit
* setQueryCacheFactory
* getConnection
* buildMinimalSQLExceptionConverter
* getDatabaseProductName
* putAll
* getDefaultReleaseMode
* setOrderUpdatesEnabled
* getProperty
* debug
* setAutoUpdateSchema
* getString
* parse
* setBatcherFactory
* setQuerySubstitutions
* setSQLExceptionConverter
—————————
Method found in diff:	public static boolean jvmSupportsGetGeneratedKeys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
