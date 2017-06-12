37/report.java
Satd-method: protected String renderLoggableString(Object value, SessionFactoryImplementor factory)
********************************************
********************************************
37/After/ HHH-10073 47b8ed51_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-10602 0385b143_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public String toLoggableString(SessionImplementor session) {
-	public String toLoggableString(SessionImplementor session) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
37/After/ HHH-11097 0a2a5c62_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-11173 122f00f3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		if ( !Hibernate.isInitialized( value ) ) {
+			return "<uninitialized>";
+		}
+

Lines added: 4. Lines removed: 0. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-11173 4d6cda15_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			list.add( elemType.toLoggableString( itr.next(), factory ) );
+			Object element = itr.next();
+			if ( element == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( element ) ) {
+				list.add( "<uninitialized>" );
+			}
+			else {
+				list.add( elemType.toLoggableString( element, factory ) );
+			}

Lines added: 7. Lines removed: 1. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(
-			return renderLoggableString( value, factory );
+		return renderLoggableString( value, factory );

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
+		if ( value == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( value ) ) {
+			return  "<uninitialized>";
+		}

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
37/After/ HHH-11646 3a813dcb_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-5855  efa72a83_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-6361  f9049a1f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-6361: 7bb43baf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-7359  30ea167c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-7771  32e87656_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-7928  cb1b9a05_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-8637  9938937f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-8741  8fe5460e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public final boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toLoggableString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toLoggableString(SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public final boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public String toLoggableString() {
-	public String toLoggableString() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
37/After/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public String toLoggableString();
+	public String toLoggableString();

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
37/After/ HHH-9777  93f56ecf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-9777  e07eef3d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public String toLoggableString(Object value, SessionFactoryImplementor factory)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String asXML() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
37/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
renderLoggableString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
* asXML
* toLoggableString
—————————
Method found in diff:	public boolean isInstance(Object object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String asXML() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toLoggableString();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
