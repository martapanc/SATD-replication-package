56/report.java
Satd-method: public Object loadByUniqueKey(
********************************************
********************************************
56/Between/ HHH-10073 47b8ed51_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public Object loadByUniqueKey(SessionImplementor session,Object key) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(
-	public Object loadByUniqueKey(SessionImplementor session,Object key) {
+	public Object loadByUniqueKey(SharedSessionContractImplementor session, Object key) {
-	public Object loadByUniqueKey(String propertyName, Object uniqueKey, SessionImplementor session) 
+	Object loadByUniqueKey(String propertyName, Object uniqueKey, SharedSessionContractImplementor session);

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
-		return sessionImplementor.getEntityPersister( entityName, object );
+		return delegate.getEntityPersister( entityName, object );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(EntityKey key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-11097 0a2a5c62_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-11703 1c349144_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(EntityKey key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) 

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName) throws MappingException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(EntityKey key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	-	public EntityMode getEntityMode();
-	public EntityMode getEntityMode();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(EntityKey key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-6813  45d46b61_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-6813  bc6f5d8a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-7225  37b64599_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-7359  9968ce3a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-7714  f77b068e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-7771  32e87656_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-8573: af03365c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-8637  9938937f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityPersister getEntityPersister() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-8741  8fe5460e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntity() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-8845  6329be56_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-8845  76aede60_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-8991  5bdef580_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-9512: 816c9761_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
********************************************
********************************************
56/Between/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private EntityPersister getEntityPersister(EntityType entityType) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
56/Between/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
loadByUniqueKey(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityMode
* getPersistenceContext
* getEntityPersister
* getFactory
* getEntity
* proxyFor
—————————
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistenceContext getPersistenceContext(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(String entityName, Serializable id) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
