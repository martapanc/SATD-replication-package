52/report.java
Satd-method: public void setName(String name) {
********************************************
********************************************
52/Between/ HHH-5616  34c2839d_diff.java
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
52/Between/ HHH-5986  0816d00e_diff.java
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
52/Between/ HHH-6069  d7cc102b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        /* Envers passes 'name' parameter wrapped with '`' signs if quotation required. Set 'quoted' property accordingly. */
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1

Lines added: 2. Lines removed: 1. Tot = 3
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
