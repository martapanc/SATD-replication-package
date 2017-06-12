52/report.java
Satd-method: public void setName(String name) {
********************************************
********************************************
52/After/ HHH-2304  bcae5600_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-4084  2725a7d4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			name.charAt(0)=='`' ||
+			!name.isEmpty() &&

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-6069  33074dc2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        /* Envers passes 'name' parameter wrapped with '`' signs if quotation required. Set 'quoted' property accordingly. */
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually

Lines added: 1. Lines removed: 2. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(
-	public void setName(String name) {
+	public void setName(String name) {
-	public void setName(String name) {
+	public void setName(String name) {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
—————————
Method found in diff:	public int indexOf(Object o) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
52/After/ HHH-7725  3e69b7bd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-8073  79073a98_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-8371  a2287b6c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(
+		user.setName( "foo" );
+	public void setName(String name) {

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-8741  8ec17e68_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-9722 1361925b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
********************************************
********************************************
52/After/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			StringHelper.isNotEmpty( name ) &&
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
-		) {
-			quoted=true;
-			this.name=name.substring( 1, name.length()-1 );
+				StringHelper.isNotEmpty( name ) &&
+						Dialect.QUOTE.indexOf( name.charAt( 0 ) ) > -1 //TODO: deprecated, remove eventually
+				) {
+			quoted = true;
+			this.name = name.substring( 1, name.length() - 1 );

Lines added: 5. Lines removed: 5. Tot = 10
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(
-		setName(columnName);
+		setName( columnName );

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
—————————
Method found in diff:	public int indexOf(Node node) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
52/After/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
—————————
Method found in diff:	public static int indexOf(Object[] array, Object object) {
-			if ( array[i].equals( object ) )
+			if ( array[i].equals( object ) ) {
+			}

Lines added: 2. Lines removed: 1. Tot = 3
********************************************
********************************************
52/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setName(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* indexOf
* charAt
* substring
—————————
Method found in diff:	public int indexOf(Object o) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
