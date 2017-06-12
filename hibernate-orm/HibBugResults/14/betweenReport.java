14/report.java
Satd-method: private boolean canReuse(FromElement fromElement, String requestedAlias) {
********************************************
********************************************
14/Between/ HHH-9090  153c4e32_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	private boolean canReuse(FromElement fromElement, String requestedAlias) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
canReuse(
-		boolean useFoundFromElement = found && canReuse( elem, classAlias );
+		boolean useFoundFromElement = found && canReuse( elem );
-	private boolean canReuse(FromElement fromElement, String requestedAlias) {
+	private boolean canReuse(FromElement fromElement) {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCurrentFromClause
* getFromClause
* getCurrentClauseType
********************************************
********************************************
