50/report.java
Satd-method: public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
********************************************
********************************************
50/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
********************************************
********************************************
50/Between/ HHH-5697  fe8c7183_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare(boolean needsAutoCommit) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-5765  3ca8216c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare(Optimizer optimizer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CallableStatement prepareCallableStatement(String sql)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PreparedStatement prepareStatement(String sql)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PreparedStatement prepareBatchStatement(String sql)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CallableStatement prepareBatchCallableStatement(String sql)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch(SQLException sqle) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement(PreparedStatement ps) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Batcher getBatcher() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-5765  b006a6c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare(SQLWarning warning) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException {
+	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException {
+		executeBatch();
+		log.trace("preparing callable statement");
+		return CallableStatement.class.cast(
+				proxiedConnection.prepareStatement( getSQL( sql ) )
+		);
+	}

Lines added: 7. Lines removed: 0. Tot = 7
—————————
Method found in diff:	+	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
+	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable)
+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable)

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	-	public CallableStatement prepareBatchCallableStatement(String sql)
-	public CallableStatement prepareBatchCallableStatement(String sql)

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	private void addToBatch(Integer batchNumber, EntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public void abortBatch(SQLException sqle) {
+	public void abortBatch(SQLException sqle) {
+		getBatcher().abortBatch( sqle );
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	-	public void closeStatement(PreparedStatement ps) throws SQLException {
-	public void closeStatement(PreparedStatement ps) throws SQLException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Batcher getBatcher();
-	public Batcher getBatcher();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-5778  7262276f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public CallableStatement prepareCallableStatement(String sql) {
+		executeBatch();
-		return CallableStatement.class.cast( prepareStatement( sql, true, true ) );
-	}
-
-	public PreparedStatement prepareStatement(String sql, final boolean isCallable, boolean forceExecuteBatch) {
-		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
-			public PreparedStatement doPrepare() throws SQLException {
-				return prepareStatementInternal( getSqlToPrepare(), isCallable );
-			}
-		};
-		return prepareStatement( statementPreparer, forceExecuteBatch );
-	}
-
-	private PreparedStatement prepareStatementInternal(String sql, boolean isCallable) throws SQLException {
-		return isCallable ?
-				proxiedConnection.prepareCall( sql ) :
-				proxiedConnection.prepareStatement( sql );
-	}
-
-	private PreparedStatement prepareScrollableStatementInternal(String sql,
-																 ScrollMode scrollMode,
-																 boolean isCallable) throws SQLException {
-		return isCallable ?
-				proxiedConnection.prepareCall(
-						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-				) :
-				proxiedConnection.prepareStatement(
-						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-				);
+		return CallableStatement.class.cast( statementPreparer.prepareStatement( getSQL( sql ), true ) );

Lines added: 2. Lines removed: 28. Tot = 30
—————————
Method found in diff:	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) {
-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public void addToBatch(Expectation expectation) {
-	public void addToBatch(Expectation expectation) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public void abortBatch(SQLException sqle) {
-	public void abortBatch(SQLException sqle) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	private Batcher getBatcher() {
-	private Batcher getBatcher() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
50/Between/ HHH-5781  73e85ee7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare(Optimizer optimizer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PreparedStatement prepareStatement(String sql) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-5949  08d9fe21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare(SQLWarning warning) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public CallableStatement prepareCallableStatement(String sql) {
-	public CallableStatement prepareCallableStatement(String sql) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)
-	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public PreparedStatement prepareBatchStatement(Object key, String sql, boolean isCallable) {
-	public PreparedStatement prepareBatchStatement(Object key, String sql, boolean isCallable) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	private void addToBatch(Integer batchNumber, EntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public void abortBatch() {
-	public void abortBatch() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public boolean entryExists(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void prepare(Properties properties) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIdentifier(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {}

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {}

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean canBeBatched() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator entries(CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getElement(Object entry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public boolean entryExists(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void prepare(Properties properties) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void addToBatch(Integer batchNumber, EntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-		public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {
-		public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-		public boolean canBeBatched() {
-		public boolean canBeBatched() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Iterator entries(CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getElement(Object entry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {
-	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	private void prepare() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6191  c930ebcd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	protected void prepare(Properties properties) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6192  36ba1bca_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public boolean entryExists(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIdentifier(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator entries(CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getElement(Object entry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public boolean entryExists(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepare(SQLWarning warning) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void addToBatch(Integer batchNumber, EntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIdentifier(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {}

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String collectionInfoString(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean canBeBatched() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator entries(CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getElement(Object entry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate() throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6199  a806626a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public void prepare( SQLWarning warning );

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
50/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
recreate(
-							//TODO: copy/paste from recreate()
+							// TODO: copy/paste from recreate()

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* entryExists
* prepare
* prepareCallableStatement
* getIndex
* prepareStatement
* prepareBatchStatement
* prepareBatchCallableStatement
* addToBatch
* getIdentifier
* abortBatch
* preInsert
* afterRowInsert
* debug
* collectionInfoString
* closeStatement
* verifyOutcome
* canBeBatched
* entries
* getBatcher
* getElement
* appropriateExpectation
* isDebugEnabled
* executeUpdate
—————————
Method found in diff:	public boolean entryExists(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepare( SQLWarning warning );

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addToBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator entries(CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getElement(Object entry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
