51/report.java
Satd-method: public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
********************************************
********************************************
51/After/ HHH-10073 1376b12c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public static ModelBinder prepare(MetadataBuildingContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getIndex() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-10133 35712181_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Index getIndex(String indexName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void insertRows(
+	void insertRows(
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void insertRows(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)

Lines added containing method: 4. Lines removed containing method: 4. Tot = 8
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public static ManagedResources prepare(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private PreparedStatement prepareStatement(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void addToBatch(Integer batchNumber, AbstractEntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private Serializable getIdentifier(EventSource session, Object obj) {
-				id = session.getSessionFactory().getClassMetadata( obj.getClass() )
-						.getIdentifier( obj, session );
+				id = session.getSessionFactory().getMetamodel().entityPersister( obj.getClass() ).getIdentifier( obj, session );

Lines added: 1. Lines removed: 2. Tot = 3
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String collectionInfoString( 

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
Method found in diff:	-	public int executeUpdate();
-	public int executeUpdate();

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
51/After/ HHH-10874 253820a2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-1775 7cecc68f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-2394  05dcc209_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-2394  dbff4c18_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-4394  d51a0d0c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-4910  bcd61858_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-5393  c893577e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-5732  bdca6dc1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(
+	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+		super.insertRows( collection, id, session );

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-6791  bec88716_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {
-		return new Integer(i);
+		return i;

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public Object getIdentifier(Object entry, int i) {
-		return identifiers.get( new Integer(i) );
+		return identifiers.get( i );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {
-			Integer loc = new Integer(i++);
+			Integer loc = i++;

Lines added: 1. Lines removed: 1. Tot = 2
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
Method found in diff:	public int executeUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-6817  6c7379c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public void addToBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-7359  30ea167c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Object getIdentifier(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public static String collectionInfoString( 
+	public static String collectionInfoString( 

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
51/After/ HHH-7545  5327ac53_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-7841  043d618c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-7841  3d332371_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-7841  a102bf2c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-7841  b846fa35_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-7841  ba1b02ed_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-7902  9ce5c32d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public void prepare(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PreparedStatement prepareStatement(String sql) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addToBatch() {
-				final int rowCount = statement.executeUpdate();
+				final int rowCount = jdbcCoordinator.getResultSetReturn().executeUpdate( statement );
-				try {
-					statement.close();
-				}
-				catch (SQLException e) {
-					LOG.debug( "Unable to close non-batched batch statement", e );
-				}
+				jdbcCoordinator.release( statement );

Lines added: 2. Lines removed: 7. Tot = 9
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public int executeUpdate( PreparedStatement statement ) {
+	public int executeUpdate( PreparedStatement statement ) {
+		try {
+			return statement.executeUpdate();
+		}
+		catch ( SQLException e ) {
+			throw sqlExceptionHelper().convert( e, "could not execute statement" );
+		}
+	}

Lines added: 8. Lines removed: 0. Tot = 8
********************************************
********************************************
51/After/ HHH-7984  dc193c32_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8083  f1f8600b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	private void addToBatch(Integer batchNumber, EntityInsertAction action) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8159  6a388b75_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	-    public int getIndex() {
-    public int getIndex() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Serializable getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {
-		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
+		final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
-			return false; // NO_VETO
+			// NO_VETO
+			return false;

Lines added: 3. Lines removed: 2. Tot = 5
—————————
Method found in diff:	public void debug(String message);

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8276  18079f34_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-8276  c607e300_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-8276  dc7cdf9d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8637  9938937f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8722  ed4fafeb_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-8741  8ec17e68_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public static void prepare(Map configValues) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Index getIndex(String indexName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8741  8fe5460e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Index getIndex(String indexName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier(Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public void prepare(XProperty collectionProperty) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIndex(Object entry, int i, CollectionPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private PreparedStatement prepareStatement(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addToBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void debug(String message) {

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
51/After/ HHH-9078  1ec115c0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-9204  420296fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-9455  460e9662_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
51/After/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(
-								// TODO: copy/paste from insertRows()
+							// TODO: copy/paste from insertRows()
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	+			public void prepare() {
+			public void prepare() {
+				hbmProcessor.prepare();
+				annotationProcessor.prepare();
+			}

Lines added: 4. Lines removed: 0. Tot = 4
—————————
Method found in diff:	+	public String getIndex() {
+	public String getIndex() {
+		return basicAttributeMapping.getIndex();
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	private PreparedStatement prepareStatement(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public JaxbHbmBasicCollectionElementType getElement();
+	public JaxbHbmBasicCollectionElementType getElement();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-9747  b476094d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public PreparedStatement prepareStatement(String sql) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addToBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getIdentifier(Object entry, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void preInsert(CollectionPersister persister) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(PreparedStatement statement) {
-			jdbcCoordinator.getTransactionCoordinator().getTransactionContext().startStatementExecution();
+			jdbcExecuteStatementStart();
-			jdbcCoordinator.getTransactionCoordinator().getTransactionContext().endStatementExecution();
+			jdbcExecuteStatementEnd();

Lines added: 2. Lines removed: 2. Tot = 4
********************************************
********************************************
51/After/ HHH-9761  288f4904_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public Serializable getIdentifier(Object object);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public void prepare() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void prepareStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public KeyValue getIdentifier() {
-    public KeyValue getIdentifier() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public void debug(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String collectionInfoString( 

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getElement() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public static ModelBinder prepare(MetadataBuildingContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean needsInserting(Object entry, int i, Type elementType) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected static String getIndex(Element element, String indexNodeName, int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getIdentifier() {

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
Method found in diff:	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
51/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
insertRows(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* prepare
* needsInserting
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
Method found in diff:	public void prepare() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getIndex() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PreparedStatement prepareStatement(String sql) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addToBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void abortBatch() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private boolean preInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void debug(String message);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String collectionInfoString( 

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeStatement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) throws SQLException, HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean canBeBatched();

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
Method found in diff:	public int executeUpdate();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
