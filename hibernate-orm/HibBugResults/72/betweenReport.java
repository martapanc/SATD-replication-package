72/report.java
Satd-method: protected final PreparedStatement prepareQueryStatement(
********************************************
********************************************
72/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
********************************************
********************************************
72/Between/ HHH-5697  fe8c7183_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
********************************************
********************************************
72/Between/ HHH-5765  3ca8216c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CallableStatement prepareCallableQueryStatement(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Batcher getBatcher() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-5765  b006a6c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(
+	public PreparedStatement prepareQueryStatement(
+	public PreparedStatement prepareQueryStatement(
-	public PreparedStatement prepareQueryStatement(
-	public PreparedStatement prepareQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
-	 * Close a prepared statement opened with <tt>prepareQueryStatement()</tt>
-				.prepareQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
+						session.getJDBCContext().getConnectionManager().prepareQueryStatement( sql, callable )

Lines added containing method: 3. Lines removed containing method: 4. Tot = 7
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	protected Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public CallableStatement prepareCallableQueryStatement(
-	public CallableStatement prepareCallableQueryStatement(

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException {
-	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public Batcher getBatcher();
-	public Batcher getBatcher();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-5782  e7daff9d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(
-						session.getJDBCContext().getConnectionManager().prepareQueryStatement( sql, callable )
+		st = session.getJDBCContext().getConnectionManager().prepareQueryStatement( 

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	private Batcher getBatcher() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-5949  08d9fe21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(
-	public PreparedStatement prepareQueryStatement(
-						statementPreparer.prepareQueryStatement(
-	public PreparedStatement prepareQueryStatement(
+	public PreparedStatement prepareQueryStatement(
+	public PreparedStatement prepareQueryStatement(String sql, boolean isCallable, ScrollMode scrollMode);
-		st = session.getJDBCContext().getConnectionManager().prepareQueryStatement( 
+		st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(

Lines added containing method: 3. Lines removed containing method: 4. Tot = 7
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public int getTimeout();
+	public int getTimeout();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-5961  ad5f88c2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public final String getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Integer getFetchSize() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsLimitOffset() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Integer getTimeout() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setFetchSize(int fetchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean bindLimitParametersFirst() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6076  de38d784_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	+	private ScrollMode getScrollMode(boolean scroll, QueryParameters queryParameters, boolean hasFirstRow, boolean useLimitOffSet) {
+	private ScrollMode getScrollMode(boolean scroll, QueryParameters queryParameters, boolean hasFirstRow, boolean useLimitOffSet) {
+		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
+		if ( !canScroll ) {
+			return null;
+		}
+		if ( scroll ) {
+			return queryParameters.getScrollMode();
+		}
+		if ( hasFirstRow && !useLimitOffSet ) {
+			return ScrollMode.SCROLL_INSENSITIVE;
+		}
+		return null;
+	}

Lines added: 13. Lines removed: 0. Tot = 13
********************************************
********************************************
72/Between/ HHH-6076  ef35cd7b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	-	private ScrollMode getScrollMode(boolean scroll, QueryParameters queryParameters, boolean hasFirstRow, boolean useLimitOffSet) {
-	private ScrollMode getScrollMode(boolean scroll, QueryParameters queryParameters, boolean hasFirstRow, boolean useLimitOffSet) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
72/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public static Dialect getDialect() throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int registerResultSetOutParameter(java.sql.CallableStatement statement,int col) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsLimitOffset() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static Integer getTimeout(String queryName, QueryHint[] hints) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean bindLimitParametersFirst() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Integer getFetchSize() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Integer getTimeout() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setFetchSize(int fetchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6191  c930ebcd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getTimeout() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6192  36ba1bca_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
********************************************
********************************************
72/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public static Dialect getDialect() throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Integer getFetchSize() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsLimitOffset() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static Integer getTimeout(String queryName, QueryHint[] hints) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Criteria setFetchSize(int fetchSize);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean bindLimitParametersFirst() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6198  4ee0d423_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
********************************************
********************************************
72/Between/ HHH-6200  360317ee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setFetchSize(int fetchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isScrollableResultSetsEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6258: e01bb8a9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Query setFetchSize(int fetchSize) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public static Dialect getDialect() throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int registerResultSetOutParameter(java.sql.CallableStatement statement,int col) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processFilters(String sql, SessionImplementor session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RowSelection getRowSelection() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFilteredSQL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsLimitOffset() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static Integer getTimeout(String queryName, QueryHint[] hints) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean bindLimitParametersFirst() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ScrollMode getScrollMode() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6809: 8fcbf71a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-6817  6c7379c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static boolean isCallable(Element e) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-7068  3f6e6339_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-7068  4270b477_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
72/Between/ HHH-7440, c46daa4c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
prepareQueryStatement(
-			PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
-		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
+		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
-			PreparedStatement st = prepareQueryStatement( queryParameters, true, session );
-			final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );

Lines added containing method: 1. Lines removed containing method: 4. Tot = 5
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDialect
* getSettings
* registerResultSetOutParameter
* processFilters
* prepareCallableQueryStatement
* getRowSelection
* getFetchSize
* setQueryTimeout
* closeQueryStatement
* getFilteredSQL
* supportsLimitOffset
* getTimeout
* intValue
* setFetchSize
* isCallable
* bindLimitParametersFirst
* getBatcher
* getScrollMode
* isScrollableResultSetsEnabled
—————————
Method found in diff:	public static Dialect getDialect() throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsLimitOffset() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean bindLimitParametersFirst() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
