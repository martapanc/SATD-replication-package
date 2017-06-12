66/report.java
Satd-method: protected void secondPassCompile() throws MappingException {
********************************************
********************************************
66/Between/ HHH-1904  fbdca395_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			int uniqueIndexPerTable = 0;
-				uniqueIndexPerTable++;
-						? "UK_" + table.getName() + "_" + uniqueIndexPerTable
+						? StringHelper.randomFixedLengthHex("UK_")

Lines added: 1. Lines removed: 3. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-2448  7b9b9b39_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-2578  0537740f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-2808  7f109720_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		// process cache queue
-		{
-			for ( CacheHolder holder : caches ) {
-				if ( holder.isClass ) {
-					applyCacheConcurrencyStrategy( holder );
-				}
-				else {
-					applyCollectionCacheConcurrencyStrategy( holder );
-				}
-			}
-			caches.clear();
-		}
+
+		// process cache queue
+		{
+			for ( CacheHolder holder : caches ) {
+				if ( holder.isClass ) {
+					applyCacheConcurrencyStrategy( holder );
+				}
+				else {
+					applyCollectionCacheConcurrencyStrategy( holder );
+				}
+			}
+			caches.clear();
+		}
+

Lines added: 14. Lines removed: 12. Tot = 26
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-4084  2725a7d4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public Value getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-4358  23a62802_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5043  5671de51_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5065  d4308460_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5765  88543c7a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5765  91d44442_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5869  e7b188c9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5903  011d7e11_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5903  b2a79676_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5913  42c609cf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5913  5adf2960_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5913  e3a0525f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-
-		applyConstraintsToDDL();

Lines added: 0. Lines removed: 2. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5913  e8ebe8e3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5916  55eb37ed_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+	public PersisterClassProvider getPersisterClassProvider() {
+		return persisterClassProvider;
+	}
+
+	/**
+	 * Defines a custom persister class provider.
+	 *
+	 * The persister class is chosen according to the following rules in decreasing priority:
+	 *  - the persister class defined explicitly via annotation or XML
+	 *  - the persister class returned by the PersisterClassProvider implementation (if not null)
+	 *  - the default provider as chosen by Hibernate Core (best choice most of the time)
+	 *
+	 *
+	 * @param persisterClassProvider implementation
+	 */
+	public Configuration setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
+		this.persisterClassProvider = persisterClassProvider;
+		return this;
+	}
+
+		public PersisterClassProvider getPersisterClassProvider() {
+			return persisterClassProvider;
+		}
+
+		public void setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
+			Configuration.this.persisterClassProvider = persisterClassProvider;
+		}
+

Lines added: 28. Lines removed: 0. Tot = 28
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5916  d7c48d77_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+	public PersisterClassProvider getPersisterClassProvider() {
+		return persisterClassProvider;
+	}
+
+	/**
+	 * Defines a custom persister class provider.
+	 *
+	 * The persister class is chosen according to the following rules in decreasing priority:
+	 *  - the persister class defined explicitly via annotation or XML
+	 *  - the persister class returned by the PersisterClassProvider implementation (if not null)
+	 *  - the default provider as chosen by Hibernate Core (best choice most of the time)
+	 *
+	 *
+	 * @param persisterClassProvider implementation
+	 */
+	public Configuration setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
+		this.persisterClassProvider = persisterClassProvider;
+		return this;
+	}
+
+		public PersisterClassProvider getPersisterClassProvider() {
+			return persisterClassProvider;
+		}
+
+		public void setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
+			Configuration.this.persisterClassProvider = persisterClassProvider;
+		}
+
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+import org.hibernate.persister.PersisterClassProvider;
+	 * Get the current persister class provider implementation
+	 */
+	public PersisterClassProvider getPersisterClassProvider();
+
+	/**
+	 * Set the current persister class provider implementation
+	 */
+	public void setPersisterClassProvider(PersisterClassProvider persisterClassProvider);
+
+	/**

Lines added: 40. Lines removed: 1. Tot = 41
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5916  ddfcc44d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove(Serializable id, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-5947: 9d697660_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-5949  08d9fe21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	-	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
-	public void remove(Serializable id, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
66/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Property getReferencedProperty(String propertyPath) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-5991  0b10334e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6047  731d00fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6051  815baf43_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6068  2ac8c0c0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
+				Map defaults = reflectionManager.getDefaults();
+				final Object isDelimited = defaults.get( "delimited-identifier" );
+				// Set default schema name if orm.xml declares it.
+				final String schema = (String) defaults.get( "schema" );
+				if ( StringHelper.isNotEmpty( schema ) ) {
+					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
+				}
+				// Set default catalog name if orm.xml declares it.
+				final String catalog = (String) defaults.get( "catalog" );
+				if ( StringHelper.isNotEmpty( catalog ) ) {
+					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
+				}

Lines added: 12. Lines removed: 1. Tot = 13
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6091  7c39b19a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6097  ad17f89c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove(Object key) throws CacheException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6144  53e04398_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6150  f07b88c7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6183  e7c26b28_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Property getReferencedProperty(String propertyPath) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6198  4ee0d423_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public Object remove(Object entity) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6271  8373871c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6271  fa1183f3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6336  89911003_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6586  68f7d9b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6640  9f214d80_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6683  f4fa1762_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        LOG.trace("Starting secondPassCompile() processing");
+		LOG.trace( "Starting secondPassCompile() processing" );

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(
-        LOG.trace("Starting secondPassCompile() processing");
+		LOG.trace( "Starting secondPassCompile() processing" );

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public Object remove(Object entity) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6817  6c7379c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-6911  5329bba1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6911  b70148a8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-6967  e75b8a77_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7134  8e30a2b8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	+		public String getValue() {
+		public String getValue() {
+			return value;
+		}

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
66/Between/ HHH-7195  5068b8e8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7387  ad2a9ef6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-7462  d184cb3e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7556  4ad49a02_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove(Object entity) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public T getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-7580  7976e239_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-7721  4e434f61_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7797  12c7ab93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7797  41397f22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7797  4204f2c5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7890  42f34227_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7927  dd44ad45_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7957  9ab92404_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7991  4d68ddf7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-7995  04fe8499_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8010  394458f6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		
+		// TEMPORARY
+		// Ensure the correct ClassLoader is used in commons-annotations.
+		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
+		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
+		
+		
+		Thread.currentThread().setContextClassLoader( tccl );

Lines added: 8. Lines removed: 0. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8026  8515ce19_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-						? "key" + uniqueIndexPerTable
+						? "UK_" + table.getName() + "_" + uniqueIndexPerTable

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8092  b5457f37_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8092  cf921df1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8162  377c3000_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8211  1825a476_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public boolean remove(Object o) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Property getReferencedProperty(String propertyPath) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-8217  9030fa01_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-				final String keyName = StringHelper.isEmpty( holder.getName() )
-						? StringHelper.randomFixedLengthHex("UK_")
-						: holder.getName();
-				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
+				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns() );
-			int uniqueIndexPerTable = 0;
-				uniqueIndexPerTable++;
-				final String keyName = StringHelper.isEmpty( holder.getName() )
-						? "idx_"+table.getName()+"_" + uniqueIndexPerTable
-						: holder.getName();
-				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns(), holder.getOrdering(), holder.isUnique() );
+				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns(), holder.getOrdering(), holder.isUnique() );

Lines added: 2. Lines removed: 10. Tot = 12
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	    public String getValue() {
+	    public String getValue() {
+	        return value;
+	    }

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
66/Between/ HHH-8222  8c95a607_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove(Object entity) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public T getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-8223  14993a46_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8246  a03d44f2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8266  5ea40ce3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
-			if ( previous != null ) {
-				throw new DuplicateMappingException( "named stored procedure query", name );
+			if ( !defaultNamedProcedure.contains( name ) ) {
+				final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
+				if ( previous != null ) {
+					throw new DuplicateMappingException( "named stored procedure query", name );
+				}
+		@Override
+		public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
+				throws DuplicateMappingException {
+			addNamedProcedureCallDefinition( definition );
+			defaultNamedProcedure.add( definition.getRegisteredName() );
+		}
+
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+	 * Adds metadata for a named stored procedure call to this repository.
+	 *
+	 * @param definition The procedure call information
+	 *
+	 * @throws DuplicateMappingException If a query already exists with that name.
+	 */
+	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
+
+
+
+	/**
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
-	public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, Mappings mappings) {
+	public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, Mappings mappings, boolean isDefault) {
-		mappings.addNamedProcedureCallDefinition( def );
+
+		if(isDefault){
+			mappings.addDefaultNamedProcedureCallDefinition( def );
+		} else{
+			mappings.addNamedProcedureCallDefinition( def );
+		}

Lines added: 32. Lines removed: 7. Tot = 39
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8297  7444c6c1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8390  580a7133_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8469  9348c23e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8478  498735aa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-8496  580af7e6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8534  2bb866a6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
-		final Iterator<PersistentClass> classes = cfg.getClassMappings();
-					classes,
+					cfg.getClassMappings(),
+					cfg.getMappedSuperclassMappingsCopy(),
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java
+
+	@LogMessage( level = INFO )
+	@Message(
+			id = 15015,
+			value = "Encountered a MappedSuperclass [%s] not used in any entity hierarchy"
+	)
+	void unusedMappedSuperclass(String name);
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
-    private final boolean ignoreUnsupported;
+	private Set<MappedSuperclass> knownMappedSuperclasses;
+	private final boolean ignoreUnsupported;
-	public MetadataContext(SessionFactoryImplementor sessionFactory, boolean ignoreUnsupported) {
+	public MetadataContext(
+			SessionFactoryImplementor sessionFactory,
+			Set<MappedSuperclass> mappedSuperclasses,
+			boolean ignoreUnsupported) {
-        this.ignoreUnsupported = ignoreUnsupported;
+		this.knownMappedSuperclasses = mappedSuperclasses;
+		this.ignoreUnsupported = ignoreUnsupported;
-	/*package*/ void registerMappedSuperclassType(MappedSuperclass mappedSuperclass,
-												  MappedSuperclassTypeImpl<?> mappedSuperclassType) {
+	/*package*/ void registerMappedSuperclassType(
+			MappedSuperclass mappedSuperclass,
+			MappedSuperclassTypeImpl<?> mappedSuperclassType) {
+
+		knownMappedSuperclasses.remove( mappedSuperclass );
+
+
+	public Set<MappedSuperclass> getUnusedMappedSuperclasses() {
+		return new HashSet<MappedSuperclass>( knownMappedSuperclasses );
+	}
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.mapping.MappedSuperclass;
-import org.hibernate.mapping.PersistentClass;
-
+import javax.persistence.metamodel.EmbeddableType;
+import javax.persistence.metamodel.EntityType;
+import javax.persistence.metamodel.ManagedType;
+import javax.persistence.metamodel.MappedSuperclassType;
+import javax.persistence.metamodel.Metamodel;
+import java.util.Collections;
-import javax.persistence.metamodel.*;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.jpa.internal.EntityManagerMessageLogger;
+import org.hibernate.jpa.internal.HEMLogging;
+import org.hibernate.mapping.MappedSuperclass;
+import org.hibernate.mapping.PersistentClass;
+	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( MetamodelImpl.class );
+
-	 * @deprecated use {@link #buildMetamodel(java.util.Iterator,org.hibernate.engine.spi.SessionFactoryImplementor,boolean)} instead
+	 * @deprecated use {@link #buildMetamodel(Iterator,Set,SessionFactoryImplementor,boolean)} instead
-        return buildMetamodel(persistentClasses, sessionFactory, false);
+        return buildMetamodel( persistentClasses, Collections.<MappedSuperclass>emptySet(), sessionFactory, false );
+	 * @param mappedSuperclasses All known MappedSuperclasses
+	 *
+			Set<MappedSuperclass> mappedSuperclasses,
-		MetadataContext context = new MetadataContext( sessionFactory, ignoreUnsupported );
+		MetadataContext context = new MetadataContext( sessionFactory, mappedSuperclasses, ignoreUnsupported );
+		handleUnusedMappedSuperclasses( context );
+	private static void handleUnusedMappedSuperclasses(MetadataContext context) {
+		final Set<MappedSuperclass> unusedMappedSuperclasses = context.getUnusedMappedSuperclasses();
+		if ( !unusedMappedSuperclasses.isEmpty() ) {
+			for ( MappedSuperclass mappedSuperclass : unusedMappedSuperclasses ) {
+				log.unusedMappedSuperclass( mappedSuperclass.getMappedClass().getName() );
+				locateOrBuildMappedsuperclassType( mappedSuperclass, context );
+			}
+		}
+	}
+
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java
+import java.util.Collections;
+import org.hibernate.mapping.MappedSuperclass;
-		MetamodelImpl.buildMetamodel( cfg.getClassMappings(), sfi, true );
+		MetamodelImpl.buildMetamodel( cfg.getClassMappings(), Collections.<MappedSuperclass>emptySet(), sfi, true );
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/MappedSuperclassType2Test.java
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.jpa.test.metamodel;
+
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.metamodel.ManagedType;
+import java.util.Arrays;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
+
+import org.junit.Test;
+
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertNotNull;
+
+/**
+ * Specifically see if we can access a MappedSuperclass via Metamodel that is not part of a entity hierarchy
+ *
+ * @author Steve Ebersole
+ */
+public class MappedSuperclassType2Test extends BaseUnitTestCase {
+	@Test
+	@TestForIssue( jiraKey = "HHH-8534" )
+	@FailureExpected( jiraKey = "HHH-8534" )
+	public void testMappedSuperclassAccessNoEntity() {
+		// stupid? yes.  tck does it? yes.
+
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				// pass in a MappedSuperclass that is not used in any entity hierarchy
+				return Arrays.asList( SomeMappedSuperclass.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+		try {
+			ManagedType<SomeMappedSuperclass> type = emf.getMetamodel().managedType( SomeMappedSuperclass.class );
+			// the issue was in regards to throwing an exception, but also check for nullness
+			assertNotNull( type );
+		}
+		finally {
+			emf.close();
+		}
+	}
+}

Lines added: 148. Lines removed: 23. Tot = 171
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8537  2060e95c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void debug(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void remove() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void doSecondPass(java.util.Map persistentClasses)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator iterator() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ HHH-8816  d023c7c9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-9388  1cba9802_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-9388  52f2c3a0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
66/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	public void remove(Serializable id, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public void setAlternateUniqueKey(boolean alternateUniqueKey) {
-	public void setAlternateUniqueKey(boolean alternateUniqueKey) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Value getValue() {
-	public Value getValue() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-		public Iterator iterator() {
-		public Iterator iterator() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
66/Between/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	protected void secondPassCompile() throws MappingException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(
-		secondPassCompile();
-		secondPassCompile();
-		secondPassCompile();
-		secondPassCompile();
-		secondPassCompile();
-	protected void secondPassCompile() throws MappingException {
-		LOG.trace( "Starting secondPassCompile() processing" );
-		secondPassCompile();

Lines added containing method: 0. Lines removed containing method: 8. Tot = 8
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
—————————
Method found in diff:	+	public void remove() {
+	public void remove() {
+		if (this.bufferReader != null && this.bufferReader.hasNext()) {
+			throw new IllegalStateException("Cannot remove a buffered element");
+		}
+
+		super.remove();
+	}

Lines added: 7. Lines removed: 0. Tot = 7
—————————
Method found in diff:	public void setAlternateUniqueKey(boolean unique) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public void doSecondPass(Map persistentClasses) throws MappingException {
-	public void doSecondPass(Map persistentClasses) throws MappingException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public String getValue();
-	public String getValue();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Property getReferencedProperty(String propertyPath) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
66/Between/ Merge bra d00c9c85_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
+				Map defaults = reflectionManager.getDefaults();
+				final Object isDelimited = defaults.get( "delimited-identifier" );
+				// Set default schema name if orm.xml declares it.
+				final String schema = (String) defaults.get( "schema" );
+				if ( StringHelper.isNotEmpty( schema ) ) {
+					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
+				}
+				// Set default catalog name if orm.xml declares it.
+				final String catalog = (String) defaults.get( "catalog" );
+				if ( StringHelper.isNotEmpty( catalog ) ) {
+					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
+				}

Lines added: 12. Lines removed: 1. Tot = 13
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
secondPassCompile(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* remove
* setAlternateUniqueKey
* doSecondPass
* getValue
* iterator
* getReferencedProperty
********************************************
********************************************
