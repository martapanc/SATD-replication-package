45/report.java
Satd-method: public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
********************************************
********************************************
45/Between/ HHH-1904  fbdca395_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
-		int uniqueIndexInteger = 0;
-				uniqueIndexInteger++;
-						"uc_" + name + "_" + uniqueIndexInteger);
+						StringHelper.randomFixedLengthHex("UK_"));

Lines added: 1. Lines removed: 3. Tot = 4
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean isQuoted(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-2448  7b9b9b39_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-2578  0537740f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-2808  7f109720_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-4084  2725a7d4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-4358  23a62802_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-5043  5671de51_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-5765  88543c7a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	}	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5765  91d44442_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5869  e7b188c9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	+	public String getName() {
+	public String getName() {
+		// todo name these annotation types for addition to the registry
+		return null;
+	}

Lines added: 4. Lines removed: 0. Tot = 4
********************************************
********************************************
45/Between/ HHH-5903  011d7e11_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	}	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5903  b2a79676_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5913  42c609cf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5913  5adf2960_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5913  e3a0525f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5913  e8ebe8e3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5916  55eb37ed_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5916  d7c48d77_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5916  ddfcc44d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5947: 9d697660_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5949  08d9fe21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	-	protected String getName() {
-	protected String getName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Object generatorKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object generatorKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping mapping, String defaultCatalog, String defaultSchema)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean isQuoted(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public XProperty getProperty() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequence(Object key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasAlterTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isTable(Object key) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-5991  0b10334e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6047  731d00fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6051  815baf43_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-6068  2ac8c0c0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6091  7c39b19a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6097  ad17f89c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object generatorKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-		public String getCatalog() {
-		public String getCatalog() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getProperty(String property) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequence(Object key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-		public String getSchema() {
-		public String getSchema() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public IndexMetadata getIndexMetadata(String indexName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ForeignKeyMetadata getForeignKeyMetadata(String keyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isTable(Object key) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6144  53e04398_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6150  f07b88c7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6183  e7c26b28_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object generatorKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Property getProperty(String propertyName) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilterName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6198  4ee0d423_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6271  8373871c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6271  fa1183f3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6336  89911003_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6586  68f7d9b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-6640  9f214d80_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6683  f4fa1762_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object generatorKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequence(Object key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isTable(Object key) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6817  6c7379c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-6967  e75b8a77_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean isQuoted(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasAlterTable() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7134  8e30a2b8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7195  5068b8e8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7387  ad2a9ef6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	+			public String getName() {
+			public String getName() {
+				return name;

Lines added: 2. Lines removed: 0. Tot = 2
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7462  d184cb3e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7556  4ad49a02_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7580  7976e239_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7721  4e434f61_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7797  12c7ab93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
-				dialect.getUniqueDelegate().generateUniqueKey( this, col );
+				UniqueKey uk = getOrCreateUniqueKey( 
+						col.getQuotedName( dialect ) + '_' );
+				uk.addColumn( col );

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7797  41397f22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping mapping, String defaultCatalog, String defaultSchema)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7797  4204f2c5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getIndexIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
-			dialect.getUniqueDelegate().applyUnique( this, col, buf );
-
+			if ( col.isUnique() ) {
+				buf.append( dialect.getUniqueDelegate().applyUniqueToColumn( this, col ) );
+			}
+				
-		dialect.getUniqueDelegate().createUniqueConstraint( this, buf );
+		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );

Lines added: 5. Lines removed: 3. Tot = 8
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getUniqueKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7890  42f34227_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	+	public String getName() {
+	public String getName() {
+		return name;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-7957  9ab92404_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public Object generatorKey();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public String getCatalog();
+	public String getCatalog();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public String getSchema();
+	public String getSchema();

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
45/Between/ HHH-7991  4d68ddf7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
********************************************
********************************************
45/Between/ HHH-8010  394458f6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-8026  8515ce19_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+		int uniqueIndexInteger = 0;
+				uniqueIndexInteger++;
-						col.getQuotedName( dialect ) + '_' );
+						"uc_" + name + "_" + uniqueIndexInteger);

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCatalog() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isQuoted() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPhysicalTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSchema() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getForeignKeyIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-8092  b5457f37_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-8092  cf921df1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ HHH-8162  377c3000_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(
-			String[] sqlStrings = configuration.generateSchemaUpdateScript( dialect, meta );

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequence(Object key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isTable(Object key) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
45/Between/ Merge bra d00c9c85_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
generateSchemaUpdateScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* generatorKey
* getIndexIterator
* sqlCreateString
* sqlCommentStrings
* getCatalog
* isQuoted
* hasImplicitIndexForForeignKey
* getProperty
* isPhysicalTable
* isSequence
* hasAlterTable
* getUniqueKeyIterator
* toStringArray
* getSchema
* getFilterName
* isPhysicalConstraint
* isForeignKey
* getIndexMetadata
* sqlCreateStrings
* getForeignKeyMetadata
* getForeignKeyIterator
* isTable
—————————
Method found in diff:	public String getProperty(String propertyName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
