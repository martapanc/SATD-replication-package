59/report.java
Satd-method: public void initialize(Serializable key, SessionImplementor session) 
********************************************
********************************************
59/Between/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(
-	public void initialize(Serializable id, SessionImplementor session) throws HibernateException;
+	void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException;
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session)
-	public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session)
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
-	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
+	void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException;
-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
-	public void initialize() throws HibernateException;
+	void initialize() throws HibernateException;
-		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+		public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {

Lines added containing method: 14. Lines removed containing method: 14. Tot = 28
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	protected Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public Query getNamedSQLQuery(String name) {
-	public Query getNamedSQLQuery(String name) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public final String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Filter setParameter(String name, Object value);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list() throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public Query setCollectionKey(Serializable collectionKey) {
-	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public BasicQueryContract setFlushMode(FlushMode flushMode);
-	public BasicQueryContract setFlushMode(FlushMode flushMode);

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Map getNamedParameters() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
********************************************
********************************************
59/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(
-							value.initialize( rs, 1 );
+							value.initialize( rs, 1 );
-										value.initialize( selectRS, 1 );
+										value.initialize( selectRS, 1 );

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	public Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list(SessionImplementor session, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setFlushMode(FlushMode fm) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getNamedParameters() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	public Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private String getRole(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setParameter(int position, Object val, Type type) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list(String query, QueryParameters queryParameters) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setFlushMode(FlushMode fm);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getNamedParameters() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	protected Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Filter setParameter(String name, Object value);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list() throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Criteria setFlushMode(FlushMode flushMode);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getNamedParameters() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-8211  1825a476_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	public Query getNamedSQLQuery(String queryName) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setParameter(int position, Object val, Type type) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list(SessionImplementor session, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public NamedQueryDefinitionBuilder setFlushMode(FlushMode flushMode) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	protected Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query getNamedSQLQuery(String queryName) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setParameter(int position, Object val, Type type) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list(SessionImplementor session, QueryParameters queryParameters)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BasicQueryContract setFlushMode(FlushMode flushMode) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(
-	public void initialize(Serializable key, SessionImplementor session)
+	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	public Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected List list(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setFlushMode(FlushModeType flushModeType) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
59/Between/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getKeyType
* getNamedSQLQuery
* getRole
* setParameter
* isDebugEnabled
* list
* setCollectionKey
* setFlushMode
* getNamedParameters
—————————
Method found in diff:	protected Type getKeyType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query getNamedSQLQuery(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getRole() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Filter setParameter(String name, Object value);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List list() throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setCollectionKey(Serializable collectionKey) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BasicQueryContract setFlushMode(FlushMode flushMode);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getNamedParameters() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
