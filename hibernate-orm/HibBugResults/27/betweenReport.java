27/report.java
Satd-method: public void serialize(ObjectOutputStream oos) throws IOException {
********************************************
********************************************
27/Between/ HHH-9701  3e5a8b66_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+	public void serialize(ObjectOutputStream oos) throws IOException {
+		final Status previousStatus = getPreviousStatus();
+		oos.writeObject( getEntityName() );
+		oos.writeObject( id );
+		oos.writeObject( getStatus().name() );
+		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
+		// todo : potentially look at optimizing these two arrays
+		oos.writeObject( loadedState );
+		oos.writeObject( getDeletedState() );
+		oos.writeObject( version );
+		oos.writeObject( getLockMode().toString() );
+		oos.writeBoolean( isExistsInDatabase() );
+		oos.writeBoolean( isBeingReplicated() );
+		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );
+	}

Lines added: 15. Lines removed: 0. Tot = 15
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
serialize(
+	public void serialize(ObjectOutputStream oos) throws IOException {
-			final EntityEntry entry = MutableEntityEntry.deserialize(ois, rtn);
+	public static EntityEntry deserialize(
-	public void serialize(ObjectOutputStream oos) throws IOException {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* writeObject
* name
* writeBoolean
—————————
Method found in diff:	@Override public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	@Override public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
