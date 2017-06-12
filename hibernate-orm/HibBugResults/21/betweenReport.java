21/report.java
Satd-method: protected final void commonRegistration() {
********************************************
********************************************
21/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
********************************************
********************************************
21/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
—————————
Method found in diff:	public void setProperty(XProperty property) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
21/Between/ HHH-6199  a806626a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	protected final void commonRegistration() {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(
-		commonRegistration();
-	protected final void commonRegistration() {
+		commonRegistration();
+	protected final void commonRegistration() {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
********************************************
********************************************
21/Between/ HHH-6944  5081861d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
********************************************
********************************************
21/Between/ HHH-7797  49c8a8e4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
********************************************
********************************************
21/Between/ HHH-8159  5fc70fc5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
-		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
+		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
+		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
-		// TBD should this be varbinary($1)?
-		//		registerColumnType(Types.VARBINARY,     "binary($1)");
-		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
-		// hibernate impelemnts cast in Dialect.java
-		// aggregate functions shouldn't be registered, right?
-		//registerFunction( "list", new StandardSQLFunction("list",StandardBasicTypes.STRING) );
-		// stopped on $list

Lines added: 2. Lines removed: 9. Tot = 11
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
commonRegistration(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setProperty
********************************************
********************************************
