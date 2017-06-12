12/report.java
Satd-method: private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
********************************************
********************************************
12/After/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getObjectFromList(
-	protected Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
+	protected Object getObjectFromList(List results, Serializable id, SharedSessionContractImplementor session) {
-	protected Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
+	protected Object getObjectFromList(List results, Serializable id, SharedSessionContractImplementor session) {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
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
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object) {
-		return sessionImplementor.getContextEntityIdentifier( object );
+		return delegate.getContextEntityIdentifier( object );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ HHH-8741  cd590470_diff.java
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
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ HHH-9803  7308e14f_diff.java
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
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ HHH-9803  bd256e47_diff.java
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
Method found in diff:	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getContextEntityIdentifier(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
