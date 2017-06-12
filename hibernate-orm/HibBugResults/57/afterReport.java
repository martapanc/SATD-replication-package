57/report.java
Satd-method: public String sqlConstraintString(Dialect dialect, String constraintName, String defaultCatalog,
********************************************
********************************************
57/After/ HHH-7797  3a995f57_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getAddPrimaryKeyConstraintString(String constraintName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsNotNullUnique() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-7797  41397f22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-7797  4204f2c5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(
-//				String constraint = uk.sqlConstraintString( dialect );
-			String constraint = uk.sqlConstraintString( dialect );
-	public String sqlConstraintString(Dialect dialect) {

Lines added containing method: 0. Lines removed containing method: 3. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	-	public boolean supportsNotNullUnique() {
-	public boolean supportsNotNullUnique() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-7797  7b05f4ae_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
********************************************
********************************************
57/After/ HHH-7969  1d9b7a06_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-8159  5fc70fc5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getAddPrimaryKeyConstraintString(String constraintName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static String replace(String type, long size, int precision, int scale) {
-		type = StringHelper.replaceOnce(type, "$s", Integer.toString(scale) );
-		type = StringHelper.replaceOnce(type, "$l", Long.toString(size) );
-		return StringHelper.replaceOnce(type, "$p", Integer.toString(precision) );
+		type = StringHelper.replaceOnce( type, "$s", Integer.toString( scale ) );
+		type = StringHelper.replaceOnce( type, "$l", Long.toString( size ) );
+		return StringHelper.replaceOnce( type, "$p", Integer.toString( precision ) );

Lines added: 3. Lines removed: 3. Tot = 6
—————————
Method found in diff:	public boolean supportsNotNullUnique() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-8217  9030fa01_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public static String replace(String template, String placeholder, String replacement) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-8741  8ec17e68_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public boolean isNullable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String replace(String template, String placeholder, String replacement) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		String[] cols = new String[ getColumnSpan() ];
-		String[] refcols = new String[ getColumnSpan() ];
-		int i=0;
-		Iterator refiter = null;
-		if(isReferenceToPrimaryKey() ) {
-			refiter = referencedTable.getPrimaryKey().getColumnIterator();
+		String[] columnNames = new String[ getColumnSpan() ];
+		String[] referencedColumnNames = new String[ getColumnSpan() ];
+
+		final Iterator referencedColumnItr;
+		if ( isReferenceToPrimaryKey() ) {
+			referencedColumnItr = referencedTable.getPrimaryKey().getColumnIterator();
-			refiter = referencedColumns.iterator();
+			referencedColumnItr = referencedColumns.iterator();
-		Iterator iter = getColumnIterator();
-		while ( iter.hasNext() ) {
-			cols[i] = ( (Column) iter.next() ).getQuotedName(dialect);
-			refcols[i] = ( (Column) refiter.next() ).getQuotedName(dialect);
+		Iterator columnItr = getColumnIterator();
+		int i=0;
+		while ( columnItr.hasNext() ) {
+			columnNames[i] = ( (Column) columnItr.next() ).getQuotedName(dialect);
+			referencedColumnNames[i] = ( (Column) referencedColumnItr.next() ).getQuotedName(dialect);
-		String result = dialect.getAddForeignKeyConstraintString(
-			constraintName, cols, referencedTable.getQualifiedName(dialect, defaultCatalog, defaultSchema), refcols, isReferenceToPrimaryKey()
+
+		final String result = dialect.getAddForeignKeyConstraintString(
+			constraintName,
+			columnNames,
+			referencedTable.getQualifiedName(dialect, defaultCatalog, defaultSchema),
+			referencedColumnNames,
+			isReferenceToPrimaryKey()
-		return cascadeDeleteEnabled && dialect.supportsCascadeDelete() ? 
-			result + " on delete cascade" : 
-			result;
+		return cascadeDeleteEnabled && dialect.supportsCascadeDelete()
+				? result + " on delete cascade"
+				: result;

Lines added: 22. Lines removed: 16. Tot = 38
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(
+					.append( table.getPrimaryKey().sqlConstraintString( dialect ) );

Lines added containing method: 1. Lines removed containing method: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getAddPrimaryKeyConstraintString(String constraintName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public Boolean isNullable() {
+	public Boolean isNullable() {
+		return basicAttributeMapping.isNotNull() == null
+				? null
+				: !basicAttributeMapping.isNotNull();
+	}

Lines added: 5. Lines removed: 0. Tot = 5
—————————
Method found in diff:	public static String replace(String template, String placeholder, String replacement) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsNotNullUnique() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public AttributePath append(String property) {
+	public AttributePath append(String property) {
+		return new AttributePath( this, property );
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public String getQuotedName() {
-		return quoted ?
-				"`" + name + "`" :
-				name;
+		return name == null ? null : name.toString();

Lines added: 1. Lines removed: 3. Tot = 4
********************************************
********************************************
57/After/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public String sqlConstraintString(Dialect dialect, String constraintName, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(
-	public String sqlConstraintString(Dialect dialect, String constraintName, String defaultCatalog, String defaultSchema) {
+	public String sqlConstraintString(
-    public String sqlConstraintString(
+	public String sqlConstraintString(

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public boolean isNullable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean replace(K key, V oldValue, V newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public static void append(StringBuilder sb, Iterator<String> contents, String separator) {
-    public static void append(StringBuilder sb, Iterator<String> contents, String separator) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
57/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sqlConstraintString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getAddPrimaryKeyConstraintString
* isNullable
* replace
* supportsNotNullUnique
* append
* getQuotedName
—————————
Method found in diff:	public String getAddPrimaryKeyConstraintString(String constraintName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Boolean isNullable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static String replace(String type, long size, int precision, int scale) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public AttributePath append(String property) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
