27/report.java
Satd-method: public void serialize(ObjectOutputStream oos) throws IOException {
********************************************
********************************************
27/After/ HHH-10267 1e44e742_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
serialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* writeObject
* name
* writeBoolean
—————————
Method found in diff:	public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
-			initializedFields.add( name );
+			attributeInitialized( name );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
-			initializedFields.add( name );
+			attributeInitialized( name );

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
27/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
serialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* writeObject
* name
* writeBoolean
—————————
Method found in diff:	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String name() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
27/After/ HHH-9857  750d6fb0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
serialize(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* writeObject
* name
* writeBoolean
********************************************
********************************************
