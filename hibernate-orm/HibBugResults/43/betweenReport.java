43/report.java
Satd-method: protected static final String orderBy(List<JoinableAssociationImpl> associations)
********************************************
********************************************
43/Between/ HHH-7841  560a397a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	protected static final String orderBy(List<JoinableAssociationImpl> associations)

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(
-	protected static final String orderBy(List<JoinableAssociationImpl> associations)
+	protected static String orderBy(List<JoinableAssociationImpl> associations)

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	public String getRHSAlias() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCollection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Joinable getJoinable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public JoinType getJoinType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isManyToManyWith(JoinableAssociationImpl other) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
43/Between/ HHH-7841  b6fd7bf2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	-	public boolean isCollection() {
-	public boolean isCollection() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Joinable getJoinable() {
-	public Joinable getJoinable() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	+	public JoinType getJoinType() {
+	public JoinType getJoinType() {
+		return joinType;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	-	public boolean isManyToManyWith(JoinableAssociation other) {
-	public boolean isManyToManyWith(JoinableAssociation other) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
43/Between/ HHH-7841  f3298620_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(
-				.setOrderByClause( orderBy( orderBy ) )
+				.setOrderByClause( orderBy( orderBy, aliasResolutionContext ) )
-	protected String orderBy(final String orderBy) {
-		return mergeOrderings( orderBy( associations ), orderBy );
+	protected String orderBy(final String orderBy, LoadQueryAliasResolutionContext aliasResolutionContext) {
+		return mergeOrderings( orderBy( associations, aliasResolutionContext ), orderBy );
-	protected static String orderBy(List<JoinableAssociationImpl> associations)
+	protected static String orderBy(

Lines added containing method: 4. Lines removed containing method: 4. Tot = 8
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	-	public String getRHSAlias() {
-	public String getRHSAlias() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isCollection() {
-		return joinableType.isCollectionType();
+		return getJoinableType().isCollectionType();

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public Joinable getJoinable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public JoinType getJoinType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public boolean isManyToManyWith(JoinableAssociationImpl other) {
-	public boolean isManyToManyWith(JoinableAssociationImpl other) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
43/Between/ HHH-8211  1825a476_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	public boolean isCollection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Joinable getJoinable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected JoinType getJoinType(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isManyToManyWith(JoinableAssociation other) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
43/Between/ HHH-8276  dc7cdf9d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(
-				.setOrderByClause( orderBy( orderBy, aliasResolutionContext ) )
-	protected String orderBy(final String orderBy, LoadQueryAliasResolutionContext aliasResolutionContext) {
+	protected String orderBy(final String orderBy, AliasResolutionContext aliasResolutionContext) {

Lines added containing method: 1. Lines removed containing method: 2. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	public boolean hasManyToManyOrdering() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCollection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasOrdering() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Joinable getJoinable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSQLOrderByString(String alias) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getManyToManyOrderByString(String alias) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected JoinType getJoinType(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isManyToManyWith(JoinableAssociation other) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
43/Between/ HHH-8597  8e2f2a9d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
orderBy(
-	protected String orderBy(final String orderBy, AliasResolutionContext aliasResolutionContext) {
-		return mergeOrderings( orderBy( associations, aliasResolutionContext ), orderBy );
-	protected static String orderBy(

Lines added containing method: 0. Lines removed containing method: 3. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getRHSAlias
* hasManyToManyOrdering
* isCollection
* hasOrdering
* getJoinable
* isManyToMany
* getSQLOrderByString
* setLength
* getManyToManyOrderByString
* getJoinType
* isManyToManyWith
* append
—————————
Method found in diff:	-	public boolean isCollection() {
-	public boolean isCollection() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Joinable getJoinable() {
-	public Joinable getJoinable() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public JoinType getJoinType() {
-	public JoinType getJoinType() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public boolean isManyToManyWith(JoinableAssociation other) {
-	public boolean isManyToManyWith(JoinableAssociation other) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
