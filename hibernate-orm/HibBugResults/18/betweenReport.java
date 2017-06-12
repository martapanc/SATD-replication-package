18/report.java
Satd-method: private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
********************************************
********************************************
18/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
existsInDatabase(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPersistenceContext
* getEntityMode
* getIdentifier
* getEntry
* getEntity
* isExistsInDatabase
********************************************
********************************************
18/Between/ HHH-5697  fe8c7183_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-				EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
-				Object managedEntity = source.getPersistenceContext().getEntity( key );
+				final EntityKey key = source.generateEntityKey( id, persister );
+				final Object managedEntity = source.getPersistenceContext().getEntity( key );
-		if ( entry == null ) {
-			// perhaps this should be an exception since it is only ever used
-			// in the above method?
-			return false;
-		}
-		else {
-			return entry.isExistsInDatabase();
-		}
+		return entry != null && entry.isExistsInDatabase();

Lines added: 3. Lines removed: 10. Tot = 13
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
existsInDatabase(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPersistenceContext
* getEntityMode
* getIdentifier
* getEntry
* getEntity
* isExistsInDatabase
—————————
Method found in diff:	public PersistenceContext getPersistenceContext();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityMode getEntityMode();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Serializable getIdentifier() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public EntityEntry getEntry(Object entity) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getEntity(EntityKey key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isExistsInDatabase() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
18/Between/ HHH-5791  262f2531_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
existsInDatabase(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPersistenceContext
* getEntityMode
* getIdentifier
* getEntry
* getEntity
* isExistsInDatabase
********************************************
********************************************
18/Between/ HHH-6026  19791a6c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
existsInDatabase(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPersistenceContext
* getEntityMode
* getIdentifier
* getEntry
* getEntity
* isExistsInDatabase
—————————
Method found in diff:	-	public EntityMode getEntityMode() {
-	public EntityMode getEntityMode() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Serializable getIdentifier(Object entity) throws HibernateException {
-	public Serializable getIdentifier(Object entity) throws HibernateException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isExistsInDatabase() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
