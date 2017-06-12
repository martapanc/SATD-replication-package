12/report.java
Satd-method: private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
********************************************
********************************************
12/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getEntityMode
* getContextEntityIdentifier
* getFactory
********************************************
********************************************
12/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getEntityMode
* getContextEntityIdentifier
* getFactory
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getEntityMode
* getContextEntityIdentifier
* getFactory
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-					session.getEntityMode(),

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getEntityMode
* getContextEntityIdentifier
* getFactory
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public EntityMode getEntityMode();
-	public EntityMode getEntityMode();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/ HHH-7746  06b0faaf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(
-	private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
-				return getObjectFromList(results, id, session); //EARLY EXIT
+	protected Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
+			return getObjectFromList(results, id, session);
+			return getObjectFromList( results, id, session );
+					return getObjectFromList(results, id, session); //EARLY EXIT

Lines added containing method: 4. Lines removed containing method: 2. Tot = 6
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getEntityMode
* getContextEntityIdentifier
* getFactory
—————————
Method found in diff:	public final SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
