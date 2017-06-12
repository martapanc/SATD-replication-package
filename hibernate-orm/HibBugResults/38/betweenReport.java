38/report.java
Satd-method: public Type toType(String propertyName) throws QueryException;
********************************************
********************************************
38/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toType(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
38/Between/ HHH-8276  af1061a4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toType(
+	public Type toType(String propertyName) throws QueryException {
+		return parentPropertyMapping.toType( toParentPropertyPath( propertyName ) );
+	 * Used to check the validity of the propertyName argument passed into {@link #toType(String)},
+	 * Builds the relative path.  Used to delegate {@link #toType(String)},

Lines added containing method: 4. Lines removed containing method: 0. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
