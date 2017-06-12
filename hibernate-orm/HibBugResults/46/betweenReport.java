46/report.java
Satd-method: public void set(Object target, Object value, SessionFactoryImplementor factory) 
********************************************
********************************************
46/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
set(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isXMLElement
* setText
* setToXMLNode
********************************************
********************************************
46/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
set(
-			setters[i].set( component, values[i], null );
+			setters[i].set( component, values[i], null );
-	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
+	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isXMLElement
* setText
* setToXMLNode
—————————
Method found in diff:	public final boolean isXMLElement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setText(int position, String val);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
46/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
set(
-			iter.set(o);
+			itr.set(o);
-		 * @see java.util.List#set(int, Object)
-			return list.set(i, o);
+			return list.set( i, o );
-				Array.set( result, i, persister.getElementType().deepCopy(elt, entityMode, persister.getFactory()) );
+				Array.set( result, i, persister.getElementType().deepCopy(elt, persister.getFactory()) );
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-	public void set(PreparedStatement st, Object value, int index) throws SQLException {
+	public void set(PreparedStatement st, Object value, int index) throws SQLException {
-		idSetter.set( root, new Long( 123 ), getSFI() );
-		textSetter.set( root, "description...", getSFI() );
-		nameSetter.set( root, "JBoss", getSFI() );
-		accountIdSetter.set( root, new Long( 456 ), getSFI() );

Lines added containing method: 4. Lines removed containing method: 13. Tot = 17
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isXMLElement
* setText
* setToXMLNode
—————————
Method found in diff:	public final boolean isXMLElement() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setText(int position, String val) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
