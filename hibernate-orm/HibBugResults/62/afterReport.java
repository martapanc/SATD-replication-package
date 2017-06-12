62/report.java
Satd-method: protected void performUpdate(
********************************************
********************************************
62/After/ HHH-5697  fe8c7183_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
performUpdate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* afterReassociate
* checkUniqueness
* trace
* infoString
* getPersistenceContext
* getTimestamp
* getRequestedId
* isTraceEnabled
* hasCache
* getVersion
* getObject
* process
* getFactory
* getCache
* isMutable
* getState
* getSession
—————————
Method found in diff:	public EntityMode getEntityMode();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterReassociate(Object entity, SessionImplementor session) {
-					final EntityKey key = new EntityKey( id, this, session.getEntityMode() );
+					final EntityKey key = session.generateEntityKey( id, this );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public long getTimestamp();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasCache() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getVersion() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionFactoryImplementor getFactory();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isMutable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final SessionImplementor getSession() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
62/After/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
performUpdate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* afterReassociate
* checkUniqueness
* trace
* infoString
* getPersistenceContext
* getTimestamp
* getRequestedId
* isTraceEnabled
* hasCache
* getVersion
* getObject
* process
* getFactory
* getCache
* isMutable
* getState
* getSession
—————————
Method found in diff:	-	private EntityMode getEntityMode() {
-	private EntityMode getEntityMode() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void trace(String msg) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public PersistenceContext getPersistenceContext() {
-	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public long getTimestamp() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasCache() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Object getVersion(Object[] fields, EntityPersister persister) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public ResultAliasContext process() {
-	public ResultAliasContext process() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getCache() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isMutable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionImplementor getSession() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
62/After/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
performUpdate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* afterReassociate
* checkUniqueness
* trace
* infoString
* getPersistenceContext
* getTimestamp
* getRequestedId
* isTraceEnabled
* hasCache
* getVersion
* getObject
* process
* getFactory
* getCache
* isMutable
* getState
* getSession
—————————
Method found in diff:	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void afterReassociate(Object entity, SessionImplementor session);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String infoString(String entityName, Serializable id) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public long getTimestamp();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getRequestedId() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasCache() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getVersion() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getObject() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String process() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Cache getCache();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isMutable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected final SessionImplementor getSession() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
62/After/ HHH-6198  4ee0d423_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
performUpdate(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* afterReassociate
* checkUniqueness
* trace
* infoString
* getPersistenceContext
* getTimestamp
* getRequestedId
* isTraceEnabled
* hasCache
* getVersion
* getObject
* process
* getFactory
* getCache
* isMutable
* getState
* getSession
—————————
Method found in diff:	public void afterReassociate(Object entity, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public long getTimestamp() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getRequestedId() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasCache() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getVersion() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getObject() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isMutable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected final SessionImplementor getSession() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
