73/report.java
Satd-method: public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
********************************************
********************************************
73/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toSqlString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityPersister
* getVersionProperty
* getPropertyNames
* getPropertyValues
* isComponentType
* getPropertyTypes
* getFactory
* getEntityName
* append
********************************************
********************************************
73/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toSqlString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityPersister
* getVersionProperty
* getPropertyNames
* getPropertyValues
* isComponentType
* getPropertyTypes
* getFactory
* getEntityName
* append
—————————
Method found in diff:	public EntityPersister getEntityPersister() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public VersionProperty getVersionProperty() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final boolean isComponentType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PropertyPath append(String property) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
73/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toSqlString(
-	public String toSqlString(
+	public String toSqlString(

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityPersister
* getVersionProperty
* getPropertyNames
* getPropertyValues
* isComponentType
* getPropertyTypes
* getFactory
* getEntityName
* append
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName) throws MappingException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getVersionProperty();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final boolean isComponentType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
73/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toSqlString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityPersister
* getVersionProperty
* getPropertyNames
* getPropertyValues
* isComponentType
* getPropertyTypes
* getFactory
* getEntityName
* append
—————————
Method found in diff:	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getVersionProperty();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final boolean isComponentType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName(Object object) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
73/Between/ HHH-8159  8c28ba84_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
toSqlString(
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
-		return projection.toSqlString(criteria, position, criteriaQuery);
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
+		return projection.toSqlString( criteria, position, criteriaQuery );
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery)
-		return "distinct " + projection.toSqlString(criteria, position, criteriaQuery);
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
+		return "distinct " + wrappedProjection.toSqlString( criteria, position, criteriaQuery );
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final String conditionFragment = condition.toSqlString( criteria, cq );
-		String critCondition = crit.toSqlString(criteria, cq);
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-    public String toSqlString( Criteria criteria, CriteriaQuery criteriaQuery )
+	public String toSqlString( Criteria criteria, CriteriaQuery criteriaQuery ) {
-	public String toSqlString(
+	public String toSqlString(Criteria criteria,CriteriaQuery criteriaQuery) {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-			lhs.toSqlString(criteria, criteriaQuery) +
-			rhs.toSqlString(criteria, criteriaQuery) +
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+				+ lhs.toSqlString( criteria, criteriaQuery )
+				+ rhs.toSqlString( criteria, criteriaQuery )
-				criterion.toSqlString( criteria, criteriaQuery ) );
+				criterion.toSqlString( criteria, criteriaQuery )
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) 
-			buf.append( proj.toSqlString(criteria, loc, criteriaQuery) );
+	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
+			buf.append( separator ).append( projection.toSqlString( criteria, loc, criteriaQuery ) );
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-	public String toSqlString(
+	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)

Lines added containing method: 27. Lines removed containing method: 27. Tot = 54
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getEntityPersister
* getVersionProperty
* getPropertyNames
* getPropertyValues
* isComponentType
* getPropertyTypes
* getFactory
* getEntityName
* append
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName(Criteria criteria);

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
